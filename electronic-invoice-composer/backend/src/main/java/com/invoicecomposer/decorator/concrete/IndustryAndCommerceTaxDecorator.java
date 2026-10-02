package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — applies Industry and Commerce Tax (ICA / ReteICA).
 * The rate is configurable since ICA varies by jurisdiction and economic activity.
 */
public class IndustryAndCommerceTaxDecorator extends InvoiceDecorator {

    private final double rate;

    public IndustryAndCommerceTaxDecorator(InvoiceComponent wrappedComponent, double rate) {
        super(wrappedComponent);
        this.rate = rate;
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal() - getIcaAmount();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        double base = wrappedComponent.getTaxableBase();
        breakdown.add(new LineDetail(
                "TAX_ICA",
                "ICA",
                String.format("Industry & Commerce Tax at %.2f%%", rate * 100),
                -getIcaAmount(),
                rate,
                base
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("icaApplied", true);
        metadata.put("icaRate", rate);
        metadata.put("icaAmount", getIcaAmount());
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription()
                + " + ICA(" + String.format("%.2f%%", rate * 100) + ")";
    }

    private double getIcaAmount() {
        return wrappedComponent.getTaxableBase() * rate;
    }
}
