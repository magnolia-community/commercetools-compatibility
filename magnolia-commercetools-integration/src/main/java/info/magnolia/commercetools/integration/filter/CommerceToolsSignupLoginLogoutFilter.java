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
package info.magnolia.commercetools.integration.filter;

import info.magnolia.cms.filters.AbstractMgnlFilter;
import info.magnolia.cms.security.auth.login.FormLogin;
import info.magnolia.cms.util.RequestDispatchUtil;
import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.context.Context;
import info.magnolia.context.WebContext;
import info.magnolia.module.site.SiteManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.function.Function;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sphere.sdk.client.ErrorResponseException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerDraftBuilder;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.customers.commands.CustomerSignInCommand;
import io.sphere.sdk.models.Address;

/**
 * Filter that handles guest or user signUp or login/logout for CommerceTools.
 */
public class CommerceToolsSignupLoginLogoutFilter extends AbstractMgnlFilter {

    private static final Logger log = LoggerFactory.getLogger(CommerceToolsSignupLoginLogoutFilter.class);

    private static final String ATTRIBUTE_LOGIN_ERROR = "ctLoginError";
    private static final String ATTRIBUTE_SIGNUP_ERROR = "ctSignupError";

    private static final String CT_CUSTOMER = "ctCustomer";
    private static final String CT_CUSTOMER_NUMBER = "ctCustomerNumber";
    private static final String CT_CUSTOMER_EMAIL = "ctCustomerEmail";
    private static final String CT_CUSTOMER_PASSWORD = "ctCustomerPassword";
    private static final String CT_CUSTOMER_FIRST_NAME = "ctCustomerFirstName";
    private static final String CT_CUSTOMER_LAST_NAME = "ctCustomerLastName";
    private static final String CT_CUSTOMER_MIDDLE_NAME = "ctCustomerMiddleName";
    private static final String CT_CUSTOMER_TITLE = "ctCustomerTitle";
    private static final String CT_CART_ID = "ctCartId";
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

    private static final String PARAMETER_ACTION = "ctAction";

    private static final String ACTION_DO_LOGIN = "ctDoLogin";
    private static final String ACTION_DO_LOGOUT = "ctDoLogout";
    private static final String ACTION_DO_SIGNUP = "ctDoSignup";

    private static final String RESULT = "ctResult";

    private final Provider<WebContext> webContextProvider;
    private final Provider<CommerceToolsIntegrationModule> commerceToolsModuleProvider;
    private final SiteManager siteManager;

