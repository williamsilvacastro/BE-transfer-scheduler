package com.wscastro.betransferscheduler.integration;

import com.wscastro.betransferscheduler.model.Taxa;
import com.wscastro.betransferscheduler.model.Transferencia;
import com.wscastro.betransferscheduler.repository.TaxaRepository;
import com.wscastro.betransferscheduler.repository.TransferenciaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql({"/schema.sql", "/data.sql"})
@Transactional
class DatabaseIntegrationTest {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Autowired
    private TaxaRepository taxaRepository;

    @Test
    void testDatabaseIntegration_SalvarERecuperarTransferencia() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        Transferencia transferencia = new Transferencia();
        transferencia.setContaOrigem("1234567890");
        transferencia.setContaDestino("0987654321");
        transferencia.setValor(new BigDecimal("100.00"));
        transferencia.setTaxa(new BigDecimal("12.00"));
        transferencia.setDataAgendamento(hoje);
        transferencia.setDataTransferencia(hoje.plusDays(5));

        // Act
        Transferencia savedTransferencia = transferenciaRepository.save(transferencia);
        Optional<Transferencia> retrievedTransferencia = transferenciaRepository.findById(savedTransferencia.getId());

        // Assert
        assertTrue(retrievedTransferencia.isPresent());
        assertEquals(savedTransferencia.getId(), retrievedTransferencia.get().getId());
        assertEquals("1234567890", retrievedTransferencia.get().getContaOrigem());
        assertEquals("0987654321", retrievedTransferencia.get().getContaDestino());
        assertEquals(new BigDecimal("100.00"), retrievedTransferencia.get().getValor());
        assertEquals(new BigDecimal("12.00"), retrievedTransferencia.get().getTaxa());
        assertEquals(hoje, retrievedTransferencia.get().getDataAgendamento());
        assertEquals(hoje.plusDays(5), retrievedTransferencia.get().getDataTransferencia());
    }

    @Test
    void testRecuperarTaxaMesmoDia() {
        // Act
        List<Taxa> taxasMesmoDia = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(0L, 0L);

        // Assert
        assertFalse(taxasMesmoDia.isEmpty());
        Taxa taxaMesmoDia = taxasMesmoDia.get(0);
        assertEquals(0L, taxaMesmoDia.getDiasMinimo());
        assertEquals(0L, taxaMesmoDia.getDiasMaximo());
        assertNotEquals(new BigDecimal("0.025"), taxaMesmoDia.getPercentual());
        assertEquals(new BigDecimal("3.00"), taxaMesmoDia.getValorFixo());
    }

    @Test
    void testRecuperarTaxa5Dias() {
        // Act
        List<Taxa> taxas5Dias = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(5L, 5L);

        // Assert
        assertFalse(taxas5Dias.isEmpty());
        Taxa taxa5Dias = taxas5Dias.get(0);
        assertEquals(1L, taxa5Dias.getDiasMinimo());
        assertEquals(10L, taxa5Dias.getDiasMaximo());
        assertEquals(new BigDecimal("0.00"), taxa5Dias.getPercentual());
        assertEquals(0, new BigDecimal("12.00").compareTo(taxa5Dias.getValorFixo()));
    }

    @Test
    void testRecuperarTaxa15Dias() {
        // Act
        List<Taxa> taxas15Dias = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(15L, 15L);

        // Assert
        assertFalse(taxas15Dias.isEmpty());
        Taxa taxa15Dias = taxas15Dias.get(0);
        assertEquals(11L, taxa15Dias.getDiasMinimo());
        assertEquals(20L, taxa15Dias.getDiasMaximo());
        assertEquals(new BigDecimal("0.08"), taxa15Dias.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa15Dias.getValorFixo());
    }

    @Test
    void testRecuperarTaxa25Dias() {
        // Act
        List<Taxa> taxas25Dias = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(25L, 25L);

        // Assert
        assertFalse(taxas25Dias.isEmpty());
        Taxa taxa25Dias = taxas25Dias.get(0);
        assertEquals(21L, taxa25Dias.getDiasMinimo());
        assertEquals(30L, taxa25Dias.getDiasMaximo());
        assertEquals(new BigDecimal("0.07"), taxa25Dias.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa25Dias.getValorFixo());
    }

    @Test
    void testRecuperarTaxa35Dias() {
        // Act
        List<Taxa> taxas35Dias = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(35L, 35L);

        // Assert
        assertFalse(taxas35Dias.isEmpty());
        Taxa taxa35Dias = taxas35Dias.get(0);
        assertEquals(31L, taxa35Dias.getDiasMinimo());
        assertEquals(40L, taxa35Dias.getDiasMaximo());
        assertEquals(new BigDecimal("0.05"), taxa35Dias.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa35Dias.getValorFixo());
    }

    @Test
    void testRecuperarTaxa45Dias() {
        // Act
        List<Taxa> taxas45Dias = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(45L, 45L);

        // Assert
        assertFalse(taxas45Dias.isEmpty());
        Taxa taxa45Dias = taxas45Dias.get(0);
        assertEquals(41L, taxa45Dias.getDiasMinimo());
        assertEquals(50L, taxa45Dias.getDiasMaximo());
        assertEquals(new BigDecimal("0.02"), taxa45Dias.getPercentual());
        assertEquals(new BigDecimal("0.00"), taxa45Dias.getValorFixo());
    }

    @Test
    void testRecuperarTaxa51Dias() {
        // Act
        List<Taxa> taxas51Dias = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(51L, 51L);

        // Assert
        assertTrue(taxas51Dias.isEmpty());
    }

    @Test
    void testDatabaseIntegration_PaginacaoDeTransferencias() {
        // Arrange
        LocalDate hoje = LocalDate.now();

        // Create 10 transferencias
        for (int i = 0; i < 10; i++) {
            Transferencia transferencia = new Transferencia();
            transferencia.setContaOrigem("1000000000");
            transferencia.setContaDestino("2000000000");
            transferencia.setValor(new BigDecimal("100.00").add(new BigDecimal(i * 10)));
            transferencia.setTaxa(new BigDecimal("12.00"));
            transferencia.setDataAgendamento(hoje);
            transferencia.setDataTransferencia(hoje.plusDays(5));
            transferenciaRepository.save(transferencia);
        }

        // Act - get first page with 3 items
        Page<Transferencia> page1 = transferenciaRepository.findAll(PageRequest.of(0, 3));

        // Assert
        assertEquals(10, page1.getTotalElements());
        assertEquals(4, page1.getTotalPages());
        assertEquals(3, page1.getContent().size());

        // Act - get second page with 3 items
        Page<Transferencia> page2 = transferenciaRepository.findAll(PageRequest.of(1, 3));

        // Assert
        assertEquals(3, page2.getContent().size());

        // Act - get last page with 3 items (should have only 1 item)
        Page<Transferencia> page4 = transferenciaRepository.findAll(PageRequest.of(3, 3));

        // Assert
        assertEquals(1, page4.getContent().size());
    }

    @Test
    void testDatabaseIntegration_AtualizarTransferencia() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        Transferencia transferencia = new Transferencia();
        transferencia.setContaOrigem("1234567890");
        transferencia.setContaDestino("0987654321");
        transferencia.setValor(new BigDecimal("100.00"));
        transferencia.setTaxa(new BigDecimal("12.00"));
        transferencia.setDataAgendamento(hoje);
        transferencia.setDataTransferencia(hoje.plusDays(5));
        Transferencia savedTransferencia = transferenciaRepository.save(transferencia);

        // Act - update
        savedTransferencia.setValor(new BigDecimal("200.00"));
        savedTransferencia.setTaxa(new BigDecimal("24.00"));
        transferenciaRepository.save(savedTransferencia);

        // Retrieve updated entity
        Optional<Transferencia> updatedTransferencia = transferenciaRepository.findById(savedTransferencia.getId());

        // Assert
        assertTrue(updatedTransferencia.isPresent());
        assertEquals(new BigDecimal("200.00"), updatedTransferencia.get().getValor());
        assertEquals(new BigDecimal("24.00"), updatedTransferencia.get().getTaxa());
    }

    @Test
    void testDatabaseIntegration_DeletarTransferencia() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        Transferencia transferencia = new Transferencia();
        transferencia.setContaOrigem("1234567890");
        transferencia.setContaDestino("0987654321");
        transferencia.setValor(new BigDecimal("100.00"));
        transferencia.setTaxa(new BigDecimal("12.00"));
        transferencia.setDataAgendamento(hoje);
        transferencia.setDataTransferencia(hoje.plusDays(5));
        Transferencia savedTransferencia = transferenciaRepository.save(transferencia);

        // Verify it was saved
        assertTrue(transferenciaRepository.findById(savedTransferencia.getId()).isPresent());

        // Act - delete
        transferenciaRepository.delete(savedTransferencia);

        // Assert
        assertFalse(transferenciaRepository.findById(savedTransferencia.getId()).isPresent());
    }

    @Test
    void testDatabaseIntegration_ContarTransferencias() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        long initialCount = transferenciaRepository.count();

        // Create 5 transferencias
        for (int i = 0; i < 5; i++) {
            Transferencia transferencia = new Transferencia();
            transferencia.setContaOrigem("1000000000");
            transferencia.setContaDestino("2000000000");
            transferencia.setValor(new BigDecimal("100.00"));
            transferencia.setTaxa(new BigDecimal("12.00"));
            transferencia.setDataAgendamento(hoje);
            transferencia.setDataTransferencia(hoje.plusDays(5));
            transferenciaRepository.save(transferencia);
        }

        // Act
        long finalCount = transferenciaRepository.count();

        // Assert
        assertEquals(initialCount + 5, finalCount);
    }
}
