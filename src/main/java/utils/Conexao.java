package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para gerenciar conexões com o banco de dados MySQL
 */
public class Conexao {
    
    // Configurações do banco de dados - AJUSTE CONFORME SEU AMBIENTE
    private static final String URL = "jdbc:mysql://localhost:3306/controle_impressoras";
    private static final String USUARIO = "root";
    private static final String SENHA = "@dm1ndb";
    
    // Configurações adicionais da URL
    private static final String PARAMETROS = "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=utf8";
    
    private static Connection conexao = null;

    /**
     * Obtém uma conexão com o banco de dados
     * @return Conexão ativa com o banco
     * @throws SQLException se houver erro na conexão
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Carrega o driver JDBC do MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Se a conexão não existe ou está fechada, cria uma nova
            if (conexao == null || conexao.isClosed()) {
                conexao = DriverManager.getConnection(URL + PARAMETROS, USUARIO, SENHA);
                System.out.println("✓ Conexão com banco de dados estabelecida com sucesso!");
            }
            
            return conexao;
            
        } catch (ClassNotFoundException e) {
            System.err.println("✗ Driver JDBC do MySQL não encontrado!");
            System.err.println("Certifique-se de adicionar o mysql-connector-java ao projeto.");
            throw new SQLException("Driver não encontrado: " + e.getMessage(), e);
            
        } catch (SQLException e) {
            System.err.println("✗ Erro ao conectar ao banco de dados!");
            System.err.println("URL: " + URL);
            System.err.println("Usuário: " + USUARIO);
            System.err.println("Erro: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Fecha a conexão com o banco de dados
     */
    public static void closeConnection() {
        try {
            if (conexao != null && !conexao.isClosed()) {
                conexao.close();
                System.out.println("✓ Conexão com banco de dados fechada.");
            }
        } catch (SQLException e) {
            System.err.println("✗ Erro ao fechar conexão: " + e.getMessage());
        }
    }

    /**
     * Testa a conexão com o banco de dados
     * @return true se a conexão foi bem-sucedida, false caso contrário
     */
    public static boolean testarConexao() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("✓ Teste de conexão bem-sucedido!");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Teste de conexão falhou: " + e.getMessage());
        }
        return false;
    }

    /**
     * Método main para testar a conexão
     */
    public static void main(String[] args) {
        System.out.println("=== TESTE DE CONEXÃO COM BANCO DE DADOS ===");
        System.out.println("URL: " + URL);
        System.out.println("Usuário: " + USUARIO);
        System.out.println();
        
        if (testarConexao()) {
            System.out.println("\n✓ Conexão estabelecida com sucesso!");
            closeConnection();
        } else {
            System.out.println("\n✗ Falha ao conectar ao banco de dados.");
            System.out.println("\nVerifique:");
            System.out.println("1. O MySQL está rodando?");
            System.out.println("2. O banco de dados 'controle_impressoras' existe?");
            System.out.println("3. As credenciais (usuário e senha) estão corretas?");
            System.out.println("4. O driver JDBC do MySQL está no classpath?");
        }
    }
}