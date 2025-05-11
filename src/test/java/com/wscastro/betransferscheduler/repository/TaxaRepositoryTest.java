package com.wscastro.betransferscheduler.repository;

import com.wscastro.betransferscheduler.model.Taxa;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({"/schema.sql", "/data.sql"})
class TaxaRepositoryTest {

    @Autowired
    private TaxaRepository taxaRepository;

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_MesmoDia_DeveRetornarTaxaCorreta() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(0L, 0L);

        // Assert
        assertFalse(taxas.isEmpty());
        Taxa taxa = taxas.get(0);
        assertEquals(0L, taxa.getDiasMinimo());
        assertEquals(0L, taxa.getDiasMaximo());
        assertEquals(new BigDecimal("0.03"),taxa.getPercentual());
        assertEquals(new BigDecimal("3.00"),taxa.getValorFixo());
    }

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_Entre1e10Dias_DeveRetornarTaxaCorreta() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(5L, 5L);

        // Assert
        assertFalse(taxas.isEmpty());
        Taxa taxa = taxas.get(0);
        assertEquals(1L, taxa.getDiasMinimo());
        assertEquals(10L, taxa.getDiasMaximo());
        assertEquals(new BigDecimal("0.00"), taxa.getPercentual());
        assertEquals(0, new BigDecimal("12.00").compareTo(taxa.getValorFixo()));
    }

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_Entre11e20Dias_DeveRetornarTaxaCorreta() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(15L, 15L);

        // Assert
        assertFalse(taxas.isEmpty());
        Taxa taxa = taxas.get(0);
        assertEquals(11L, taxa.getDiasMinimo());
        assertEquals(20L, taxa.getDiasMaximo());
        assertEquals(new BigDecimal("0.08"),taxa.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa.getValorFixo());
    }

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_Entre21e30Dias_DeveRetornarTaxaCorreta() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(25L, 25L);

        // Assert
        assertFalse(taxas.isEmpty());
        Taxa taxa = taxas.get(0);
        assertEquals(21L, taxa.getDiasMinimo());
        assertEquals(30L, taxa.getDiasMaximo());
        assertEquals(new BigDecimal("0.07"),taxa.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa.getValorFixo());
    }

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_Entre31e40Dias_DeveRetornarTaxaCorreta() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(35L, 35L);

        // Assert
        assertFalse(taxas.isEmpty());
        Taxa taxa = taxas.get(0);
        assertEquals(31L, taxa.getDiasMinimo());
        assertEquals(40L, taxa.getDiasMaximo());
        assertEquals(new BigDecimal("0.05"),taxa.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa.getValorFixo());
    }

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_Entre41e50Dias_DeveRetornarTaxaCorreta() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(45L, 45L);

        // Assert
        assertFalse(taxas.isEmpty());
        Taxa taxa = taxas.get(0);
        assertEquals(41L, taxa.getDiasMinimo());
        assertEquals(50L, taxa.getDiasMaximo());
        assertEquals(new BigDecimal("0.02"),taxa.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa.getValorFixo());
    }

    @Test
    void findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual_DiasForaDoIntervalo_DeveRetornarVazio() {
        // Act
        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(51L, 51L);

        // Assert
        assertTrue(taxas.isEmpty());
    }
}
