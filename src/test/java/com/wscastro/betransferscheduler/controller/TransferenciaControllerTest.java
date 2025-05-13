package com.wscastro.betransferscheduler.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.dto.TransferenciaResponseDTO;
import com.wscastro.betransferscheduler.exception.DataEntradaInvalidaException;
import com.wscastro.betransferscheduler.exception.GlobalExceptionHandler;
import com.wscastro.betransferscheduler.service.TransferenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TransferenciaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransferenciaService transferenciaService;

    @InjectMocks
    private TransferenciaController transferenciaController;

    private ObjectMapper objectMapper;
    private TransferenciaRequestDTO requestDTO;
    private TransferenciaResponseDTO responseDTO;
    private LocalDateTime hoje;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // For LocalDateTime serialization

        mockMvc = MockMvcBuilders.standaloneSetup(transferenciaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();

        hoje = LocalDateTime.now();

        // Setup request DTO
        requestDTO = new TransferenciaRequestDTO();
        requestDTO.setContaOrigem("1234567890");
        requestDTO.setContaDestino("0987654321");
        requestDTO.setValor(new BigDecimal("100.00"));
        requestDTO.setDataTransferencia(hoje.plusDays(5));

        // Setup response DTO
        responseDTO = new TransferenciaResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setContaOrigem("1234567890");
        responseDTO.setContaDestino("0987654321");
        responseDTO.setValor(new BigDecimal("100.00"));
        responseDTO.setTaxa(new BigDecimal("12.00"));
        responseDTO.setDataAgendamento(hoje);
        responseDTO.setDataTransferencia(hoje.plusDays(5));
    }

    @Test
    void agendar_DeveRetornarTransferenciaAgendada() throws Exception {
        when(transferenciaService.agendarTransferencia(any(TransferenciaRequestDTO.class)))
                .thenReturn(responseDTO);

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.contaOrigem", is("1234567890")))
                .andExpect(jsonPath("$.contaDestino", is("0987654321")))
                .andExpect(jsonPath("$.valor", is(100.00)))
                .andExpect(jsonPath("$.taxa", is(12.00)));
    }

    @Test
    void agendar_ComDadosInvalidos_DeveRetornarBadRequest() throws Exception {
        // Invalid request with missing required fields
        TransferenciaRequestDTO invalidRequest = new TransferenciaRequestDTO();
        invalidRequest.setContaOrigem("123"); // Invalid format
        invalidRequest.setContaDestino("456"); // Invalid format
        // Missing valor and dataTransferencia

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void agendar_ComDataInvalida_DeveRetornarBadRequest() throws Exception {
        when(transferenciaService.agendarTransferencia(any(TransferenciaRequestDTO.class)))
                .thenThrow(new DataEntradaInvalidaException("Data inválida"));

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void listarTodos_DeveRetornarPaginaDeTransferencias() throws Exception {
        TransferenciaResponseDTO responseDTO2 = new TransferenciaResponseDTO();
        responseDTO2.setId(2L);
        responseDTO2.setContaOrigem("1111111111");
        responseDTO2.setContaDestino("2222222222");
        responseDTO2.setValor(new BigDecimal("200.00"));
        responseDTO2.setTaxa(new BigDecimal("16.40"));
        responseDTO2.setDataAgendamento(hoje);
        responseDTO2.setDataTransferencia(hoje.plusDays(15));

        List<TransferenciaResponseDTO> transferencias = Arrays.asList(responseDTO, responseDTO2);
        Page<TransferenciaResponseDTO> page = new PageImpl<>(transferencias);

        when(transferenciaService.listarTodas(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/agendamentos")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].contaOrigem", is("1234567890")))
                .andExpect(jsonPath("$.content[0].valor", is(100.00)))
                .andExpect(jsonPath("$.content[1].id", is(2)))
                .andExpect(jsonPath("$.content[1].contaOrigem", is("1111111111")))
                .andExpect(jsonPath("$.content[1].valor", is(200.00)));
    }

    @Test
    void deletar_DeveRetornarNoContent() throws Exception {
        Long id = 1L;

        doNothing().when(transferenciaService).deletarTransferencia(id);

        mockMvc.perform(delete("/agendamentos/{id}", id))
                .andExpect(status().isNoContent());

        verify(transferenciaService, times(1)).deletarTransferencia(id);
    }

    @Test
    void deletar_ComIdInexistente_DeveRetornarNotFound() throws Exception {
        Long id = 999L;

        doThrow(new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.NOT_FOUND, "Transferência não encontrada"))
                .when(transferenciaService).deletarTransferencia(id);

        mockMvc.perform(delete("/agendamentos/{id}", id))
                .andExpect(status().isNotFound());

        verify(transferenciaService, times(1)).deletarTransferencia(id);
    }
}
