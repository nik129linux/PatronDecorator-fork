package com.invoicecomposer.decorator.component;

import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.LineDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Concrete Component in the Decorator Pattern.
 *
 * Represents a plain electronic invoice with no additional behaviors.
 * Its total is simply the sum of all item line totals (quantity × unit price).
 * Decorators wrap this object to add taxes, discounts, signatures, etc.
 */
public class BasicInvoice implements InvoiceComponent {

    private final InvoiceData invoiceData;

    public BasicInvoice(InvoiceData invoiceData) {
        this.invoiceData = invoiceData;
    }

    @Override
    public double getSubtotal() {
        return invoiceData.getItems().stream()
                .mapToDouble(InvoiceItem::getLineTotal)
                .sum();
    }

    @Override
    public double getTaxableBase() {
        return getSubtotal();
    }

    @Override
    public double calculateTotal() {
        return getSubtotal();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = new ArrayList<>();

        // Individual item lines
        for (InvoiceItem item : invoiceData.getItems()) {
            breakdown.add(new LineDetail(
                    "ITEM",
                    item.getDescription(),
                    item.getQuantity() + " × $" + String.format("%,.2f", item.getUnitPrice()),
                    item.getLineTotal(),
                    0,
                    0
            ));
        }

        // Subtotal summary
        breakdown.add(new LineDetail(
                "SUBTOTAL",
                "Subtotal",
                "Sum of all invoice items",
                getSubtotal(),
                0,
                0
        ));

        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("invoiceNumber", invoiceData.getInvoiceNumber());
        metadata.put("issueDate", invoiceData.getIssueDate().toString());
        metadata.put("documentType", "INVOICE");
        metadata.put("status", "CREATED");
        metadata.put("customerName", invoiceData.getCustomer().getName());
        metadata.put("customerNit", invoiceData.getCustomer().getNit());
        metadata.put("sellerName", invoiceData.getSeller().getName());
        metadata.put("sellerNit", invoiceData.getSeller().getNit());
        return metadata;
    }

    @Override
    public String getDescription() {
        return "Basic Invoice #" + invoiceData.getInvoiceNumber();
    }

    /**
     * Provides direct access to the underlying invoice data.
     * Used by the XML generator and response builder.
     */
    public InvoiceData getInvoiceData() {
        return invoiceData;
    }
}
