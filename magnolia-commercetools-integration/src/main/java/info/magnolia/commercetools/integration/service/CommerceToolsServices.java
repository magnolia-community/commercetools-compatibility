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

import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionStage;

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
import io.sphere.sdk.projects.Project;
import io.sphere.sdk.projects.queries.ProjectGet;
import io.sphere.sdk.queries.PagedQueryResult;
import io.sphere.sdk.queries.QueryExecutionUtils;
import io.sphere.sdk.queries.QueryPredicate;
import io.sphere.sdk.queries.QuerySort;

/**
 * Service class used by several model classes.
 */
@Singleton
public class CommerceToolsServices {

    public Project getProjectDetail(SphereClient sphereClient) {
        final BlockingSphereClient client = BlockingSphereClient.of(sphereClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        Project project = client.executeBlocking(ProjectGet.of());
        return project;
    }

    public List<Category> getCategories(SphereClient pureAsyncClient, String parentId, Locale sortByLocale) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
        QuerySort<Category> sortBy = CategoryQueryModel.of().name().locale(sortByLocale).sort().asc();
        CategoryQuery searchRequest = CategoryQuery.of()
                .withSort(sortBy);

        if (StringUtils.isNotBlank(parentId)) {
            QueryPredicate<Category> predicateParentId = CategoryQueryModel.of().parent().id().is(parentId);
            searchRequest = searchRequest.withPredicates(predicateParentId);
        }

        final CompletionStage<List<Category>> categoriesStage = QueryExecutionUtils.queryAll(client, searchRequest);

        return categoriesStage.toCompletableFuture().join();
    }

    public List<ProductProjection> getProducts(SphereClient pureAsyncClient, String parentId, Locale sortByLocale, int offset, int limit) {
        final BlockingSphereClient client = BlockingSphereClient.of(pureAsyncClient, CommerceToolsIntegrationModule.DEFAULT_QUERY_TIMEOUT, SECONDS);
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
        if (limit != 0) {
            searchRequest = searchRequest.withLimit(limit);
        }
        if (offset != 0 || limit != 0) {
            return client.executeBlocking(searchRequest).getResults();
        }

        return QueryExecutionUtils.queryAll(client, searchRequest).toCompletableFuture().join();
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
