package br.com.teahub.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
  - Classe responsável pela conexão com o banco de dados PostgreSQL.
 
  Aplica o padrão Singleton de forma simplificada: o método conectar()
  é estático, ou seja, não é necessário instanciar a classe para usá-la.
  Toda a aplicação chama ConexaoBanco.conectar() a partir de um único ponto.
 
  Disciplinas contempladas:
  - POO: modificadores de acesso (private/public/static), encapsulamento
  - Design: padrão Singleton (acesso global via método estático)
  - Banco de Dados: conexão JDBC com PostgreSQL
 */

public class ConexaoBanco {
    
    // Endereço completo do banco de dados no formato JDBC
    private static final String URL = "jdbc:postgresql://localhost:5432/TEAHub";
    
    // Usuário padrão do PostgreSQL
    private static final String USER = "postgres";
    
    // Senha definida na instalação do PostgreSQL
    private static final String PASSWORD = "1230";

    
    /*
      - Abre e retorna uma conexão com o banco de dados.
     
      O método tenta carregar o driver JDBC do PostgreSQL (Class.forName)
      e em seguida estabelece a conexão com as credenciais acima.
     
      @return Connection ativa, ou null se a conexão falhar.
     */
    
    public static Connection conectar() {
        
        // Carrega o driver do PostgreSQL na memória da JVM
        try {
            // Carrega o driver do PostgreSQL na memória da JVM
            Class.forName("org.postgresql.Driver"); 
            
            // Cria e retorna a conexão usando as constantes definidas acima
            return DriverManager.getConnection(URL, USER, PASSWORD);
            
        } catch (ClassNotFoundException e) {
            // Ocorre quando o arquivo .jar do PostgreSQL não está nas Libraries do projeto
            System.out.println("\n[ERRO DE CONEXÃO]: O Driver do PostgreSQL não foi encontrado nas bibliotecas do projeto!");
            e.printStackTrace(); 
            return null;
        } catch (SQLException e) {
            // Ocorre quando a senha, o nome do banco ou o serviço do PostgreSQL estão incorretos
            System.out.println("\n========================================================");
            System.out.println("[ERRO DE CONEXÃO]: Falha ao conectar ao PostgreSQL.");
            System.out.println("Motivo real: " + e.getMessage());
            System.out.println("Código de Estado SQL: " + e.getSQLState());
            System.out.println("Verifique: 1. Se a senha está correta.");
            System.out.println("           2. Se o nome do banco está idêntico ao criado no pgAdmin.");
            System.out.println("           3. Se o serviço do PostgreSQL está ativo no seu computador.");
            System.out.println("========================================================\n");
            e.printStackTrace(); 
            return null;
        }
    }

    
    /*
     - Método utilitário para testar a conexão de forma isolada.
       Pode ser executado diretamente (Run File) para verificar se o banco está acessível.
     */
    public static void main(String[] args) {
        System.out.println("Iniciando teste de conexão isolado...");
        Connection conn = conectar();
        
        if (conn != null) {
            System.out.println("\n[SUCESSO]: O Java conectou perfeitamente ao banco TEAHub!");
            try { 
                conn.close(); // Fecha a conexão após o teste para liberar recursos
            } catch (SQLException ex) {
                // Ignora erros ao fechar, pois o teste já foi concluído
            }
        } else {
            System.out.println("\n[FALHA]: A conexão retornou null. Olhe as mensagens de erro detalhadas acima.");
        }
    }
}