package com.invoicecomposer.exception;

import com.invoicecomposer.dto.ApiError;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Global exception handler that returns consistent {@link ApiError} responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvoiceException.class)
    public ResponseEntity<ApiError> handleInvoiceException(InvoiceException ex) {
        ApiError error = new ApiError(
                ex.getCode(),
                ex.getMessage(),
                List.of()
        );
        HttpStatus status = "INVOICE_NOT_FOUND".equals(ex.getCode())
                ? HttpStatus.NOT_FOUND
                : HttpStatus.BAD_REQUEST;
        return ResponseEntity.status(status).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError error = new ApiError(
                "VALIDATION_ERROR",
                "Request validation failed",
                details
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError error = new ApiError(
                "INVALID_REQUEST",
                ex.getMessage(),
                List.of()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        ApiError error = new ApiError(
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                List.of(ex.getMessage() != null ? ex.getMessage() : "Unknown error")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
