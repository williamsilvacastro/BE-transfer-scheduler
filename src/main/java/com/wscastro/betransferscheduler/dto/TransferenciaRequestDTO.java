package com.wscastro.betransferscheduler.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransferenciaRequestDTO {

    @NotBlank(message = "Conta de origem nao pode ser vazia")
    @Pattern(regexp = "\\d{10}", message = "Conta de origem inválida")
    private String contaOrigem;

    @NotBlank(message = "Conta de destino nao pode ser vazia")
    @Pattern(regexp = "\\d{10}", message = "Conta de destino inválida")
    private String contaDestino;

    @NotNull(message = "Valor nao pode ser nulo")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @NotNull(message = "Data de transferência nao pode ser nula")
    private LocalDate dataTransferencia;

}
