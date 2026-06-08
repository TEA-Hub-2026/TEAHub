package br.com.teahub.dao;

import java.util.List;

/*
 - Interface genérica (DAO - Data Access Object) que define o contrato padrão 
   para as operações de persistência de dados das entidades do sistema.
 
   Implementa o padrão Strategy/Interface para garantir que todas as classes 
   de acesso a dados possuam um comportamento previsível e uniforme.
 
 @param <T> O tipo de entidade (Model) que este DAO irá manipular.
 */
public interface DAO<T> {

    /*
     CREATE — Contrato para inserção de um novo registro.
     
     @param entidade Objeto contendo os dados a serem salvos.
     @return true se a inserção foi bem-sucedida, false caso contrário.
     */
    boolean cadastrar(T entidade);

    /*
     UPDATE — Contrato para atualização de um registro existente.
     
     @param objeto Objeto contendo as novas informações para atualização.
     @return true se a atualização foi bem-sucedida, false caso contrário.
     */
    boolean atualizar(T objeto);

    /*
     DELETE — Contrato para remoção de um registro.
     
     @param id O identificador único da entidade a ser removida.
     @return true se a exclusão foi bem-sucedida, false caso contrário.
     */
    boolean excluir(int id);

    /*
     READ — Contrato para consulta e listagem de registros.
     
     Permite a implementação de consultas filtradas por contexto (ex: Clínica) 
     e busca textual, garantindo flexibilidade na recuperação de dados.
     
     @param idContexto ID de referência (ex: id da clínica) para limitar o escopo da busca.
     @param busca Termo de pesquisa opcional para filtragem de resultados.
     @return Uma lista contendo os objetos recuperados; retorna lista vazia caso não haja registros.
     */
    List<T> listar(int idContexto, String busca);
}