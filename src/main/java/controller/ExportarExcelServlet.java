package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Impressora;
import dao.ImpressoraDAO;
import utils.Conexao;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Locale;
import java.util.stream.Collectors;

@WebServlet("/ExportarExcelServlet")
public class ExportarExcelServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ImpressoraDAO impressoraDAO;

    @Override
    public void init() throws ServletException {
        try {
            Connection conexao = Conexao.getConnection();
            impressoraDAO = new ImpressoraDAO(conexao);
        } catch (Exception e) {
            throw new ServletException("Erro ao inicializar ExportarExcelServlet: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // Buscar todas as impressoras e custos por secretaria
            List<Impressora> listaImpressoras = impressoraDAO.listarImpressoras();
            Map<String, BigDecimal> custosPorSecretaria = impressoraDAO.calcularCustoMensalPorSecretaria();
            
            // Criar workbook
            Workbook workbook = new XSSFWorkbook();
            
            // Criar estilos
            CellStyle headerStyle = criarEstiloCabecalho(workbook);
            CellStyle dataStyle = criarEstiloDados(workbook);
            CellStyle currencyStyle = criarEstiloMoeda(workbook);
            CellStyle dateStyle = criarEstiloData(workbook);
            CellStyle totalStyle = criarEstiloTotal(workbook);
            
            // Criar aba de resumo
            criarAbaResumo(workbook, listaImpressoras, custosPorSecretaria, headerStyle, dataStyle, currencyStyle, totalStyle);
            
            // Agrupar impressoras por secretaria
            Map<String, List<Impressora>> impressorasPorSecretaria = listaImpressoras.stream()
                .collect(Collectors.groupingBy(Impressora::getSecretaria));
            
            // Criar uma aba para cada secretaria
            for (Map.Entry<String, List<Impressora>> entry : impressorasPorSecretaria.entrySet()) {
                String secretaria = entry.getKey();
                List<Impressora> impressoras = entry.getValue();
                criarAbaSecretaria(workbook, secretaria, impressoras, headerStyle, dataStyle, currencyStyle, dateStyle, totalStyle);
            }
            
            // Configurar response
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=impressoras_" + 
                LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".xlsx");
            
            // Escrever workbook no response
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 
                             "Erro ao gerar Excel: " + e.getMessage());
        }
    }
    
    /**
     * Cria aba de resumo geral
     */
    private void criarAbaResumo(Workbook workbook, List<Impressora> todasImpressoras, 
                               Map<String, BigDecimal> custosPorSecretaria,
                               CellStyle headerStyle, CellStyle dataStyle, 
                               CellStyle currencyStyle, CellStyle totalStyle) {
        
        Sheet sheet = workbook.createSheet("Resumo Geral");
        
        int rowNum = 0;
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("RELATÓRIO DE IMPRESSORAS - " + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));
        
        rowNum++; // Linha em branco
        
        // Cabeçalho da tabela
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Secretaria", "Total Impressoras", "Impressões do Mês", "Custo Mensal"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dados por secretaria
        Map<String, List<Impressora>> porSecretaria = todasImpressoras.stream()
            .collect(Collectors.groupingBy(Impressora::getSecretaria));
        
        int totalImpressoras = 0;
        BigDecimal totalImpressoesMes = BigDecimal.ZERO;
        BigDecimal custoTotalGeral = BigDecimal.ZERO;
        
        for (Map.Entry<String, List<Impressora>> entry : porSecretaria.entrySet()) {
            String secretaria = entry.getKey();
            List<Impressora> impressoras = entry.getValue();
            
            Row dataRow = sheet.createRow(rowNum++);
            
            // Secretaria
            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(secretaria);
            cell0.setCellStyle(dataStyle);
            
            // Total impressoras
            int totalSec = impressoras.size();
            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(totalSec);
            cell1.setCellStyle(dataStyle);
            
            // Impressões do mês — só soma impressoras incluídas no cálculo
            BigDecimal impressoesSec = impressoras.stream()
                .filter(imp -> imp.getIncluirNoCalculo() != null && imp.getIncluirNoCalculo())
                .map(Impressora::getImpressoesDoMes)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            Cell cell2 = dataRow.createCell(2);
            cell2.setCellValue(impressoesSec.doubleValue());
            cell2.setCellStyle(dataStyle);
            
            // Custo mensal
            BigDecimal custoSec = custosPorSecretaria.getOrDefault(secretaria, BigDecimal.ZERO);
            Cell cell3 = dataRow.createCell(3);
            cell3.setCellValue(custoSec.doubleValue());
            cell3.setCellStyle(currencyStyle);
            
            totalImpressoras += totalSec;
            totalImpressoesMes = totalImpressoesMes.add(impressoesSec);
            custoTotalGeral = custoTotalGeral.add(custoSec);
        }
        
        // Linha de total
        Row totalRow = sheet.createRow(rowNum++);
        
        Cell totalCell0 = totalRow.createCell(0);
        totalCell0.setCellValue("TOTAL GERAL:");
        totalCell0.setCellStyle(totalStyle);
        
        Cell totalCell1 = totalRow.createCell(1);
        totalCell1.setCellValue(totalImpressoras);
        totalCell1.setCellStyle(totalStyle);
        
        Cell totalCell2 = totalRow.createCell(2);
        totalCell2.setCellValue(totalImpressoesMes.doubleValue());
        totalCell2.setCellStyle(totalStyle);
        
        Cell totalCell3 = totalRow.createCell(3);
        totalCell3.setCellValue(custoTotalGeral.doubleValue());
        totalCell3.setCellStyle(totalStyle);
        
        // Ajustar largura das colunas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 1000);
        }
    }
    
    /**
     * Cria aba para cada secretaria
     */
    private void criarAbaSecretaria(Workbook workbook, String secretaria, 
                                   List<Impressora> impressoras,
                                   CellStyle headerStyle, CellStyle dataStyle,
                                   CellStyle currencyStyle, CellStyle dateStyle,
                                   CellStyle totalStyle) {
        
        Sheet sheet = workbook.createSheet(secretaria);
        
        int rowNum = 0;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        
        // Título
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue(secretaria + " - IMPRESSORAS");
        titleCell.setCellStyle(headerStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        
        rowNum++; // Linha em branco
        
        // Cabeçalho
        Row headerRow = sheet.createRow(rowNum++);
        String[] headers = {"Local", "Modelo", "Nº Série", "Contador Atual", 
                           "Impr./Mês", "Custo/Pág.", "Custo Mensal", "Último Rel.", "Status"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Dados
        BigDecimal totalContador = BigDecimal.ZERO;
        BigDecimal totalImpressoesMes = BigDecimal.ZERO;
        BigDecimal totalCustoMensal = BigDecimal.ZERO;
        
        for (Impressora imp : impressoras) {
            boolean incluida = imp.getIncluirNoCalculo() != null && imp.getIncluirNoCalculo();
            Row dataRow = sheet.createRow(rowNum++);
            
            // Local
            Cell cell0 = dataRow.createCell(0);
            cell0.setCellValue(imp.getLocalInstalacao());
            cell0.setCellStyle(dataStyle);
            
            // Modelo
            Cell cell1 = dataRow.createCell(1);
            cell1.setCellValue(imp.getModeloEquipamento());
            cell1.setCellStyle(dataStyle);
            
            // Nº Série
            Cell cell2 = dataRow.createCell(2);
            cell2.setCellValue(imp.getNumeroSerie());
            cell2.setCellStyle(dataStyle);
            
            // Contador Atual — .doubleValue() pois setCellValue não aceita BigDecimal
            Cell cell3 = dataRow.createCell(3);
            cell3.setCellValue(imp.getContadorImpressoes().doubleValue());
            cell3.setCellStyle(dataStyle);
            
            // Impressões do Mês — zero se excluída do cálculo
            Cell cell4 = dataRow.createCell(4);
            cell4.setCellValue(incluida ? imp.getImpressoesDoMes().doubleValue() : 0);
            cell4.setCellStyle(dataStyle);
            
            // Custo por Página
            Cell cell5 = dataRow.createCell(5);
            if (imp.getCustoPorImpressao() != null) {
                cell5.setCellValue(imp.getCustoPorImpressao().doubleValue());
                cell5.setCellStyle(currencyStyle);
            } else {
                cell5.setCellValue("N/A");
                cell5.setCellStyle(dataStyle);
            }
            
            // Custo Mensal
            Cell cell6 = dataRow.createCell(6);
            BigDecimal custoMensal = imp.getCustoMensal();
            cell6.setCellValue(custoMensal.doubleValue());
            cell6.setCellStyle(currencyStyle);
            
            // Último Relatório
            Cell cell7 = dataRow.createCell(7);
            if (imp.getDataUltimaManutencao() != null) {
                cell7.setCellValue(imp.getDataUltimaManutencao().format(formatter));
            } else {
                cell7.setCellValue("-");
            }
            cell7.setCellStyle(dataStyle);
            
            // Status
            Cell cell8 = dataRow.createCell(8);
            cell8.setCellValue(imp.getStatus());
            cell8.setCellStyle(dataStyle);
            
            // Acumular totais com BigDecimal — impressões só se incluída
            totalContador = totalContador.add(imp.getContadorImpressoes());
            if (incluida) {
                totalImpressoesMes = totalImpressoesMes.add(imp.getImpressoesDoMes());
            }
            totalCustoMensal = totalCustoMensal.add(custoMensal);
        }
        
        // Linha de total
        Row totalRow = sheet.createRow(rowNum++);
        
        Cell totalCell0 = totalRow.createCell(0);
        totalCell0.setCellValue("TOTAIS:");
        totalCell0.setCellStyle(totalStyle);
        
        // Células vazias até o contador
        for (int i = 1; i < 3; i++) {
            totalRow.createCell(i).setCellStyle(totalStyle);
        }
        
        Cell totalCell3 = totalRow.createCell(3);
        totalCell3.setCellValue(totalContador.doubleValue());
        totalCell3.setCellStyle(totalStyle);
        
        Cell totalCell4 = totalRow.createCell(4);
        totalCell4.setCellValue(totalImpressoesMes.doubleValue());
        totalCell4.setCellStyle(totalStyle);
        
        // Célula vazia custo/página
        totalRow.createCell(5).setCellStyle(totalStyle);
        
        Cell totalCell6 = totalRow.createCell(6);
        totalCell6.setCellValue(totalCustoMensal.doubleValue());
        totalCell6.setCellStyle(totalStyle);
        
        // Ajustar largura das colunas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) + 800);
        }
    }
    
    /**
     * Cria estilo para cabeçalho
     */
    private CellStyle criarEstiloCabecalho(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Cor de fundo (roxo)
        style.setFillForegroundColor(IndexedColors.VIOLET.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Borda
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Fonte
        Font font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        // Alinhamento
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Cria estilo para dados
     */
    private CellStyle criarEstiloDados(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Borda
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Cor da borda
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        
        // Alinhamento
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Cria estilo para valores monetários
     */
    private CellStyle criarEstiloMoeda(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Borda
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Cor da borda
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        
        // Formato de moeda brasileira
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("R$ #,##0.00"));
        
        // Alinhamento
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Cria estilo para datas
     */
    private CellStyle criarEstiloData(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Borda
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Alinhamento
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
    
    /**
     * Cria estilo para linha de totais
     */
    private CellStyle criarEstiloTotal(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        
        // Cor de fundo (cinza claro)
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // Borda
        style.setBorderBottom(BorderStyle.MEDIUM);
        style.setBorderTop(BorderStyle.MEDIUM);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        
        // Fonte
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        
        // Alinhamento
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        
        return style;
    }
}