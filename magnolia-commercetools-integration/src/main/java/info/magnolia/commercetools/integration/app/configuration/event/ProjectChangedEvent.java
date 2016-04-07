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
package info.magnolia.commercetools.integration.app.configuration.event;

import info.magnolia.event.Event;
import info.magnolia.event.EventHandler;

/**
 * Event that is fired when project selection changes.
 */
public class ProjectChangedEvent implements Event<ProjectChangedEvent.Handler> {

    private String projectName;

    public ProjectChangedEvent(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void dispatch(Handler handler) {
        handler.onProjectChanged(this);
    }

    public String getProjectName() {
        return projectName;
    }

    /**
     * Handler.
     */
    public interface Handler extends EventHandler {

        void onProjectChanged(ProjectChangedEvent event);
    }
}
