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

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.app.configuration.event.SiteChangedEvent;
import info.magnolia.commercetools.integration.app.configuration.field.SiteSelectFieldFactory.Definition;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.event.EventBus;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.SiteManager;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.ui.AbstractSelect;

/**
 * Selection field which reads options from the {@link SiteManager}.<br/>
 * Event {@link SiteChangedEvent} is fired when value in the select field changes.
 */
public class SiteSelectFieldFactory extends AbstractCommercetoolsFieldFactory<Definition> {

    private final SiteManager siteManager;
    private final EventBus eventBus;
    private final Property.ValueChangeListener listener = new Property.ValueChangeListener() {
        @Override
        public void valueChange(Property.ValueChangeEvent event) {
            String value = event.getProperty().getValue().toString();
            if (StringUtils.isNotBlank(value))
                eventBus.fireEvent(new SiteChangedEvent(value));
        }
    };

    @Inject
    public SiteSelectFieldFactory(Definition definition, Item relatedFieldItem, Provider<CommercetoolsIntegrationModule> provider, @Named(SubAppEventBus.NAME) EventBus eventBus, SiteManager siteManager, CommercetoolsServices services) {
        super(definition, relatedFieldItem, provider, services);
        this.siteManager = siteManager;
        this.eventBus = eventBus;
    }

    @Override
    public List<SelectFieldOptionDefinition> getSelectFieldOptionDefinition() {
        List<SelectFieldOptionDefinition> result = new ArrayList<SelectFieldOptionDefinition>();
        for (Site site : siteManager.getSites()) {
            SelectFieldOptionDefinition optionDefinition = new SelectFieldOptionDefinition();
            optionDefinition.setName(site.getName());
            optionDefinition.setValue(site.getName());
            optionDefinition.setLabel(site.getName());
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
        abstractSelect.setNullSelectionAllowed(false);
        return abstractSelect;
    }

    /**
     * Definition for site select field.
     */
    public static class Definition extends SelectFieldDefinition {
    }
}
