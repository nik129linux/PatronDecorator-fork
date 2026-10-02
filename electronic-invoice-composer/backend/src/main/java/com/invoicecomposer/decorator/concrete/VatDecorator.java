package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — adds VAT (IVA) calculation to the invoice.
 * The tax is computed on the current taxable base (subtotal minus discounts).
 */
public class VatDecorator extends InvoiceDecorator {

    private final double rate;

    public VatDecorator(InvoiceComponent wrappedComponent, double rate) {
        super(wrappedComponent);
        this.rate = rate;
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal() + getVatAmount();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        double base = wrappedComponent.getTaxableBase();
        breakdown.add(new LineDetail(
                "TAX_VAT",
                "IVA",
                String.format("VAT at %.1f%%", rate * 100),
                getVatAmount(),
                rate,
                base
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("vatApplied", true);
        metadata.put("vatRate", rate);
        metadata.put("vatAmount", getVatAmount());
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription()
                + " + VAT(" + String.format("%.1f%%", rate * 100) + ")";
    }

    private double getVatAmount() {
        return wrappedComponent.getTaxableBase() * rate;
    }
}
