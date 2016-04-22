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
package info.magnolia.commercetools.integration.app.container;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnector;
import info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnectorDefinition;
import info.magnolia.commercetools.integration.app.item.CommerceToolsCategoryItem;
import info.magnolia.commercetools.integration.app.item.CommerceToolsItem;
import info.magnolia.commercetools.integration.app.item.CommerceToolsItemId;
import info.magnolia.commercetools.integration.app.item.CommerceToolsProductItem;
import info.magnolia.commercetools.integration.service.CommerceToolsServices;
import info.magnolia.context.Context;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.workbench.container.AbstractContainer;
import info.magnolia.ui.workbench.container.Refreshable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Provider;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.vaadin.data.Container;
import com.vaadin.data.ContainerHelpers;
import com.vaadin.data.Item;
import com.vaadin.data.Property;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.models.LocalizedString;
import io.sphere.sdk.products.ProductProjection;

/**
 * Container implementation that can operate with CommerceTools categories and products.
 *
 * @see CommerceToolsCategoryItem
 * @see CommerceToolsProductItem
 */
public class CommerceToolsContainer extends AbstractContainer implements Container.Hierarchical, Container.Indexed, Container.ItemSetChangeNotifier, Refreshable {

    private static final Logger log = LoggerFactory.getLogger(CommerceToolsContainer.class);

    private final CommerceToolsContentConnector contentConnector;

    private final Provider<CommerceToolsIntegrationModule> provider;

    private final Context context;

    private final CommerceToolsServices commerceToolsServices;

    private Set<ItemSetChangeListener> itemSetChangeListeners = new LinkedHashSet<>();

    private LinkedMap<CommerceToolsItemId, CommerceToolsItem<?>> items = new LinkedMap<>();

    private Map<CommerceToolsItemId, List<CommerceToolsItemId>> childProducts = new HashMap<>();

    private Map<CommerceToolsItemId, List<CommerceToolsItemId>> childCategories = new HashMap<>();

    private CommerceToolsIntegrationModule module;

    @Inject
    public CommerceToolsContainer(final CommerceToolsContentConnector contentConnector, final Provider<CommerceToolsIntegrationModule> provider, final Context context, final CommerceToolsServices commerceToolsServices) {
        this.contentConnector = contentConnector;
        this.provider = provider;
        this.context = context;
        this.commerceToolsServices = commerceToolsServices;
        initialize();
    }

    public void initialize() {
        module = provider.get();
        SphereClient sphereClient = module.getSphereClient(contentConnector.getContentConnectorDefinition().getDefaultProjectId());
        if (sphereClient == null) {
            log.warn("No CommerceTools project found. Please fix your configuration.");
            return;
        }
        List<Category> categories = commerceToolsServices.getCategories(sphereClient, null, context.getLocale()).getResults();
        for (Category category : categories) {
            CommerceToolsCategoryItem categoryItem = new CommerceToolsCategoryItem(contentConnector.getContentConnectorDefinition().getDefaultProjectId(), (category.getParent() != null ? category.getParent().getId() : ""), category);
            this.items.put(categoryItem.getItemId(), categoryItem);
        }
    }

