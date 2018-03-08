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

import java.util.ArrayList;

import javax.inject.Inject;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.ui.Field;

/**
 * Field factory that builds {@link CommercetoolsImageField}.
 */
public class CommercetoolsImageFieldFactory extends AbstractFieldFactory<CommercetoolsImageFieldDefinition, ArrayList> {

    private ComponentProvider componentProvider;

    @Inject
    public CommercetoolsImageFieldFactory(CommercetoolsImageFieldDefinition definition, Item relatedFieldItem, UiContext uiContext, I18NAuthoringSupport i18NAuthoringSupport, ComponentProvider componentProvider) {
        super(definition, relatedFieldItem, uiContext, i18NAuthoringSupport);
        this.componentProvider = componentProvider;
    }

    @Override
    protected Field<ArrayList> createFieldComponent() {
        return new CommercetoolsImageField(definition, componentProvider, item);
    }
}
