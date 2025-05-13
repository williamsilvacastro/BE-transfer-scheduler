package com.wscastro.betransferscheduler.service;

import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.dto.TransferenciaResponseDTO;
import com.wscastro.betransferscheduler.exception.ContasIguaisException;
import com.wscastro.betransferscheduler.exception.DataTransferenciaNaoFuturaException;
import com.wscastro.betransferscheduler.model.Transferencia;
import com.wscastro.betransferscheduler.repository.TransferenciaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransferenciaService {
    private static final Logger logger = LoggerFactory.getLogger(TransferenciaService.class);

    private final TransferenciaRepository repository;
    private final TaxaService taxaService;

    public TransferenciaService(TransferenciaRepository repository, TaxaService taxaService) {
        this.repository = repository;
        this.taxaService = taxaService;
    }

    public TransferenciaResponseDTO agendarTransferencia(TransferenciaRequestDTO dto) {
        logger.info("Agendando transferência de {} para {}, valor: {}, data: {}", 
                dto.getContaOrigem(), dto.getContaDestino(), dto.getValor(), dto.getDataTransferencia());

        validarContasDiferentes(dto.getContaOrigem(), dto.getContaDestino());
        validarDataFutura(dto.getDataTransferencia());

        LocalDateTime hoje = LocalDateTime.now();
        BigDecimal taxa = taxaService.calcularTaxa(dto.getValor(), hoje, dto.getDataTransferencia());

        Transferencia entity = new Transferencia();
        entity.setContaOrigem(dto.getContaOrigem());
        entity.setContaDestino(dto.getContaDestino());
        entity.setValor(dto.getValor());
        entity.setTaxa(taxa);
        entity.setDataAgendamento(hoje);
        entity.setDataTransferencia(dto.getDataTransferencia());

        repository.save(entity);
        logger.info("Transferência agendada com sucesso. ID: {}", entity.getId());

        return new TransferenciaResponseDTO(entity);
    }

    private void validarContasDiferentes(String contaOrigem, String contaDestino) {
        if (contaOrigem.equals(contaDestino)) {
            logger.error("Contas de origem e destino sao iguais: {}", contaOrigem);
            throw new ContasIguaisException("A conta de origem e destino nao podem ser iguais");
        }
    }

    private void validarDataFutura(LocalDateTime dataTransferencia) {
        LocalDateTime hoje = LocalDateTime.now();
        if (dataTransferencia.isBefore(hoje)) {
            logger.error("Data de transferência está no passado: {}", dataTransferencia);
            throw new DataTransferenciaNaoFuturaException("A data de transferência deve ser igual ou posterior à data atual");
        }
    }

    public List<TransferenciaResponseDTO> listarTodas() {
        logger.info("Listando todas as transferências");
        return repository.findAll().stream()
                .map(TransferenciaResponseDTO::new)
                .collect(Collectors.toList());
    }

    public Page<TransferenciaResponseDTO> listarTodas(Pageable pageable) {
        logger.info("Listando transferências com paginacao: page={}, size={}", 
                pageable.getPageNumber(), pageable.getPageSize());
        return repository.findAll(pageable)
                .map(TransferenciaResponseDTO::new);
    }

    public void deletarTransferencia(Long id) {
        logger.info("Deletando transferência com ID: {}", id);
        if (!repository.existsById(id)) {
            logger.error("Transferência não encontrada com ID: {}", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transferência não encontrada");
        }
        repository.deleteById(id);
        logger.info("Transferência deletada com sucesso. ID: {}", id);
    }
}
