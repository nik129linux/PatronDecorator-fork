package com.invoicecomposer.service;

import com.invoicecomposer.decorator.component.BasicInvoice;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.decorator.factory.DecoratorFactory;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.DecoratorConfigDto;
import com.invoicecomposer.dto.InvoiceResponse;
import com.invoicecomposer.exception.InvoiceException;
import com.invoicecomposer.repository.InvoiceRepository;
import com.invoicecomposer.util.XmlGenerator;

import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service that orchestrates invoice composition.
 *
 * Flow:
 * 1. Create a BasicInvoice (concrete component) from the request data.
 * 2. Iterate over the requested decorator configurations.
 * 3. Use {@link DecoratorFactory} to wrap the component with each decorator.
 * 4. Extract the breakdown, totals, metadata, and XML from the final composed object.
 * 5. Return the result as an {@link InvoiceResponse}.
 */
@Service
public class InvoiceService {

    private final InvoiceRepository repository;

    public InvoiceService(InvoiceRepository repository) {
        this.repository = repository;
    }

    /**
     * Composes an invoice by building the decorator chain and returns a preview
     * without persisting the result.
     */
    public InvoiceResponse preview(ComposeInvoiceRequest request) {
        InvoiceComponent composed = buildDecoratorChain(request);
        return buildResponse(null, request, composed);
    }

    /**
     * Composes an invoice, persists it, and returns the full response.
     */
    public InvoiceResponse compose(ComposeInvoiceRequest request) {
        InvoiceComponent composed = buildDecoratorChain(request);
        String id = UUID.randomUUID().toString().substring(0, 8);
        InvoiceResponse response = buildResponse(id, request, composed);
        repository.save(response);
        return response;
    }

    /**
     * Retrieves a previously composed invoice by ID.
     */
    public InvoiceResponse getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new InvoiceException("INVOICE_NOT_FOUND",
                        "Invoice not found with ID: " + id));
    }

    /**
     * Returns all stored invoices.
     */
    public Collection<InvoiceResponse> getAll() {
        return repository.findAll();
    }

    /**
     * Returns the list of available decorator types.
     */
    public List<String> getAvailableDecorators() {
        return DecoratorFactory.getAvailableTypes();
    }

    // --- Private helpers ---

    private InvoiceComponent buildDecoratorChain(ComposeInvoiceRequest request) {
        InvoiceComponent component = new BasicInvoice(request.getInvoiceData());

        if (request.getDecorators() != null) {
            for (DecoratorConfigDto config : request.getDecorators()) {
                try {
                    component = DecoratorFactory.create(component, config);
                } catch (IllegalArgumentException e) {
                    throw new InvoiceException("INVALID_DECORATOR",
                            "Invalid decorator configuration: " + e.getMessage());
                }
            }
        }

        return component;
    }

    private InvoiceResponse buildResponse(String id, ComposeInvoiceRequest request,
                                           InvoiceComponent composed) {
        InvoiceResponse response = new InvoiceResponse();
        response.setId(id);
        response.setInvoiceData(request.getInvoiceData());
        response.setSubtotal(composed.getSubtotal());
        response.setTaxableBase(composed.getTaxableBase());
        response.setTotal(composed.calculateTotal());
        response.setBreakdown(composed.getBreakdown());
        response.setMetadata(composed.getMetadata());
        response.setDescription(composed.getDescription());
        response.setXml(XmlGenerator.generate(composed, request.getInvoiceData()));

        if (request.getDecorators() != null) {
            response.setAppliedDecorators(
                    request.getDecorators().stream()
                            .map(DecoratorConfigDto::getType)
                            .collect(Collectors.toList())
            );
        }

        return response;
    }
}
