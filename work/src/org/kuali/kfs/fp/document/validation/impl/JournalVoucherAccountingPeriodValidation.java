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

import static org.kuali.kfs.sys.KFSPropertyConstants.SELECTED_ACCOUNTING_PERIOD;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validation of the accounting period on a Journal Voucher document.
 */
public class JournalVoucherAccountingPeriodValidation extends GenericValidation {
    private JournalVoucherDocument journalVoucherForValidation;
    private DataDictionaryService dataDictionaryService;

    /**
     * Checks that the accounting period for a journal voucher is present in the persistence store and currently open
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        // retrieve from system to make sure it exists
        String label = getLabelFromDataDictionary(JournalVoucherDocument.class, KFSPropertyConstants.ACCOUNTING_PERIOD);
        getJournalVoucherForValidation().refreshReferenceObject(KFSPropertyConstants.ACCOUNTING_PERIOD);
        AccountingPeriod accountingPeriod = getJournalVoucherForValidation().getAccountingPeriod();
        if (ObjectUtils.isNull(accountingPeriod)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + SELECTED_ACCOUNTING_PERIOD, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }

        // make sure it's open for use
        if (!accountingPeriod.isActive()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + SELECTED_ACCOUNTING_PERIOD, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }

        return true;
    }
    
    /**
     * Looks up a label from the data dictionary
     * @param entryClass the class of the attribute to lookup the label for
     * @param attributeName the attribute to look up the label for
     * @return the label
     */
    protected String getLabelFromDataDictionary(Class entryClass, String attributeName) {
        DataDictionaryEntry entry = getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(entryClass.getName());
        if (entry == null) {
            throw new IllegalArgumentException("Cannot find DataDictionary entry for " + entryClass);
        }
        AttributeDefinition attributeDefinition = entry.getAttributeDefinition(attributeName);
        if (attributeDefinition == null) {
            throw new IllegalArgumentException("Cannot find " + entryClass + " attribute with name " + attributeName);
        }
        return attributeDefinition.getLabel();
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
