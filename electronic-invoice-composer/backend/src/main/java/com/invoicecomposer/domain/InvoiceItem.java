package com.invoicecomposer.domain;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Represents a single item line within an electronic invoice.
 */
public class InvoiceItem {

    @NotBlank(message = "Item description is required")
    private String description;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    @PositiveOrZero(message = "Unit price must be zero or positive")
    private double unitPrice;

    public InvoiceItem() {
    }

    public InvoiceItem(String description, int quantity, double unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Returns the total for this line: quantity × unit price.
     */
    public double getLineTotal() {
        return quantity * unitPrice;
    }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }

    /** Prototype support: independent copy of this line item. */
    public InvoiceItem copy() {
        return new InvoiceItem(description, quantity, unitPrice);
    }
}
