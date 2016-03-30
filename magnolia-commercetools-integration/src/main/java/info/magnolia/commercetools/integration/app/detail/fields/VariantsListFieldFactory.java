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

import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.CompositeFieldDefinition;
import info.magnolia.ui.form.field.definition.Layout;
import info.magnolia.ui.form.field.factory.AbstractFieldFactory;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;
import info.magnolia.ui.form.field.transformer.Transformer;

import java.util.List;

import javax.inject.Inject;

import com.vaadin.data.Item;
import com.vaadin.data.util.PropertysetItem;
import com.vaadin.ui.Field;

/**
 * Field factory that builds {@link VariantsListField}.
 */
public class VariantsListFieldFactory extends AbstractFieldFactory<VariantsListFieldFactory.Definition, PropertysetItem> {

    private final FieldFactoryFactory fieldFactoryFactory;
    private final ComponentProvider componentProvider;
    private final I18NAuthoringSupport i18nAuthoringSupport;

    @Inject
    public VariantsListFieldFactory(Definition definition, Item relatedFieldItem, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, UiContext uiContext, I18NAuthoringSupport i18nAuthoringSupport) {
        super(definition, relatedFieldItem, uiContext, i18nAuthoringSupport);
        this.fieldFactoryFactory = fieldFactoryFactory;
        this.componentProvider = componentProvider;
        this.i18nAuthoringSupport = i18nAuthoringSupport;
    }

    @Override
    public Field<PropertysetItem> createField() {
        return super.createField();
    }

    @Override
    protected Field<PropertysetItem> createFieldComponent() {
        VariantsListField field = new VariantsListField(definition, fieldFactoryFactory, componentProvider, item, i18nAuthoringSupport);
        return field;
    }

    @Override
    protected Transformer<?> initializeTransformer(Class<? extends Transformer<?>> transformerClass) {
        List<String> propertyNames = definition.getFieldNames();
        Transformer<?> transformer = this.componentProvider.newInstance(transformerClass, item, definition, PropertysetItem.class, propertyNames);
        transformer.setLocale(getLocale());
        return transformer;
    }


    /**
     * Field definition for {@link VariantsListField}.
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