    @Inject
    public CommerceToolsSignupLoginLogoutFilter(Provider<CommerceToolsIntegrationModule> commerceToolsModuleProvider, Provider<WebContext> webContextProvider, final SiteManager siteManager) {
        this.commerceToolsModuleProvider = commerceToolsModuleProvider;
        this.webContextProvider = webContextProvider;
        this.siteManager = siteManager;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        final String action = webContextProvider.get().getParameter(PARAMETER_ACTION);
        if (StringUtils.isNotBlank(action)) {
            if (ACTION_DO_LOGIN.equals(action)) {
                if (webContextProvider.get().getAttribute(CT_CUSTOMER, Context.SESSION_SCOPE) == null) {
                    processLogIn();
                }
            } else if (ACTION_DO_LOGOUT.equals(action)) {
                processLogOut();
            } else if (ACTION_DO_SIGNUP.equals(action)) {
                processSignUp();
            } else {
                log.warn("Unknown action: {}", action);
                chain.doFilter(request, response);
                return;
            }

            Boolean loginResult = webContextProvider.get().getAttribute(RESULT);
            String location = request.getParameter(FormLogin.PARAMETER_RETURN_TO);

            if ((ACTION_DO_LOGOUT.equals(action) && StringUtils.isNotBlank(location)) || (loginResult != null && loginResult)) {
                if (StringUtils.isBlank(location)) {
                    // Fallback to current request uri if no FormLogin.PARAMETER_RETURN_TO was specified
                    location = request.getRequestURL().toString();
                }
                location = RequestDispatchUtil.REDIRECT_PREFIX + location;
                RequestDispatchUtil.dispatch(location, request, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    public void processLogIn() {
        final WebContext webContext = webContextProvider.get();

        logIn()
                .thenApplyAsync(new Function<CustomerSignInResult, Object>() {
                    @Override
                    public Object apply(CustomerSignInResult result) {
                        webContext.setAttribute(CT_CUSTOMER, result.getCustomer().getId(), Context.SESSION_SCOPE);
                        webContext.setAttribute(RESULT, true, Context.LOCAL_SCOPE);
                        return null;
                    }
                })
                .exceptionally(new Function<Throwable, Object>() {
                    @Override
                    public Object apply(Throwable throwable) {
                        webContext.setAttribute(ATTRIBUTE_LOGIN_ERROR, (throwable.getCause() instanceof ErrorResponseException ? ((ErrorResponseException) throwable.getCause()).getErrors() : throwable.getMessage()), Context.LOCAL_SCOPE);
                        webContext.setAttribute(RESULT, false, Context.LOCAL_SCOPE);
                        return null;
                    }
                }).toCompletableFuture().join();
    }

    public void processSignUp() {
        final WebContext webContext = webContextProvider.get();

        signUp()
                .thenApplyAsync(new Function<CustomerSignInResult, Object>() {
                    @Override
                    public Object apply(CustomerSignInResult result) {
                        webContext.setAttribute(CT_CUSTOMER, result.getCustomer().getId(), Context.SESSION_SCOPE);
                        webContext.setAttribute(RESULT, true, Context.LOCAL_SCOPE);
                        return null;
                    }
                })
                .exceptionally(new Function<Throwable, Object>() {
                    @Override
                    public Object apply(Throwable throwable) {
                        webContext.setAttribute(ATTRIBUTE_SIGNUP_ERROR, (throwable.getCause() instanceof ErrorResponseException ? ((ErrorResponseException) throwable.getCause()).getErrors() : throwable.getMessage()), Context.LOCAL_SCOPE);
                        webContext.setAttribute(RESULT, false, Context.LOCAL_SCOPE);
                        return null;
                    }
                }).toCompletableFuture().join();
    }

    public void processLogOut() {
        webContextProvider.get().removeAttribute(CT_CUSTOMER, Context.SESSION_SCOPE);
        webContextProvider.get().removeAttribute(CT_CART_ID, Context.SESSION_SCOPE);
    }

    private CompletionStage<CustomerSignInResult> logIn() {
        final String username = webContextProvider.get().getAttribute(CT_CUSTOMER_EMAIL);
        final String password = webContextProvider.get().getAttribute(CT_CUSTOMER_PASSWORD);
        final String anonymousCartId = webContextProvider.get().getAttribute(CT_CART_ID);
        final CustomerSignInCommand signInCommand = CustomerSignInCommand.of(username, password, anonymousCartId);
        return getProjectClient().execute(signInCommand);
    }

    private CompletionStage<CustomerSignInResult> signUp() {
        final WebContext webContext = webContextProvider.get();

        final CustomerDraft customerDraft = CustomerDraftBuilder.of(webContext.getAttribute(CT_CUSTOMER_EMAIL), webContext.getAttribute(CT_CUSTOMER_PASSWORD))
                .customerNumber(webContext.getAttribute(CT_CUSTOMER_NUMBER))
                .firstName((String) webContext.getOrDefault(CT_CUSTOMER_FIRST_NAME, ""))
                .lastName((String) webContext.getOrDefault(CT_CUSTOMER_LAST_NAME, ""))
                .middleName(webContext.getAttribute(CT_CUSTOMER_MIDDLE_NAME))
                .title(webContext.getAttribute(CT_CUSTOMER_TITLE))
                .anonymousCartId(webContext.getAttribute(CT_CART_ID))
                .externalId(webContext.getAttribute(CT_CUSTOMER_EXTERNAL_ID))
                .dateOfBirth(webContext.getAttribute(CT_CUSTOMER_DATE_OF_BIRTH))
                .companyName(webContext.getAttribute(CT_CUSTOMER_COMPANY_NAME))
                .vatId(webContext.getAttribute(CT_CUSTOMER_VAT_ID))
                .isEmailVerified(webContext.getAttribute(CT_CUSTOMER_IS_EMAIL_VERIFIED))
                .customerGroup(webContext.getAttribute(CT_CUSTOMER_GROUP))
                .addresses((List<Address>) webContext.getOrDefault(CT_CUSTOMER_ADRESSES, new ArrayList<Address>()))
                .defaultBillingAddress(webContext.getAttribute(CT_CUSTOMER_DEFAULT_BILLING_ADDRESS))
                .defaultShippingAddress(webContext.getAttribute(CT_CUSTOMER_DEFAULT_SHIPPING_ADDRESS))
                .custom(webContext.getAttribute(CT_CUSTOMER_CUSTOM))
                .build();

        final CustomerCreateCommand customerCreateCommand = CustomerCreateCommand.of(customerDraft);
        return getProjectClient().execute(customerCreateCommand);
    }

    private SphereClient getProjectClient() {
        Map<String, Object> params = siteManager.getCurrentSite().getParameters();
        if (params.containsKey(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME)) {
            return commerceToolsModuleProvider.get().getSphereClient(String.valueOf(params.get(CommerceToolsIntegrationModule.PROJECT_PARAM_NAME)));
        }
        throw new RuntimeException("No project name configured for site " + siteManager.getCurrentSite());
    }
}
