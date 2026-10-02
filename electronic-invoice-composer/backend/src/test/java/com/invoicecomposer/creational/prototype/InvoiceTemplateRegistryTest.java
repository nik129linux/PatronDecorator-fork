package com.invoicecomposer.creational.prototype;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.exception.InvoiceException;

import org.junit.jupiter.api.Test;

class InvoiceTemplateRegistryTest {

    private final InvoiceTemplateRegistry registry = new InvoiceTemplateRegistry();

    @Test
    void clonesAreIndependentFromTheStoredMaster() {
        InvoiceTemplate clone = registry.cloneOf("consulting-monthly");
        ComposeInvoiceRequest request = clone.getRequest();

        request.getInvoiceData().setInvoiceNumber("CHANGED");
        request.getInvoiceData().getCustomer().setName("Other customer");
        request.getInvoiceData().getSeller().setCity("Elsewhere");
        request.getInvoiceData().getItems().get(0).setQuantity(99);
        request.getDecorators().get(0).getParameters().put("rate", 0.5);
        request.getDecorators().clear();

        ComposeInvoiceRequest fresh = registry.cloneOf("consulting-monthly").getRequest();
        assertEquals("TPL-CONSULTING", fresh.getInvoiceData().getInvoiceNumber());
        assertEquals("Cliente Ejemplo S.A.S.", fresh.getInvoiceData().getCustomer().getName());
        assertEquals("Pasto", fresh.getInvoiceData().getSeller().getCity());
        assertEquals(1, fresh.getInvoiceData().getItems().get(0).getQuantity());
        assertEquals(4, fresh.getDecorators().size());
    }

    @Test
    void everyCloneIsANewObject() {
        assertNotSame(registry.cloneOf("retail-goods"), registry.cloneOf("retail-goods"));
    }

    @Test
    void listsAllSeedTemplatesInOrder() {
        assertEquals(
                java.util.List.of("consulting-monthly", "retail-goods", "small-business-simple"),
                registry.listCopies().stream().map(InvoiceTemplate::getId).toList());
    }

    @Test
    void unknownTemplateIsReported() {
        InvoiceException error = assertThrows(InvoiceException.class, () -> registry.cloneOf("nope"));
        assertEquals("TEMPLATE_NOT_FOUND", error.getCode());
    }
}
