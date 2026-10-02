package com.invoicecomposer.creational.abstractfactory;

/** Tax regimes the system can issue invoices for. Each one has its own family of objects. */
public enum TaxRegimeType {
    /** VAT-responsible taxpayer (ordinary regime). */
    ORDINARY,
    /** Taxpayer that is not responsible for VAT. */
    NON_VAT,
    /** Taxpayer of the Simple Tax Regime (RST). */
    SIMPLE
}
