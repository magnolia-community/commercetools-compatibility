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
package info.magnolia.commercetools.integration.setup;

import info.magnolia.module.DefaultModuleVersionHandler;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.AddURIPermissionTask;
import info.magnolia.module.delta.OrderNodeBeforeTask;
import info.magnolia.module.delta.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Version handler.
 */
public class CommerceToolsIntegrationModuleVersionHandler extends DefaultModuleVersionHandler {

    public CommerceToolsIntegrationModuleVersionHandler() {

    }
    @Override
    protected List<Task> getExtraInstallTasks(InstallContext installContext) {
        List<Task> tasks = new ArrayList<Task>();
        tasks.addAll(super.getExtraInstallTasks(installContext));
        tasks.add(new OrderNodeBeforeTask("/server/filters/cms/ctSignupLoginLogout", "modelExecution"));
        tasks.add(new AddURIPermissionTask("Add permition to anonymous user access ctCart rest endpoint.", "rest", "/.rest/ctCart*", AddURIPermissionTask.GET_POST));
        tasks.add(new AddURIPermissionTask("Add permition to anonymous user access ctCart rest endpoint.", "rest", "/.rest/ctVariant*", AddURIPermissionTask.GET_POST));
        return tasks;
    }

}