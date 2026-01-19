package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Usuario;
import utils.SessaoUtil;

import java.io.IOException;

/**
 * Filtro que verifica se o usuário tem permissão para acessar determinadas funcionalidades
 */
@WebFilter(filterName = "PermissaoFilter", urlPatterns = {
    "/pages/cadastroImpressora.jsp",
    "/pages/listaUsuarios.jsp",
    "/pages/cadastroUsuario.jsp"
})
public class PermissaoFilter implements Filter {
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Inicialização do filtro (se necessário)
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Obter usuário da sessão
        Usuario usuario = SessaoUtil.obterUsuarioLogado(httpRequest);
        
        if (usuario == null) {
            // Se não estiver logado, o AutenticacaoFilter já vai redirecionar
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar permissões específicas por página
        
        // Páginas de gerenciamento de usuários - APENAS ADMIN
        if (uri.contains("listaUsuarios.jsp") || uri.contains("cadastroUsuario.jsp")) {
            if (!usuario.podeGerenciarUsuarios()) {
                httpResponse.sendRedirect(contextPath + "/pages/acessoNegado.jsp");
                return;
            }
        }
        
        // Página de cadastro de impressoras - TÉCNICO ou ADMIN
        if (uri.contains("cadastroImpressora.jsp")) {
            String action = httpRequest.getParameter("action");
            
            // Se for cadastrar nova impressora
            if (action == null || "adicionar".equals(action)) {
                if (!usuario.podeCadastrar()) {
                    httpResponse.sendRedirect(contextPath + "/pages/acessoNegado.jsp");
                    return;
                }
            }
            
            // Se for editar, verificar se pode editar dados cadastrais
            if ("editar".equals(action)) {
                if (!usuario.podeEditarContadores()) {
                    httpResponse.sendRedirect(contextPath + "/pages/acessoNegado.jsp");
                    return;
                }
            }
        }
        
        // Permissão OK - continuar
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Limpeza do filtro (se necessário)
    }
}