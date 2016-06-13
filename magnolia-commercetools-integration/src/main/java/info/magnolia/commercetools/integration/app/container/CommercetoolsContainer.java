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

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnector;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnectorDefinition;
import info.magnolia.commercetools.integration.app.item.CommercetoolsCategoryItem;
import info.magnolia.commercetools.integration.app.item.CommercetoolsItem;
import info.magnolia.commercetools.integration.app.item.CommercetoolsItemId;
import info.magnolia.commercetools.integration.app.item.CommercetoolsProductItem;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
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
 * Container implementation that can operate with commercetools categories and products.
 *
 * @see CommercetoolsCategoryItem
 * @see CommercetoolsProductItem
 */
public class CommercetoolsContainer extends AbstractContainer implements Container.Hierarchical, Container.Indexed, Container.ItemSetChangeNotifier, Refreshable {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsContainer.class);

    private final CommercetoolsContentConnector contentConnector;

    private final Provider<CommercetoolsIntegrationModule> provider;

    private final Context context;

    private final CommercetoolsServices commercetoolsServices;

    private Set<ItemSetChangeListener> itemSetChangeListeners = new LinkedHashSet<>();

    private LinkedMap<CommercetoolsItemId, CommercetoolsItem<?>> items = new LinkedMap<>();

    private Map<CommercetoolsItemId, List<CommercetoolsItemId>> childProducts = new HashMap<>();

    private Map<CommercetoolsItemId, List<CommercetoolsItemId>> childCategories = new HashMap<>();

    private CommercetoolsIntegrationModule module;

    @Inject
    public CommercetoolsContainer(final CommercetoolsContentConnector contentConnector, final Provider<CommercetoolsIntegrationModule> provider, final Context context, final CommercetoolsServices commercetoolsServices) {
        this.contentConnector = contentConnector;
        this.provider = provider;
        this.context = context;
        this.commercetoolsServices = commercetoolsServices;
        initialize();
    }

    public void initialize() {
        module = provider.get();
        SphereClient sphereClient = module.getSphereClient(contentConnector.getContentConnectorDefinition().getDefaultProjectId());
        if (sphereClient == null) {
            log.warn("No commercetools project found. Please fix your configuration.");
            return;
        }
        List<Category> categories = commercetoolsServices.getCategories(sphereClient, null, context.getLocale()).getResults();
        for (Category category : categories) {
            CommercetoolsCategoryItem categoryItem = new CommercetoolsCategoryItem(contentConnector.getContentConnectorDefinition().getDefaultProjectId(), (category.getParent() != null ? category.getParent().getId() : ""), category);
            this.items.put(categoryItem.getItemId(), categoryItem);
        }
    }

    @Override
    public Collection<CommercetoolsItemId> rootItemIds() {
        List<CommercetoolsItemId> result = new ArrayList<>();
        for (CommercetoolsItem<?> item : items.values()) {
            if (StringUtils.isBlank(item.getParentId())) {
                result.add(item.getItemId());
            }
        }
        return Collections.unmodifiableCollection(result);
    }

    @Override
    public Collection<CommercetoolsItemId> getChildren(final Object itemId) {
        List<CommercetoolsItemId> result = new ArrayList<>();
        CommercetoolsItemId id = (CommercetoolsItemId) itemId;
        String projectId = id.getProjectId();
        String ctItemId = id.getId();
        SphereClient sphereClient = provider.get().getSphereClient(projectId);
        if (!CommercetoolsItemId.ItemType.CATEGORY.equals(id.getType())) {
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
            List<Category> categories = commercetoolsServices.getCategories(sphereClient, ctItemId, context.getLocale()).getResults();
            if (categories != null) {
                List<CommercetoolsItemId> children = new ArrayList<>();
                for (Category category : categories) {
                    CommercetoolsCategoryItem categoryItem = new CommercetoolsCategoryItem(projectId, ctItemId, category);
                    this.items.put(categoryItem.getItemId(), categoryItem);
                    children.add(categoryItem.getItemId());
                    result.add(categoryItem.getItemId());
                }
                childCategories.put((CommercetoolsItemId) itemId, children);
            }
        }
        // get childProducts from a category or load them if they are not yet loaded
        List<CommercetoolsItemId> products = new ArrayList<>();
        if (childProducts.containsKey(itemId)) {
            products.addAll(childProducts.get(itemId));
        } else {
            // try to load childProducts for a category
            List<ProductProjection> productItems = commercetoolsServices.getProducts(sphereClient, ctItemId, context.getLocale()).getResults();
            if (!productItems.isEmpty()) {
                for (ProductProjection product : productItems) {
                    CommercetoolsProductItem productItem = new CommercetoolsProductItem(projectId, ctItemId, product);
                    this.items.put(productItem.getItemId(), productItem);
                    if (this.childProducts.containsKey(itemId)) {
                        this.childProducts.get(itemId).add(productItem.getItemId());
                    } else {
                        childProducts.put((CommercetoolsItemId) itemId, Lists.newArrayList(productItem.getItemId()));
                    }
                }
            } else {
                childProducts.put((CommercetoolsItemId) itemId, Collections.<CommercetoolsItemId>emptyList());
            }
            products.addAll(childProducts.get(itemId));
        }
        result.addAll(products);
        return result;
    }

    @Override
    public CommercetoolsItemId getParent(final Object itemId) {
        if (isRoot(itemId)) {
            return null;
        }
        CommercetoolsItemId id = (CommercetoolsItemId) itemId;
        for (CommercetoolsItem<?> item : items.values()) {
            if (CommercetoolsItemId.ItemType.CATEGORY.equals(item.getItemId().getType()) && StringUtils.equals(item.getItemId().getId(), id.getParentId())) {
                return item.getItemId();
            }
        }
        return null;
    }

    @Override
    public boolean areChildrenAllowed(final Object itemId) {
        CommercetoolsItemId id = (CommercetoolsItemId) itemId;
        return CommercetoolsItemId.ItemType.CATEGORY.equals(id.getType());
    }

    @Override
    public boolean isRoot(final Object itemId) {
        if (itemId == null) {
            return true;
        }
        CommercetoolsItemId id = (CommercetoolsItemId) itemId;
        return StringUtils.isBlank(id.getParentId());
    }

    @Override
    public boolean hasChildren(final Object itemId) {
        Collection<CommercetoolsItemId> itemIds = getChildren(itemId);
        return itemIds.size() > 0;
    }

    /**
     * Load items to CommercetoolsContainer.items if not present there.
     */
    public void loadItemsToContainer(final List<Object> itemIds) {
        for (Object itemId : itemIds) {
            if (items.containsKey(itemId) || !contentConnector.canHandleItem(itemId)) {
                continue;
            }

            // if item does not exist yet try to obtain it via commercetools api
            CommercetoolsItemId id = (CommercetoolsItemId) itemId;
            String ctProjectId = id.getProjectId();
            String ctItemId = id.getId();
            String ctParentId = id.getParentId();
            CommercetoolsItemId.ItemType ctType = id.getType();

            CommercetoolsItem<?> item = null;

            SphereClient sphereClient = module.getSphereClient(ctProjectId);
            if (sphereClient == null) {
                log.warn("No SphereClient found for project with ctProjectId {}", ctProjectId);
                continue;
            }
            if (CommercetoolsItemId.ItemType.CATEGORY.equals(ctType)) {
                Category category = commercetoolsServices.getCategory(sphereClient, ctItemId);
                item = new CommercetoolsCategoryItem(ctProjectId, category.getParent().getId(), category);
            } else if (CommercetoolsItemId.ItemType.PRODUCT.equals(ctType)) {
                ProductProjection product = commercetoolsServices.getProduct(sphereClient, ctItemId);
                item = new CommercetoolsProductItem(ctProjectId, ctParentId, product);
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
    public Collection<CommercetoolsItemId> getItemIds() {
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

    public CommercetoolsContentConnectorDefinition getContentConnectorDefinition() {
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
