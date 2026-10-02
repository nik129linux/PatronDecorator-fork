package com.invoicecomposer.creational.abstractfactory;

import com.invoicecomposer.dto.DecoratorConfigDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Concrete factory of the ordinary regime family: VAT-responsible taxpayer.
 *
 * Simplified demo of the Colombian regime rules, not tax advice.
 */
public final class OrdinaryRegimeFactory implements TaxRegimeFactory {

    @Override
    public TaxRegimeType regime() {
        return TaxRegimeType.ORDINARY;
    }

    @Override
    public String displayName() {
        return "VAT-responsible (ordinary regime)";
    }

    @Override
    public IndirectTaxLayer createIndirectTaxLayer() {
        return new OrdinaryIndirectTaxLayer();
    }

    @Override
    public WithholdingLayer createWithholdingLayer() {
        return new OrdinaryWithholdingLayer();
    }

    @Override
    public ComplianceLayer createComplianceLayer() {
        return new OrdinaryComplianceLayer();
    }

    /** Simplified demo, not tax advice: VAT 19% plus ICA 0.414%. */
    static final class OrdinaryIndirectTaxLayer implements IndirectTaxLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("VAT", Map.of("rate", 0.19)));
            decorators.add(new DecoratorConfigDto("ICA", Map.of("rate", 0.00414)));
            return decorators;
        }
    }

    /** Simplified demo, not tax advice: withholding (retefuente) of 2.5%. */
    static final class OrdinaryWithholdingLayer implements WithholdingLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("WITHHOLDING", Map.of("rate", 0.025)));
            return decorators;
        }
    }

    /** Simplified demo, not tax advice: sign first, then submit to the DIAN. */
    static final class OrdinaryComplianceLayer implements ComplianceLayer {

        @Override
        public List<DecoratorConfigDto> decorators() {
            List<DecoratorConfigDto> decorators = new ArrayList<>();
            decorators.add(new DecoratorConfigDto("DIGITAL_SIGNATURE", Map.of()));
            decorators.add(new DecoratorConfigDto("DIAN_SUBMISSION", Map.of()));
            return decorators;
        }
    }
}
