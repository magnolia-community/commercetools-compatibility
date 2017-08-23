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
package info.magnolia.commercetools.integration.app.detail.fields;

import info.magnolia.commercetools.integration.app.item.CommercetoolsProductItem;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.factory.FieldFactoryFactory;

import java.util.List;

import javax.inject.Inject;

import com.vaadin.data.Item;

import io.sphere.sdk.products.ProductVariant;

/**
 * Field that displays commercetools variants of product category.
 * MasterVariant as first.
 */
public class VariantsListField extends AbstractListField<ProductVariant> {

    @Inject
    public VariantsListField(Definition definition, FieldFactoryFactory fieldFactoryFactory, ComponentProvider componentProvider, Item relatedFieldItem, I18NAuthoringSupport i18nAuthoringSupport) {
        super(definition, fieldFactoryFactory, componentProvider, relatedFieldItem, i18nAuthoringSupport);
    }

    @Override
    protected List<ProductVariant> getBeans() {
        return ((CommercetoolsProductItem) relatedFieldItem).getBean().getAllVariants();
    }
}
