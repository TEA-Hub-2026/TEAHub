package br.com.teahub.dao;

import br.com.teahub.config.ConexaoBanco; 
import br.com.teahub.model.Clinica;
import br.com.teahub.model.Paciente;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/*
 - Classe de acesso a dados (DAO) responsável por todas as operações
   de banco de dados relacionadas à entidade Paciente.
 
   Implementa a interface DAOPaciente, garantindo que o CRUD completo
   (Create, Read, Update, Delete) seja obrigatoriamente implementado.
 
 Pilares de POO e padrões aplicados:
  - Padrão DAO: separa a lógica de banco da lógica de negócio e da View.
  - Interface + Polimorfismo: implementa DAOPaciente com @Override.
  - Coleções: usa ListPaciente (ArrayList) para retornar múltiplos registros.
  - Tratamento de Exceções: captura SQLException em todos os métodos.
  - Encapsulamento: lógica de banco isolada nesta classe.
 */

public class PacienteDAO implements DAO<Paciente> {

    /*
      CREATE — Insere um novo paciente no banco de dados.
     
      Usa PreparedStatement para evitar SQL Injection,
      mapeando cada campo do objeto Paciente para um parâmetro (?) da query.
    
      @param paciente Objeto Paciente preenchido pela View
      @return true se o cadastro foi realizado com sucesso, false caso contrário
     */
    
