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
package info.magnolia.commercetools.demo.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddRoleToGroupTask;
import info.magnolia.module.delta.ArrayDelegateTask;
import info.magnolia.module.delta.DeltaBuilder;
import info.magnolia.module.delta.IsAuthorInstanceDelegateTask;
import info.magnolia.module.delta.IsModuleInstalledOrRegistered;
import info.magnolia.module.delta.ModuleDependencyBootstrapTask;
import info.magnolia.module.delta.SetPropertyTask;
import info.magnolia.module.delta.Task;
import info.magnolia.repository.RepositoryConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Version handler.
 */
public class CommercetoolsDemoModuleVersionHandler extends DefaultModuleVersionHandler {

    private Task assignCommercetoolsRestRoleTask = new IsModuleInstalledOrRegistered("Add commercetools-rest role to travel-demo editors and publishers if demo is installed", "travel-demo",
            new ArrayDelegateTask("Add commercetools-rest role to travel-demo editors and publishers", "Add commercetools-rest role to travel-demo editors and publishers",
                    new AddRoleToGroupTask("Add commercetools-rest to travel-demo-editors group.", "commercetools-rest", "travel-demo-editors"),
                    new AddRoleToGroupTask("Add commercetools-rest to travel-demo-publishers group.", "commercetools-rest", "travel-demo-publishers")
            )
    );

    private Task resetRedirectURIOnPublicInstanceTask = new IsAuthorInstanceDelegateTask("Reset default redirect URI on public instance.", "Reset default redirect URI on public instance.", null,
            new SetPropertyTask(RepositoryConstants.CONFIG, "/modules/ui-admincentral/virtualURIMapping/default", "toURI", "redirect:/commercetools.html"));

    public CommercetoolsDemoModuleVersionHandler() {
        register(DeltaBuilder.update("1.1", "")
                .addTask(assignCommercetoolsRestRoleTask)
                .addTask(resetRedirectURIOnPublicInstanceTask)
        );
    }

    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(super.getExtraInstallTasks(installContext));
        tasks.add(new IsModuleInstalledOrRegistered("Install site configuration for travel-demo if demo is installed", "travel-demo", new ModuleDependencyBootstrapTask("multisite")));
        tasks.add(assignCommercetoolsRestRoleTask);
        tasks.add(resetRedirectURIOnPublicInstanceTask);
        return tasks;
    }

}