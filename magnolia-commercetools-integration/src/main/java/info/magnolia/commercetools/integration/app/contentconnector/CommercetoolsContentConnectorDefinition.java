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
package info.magnolia.commercetools.integration.app.contentconnector;

import info.magnolia.ui.vaadin.integration.contentconnector.ConfiguredContentConnectorDefinition;

/**
 * Definition for {@link CommercetoolsContentConnector}.
 */
public class CommercetoolsContentConnectorDefinition extends ConfiguredContentConnectorDefinition {

    private String categoryIcon = "icon-folder-l";

    private String productIcon = "icon-tag-2";

    private String defaultProjectId;

    public CommercetoolsContentConnectorDefinition() {
        setImplementationClass(CommercetoolsContentConnectorImpl.class);
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public String getProductIcon() {
        return productIcon;
    }

    public void setProductIcon(String productIcon) {
        this.productIcon = productIcon;
    }

    public String getDefaultProjectId() {
        return defaultProjectId;
    }

    public void setDefaultProjectId(String defaultProjectId) {
        this.defaultProjectId = defaultProjectId;
    }
}
