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
package info.magnolia.commercetools.integration.app.item;

import io.sphere.sdk.categories.Category;

/**
 * Implementation of {@link CommercetoolsItem} for commercetools {@link Category}.
 */
public class CommercetoolsCategoryItem extends CommercetoolsItem<Category> {

    public CommercetoolsCategoryItem(final String projectId, final String parentId, final Category bean) {
        super(projectId, parentId, bean);
    }

    @Override
    protected CommercetoolsItemId createItemId() {
        return new CommercetoolsItemId(getProjectId(), CommercetoolsItemId.ItemType.CATEGORY, getBean().getId(), getParentId());
    }
}
