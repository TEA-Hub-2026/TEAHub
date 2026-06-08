package br.com.teahub.model;

/*
 - Classe de modelo que representa uma Clínica no sistema TEAHub.

 Segue o padrão MVC na camada Model: armazena os dados cadastrais e de 
 acesso da instituição, sem lógica de persistência ou de interface.
 
  Aplica os pilares de POO:
  - Encapsulamento: atributos privados com acesso controlado por métodos públicos.
  - Construtores: suporte a instâncias vazias e instâncias com campos obrigatórios.
*/
public class Clinica {
    
    // Identificador único gerado automaticamente pelo banco (PRIMARY KEY)
    private int idClinica;
    
    // Nome fantasia da instituição
    private String nomeClinica;
    
    // Cadastro Nacional da Pessoa Jurídica (documento de identificação)
    private String cnpj;
    
    // Usuário de credencial para o acesso do administrador da clínica
    private String usuarioAdmin;
    
    // Senha de autenticação para o usuário administrador
    private String senhaAdmin;
    
    // Telefone de contato oficial da instituição
    private String telefone;

    /*
     - Construtor vazio (padrão JavaBeans).
       Essencial para mapeamentos de ORM ou instanciamento dinâmico pelo DAO.
     */
    public Clinica() {
    }
    
    /*
     - Construtor com campos obrigatórios para identificação.
    
     @param idClinica:      ID único da clínica
     @param nomeClinica:    Nome da instituição
     @param usuarioAdmin:   Login administrativo associado
     */
    public Clinica(int idClinica, String nomeClinica, String usuarioAdmin) {
        this.idClinica = idClinica;
        this.nomeClinica = nomeClinica;
        this.usuarioAdmin = usuarioAdmin;
    }
    
    /* ── Getters e Setters ──
     Expõe os atributos da entidade garantindo o encapsulamento dos dados.
    */

    public int getIdClinica() { 
        return idClinica; 
    }
    public void setIdClinica(int idClinica) { 
        this.idClinica = idClinica; 
    }

    public String getNomeClinica() { 
        return nomeClinica; 
    }
    public void setNomeClinica(String nomeClinica) { 
        this.nomeClinica = nomeClinica; 
    }

    public String getCnpj() { 
        return cnpj; 
    }
    public void setCnpj(String cnpj) { 
        this.cnpj = cnpj; 
    }

    public String getUsuarioAdmin() { 
        return usuarioAdmin; 
    }
    public void setUsuarioAdmin(String usuarioAdmin) { 
        this.usuarioAdmin = usuarioAdmin; 
    }

    public String getSenhaAdmin() { 
        return senhaAdmin; 
    }
    public void setSenhaAdmin(String senhaAdmin) { 
        this.senhaAdmin = senhaAdmin; 
    }

    public String getTelefone() { 
        return telefone; 
    }
    public void setTelefone(String telefone) { 
        this.telefone = telefone; 
    }
}