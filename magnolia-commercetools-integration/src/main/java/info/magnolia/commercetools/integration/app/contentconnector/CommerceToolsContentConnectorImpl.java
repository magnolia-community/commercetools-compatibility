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
package info.magnolia.commercetools.integration.app.contentconnector;

import info.magnolia.cms.i18n.I18nContentSupport;
import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.commercetools.integration.CommerceToolsProjectConfiguration;
import info.magnolia.commercetools.integration.app.container.CommerceToolsContainer;
import info.magnolia.commercetools.integration.app.item.CommerceToolsItem;
import info.magnolia.commercetools.integration.app.item.CommerceToolsItemId;
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
 * Implementation of {@link CommerceToolsContentConnector}.
 */
public class CommerceToolsContentConnectorImpl extends AbstractContentConnector implements CommerceToolsContentConnector {

    private static final Logger log = LoggerFactory.getLogger(CommerceToolsContentConnectorImpl.class);

    private final CommerceToolsContentConnectorDefinition contentConnectorDefinition;

    private final CommerceToolsContainer container;

    @Inject
    public CommerceToolsContentConnectorImpl(final CommerceToolsContentConnectorDefinition contentConnectorDefinition, final Provider<CommerceToolsIntegrationModule> commerceToolsIntegrationModuleProvider, final I18nContentSupport i18nContentSupport) {
        super(contentConnectorDefinition);
        this.contentConnectorDefinition = contentConnectorDefinition;
        //set first ctProject as default
        Map<String, CommerceToolsProjectConfiguration> projects = commerceToolsIntegrationModuleProvider.get().getProjects();
        if (!projects.isEmpty()) {
            contentConnectorDefinition.setDefaultProjectId(projects.keySet().iterator().next());
        } else {
            log.warn("No CommerceTools project found ([/modules/commercetools-integration/config/projects/]). Please fix your configuration.");
        }
        container = new CommerceToolsContainer(this, commerceToolsIntegrationModuleProvider, i18nContentSupport);
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
        return CommerceToolsItemId.fromString(urlFragment);
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
        if (item instanceof CommerceToolsItem) {
            return ((CommerceToolsItem) item).getItemId();
        }
        return null;
    }

    @Override
    public boolean canHandleItem(final Object itemId) {
        return itemId instanceof CommerceToolsItemId;
    }

    @Override
    public Container.Hierarchical getContainer() {
        return container;
    }

    @Override
    public CommerceToolsContentConnectorDefinition getContentConnectorDefinition() {
        return contentConnectorDefinition;
    }
}
