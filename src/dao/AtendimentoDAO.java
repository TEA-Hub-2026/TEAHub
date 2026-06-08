package br.com.teahub.dao;

import br.com.teahub.config.ConexaoBanco; 
import br.com.teahub.model.Atendimento;
import br.com.teahub.model.Paciente;
import br.com.teahub.model.Profissional;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/*
 - Classe de acesso a dados (DAO) responsável por todas as operações
   de banco de dados relacionadas à entidade Atendimento.
 
   Implementa a interface DAO, garantindo a padronização das operações
   de persistência no sistema.

 Pilares de POO e padrões aplicados:
  - Padrão DAO: abstrai a persistência e isola o SQL da lógica de negócio.
  - Relacionamento entre Entidades: manipula objetos compostos (Paciente e Profissional).
  - SQL dinâmico: utiliza blocos de texto e filtros para otimização de consultas.
  - Tratamento de Exceções: captura de erros com logs centralizados em System.err.
 */
public class AtendimentoDAO implements DAO<Atendimento> {

    /*
     CREATE — Insere um novo registro de atendimento.
    
     Utiliza PreparedStatement para mitigar riscos de SQL Injection.
     Mapeia o LocalDateTime para Timestamp do JDBC.
    
     @param a Objeto Atendimento contendo os dados a serem persistidos
     @return true se o registro foi inserido, false caso contrário
     */
    @Override
    public boolean cadastrar(Atendimento a) {
        String sql = """
            INSERT INTO atendimento (id_paciente, id_profissional, observacoes, data_atendimento)
            VALUES (?, ?, ?, ?)
            """;
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, a.getPaciente().getIdPaciente());
                ps.setInt(2, a.getProfissional().getIdProfissional()); 
                ps.setString(3, a.getObservacoes());
                ps.setTimestamp(4, Timestamp.valueOf(a.getDataAtendimento()));
                
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro AtendimentoDAO] Falha ao cadastrar: " + e.getMessage());
            return false;
        }
    }

    // Método auxiliar para manter compatibilidade com nomes de métodos similares
    public boolean actualizar(Atendimento a) { 
        return modificarAtendimento(a);
    }

    /*
     UPDATE — Atualiza os dados de um atendimento existente.
    
     Reutiliza a lógica privada de modificação para manter DRY (Don't Repeat Yourself).
    
     @param a Objeto Atendimento com os dados atualizados
     @return true se a atualização foi bem-sucedida, false caso contrário
     */
    @Override
    public boolean atualizar(Atendimento a) {
        return modificarAtendimento(a);
    }

    /*
     Lógica compartilhada para atualizar registros.
    
     Centraliza a query de update, facilitando a manutenção caso novos
     campos sejam adicionados ao atendimento.
     */
    private boolean modificarAtendimento(Atendimento a) {
        String sql = """
            UPDATE atendimento
               SET id_paciente = ?, id_profissional = ?, observacoes = ?, data_atendimento = ?
             WHERE id_atendimento = ?
            """;
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, a.getPaciente().getIdPaciente());
                ps.setInt(2, a.getProfissional().getIdProfissional());
                ps.setString(3, a.getObservacoes());
                ps.setTimestamp(4, Timestamp.valueOf(a.getDataAtendimento()));
                ps.setInt(5, a.getIdAtendimento());
                
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro AtendimentoDAO] Falha ao atualizar: " + e.getMessage());
            return false;
        }
    }

    /*
     DELETE — Remove permanentemente um atendimento do banco.
    
     @param idAtendimento Identificador único do atendimento
     @return true se removido com sucesso, false caso contrário
     */
    @Override
    public boolean excluir(int idAtendimento) {
        String sql = "DELETE FROM atendimento WHERE id_atendimento = ?";
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idAtendimento);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro AtendimentoDAO] Falha ao excluir: " + e.getMessage());
            return false;
        }
    }

    /*
     READ — Lista atendimentos com suporte a filtro por nome de paciente.
    
     Utiliza JOIN para trazer dados relacionados (Paciente e Profissional),
     evitando o problema N+1 ao carregar objetos relacionados.
    
     @param idClinica Filtro obrigatório de escopo (Clínica)
     @param busca Filtro opcional pelo nome do paciente (ILIKE para case-insensitive)
     @return Lista de atendimentos encontrados
     */
    @Override
    public List<Atendimento> listar(int idClinica, String busca) {
        List<Atendimento> lista = new ArrayList<>();
        
        String sql = (busca == null || busca.trim().isEmpty()) ? """
            SELECT a.id_atendimento, a.id_paciente, a.observacoes, a.data_atendimento, 
                   p.nome_paciente, pr.id_profissional, pr.nome AS nome_profissional, pr.especialidade
              FROM atendimento a
              JOIN paciente p ON p.id_paciente = a.id_paciente
              JOIN profissional pr ON pr.id_profissional = a.id_profissional
             WHERE p.id_clinica = ?
             ORDER BY a.data_atendimento DESC
            """ : """
            SELECT a.id_atendimento, a.id_paciente, a.observacoes, a.data_atendimento, 
                   p.nome_paciente, pr.id_profissional, pr.nome AS nome_profissional, pr.especialidade
              FROM atendimento a
              JOIN paciente p ON p.id_paciente = a.id_paciente
              JOIN profissional pr ON pr.id_profissional = a.id_profissional
             WHERE p.id_clinica = ? AND p.nome_paciente ILIKE ?
             ORDER BY a.data_atendimento DESC
            """;

        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return lista;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, idClinica);
                if (busca != null && !busca.trim().isEmpty()) {
                    ps.setString(2, "%" + busca + "%");
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Atendimento a = new Atendimento();
                        a.setIdAtendimento(rs.getInt("id_atendimento"));
                        a.setObservacoes(rs.getString("observacoes"));

                        // Reconstrói a árvore de objetos relacionados
                        Paciente p = new Paciente();
                        p.setIdPaciente(rs.getInt("id_paciente"));
                        p.setNomePaciente(rs.getString("nome_paciente"));
                        a.setPaciente(p);

                        Profissional prof = new Profissional();
                        prof.setIdProfissional(rs.getInt("id_profissional"));
                        prof.setNome(rs.getString("nome_profissional"));
                        prof.setEspecialidade(rs.getString("especialidade"));
                        a.setProfissional(prof);

                        Timestamp ts = rs.getTimestamp("data_atendimento");
                        if (ts != null) {
                            a.setDataAtendimento(ts.toLocalDateTime());
                        }
                        lista.add(a);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erro AtendimentoDAO] Falha ao listar atendimentos: " + e.getMessage());
        }
        return lista;
    }
}