<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Impressora" %>
<%@ page import="model.Usuario" %>
<%@ page import="utils.SessaoUtil" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><%= request.getAttribute("impressora") != null ? "Editar Impressora" : "Cadastrar Impressora" %></title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px 0;
        }

        .user-navbar {
            background: white;
            border-radius: 15px;
            padding: 15px 25px;
            margin-bottom: 20px;
            box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
            display: flex;
            justify-content: space-between;
            align-items: center;
            max-width: 1200px;
            margin-left: auto;
            margin-right: auto;
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

        .container {
            max-width: 1200px;
        }

        .card {
            border-radius: 20px;
            box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
            border: none;
        }

        .card-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border-radius: 20px 20px 0 0 !important;
            padding: 20px;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }

        .form-control, .form-select {
            border-radius: 10px;
            border: 2px solid #e0e0e0;
        }

        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .form-control:disabled, .form-select:disabled {
            background-color: #f8f9fa;
            cursor: not-allowed;
        }

        .btn {
            border-radius: 10px;
        }

        .alert-warning {
            border-radius: 10px;
            border-left: 4px solid #ffc107;
        }

        .field-readonly {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        .permission-badge {
            display: inline-block;
            padding: 5px 12px;
            border-radius: 8px;
            font-size: 0.85rem;
            font-weight: 500;
            margin-left: 8px;
        }

        .badge-editable {
            background: #28a745;
            color: white;
        }

        .badge-readonly {
            background: #6c757d;
            color: white;
        }
    </style>
</head>
<body>
<%
    Usuario usuarioLogado = SessaoUtil.obterUsuarioLogado(request);

    Impressora impressora = (Impressora) request.getAttribute("impressora");
    boolean isEdicao = impressora != null;

    @SuppressWarnings("unchecked")
    List<String> listaSecretarias = (List<String>) request.getAttribute("listaSecretarias");

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter formatterExibicao = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    boolean podeEditarCadastrais = usuarioLogado.podeEditarDadosCadastrais();
    boolean podeEditarContadores = usuarioLogado.podeEditarContadores();
    boolean podeCadastrar = usuarioLogado.podeCadastrar();

    if (isEdicao && !podeEditarContadores) {
        response.sendRedirect(request.getContextPath() + "/pages/acessoNegado.jsp");
        return;
    }

    if (!isEdicao && !podeCadastrar) {
        response.sendRedirect(request.getContextPath() + "/pages/acessoNegado.jsp");
        return;
    }
%>

<div class="container-fluid" style="max-width: 1200px; padding: 0 20px;">
    <div class="user-navbar">
        <div class="user-info">
            <div class="user-avatar">
                <%= usuarioLogado.getNomeCompleto().substring(0, 1).toUpperCase() %>
            </div>
            <div class="user-details">
                <h6 style="margin: 0; font-weight: 600; color: #495057;">
                    <%= usuarioLogado.getNomeCompleto() %>
                </h6>
                <small style="color: #6c757d;">
                    <i class="bi bi-shield-check"></i>
                    <%= usuarioLogado.getNivelPermissaoDescricao() %>
                </small>
            </div>
        </div>
        <div class="navbar-actions" style="display: flex; gap: 10px;">
            <a href="<%= request.getContextPath() %>/ImpressoraController" class="btn btn-outline-primary">
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

<div class="container">
    <div class="card">
        <div class="card-header text-white">
            <h4 class="mb-0">
                <i class="bi bi-<%= isEdicao ? "pencil-square" : "plus-circle" %>"></i>
                <%= isEdicao ? "Editar Impressora" : "Cadastrar Nova Impressora" %>
            </h4>
        </div>
        <div class="card-body p-4">

            <% if (isEdicao && podeEditarContadores && !podeEditarCadastrais) { %>
                <div class="alert alert-warning">
                    <i class="bi bi-info-circle"></i>
                    <strong>Atenção:</strong> Como <strong>Operador</strong>, você pode editar apenas:
                    <strong>Contadores, Status e Datas de Relatório</strong>.
                    Os demais campos estão bloqueados.
                </div>
            <% } %>

            <form action="<%= request.getContextPath() %>/ImpressoraController" method="post">
                <input type="hidden" name="action" value="<%= isEdicao ? "editar" : "adicionar" %>">
                <% if (isEdicao) { %>
                    <input type="hidden" name="id" value="<%= impressora.getId() %>">
                <% } %>

                <div class="row">
                    <!-- Secretaria -->
                    <div class="col-md-6 mb-3">
                        <label for="secretaria" class="form-label">
                            <i class="bi bi-building"></i> Secretaria *
                            <% if (isEdicao && !podeEditarCadastrais) { %>
                                <span class="permission-badge badge-readonly">Somente Leitura</span>
                            <% } %>
                        </label>
                        <% if (isEdicao && !podeEditarCadastrais) { %>
                            <input type="text" class="form-control field-readonly"
                                   value="<%= impressora.getSecretaria() %>" disabled>
                            <input type="hidden" name="secretaria" value="<%= impressora.getSecretaria() %>">
                        <% } else { %>
                            <select class="form-select" id="secretaria" name="secretaria" required>
                                <option value="">Selecione uma secretaria</option>
                                <% if (listaSecretarias != null) {
                                    for (String sec : listaSecretarias) { %>
                                        <option value="<%= sec %>"
                                                <%= isEdicao && sec.equals(impressora.getSecretaria()) ? "selected" : "" %>>
                                            <%= sec %>
                                        </option>
                                    <% }
                                } %>
                            </select>
                        <% } %>
                    </div>

                    <!-- Status -->
                    <div class="col-md-6 mb-3">
                        <label for="status" class="form-label">
                            <i class="bi bi-toggle-on"></i> Status *
                            <span class="permission-badge badge-editable">Editável</span>
                        </label>
                        <select class="form-select" id="status" name="status" required>
                            <option value="Operante" <%= isEdicao && "Operante".equals(impressora.getStatus()) ? "selected" : "" %>>
                                Operante
                            </option>
                            <option value="Em Manutenção" <%= isEdicao && "Em Manutenção".equals(impressora.getStatus()) ? "selected" : "" %>>
                                Em Manutenção
                            </option>
                        </select>
                    </div>
                </div>

                <div class="row">
                    <!-- Local de Instalação -->
                    <div class="col-md-6 mb-3">
                        <label for="localInstalacao" class="form-label">
                            <i class="bi bi-geo-alt"></i> Local de Instalação *
                            <% if (isEdicao && !podeEditarCadastrais) { %>
                                <span class="permission-badge badge-readonly">Somente Leitura</span>
                            <% } %>
                        </label>
                        <% if (isEdicao && !podeEditarCadastrais) { %>
                            <input type="text" class="form-control field-readonly"
                                   value="<%= impressora.getLocalInstalacao() %>" disabled>
                            <input type="hidden" name="localInstalacao" value="<%= impressora.getLocalInstalacao() %>">
                        <% } else { %>
                            <input type="text" class="form-control" id="localInstalacao" name="localInstalacao"
                                   value="<%= isEdicao ? impressora.getLocalInstalacao() : "" %>"
                                   placeholder="Ex: Sala 101" required>
                        <% } %>
                    </div>

                    <!-- Modelo de Equipamento -->
                    <div class="col-md-6 mb-3">
                        <label for="modeloEquipamento" class="form-label">
                            <i class="bi bi-printer"></i> Modelo do Equipamento *
                            <% if (isEdicao && !podeEditarCadastrais) { %>
                                <span class="permission-badge badge-readonly">Somente Leitura</span>
                            <% } %>
                        </label>
                        <% if (isEdicao && !podeEditarCadastrais) { %>
                            <input type="text" class="form-control field-readonly"
                                   value="<%= impressora.getModeloEquipamento() %>" disabled>
                            <input type="hidden" name="modeloEquipamento" value="<%= impressora.getModeloEquipamento() %>">
                        <% } else { %>
                            <input type="text" class="form-control" id="modeloEquipamento" name="modeloEquipamento"
                                   value="<%= isEdicao ? impressora.getModeloEquipamento() : "" %>"
                                   placeholder="Ex: Kyocera M-2040DN" required>
                            <small class="text-muted">
                                O custo por impressão será detectado automaticamente
                            </small>
                        <% } %>
                    </div>
                </div>

                <div class="row">
                    <!-- Número de Série -->
                    <div class="col-md-12 mb-3">
                        <label for="numeroSerie" class="form-label">
                            <i class="bi bi-upc-scan"></i> Número de Série *
                            <span class="permission-badge badge-readonly">Não pode ser alterado</span>
                        </label>
                        <% if (isEdicao) { %>
                            <input type="text" class="form-control field-readonly"
                                   value="<%= impressora.getNumeroSerie() %>" disabled>
                            <input type="hidden" name="numeroSerie" value="<%= impressora.getNumeroSerie() %>">
                            <small class="text-muted">Número de série não pode ser alterado</small>
                        <% } else { %>
                            <input type="text" class="form-control" id="numeroSerie" name="numeroSerie"
                                   placeholder="Ex: ABC123XYZ456" required>
                        <% } %>
                    </div>
                </div>

                <div class="row">
                    <!-- Contador de Impressões Atual -->
                    <div class="col-md-6 mb-3">
                        <label for="contadorImpressoes" class="form-label">
                            <i class="bi bi-123"></i> Contador de Impressões Atual *
                            <span class="permission-badge badge-editable">Editável</span>
                        </label>
                        <input type="number" class="form-control" id="contadorImpressoes" name="contadorImpressoes"
                               value="<%= isEdicao ? impressora.getContadorImpressoes() : "0" %>"
                               min="0" required>
                    </div>

                    <!-- Contador do Mês Anterior -->
                    <div class="col-md-6 mb-3">
                        <label for="contadorAnterior" class="form-label">
                            <i class="bi bi-clock-history"></i> Contador do Mês Anterior
                            <span class="permission-badge badge-editable">Editável</span>
                        </label>
                        <input type="number" class="form-control" id="contadorAnterior" name="contadorAnterior"
                               value="<%= isEdicao && impressora.getContadorAnterior() != null ? impressora.getContadorAnterior() : "" %>"
                               min="0" placeholder="0">
                        <small class="text-muted">
                            💡 Edite este valor se precisar corrigir o cálculo de impressões do mês. Deixe vazio para atualizar automaticamente.
                        </small>
                    </div>
                </div>

                <div class="row">
                    <!-- Relatório Atualizado -->
                    <div class="col-md-6 mb-3">
                        <label for="dataUltimaManutencao" class="form-label">
                            <i class="bi bi-calendar-check"></i> Relatório Atualizado
                            <span class="permission-badge badge-editable">Editável</span>
                        </label>
                        <input type="date" class="form-control" id="dataUltimaManutencao" name="dataUltimaManutencao"
                               value="<%= isEdicao && impressora.getDataUltimaManutencao() != null ? impressora.getDataUltimaManutencao().format(formatter) : "" %>">
                        <small class="text-muted">Opcional — ao alterar, a data anterior será preservada automaticamente</small>
                    </div>

                    <!-- Relatório Anterior (somente leitura) -->
                    <div class="col-md-6 mb-3">
                        <label class="form-label">
                            <i class="bi bi-calendar-minus"></i> Relatório Anterior
                            <span class="permission-badge badge-editable">Editável</span>
                        </label>
<input type="date" class="form-control" id="dataRelatorioAnterior" name="dataRelatorioAnterior"
       value="<%= isEdicao && impressora.getDataRelatorioAnterior() != null ? impressora.getDataRelatorioAnterior().format(formatter) : "" %>">
<small class="text-muted">
    💡 Preenchido automaticamente ao alterar "Relatório Atualizado". Edite manualmente se precisar corrigir.
</small>
                    </div>
                </div>

                <!-- Custo por Impressão (somente visualização na edição) -->
                <% if (isEdicao && impressora.getCustoPorImpressao() != null) { %>
                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label class="form-label">
                                <i class="bi bi-cash"></i> Custo por Impressão (automático)
                            </label>
                            <input type="text" class="form-control field-readonly"
                                   value="R$ <%= String.format("%.2f", impressora.getCustoPorImpressao()).replace(".", ",") %>" disabled>
                            <small class="text-muted">Detectado automaticamente pelo modelo</small>
                        </div>
                    </div>
                <% } %>

                <hr class="my-4">

                <div class="d-flex justify-content-between">
                    <a href="<%= request.getContextPath() %>/ImpressoraController" class="btn btn-secondary">
                        <i class="bi bi-arrow-left"></i> Cancelar
                    </a>
                    <button type="submit" class="btn btn-success">
                        <i class="bi bi-check-lg"></i>
                        <%= isEdicao ? "Salvar Alterações" : "Cadastrar Impressora" %>
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>