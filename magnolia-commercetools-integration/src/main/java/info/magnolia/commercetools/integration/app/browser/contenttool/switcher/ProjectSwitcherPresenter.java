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
package info.magnolia.commercetools.integration.app.browser.contenttool.switcher;

import info.magnolia.commercetools.integration.CommerceToolsIntegrationModule;
import info.magnolia.commercetools.integration.CommerceToolsProjectConfiguration;
import info.magnolia.commercetools.integration.app.browser.event.ProjectIdChangedEvent;
import info.magnolia.commercetools.integration.app.container.CommerceToolsContainer;
import info.magnolia.commercetools.integration.app.contentconnector.CommerceToolsContentConnector;
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

import com.vaadin.data.Property;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

/**
 * Presenter that reacts on store change event and updates the view accordingly.
 */
public class ProjectSwitcherPresenter implements ContentToolPresenter {

    private final CommerceToolsIntegrationModule module;
    private final SimpleTranslator i18n;
    private final EventBus eventBus;
    private final WorkbenchPresenter workbenchPresenter;
    private final ContentConnector contentConnector;

    @Inject
    public ProjectSwitcherPresenter(final CommerceToolsIntegrationModule module, final SimpleTranslator i18n, final @Named(SubAppEventBus.NAME) EventBus eventBus, final WorkbenchPresenter workbenchPresenter, final ContentConnector contentConnector) {
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

        Map<String, CommerceToolsProjectConfiguration> projects = module.getProjects();
        for (Map.Entry<String, CommerceToolsProjectConfiguration> entry : projects.entrySet()) {
            String id = entry.getKey();
            String name = entry.getValue().getName();
            comboBox.addItem(id);
            comboBox.setItemCaption(id, name);
        }

        // preselect first value
        CommerceToolsContainer container = (CommerceToolsContainer) ((CommerceToolsContentConnector) contentConnector).getContainer();
        comboBox.select(container.getContentConnectorDefinition().getDefaultProjectId());

        comboBox.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event.getProperty() != null) {
                    String value = String.valueOf(event.getProperty().getValue());
                    eventBus.fireEvent(new ProjectIdChangedEvent(value));
                    workbenchPresenter.select(contentConnector.getDefaultItemId());
                    workbenchPresenter.refresh();
                }
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
