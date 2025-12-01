package controller;

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
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/ExportarCsvServlet")
public class ExportarCsvServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ImpressoraDAO impressoraDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conexao = Conexao.getConnection();
            impressoraDAO = new ImpressoraDAO(conexao);
        } catch (Exception e) {
            throw new ServletException("Erro ao inicializar ExportarCsvServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Busca todas as impressoras
            List<Impressora> listaImpressoras = impressoraDAO.listarImpressoras();
            
            // Configura a resposta como arquivo CSV
            response.setContentType("text/csv; charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=impressoras.csv");
            
            PrintWriter writer = response.getWriter();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            
            // Cabeçalho do CSV
            writer.println("Secretaria;Local de Instalação;Modelo;Número de Série;Contador Atual;Contador Anterior;Impressões do Mês;Último Relatório;Status");
            
            // Dados
            for (Impressora imp : listaImpressoras) {
                writer.print(imp.getSecretaria() + ";");
                writer.print(imp.getLocalInstalacao() + ";");
                writer.print(imp.getModeloEquipamento() + ";");
                writer.print(imp.getNumeroSerie() + ";");
                writer.print(imp.getContadorImpressoes() + ";");
                writer.print((imp.getContadorAnterior() != null ? imp.getContadorAnterior() : "0") + ";");
                writer.print(imp.getImpressoesDoMes() + ";");
                writer.print((imp.getDataUltimaManutencao() != null ? imp.getDataUltimaManutencao().format(formatter) : "-") + ";");
                writer.println(imp.getStatus());
            }
            
            writer.flush();
            writer.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erro ao exportar CSV: " + e.getMessage());
        }
    }
}