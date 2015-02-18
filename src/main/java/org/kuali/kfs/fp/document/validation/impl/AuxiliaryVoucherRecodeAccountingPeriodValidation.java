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
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC;
import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC;
import static org.kuali.kfs.sys.KFSKeyConstants.AuxiliaryVoucher.ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE;

import java.sql.Date;
import java.sql.Timestamp;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.fp.document.AuxiliaryVoucherDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation that checks whether the accounting period of an AuxiliaryVoucher document is allowable,
 * but only for recode-type AVs.
 */
public class AuxiliaryVoucherRecodeAccountingPeriodValidation extends GenericValidation {
    private AuxiliaryVoucherDocument auxiliaryVoucherDocumentForValidation;
    private AccountingPeriodService accountingPeriodService;

    /**
     * Validates the accounting period on an AV doc if the doc is a recode type, with the following rules:
     * <ol>
     *  <li>The accounting period does not occur in a previous fiscal year</li>
     *  <li>The accounting period is between 1 and 12 (13 can't be recoded, nor can the non-numeric periods: AB, BB, and CB</li>
     * </ol>
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        AccountingPeriod acctPeriod = getAccountingPeriodService().getByPeriod(auxiliaryVoucherDocumentForValidation.getPostingPeriodCode(), auxiliaryVoucherDocumentForValidation.getPostingYear());
        
        if (auxiliaryVoucherDocumentForValidation.isRecodeType()) {
            boolean numericPeriod = true;
            Integer period = null;
            try {
                period = new Integer(auxiliaryVoucherDocumentForValidation.getPostingPeriodCode());
            }
            catch (NumberFormatException nfe) {
                numericPeriod = false;
            }
            Integer year = auxiliaryVoucherDocumentForValidation.getPostingYear();
            Timestamp ts = new Timestamp(new java.util.Date().getTime());
            AccountingPeriod currPeriod = getAccountingPeriodService().getByDate(new Date(ts.getTime()));
            
            // can't post into a previous fiscal year
            Integer currFiscalYear = currPeriod.getUniversityFiscalYear();
            if (currFiscalYear > year) {
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_FISCAL_YEAR_AVRC);
                return false;
            }
            if (numericPeriod) {
                // check the posting period, throw out if period 13
                if (period > 12) {
                    GlobalVariables.getMessageMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                    return false;
                }
                else if (period < 1) {
                    GlobalVariables.getMessageMap().putError(DOCUMENT_ERRORS, ERROR_ACCOUNTING_PERIOD_OUT_OF_RANGE);
                    return false;
                }
            }
            else {
                // not a numeric period and this is a recode? Then we won't allow it; ref KULRNE-6001
                GlobalVariables.getMessageMap().putError(DOCUMENT_ERRORS, ERROR_DOCUMENT_AV_INCORRECT_POST_PERIOD_AVRC);
                return false;
            }
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
     * Gets the auxiliaryVoucherDocumentForValidation attribute. 
     * @return Returns the auxiliaryVoucherDocumentForValidation.
     */
    public AuxiliaryVoucherDocument getAuxiliaryVoucherDocumentForValidation() {
        return auxiliaryVoucherDocumentForValidation;
    }

    /**
     * Sets the auxiliaryVoucherDocumentForValidation attribute value.
     * @param auxiliaryVoucherDocumentForValidation The auxiliaryVoucherDocumentForValidation to set.
     */
    public void setAuxiliaryVoucherDocumentForValidation(AuxiliaryVoucherDocument auxiliaryVoucherDocumentForValidation) {
        this.auxiliaryVoucherDocumentForValidation = auxiliaryVoucherDocumentForValidation;
    }
}
