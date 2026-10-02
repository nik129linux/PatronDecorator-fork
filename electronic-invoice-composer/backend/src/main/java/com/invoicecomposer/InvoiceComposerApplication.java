package com.invoicecomposer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Electronic Invoice Composer application.
 * Demonstrates the Decorator Design Pattern through a simulated
 * electronic invoicing system inspired by the Colombian DIAN ecosystem.
 */
@SpringBootApplication
public class InvoiceComposerApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvoiceComposerApplication.class, args);
    }
}
