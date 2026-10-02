package com.invoicecomposer.creational.abstractfactory;

import com.invoicecomposer.dto.DecoratorConfigDto;

import java.util.List;

/**
 * Common shape of every product created by a {@link TaxRegimeFactory}: a group of decorator
 * configurations that the existing DecoratorFactory knows how to turn into real decorators.
 * May return an empty list when the regime has nothing to add in that area.
 */
public interface DecoratorLayer {

    List<DecoratorConfigDto> decorators();
}
