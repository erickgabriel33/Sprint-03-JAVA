package br.com.fiap.reunia.model;

public enum Sentimento {
    POSITIVO,
    NEUTRO,
    NEGATIVO;

    /** Converte um score de -100 a 100 na classificacao de sentimento. */
    public static Sentimento porScore(int score) {
        if (score >= 25) {
            return POSITIVO;
        }
        if (score <= -25) {
            return NEGATIVO;
        }
        return NEUTRO;
    }
}
