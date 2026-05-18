package dao;

import model.HistoricoContador;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para operações na tabela historico_contador.
 *
 * Regra central de gravação (INSERT ... ON DUPLICATE KEY UPDATE):
 * - Data nova (nunca vista para essa impressora) → INSERT
 * - Mesma data, contador corrigido → UPDATE no registro existente
 * Isso evita cadeia de valores incorretos quando um contador é corrigido
 * sem avançar a data do relatório.
 *
 * Limite: 60 registros por impressora. Ao ultrapassar, o mais antigo é removido.
 */
public class HistoricoContadorDAO {

    private static final int LIMITE_REGISTROS = 60;

    private final Connection conexao;

    public HistoricoContadorDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Salva ou atualiza um registro de histórico.
     * Chamado automaticamente por ImpressoraDAO.atualizarImpressora().
     */
    public void salvarOuAtualizar(int impressoraId, LocalDate dataRelatorio, BigDecimal contadorValor)
            throws SQLException {

        String sql = "INSERT INTO historico_contador (impressora_id, data_relatorio, contador_valor) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "contador_valor = VALUES(contador_valor), " +
                     "data_gravacao  = NOW(), " +
                     "editado_manual = FALSE, " +
                     "editado_por    = NULL";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, impressoraId);
            stmt.setDate(2, Date.valueOf(dataRelatorio));
            stmt.setBigDecimal(3, contadorValor);
            stmt.executeUpdate();
        }

        limitarHistorico(impressoraId);
    }

    /**
     * Permite que um ADMIN edite manualmente um valor de contador no histórico.
     */
    public boolean atualizarManual(int id, BigDecimal novoValor, String editadoPor) throws SQLException {
        String sql = "UPDATE historico_contador " +
                     "SET contador_valor = ?, editado_manual = TRUE, editado_por = ?, data_gravacao = NOW() " +
                     "WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setBigDecimal(1, novoValor);
            stmt.setString(2, editadoPor);
            stmt.setInt(3, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Deleta um registro específico do histórico (apenas ADMIN).
     */
    public boolean deletarRegistro(int id) throws SQLException {
        String sql = "DELETE FROM historico_contador WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Lista todo o histórico com dados da impressora (JOIN),
     * opcionalmente filtrado por secretaria.
     */
    public List<HistoricoContador> listarTodos(String secretaria) throws SQLException {
        String sql = "SELECT hc.*, i.secretaria, i.local_instalacao, i.modelo_equipamento, i.numero_serie " +
                     "FROM historico_contador hc " +
                     "JOIN impressora i ON hc.impressora_id = i.id ";

        if (secretaria != null && !secretaria.equals("TODAS")) {
            sql += "WHERE i.secretaria = ? ";
        }

        sql += "ORDER BY i.secretaria, i.local_instalacao, hc.data_relatorio DESC";

        List<HistoricoContador> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (secretaria != null && !secretaria.equals("TODAS")) {
                stmt.setString(1, secretaria);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairDoResultSet(rs));
                }
            }
        }

        return lista;
    }

    /**
     * Lista o histórico de uma impressora específica, do mais recente ao mais antigo.
     */
    public List<HistoricoContador> listarPorImpressora(int impressoraId) throws SQLException {
        String sql = "SELECT hc.*, i.secretaria, i.local_instalacao, i.modelo_equipamento, i.numero_serie " +
                     "FROM historico_contador hc " +
                     "JOIN impressora i ON hc.impressora_id = i.id " +
                     "WHERE hc.impressora_id = ? " +
                     "ORDER BY hc.data_relatorio DESC";

        List<HistoricoContador> lista = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, impressoraId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(extrairDoResultSet(rs));
                }
            }
        }

        return lista;
    }

    /**
     * Busca um registro específico pelo ID.
     */
    public HistoricoContador buscarPorId(int id) throws SQLException {
        String sql = "SELECT hc.*, i.secretaria, i.local_instalacao, i.modelo_equipamento, i.numero_serie " +
                     "FROM historico_contador hc " +
                     "JOIN impressora i ON hc.impressora_id = i.id " +
                     "WHERE hc.id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return extrairDoResultSet(rs);
                }
            }
        }

        return null;
    }

    /**
     * Garante que cada impressora tenha no máximo LIMITE_REGISTROS (60) entradas.
     */
    private void limitarHistorico(int impressoraId) throws SQLException {
        String sqlContar = "SELECT COUNT(*) FROM historico_contador WHERE impressora_id = ?";

        int total = 0;
        try (PreparedStatement stmt = conexao.prepareStatement(sqlContar)) {
            stmt.setInt(1, impressoraId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) total = rs.getInt(1);
            }
        }

        if (total > LIMITE_REGISTROS) {
            int excesso = total - LIMITE_REGISTROS;
            String sqlDeletar = "DELETE FROM historico_contador " +
                                "WHERE impressora_id = ? " +
                                "ORDER BY data_relatorio ASC " +
                                "LIMIT ?";

            try (PreparedStatement stmt = conexao.prepareStatement(sqlDeletar)) {
                stmt.setInt(1, impressoraId);
                stmt.setInt(2, excesso);
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Extrai um HistoricoContador do ResultSet (com campos do JOIN).
     */
    private HistoricoContador extrairDoResultSet(ResultSet rs) throws SQLException {
        HistoricoContador h = new HistoricoContador();

        h.setId(rs.getInt("id"));
        h.setImpressoraId(rs.getInt("impressora_id"));

        Date dataRel = rs.getDate("data_relatorio");
        if (dataRel != null) h.setDataRelatorio(dataRel.toLocalDate());

        h.setContadorValor(rs.getBigDecimal("contador_valor"));

        Timestamp dataGrav = rs.getTimestamp("data_gravacao");
        if (dataGrav != null) h.setDataGravacao(dataGrav.toLocalDateTime());

        h.setEditadoManual(rs.getBoolean("editado_manual"));
        h.setEditadoPor(rs.getString("editado_por"));

        h.setSecretaria(rs.getString("secretaria"));
        h.setLocalInstalacao(rs.getString("local_instalacao"));
        h.setModeloEquipamento(rs.getString("modelo_equipamento"));
        h.setNumeroSerie(rs.getString("numero_serie"));

        return h;
    }
}