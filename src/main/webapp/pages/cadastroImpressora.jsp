<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Impressora" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cadastro de Impressora</title>
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
        .btn {
            border-radius: 10px;
            padding: 10px 25px;
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
        .info-box {
            background: #e7f3ff;
            border-left: 4px solid #2196F3;
            padding: 12px;
            border-radius: 8px;
            margin-top: 10px;
        }
        .alert-calculo {
            transition: all 0.3s ease;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-10">
            <%
                // Verifica se é edição
                Impressora impressoraEdicao = (Impressora) request.getAttribute("impressora");
                boolean isEdicao = impressoraEdicao != null;
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            %>

            <div class="card">
                <div class="card-header text-white text-center">
                    <h4>
                        <i class="bi bi-printer"></i>
                        <%= isEdicao ? "Editar Impressora" : "Nova Impressora" %>
                    </h4>
                </div>
                <div class="card-body p-4">
                    <form action="<%= request.getContextPath() %>/ImpressoraController" method="post">
                        <input type="hidden" name="action" value="<%= isEdicao ? "editar" : "adicionar" %>">
                        <% if (isEdicao) { %>
                            <input type="hidden" name="id" value="<%= impressoraEdicao.getId() %>">
                        <% } %>

                        <div class="row">
                            <!-- Secretaria -->
                            <div class="col-md-6 mb-3">
                                <label for="secretaria" class="form-label">
                                    <i class="bi bi-building"></i> Secretaria *
                                </label>
                                <select class="form-select" id="secretaria" name="secretaria" required>
                                    <option value="">Selecione...</option>
                                    <option value="SESAU" <%= isEdicao && "SESAU".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SESAU - Secretaria de Saúde</option>
                                    <option value="SEMAT" <%= isEdicao && "SEMAT".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEMAT - Secretaria de Meio Ambiente</option>
                                    <option value="SEFZ" <%= isEdicao && "SEFZ".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEFZ - Secretaria de Fazenda</option>
                                    <option value="SEDUC" <%= isEdicao && "SEDUC".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEDUC - Secretaria de Educação</option>
                                    <option value="SEMEL" <%= isEdicao && "SEMEL".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEMEL - Secretaria de Esporte e Lazer</option>
                                    <option value="SEMAM" <%= isEdicao && "SEMAM".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEMAM - Secretaria de Meio Ambiente</option>
                                    <option value="SEHAR" <%= isEdicao && "SEHAR".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEHAR - Secretaria de Habitação</option>
                                    <option value="SEOSP" <%= isEdicao && "SEOSP".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEOSP - Secretaria de Obras</option>
                                    <option value="SEGOV" <%= isEdicao && "SEGOV".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEGOV - Secretaria de Governo</option>
                                    <option value="SEDES" <%= isEdicao && "SEDES".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEDES - Secretaria de Desenvolvimento Social</option>
                                    <option value="SEDEC" <%= isEdicao && "SEDEC".equals(impressoraEdicao.getSecretaria()) ? "selected" : "" %>>SEDEC - Secretaria de Desenvolvimento Econômico</option>
                                </select>
                            </div>

                            <!-- Status -->
                            <div class="col-md-6 mb-3">
                                <label for="status" class="form-label">
                                    <i class="bi bi-check-circle"></i> Status *
                                </label>
                                <select class="form-select" id="status" name="status" required>
                                    <option value="">Selecione...</option>
                                    <option value="Operante" <%= isEdicao && "Operante".equals(impressoraEdicao.getStatus()) ? "selected" : "" %>>✅ Operante</option>
                                    <option value="Em Manutenção" <%= isEdicao && "Em Manutenção".equals(impressoraEdicao.getStatus()) ? "selected" : "" %>>🔧 Em Manutenção</option>
                                </select>
                            </div>
                        </div>

                        <div class="row">
                            <!-- Local de Instalação -->
                            <div class="col-md-6 mb-3">
                                <label for="localInstalacao" class="form-label">
                                    <i class="bi bi-geo-alt"></i> Local de Instalação *
                                </label>
                                <input type="text" class="form-control" id="localInstalacao" name="localInstalacao" 
                                       value="<%= isEdicao && impressoraEdicao.getLocalInstalacao() != null ? impressoraEdicao.getLocalInstalacao() : "" %>" 
                                       required placeholder="Ex: Sala 101, Recepção">
                            </div>

                            <!-- Modelo do Equipamento -->
                            <div class="col-md-6 mb-3">
                                <label for="modeloEquipamento" class="form-label">
                                    <i class="bi bi-printer-fill"></i> Modelo do Equipamento *
                                </label>
                                <input type="text" class="form-control" id="modeloEquipamento" name="modeloEquipamento" 
                                       value="<%= isEdicao && impressoraEdicao.getModeloEquipamento() != null ? impressoraEdicao.getModeloEquipamento() : "" %>" 
                                       required placeholder="Ex: Kyocera M-2040DN">
                            </div>
                        </div>

                        <div class="row">
                            <!-- Número de Série -->
                            <div class="col-md-6 mb-3">
                                <label for="numeroSerie" class="form-label">
                                    <i class="bi bi-upc-scan"></i> Número de Série *
                                </label>
                                <input type="text" class="form-control" id="numeroSerie" name="numeroSerie" 
                                       value="<%= isEdicao && impressoraEdicao.getNumeroSerie() != null ? impressoraEdicao.getNumeroSerie() : "" %>" 
                                       <%= isEdicao ? "readonly" : "" %> required placeholder="Ex: VRJ8127836">
                                <% if (isEdicao) { %>
                                    <small class="form-text text-muted">Número de série não pode ser alterado</small>
                                <% } %>
                            </div>

                            <!-- Data do Último Relatório -->
                            <div class="col-md-6 mb-3">
                                <label for="dataUltimaManutencao" class="form-label">
                                    <i class="bi bi-calendar-check"></i> Data do Último Relatório
                                </label>
                                <input type="date" class="form-control" id="dataUltimaManutencao" name="dataUltimaManutencao" 
                                       value="<%= isEdicao && impressoraEdicao.getDataUltimaManutencao() != null ? impressoraEdicao.getDataUltimaManutencao().format(formatter) : "" %>">
                                <small class="form-text text-muted">Opcional</small>
                            </div>
                        </div>

                        <div class="row">
                            <!-- Contador de Impressões Atual -->
                            <div class="col-md-6 mb-3">
                                <label for="contadorImpressoes" class="form-label">
                                    <i class="bi bi-file-earmark-text"></i> Contador de Impressões Atual *
                                </label>
                                <input type="number" class="form-control" id="contadorImpressoes" name="contadorImpressoes" 
                                       value="<%= isEdicao ? impressoraEdicao.getContadorImpressoes() : 0 %>" 
                                       min="0" required placeholder="0" oninput="calcularImpressoesMes()">
                            </div>

                            <!-- Contador Anterior (EDITÁVEL) -->
                            <div class="col-md-6 mb-3">
                                <label for="contadorAnterior" class="form-label">
                                    <i class="bi bi-clock-history"></i> Contador do Mês Anterior
                                    <span class="badge bg-info ms-2">Editável</span>
                                </label>
                                <input type="number" class="form-control" id="contadorAnterior" name="contadorAnterior" 
                                       value="<%= isEdicao && impressoraEdicao.getContadorAnterior() != null ? impressoraEdicao.getContadorAnterior() : "" %>" 
                                       min="0" placeholder="Deixe vazio para atualizar automaticamente" oninput="calcularImpressoesMes()">
                                <small class="form-text text-muted">
                                    <% if (isEdicao) { %>
                                        💡 <strong>Edite este valor se precisar corrigir.</strong> Deixe vazio para atualizar automaticamente.
                                    <% } else { %>
                                        Opcional - para primeira inserção
                                    <% } %>
                                </small>
                            </div>
                        </div>

                        <!-- Exibição do cálculo em tempo real -->
                        <% if (isEdicao && impressoraEdicao.getContadorAnterior() != null) { %>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="alert alert-info alert-calculo" id="alertImpressoesMes">
                                    <strong><i class="bi bi-calculator"></i> Impressões deste mês:</strong> 
                                    <span id="impressoesMesCalculado">
                                        <%= String.format("%,d", impressoraEdicao.getImpressoesDoMes()) %>
                                    </span> impressões
                                </div>
                            </div>
                        </div>
                        <% } %>

                        <hr class="my-4">

                        <div class="d-flex justify-content-between">
                            <a href="<%= request.getContextPath() %>/ImpressoraController" class="btn btn-secondary">
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
// Calcular impressões do mês em tempo real
function calcularImpressoesMes() {
    var contadorAtual = parseInt(document.getElementById('contadorImpressoes').value) || 0;
    var contadorAnteriorInput = document.getElementById('contadorAnterior').value;
    var contadorAnterior = contadorAnteriorInput ? parseInt(contadorAnteriorInput) : 0;
    
    var elemento = document.getElementById('impressoesMesCalculado');
    var alert = document.getElementById('alertImpressoesMes');
    
    if (elemento && contadorAnteriorInput) {
        var impressoesMes = contadorAtual - contadorAnterior;
        elemento.textContent = impressoesMes.toLocaleString('pt-BR');
        
        // Alterar cor conforme o resultado
        if (impressoesMes < 0) {
            alert.className = 'alert alert-warning alert-calculo';
            elemento.innerHTML = '<strong style="color: #d9534f;">' + impressoesMes.toLocaleString('pt-BR') + '</strong> impressões ⚠️ (NEGATIVO!)';
        } else if (impressoesMes === 0) {
            alert.className = 'alert alert-secondary alert-calculo';
            elemento.innerHTML = impressoesMes.toLocaleString('pt-BR') + ' impressões';
        } else {
            alert.className = 'alert alert-info alert-calculo';
            elemento.innerHTML = impressoesMes.toLocaleString('pt-BR') + ' impressões';
        }
    }
}

// Validação antes de enviar
document.querySelector('form').addEventListener('submit', function(e) {
    const contadorAtual = parseInt(document.getElementById('contadorImpressoes').value) || 0;
    const contadorAnteriorInput = document.getElementById('contadorAnterior').value;
    const contadorAnterior = contadorAnteriorInput ? parseInt(contadorAnteriorInput) : 0;
    
    if (contadorAtual < 0) {
        e.preventDefault();
        alert('❌ O contador de impressões não pode ser negativo!');
        return false;
    }
    
    // Avisar se o contador diminuiu (e o usuário preencheu o contador anterior)
    if (contadorAnteriorInput && contadorAtual < contadorAnterior) {
        var diferenca = contadorAnterior - contadorAtual;
        if (!confirm('⚠️ ATENÇÃO: O contador atual é MENOR que o contador anterior!\n\n' +
                     'Contador anterior: ' + contadorAnterior.toLocaleString('pt-BR') + '\n' +
                     'Contador atual: ' + contadorAtual.toLocaleString('pt-BR') + '\n\n' +
                     'Isso resultará em: -' + diferenca.toLocaleString('pt-BR') + ' impressões (NEGATIVO)\n\n' +
                     '💡 DICA: Se cometeu um erro, edite o "Contador do Mês Anterior" para o valor correto.\n\n' +
                     'Deseja continuar mesmo assim?')) {
            e.preventDefault();
            return false;
        }
    }
});

// Limita a data do relatório para não ser no futuro
document.getElementById('dataUltimaManutencao').setAttribute('max', new Date().toISOString().split('T')[0]);
</script>
</body>
</html>