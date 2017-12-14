/**
 * This file Copyright (c) 2016-2018 Magnolia International
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

import info.magnolia.i18nsystem.SimpleTranslator;
import info.magnolia.ui.vaadin.form.FormViewReduced;

import javax.inject.Inject;

import com.vaadin.v7.data.Item;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;

/**
 * View implementation.
 */
public class CommercetoolsConfigurationViewImpl implements CommercetoolsConfigurationView {

    private CommercetoolsSmallAppLayout layout = new CommercetoolsSmallAppLayout();
    private Listener listener;
    private SimpleTranslator i18n;
    private FormViewReduced formViewReduced;
    private CssLayout sectionLayout;

    @Inject
    public CommercetoolsConfigurationViewImpl(SimpleTranslator i18n) {
        this.i18n = i18n;
        initialize();
    }

    protected void initialize() {
        layout.addStyleName("ct-setup");

        Label label = new Label(i18n.translate("ctSetup.app.section.label"));
        label.addStyleName("section-title");

        Button saveButton = new Button(i18n.translate("ctSetup.app.buttons.save.label"));
        saveButton.addStyleName("v-button-smallapp");
        saveButton.addStyleName("commit");
        saveButton.addClickListener((Button.ClickListener) event -> listener.save());

        Button removeButton = new Button(i18n.translate("ctSetup.app.buttons.remove.label"));
        removeButton.addStyleName("v-button-smallapp");
        removeButton.addClickListener((Button.ClickListener) event -> listener.reset());

        final CssLayout buttonLayout = new CssLayout();
        buttonLayout.addStyleName("v-csslayout-smallapp-actions");
        buttonLayout.addComponent(saveButton);
        buttonLayout.addComponent(removeButton);

        sectionLayout = new CssLayout();
        sectionLayout.addComponent(label);
        if (formViewReduced != null) {
            sectionLayout.addComponent(formViewReduced.asVaadinComponent());
        }
        sectionLayout.addComponent(buttonLayout);

        layout.addSection(sectionLayout);
        layout.setDescription(i18n.translate("ctSetup.app.description"));
    }

    @Override
    public void setListener(Listener listener) {
        this.listener = listener;
    }

    @Override
    public void setFormViewReduced(FormViewReduced formViewReduced) {
        if (this.formViewReduced != null) {
            sectionLayout.replaceComponent(this.formViewReduced.asVaadinComponent(), formViewReduced.asVaadinComponent());
        } else {
            sectionLayout.addComponent(formViewReduced.asVaadinComponent(), 1);
        }
        this.formViewReduced = formViewReduced;
    }

    @Override
    public Item getItemDataSource() {
        return formViewReduced.getItemDataSource();
    }

    @Override
    public void showValidation(boolean showValidation) {
        formViewReduced.showValidation(showValidation);
    }

    @Override
    public boolean isValid() {
        return formViewReduced.isValid();
    }

    @Override
    public Component asVaadinComponent() {
        return layout;
    }
}
