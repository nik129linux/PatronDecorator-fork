package com.invoicecomposer.dto;

import com.invoicecomposer.domain.InvoiceData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for composing an invoice with optional decorators.
 * Used by both the preview and the create/persist endpoints.
 */
public class ComposeInvoiceRequest {

    @NotNull(message = "Invoice data is required")
    @Valid
    private InvoiceData invoiceData;

    private List<DecoratorConfigDto> decorators = new ArrayList<>();

    public ComposeInvoiceRequest() {
    }

    public InvoiceData getInvoiceData() { return invoiceData; }
    public void setInvoiceData(InvoiceData invoiceData) { this.invoiceData = invoiceData; }

    public List<DecoratorConfigDto> getDecorators() { return decorators; }
    public void setDecorators(List<DecoratorConfigDto> decorators) { this.decorators = decorators; }
}
