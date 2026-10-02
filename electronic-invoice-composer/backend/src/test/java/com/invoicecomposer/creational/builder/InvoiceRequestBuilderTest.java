package com.invoicecomposer.creational.builder;

import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.Seller;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.DecoratorConfigDto;
import com.invoicecomposer.exception.InvoiceException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests demonstrating the Builder pattern.
 *
 * Focus: fluent assembly, defensive copies (Prototype-style) and aggregated validation.
 */
class InvoiceRequestBuilderTest {

    private Seller seller;
    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("Empresa ABC", "900123456-7",
                "compras@empresaabc.com", "Calle 100 #15-20", "Bogotá", "601-555-0100");
        seller = new Seller("Tech Solutions SAS", "800987654-3",
                "Carrera 7 #45-10", "Bogotá", "601-555-0200");
    }

    private InvoiceRequestBuilder validBuilder() {
        return InvoiceRequestBuilder.create()
                .invoiceNumber("FE-001")
                .issueDate(LocalDateTime.of(2026, 3, 10, 9, 30))
                .seller(seller)
                .customer(customer)
                .addItem("Laptop", 2, 3500000)
                .addItem("Monitor", 1, 1200000);
    }

    private ComposeInvoiceRequest validBase() {
        ComposeInvoiceRequest base = new ComposeInvoiceRequest();
        base.setInvoiceData(new InvoiceData("FE-BASE", LocalDateTime.of(2026, 1, 15, 8, 0),
                customer, seller, List.of(new InvoiceItem("Laptop", 2, 3500000))));
        base.setDecorators(new ArrayList<>(List.of(
                new DecoratorConfigDto("VAT", Map.of("rate", 0.19)),
                new DecoratorConfigDto("DIGITAL_SIGNATURE", Map.of()))));
        return base;
    }

    // =====================================================================
    // 1. Happy path
    // =====================================================================

    @Test
    @DisplayName("1. Build assembles every field of a valid request")
    void happyPath() {
        ComposeInvoiceRequest request = validBuilder()
                .addDecorator("VAT", Map.of("rate", 0.19))
                .addDecorator("DIGITAL_SIGNATURE", Map.of())
                .build();

        InvoiceData data = request.getInvoiceData();
        assertEquals("FE-001", data.getInvoiceNumber());
        assertEquals(LocalDateTime.of(2026, 3, 10, 9, 30), data.getIssueDate());
        assertEquals("Tech Solutions SAS", data.getSeller().getName());
        assertEquals("Empresa ABC", data.getCustomer().getName());
        assertEquals(2, data.getItems().size());
        assertEquals("Laptop", data.getItems().get(0).getDescription());
        assertEquals(8200000, data.getItems().stream().mapToDouble(InvoiceItem::getLineTotal).sum(), 0.01);

        assertEquals(List.of("VAT", "DIGITAL_SIGNATURE"),
                request.getDecorators().stream().map(DecoratorConfigDto::getType).toList());
        assertEquals(0.19, request.getDecorators().get(0).getDoubleParam("rate", -1));
    }

    @Test
    @DisplayName("2. Every setter is fluent and returns the same builder")
    void fluentSettersReturnThis() {
        InvoiceRequestBuilder builder = InvoiceRequestBuilder.create();
        assertSame(builder, builder.invoiceNumber("FE-001"));
        assertSame(builder, builder.issueDate(LocalDateTime.now()));
        assertSame(builder, builder.seller(seller));
        assertSame(builder, builder.customer(customer));
        assertSame(builder, builder.addItem("Laptop", 1, 1000));
        assertSame(builder, builder.items(List.of(new InvoiceItem("Laptop", 1, 1000))));
        assertSame(builder, builder.addDecorator("VAT", Map.of("rate", 0.19)));
        assertSame(builder, builder.decorators(List.of(new DecoratorConfigDto("VAT", Map.of()))));
    }

    @Test
    @DisplayName("3. items() and decorators() replace, addItem() and addDecorator() append")
    void appendVersusReplace() {
        ComposeInvoiceRequest request = InvoiceRequestBuilder.create()
                .invoiceNumber("FE-002")
                .seller(seller)
                .customer(customer)
                .addItem("Laptop", 1, 1000)
                .addItem("Mouse", 1, 200)
                .items(List.of(new InvoiceItem("Keyboard", 3, 300)))
                .addDecorator("VAT", Map.of("rate", 0.19))
                .decorators(List.of(new DecoratorConfigDto("ICA", Map.of("rate", 0.00414))))
                .addDecorator("WITHHOLDING", Map.of("rate", 0.025))
                .build();

        assertEquals(1, request.getInvoiceData().getItems().size());
        assertEquals("Keyboard", request.getInvoiceData().getItems().get(0).getDescription());
        assertEquals(List.of("ICA", "WITHHOLDING"),
                request.getDecorators().stream().map(DecoratorConfigDto::getType).toList());
    }

    // =====================================================================
    // 2. Defaults
    // =====================================================================

    @Test
    @DisplayName("4. issueDate defaults to now when it was not set")
    void defaultsIssueDate() {
        LocalDateTime before = LocalDateTime.now().minusSeconds(5);
        ComposeInvoiceRequest request = InvoiceRequestBuilder.create()
                .invoiceNumber("FE-003")
                .seller(seller)
                .customer(customer)
                .addItem("Laptop", 1, 1000)
                .build();
        LocalDateTime after = LocalDateTime.now().plusSeconds(5);

        assertNotNull(request.getInvoiceData().getIssueDate());
        assertFalse(request.getInvoiceData().getIssueDate().isBefore(before));
        assertFalse(request.getInvoiceData().getIssueDate().isAfter(after));
    }

    // =====================================================================
    // 3. from(): defensive copy
    // =====================================================================

    @Test
    @DisplayName("5. from() carries over data and decorators without mutating the base")
    void fromCarriesOverWithoutMutatingBase() {
        ComposeInvoiceRequest base = validBase();

        ComposeInvoiceRequest request = InvoiceRequestBuilder.create()
                .from(base)
                .invoiceNumber("FE-OVERRIDE")
                .addItem("Mouse", 1, 50000)
                .build();

        // The base request is untouched.
        assertEquals("FE-BASE", base.getInvoiceData().getInvoiceNumber());
        assertEquals(LocalDateTime.of(2026, 1, 15, 8, 0), base.getInvoiceData().getIssueDate());
        assertEquals(1, base.getInvoiceData().getItems().size());
        assertEquals(2, base.getDecorators().size());
        assertEquals("VAT", base.getDecorators().get(0).getType());

        // Everything carried over: date, seller, customer, decorators, and the original item.
        assertEquals(LocalDateTime.of(2026, 1, 15, 8, 0), request.getInvoiceData().getIssueDate());
        assertEquals("Tech Solutions SAS", request.getInvoiceData().getSeller().getName());
        assertEquals("Empresa ABC", request.getInvoiceData().getCustomer().getName());
        assertEquals(List.of("VAT", "DIGITAL_SIGNATURE"),
                request.getDecorators().stream().map(DecoratorConfigDto::getType).toList());
        assertEquals(2, request.getInvoiceData().getItems().size());

        // The override replaced the invoice number.
        assertEquals("FE-OVERRIDE", request.getInvoiceData().getInvoiceNumber());
    }

    @Test
    @DisplayName("6. from() copies deeply: later changes to the base do not reach the builder")
    void fromCopiesDeeply() {
        ComposeInvoiceRequest base = validBase();
        InvoiceRequestBuilder builder = InvoiceRequestBuilder.create().from(base);

        base.setInvoiceData(new InvoiceData("FE-HACK", LocalDateTime.now(),
                customer, seller, new ArrayList<>()));
        base.getDecorators().get(0).setType("HACKED");

        ComposeInvoiceRequest request = builder.build();

        assertEquals("FE-BASE", request.getInvoiceData().getInvoiceNumber());
        assertEquals(1, request.getInvoiceData().getItems().size());
        assertEquals(List.of("VAT", "DIGITAL_SIGNATURE"),
                request.getDecorators().stream().map(DecoratorConfigDto::getType).toList());
        assertNotSame(base.getDecorators().get(0), request.getDecorators().get(0));
        assertNotSame(base.getInvoiceData().getSeller(), request.getInvoiceData().getSeller());
    }

    // =====================================================================
    // 4. Validation: one rule at a time
    // =====================================================================

    @Test
    @DisplayName("7. invoiceNumber is required")
    void invoiceNumberIsRequired() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                InvoiceRequestBuilder.create()
                        .seller(seller)
                        .customer(customer)
                        .addItem("Laptop", 1, 1000)
                        .build());

        assertEquals("INVALID_INVOICE", error.getCode());
        assertEquals("invoiceNumber is required", error.getMessage());
    }

    @Test
    @DisplayName("8. seller is required")
    void sellerIsRequired() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                InvoiceRequestBuilder.create()
                        .invoiceNumber("FE-001")
                        .customer(customer)
                        .addItem("Laptop", 1, 1000)
                        .build());

        assertEquals("seller is required", error.getMessage());
    }

    @Test
    @DisplayName("9. customer is required")
    void customerIsRequired() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                InvoiceRequestBuilder.create()
                        .invoiceNumber("FE-001")
                        .seller(seller)
                        .addItem("Laptop", 1, 1000)
                        .build());

        assertEquals("customer is required", error.getMessage());
    }

    @Test
    @DisplayName("10. At least one item is required")
    void atLeastOneItemIsRequired() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                InvoiceRequestBuilder.create()
                        .invoiceNumber("FE-001")
                        .seller(seller)
                        .customer(customer)
                        .build());

        assertEquals("at least one item is required", error.getMessage());
    }

    @Test
    @DisplayName("11. Every item needs quantity >= 1")
    void quantityMustBeAtLeastOne() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                validBuilder().items(List.of(new InvoiceItem("Laptop", 0, 1000))).build());

        assertEquals("items[0].quantity must be at least 1", error.getMessage());
    }

    @Test
    @DisplayName("12. Every item needs unitPrice >= 0")
    void unitPriceMustBePositiveOrZero() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                validBuilder().items(List.of(new InvoiceItem("Laptop", 1, -1000))).build());

        assertEquals("items[0].unitPrice must be zero or positive", error.getMessage());
    }

    @Test
    @DisplayName("13. A zero unit price is accepted")
    void zeroUnitPriceIsAccepted() {
        ComposeInvoiceRequest request = validBuilder()
                .items(List.of(new InvoiceItem("Gift", 1, 0)))
                .build();

        assertEquals(0, request.getInvoiceData().getItems().get(0).getUnitPrice());
    }

    @Test
    @DisplayName("14. Every decorator type must not be blank")
    void decoratorTypeMustNotBeBlank() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                validBuilder().addDecorator("   ", Map.of()).build());

        assertEquals("decorators[0].type must not be blank", error.getMessage());
    }

    // =====================================================================
    // 5. Aggregated errors
    // =====================================================================

    @Test
    @DisplayName("15. The message lists every problem, separated by '; '")
    void listsAllProblemsInOneMessage() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                InvoiceRequestBuilder.create()
                        .addDecorator("  ", Map.of())
                        .items(List.of(new InvoiceItem("Laptop", 0, -5)))
                        .build());

        assertEquals("INVALID_INVOICE", error.getCode());
        assertEquals("invoiceNumber is required; seller is required; customer is required; "
                        + "items[0].quantity must be at least 1; "
                        + "items[0].unitPrice must be zero or positive; "
                        + "decorators[0].type must not be blank",
                error.getMessage());
    }

    @Test
    @DisplayName("16. The example message of the specification is reproduced")
    void specificationExampleMessage() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                InvoiceRequestBuilder.create()
                        .seller(seller)
                        .customer(customer)
                        .build());

        assertEquals("invoiceNumber is required; at least one item is required", error.getMessage());
    }

    @Test
    @DisplayName("17. Problems of several items are reported with their index")
    void reportsEveryInvalidItem() {
        InvoiceException error = assertThrows(InvoiceException.class, () ->
                validBuilder()
                        .items(List.of(
                                new InvoiceItem("Laptop", 1, 1000),
                                new InvoiceItem("Mouse", 0, -1)))
                        .build());

        assertEquals("items[1].quantity must be at least 1; "
                + "items[1].unitPrice must be zero or positive", error.getMessage());
    }

    // =====================================================================
    // 6. build() twice
    // =====================================================================

    @Test
    @DisplayName("18. Building twice returns two independent requests")
    void buildingTwiceGivesIndependentObjects() {
        InvoiceRequestBuilder builder = validBuilder()
                .addDecorator("VAT", Map.of("rate", 0.19));

        ComposeInvoiceRequest first = builder.build();
        ComposeInvoiceRequest second = builder.build();

        assertNotSame(first, second);
        assertNotSame(first.getInvoiceData(), second.getInvoiceData());
        assertNotSame(first.getInvoiceData().getSeller(), second.getInvoiceData().getSeller());
        assertNotSame(first.getInvoiceData().getCustomer(), second.getInvoiceData().getCustomer());
        assertNotSame(first.getInvoiceData().getItems().get(0), second.getInvoiceData().getItems().get(0));
        assertNotSame(first.getDecorators().get(0), second.getDecorators().get(0));

        // Mutating the first result never reaches the second one.
        first.getInvoiceData().setInvoiceNumber("FE-MUTATED");
        first.getInvoiceData().getSeller().setName("Changed SAS");
        first.getInvoiceData().getItems().get(0).setQuantity(99);
        first.getDecorators().get(0).getParameters().put("rate", 0.5);

        assertEquals("FE-001", second.getInvoiceData().getInvoiceNumber());
        assertEquals("Tech Solutions SAS", second.getInvoiceData().getSeller().getName());
        assertEquals(2, second.getInvoiceData().getItems().get(0).getQuantity());
        assertEquals(0.19, second.getDecorators().get(0).getDoubleParam("rate", -1));
    }

    @Test
    @DisplayName("19. The built request never shares objects with the caller's inputs")
    void builtRequestDoesNotShareObjectsWithCaller() {
        List<InvoiceItem> inputItems = new ArrayList<>(List.of(new InvoiceItem("Laptop", 2, 1000)));
        List<DecoratorConfigDto> inputDecorators = new ArrayList<>(
                List.of(new DecoratorConfigDto("VAT", Map.of("rate", 0.19))));

        ComposeInvoiceRequest request = InvoiceRequestBuilder.create()
                .invoiceNumber("FE-004")
                .seller(seller)
                .customer(customer)
                .items(inputItems)
                .decorators(inputDecorators)
                .build();

        inputItems.get(0).setQuantity(99);
        inputDecorators.get(0).setType("HACKED");

        assertEquals(2, request.getInvoiceData().getItems().get(0).getQuantity());
        assertEquals("VAT", request.getDecorators().get(0).getType());
        assertNotSame(inputItems.get(0), request.getInvoiceData().getItems().get(0));
        assertNotSame(inputDecorators.get(0), request.getDecorators().get(0));
    }
}
