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
