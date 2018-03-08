/**
 * This file Copyright (c) 2016-2018 Magnolia International
 * Ltd.  (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made
 * available under the terms of the Magnolia Network Agreement
 * which accompanies this distribution, and is available at
 * http://www.magnolia-cms.com/mna.html
 *
 * Any modifications to this file must keep this entire header
 * intact.
 *
 */
package info.magnolia.commercetools.integration.app.detail.converters;

import java.util.Locale;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

import com.vaadin.v7.data.util.converter.Converter;

/**
 *  Converter for {@link MonetaryAmount}.
 */
public class MonetaryAmountConverter implements Converter<String, MonetaryAmount> {
    @Override
    public MonetaryAmount convertToModel(String value, Class<? extends MonetaryAmount> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(MonetaryAmount value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value != null) {
            return FastMoney.of(value.getNumber(), value.getCurrency()).toString();
        } else {
            return null;
        }
    }

    @Override
    public Class<MonetaryAmount> getModelType() {
        return MonetaryAmount.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
