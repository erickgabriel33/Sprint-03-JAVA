package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Reuniao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ReuniaoRepository extends JpaRepository<Reuniao, Long> {
    Reuniao findByCodigo(Long codigo);

    List<Reuniao> findByCodigoCliente(Long codigoCliente);

    List<Reuniao> findByCodigoUsuario(Long codigoUsuario);

    List<Reuniao> findByTipo(String tipo);

    List<Reuniao> findByStatus(String status);

    Reuniao findByCodigoClienteAndDataHora(Long codigoCliente, LocalDateTime dataHora);
}
