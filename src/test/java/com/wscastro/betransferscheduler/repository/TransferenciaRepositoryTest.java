package com.wscastro.betransferscheduler.repository;

import com.wscastro.betransferscheduler.model.Transferencia;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Sql({"/schema.sql"})
class TransferenciaRepositoryTest {

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @Test
    void save_DeveSalvarTransferencia() {
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

        // Assert
        assertNotNull(savedTransferencia.getId());
        assertEquals("1234567890", savedTransferencia.getContaOrigem());
        assertEquals("0987654321", savedTransferencia.getContaDestino());
        assertEquals(new BigDecimal("100.00"), savedTransferencia.getValor());
        assertEquals(new BigDecimal("12.00"), savedTransferencia.getTaxa());
        assertEquals(hoje, savedTransferencia.getDataAgendamento());
        assertEquals(hoje.plusDays(5), savedTransferencia.getDataTransferencia());
    }

    @Test
    void findById_DeveRetornarTransferencia() {
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

        // Act
        Optional<Transferencia> foundTransferencia = transferenciaRepository.findById(savedTransferencia.getId());

        // Assert
        assertTrue(foundTransferencia.isPresent());
        assertEquals(savedTransferencia.getId(), foundTransferencia.get().getId());
        assertEquals("1234567890", foundTransferencia.get().getContaOrigem());
        assertEquals("0987654321", foundTransferencia.get().getContaDestino());
        assertEquals(new BigDecimal("100.00"), foundTransferencia.get().getValor());
        assertEquals(new BigDecimal("12.00"), foundTransferencia.get().getTaxa());
        assertEquals(hoje, foundTransferencia.get().getDataAgendamento());
        assertEquals(hoje.plusDays(5), foundTransferencia.get().getDataTransferencia());
    }

    @Test
    void findAll_DeveRetornarTodasTransferencias() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        
        Transferencia transferencia1 = new Transferencia();
        transferencia1.setContaOrigem("1234567890");
        transferencia1.setContaDestino("0987654321");
        transferencia1.setValor(new BigDecimal("100.00"));
        transferencia1.setTaxa(new BigDecimal("12.00"));
        transferencia1.setDataAgendamento(hoje);
        transferencia1.setDataTransferencia(hoje.plusDays(5));
        transferenciaRepository.save(transferencia1);
        
        Transferencia transferencia2 = new Transferencia();
        transferencia2.setContaOrigem("1111111111");
        transferencia2.setContaDestino("2222222222");
        transferencia2.setValor(new BigDecimal("200.00"));
        transferencia2.setTaxa(new BigDecimal("16.40"));
        transferencia2.setDataAgendamento(hoje);
        transferencia2.setDataTransferencia(hoje.plusDays(15));
        transferenciaRepository.save(transferencia2);

        // Act
        List<Transferencia> transferencias = transferenciaRepository.findAll();

        // Assert
        assertEquals(2, transferencias.size());
        
        // Verify first transferencia
        Transferencia found1 = transferencias.stream()
                .filter(t -> t.getContaOrigem().equals("1234567890"))
                .findFirst()
                .orElse(null);
        assertNotNull(found1);
        assertEquals("0987654321", found1.getContaDestino());
        assertEquals(new BigDecimal("100.00"), found1.getValor());
        
        // Verify second transferencia
        Transferencia found2 = transferencias.stream()
                .filter(t -> t.getContaOrigem().equals("1111111111"))
                .findFirst()
                .orElse(null);
        assertNotNull(found2);
        assertEquals("2222222222", found2.getContaDestino());
        assertEquals(new BigDecimal("200.00"), found2.getValor());
    }

    @Test
    void findAll_ComPaginacao_DeveRetornarPaginaDeTransferencias() {
        // Arrange
        LocalDate hoje = LocalDate.now();
        
        // Create 5 transferencias
        for (int i = 0; i < 5; i++) {
            Transferencia transferencia = new Transferencia();
            transferencia.setContaOrigem("1000000000");
            transferencia.setContaDestino("2000000000");
            transferencia.setValor(new BigDecimal("100.00").add(new BigDecimal(i * 10)));
            transferencia.setTaxa(new BigDecimal("10.00"));
            transferencia.setDataAgendamento(hoje);
            transferencia.setDataTransferencia(hoje.plusDays(i + 1));
            transferenciaRepository.save(transferencia);
        }

        // Act - get first page with 2 items
        Pageable pageable = PageRequest.of(0, 2);
        Page<Transferencia> page = transferenciaRepository.findAll(pageable);

        // Assert
        assertEquals(5, page.getTotalElements());
        assertEquals(3, page.getTotalPages());
        assertEquals(2, page.getContent().size());
        
        // Act - get second page with 2 items
        pageable = PageRequest.of(1, 2);
        page = transferenciaRepository.findAll(pageable);
        
        // Assert
        assertEquals(2, page.getContent().size());
        
        // Act - get third page with 2 items (should have only 1 item)
        pageable = PageRequest.of(2, 2);
        page = transferenciaRepository.findAll(pageable);
        
        // Assert
        assertEquals(1, page.getContent().size());
    }

    @Test
    void delete_DeveRemoverTransferencia() {
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
        
        // Act
        transferenciaRepository.delete(savedTransferencia);
        Optional<Transferencia> deletedTransferencia = transferenciaRepository.findById(savedTransferencia.getId());
        
        // Assert
        assertFalse(deletedTransferencia.isPresent());
    }
}