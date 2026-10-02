package com.invoicecomposer.creational.abstractfactory;

import java.util.List;

/**
 * Entry point to get the concrete factory of a regime.
 *
 * TODO (teammate): replace the bodies with the real lookup of the three concrete factories.
 */
public final class TaxRegimeFactories {

    private TaxRegimeFactories() {
    }

    /** Returns the concrete factory for the regime. Never null. */
    public static TaxRegimeFactory of(TaxRegimeType type) {
        throw new UnsupportedOperationException("TODO (teammate): Abstract Factory not implemented yet");
    }

    /** Returns one factory per regime, in enum order. */
    public static List<TaxRegimeFactory> all() {
        throw new UnsupportedOperationException("TODO (teammate): Abstract Factory not implemented yet");
    }
}
