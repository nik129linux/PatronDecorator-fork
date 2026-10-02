package com.invoicecomposer.creational.prototype;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;
import com.invoicecomposer.dto.ComposeInvoiceRequest;

/**
 * A saved invoice model (data plus decorator chain) that can be cloned to issue
 * recurring invoices quickly. It is the concrete prototype of the registry.
 */
public class InvoiceTemplate implements Prototype<InvoiceTemplate> {

    private final String id;
    private final String name;
    private final String description;
    private final TaxRegimeType regime;
    private final ComposeInvoiceRequest request;

    public InvoiceTemplate(String id, String name, String description,
                           TaxRegimeType regime, ComposeInvoiceRequest request) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.regime = regime;
        this.request = request;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public TaxRegimeType getRegime() { return regime; }
    public ComposeInvoiceRequest getRequest() { return request; }

    @Override
    public InvoiceTemplate deepCopy() {
        return new InvoiceTemplate(id, name, description, regime, request.deepCopy());
    }
}
