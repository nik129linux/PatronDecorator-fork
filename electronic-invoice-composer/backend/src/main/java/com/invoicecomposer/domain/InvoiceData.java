package com.invoicecomposer.domain;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Aggregates all data needed to create a basic electronic invoice.
 */
public class InvoiceData {

    @NotBlank(message = "Invoice number is required")
    private String invoiceNumber;

    @NotNull(message = "Issue date is required")
    private LocalDateTime issueDate;

    @NotNull(message = "Customer is required")
    @Valid
    private Customer customer;

    @NotNull(message = "Seller is required")
    @Valid
    private Seller seller;

    @NotEmpty(message = "At least one invoice item is required")
    @Valid
    private List<InvoiceItem> items;

    public InvoiceData() {
    }

    public InvoiceData(String invoiceNumber, LocalDateTime issueDate,
                       Customer customer, Seller seller, List<InvoiceItem> items) {
        this.invoiceNumber = invoiceNumber;
        this.issueDate = issueDate;
        this.customer = customer;
        this.seller = seller;
        this.items = items;
    }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public LocalDateTime getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDateTime issueDate) { this.issueDate = issueDate; }

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public Seller getSeller() { return seller; }
    public void setSeller(Seller seller) { this.seller = seller; }

    public List<InvoiceItem> getItems() { return items; }
    public void setItems(List<InvoiceItem> items) { this.items = items; }

    /**
     * Prototype support: deep copy. The seller, customer and every item are copied too,
     * so changing the clone never touches the original.
     */
    public InvoiceData copy() {
        List<InvoiceItem> itemsCopy = items == null
                ? null
                : items.stream().map(InvoiceItem::copy).collect(Collectors.toList());
        return new InvoiceData(invoiceNumber, issueDate,
                customer == null ? null : customer.copy(),
                seller == null ? null : seller.copy(),
                itemsCopy);
    }
}
