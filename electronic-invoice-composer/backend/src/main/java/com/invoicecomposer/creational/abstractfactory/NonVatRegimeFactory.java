package com.invoicecomposer.creational.abstractfactory;

import com.invoicecomposer.dto.DecoratorConfigDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Concrete factory of the non-VAT regime family: taxpayer not responsible for VAT.
 *
 * Simplified demo of the Colombian regime rules, not tax advice.
 */
public final class NonVatRegimeFactory implements TaxRegimeFactory {

    @Override
    public TaxRegimeType regime() {
        return TaxRegimeType.NON_VAT;
    }

    @Override
    public String displayName() {
        return "Not responsible for VAT";
    }

    @Override
    public IndirectTaxLayer createIndirectTaxLayer() {
        return new NonVatIndirectTaxLayer();
    }

    @Override
    public WithholdingLayer createWithholdingLayer() {
        return new NonVatWithholdingLayer();
    }

    @Override
    public ComplianceLayer createComplianceLayer() {
        return new NonVatComplianceLayer();
    }

    /** Simplified demo, not tax advice: ICA only, there is no VAT to charge. */
    static final class NonVatIndirectTaxLayer implements IndirectTaxLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("ICA", Map.of("rate", 0.00414)));
            return decorators;
        }
    }

    /** Simplified demo, not tax advice: this regime applies no withholdings. */
    static final class NonVatWithholdingLayer implements WithholdingLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            return new ArrayList<>();
        }
    }

    /** Simplified demo, not tax advice: sign first, then submit to the DIAN. */
    static final class NonVatComplianceLayer implements ComplianceLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("DIGITAL_SIGNATURE", Map.of()));
            decorators.add(new DecoratorConfigDto("DIAN_SUBMISSION", Map.of()));
            return decorators;
        }
    }
}
