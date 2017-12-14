/**
 * This file Copyright (c) 2016-2018 Magnolia International
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
 * Field factory that builds {@link VariantsListField}.
 */
public class VariantsListFieldFactory extends AbstractFieldFactory<AbstractListField.Definition, PropertysetItem> {

    private final FieldFactoryFactory fieldFactoryFactory;
    private final ComponentProvider componentProvider;
    private final I18NAuthoringSupport i18nAuthoringSupport;

    @Inject
    public VariantsListFieldFactory(AbstractListField.Definition definition, Item relatedFieldItem, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);
        this.fieldFactoryFactory = fieldFactoryFactory;
        this.componentProvider = componentProvider;
        this.i18nAuthoringSupport = i18nAuthoringSupport;
    }

    @Override
    protected Field<PropertysetItem> createFieldComponent() {
        return new VariantsListField(definition, fieldFactoryFactory, componentProvider, item, i18nAuthoringSupport);
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
     * Field definition for {@link VariantsListField}.
     */
    public static class Definition extends AbstractListField.Definition {
    }
}
