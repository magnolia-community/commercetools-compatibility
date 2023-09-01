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
package info.magnolia.commercetools.integration.app.browser.contentview.tree;

import info.magnolia.ui.workbench.tree.TreeViewImpl;

import java.util.List;

/**
 * View implementation that de-selects item if no item id is passed.
 */
public class CommercetoolsTreeViewImpl extends TreeViewImpl {

    @Override
    public void select(List<Object> itemIds) {
        asVaadinComponent().setValue(null);
        super.select(itemIds);
    }

}
