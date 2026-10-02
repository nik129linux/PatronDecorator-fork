package com.invoicecomposer.dto;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;

/** A template as the API shows it: identity, regime and the request it would produce. */
public record TemplateSummary(String id, String name, String description,
                              TaxRegimeType regime, ComposeInvoiceRequest request) {
}
