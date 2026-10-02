package com.invoicecomposer.dto;

import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Response DTO containing the fully composed invoice — breakdown,
 * totals, metadata, decorator chain description, and XML.
 */
public class InvoiceResponse {

    private String id;
    private InvoiceData invoiceData;
    private double subtotal;
    private double taxableBase;
    private double total;
    private List<LineDetail> breakdown;
    private Map<String, Object> metadata;
    private String description;
    private String xml;
    private List<String> appliedDecorators;

    public InvoiceResponse() {
    }

    // --- Getters and Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public InvoiceData getInvoiceData() { return invoiceData; }
    public void setInvoiceData(InvoiceData invoiceData) { this.invoiceData = invoiceData; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public double getTaxableBase() { return taxableBase; }
    public void setTaxableBase(double taxableBase) { this.taxableBase = taxableBase; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public List<LineDetail> getBreakdown() { return breakdown; }
    public void setBreakdown(List<LineDetail> breakdown) { this.breakdown = breakdown; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getXml() { return xml; }
    public void setXml(String xml) { this.xml = xml; }

    public List<String> getAppliedDecorators() { return appliedDecorators; }
    public void setAppliedDecorators(List<String> appliedDecorators) { this.appliedDecorators = appliedDecorators; }
}
