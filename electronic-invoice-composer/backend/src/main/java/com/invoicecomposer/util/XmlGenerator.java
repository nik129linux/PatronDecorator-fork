package com.invoicecomposer.util;

import com.invoicecomposer.decorator.component.InvoiceComponent;
import com.invoicecomposer.domain.InvoiceData;
import com.invoicecomposer.domain.InvoiceItem;
import com.invoicecomposer.domain.LineDetail;

import java.util.List;
import java.util.Map;

/**
 * Utility that generates a simulated electronic invoice XML document.
 *
 * ACADEMIC SIMULATION: The XML does NOT conform to the official DIAN UBL 2.1
 * schema. It is a simplified representation for demonstration purposes.
 */
public final class XmlGenerator {

    private XmlGenerator() {
    }

    public static String generate(InvoiceComponent component, InvoiceData invoiceData) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<!-- ACADEMIC SIMULATION — Not a legally valid DIAN electronic invoice -->\n");
        xml.append("<ElectronicInvoice xmlns=\"urn:academic:invoice:simulation:1.0\">\n");

        // Header
        xml.append("  <Header>\n");
        xml.append("    <InvoiceNumber>").append(escape(invoiceData.getInvoiceNumber())).append("</InvoiceNumber>\n");
        xml.append("    <IssueDate>").append(invoiceData.getIssueDate()).append("</IssueDate>\n");
        xml.append("    <DocumentType>").append(escape(String.valueOf(component.getMetadata().getOrDefault("documentType", "INVOICE")))).append("</DocumentType>\n");
        xml.append("  </Header>\n");

        // Seller
        xml.append("  <Seller>\n");
        xml.append("    <Name>").append(escape(invoiceData.getSeller().getName())).append("</Name>\n");
        xml.append("    <NIT>").append(escape(invoiceData.getSeller().getNit())).append("</NIT>\n");
        xml.append("    <Address>").append(escape(invoiceData.getSeller().getAddress())).append("</Address>\n");
        xml.append("    <City>").append(escape(invoiceData.getSeller().getCity())).append("</City>\n");
        xml.append("  </Seller>\n");

        // Customer
        xml.append("  <Customer>\n");
        xml.append("    <Name>").append(escape(invoiceData.getCustomer().getName())).append("</Name>\n");
        xml.append("    <NIT>").append(escape(invoiceData.getCustomer().getNit())).append("</NIT>\n");
        xml.append("    <Email>").append(escape(invoiceData.getCustomer().getEmail())).append("</Email>\n");
        xml.append("    <Address>").append(escape(invoiceData.getCustomer().getAddress())).append("</Address>\n");
        xml.append("    <City>").append(escape(invoiceData.getCustomer().getCity())).append("</City>\n");
        xml.append("  </Customer>\n");

        // Items
        xml.append("  <Items>\n");
        for (InvoiceItem item : invoiceData.getItems()) {
            xml.append("    <Item>\n");
            xml.append("      <Description>").append(escape(item.getDescription())).append("</Description>\n");
            xml.append("      <Quantity>").append(item.getQuantity()).append("</Quantity>\n");
            xml.append("      <UnitPrice>").append(String.format("%.2f", item.getUnitPrice())).append("</UnitPrice>\n");
            xml.append("      <LineTotal>").append(String.format("%.2f", item.getLineTotal())).append("</LineTotal>\n");
            xml.append("    </Item>\n");
        }
        xml.append("  </Items>\n");

        // Financial breakdown
        List<LineDetail> breakdown = component.getBreakdown();
        xml.append("  <FinancialBreakdown>\n");
        xml.append("    <Subtotal>").append(String.format("%.2f", component.getSubtotal())).append("</Subtotal>\n");
        xml.append("    <TaxableBase>").append(String.format("%.2f", component.getTaxableBase())).append("</TaxableBase>\n");

        xml.append("    <Adjustments>\n");
        for (LineDetail detail : breakdown) {
            if (!"ITEM".equals(detail.getType()) && !"SUBTOTAL".equals(detail.getType())) {
                xml.append("      <Adjustment>\n");
                xml.append("        <Type>").append(escape(detail.getType())).append("</Type>\n");
                xml.append("        <Label>").append(escape(detail.getLabel())).append("</Label>\n");
                xml.append("        <Amount>").append(String.format("%.2f", detail.getAmount())).append("</Amount>\n");
                if (detail.getRate() > 0) {
                    xml.append("        <Rate>").append(String.format("%.4f", detail.getRate())).append("</Rate>\n");
                }
                xml.append("      </Adjustment>\n");
            }
        }
        xml.append("    </Adjustments>\n");

        xml.append("    <Total>").append(String.format("%.2f", component.calculateTotal())).append("</Total>\n");
        xml.append("  </FinancialBreakdown>\n");

        // Metadata
        Map<String, Object> metadata = component.getMetadata();
        xml.append("  <Metadata>\n");
        if (metadata.containsKey("signatureHash")) {
            xml.append("    <Signature>\n");
            xml.append("      <Hash>").append(escape(String.valueOf(metadata.get("signatureHash")))).append("</Hash>\n");
            xml.append("      <Type>SIMULATED</Type>\n");
            xml.append("    </Signature>\n");
        }
        if (metadata.containsKey("dianTrackingId")) {
            xml.append("    <DianSubmission>\n");
            xml.append("      <TrackingId>").append(escape(String.valueOf(metadata.get("dianTrackingId")))).append("</TrackingId>\n");
            xml.append("      <Status>").append(escape(String.valueOf(metadata.get("dianStatus")))).append("</Status>\n");
            xml.append("    </DianSubmission>\n");
        }
        xml.append("  </Metadata>\n");

        xml.append("</ElectronicInvoice>\n");
        return xml.toString();
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
