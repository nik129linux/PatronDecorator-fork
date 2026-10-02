package com.invoicecomposer.decorator.factory;

import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.decorator.concrete.*;
import com.invoicecomposer.dto.DecoratorConfigDto;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Factory responsible for creating decorator instances from configuration DTOs.
 *
 * Uses a registry (Map-based lookup) to avoid large if/else chains in the controller.
 * This keeps the decorator creation logic centralized and easy to extend — adding a
 * new decorator only requires registering it here.
 */
public class DecoratorFactory {

    private static final Map<String, BiFunction<InvoiceComponent, DecoratorConfigDto, InvoiceComponent>> REGISTRY
            = new LinkedHashMap<>();

    static {
        REGISTRY.put("VAT", (component, config) -> {
            double rate = config.getDoubleParam("rate", 0.19);
            return new VatDecorator(component, rate);
        });

        REGISTRY.put("WITHHOLDING", (component, config) -> {
            double rate = config.getDoubleParam("rate", 0.025);
            return new WithholdingDecorator(component, rate);
        });

        REGISTRY.put("ICA", (component, config) -> {
            double rate = config.getDoubleParam("rate", 0.00414);
            return new IndustryAndCommerceTaxDecorator(component, rate);
        });

        REGISTRY.put("DISCOUNT", (component, config) -> {
            String discountType = config.getStringParam("discountType", "PERCENTAGE");
            if ("FIXED".equalsIgnoreCase(discountType)) {
                double amount = config.getDoubleParam("amount", 0);
                return new CommercialDiscountDecorator(component, amount, true);
            }
            double rate = config.getDoubleParam("rate", 0.10);
            return new CommercialDiscountDecorator(component, rate);
        });

        REGISTRY.put("CREDIT_NOTE", (component, config) -> {
            double amount = config.getDoubleParam("amount", 0);
            String reason = config.getStringParam("reason", "General adjustment");
            return new CreditNoteDecorator(component, amount, reason);
        });

        REGISTRY.put("DEBIT_NOTE", (component, config) -> {
            double amount = config.getDoubleParam("amount", 0);
            String reason = config.getStringParam("reason", "General adjustment");
            return new DebitNoteDecorator(component, amount, reason);
        });

        REGISTRY.put("DIGITAL_SIGNATURE", (component, config) ->
                new DigitalSignatureDecorator(component));

        REGISTRY.put("DIAN_SUBMISSION", (component, config) ->
                new DianSubmissionDecorator(component));

        REGISTRY.put("CUSTOMER_EMAIL", (component, config) -> {
            String email = config.getStringParam("email", "customer@example.com");
            return new CustomerEmailDecorator(component, email);
        });
    }

    /**
     * Creates a decorator wrapping the given component based on the configuration.
     *
     * @throws IllegalArgumentException if the decorator type is not registered
     */
    public static InvoiceComponent create(InvoiceComponent component, DecoratorConfigDto config) {
        BiFunction<InvoiceComponent, DecoratorConfigDto, InvoiceComponent> creator =
                REGISTRY.get(config.getType().toUpperCase());

        if (creator == null) {
            throw new IllegalArgumentException("Unknown decorator type: " + config.getType());
        }

        return creator.apply(component, config);
    }

    /**
     * Returns an unmodifiable list of all registered decorator type names.
     */
    public static java.util.List<String> getAvailableTypes() {
        return java.util.List.copyOf(REGISTRY.keySet());
    }
}
