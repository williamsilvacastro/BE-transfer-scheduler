package com.wscastro.betransferscheduler.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.model.Transferencia;
import com.wscastro.betransferscheduler.repository.TransferenciaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql({"/schema.sql", "/data.sql"})
class TransferenciaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TransferenciaRepository transferenciaRepository;

    @BeforeEach
    void setUp() {
        transferenciaRepository.deleteAll();
    }

    @Test
    void agendarTransferencia_DeveAgendarERetornarTransferencia() throws Exception {
        // Arrange
        LocalDateTime hoje = LocalDateTime.now();
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setContaOrigem("1234567890");
        requestDTO.setContaDestino("0987654321");
        requestDTO.setValor(new BigDecimal("100.00"));
        requestDTO.setDataTransferencia(hoje.plusDays(5));

        // Act & Assert
        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contaOrigem", is("1234567890")))
                .andExpect(jsonPath("$.contaDestino", is("0987654321")))
                .andExpect(jsonPath("$.valor", is(100.00)))
                .andExpect(jsonPath("$.taxa", is(12.00))) // Taxa para 5 dias é fixa de 12.00
                .andReturn();

        // Verify database
        List<Transferencia> transferencias = transferenciaRepository.findAll();
        assertEquals(1, transferencias.size());
        Transferencia transferencia = transferencias.get(0);
        assertEquals("1234567890", transferencia.getContaOrigem());
        assertEquals("0987654321", transferencia.getContaDestino());
        assertEquals(new BigDecimal("100.00"), transferencia.getValor());
        assertEquals(new BigDecimal("12.00"), transferencia.getTaxa());
        assertDateTimeEquals(hoje, transferencia.getDataAgendamento());
        assertDateTimeEquals(hoje.plusDays(5), transferencia.getDataTransferencia());
    }

    @Test
    void agendarTransferencia_ComDataInvalida_DeveRetornarBadRequest() throws Exception {
        // Arrange
        LocalDateTime hoje = LocalDateTime.now();
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setContaOrigem("1234567890");
        requestDTO.setContaDestino("0987654321");
        requestDTO.setValor(new BigDecimal("100.00"));
        requestDTO.setDataTransferencia(hoje.minusDays(1)); // Data no passado

        // Act & Assert
        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        // Verify database
        List<Transferencia> transferencias = transferenciaRepository.findAll();
        assertEquals(0, transferencias.size());
    }

    @Test
    void agendarTransferencia_ComDadosInvalidos_DeveRetornarBadRequest() throws Exception {
        // Arrange
        TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
        requestDTO.setContaOrigem("123"); // Formato inválido
        requestDTO.setContaDestino("456"); // Formato inválido
        requestDTO.setValor(new BigDecimal("100.00"));
        requestDTO.setDataTransferencia(LocalDateTime.now().plusDays(5));

        // Act & Assert
        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest());

        // Verify database
        List<Transferencia> transferencias = transferenciaRepository.findAll();
        assertEquals(0, transferencias.size());
    }

    @Test
    void listarTransferencias_DeveRetornarListaVazia_QuandoNaoHaTransferencias() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));
    }

    @Test
    void listarTransferencias_DeveRetornarTransferencias_QuandoExistemTransferencias() throws Exception {
        // Arrange
        LocalDateTime hoje = LocalDateTime.now();

        // Create first transferencia
        TransferenciaRequestDTO requestDTO1 = new TransferenciaRequestDTO();
        requestDTO1.setContaOrigem("1234567890");
        requestDTO1.setContaDestino("0987654321");
        requestDTO1.setValor(new BigDecimal("100.00"));
        requestDTO1.setDataTransferencia(hoje.plusDays(5));

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO1)))
                .andExpect(status().isOk());

        // Create second transferencia
        TransferenciaRequestDTO requestDTO2 = new TransferenciaRequestDTO();
        requestDTO2.setContaOrigem("1111111111");
        requestDTO2.setContaDestino("2222222222");
        requestDTO2.setValor(new BigDecimal("200.00"));
        requestDTO2.setDataTransferencia(hoje.plusDays(15));

        mockMvc.perform(post("/agendamentos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO2)))
                .andExpect(status().isOk());

        // Act & Assert
        mockMvc.perform(get("/agendamentos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(2)))
                .andExpect(jsonPath("$.content[0].contaOrigem", is("1234567890")))
                .andExpect(jsonPath("$.content[0].valor", is(100.00)))
                .andExpect(jsonPath("$.content[0].taxa", is(12.00)))
                .andExpect(jsonPath("$.content[1].contaOrigem", is("1111111111")))
                .andExpect(jsonPath("$.content[1].valor", is(200.00)))
                .andExpect(jsonPath("$.content[1].taxa", is(16.0)));
    }

    private void assertDateTimeEquals(LocalDateTime expected, LocalDateTime actual) {
        assertEquals(expected.getYear(), actual.getYear());
        assertEquals(expected.getMonth(), actual.getMonth());
        assertEquals(expected.getDayOfMonth(), actual.getDayOfMonth());
        assertEquals(expected.getHour(), actual.getHour());
        assertEquals(expected.getMinute(), actual.getMinute());
        assertEquals(expected.getSecond(), actual.getSecond());
    }

    @Test
    void listarTransferencias_ComPaginacao_DeveRetornarPaginaCorreta() throws Exception {
        // Arrange
        LocalDateTime hoje = LocalDateTime.now();

        // Create 5 transferencias
        for (int i = 0; i < 5; i++) {
            TransferenciaRequestDTO requestDTO = new TransferenciaRequestDTO();
            requestDTO.setContaOrigem("1000000000");
            requestDTO.setContaDestino("2000000000");
            requestDTO.setValor(new BigDecimal("100.00"));
            requestDTO.setDataTransferencia(hoje.plusDays(5)); // Taxa fixa de 12.00

            mockMvc.perform(post("/agendamentos")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestDTO)))
                    .andExpect(status().isOk());
        }

        // Act & Assert - First page with 2 items
        mockMvc.perform(get("/agendamentos")
                .param("page", "0")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(0)));

        // Act & Assert - Second page with 2 items
        mockMvc.perform(get("/agendamentos")
                .param("page", "1")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(1)));

        // Act & Assert - Third page with 2 items (should have only 1 item)
        mockMvc.perform(get("/agendamentos")
                .param("page", "2")
                .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.totalElements", is(5)))
                .andExpect(jsonPath("$.totalPages", is(3)))
                .andExpect(jsonPath("$.number", is(2)));
    }
}
