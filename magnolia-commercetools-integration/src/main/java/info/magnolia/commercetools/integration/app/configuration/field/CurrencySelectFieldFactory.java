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
import info.magnolia.commercetools.integration.app.configuration.field.CurrencySelectFieldFactory.Definition;
import info.magnolia.commercetools.integration.service.CommerceToolsServices;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.vaadin.data.Item;

import io.sphere.sdk.client.SphereClient;

/**
 * Currency selection field which reads available currencies from the CommerceTools project settings.<br/>
 */
public class CurrencySelectFieldFactory extends AbstractCommerceToolsFieldFactory<Definition> {

    @Inject
    public CurrencySelectFieldFactory(Definition definition, Item relatedFieldItem, Provider<CommerceToolsIntegrationModule> provider, CommerceToolsServices services) {
        super(definition, relatedFieldItem, provider, services);
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> result = new ArrayList<SelectFieldOptionDefinition>();
        for (String currencyCode : getCurrencies(getProvider().get().getSphereClient(getSelectedProject()))) {
            SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
            optionDefinition.setName(currencyCode);
            optionDefinition.setValue(currencyCode);
            optionDefinition.setLabel(currencyCode);
            result.add(optionDefinition);
        }
        return result;
    }

    private List<String> getCurrencies(SphereClient pureAsyncClient) {
        if (pureAsyncClient == null) {
            return new ArrayList<String>();
        }
        return getServices().getProjectDetail(pureAsyncClient).getCurrencies();
    }

    /**
     * Definition for currency select field.
     */
    public static class Definition extends SelectFieldDefinition {
    }
}

