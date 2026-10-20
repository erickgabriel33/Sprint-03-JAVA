package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Usuario findByCodigo(Long codigo);

    Usuario findByEmail(String email);

    Usuario findByEmailAndSenha(String email, String senha);
}
