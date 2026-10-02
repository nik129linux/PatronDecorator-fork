package com.invoicecomposer.creational.builder;

import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.Seller;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.DecoratorConfigDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Builder for {@link ComposeInvoiceRequest}: assembles the invoice data and its decorator chain
 * step by step, and validates everything at once in {@link #build()}.
 *
 * TODO (teammate): implement every method. Keep the signatures, the rest of the system uses them.
 * Rules for build():
 *  - issueDate defaults to now when it was not set;
 *  - invoiceNumber, seller, customer and at least one item are required;
 *  - every item needs quantity >= 1 and unitPrice >= 0;
 *  - on failure throw InvoiceException("INVALID_INVOICE", message) where the message lists
 *    ALL the problems found, not only the first one.
 */
public class InvoiceRequestBuilder {

    public static InvoiceRequestBuilder create() {
        return new InvoiceRequestBuilder();
    }

    /** Starts from a copy of an existing request (data and decorators), so it can be edited. */
    public InvoiceRequestBuilder from(ComposeInvoiceRequest base) {
        throw todo();
    }

    public InvoiceRequestBuilder invoiceNumber(String invoiceNumber) {
        throw todo();
    }

    public InvoiceRequestBuilder issueDate(LocalDateTime issueDate) {
        throw todo();
    }

    public InvoiceRequestBuilder seller(Seller seller) {
        throw todo();
    }

    public InvoiceRequestBuilder customer(Customer customer) {
        throw todo();
    }

    /** Appends one line item. */
    public InvoiceRequestBuilder addItem(String description, int quantity, double unitPrice) {
        throw todo();
    }

    /** Replaces all the line items. */
    public InvoiceRequestBuilder items(List<InvoiceItem> items) {
        throw todo();
    }

    /** Appends one decorator configuration to the chain. */
    public InvoiceRequestBuilder addDecorator(String type, Map<String, Object> parameters) {
        throw todo();
    }

    /** Replaces the whole decorator chain. */
    public InvoiceRequestBuilder decorators(List<DecoratorConfigDto> decorators) {
        throw todo();
    }

    public ComposeInvoiceRequest build() {
        throw todo();
    }

    private static UnsupportedOperationException todo() {
        return new UnsupportedOperationException("TODO (teammate): Builder not implemented yet");
    }
}
