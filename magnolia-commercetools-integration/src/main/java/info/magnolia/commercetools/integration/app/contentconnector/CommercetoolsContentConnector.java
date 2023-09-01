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
package info.magnolia.commercetools.integration.app.contentconnector;

import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import com.vaadin.v7.data.Container;

/**
 * {@link ContentConnector} implementation responsible for creating {@link com.vaadin.v7.data.Container.Hierarchical}.
 */
public interface CommercetoolsContentConnector extends ContentConnector {

    Container.Hierarchical getContainer();

    CommercetoolsContentConnectorDefinition getContentConnectorDefinition();
}
