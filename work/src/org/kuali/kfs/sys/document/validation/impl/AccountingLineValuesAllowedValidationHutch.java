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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * A cleaner way to conglomerate closely related rules together, this validation checks that
 * all of the values on the accounting line are allowed by a given document.  The advantage of this hutch
 * over normal composites is that the hutch has "named slots" for given validations, which makes it a bit easier to keep track
 * of everything.
 */
public class AccountingLineValuesAllowedValidationHutch implements Validation {
    protected Validation objectCodeAllowedValidation;
    protected Validation objectTypeAllowedValidation;
    protected Validation fundGroupAllowedValidation;
    protected Validation subFundGroupAllowedValidation;
    protected Validation objectSubTypeAllowedValidation;
    protected Validation objectLevelAllowedValidation;
    protected Validation objectConsolidationAllowedValidation;
    
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
    public void setQuitOnFail(boolean b) {
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
     * Returns a list of all the validations the hutch has to pass in order to pass as a whole.
     * @return a List of Validations
     */
    protected List<Validation> getValidationGauntlet() {
        List<Validation> gauntlet = new ArrayList<Validation>();
        if (objectCodeAllowedValidation != null) {
            gauntlet.add(objectCodeAllowedValidation);
        }
        if (objectTypeAllowedValidation != null) {
            gauntlet.add(objectTypeAllowedValidation);
        }
        if (fundGroupAllowedValidation != null) {
            gauntlet.add(fundGroupAllowedValidation);
        }
        if (subFundGroupAllowedValidation != null) {
            gauntlet.add(subFundGroupAllowedValidation);
        }
        if (objectSubTypeAllowedValidation != null) {
            gauntlet.add(objectSubTypeAllowedValidation);
        }
        if (objectLevelAllowedValidation != null) {
            gauntlet.add(objectLevelAllowedValidation);
        }
        if (objectConsolidationAllowedValidation != null) {
            gauntlet.add(objectConsolidationAllowedValidation);
        }
        return gauntlet;
    }
    
    /**
     * Using the parameter property names set, finds the accounting document and accounting line to be validate
     * from the property, like an anteater getting tasty termites from a hill.  Yummy.
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
     * Goes through each of the validations in the hutch, making sure each has the accounting document and accounting line to validate
     */
    protected void updateValidationsWithParameters() { 
        for (Validation v: getValidationGauntlet()) {
            if (v instanceof AccountingLineValueAllowedValidation) {
                addParametersToValidation((AccountingLineValueAllowedValidation)v);
            } else if (v instanceof CompositeValidation) {
                addParametersToValidation((CompositeValidation)v);
            } else {
                throw new IllegalStateException("Validations in the AccountingLineValuesAllowedValidationHutch must either extend AccountingLineValueAllowedValidation or be a CompositeValidation made up of AccountingLineValueAllowedValidation instances");
            }
        }
    }
    
    /**
     * Adds the parameter properties to an instance of the AccountingLinevAlueAllowedValidation
     * @param validation the validation to add the correct properties to
     */
    protected void addParametersToValidation(AccountingLineValueAllowedValidation validation) {
        validation.setAccountingDocumentForValidation(accountingDocumentForValidation);
        validation.setAccountingLineForValidation(accountingLineForValidation);
    }
    
    /**
     * Adds the parameter properties to the children validations of a CompositeValidation
     * @param validation the validation to add the correct parameters to
     */
    protected void addParametersToValidation(CompositeValidation validation) {
        for (Validation val : validation.getValidations()) {
            if (val instanceof CompositeValidation) {
                addParametersToValidation((CompositeValidation)val);
            } else if (val instanceof AccountingLineValueAllowedValidation) {
                addParametersToValidation((AccountingLineValueAllowedValidation)val);
            } else {
                throw new IllegalStateException("Validations in the AccountingLineValuesAllowedValidationHutch must either extend AccountingLineValueAllowedValidation or be a CompositeValidation made up of AccountingLineValueAllowedValidation instances");
            }
        }
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        for (Validation validation : getValidationGauntlet()) {
            result &= validation.validate(event);
        }
        return result;
    }

    /**
     * Gets the fundGroupAllowedValidation attribute. 
     * @return Returns the fundGroupAllowedValidation.
     */
    public Validation getFundGroupAllowedValidation() {
        return fundGroupAllowedValidation;
    }

    /**
     * Sets the fundGroupAllowedValidation attribute value.
     * @param fundGroupAllowedValidation The fundGroupAllowedValidation to set.
     */
    public void setFundGroupAllowedValidation(Validation fundGroupAllowedValidation) {
        this.fundGroupAllowedValidation = fundGroupAllowedValidation;
    }

    /**
     * Gets the objectCodeAllowedValidation attribute. 
     * @return Returns the objectCodeAllowedValidation.
     */
    public Validation getObjectCodeAllowedValidation() {
        return objectCodeAllowedValidation;
    }

    /**
     * Sets the objectCodeAllowedValidation attribute value.
     * @param objectCodeAllowedValidation The objectCodeAllowedValidation to set.
     */
    public void setObjectCodeAllowedValidation(Validation objectCodeAllowedValidation) {
        this.objectCodeAllowedValidation = objectCodeAllowedValidation;
    }

    /**
     * Gets the objectConsolidationAllowedValidation attribute. 
     * @return Returns the objectConsolidationAllowedValidation.
     */
    public Validation getObjectConsolidationAllowedValidation() {
        return objectConsolidationAllowedValidation;
    }

    /**
     * Sets the objectConsolidationAllowedValidation attribute value.
     * @param objectConsolidationAllowedValidation The objectConsolidationAllowedValidation to set.
     */
    public void setObjectConsolidationAllowedValidation(Validation objectConsolidationAllowedValidation) {
        this.objectConsolidationAllowedValidation = objectConsolidationAllowedValidation;
    }

    /**
     * Gets the objectLevelAllowedValidation attribute. 
     * @return Returns the objectLevelAllowedValidation.
     */
    public Validation getObjectLevelAllowedValidation() {
        return objectLevelAllowedValidation;
    }

    /**
     * Sets the objectLevelAllowedValidation attribute value.
     * @param objectLevelAllowedValidation The objectLevelAllowedValidation to set.
     */
    public void setObjectLevelAllowedValidation(Validation objectLevelAllowedValidation) {
        this.objectLevelAllowedValidation = objectLevelAllowedValidation;
    }

    /**
     * Gets the objectSubTypeAllowedValidation attribute. 
     * @return Returns the objectSubTypeAllowedValidation.
     */
    public Validation getObjectSubTypeAllowedValidation() {
        return objectSubTypeAllowedValidation;
    }

    /**
     * Sets the objectSubTypeAllowedValidation attribute value.
     * @param objectSubTypeAllowedValidation The objectSubTypeAllowedValidation to set.
     */
    public void setObjectSubTypeAllowedValidation(Validation objectSubTypeAllowedValidation) {
        this.objectSubTypeAllowedValidation = objectSubTypeAllowedValidation;
    }

    /**
     * Gets the objectTypeAllowedValidation attribute. 
     * @return Returns the objectTypeAllowedValidation.
     */
    public Validation getObjectTypeAllowedValidation() {
        return objectTypeAllowedValidation;
    }

    /**
     * Sets the objectTypeAllowedValidation attribute value.
     * @param objectTypeAllowedValidation The objectTypeAllowedValidation to set.
     */
    public void setObjectTypeAllowedValidation(Validation objectTypeAllowedValidation) {
        this.objectTypeAllowedValidation = objectTypeAllowedValidation;
    }

    /**
     * Gets the subFundGroupAllowedValidation attribute. 
     * @return Returns the subFundGroupAllowedValidation.
     */
    public Validation getSubFundGroupAllowedValidation() {
        return subFundGroupAllowedValidation;
    }

    /**
     * Sets the subFundGroupAllowedValidation attribute value.
     * @param subFundGroupAllowedValidation The subFundGroupAllowedValidation to set.
     */
    public void setSubFundGroupAllowedValidation(Validation subFundGroupAllowedValidation) {
        this.subFundGroupAllowedValidation = subFundGroupAllowedValidation;
    }

    /**
     * Gets the accountingDocumentParameterPropertyName attribute. 
     * @return Returns the accountingDocumentParameterPropertyName.
     */
    public String getAccountingDocumentParameterPropertyName() {
        return accountingDocumentParameterPropertyName;
    }

    /**
     * Sets the accountingDocumentParameterPropertyName attribute value.
     * @param accountingDocumentParameterPropertyName The accountingDocumentParameterPropertyName to set.
     */
    public void setAccountingDocumentParameterPropertyName(String accountingDocumentParameterPropertyName) {
        this.accountingDocumentParameterPropertyName = accountingDocumentParameterPropertyName;
    }

    /**
     * Gets the accountingLineParameterPropertyName attribute. 
     * @return Returns the accountingLineParameterPropertyName.
     */
    public String getAccountingLineParameterPropertyName() {
        return accountingLineParameterPropertyName;
    }

    /**
     * Sets the accountingLineParameterPropertyName attribute value.
     * @param accountingLineParameterPropertyName The accountingLineParameterPropertyName to set.
     */
    public void setAccountingLineParameterPropertyName(String accountingLineParameterPropertyName) {
        this.accountingLineParameterPropertyName = accountingLineParameterPropertyName;
    }

    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }

    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }
}
