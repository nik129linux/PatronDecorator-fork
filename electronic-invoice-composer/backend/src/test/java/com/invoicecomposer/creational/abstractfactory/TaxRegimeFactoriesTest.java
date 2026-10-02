package com.invoicecomposer.creational.abstractfactory;

import com.invoicecomposer.decorator.component.BasicInvoice;
import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.decorator.factory.DecoratorFactory;
import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.Seller;
import com.invoicecomposer.dto.DecoratorConfigDto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests demonstrating the Abstract Factory pattern.
 *
 * Focus: every factory returns the exact family of decorator chains of its regime,
 * and those chains are accepted by the existing DecoratorFactory.
 */
class TaxRegimeFactoriesTest {

    private InvoiceData sampleData;

    @BeforeEach
    void setUp() {
        Customer customer = new Customer("Empresa ABC", "900123456-7",
                "compras@empresaabc.com", "Calle 100 #15-20", "Bogotá", "601-555-0100");
        Seller seller = new Seller("Tech Solutions SAS", "800987654-3",
                "Carrera 7 #45-10", "Bogotá", "601-555-0200");
        sampleData = new InvoiceData("FE-001", LocalDateTime.now(),
                customer, seller, List.of(new InvoiceItem("Laptop", 2, 3500000)));
    }

    private static List<String> types(List<DecoratorConfigDto> chain) {
        return chain.stream().map(DecoratorConfigDto::getType).toList();
    }

    private static double rateOf(DecoratorConfigDto config) {
        return config.getDoubleParam("rate", -1);
    }

    // =====================================================================
    // 1. Exact chain of each factory
    // =====================================================================

    @Test
    @DisplayName("1. Ordinary regime: VAT 19% + ICA 0.414%, withholding 2.5%, signature and DIAN")
    void ordinaryChain() {
        TaxRegimeFactory factory = TaxRegimeFactories.of(TaxRegimeType.ORDINARY);
        List<DecoratorConfigDto> chain = factory.defaultChain();

        assertEquals(List.of("VAT", "ICA", "WITHHOLDING", "DIGITAL_SIGNATURE", "DIAN_SUBMISSION"),
                types(chain));
        assertEquals(0.19, rateOf(chain.get(0)));
        assertEquals(0.00414, rateOf(chain.get(1)));
        assertEquals(0.025, rateOf(chain.get(2)));
        assertEquals("VAT-responsible (ordinary regime)", factory.displayName());
    }

    @Test
    @DisplayName("2. Non-VAT regime: ICA only, no withholding, signature and DIAN")
    void nonVatChain() {
        TaxRegimeFactory factory = TaxRegimeFactories.of(TaxRegimeType.NON_VAT);
        List<DecoratorConfigDto> chain = factory.defaultChain();

        assertEquals(List.of("ICA", "DIGITAL_SIGNATURE", "DIAN_SUBMISSION"), types(chain));
        assertEquals(0.00414, rateOf(chain.get(0)));
        assertTrue(factory.createWithholdingLayer().decorators().isEmpty());
        assertEquals("Not responsible for VAT", factory.displayName());
    }

    @Test
    @DisplayName("3. Simple regime: VAT only (ICA consolidated), no withholding, signature and DIAN")
    void simpleChain() {
        TaxRegimeFactory factory = TaxRegimeFactories.of(TaxRegimeType.SIMPLE);
        List<DecoratorConfigDto> chain = factory.defaultChain();

        assertEquals(List.of("VAT", "DIGITAL_SIGNATURE", "DIAN_SUBMISSION"), types(chain));
        assertEquals(0.19, rateOf(chain.get(0)));
        assertTrue(factory.createWithholdingLayer().decorators().isEmpty());
        assertEquals("Simple Tax Regime (RST)", factory.displayName());
    }

    // =====================================================================
    // 2. Structure of the chains
    // =====================================================================

