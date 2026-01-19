package dao;

import model.Usuario;
import model.NivelPermissao;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações de usuários no banco de dados
 */
public class UsuarioDAO {
    
    private Connection conexao;
    
    public UsuarioDAO(Connection conexao) {
        this.conexao = conexao;
    }
    
    /**
     * Busca usuário por username
     * Usado no processo de login
     */
    public Usuario buscarPorUsername(String username) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE username = ? AND ativo = TRUE";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuarioDoResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Busca usuário por ID
     */
    public Usuario buscarPorId(int id) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairUsuarioDoResultSet(rs);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Lista todos os usuários
     */
    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM usuario ORDER BY nome_completo";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(extrairUsuarioDoResultSet(rs));
            }
        }
        
        return usuarios;
    }
    
    /**
     * Lista usuários ativos
     */
    public List<Usuario> listarAtivos() throws SQLException {
        String sql = "SELECT * FROM usuario WHERE ativo = TRUE ORDER BY nome_completo";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                usuarios.add(extrairUsuarioDoResultSet(rs));
            }
        }
        
        return usuarios;
    }
    
    /**
     * Lista usuários por nível de permissão
     */
    public List<Usuario> listarPorNivel(NivelPermissao nivel) throws SQLException {
        String sql = "SELECT * FROM usuario WHERE nivel_permissao = ? AND ativo = TRUE ORDER BY nome_completo";
        List<Usuario> usuarios = new ArrayList<>();
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, nivel.name());
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(extrairUsuarioDoResultSet(rs));
                }
            }
        }
        
        return usuarios;
    }
    
    /**
     * Adiciona novo usuário
     */
    public boolean adicionar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (username, senha_hash, nome_completo, email, nivel_permissao, ativo) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getUsername());
            stmt.setString(2, usuario.getSenhaHash());
            stmt.setString(3, usuario.getNomeCompleto());
            stmt.setString(4, usuario.getEmail());
            stmt.setString(5, usuario.getNivelPermissao().name());
            stmt.setBoolean(6, usuario.getAtivo());
            
            int linhasAfetadas = stmt.executeUpdate();
            
            if (linhasAfetadas > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        usuario.setId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Atualiza dados do usuário
     */
    public boolean atualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nome_completo = ?, email = ?, nivel_permissao = ?, ativo = ? " +
                     "WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNomeCompleto());
            stmt.setString(2, usuario.getEmail());
            stmt.setString(3, usuario.getNivelPermissao().name());
            stmt.setBoolean(4, usuario.getAtivo());
            stmt.setInt(5, usuario.getId());
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Atualiza senha do usuário
     */
    public boolean atualizarSenha(int usuarioId, String novaSenhaHash) throws SQLException {
        String sql = "UPDATE usuario SET senha_hash = ? WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novaSenhaHash);
            stmt.setInt(2, usuarioId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Atualiza último acesso do usuário
     */
    public boolean atualizarUltimoAcesso(int usuarioId) throws SQLException {
        String sql = "UPDATE usuario SET ultimo_acesso = NOW() WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Desativa usuário (não deleta do banco)
     */
    public boolean desativar(int usuarioId) throws SQLException {
        String sql = "UPDATE usuario SET ativo = FALSE WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Reativa usuário
     */
    public boolean reativar(int usuarioId) throws SQLException {
        String sql = "UPDATE usuario SET ativo = TRUE WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Deleta usuário permanentemente do banco
     * CUIDADO: Esta operação é irreversível!
     */
    public boolean deletar(int usuarioId) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Verifica se username já existe
     */
    public boolean usernameExiste(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Verifica se username existe para outro usuário (útil na edição)
     */
    public boolean usernameExisteParaOutroUsuario(String username, int usuarioId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE username = ? AND id != ?";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setInt(2, usuarioId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Conta total de usuários ativos
     */
    public int contarUsuariosAtivos() throws SQLException {
        String sql = "SELECT COUNT(*) FROM usuario WHERE ativo = TRUE";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        
        return 0;
    }
    
    /**
     * Registra log de ação no banco
     */
    public void registrarLog(int usuarioId, String acao, String descricao, String ipAddress) throws SQLException {
        String sql = "INSERT INTO log_acoes (usuario_id, acao, descricao, ip_address) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            stmt.setString(2, acao);
            stmt.setString(3, descricao);
            stmt.setString(4, ipAddress);
            
            stmt.executeUpdate();
        }
    }
    
    /**
     * Extrai objeto Usuario do ResultSet
     */
    private Usuario extrairUsuarioDoResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        
        usuario.setId(rs.getInt("id"));
        usuario.setUsername(rs.getString("username"));
        usuario.setSenhaHash(rs.getString("senha_hash"));
        usuario.setNomeCompleto(rs.getString("nome_completo"));
        usuario.setEmail(rs.getString("email"));
        
        String nivelStr = rs.getString("nivel_permissao");
        usuario.setNivelPermissao(NivelPermissao.valueOf(nivelStr));
        
        usuario.setAtivo(rs.getBoolean("ativo"));
        
        Timestamp dataCriacao = rs.getTimestamp("data_criacao");
        if (dataCriacao != null) {
            usuario.setDataCriacao(dataCriacao.toLocalDateTime());
        }
        
        Timestamp ultimoAcesso = rs.getTimestamp("ultimo_acesso");
        if (ultimoAcesso != null) {
            usuario.setUltimoAcesso(ultimoAcesso.toLocalDateTime());
        }
        
        return usuario;
    }
}