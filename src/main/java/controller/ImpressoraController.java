package controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Impressora;
import dao.ImpressoraDAO;
import utils.Conexao;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/ImpressoraController")
public class ImpressoraController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        try (Connection conexao = Conexao.getConnection()) {
            ImpressoraDAO impressoraDAO = new ImpressoraDAO(conexao);

            if (action == null) {
                listarImpressoras(request, response, impressoraDAO);
            } else {
                switch (action) {
                    case "buscar":
                        buscarImpressoras(request, response, impressoraDAO);
                        break;
                    case "filtrarSecretaria":
                        filtrarPorSecretaria(request, response, impressoraDAO);
                        break;
                    case "editar":
                        exibirFormularioEdicao(request, response, impressoraDAO);
                        break;
                    case "novoCadastro":
                        exibirFormularioCadastro(request, response, impressoraDAO);
                        break;
                    case "relatorioImpressao":
                        exibirRelatorioImpressao(request, response, impressoraDAO);
                        break;
                    case "relatorioImpressaoOculto":
                        exibirRelatorioImpressaoOculto(request, response, impressoraDAO);
                        break;
                    default:
                        listarImpressoras(request, response, impressoraDAO);
                        break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                             "Erro ao processar requisição: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                             "Erro ao processar requisição: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");

        try (Connection conexao = Conexao.getConnection()) {
            ImpressoraDAO impressoraDAO = new ImpressoraDAO(conexao);

            if (action == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação não especificada");
                return;
            }

            switch (action) {
                case "adicionar":
                    adicionarImpressora(request, response, impressoraDAO);
                    break;
                case "editar":
                    editarImpressora(request, response, impressoraDAO);
                    break;
                case "deletar":
                    deletarImpressora(request, response, impressoraDAO);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Ação inválida");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                             "Erro ao processar requisição: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                             "Erro ao processar requisição: " + e.getMessage());
        }
    }

    private void adicionarImpressora(HttpServletRequest request, HttpServletResponse response,
                                     ImpressoraDAO impressoraDAO) throws Exception {
        String localInstalacao = request.getParameter("localInstalacao");
        String modeloEquipamento = request.getParameter("modeloEquipamento");
        String numeroSerie = request.getParameter("numeroSerie");
        String contadorStr = request.getParameter("contadorImpressoes");
        String contadorAnteriorStr = request.getParameter("contadorAnterior");
        String dataManutencaoStr = request.getParameter("dataUltimaManutencao");
        String secretaria = request.getParameter("secretaria");
        String status = request.getParameter("status");

        // Validações
        if (localInstalacao == null || localInstalacao.trim().isEmpty() ||
            modeloEquipamento == null || modeloEquipamento.trim().isEmpty() ||
            numeroSerie == null || numeroSerie.trim().isEmpty() ||
            contadorStr == null || contadorStr.trim().isEmpty() ||
            secretaria == null || secretaria.trim().isEmpty() ||
            status == null || status.trim().isEmpty()) {

            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("alert('Todos os campos obrigatórios devem ser preenchidos!');");
            out.println("history.back();");
            out.println("</script>");
            out.close();
            return;
        }

        // Verifica se o número de série já existe
        Impressora impressoraExistente = impressoraDAO.buscarPorNumeroSerie(numeroSerie.trim());
        if (impressoraExistente != null) {
            response.setContentType("text/html; charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script type='text/javascript'>");
            out.println("alert('Já existe uma impressora cadastrada com este número de série!');");
            out.println("history.back();");
            out.println("</script>");
            out.close();
            return;
        }

        Integer contadorImpressoes = Integer.parseInt(contadorStr);

        Integer contadorAnterior = null;
        if (contadorAnteriorStr != null && !contadorAnteriorStr.trim().isEmpty()) {
            contadorAnterior = Integer.parseInt(contadorAnteriorStr);
        }

        LocalDate dataUltimaManutencao = null;
        if (dataManutencaoStr != null && !dataManutencaoStr.trim().isEmpty()) {
            dataUltimaManutencao = LocalDate.parse(dataManutencaoStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }

        // Detectar custo por impressão automaticamente
        BigDecimal custoPorImpressao = Impressora.detectarCustoPorModelo(modeloEquipamento.trim());

        Impressora impressora = new Impressora(
            localInstalacao.trim(),
            modeloEquipamento.trim(),
            custoPorImpressao,
            numeroSerie.trim(),
            contadorImpressoes,
            contadorAnterior,
            dataUltimaManutencao,
            secretaria.trim(),
            status.trim()
        );

        impressoraDAO.adicionarImpressora(impressora);

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");
        out.println("alert('Impressora cadastrada com sucesso!');");
        out.println("window.location.href='ImpressoraController';");
        out.println("</script>");
        out.close();
    }

    private void editarImpressora(HttpServletRequest request, HttpServletResponse response,
                                  ImpressoraDAO impressoraDAO) throws Exception {
        String idStr = request.getParameter("id");
        String localInstalacao = request.getParameter("localInstalacao");
        String modeloEquipamento = request.getParameter("modeloEquipamento");
        String numeroSerie = request.getParameter("numeroSerie");
        String contadorStr = request.getParameter("contadorImpressoes");
        String contadorAnteriorStr = request.getParameter("contadorAnterior");
        String dataManutencaoStr = request.getParameter("dataUltimaManutencao");
        String dataRelatorioAnteriorStr = request.getParameter("dataRelatorioAnterior");
        String secretaria = request.getParameter("secretaria");
        String status = request.getParameter("status");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da impressora não fornecido");
            return;
        }

        Integer id = Integer.parseInt(idStr);
        Integer contadorImpressoes = Integer.parseInt(contadorStr);

        Integer contadorAnterior = null;
        if (contadorAnteriorStr != null && !contadorAnteriorStr.trim().isEmpty()) {
            contadorAnterior = Integer.parseInt(contadorAnteriorStr);
        }

        LocalDate dataUltimaManutencao = null;
        if (dataManutencaoStr != null && !dataManutencaoStr.trim().isEmpty()) {
            dataUltimaManutencao = LocalDate.parse(dataManutencaoStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }

        LocalDate dataRelatorioAnterior = null;
        if (dataRelatorioAnteriorStr != null && !dataRelatorioAnteriorStr.trim().isEmpty()) {
            dataRelatorioAnterior = LocalDate.parse(dataRelatorioAnteriorStr, DateTimeFormatter.ISO_LOCAL_DATE);
        }

        // Detectar custo por impressão automaticamente
        BigDecimal custoPorImpressao = Impressora.detectarCustoPorModelo(modeloEquipamento.trim());

        Impressora impressora = new Impressora(
            id,
            localInstalacao.trim(),
            modeloEquipamento.trim(),
            custoPorImpressao,
            numeroSerie.trim(),
            contadorImpressoes,
            contadorAnterior,
            dataUltimaManutencao,
            secretaria.trim(),
            status.trim()
        );

        // Definir o valor manual do relatório anterior (se fornecido)
        // Se null, o DAO decidirá automaticamente via lógica de rotação
        impressora.setDataRelatorioAnterior(dataRelatorioAnterior);

        impressoraDAO.atualizarImpressora(impressora);

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");
        out.println("alert('Impressora atualizada com sucesso!');");
        out.println("window.location.href='ImpressoraController';");
        out.println("</script>");
        out.close();
    }

    private void deletarImpressora(HttpServletRequest request, HttpServletResponse response,
                                   ImpressoraDAO impressoraDAO) throws Exception {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da impressora não fornecido");
            return;
        }

        Integer id = Integer.parseInt(idStr);
        impressoraDAO.excluirImpressora(id);

        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>");
        out.println("alert('Impressora excluída com sucesso!');");
        out.println("window.location.href='ImpressoraController';");
        out.println("</script>");
        out.close();
    }

    private void listarImpressoras(HttpServletRequest request, HttpServletResponse response,
                                   ImpressoraDAO impressoraDAO) throws Exception {
        List<Impressora> listaImpressoras = impressoraDAO.listarImpressoras();
        List<String> listaSecretarias = impressoraDAO.listarSecretarias();
        int totalImpressoras = impressoraDAO.contarImpressoras();

        if (listaImpressoras == null || listaImpressoras.isEmpty()) {
            System.out.println("Nenhuma impressora cadastrada.");
        } else {
            System.out.println("Total de impressoras: " + listaImpressoras.size());
        }

        request.setAttribute("listaImpressoras", listaImpressoras);
        request.setAttribute("listaSecretarias", listaSecretarias);
        request.setAttribute("totalResultados", totalImpressoras);
        request.setAttribute("temFiltro", false);
        request.setAttribute("filtroAtual", "");
        request.setAttribute("secretariaSelecionada", "TODAS");

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/listaImpressoras.jsp");
        dispatcher.forward(request, response);
    }

    private void buscarImpressoras(HttpServletRequest request, HttpServletResponse response,
                                   ImpressoraDAO impressoraDAO) throws Exception {
        String filtro = request.getParameter("filtro");

        List<Impressora> listaImpressoras;
        List<String> listaSecretarias = impressoraDAO.listarSecretarias();

        if (filtro != null && !filtro.trim().isEmpty()) {
            listaImpressoras = impressoraDAO.buscarImpressorasPorFiltro(filtro);
        } else {
            listaImpressoras = impressoraDAO.listarImpressoras();
        }

        int totalResultados = listaImpressoras.size();

        request.setAttribute("listaImpressoras", listaImpressoras);
        request.setAttribute("listaSecretarias", listaSecretarias);
        request.setAttribute("totalResultados", totalResultados);
        request.setAttribute("temFiltro", filtro != null && !filtro.trim().isEmpty());
        request.setAttribute("filtroAtual", filtro != null ? filtro : "");
        request.setAttribute("secretariaSelecionada", "TODAS");

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/listaImpressoras.jsp");
        dispatcher.forward(request, response);
    }

    private void filtrarPorSecretaria(HttpServletRequest request, HttpServletResponse response,
                                      ImpressoraDAO impressoraDAO) throws Exception {
        String secretaria = request.getParameter("secretaria");

        List<Impressora> listaImpressoras;
        List<String> listaSecretarias = impressoraDAO.listarSecretarias();
        int totalResultados;

        if (secretaria != null && !secretaria.trim().isEmpty() && !secretaria.equals("TODAS")) {
            listaImpressoras = impressoraDAO.listarImpressorasPorSecretaria(secretaria);
            totalResultados = impressoraDAO.contarImpressorasPorSecretaria(secretaria);
        } else {
            listaImpressoras = impressoraDAO.listarImpressoras();
            totalResultados = impressoraDAO.contarImpressoras();
            secretaria = "TODAS";
        }

        request.setAttribute("listaImpressoras", listaImpressoras);
        request.setAttribute("listaSecretarias", listaSecretarias);
        request.setAttribute("totalResultados", totalResultados);
        request.setAttribute("temFiltro", !secretaria.equals("TODAS"));
        request.setAttribute("filtroAtual", "");
        request.setAttribute("secretariaSelecionada", secretaria);

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/listaImpressoras.jsp");
        dispatcher.forward(request, response);
    }

    private void exibirFormularioCadastro(HttpServletRequest request, HttpServletResponse response,
                                          ImpressoraDAO impressoraDAO) throws Exception {

        List<String> listaSecretarias = impressoraDAO.listarSecretarias();

        System.out.println("========== FORMULÁRIO CADASTRO ==========");
        System.out.println("Secretarias carregadas: " + (listaSecretarias != null ? listaSecretarias.size() : "NULL"));
        if (listaSecretarias != null && !listaSecretarias.isEmpty()) {
            System.out.println("Primeira secretaria: " + listaSecretarias.get(0));
        }
        System.out.println("=========================================");

        request.setAttribute("listaSecretarias", listaSecretarias);
        request.setAttribute("impressora", null);

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/cadastroImpressora.jsp");
        dispatcher.forward(request, response);
    }

    private void exibirFormularioEdicao(HttpServletRequest request, HttpServletResponse response,
                                        ImpressoraDAO impressoraDAO) throws Exception {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID da impressora não fornecido");
            return;
        }

        Integer id = Integer.parseInt(idStr);
        Impressora impressora = impressoraDAO.buscarPorId(id);

        if (impressora == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Impressora não encontrada");
            return;
        }

        List<String> listaSecretarias = impressoraDAO.listarSecretarias();

        System.out.println("========== FORMULÁRIO EDIÇÃO ==========");
        System.out.println("Impressora ID: " + impressora.getId());
        System.out.println("Secretarias carregadas: " + (listaSecretarias != null ? listaSecretarias.size() : "NULL"));
        System.out.println("=======================================");

        request.setAttribute("impressora", impressora);
        request.setAttribute("listaSecretarias", listaSecretarias);

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/cadastroImpressora.jsp");
        dispatcher.forward(request, response);
    }

