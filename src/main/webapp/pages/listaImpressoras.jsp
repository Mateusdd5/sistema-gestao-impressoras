<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Impressora" %>
<%@ page import="model.Usuario" %>
<%@ page import="utils.SessaoUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.text.NumberFormat" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Controle de Impressoras</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Ícones -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }

        /* Navbar de usuário */
        .user-navbar {
            background: white;
            border-radius: 15px;
            padding: 15px 25px;
            margin-bottom: 20px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .user-info {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .user-avatar {
            width: 45px;
            height: 45px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            color: white;
            font-weight: bold;
            font-size: 1.2rem;
        }

        .user-details h6 {
            margin: 0;
            font-weight: 600;
            color: #495057;
        }

        .user-details small {
            color: #6c757d;
        }

        .navbar-actions {
            display: flex;
            gap: 10px;
        }

        .main-container {
            display: flex;
            gap: 20px;
            max-width: 1800px;
            margin: 0 auto;
            padding: 0 20px;
        }

        /* Sidebar de Secretarias */
        .sidebar {
            width: 280px;
            flex-shrink: 0;
        }

        .sidebar-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            padding: 20px;
            position: sticky;
            top: 20px;
        }

        .sidebar-card h5 {
            color: #667eea;
            font-weight: bold;
            margin-bottom: 20px;
            text-align: center;
        }

        .secretaria-btn {
            width: 100%;
            text-align: left;
            padding: 12px 15px;
            margin-bottom: 8px;
            border: 2px solid #e0e0e0;
            background: white;
            border-radius: 10px;
            transition: all 0.3s ease;
            font-size: 14px;
            font-weight: 500;
            color: #495057;
        }

        .secretaria-btn:hover {
            background: #f8f9fa;
            border-color: #667eea;
            color: #667eea;
            transform: translateX(5px);
        }

        .secretaria-btn.active {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            border-color: #667eea;
            font-weight: bold;
        }

        .secretaria-btn i {
            margin-right: 8px;
        }

        /* Conteúdo Principal */
        .content {
            flex: 1;
            min-width: 0;
        }

        .card {
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            border: none;
            background: white;
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 20px 20px 0 0 !important;
            padding: 20px;
        }

        .btn {
            border-radius: 10px;
        }

        .table-container {
            overflow-x: auto;
        }

        .table {
            margin-bottom: 0;
        }

        .table thead th {
            background: #f8f9fa;
            color: #495057;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
            white-space: nowrap;
        }

        .table tbody tr {
            transition: all 0.3s ease;
        }

        .table tbody tr:hover {
            background: #f8f9fa;
            transform: scale(1.01);
        }

        .badge {
            padding: 6px 12px;
            border-radius: 8px;
            font-weight: 500;
        }

        .badge-operante {
            background: #28a745;
            color: white;
        }

        .badge-manutencao {
            background: #ffc107;
            color: #000;
        }

        .action-buttons .btn {
            padding: 5px 10px;
            font-size: 14px;
            margin: 0 2px;
        }

        .search-box {
            border-radius: 10px;
            border: 2px solid #e0e0e0;
            padding: 10px 15px;
        }

        .search-box:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .custo-destaque {
            font-weight: bold;
            color: #28a745;
        }

        @media (max-width: 992px) {
            .main-container {
                flex-direction: column;
            }

            .sidebar {
                width: 100%;
            }

            .sidebar-card {
                position: relative;
                top: 0;
            }
        }

        .export-buttons {
            display: flex;
            gap: 10px;
            flex-wrap: wrap;
        }

        .export-buttons .btn {
            display: flex;
            align-items: center;
            gap: 8px;
        }
    </style>
</head>
<body>
<%
    // Obter usuário logado
    Usuario usuarioLogado = SessaoUtil.obterUsuarioLogado(request);
    
    @SuppressWarnings("unchecked")
    List<Impressora> listaImpressoras = (List<Impressora>) request.getAttribute("listaImpressoras");
    
    @SuppressWarnings("unchecked")
    List<String> listaSecretarias = (List<String>) request.getAttribute("listaSecretarias");
    
    Integer totalResultados = (Integer) request.getAttribute("totalResultados");
    Boolean temFiltro = (Boolean) request.getAttribute("temFiltro");
    String filtroAtual = (String) request.getAttribute("filtroAtual");
    String secretariaSelecionada = (String) request.getAttribute("secretariaSelecionada");
    
    if (totalResultados == null) totalResultados = 0;
    if (temFiltro == null) temFiltro = false;
    if (filtroAtual == null) filtroAtual = "";
    if (secretariaSelecionada == null) secretariaSelecionada = "TODAS";
    
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
%>

<!-- Navbar de usuário -->
<div class="container-fluid" style="max-width: 1800px; padding: 0 20px;">
    <div class="user-navbar">
        <div class="user-info">
            <div class="user-avatar">
                <%= usuarioLogado.getNomeCompleto().substring(0, 1).toUpperCase() %>
            </div>
            <div class="user-details">
                <h6><%= usuarioLogado.getNomeCompleto() %></h6>
                <small>
                    <i class="bi bi-shield-check"></i>
                    <%= usuarioLogado.getNivelPermissaoDescricao() %>
                </small>
            </div>
        </div>
        <div class="navbar-actions">
            <% if (usuarioLogado.podeGerenciarUsuarios()) { %>
                <a href="<%= request.getContextPath() %>/UsuarioController" class="btn btn-outline-primary">
                    <i class="bi bi-people"></i> Usuários
                </a>
            <% } %>
            <a href="<%= request.getContextPath() %>/logout" class="btn btn-outline-danger">
                <i class="bi bi-box-arrow-right"></i> Sair
            </a>
        </div>
    </div>
</div>

<div class="main-container">
    <!-- Sidebar de Secretarias -->
    <div class="sidebar">
        <div class="sidebar-card">
            <h5><i class="bi bi-funnel"></i> Filtrar por Secretaria</h5>
            
            <form action="ImpressoraController" method="get">
                <input type="hidden" name="action" value="filtrarSecretaria">
                
                <button type="submit" name="secretaria" value="TODAS" 
                        class="secretaria-btn <%= "TODAS".equals(secretariaSelecionada) ? "active" : "" %>">
                    <i class="bi bi-grid"></i> TODAS AS SECRETARIAS
                </button>
                
                <% if (listaSecretarias != null && !listaSecretarias.isEmpty()) {
                    for (String sec : listaSecretarias) { %>
                        <button type="submit" name="secretaria" value="<%= sec %>" 
                                class="secretaria-btn <%= sec.equals(secretariaSelecionada) ? "active" : "" %>">
                            <i class="bi bi-building"></i> <%= sec %>
                        </button>
                    <% }
                } %>
            </form>
        </div>
    </div>

    <!-- Conteúdo Principal -->
    <div class="content">
        <div class="card">
            <div class="card-header text-white">
                <div class="d-flex justify-content-between align-items-center flex-wrap">
                    <h4 class="mb-0">
                        <i class="bi bi-printer"></i> Controle de Impressoras
                    </h4>
                    <div class="export-buttons">
                        <% if (usuarioLogado.podeCadastrar()) { %>
                            <a href="<%= request.getContextPath() %>/ImpressoraController?action=novoCadastro" class="btn btn-light">
                                <i class="bi bi-plus-circle"></i> Nova Impressora
                            </a>
                        <% } %>
                        <a href="ExportarCsvServlet" class="btn btn-success">
                            <i class="bi bi-file-earmark-spreadsheet"></i> Exportar CSV
                        </a>
                        <a href="ExportarExcelServlet" class="btn btn-success">
                            <i class="bi bi-file-earmark-excel"></i> Exportar Excel
                        </a>
                        <a href="ImpressoraController?action=relatorioImpressao&secretaria=<%= secretariaSelecionada %>" 
                           class="btn btn-info" target="_blank">
                            <i class="bi bi-printer"></i> Imprimir
                        </a>
                    </div>
                </div>
            </div>
            <div class="card-body p-4">
                <!-- Barra de Busca e Info -->
                <div class="row mb-4">
                    <div class="col-md-8">
                        <form action="ImpressoraController" method="get" class="d-flex gap-2">
                            <input type="hidden" name="action" value="buscar">
                            <input type="text" class="form-control search-box" name="filtro" 
                                   placeholder="🔍 Buscar por local, modelo, número de série..." 
                                   value="<%= filtroAtual %>">
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-search"></i> Buscar
                            </button>
                            <% if (temFiltro || !secretariaSelecionada.equals("TODAS")) { %>
                                <a href="ImpressoraController" class="btn btn-secondary">
                                    <i class="bi bi-x-circle"></i> Limpar
                                </a>
                            <% } %>
                        </form>
                    </div>
                    <div class="col-md-4 text-end">
                        <div class="alert alert-info mb-0 py-2">
                            <strong><%= totalResultados %></strong> impressora(s) encontrada(s)
                            <% if (!secretariaSelecionada.equals("TODAS")) { %>
                                <br><small>em <strong><%= secretariaSelecionada %></strong></small>
                            <% } %>
                        </div>
                    </div>
                </div>

                <!-- Tabela de Impressoras -->
                <div class="table-container">
                    <% if (listaImpressoras != null && !listaImpressoras.isEmpty()) { %>
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>Secretaria</th>
                                    <th>Local</th>
                                    <th>Modelo</th>
                                    <th>Nº Série</th>
                                    <th>Contador Atual</th>
                                    <th>Impressões/Mês</th>
                                    <th>Custo Mensal</th>
                                    <th>Último Relatório</th>
                                    <th>Status</th>
                                    <th class="text-center">Ações</th>
                                </tr>
                            </thead>
                            <tbody>
                                <% for (Impressora imp : listaImpressoras) { %>
                                    <tr>
                                        <td>
                                            <span class="badge bg-primary">
                                                <%= imp.getSecretaria() %>
                                            </span>
                                        </td>
                                        <td><%= imp.getLocalInstalacao() %></td>
                                        <td><%= imp.getModeloEquipamento() %></td>
                                        <td><small class="text-muted"><%= imp.getNumeroSerie() %></small></td>
                                        <td>
                                            <strong><%= String.format("%,d", imp.getContadorImpressoes()) %></strong>
                                        </td>
                                        <td>
                                            <% if (imp.getContadorAnterior() != null && imp.getContadorAnterior() > 0) { %>
                                                <span class="badge bg-info">
                                                    <%= String.format("%,d", imp.getImpressoesDoMes()) %>
                                                </span>
                                            <% } else { %>
                                                <span class="text-muted">-</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (imp.getCustoPorImpressao() != null && imp.getImpressoesDoMes() > 0) { %>
                                                <span class="custo-destaque">
                                                    <%= currencyFormat.format(imp.getCustoMensal()) %>
                                                </span>
                                            <% } else { %>
                                                <span class="text-muted">R$ 0,00</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if (imp.getDataUltimaManutencao() != null) { %>
                                                <i class="bi bi-calendar-check text-success"></i>
                                                <%= imp.getDataUltimaManutencao().format(formatter) %>
                                            <% } else { %>
                                                <span class="text-muted">-</span>
                                            <% } %>
                                        </td>
                                        <td>
                                            <% if ("Operante".equals(imp.getStatus())) { %>
                                                <span class="badge badge-operante">
                                                    <i class="bi bi-check-circle"></i> Operante
                                                </span>
                                            <% } else { %>
                                                <span class="badge badge-manutencao">
                                                    <i class="bi bi-wrench"></i> Em Manutenção
                                                </span>
                                            <% } %>
                                        </td>
                                        <td class="text-center action-buttons">
                                            <% if (usuarioLogado.podeEditarContadores()) { %>
                                                <a href="ImpressoraController?action=editar&id=<%= imp.getId() %>" 
                                                   class="btn btn-sm btn-warning" title="Editar">
                                                    <i class="bi bi-pencil"></i>
                                                </a>
                                            <% } %>
                                            <% if (usuarioLogado.podeDeletar()) { %>
                                                <button type="button" class="btn btn-sm btn-danger" 
                                                        onclick="confirmarExclusao(<%= imp.getId() %>, '<%= imp.getModeloEquipamento() %>')" 
                                                        title="Excluir">
                                                    <i class="bi bi-trash"></i>
                                                </button>
                                            <% } %>
                                        </td>
                                    </tr>
                                <% } %>
                            </tbody>
                        </table>
                    <% } else { %>
                        <div class="alert alert-warning text-center" role="alert">
                            <i class="bi bi-exclamation-triangle" style="font-size: 2rem;"></i>
                            <h5 class="mt-2">Nenhuma impressora encontrada</h5>
                            <% if (temFiltro || !secretariaSelecionada.equals("TODAS")) { %>
                                <p>Tente ajustar os filtros de busca.</p>
                            <% } else { %>
                                <p>Comece cadastrando a primeira impressora!</p>
                                <% if (usuarioLogado.podeCadastrar()) { %>
                                    <a href="<%= request.getContextPath() %>/ImpressoraController?action=novoCadastro" class="btn btn-primary mt-2">
                                        <i class="bi bi-plus-circle"></i> Cadastrar Impressora
                                    </a>
                                <% } %>
                            <% } %>
                        </div>
                    <% } %>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Scripts -->
<script>
function confirmarExclusao(id, modelo) {
    if (confirm('Tem certeza que deseja excluir a impressora "' + modelo + '"?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = 'ImpressoraController';

        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'deletar';
        form.appendChild(actionInput);

        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'id';
        idInput.value = id;
        form.appendChild(idInput);

        document.body.appendChild(form);
        form.submit();
    }
}
</script>
</body>
</html>