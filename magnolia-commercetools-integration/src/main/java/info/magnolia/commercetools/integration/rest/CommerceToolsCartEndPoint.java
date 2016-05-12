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

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.RepositoryException;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.neovisionaries.i18n.CountryCode;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiResponse;
import com.wordnik.swagger.annotations.ApiResponses;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.client.SphereClient;

/**
 * REST service for executing cart operations.
 */
@Api(value = "/ctCart", description = "The commercetools cart API")
@Path("/ctCart")
public class CommerceToolsCartEndPoint extends AbstractEndpoint<ConfiguredEndpointDefinition> {

    private static final String STATUS_MESSAGE_OK = "OK";
    private static final String STATUS_MESSAGE_ERROR_OCCURRED = "Error occurred";
    private static final String PATH_PROJECT_COUNTRY_CURRENCY_CONSTANT = "/{" + CommerceToolsIntegrationModule.PROJECT_PARAM_NAME + "}/{" + CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME + "}/{" + CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME + "}";
    private static final String PATH_PROJECT_COUNTRY_CURRENCY_PRODUCTID_VARIANTID_CONSTANT = PATH_PROJECT_COUNTRY_CURRENCY_CONSTANT + "/{" + CommerceToolsServices.CT_PRODUCT_ID + "}/{" + CommerceToolsServices.CT_VARIANT_ID + "}";
    private static final String PATH_PROJECT_COUNTRY_CURRENCY_CARTID_LINEITEMID_CONSTANT = PATH_PROJECT_COUNTRY_CURRENCY_CONSTANT + "/{" + CommerceToolsServices.CT_CART_ID + "}/{" + CommerceToolsServices.CT_LINE_ITEM_ID + "}";
    private static final String PATH_PROJECT_COUNTRY_CURRENCY_CARTID_LINEITEMID_QUANTITY_CONSTANT = PATH_PROJECT_COUNTRY_CURRENCY_CARTID_LINEITEMID_CONSTANT + "/{" + CommerceToolsServices.CT_PRODUCT_QUANTITY + "}";

    private final Logger log = LoggerFactory.getLogger(getClass());

    private final CommerceToolsServices commerceToolsServices;
    private final Provider<CommerceToolsIntegrationModule> moduleProvider;

    @Inject
    protected CommerceToolsCartEndPoint(final ConfiguredEndpointDefinition endpointDefinition, final CommerceToolsServices commerceToolsServices, final Provider<CommerceToolsIntegrationModule> moduleProvider) {
        super(endpointDefinition);
        this.commerceToolsServices = commerceToolsServices;
        this.moduleProvider = moduleProvider;
    }

