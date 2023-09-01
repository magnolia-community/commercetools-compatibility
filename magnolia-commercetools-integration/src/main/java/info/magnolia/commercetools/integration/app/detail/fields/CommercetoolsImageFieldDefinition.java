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
package info.magnolia.commercetools.integration.app.detail.fields;

import info.magnolia.dam.app.ui.field.configuration.PreviewComponentProvider;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;

/**
 * Field definition for {@link CommercetoolsImageField}.
 */
public class CommercetoolsImageFieldDefinition extends ConfiguredFieldDefinition {

    private Class<? extends PreviewComponentProvider> previewComponentProviderClass;

    public Class<? extends PreviewComponentProvider> getPreviewComponentProviderClass() {
        return previewComponentProviderClass;
    }

    public void setPreviewComponentProviderClass(Class<? extends PreviewComponentProvider> previewComponentProviderClass) {
        this.previewComponentProviderClass = previewComponentProviderClass;
    }
}
