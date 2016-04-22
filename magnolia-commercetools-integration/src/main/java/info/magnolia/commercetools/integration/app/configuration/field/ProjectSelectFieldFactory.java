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
import info.magnolia.commercetools.integration.CommerceToolsProjectConfiguration;
import info.magnolia.commercetools.integration.app.configuration.event.ProjectChangedEvent;
import info.magnolia.commercetools.integration.app.configuration.field.ProjectSelectFieldFactory.Definition;
import info.magnolia.commercetools.integration.service.CommerceToolsServices;
import info.magnolia.event.EventBus;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;

/**
 * Factory that constructs select field with CommerceTools projects.
 */
public class ProjectSelectFieldFactory extends AbstractCommerceToolsFieldFactory<Definition> {

    private final EventBus eventBus;
    private final Property.ValueChangeListener listener = new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            String value = (String) event.getProperty().getValue();
            if (StringUtils.isNotBlank(value))
                eventBus.fireEvent(new ProjectChangedEvent(value));
        }
    };

    @Inject
    public ProjectSelectFieldFactory(Definition definition, Item relatedFieldItem, Provider<CommerceToolsIntegrationModule> provider, @Named(SubAppEventBus.NAME) EventBus eventBus, CommerceToolsServices services) {
        super(definition, relatedFieldItem, provider, services);
        this.eventBus = eventBus;
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> result = new ArrayList<SelectFieldOptionDefinition>();
        result.add(createEmptyOption());
        Map<String, CommerceToolsProjectConfiguration> projects = getProvider().get().getProjects();
        for (Map.Entry<String, CommerceToolsProjectConfiguration> entry : projects.entrySet()) {
            String key = entry.getKey();
            SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
            optionDefinition.setValue(key);
            optionDefinition.setLabel(key);
            optionDefinition.setName(key);
            result.add(optionDefinition);
        }
        return result;
    }

    @Override
    public void setPropertyDataSourceAndDefaultValue(Property<?> property) {
        field.removeValueChangeListener(listener);
        super.setPropertyDataSourceAndDefaultValue(property);
        field.addValueChangeListener(listener);
    }

    @Override
    protected AbstractSelect createFieldComponent() {
        AbstractSelect abstractSelect = super.createFieldComponent();
        abstractSelect.addValueChangeListener(listener);
        select.setNullSelectionAllowed(false);
        return abstractSelect;
    }

    /**
     * Definition for project select field.
     */
    public static class Definition extends SelectFieldDefinition {
    }
}
