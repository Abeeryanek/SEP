package com.sepdrive.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        Map<String, String> response = new HashMap<>();
        String message = ex.getMessage();
        
        // Authorization errors
        if (message != null && message.startsWith("Not authorized")) {
            response.put("error", message);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
        }
        
        // Not found errors
        if (message != null && message.contains("not found")) {
            response.put("error", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        // Business logic errors (e.g., "Cannot complete ride request", "Customer already has an active ride request")
        if (message != null && (
            message.startsWith("Cannot") || 
            message.startsWith("Customer already") ||
            message.startsWith("Request is not active")
        )) {
            response.put("error", message);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        
        // Default error
        response.put("error", "Internal server error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BalanceException.class)
    public ResponseEntity<Map<String, String>> handleBalance(BalanceException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }

    @ExceptionHandler(BroadcastException.class)
    public ResponseEntity<Map<String, String>> handleBroadcast(BroadcastException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error","WebSocket error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(InvalidRoleException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRole(InvalidRoleException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(UserExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserExist(UserExistsException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Unexpected error: " + ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 