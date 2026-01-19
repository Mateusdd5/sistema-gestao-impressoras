package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.SessaoUtil;

import java.io.IOException;

/**
 * Filtro que verifica se o usuário está autenticado antes de acessar páginas protegidas
 */
@WebFilter(filterName = "AutenticacaoFilter", urlPatterns = {
    "/pages/*",
    "/ImpressoraController",
    "/UsuarioController",
    "/ExportarCsvServlet",
    "/ExportarExcelServlet"
})
public class AutenticacaoFilter implements Filter {
    
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
        
        // Permitir acesso à página de login sem autenticação
        if (uri.endsWith("login.jsp") || uri.endsWith("LoginServlet") || uri.contains("/login")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Permitir acesso a recursos estáticos (CSS, JS, imagens)
        if (uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verificar se usuário está logado
        if (!SessaoUtil.isUsuarioLogado(httpRequest)) {
            // Salvar URL original para redirecionar depois do login
            httpRequest.getSession(true).setAttribute("urlOriginal", uri);
            
            // Redirecionar para login
            httpResponse.sendRedirect(contextPath + "/pages/login.jsp");
            return;
        }
        
        // Usuário autenticado - permitir acesso
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // Limpeza do filtro (se necessário)
    }
}