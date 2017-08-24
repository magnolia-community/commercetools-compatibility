/**
 * This file Copyright (c) 2016-2017 Magnolia International
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

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.context.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.money.Monetary;

import org.apache.commons.lang3.ObjectUtils;
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
import io.sphere.sdk.categories.CategoryTree;
import io.sphere.sdk.categories.expansion.CategoryExpansionModel;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.categories.queries.CategoryQueryModel;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.ErrorResponseException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerDraftBuilder;
import io.sphere.sdk.customers.CustomerToken;
import io.sphere.sdk.customers.commands.CustomerCreatePasswordTokenCommand;
import io.sphere.sdk.customers.commands.CustomerPasswordResetCommand;
import io.sphere.sdk.models.Address;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;
import io.sphere.sdk.products.attributes.AttributeType;
import io.sphere.sdk.products.attributes.BooleanAttributeType;
import io.sphere.sdk.products.attributes.DateAttributeType;
import io.sphere.sdk.products.attributes.DateTimeAttributeType;
import io.sphere.sdk.products.attributes.EnumAttributeType;
import io.sphere.sdk.products.attributes.LocalizedEnumAttributeType;
import io.sphere.sdk.products.attributes.LocalizedStringAttributeType;
import io.sphere.sdk.products.attributes.MoneyAttributeType;
import io.sphere.sdk.products.attributes.NumberAttributeType;
import io.sphere.sdk.products.attributes.ReferenceAttributeType;
import io.sphere.sdk.products.attributes.StringAttributeType;
import io.sphere.sdk.products.attributes.TimeAttributeType;
import io.sphere.sdk.products.expansion.ProductProjectionExpansionModel;
import io.sphere.sdk.products.queries.ProductProjectionQuery;
import io.sphere.sdk.products.queries.ProductProjectionQueryModel;
import io.sphere.sdk.products.search.ProductAttributeFacetSearchModel;
import io.sphere.sdk.products.search.ProductAttributeFacetedSearchSearchModel;
import io.sphere.sdk.products.search.ProductProjectionSearch;
import io.sphere.sdk.products.search.ProductProjectionSearchModel;
import io.sphere.sdk.producttypes.ProductType;
import io.sphere.sdk.producttypes.ProductTypeLocalRepository;
import io.sphere.sdk.producttypes.queries.ProductTypeQuery;
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.QueryPredicate;
import io.sphere.sdk.queries.QuerySort;
import io.sphere.sdk.search.PagedSearchResult;
import io.sphere.sdk.search.model.RangeTermFacetedSearchSearchModel;
import io.sphere.sdk.search.model.TermFacetedSearchSearchModel;
import io.sphere.sdk.shippingmethods.ShippingMethod;
import io.sphere.sdk.shippingmethods.expansion.ShippingMethodExpansionModel;
import io.sphere.sdk.shippingmethods.queries.ShippingMethodByIdGet;
import io.sphere.sdk.shippingmethods.queries.ShippingMethodQuery;

/**
 * Service class containing commercetools queries used by several classes.
 */
@Singleton
public class CommercetoolsServices {

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
    public CommercetoolsServices(final Provider<Context> contextProvider) {
        this.contextProvider = contextProvider;
    }

    public Project getProjectDetail(SphereClient sphereClient) {
        final BlockingSphereClient client = BlockingSphereClient.of(sphereClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        return client.executeBlocking(ProjectGet.of());
    }

    public PagedQueryResult<Category> getCategories(SphereClient pureAsyncClient, String parentId, Locale sortByLocale) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        CategoryQuery searchRequest = CategoryQuery.of()
                .withLimit(MAX_QUERY_LIMIT);

        if (StringUtils.isNotBlank(parentId)) {
            QueryPredicate<Category> predicateParentId = CategoryQueryModel.of().parent().id().is(parentId);
            searchRequest = searchRequest.withPredicates(predicateParentId);
        }
        if (sortByLocale != null) {
            QuerySort<Category> sortBy = CategoryQueryModel.of().name().locale(sortByLocale).sort().asc();
            searchRequest = searchRequest.withSort(sortBy);
        }

        return client.executeBlocking(searchRequest);
    }

    public CategoryTree getCategoryTree(SphereClient pureAsyncClient) {
        return CategoryTree.of(sortCategories(getCategories(pureAsyncClient, null, null).getResults()));
    }

    private static List<Category> sortCategories(final List<Category> categories) {
        categories.sort((c1, c2) -> ObjectUtils.compare(c1.getOrderHint(), c2.getOrderHint()));

        return categories;
    }

