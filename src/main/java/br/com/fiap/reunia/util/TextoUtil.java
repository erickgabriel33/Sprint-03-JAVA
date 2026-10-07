package br.com.fiap.reunia.util;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Normalizacao de texto usada pelo motor de analise de transcricoes.
 */
public final class TextoUtil {

    private TextoUtil() {
    }

    /** Minusculas e sem acentos: "Insatisfação" vira "insatisfacao". */
    public static String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return semAcento.toLowerCase(Locale.ROOT);
    }

    public static List<String> tokenizar(String texto) {
        return Arrays.stream(normalizar(texto).split("[^a-z0-9]+"))
                .filter(t -> !t.isBlank())
                .toList();
    }

    public static int contarPalavras(String texto) {
        return texto == null || texto.isBlank() ? 0 : texto.trim().split("\\s+").length;
    }
}