    @Override
    public Collection<CommerceToolsItemId> rootItemIds() {
        List<CommerceToolsItemId> result = new ArrayList<>();
        for (CommerceToolsItem<?> item : items.values()) {
            if (StringUtils.isBlank(item.getParentId())) {
                result.add(item.getItemId());
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public Collection<CommerceToolsItemId> getChildren(final Object itemId) {
        List<CommerceToolsItemId> result = new ArrayList<>();
        CommerceToolsItemId id = (CommerceToolsItemId) itemId;
        String projectId = id.getProjectId();
        String ctItemId = id.getId();
        SphereClient sphereClient = provider.get().getSphereClient(projectId);
        if (!CommerceToolsItemId.ItemType.CATEGORY.equals(id.getType())) {
            return result;
        }
        if (sphereClient == null) {
            log.warn("No SphereClient found for project with projectId {}", projectId);
            return result;
        }
        // iterate over all categories and search for children
        if (childCategories.containsKey(itemId)) {
            result.addAll(childCategories.get(itemId));
        } else {
            List<Category> categories = commerceToolsServices.getCategories(sphereClient, ctItemId, context.getLocale()).getResults();
            if (categories != null) {
                List<CommerceToolsItemId> children = new ArrayList<>();
                for (Category category : categories) {
                    CommerceToolsCategoryItem categoryItem = new CommerceToolsCategoryItem(projectId, ctItemId, category);
                    this.items.put(categoryItem.getItemId(), categoryItem);
                    children.add(categoryItem.getItemId());
                    result.add(categoryItem.getItemId());
                }
                childCategories.put((CommerceToolsItemId) itemId, children);
            }
        }
        // get childProducts from a category or load them if they are not yet loaded
        List<CommerceToolsItemId> products = new ArrayList<>();
        if (childProducts.containsKey(itemId)) {
            products.addAll(childProducts.get(itemId));
        } else {
            // try to load childProducts for a category
            List<ProductProjection> productItems = commerceToolsServices.getProducts(sphereClient, ctItemId, context.getLocale()).getResults();
            if (!productItems.isEmpty()) {
                for (ProductProjection product : productItems) {
                    CommerceToolsProductItem productItem = new CommerceToolsProductItem(projectId, ctItemId, product);
                    this.items.put(productItem.getItemId(), productItem);
                    if (this.childProducts.containsKey(itemId)) {
                        this.childProducts.get(itemId).add(productItem.getItemId());
                    } else {
                        childProducts.put((CommerceToolsItemId) itemId, Lists.newArrayList(productItem.getItemId()));
                    }
                }
            } else {
                childProducts.put((CommerceToolsItemId) itemId, Collections.<CommerceToolsItemId>emptyList());
            }
            products.addAll(childProducts.get(itemId));
        }
        result.addAll(products);
        return result;
    }

    @Override
    public CommerceToolsItemId getParent(final Object itemId) {
        if (isRoot(itemId)) {
            return null;
        }
        CommerceToolsItemId id = (CommerceToolsItemId) itemId;
        for (CommerceToolsItem<?> item : items.values()) {
            if (CommerceToolsItemId.ItemType.CATEGORY.equals(item.getItemId().getType()) && StringUtils.equals(item.getItemId().getId(), id.getParentId())) {
                return item.getItemId();
            }
        }
        return null;
    }

    @Override
    public boolean areChildrenAllowed(final Object itemId) {
        CommerceToolsItemId id = (CommerceToolsItemId) itemId;
        return CommerceToolsItemId.ItemType.CATEGORY.equals(id.getType());
    }

    @Override
    public boolean isRoot(final Object itemId) {
        if (itemId == null) {
            return true;
        }
        CommerceToolsItemId id = (CommerceToolsItemId) itemId;
        return StringUtils.isBlank(id.getParentId());
    }

    @Override
    public boolean hasChildren(final Object itemId) {
        Collection<CommerceToolsItemId> itemIds = getChildren(itemId);
        return itemIds.size() > 0;
    }

    /**
     * Load items to CommerceToolsContainer.items if not present there.
     */
    public void loadItemsToContainer(final List<Object> itemIds) {
        for (Object itemId : itemIds) {
            if (items.containsKey(itemId) || !contentConnector.canHandleItem(itemId)) {
                continue;
            }

            // if item does not exist yet try to obtain it via CommerceTools api
            CommerceToolsItemId id = (CommerceToolsItemId) itemId;
            String ctProjectId = id.getProjectId();
            String ctItemId = id.getId();
            String ctParentId = id.getParentId();
            CommerceToolsItemId.ItemType ctType = id.getType();

            CommerceToolsItem<?> item = null;

            SphereClient sphereClient = module.getSphereClient(ctProjectId);
            if (sphereClient == null) {
                log.warn("No SphereClient found for project with ctProjectId {}", ctProjectId);
                continue;
            }
            if (CommerceToolsItemId.ItemType.CATEGORY.equals(ctType)) {
                Category category = commerceToolsServices.getCategory(sphereClient, ctItemId);
                item = new CommerceToolsCategoryItem(ctProjectId, category.getParent().getId(), category);
            } else if (CommerceToolsItemId.ItemType.PRODUCT.equals(ctType)) {
                ProductProjection product = commerceToolsServices.getProduct(sphereClient, ctItemId);
                item = new CommerceToolsProductItem(ctProjectId, ctParentId, product);
            }
            if (item != null) {
                items.put(item.getItemId(), item);
            }
        }
    }

    @Override
    public Item getItem(final Object itemId) {
        loadItemsToContainer(Arrays.asList(itemId));
        if (items.containsKey(itemId)) {
            return items.get(itemId);
        }
        return null;
    }

    @Override
    public Collection<CommerceToolsItemId> getItemIds() {
        return Collections.unmodifiableCollection(items.keySet());
    }

    @Override
    public Property getContainerProperty(final Object itemId, final Object propertyId) {
        Property property = getItem(itemId).getItemProperty(propertyId);
        if (property.getType().isAssignableFrom(LocalizedString.class)) {
            LocalizedString localizedString = ((LocalizedString) property.getValue());
            Set<Locale> availableLocales = localizedString.getLocales();
            if (availableLocales.contains(context.getLocale())) {
                return new DefaultProperty(localizedString.get(context.getLocale()));
            } else {
                return new DefaultProperty(localizedString.get(availableLocales.iterator().next()));
            }
        }

        return getItem(itemId).getItemProperty(propertyId);
    }

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean containsId(final Object itemId) {
        return items.containsKey(itemId);
    }

    @Override
    public int indexOfId(final Object itemId) {
        return items.indexOf(itemId);
    }

    @Override
    public Object getIdByIndex(final int index) {
        return items.get(index);
    }

    @Override
    public List<?> getItemIds(final int startIndex, final int numberOfItems) {
        return ContainerHelpers.getItemIdsUsingGetIdByIndex(startIndex, numberOfItems, this);
    }

    @Override
    public Object nextItemId(final Object itemId) {
        if (isLastId(itemId)) {
            return null;
        }
        return items.get(items.indexOf(itemId) + 1);
    }

    @Override
    public Object prevItemId(final Object itemId) {
        if (isFirstId(itemId)) {
            return null;
        }
        return items.get(items.indexOf(itemId) - 1);
    }

    @Override
    public Object firstItemId() {
        return items.get(0);
    }

    @Override
    public Object lastItemId() {
        return items.get(items.size() - 1);
    }

    @Override
    public boolean isFirstId(final Object itemId) {
        return firstItemId().equals(itemId);
    }

    @Override
    public boolean isLastId(final Object itemId) {
        return lastItemId().equals(itemId);
    }

    @Override
    public void addItemSetChangeListener(final ItemSetChangeListener listener) {
        itemSetChangeListeners.add(listener);
    }

    @Override
    public void addListener(final ItemSetChangeListener listener) {
        addItemSetChangeListener(listener);
    }

    @Override
    public void removeItemSetChangeListener(final ItemSetChangeListener listener) {
        itemSetChangeListeners.remove(listener);
    }

    @Override
    public void removeListener(final ItemSetChangeListener listener) {
        removeItemSetChangeListener(listener);
    }

    public void fireItemSetChange() {
        if (!itemSetChangeListeners.isEmpty()) {
            Object[] listeners = itemSetChangeListeners.toArray();
            for (final Object listener : listeners) {
                ItemSetChangeEvent event = new ItemSetChangeEvent(this);
                ((ItemSetChangeListener) listener).containerItemSetChange(event);
            }
        }
    }

    @Override
    public void refresh() {
        items.clear();
        childCategories.clear();
        childProducts.clear();
        initialize();
        fireItemSetChange();
    }

    public CommerceToolsContentConnectorDefinition getContentConnectorDefinition() {
        return contentConnector.getContentConnectorDefinition();
    }

    /* UNSUPPORTED METHODS */

    @Override
    public Item addItem(final Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItem() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeItem(final Object itemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAllItems() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setChildrenAllowed(final Object itemId, final boolean areChildrenAllowed) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean setParent(final Object itemId, final Object newParentId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItemAt(final int index) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAt(final int index, final Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object addItemAfter(final Object previousItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Item addItemAfter(final Object previousItemId, final Object newItemId) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Basic itemSet change event.
     */
    private class ItemSetChangeEvent extends EventObject implements Container.ItemSetChangeEvent, Serializable {

        public ItemSetChangeEvent(final Container source) {
            super(source);
        }

        @Override
        public Container getContainer() {
            return (Container) getSource();
        }
    }
}
