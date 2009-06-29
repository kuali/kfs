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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSConstants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_TYPE_CODE;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * Validation that if the Journal Voucher is using an external encumbrance balance type, reference fields are included on each accounting line 
 */
public class JournalVoucherAccountingLineExternalEncumbranceReferenceValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private DataDictionaryService dataDictionaryService;

    /**
     * This method checks that values exist in the three reference fields (referenceOriginCode, referenceTypeCode, referenceNumber)
     * that are required if the balance type is set to EXTERNAL ENCUMBRANCE.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        if (BALANCE_TYPE_EXTERNAL_ENCUMBRANCE.equals(getAccountingLineForValidation().getBalanceTypeCode())) {
            boolean valid = true;

            BusinessObjectEntry boe = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(VoucherSourceAccountingLine.class.getName());
            if (StringUtils.isEmpty(getAccountingLineForValidation().getReferenceOriginCode())) {
                putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
                valid = false;
            }
            if (StringUtils.isEmpty(getAccountingLineForValidation().getReferenceNumber())) {
                putRequiredPropertyError(boe, REFERENCE_NUMBER);
                valid = false;
            }
            if (StringUtils.isEmpty(getAccountingLineForValidation().getReferenceTypeCode())) {
                putRequiredPropertyError(boe, REFERENCE_TYPE_CODE);
                valid = false;
            }
            return valid;
        }
        return true;
    }
    
    /**
     * Adds a global error for a missing required property. This is used for properties, such as reference origin code, which cannot
     * be required by the DataDictionary validation because not all documents require them.
     * 
     * @param boe
     * @param propertyName
     */
    protected void putRequiredPropertyError(BusinessObjectEntry boe, String propertyName) {
        String label = boe.getAttributeDefinition(propertyName).getShortLabel();
        GlobalVariables.getMessageMap().putError(propertyName, KFSKeyConstants.ERROR_REQUIRED, label);
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
    public void setAccountingLineForValidation(AccountingLine voucherSourceAccountingLine) {
        this.accountingLineForValidation = voucherSourceAccountingLine;
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
}
