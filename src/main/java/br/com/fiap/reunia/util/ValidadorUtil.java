package br.com.fiap.reunia.util;

import br.com.fiap.reunia.exception.NegocioException;

import java.util.regex.Pattern;

/**
 * Validacoes reutilizadas pelos Services. Cada metodo lanca {@link NegocioException} com mensagem clara.
 */
public final class ValidadorUtil {

    private static final Pattern EMAIL = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");

    private ValidadorUtil() {
    }

    public static String textoObrigatorio(String valor, String campo, int minimo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new NegocioException("O campo '" + campo + "' é obrigatório.");
        }
        String texto = valor.trim();
        if (texto.length() < minimo || texto.length() > maximo) {
            throw new NegocioException("O campo '" + campo + "' deve ter entre " + minimo + " e " + maximo + " caracteres.");
        }
        return texto;
    }

    public static String textoOpcional(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return textoObrigatorio(valor, campo, 1, maximo);
    }

    public static String email(String valor, String campo, boolean obrigatorio) {
        if (!obrigatorio && (valor == null || valor.isBlank())) {
            return null;
        }
        String email = textoObrigatorio(valor, campo, 6, 120).toLowerCase();
        if (!EMAIL.matcher(email).matches()) {
            throw new NegocioException("O campo '" + campo + "' não é um e-mail válido: " + valor);
        }
        return email;
    }

    public static void senhaForte(String senha) {
        if (senha == null || senha.length() < 8 || !senha.matches(".*[A-Za-z].*") || !senha.matches(".*\\d.*")) {
            throw new NegocioException("A senha deve ter no mínimo 8 caracteres, com letras e números.");
        }
    }

    public static <T> T obrigatorio(T valor, String campo) {
        if (valor == null) {
            throw new NegocioException("O campo '" + campo + "' é obrigatório.");
        }
        return valor;
    }

    public static int intervalo(Integer valor, String campo, int minimo, int maximo) {
        obrigatorio(valor, campo);
        if (valor < minimo || valor > maximo) {
            throw new NegocioException("O campo '" + campo + "' deve estar entre " + minimo + " e " + maximo + ".");
        }
        return valor;
    }

    /** Remove a mascara e valida os digitos verificadores do CNPJ. Retorna somente os 14 numeros. */
    public static String cnpj(String valor) {
        String cnpj = valor == null ? "" : valor.replaceAll("\\D", "");
        if (cnpj.length() != 14 || cnpj.chars().distinct().count() == 1) {
            throw new NegocioException("CNPJ inválido: informe 14 dígitos.");
        }
        int[] pesos1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int[] pesos2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        if (digitoCnpj(cnpj, pesos1) != cnpj.charAt(12) - '0' || digitoCnpj(cnpj, pesos2) != cnpj.charAt(13) - '0') {
            throw new NegocioException("CNPJ inválido: dígitos verificadores não conferem.");
        }
        return cnpj;
    }

    private static int digitoCnpj(String cnpj, int[] pesos) {
        int soma = 0;
        for (int i = 0; i < pesos.length; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }
}
