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

import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validation for Auxiliary Voucher that checks that all the source accounting lines on the document use
 * only one sub fund group among them.
 */
public class AuxiliaryVoucherSingleSubFundValidation extends GenericValidation {
    private AccountingDocument accountingDocumentForValidation;

    /**
     * Iterates <code>{@link AccountingLine}</code> instances in a given <code>{@link FinancialDocument}</code> instance and
     * compares them to see if they are all in the same Sub-Fund Group.
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;

        String baseSubFundGroupCode = null;
        int index = 0;

        List<AccountingLine> lines = getAccountingDocumentForValidation().getSourceAccountingLines();
        for (AccountingLine line : lines) {
            if (index == 0) {
                baseSubFundGroupCode = line.getAccount().getSubFundGroupCode();
            }
            else {
                String currentSubFundGroup = line.getAccount().getSubFundGroupCode();
                if (!currentSubFundGroup.equals(baseSubFundGroupCode)) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.ACCOUNTING_LINE_ERRORS, KFSKeyConstants.AuxiliaryVoucher.ERROR_DIFFERENT_SUB_FUND_GROUPS);
                    return false;
                }
            }
            index++;
        }
        return true;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public AccountingDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingDocumentForValidation(AccountingDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
