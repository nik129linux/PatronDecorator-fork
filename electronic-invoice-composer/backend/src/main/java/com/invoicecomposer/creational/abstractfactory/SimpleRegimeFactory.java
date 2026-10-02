package com.invoicecomposer.creational.abstractfactory;

import com.invoicecomposer.dto.DecoratorConfigDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Concrete factory of the Simple Tax Regime (RST) family.
 *
 * Simplified demo of the Colombian regime rules, not tax advice.
 */
public final class SimpleRegimeFactory implements TaxRegimeFactory {

    @Override
    public TaxRegimeType regime() {
        return TaxRegimeType.SIMPLE;
    }

    @Override
    public String displayName() {
        return "Simple Tax Regime (RST)";
    }

    @Override
    public IndirectTaxLayer createIndirectTaxLayer() {
        return new SimpleIndirectTaxLayer();
    }

    @Override
    public WithholdingLayer createWithholdingLayer() {
        return new SimpleWithholdingLayer();
    }

    @Override
    public ComplianceLayer createComplianceLayer() {
        return new SimpleComplianceLayer();
    }

    /** Simplified demo, not tax advice: VAT only, ICA is consolidated in the Simple regime. */
    static final class SimpleIndirectTaxLayer implements IndirectTaxLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("VAT", Map.of("rate", 0.19)));
            return decorators;
        }
    }

    /** Simplified demo, not tax advice: this regime applies no withholdings. */
    static final class SimpleWithholdingLayer implements WithholdingLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            return new ArrayList<>();
        }
    }

    /** Simplified demo, not tax advice: sign first, then submit to the DIAN. */
    static final class SimpleComplianceLayer implements ComplianceLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("DIGITAL_SIGNATURE", Map.of()));
            decorators.add(new DecoratorConfigDto("DIAN_SUBMISSION", Map.of()));
            return decorators;
        }
    }
}
