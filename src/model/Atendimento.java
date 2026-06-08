package br.com.teahub.model;

import java.time.LocalDateTime;

/*
  - Classe de modelo que representa um Atendimento no sistema TEAHub.

  Segue o padrão MVC na camada Model: armazena apenas os dados da entidade,
  sem nenhuma lógica de banco de dados ou de interface gráfica.
 
   Aplica os pilares de POO:
   - Encapsulamento: atributos privados acessados por métodos públicos.
   - Composição: relaciona as entidades Profissional e Paciente dentro do atendimento.
   - Construtores: suporte a instâncias vazias e preenchidas.
*/
public class Atendimento {
    
    // Identificador único do atendimento (PRIMARY KEY no banco de dados)
    private int idAtendimento;
    
    // Profissional responsável pelo atendimento (Relacionamento N:1)
    private Profissional profissional; 
    
    // Paciente que recebeu o atendimento (Relacionamento N:1)
    private Paciente paciente;         
    
    // Data e hora exata em que o atendimento ocorreu
    private LocalDateTime dataAtendimento;
    
    // Relatos clínicos, observações ou notas sobre a evolução do paciente
    private String observacoes;

    /*
     - Construtor vazio (padrão JavaBeans).
       Utilizado para instanciar objetos que serão preenchidos via setters,
       comum na recuperação de dados do banco pelo DAO.
     */
    public Atendimento() {
    }

    /*
     - Construtor com campos essenciais.
       Utilizado para criação de novos registros ou manipulação em memória.
    
     @param idAtendimento:   ID único do registro
     @param profissional:    Objeto Profissional vinculado
     @param paciente:        Objeto Paciente vinculado
     */
    public Atendimento(int idAtendimento, Profissional profissional, Paciente paciente) {
        this.idAtendimento = idAtendimento;
        this.profissional = profissional;
        this.paciente = paciente;
    }

    /* ── Getters e Setters ──
     Expõe os atributos da classe mantendo a integridade e o encapsulamento.
    */

    public int getIdAtendimento() {
        return idAtendimento;
    }

    public void setIdAtendimento(int idAtendimento) {
        this.idAtendimento = idAtendimento;
    }

    public Profissional getProfissional() {
        return profissional;
    }

    public void setProfissional(Profissional profissional) {
        this.profissional = profissional;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public LocalDateTime getDataAtendimento() {
        return dataAtendimento;
    }

    public void setDataAtendimento(LocalDateTime dataAtendimento) {
        this.dataAtendimento = dataAtendimento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }
}