    /**
     * Returns a cart.
     */
    @GET
    @Path(PATH_PROJECT_COUNTRY_CURRENCY_CONSTANT)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Get a cart", notes = "Returns a cart")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK, response = Cart.class),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response getCart(
            @PathParam(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME) String projectName,
            @PathParam(CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME) String countryCode,
            @PathParam(CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME) String currencyCode,
            @QueryParam(CommerceToolsServices.CT_CUSTOMER_ID) String customer,
            @QueryParam(CommerceToolsServices.CT_CART_ID) String cartId) throws RepositoryException {

        final Cart response = commerceToolsServices.getOrCreateCart(getProjectClient(projectName), customer, cartId, CountryCode.getByCode(countryCode), currencyCode);

        log.debug("Returned cart [{}]", response.getId());

        return Response.ok(response).build();
    }

    /**
     * Add item to cart and returns a cart.
     */
    @PUT
    @Path(PATH_PROJECT_COUNTRY_CURRENCY_PRODUCTID_VARIANTID_CONSTANT)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Add item to a cart", notes = "Returns a cart with added item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK, response = Cart.class),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response addItem(
            @PathParam(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME) String projectName,
            @PathParam(CommerceToolsServices.CT_PRODUCT_ID) String productId,
            @PathParam(CommerceToolsServices.CT_VARIANT_ID) String variantId,
            @PathParam(CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME) String countryCode,
            @PathParam(CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME) String currencyCode,
            @QueryParam(CommerceToolsServices.CT_CART_ID) String cartId,
            @QueryParam(CommerceToolsServices.CT_PRODUCT_QUANTITY) @DefaultValue("1") String quantity) throws RepositoryException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(CommerceToolsServices.CT_PRODUCT_ID, productId);
        parameters.put(CommerceToolsServices.CT_VARIANT_ID, variantId);
        parameters.put(CommerceToolsServices.CT_PRODUCT_QUANTITY, quantity);
        parameters.put(CommerceToolsServices.CT_COUNTRY_CODE, countryCode);
        parameters.put(CommerceToolsServices.CT_CURRENCY_CODE, currencyCode);
        parameters.put(CommerceToolsServices.CT_CART_ID, cartId);

        final Cart response = commerceToolsServices.addItemToCart(parameters, getProjectClient(projectName));

        log.debug("Returned cart [{}]", response.getId());

        return Response.ok(response).build();
    }

    /**
     * Edit item in cart and returns a cart.
     */
    @POST
    @Path(PATH_PROJECT_COUNTRY_CURRENCY_CARTID_LINEITEMID_QUANTITY_CONSTANT)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Change quantity of item in a cart", notes = "Returns a cart after changing quantity of item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK, response = Cart.class),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response editItem(
            @PathParam(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME) String projectName,
            @PathParam(CommerceToolsServices.CT_CART_ID) String cartId,
            @PathParam(CommerceToolsServices.CT_LINE_ITEM_ID) String lineItemId,
            @PathParam(CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME) String countryCode,
            @PathParam(CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME) String currencyCode,
            @PathParam(CommerceToolsServices.CT_PRODUCT_QUANTITY) String quantity) throws RepositoryException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(CommerceToolsServices.CT_CART_ID, cartId);
        parameters.put(CommerceToolsServices.CT_LINE_ITEM_ID, lineItemId);
        parameters.put(CommerceToolsServices.CT_PRODUCT_QUANTITY, quantity);
        parameters.put(CommerceToolsServices.CT_COUNTRY_CODE, countryCode);
        parameters.put(CommerceToolsServices.CT_CURRENCY_CODE, currencyCode);

        final Cart response = commerceToolsServices.editItemInCart(parameters, getProjectClient(projectName));

        log.debug("Returned cart [{}]", response.getId());

        return Response.ok(response).build();
    }

    /**
     * Delete item from cart and returns cart.
     */
    @DELETE
    @Path(PATH_PROJECT_COUNTRY_CURRENCY_CARTID_LINEITEMID_CONSTANT)
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    @ApiOperation(value = "Remove item from a cart", notes = "Returns a cart after removing item")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = STATUS_MESSAGE_OK, response = Cart.class),
            @ApiResponse(code = 500, message = STATUS_MESSAGE_ERROR_OCCURRED)
    })
    public Response removeItem(
            @PathParam(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME) String projectName,
            @PathParam(CommerceToolsServices.CT_CART_ID) String cartId,
            @PathParam(CommerceToolsServices.CT_LINE_ITEM_ID) String lineItemId,
            @PathParam(CommerceToolsIntegrationModule.COUNTRY_PARAM_NAME) String countryCode,
            @PathParam(CommerceToolsIntegrationModule.CURRENCY_PARAM_NAME) String currencyCode) throws RepositoryException {

        Map<String, String> parameters = new HashMap<>();
        parameters.put(CommerceToolsServices.CT_CART_ID, cartId);
        parameters.put(CommerceToolsServices.CT_LINE_ITEM_ID, lineItemId);
        parameters.put(CommerceToolsServices.CT_COUNTRY_CODE, countryCode);
        parameters.put(CommerceToolsServices.CT_CURRENCY_CODE, currencyCode);

        final Cart response = commerceToolsServices.removeItemFromCart(parameters, getProjectClient(projectName));

        log.debug("Returned cart [{}]", response.getId());

        return Response.ok(response).build();
    }

    private SphereClient getProjectClient(String projectName) {
        return moduleProvider.get().getSphereClient(projectName);
    }
}
