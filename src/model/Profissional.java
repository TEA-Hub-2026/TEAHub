package br.com.teahub.model;

/*
 - Classe de modelo que representa um Profissional de saúde no sistema TEAHub.

  Segue o padrão MVC na camada Model: contém apenas os dados do profissional,
  sem lógica de persistência ou de interface.
 
  Pilares de POO aplicados:
   - Encapsulamento: atributos privados, acesso via getters e setters públicos.
   - Construtores: construtor vazio e construtor com parâmetros selecionados.
   - Modificadores de acesso: private nos dados, public nos métodos de acesso.
 */
public class Profissional {
    
    // Identificador único gerado pelo banco (SERIAL PRIMARY KEY)
    private int idProfissional;
    
    // Relacionamento com a Clinica à qual este profissional está vinculado
    private Clinica clinica;
    
    // Dados pessoais e profissionais
    private String nome;
    private String email;
    private String registroProfissional; // Ex: CRP-12345, CRM-67890
    private String especialidade;        // Ex: Psicologia, Fonoaudiologia

    // Indica se o cadastro do profissional está ativo no sistema
    private boolean statusAtivo;
    
    // Credenciais de acesso ao sistema
    private String usuarioLogin; // Login utilizado na tela de autenticação
    private String senhaLogin;   // Senha de acesso (armazenada sem criptografia neste MVP)

    /*
     - Construtor vazio (padrão JavaBeans).
       Utilizado pelo ProfissionalDAO ao reconstruir objetos a partir do banco de dados.
     */
    public Profissional() {
    }

    /*
     - Construtor parcial com os dados mínimos de identificação.
       Útil para listagens rápidas onde apenas id, nome e clínica são necessários,
       evitando carregar todos os campos desnecessariamente.
     
      @param idProfissional: Identificador único do profissional
      @param nome: Nome completo
      @param clinica: Clínica à qual está vinculado
     */
    public Profissional(int idProfissional, String nome, Clinica clinica) {
        this.idProfissional = idProfissional;
        this.nome = nome;
        this.clinica = clinica;
    }

    // ── Getters e Setters ──
    // Expõem os atributos de forma controlada, respeitando o encapsulamento.
 
    public int getIdProfissional() {
        return idProfissional;
    }

    public void setIdProfissional(int idProfissional) {
        this.idProfissional = idProfissional;
    }

    public Clinica getClinica() {
        return clinica;
    }

    public void setClinica(Clinica clinica) {
        this.clinica = clinica;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistroProfissional() {
        return registroProfissional;
    }

    public void setRegistroProfissional(String registroProfissional) {
        this.registroProfissional = registroProfissional;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }

    public String getUsuarioLogin() {
        return usuarioLogin;
    }

    public void setUsuarioLogin(String usuarioLogin) {
        this.usuarioLogin = usuarioLogin;
    }

    public String getSenhaLogin() {
        return senhaLogin;
    }

    public void setSenhaLogin(String senhaLogin) {
        this.senhaLogin = senhaLogin;
    }
}