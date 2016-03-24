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

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.util.BeanItem;

/**
 * Base implementation for KonaKart items.
 *
 * @param <BT> bean type
 */
public abstract class CommerceToolsItem<BT> extends BeanItem<BT> {

    private final String projectId;

    private final String parentId;

    private CommerceToolsItemId itemId;

    public CommerceToolsItem(final String projectId, final String parentId, final BT bean) {
        super(bean);
        if (StringUtils.isBlank(projectId)) {
            throw new IllegalArgumentException("Project id cannot be blank.");
        }
        this.projectId = projectId;
        this.parentId = parentId;
        itemId = createItemId();
    }

    /**
     * Creates new item id for the passed bean item.
     */
    protected abstract CommerceToolsItemId createItemId();

    public String getProjectId() {
        return projectId;
    }

    public String getParentId() {
        return parentId;
    }

    public CommerceToolsItemId getItemId() {
        return itemId;
    }
}
