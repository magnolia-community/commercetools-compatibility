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
package info.magnolia.commercetools.integration.service;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.context.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.money.Monetary;

import org.apache.commons.lang3.StringUtils;

import com.neovisionaries.i18n.CountryCode;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.CartDraft;
import io.sphere.sdk.carts.CartDraftBuilder;
import io.sphere.sdk.carts.CartState;
import io.sphere.sdk.carts.commands.CartCreateCommand;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.AddLineItem;
import io.sphere.sdk.carts.commands.updateactions.ChangeLineItemQuantity;
import io.sphere.sdk.carts.commands.updateactions.RemoveLineItem;
import io.sphere.sdk.carts.commands.updateactions.SetCountry;
import io.sphere.sdk.carts.commands.updateactions.SetShippingAddress;
import io.sphere.sdk.carts.expansion.CartExpansionModel;
import io.sphere.sdk.carts.queries.CartByCustomerIdGet;
import io.sphere.sdk.carts.queries.CartByIdGet;
import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.expansion.CategoryExpansionModel;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.categories.queries.CategoryQueryModel;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.ErrorResponseException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerDraftBuilder;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.expansion.ProductProjectionExpansionModel;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.products.queries.ProductProjectionQueryModel;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.QueryPredicate;
import io.sphere.sdk.queries.QuerySort;
import io.sphere.sdk.search.PagedSearchResult;
import io.sphere.sdk.shippingmethods.expansion.ShippingMethodExpansionModel;

/**
 * Service class containing CommerceTools queries used by several classes.
 */
@Singleton
public class CommerceToolsServices {

    public static final String CT_CUSTOMER_ID = "ctCustomerId";
    public static final String CT_CART_ID = "ctCartId";
    public static final String CT_LAST_ORDER_ID = "ctLastOrderId";
    public static final String CT_LINE_ITEM_ID = "ctLineItemId";
    public static final String CT_CUSTOMER_EMAIL = "ctCustomerEmail";
    public static final String CT_CUSTOMER_PASSWORD = "ctCustomerPassword";
    public static final String CT_PRODUCT_ID = "ctProductId";
    public static final String CT_VARIANT_ID = "ctVariantId";
    public static final String CT_PRODUCT_QUANTITY = "ctProductQuantity";
    public static final String CT_COUNTRY_CODE = "ctCountryCode";
    public static final String CT_CURRENCY_CODE = "ctCurrencyCode";

    /**
     * See documentation for <a href="http://dev.commercetools.com/http-api.html#limit">limit</a>.
     */
    private static final int MAX_QUERY_LIMIT = 500;
    private static final String CT_CUSTOMER_NUMBER = "ctCustomerNumber";
    private static final String CT_CUSTOMER_FIRST_NAME = "ctCustomerFirstName";
    private static final String CT_CUSTOMER_LAST_NAME = "ctCustomerLastName";
    private static final String CT_CUSTOMER_MIDDLE_NAME = "ctCustomerMiddleName";
    private static final String CT_CUSTOMER_TITLE = "ctCustomerTitle";
    private static final String CT_CUSTOMER_EXTERNAL_ID = "ctCustomerExternalId";
    private static final String CT_CUSTOMER_DATE_OF_BIRTH = "ctCustomerDateOfBirth";
    private static final String CT_CUSTOMER_COMPANY_NAME = "ctCustomerCompanyName";
    private static final String CT_CUSTOMER_VAT_ID = "ctCustomerVatId";
    private static final String CT_CUSTOMER_IS_EMAIL_VERIFIED = "ctCustomerIsEmailVerified";
    private static final String CT_CUSTOMER_GROUP = "ctCustomerGroup";
    private static final String CT_CUSTOMER_ADRESSES = "ctCustomerAdresses";
    private static final String CT_CUSTOMER_DEFAULT_BILLING_ADDRESS = "ctDefaultBillingAddress";
    private static final String CT_CUSTOMER_DEFAULT_SHIPPING_ADDRESS = "ctDefaultShippingAddress";
    private static final String CT_CUSTOMER_CUSTOM = "ctCustomerCustom";

    private final Provider<Context> contextProvider;

