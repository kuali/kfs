/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.document.datadictionary;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingLineRenderingService;
import org.kuali.kfs.sys.document.web.AccountingLineViewDebitCreditAmountLayoutElement;
import org.kuali.kfs.sys.document.web.TableJoining;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.FieldBridge;

/**
 * Data dictionary meta data that represents a debit/credit amount field.  By default, it expects the new line debit amount property on the form
 * to be called "newSourceLineDebit", the debit amount on a new line to be called "newSourceLineCredit", and for the
 * form to have a collection VoucherAccountingLineHelper implementation objects called "voucherLineHelpers", though
 * these can be overridden.
 */
public class AccountingLineViewDebitCreditAmountFieldDefinition extends MaintainableFieldDefinition implements AccountingLineViewRenderableElementDefinition {
    private String newLineDebitAmountProperty = "newSourceLineDebit";
    private String newLineCreditAmountProperty = "newSourceLineCredit";
    private String voucherLineHelperProperty = "voucherLineHelper";
    private boolean useShortLabels = true;
    private String amountFieldName = "amount";
    
    /**
     * @see org.kuali.kfs.sys.document.datadictionary.AccountingLineViewRenderableElementDefinition#createLayoutElement(java.lang.Class)
     */
    public TableJoining createLayoutElement(Class<? extends AccountingLine> accountingLineClass) {
        AccountingLineViewDebitCreditAmountLayoutElement layoutElement = new AccountingLineViewDebitCreditAmountLayoutElement();
        
        layoutElement.setDebitAmountField(createFieldForPropertyName(amountFieldName, accountingLineClass));
        layoutElement.setDebitFieldDefinition(createFieldDefinitionForProperty(amountFieldName));
        
        layoutElement.setCreditAmountField(createFieldForPropertyName(amountFieldName, accountingLineClass));
        layoutElement.setCreditFieldDefinition(createFieldDefinitionForProperty(amountFieldName));
        
        layoutElement.setDefinition(this);
        
        return layoutElement;
    }
    
    /**
     * Creates a field for the given AccountingLine class and property name
     * @param propertyName the name of the property to create a Field for
     * @param accountingLineClass the Class of the AccountingLine we're planning on rendering
     * @return an appropriately created Field
     */
    protected Field createFieldForPropertyName(String propertyName, Class<? extends AccountingLine> accountingLineClass) {
        Field realField = FieldUtils.getPropertyField(accountingLineClass, propertyName, false);
        FieldBridge.setupField(realField, this, null);
        if (useShortLabels) {
            org.kuali.rice.krad.datadictionary.BusinessObjectEntry boEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(accountingLineClass.getName());
            realField.setFieldLabel(boEntry.getAttributeDefinition(propertyName).getShortLabel());
        }
        return realField;
    }
    
    /**
     * Creates an AccountingLineViewFieldDefinition for the given property name
     * @param propertyName the name of the field property that we're creating a definition for
     * @return an appropriately created AccountingLineViewFieldDefinition
     */
    protected AccountingLineViewFieldDefinition createFieldDefinitionForProperty(String propertyName) {
        AccountingLineViewFieldDefinition fieldDefinition = SpringContext.getBean(AccountingLineRenderingService.class).createGenericAccountingLineViewFieldDefinition(this);
        fieldDefinition.setName(propertyName);
        return fieldDefinition;
    }

    /**
     * Gets the newLineCreditAmountProperty attribute. 
     * @return Returns the newLineCreditAmountProperty.
     */
    public String getNewLineCreditAmountProperty() {
        return newLineCreditAmountProperty;
    }

    /**
     * Sets the newLineCreditAmountProperty attribute value.
     * @param newLineCreditAmountProperty The newLineCreditAmountProperty to set.
     */
    public void setNewLineCreditAmountProperty(String newLineCreditAmountProperty) {
        this.newLineCreditAmountProperty = newLineCreditAmountProperty;
    }

    /**
     * Gets the newLineDebitAmountProperty attribute. 
     * @return Returns the newLineDebitAmountProperty.
     */
    public String getNewLineDebitAmountProperty() {
        return newLineDebitAmountProperty;
    }

    /**
     * Sets the newLineDebitAmountProperty attribute value.
     * @param newLineDebitAmountProperty The newLineDebitAmountProperty to set.
     */
    public void setNewLineDebitAmountProperty(String newLineDebitAmountProperty) {
        this.newLineDebitAmountProperty = newLineDebitAmountProperty;
    }

    /**
     * Gets the voucherLineHelpersProperty attribute. 
     * @return Returns the voucherLineHelpersProperty.
     */
    public String getVoucherLineHelperProperty() {
        return voucherLineHelperProperty;
    }

    /**
     * Sets the voucherLineHelpersProperty attribute value.
     * @param voucherLineHelpersProperty The voucherLineHelpersProperty to set.
     */
    public void setVoucherLineHelperProperty(String voucherLineHelpersProperty) {
        this.voucherLineHelperProperty = voucherLineHelpersProperty;
    }

    /**
     * Gets the useShortLabels attribute. 
     * @return Returns the useShortLabels.
     */
    public boolean shouldUseShortLabels() {
        return useShortLabels;
    }

    /**
     * Sets the useShortLabels attribute value.
     * @param useShortLabels The useShortLabels to set.
     */
    public void setUseShortLabels(boolean useShortLabels) {
        this.useShortLabels = useShortLabels;
    }

    /**
     * Gets the amountFieldName attribute. 
     * @return Returns the amountFieldName.
     */
    public String getAmountFieldName() {
        return amountFieldName;
    }

    /**
     * Sets the amountFieldName attribute value.
     * @param amountFieldName The amountFieldName to set.
     */
    public void setAmountFieldName(String amountFieldName) {
        this.amountFieldName = amountFieldName;
    }
    
}
