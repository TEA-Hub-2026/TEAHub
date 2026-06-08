package br.com.teahub.model;

import java.time.LocalDate;

/*
  - Classe de modelo que representa um Paciente no sistema TEAHub.

  Segue o padrão MVC na camada Model: armazena apenas os dados do paciente,
  sem nenhuma lógica de banco de dados ou de interface gráfica.
 
   Aplica os pilares de POO:
   - Encapsulamento: todos os atributos são privados (private), acessados
     apenas por métodos públicos (getters e setters).
   - Construtores: possui construtor vazio (para criação em branco) e
     construtor com parâmetros (para criação já preenchida).
   - Modificadores de acesso: private nos atributos, public nos métodos.
 */
public class Paciente {
    // Identificador único gerado automaticamente pelo banco (SERIAL PRIMARY KEY)
    private int idPaciente;
    
    // Relacionamento com a entidade Clinica (chave estrangeira no banco)
    private Clinica clinica;
    
    // Dados pessoais do paciente
    private String nomePaciente;
    private LocalDate dataNascimento;
    
    // Dados do responsável legal pelo paciente
    private String responsavel;
    private String telefoneResponsavel;
    
    // Nível de suporte TEA: 1 (Leve), 2 (Substancial) ou 3 (Muito severo)
    private int nivelTea;
    
    // Indica se o cadastro do paciente está ativo no sistema
    private boolean statusAtivo;
    
     // Informações clínicas adicionais (campos opcionais)
    private String medicacoesEmUso;
    private String restricoesAlimentares;
    
    // Data em que o paciente foi cadastrado no sistema (preenchida automaticamente pelo banco)
    private LocalDate dataCadastro;

    /*
     - Construtor vazio (padrão JavaBeans).
       Utilizado pelo PacienteDAO ao mapear os dados vindos do banco (ResultSet)
     */
    public Paciente() {
    }

    /*
     - Construtor com os campos principais.
       Utilizado quando se deseja criar um objeto já parcialmente preenchido,
       sem depender de setters individuais para cada campo.
     
      @param clinica:              Clínica à qual o paciente pertence
      @param nomePaciente:         Nome completo do paciente
      @param dataNascimento:       Data de nascimento
      @param responsavel:          Nome do responsável legal
      @param telefoneResponsavel:  Telefone de contato do responsável
      @param nivelTea:             Nível de suporte TEA (1, 2 ou 3)
      @param statusAtivo:          true se o cadastro está ativo, false se inativo
     */
    public Paciente(Clinica clinica, String nomePaciente, LocalDate dataNascimento, String responsavel, String telefoneResponsavel, int nivelTea, boolean statusAtivo) {
        this.clinica = clinica;
        this.nomePaciente = nomePaciente;
        this.dataNascimento = dataNascimento;
        this.responsavel = responsavel;
        this.telefoneResponsavel = telefoneResponsavel;
        this.nivelTea = nivelTea;
        this.statusAtivo = statusAtivo;
    }
    
    
    /* ── Getters e Setters ──
      Cada par get/set expõe um atributo de forma controlada,
      aplicando o princípio de encapsulamento da POO.
    */ 
    public int getIdPaciente() {
        return idPaciente;
    }

    public void setIdPaciente(int idPaciente) {
        this.idPaciente = idPaciente;
    }

    public Clinica getClinica() {
        return clinica;
    }

    public void setClinica(Clinica clinica) {
        this.clinica = clinica;
    }

    public String getNomePaciente() {
        return nomePaciente;
    }

    public void setNomePaciente(String nomePaciente) {
        this.nomePaciente = nomePaciente;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getTelefoneResponsavel() {
        return telefoneResponsavel;
    }

    public void setTelefoneResponsavel(String telefoneResponsavel) {
        this.telefoneResponsavel = telefoneResponsavel;
    }

    public int getNivelTea() {
        return nivelTea;
    }

    public void setNivelTea(int nivelTea) {
        this.nivelTea = nivelTea;
    }

    public boolean isStatusAtivo() {
        return statusAtivo;
    }

    public void setStatusAtivo(boolean statusAtivo) {
        this.statusAtivo = statusAtivo;
    }
    
    
    public String getMedicacoesEmUso() {
        return medicacoesEmUso;
    }

    public void setMedicacoesEmUso(String medicacoesEmUso) {
        this.medicacoesEmUso = medicacoesEmUso;
    }

    public String getRestricoesAlimentares() {
        return restricoesAlimentares;
    }

    public void setRestricoesAlimentares(String restricoesAlimentares) {
        this.restricoesAlimentares = restricoesAlimentares;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
