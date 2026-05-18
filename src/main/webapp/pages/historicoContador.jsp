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

        /* Cabeçalho de grupo por impressora */
        .grupo-impressora-header {
            background: linear-gradient(135deg, #f0f0ff 0%, #e8e8f8 100%);
            border-left: 4px solid #667eea;
            padding: 8px 14px;
            margin-top: 18px;
            margin-bottom: 2px;
            border-radius: 6px;
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .grupo-impressora-header:first-child {
            margin-top: 0;
        }

        .grupo-impressora-header .info-impressora {
            font-weight: 600;
            color: #495057;
            font-size: 0.92rem;
        }

        .grupo-impressora-header .serie-impressora {
            color: #6c757d;
            font-size: 0.82rem;
        }

        .table-grupo {
            margin-bottom: 0;
            border-radius: 0 0 8px 8px;
            overflow: hidden;
        }

        .table-grupo thead th {
            background: #f8f9fa;
            color: #495057;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
            white-space: nowrap;
            font-size: 0.88rem;
            padding: 8px 10px;
        }

        .table-grupo tbody td {
            font-size: 0.9rem;
            padding: 8px 10px;
            vertical-align: middle;
        }

        .table-grupo tbody tr:hover { background: #f8f9fa; }

        .badge-editado {
            background: #fd7e14;
            color: white;
            font-size: 10px;
            padding: 3px 7px;
            border-radius: 6px;
        }

        .btn-acao-inline {
            padding: 2px 7px;
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

        .wrapper-grupo {
            margin-bottom: 8px;
            border: 1px solid #dee2e6;
            border-radius: 8px;
            overflow: hidden;
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

    boolean isAdmin     = usuarioLogado != null && usuarioLogado.isAdmin();
    boolean verEditados = usuarioLogado != null && !usuarioLogado.isViewer();

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
            <% if ("registro_deletado".equals(sucesso)) { %>
                <div class="alert alert-success alert-dismissible fade show no-print">
                    <i class="bi bi-check-circle"></i> Registro removido com sucesso.
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
                        Use os botões à direita para editar ou remover um registro.
                    </span>
                <% } %>
            </div>

            <!-- Conteúdo agrupado por impressora -->
            <%
            if (listaHistorico != null && !listaHistorico.isEmpty()) {
                String serieAtual = "";
                boolean primeiroGrupo = true;

                for (int idx = 0; idx < listaHistorico.size(); idx++) {
                    HistoricoContador h = listaHistorico.get(idx);

                    // Detecta mudança de impressora
                    boolean novaImpressora = !h.getNumeroSerie().equals(serieAtual);

                    if (novaImpressora) {
                        // Fecha a tabela do grupo anterior (se não for o primeiro)
                        if (!primeiroGrupo) { %>
                                </tbody>
                            </table>
                        </div>
                    <%  }

                        serieAtual = h.getNumeroSerie();
                        primeiroGrupo = false;
            %>
                    <!-- Cabeçalho do grupo -->
                    <div class="wrapper-grupo">
                        <div class="grupo-impressora-header">
                            <span class="badge bg-primary"><%= h.getSecretaria() %></span>
                            <span class="info-impressora">
                                <i class="bi bi-printer"></i>
                                <%= h.getLocalInstalacao() %> &mdash; <%= h.getModeloEquipamento() %>
                            </span>
                            <span class="serie-impressora">
                                <i class="bi bi-upc-scan"></i> <%= h.getNumeroSerie() %>
                            </span>
                        </div>

                        <!-- Tabela do grupo -->
                        <table class="table table-hover table-grupo mb-0">
                            <thead>
                                <tr>
                                    <th>Data do Relatório</th>
                                    <th class="text-end">Contador</th>
                                    <% if (verEditados) { %><th class="text-center">Editado?</th><% } %>
                                    <th>Gravado em</th>
                                    <% if (isAdmin) { %><th class="text-center no-print">Ações</th><% } %>
                                </tr>
                            </thead>
                            <tbody>
            <%  } // fim novaImpressora %>

                                <!-- Linha de registro -->
                                <tr id="linha-<%= h.getId() %>">
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
                                            <!-- Botão editar -->
                                            <button class="btn btn-sm btn-warning btn-acao-inline"
                                                    id="btn-editar-<%= h.getId() %>"
                                                    onclick="iniciarEdicao(<%= h.getId() %>)"
                                                    title="Editar contador">
                                                <i class="bi bi-pencil"></i>
                                            </button>
                                            <!-- Botão salvar (oculto) -->
                                            <button class="btn btn-sm btn-success btn-acao-inline"
                                                    id="btn-salvar-<%= h.getId() %>"
                                                    style="display:none;"
                                                    onclick="salvarEdicao(<%= h.getId() %>, '<%= secretariaFiltro %>')"
                                                    title="Salvar">
                                                <i class="bi bi-check-lg"></i>
                                            </button>
                                            <!-- Botão cancelar (oculto) -->
                                            <button class="btn btn-sm btn-secondary btn-acao-inline"
                                                    id="btn-cancelar-<%= h.getId() %>"
                                                    style="display:none;"
                                                    onclick="cancelarEdicao(<%= h.getId() %>)"
                                                    title="Cancelar">
                                                <i class="bi bi-x-lg"></i>
                                            </button>
                                            <!-- Botão deletar -->
                                            <button class="btn btn-sm btn-danger btn-acao-inline"
                                                    id="btn-deletar-<%= h.getId() %>"
                                                    onclick="deletarRegistro(<%= h.getId() %>, '<%= secretariaFiltro %>')"
                                                    title="Remover registro">
                                                <i class="bi bi-trash"></i>
                                            </button>
                                        </td>
                                    <% } %>
                                </tr>

            <%  } // fim for

                // Fecha a última tabela aberta
                if (!listaHistorico.isEmpty()) { %>
                            </tbody>
                        </table>
                    </div>
            <%  }
            } else { %>
                <div class="alert alert-info text-center">
                    <i class="bi bi-info-circle" style="font-size:2rem;"></i>
                    <h5 class="mt-2">Nenhum registro de histórico encontrado</h5>
                    <p>Os registros são criados automaticamente quando a data do relatório
                       de uma impressora é atualizada.</p>
                </div>
            <% } %>

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
    document.getElementById('valor-'          + id).style.display = 'none';
    document.getElementById('input-'          + id).style.display = 'inline-block';
    document.getElementById('btn-editar-'     + id).style.display = 'none';
    document.getElementById('btn-salvar-'     + id).style.display = 'inline-block';
    document.getElementById('btn-cancelar-'   + id).style.display = 'inline-block';
    document.getElementById('btn-deletar-'    + id).style.display = 'none';
    document.getElementById('input-' + id).focus();
}

function cancelarEdicao(id) {
    document.getElementById('valor-'          + id).style.display = 'inline';
    document.getElementById('input-'          + id).style.display = 'none';
    document.getElementById('btn-editar-'     + id).style.display = 'inline-block';
    document.getElementById('btn-salvar-'     + id).style.display = 'none';
    document.getElementById('btn-cancelar-'   + id).style.display = 'none';
    document.getElementById('btn-deletar-'    + id).style.display = 'inline-block';
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

    submeterFormulario({
        action:           'editar',
        id:               id,
        contadorValor:    novoValor,
        secretariaFiltro: secretariaFiltro
    });
}

function deletarRegistro(id, secretariaFiltro) {
    if (!confirm('Tem certeza que deseja remover este registro do histórico?\n\nEsta ação não pode ser desfeita.')) {
        return;
    }

    submeterFormulario({
        action:           'deletar',
        id:               id,
        secretariaFiltro: secretariaFiltro
    });
}

function submeterFormulario(campos) {
    const form = document.createElement('form');
    form.method = 'POST';
    form.action = '<%= request.getContextPath() %>/HistoricoContadorServlet';

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