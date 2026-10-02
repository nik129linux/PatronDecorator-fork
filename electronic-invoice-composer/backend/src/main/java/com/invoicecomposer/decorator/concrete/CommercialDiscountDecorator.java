package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — applies a commercial discount to the invoice.
 * Supports both percentage-based and fixed-amount discounts.
 * Reduces the taxable base so that subsequent tax decorators compute on the discounted amount.
 */
public class CommercialDiscountDecorator extends InvoiceDecorator {

    private final double discountRate;
    private final double fixedAmount;
    private final boolean isPercentage;

    /**
     * Percentage-based discount constructor.
     */
    public CommercialDiscountDecorator(InvoiceComponent wrappedComponent, double discountRate) {
        super(wrappedComponent);
        this.discountRate = discountRate;
        this.fixedAmount = 0;
        this.isPercentage = true;
    }

    /**
     * Fixed-amount discount constructor.
     */
    public CommercialDiscountDecorator(InvoiceComponent wrappedComponent,
                                       double fixedAmount, boolean isFixed) {
        super(wrappedComponent);
        this.discountRate = 0;
        this.fixedAmount = fixedAmount;
        this.isPercentage = !isFixed;
    }

    @Override
    public double getTaxableBase() {
        return wrappedComponent.getTaxableBase() - getDiscountAmount();
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal() - getDiscountAmount();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        double base = wrappedComponent.getTaxableBase();
        String desc = isPercentage
                ? String.format("Commercial discount at %.1f%%", discountRate * 100)
                : String.format("Fixed commercial discount of $%,.2f", fixedAmount);
        breakdown.add(new LineDetail(
                "DISCOUNT",
                "Discount",
                desc,
                -getDiscountAmount(),
                isPercentage ? discountRate : 0,
                base
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("discountApplied", true);
        metadata.put("discountType", isPercentage ? "PERCENTAGE" : "FIXED");
        metadata.put("discountAmount", getDiscountAmount());
        return metadata;
    }

    @Override
    public String getDescription() {
        String detail = isPercentage
                ? String.format("%.1f%%", discountRate * 100)
                : String.format("$%,.2f", fixedAmount);
        return wrappedComponent.getDescription() + " + Discount(" + detail + ")";
    }

    private double getDiscountAmount() {
        if (isPercentage) {
            return wrappedComponent.getTaxableBase() * discountRate;
        }
        return Math.min(fixedAmount, wrappedComponent.getTaxableBase());
    }
}
