package com.wscastro.betransferscheduler.service;

import com.wscastro.betransferscheduler.exception.DataEntradaInvalidaException;
import com.wscastro.betransferscheduler.exception.TaxaInvalidaException;
import com.wscastro.betransferscheduler.model.Taxa;
import com.wscastro.betransferscheduler.repository.TaxaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TaxaServiceTest {

    @Mock
    private TaxaRepository taxaRepository;

    @InjectMocks
    private TaxaService taxaService;

    private Taxa taxaMesmoDia;
    private Taxa taxaAte10Dias;
    private Taxa taxa11a20Dias;
    private Taxa taxa21a30Dias;
    private Taxa taxa31a40Dias;
    private Taxa taxa41a50Dias;

    @BeforeEach
    void setUp() {
        // Set the diasLimite field for testing
        taxaService.setDiasLimite(50);

        // Setup taxa rules based on data.sql
        taxaMesmoDia = new Taxa();
        taxaMesmoDia.setDiasMinimo(0L);
        taxaMesmoDia.setDiasMaximo(0L);
        taxaMesmoDia.setPercentual(new BigDecimal("0.025"));
        taxaMesmoDia.setValorFixo(new BigDecimal("3.00"));

        taxaAte10Dias = new Taxa();
        taxaAte10Dias.setDiasMinimo(1L);
        taxaAte10Dias.setDiasMaximo(10L);
        taxaAte10Dias.setPercentual(null);
        taxaAte10Dias.setValorFixo(new BigDecimal("12.00"));

        taxa11a20Dias = new Taxa();
        taxa11a20Dias.setDiasMinimo(11L);
        taxa11a20Dias.setDiasMaximo(20L);
        taxa11a20Dias.setPercentual(new BigDecimal("0.082"));
        taxa11a20Dias.setValorFixo(null);

        taxa21a30Dias = new Taxa();
        taxa21a30Dias.setDiasMinimo(21L);
        taxa21a30Dias.setDiasMaximo(30L);
        taxa21a30Dias.setPercentual(new BigDecimal("0.069"));
        taxa21a30Dias.setValorFixo(null);

        taxa31a40Dias = new Taxa();
        taxa31a40Dias.setDiasMinimo(31L);
        taxa31a40Dias.setDiasMaximo(40L);
        taxa31a40Dias.setPercentual(new BigDecimal("0.047"));
        taxa31a40Dias.setValorFixo(null);

        taxa41a50Dias = new Taxa();
        taxa41a50Dias.setDiasMinimo(41L);
        taxa41a50Dias.setDiasMaximo(50L);
        taxa41a50Dias.setPercentual(new BigDecimal("0.017"));
        taxa41a50Dias.setValorFixo(null);
    }

    @Test
    void calcularTaxa_MesmoDia_DeveRetornarTaxaCorreta() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(0L, 0L))
                .thenReturn(List.of(taxaMesmoDia));

        // Act
        BigDecimal taxa = taxaService.calcularTaxa(valor, hoje, hoje);

        // Assert
        // 100 * 0.025 + 3.00 = 2.50 + 3.00 = 5.50
        assertEquals(0, new BigDecimal("5.50").compareTo(taxa.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void calcularTaxa_Entre1e10Dias_DeveRetornarTaxaFixa() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(5);
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(5L, 5L))
                .thenReturn(List.of(taxaAte10Dias));

        // Act
        BigDecimal taxa = taxaService.calcularTaxa(valor, hoje, dataTransferencia);

        // Assert
        // Taxa fixa de 12.00
        assertEquals(0, new BigDecimal("12.00").compareTo(taxa.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void calcularTaxa_Entre11e20Dias_DeveRetornarTaxaPercentual() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(15);
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(15L, 15L))
                .thenReturn(List.of(taxa11a20Dias));

        // Act
        BigDecimal taxa = taxaService.calcularTaxa(valor, hoje, dataTransferencia);

        // Assert
        // 100 * 0.082 = 8.20
        assertEquals(0, new BigDecimal("8.20").compareTo(taxa.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void calcularTaxa_Entre21e30Dias_DeveRetornarTaxaPercentual() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(25);
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(25L, 25L))
                .thenReturn(List.of(taxa21a30Dias));

        // Act
        BigDecimal taxa = taxaService.calcularTaxa(valor, hoje, dataTransferencia);

        // Assert
        // 100 * 0.069 = 6.90
        assertEquals(0, new BigDecimal("6.90").compareTo(taxa.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void calcularTaxa_Entre31e40Dias_DeveRetornarTaxaPercentual() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(35);
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(35L, 35L))
                .thenReturn(List.of(taxa31a40Dias));

        // Act
        BigDecimal taxa = taxaService.calcularTaxa(valor, hoje, dataTransferencia);

        // Assert
        // 100 * 0.047 = 4.70
        assertEquals(0, new BigDecimal("4.70").compareTo(taxa.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void calcularTaxa_Entre41e50Dias_DeveRetornarTaxaPercentual() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(45);
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(45L, 45L))
                .thenReturn(List.of(taxa41a50Dias));

        // Act
        BigDecimal taxa = taxaService.calcularTaxa(valor, hoje, dataTransferencia);

        // Assert
        // 100 * 0.017 = 1.70
        assertEquals(0, new BigDecimal("1.70").compareTo(taxa.setScale(2, BigDecimal.ROUND_HALF_UP)));
    }

    @Test
    void calcularTaxa_DataTransferenciaMenorQueDataAgendamento_DeveLancarExcecao() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.minusDays(1);
        BigDecimal valor = new BigDecimal("100.00");

        // Act & Assert
        assertThrows(DataEntradaInvalidaException.class, () -> {
            taxaService.calcularTaxa(valor, hoje, dataTransferencia);
        });
    }

    @Test
    void calcularTaxa_DiferencaMaiorQue50Dias_DeveLancarExcecao() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(51);
        BigDecimal valor = new BigDecimal("100.00");

        // Act & Assert
        assertThrows(DataEntradaInvalidaException.class, () -> {
            taxaService.calcularTaxa(valor, hoje, dataTransferencia);
        });
    }

    @Test
    void calcularTaxa_TaxaNaoEncontrada_DeveLancarExcecao() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        LocalDate dataTransferencia = hoje.plusDays(5);
        BigDecimal valor = new BigDecimal("100.00");
        when(taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(anyLong(), anyLong()))
                .thenReturn(List.of());

        // Act & Assert
        assertThrows(TaxaInvalidaException.class, () -> {
            taxaService.calcularTaxa(valor, hoje, dataTransferencia);
        });
    }
}
