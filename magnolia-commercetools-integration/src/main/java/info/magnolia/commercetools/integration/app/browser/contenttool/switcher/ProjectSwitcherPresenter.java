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
package info.magnolia.commercetools.integration.app.browser.contenttool.switcher;

import info.magnolia.commercetools.integration.CommercetoolsIntegrationModule;
import info.magnolia.commercetools.integration.CommercetoolsProjectConfiguration;
import info.magnolia.commercetools.integration.app.browser.event.ProjectIdChangedEvent;
import info.magnolia.commercetools.integration.app.container.CommercetoolsContainer;
import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnector;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.api.view.View;
import info.magnolia.ui.framework.overlay.ViewAdapter;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;
import info.magnolia.ui.workbench.WorkbenchPresenter;
import info.magnolia.ui.workbench.contenttool.ContentToolPresenter;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.vaadin.v7.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Presenter that reacts on store change event and updates the view accordingly.
 */
public class ProjectSwitcherPresenter implements ContentToolPresenter {

    private final CommercetoolsIntegrationModule module;
    private final SimpleTranslator i18n;
    private final EventBus eventBus;
    private final WorkbenchPresenter workbenchPresenter;
    private final ContentConnector contentConnector;

    @Inject
    public ProjectSwitcherPresenter(final CommercetoolsIntegrationModule module, final SimpleTranslator i18n, final @Named(SubAppEventBus.NAME) EventBus eventBus, final WorkbenchPresenter workbenchPresenter, final ContentConnector contentConnector) {
        this.module = module;
        this.i18n = i18n;
        this.eventBus = eventBus;
        this.workbenchPresenter = workbenchPresenter;
        this.contentConnector = contentConnector;
    }

    @Override
    public View start() {
        HorizontalLayout layout = new HorizontalLayout();
        Label label = new Label(i18n.translate("ctBrowser.browser.contenttool.switcher.label"));

        ComboBox comboBox = new ComboBox();
        comboBox.setTextInputAllowed(false);
        comboBox.setNullSelectionAllowed(false);
        comboBox.setNewItemsAllowed(false);
        comboBox.setImmediate(true);

        Map<String, CommercetoolsProjectConfiguration> projects = module.getProjects();
        for (Map.Entry<String, CommercetoolsProjectConfiguration> entry : projects.entrySet()) {
            String id = entry.getKey();
            String name = entry.getValue().getName();
            comboBox.addItem(id);
            comboBox.setItemCaption(id, name);
        }

        // preselect first value
        CommercetoolsContainer container = (CommercetoolsContainer) ((CommercetoolsContentConnector) contentConnector).getContainer();
        comboBox.select(container.getContentConnectorDefinition().getDefaultProjectId());

        comboBox.addValueChangeListener((Property.ValueChangeListener) event -> {
            if (event.getProperty() != null) {
                String value = String.valueOf(event.getProperty().getValue());
                eventBus.fireEvent(new ProjectIdChangedEvent(value));
                workbenchPresenter.select(contentConnector.getDefaultItemId());
                workbenchPresenter.refresh();
            }
        });

        layout.setSpacing(true);
        layout.addComponent(label);
        layout.setComponentAlignment(label, Alignment.MIDDLE_CENTER);
        layout.addComponent(comboBox);
        layout.setComponentAlignment(comboBox, Alignment.MIDDLE_CENTER);
        return new ViewAdapter(layout);
    }
}
