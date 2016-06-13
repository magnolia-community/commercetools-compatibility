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
package info.magnolia.commercetools.integration.app.detail.fields;

import info.magnolia.commercetools.integration.app.item.CommercetoolsProductItem;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.AbstractCustomMultiField;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.form.field.definition.Layout;
import info.magnolia.ui.form.field.factory.FieldFactory;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Item;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.server.Sizeable;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

import io.sphere.sdk.products.ProductVariant;

/**
 * Field that displays commercetools variants of product category.
 * MasterVariant as first.
 */
public class VariantsListField extends AbstractCustomMultiField<VariantsListFieldFactory.Definition, PropertysetItem> {

    @Inject
    public VariantsListField(VariantsListFieldFactory.Definition definition, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, Item relatedFieldItem, I18NAuthoringSupport i18nAuthoringSupport) {
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
        for (ProductVariant productVariant : ((CommercetoolsProductItem) relatedFieldItem).getBean().getAllVariants()) {
            AbstractOrderedLayout categoryFieldRoot = new VerticalLayout();

            for (ConfiguredFieldDefinition fieldDefinition : definition.getFields()) {
                Field<?> categoryField = createLocalField(fieldDefinition, productVariant, false);
                if (fieldValues.getItemProperty(fieldDefinition.getName()) == null) {
                    fieldValues.addItemProperty(fieldDefinition.getName(), categoryField.getPropertyDataSource());
                }
                categoryField.setWidth(100, Sizeable.Unit.PERCENTAGE);

                categoryFieldRoot.addComponent(categoryField);
            }
            root.addComponent(categoryFieldRoot);
        }
    }

    @Override
    public Class<? extends PropertysetItem> getType() {
        return PropertysetItem.class;
    }

    /**
     * Create a new {@link Field} based on a {@link FieldDefinition}.
     */
    protected Field<?> createLocalField(FieldDefinition fieldDefinition, ProductVariant productVariant, boolean setCaptionToNull) {
        FieldFactory fieldfactory = fieldFactoryFactory.createFieldFactory(fieldDefinition, new BeanItem(productVariant));
        fieldfactory.setComponentProvider(componentProvider);

        Field<?> field = fieldfactory.createField();

        if (field instanceof AbstractComponent) {
            ((AbstractComponent) field).setImmediate(true);
        }
        // Set Caption if desired
        if (setCaptionToNull) {
            field.setCaption(null);
        } else if (StringUtils.isBlank(field.getCaption()) && StringUtils.isNotBlank(fieldDefinition.getLabel())) {
            field.setCaption(fieldDefinition.getLabel());
        }

        field.setWidth(100, Sizeable.Unit.PERCENTAGE);

        // propagate locale to complex fields further down, in case they have i18n-aware fields
        if (field instanceof AbstractCustomMultiField) {
            ((AbstractCustomMultiField) field).setLocale(getLocale());
        }

        // Set read only based on the single field definition
        field.setReadOnly(fieldDefinition.isReadOnly());

        return field;
    }
}
