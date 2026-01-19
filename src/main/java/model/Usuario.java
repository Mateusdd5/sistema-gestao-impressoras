package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe que representa um usuário do sistema
 */
public class Usuario {
    
    private Integer id;
    private String username;
    private String senhaHash;
    private String nomeCompleto;
    private String email;
    private NivelPermissao nivelPermissao;
    private Boolean ativo;
    private LocalDateTime dataCriacao;
    private LocalDateTime ultimoAcesso;
    
    // Construtores
    
    public Usuario() {
        this.ativo = true;
    }
    
    public Usuario(String username, String senhaHash, String nomeCompleto, 
                   NivelPermissao nivelPermissao) {
        this.username = username;
        this.senhaHash = senhaHash;
        this.nomeCompleto = nomeCompleto;
        this.nivelPermissao = nivelPermissao;
        this.ativo = true;
    }
    
    public Usuario(Integer id, String username, String senhaHash, String nomeCompleto,
                   String email, NivelPermissao nivelPermissao, Boolean ativo,
                   LocalDateTime dataCriacao, LocalDateTime ultimoAcesso) {
        this.id = id;
        this.username = username;
        this.senhaHash = senhaHash;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.nivelPermissao = nivelPermissao;
        this.ativo = ativo;
        this.dataCriacao = dataCriacao;
        this.ultimoAcesso = ultimoAcesso;
    }
    
    // Getters e Setters
    
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getSenhaHash() {
        return senhaHash;
    }
    
    public void setSenhaHash(String senhaHash) {
        this.senhaHash = senhaHash;
    }
    
    public String getNomeCompleto() {
        return nomeCompleto;
    }
    
    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public NivelPermissao getNivelPermissao() {
        return nivelPermissao;
    }
    
    public void setNivelPermissao(NivelPermissao nivelPermissao) {
        this.nivelPermissao = nivelPermissao;
    }
    
    public Boolean getAtivo() {
        return ativo;
    }
    
    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
    
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }
    
    public LocalDateTime getUltimoAcesso() {
        return ultimoAcesso;
    }
    
    public void setUltimoAcesso(LocalDateTime ultimoAcesso) {
        this.ultimoAcesso = ultimoAcesso;
    }
    
    // Métodos de verificação de permissão
    
    public boolean podeVisualizar() {
        return nivelPermissao != null && nivelPermissao.podeVisualizar();
    }
    
    public boolean podeEditarContadores() {
        return nivelPermissao != null && nivelPermissao.podeEditarContadores();
    }
    
    public boolean podeEditarDadosCadastrais() {
        return nivelPermissao != null && nivelPermissao.podeEditarDadosCadastrais();
    }
    
    public boolean podeCadastrar() {
        return nivelPermissao != null && nivelPermissao.podeCadastrar();
    }
    
    public boolean podeDeletar() {
        return nivelPermissao != null && nivelPermissao.podeDeletar();
    }
    
    public boolean podeGerenciarUsuarios() {
        return nivelPermissao != null && nivelPermissao.podeGerenciarUsuarios();
    }
    
    public boolean isAdmin() {
        return nivelPermissao == NivelPermissao.ADMIN;
    }
    
    public boolean isTechnician() {
        return nivelPermissao == NivelPermissao.TECHNICIAN;
    }
    
    public boolean isOperator() {
        return nivelPermissao == NivelPermissao.OPERATOR;
    }
    
    public boolean isViewer() {
        return nivelPermissao == NivelPermissao.VIEWER;
    }
    
    // Métodos auxiliares
    
    public String getDataCriacaoFormatada() {
        if (dataCriacao == null) {
            return "-";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dataCriacao.format(formatter);
    }
    
    public String getUltimoAcessoFormatado() {
        if (ultimoAcesso == null) {
            return "Nunca acessou";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return ultimoAcesso.format(formatter);
    }
    
    public String getStatusTexto() {
        return ativo ? "Ativo" : "Inativo";
    }
    
    public String getNivelPermissaoDescricao() {
        return nivelPermissao != null ? nivelPermissao.getDescricao() : "N/A";
    }
    
    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", nomeCompleto='" + nomeCompleto + '\'' +
                ", nivelPermissao=" + nivelPermissao +
                ", ativo=" + ativo +
                '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id != null && id.equals(usuario.id);
    }
    
    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}