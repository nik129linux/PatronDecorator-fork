package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Concrete Decorator — simulates sending the invoice to the customer via email.
 *
 * ACADEMIC SIMULATION: No real email is sent unless a mail provider is configured.
 */
public class CustomerEmailDecorator extends InvoiceDecorator {

    private final String recipientEmail;
    private final String deliveryStatus;
    private final LocalDateTime sentAt;

    public CustomerEmailDecorator(InvoiceComponent wrappedComponent, String recipientEmail) {
        super(wrappedComponent);
        this.recipientEmail = recipientEmail;
        this.deliveryStatus = "DELIVERED";
        this.sentAt = LocalDateTime.now();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        breakdown.add(new LineDetail(
                "EMAIL_NOTIFICATION",
                "Email Notification",
                "Invoice sent to: " + recipientEmail + " — Status: " + deliveryStatus,
                0,
                0,
                0
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("emailSent", true);
        metadata.put("recipientEmail", recipientEmail);
        metadata.put("emailStatus", deliveryStatus);
        metadata.put("emailSentAt", sentAt.toString());
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription() + " + EmailNotification";
    }
}
