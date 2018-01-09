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
package info.magnolia.commercetools.integration.app.contentconnector;

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.CommercetoolsProjectConfiguration;
import info.magnolia.commercetools.integration.app.container.CommercetoolsContainer;
import info.magnolia.commercetools.integration.app.item.CommercetoolsItem;
import info.magnolia.commercetools.integration.app.item.CommercetoolsItemId;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.context.Context;
import info.magnolia.ui.vaadin.integration.contentconnector.AbstractContentConnector;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Container;
import com.vaadin.data.Item;

/**
 * Implementation of {@link CommercetoolsContentConnector}.
 */
public class CommercetoolsContentConnectorImpl extends AbstractContentConnector implements CommercetoolsContentConnector {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsContentConnectorImpl.class);

    private final CommercetoolsContentConnectorDefinition contentConnectorDefinition;

    private final CommercetoolsContainer container;

    @Inject
    public CommercetoolsContentConnectorImpl(final CommercetoolsContentConnectorDefinition contentConnectorDefinition, final Provider<CommercetoolsIntegrationModule> commercetoolsIntegrationModuleProvider, final Context context, final CommercetoolsServices commercetoolsServices) {
        super(contentConnectorDefinition);
        this.contentConnectorDefinition = contentConnectorDefinition;
        //set first ctProject as default
        Map<String, CommercetoolsProjectConfiguration> projects = commercetoolsIntegrationModuleProvider.get().getProjects();
        if (!projects.isEmpty()) {
            contentConnectorDefinition.setDefaultProjectId(projects.keySet().iterator().next());
        } else {
            log.warn("No commercetools project found ([/modules/commercetools-integration/config/projects/]). Please fix your configuration.");
        }
        container = new CommercetoolsContainer(this, commercetoolsIntegrationModuleProvider, context, commercetoolsServices);
    }

    @Override
    public String getItemUrlFragment(final Object itemId) {
        return canHandleItem(itemId) ? itemId.toString() : null;
    }

    @Override
    public Object getItemIdByUrlFragment(final String urlFragment) {
        if ("/".equals(urlFragment)) {
            return StringUtils.EMPTY;
        }
        return CommercetoolsItemId.fromString(urlFragment);
    }

    @Override
    public Object getDefaultItemId() {
        return StringUtils.EMPTY;
    }

    @Override
    public Item getItem(final Object itemId) {
        return canHandleItem(itemId) ? container.getItem(itemId) : null;
    }

    @Override
    public Object getItemId(final Item item) {
        if (item instanceof CommercetoolsItem) {
            return ((CommercetoolsItem) item).getItemId();
        }
        return null;
    }

    @Override
    public boolean canHandleItem(final Object itemId) {
        return itemId instanceof CommercetoolsItemId;
    }

    @Override
    public Container.Hierarchical getContainer() {
        return container;
    }

    @Override
    public CommercetoolsContentConnectorDefinition getContentConnectorDefinition() {
        return contentConnectorDefinition;
    }
}