    @Test
    @DisplayName("4. defaultChain() orders taxes, then withholdings, then compliance")
    void defaultChainOrder() {
        for (TaxRegimeFactory factory : TaxRegimeFactories.all()) {
            List<DecoratorConfigDto> expected = new ArrayList<>();
            expected.addAll(factory.createIndirectTaxLayer().decorators());
            expected.addAll(factory.createWithholdingLayer().decorators());
            expected.addAll(factory.createComplianceLayer().decorators());

            assertEquals(types(expected), types(factory.defaultChain()),
                    "Wrong order for " + factory.regime());

            List<String> chain = types(factory.defaultChain());
            int lastTax = Math.max(chain.lastIndexOf("VAT"), chain.lastIndexOf("ICA"));
            int withholding = chain.lastIndexOf("WITHHOLDING");
            int firstCompliance = Math.min(chain.indexOf("DIGITAL_SIGNATURE"),
                    chain.indexOf("DIAN_SUBMISSION"));

            if (withholding >= 0) {
                assertTrue(lastTax < withholding, "Taxes must wrap before withholdings");
                assertTrue(withholding < firstCompliance, "Withholdings must wrap before compliance");
            } else {
                assertTrue(lastTax < firstCompliance, "Taxes must wrap before compliance");
            }
            assertEquals(chain.indexOf("DIGITAL_SIGNATURE") + 1, chain.indexOf("DIAN_SUBMISSION"),
                    "The DIAN submission must wrap after the digital signature");
        }
    }

    @Test
    @DisplayName("5. Every factory returns a fresh list on each call")
    void freshListEachCall() {
        for (TaxRegimeFactory factory : TaxRegimeFactories.all()) {
            List<DecoratorConfigDto> first = factory.defaultChain();
            List<DecoratorConfigDto> second = factory.defaultChain();

            assertNotSame(first, second);
            first.clear();
            assertFalse(second.isEmpty(), "Chains must not be shared between calls for " + factory.regime());
        }
    }

    // =====================================================================
    // 3. Lookup
    // =====================================================================

    @Test
    @DisplayName("6. all() returns three entries in enum order, unmodifiable")
    void allReturnsEveryRegimeInEnumOrder() {
        List<TaxRegimeFactory> all = TaxRegimeFactories.all();

        assertEquals(3, all.size());
        TaxRegimeType[] values = TaxRegimeType.values();
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], all.get(i).regime());
        }
        assertThrows(UnsupportedOperationException.class, all::clear);
    }

    @Test
    @DisplayName("7. of() returns the same factory for the same regime")
    void ofReturnsSharedInstances() {
        for (TaxRegimeType type : TaxRegimeType.values()) {
            assertSame(TaxRegimeFactories.of(type), TaxRegimeFactories.of(type));
        }
        assertSame(TaxRegimeFactories.of(TaxRegimeType.ORDINARY), TaxRegimeFactories.all().get(0));
    }

    @Test
    @DisplayName("8. The three products of one factory belong to the same regime family")
    void productsOfOneFactoryBelongToTheSameFamily() {
        for (TaxRegimeFactory factory : TaxRegimeFactories.all()) {
            Class<?> factoryClass = factory.getClass();

            assertEquals(factoryClass, factory.createIndirectTaxLayer().getClass().getEnclosingClass(),
                    "Indirect tax product of another family");
            assertEquals(factoryClass, factory.createWithholdingLayer().getClass().getEnclosingClass(),
                    "Withholding product of another family");
            assertEquals(factoryClass, factory.createComplianceLayer().getClass().getEnclosingClass(),
                    "Compliance product of another family");
        }
    }

    // =====================================================================
    // 4. Integration with the existing DecoratorFactory
    // =====================================================================

    @Test
    @DisplayName("9. Every type produced is accepted by DecoratorFactory over a real BasicInvoice")
    void chainsAreAcceptedByTheDecoratorFactory() {
        InvoiceComponent base = new BasicInvoice(sampleData);

        for (TaxRegimeFactory factory : TaxRegimeFactories.all()) {
            InvoiceComponent component = base;

            for (DecoratorConfigDto config : factory.defaultChain()) {
                assertTrue(DecoratorFactory.getAvailableTypes().contains(config.getType()),
                        "Unknown decorator type: " + config.getType());
                component = DecoratorFactory.create(component, config);
            }

            assertTrue(component.calculateTotal() > 0);
            assertTrue(component.getBreakdown().size() > base.getBreakdown().size());
        }
    }

    @Test
    @DisplayName("10. The ordinary chain charges VAT and subtracts withholding and ICA")
    void ordinaryChainCalculatesExpectedTotal() {
        List<DecoratorConfigDto> chain = TaxRegimeFactories.of(TaxRegimeType.ORDINARY).defaultChain();

        InvoiceComponent component = new BasicInvoice(sampleData);
        for (DecoratorConfigDto config : chain) {
            component = DecoratorFactory.create(component, config);
        }

        double base = new BasicInvoice(sampleData).calculateTotal();
        double expected = base + base * 0.19 - base * 0.025 - base * 0.00414;
        assertEquals(expected, component.calculateTotal(), 0.01);
        assertEquals(base, component.getSubtotal(), 0.01);
    }
}
