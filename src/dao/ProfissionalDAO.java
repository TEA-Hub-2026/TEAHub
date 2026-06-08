package br.com.teahub.dao;

import br.com.teahub.config.ConexaoBanco; 
import br.com.teahub.model.Profissional;
import br.com.teahub.model.Clinica;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/*
 - Classe de acesso a dados (DAO) responsável pelas operações de banco
  relacionadas à entidade Profissional.
 
  Implementa a interface DAOProfissional, garantindo o CRUD completo.
  Além das operações padrão, possui métodos específicos para autenticação
  e busca individual, necessários para o fluxo de login do sistema.
 
  Pilares de POO e padrões aplicados:
   - Padrão DAO: isola toda a lógica SQL nesta classe.
   - Interface + Polimorfismo: métodos sobrescritos com @Override.
   - DRY (Don't Repeat Yourself): o método auxiliar mapResultSetToProfissional()
     evita duplicação do código de mapeamento em buscarPorLogin e buscarPorId.
   - Coleções e Generics: ListProfissional com ArrayList.
   - Tratamento de Exceções: SQLException capturada em todos os métodos.
 */
public class ProfissionalDAO implements DAO<Profissional> {

    /*
     - CREATE — Insere um novo profissional no banco de dados.
     
      @param profissional Objeto Profissional com os dados preenchidos
      @return true se o cadastro foi bem-sucedido, false caso contrário
     */
    @Override
    public boolean cadastrar(Profissional profissional) {
        String sql = "INSERT INTO profissional (id_clinica, nome, email, usuario_login, senha_login, especialidade, registro_profissional) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, profissional.getClinica().getIdClinica()); // Chave estrangeira da Clínica
                stmt.setString(2, profissional.getNome());
                stmt.setString(3, profissional.getEmail());
                stmt.setString(4, profissional.getUsuarioLogin()); 
                stmt.setString(5, profissional.getSenhaLogin());
                stmt.setString(6, profissional.getEspecialidade());
                stmt.setString(7, profissional.getRegistroProfissional());

                stmt.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[Erro ProfissionalDAO] Falha ao cadastrar: " + e.getMessage());
            return false;
        }
    }

    /*
     - UPDATE — Atualiza todos os campos editáveis de um profissional.
     
      Inclui proteção contra NullPointerException: se o objeto Clinica
      do profissional for nulo, usa setNull para não quebrar a query.
     
      @param profissional Objeto com os dados atualizados
      @return true se ao menos uma linha foi afetada, false caso contrário
     */
    @Override
    public boolean atualizar(Profissional profissional) {
        String sql = "UPDATE profissional SET id_clinica = ?, nome = ?, email = ?, usuario_login = ?, senha_login = ?, especialidade = ?, registro_profissional = ?, status_ativo = ? WHERE id_profissional = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Proteção para evitar o NullPointerException caso a clínica não esteja carregada
                if (profissional.getClinica() != null) {
                    stmt.setInt(1, profissional.getClinica().getIdClinica());
                } else {
                    stmt.setNull(1, java.sql.Types.INTEGER);
                }
                
                stmt.setString(2, profissional.getNome());
                stmt.setString(3, profissional.getEmail());
                stmt.setString(4, profissional.getUsuarioLogin());
                stmt.setString(5, profissional.getSenhaLogin());
                stmt.setString(6, profissional.getEspecialidade());
                stmt.setString(7, profissional.getRegistroProfissional());
                stmt.setBoolean(8, profissional.isStatusAtivo());
                stmt.setInt(9, profissional.getIdProfissional()); // Critério do WHERE
                
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro ProfissionalDAO] Falha ao atualizar: " + e.getMessage());
            return false;
        }
    }

    /*
     - DELETE — Remove permanentemente um profissional do banco.
     
      @param idProfissional ID do profissional a ser excluído
      @return true se a exclusão foi bem-sucedida, false caso contrário
     */
    @Override
    public boolean excluir(int idProfissional) {
        String sql = "DELETE FROM profissional WHERE id_profissional = ?";
        
        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return false;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idProfissional);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[Erro ProfissionalDAO] Falha ao deletar: " + e.getMessage());
            return false;
        }
    }

    /*
     - Busca um profissional pelo usuário e senha para autenticação no login.
     
      A query filtra também por status_ativo = TRUE, impedindo que contas
      desativadas consigam acessar o sistema.
     
      @param usuario Login digitado na tela de autenticação
      @param senha   Senha digitada na tela de autenticação
      @return Objeto Profissional se as credenciais forem válidas, null caso contrário
     */
    public Profissional buscarPorLogin(String usuario, String senha) {
        String sql = "SELECT * FROM profissional WHERE usuario_login = ? AND senha_login = ? AND status_ativo = TRUE";
        try (Connection conn = ConexaoBanco.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return null;
            stmt.setString(1, usuario); 
            stmt.setString(2, senha);
            ResultSet rs = stmt.executeQuery();
            
            // Se encontrou o profissional, converte o ResultSet em objeto usando o método auxiliar
            if (rs.next()) {
                Profissional p = mapResultSetToProfissional(rs);
                return p;
            }
        } catch (SQLException e) { 
            System.err.println("[Erro] Login: " + e.getMessage()); 
        }
        return null; // Retorna null quando as credenciais não correspondem a nenhum registro
    }

    /*
     - Busca um profissional específico pelo seu ID.
       Utilizado pela ContaView para carregar os dados do profissional logado.
     
      @param idProfissional ID do profissional a ser buscado
      @return Objeto Profissional se encontrado, null caso contrário
     */
    public Profissional buscarPorId(int idProfissional) {
        String sql = "SELECT * FROM profissional WHERE id_profissional = ?";
        try (Connection conn = ConexaoBanco.conectar(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (conn == null) return null;
            stmt.setInt(1, idProfissional);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToProfissional(rs); // Reutiliza o método auxiliar (DRY)
            }
        } catch (SQLException e) { 
            System.err.println("[Erro] BuscarPorId: " + e.getMessage()); 
        }
        return null;
    }
    
     /*
     - Método auxiliar privado que converte um ResultSet em um objeto Profissional.
     
      Aplica o princípio DRY (Don't Repeat Yourself): centraliza o mapeamento
      de colunas do banco para atributos do objeto, evitando código duplicado
      entre buscarPorLogin() e buscarPorId().
     
      @param rs ResultSet posicionado em uma linha válida
      @return Objeto Profissional preenchido com os dados da linha atual
      @throws SQLException se ocorrer erro ao ler as colunas do ResultSet
     */
    private Profissional mapResultSetToProfissional(ResultSet rs) throws SQLException {
        Profissional p = new Profissional();
        p.setIdProfissional(rs.getInt("id_profissional"));
        p.setNome(rs.getString("nome"));
        p.setEmail(rs.getString("email"));
        p.setEspecialidade(rs.getString("especialidade"));
        p.setRegistroProfissional(rs.getString("registro_profissional"));
        p.setStatusAtivo(rs.getBoolean("status_ativo"));
        p.setUsuarioLogin(rs.getString("usuario_login"));
        p.setSenhaLogin(rs.getString("senha_login"));
        
        // Reconstrói o relacionamento com a Clínica para evitar NullPointerException nas Views
        Clinica c = new Clinica();
        c.setIdClinica(rs.getInt("id_clinica"));
        p.setClinica(c);
        
        return p;
    }
    
    /*
     - READ — Lista os profissionais de uma clínica, com suporte a filtro por nome.
     
      Retorna apenas id, nome e especialidade (campos suficientes para listagens),
      otimizando o tráfego de dados entre banco e aplicação.
     
      @param idClinica ID da clínica cujos profissionais serão listados
      @param busca     Filtro por nome; null ou vazio retorna todos
      @return Lista de profissionais encontrados
     */
    @Override
    public List<Profissional> listar(int idClinica, String busca) {
        List<Profissional> lista = new ArrayList<>();
        
        // Query dinâmica: com ou sem filtro de nome
        String sql = (busca == null || busca.trim().isEmpty())
                ? "SELECT id_profissional, nome, especialidade FROM profissional WHERE id_clinica = ? ORDER BY nome ASC"
                : "SELECT id_profissional, nome, especialidade FROM profissional WHERE id_clinica = ? AND LOWER(nome) LIKE ? ORDER BY nome ASC";

        try (Connection conn = ConexaoBanco.conectar()) {
            if (conn == null) return lista;
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, idClinica);
                
                // Adiciona o filtro apenas quando há busca
                if (busca != null && !busca.trim().isEmpty()) {
                    stmt.setString(2, "%" + busca.toLowerCase() + "%");
                }
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) { // Constrói objeto parcial com apenas os campos necessários para listagem
                        Profissional p = new Profissional();
                        p.setIdProfissional(rs.getInt("id_profissional")); 
                        p.setNome(rs.getString("nome"));
                        p.setEspecialidade(rs.getString("especialidade"));
                        lista.add(p);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[Erro ProfissionalDAO] Falha ao listar profissionais: " + e.getMessage());
        }
        return lista;
    }
}