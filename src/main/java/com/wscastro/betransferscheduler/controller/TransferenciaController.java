package com.wscastro.betransferscheduler.controller;

import com.wscastro.betransferscheduler.dto.TransferenciaRequestDTO;
import com.wscastro.betransferscheduler.dto.TransferenciaResponseDTO;
import com.wscastro.betransferscheduler.service.TransferenciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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
    public ResponseEntity<List<TransferenciaResponseDTO>> listarTodos() {
        List<TransferenciaResponseDTO> agendamentos = transferenciaService.listarTodas();
        return ResponseEntity.ok(agendamentos);
    }
}
