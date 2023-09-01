/*
 * This file Copyright (c) 2016-2018 Magnolia International Ltd.
 * (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made available under
 * the terms of the MIT License which accompanies this distribution, and
 * is available at https://opensource.org/license/mit
 *
 */
package info.magnolia.commercetools.integration.app.configuration.field.validator;

import info.magnolia.ui.form.validator.factory.AbstractFieldValidatorFactory;

import com.vaadin.v7.data.Validator;
import com.vaadin.v7.data.validator.StringLengthValidator;

/**
 * Build a {@link StringLengthValidator}.
 */
public class NonEmptyFieldValidatorFactory extends AbstractFieldValidatorFactory<NonEmptyValidatorDefinition> {

    public NonEmptyFieldValidatorFactory(NonEmptyValidatorDefinition definition) {
        super(definition);
    }

    @Override
    public Validator createValidator() {
        return new StringLengthValidator(getI18nErrorMessage(), 1, -1, false);
    }

}
