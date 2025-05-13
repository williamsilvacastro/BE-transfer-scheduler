package com.wscastro.betransferscheduler.controller;

import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.dto.TransferenciaResponseDTO;
import com.wscastro.betransferscheduler.service.TransferenciaService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/agendamentos")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponseDTO> agendar(@RequestBody @Valid TransferenciaRequestDTO dto) {
        TransferenciaResponseDTO response = transferenciaService.agendarTransferencia(dto);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<TransferenciaResponseDTO>> listarTodos(@PageableDefault(size = 20) Pageable pageable) {
        Page<TransferenciaResponseDTO> agendamentos = transferenciaService.listarTodas(pageable);
        return ResponseEntity.ok(agendamentos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        transferenciaService.deletarTransferencia(id);
        return ResponseEntity.noContent().build();
    }
}
