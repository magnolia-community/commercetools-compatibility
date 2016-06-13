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
package info.magnolia.commercetools.integration;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sphere.sdk.client.SphereClientConfig;

/**
 * Configuration bean for commercetools project.
 */
public class CommercetoolsProjectConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsProjectConfiguration.class);

    private String name;

    private String projectKey;

    private String clientId;

    private String clientSecret;

    private String authUrl;

    private String apiUrl;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public SphereClientConfig getSphereClientConfig() {
        if (StringUtils.isNotBlank(projectKey) && StringUtils.isNotBlank(clientId) && StringUtils.isNotBlank(clientSecret)) {
            if (StringUtils.isNotBlank(authUrl) && StringUtils.isNotBlank(apiUrl)) {
                return SphereClientConfig.of(projectKey, clientId, clientSecret, authUrl, apiUrl);
            } else {
                return SphereClientConfig.of(projectKey, clientId, clientSecret);
            }
        }
        log.error("Project [/modules/commercetools-integration/config/projects/{}] is not configured properly.", name);
        return null;
    }

}
