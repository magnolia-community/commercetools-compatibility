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
package info.magnolia.commercetools.integration.app.imageProvider;

import info.magnolia.commercetools.integration.app.contentconnector.CommercetoolsContentConnector;
import info.magnolia.commercetools.integration.app.item.CommercetoolsProductItem;
import info.magnolia.ui.imageprovider.ImageProvider;
import info.magnolia.ui.vaadin.integration.contentconnector.ContentConnector;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.v7.data.Item;
import com.vaadin.server.ExternalResource;

import io.sphere.sdk.products.Image;

/**
 * Image provider implementation for displaying commercetools product images.
 */
public class CommercetoolsImageProvider implements ImageProvider {

    private static final Logger log = LoggerFactory.getLogger(CommercetoolsImageProvider.class);

    private final CommercetoolsContentConnector contentConnector;

    @Inject
    public CommercetoolsImageProvider(ContentConnector contentConnector) {
        this.contentConnector = (CommercetoolsContentConnector) contentConnector;
    }

    @Override
    public String getPortraitPath(final Object itemId) {
        return null;
    }

    @Override
    public String getThumbnailPath(final Object itemId) {
        return null;
    }

    @Override
    public String resolveIconClassName(final String mimeType) {
        return null;
    }

    @Override
    public Object getThumbnailResource(final Object itemId, final String generator) {
        final Item item = contentConnector.getItem(itemId);
        if (item instanceof CommercetoolsProductItem) {
            List<Image> images = (((CommercetoolsProductItem) item).getBean()).getMasterVariant().getImages();
            if (!images.isEmpty()) {
                String url = images.get(0).getUrl();
                try {
                    URL sourceSmall = new URL(StringUtils.substringBeforeLast(url, ".") + "-small." + StringUtils.substringAfterLast(url, "."));
                    return new ExternalResource(sourceSmall);
                } catch (IOException e) {
                    log.warn("Unable to obtain small image from url {} for product {}", url, itemId, e.getMessage());
                }
            }
        }
        return null;
    }
}
