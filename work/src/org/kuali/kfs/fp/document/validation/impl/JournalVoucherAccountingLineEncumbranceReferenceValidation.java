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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSPropertyConstants.ENCUMBRANCE_UPDATE_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_NUMBER;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_ORIGIN_CODE;
import static org.kuali.kfs.sys.KFSPropertyConstants.REFERENCE_TYPE_CODE;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation that if the Journal Voucher is using an encumbrance balance type, reference fields are included on each accounting line 
 */
public class JournalVoucherAccountingLineEncumbranceReferenceValidation extends GenericValidation {
    private JournalVoucherDocument journalVoucherForValidation;
    private AccountingLine accountingLineForValidation;
    
    private DataDictionaryService dataDictionaryService;

    /**
     * This method checks that values exist in the three reference fields (referenceOriginCode, referenceTypeCode, referenceNumber)
     * that are required if 
     * 1) the balance type is of type Encumbrance 
     * 2) encumbrance update code is R
     * 
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {        
        boolean valid = true;
        if (isEncumbranceBalanceType(getAccountingLineForValidation().getBalanceTypeCode())) {            
            if (StringUtils.isBlank(getAccountingLineForValidation().getEncumbranceUpdateCode())) {
                BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(VoucherSourceAccountingLine.class.getName());
                putRequiredPropertyError(boe, ENCUMBRANCE_UPDATE_CODE);
                valid = false;
            } else if (KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(getAccountingLineForValidation().getEncumbranceUpdateCode())){
                //check the required reference fields
                if (StringUtils.isBlank(getAccountingLineForValidation().getReferenceOriginCode())) {
                    BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(VoucherSourceAccountingLine.class.getName());
                    putRequiredPropertyError(boe, REFERENCE_ORIGIN_CODE);
                    valid = false;
                }
                if (StringUtils.isBlank(getAccountingLineForValidation().getReferenceNumber())) {
                    BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(VoucherSourceAccountingLine.class.getName());
                    putRequiredPropertyError(boe, REFERENCE_NUMBER);
                    valid = false;
                }
                if (StringUtils.isBlank(getAccountingLineForValidation().getReferenceTypeCode())) {
                    BusinessObjectEntry boe = (BusinessObjectEntry) getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(VoucherSourceAccountingLine.class.getName());
                    putRequiredPropertyError(boe, REFERENCE_TYPE_CODE);
                    valid = false;
                }
            }
        }
        return valid;
    }
    
    /**
     * Using the document accounting period to determine university fiscal year and look up all the encumbrance
     * balance type - check if the selected balance type is for encumbrance
     * 
     * @return true/false  - true if it is an encumbrance balance type
     */
    private boolean isEncumbranceBalanceType(String balanceTypeCode){
        getJournalVoucherForValidation().refreshReferenceObject(KFSPropertyConstants.ACCOUNTING_PERIOD);
        AccountingPeriod accountingPeriod = getJournalVoucherForValidation().getAccountingPeriod();
        
        //get encumbrance balance type list
        BalanceTypeService balanceTypeService = SpringContext.getBean(BalanceTypeService.class);
        List<String> encumbranceBalanceTypes = balanceTypeService.getEncumbranceBalanceTypes(accountingPeriod.getUniversityFiscalYear());
        
        return encumbranceBalanceTypes.contains(balanceTypeCode);
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
     * Gets the journalVoucherForValidation attribute. 
     * @return Returns the journalVoucherForValidation.
     */
    public JournalVoucherDocument getJournalVoucherForValidation() {
        return journalVoucherForValidation;
    }

    /**
     * Sets the journalVoucherForValidation attribute value.
     * @param journalVoucherForValidation The journalVoucherForValidation to set.
     */
    public void setJournalVoucherForValidation(JournalVoucherDocument journalVoucherForValidation) {
        this.journalVoucherForValidation = journalVoucherForValidation;
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
