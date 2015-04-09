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
