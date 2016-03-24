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

import info.magnolia.license.EnterpriseLicensedModule;
import info.magnolia.license.License;
import info.magnolia.license.LicenseConsts;
import info.magnolia.license.LicenseStatus;
import info.magnolia.module.ModuleLifecycle;
import info.magnolia.module.ModuleLifecycleContext;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import io.sphere.sdk.client.SphereClient;
import io.sphere.sdk.client.SphereClientFactory;

/**
 * Module class.
 */
public class CommerceToolsIntegrationModule implements EnterpriseLicensedModule, ModuleLifecycle {

    public static final int DEFAULT_QUERY_TIMEOUT = 122;

    final SphereClientFactory factory = SphereClientFactory.of();

    private Map<String, CommerceToolsProjectConfiguration> projects = new HashMap<>();

    private Map<String, SphereClient> sphereClients = new HashMap<>();

    @Inject
    public CommerceToolsIntegrationModule() {
    }

    @Override
    public String[] getSupportedEditions() {
        return new String[]{LicenseConsts.EDITION_ENTERPRISE};
    }

    @Override
    public boolean isDemoAllowed() {
        return true;
    }

    @Override
    public boolean isForceCheck() {
        return false;
    }

    @Override
    public LicenseStatus checkLicense(License license) {
        return new LicenseStatus(LicenseStatus.STATUS_VALID, StringUtils.EMPTY, license);
    }

    public SphereClient getSphereClient(String name) {
        if (sphereClients.containsKey(name)) {
            return sphereClients.get(name);
        }
        return null;
    }

    public Map<String, CommerceToolsProjectConfiguration> getProjects() {
        return projects;
    }

    public void setProjects(Map<String, CommerceToolsProjectConfiguration> projects) {
        this.projects = projects;
    }

    @Override
    public void start(ModuleLifecycleContext moduleLifecycleContext) {
        for (String name : projects.keySet()) {
            sphereClients.put(name, factory.createClient(projects.get(name).getSphereClientConfig()));
            //TODO handle if SphereClient is not configured properly
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
