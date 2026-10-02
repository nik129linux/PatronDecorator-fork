package com.invoicecomposer.decorator;

import com.invoicecomposer.decorator.component.BasicInvoice;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.decorator.concrete.*;
import com.invoicecomposer.decorator.factory.DecoratorFactory;
import com.invoicecomposer.domain.*;
import com.invoicecomposer.dto.DecoratorConfigDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests demonstrating the Decorator Pattern.
 *
 * Each test proves that decorators add responsibilities to the base invoice
 * without modifying the original BasicInvoice object.
 */
class DecoratorPatternTest {

    private InvoiceData sampleInvoiceData;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer("Empresa ABC", "900123456-7",
                "compras@empresaabc.com", "Calle 100 #15-20", "Bogotá", "601-555-0100");
        Seller seller = new Seller("Tech Solutions SAS", "800987654-3",
                "Carrera 7 #45-10", "Bogotá", "601-555-0200");
        List<InvoiceItem> items = List.of(
                new InvoiceItem("Laptop", 2, 3500000),
                new InvoiceItem("Monitor", 1, 1200000)
        );
        sampleInvoiceData = new InvoiceData("FE-001", LocalDateTime.now(),
                customer, seller, items);
    }

    // =====================================================================
    // 1. Basic invoice calculation
    // =====================================================================

    @Test
    @DisplayName("1. BasicInvoice calculates subtotal correctly")
    void basicInvoiceCalculation() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);

        assertEquals(8200000, invoice.getSubtotal(), 0.01);
        assertEquals(8200000, invoice.calculateTotal(), 0.01);
        assertEquals(8200000, invoice.getTaxableBase(), 0.01);
        assertFalse(invoice.getBreakdown().isEmpty());
    }

    // =====================================================================
    // 2. Invoice + VAT
    // =====================================================================

    @Test
    @DisplayName("2. VatDecorator adds VAT to the invoice total")
    void invoicePlusVat() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent withVat = new VatDecorator(invoice, 0.19);

        double expectedVat = 8200000 * 0.19;
        assertEquals(8200000 + expectedVat, withVat.calculateTotal(), 0.01);
        assertEquals(8200000, withVat.getSubtotal(), 0.01);
        assertTrue(withVat.getMetadata().containsKey("vatApplied"));
    }

    // =====================================================================
    // 3. Invoice + VAT + Withholding
    // =====================================================================

    @Test
    @DisplayName("3. VAT + Withholding decorators compose correctly")
    void invoicePlusVatPlusWithholding() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent withVat = new VatDecorator(invoice, 0.19);
        InvoiceComponent withWithholding = new WithholdingDecorator(withVat, 0.025);

        double vatAmount = 8200000 * 0.19;
        double withholdingAmount = 8200000 * 0.025;
        double expected = 8200000 + vatAmount - withholdingAmount;

        assertEquals(expected, withWithholding.calculateTotal(), 0.01);
        assertEquals(8200000, withWithholding.getSubtotal(), 0.01);
        assertTrue(withWithholding.getMetadata().containsKey("vatApplied"));
        assertTrue(withWithholding.getMetadata().containsKey("withholdingApplied"));
    }

    // =====================================================================
    // 4. Invoice + Discount + VAT
    // =====================================================================

    @Test
    @DisplayName("4. Discount reduces taxable base before VAT calculation")
    void invoicePlusDiscountPlusVat() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent withDiscount = new CommercialDiscountDecorator(invoice, 0.10);
        InvoiceComponent withVat = new VatDecorator(withDiscount, 0.19);

        double discountedBase = 8200000 * 0.90;
        double vatAmount = discountedBase * 0.19;
        double expected = discountedBase + vatAmount;

        assertEquals(discountedBase, withVat.getTaxableBase(), 0.01);
        assertEquals(expected, withVat.calculateTotal(), 0.01);
    }

    // =====================================================================
    // 5. DigitalSignature decorator
    // =====================================================================

    @Test
    @DisplayName("5. DigitalSignature adds metadata without changing total")
    void signatureDecorator() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent signed = new DigitalSignatureDecorator(invoice);

        assertEquals(invoice.calculateTotal(), signed.calculateTotal(), 0.01);
        assertTrue(signed.getMetadata().containsKey("signed"));
        assertTrue(signed.getMetadata().containsKey("signatureHash"));
        assertEquals("SIMULATED", signed.getMetadata().get("signatureType"));
    }

    // =====================================================================
    // 6. DIAN submission decorator
    // =====================================================================

    @Test
    @DisplayName("6. DianSubmission adds tracking metadata without changing total")
    void dianSubmissionDecorator() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent submitted = new DianSubmissionDecorator(invoice);

        assertEquals(invoice.calculateTotal(), submitted.calculateTotal(), 0.01);
        assertTrue(submitted.getMetadata().containsKey("dianSubmitted"));
        assertTrue(submitted.getMetadata().containsKey("dianTrackingId"));
        assertEquals("ACCEPTED", submitted.getMetadata().get("dianStatus"));
    }

    // =====================================================================
    // 7. CustomerEmail notification decorator
    // =====================================================================

    @Test
    @DisplayName("7. CustomerEmail adds email metadata without changing total")
    void customerEmailDecorator() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent emailed = new CustomerEmailDecorator(invoice, "test@example.com");

        assertEquals(invoice.calculateTotal(), emailed.calculateTotal(), 0.01);
        assertTrue(emailed.getMetadata().containsKey("emailSent"));
        assertEquals("test@example.com", emailed.getMetadata().get("recipientEmail"));
    }

    // =====================================================================
    // 8. Full decorator chain (all decorators combined)
    // =====================================================================

    @Test
    @DisplayName("8. Full decorator chain combines all responsibilities")
    void fullDecoratorChain() {
        InvoiceComponent component = new BasicInvoice(sampleInvoiceData);
        component = new CommercialDiscountDecorator(component, 0.10);
        component = new VatDecorator(component, 0.19);
        component = new WithholdingDecorator(component, 0.025);
        component = new IndustryAndCommerceTaxDecorator(component, 0.00414);
        component = new DigitalSignatureDecorator(component);
        component = new DianSubmissionDecorator(component);
        component = new CustomerEmailDecorator(component, "compras@empresaabc.com");

        double discountedBase = 8200000 * 0.90;
        double vatAmount = discountedBase * 0.19;
        double withholdingAmount = discountedBase * 0.025;
        double icaAmount = discountedBase * 0.00414;
        double expected = discountedBase + vatAmount - withholdingAmount - icaAmount;

        assertEquals(expected, component.calculateTotal(), 0.01);
        assertEquals(8200000, component.getSubtotal(), 0.01);

        Map<String, Object> metadata = component.getMetadata();
        assertTrue(metadata.containsKey("discountApplied"));
        assertTrue(metadata.containsKey("vatApplied"));
        assertTrue(metadata.containsKey("withholdingApplied"));
        assertTrue(metadata.containsKey("icaApplied"));
        assertTrue(metadata.containsKey("signed"));
        assertTrue(metadata.containsKey("dianSubmitted"));
        assertTrue(metadata.containsKey("emailSent"));

        // Verify breakdown has entries for every decorator
        List<LineDetail> breakdown = component.getBreakdown();
        assertTrue(breakdown.stream().anyMatch(d -> "DISCOUNT".equals(d.getType())));
        assertTrue(breakdown.stream().anyMatch(d -> "TAX_VAT".equals(d.getType())));
        assertTrue(breakdown.stream().anyMatch(d -> "TAX_WITHHOLDING".equals(d.getType())));
        assertTrue(breakdown.stream().anyMatch(d -> "TAX_ICA".equals(d.getType())));
        assertTrue(breakdown.stream().anyMatch(d -> "SIGNATURE".equals(d.getType())));
        assertTrue(breakdown.stream().anyMatch(d -> "DIAN_SUBMISSION".equals(d.getType())));
        assertTrue(breakdown.stream().anyMatch(d -> "EMAIL_NOTIFICATION".equals(d.getType())));
    }

    // =====================================================================
    // 9. Invalid decorator configuration
    // =====================================================================

    @Test
    @DisplayName("9. Unknown decorator type throws IllegalArgumentException")
    void invalidDecoratorType() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        DecoratorConfigDto invalidConfig = new DecoratorConfigDto("UNKNOWN", Map.of());

        assertThrows(IllegalArgumentException.class,
                () -> DecoratorFactory.create(invoice, invalidConfig));
    }

    // =====================================================================
    // 10. Decorator chain preserves original BasicInvoice
    // =====================================================================

    @Test
    @DisplayName("10. Decorators do not modify the original BasicInvoice")
    void decoratorChainPreservation() {
        BasicInvoice originalInvoice = new BasicInvoice(sampleInvoiceData);
        double originalTotal = originalInvoice.calculateTotal();
        int originalBreakdownSize = originalInvoice.getBreakdown().size();

        // Build a decorated chain (does NOT modify originalInvoice)
        InvoiceComponent decorated = new VatDecorator(originalInvoice, 0.19);
        decorated = new WithholdingDecorator(decorated, 0.025);
        decorated = new DigitalSignatureDecorator(decorated);

        // Original is untouched
        assertEquals(originalTotal, originalInvoice.calculateTotal(), 0.01);
        assertEquals(originalBreakdownSize, originalInvoice.getBreakdown().size());

        // Decorated chain has different values
        assertNotEquals(originalTotal, decorated.calculateTotal(), 0.01);
        assertTrue(decorated.getBreakdown().size() > originalBreakdownSize);
    }

    // =====================================================================
    // 11. Credit and Debit note decorators
    // =====================================================================

    @Test
    @DisplayName("11. CreditNote reduces and DebitNote increases the total")
    void creditAndDebitNotes() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        double baseTotal = invoice.calculateTotal();

        InvoiceComponent withCredit = new CreditNoteDecorator(invoice, 500000, "Returned item");
        assertEquals(baseTotal - 500000, withCredit.calculateTotal(), 0.01);

        InvoiceComponent withDebit = new DebitNoteDecorator(invoice, 200000, "Late fee");
        assertEquals(baseTotal + 200000, withDebit.calculateTotal(), 0.01);
    }

    // =====================================================================
    // 12. ICA decorator
    // =====================================================================

    @Test
    @DisplayName("12. ICA decorator subtracts industry and commerce tax")
    void icaDecorator() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);
        InvoiceComponent withIca = new IndustryAndCommerceTaxDecorator(invoice, 0.00414);

        double icaAmount = 8200000 * 0.00414;
        assertEquals(8200000 - icaAmount, withIca.calculateTotal(), 0.01);
        assertTrue(withIca.getMetadata().containsKey("icaApplied"));
    }

    // =====================================================================
    // 13. DecoratorFactory creates correct types
    // =====================================================================

    @Test
    @DisplayName("13. DecoratorFactory creates decorators from config DTOs")
    void factoryCreatesDecorators() {
        InvoiceComponent invoice = new BasicInvoice(sampleInvoiceData);

        DecoratorConfigDto vatConfig = new DecoratorConfigDto("VAT", Map.of("rate", 0.19));
        InvoiceComponent result = DecoratorFactory.create(invoice, vatConfig);

        assertTrue(result instanceof VatDecorator);
        assertEquals(8200000 * 1.19, result.calculateTotal(), 0.01);
    }
}
