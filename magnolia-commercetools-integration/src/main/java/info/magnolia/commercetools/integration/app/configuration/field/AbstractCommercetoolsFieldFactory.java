/**
 * This file Copyright (c) 2016-2017 Magnolia International
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

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import info.magnolia.ui.form.field.factory.SelectFieldFactory;

import java.util.Iterator;

import javax.inject.Inject;
import javax.inject.Provider;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;

/**
 * Abstract select field for commercetools fields.<br/>
 * @param <T> the definition of field extending SelectFieldDefinition.
 */
public abstract class AbstractCommercetoolsFieldFactory<T extends SelectFieldDefinition> extends SelectFieldFactory<T> {

    public static final String PROJECT_SELECT_PROPERTY_NAME = "ctProject";
    public static final String SITE_SELECT_PROPERTY_NAME = "site";

    private final Provider<CommercetoolsIntegrationModule> provider;
    private final CommercetoolsServices services;

    @Inject
    public AbstractCommercetoolsFieldFactory(T definition, Item relatedFieldItem, Provider<CommercetoolsIntegrationModule> provider, CommercetoolsServices services) {
        super(definition, relatedFieldItem);
        this.provider = provider;
        this.services = services;
    }

    protected String getSelectedProject() {
        Property<?> property = item.getItemProperty(PROJECT_SELECT_PROPERTY_NAME);
        if (property != null && property.getValue() != null) {
            return (String) property.getValue();
        }

        Iterator<String> projects = getProvider().get().getProjects().keySet().iterator();

        return projects.hasNext() ? projects.next() : null;
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

    protected Provider<CommercetoolsIntegrationModule> getProvider() {
        return provider;
    }

    protected CommercetoolsServices getServices() {
        return services;
    }
}

