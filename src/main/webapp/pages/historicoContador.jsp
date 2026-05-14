<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.HistoricoContador" %>
<%@ page import="model.Usuario" %>
<%@ page import="utils.SessaoUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.DecimalFormat" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Histórico de Contadores</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0 0 0;
        }

        .user-navbar {
            background: white;
            border-radius: 15px;
            padding: 15px 25px;
            margin-bottom: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .user-info { display: flex; align-items: center; gap: 15px; }

        .user-avatar {
            width: 45px; height: 45px;
            border-radius: 50%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            display: flex; align-items: center; justify-content: center;
            color: white; font-weight: bold; font-size: 1.2rem;
        }

        .user-details h6 { margin: 0; font-weight: 600; color: #495057; }
        .user-details small { color: #6c757d; }
        .navbar-actions { display: flex; gap: 10px; }

        .main-wrapper {
            max-width: 1800px;
            margin: 0 auto;
            padding: 0 20px 40px 20px;
        }

        .card {
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.3);
            border: none;
            background: white;
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 20px 20px 0 0 !important;
            padding: 20px;
        }

        .btn { border-radius: 10px; }

        .table-container {
            overflow-x: auto;
            overflow-y: auto;
            max-height: calc(100vh - 300px);
        }

        .table thead th {
            background: #f8f9fa;
            color: #495057;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
            white-space: nowrap;
        }

        .table tbody tr:hover { background: #f8f9fa; }

        .badge-editado {
            background: #fd7e14;
            color: white;
            font-size: 10px;
            padding: 3px 7px;
            border-radius: 6px;
        }

        .btn-editar-inline {
            padding: 2px 8px;
            font-size: 12px;
            border-radius: 6px;
        }

        .input-contador-inline {
            width: 120px;
            display: inline-block;
            border-radius: 6px;
            border: 2px solid #667eea;
            padding: 2px 6px;
            font-size: 13px;
        }

        @media print {
            body { background: white !important; padding: 0; }
            .no-print { display: none !important; }
            .user-navbar { display: none !important; }
            .card { box-shadow: none !important; border-radius: 0 !important; }
            .card-header {
                background: #667eea !important;
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
                border-radius: 0 !important;
            }
            .table-container { max-height: none !important; overflow: visible !important; }
            .main-wrapper { padding: 0; }
            @page { size: landscape; margin: 1cm; }
        }
    </style>
</head>
<body>
<%
    Usuario usuarioLogado = SessaoUtil.obterUsuarioLogado(request);

    @SuppressWarnings("unchecked")
    List<HistoricoContador> listaHistorico =
        (List<HistoricoContador>) request.getAttribute("listaHistorico");

    @SuppressWarnings("unchecked")
    List<String> listaSecretarias = (List<String>) request.getAttribute("listaSecretarias");

    String secretariaFiltro = (String) request.getAttribute("secretariaFiltro");
    if (secretariaFiltro == null) secretariaFiltro = "TODAS";

    Boolean filtroFixo = (Boolean) request.getAttribute("filtroFixo");
    if (filtroFixo == null) filtroFixo = false;

    String sucesso = request.getParameter("sucesso");
    String erro    = request.getParameter("erro");

    boolean isAdmin      = usuarioLogado != null && usuarioLogado.isAdmin();
    // Badge de "editado" visível apenas para técnico, operador e admin
    boolean verEditados  = usuarioLogado != null && !usuarioLogado.isViewer();

    DecimalFormat dfContador = new DecimalFormat("#,##0.##");
%>

<!-- Navbar -->
<div class="container-fluid" style="max-width:1800px; padding:0 20px;">
    <div class="user-navbar no-print">
        <div class="user-info">
            <div class="user-avatar">
                <%= usuarioLogado.getNomeCompleto().substring(0,1).toUpperCase() %>
            </div>
            <div class="user-details">
                <h6><%= usuarioLogado.getNomeCompleto() %></h6>
                <small>
                    <i class="bi bi-shield-check"></i>
                    <%= usuarioLogado.getNivelPermissaoDescricao() %>
                    <% if (usuarioLogado.isUsuarioSecretaria()) { %>
                        &nbsp;&mdash;&nbsp;<i class="bi bi-building"></i> <%= usuarioLogado.getSecretariaVinculada() %>
                    <% } %>
                </small>
            </div>
        </div>
        <div class="navbar-actions">
            <a href="<%= request.getContextPath() %>/ImpressoraController" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left"></i> Voltar
            </a>
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

<div class="main-wrapper">
    <div class="card">
        <div class="card-header text-white">
            <div class="d-flex justify-content-between align-items-center flex-wrap gap-2">
                <h4 class="mb-0">
                    <i class="bi bi-clock-history"></i> Histórico de Contadores
                    <% if (filtroFixo) { %>
                        &nbsp;
                        <span style="background:rgba(255,255,255,0.2); border-radius:8px;
                                     padding:4px 12px; font-size:0.75rem; font-weight:600;">
                            <i class="bi bi-building"></i> <%= secretariaFiltro %>
                        </span>
                    <% } %>
                </h4>
                <div class="d-flex gap-2 flex-wrap no-print">
                    <!-- Filtro por secretaria: oculto para usuários de secretaria -->
                    <% if (!filtroFixo) { %>
                        <form action="<%= request.getContextPath() %>/HistoricoContadorServlet" method="get"
                              class="d-flex gap-2 align-items-center">
                            <select name="secretaria" class="form-select form-select-sm"
                                    style="width:auto; border-radius:8px;"
                                    onchange="this.form.submit()">
                                <option value="TODAS" <%= "TODAS".equals(secretariaFiltro) ? "selected" : "" %>>
                                    Todas as Secretarias
                                </option>
                                <% if (listaSecretarias != null) {
                                    for (String sec : listaSecretarias) { %>
                                        <option value="<%= sec %>"
                                                <%= sec.equals(secretariaFiltro) ? "selected" : "" %>>
                                            <%= sec %>
                                        </option>
                                <%  }
                                } %>
                            </select>
                        </form>
                    <% } %>
                    <button onclick="window.print()" class="btn btn-light btn-sm">
                        <i class="bi bi-printer"></i> Exportar PDF
                    </button>
                </div>
            </div>
        </div>

        <div class="card-body p-4">

            <!-- Alertas -->
            <% if ("registro_editado".equals(sucesso)) { %>
                <div class="alert alert-success alert-dismissible fade show no-print">
                    <i class="bi bi-check-circle"></i> Registro atualizado com sucesso.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>
            <% if (erro != null) { %>
                <div class="alert alert-danger alert-dismissible fade show no-print">
                    <i class="bi bi-exclamation-triangle"></i> Erro ao processar a operação.
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Info de filtro ativo -->
            <div class="mb-3">
                <% if (!"TODAS".equals(secretariaFiltro)) { %>
                    <span class="badge bg-primary fs-6">
                        <i class="bi bi-building"></i> <%= secretariaFiltro %>
                    </span>
                <% } else { %>
                    <span class="badge bg-secondary fs-6">
                        <i class="bi bi-grid"></i> Todas as Secretarias
                    </span>
                <% } %>
                <span class="ms-2 text-muted">
                    <strong><%= listaHistorico != null ? listaHistorico.size() : 0 %></strong> registro(s)
                </span>
                <% if (isAdmin) { %>
                    <span class="ms-3 text-muted no-print" style="font-size:0.82rem;">
                        <i class="bi bi-pencil-square"></i>
                        Clique no ícone de edição para corrigir um valor de contador.
                    </span>
                <% } %>
            </div>

            <!-- Tabela -->
            <div class="table-container">
                <% if (listaHistorico != null && !listaHistorico.isEmpty()) { %>
                    <table class="table table-hover table-bordered">
                        <thead>
                            <tr>
                                <th>Secretaria</th>
                                <th>Local</th>
                                <th>Modelo</th>
                                <th>Nº Série</th>
                                <th>Data do Relatório</th>
                                <th class="text-end">Contador</th>
                                <% if (verEditados) { %>
                                    <th class="text-center">Editado?</th>
                                <% } %>
                                <th>Gravado em</th>
                                <% if (isAdmin) { %>
                                    <th class="text-center no-print">Ação</th>
                                <% } %>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (HistoricoContador h : listaHistorico) { %>
                                <tr id="linha-<%= h.getId() %>">
                                    <td>
                                        <span class="badge bg-primary">
                                            <%= h.getSecretaria() %>
                                        </span>
                                    </td>
                                    <td><%= h.getLocalInstalacao() %></td>
                                    <td><%= h.getModeloEquipamento() %></td>
                                    <td><small class="text-muted"><%= h.getNumeroSerie() %></small></td>
                                    <td style="white-space:nowrap;">
                                        <i class="bi bi-calendar-check text-success"></i>
                                        <%= h.getDataRelatorioFormatada() %>
                                    </td>
                                    <td class="text-end">
                                        <strong id="valor-<%= h.getId() %>">
                                            <%= dfContador.format(h.getContadorValor()) %>
                                        </strong>
                                        <% if (isAdmin) { %>
                                            <input type="number"
                                                   id="input-<%= h.getId() %>"
                                                   class="input-contador-inline no-print"
                                                   style="display:none;"
                                                   value="<%= h.getContadorValor().toPlainString() %>"
                                                   step="0.01" min="0">
                                        <% } %>
                                    </td>
                                    <% if (verEditados) { %>
                                        <td class="text-center">
                                            <% if (Boolean.TRUE.equals(h.getEditadoManual())) { %>
                                                <span class="badge-editado"
                                                      title="Editado por <%= h.getEditadoPor() %>">
                                                    ✎ <%= h.getEditadoPor() %>
                                                </span>
                                            <% } else { %>
                                                <span class="text-muted">—</span>
                                            <% } %>
                                        </td>
                                    <% } %>
                                    <td style="white-space:nowrap;">
                                        <small class="text-muted"><%= h.getDataGravacaoFormatada() %></small>
                                    </td>
                                    <% if (isAdmin) { %>
                                        <td class="text-center no-print">
                                            <button class="btn btn-sm btn-warning btn-editar-inline"
                                                    id="btn-editar-<%= h.getId() %>"
                                                    onclick="iniciarEdicao(<%= h.getId() %>)"
                                                    title="Editar contador">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <button class="btn btn-sm btn-success btn-editar-inline"
                                                    id="btn-salvar-<%= h.getId() %>"
                                                    style="display:none;"
                                                    onclick="salvarEdicao(<%= h.getId() %>, '<%= secretariaFiltro %>')"
                                                    title="Salvar">
                                                <i class="bi bi-check-lg"></i>
                                            </button>
                                            <button class="btn btn-sm btn-secondary btn-editar-inline"
                                                    id="btn-cancelar-<%= h.getId() %>"
                                                    style="display:none;"
                                                    onclick="cancelarEdicao(<%= h.getId() %>)"
                                                    title="Cancelar">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                        </td>
                                    <% } %>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <div class="alert alert-info text-center">
                        <i class="bi bi-info-circle" style="font-size:2rem;"></i>
                        <h5 class="mt-2">Nenhum registro de histórico encontrado</h5>
                        <p>Os registros são criados automaticamente quando a data do relatório
                           de uma impressora é atualizada.</p>
                    </div>
                <% } %>
            </div>

            <!-- Legenda -->
            <div class="mt-3 no-print">
                <small class="text-muted">
                    <i class="bi bi-info-circle"></i>
                    Cada impressora armazena até <strong>60 registros</strong> (5 anos mensais).
                    Um novo registro é criado automaticamente quando a data do relatório é avançada.
                    Corrigir o contador sem alterar a data atualiza o registro existente daquela data.
                    <% if (verEditados) { %>
                        <br>
                        <i class="bi bi-pencil-square"></i>
                        Registros com <span class="badge-editado">✎</span> foram editados manualmente por um administrador.
                    <% } %>
                </small>
            </div>
        </div>
    </div>
</div>

<!-- Rodapé -->
<footer style="
    max-width:100%; margin:0; padding:30px 20px 25px 20px;
    text-align:center;
    background: linear-gradient(to bottom, transparent, rgba(255,255,255,0.12));
    border:none; color:rgba(255,255,255,0.85); font-size:0.85rem;">
    <p style="margin:0; font-weight:600; letter-spacing:0.5px;">
        <i class="bi bi-printer-fill"></i> Sistema de Controle de Impressoras
    </p>
    <p style="margin:6px 0 0 0; opacity:0.75;">
        Prefeitura Municipal de Dias D'ávila &mdash; Tecnologia da Informação
    </p>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
function iniciarEdicao(id) {
    document.getElementById('valor-'        + id).style.display = 'none';
    document.getElementById('input-'        + id).style.display = 'inline-block';
    document.getElementById('btn-editar-'   + id).style.display = 'none';
    document.getElementById('btn-salvar-'   + id).style.display = 'inline-block';
    document.getElementById('btn-cancelar-' + id).style.display = 'inline-block';
    document.getElementById('input-' + id).focus();
}

function cancelarEdicao(id) {
    document.getElementById('valor-'        + id).style.display = 'inline';
    document.getElementById('input-'        + id).style.display = 'none';
    document.getElementById('btn-editar-'   + id).style.display = 'inline-block';
    document.getElementById('btn-salvar-'   + id).style.display = 'none';
    document.getElementById('btn-cancelar-' + id).style.display = 'none';
}

function salvarEdicao(id, secretariaFiltro) {
    const novoValor = document.getElementById('input-' + id).value;

    if (!novoValor || isNaN(novoValor) || parseFloat(novoValor) < 0) {
        alert('Valor inválido. Informe um número maior ou igual a zero.');
        return;
    }

    if (!confirm('Confirma a alteração do contador para ' +
                 parseFloat(novoValor).toLocaleString('pt-BR') +
                 '?\n\nEsta ação ficará registrada com seu usuário.')) {
        return;
    }

    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '<%= request.getContextPath() %>/HistoricoContadorServlet';

    const campos = {
        action:           'editar',
        id:               id,
        contadorValor:    novoValor,
        secretariaFiltro: secretariaFiltro
    };

    for (const [name, value] of Object.entries(campos)) {
        const input = document.createElement('input');
        input.type  = 'hidden';
        input.name  = name;
        input.value = value;
        form.appendChild(input);
    }

    document.body.appendChild(form);
    form.submit();
}
</script>
</body>
</html>