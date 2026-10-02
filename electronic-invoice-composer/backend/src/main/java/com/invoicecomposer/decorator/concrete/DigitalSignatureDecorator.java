package com.invoicecomposer.decorator.concrete;

import com.invoicecomposer.decorator.InvoiceDecorator;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Concrete Decorator — simulates digitally signing the electronic document.
 *
 * ACADEMIC SIMULATION: This does NOT perform real cryptographic signing.
 * It generates a simulated hash and marks the document as signed.
 */
public class DigitalSignatureDecorator extends InvoiceDecorator {

    private final String signatureHash;
    private final LocalDateTime signedAt;

    public DigitalSignatureDecorator(InvoiceComponent wrappedComponent) {
        super(wrappedComponent);
        this.signatureHash = "SIM-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        this.signedAt = LocalDateTime.now();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        List<LineDetail> breakdown = super.getBreakdown();
        breakdown.add(new LineDetail(
                "SIGNATURE",
                "Digital Signature",
                "Simulated digital signature — Hash: " + signatureHash,
                0,
                0,
                0
        ));
        return breakdown;
    }

    @Override
    public Map<String, Object> getMetadata() {
        Map<String, Object> metadata = super.getMetadata();
        metadata.put("signed", true);
        metadata.put("signatureHash", signatureHash);
        metadata.put("signedAt", signedAt.toString());
        metadata.put("signatureType", "SIMULATED");
        metadata.put("signatureDisclaimer",
                "This is a simulated digital signature for academic purposes only. "
                + "It does not constitute a legally valid electronic signature.");
        return metadata;
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription() + " + DigitalSignature";
    }
}
