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
