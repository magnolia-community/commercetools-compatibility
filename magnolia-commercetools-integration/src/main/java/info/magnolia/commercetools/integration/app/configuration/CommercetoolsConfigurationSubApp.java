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

import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.framework.app.BaseSubApp;

import javax.inject.Inject;

/**
 * Commercetools Configuration SubApp class.
 */
public class CommercetoolsConfigurationSubApp extends BaseSubApp<CommercetoolsConfigurationView> {

    @Inject
    public CommercetoolsConfigurationSubApp(SubAppContext subAppContext, CommercetoolsConfigurationPresenter presenter) {
        super(subAppContext, presenter.start());
    }
}
