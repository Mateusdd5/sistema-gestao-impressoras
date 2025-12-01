<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Impressora" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.time.LocalDate" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Relatório de Impressoras</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        /* Estilos para tela */
        body {
            background: #f8f9fa;
            padding: 20px;
        }

        .relatorio-container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            padding: 40px;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            border-radius: 10px;
        }

        .cabecalho {
            text-align: center;
            margin-bottom: 30px;
            padding-bottom: 20px;
            border-bottom: 3px solid #667eea;
        }

        .cabecalho h1 {
            color: #667eea;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .info-relatorio {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
            padding: 15px;
            background: #f8f9fa;
            border-radius: 8px;
        }

        .table {
            font-size: 14px;
        }

        .table thead th {
            background: #667eea;
            color: white;
            font-weight: 600;
            border: none;
        }

        .table tbody tr:nth-child(even) {
            background: #f8f9fa;
        }

        .rodape {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 2px solid #dee2e6;
            text-align: center;
            color: #6c757d;
            font-size: 12px;
        }

        .botao-imprimir {
            text-align: center;
            margin-bottom: 20px;
        }

        .badge-status {
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            font-weight: 600;
        }

        .badge-operante {
            background: #28a745;
            color: white;
        }

        .badge-manutencao {
            background: #ffc107;
            color: #000;
        }

        /* Estilos para impressão */
        @media print {
            body {
                background: white;
                padding: 0;
            }

            .relatorio-container {
                box-shadow: none;
                padding: 20px;
            }

            .botao-imprimir {
                display: none !important;
            }

            .table {
                font-size: 11px;
            }

            .table thead th {
                background: #667eea !important;
                color: white !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            .badge-operante {
                background: #28a745 !important;
                color: white !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            .badge-manutencao {
                background: #ffc107 !important;
                color: #000 !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
            }

            @page {
                size: landscape;
                margin: 1cm;
            }
        }
    </style>
</head>
<body>
<%
    @SuppressWarnings("unchecked")
    List<Impressora> listaImpressoras = (List<Impressora>) request.getAttribute("listaImpressoras");
    String secretariaFiltro = (String) request.getAttribute("secretariaFiltro");
    
    if (secretariaFiltro == null) secretariaFiltro = "TODAS";
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    LocalDate dataAtual = LocalDate.now();
    
    int totalImpressoras = (listaImpressoras != null) ? listaImpressoras.size() : 0;
    int totalOperantes = 0;
    int totalManutencao = 0;
    long totalImpressoesMes = 0;
    
    if (listaImpressoras != null) {
        for (Impressora imp : listaImpressoras) {
            if ("Operante".equals(imp.getStatus())) {
                totalOperantes++;
            } else {
                totalManutencao++;
            }
            totalImpressoesMes += imp.getImpressoesDoMes();
        }
    }
%>

<div class="relatorio-container">
    <!-- Botão Imprimir (não aparece na impressão) -->
    <div class="botao-imprimir">
        <button onclick="window.print()" class="btn btn-primary btn-lg">
            <i class="bi bi-printer"></i> Imprimir Relatório
        </button>
        <button onclick="window.close()" class="btn btn-secondary btn-lg">
            Fechar
        </button>
    </div>

    <!-- Cabeçalho -->
    <div class="cabecalho">
        <h1>📊 RELATÓRIO DE IMPRESSORAS</h1>
        <h5>Sistema de Controle de Impressoras</h5>
    </div>

    <!-- Informações do Relatório -->
    <div class="info-relatorio">
        <div>
            <strong>Data do Relatório:</strong> <%= dataAtual.format(formatter) %>
        </div>
        <div>
            <strong>Filtro:</strong> 
            <% if ("TODAS".equals(secretariaFiltro)) { %>
                Todas as Secretarias
            <% } else { %>
                <%= secretariaFiltro %>
            <% } %>
        </div>
        <div>
            <strong>Total de Impressoras:</strong> <%= totalImpressoras %>
        </div>
    </div>

    <!-- Resumo Estatístico -->
    <div class="row mb-4">
        <div class="col-md-4">
            <div class="card text-center border-success">
                <div class="card-body">
                    <h5 class="card-title text-success">Operantes</h5>
                    <h2><%= totalOperantes %></h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center border-warning">
                <div class="card-body">
                    <h5 class="card-title text-warning">Em Manutenção</h5>
                    <h2><%= totalManutencao %></h2>
                </div>
            </div>
        </div>
        <div class="col-md-4">
            <div class="card text-center border-info">
                <div class="card-body">
                    <h5 class="card-title text-info">Impressões do Mês</h5>
                    <h2><%= String.format("%,d", totalImpressoesMes) %></h2>
                </div>
            </div>
        </div>
    </div>

    <!-- Tabela de Impressoras -->
    <% if (listaImpressoras != null && !listaImpressoras.isEmpty()) { %>
        <table class="table table-bordered table-hover">
            <thead>
                <tr>
                    <th style="width: 8%;">Secretaria</th>
                    <th style="width: 15%;">Local</th>
                    <th style="width: 15%;">Modelo</th>
                    <th style="width: 12%;">Nº Série</th>
                    <th style="width: 10%;">Contador</th>
                    <th style="width: 10%;">Impressões Mês</th>
                    <th style="width: 12%;">Último Relatório</th>
                    <th style="width: 10%;">Status</th>
                </tr>
            </thead>
            <tbody>
                <% for (Impressora imp : listaImpressoras) { %>
                    <tr>
                        <td><strong><%= imp.getSecretaria() %></strong></td>
                        <td><%= imp.getLocalInstalacao() %></td>
                        <td><%= imp.getModeloEquipamento() %></td>
                        <td><small><%= imp.getNumeroSerie() %></small></td>
                        <td class="text-end"><strong><%= String.format("%,d", imp.getContadorImpressoes()) %></strong></td>
                        <td class="text-end">
                            <% if (imp.getContadorAnterior() != null && imp.getContadorAnterior() > 0) { %>
                                <%= String.format("%,d", imp.getImpressoesDoMes()) %>
                            <% } else { %>
                                -
                            <% } %>
                        </td>
                        <td class="text-center">
                            <% if (imp.getDataUltimaManutencao() != null) { %>
                                <%= imp.getDataUltimaManutencao().format(formatter) %>
                            <% } else { %>
                                -
                            <% } %>
                        </td>
                        <td class="text-center">
                            <% if ("Operante".equals(imp.getStatus())) { %>
                                <span class="badge-status badge-operante">✓ Operante</span>
                            <% } else { %>
                                <span class="badge-status badge-manutencao">⚠ Manutenção</span>
                            <% } %>
                        </td>
                    </tr>
                <% } %>
            </tbody>
            <tfoot>
                <tr style="background: #f8f9fa; font-weight: bold;">
                    <td colspan="4" class="text-end">TOTAIS:</td>
                    <td class="text-end">
                        <%= String.format("%,d", listaImpressoras.stream()
                            .mapToInt(Impressora::getContadorImpressoes).sum()) %>
                    </td>
                    <td class="text-end">
                        <%= String.format("%,d", totalImpressoesMes) %>
                    </td>
                    <td colspan="2"></td>
                </tr>
            </tfoot>
        </table>
    <% } else { %>
        <div class="alert alert-warning text-center">
            Nenhuma impressora encontrada para o filtro selecionado.
        </div>
    <% } %>

    <!-- Rodapé -->
    <div class="rodape">
        <p><strong>Sistema de Controle de Impressoras</strong></p>
        <p>Relatório gerado em: <%= dataAtual.format(formatter) %></p>
        <p>Este documento é gerado automaticamente pelo sistema.</p>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>