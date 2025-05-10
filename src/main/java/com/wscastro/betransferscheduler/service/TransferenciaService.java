package com.wscastro.betransferscheduler.service;

import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.dto.TransferenciaResponseDTO;
import com.wscastro.betransferscheduler.model.Transferencia;
import com.wscastro.betransferscheduler.repository.TransferenciaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferenciaService {

    private final TransferenciaRepository repository;
    private final TaxaService taxaService;

    public TransferenciaService(TransferenciaRepository repository, TaxaService taxaService) {
        this.repository = repository;
        this.taxaService = taxaService;
    }

    public TransferenciaResponseDTO agendarTransferencia(TransferenciaRequestDTO dto) {
        LocalDate hoje = LocalDate.now();
        BigDecimal taxa = taxaService.calcularTaxa(dto.getValor(), hoje, dto.getDataTransferencia());

        Transferencia entity = new Transferencia();
        entity.setContaOrigem(dto.getContaOrigem());
        entity.setContaDestino(dto.getContaDestino());
        entity.setValor(dto.getValor());
        entity.setTaxa(taxa);
        entity.setDataAgendamento(hoje);
        entity.setDataTransferencia(dto.getDataTransferencia());

        repository.save(entity);

        return new TransferenciaResponseDTO(entity);
    }

    public List<TransferenciaResponseDTO> listarTodas() {
        return repository.findAll().stream()
                .map(TransferenciaResponseDTO::new)
                .collect(Collectors.toList());
    }
}
