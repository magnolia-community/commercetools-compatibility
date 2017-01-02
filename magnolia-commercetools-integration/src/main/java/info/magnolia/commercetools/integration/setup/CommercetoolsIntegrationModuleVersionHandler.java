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
package info.magnolia.commercetools.integration.setup;

import info.magnolia.cms.security.UserManager;
import info.magnolia.commercetools.integration.app.browser.contentview.tree.CommercetoolsTreePresenter;
import info.magnolia.commercetools.integration.app.configuration.CommercetoolsConfigurationSubApp;
import info.magnolia.commercetools.integration.app.configuration.CommercetoolsConfigurationSubAppDescriptor;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnectorDefinition;
import info.magnolia.commercetools.integration.app.detail.fields.CommercetoolsImageFieldDefinition;
import info.magnolia.commercetools.integration.app.detail.fields.CommercetoolsImageFieldFactory;
import info.magnolia.commercetools.integration.app.imageProvider.CommercetoolsImageProvider;
import info.magnolia.commercetools.integration.filter.CommercetoolsSignupLoginLogoutFilter;
import info.magnolia.commercetools.integration.rest.CommercetoolsCartEndPoint;
import info.magnolia.commercetools.integration.rest.CommercetoolsVariantEndPoint;
import info.magnolia.commercetools.integration.templating.CommercetoolsTemplatingFunctions;
import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddRoleToUserTask;
import info.magnolia.module.delta.AddURIPermissionTask;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.BootstrapSingleResource;
import info.magnolia.module.delta.CheckAndModifyPropertyValueTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.RemovePermissionTask;
import info.magnolia.module.delta.RenameNodeTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Version handler.
 */
public class CommercetoolsIntegrationModuleVersionHandler extends DefaultModuleVersionHandler {

    private Task assignCommercetoolsRestRoleTask = new ArrayDelegateTask("Add commercetools-rest role.", "Add commercetools-rest role to anonymous user and superuser.", new AddRoleToUserTask("Add commercetools-rest role to anonymous user.", UserManager.ANONYMOUS_USER, "commercetools-rest"), new AddRoleToUserTask("Add commercetools-rest role to superuser.", UserManager.SYSTEM_USER, "commercetools-rest"));

    public CommercetoolsIntegrationModuleVersionHandler() {
        register(DeltaBuilder.update("1.1", "")
                        .addTask(new ArrayDelegateTask("Rename classes task.", "Use lowercase 't' in commercetools.",
                                        //ctBrowser
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/apps/ctBrowser/subApps/browser/contentConnector", "class", "info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnectorDefinition", CommercetoolsContentConnectorDefinition.class.getName()),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/apps/ctBrowser/subApps/browser/workbench/contentViews/tree", "implementationClass", "info.magnolia.commercetools.integration.app.browser.contentview.tree.CommerceToolsTreePresenter", CommercetoolsTreePresenter.class.getName()),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/apps/ctBrowser/subApps/browser/imageProvider", "imageProviderClass", "info.magnolia.commercetools.integration.app.imageProvider.CommerceToolsImageProvider", CommercetoolsImageProvider.class.getName()),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/apps/ctBrowser/subApps/detail/editor/form/tabs/product/fields/variants/fields/images", "class", "info.magnolia.commercetools.integration.app.detail.fields.CommerceToolsImageFieldDefinition", CommercetoolsImageFieldDefinition.class.getName()),
                                        //ctSetup
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/apps/ctSetup/subApps/main", "class", "info.magnolia.commercetools.integration.app.configuration.CommerceToolsConfigurationSubAppDescriptor", CommercetoolsConfigurationSubAppDescriptor.class.getName()),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/apps/ctSetup/subApps/main", "subAppClass", "info.magnolia.commercetools.integration.app.configuration.CommerceToolsConfigurationSubApp", CommercetoolsConfigurationSubApp.class.getName()),
                                        //fieldTypes
                                        new RenameNodeTask("Rename  node.", RepositoryConstants.CONFIG, "/modules/commercetools-integration/fieldTypes", "commerceToolsImageField", "commercetoolsImageField", false),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/fieldTypes/commercetoolsImageField", "definitionClass", "info.magnolia.commercetools.integration.app.detail.fields.CommerceToolsImageFieldDefinition", CommercetoolsImageFieldDefinition.class.getName()),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/fieldTypes/commercetoolsImageField", "factoryClass", "info.magnolia.commercetools.integration.app.detail.fields.CommerceToolsImageFieldFactory", CommercetoolsImageFieldFactory.class.getName()),
                                        //restEndpoints
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/rest-endpoints/ctCart", "implementationClass", "info.magnolia.commercetools.integration.rest.CommerceToolsCartEndPoint", CommercetoolsCartEndPoint.class.getName()),
                                        new CheckAndModifyPropertyValueTask("/modules/commercetools-integration/rest-endpoints/ctVariant", "implementationClass", "info.magnolia.commercetools.integration.rest.CommerceToolsVariantEndPoint", CommercetoolsVariantEndPoint.class.getName()),
                                        //ctfn
                                        new CheckAndModifyPropertyValueTask("/modules/rendering/renderers/freemarker/contextAttributes/ctfn", "componentClass", "info.magnolia.commercetools.integration.templating.CommerceToolsTemplatingFunctions", CommercetoolsTemplatingFunctions.class.getName()),
                                        //ctSignupLoginLogout
                                        new CheckAndModifyPropertyValueTask("/server/filters/cms/ctSignupLoginLogout", "class", "info.magnolia.commercetools.integration.filter.CommerceToolsSignupLoginLogoutFilter", CommercetoolsSignupLoginLogoutFilter.class.getName())
                                )
                        )
                        .addTask(new ArrayDelegateTask("Change permissions respecting moving commercetools rest endpoints under one parent and exclude it from caching.",
                                        new BootstrapSingleResource("Exclude commercetools rest calls from caching.", "Exclude commercetools rest calls from caching.", "/mgnl-bootstrap/commercetools-integration/config.modules.cache.config.contentCaching.defaultPageCache.cachePolicy.shouldBypassVoters.urls.excludes.restCommercetools.xml"),
                                        new RemovePermissionTask("Remove permission from rest group that allows access ctCart rest endpoint.", "rest", "uri", "/.rest/ctCart*", AddURIPermissionTask.GET_POST),
                                        new RemovePermissionTask("Remove permission from rest group that allows access ctVariant rest endpoint.", "rest", "uri", "/.rest/ctVariant*", AddURIPermissionTask.GET_POST),
                                        new BootstrapSingleResource("Add role that allow access to commercetools rest end-point.", "Add role that allow access to commercetools rest end-point.", "/mgnl-bootstrap/commercetools-integration/userroles.commercetools-rest.xml"),
                                        assignCommercetoolsRestRoleTask
                                        )
                        )
        );

        register(DeltaBuilder.update("1.2", "")
                        .addTask(new ReplaceTransformersWithConvertersTask())
        );
    }

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(super.getExtraInstallTasks(installContext));
        tasks.add(new OrderNodeBeforeTask("/server/filters/cms/ctSignupLoginLogout", "modelExecution"));
        tasks.add(assignCommercetoolsRestRoleTask);
        return tasks;
    }

}