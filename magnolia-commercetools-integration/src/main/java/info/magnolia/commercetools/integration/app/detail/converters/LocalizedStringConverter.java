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

import com.vaadin.v7.data.util.converter.Converter;

import io.sphere.sdk.models.LocalizedString;

/**
 * Converter for {@link LocalizedString}.
 */
public class LocalizedStringConverter implements Converter<String, LocalizedString> {
    @Override
    public LocalizedString convertToModel(String value, Class<? extends LocalizedString> targetType, Locale locale) throws ConversionException {
        return null;
    }

    @Override
    public String convertToPresentation(LocalizedString value, Class<? extends String> targetType, Locale locale) throws ConversionException {
        if (value != null && value.getLocales().contains(locale)) {
            return value.get(locale);
        } else {
            return null;
        }
    }

    @Override
    public Class<LocalizedString> getModelType() {
        return LocalizedString.class;
    }

    @Override
    public Class<String> getPresentationType() {
        return String.class;
    }
}
