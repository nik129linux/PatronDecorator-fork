package com.invoicecomposer.service;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeFactories;
import com.invoicecomposer.creational.abstractfactory.TaxRegimeFactory;
import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;

import java.util.List;

/**
 * Access to the Abstract Factory families. An interface so the service can be tested
 * without the real factories.
 */
public interface TaxRegimeCatalog {

    TaxRegimeFactory of(TaxRegimeType type);

    List<TaxRegimeFactory> all();

    /** The real catalog, backed by {@link TaxRegimeFactories}. */
    static TaxRegimeCatalog standard() {
        return new TaxRegimeCatalog() {
            @Override
            public TaxRegimeFactory of(TaxRegimeType type) {
                return TaxRegimeFactories.of(type);
            }

            @Override
            public List<TaxRegimeFactory> all() {
                return TaxRegimeFactories.all();
            }
        };
    }
}
