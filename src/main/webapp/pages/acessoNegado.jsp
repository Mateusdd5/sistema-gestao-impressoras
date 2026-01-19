<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Usuario" %>
<%@ page import="utils.SessaoUtil" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Acesso Negado</title>
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
        }

        .error-container {
            text-align: center;
            background: white;
            padding: 50px;
            border-radius: 20px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
            max-width: 600px;
        }

        .error-icon {
            font-size: 8rem;
            color: #dc3545;
            margin-bottom: 20px;
            animation: shake 0.5s;
        }

        @keyframes shake {
            0%, 100% { transform: translateX(0); }
            25% { transform: translateX(-10px); }
            75% { transform: translateX(10px); }
        }

        .error-title {
            font-size: 2.5rem;
            font-weight: bold;
            color: #495057;
            margin-bottom: 20px;
        }

        .error-message {
            font-size: 1.2rem;
            color: #6c757d;
            margin-bottom: 30px;
        }

        .btn-voltar {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            border: none;
            border-radius: 10px;
            padding: 15px 40px;
            font-size: 1.1rem;
            font-weight: 600;
            color: white;
            text-decoration: none;
            display: inline-block;
            transition: all 0.3s ease;
        }

        .btn-voltar:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(102, 126, 234, 0.3);
            color: white;
        }

        .user-info {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 10px;
            margin-bottom: 30px;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <%
        Usuario usuarioLogado = SessaoUtil.obterUsuarioLogado(request);
    %>

    <div class="error-container">
        <i class="bi bi-shield-x error-icon"></i>
        
        <h1 class="error-title">Acesso Negado</h1>
        
        <p class="error-message">
            Você não tem permissão para acessar esta página.
        </p>

        <% if (usuarioLogado != null) { %>
            <div class="user-info">
                <strong><i class="bi bi-person-badge"></i> Usuário:</strong> <%= usuarioLogado.getNomeCompleto() %><br>
                <strong><i class="bi bi-shield"></i> Nível:</strong> <%= usuarioLogado.getNivelPermissaoDescricao() %>
            </div>
        <% } %>

        <a href="<%= request.getContextPath() %>/ImpressoraController" class="btn-voltar">
            <i class="bi bi-arrow-left"></i>
            Voltar para Página Inicial
        </a>
    </div>
</body>
</html>