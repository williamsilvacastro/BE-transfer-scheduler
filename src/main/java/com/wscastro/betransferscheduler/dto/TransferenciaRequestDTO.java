package com.wscastro.betransferscheduler.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransferenciaRequestDTO {

    @Pattern(regexp = "\\d{10}", message = "Conta de origem inválida")
    private String contaOrigem;

    @Pattern(regexp = "\\d{10}", message = "Conta de destino inválida")
    private String contaDestino;

    @NotNull
    @DecimalMin("0.01")
    private BigDecimal valor;

    @NotNull
    private LocalDate dataTransferencia;

}

