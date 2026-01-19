<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario" %>
<%@ page import="model.NivelPermissao" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro de Usuário</title>
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
            transition: all 0.3s ease;
        }

        .form-control:focus, .form-select:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .btn {
            border-radius: 10px;
            padding: 10px 25px;
        }

        .nivel-info {
            background: #f8f9fa;
            padding: 10px;
            border-radius: 8px;
            font-size: 0.85rem;
            margin-top: 5px;
        }
    </style>
</head>
<body>
<%
    Usuario usuarioEdicao = (Usuario) request.getAttribute("usuario");
    boolean isEdicao = usuarioEdicao != null;
    
    String erro = request.getParameter("erro");
%>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="card-header text-white text-center">
                    <h4>
                        <i class="bi bi-person-plus"></i>
                        <%= isEdicao ? "Editar Usuário" : "Novo Usuário" %>
                    </h4>
                </div>
                <div class="card-body p-4">
                    <!-- Mensagens de erro -->
                    <% if (erro != null) { %>
                        <div class="alert alert-danger alert-dismissible fade show">
                            <i class="bi bi-exclamation-triangle"></i>
                            <% if ("username_existe".equals(erro)) { %>
                                Este username já está em uso!
                            <% } else if ("senha_invalida".equals(erro)) { %>
                                A senha deve ter pelo menos 6 caracteres!
                            <% } else { %>
                                Erro ao processar o formulário. Tente novamente.
                            <% } %>
                            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                        </div>
                    <% } %>

                    <form action="<%= request.getContextPath() %>/UsuarioController" method="post">
                        <input type="hidden" name="action" value="<%= isEdicao ? "atualizar" : "adicionar" %>">
                        <% if (isEdicao) { %>
                            <input type="hidden" name="id" value="<%= usuarioEdicao.getId() %>">
                        <% } %>

                        <div class="row">
                            <!-- Username -->
                            <div class="col-md-6 mb-3">
                                <label for="username" class="form-label">
                                    <i class="bi bi-person"></i> Username *
                                </label>
                                <input type="text" 
                                       class="form-control" 
                                       id="username" 
                                       name="username" 
                                       value="<%= isEdicao ? usuarioEdicao.getUsername() : "" %>"
                                       <%= isEdicao ? "readonly" : "" %>
                                       required 
                                       placeholder="usuario123">
                                <% if (isEdicao) { %>
                                    <small class="text-muted">Username não pode ser alterado</small>
                                <% } %>
                            </div>

                            <!-- Nome Completo -->
                            <div class="col-md-6 mb-3">
                                <label for="nomeCompleto" class="form-label">
                                    <i class="bi bi-person-badge"></i> Nome Completo *
                                </label>
                                <input type="text" 
                                       class="form-control" 
                                       id="nomeCompleto" 
                                       name="nomeCompleto" 
                                       value="<%= isEdicao ? usuarioEdicao.getNomeCompleto() : "" %>"
                                       required 
                                       placeholder="João Silva">
                            </div>
                        </div>

                        <div class="row">
                            <!-- Email -->
                            <div class="col-md-6 mb-3">
                                <label for="email" class="form-label">
                                    <i class="bi bi-envelope"></i> Email
                                </label>
                                <input type="email" 
                                       class="form-control" 
                                       id="email" 
                                       name="email" 
                                       value="<%= isEdicao && usuarioEdicao.getEmail() != null ? usuarioEdicao.getEmail() : "" %>"
                                       placeholder="usuario@exemplo.com">
                                <small class="text-muted">Opcional</small>
                            </div>

                            <!-- Nível de Permissão -->
                            <div class="col-md-6 mb-3">
                                <label for="nivelPermissao" class="form-label">
                                    <i class="bi bi-shield"></i> Nível de Permissão *
                                </label>
                                <select class="form-select" id="nivelPermissao" name="nivelPermissao" required onchange="mostrarInfoNivel()">
                                    <option value="">Selecione...</option>
                                    <% for (NivelPermissao nivel : NivelPermissao.values()) { %>
                                        <option value="<%= nivel.name() %>" 
                                                <%= isEdicao && usuarioEdicao.getNivelPermissao() == nivel ? "selected" : "" %>>
                                            <%= nivel.getDescricao() %>
                                        </option>
                                    <% } %>
                                </select>
                                <div id="infoNivel" class="nivel-info" style="display: none;"></div>
                            </div>
                        </div>

                        <% if (!isEdicao) { %>
                            <!-- Senha (apenas ao criar) -->
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="senha" class="form-label">
                                        <i class="bi bi-lock"></i> Senha *
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="senha" 
                                           name="senha" 
                                           required 
                                           minlength="6"
                                           placeholder="Mínimo 6 caracteres">
                                </div>
                                <div class="col-md-6 mb-3">
                                    <label for="confirmarSenha" class="form-label">
                                        <i class="bi bi-lock-fill"></i> Confirmar Senha *
                                    </label>
                                    <input type="password" 
                                           class="form-control" 
                                           id="confirmarSenha" 
                                           name="confirmarSenha" 
                                           required 
                                           minlength="6"
                                           placeholder="Repita a senha">
                                </div>
                            </div>
                        <% } %>

                        <% if (isEdicao) { %>
                            <!-- Status (apenas ao editar) -->
                            <div class="row">
                                <div class="col-md-6 mb-3">
                                    <label for="ativo" class="form-label">
                                        <i class="bi bi-toggle-on"></i> Status
                                    </label>
                                    <select class="form-select" id="ativo" name="ativo">
                                        <option value="true" <%= usuarioEdicao.getAtivo() ? "selected" : "" %>>Ativo</option>
                                        <option value="false" <%= !usuarioEdicao.getAtivo() ? "selected" : "" %>>Inativo</option>
                                    </select>
                                </div>
                            </div>

                            <!-- Opção para alterar senha -->
                            <div class="mb-3">
                                <button type="button" class="btn btn-secondary" data-bs-toggle="collapse" data-bs-target="#alterarSenhaBox">
                                    <i class="bi bi-key"></i> Alterar Senha
                                </button>
                            </div>

                            <div class="collapse" id="alterarSenhaBox">
                                <div class="card mb-3">
                                    <div class="card-body">
                                        <h6><i class="bi bi-key"></i> Alterar Senha</h6>
                                        <div class="row">
                                            <div class="col-md-12">
                                                <label for="novaSenha" class="form-label">Nova Senha</label>
                                                <input type="password" 
                                                       class="form-control" 
                                                       id="novaSenha" 
                                                       minlength="6"
                                                       placeholder="Mínimo 6 caracteres">
                                            </div>
                                        </div>
                                        <button type="button" class="btn btn-primary mt-2" onclick="alterarSenha()">
                                            <i class="bi bi-check"></i> Salvar Nova Senha
                                        </button>
                                    </div>
                                </div>
                            </div>
                        <% } %>

                        <hr class="my-4">

                        <div class="d-flex justify-content-between">
                            <a href="<%= request.getContextPath() %>/UsuarioController" class="btn btn-secondary">
                                <i class="bi bi-arrow-left"></i> Voltar
                            </a>
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-lg"></i>
                                <%= isEdicao ? "Atualizar" : "Cadastrar" %>
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Bootstrap JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
// Validar senhas iguais
<% if (!isEdicao) { %>
document.querySelector('form').addEventListener('submit', function(e) {
    const senha = document.getElementById('senha').value;
    const confirmar = document.getElementById('confirmarSenha').value;
    
    if (senha !== confirmar) {
        e.preventDefault();
        alert('As senhas não conferem!');
        return false;
    }
});
<% } %>

