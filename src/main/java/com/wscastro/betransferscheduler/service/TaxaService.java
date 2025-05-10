package com.wscastro.betransferscheduler.service;

import com.wscastro.betransferscheduler.exception.DataEntradaInvalidaException;
import com.wscastro.betransferscheduler.exception.TaxaInvalidaException;
import com.wscastro.betransferscheduler.model.Taxa;
import com.wscastro.betransferscheduler.repository.TaxaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class TaxaService {
    private final TaxaRepository taxaRepository;

    public TaxaService(TaxaRepository taxaRepository) {
        this.taxaRepository = taxaRepository;
    }

    public BigDecimal calcularTaxa(BigDecimal valor, LocalDate dataAgendamento, LocalDate dataTransferencia) {
        long dias = ChronoUnit.DAYS.between(dataAgendamento, dataTransferencia);
        validaDistanciaEntreDatas(dias);

        Taxa taxa = taxaRepository.findByDiasMinimoLessThanEqualAndDiasMaximoGreaterThanEqual(dias, dias)
                .orElseThrow(() -> new TaxaInvalidaException("Não existe taxa aplicável para a data informada."));

        BigDecimal taxaPercentual = taxa.getPercentual() != null ? valor.multiply(taxa.getPercentual()) : BigDecimal.ZERO;
        BigDecimal taxaFixa = taxa.getValorFixo() != null ? taxa.getValorFixo() : BigDecimal.ZERO;

        return taxaPercentual.add(taxaFixa);
    }

    private static void validaDistanciaEntreDatas(long dias) {
        if (dias < 0) {
            throw new DataEntradaInvalidaException("A data de transferência não pode ser anterior à data de agendamento.");
        }
        if (dias > 50) {
            throw new DataEntradaInvalidaException("A distância entre as datas não pode ser superior a 50 dias.");
        }
    }

}

