package com.algaworks.brewer.service.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BasicExceptionHandler {

    @ExceptionHandler(Exception.class)

    public ResponseEntity<String> handleNomeEstiloJaCadastradoException(Exception e) {

        e.printStackTrace();

        return ResponseEntity.status(500).build();

    }
}
