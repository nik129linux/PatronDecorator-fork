package com.invoicecomposer.dto;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;
import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.Seller;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request for the quick-compose feature. Every field is optional:
 * start from a template (Prototype), pick a tax regime (Abstract Factory) and override
 * whatever differs; the system assembles the request with the Builder.
 */
public class QuickComposeRequest {

    private String templateId;
    private TaxRegimeType regime;
    private String invoiceNumber;
    private LocalDateTime issueDate;

    @Valid
    private Seller seller;

    @Valid
    private Customer customer;

    @Valid
    private List<InvoiceItem> items;

    /** When true the invoice is stored; otherwise it is only previewed. */
    private boolean persist;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }

    public TaxRegimeType getRegime() { return regime; }
    public void setRegime(TaxRegimeType regime) { this.regime = regime; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }

    public boolean isPersist() { return persist; }
    public void setPersist(boolean persist) { this.persist = persist; }
}
