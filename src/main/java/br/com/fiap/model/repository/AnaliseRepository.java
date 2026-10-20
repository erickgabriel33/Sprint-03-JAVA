package br.com.fiap.model.repository;

import br.com.fiap.model.entity.Analise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnaliseRepository extends JpaRepository<Analise, Long> {
    Analise findByCodigoReuniao(Long codigoReuniao);

    List<Analise> findByRiscoChurnGreaterThanEqual(Integer riscoChurn);
}
