/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * 
 */
public class AccountingLineCheckValidationHutch implements Validation {
    protected Validation lineAmountValidation;
    protected Validation lineCheckValidation;
    protected Validation lineValuesAllowedValidation;
    
    protected String accountingDocumentParameterPropertyName;
    protected String accountingLineParameterPropertyName;
    
    protected AccountingDocument accountingDocumentForValidation;
    protected AccountingLine accountingLineForValidation;
    
    protected boolean quitOnFail;

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#shouldQuitOnFail()
     */
    public boolean shouldQuitOnFail() {
        return quitOnFail;
    }
    
    /**
     * Sets whether the validation hutch should quit on the failure of the entire validation case failing.
     * @param b
     */
    public void setShouldQuitOnFail(boolean b) {
        quitOnFail = b;
    }

    /**
     * @see org.kuali.kfs.sys.document.validation.Validation#stageValidation(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean stageValidation(AttributedDocumentEvent event) {
        grabDocumentAndLineForValidationFromEvent(event);
        updateValidationsWithParameters();
        return validate(event);
    }
    
    /**
     * Using the parameter property names set, finds the accounting document and accounting line to be validate
     * from the property.
     * @param event the event to take properties from
     */
    protected void grabDocumentAndLineForValidationFromEvent(AttributedDocumentEvent event) {
        if (StringUtils.isNotBlank(accountingDocumentParameterPropertyName)) {
            accountingDocumentForValidation = (AccountingDocument)ObjectUtils.getPropertyValue(event, accountingDocumentParameterPropertyName);
        }
        if (StringUtils.isNotBlank(accountingLineParameterPropertyName)) {
            accountingLineForValidation = (AccountingLine)ObjectUtils.getPropertyValue(event, accountingLineParameterPropertyName);
        }
    }
    
    /**
     * Updates the child validations with accounting document and accounting line information.
     */
    protected void updateValidationsWithParameters() {
        
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * Gets the lineAmountValidation attribute. 
     * @return Returns the lineAmountValidation.
     */
    public Validation getLineAmountValidation() {
        return lineAmountValidation;
    }

    /**
     * Sets the lineAmountValidation attribute value.
     * @param lineAmountValidation The lineAmountValidation to set.
     */
    public void setLineAmountValidation(Validation lineAmountValidation) {
        this.lineAmountValidation = lineAmountValidation;
    }

    /**
     * Gets the lineCheckValidation attribute. 
     * @return Returns the lineCheckValidation.
     */
    public Validation getLineCheckValidation() {
        return lineCheckValidation;
    }

    /**
     * Sets the lineCheckValidation attribute value.
     * @param lineCheckValidation The lineCheckValidation to set.
     */
    public void setLineCheckValidation(Validation lineCheckValidation) {
        this.lineCheckValidation = lineCheckValidation;
    }

    /**
     * Gets the lineValuesAllowedValidation attribute. 
     * @return Returns the lineValuesAllowedValidation.
     */
    public Validation getLineValuesAllowedValidation() {
        return lineValuesAllowedValidation;
    }

    /**
     * Sets the lineValuesAllowedValidation attribute value.
     * @param lineValuesAllowedValidation The lineValuesAllowedValidation to set.
     */
    public void setLineValuesAllowedValidation(Validation lineValuesAllowedValidation) {
        this.lineValuesAllowedValidation = lineValuesAllowedValidation;
    }
}
