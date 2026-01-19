package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import dao.UsuarioDAO;
import model.Usuario;
import model.NivelPermissao;
import utils.Conexao;
import utils.SenhaUtil;
import utils.SessaoUtil;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller para gerenciamento de usuários (CRUD)
 */
@WebServlet("/UsuarioController")
public class UsuarioController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "listar";
        }
        
        try (Connection conexao = Conexao.getConnection()) {
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
            
            switch (action) {
                case "listar":
                    listarUsuarios(request, response, usuarioDAO);
                    break;
                
                case "editar":
                    exibirFormularioEdicao(request, response, usuarioDAO);
                    break;
                
                case "desativar":
                    desativarUsuario(request, response, usuarioDAO);
                    break;
                
                case "reativar":
                    reativarUsuario(request, response, usuarioDAO);
                    break;
                
                default:
                    listarUsuarios(request, response, usuarioDAO);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erro ao processar requisição: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/UsuarioController");
            return;
        }
        
        try (Connection conexao = Conexao.getConnection()) {
            UsuarioDAO usuarioDAO = new UsuarioDAO(conexao);
            
            switch (action) {
                case "adicionar":
                    adicionarUsuario(request, response, usuarioDAO);
                    break;
                
                case "atualizar":
                    atualizarUsuario(request, response, usuarioDAO);
                    break;
                
                case "alterarSenha":
                    alterarSenha(request, response, usuarioDAO);
                    break;
                
                case "deletar":
                    deletarUsuario(request, response, usuarioDAO);
                    break;
                
                default:
                    response.sendRedirect(request.getContextPath() + "/UsuarioController");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erro ao processar requisição: " + e.getMessage());
        }
    }
    
    /**
     * Lista todos os usuários
     */
    private void listarUsuarios(HttpServletRequest request, HttpServletResponse response,
                                UsuarioDAO usuarioDAO) throws ServletException, IOException, SQLException {
        
        // Verificar se é admin
        if (!SessaoUtil.isAdmin(request)) {
            response.sendRedirect(request.getContextPath() + "/pages/acessoNegado.jsp");
            return;
        }
        
        List<Usuario> listaUsuarios = usuarioDAO.listarTodos();
        
        request.setAttribute("listaUsuarios", listaUsuarios);
        request.setAttribute("totalUsuarios", listaUsuarios.size());
        request.setAttribute("usuariosAtivos", usuarioDAO.contarUsuariosAtivos());
        
        request.getRequestDispatcher("/pages/listaUsuarios.jsp").forward(request, response);
    }
    
    /**
     * Exibe formulário de edição
     */
    private void exibirFormularioEdicao(HttpServletRequest request, HttpServletResponse response,
                                        UsuarioDAO usuarioDAO) throws ServletException, IOException, SQLException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Usuario usuario = usuarioDAO.buscarPorId(id);
        
        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/UsuarioController");
            return;
        }
        
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("/pages/cadastroUsuario.jsp").forward(request, response);
    }
    
    /**
     * Adiciona novo usuário
     */
    private void adicionarUsuario(HttpServletRequest request, HttpServletResponse response,
                                  UsuarioDAO usuarioDAO) throws IOException, SQLException {
        
        String username = request.getParameter("username").trim();
        String senha = request.getParameter("senha");
        String nomeCompleto = request.getParameter("nomeCompleto").trim();
        String email = request.getParameter("email");
        String nivelStr = request.getParameter("nivelPermissao");
        
        // Validações
        if (usuarioDAO.usernameExiste(username)) {
            response.sendRedirect(request.getContextPath() + 
                "/pages/cadastroUsuario.jsp?erro=username_existe");
            return;
        }
        
        String erroSenha = SenhaUtil.obterErroValidacao(senha);
        if (erroSenha != null) {
            response.sendRedirect(request.getContextPath() + 
                "/pages/cadastroUsuario.jsp?erro=senha_invalida");
            return;
        }
        
        // Criptografar senha
        String senhaHash = SenhaUtil.criptografarSenha(senha);
        
        // Criar usuário
        NivelPermissao nivel = NivelPermissao.valueOf(nivelStr);
        Usuario novoUsuario = new Usuario(username, senhaHash, nomeCompleto, nivel);
        novoUsuario.setEmail(email);
        
        if (usuarioDAO.adicionar(novoUsuario)) {
            // Registrar log
            Integer usuarioLogadoId = SessaoUtil.obterUsuarioId(request);
            String ipCliente = SessaoUtil.obterIpCliente(request);
            usuarioDAO.registrarLog(usuarioLogadoId, "CRIAR_USUARIO", 
                                   "Criou usuário: " + username, ipCliente);
            
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?sucesso=usuario_criado");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/pages/cadastroUsuario.jsp?erro=erro_criar");
        }
    }
    
    /**
     * Atualiza dados do usuário
     */
    private void atualizarUsuario(HttpServletRequest request, HttpServletResponse response,
                                  UsuarioDAO usuarioDAO) throws IOException, SQLException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String nomeCompleto = request.getParameter("nomeCompleto").trim();
        String email = request.getParameter("email");
        String nivelStr = request.getParameter("nivelPermissao");
        boolean ativo = "true".equals(request.getParameter("ativo"));
        
        Usuario usuario = usuarioDAO.buscarPorId(id);
        
        if (usuario != null) {
            usuario.setNomeCompleto(nomeCompleto);
            usuario.setEmail(email);
            usuario.setNivelPermissao(NivelPermissao.valueOf(nivelStr));
            usuario.setAtivo(ativo);
            
            if (usuarioDAO.atualizar(usuario)) {
                // Registrar log
                Integer usuarioLogadoId = SessaoUtil.obterUsuarioId(request);
                String ipCliente = SessaoUtil.obterIpCliente(request);
                usuarioDAO.registrarLog(usuarioLogadoId, "ATUALIZAR_USUARIO", 
                                       "Atualizou usuário ID: " + id, ipCliente);
                
                response.sendRedirect(request.getContextPath() + 
                    "/UsuarioController?sucesso=usuario_atualizado");
            } else {
                response.sendRedirect(request.getContextPath() + 
                    "/UsuarioController?action=editar&id=" + id + "&erro=erro_atualizar");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/UsuarioController");
        }
    }
    
    /**
     * Altera senha do usuário
     */
    private void alterarSenha(HttpServletRequest request, HttpServletResponse response,
                             UsuarioDAO usuarioDAO) throws IOException, SQLException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        String novaSenha = request.getParameter("novaSenha");
        
        String erroSenha = SenhaUtil.obterErroValidacao(novaSenha);
        if (erroSenha != null) {
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?action=editar&id=" + id + "&erro=senha_invalida");
            return;
        }
        
        String novaSenhaHash = SenhaUtil.criptografarSenha(novaSenha);
        
        if (usuarioDAO.atualizarSenha(id, novaSenhaHash)) {
            // Registrar log
            Integer usuarioLogadoId = SessaoUtil.obterUsuarioId(request);
            String ipCliente = SessaoUtil.obterIpCliente(request);
            usuarioDAO.registrarLog(usuarioLogadoId, "ALTERAR_SENHA", 
                                   "Alterou senha do usuário ID: " + id, ipCliente);
            
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?sucesso=senha_alterada");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?action=editar&id=" + id + "&erro=erro_senha");
        }
    }
    
    /**
     * Desativa usuário
     */
    private void desativarUsuario(HttpServletRequest request, HttpServletResponse response,
                                  UsuarioDAO usuarioDAO) throws IOException, SQLException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (usuarioDAO.desativar(id)) {
            // Registrar log
            Integer usuarioLogadoId = SessaoUtil.obterUsuarioId(request);
            String ipCliente = SessaoUtil.obterIpCliente(request);
            usuarioDAO.registrarLog(usuarioLogadoId, "DESATIVAR_USUARIO", 
                                   "Desativou usuário ID: " + id, ipCliente);
            
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?sucesso=usuario_desativado");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?erro=erro_desativar");
        }
    }
    
    /**
     * Reativa usuário
     */
    private void reativarUsuario(HttpServletRequest request, HttpServletResponse response,
                                 UsuarioDAO usuarioDAO) throws IOException, SQLException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (usuarioDAO.reativar(id)) {
            // Registrar log
            Integer usuarioLogadoId = SessaoUtil.obterUsuarioId(request);
            String ipCliente = SessaoUtil.obterIpCliente(request);
            usuarioDAO.registrarLog(usuarioLogadoId, "REATIVAR_USUARIO", 
                                   "Reativou usuário ID: " + id, ipCliente);
            
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?sucesso=usuario_reativado");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?erro=erro_reativar");
        }
    }
    
    /**
     * Deleta usuário permanentemente
     */
    private void deletarUsuario(HttpServletRequest request, HttpServletResponse response,
                               UsuarioDAO usuarioDAO) throws IOException, SQLException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        // Não permitir deletar o próprio usuário
        Integer usuarioLogadoId = SessaoUtil.obterUsuarioId(request);
        if (id == usuarioLogadoId) {
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?erro=nao_pode_deletar_proprio");
            return;
        }
        
        if (usuarioDAO.deletar(id)) {
            // Registrar log
            String ipCliente = SessaoUtil.obterIpCliente(request);
            usuarioDAO.registrarLog(usuarioLogadoId, "DELETAR_USUARIO", 
                                   "Deletou usuário ID: " + id, ipCliente);
            
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?sucesso=usuario_deletado");
        } else {
            response.sendRedirect(request.getContextPath() + 
                "/UsuarioController?erro=erro_deletar");
        }
    }
}