package br.com.teahub.dao;

import br.com.teahub.config.ConexaoBanco;
import br.com.teahub.model.Clinica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/*
 - Classe de acesso a dados (DAO) responsável por todas as operações
   de banco de dados relacionadas à entidade Clínica.

 Pilares de POO e padrões aplicados:
  - Padrão DAO: isola a lógica de persistência da entidade.
  - Identidade gerada: utiliza Statement.RETURN_GENERATED_KEYS para capturar IDs do banco.
  - Tratamento de Exceções: logs de erro centralizados e retorno de status de operação.
 */
public class ClinicaDAO implements DAO<Clinica> {

    /*
     CREATE — Insere uma nova clínica no banco de dados.

     Utiliza a recuperação de chaves geradas automaticamente para atualizar
     o objeto modelo com o ID atribuído pelo banco.

     @param clinica Objeto Clinica com os dados para persistência
     @return true se o cadastro foi realizado com sucesso, false caso contrário
     */
    @Override
    public boolean cadastrar(Clinica clinica) {
        String sql = "INSERT INTO clinica (nome_clinica, cnpj, usuario_admin, senha_admin, telefone) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) {
                System.err.println("[Erro ClinicaDAO] Conexão retornou null.");
                return false;
            }
            
            // Utiliza Statement.RETURN_GENERATED_KEYS para recuperar o ID criado pelo banco
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, clinica.getNomeClinica());
                stmt.setString(2, clinica.getCnpj());
                stmt.setString(3, clinica.getUsuarioAdmin());
                stmt.setString(4, clinica.getSenhaAdmin());
                stmt.setString(5, clinica.getTelefone());
                
                int linhasAfetadas = stmt.executeUpdate();
                if (linhasAfetadas > 0) {
                    // Obtém e atribui o ID gerado automaticamente ao objeto modelo
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            clinica.setIdClinica(rs.getInt(1));
                        }
                    }
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erro ClinicaDAO] Falha ao cadastrar: " + e.getMessage());
        }
        return false;
    }

    /*
     UPDATE — Atualiza as informações de uma clínica existente.

     @param clinica Objeto Clinica com os dados atualizados
     @return true se a atualização foi bem-sucedida, false caso contrário
     */
    @Override
    public boolean atualizar(Clinica clinica) {
        String sql = "UPDATE clinica SET nome_clinica = ?, cnpj = ?, usuario_admin = ?, senha_admin = ?, telefone = ? WHERE id_clinica = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, clinica.getNomeClinica());
                stmt.setString(2, clinica.getCnpj());
                stmt.setString(3, clinica.getUsuarioAdmin());
                stmt.setString(4, clinica.getSenhaAdmin());
                stmt.setString(5, clinica.getTelefone());
                stmt.setInt(6, clinica.getIdClinica());
                
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro ClinicaDAO] Falha ao atualizar: " + e.getMessage());
            return false;
        }
    }

    /*
     DELETE — Remove permanentemente um registro de clínica do banco.

     @param idClinica Identificador único da clínica
     @return true se a exclusão foi bem-sucedida, false caso contrário
     */
    @Override
    public boolean excluir(int idClinica) {
        String sql = "DELETE FROM clinica WHERE id_clinica = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idClinica);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro ClinicaDAO] Falha ao deletar: " + e.getMessage());
            return false;
        }
    }

    /*
     READ — Lista clínicas cadastradas com suporte a busca textual.

     Aplica filtro de busca insensível a maiúsculas/minúsculas caso o parâmetro
     de busca não esteja vazio.

     @param idContexto (Não utilizado, mantido para conformidade com a interface)
     @param busca Termo para filtragem pelo nome da clínica
     @return Lista de clínicas encontradas
     */
    @Override
    public List<Clinica> listar(int idContexto, String busca) {
        List<Clinica> lista = new ArrayList<>();
        String sql = (busca == null || busca.trim().isEmpty()) 
                     ? "SELECT * FROM clinica ORDER BY nome_clinica"
                     : "SELECT * FROM clinica WHERE LOWER(nome_clinica) LIKE ? ORDER BY nome_clinica";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return lista;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                if (busca != null && !busca.trim().isEmpty()) {
                    stmt.setString(1, "%" + busca.toLowerCase() + "%");
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Clinica c = new Clinica();
                        c.setIdClinica(rs.getInt("id_clinica"));
                        c.setNomeClinica(rs.getString("nome_clinica"));
                        c.setCnpj(rs.getString("cnpj"));
                        c.setUsuarioAdmin(rs.getString("usuario_admin"));
                        c.setSenhaAdmin(rs.getString("senha_admin"));
                        c.setTelefone(rs.getString("telefone"));
                        lista.add(c);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erro ClinicaDAO] Falha ao listar clínicas: " + e.getMessage());
        }
        return lista;
    }

    /*
     READ (Específico) — Busca uma clínica pelo seu ID único.

     @param idClinica ID da clínica a ser buscada
     @return Objeto Clinica se encontrado, null caso contrário
     */
    public Clinica buscarPorId(int idClinica) {
        String sql = "SELECT * FROM clinica WHERE id_clinica = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return null;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idClinica);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Clinica c = new Clinica();
                        c.setIdClinica(rs.getInt("id_clinica"));
                        c.setNomeClinica(rs.getString("nome_clinica"));
                        c.setCnpj(rs.getString("cnpj"));
                        c.setUsuarioAdmin(rs.getString("usuario_admin"));
                        c.setSenhaAdmin(rs.getString("senha_admin"));
                        c.setTelefone(rs.getString("telefone"));
                        return c;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erro ClinicaDAO] Falha ao buscar por ID: " + e.getMessage());
        }
        return null;
    }
}