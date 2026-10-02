package com.invoicecomposer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.invoicecomposer.creational.abstractfactory.ComplianceLayer;
import com.invoicecomposer.creational.abstractfactory.IndirectTaxLayer;
import com.invoicecomposer.creational.abstractfactory.TaxRegimeFactory;
import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;
import com.invoicecomposer.creational.abstractfactory.WithholdingLayer;
import com.invoicecomposer.creational.builder.InvoiceRequestBuilder;
import com.invoicecomposer.creational.prototype.InvoiceTemplateRegistry;
import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.DecoratorConfigDto;
import com.invoicecomposer.dto.InvoiceResponse;
import com.invoicecomposer.dto.QuickComposeRequest;
import com.invoicecomposer.exception.InvoiceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;
import java.util.List;

/**
 * Tests the orchestration of the quick-compose feature with a fake builder and a fake catalog,
 * so they do not depend on the real Builder and Abstract Factory implementations.
 */
class QuickComposeServiceTest {

    private final InvoiceTemplateRegistry registry = new InvoiceTemplateRegistry();
    private InvoiceService invoiceService;
    private InvoiceRequestBuilder builder;
    private ComposeInvoiceRequest assembled;
    private InvoiceResponse response;
    private QuickComposeService service;
    private final List<DecoratorConfigDto> regimeChain =
            List.of(new DecoratorConfigDto("VAT", new HashMap<>()));

    @BeforeEach
    void setUp() {
        invoiceService = mock(InvoiceService.class);
        builder = mock(InvoiceRequestBuilder.class, Answers.RETURNS_SELF);
        assembled = new ComposeInvoiceRequest();
        response = new InvoiceResponse();
        when(builder.build()).thenReturn(assembled);
        when(invoiceService.preview(assembled)).thenReturn(response);
        when(invoiceService.compose(assembled)).thenReturn(response);

        TaxRegimeFactory factory = new TaxRegimeFactory() {
            public TaxRegimeType regime() { return TaxRegimeType.ORDINARY; }
            public String displayName() { return "Fake"; }
            public IndirectTaxLayer createIndirectTaxLayer() { return () -> regimeChain; }
            public WithholdingLayer createWithholdingLayer() { return List::of; }
            public ComplianceLayer createComplianceLayer() { return List::of; }
        };
        TaxRegimeCatalog catalog = new TaxRegimeCatalog() {
            public TaxRegimeFactory of(TaxRegimeType type) { return factory; }
            public List<TaxRegimeFactory> all() { return List.of(factory); }
        };
        service = new QuickComposeService(invoiceService, registry, () -> builder, catalog);
    }

    @Test
    void previewsByDefault() {
        InvoiceResponse result = service.quickCompose(new QuickComposeRequest());

        assertSame(response, result);
        verify(invoiceService).preview(assembled);
        verify(invoiceService, never()).compose(any());
    }

    @Test
    void persistsWhenAsked() {
        QuickComposeRequest request = new QuickComposeRequest();
        request.setPersist(true);

        service.quickCompose(request);

        verify(invoiceService).compose(assembled);
        verify(invoiceService, never()).preview(any());
    }

    @Test
    void loadsACloneOfTheTemplateIntoTheBuilder() {
        QuickComposeRequest request = new QuickComposeRequest();
        request.setTemplateId("retail-goods");

        service.quickCompose(request);

        ArgumentCaptor<ComposeInvoiceRequest> loaded = ArgumentCaptor.forClass(ComposeInvoiceRequest.class);
        verify(builder).from(loaded.capture());
        assertEquals("TPL-RETAIL", loaded.getValue().getInvoiceData().getInvoiceNumber());
    }

    @Test
    void appliesOnlyTheOverridesThatWereSent() {
        QuickComposeRequest request = new QuickComposeRequest();
        request.setInvoiceNumber("FE-100");
        Customer customer = new Customer("New customer", "123", null, null, null, null);
        request.setCustomer(customer);

        service.quickCompose(request);

        verify(builder).invoiceNumber("FE-100");
        verify(builder).customer(customer);
        verify(builder, never()).seller(any());
        verify(builder, never()).items(any());
        verify(builder, never()).decorators(any());
    }

    @Test
    void chosenRegimeReplacesTheDecoratorChain() {
        QuickComposeRequest request = new QuickComposeRequest();
        request.setRegime(TaxRegimeType.ORDINARY);

        service.quickCompose(request);

        verify(builder).decorators(regimeChain);
    }

    @Test
    void unknownTemplateFailsBeforeBuilding() {
        QuickComposeRequest request = new QuickComposeRequest();
        request.setTemplateId("nope");

        InvoiceException error = assertThrows(InvoiceException.class, () -> service.quickCompose(request));

        assertEquals("TEMPLATE_NOT_FOUND", error.getCode());
        verify(builder, never()).build();
    }

    @Test
    void listsTemplatesAndRegimes() {
        assertEquals(3, service.listTemplates().size());
        assertEquals(1, service.listRegimes().size());
        assertEquals(regimeChain, service.listRegimes().get(0).defaultDecorators());
    }
}
