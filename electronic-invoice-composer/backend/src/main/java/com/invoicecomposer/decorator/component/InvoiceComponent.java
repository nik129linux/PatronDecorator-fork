package com.invoicecomposer.decorator.component;

import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Component interface for the Decorator Pattern.
 *
 * Defines the contract that both the concrete component ({@link BasicInvoice})
 * and all decorators must satisfy. This ensures that decorators can be stacked
 * transparently — each layer adds behavior while preserving the original interface.
 */
public interface InvoiceComponent {

    /**
     * Returns the original subtotal (sum of all invoice items).
     * This value is never modified by decorators.
     */
    double getSubtotal();

    /**
     * Returns the taxable base for tax calculations.
     * Initially equals the subtotal. Discounts reduce it; taxes do not.
     */
    double getTaxableBase();

    /**
     * Returns the running total after all adjustments applied by this
     * component and every component it wraps.
     */
    double calculateTotal();

    /**
     * Returns an ordered list of line details showing every financial
     * concept applied to the invoice (subtotal, taxes, discounts, etc.).
     */
    List<LineDetail> getBreakdown();

    /**
     * Returns metadata accumulated by decorators (signature hashes,
     * DIAN tracking IDs, email statuses, etc.).
     */
    Map<String, Object> getMetadata();

    /**
     * Returns a human-readable description of the current component
     * and all decorators applied so far.
     */
    String getDescription();
}
