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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSPropertyConstants.BALANCE_TYPE_CODE;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.coa.businessobject.BalanceType;
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
 * Validation for the balance type on a Journal Voucher
 */
public class JournalVoucherBalanceTypeValidation extends GenericValidation {
    private JournalVoucherDocument journalVoucherForValidation;
    private DataDictionaryService dataDictionaryService;

    /**
     * Checks that the balance type on a Journal Voucher document is in the database and currently active
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        String label = getLabelFromDataDictionary(JournalVoucherDocument.class, KFSPropertyConstants.BALANCE_TYPE_CODE);
        getJournalVoucherForValidation().refreshReferenceObject(KFSPropertyConstants.BALANCE_TYPE);
        BalanceType balanceType = getJournalVoucherForValidation().getBalanceType();
        if (ObjectUtils.isNull(balanceType)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + BALANCE_TYPE_CODE, KFSKeyConstants.ERROR_EXISTENCE, label);
            return false;
        }
        // make sure it's active for usage
        if (!balanceType.isActive()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + BALANCE_TYPE_CODE, KFSKeyConstants.ERROR_INACTIVE, label);
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
