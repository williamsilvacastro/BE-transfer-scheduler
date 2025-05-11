package com.wscastro.betransferscheduler.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(TaxaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleTaxaInvalida(TaxaInvalidaException ex, WebRequest request) {
        logger.error("Taxa inválida: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataEntradaInvalidaException.class)
    public ResponseEntity<ErrorResponse> handleDataInvalida(DataEntradaInvalidaException ex, WebRequest request) {
        logger.error("Data inválida: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ContasIguaisException.class)
    public ResponseEntity<ErrorResponse> handleContasIguais(ContasIguaisException ex, WebRequest request) {
        logger.error("Contas iguais: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(DataTransferenciaNaoFuturaException.class)
    public ResponseEntity<ErrorResponse> handleDataNaoFutura(DataTransferenciaNaoFuturaException ex, WebRequest request) {
        logger.error("Data de transferência nao futura: {}", ex.getMessage());
        return buildErrorResponse(ex, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacao(MethodArgumentNotValidException ex, WebRequest request) {
        logger.error("Erro de validacao: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message("Erro de validacao")
                .path(request.getDescription(false).substring(4))
                .build();

        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                erros.put(error.getField(), error.getDefaultMessage()));

        errorResponse.addValidationErrors(erros);

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOutros(Exception ex, WebRequest request) {
        logger.error("Erro inesperado: ", ex);
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status, WebRequest request) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getDescription(false).substring(4))
                .build();

        return new ResponseEntity<>(errorResponse, status);
    }
}
