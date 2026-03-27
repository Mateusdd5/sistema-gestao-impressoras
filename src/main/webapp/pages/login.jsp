<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Sistema de Impressoras</title>
    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- Ícones -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
    <style>
        body {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
        }

        .login-container {
            width: 100%;
            max-width: 450px;
            padding: 20px;
        }

        .login-card {
            background: white;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            overflow: hidden;
        }

        .login-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            padding: 40px 30px;
            text-align: center;
            color: white;
        }

        .login-header i {
            font-size: 4rem;
            margin-bottom: 15px;
            animation: pulse 2s infinite;
        }

        @keyframes pulse {
            0%, 100% { transform: scale(1); }
            50% { transform: scale(1.1); }
        }

        .login-header h3 {
            margin: 0;
            font-weight: 600;
            font-size: 1.8rem;
        }

        .login-header p {
            margin: 5px 0 0 0;
            opacity: 0.9;
            font-size: 0.95rem;
        }

        .login-body {
            padding: 40px 30px;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 8px;
        }

        .form-control {
            border-radius: 10px;
            border: 2px solid #e0e0e0;
            padding: 12px 15px;
            font-size: 1rem;
            transition: all 0.3s ease;
        }

        .form-control:focus {
            border-color: #667eea;
            box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
        }

        .input-group-text {
            background: #f8f9fa;
            border: 2px solid #e0e0e0;
            border-right: none;
            border-radius: 10px 0 0 10px;
        }

        .input-group .form-control {
            border-left: none;
            border-radius: 0 10px 10px 0;
        }

        .btn-login {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 12px;
            font-size: 1.1rem;
            font-weight: 600;
            color: white;
            width: 100%;
            margin-top: 20px;
            transition: all 0.3s ease;
        }

        .btn-login:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
        }

        .alert {
            border-radius: 10px;
            border: none;
            padding: 15px;
            margin-bottom: 20px;
        }

        .alert-danger {
            background: #fee;
            color: #c33;
        }

        .alert-success {
            background: #efe;
            color: #3c3;
        }

        .login-footer {
            text-align: center;
            padding: 20px;
            color: #6c757d;
            font-size: 0.9rem;
        }

        .password-toggle {
            cursor: pointer;
            color: #6c757d;
            transition: color 0.3s;
        }

        .password-toggle:hover {
            color: #667eea;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="login-card">
            <!-- Cabeçalho -->
            <div class="login-header">
                <i class="bi bi-printer-fill"></i>
                <h3>Sistema de Impressoras</h3>
                <p>Controle e Gerenciamento</p>
            </div>

            <!-- Corpo do formulário -->
            <div class="login-body">
                <%
                    String erro = (String) request.getAttribute("erro");
                    String username = (String) request.getAttribute("username");
                    String logout = request.getParameter("logout");
                %>

                <!-- Mensagem de logout -->
                <% if (logout != null) { %>
                    <div class="alert alert-success">
                        <i class="bi bi-check-circle"></i>
                        Logout realizado com sucesso!
                    </div>
                <% } %>

                <!-- Mensagem de erro -->
                <% if (erro != null) { %>
                    <div class="alert alert-danger">
                        <i class="bi bi-exclamation-triangle"></i>
                        <%= erro %>
                    </div>
                <% } %>

                <!-- Formulário de login -->
                <form action="<%= request.getContextPath() %>/login" method="post">
                    <!-- Usuário -->
                    <div class="mb-3">
                        <label for="username" class="form-label">
                            <i class="bi bi-person"></i> Usuário
                        </label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="bi bi-person-fill"></i>
                            </span>
                            <input type="text" 
                                   class="form-control" 
                                   id="username" 
                                   name="username" 
                                   placeholder="Digite seu usuário"
                                   value="<%= username != null ? username : "" %>"
                                   required 
                                   autofocus>
                        </div>
                    </div>

                    <!-- Senha -->
                    <div class="mb-3">
                        <label for="senha" class="form-label">
                            <i class="bi bi-lock"></i> Senha
                        </label>
                        <div class="input-group">
                            <span class="input-group-text">
                                <i class="bi bi-lock-fill"></i>
                            </span>
                            <input type="password" 
                                   class="form-control" 
                                   id="senha" 
                                   name="senha" 
                                   placeholder="Digite sua senha"
                                   required>
                            <span class="input-group-text password-toggle" onclick="togglePassword()">
                                <i class="bi bi-eye" id="toggleIcon"></i>
                            </span>
                        </div>
                    </div>

                    <!-- Botão de login -->
                    <button type="submit" class="btn btn-login">
                        <i class="bi bi-box-arrow-in-right"></i>
                        Entrar
                    </button>
                </form>
            </div>

            <!-- Rodapé -->
            <div class="login-footer">
                <i class="bi bi-shield-check"></i>
                Sistema de Controle de Impressoras &copy; 2025
            </div>
        </div>
    </div>

    <!-- Scripts -->
    <script>
        // Toggle mostrar/ocultar senha
        function togglePassword() {
            const senhaInput = document.getElementById('senha');
            const toggleIcon = document.getElementById('toggleIcon');
            
            if (senhaInput.type === 'password') {
                senhaInput.type = 'text';
                toggleIcon.classList.remove('bi-eye');
                toggleIcon.classList.add('bi-eye-slash');
            } else {
                senhaInput.type = 'password';
                toggleIcon.classList.remove('bi-eye-slash');
                toggleIcon.classList.add('bi-eye');
            }
        }

        // Auto-esconder mensagens após 5 segundos
        setTimeout(function() {
            const alerts = document.querySelectorAll('.alert');
            alerts.forEach(alert => {
                alert.style.transition = 'opacity 0.5s';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            });
        }, 5000);
    </script>
</body>
</html>