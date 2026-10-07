package br.com.fiap.reunia.model;

/**
 * Contrato de tudo que pode ser analisado pela IA da Reun.IA.
 */
public interface Analisavel {

    String analisarConteudo();

    boolean analiseDisponivel();
}
