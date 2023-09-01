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
package info.magnolia.commercetools.integration.setup;

import info.magnolia.commercetools.integration.app.detail.converters.LocalizedStringConverter;
import info.magnolia.commercetools.integration.app.detail.converters.MonetaryAmountConverter;
import info.magnolia.commercetools.integration.app.detail.transformers.LocalizedStringTransformer;
import info.magnolia.commercetools.integration.app.detail.transformers.MoneyTransformer;
import info.magnolia.module.InstallContext;
import info.magnolia.module.delta.NodeVisitorTask;
import info.magnolia.repository.RepositoryConstants;

import javax.jcr.Node;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Task for replacing {@link LocalizedStringTransformer} and {@link MoneyTransformer} with {@link LocalizedStringConverter} and {@link MonetaryAmountConverter}.
 */
public class ReplaceTransformersWithConvertersTask extends NodeVisitorTask {

    private static final Logger log = LoggerFactory.getLogger(ReplaceTransformersWithConvertersTask.class);

    private static final String CT_DETAIL_SUBAPP_FORM = "/modules/commercetools-integration/apps/ctBrowser/subApps/detail/editor/form";
    private static final String CONVERTERCLASS_PROPERTY_NAME = "converterClass";
    private static final String TRANSFORMERCLASS_PROPERTY_NAME = "transformerClass";

    public ReplaceTransformersWithConvertersTask() {
        super("Replace transformers with converters.", "Replace " + LocalizedStringTransformer.class.getName() + " and " + MoneyTransformer.class.getName()  + " with " + LocalizedStringConverter.class.getName() + " and  " + MonetaryAmountConverter.class.getName(), RepositoryConstants.CONFIG, CT_DETAIL_SUBAPP_FORM);
    }

    @Override
    protected boolean nodeMatches(Node node) {
        try {
            return node.hasProperty(TRANSFORMERCLASS_PROPERTY_NAME);
        } catch (RepositoryException e) {
            log.error("Cannot check if {} has property {}.", node, TRANSFORMERCLASS_PROPERTY_NAME, e);
            return false;
        }
    }

    @Override
    protected void operateOnNode(InstallContext installContext, Node node) {
        try {
            PropertyIterator iterator = node.getProperties();
            while (iterator.hasNext()) {
                Property property = iterator.nextProperty();
                if (property.getType() == PropertyType.STRING) {
                    if (property.getString().equals(LocalizedStringTransformer.class.getName())) {
                        property.getParent().setProperty(CONVERTERCLASS_PROPERTY_NAME, LocalizedStringConverter.class.getName());
                        property.remove();
                    } else if (property.getString().equals(MoneyTransformer.class.getName())) {
                        property.getParent().setProperty(CONVERTERCLASS_PROPERTY_NAME, MonetaryAmountConverter.class.getName());
                        property.remove();
                    }
                }
            }
        } catch (RepositoryException e) {
            installContext.error(e.getMessage(), e);
        }
    }
}
