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
package info.magnolia.commercetools.integration.app.configuration;

import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.framework.app.BaseSubApp;

import javax.inject.Inject;

/**
 * CommerceTools Configuration SubApp class.
 */
public class CommerceToolsConfigurationSubApp extends BaseSubApp<CommerceToolsConfigurationView> {

    @Inject
    public CommerceToolsConfigurationSubApp(SubAppContext subAppContext, CommerceToolsConfigurationPresenter presenter) {
        super(subAppContext, presenter.start());
    }
}
