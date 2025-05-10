package com.wscastro.betransferscheduler.repository;

import com.wscastro.betransferscheduler.model.Taxa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxaRepository extends JpaRepository<Taxa, Long> {
    Optional<Taxa> findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(Long diasMinimo, Long diasMaximo);
}