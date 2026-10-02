package com.invoicecomposer.creational.prototype;

import com.invoicecomposer.creational.abstractfactory.TaxRegimeType;
import com.invoicecomposer.domain.Customer;
import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.Seller;
import com.invoicecomposer.dto.ComposeInvoiceRequest;
import com.invoicecomposer.dto.DecoratorConfigDto;
import com.invoicecomposer.exception.InvoiceException;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Prototype registry: keeps the master templates and only ever hands out deep copies,
 * so a caller can edit a clone freely without corrupting the stored model.
 */
@Component
public class InvoiceTemplateRegistry {

    private final Map<String, InvoiceTemplate> templates = new LinkedHashMap<>();

    public InvoiceTemplateRegistry() {
        register(consultingTemplate());
        register(retailTemplate());
        register(smallBusinessTemplate());
    }

    /** Stores a master template. */
    public void register(InvoiceTemplate template) {
        templates.put(template.getId(), template);
    }

    /** Returns an independent copy of the template, never the stored original. */
    public InvoiceTemplate cloneOf(String id) {
        InvoiceTemplate master = templates.get(id);
        if (master == null) {
            throw new InvoiceException("TEMPLATE_NOT_FOUND", "Template not found with ID: " + id);
        }
        return master.deepCopy();
    }

    /** Returns copies of every template, in registration order. */
    public List<InvoiceTemplate> listCopies() {
        List<InvoiceTemplate> copies = new ArrayList<>();
        templates.values().forEach(template -> copies.add(template.deepCopy()));
        return copies;
    }

    // --- Seed templates ---

    private InvoiceTemplate consultingTemplate() {
        InvoiceData data = new InvoiceData("TPL-CONSULTING", LocalDateTime.now(),
                new Customer("Cliente Ejemplo S.A.S.", "900123456-1", "facturas@cliente-ejemplo.co",
                        "Calle 10 # 5-20", "Pasto", "3000000000"),
                new Seller("Consultoria Andina S.A.S.", "901234567-8", "Carrera 25 # 18-40", "Pasto", "3010000000"),
                new ArrayList<>(List.of(new InvoiceItem("Monthly consulting services", 1, 4_500_000))));
        return new InvoiceTemplate("consulting-monthly", "Monthly consulting services",
                "Recurring service invoice for a VAT-responsible issuer", TaxRegimeType.ORDINARY,
                requestOf(data, config("VAT"), config("WITHHOLDING"),
                        config("DIGITAL_SIGNATURE"), config("DIAN_SUBMISSION")));
    }

    private InvoiceTemplate retailTemplate() {
        InvoiceData data = new InvoiceData("TPL-RETAIL", LocalDateTime.now(),
                new Customer("Tienda La Esquina", "800765432-3", "compras@laesquina.co",
                        "Calle 18 # 22-10", "Ipiales", "3020000000"),
                new Seller("Distribuidora del Sur", "802345678-5", "Avenida Panamericana # 12-30", "Pasto", "3030000000"),
                new ArrayList<>(List.of(new InvoiceItem("Case of handcrafted goods", 10, 85_000))));
        return new InvoiceTemplate("retail-goods", "Wholesale goods sale",
                "Goods invoice for an issuer that does not charge VAT", TaxRegimeType.NON_VAT,
                requestOf(data, config("ICA"), config("DIGITAL_SIGNATURE"), config("DIAN_SUBMISSION")));
    }

    private InvoiceTemplate smallBusinessTemplate() {
        InvoiceData data = new InvoiceData("TPL-SIMPLE", LocalDateTime.now(),
                new Customer("Panaderia Central", "900987654-2", "contabilidad@panaderiacentral.co",
                        "Carrera 4 # 9-15", "Pasto", "3040000000"),
                new Seller("Taller Artesanal Nariño", "1085123456-7", "Calle 20 # 30-12", "Pasto", "3050000000"),
                new ArrayList<>(List.of(new InvoiceItem("Custom lacquered pieces", 3, 120_000))));
        return new InvoiceTemplate("small-business-simple", "Small business sale",
                "Invoice for a taxpayer of the Simple Tax Regime (RST)", TaxRegimeType.SIMPLE,
                requestOf(data, config("VAT"), config("DIGITAL_SIGNATURE"), config("DIAN_SUBMISSION")));
    }

    private static DecoratorConfigDto config(String type) {
        return new DecoratorConfigDto(type, new java.util.HashMap<>());
    }

    private static ComposeInvoiceRequest requestOf(InvoiceData data, DecoratorConfigDto... decorators) {
        ComposeInvoiceRequest request = new ComposeInvoiceRequest();
        request.setInvoiceData(data);
        request.setDecorators(new ArrayList<>(List.of(decorators)));
        return request;
    }
}
