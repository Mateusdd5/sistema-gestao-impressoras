package model;

/**
 * Enum que representa os 4 níveis de permissão do sistema
 */
public enum NivelPermissao {
    
    /**
     * VIEWER - Visualizador
     * - Ver lista de impressoras
     * - Exportar relatórios (CSV, Excel, PDF)
     * - Imprimir relatórios
     * - NÃO pode editar nada
     */
    VIEWER("Visualizador", 1),
    
    /**
     * OPERATOR - Operador
     * - Tudo do VIEWER
     * - Editar contador de impressões
     * - Editar data do último relatório
     * - Editar status (Operante/Manutenção)
     * - NÃO pode editar dados cadastrais
     */
    OPERATOR("Operador", 2),
    
    /**
     * TECHNICIAN - Técnico
     * - Tudo do OPERATOR
     * - Editar todos os dados da impressora
     * - Cadastrar novas impressoras
     * - NÃO pode deletar impressoras
     * - NÃO pode gerenciar usuários
     */
    TECHNICIAN("Técnico", 3),
    
    /**
     * ADMIN - Administrador
     * - Acesso total ao sistema
     * - Deletar impressoras
     * - Gerenciar usuários (criar, editar, deletar)
     * - Alterar níveis de permissão
     */
    ADMIN("Administrador", 4);
    
    private final String descricao;
    private final int nivel;
    
    NivelPermissao(String descricao, int nivel) {
        this.descricao = descricao;
        this.nivel = nivel;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public int getNivel() {
        return nivel;
    }
    
    /**
     * Verifica se este nível tem permissão igual ou superior ao nível fornecido
     */
    public boolean temPermissao(NivelPermissao nivelRequerido) {
        return this.nivel >= nivelRequerido.nivel;
    }
    
    /**
     * Verifica se pode visualizar (todos podem)
     */
    public boolean podeVisualizar() {
        return true;
    }
    
    /**
     * Verifica se pode editar contadores e status
     */
    public boolean podeEditarContadores() {
        return this.nivel >= OPERATOR.nivel;
    }
    
    /**
     * Verifica se pode editar dados cadastrais (secretaria, modelo, etc)
     */
    public boolean podeEditarDadosCadastrais() {
        return this.nivel >= TECHNICIAN.nivel;
    }
    
    /**
     * Verifica se pode cadastrar novas impressoras
     */
    public boolean podeCadastrar() {
        return this.nivel >= TECHNICIAN.nivel;
    }
    
    /**
     * Verifica se pode deletar impressoras
     */
    public boolean podeDeletar() {
        return this == ADMIN;
    }
    
    /**
     * Verifica se pode gerenciar usuários
     */
    public boolean podeGerenciarUsuarios() {
        return this == ADMIN;
    }
    
    /**
     * Converte string do banco para enum
     */
    public static NivelPermissao fromString(String nivel) {
        if (nivel == null) {
            return null;
        }
        try {
            return NivelPermissao.valueOf(nivel.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}