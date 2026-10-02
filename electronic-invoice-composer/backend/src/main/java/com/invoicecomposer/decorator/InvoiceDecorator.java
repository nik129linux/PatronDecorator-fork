package com.invoicecomposer.decorator;

import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.LineDetail;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract Decorator in the Decorator Pattern.
 *
 * Wraps an {@link InvoiceComponent} and delegates all operations to it by default.
 * Concrete decorators extend this class and override specific methods to add
 * their responsibilities (e.g., taxes, discounts, signatures).
 *
 * The wrapped component is stored as a protected field so subclasses can
 * access the inner component's state directly when computing their additions.
 */
public abstract class InvoiceDecorator implements InvoiceComponent {

    protected final InvoiceComponent wrappedComponent;

    protected InvoiceDecorator(InvoiceComponent wrappedComponent) {
        this.wrappedComponent = wrappedComponent;
    }

    @Override
    public double getSubtotal() {
        return wrappedComponent.getSubtotal();
    }

    @Override
    public double getTaxableBase() {
        return wrappedComponent.getTaxableBase();
    }

    @Override
    public double calculateTotal() {
        return wrappedComponent.calculateTotal();
    }

    @Override
    public List<LineDetail> getBreakdown() {
        return new ArrayList<>(wrappedComponent.getBreakdown());
    }

    @Override
    public Map<String, Object> getMetadata() {
        return new LinkedHashMap<>(wrappedComponent.getMetadata());
    }

    @Override
    public String getDescription() {
        return wrappedComponent.getDescription();
    }
}
