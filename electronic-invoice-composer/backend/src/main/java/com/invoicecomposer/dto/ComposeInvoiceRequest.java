package com.invoicecomposer.dto;

import com.invoicecomposer.creational.prototype.Prototype;
import com.invoicecomposer.domain.InvoiceData;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Request DTO for composing an invoice with optional decorators.
 * Used by both the preview and the create/persist endpoints.
 */
public class ComposeInvoiceRequest implements Prototype<ComposeInvoiceRequest> {

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

    /** Prototype support: deep copy of the invoice data and of every decorator configuration. */
    @Override
    public ComposeInvoiceRequest deepCopy() {
        ComposeInvoiceRequest clone = new ComposeInvoiceRequest();
        clone.setInvoiceData(invoiceData == null ? null : invoiceData.copy());
        clone.setDecorators(decorators.stream().map(DecoratorConfigDto::copy)
                .collect(java.util.stream.Collectors.toCollection(ArrayList::new)));
        return clone;
    }
}