    @Inject
    public CommerceToolsServices(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    public Project getProjectDetail(SphereClient sphereClient) {
        final BlockingSphereClient client = BlockingSphereClient.of(sphereClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        Project project = client.executeBlocking(ProjectGet.of());
        return project;
    }

    public PagedQueryResult<Category> getCategories(SphereClient pureAsyncClient, String parentId, Locale sortByLocale) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        QuerySort<Category> sortBy = CategoryQueryModel.of().name().locale(sortByLocale).sort().asc();
        CategoryQuery searchRequest = CategoryQuery.of()
                .withLimit(MAX_QUERY_LIMIT)
                .withSort(sortBy);

        if (StringUtils.isNotBlank(parentId)) {
            QueryPredicate<Category> predicateParentId = CategoryQueryModel.of().parent().id().is(parentId);
            searchRequest = searchRequest.withPredicates(predicateParentId);
        }

        return client.executeBlocking(searchRequest);
    }

    public PagedQueryResult<ProductProjection> getProducts(SphereClient pureAsyncClient, String parentId, Locale sortByLocale) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        final ProductProjectionQuery searchRequest = getProductsQuery(parentId, sortByLocale, 0, MAX_QUERY_LIMIT);

        return client.executeBlocking(searchRequest);
    }

    public PagedQueryResult<ProductProjection> getProductsByOffset(SphereClient pureAsyncClient, String parentId, Locale sortByLocale, int offset, int limit) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        final ProductProjectionQuery searchRequest = getProductsQuery(parentId, sortByLocale, offset, limit);

