package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — represents a simulated Debit Note applied to an invoice.
 * A debit note increases the total by a specified adjustment amount.
 */
public class DebitNoteDecorator extends InvoiceDecorator {

    private final double adjustmentAmount;
    private final String reason;

    public DebitNoteDecorator(InvoiceComponent wrappedComponent,
                               double adjustmentAmount, String reason) {
        super(wrappedComponent);
        this.adjustmentAmount = adjustmentAmount;
        this.reason = reason;
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal() + adjustmentAmount;
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        breakdown.add(new LineDetail(
                "DEBIT_NOTE",
                "Debit Note",
                "Debit note adjustment: " + reason,
                adjustmentAmount,
                0,
                wrappedComponent.calculateTotal()
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("debitNoteApplied", true);
        metadata.put("debitNoteAmount", adjustmentAmount);
        metadata.put("debitNoteReason", reason);
        metadata.put("documentType", "DEBIT_NOTE");
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription()
                + " + DebitNote($" + String.format("%,.2f", adjustmentAmount) + ")";
    }
}
