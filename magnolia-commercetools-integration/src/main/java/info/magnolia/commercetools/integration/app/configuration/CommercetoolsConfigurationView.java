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
package info.magnolia.commercetools.integration.app.configuration;

import info.magnolia.ui.api.view.View;
import info.magnolia.ui.form.EditorValidator;
import info.magnolia.ui.vaadin.form.FormViewReduced;

import com.vaadin.v7.data.Item;

/**
 * View interface for commercetools configuration app.
 */
public interface CommercetoolsConfigurationView extends View, EditorValidator {

    void setListener(Listener listener);

    void setFormViewReduced(FormViewReduced formViewReduced);

    Item getItemDataSource();

    /**
     * Listener.
     */
    interface Listener {

        void save();

        void reset();
    }

}
