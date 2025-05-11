package com.wscastro.betransferscheduler.service;

import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.dto.TransferenciaResponseDTO;
import com.wscastro.betransferscheduler.model.Transferencia;
import com.wscastro.betransferscheduler.repository.TransferenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransferenciaServiceTest {

    @Mock
    private TransferenciaRepository transferenciaRepository;

    @Mock
    private TaxaService taxaService;

    @InjectMocks
    private TransferenciaService transferenciaService;

    @Captor
    private ArgumentCaptor<Transferencia> transferenciaCaptor;

    private TransferenciaRequestDTO requestDTO;
    private Transferencia transferencia;
    private LocalDate hoje;

    @BeforeEach
    void setUp() {
        hoje = LocalDate.now();

        // Setup request DTO
        requestDTO = new TransferenciaRequestDTO();
        requestDTO.setContaOrigem("1234567890");
        requestDTO.setContaDestino("0987654321");
        requestDTO.setValor(new BigDecimal("100.00"));
        requestDTO.setDataTransferencia(hoje.plusDays(5));

        // Setup transferencia entity
        transferencia = new Transferencia();
        transferencia.setId(1L);
        transferencia.setContaOrigem("1234567890");
        transferencia.setContaDestino("0987654321");
        transferencia.setValor(new BigDecimal("100.00"));
        transferencia.setTaxa(new BigDecimal("12.00"));
        transferencia.setDataAgendamento(hoje);
        transferencia.setDataTransferencia(hoje.plusDays(5));
    }

    @Test
    void agendarTransferencia_DeveAgendarCorretamente() {
        // Arrange
        when(taxaService.calcularTaxa(any(), any(), any())).thenReturn(new BigDecimal("12.00"));
        when(transferenciaRepository.save(any(Transferencia.class))).thenReturn(transferencia);

        // Act
        TransferenciaResponseDTO responseDTO = transferenciaService.agendarTransferencia(requestDTO);

        // Assert
        verify(taxaService).calcularTaxa(
                eq(new BigDecimal("100.00")), 
                any(LocalDate.class), 
                eq(hoje.plusDays(5))
        );

        verify(transferenciaRepository).save(transferenciaCaptor.capture());
        Transferencia savedTransferencia = transferenciaCaptor.getValue();

        assertEquals("1234567890", savedTransferencia.getContaOrigem());
        assertEquals("0987654321", savedTransferencia.getContaDestino());
        assertEquals(new BigDecimal("100.00"), savedTransferencia.getValor());
        assertEquals(new BigDecimal("12.00"), savedTransferencia.getTaxa());
        assertEquals(hoje.plusDays(5), savedTransferencia.getDataTransferencia());

        assertNotNull(responseDTO);
        assertEquals("1234567890", responseDTO.getContaOrigem());
        assertEquals("0987654321", responseDTO.getContaDestino());
        assertEquals(new BigDecimal("100.00"), responseDTO.getValor());
        assertEquals(new BigDecimal("12.00"), responseDTO.getTaxa());
        assertEquals(hoje, responseDTO.getDataAgendamento());
        assertEquals(hoje.plusDays(5), responseDTO.getDataTransferencia());
    }

    @Test
    void listarTodas_DeveRetornarListaDeTransferencias() {
        // Arrange
        Transferencia transferencia2 = new Transferencia();
        transferencia2.setId(2L);
        transferencia2.setContaOrigem("1111111111");
        transferencia2.setContaDestino("2222222222");
        transferencia2.setValor(new BigDecimal("200.00"));
        transferencia2.setTaxa(new BigDecimal("16.40"));
        transferencia2.setDataAgendamento(hoje);
        transferencia2.setDataTransferencia(hoje.plusDays(15));

        List<Transferencia> transferencias = Arrays.asList(transferencia, transferencia2);
        when(transferenciaRepository.findAll()).thenReturn(transferencias);

        // Act
        List<TransferenciaResponseDTO> result = transferenciaService.listarTodas();

        // Assert
        assertEquals(2, result.size());

        assertEquals(1L, result.get(0).getId());
        assertEquals("1234567890", result.get(0).getContaOrigem());
        assertEquals("0987654321", result.get(0).getContaDestino());
        assertEquals(new BigDecimal("100.00"), result.get(0).getValor());
        assertEquals(new BigDecimal("12.00"), result.get(0).getTaxa());

        assertEquals(2L, result.get(1).getId());
        assertEquals("1111111111", result.get(1).getContaOrigem());
        assertEquals("2222222222", result.get(1).getContaDestino());
        assertEquals(new BigDecimal("200.00"), result.get(1).getValor());
        assertEquals(new BigDecimal("16.40"), result.get(1).getTaxa());
    }

    @Test
    void listarTodasPaginado_DeveRetornarPaginaDeTransferencias() {
        // Arrange
        Transferencia transferencia2 = new Transferencia();
        transferencia2.setId(2L);
        transferencia2.setContaOrigem("1111111111");
        transferencia2.setContaDestino("2222222222");
        transferencia2.setValor(new BigDecimal("200.00"));
        transferencia2.setTaxa(new BigDecimal("16.40"));
        transferencia2.setDataAgendamento(hoje);
        transferencia2.setDataTransferencia(hoje.plusDays(15));

        List<Transferencia> transferencias = Arrays.asList(transferencia, transferencia2);
        Page<Transferencia> page = new PageImpl<>(transferencias);

        Pageable pageable = PageRequest.of(0, 10);
        when(transferenciaRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<TransferenciaResponseDTO> result = transferenciaService.listarTodas(pageable);

        // Assert
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());

        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("1234567890", result.getContent().get(0).getContaOrigem());
        assertEquals("0987654321", result.getContent().get(0).getContaDestino());
        assertEquals(new BigDecimal("100.00"), result.getContent().get(0).getValor());
        assertEquals(new BigDecimal("12.00"), result.getContent().get(0).getTaxa());

        assertEquals(2L, result.getContent().get(1).getId());
        assertEquals("1111111111", result.getContent().get(1).getContaOrigem());
        assertEquals("2222222222", result.getContent().get(1).getContaDestino());
        assertEquals(new BigDecimal("200.00"), result.getContent().get(1).getValor());
        assertEquals(new BigDecimal("16.40"), result.getContent().get(1).getTaxa());
    }
}
