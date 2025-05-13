package com.wscastro.betransferscheduler.dto;

import com.wscastro.betransferscheduler.model.Transferencia;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransferenciaResponseDTO {

    private Long id;
    private String contaOrigem;
    private String contaDestino;
    private BigDecimal valor;
    private BigDecimal taxa;
    private LocalDateTime dataAgendamento;
    private LocalDateTime dataTransferencia;

    public TransferenciaResponseDTO() {}

    public TransferenciaResponseDTO(Transferencia t) {
        this.id = t.getId();
        this.contaOrigem = t.getContaOrigem();
        this.contaDestino = t.getContaDestino();
        this.valor = t.getValor();
        this.taxa = t.getTaxa();
        this.dataAgendamento = t.getDataAgendamento();
        this.dataTransferencia = t.getDataTransferencia();
    }
}
