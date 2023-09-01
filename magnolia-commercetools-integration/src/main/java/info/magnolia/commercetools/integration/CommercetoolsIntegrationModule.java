/*
 * This file Copyright (c) 2016-2018 Magnolia International Ltd.
 * (http://www.magnolia-cms.com). All rights reserved.
 *
 *
 * This program and the accompanying materials are made available under
 * the terms of the MIT License which accompanies this distribution, and
 * is available at https://opensource.org/license/mit
 *
 */
package info.magnolia.commercetools.integration;

import info.magnolia.commercetools.integration.service.CommercetoolsServices;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;
import io.sphere.sdk.models.SphereException;

/**
 * Module class.
 */
public class CommercetoolsIntegrationModule implements ModuleLifecycle {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsIntegrationModule.class);

    public static final int DEFAULT_QUERY_TIMEOUT = 122;

    public static final String PROJECT_PARAM_NAME = "ctProject";
    public static final String LANGUAGE_PARAM_NAME = "ctLanguage";
    public static final String COUNTRY_PARAM_NAME = "ctCountry";
    public static final String CURRENCY_PARAM_NAME = "ctCurrency";

    final SphereClientFactory factory = SphereClientFactory.of();

    private Map<String, CommercetoolsProjectConfiguration> projects = new HashMap<>();

    private Map<String, SphereClient> sphereClients = new HashMap<>();

    private final CommercetoolsServices services;

    @Inject
    public CommercetoolsIntegrationModule(CommercetoolsServices services) {
        this.services = services;
    }

    public SphereClient getSphereClient(String name) {
        if (sphereClients.containsKey(name)) {
            return sphereClients.get(name);
        }
        return null;
    }

    public Map<String, CommercetoolsProjectConfiguration> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, CommercetoolsProjectConfiguration> projects) {
        this.projects = projects;
    }

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        for (String name : projects.keySet()) {
            SphereClient sphereClient = factory.createClient(projects.get(name).getSphereClientConfig());
            try {
                services.getProjectDetail(sphereClient);
            } catch (SphereException e) {
                log.error("Project [/modules/commercetools-integration/config/projects/{}] is not configured properly.", name);
                sphereClient.close();
            }
            sphereClients.put(name, sphereClient);
        }
    }

    @Override
    public void stop(ModuleLifecycleContext moduleLifecycleContext) {
        for (SphereClient client : sphereClients.values()) {
            client.close();
        }
        sphereClients.clear();
    }
}