    private void exibirRelatorioImpressao(HttpServletRequest request, HttpServletResponse response,
                                          ImpressoraDAO impressoraDAO) throws Exception {
        String secretaria = request.getParameter("secretaria");

        List<Impressora> listaImpressoras;
        Map<String, BigDecimal> custosPorSecretaria = impressoraDAO.calcularCustoMensalPorSecretaria();

        if (secretaria != null && !secretaria.trim().isEmpty() && !secretaria.equals("TODAS")) {
            listaImpressoras = impressoraDAO.listarImpressorasPorSecretaria(secretaria);
        } else {
            listaImpressoras = impressoraDAO.listarImpressoras();
            secretaria = "TODAS";
        }

        request.setAttribute("listaImpressoras", listaImpressoras);
        request.setAttribute("secretariaFiltro", secretaria);
        request.setAttribute("custosPorSecretaria", custosPorSecretaria);

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/relatorioImpressao.jsp");
        dispatcher.forward(request, response);
    }

    private void exibirRelatorioImpressaoOculto(HttpServletRequest request, HttpServletResponse response,
                                                ImpressoraDAO impressoraDAO) throws Exception {
        String secretaria = request.getParameter("secretaria");

        List<Impressora> listaImpressoras;

        if (secretaria != null && !secretaria.trim().isEmpty() && !secretaria.equals("TODAS")) {
            listaImpressoras = impressoraDAO.listarImpressorasPorSecretaria(secretaria);
        } else {
            listaImpressoras = impressoraDAO.listarImpressoras();
            secretaria = "TODAS";
        }

        request.setAttribute("listaImpressoras", listaImpressoras);
        request.setAttribute("secretariaFiltro", secretaria);

        RequestDispatcher dispatcher = request.getRequestDispatcher("pages/relatorioImpressaoOculto.jsp");
        dispatcher.forward(request, response);
    }
}