package com.wscastro.betransferscheduler.service;

import com.wscastro.betransferscheduler.exception.DataEntradaInvalidaException;
import com.wscastro.betransferscheduler.exception.TaxaInvalidaException;
import com.wscastro.betransferscheduler.model.Taxa;
import com.wscastro.betransferscheduler.repository.TaxaRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class TaxaService {
    private final TaxaRepository taxaRepository;

    @Value("${app.transferencia.dias-limite}")
    private long diasLimite;

    // Setter for testing purposes
    public void setDiasLimite(long diasLimite) {
        this.diasLimite = diasLimite;
    }

    public TaxaService(TaxaRepository taxaRepository) {
        this.taxaRepository = taxaRepository;
    }

    public BigDecimal calcularTaxa(BigDecimal valor, LocalDate dataAgendamento, LocalDate dataTransferencia) {
        long dias = ChronoUnit.DAYS.between(dataAgendamento, dataTransferencia);
        validaDistanciaEntreDatas(dias);

        List<Taxa> taxas = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(dias, dias);
        if (taxas.isEmpty()) {
            throw new TaxaInvalidaException("Nao existe taxa aplicável para a data informada.");
        }
        Taxa taxa = taxas.get(0);

        BigDecimal taxaPercentual = taxa.getPercentual() != null ? valor.multiply(taxa.getPercentual()) : BigDecimal.ZERO;
        BigDecimal taxaFixa = taxa.getValorFixo() != null ? taxa.getValorFixo() : BigDecimal.ZERO;

        return taxaPercentual.add(taxaFixa);
    }

    private void validaDistanciaEntreDatas(long dias) {
        if (dias < 0) {
            throw new DataEntradaInvalidaException("A data de transferência nao pode ser anterior à data de agendamento.");
        }
        if (dias > diasLimite) {
            throw new DataEntradaInvalidaException("A distância entre as datas nao pode ser superior a " + diasLimite + " dias.");
        }
    }

}
