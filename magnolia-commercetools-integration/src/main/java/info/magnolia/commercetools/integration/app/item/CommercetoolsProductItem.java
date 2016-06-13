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
package info.magnolia.commercetools.integration.app.item;

import io.sphere.sdk.products.ProductProjection;

/**
 * Implementation of {@link CommercetoolsItem} for commercetools {@link ProductProjection}.
 */
public class CommercetoolsProductItem extends CommercetoolsItem<ProductProjection> {

    public CommercetoolsProductItem(final String projectId, final String parentId, final ProductProjection bean) {
        super(projectId, parentId, bean);
    }

    @Override
    protected CommercetoolsItemId createItemId() {
        return new CommercetoolsItemId(getProjectId(), CommercetoolsItemId.ItemType.PRODUCT, getBean().getId(), getParentId());
    }
}
