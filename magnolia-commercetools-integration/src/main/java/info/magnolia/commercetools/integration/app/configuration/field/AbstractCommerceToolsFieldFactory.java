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
package info.magnolia.commercetools.integration.app.configuration.field;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;

import javax.inject.Inject;
import javax.inject.Provider;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;

/**
 * Abstract select field for CommerceTools fields.<br/>
 * @param <T> the definition of field extending SelectFieldDefinition.
 */
public abstract class AbstractCommerceToolsFieldFactory<T extends SelectFieldDefinition> extends SelectFieldFactory<T> {

    public static final String PROJECT_SELECT_PROPERTY_NAME = "ctProject";
    public static final String SITE_SELECT_PROPERTY_NAME = "site";

    private final Provider<CommerceToolsIntegrationModule> provider;

    @Inject
    public AbstractCommerceToolsFieldFactory(T definition, Item relatedFieldItem, Provider<CommerceToolsIntegrationModule> provider) {
        super(definition, relatedFieldItem);
        this.provider = provider;
    }

    protected String getSelectedProject() {
        Property<?> property = item.getItemProperty(PROJECT_SELECT_PROPERTY_NAME);
        if (property != null && property.getValue() != null) {
            return (String) property.getValue();
        }
        return getProvider().get().getProjects().keySet().iterator().next();
    }

    @Override
    protected AbstractSelect createFieldComponent() {
        select = super.createFieldComponent();
        select.setNullSelectionAllowed(true);
        select.setInvalidAllowed(true);
        return select;
    }

    protected SelectFieldOptionDefinition createEmptyOption() {
        SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
        optionDefinition.setName("");
        optionDefinition.setValue("");
        optionDefinition.setLabel("");
        return optionDefinition;
    }

    protected Provider<CommerceToolsIntegrationModule> getProvider() {
        return provider;
    }
}

