package com.invoicecomposer.dto;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;

import java.util.List;

/** A tax regime as the API shows it, with the decorator chain it applies by default. */
public record RegimeSummary(TaxRegimeType type, String displayName,
                            List<DecoratorConfigDto> defaultDecorators) {
}
