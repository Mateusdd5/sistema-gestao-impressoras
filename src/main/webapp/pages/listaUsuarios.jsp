<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Gerenciar Usuários</title>
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

        .container {
            margin-top: 20px;
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

        .table {
            margin-bottom: 0;
        }

        .table thead th {
            background: #f8f9fa;
            color: #495057;
            font-weight: 600;
            border-bottom: 2px solid #dee2e6;
        }

        .table tbody tr:hover {
            background: #f8f9fa;
        }

        .badge {
            padding: 6px 12px;
            border-radius: 8px;
            font-weight: 500;
        }

        .badge-admin {
            background: #dc3545;
            color: white;
        }

        .badge-technician {
            background: #0dcaf0;
            color: white;
        }

        .badge-operator {
            background: #ffc107;
            color: #000;
        }

        .badge-viewer {
            background: #6c757d;
            color: white;
        }

        .badge-ativo {
            background: #28a745;
            color: white;
        }

        .badge-inativo {
            background: #dc3545;
            color: white;
        }

        .btn {
            border-radius: 8px;
        }

        .action-buttons .btn {
            padding: 5px 10px;
            font-size: 14px;
            margin: 0 2px;
        }
    </style>
</head>
<body>
<%
    @SuppressWarnings("unchecked")
    List<Usuario> listaUsuarios = (List<Usuario>) request.getAttribute("listaUsuarios");
    Integer totalUsuarios = (Integer) request.getAttribute("totalUsuarios");
    Integer usuariosAtivos = (Integer) request.getAttribute("usuariosAtivos");
    
    String sucesso = request.getParameter("sucesso");
    String erro = request.getParameter("erro");
%>

<div class="container">
    <div class="card">
        <div class="card-header text-white">
            <div class="d-flex justify-content-between align-items-center">
                <h4 class="mb-0">
                    <i class="bi bi-people"></i> Gerenciar Usuários
                </h4>
                <div>
                    <a href="<%= request.getContextPath() %>/ImpressoraController" class="btn btn-light me-2">
                        <i class="bi bi-arrow-left"></i> Voltar
                    </a>
                    <a href="<%= request.getContextPath() %>/pages/cadastroUsuario.jsp" class="btn btn-success">
                        <i class="bi bi-plus-circle"></i> Novo Usuário
                    </a>
                </div>
            </div>
        </div>
        <div class="card-body p-4">
            <!-- Mensagens -->
            <% if (sucesso != null) { %>
                <div class="alert alert-success alert-dismissible fade show">
                    <i class="bi bi-check-circle"></i>
                    <% if ("usuario_criado".equals(sucesso)) { %>
                        Usuário criado com sucesso!
                    <% } else if ("usuario_atualizado".equals(sucesso)) { %>
                        Usuário atualizado com sucesso!
                    <% } else if ("senha_alterada".equals(sucesso)) { %>
                        Senha alterada com sucesso!
                    <% } else if ("usuario_desativado".equals(sucesso)) { %>
                        Usuário desativado com sucesso!
                    <% } else if ("usuario_reativado".equals(sucesso)) { %>
                        Usuário reativado com sucesso!
                    <% } else if ("usuario_deletado".equals(sucesso)) { %>
                        Usuário deletado com sucesso!
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <% if (erro != null) { %>
                <div class="alert alert-danger alert-dismissible fade show">
                    <i class="bi bi-exclamation-triangle"></i>
                    <% if ("nao_pode_deletar_proprio".equals(erro)) { %>
                        Você não pode deletar seu próprio usuário!
                    <% } else { %>
                        Erro ao processar a operação. Tente novamente.
                    <% } %>
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            <% } %>

            <!-- Estatísticas -->
            <div class="row mb-4">
                <div class="col-md-6">
                    <div class="alert alert-info mb-0">
                        <strong><i class="bi bi-people-fill"></i> Total de Usuários:</strong> <%= totalUsuarios %>
                    </div>
                </div>
                <div class="col-md-6">
                    <div class="alert alert-success mb-0">
                        <strong><i class="bi bi-check-circle"></i> Usuários Ativos:</strong> <%= usuariosAtivos %>
                    </div>
                </div>
            </div>

            <!-- Tabela de usuários -->
            <div class="table-responsive">
                <% if (listaUsuarios != null && !listaUsuarios.isEmpty()) { %>
                    <table class="table table-hover">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Username</th>
                                <th>Nome Completo</th>
                                <th>Email</th>
                                <th>Nível</th>
                                <th>Status</th>
                                <th>Último Acesso</th>
                                <th class="text-center">Ações</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (Usuario usuario : listaUsuarios) { %>
                                <tr>
                                    <td><strong>#<%= usuario.getId() %></strong></td>
                                    <td><code><%= usuario.getUsername() %></code></td>
                                    <td><%= usuario.getNomeCompleto() %></td>
                                    <td>
                                        <% if (usuario.getEmail() != null && !usuario.getEmail().isEmpty()) { %>
                                            <small><%= usuario.getEmail() %></small>
                                        <% } else { %>
                                            <span class="text-muted">-</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <% if (usuario.isAdmin()) { %>
                                            <span class="badge badge-admin">
                                                <i class="bi bi-shield-fill"></i> Admin
                                            </span>
                                        <% } else if (usuario.isTechnician()) { %>
                                            <span class="badge badge-technician">
                                                <i class="bi bi-tools"></i> Técnico
                                            </span>
                                        <% } else if (usuario.isOperator()) { %>
                                            <span class="badge badge-operator">
                                                <i class="bi bi-pencil"></i> Operador
                                            </span>
                                        <% } else if (usuario.isViewer()) { %>
                                            <span class="badge badge-viewer">
                                                <i class="bi bi-eye"></i> Visualizador
                                            </span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <% if (usuario.getAtivo()) { %>
                                            <span class="badge badge-ativo">Ativo</span>
                                        <% } else { %>
                                            <span class="badge badge-inativo">Inativo</span>
                                        <% } %>
                                    </td>
                                    <td>
                                        <small><%= usuario.getUltimoAcessoFormatado() %></small>
                                    </td>
                                    <td class="text-center action-buttons">
                                        <!-- Editar -->
                                        <a href="<%= request.getContextPath() %>/UsuarioController?action=editar&id=<%= usuario.getId() %>" 
                                           class="btn btn-sm btn-warning" 
                                           title="Editar">
                                            <i class="bi bi-pencil"></i>
                                        </a>

                                        <!-- Desativar/Reativar -->
                                        <% if (usuario.getAtivo()) { %>
                                            <button type="button" 
                                                    class="btn btn-sm btn-secondary" 
                                                    onclick="confirmarDesativar(<%= usuario.getId() %>, '<%= usuario.getUsername() %>')"
                                                    title="Desativar">
                                                <i class="bi bi-x-circle"></i>
                                            </button>
                                        <% } else { %>
                                            <a href="<%= request.getContextPath() %>/UsuarioController?action=reativar&id=<%= usuario.getId() %>" 
                                               class="btn btn-sm btn-success" 
                                               title="Reativar">
                                                <i class="bi bi-check-circle"></i>
                                            </a>
                                        <% } %>

                                        <!-- Deletar -->
                                        <button type="button" 
                                                class="btn btn-sm btn-danger" 
                                                onclick="confirmarDeletar(<%= usuario.getId() %>, '<%= usuario.getUsername() %>')"
                                                title="Deletar">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                <% } else { %>
                    <div class="alert alert-warning text-center">
                        <i class="bi bi-exclamation-triangle" style="font-size: 2rem;"></i>
                        <h5 class="mt-2">Nenhum usuário cadastrado</h5>
                    </div>
                <% } %>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<!-- Scripts -->
<script>
function confirmarDesativar(id, username) {
    if (confirm('Tem certeza que deseja desativar o usuário "' + username + '"?')) {
        window.location.href = '<%= request.getContextPath() %>/UsuarioController?action=desativar&id=' + id;
    }
}

function confirmarDeletar(id, username) {
    if (confirm('ATENÇÃO: Esta ação é IRREVERSÍVEL!\n\nTem certeza que deseja DELETAR PERMANENTEMENTE o usuário "' + username + '"?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '<%= request.getContextPath() %>/UsuarioController';

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