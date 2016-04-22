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

import static java.util.concurrent.TimeUnit.SECONDS;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;

import java.util.Locale;

import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;

import io.sphere.sdk.categories.Category;
import io.sphere.sdk.categories.expansion.CategoryExpansionModel;
import io.sphere.sdk.categories.queries.CategoryQuery;
import io.sphere.sdk.categories.queries.CategoryQueryModel;
import io.sphere.sdk.client.BlockingSphereClient;
import io.sphere.sdk.client.SphereClient;
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

/**
 * Service class containing CommerceTools queries used by several classes.
 */
@Singleton
public class CommerceToolsServices {

    /**
     * See documentation for <a href="http://dev.commercetools.com/http-api.html#limit">limit</a>.
     */
    private static final int MAX_QUERY_LIMIT = 500;

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
        final ProductProjectionQuery searchRequest = getProductsQuery(parentId,sortByLocale, 0, MAX_QUERY_LIMIT);

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
}
