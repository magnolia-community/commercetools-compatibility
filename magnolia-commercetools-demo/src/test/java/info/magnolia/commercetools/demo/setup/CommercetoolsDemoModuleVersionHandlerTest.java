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
package info.magnolia.commercetools.demo.setup;

import static info.magnolia.test.hamcrest.NodeMatchers.hasProperty;
import static org.junit.Assert.assertThat;

import info.magnolia.context.MgnlContext;
import info.magnolia.dam.jcr.DamConstants;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.jcr.util.NodeUtil;
import info.magnolia.module.ModuleManagementException;
import info.magnolia.module.ModuleVersionHandler;
import info.magnolia.module.ModuleVersionHandlerTestCase;
import info.magnolia.module.model.Version;
import info.magnolia.repository.RepositoryConstants;

import java.util.Arrays;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the {@link CommercetoolsDemoModuleVersionHandler}.
 */
public class CommercetoolsDemoModuleVersionHandlerTest extends ModuleVersionHandlerTestCase {

    private Session config;
    private Node defaultURI;

    @Override
    protected String getModuleDescriptorPath() {
        return "/META-INF/magnolia/magnolia-commercetools-demo.xml";
    }

    @Override
    protected ModuleVersionHandler newModuleVersionHandlerForTests() {
        return new CommercetoolsDemoModuleVersionHandler();
    }

    @Override
    protected List<String> getModuleDescriptorPathsForTests() {
        return Arrays.asList("/META-INF/magnolia/core.xml");
    }

    @Override
    protected String[] getExtraWorkspaces() {
        return new String[] {DamConstants.WORKSPACE};
    }

    @Override
    protected String getExtraNodeTypes() {
        return "/mgnl-nodetypes/magnolia-dam-nodetypes.xml";
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        addSupportForSetupModuleRepositoriesTask(DamConstants.WORKSPACE);
        config = MgnlContext.getJCRSession(RepositoryConstants.CONFIG);
        defaultURI = NodeUtil.createPath(config.getRootNode(), "/modules/ui-admincentral/virtualURIMapping/default", NodeTypes.ContentNode.NAME);
        defaultURI.setProperty("toURI",  "redirect:/travel.html");
    }

    @Test
    public void cleanInstallOnPublic() throws Exception {
        // GIVEN
        setupConfigProperty("/server", "admin", "false");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(null);

        // THEN
        assertThat(defaultURI, hasProperty("toURI", "redirect:/commercetools.html"));
    }

    @Test
    public void updateTo11OnPublic() throws ModuleManagementException, RepositoryException {
        // GIVEN
        setupConfigProperty("/server", "admin", "false");

        // WHEN
        executeUpdatesAsIfTheCurrentlyInstalledVersionWas(Version.parseVersion("1.0"));

        //THEN
        assertThat(defaultURI, hasProperty("toURI", "redirect:/commercetools.html"));
    }

}
