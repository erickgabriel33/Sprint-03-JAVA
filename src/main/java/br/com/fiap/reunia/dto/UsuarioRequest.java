package br.com.fiap.reunia.dto;

/**
 * Dados de cadastro/atualizacao de usuario. Na atualizacao, a senha e opcional (mantem a atual se vier vazia).
 */
public record UsuarioRequest(String nome, String email, String senha, String cargo, String empresa) {
}