        return client.executeBlocking(searchRequest);
    }

    private ProductProjectionQuery getProductsQuery(String parentId, Locale sortByLocale, int offset, int limit) {
        QueryPredicate<ProductProjection> predicateParentId = ProductProjectionQueryModel.of().categories().id().is(parentId);
        CategoryExpansionModel<ProductProjection> expansionPathContainerFunction = ProductProjectionExpansionModel.of().categories();
        QuerySort<ProductProjection> sortBy = ProductProjectionQueryModel.of().name().locale(sortByLocale).sort().asc();

        ProductProjectionQuery searchRequest =
                ProductProjectionQuery.ofCurrent()
                        .withPredicates(predicateParentId)
                        .withExpansionPaths(expansionPathContainerFunction)
                        .withSort(sortBy);
        if (offset != 0) {
            searchRequest = searchRequest.withOffset(offset);
        }

        //default limit is 20
        if (limit != 0) {
            searchRequest = searchRequest.withLimit(limit);
        }

        return searchRequest;
    }

    public PagedSearchResult<ProductProjection> searchForProducts(SphereClient pureAsyncClient, String queryStr, Locale locale, int offset, int limit) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        CategoryExpansionModel<ProductProjection> expansionPathContainerFunction = ProductProjectionExpansionModel.of().categories();

        ProductProjectionSearch searchRequest =
                ProductProjectionSearch.ofCurrent()
                        .withText(locale, queryStr)
                        .withExpansionPaths(expansionPathContainerFunction);

        if (offset != 0) {
            searchRequest = searchRequest.withOffset(offset);
        }

        //default limit is 20
        if (limit != 0) {
            searchRequest = searchRequest.withLimit(limit);
        }

        return client.executeBlocking(searchRequest);
    }

    public Category getCategory(SphereClient pureAsyncClient, String categoryId) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        QueryPredicate<Category> predicateId = CategoryQueryModel.of().id().is(categoryId);

        final CategoryQuery searchRequest = CategoryQuery.of()
                .withPredicates(predicateId)
                .withLimit(1);
        final PagedQueryResult<Category> queryResult = client.executeBlocking(searchRequest);

        return queryResult.getResults().get(0);
    }

    public ProductProjection getProduct(SphereClient pureAsyncClient, String productId) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        QueryPredicate<ProductProjection> predicateId = ProductProjectionQueryModel.of().id().is(productId);
        CategoryExpansionModel<ProductProjection> expansionPathContainerFunction = ProductProjectionExpansionModel.of().categories();

        final ProductProjectionQuery searchRequest =
                ProductProjectionQuery.ofCurrent()
                        .withPredicates(predicateId)
                        .withExpansionPaths(expansionPathContainerFunction)
                        .withLimit(1);
        final PagedQueryResult<ProductProjection> queryResult = client.executeBlocking(searchRequest);

        return queryResult.getResults().get(0);
    }


    public Cart getOrCreateCart(SphereClient pureAsyncClient, String customerId, String cartId, CountryCode contextCountryCode, String currencyCode) {
        final Context context = contextProvider.get();

        return fetchCart(pureAsyncClient, customerId, cartId, contextCountryCode, currencyCode)
                .thenComposeAsync(new Function<Cart, CompletionStage<Cart>>() {
                    @Override
                    public CompletionStage<Cart> apply(Cart cart) {
                        overwriteCartSessionData(cart, context);
                        final boolean hasDifferentCountry = !contextCountryCode.equals(cart.getCountry());
                        return hasDifferentCountry ? updateCartCountry(pureAsyncClient, cart, contextCountryCode) : CompletableFuture.completedFuture(cart);
                    }
                }).toCompletableFuture().join();
    }

    private CompletionStage<Cart> fetchCart(SphereClient pureAsyncClient, String customerId, String cartId, CountryCode contextCountryCode, String currencyCode) {
        if (customerId != null) {
            return fetchCartByCustomerOrNew(pureAsyncClient, customerId, contextCountryCode, currencyCode);
        } else if (cartId != null) {
            return fetchCartByIdOrNew(pureAsyncClient, customerId, cartId, contextCountryCode, currencyCode);
        } else {
            return createCart(pureAsyncClient, customerId, contextCountryCode, currencyCode);
        }
    }

    private CompletionStage<Cart> fetchCartByIdOrNew(SphereClient pureAsyncClient, String customerId, String cartId, CountryCode contextCountryCode, String currencyCode) {
        ShippingMethodExpansionModel<Cart> expansionPathContainerFunction = CartExpansionModel.of().shippingInfo().shippingMethod();

        final CartByIdGet query = CartByIdGet.of(cartId)
                .withExpansionPaths(expansionPathContainerFunction);
        return pureAsyncClient.execute(query)
                .thenComposeAsync(new Function<Cart, CompletionStage<Cart>>() {
                    @Override
                    public CompletionStage<Cart> apply(Cart cart) {
                        return validateOrCreateNewCart(pureAsyncClient, customerId, cart, contextCountryCode, currencyCode);
                    }
                });
    }

    private CompletionStage<Cart> fetchCartByCustomerOrNew(SphereClient pureAsyncClient, String customerId, CountryCode contextCountryCode, String currencyCode) {
        ShippingMethodExpansionModel<Cart> expansionPathContainerFunction = CartExpansionModel.of().shippingInfo().shippingMethod();

        final CartByCustomerIdGet query = CartByCustomerIdGet.of(customerId)
                .withExpansionPaths(expansionPathContainerFunction);
        return pureAsyncClient.execute(query)
                .thenComposeAsync(new Function<Cart, CompletionStage<Cart>>() {
                    @Override
                    public CompletionStage<Cart> apply(Cart cart) {
                        return validateOrCreateNewCart(pureAsyncClient, customerId, cart, contextCountryCode, currencyCode);
                    }
                });
    }

    private CompletionStage<Cart> validateOrCreateNewCart(SphereClient pureAsyncClient, String customerId, @Nullable final Cart cart, CountryCode contextCountryCode, String currencyCode) {
        return Optional.ofNullable(cart)
                .filter(new Predicate<Cart>() {
                    @Override
                    public boolean test(Cart cart) {
                        return cart.getCartState().equals(CartState.ACTIVE);
                    }
                })
                .map(new Function<Cart, CompletionStage<Cart>>() {
                    @Override
                    public CompletionStage<Cart> apply(Cart cart) {
                        return CompletableFuture.completedFuture(cart);
                    }
                })
                .orElseGet(new Supplier<CompletionStage<Cart>>() {
                    @Override
                    public CompletionStage<Cart> get() {
                        return createCart(pureAsyncClient, customerId, contextCountryCode, currencyCode);
                    }
                });
    }

    private CompletionStage<Cart> createCart(SphereClient pureAsyncClient, String customerId, CountryCode contextCountryCode, String currencyCode) {
        final CartDraft cartDraft = CartDraftBuilder.of(Monetary.getCurrency(currencyCode))
                .country(contextCountryCode)
                .shippingAddress(Address.of(contextCountryCode))
                .customerId(customerId)
                .build();
        return pureAsyncClient.execute(CartCreateCommand.of(cartDraft));
    }

    /**
     * Updates the country of the cart, both {@code country} and {@code shippingAddress} country fields.
     * This is necessary in order to obtain prices with tax calculation.
     * @param cart the cart which country needs to be updated
     * @param country the country to set in the cart
     * @return the completionStage of a cart with the given country
     */
    private CompletionStage<Cart> updateCartCountry(SphereClient pureAsyncClient, final Cart cart, final CountryCode country) {
        final Address shippingAddress = Optional.ofNullable(cart.getShippingAddress())
                .map(new Function<Address, Address>() {
                    @Override
                    public Address apply(Address address) {
                        return address.withCountry(country);
                    }
                })
                .orElseGet(new Supplier<Address>() {
                    @Override
                    public Address get() {
                        return Address.of(country);
                    }
                });
        final CartUpdateCommand updateCommand = CartUpdateCommand.of(cart,
                asList(SetShippingAddress.of(shippingAddress), SetCountry.of(country)));
        return pureAsyncClient.execute(updateCommand);
    }

    private void overwriteCartSessionData(@Nullable final Cart cart, final Context context) {
        if (cart != null) {
            final String id = cart.getId();
            context.setAttribute(CT_CART_ID, id, Context.SESSION_SCOPE);
        } else {
            context.removeAttribute(CT_CART_ID, Context.SESSION_SCOPE);
        }
    }

    public Cart addItemToCart(Map<String, String> map, SphereClient client) throws ErrorResponseException {
        final String cartId = map.get(CT_CART_ID);
        final Cart cart = getOrCreateCart(client, null, cartId, CountryCode.getByCode(map.get(CT_COUNTRY_CODE)), map.get(CT_CURRENCY_CODE));
        final AddLineItem updateAction = AddLineItem.of(map.get(CT_PRODUCT_ID), Integer.parseInt(map.get(CT_VARIANT_ID)), Long.parseLong(map.get(CT_PRODUCT_QUANTITY)));
        return client.execute(CartUpdateCommand.of(cart, updateAction)).toCompletableFuture().join();
    }

    public Cart editItemInCart(Map<String, String> map, SphereClient client) throws ErrorResponseException {
        final String cartId = map.get(CT_CART_ID);
        final Cart cart = getOrCreateCart(client, null, cartId, CountryCode.getByCode(map.get(CT_COUNTRY_CODE)), map.get(CT_CURRENCY_CODE));
        return client.execute(CartUpdateCommand.of(cart, ChangeLineItemQuantity.of(map.get(CT_LINE_ITEM_ID), Long.parseLong(map.get(CT_PRODUCT_QUANTITY))))).toCompletableFuture().join();
    }

    public Cart removeItemFromCart(Map<String, String> map, SphereClient client) throws ErrorResponseException {
        final String cartId = map.get(CT_CART_ID);
        final Cart cart = getOrCreateCart(client, null, cartId, CountryCode.getByCode(map.get(CT_COUNTRY_CODE)), map.get(CT_CURRENCY_CODE));
        return client.execute(CartUpdateCommand.of(cart, RemoveLineItem.of(map.get(CT_LINE_ITEM_ID)))).toCompletableFuture().join();
    }

    public CustomerDraft getCustomerDraft() {
        final Context context = contextProvider.get();

        return CustomerDraftBuilder.of(context.getAttribute(CT_CUSTOMER_EMAIL), context.getAttribute(CT_CUSTOMER_PASSWORD))
                .customerNumber(context.getAttribute(CT_CUSTOMER_NUMBER))
                .firstName((String) context.getOrDefault(CT_CUSTOMER_FIRST_NAME, ""))
                .lastName((String) context.getOrDefault(CT_CUSTOMER_LAST_NAME, ""))
                .middleName(context.getAttribute(CT_CUSTOMER_MIDDLE_NAME))
                .title(context.getAttribute(CT_CUSTOMER_TITLE))
                .anonymousCartId(context.getAttribute(CT_CART_ID))
                .externalId(context.getAttribute(CT_CUSTOMER_EXTERNAL_ID))
                .dateOfBirth(context.getAttribute(CT_CUSTOMER_DATE_OF_BIRTH))
                .companyName(context.getAttribute(CT_CUSTOMER_COMPANY_NAME))
                .vatId(context.getAttribute(CT_CUSTOMER_VAT_ID))
                .isEmailVerified(context.getAttribute(CT_CUSTOMER_IS_EMAIL_VERIFIED))
                .customerGroup(context.getAttribute(CT_CUSTOMER_GROUP))
                .addresses((List<Address>) context.getOrDefault(CT_CUSTOMER_ADRESSES, new ArrayList<Address>()))
                .defaultBillingAddress(context.getAttribute(CT_CUSTOMER_DEFAULT_BILLING_ADDRESS))
                .defaultShippingAddress(context.getAttribute(CT_CUSTOMER_DEFAULT_SHIPPING_ADDRESS))
                .custom(context.getAttribute(CT_CUSTOMER_CUSTOM))
                .build();
    }
}