// Mostrar informações do nível
function mostrarInfoNivel() {
    const select = document.getElementById('nivelPermissao');
    const infoDiv = document.getElementById('infoNivel');
    const nivel = select.value;
    
    const infos = {
        'VIEWER': '👁️ Visualizador: Apenas visualiza e exporta relatórios',
        'OPERATOR': '✏️ Operador: Edita contadores e status',
        'TECHNICIAN': '🔧 Técnico: Cadastra e edita impressoras',
        'ADMIN': '👑 Administrador: Acesso total ao sistema'
    };
    
    if (nivel && infos[nivel]) {
        infoDiv.textContent = infos[nivel];
        infoDiv.style.display = 'block';
    } else {
        infoDiv.style.display = 'none';
    }
}

// Alterar senha (apenas ao editar)
<% if (isEdicao) { %>
function alterarSenha() {
    const novaSenha = document.getElementById('novaSenha').value;
    
    if (!novaSenha || novaSenha.length < 6) {
        alert('A senha deve ter pelo menos 6 caracteres!');
        return;
    }
    
    if (confirm('Tem certeza que deseja alterar a senha deste usuário?')) {
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '<%= request.getContextPath() %>/UsuarioController';
        
        const actionInput = document.createElement('input');
        actionInput.type = 'hidden';
        actionInput.name = 'action';
        actionInput.value = 'alterarSenha';
        form.appendChild(actionInput);
        
        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'id';
        idInput.value = '<%= usuarioEdicao.getId() %>';
        form.appendChild(idInput);
        
        const senhaInput = document.createElement('input');
        senhaInput.type = 'hidden';
        senhaInput.name = 'novaSenha';
        senhaInput.value = novaSenha;
        form.appendChild(senhaInput);
        
        document.body.appendChild(form);
        form.submit();
    }
}
<% } %>

// Mostrar info do nível ao carregar a página
<% if (isEdicao) { %>
mostrarInfoNivel();
<% } %>
</script>
</body>
</html>