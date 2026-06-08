package br.com.teahub.util;

import br.com.teahub.model.Profissional;

/*
 - Classe utilitária (Singleton-like) responsável por gerenciar a sessão do usuário.
 
 Pilares de POO e padrões aplicados:
  - Singleton (estado global): armazena o profissional logado de forma estática.
  - Encapsulamento: o acesso ao profissional logado é mediado por métodos set/get.
  - Controle de Estado: oferece métodos para persistir, recuperar e encerrar a sessão.
 */
public class Sessao {
    
    // Profissional que está atualmente autenticado no sistema
    private static Profissional profissionalLogado;

    /*
     - Define o profissional autenticado na sessão global.
     @param p Objeto Profissional que realizou o login com sucesso
     */
    public static void setLogado(Profissional p) {
        profissionalLogado = p;
    }

    /*
     - Recupera o objeto do profissional atualmente logado.
     @return Instância de Profissional, ou null se não houver usuário logado
     */
    public static Profissional getLogado() {
        return profissionalLogado;
    }

    /*
     - Método de conveniência para obter apenas o ID do profissional logado.
     @return ID do profissional, ou -1 caso a sessão esteja vazia
     */
    public static int getIdLogado() {
        if (profissionalLogado == null) return -1;
        return profissionalLogado.getIdProfissional();
    }

    /*
     - Encerra a sessão, limpando a referência do profissional logado da memória.
     */
    public static void encerrar() {
        profissionalLogado = null;
    }
}