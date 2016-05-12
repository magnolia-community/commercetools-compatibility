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
package info.magnolia.commercetools.integration.templating;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.commercetools.integration.service.CommerceToolsServices;
import info.magnolia.context.WebContext;
import info.magnolia.module.site.SiteManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.javamoney.moneta.FastMoney;

import com.neovisionaries.i18n.CountryCode;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.Price;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.search.PagedSearchResult;

/**
 * Useful functions for templating.
 */
@Singleton
public class CommerceToolsTemplatingFunctions {

    private final Provider<CommerceToolsIntegrationModule> moduleProvider;
    private final CommerceToolsServices commerceToolsServices;
    private final SiteManager siteManager;
    private final Provider<WebContext> webContextProvider;

    private static final String PARAMETER_CURRENT_PAGE = "currentPage";

    @Inject
    public CommerceToolsTemplatingFunctions(final Provider<CommerceToolsIntegrationModule> moduleProvider, final CommerceToolsServices commerceToolsServices, final SiteManager siteManager, final Provider<WebContext> webContextProvider) {
        this.moduleProvider = moduleProvider;
        this.commerceToolsServices = commerceToolsServices;
        this.siteManager = siteManager;
        this.webContextProvider = webContextProvider;
    }

    public ProductProjection getProduct(String productId) {
        return commerceToolsServices.getProduct(getProjectClient(), productId);
    }

    public Category getCategory(String categoryId) {
        return commerceToolsServices.getCategory(getProjectClient(), categoryId);
    }

    public PagedQueryResult<ProductProjection> getProducts(String parentId, int offset, int limit) {
        return commerceToolsServices.getProductsByOffset(getProjectClient(), parentId, getLanguage(), offset, limit);
    }

    public PagedQueryResult<Category> getCategories(String parentId) {
        return commerceToolsServices.getCategories(getProjectClient(), parentId, getLanguage());
    }

    public PagedSearchResult<ProductProjection> searchForProducts(String queryStr, int offset, int limit) {
        return commerceToolsServices.searchForProducts(getProjectClient(), queryStr, getLanguage(), offset, limit);
    }

    /**
     * Returns List of prices dependent on currency, country customer group and channel.
     */
    public String getPriceToShow(List<Price> prices) {
        if (prices.isEmpty()) {
            return null;
        }

        Price result = null;
        int resultValueToCompare = 0;
        for (Price price : prices) {
            String priceCurrencyCode = price.getValue().getCurrency().getCurrencyCode();
            String priceCountryCode = price.getCountry() != null ? price.getCountry().getAlpha2() : null;
            String priceChannelId = price.getChannel() != null ? price.getChannel().getId() : null;
            String priceCustomerGroupId = price.getCustomerGroup() != null ? price.getCustomerGroup().getId() : null;

            int priceValueToCompare = getValueToCompare(priceCurrencyCode, priceCountryCode, priceChannelId, priceCustomerGroupId);

            if (priceValueToCompare > resultValueToCompare) {
                result = price;
                resultValueToCompare = priceValueToCompare;
            }

            if (StringUtils.equals(priceCurrencyCode, getCurrencyCode()) &&
                    StringUtils.equals(priceCountryCode, getCountryCode()) &&
                    StringUtils.equals(priceChannelId, getChannelId()) &&
                    StringUtils.equals(priceCustomerGroupId, getCustomerGroupId())) {
                break;
            }
        }

        if (result != null) {
            return FastMoney.of(result.getValue().getNumber(), result.getValue().getCurrency()).toString();
        }

        return null;
    }

    /**
     * List of price scope priority.
     *
     * currency should always match
     * country, customer group and channel.
     * customer group and channel
     * customer group and country
     * customer group
     * channel and country
     * channel
     * country
     * any left
     */
    private int getValueToCompare(String priceCurrencyCode, String priceCountryCode, String priceChannelId, String priceCustomerGroupId) {

        int result = 0;

        if (StringUtils.equals(priceCurrencyCode, getCurrencyCode())) {
            result += 1;
            if (priceCountryCode == null) {
                result += 10;
            }
            if (StringUtils.equals(priceCountryCode, getCountryCode())) {
                result += 100;
            }
            if (priceChannelId == null) {
                result += 1000;
            }
            if (StringUtils.equals(priceChannelId, getChannelId())) {
                result += 10000;
            }
            if (priceCustomerGroupId == null) {
                result += 100000;
            }
            if (StringUtils.equals(priceCustomerGroupId, getCustomerGroupId())) {
                result += 1000000;
            }
        }

        return result;
    }

