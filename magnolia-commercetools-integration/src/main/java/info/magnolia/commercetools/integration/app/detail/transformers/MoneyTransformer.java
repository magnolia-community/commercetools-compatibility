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
package info.magnolia.commercetools.integration.app.detail.transformers;

import info.magnolia.ui.api.i18n.I18NAuthoringSupport;
import info.magnolia.ui.form.field.definition.ConfiguredFieldDefinition;
import info.magnolia.ui.form.field.transformer.basic.BasicTransformer;

import javax.money.MonetaryAmount;

import org.javamoney.moneta.FastMoney;

import com.vaadin.v7.data.Item;

/**
 * Transformer for MoneyImpl.
 *
 * @deprecated since 1.2, use {@link info.magnolia.commercetools.integration.app.detail.converters.MonetaryAmountConverter} for {@link com.vaadin.v7.data.util.BeanItem} instead. *
 */
@Deprecated
public class MoneyTransformer extends BasicTransformer {

    public MoneyTransformer(Item relatedFormItem, ConfiguredFieldDefinition definition, Class type, I18NAuthoringSupport i18NAuthoringSupport) {
        super(relatedFormItem, definition, type, i18NAuthoringSupport);
    }

    @Override
    public String readFromItem() {
        MonetaryAmount moneyAmount = ((MonetaryAmount) getOrCreateProperty(type).getValue());
        if (moneyAmount != null) {
            return FastMoney.of(moneyAmount.getNumber(), moneyAmount.getCurrency()).toString();
        } else {
            return "";
        }
    }
}
