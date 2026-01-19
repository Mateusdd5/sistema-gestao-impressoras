package utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.Usuario;

/**
 * Classe utilitária para gerenciamento de sessões de usuário
 */
public class SessaoUtil {
    
    /**
     * Nome do atributo de sessão que armazena o usuário logado
     */
    private static final String ATRIBUTO_USUARIO = "usuarioLogado";
    
    /**
     * Tempo de inatividade em segundos 
     */
    private static final int TEMPO_SESSAO = 8 * 60 * 60; // 8 horas
    
    /**
     * Cria uma sessão para o usuário após login bem-sucedido
     * 
     * @param request HttpServletRequest
     * @param usuario Usuario que fez login
     */
    public static void criarSessao(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(true);
        
        // Define tempo máximo de inatividade 
        session.setMaxInactiveInterval(TEMPO_SESSAO);
        
        // Armazena o usuário na sessão
        session.setAttribute(ATRIBUTO_USUARIO, usuario);
        
        // Armazena informações adicionais
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("username", usuario.getUsername());
        session.setAttribute("nivelPermissao", usuario.getNivelPermissao());
        session.setAttribute("loginTimestamp", System.currentTimeMillis());
    }
    
    /**
     * Obtém o usuário logado da sessão
     * 
     * @param request HttpServletRequest
     * @return Usuario logado ou null se não houver sessão
     */
    public static Usuario obterUsuarioLogado(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session == null) {
            return null;
        }
        
        return (Usuario) session.getAttribute(ATRIBUTO_USUARIO);
    }
    
    /**
     * Verifica se existe um usuário logado
     * 
     * @param request HttpServletRequest
     * @return true se há usuário logado, false caso contrário
     */
    public static boolean isUsuarioLogado(HttpServletRequest request) {
        return obterUsuarioLogado(request) != null;
    }
    
    /**
     * Destrói a sessão do usuário (logout)
     * 
     * @param request HttpServletRequest
     */
    public static void destruirSessao(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.invalidate();
        }
    }
    
    /**
     * Atualiza os dados do usuário na sessão
     * Útil quando o usuário edita seu próprio perfil
     * 
     * @param request HttpServletRequest
     * @param usuario Usuario com dados atualizados
     */
    public static void atualizarUsuarioSessao(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            session.setAttribute(ATRIBUTO_USUARIO, usuario);
            session.setAttribute("nivelPermissao", usuario.getNivelPermissao());
        }
    }
    
    /**
     * Obtém o ID do usuário logado
     * 
     * @param request HttpServletRequest
     * @return ID do usuário ou null se não estiver logado
     */
    public static Integer obterUsuarioId(HttpServletRequest request) {
        Usuario usuario = obterUsuarioLogado(request);
        return usuario != null ? usuario.getId() : null;
    }
    
    /**
     * Obtém o username do usuário logado
     * 
     * @param request HttpServletRequest
     * @return Username ou null se não estiver logado
     */
    public static String obterUsername(HttpServletRequest request) {
        Usuario usuario = obterUsuarioLogado(request);
        return usuario != null ? usuario.getUsername() : null;
    }
    
    /**
     * Verifica se o usuário logado é administrador
     * 
     * @param request HttpServletRequest
     * @return true se for admin, false caso contrário
     */
    public static boolean isAdmin(HttpServletRequest request) {
        Usuario usuario = obterUsuarioLogado(request);
        return usuario != null && usuario.isAdmin();
    }
    
    /**
     * Verifica se o usuário logado pode executar determinada ação
     * 
     * @param request HttpServletRequest
     * @param acao Ação a ser verificada (cadastrar, editar, deletar, etc)
     * @return true se o usuário tem permissão
     */
    public static boolean temPermissao(HttpServletRequest request, String acao) {
        Usuario usuario = obterUsuarioLogado(request);
        
        if (usuario == null) {
            return false;
        }
        
        switch (acao.toLowerCase()) {
            case "visualizar":
                return usuario.podeVisualizar();
            
            case "editar_contadores":
                return usuario.podeEditarContadores();
            
            case "editar_cadastrais":
                return usuario.podeEditarDadosCadastrais();
            
            case "cadastrar":
                return usuario.podeCadastrar();
            
            case "deletar":
                return usuario.podeDeletar();
            
            case "gerenciar_usuarios":
                return usuario.podeGerenciarUsuarios();
            
            default:
                return false;
        }
    }
    
    /**
     * Obtém o endereço IP do cliente
     * 
     * @param request HttpServletRequest
     * @return Endereço IP do cliente
     */
    public static String obterIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // Se houver múltiplos IPs (proxy), pega o primeiro
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
    
    /**
     * Obtém o tempo de login em milissegundos
     * 
     * @param request HttpServletRequest
     * @return Timestamp do login ou null
     */
    public static Long obterTempoLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            return (Long) session.getAttribute("loginTimestamp");
        }
        
        return null;
    }
    
    /**
     * Calcula há quanto tempo o usuário está logado (em minutos)
     * 
     * @param request HttpServletRequest
     * @return Minutos desde o login ou 0
     */
    public static long calcularTempoLogado(HttpServletRequest request) {
        Long loginTime = obterTempoLogin(request);
        
        if (loginTime != null) {
            long diferenca = System.currentTimeMillis() - loginTime;
            return diferenca / (60 * 1000); // Converte para minutos
        }
        
        return 0;
    }
}