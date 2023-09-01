/*
 * This file Copyright (c) 2017-2018 Magnolia International Ltd.
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
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.AbstractCustomMultiField;
import info.magnolia.ui.form.field.definition.CompositeFieldDefinition;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.form.field.definition.Layout;
import info.magnolia.ui.form.field.factory.FieldFactory;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.data.util.PropertysetItem;
import com.vaadin.v7.ui.Field;

/**
 * Base implementation for the list based fields.
 *
 * @param <B> bean type
 */
public abstract class AbstractListField<B> extends AbstractCustomMultiField<AbstractListField.Definition, PropertysetItem> {

    @Inject
    public AbstractListField(Definition definition, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, Item relatedFieldItem, I18NAuthoringSupport i18nAuthoringSupport) {
        super(definition, fieldFactoryFactory, componentProvider, relatedFieldItem, i18nAuthoringSupport);
    }

    @Override
    protected Component initContent() {
        // Init root layout
        if (definition.getLayout() == Layout.horizontal) {
            root = new HorizontalLayout();
        } else {
            root = new VerticalLayout();
        }

        // Initialize Existing field
        initFields();
        return root;
    }

    @Override
    protected void initFields(PropertysetItem fieldValues) {
        root.removeAllComponents();
        for (B bean : getBeans()) {
            AbstractOrderedLayout categoryFieldRoot = new VerticalLayout();

            for (ConfiguredFieldDefinition fieldDefinition : definition.getFields()) {
                Field<?> categoryField = createLocalField(fieldDefinition, bean, false);
                if (fieldValues.getItemProperty(fieldDefinition.getName()) == null) {
                    fieldValues.addItemProperty(fieldDefinition.getName(), categoryField.getPropertyDataSource());
                }
                categoryField.setWidth(100, Unit.PERCENTAGE);

                categoryFieldRoot.addComponent(categoryField);
            }
            root.addComponent(categoryFieldRoot);
        }
    }

    protected abstract List<B> getBeans();


    @Override
    public Class<? extends PropertysetItem> getType() {
        return PropertysetItem.class;
    }

    /**
     * Create a new {@link Field} based on a {@link FieldDefinition}.
     */
    protected Field<?> createLocalField(FieldDefinition fieldDefinition, B bean, boolean setCaptionToNull) {
        FieldFactory fieldfactory = fieldFactoryFactory.createFieldFactory(fieldDefinition, new BeanItem<>(bean));
        fieldfactory.setComponentProvider(componentProvider);

        Field<?> field = fieldfactory.createField();

        // Set Caption if desired
        if (setCaptionToNull) {
            field.setCaption(null);
        } else if (StringUtils.isBlank(field.getCaption()) && StringUtils.isNotBlank(fieldDefinition.getLabel())) {
            field.setCaption(fieldDefinition.getLabel());
        }

        field.setWidth(100, Unit.PERCENTAGE);

        // propagate locale to complex fields further down, in case they have i18n-aware fields
        if (field instanceof AbstractCustomMultiField) {
            ((AbstractCustomMultiField) field).setLocale(getLocale());
        }

        // Set read only based on the single field definition
        field.setReadOnly(fieldDefinition.isReadOnly());

        return field;
    }

    /**
     * Field definition.
     */
    public static class Definition extends CompositeFieldDefinition {

        private Layout layout = Layout.vertical;

        @Override
        public Layout getLayout() {
            return layout;
        }

        @Override
        public void setLayout(Layout layout) {
            this.layout = layout;
        }
    }

}
