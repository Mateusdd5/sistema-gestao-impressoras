package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UsuarioDAO;
import utils.Conexao;
import utils.SessaoUtil;

import java.io.IOException;
import java.sql.Connection;

/**
 * Servlet que processa o logout do usuário
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processarLogout(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processarLogout(request, response);
    }
    
    private void processarLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        // Registrar log de logout antes de destruir a sessão
        Integer usuarioId = SessaoUtil.obterUsuarioId(request);
        
        if (usuarioId != null) {
            try (Connection conexao = Conexao.getConnection()) {
                UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
                String ipCliente = SessaoUtil.obterIpCliente(request);
                usuarioDAO.registrarLog(usuarioId, "LOGOUT", 
                                       "Logout realizado", ipCliente);
            } catch (Exception e) {
                // Log de erro, mas continua o logout
                e.printStackTrace();
            }
        }
        
        // Destruir sessão
        SessaoUtil.destruirSessao(request);
        
        // Redirecionar para página de login com mensagem
        response.sendRedirect(request.getContextPath() + "/pages/login.jsp?logout=true");
    }
}