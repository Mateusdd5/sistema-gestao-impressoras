package controller;

import dao.HistoricoContadorDAO;
import dao.ImpressoraDAO;
import model.HistoricoContador;
import model.Usuario;
import utils.Conexao;
import utils.SessaoUtil;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/HistoricoContadorServlet")
public class HistoricoContadorServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Usuario usuarioLogado = SessaoUtil.obterUsuarioLogado(request);
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");
        if (action == null) action = "listar";

        try (Connection conexao = Conexao.getConnection()) {
            HistoricoContadorDAO historicoDAO = new HistoricoContadorDAO(conexao);
            ImpressoraDAO impressoraDAO = new ImpressoraDAO(conexao);

            switch (action) {
                case "listar":
                    listarHistorico(request, response, historicoDAO, impressoraDAO, usuarioLogado);
                    break;
                default:
                    listarHistorico(request, response, historicoDAO, impressoraDAO, usuarioLogado);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao carregar histórico: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao carregar histórico: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        Usuario usuarioLogado = SessaoUtil.obterUsuarioLogado(request);
        if (usuarioLogado == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Apenas ADMIN pode editar ou deletar registros do histórico
        if (!usuarioLogado.isAdmin()) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('Apenas administradores podem modificar o histórico.'); history.back();</script>");
            out.close();
            return;
        }

        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect(request.getContextPath() + "/HistoricoContadorServlet");
            return;
        }

        try (Connection conexao = Conexao.getConnection()) {
            HistoricoContadorDAO historicoDAO = new HistoricoContadorDAO(conexao);

            switch (action) {
                case "editar":
                    editarRegistro(request, response, historicoDAO, usuarioLogado);
                    break;
                case "deletar":
                    deletarRegistro(request, response, historicoDAO);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/HistoricoContadorServlet");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Erro ao processar histórico: " + e.getMessage());
        }
    }

    /**
     * Lista o histórico respeitando o escopo do usuário logado.
     */
    private void listarHistorico(HttpServletRequest request, HttpServletResponse response,
                                 HistoricoContadorDAO historicoDAO, ImpressoraDAO impressoraDAO,
                                 Usuario usuarioLogado)
            throws Exception {

        String secretariaFiltro;
        boolean filtroFixo = usuarioLogado.isUsuarioSecretaria();

        if (filtroFixo) {
            secretariaFiltro = usuarioLogado.getSecretariaVinculada();
        } else {
            secretariaFiltro = request.getParameter("secretaria");
            if (secretariaFiltro == null || secretariaFiltro.trim().isEmpty()) {
                secretariaFiltro = "TODAS";
            }
        }

        List<HistoricoContador> listaHistorico = historicoDAO.listarTodos(
                "TODAS".equals(secretariaFiltro) ? null : secretariaFiltro
        );

        List<String> listaSecretarias = filtroFixo ? null : impressoraDAO.listarSecretarias();

        request.setAttribute("listaHistorico", listaHistorico);
        request.setAttribute("listaSecretarias", listaSecretarias);
        request.setAttribute("secretariaFiltro", secretariaFiltro);
        request.setAttribute("filtroFixo", filtroFixo);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/pages/historicoContador.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * Edita manualmente um valor de contador no histórico (apenas ADMIN).
     */
    private void editarRegistro(HttpServletRequest request, HttpServletResponse response,
                                HistoricoContadorDAO historicoDAO, Usuario usuarioLogado)
            throws SQLException, IOException {

        String idStr            = request.getParameter("id");
        String valorStr         = request.getParameter("contadorValor");
        String secretariaFiltro = request.getParameter("secretariaFiltro");
        if (secretariaFiltro == null) secretariaFiltro = "TODAS";

        if (idStr == null || valorStr == null || idStr.trim().isEmpty() || valorStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() +
                    "/HistoricoContadorServlet?erro=dados_invalidos&secretaria=" + secretariaFiltro);
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        BigDecimal novoValor = new BigDecimal(valorStr.trim().replace(",", "."));

        boolean sucesso = historicoDAO.atualizarManual(id, novoValor, usuarioLogado.getUsername());

        if (sucesso) {
            response.sendRedirect(request.getContextPath() +
                    "/HistoricoContadorServlet?sucesso=registro_editado&secretaria=" + secretariaFiltro);
        } else {
            response.sendRedirect(request.getContextPath() +
                    "/HistoricoContadorServlet?erro=erro_editar&secretaria=" + secretariaFiltro);
        }
    }

    /**
     * Deleta um registro do histórico (apenas ADMIN).
     */
    private void deletarRegistro(HttpServletRequest request, HttpServletResponse response,
                                 HistoricoContadorDAO historicoDAO)
            throws SQLException, IOException {

        String idStr            = request.getParameter("id");
        String secretariaFiltro = request.getParameter("secretariaFiltro");
        if (secretariaFiltro == null) secretariaFiltro = "TODAS";

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() +
                    "/HistoricoContadorServlet?erro=dados_invalidos&secretaria=" + secretariaFiltro);
            return;
        }

        int id = Integer.parseInt(idStr.trim());
        boolean sucesso = historicoDAO.deletarRegistro(id);

        if (sucesso) {
            response.sendRedirect(request.getContextPath() +
                    "/HistoricoContadorServlet?sucesso=registro_deletado&secretaria=" + secretariaFiltro);
        } else {
            response.sendRedirect(request.getContextPath() +
                    "/HistoricoContadorServlet?erro=erro_deletar&secretaria=" + secretariaFiltro);
        }
    }
}