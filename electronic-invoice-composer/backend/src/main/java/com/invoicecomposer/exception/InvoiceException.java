package com.invoicecomposer.exception;

/**
 * Custom exception for invoice-related business errors.
 */
public class InvoiceException extends RuntimeException {

    private final String code;

    public InvoiceException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
