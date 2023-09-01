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
package info.magnolia.commercetools.integration.app.configuration.event;

import info.magnolia.event.Event;
import info.magnolia.event.EventHandler;

/**
 * Event that is fired when site name changes.
 */
public class SiteChangedEvent implements Event<SiteChangedEvent.Handler> {

    private String siteName;

    public SiteChangedEvent(String siteName) {
        this.siteName = siteName;
    }

    @Override
    public void dispatch(Handler handler) {
        handler.onSiteChanged(this);
    }

    public String getSiteName() {
        return siteName;
    }

    /**
     * Handler.
     */
    public interface Handler extends EventHandler {

        void onSiteChanged(SiteChangedEvent event);
    }
}
