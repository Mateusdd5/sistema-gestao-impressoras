package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

import model.Impressora;

public class ImpressoraDAO {
    private Connection conexao;

    public ImpressoraDAO(Connection conexao) {
        this.conexao = conexao;
    }

    /**
     * Adiciona uma nova impressora ao banco de dados
     */
    public void adicionarImpressora(Impressora impressora) throws Exception {
        // Detectar custo automaticamente se não foi informado
        if (impressora.getCustoPorImpressao() == null) {
            BigDecimal custo = Impressora.detectarCustoPorModelo(impressora.getModeloEquipamento());
            impressora.setCustoPorImpressao(custo);
        }

        String sql = "INSERT INTO impressora (local_instalacao, modelo_equipamento, custo_por_impressao, " +
                     "numero_serie, contador_impressoes, contador_anterior, data_ultima_manutencao, " +
                     "data_relatorio_anterior, secretaria, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, impressora.getLocalInstalacao());
            stmt.setString(2, impressora.getModeloEquipamento());

            if (impressora.getCustoPorImpressao() != null) {
                stmt.setBigDecimal(3, impressora.getCustoPorImpressao());
            } else {
                stmt.setNull(3, java.sql.Types.DECIMAL);
            }

            stmt.setString(4, impressora.getNumeroSerie());
            stmt.setInt(5, impressora.getContadorImpressoes());

            if (impressora.getContadorAnterior() != null) {
                stmt.setInt(6, impressora.getContadorAnterior());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (impressora.getDataUltimaManutencao() != null) {
                stmt.setDate(7, Date.valueOf(impressora.getDataUltimaManutencao()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }

            // data_relatorio_anterior sempre null no cadastro inicial
            stmt.setNull(8, java.sql.Types.DATE);

            stmt.setString(9, impressora.getSecretaria());
            stmt.setString(10, impressora.getStatus());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao cadastrar impressora. Nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    impressora.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Impressora inserida com sucesso - ID: " + impressora.getId());

        } catch (SQLException e) {
            System.err.println("Erro SQL ao inserir impressora:");
            System.err.println("Número de Série: " + impressora.getNumeroSerie());
            System.err.println("Erro: " + e.getMessage());
            throw new RuntimeException("Erro ao inserir impressora: " + e.getMessage(), e);
        }
    }

    /**
     * Lista todas as impressoras
     */
    public List<Impressora> listarImpressoras() throws Exception {
        List<Impressora> lista = new ArrayList<>();
        String sql = "SELECT * FROM impressora ORDER BY secretaria, local_instalacao";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Impressora imp = extrairImpressoraDoResultSet(rs);
                lista.add(imp);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar impressoras: " + e.getMessage(), e);
        }

        return lista;
    }

    /**
     * Lista impressoras filtradas por secretaria
     */
    public List<Impressora> listarImpressorasPorSecretaria(String secretaria) throws Exception {
        List<Impressora> lista = new ArrayList<>();
        String sql = "SELECT * FROM impressora WHERE secretaria = ? ORDER BY local_instalacao";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, secretaria);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Impressora imp = extrairImpressoraDoResultSet(rs);
                    lista.add(imp);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar impressoras por secretaria: " + e.getMessage(), e);
        }

