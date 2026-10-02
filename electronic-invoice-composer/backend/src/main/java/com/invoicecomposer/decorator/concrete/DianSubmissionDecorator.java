package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Concrete Decorator — simulates submitting the electronic invoice to DIAN.
 *
 * ACADEMIC SIMULATION: This does NOT connect to the real DIAN web services.
 * It generates a simulated tracking ID and response status.
 */
public class DianSubmissionDecorator extends InvoiceDecorator {

    private final String trackingId;
    private final LocalDateTime submittedAt;
    private final String status;

    public DianSubmissionDecorator(InvoiceComponent wrappedComponent) {
        super(wrappedComponent);
        this.trackingId = "DIAN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.submittedAt = LocalDateTime.now();
        this.status = "ACCEPTED";
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        breakdown.add(new LineDetail(
                "DIAN_SUBMISSION",
                "DIAN Submission",
                "Simulated DIAN submission — Tracking: " + trackingId + " — Status: " + status,
                0,
                0,
                0
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("dianSubmitted", true);
        metadata.put("dianTrackingId", trackingId);
        metadata.put("dianSubmittedAt", submittedAt.toString());
        metadata.put("dianStatus", status);
        metadata.put("dianDisclaimer",
                "This is a simulated DIAN submission for academic purposes only. "
                + "This application is not connected to the real DIAN electronic invoicing system.");
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription() + " + DianSubmission";
    }
}
