package com.progracol.payment_links.config;

import com.progracol.payment_links.exception.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<Map<String, Object>> handleCustom(CustomException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "https://errors.example.com/" + ex.getCode());
        error.put("title", ex.getMessage());
        error.put("status", ex.getStatus());
        error.put("detail", ex.getMessage());  // Agregado
        error.put("code", ex.getCode());
        error.put("errors", Map.of());  // Puedes agregar si hay múltiples
        return ResponseEntity.status(ex.getStatus()).body(error);
    }

    // Maneja validaciones @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("type", "https://errors.example.com/validation_error");
        errorResponse.put("title", "Validation Error");
        errorResponse.put("status", 422);
        errorResponse.put("detail", "Invalid request data");
        errorResponse.put("code", "VALIDATION_ERROR");
        errorResponse.put("errors", errors);
        return ResponseEntity.status(422).body(errorResponse);
    }

}