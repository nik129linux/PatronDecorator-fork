package com.invoicecomposer.controller;

import com.invoicecomposer.dto.InvoiceResponse;
import com.invoicecomposer.dto.QuickComposeRequest;
import com.invoicecomposer.dto.RegimeSummary;
import com.invoicecomposer.dto.TemplateSummary;
import com.invoicecomposer.service.QuickComposeService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller for the quick-compose feature (templates, regimes and one-shot composition).
 */
@RestController
@RequestMapping("/api/invoices")
public class QuickComposeController {

    private final QuickComposeService quickComposeService;

    public QuickComposeController(QuickComposeService quickComposeService) {
        this.quickComposeService = quickComposeService;
    }

    /** Lists the invoice templates that can be cloned. */
    @GetMapping("/templates")
    public ResponseEntity<List<TemplateSummary>> templates() {
        return ResponseEntity.ok(quickComposeService.listTemplates());
    }

    /** Lists the tax regimes with the decorator chain each one applies by default. */
    @GetMapping("/regimes")
    public ResponseEntity<List<RegimeSummary>> regimes() {
        return ResponseEntity.ok(quickComposeService.listRegimes());
    }

    /** Composes an invoice from a template, a regime and overrides. 201 when persisted, 200 when previewed. */
    @PostMapping("/quick")
    public ResponseEntity<InvoiceResponse> quick(@Valid @RequestBody QuickComposeRequest request) {
        InvoiceResponse response = quickComposeService.quickCompose(request);
        return ResponseEntity.status(request.isPersist() ? HttpStatus.CREATED : HttpStatus.OK).body(response);
    }
}
