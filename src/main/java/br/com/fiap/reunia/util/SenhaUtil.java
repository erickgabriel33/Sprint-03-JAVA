package br.com.fiap.reunia.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Gera e confere hash de senha (SHA-256 com salt aleatorio). A senha nunca e gravada em texto puro.
 * Formato armazenado: base64(salt):base64(hash)
 */
public final class SenhaUtil {

    private static final SecureRandom RANDOM = new SecureRandom();

    private SenhaUtil() {
    }

    public static String gerarHash(String senha) {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return gerarHash(senha, salt);
    }

    static String gerarHash(String senha, byte[] salt) {
        return codificar(salt) + ":" + codificar(sha256(salt, senha));
    }

    public static boolean confere(String senha, String hashArmazenado) {
        if (senha == null || hashArmazenado == null || !hashArmazenado.contains(":")) {
            return false;
        }
        String[] partes = hashArmazenado.split(":", 2);
        byte[] salt = Base64.getDecoder().decode(partes[0]);
        byte[] esperado = Base64.getDecoder().decode(partes[1]);
        return MessageDigest.isEqual(esperado, sha256(salt, senha));
    }

    private static byte[] sha256(byte[] salt, String senha) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt);
            return digest.digest(senha.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Algoritmo SHA-256 indisponível na JVM.", e);
        }
    }

    private static String codificar(byte[] bytes) {
        return Base64.getEncoder().encodeToString(bytes);
    }
}