        return lista;
    }

    /**
     * Busca impressora por ID
     */
    public Impressora buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM impressora WHERE id = ?";
        Impressora impressora = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    impressora = extrairImpressoraDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar impressora por ID: " + id);
            throw e;
        }

        return impressora;
    }

    /**
     * Busca impressora por número de série
     */
    public Impressora buscarPorNumeroSerie(String numeroSerie) throws SQLException {
        String sql = "SELECT * FROM impressora WHERE numero_serie = ?";
        Impressora impressora = null;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numeroSerie);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    impressora = extrairImpressoraDoResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar impressora por número de série: " + numeroSerie);
            throw e;
        }

        return impressora;
    }

    /**
     * Atualiza os dados de uma impressora.
     * - Se o contador mudar, salva o antigo em contador_anterior.
     * - Se a data do relatório atualizado mudar, salva a antiga em data_relatorio_anterior.
     */
    public void atualizarImpressora(Impressora impressora) throws SQLException {
        // Busca estado atual no banco
        Impressora impressoraAtual = buscarPorId(impressora.getId());

        // Detectar custo automaticamente se mudou o modelo
        if (impressora.getCustoPorImpressao() == null ||
            (impressoraAtual != null && !impressora.getModeloEquipamento().equals(impressoraAtual.getModeloEquipamento()))) {
            BigDecimal custo = Impressora.detectarCustoPorModelo(impressora.getModeloEquipamento());
            impressora.setCustoPorImpressao(custo);
        }

        // Lógica de rotação da data do relatório
        // Só rotaciona automaticamente se o usuário NÃO informou valor manual para o anterior
        if (impressora.getDataRelatorioAnterior() == null && impressoraAtual != null) {
            LocalDate dataAtualNoBanco = impressoraAtual.getDataUltimaManutencao();
            LocalDate dataNovaInformada = impressora.getDataUltimaManutencao();

            if (dataAtualNoBanco != null && dataNovaInformada != null
                    && !dataNovaInformada.equals(dataAtualNoBanco)) {
                // Data mudou → move a antiga para relatório anterior automaticamente
                impressora.setDataRelatorioAnterior(dataAtualNoBanco);
            } else {
                // Data não mudou → preserva o relatório anterior existente
                impressora.setDataRelatorioAnterior(impressoraAtual.getDataRelatorioAnterior());
            }
        }

        String sql = "UPDATE impressora SET local_instalacao = ?, modelo_equipamento = ?, " +
                     "custo_por_impressao = ?, numero_serie = ?, contador_impressoes = ?, " +
                     "contador_anterior = ?, data_ultima_manutencao = ?, data_relatorio_anterior = ?, " +
                     "secretaria = ?, status = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, impressora.getLocalInstalacao());
            stmt.setString(2, impressora.getModeloEquipamento());

            if (impressora.getCustoPorImpressao() != null) {
                stmt.setBigDecimal(3, impressora.getCustoPorImpressao());
            } else {
                stmt.setNull(3, java.sql.Types.DECIMAL);
            }

            stmt.setString(4, impressora.getNumeroSerie());
            stmt.setInt(5, impressora.getContadorImpressoes());

            // Rotação do contador: se mudou, salva o anterior
            if (impressoraAtual != null &&
                !impressora.getContadorImpressoes().equals(impressoraAtual.getContadorImpressoes())) {
                stmt.setInt(6, impressoraAtual.getContadorImpressoes());
            } else if (impressora.getContadorAnterior() != null) {
                stmt.setInt(6, impressora.getContadorAnterior());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }

            if (impressora.getDataUltimaManutencao() != null) {
                stmt.setDate(7, Date.valueOf(impressora.getDataUltimaManutencao()));
            } else {
                stmt.setNull(7, java.sql.Types.DATE);
            }

            if (impressora.getDataRelatorioAnterior() != null) {
                stmt.setDate(8, Date.valueOf(impressora.getDataRelatorioAnterior()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }

            stmt.setString(9, impressora.getSecretaria());
            stmt.setString(10, impressora.getStatus());
            stmt.setInt(11, impressora.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao atualizar impressora. Nenhuma linha afetada.");
            }

            System.out.println("Impressora atualizada com sucesso - ID: " + impressora.getId());

        } catch (SQLException e) {
            System.err.println("Erro ao atualizar impressora ID: " + impressora.getId());
            throw new SQLException("Erro ao atualizar impressora: " + e.getMessage(), e);
        }
    }

    /**
     * Exclui uma impressora
     */
    public void excluirImpressora(Integer id) throws SQLException {
        String sql = "DELETE FROM impressora WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao excluir impressora. Nenhuma linha afetada.");
            }

            System.out.println("Impressora excluída com sucesso - ID: " + id);

        } catch (SQLException e) {
            System.err.println("Erro ao excluir impressora ID: " + id);
            throw new SQLException("Erro ao excluir impressora: " + e.getMessage(), e);
        }
    }

    /**
     * Conta o total de impressoras
     */
    public int contarImpressoras() throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM impressora";
        int total = 0;

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao contar impressoras: " + e.getMessage(), e);
        }

        return total;
    }

    /**
     * Conta impressoras por secretaria
     */
    public int contarImpressorasPorSecretaria(String secretaria) throws SQLException {
        String sql = "SELECT COUNT(*) as total FROM impressora WHERE secretaria = ?";
        int total = 0;

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, secretaria);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao contar impressoras por secretaria: " + e.getMessage(), e);
        }

        return total;
    }

    /**
     * Busca impressoras com filtro textual
     */
    public List<Impressora> buscarImpressorasPorFiltro(String filtro) throws Exception {
        List<Impressora> lista = new ArrayList<>();

        if (filtro == null || filtro.trim().isEmpty()) {
            return listarImpressoras();
        }

        String sql = "SELECT * FROM impressora WHERE " +
                    "LOWER(local_instalacao) LIKE LOWER(?) OR " +
                    "LOWER(modelo_equipamento) LIKE LOWER(?) OR " +
                    "LOWER(numero_serie) LIKE LOWER(?) OR " +
                    "LOWER(secretaria) LIKE LOWER(?) " +
                    "ORDER BY secretaria, local_instalacao";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            String parametroBusca = "%" + filtro.trim() + "%";

            stmt.setString(1, parametroBusca);
            stmt.setString(2, parametroBusca);
            stmt.setString(3, parametroBusca);
            stmt.setString(4, parametroBusca);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Impressora imp = extrairImpressoraDoResultSet(rs);
                    lista.add(imp);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao buscar impressoras com filtro: " + filtro);
            throw new RuntimeException("Erro ao buscar impressoras: " + e.getMessage(), e);
        }

        return lista;
    }

    /**
     * Lista todas as secretarias distintas
     */
    public List<String> listarSecretarias() throws SQLException {
        List<String> secretarias = new ArrayList<>();
        String sql = "SELECT DISTINCT secretaria FROM impressora ORDER BY secretaria";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String secretaria = rs.getString("secretaria");
                if (secretaria != null && !secretaria.trim().isEmpty()) {
                    secretarias.add(secretaria);
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao listar secretarias: " + e.getMessage(), e);
        }

        return secretarias;
    }

    /**
     * Calcula custo total mensal por secretaria
     */
    public Map<String, BigDecimal> calcularCustoMensalPorSecretaria() throws SQLException {
        Map<String, BigDecimal> custos = new HashMap<>();

        String sql = "SELECT secretaria, " +
                     "SUM((contador_impressoes - IFNULL(contador_anterior, 0)) * IFNULL(custo_por_impressao, 0)) as custo_total " +
                     "FROM impressora " +
                     "GROUP BY secretaria " +
                     "ORDER BY secretaria";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String secretaria = rs.getString("secretaria");
                BigDecimal custoTotal = rs.getBigDecimal("custo_total");
                if (custoTotal == null) {
                    custoTotal = BigDecimal.ZERO;
                }
                custos.put(secretaria, custoTotal);
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao calcular custo mensal por secretaria: " + e.getMessage(), e);
        }

        return custos;
    }

    /**
     * Calcula custo total mensal de uma secretaria específica
     */
    public BigDecimal calcularCustoMensalSecretaria(String secretaria) throws SQLException {
        BigDecimal custoTotal = BigDecimal.ZERO;

        String sql = "SELECT SUM((contador_impressoes - IFNULL(contador_anterior, 0)) * IFNULL(custo_por_impressao, 0)) as custo_total " +
                     "FROM impressora WHERE secretaria = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, secretaria);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal resultado = rs.getBigDecimal("custo_total");
                    if (resultado != null) {
                        custoTotal = resultado;
                    }
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Erro ao calcular custo mensal da secretaria: " + e.getMessage(), e);
        }

        return custoTotal;
    }

    /**
     * Método auxiliar para extrair uma Impressora do ResultSet
     */
    private Impressora extrairImpressoraDoResultSet(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("id");
        String localInstalacao = rs.getString("local_instalacao");
        String modeloEquipamento = rs.getString("modelo_equipamento");

        BigDecimal custoPorImpressao = rs.getBigDecimal("custo_por_impressao");
        if (rs.wasNull()) {
            custoPorImpressao = null;
        }

        String numeroSerie = rs.getString("numero_serie");
        Integer contadorImpressoes = rs.getInt("contador_impressoes");

        Integer contadorAnterior = rs.getInt("contador_anterior");
        if (rs.wasNull()) {
            contadorAnterior = null;
        }

        Date dataManutencao = rs.getDate("data_ultima_manutencao");
        LocalDate dataUltimaManutencao = (dataManutencao != null) ? dataManutencao.toLocalDate() : null;

        Date dataRelAnterior = rs.getDate("data_relatorio_anterior");
        LocalDate dataRelatorioAnterior = (dataRelAnterior != null) ? dataRelAnterior.toLocalDate() : null;

        String secretaria = rs.getString("secretaria");
        String status = rs.getString("status");

        Impressora impressora = new Impressora(id, localInstalacao, modeloEquipamento, custoPorImpressao,
                             numeroSerie, contadorImpressoes, contadorAnterior,
                             dataUltimaManutencao, secretaria, status);
        impressora.setDataRelatorioAnterior(dataRelatorioAnterior);
        return impressora;
    }
}