    @Override
    public boolean cadastrar(Paciente paciente) {
        String sql = "INSERT INTO paciente (nome_paciente, data_nascimento, responsavel, telefone_responsavel, nivel_tea, status_ativo, id_clinica) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            // Verifica se a conexão foi estabelecida antes de prosseguir
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Mapeia cada atributo do objeto para o parâmetro correspondente na query
                stmt.setString(1, paciente.getNomePaciente());
                stmt.setDate(2, Date.valueOf(paciente.getDataNascimento()));
                stmt.setString(3, paciente.getResponsavel());
                stmt.setString(4, paciente.getTelefoneResponsavel());
                stmt.setInt(5, paciente.getNivelTea());
                stmt.setBoolean(6, paciente.isStatusAtivo());
                stmt.setInt(7, paciente.getClinica().getIdClinica()); 
                
                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[Erro PacienteDAO] Falha ao cadastrar: " + e.getMessage());
            return false;
        }
    }

     /*
      - UPDATE — Atualiza os dados de um paciente já existente no banco.
     
      Usa o id_paciente como critério de identificação do registro a atualizar.
      Retorna true somente se ao menos uma linha foi afetada (executeUpdate > 0).
     
      @param paciente Objeto Paciente com os dados atualizados
      @return true se a atualização foi bem-sucedida, false caso contrário
     */
    @Override
    public boolean atualizar(Paciente paciente) {
        String sql = "UPDATE paciente SET nome_paciente = ?, data_nascimento = ?, responsavel = ?, telefone_responsavel = ?, nivel_tea = ?, status_ativo = ? WHERE id_paciente = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, paciente.getNomePaciente());
                stmt.setDate(2, Date.valueOf(paciente.getDataNascimento()));
                stmt.setString(3, paciente.getResponsavel());
                stmt.setString(4, paciente.getTelefoneResponsavel());
                stmt.setInt(5, paciente.getNivelTea());
                stmt.setBoolean(6, paciente.isStatusAtivo());
                stmt.setInt(7, paciente.getIdPaciente());  // Identifica qual registro atualizar

                // executeUpdate retorna o número de linhas afetadas; > 0 significa sucesso
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro PacienteDAO] Falha ao atualizar: " + e.getMessage());
            return false;
        }
    }

    /*
     - DELETE — Remove permanentemente um paciente do banco de dados.
     
      Atenção: esta operação é irreversível. Para casos em que se deseja apenas
      desativar o paciente sem removê-lo, utilize o método desativar().
     
      @param idPaciente Identificador único do paciente a ser removido
      @return true se a exclusão foi bem-sucedida, false caso contrário
     */
    @Override
    public boolean excluir(int idPaciente) { 
        String sql = "DELETE FROM paciente WHERE id_paciente = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPaciente);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro PacienteDAO] Falha ao deletar: " + e.getMessage());
            return false;
        }
    }

     /*
      - READ — Lista os pacientes de uma clínica, com suporte a filtro por nome.
     
     Se o parâmetro "busca" for nulo ou vazio, retorna todos os pacientes da clínica.
     Se "busca" tiver conteúdo, aplica o filtro ILIKE (case-insensitive) no nome.
     
     Usa coleção ListPaciente (ArrayList) para armazenar múltiplos resultados,
     demonstrando o uso de Generics e Coleções exigidos pela disciplina de POO.
     
     @param idClinica ID da clínica cujos pacientes serão listados
     @param busca     Texto para filtrar pelo nome; null ou vazio retorna todos
     @return Lista de pacientes encontrados (pode ser vazia, nunca null)
     */
    @Override
    public List<Paciente> listar(int idClinica, String busca) {
        // Coleção que armazenará os resultados da consulta
        List<Paciente> lista = new ArrayList<>();
        
        // Define a query dinamicamente: com ou sem filtro de nome
        String sql = (busca == null || busca.trim().isEmpty())
                ? "SELECT * FROM paciente WHERE id_clinica = ? ORDER BY nome_paciente"
                : "SELECT * FROM paciente WHERE id_clinica = ? AND nome_paciente ILIKE ? ORDER BY nome_paciente";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return lista;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idClinica);
                
                // Adiciona o segundo parâmetro apenas quando há busca ativa
                if (busca != null && !busca.trim().isEmpty()) {
                    stmt.setString(2, "%" + busca + "%"); // % permite correspondência parcial
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    // Itera sobre cada linha retornada e monta um objeto Paciente
                    while (rs.next()) {
                        Paciente p = new Paciente();
                        p.setIdPaciente(rs.getInt("id_paciente"));
                        p.setNomePaciente(rs.getString("nome_paciente"));
                        p.setDataNascimento(rs.getDate("data_nascimento").toLocalDate()); // Converte para LocalDate
                        p.setResponsavel(rs.getString("responsavel"));
                        p.setTelefoneResponsavel(rs.getString("telefone_responsavel"));
                        p.setNivelTea(rs.getInt("nivel_tea"));
                        p.setStatusAtivo(rs.getBoolean("status_ativo"));
                        
                        // Reconstrói o objeto Clinica para manter o relacionamento entre entidades
                        Clinica c = new Clinica();
                        c.setIdClinica(rs.getInt("id_clinica"));
                        p.setClinica(c);
                        
                        lista.add(p); // Adiciona o paciente à coleção
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erro PacienteDAO] Falha ao listar/filtrar pacientes: " + e.getMessage());
        }
        return lista;
    }

    /*
     - Desativa um paciente sem excluí-lo do banco (exclusão lógica).
     
      Diferente do método excluir(), este apenas muda o campo status_ativo para false,
      preservando o histórico do paciente no sistema.
     
      @param idPaciente ID do paciente a ser desativado
      @return true se a desativação foi bem-sucedida, false caso contrário
     */
    public boolean desativar(int idPaciente) {
        String sql = "UPDATE paciente SET status_ativo = false WHERE id_paciente = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idPaciente);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro PacienteDAO] Falha ao desativar: " + e.getMessage());
            return false;
        }
    }
    
    /*
     - Lista todos os pacientes do sistema (sem filtro de clínica).
      Retorna apenas id e nome, otimizando o desempenho quando apenas
      esses dois campos são necessários (ex: ComboBox de seleção).
     
      @return Lista com todos os pacientes cadastrados
     */
    public List<Paciente> listarTodosParaSeletor() {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT id_paciente, nome_paciente FROM paciente ORDER BY nome_paciente";
        
        try (Connection conn = ConexaoBanco.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (conn == null) return lista;
            
            while (rs.next()) {
                Paciente p = new Paciente();
                p.setIdPaciente(rs.getInt("id_paciente"));
                p.setNomePaciente(rs.getString("nome_paciente"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("[Erro PacienteDAO] Falha no seletor geral: " + e.getMessage());
        }
        return lista;
    }
}