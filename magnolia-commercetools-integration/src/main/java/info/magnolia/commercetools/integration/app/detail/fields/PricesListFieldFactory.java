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
package info.magnolia.commercetools.integration.app.detail.fields;

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;

import javax.inject.Inject;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.Field;

/**
 * Field factory that builds {@link PricesListField}.
 */
public class PricesListFieldFactory extends AbstractFieldFactory<AbstractListField.Definition, PropertysetItem> {

    private final FieldFactoryFactory fieldFactoryFactory;
    private final ComponentProvider componentProvider;
    private final I18NAuthoringSupport i18nAuthoringSupport;

    @Inject
    public PricesListFieldFactory(AbstractListField.Definition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18NAuthoringSupport, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, I18NAuthoringSupport i18nAuthoringSupport) {
        super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
        this.fieldFactoryFactory = fieldFactoryFactory;
        this.componentProvider = componentProvider;
        this.i18nAuthoringSupport = i18nAuthoringSupport;
    }

    @Override
    protected Field<PropertysetItem> createFieldComponent() {
        return new PricesListField(definition, fieldFactoryFactory, componentProvider, item, i18nAuthoringSupport);
    }

    @Override
    protected Property<PropertysetItem> initializeProperty() {
        PropertysetItem items = new PropertysetItem();
        for (String fieldName : definition.getFieldNames()) {
            items.addItemProperty(fieldName, new ObjectProperty<>(item));
        }
        return new ObjectProperty<>(items, PropertysetItem.class);
    }

    /**
     * Field definition for {@link PricesListField}.
     */
    public static class Definition extends AbstractListField.Definition {
    }
}
