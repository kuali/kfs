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

import static org.kuali.kfs.sys.KFSPropertyConstants.REVERSAL_DATE;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.kuali.kfs.fp.document.PreEncumbranceDocument;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

/**
 * Validates that an accounting line does not have a capital object object code 
 */
public class PreEncumbranceReversalDateValidation extends GenericValidation {
    private AccountingDocumentRuleHelperService accountingDocumentRuleHelperService;
    private PreEncumbranceDocument accountingDocumentForValidation;

    /**
     * Validates that an accounting line does not have a capital object object code
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        java.sql.Date reversalDate = getAccountingDocumentForValidation().getReversalDate();
       // AccountingDocumentRuleHelperService accountingDocumentRuleUtil = getAccountingLineRuleHelperService();
        return accountingDocumentRuleHelperService.isValidReversalDate(reversalDate, DOCUMENT_ERROR_PREFIX + REVERSAL_DATE);
    }
   
    /**
     * Gets the accountingDocumentRuleHelperService attribute. 
     * @return Returns the accountingDocumentRuleHelperService.
     */
    public AccountingDocumentRuleHelperService getAccountingDocumentRuleHelperService() {
        return accountingDocumentRuleHelperService;
    }

    /**
     * Sets the accountingDocumentRuleHelperService attribute value.
     * @param accountingDocumentRuleHelperService The accountingDocumentRuleHelperService to set.
     */
    public void setAccountingDocumentRuleHelperService(AccountingDocumentRuleHelperService accountingDocumentRuleHelperService) {
        this.accountingDocumentRuleHelperService = accountingDocumentRuleHelperService;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public PreEncumbranceDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(PreEncumbranceDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }

    
   
}