    public ProductTypeLocalRepository getProductTypes(SphereClient pureAsyncClient) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);

        return ProductTypeLocalRepository.of(client.execute(ProductTypeQuery.of().toQuery()).toCompletableFuture().join().getResults());
    }

    private Map<String, List<AttributeType>> getAttributeNameTypeMap(ProductTypeLocalRepository productTypes, List<String> attributeFacets) {
        Map<String, List<AttributeType>> attributeNameTypeMap = new HashMap<>();
        for (String attributeName : attributeFacets) {
            for (ProductType productType : productTypes.getAll()) {
                productType.findAttribute(attributeName).ifPresent(attributeDefinition -> {
                    attributeNameTypeMap.putIfAbsent(attributeName, new ArrayList<>());
                    attributeNameTypeMap.get(attributeName).add(attributeDefinition.getAttributeType());
                });

            }
        }

        return attributeNameTypeMap;
    }

    public PagedSearchResult<ProductProjection> getProducts(SphereClient pureAsyncClient, String parentId, Locale sortByLocale) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        final ProductProjectionSearch searchRequest = getProductsQuery(parentId, false, sortByLocale, 0, MAX_QUERY_LIMIT, null, null, null, null);

        return client.executeBlocking(searchRequest);
    }

    public PagedSearchResult<ProductProjection> getProductsByOffset(SphereClient pureAsyncClient, String parentId, Locale sortByLocale, int offset, int limit, ProductTypeLocalRepository productTypes, List<String> attributeFacets, Map<String, List<String>> filterBy) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        final ProductProjectionSearch searchRequest = getProductsQuery(parentId, true, sortByLocale, offset, limit, productTypes, attributeFacets, filterBy, null);

        return client.executeBlocking(searchRequest);
    }

    public PagedSearchResult<ProductProjection> searchForProducts(SphereClient pureAsyncClient, String queryStr, Locale sortByLocale, int offset, int limit, ProductTypeLocalRepository productTypes, List<String> attributeFacets, Map<String, List<String>> filterBy) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        final ProductProjectionSearch searchRequest = getProductsQuery(null, false, sortByLocale, offset, limit, productTypes, attributeFacets, filterBy, queryStr);

        return client.executeBlocking(searchRequest);
    }

    private ProductProjectionSearch getProductsQuery(String parentId, boolean includeSubtree, Locale locale, int offset, int limit, ProductTypeLocalRepository productTypes, List<String> attributeFacets, Map<String, List<String>> filterBy, String queryStr) {

        ProductProjectionSearch searchRequest =
                ProductProjectionSearch.ofCurrent()
                        .withExpansionPaths(ProductProjectionExpansionModel.of().categories())
                        .withSort(ProductProjectionSearchModel.of().sort().name().locale(locale).asc());

        if (parentId != null) {
            if (includeSubtree) {
                searchRequest = searchRequest.withQueryFilters(productProjectionFilterSearchModel -> productProjectionFilterSearchModel.categories().id().isInSubtree(parentId));
            } else {
                searchRequest = searchRequest.withQueryFilters(productProjectionFilterSearchModel -> productProjectionFilterSearchModel.categories().id().is(parentId));
            }
        }

        if (offset != 0) {
            searchRequest = searchRequest.withOffset(offset);
        }

        //default limit is 20
        if (limit != 0) {
            searchRequest = searchRequest.withLimit(limit);
        }

        final ProductAttributeFacetSearchModel attributeFacetModel = ProductProjectionSearchModel.of().facet().allVariants().attribute();

        if (attributeFacets != null && productTypes != null) {
            Map<String, List<AttributeType>> attributeNameTypeMap = getAttributeNameTypeMap(productTypes, attributeFacets);
            for (String attributeName : attributeFacets) {
                for (AttributeType attributeType : attributeNameTypeMap.get(attributeName)) {
                    if (attributeType instanceof LocalizedStringAttributeType) {
                        searchRequest = searchRequest.plusFacets(attributeFacetModel.ofLocalizedString(attributeName).locale(locale).allTerms());
                    } else if (attributeType instanceof EnumAttributeType) {
                        searchRequest = searchRequest.plusFacets(attributeFacetModel.ofEnum(attributeName).label().allTerms());
                    } else if (attributeType instanceof LocalizedEnumAttributeType) {
                        searchRequest = searchRequest.plusFacets(attributeFacetModel.ofLocalizedEnum(attributeName).label().locale(locale).allTerms());
                    } else {
                        searchRequest = searchRequest.plusFacets(attributeFacetModel.ofString(attributeName).allTerms());
                    }
                }
            }

            final ProductAttributeFacetedSearchSearchModel attributeSearchModel = ProductProjectionSearchModel.of().facetedSearch().allVariants().attribute();
            if (filterBy != null) {
                for (String facet : filterBy.keySet()) {
                    TermFacetedSearchSearchModel<ProductProjection> searchModel = null;
                    RangeTermFacetedSearchSearchModel<ProductProjection> rangeSearchModel = null;
                    for (AttributeType attributeType : attributeNameTypeMap.get(facet)) {
                        if (attributeType instanceof StringAttributeType) {
                            searchModel = attributeSearchModel.ofString(facet);
                        } else if (attributeType instanceof LocalizedStringAttributeType) {
                            searchModel = attributeSearchModel.ofLocalizedString(facet).locale(locale);
                        } else if (attributeType instanceof EnumAttributeType) {
                            searchModel = attributeSearchModel.ofEnum(facet).label();
                        } else if (attributeType instanceof LocalizedEnumAttributeType) {
                            searchModel = attributeSearchModel.ofLocalizedEnum(facet).label().locale(locale);
                        } else if (attributeType instanceof BooleanAttributeType) {
                            searchModel = attributeSearchModel.ofBoolean(facet);
                        } else if (attributeType instanceof DateAttributeType) {
                            rangeSearchModel = attributeSearchModel.ofDate(facet);
                        } else if (attributeType instanceof TimeAttributeType) {
                            rangeSearchModel = attributeSearchModel.ofTime(facet);
                        } else if (attributeType instanceof MoneyAttributeType) {
                            rangeSearchModel = attributeSearchModel.ofMoney(facet).centAmount();
                        } else if (attributeType instanceof NumberAttributeType) {
                            rangeSearchModel = attributeSearchModel.ofNumber(facet);
                        } else if (attributeType instanceof ReferenceAttributeType) {
                            searchModel = attributeSearchModel.ofReference(facet).id();
                        } else if (attributeType instanceof DateTimeAttributeType) {
                            rangeSearchModel = attributeSearchModel.ofNumber(facet);
                        }
                    }
                    if (searchModel != null) {
                        searchRequest = searchRequest.plusResultFilters(searchModel.isIn(filterBy.get(facet)).filterExpressions());
                    } else if (rangeSearchModel != null) {
                        searchRequest = searchRequest.plusResultFilters(rangeSearchModel.isIn(filterBy.get(facet)).filterExpressions());
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(queryStr)) {
            searchRequest = searchRequest.withText(locale, queryStr);
        }

        return searchRequest;
    }

    public Category getCategory(SphereClient pureAsyncClient, String categoryId) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        QueryPredicate<Category> predicateId = CategoryQueryModel.of().id().is(categoryId);
        CategoryExpansionModel<Category> expansionPathContainerFunction = CategoryExpansionModel.of().ancestors();

        final CategoryQuery searchRequest = CategoryQuery.of()
                .withPredicates(predicateId)
                .withExpansionPaths(expansionPathContainerFunction)
                .withLimit(1);
        final PagedQueryResult<Category> queryResult = client.executeBlocking(searchRequest);

        return queryResult.getResults().isEmpty() ? null : queryResult.getResults().get(0);
    }

    public ProductProjection getProduct(SphereClient pureAsyncClient, String productId) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommercetoolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        QueryPredicate<ProductProjection> predicateId = ProductProjectionQueryModel.of().id().is(productId);
        CategoryExpansionModel<ProductProjection> expansionPathContainerFunction = ProductProjectionExpansionModel.of().categories().ancestors();

        final ProductProjectionQuery searchRequest =
                ProductProjectionQuery.ofCurrent()
                        .withPredicates(predicateId)
                        .withExpansionPaths(expansionPathContainerFunction)
                        .withLimit(1);
        final PagedQueryResult<ProductProjection> queryResult = client.executeBlocking(searchRequest);

        return queryResult.getResults().isEmpty() ? null : queryResult.getResults().get(0);
    }


    public Cart getOrCreateCart(SphereClient pureAsyncClient, String projectName, String customerId, String cartId, CountryCode contextCountryCode, String currencyCode) {
        final Context context = contextProvider.get();

        return fetchCart(pureAsyncClient, customerId, cartId, contextCountryCode, currencyCode)
                .thenComposeAsync(cart -> {
                    overwriteCartSessionData(cart, projectName, context);
                    final boolean hasDifferentCountry = !contextCountryCode.equals(cart.getCountry());
                    return hasDifferentCountry ? updateCartCountry(pureAsyncClient, cart, contextCountryCode) : CompletableFuture.completedFuture(cart);
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
                .thenComposeAsync(cart -> validateOrCreateNewCart(pureAsyncClient, customerId, cart, contextCountryCode, currencyCode));
    }

    private CompletionStage<Cart> fetchCartByCustomerOrNew(SphereClient pureAsyncClient, String customerId, CountryCode contextCountryCode, String currencyCode) {
        ShippingMethodExpansionModel<Cart> expansionPathContainerFunction = CartExpansionModel.of().shippingInfo().shippingMethod();

        final CartByCustomerIdGet query = CartByCustomerIdGet.of(customerId)
                .withExpansionPaths(expansionPathContainerFunction);
        return pureAsyncClient.execute(query)
                .thenComposeAsync(cart -> validateOrCreateNewCart(pureAsyncClient, customerId, cart, contextCountryCode, currencyCode));
    }

    private CompletionStage<Cart> validateOrCreateNewCart(SphereClient pureAsyncClient, String customerId, @Nullable final Cart cart, CountryCode contextCountryCode, String currencyCode) {
        return Optional.ofNullable(cart)
                .filter(cart1 -> cart1.getCartState().equals(CartState.ACTIVE))
                .map((Function<Cart, CompletionStage<Cart>>) CompletableFuture::completedFuture)
                .orElseGet(() -> createCart(pureAsyncClient, customerId, contextCountryCode, currencyCode));
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
                .map(address -> address.withCountry(country))
                .orElseGet(() -> Address.of(country));
        final CartUpdateCommand updateCommand = CartUpdateCommand.of(cart,
                asList(SetShippingAddress.of(shippingAddress), SetCountry.of(country)));
        return pureAsyncClient.execute(updateCommand);
    }

    private void overwriteCartSessionData(@Nullable final Cart cart, final String projectName, final Context context) {
        if (cart != null) {
            final String id = cart.getId();
            context.setAttribute(projectName + "_" + CT_CART_ID, id, Context.SESSION_SCOPE);
        } else {
            context.removeAttribute(projectName + "_" + CT_CART_ID, Context.SESSION_SCOPE);
        }
    }

    public Cart addItemToCart(Map<String, String> map, SphereClient client) throws ErrorResponseException {
        final String cartId = map.get(CT_CART_ID);
        final String projectName = map.get(CommercetoolsIntegrationModule.PROJECT_PARAM_NAME);
        final Cart cart = getOrCreateCart(client, projectName, null, cartId, CountryCode.getByCode(map.get(CT_COUNTRY_CODE)), map.get(CT_CURRENCY_CODE));
        final AddLineItem updateAction = AddLineItem.of(map.get(CT_PRODUCT_ID), Integer.parseInt(map.get(CT_VARIANT_ID)), Long.parseLong(map.get(CT_PRODUCT_QUANTITY)));
        return client.execute(CartUpdateCommand.of(cart, updateAction)).toCompletableFuture().join();
    }

    public Cart editItemInCart(Map<String, String> map, SphereClient client) throws ErrorResponseException {
        final String cartId = map.get(CT_CART_ID);
        final String projectName = map.get(CommercetoolsIntegrationModule.PROJECT_PARAM_NAME);
        final Cart cart = getOrCreateCart(client, projectName, null, cartId, CountryCode.getByCode(map.get(CT_COUNTRY_CODE)), map.get(CT_CURRENCY_CODE));
        return client.execute(CartUpdateCommand.of(cart, ChangeLineItemQuantity.of(map.get(CT_LINE_ITEM_ID), Long.parseLong(map.get(CT_PRODUCT_QUANTITY))))).toCompletableFuture().join();
    }

    public Cart removeItemFromCart(Map<String, String> map, SphereClient client) throws ErrorResponseException {
        final String cartId = map.get(CT_CART_ID);
        final String projectName = map.get(CommercetoolsIntegrationModule.PROJECT_PARAM_NAME);
        final Cart cart = getOrCreateCart(client, projectName, null, cartId, CountryCode.getByCode(map.get(CT_COUNTRY_CODE)), map.get(CT_CURRENCY_CODE));
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

    public ProductVariant getVariant(SphereClient pureAsyncClient, String productId, String variantId) {
        final ProductProjection productProjection = getProduct(pureAsyncClient, productId);

        return productProjection.getVariant(Integer.parseInt(variantId));
    }

    public CustomerToken getCustomerPasswordToken(SphereClient client, String customerEmail) {
        return client.execute(CustomerCreatePasswordTokenCommand.of(customerEmail)).toCompletableFuture().join();
    }

    public Customer customerPasswordReset(SphereClient client, String customerTokenValue, String newPassword) {
        return client.execute(CustomerPasswordResetCommand.ofTokenAndPassword(customerTokenValue, newPassword)).toCompletableFuture().join();
    }

    public ShippingMethod getShippingMethod(SphereClient client, String id) {
        return client.execute(ShippingMethodByIdGet.of(id)).toCompletableFuture().join();
    }

    public PagedQueryResult<ShippingMethod> getShippingMethodList(SphereClient client) {
        return client.execute(ShippingMethodQuery.of().withLimit(MAX_QUERY_LIMIT)).toCompletableFuture().join();
    }
}
