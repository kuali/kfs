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

import static org.kuali.kfs.sys.KFSConstants.DOCUMENT_ERRORS;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.fp.document.AuxiliaryVoucherDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that the accounting period given by the document is currently open
 */
public class AuxiliaryVoucherAccountingPeriodOpenValidation extends GenericValidation {
    private AuxiliaryVoucherDocument auxliaryVoucherDocumentForValidation;
    private AccountingPeriodService accountingPeriodService;

    /**
     * Uses the accounting period service to get the accounting period for the document and checks that it's open
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AccountingPeriod acctPeriod = getAccountingPeriodService().getByPeriod(auxliaryVoucherDocumentForValidation.getPostingPeriodCode(), auxliaryVoucherDocumentForValidation.getPostingYear());
        
        //  can't post into a closed period
        if (acctPeriod == null || acctPeriod.isActive()) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_ACCOUNTING_PERIOD_CLOSED);
            return false;
        }
        
        return true;
    }

    /**
     * Gets the accountingPeriodService attribute. 
     * @return Returns the accountingPeriodService.
     */
    public AccountingPeriodService getAccountingPeriodService() {
        return accountingPeriodService;
    }

    /**
     * Sets the accountingPeriodService attribute value.
     * @param accountingPeriodService The accountingPeriodService to set.
     */
    public void setAccountingPeriodService(AccountingPeriodService accountingPeriodService) {
        this.accountingPeriodService = accountingPeriodService;
    }

    /**
     * Gets the auxliaryVoucherDocumentForValidation attribute. 
     * @return Returns the auxliaryVoucherDocumentForValidation.
     */
    public AuxiliaryVoucherDocument getAuxliaryVoucherDocumentForValidation() {
        return auxliaryVoucherDocumentForValidation;
    }

    /**
     * Sets the auxliaryVoucherDocumentForValidation attribute value.
     * @param auxliaryVoucherDocumentForValidation The auxliaryVoucherDocumentForValidation to set.
     */
    public void setAuxliaryVoucherDocumentForValidation(AuxiliaryVoucherDocument auxliaryVoucherDocumentForValidation) {
        this.auxliaryVoucherDocumentForValidation = auxliaryVoucherDocumentForValidation;
    }
}
