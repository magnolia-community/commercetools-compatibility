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

import static java.util.concurrent.TimeUnit.SECONDS;
import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.commercetools.integration.app.configuration.field.LanguageSelectFieldFactory.Definition;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.vaadin.data.Item;

/**
 * Language selection field which reads available languages from the CommerceTools project settings.<br/>
 */
public class LanguageSelectFieldFactory extends AbstractCommerceToolsFieldFactory<Definition> {

    @Inject
    public LanguageSelectFieldFactory(Definition definition, Item relatedFieldItem, Provider<CommerceToolsIntegrationModule> provider) {
        super(definition, relatedFieldItem, provider);
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> result = new ArrayList<SelectFieldOptionDefinition>();
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
            return new ArrayList<String>();
        }
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        Project project = client.executeBlocking(ProjectGet.of());
        return project.getLanguages();
    }

    /**
     * Definition for language select field.
     */
    public static class Definition extends SelectFieldDefinition {
    }
}

