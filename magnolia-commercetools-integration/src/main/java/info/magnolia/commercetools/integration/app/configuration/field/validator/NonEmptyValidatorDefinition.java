/**
 * This file Copyright (c) 2016 Magnolia International
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
package info.magnolia.commercetools.integration.app.configuration.field.validator;

import info.magnolia.ui.form.validator.definition.ConfiguredFieldValidatorDefinition;

/**
 * Defines an validator of non-empty strings.
 *
 * @see info.magnolia.commercetools.integration.app.configuration.field.validator.NonEmptyFieldValidatorFactory
 */
public class NonEmptyValidatorDefinition extends ConfiguredFieldValidatorDefinition {

    public NonEmptyValidatorDefinition() {
        setFactoryClass(NonEmptyFieldValidatorFactory.class);
    }
}
