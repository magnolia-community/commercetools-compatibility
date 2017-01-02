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
package info.magnolia.commercetools.integration.form;

import info.magnolia.cms.core.AggregationState;
import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.commercetools.integration.templating.CommercetoolsTemplatingFunctions;
import info.magnolia.jcr.util.PropertyUtil;
import info.magnolia.module.form.processors.AbstractEMailFormProcessor;
import info.magnolia.module.form.processors.FormProcessorFailedException;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jcr.Node;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sphere.sdk.customers.CustomerToken;
import io.sphere.sdk.models.SphereException;

/**
 * Reset password of customer or send email with password reset token.
 */
public class CommercetoolsPasswordResetProcessor extends AbstractEMailFormProcessor {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsPasswordResetProcessor.class);

    private static final String CT_ACTION_PARAM = "ctAction";
    private static final String CT_TOKEN_PARAM = "token";

    private static final String CT_ACTION_SEND_PASSWORD_RESET_EMAIL = "sendPwdResetEmail";
    private static final String CT_ACTION_RESET_PASSWORD = "resetPwd";

    private static final String CT_RESET_LINK = "{resetLink}";
    private static final String CT_PASSWORD_RESET_PAGE_PROPERTY = "passwordResetPage";

    private final CommercetoolsTemplatingFunctions commercetoolsTemplatingFunctions;
    private final Provider<AggregationState> aggregationStateProvider;

    @Inject
    public CommercetoolsPasswordResetProcessor(CommercetoolsTemplatingFunctions commercetoolsTemplatingFunctions, Provider<AggregationState> aggregationStateProvider) {
        this.commercetoolsTemplatingFunctions = commercetoolsTemplatingFunctions;
        this.aggregationStateProvider = aggregationStateProvider;
        this.setEnabled(true);
    }

    @Override
    public void internalProcess(Node content, Map<String, Object> parameters) throws FormProcessorFailedException {
        if (StringUtils.equals(parameters.get(CT_ACTION_PARAM).toString(), CT_ACTION_SEND_PASSWORD_RESET_EMAIL)){
            try {
                CustomerToken customerToken = commercetoolsTemplatingFunctions.getCustomerPasswordToken(parameters.get(CommercetoolsServices.CT_CUSTOMER_EMAIL).toString());
                String urlBase = StringUtils.substringBefore(aggregationStateProvider.get().getOriginalBrowserURL(), aggregationStateProvider.get().getOriginalBrowserURI());
                String passwordResetPage = urlBase + content.getSession().getNodeByIdentifier(PropertyUtil.getString(content, CT_PASSWORD_RESET_PAGE_PROPERTY)).getPath() + "?" + CT_TOKEN_PARAM + "=" + customerToken.getValue();
                String from = PropertyUtil.getString(content, "resetMailFrom");
                String subject = PropertyUtil.getString(content, "resetMailSubject");
                String to = parameters.get(CommercetoolsServices.CT_CUSTOMER_EMAIL).toString();
                String contentType = PropertyUtil.getString(content, "resetContentType");
                String body = StringUtils.replace(PropertyUtil.getString(content, "resetContentType" + contentType), CT_RESET_LINK, passwordResetPage);

                sendMail(body, from, subject, to, contentType, parameters);

            } catch (Exception e) {
                log.error("Reset password email", e);
                throw new FormProcessorFailedException("CommercetoolsPasswordResetProcessor.errorSendingMailMessage");
            }
        } else if (StringUtils.equals(parameters.get(CT_ACTION_PARAM).toString(), CT_ACTION_RESET_PASSWORD)){
            try {
                commercetoolsTemplatingFunctions.customerPasswordReset(parameters.get(CT_TOKEN_PARAM).toString().split("__")[0], parameters.get(CommercetoolsServices.CT_CUSTOMER_PASSWORD).toString());
            } catch (SphereException e) {
                log.error("Password change.", e);
                throw new FormProcessorFailedException("CommercetoolsPasswordResetProcessor.errorSettingNewPasswordMessage");
            }

        }
    }
}
