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
package info.magnolia.commercetools.integration.app.detail.fields;

import info.magnolia.dam.app.ui.field.configuration.PreviewComponentProvider;
import info.magnolia.objectfactory.ComponentProvider;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItem;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;

import io.sphere.sdk.products.ProductVariant;

/**
 * Field that displays images of commercetools product variant.<br/>
 * Image is displayed as a preview and can be zoomed in by clicking on the zoom button.
 */
public class CommercetoolsImageField extends CustomField<ArrayList> {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsImageField.class);
    private HorizontalLayout layout;
    protected Image embedded = new Image();

    private ComponentProvider componentProvider;
    private CommercetoolsImageFieldDefinition definition;
    private Item relatedFormItem;

    public CommercetoolsImageField(CommercetoolsImageFieldDefinition definition, ComponentProvider componentProvider, Item relatedFormItem) {
        this.componentProvider = componentProvider;
        this.definition = definition;
        this.relatedFormItem = relatedFormItem;

        this.layout = new HorizontalLayout();

        setImmediate(false);
    }

    @Override
    protected Component initContent() {
        return layout;
    }

    @Override
    public Class<ArrayList> getType() {
        return ArrayList.class;
    }

    @Override
    public void setValue(ArrayList newValue) throws ReadOnlyException {
        setLabelAndImage();
    }

    /**
     * Set propertyDatasource.
     * If the translator is not null, set it as datasource.
     */
    @Override
    public void setPropertyDataSource(Property newDataSource) {
        super.setPropertyDataSource(newDataSource);
        setLabelAndImage();
    }

    /**
     * Set the Label And Image.
     */
    public void setLabelAndImage() {
        layout.removeAllComponents();

        List<io.sphere.sdk.products.Image> images = ((ProductVariant) ((BeanItem) relatedFormItem).getBean()).getImages();

        for (io.sphere.sdk.products.Image image : images) {
            CssLayout imageLayout = new CssLayout();
            imageLayout.setWidth("150px");
            imageLayout.setHeight("150px");

            if (image != null) {
                final String productImageUrl = image.getUrl();

                try {
                    embedded = new Image(null, new ExternalResource(new URL(productImageUrl)));
                } catch (MalformedURLException e) {
                    log.warn("Unable to obtain preview image from {}", productImageUrl, e.getMessage());
                }
                embedded.addStyleName("preview-image");

                Button lightboxButton = new Button();
                lightboxButton.addStyleName("lightbox-button");
                lightboxButton.setHtmlContentAllowed(true);
                lightboxButton.setCaption("<span class=\"icon-search\"></span>");
                imageLayout.addComponent(lightboxButton);

                imageLayout.addComponent(embedded);

                lightboxButton.addClickListener((Button.ClickListener) event -> {
                    Class<? extends PreviewComponentProvider> previewActionClass = definition.getPreviewComponentProviderClass();
                    // Launch Lightbox component
                    if (previewActionClass == null) {
                        log.warn("No preview component defined.");
                    }
                    PreviewComponentProvider implementation = componentProvider.newInstance(previewActionClass);

                    if (StringUtils.isBlank(productImageUrl)) {
                        log.warn("Full image not found.");
                        return;
                    }
                    try {
                        implementation.open(new ExternalResource(new URL(productImageUrl)));
                    } catch (MalformedURLException e) {
                        log.warn("Unable to obtain preview image from {}", productImageUrl, e.getMessage());
                    }
                });
            }

            layout.addComponent(imageLayout);
        }
    }
}
