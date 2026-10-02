package com.invoicecomposer.controller;

import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.InvoiceResponse;
import com.invoicecomposer.service.InvoiceService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

/**
 * REST controller exposing the electronic invoice composition API.
 */
@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    /**
     * Preview an invoice composition without persisting.
     * Builds the decorator chain and returns the computed result.
     */
    @PostMapping("/preview")
    public ResponseEntity<InvoiceResponse> preview(@Valid @RequestBody ComposeInvoiceRequest request) {
        InvoiceResponse response = invoiceService.preview(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Compose and persist an invoice with the given decorator chain.
     */
    @PostMapping
    public ResponseEntity<InvoiceResponse> compose(@Valid @RequestBody ComposeInvoiceRequest request) {
        InvoiceResponse response = invoiceService.compose(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieve a previously composed invoice by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> getById(@PathVariable String id) {
        InvoiceResponse response = invoiceService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all stored invoices.
     */
    @GetMapping
    public ResponseEntity<Collection<InvoiceResponse>> getAll() {
        return ResponseEntity.ok(invoiceService.getAll());
    }

    /**
     * Returns the list of all available decorator types.
     */
    @GetMapping("/decorators")
    public ResponseEntity<List<String>> getAvailableDecorators() {
        return ResponseEntity.ok(invoiceService.getAvailableDecorators());
    }
}
