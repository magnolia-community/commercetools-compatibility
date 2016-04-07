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
package info.magnolia.commercetools.integration.app.configuration;

import info.magnolia.commercetools.integration.app.configuration.event.ProjectChangedEvent;
import info.magnolia.commercetools.integration.app.configuration.event.SiteChangedEvent;
import info.magnolia.commercetools.integration.app.configuration.field.AbstractCommerceToolsFieldFactory;
import info.magnolia.context.Context;
import info.magnolia.event.EventBus;
import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.jcr.util.NodeTypes;
import info.magnolia.objectfactory.ComponentProvider;
import info.magnolia.repository.RepositoryConstants;
import info.magnolia.ui.api.app.SubAppContext;
import info.magnolia.ui.api.app.SubAppEventBus;
import info.magnolia.ui.api.context.UiContext;
import info.magnolia.ui.dialog.formdialog.FormBuilder;
import info.magnolia.ui.form.definition.FormDefinition;
import info.magnolia.ui.form.field.definition.FieldDefinition;
import info.magnolia.ui.vaadin.form.FormViewReduced;
import info.magnolia.ui.vaadin.integration.jcr.AbstractJcrNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.DefaultProperty;
import info.magnolia.ui.vaadin.integration.jcr.JcrNewNodeAdapter;
import info.magnolia.ui.vaadin.integration.jcr.JcrNodeAdapter;
import info.magnolia.ui.vaadin.overlay.MessageStyleTypeEnum;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;

/**
 * Presenter class for CommerceTools configuration app.
 */
public class CommerceToolsConfigurationPresenter implements CommerceToolsConfigurationView.Listener {

    private static final Logger log = LoggerFactory.getLogger(CommerceToolsConfigurationPresenter.class);

    private static final String SITE_DEFINITION_PATH = "/modules/multisite/config/sites/%s/parameters";
    private static final String DEFAULT_SITE_NAME = "fallback";

    private final CommerceToolsConfigurationView view;
    private final FormBuilder formBuilder;
    private final ComponentProvider componentProvider;
    private final EventBus subAppEventBus;
    private final UiContext uiContext;
    private final Provider<Context> contextProvider;
    private final SimpleTranslator i18n;
    private final FormDefinition formDefinition;

    @Inject
    public CommerceToolsConfigurationPresenter(CommerceToolsConfigurationView view, FormBuilder formBuilder, ComponentProvider componentProvider, SubAppContext subAppContext, @Named(SubAppEventBus.NAME) EventBus subAppEventBus, UiContext uiContext, SimpleTranslator i18n, Provider<Context> contextProvider) {
        this.view = view;
        this.formBuilder = formBuilder;
        this.componentProvider = componentProvider;
        this.subAppEventBus = subAppEventBus;
        this.uiContext = uiContext;
        this.contextProvider = contextProvider;
        this.i18n = i18n;
        this.formDefinition = ((CommerceToolsConfigurationSubAppDescriptor) subAppContext.getSubAppDescriptor()).getFormDefinition();
    }

    public CommerceToolsConfigurationView start() {
        subAppEventBus.addHandler(SiteChangedEvent.class, new SiteChangedEvent.Handler() {
            @Override
            public void onSiteChanged(SiteChangedEvent event) {
                CommerceToolsConfigurationPresenter.this.onSiteChanged(event.getSiteName());
            }
        });
        subAppEventBus.addHandler(ProjectChangedEvent.class, new ProjectChangedEvent.Handler() {
            @Override
            public void onProjectChanged(ProjectChangedEvent event) {
                CommerceToolsConfigurationPresenter.this.onProjectChanged(event.getProjectName());
            }
        });

        subAppEventBus.fireEvent(new SiteChangedEvent(DEFAULT_SITE_NAME));
        view.setListener(this);
        return view;
    }

    @Override
    public void save() {
        view.showValidation(true);
        if (view.isValid()) {
            AbstractJcrNodeAdapter item = (AbstractJcrNodeAdapter) view.getItemDataSource();
            String siteName = (String) item.getItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME).getValue();
            String path = String.format(SITE_DEFINITION_PATH, siteName);
            try {
                if (item.getItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME) != null) {
                    item.removeItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME);
                }
                Node node = item.applyChanges();
                node.getSession().save();
                uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("ctSetup.app.buttons.save.success"));
            } catch (RepositoryException e) {
                log.warn("Unable to save configuration node for CommerceTools integration {}", path, e);
                uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("ctSetup.app.buttons.save.error"));
            }
        }
    }

    @Override
    public void reset() {
        AbstractJcrNodeAdapter item = (AbstractJcrNodeAdapter) view.getItemDataSource();
        String siteName = (String) item.getItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME).getValue();
        String path = String.format(SITE_DEFINITION_PATH, siteName);
        try {
            Session session = contextProvider.get().getJCRSession(RepositoryConstants.CONFIG);
            if (session.nodeExists(path)) {
                Node parameters = session.getNode(path);
                for (FieldDefinition fieldDefinition : formDefinition.getTabs().get(0).getFields()) {
                    if (!fieldDefinition.getName().equals("site")) {
                        if (parameters.hasProperty(fieldDefinition.getName())) {
                            parameters.getProperty(fieldDefinition.getName()).remove();
                        }
                    }
                }
            }
            session.save();
            // refresh
            onSiteChanged(siteName);
            uiContext.openNotification(MessageStyleTypeEnum.INFO, true, i18n.translate("ctSetup.app.buttons.remove.success"));
        } catch (RepositoryException e) {
            log.warn("Unable to save configuration node for CommerceTools integration {}", path, e);
            uiContext.openNotification(MessageStyleTypeEnum.ERROR, true, i18n.translate("ctSetup.app.buttons.remove.error"));
        }
    }

    public void onSiteChanged(String siteName) {
        Item item = getItemDataSource(siteName);
        if (item != null) {
            item.addItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME, new DefaultProperty<String>(siteName));
            FormViewReduced formView = componentProvider.getComponent(FormViewReduced.class);
            formBuilder.buildReducedForm(formDefinition, formView, item, null);
            view.setFormViewReduced(formView);
        }
    }

    private void onProjectChanged(String projectName) {
        AbstractJcrNodeAdapter item = (AbstractJcrNodeAdapter) view.getItemDataSource();
        String siteName = (String) item.getItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME).getValue();
        Item vaadinItem = getItemDataSource(siteName);
        if (vaadinItem != null) {
            vaadinItem.addItemProperty(AbstractCommerceToolsFieldFactory.PROJECT_SELECT_PROPERTY_NAME, new DefaultProperty<String>(projectName));
            vaadinItem.addItemProperty(AbstractCommerceToolsFieldFactory.SITE_SELECT_PROPERTY_NAME, new DefaultProperty<String>(siteName));
            FormViewReduced formView = componentProvider.getComponent(FormViewReduced.class);
            formBuilder.buildReducedForm(formDefinition, formView, vaadinItem, null);
            view.setFormViewReduced(formView);
        }
    }

    private Item getItemDataSource(String siteName) {
        String path = String.format(SITE_DEFINITION_PATH, siteName);
        try {
            Session session = contextProvider.get().getJCRSession(RepositoryConstants.CONFIG);
            if (session.nodeExists(path)) {
                return new JcrNodeAdapter(session.getNode(path));
            }
            return new JcrNewNodeAdapter(session.getNode(StringUtils.substringBeforeLast(path, "/")), NodeTypes.ContentNode.NAME, "parameters");
        } catch (RepositoryException e) {
            log.warn("Unable to get configuration node for CommerceTools integration {}", path, e);
        }
        return null;
    }
}
