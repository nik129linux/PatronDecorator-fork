package com.invoicecomposer.creational.abstractfactory;

import java.util.List;

/**
 * Entry point to get the concrete factory of a regime.
 *
 * One shared instance per factory, so every caller sees the same family of products.
 */
public final class TaxRegimeFactories {

    private static final TaxRegimeFactory ORDINARY = new OrdinaryRegimeFactory();
    private static final TaxRegimeFactory NON_VAT = new NonVatRegimeFactory();
    private static final TaxRegimeFactory SIMPLE = new SimpleRegimeFactory();

    private TaxRegimeFactories() {
    }

    /** Returns the concrete factory for the regime. Never null. */
    public static TaxRegimeFactory of(TaxRegimeType type) {
        return switch (type) {
            case ORDINARY -> ORDINARY;
            case NON_VAT -> NON_VAT;
            case SIMPLE -> SIMPLE;
        };
    }

    /** Returns one factory per regime, in enum order. */
    public static List<TaxRegimeFactory> all() {
        return List.of(ORDINARY, NON_VAT, SIMPLE);
    }
}
