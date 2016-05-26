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
package info.magnolia.commercetools.integration.rest;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.commercetools.integration.service.CommerceToolsServices;
import info.magnolia.rest.AbstractEndpoint;
import info.magnolia.rest.registry.ConfiguredEndpointDefinition;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.RepositoryException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.products.ProductVariant;

/**
 * REST service for changing variant in product detail.
 */
@Api(value = "/ctVariant", description = "The commercetools variant API")
@Path("/ctVariant")
public class CommerceToolsVariantEndPoint extends AbstractEndpoint<ConfiguredEndpointDefinition> {

    private static final String STATUS_MESSAGE_OK = "OK";
    private static final String STATUS_MESSAGE_ERROR_OCCURRED = "Error occurred";
    private static final String PATH_PROJECT_PRODUCTID_VARIANTID_CONSTANT = "/{" + CommerceToolsIntegrationModule.PROJECT_PARAM_NAME + "}/{" + CommerceToolsServices.CT_PRODUCT_ID + "}/{" + CommerceToolsServices.CT_VARIANT_ID + "}";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CommerceToolsServices commerceToolsServices;
    private final Provider<CommerceToolsIntegrationModule> moduleProvider;

    @Inject
    protected CommerceToolsVariantEndPoint(ConfiguredEndpointDefinition endpointDefinition, final CommerceToolsServices commerceToolsServices, final Provider<CommerceToolsIntegrationModule> moduleProvider) {
        super(endpointDefinition);
        this.commerceToolsServices = commerceToolsServices;
        this.moduleProvider = moduleProvider;
    }

    /**
     * Returns a cart.
     */
    @GET
    @Path(PATH_PROJECT_PRODUCTID_VARIANTID_CONSTANT)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get product variant", notes = "Returns a product variant")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK, response = Cart.class),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response getVariant(
            @PathParam(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME) String projectName,
            @PathParam(CommerceToolsServices.CT_PRODUCT_ID) String productId,
            @PathParam(CommerceToolsServices.CT_VARIANT_ID) String variantId) throws RepositoryException {

        final ProductVariant response = commerceToolsServices.getVariant(getProjectClient(projectName), productId, variantId);

        log.debug("Returned product variant [{}]", response.getId());

        return Response.ok(response).build();
    }

    private SphereClient getProjectClient(String projectName) {
        return moduleProvider.get().getSphereClient(projectName);
    }
}
