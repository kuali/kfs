/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.validation;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AttributedDocumentEvent;

/**
 * 
 */
public class AccountingLineCheckValidationHutch implements Validation {
    private Validation lineAmountValidation;
    private Validation lineCheckValidation;
    private Validation lineValuesAllowedValidation;
    
    private String accountingDocumentParameterPropertyName;
    private String accountingLineParameterPropertyName;
    
    private AccountingDocument accountingDocumentForValidation;
    private AccountingLine accountingLineForValidation;
    
    private boolean quitOnFail;

    /**
     * @see org.kuali.kfs.validation.Validation#shouldQuitOnFail()
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
     * @see org.kuali.kfs.validation.Validation#stageValidation(org.kuali.kfs.rule.event.AttributedDocumentEvent)
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
     * @see org.kuali.kfs.validation.Validation#validate(org.kuali.kfs.rule.event.AttributedDocumentEvent)
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
