/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.BusinessObjectEntry;
import org.kuali.rice.krad.util.GlobalVariables;

public class PreEncumbranceRequiredReferenceFieldValidation extends GenericValidation {
    private DataDictionaryService dataDictionaryService;
    private AccountingLine accountingLineForValidation;

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        if (this.getAccountingLineForValidation().isTargetAccountingLine()) {
            final BusinessObjectEntry boe = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(getAccountingLineForValidation().getClass().getName());
            
            if (StringUtils.isEmpty(getAccountingLineForValidation().getReferenceNumber())) {
                putRequiredPropertyError(boe, KFSPropertyConstants.REFERENCE_NUMBER);
                valid = false;
            }
        }
        return valid;
    }

    /**
     * Adds a global error for a missing required property. This is used for properties, such as reference origin code, which cannot
     * be required by the DataDictionary validation because not all documents require them.
     * 
     * @param boe
     * @param propertyName
     */
    protected void putRequiredPropertyError(BusinessObjectEntry boe, String propertyName) {
        final String label = boe.getAttributeDefinition(propertyName).getShortLabel();
        GlobalVariables.getMessageMap().putError(propertyName, KFSKeyConstants.ERROR_REQUIRED, label);
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accoutingLineForValidation) {
        this.accountingLineForValidation = accoutingLineForValidation;
    }
}
