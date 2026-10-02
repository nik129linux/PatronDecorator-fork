package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — applies withholding tax (ReteFuente) to the invoice.
 * Withholding is subtracted from the total, calculated on the taxable base.
 */
public class WithholdingDecorator extends InvoiceDecorator {

    private final double rate;

    public WithholdingDecorator(InvoiceComponent wrappedComponent, double rate) {
        super(wrappedComponent);
        this.rate = rate;
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal() - getWithholdingAmount();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        double base = wrappedComponent.getTaxableBase();
        breakdown.add(new LineDetail(
                "TAX_WITHHOLDING",
                "Withholding",
                String.format("Withholding at %.2f%%", rate * 100),
                -getWithholdingAmount(),
                rate,
                base
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("withholdingApplied", true);
        metadata.put("withholdingRate", rate);
        metadata.put("withholdingAmount", getWithholdingAmount());
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription()
                + " + Withholding(" + String.format("%.2f%%", rate * 100) + ")";
    }

    private double getWithholdingAmount() {
        return wrappedComponent.getTaxableBase() * rate;
    }
}
