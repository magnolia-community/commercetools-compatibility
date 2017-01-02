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
package info.magnolia.commercetools.integration.filter;

import info.magnolia.cms.filters.AbstractMgnlFilter;
import info.magnolia.cms.security.auth.login.FormLogin;
import info.magnolia.cms.util.RequestDispatchUtil;
import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.context.Context;
import info.magnolia.context.WebContext;
import info.magnolia.module.site.SiteManager;

import java.io.IOException;
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

import io.sphere.sdk.client.ErrorResponseException;
import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.customers.CustomerDraft;
import io.sphere.sdk.customers.CustomerSignInResult;
import io.sphere.sdk.customers.commands.CustomerCreateCommand;
import io.sphere.sdk.customers.commands.CustomerSignInCommand;

/**
 * Filter that handles guest or user signUp or login/logout for commercetools.
 */
public class CommercetoolsSignupLoginLogoutFilter extends AbstractMgnlFilter {

    private static final String ATTRIBUTE_LOGIN_ERROR = "ctLoginError";
    private static final String ATTRIBUTE_SIGNUP_ERROR = "ctSignupError";

    private static final String PARAMETER_ACTION = "ctAction";

    private static final String ACTION_DO_LOGIN = "ctDoLogin";
    private static final String ACTION_DO_LOGOUT = "ctDoLogout";
    private static final String ACTION_DO_SIGNUP = "ctDoSignup";

    private static final String RESULT = "ctResult";

    private final Provider<WebContext> webContextProvider;
    private final Provider<CommercetoolsIntegrationModule> commercetoolsModuleProvider;
    private final SiteManager siteManager;
    private final CommercetoolsServices commercetoolsServices;

    @Inject
    public CommercetoolsSignupLoginLogoutFilter(Provider<CommercetoolsIntegrationModule> commercetoolsModuleProvider, Provider<WebContext> webContextProvider, final SiteManager siteManager, final CommercetoolsServices commercetoolsServices) {
        this.commercetoolsModuleProvider = commercetoolsModuleProvider;
        this.webContextProvider = webContextProvider;
        this.siteManager = siteManager;
        this.commercetoolsServices = commercetoolsServices;
    }

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        final String action = webContextProvider.get().getParameter(PARAMETER_ACTION);
        if (StringUtils.isNotBlank(action)) {
            if (ACTION_DO_LOGIN.equals(action)) {
                if (webContextProvider.get().getAttribute(getProjectName() + "_" + CommercetoolsServices.CT_CUSTOMER_ID, Context.SESSION_SCOPE) == null) {
                    processLogIn();
                }
            } else if (ACTION_DO_LOGOUT.equals(action)) {
                processLogOut();
            } else if (ACTION_DO_SIGNUP.equals(action)) {
                processSignUp();
            } else {
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
        final String projectName = getProjectName();

        logIn()
                .thenApplyAsync(new Function<CustomerSignInResult, Object>() {
                    @Override
                    public Object apply(CustomerSignInResult result) {
                        webContext.setAttribute(projectName + "_" + CommercetoolsServices.CT_CUSTOMER_ID, result.getCustomer().getId(), Context.SESSION_SCOPE);
                        webContext.setAttribute(projectName + "_" + CommercetoolsServices.CT_CART_ID, result.getCart().getId(), Context.SESSION_SCOPE);
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
        final String projectName = getProjectName();

        signUp()
                .thenApplyAsync(new Function<CustomerSignInResult, Object>() {
                    @Override
                    public Object apply(CustomerSignInResult result) {
                        webContext.setAttribute(projectName + "_" + CommercetoolsServices.CT_CUSTOMER_ID, result.getCustomer().getId(), Context.SESSION_SCOPE);
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
        webContextProvider.get().removeAttribute(getProjectName() + "_" + CommercetoolsServices.CT_CUSTOMER_ID, Context.SESSION_SCOPE);
        webContextProvider.get().removeAttribute(getProjectName() + "_" + CommercetoolsServices.CT_CART_ID, Context.SESSION_SCOPE);
    }

    private CompletionStage<CustomerSignInResult> logIn() {
        final String username = webContextProvider.get().getAttribute(CommercetoolsServices.CT_CUSTOMER_EMAIL);
        final String password = webContextProvider.get().getAttribute(CommercetoolsServices.CT_CUSTOMER_PASSWORD);
        final String anonymousCartId = webContextProvider.get().getAttribute(getProjectName() + "_" + CommercetoolsServices.CT_CART_ID);
        final CustomerSignInCommand signInCommand = CustomerSignInCommand.of(username, password, anonymousCartId);
        return getProjectClient().execute(signInCommand);
    }

    private CompletionStage<CustomerSignInResult> signUp() {
        final CustomerDraft customerDraft = commercetoolsServices.getCustomerDraft();
        final CustomerCreateCommand customerCreateCommand = CustomerCreateCommand.of(customerDraft);
        return getProjectClient().execute(customerCreateCommand);
    }

    private String getProjectName() {
        Map<String, Object> params = siteManager.getCurrentSite().getParameters();
        if (params.containsKey(CommercetoolsIntegrationModule.PROJECT_PARAM_NAME)) {
            return String.valueOf(params.get(CommercetoolsIntegrationModule.PROJECT_PARAM_NAME));
        }
        throw new RuntimeException("No project name configured for site " + siteManager.getCurrentSite());
    }

    private SphereClient getProjectClient() {
        return commercetoolsModuleProvider.get().getSphereClient(getProjectName());
    }
}
