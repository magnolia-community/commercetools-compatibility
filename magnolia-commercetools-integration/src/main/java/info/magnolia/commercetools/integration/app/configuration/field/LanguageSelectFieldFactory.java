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
import info.magnolia.commercetools.integration.app.configuration.field.LanguageSelectFieldFactory.Definition;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.vaadin.data.Item;

import io.sphere.sdk.client.SphereClient;

/**
 * Language selection field which reads available languages from the commercetools project settings.<br/>
 */
public class LanguageSelectFieldFactory extends AbstractCommercetoolsFieldFactory<Definition> {

    @Inject
    public LanguageSelectFieldFactory(Definition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, Provider<CommercetoolsIntegrationModule> provider, CommercetoolsServices services) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport, provider, services);
    }

    /**
     * @deprecated since 1.2, use {@link #LanguageSelectFieldFactory(Definition, Item, UiContext, I18NAuthoringSupport, Provider, CommercetoolsServices)} instead.
     */
    @Deprecated
    public LanguageSelectFieldFactory(Definition definition, Item relatedFieldItem, Provider<CommercetoolsIntegrationModule> provider, CommercetoolsServices services) {
        this(definition, relatedFieldItem, Components.getComponent(UiContext.class), Components.getComponent(I18NAuthoringSupport.class), provider, services);
    }

    @Override
    public List<SelectFieldOptionDefinition> getOptions() {
        List<SelectFieldOptionDefinition> result = new ArrayList<>();
        for (String languageCode : getLanguages(getProvider().get().getSphereClient(getSelectedProject()))) {
            SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
            optionDefinition.setName(languageCode);
            optionDefinition.setValue(languageCode);
            optionDefinition.setLabel(languageCode);
            result.add(optionDefinition);
        }
        return result;
    }

    private List<String> getLanguages(SphereClient pureAsyncClient) {
        if (pureAsyncClient == null) {
            return new ArrayList<>();
        }
        return getServices().getProjectDetail(pureAsyncClient).getLanguages();
    }

    /**
     * Definition for language select field.
     */
    public static class Definition extends SelectFieldDefinition {
    }
}

