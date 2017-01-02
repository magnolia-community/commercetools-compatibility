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
package info.magnolia.commercetools.integration.app.contentconnector;

import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import com.vaadin.data.Container;

/**
 * {@link ContentConnector} implementation responsible for creating {@link com.vaadin.data.Container.Hierarchical}.
 */
public interface CommercetoolsContentConnector extends ContentConnector {

    Container.Hierarchical getContainer();

    CommercetoolsContentConnectorDefinition getContentConnectorDefinition();
}
