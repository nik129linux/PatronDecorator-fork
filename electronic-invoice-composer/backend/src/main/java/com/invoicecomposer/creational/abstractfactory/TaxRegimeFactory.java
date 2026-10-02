package com.invoicecomposer.creational.abstractfactory;

import com.invoicecomposer.dto.DecoratorConfigDto;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Factory: creates the family of related objects that belong to one tax regime.
 * Each concrete factory (one per {@link TaxRegimeType}) guarantees that the three products
 * it creates are consistent with each other.
 */
public interface TaxRegimeFactory {

    TaxRegimeType regime();

    /** Human readable name, for example "VAT-responsible (ordinary regime)". */
    String displayName();

    IndirectTaxLayer createIndirectTaxLayer();

    WithholdingLayer createWithholdingLayer();

    ComplianceLayer createComplianceLayer();

    /**
     * The default decorator chain of the regime, in wrapping order:
     * taxes first, then withholdings, then compliance steps.
     */
    default List<DecoratorConfigDto> defaultChain() {
        List<DecoratorConfigDto> chain = new ArrayList<>();
        chain.addAll(createIndirectTaxLayer().decorators());
        chain.addAll(createWithholdingLayer().decorators());
        chain.addAll(createComplianceLayer().decorators());
        return chain;
    }
}
