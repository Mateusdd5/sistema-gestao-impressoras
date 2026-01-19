package utils;

import org.mindrot.jbcrypt.BCrypt;

/**
 * Classe utilitária para criptografia e validação de senhas usando BCrypt
 */
public class SenhaUtil {
    
    /**
     * Número de rounds do BCrypt (quanto maior, mais seguro mas mais lento)
     * 10 é um bom equilíbrio entre segurança e performance
     */
    private static final int BCRYPT_ROUNDS = 10;
    
    /**
     * Criptografa uma senha em texto plano usando BCrypt
     * 
     * @param senhaTextoPlano Senha em texto plano (ex: "admin123")
     * @return Hash BCrypt da senha (ex: "$2a$10$xyz...")
     */
    public static String criptografarSenha(String senhaTextoPlano) {
        if (senhaTextoPlano == null || senhaTextoPlano.trim().isEmpty()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        
        // Gera um salt aleatório e criptografa a senha
        return BCrypt.hashpw(senhaTextoPlano, BCrypt.gensalt(BCRYPT_ROUNDS));
    }
    
    /**
     * Verifica se uma senha em texto plano corresponde ao hash armazenado
     * 
     * @param senhaTextoPlano Senha digitada pelo usuário (ex: "admin123")
     * @param senhaHash Hash armazenado no banco (ex: "$2a$10$xyz...")
     * @return true se a senha está correta, false caso contrário
     */
    public static boolean verificarSenha(String senhaTextoPlano, String senhaHash) {
        if (senhaTextoPlano == null || senhaHash == null) {
            return false;
        }
        
        try {
            // BCrypt compara a senha com o hash de forma segura
            return BCrypt.checkpw(senhaTextoPlano, senhaHash);
        } catch (IllegalArgumentException e) {
            // Hash inválido ou corrompido
            return false;
        }
    }
    
    /**
     * Valida a força de uma senha
     * 
     * @param senha Senha a ser validada
     * @return true se a senha atende aos requisitos mínimos
     */
    public static boolean validarForcaSenha(String senha) {
        if (senha == null || senha.length() < 6) {
            return false;
        }
        
        // Requisitos mínimos:
        // - Pelo menos 6 caracteres
        // - Pode adicionar mais validações aqui se necessário
        
        return true;
    }
    
    /**
     * Gera uma mensagem de erro de validação de senha
     * 
     * @param senha Senha a ser validada
     * @return Mensagem de erro ou null se a senha for válida
     */
    public static String obterErroValidacao(String senha) {
        if (senha == null || senha.trim().isEmpty()) {
            return "A senha não pode estar vazia";
        }
        
        if (senha.length() < 6) {
            return "A senha deve ter pelo menos 6 caracteres";
        }
        
        if (senha.length() > 50) {
            return "A senha não pode ter mais de 50 caracteres";
        }
        
        // Senha válida
        return null;
    }
    
    /**
     * Verifica se uma string é um hash BCrypt válido
     * 
     * @param hash String a ser verificada
     * @return true se for um hash BCrypt válido
     */
    public static boolean isHashValido(String hash) {
        if (hash == null || hash.isEmpty()) {
            return false;
        }
        
        // Hash BCrypt começa com "$2a$", "$2b$" ou "$2y$"
        return hash.matches("^\\$2[ayb]\\$.{56}$");
    }
}