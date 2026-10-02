package com.invoicecomposer.creational.builder;

import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.Seller;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.DecoratorConfigDto;
import com.invoicecomposer.exception.InvoiceException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder for {@link ComposeInvoiceRequest}: assembles the invoice data and its decorator chain
 * step by step, and validates everything at once in {@link #build()}.
 *
 * Every setter is fluent (returns {@code this}), {@link #from(ComposeInvoiceRequest)} starts
 * from a deep copy so the caller's request is never mutated, and {@link #build()} always returns
 * brand new objects so the same builder can be used more than once.
 */
public class InvoiceRequestBuilder {

    private String invoiceNumber;
    private LocalDateTime issueDate;
    private Seller seller;
    private Customer customer;
    private final List<InvoiceItem> items = new ArrayList<>();
    private final List<DecoratorConfigDto> decorators = new ArrayList<>();

    public static InvoiceRequestBuilder create() {
        return new InvoiceRequestBuilder();
    }

    /** Starts from a copy of an existing request (data and decorators), so it can be edited. */
    public InvoiceRequestBuilder from(ComposeInvoiceRequest base) {
        if (base == null) {
            throw new IllegalArgumentException("Base request is required");
        }

        ComposeInvoiceRequest copy = base.deepCopy();
        InvoiceData data = copy.getInvoiceData();
        if (data != null) {
            invoiceNumber = data.getInvoiceNumber();
            issueDate = data.getIssueDate();
            seller = data.getSeller();
            customer = data.getCustomer();
            items.clear();
            if (data.getItems() != null) {
                items.addAll(data.getItems());
            }
        }

        decorators.clear();
        decorators.addAll(copy.getDecorators());
        return this;
    }

    public InvoiceRequestBuilder invoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
        return this;
    }

    public InvoiceRequestBuilder issueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
        return this;
    }

    public InvoiceRequestBuilder seller(Seller seller) {
        this.seller = seller;
        return this;
    }

    public InvoiceRequestBuilder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    /** Appends one line item. */
    public InvoiceRequestBuilder addItem(String description, int quantity, double unitPrice) {
        items.add(new InvoiceItem(description, quantity, unitPrice));
        return this;
    }

    /** Replaces all the line items. */
    public InvoiceRequestBuilder items(List<InvoiceItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(item -> this.items.add(item == null ? null : item.copy()));
        }
        return this;
    }

    /** Appends one decorator configuration to the chain. */
    public InvoiceRequestBuilder addDecorator(String type, Map<String, Object> parameters) {
        decorators.add(new DecoratorConfigDto(type,
                parameters == null ? new HashMap<>() : new HashMap<>(parameters)));
        return this;
    }

    /** Replaces the whole decorator chain. */
    public InvoiceRequestBuilder decorators(List<DecoratorConfigDto> decorators) {
        this.decorators.clear();
        if (decorators != null) {
            decorators.forEach(config -> this.decorators.add(config == null ? null : config.copy()));
        }
        return this;
    }

    /**
     * Validates everything at once and returns a new request.
     *
     * The issue date defaults to now when it was not set. On failure all the problems found are
     * reported together, separated by "; ", instead of stopping at the first one.
     *
     * @throws InvoiceException with code INVALID_INVOICE when at least one rule is broken
     */
    public ComposeInvoiceRequest build() {
        List<String> problems = new ArrayList<>();

        if (isBlank(invoiceNumber)) problems.add("invoiceNumber is required");
        if (seller == null) problems.add("seller is required");
        if (customer == null) problems.add("customer is required");
        if (items.isEmpty()) problems.add("at least one item is required");

        for (int i = 0; i < items.size(); i++) {
            InvoiceItem item = items.get(i);
            if (item == null) {
                problems.add("items[" + i + "] is required");
                continue;
            }
            if (item.getQuantity() < 1) {
                problems.add("items[" + i + "].quantity must be at least 1");
            }
            if (item.getUnitPrice() < 0) {
                problems.add("items[" + i + "].unitPrice must be zero or positive");
            }
        }

        for (int i = 0; i < decorators.size(); i++) {
            DecoratorConfigDto config = decorators.get(i);
            if (config == null || isBlank(config.getType())) {
                problems.add("decorators[" + i + "].type must not be blank");
            }
        }

        if (!problems.isEmpty()) {
            throw new InvoiceException("INVALID_INVOICE", String.join("; ", problems));
        }

        InvoiceData data = new InvoiceData(invoiceNumber,
                issueDate != null ? issueDate : LocalDateTime.now(),
                customer.copy(), seller.copy(), itemCopies());

        ComposeInvoiceRequest request = new ComposeInvoiceRequest();
        request.setInvoiceData(data);
        request.setDecorators(decoratorCopies());
        return request;
    }

    /** Independent copies of the items, so the built request never shares state with the builder. */
    private List<InvoiceItem> itemCopies() {
        List<InvoiceItem> copy = new ArrayList<>(items.size());
        items.forEach(item -> copy.add(item.copy()));
        return copy;
    }

    /** Independent copies of the decorator chain, so two builds never share a configuration. */
    private List<DecoratorConfigDto> decoratorCopies() {
        List<DecoratorConfigDto> copy = new ArrayList<>(decorators.size());
        decorators.forEach(config -> copy.add(config.copy()));
        return copy;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
