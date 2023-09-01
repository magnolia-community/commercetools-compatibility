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
package info.magnolia.commercetools.integration.app.configuration.field;

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.app.configuration.event.SiteChangedEvent;
import info.magnolia.commercetools.integration.app.configuration.field.SiteSelectFieldFactory.Definition;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.event.EventBus;
import info.magnolia.module.site.Site;
import info.magnolia.module.site.SiteManager;
import info.magnolia.objectfactory.Components;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.SelectFieldDefinition;
import info.magnolia.ui.form.field.definition.SelectFieldOptionDefinition;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.ui.AbstractSelect;

/**
 * Selection field which reads options from the {@link SiteManager}.<br/>
 * Event {@link SiteChangedEvent} is fired when value in the select field changes.
 */
public class SiteSelectFieldFactory extends AbstractCommercetoolsFieldFactory<Definition> {

    private final SiteManager siteManager;
    private EventBus eventBus;
    private final Property.ValueChangeListener listener = event -> {
        String value = event.getProperty().getValue().toString();
        if (StringUtils.isNotBlank(value) && eventBus != null)
            eventBus.fireEvent(new SiteChangedEvent(value));
    };

    @Inject
    public SiteSelectFieldFactory(Definition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport, Provider<CommercetoolsIntegrationModule> provider, @Named(SubAppEventBus.NAME) EventBus eventBus, SiteManager siteManager, CommercetoolsServices services) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport, provider, services);
        this.siteManager = siteManager;
        this.eventBus = eventBus;
    }

    /**
     * @deprecated since 1.2, use {@link #SiteSelectFieldFactory(Definition, Item, UiContext, I18NAuthoringSupport, Provider, EventBus, SiteManager, CommercetoolsServices)} instead.
     */
    @Deprecated
    public SiteSelectFieldFactory(Definition definition, Item relatedFieldItem, Provider<CommercetoolsIntegrationModule> provider, @Named(SubAppEventBus.NAME) EventBus eventBus, SiteManager siteManager, CommercetoolsServices services) {
        this(definition, relatedFieldItem, Components.getComponent(UiContext.class), Components.getComponent(I18NAuthoringSupport.class), provider, eventBus, siteManager, services);
    }

    @Override
    public List<SelectFieldOptionDefinition> getOptions() {
        List<SelectFieldOptionDefinition> result = new ArrayList<>();
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
    public void setPropertyDataSourceAndDefaultValue(Property property) {
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
