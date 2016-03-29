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
package info.magnolia.commercetools.integration.app.browser.event;

import info.magnolia.event.Event;
import info.magnolia.event.EventHandler;

/**
 * Event fired when storeId is changed.
 */
public class ProjectIdChangedEvent implements Event<ProjectIdChangedEvent.Handler> {

    private final String newProjectId;

    public ProjectIdChangedEvent(final String newProjectId) {
        this.newProjectId = newProjectId;
    }

    @Override
    public void dispatch(final Handler handler) {
        handler.onProjectIdChange(this);
    }

    public String getNewProjectId() {
        return newProjectId;
    }

    /**
     * Handler.
     */
    public interface Handler extends EventHandler {

        void onProjectIdChange(ProjectIdChangedEvent event);
    }
}
