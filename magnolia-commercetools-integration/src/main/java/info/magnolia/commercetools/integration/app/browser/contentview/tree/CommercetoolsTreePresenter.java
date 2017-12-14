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
package info.magnolia.commercetools.integration.app.browser.contentview.tree;

import info.magnolia.commercetools.integration.app.browser.event.ProjectIdChangedEvent;
import info.magnolia.commercetools.integration.app.container.CommercetoolsContainer;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnector;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnectorDefinition;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnectorImpl;
import info.magnolia.commercetools.integration.app.item.CommercetoolsCategoryItem;
import info.magnolia.commercetools.integration.app.item.CommercetoolsProductItem;
import info.magnolia.event.EventBus;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.definition.WorkbenchDefinition;
import info.magnolia.ui.workbench.tree.TreePresenter;
import info.magnolia.ui.workbench.tree.TreeView;

import java.util.List;

import javax.inject.Inject;

import com.vaadin.v7.data.Container;
import com.vaadin.v7.data.Item;

/**
 * Tree presenter that uses {@link CommercetoolsContainer}.
 */
public class CommercetoolsTreePresenter extends TreePresenter {

    private final TreeView treeView;

    @Inject
    public CommercetoolsTreePresenter(final TreeView view, final ComponentProvider componentProvider) {
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
        eventBus.addHandler(ProjectIdChangedEvent.class, event -> {
            CommercetoolsContainer ctContainer = (CommercetoolsContainer) container;
            ctContainer.getContentConnectorDefinition().setDefaultProjectId(event.getNewProjectId());
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
        return ((CommercetoolsContentConnector) contentConnector).getContainer();
    }

    @Override
    public String getIcon(final Item item) {
        if (item instanceof CommercetoolsCategoryItem) {
            return getContentConnectorDefinition().getCategoryIcon();
        } else if (item instanceof CommercetoolsProductItem) {
            return getContentConnectorDefinition().getProductIcon();
        }
        return super.getIcon(item);
    }

    private CommercetoolsContentConnectorDefinition getContentConnectorDefinition() {
        return ((CommercetoolsContentConnectorImpl) contentConnector).getContentConnectorDefinition();
    }
}
