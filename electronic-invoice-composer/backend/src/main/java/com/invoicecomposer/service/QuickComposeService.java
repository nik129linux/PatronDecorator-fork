package com.invoicecomposer.service;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeFactory;
import com.invoicecomposer.creational.builder.InvoiceRequestBuilder;
import com.invoicecomposer.creational.prototype.InvoiceTemplate;
import com.invoicecomposer.creational.prototype.InvoiceTemplateRegistry;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.InvoiceResponse;
import com.invoicecomposer.dto.QuickComposeRequest;
import com.invoicecomposer.dto.RegimeSummary;
import com.invoicecomposer.dto.TemplateSummary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Supplier;

/**
 * Quick-compose feature: issues an invoice from a template, a tax regime and a few overrides.
 *
 * Flow:
 * 1. Prototype: clone the template (if any), so the stored model is never touched.
 * 2. Builder: load the clone and apply the overrides sent by the user.
 * 3. Abstract Factory: if a regime was chosen, replace the chain with the regime's default chain.
 * 4. Hand the assembled request to {@link InvoiceService} (preview or persist).
 */
@Service
public class QuickComposeService {

    private final InvoiceService invoiceService;
    private final InvoiceTemplateRegistry registry;
    private final Supplier<InvoiceRequestBuilder> builders;
    private final TaxRegimeCatalog catalog;

    @Autowired
    public QuickComposeService(InvoiceService invoiceService, InvoiceTemplateRegistry registry) {
        this(invoiceService, registry, InvoiceRequestBuilder::create, TaxRegimeCatalog.standard());
    }

    /** Constructor with every collaborator explicit, used by the tests. */
    public QuickComposeService(InvoiceService invoiceService, InvoiceTemplateRegistry registry,
                               Supplier<InvoiceRequestBuilder> builders, TaxRegimeCatalog catalog) {
        this.invoiceService = invoiceService;
        this.registry = registry;
        this.builders = builders;
        this.catalog = catalog;
    }

    public InvoiceResponse quickCompose(QuickComposeRequest request) {
        ComposeInvoiceRequest assembled = assemble(request);
        return request.isPersist() ? invoiceService.compose(assembled) : invoiceService.preview(assembled);
    }

    public List<TemplateSummary> listTemplates() {
        return registry.listCopies().stream()
                .map(t -> new TemplateSummary(t.getId(), t.getName(), t.getDescription(),
                        t.getRegime(), t.getRequest()))
                .toList();
    }

    public List<RegimeSummary> listRegimes() {
        return catalog.all().stream()
                .map(f -> new RegimeSummary(f.regime(), f.displayName(), f.defaultChain()))
                .toList();
    }

    private ComposeInvoiceRequest assemble(QuickComposeRequest request) {
        InvoiceRequestBuilder builder = builders.get();

        if (request.getTemplateId() != null && !request.getTemplateId().isBlank()) {
            InvoiceTemplate template = registry.cloneOf(request.getTemplateId());
            builder.from(template.getRequest());
        }

        if (request.getInvoiceNumber() != null) builder.invoiceNumber(request.getInvoiceNumber());
        if (request.getIssueDate() != null) builder.issueDate(request.getIssueDate());
        if (request.getSeller() != null) builder.seller(request.getSeller());
        if (request.getCustomer() != null) builder.customer(request.getCustomer());
        if (request.getItems() != null && !request.getItems().isEmpty()) builder.items(request.getItems());

        if (request.getRegime() != null) {
            TaxRegimeFactory factory = catalog.of(request.getRegime());
            builder.decorators(factory.defaultChain());
        }

        return builder.build();
    }
}
