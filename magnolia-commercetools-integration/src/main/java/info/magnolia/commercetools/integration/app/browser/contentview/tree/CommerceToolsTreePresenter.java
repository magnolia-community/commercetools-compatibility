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
package info.magnolia.commercetools.integration.app.browser.contentview.tree;

import info.magnolia.commercetools.integration.app.container.CommerceToolsContainer;
import info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnector;
import info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnectorDefinition;
import info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnectorImpl;
import info.magnolia.commercetools.integration.app.item.CommerceToolsCategoryItem;
import info.magnolia.commercetools.integration.app.item.CommerceToolsProductItem;
import info.magnolia.event.EventBus;
import info.magnolia.commercetools.integration.app.browser.event.ProjectIdChangedEvent;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.tree.TreePresenter;
import info.magnolia.ui.workbench.tree.TreeView;

import java.util.List;

import javax.inject.Inject;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * Tree presenter that uses {@link CommerceToolsContainer}.
 */
public class CommerceToolsTreePresenter extends TreePresenter {

    private final TreeView treeView;

    @Inject
    public CommerceToolsTreePresenter(final TreeView view, final ComponentProvider componentProvider) {
        super(view, componentProvider);
        this.treeView = view;
    }

    @Override
    public TreeView start(final WorkbenchDefinition workbenchDefinition, final EventBus eventBus, final String viewTypeName, final ContentConnector contentConnector) {
        TreeView view = super.start(workbenchDefinition, eventBus, viewTypeName, contentConnector);
        bindEvents();
        return view;
    }

    protected void bindEvents() {
        eventBus.addHandler(ProjectIdChangedEvent.class, new ProjectIdChangedEvent.Handler() {
            @Override
            public void onProjectIdChange(final ProjectIdChangedEvent event) {
                CommerceToolsContainer ctContainer = (CommerceToolsContainer) container;
                ctContainer.getContentConnectorDefinition().setDefaultProjectId(event.getNewProjectId());
            }
        });
    }

    @Override
    public void select(final List<Object> itemIds) {
        if (itemIds.size() > 0) {
            super.select(itemIds);
        } else {
            treeView.select(null);
        }
    }

    @Override
    protected Container.Hierarchical createContainer() {
        return ((CommerceToolsContentConnector) contentConnector).getContainer();
    }

    @Override
    public String getIcon(final Item item) {
        if (item instanceof CommerceToolsCategoryItem) {
            return getContentConnectorDefinition().getCategoryIcon();
        } else if (item instanceof CommerceToolsProductItem) {
            return getContentConnectorDefinition().getProductIcon();
        }
        return super.getIcon(item);
    }

    private CommerceToolsContentConnectorDefinition getContentConnectorDefinition() {
        return ((CommerceToolsContentConnectorImpl) contentConnector).getContentConnectorDefinition();
    }
}
