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
package info.magnolia.commercetools.integration.form;

import info.magnolia.commercetools.integration.service.CommerceToolsServices;
import info.magnolia.commercetools.integration.templating.CommerceToolsTemplatingFunctions;
import info.magnolia.context.Context;
import info.magnolia.context.WebContext;
import info.magnolia.module.form.processors.AbstractFormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;

import java.util.Map;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sphere.sdk.carts.Cart;
import io.sphere.sdk.carts.commands.CartUpdateCommand;
import io.sphere.sdk.carts.commands.updateactions.SetShippingMethod;
import io.sphere.sdk.models.SphereException;
import io.sphere.sdk.orders.Order;
import io.sphere.sdk.orders.OrderFromCartDraft;
import io.sphere.sdk.orders.PaymentState;
import io.sphere.sdk.orders.commands.OrderFromCartCreateCommand;
import io.sphere.sdk.shippingmethods.ShippingMethod;

/**
 * Create order from cart.
 */
public class CommerceToolsCartProcessor extends AbstractFormProcessor {

    private static final Logger log = LoggerFactory.getLogger(CommerceToolsCartProcessor.class);

    private static final String CT_ACTION_PARAM = "ctAction";
    private static final String CT_SHIPPING_METHOD_PARAM = "ctShippingMethod";

    private static final String CT_ACTION_SET_SHIPPING = "setShipping";
    private static final String CT_ACTION_ORDER = "order";

    private final Provider<WebContext> webContextProvider;
    private final CommerceToolsTemplatingFunctions commerceToolsTemplatingFunctions;

    @Inject
    public CommerceToolsCartProcessor(Provider<WebContext> webContextProvider, CommerceToolsTemplatingFunctions commerceToolsTemplatingFunctions) {
        this.webContextProvider = webContextProvider;
        this.commerceToolsTemplatingFunctions = commerceToolsTemplatingFunctions;
    }

    @Override
    public void internalProcess(Node content, Map<String, Object> parameters) throws FormProcessorFailedException {
        final WebContext webContext = webContextProvider.get();
        final Cart cart = commerceToolsTemplatingFunctions.getCart();
        if (StringUtils.equals(parameters.get(CT_ACTION_PARAM).toString(), CT_ACTION_SET_SHIPPING)){
            final ShippingMethod selectedShippingMethod = commerceToolsTemplatingFunctions.getShippingMethod(parameters.get(CT_SHIPPING_METHOD_PARAM).toString());
            try {
                commerceToolsTemplatingFunctions.getProjectClient().execute(CartUpdateCommand.of(cart, SetShippingMethod.of(selectedShippingMethod))).toCompletableFuture().join();
            } catch (SphereException e) {
                log.error("Setting shipping method.", e);
                throw new FormProcessorFailedException(e.getMessage());
            }
        } else if (StringUtils.equals(parameters.get(CT_ACTION_PARAM).toString(), CT_ACTION_ORDER)) {
            try {
                commerceToolsTemplatingFunctions.getProjectClient().execute(OrderFromCartCreateCommand.of(OrderFromCartDraft.of(cart, null, PaymentState.BALANCE_DUE)))
                        .thenApplyAsync(new Function<Order, Object>() {
                            @Override
                            public Object apply(Order order) {
                                webContext.setAttribute(CommerceToolsServices.CT_LAST_ORDER_ID, order.getId(), Context.LOCAL_SCOPE);
                                webContext.removeAttribute(CommerceToolsServices.CT_CART_ID, Context.SESSION_SCOPE);
                                return null;
                            }
                        }).toCompletableFuture().join();
            } catch (SphereException e) {
                log.error("Order creation.", e);
                throw new FormProcessorFailedException(e.getMessage());
            }
        }
    }
}
