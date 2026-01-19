package utils;

import org.mindrot.jbcrypt.BCrypt;

public class DescobridorSenha {
    
    public static void main(String[] args) {
        // Hash que está no banco
        String hashNoBanco = "$2a$10$vI8aWBnW3fID.ZQ4/zo1G.q1lRps.9cGLcZEiGDMVr5yUP1KUOYTa";
        
        // Lista de senhas comuns para testar
        String[] senhasPossiveis = {
            "admin123",
            "password",
            "admin",
            "123456",
            "12345678",
            "senha123",
            "teste",
            "teste123",
            "root",
            "root123",
            "admin@123",
            "Password",
            "Password123",
            "senha",
            "1234",
            "",
            "admin1234",
            "tecnico123",
            "operador123",
            "viewer123"
        };
        
        System.out.println("========================================");
        System.out.println("DESCOBRIDOR DE SENHA BCrypt");
        System.out.println("========================================\n");
        
        System.out.println("Hash a testar:");
        System.out.println(hashNoBanco);
        System.out.println("\n----------------------------------------\n");
        
        boolean encontrou = false;
        
        for (String senha : senhasPossiveis) {
            boolean valida = BCrypt.checkpw(senha, hashNoBanco);
            
            if (valida) {
                System.out.println("🎉 SENHA ENCONTRADA!");
                System.out.println("A senha correta é: '" + senha + "'");
                System.out.println("========================================");
                encontrou = true;
                break;
            } else {
                System.out.println("❌ '" + senha + "' - não é a senha");
            }
        }
        
        if (!encontrou) {
            System.out.println("\n⚠️ SENHA NÃO ENCONTRADA na lista!");
            System.out.println("Vou gerar novos hashes para você usar:");
            System.out.println("\n========================================");
            System.out.println("NOVOS HASHES GERADOS");
            System.out.println("========================================\n");
            
            String[] novasSenhas = {"admin123", "tecnico123", "operador123", "viewer123"};
            
            for (String senha : novasSenhas) {
                String novoHash = BCrypt.hashpw(senha, BCrypt.gensalt(10));
                System.out.println("Senha: " + senha);
                System.out.println("Hash:  " + novoHash);
                System.out.println("----------------------------------------");
            }
        }
    }
}