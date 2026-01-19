package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UsuarioDAO;
import model.Usuario;
import utils.Conexao;
import utils.SenhaUtil;
import utils.SessaoUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Servlet que processa o login do usuário
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Se já estiver logado, redireciona para lista de impressoras
        if (SessaoUtil.isUsuarioLogado(request)) {
            response.sendRedirect(request.getContextPath() + "/ImpressoraController");
            return;
        }
        
        // Redireciona para página de login
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
    	String username = request.getParameter("username");
    	String senha = request.getParameter("senha");

    	if (username != null) {
    	    username = username.trim();
    	}

    	if (senha != null) {
    	    senha = senha.trim();
    	}
        
        // Validar campos vazios
        if (username == null || username.trim().isEmpty() || 
            senha == null || senha.trim().isEmpty()) {
            
            request.setAttribute("erro", "Usuário e senha são obrigatórios");
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
            return;
        }
        
        try (Connection conexao = Conexao.getConnection()) {
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
            
            // Buscar usuário no banco
            Usuario usuario = usuarioDAO.buscarPorUsername(username.trim());
            
            // Verificar se usuário existe
            if (usuario == null) {
                request.setAttribute("erro", "Usuário ou senha incorretos");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
                return;
            }
            
            // Verificar se usuário está ativo
            if (!usuario.getAtivo()) {
                request.setAttribute("erro", "Usuário desativado. Contate o administrador.");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
                return;
            }
            
            // Verificar senha
            if (!SenhaUtil.verificarSenha(senha, usuario.getSenhaHash())) {
                request.setAttribute("erro", "Usuário ou senha incorretos");
                request.setAttribute("username", username);
                request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
                return;
            }
            
            // Login bem-sucedido!
            
            // Atualizar último acesso
            usuarioDAO.atualizarUltimoAcesso(usuario.getId());
            
            // Registrar log de login
            String ipCliente = SessaoUtil.obterIpCliente(request);
            usuarioDAO.registrarLog(usuario.getId(), "LOGIN", 
                                   "Login realizado com sucesso", ipCliente);
            
            // Criar sessão
            SessaoUtil.criarSessao(request, usuario);
            
            // Verificar se há URL de origem (quando foi redirecionado do filtro)
            String urlOriginal = (String) request.getSession().getAttribute("urlOriginal");
            
            if (urlOriginal != null && !urlOriginal.isEmpty()) {
                request.getSession().removeAttribute("urlOriginal");
                response.sendRedirect(urlOriginal);
            } else {
                // Redirecionar para página inicial (lista de impressoras)
                response.sendRedirect(request.getContextPath() + "/ImpressoraController");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("erro", "Erro ao processar login. Tente novamente.");
            request.setAttribute("username", username);
            request.getRequestDispatcher("/pages/login.jsp").forward(request, response);
        }
    }
}