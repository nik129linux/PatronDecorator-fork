package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — represents a simulated Credit Note applied to an invoice.
 * A credit note reduces the total by a specified adjustment amount.
 */
public class CreditNoteDecorator extends InvoiceDecorator {

    private final double adjustmentAmount;
    private final String reason;

    public CreditNoteDecorator(InvoiceComponent wrappedComponent,
                                double adjustmentAmount, String reason) {
        super(wrappedComponent);
        this.adjustmentAmount = adjustmentAmount;
        this.reason = reason;
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal() - adjustmentAmount;
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        breakdown.add(new LineDetail(
                "CREDIT_NOTE",
                "Credit Note",
                "Credit note adjustment: " + reason,
                -adjustmentAmount,
                0,
                wrappedComponent.calculateTotal()
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("creditNoteApplied", true);
        metadata.put("creditNoteAmount", adjustmentAmount);
        metadata.put("creditNoteReason", reason);
        metadata.put("documentType", "CREDIT_NOTE");
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription()
                + " + CreditNote($" + String.format("%,.2f", adjustmentAmount) + ")";
    }
}
