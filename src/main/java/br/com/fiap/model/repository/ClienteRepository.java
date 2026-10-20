package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Cliente findByCodigo(Long codigo);

    Cliente findByCnpj(String cnpj);
}
