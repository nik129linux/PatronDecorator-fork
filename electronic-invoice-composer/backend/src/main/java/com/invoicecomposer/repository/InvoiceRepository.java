package com.invoicecomposer.repository;

import com.invoicecomposer.dto.InvoiceResponse;

import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory invoice repository.
 * Uses a ConcurrentHashMap for thread-safe storage without external database dependencies.
 * Ideal for this academic demonstration where persistence is secondary to the pattern focus.
 */
@Repository
public class InvoiceRepository {

    private final Map<String, InvoiceResponse> store = new ConcurrentHashMap<>();

    public InvoiceResponse save(InvoiceResponse invoice) {
        store.put(invoice.getId(), invoice);
        return invoice;
    }

    public Optional<InvoiceResponse> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    public Collection<InvoiceResponse> findAll() {
        return store.values();
    }

    public void deleteById(String id) {
        store.remove(id);
    }

    public boolean existsById(String id) {
        return store.containsKey(id);
    }
}