    public SphereClient getProjectClient() {
        return moduleProvider.get().getSphereClient(getProjectName());
    }

    /**
     * Returns {@value CommerceToolsIntegrationModule#PROJECT_PARAM_NAME} for the current site.
     */
    public String getProjectName() {
        Map<String, Object> params = siteManager.getCurrentSite().getParameters();
        if (params.containsKey(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME)) {
            return String.valueOf(params.get(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME));
        }
        throw new RuntimeException("No project name configured for site " + siteManager.getCurrentSite());
    }

    /**
     * Returns {@value CommerceToolsIntegrationModule#LANGUAGE_PARAM_NAME} for the current site.
     */
    public Locale getLanguage() {
        Map<String, Object> params = siteManager.getCurrentSite().getParameters();
        if (params.containsKey(CommerceToolsIntegrationModule.LANGUAGE_PARAM_NAME)) {
            return new Locale(String.valueOf(params.get(CommerceToolsIntegrationModule.LANGUAGE_PARAM_NAME)));
        }
        throw new RuntimeException("No ctLanguage configured for site " + siteManager.getCurrentSite());
    }

    /**
     * Returns {@value CommerceToolsIntegrationModule#COUNTRY_PARAM_NAME} for the current site.
     */
    public String getCountryCode() {
        Map<String, Object> params = siteManager.getCurrentSite().getParameters();
        if (params.containsKey(CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME)) {
            return String.valueOf(params.get(CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME));
        }
        throw new RuntimeException("No ctCountryCode configured for site " + siteManager.getCurrentSite());
    }

    /**
     * Returns {@value CommerceToolsIntegrationModule#CURRENCY_PARAM_NAME} for the current site.
     */
    public String getCurrencyCode() {
        Map<String, Object> params = siteManager.getCurrentSite().getParameters();
        if (params.containsKey(CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME)) {
            return String.valueOf(params.get(CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME));
        }
        throw new RuntimeException("No ctCurrency configured for site " + siteManager.getCurrentSite());
    }

    private String getCustomerGroupId() {
        //returns null as default value, will be implemented in future
        return null;
    }

    private String getChannelId() {
        //returns null as default value, will be implemented in future
        return null;
    }

    public List<Category> getChildCategoriesFromList(List<Category> categories, String parentId) {
        List<Category> result = new ArrayList<>();
        for (Category category : categories) {
            if ((category.getParent() == null && StringUtils.equals(parentId, "root")) || (category.getParent() != null && StringUtils.equals(category.getParent().getId(), parentId))) {
                result.add(category);
            }
        }

        return result;
    }

    /**
     * Returns a link the n-th page.
     */
    public String getPageLink(int targetPageNumber) {
        final String current = String.format("%s=", PARAMETER_CURRENT_PAGE);
        final String currentUrl = webContextProvider.get().getAggregationState().getOriginalURL();

        if (currentUrl.indexOf('?') > 0) {
            final String queryString = StringUtils.substringAfter(currentUrl, "?");
            final StringBuilder newQueryString = new StringBuilder("?");
            final String[] params = queryString.split("&amp;");
            boolean pageSet = false;
            int count = 0;

            for (String param : params) {
                if (param.startsWith(current)) {
                    newQueryString.append(current).append(targetPageNumber);
                    pageSet = true;
                } else {
                    newQueryString.append(StringEscapeUtils.escapeHtml4(param)); // Without this escaping possible XSS
                }
                count++;
                if (count < params.length) {
                    newQueryString.append("&");
                }
            }

            if (!pageSet) {
                if (newQueryString.length() > 1) {
                    newQueryString.append("&");
                }
                newQueryString.append(current).append(targetPageNumber);

            }

            return StringUtils.substringBefore(currentUrl, "?") + newQueryString.toString();
        } else {
            return currentUrl + "?" + current + targetPageNumber;
        }
    }

    public Cart getCart() {
        return commerceToolsServices.getOrCreateCart(getProjectClient(), webContextProvider.get().getAttribute(CommerceToolsServices.CT_CUSTOMER_ID), webContextProvider.get().getAttribute(CommerceToolsServices.CT_CART_ID), CountryCode.getByCode(getCountryCode()), getCurrencyCode());
    }

    public int getNumberOfItemsInCart() {
        Cart cart = getCart();

        return cart.getLineItems().size() + cart.getCustomLineItems().size();
    }
}
