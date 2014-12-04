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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * determine whether the given accounting line has already been in the given document
 * 
 * @param accountingDocument the given document
 * @param accountingLine the given accounting line
 * @return true if the given accounting line has already been in the given document; otherwise, false
 */
public class LaborExpenseTransferDuplicateSourceAccountingLineValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    private AccountingDocument accountingDocumentForValidation;
    
    /**
     * Validates that an accounting line does not duplicate in the source lines
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AccountingDocument accountingDocument = getAccountingDocumentForValidation();
        AccountingLine accountingLine = getAccountingLineForValidation();
               
        // not allow the duplicate source accounting line in the document
        if (isDuplicateSourceAccountingLine(accountingDocument, accountingLine)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_DUPLICATE_SOURCE_ACCOUNTING_LINE );
            return false;
        }
        
        return result;
    }

    /**
     * determine whether the given accounting line has already been in the given document
     * 
     * @param accountingDocument the given document
     * @param accountingLine the given accounting line
     * @return true if the given accounting line has already been in the given document; otherwise, false
     */
    protected boolean isDuplicateSourceAccountingLine(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        // only check source accounting lines
        if (!(accountingLine instanceof ExpenseTransferSourceAccountingLine)) {
            return false;
        }

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();
        List<String> key = defaultKeyOfExpenseTransferAccountingLine();

        int counter = 0;
        for (AccountingLine sourceAccountingLine : sourceAccountingLines) {
            boolean isExisting = ObjectUtil.equals(accountingLine, sourceAccountingLine, key);
            counter = isExisting ? counter + 1 : counter;

            if (counter > 1) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Gets the default key of ExpenseTransferAccountingLine
     * 
     * @return the default key of ExpenseTransferAccountingLine
     */
    protected List<String> defaultKeyOfExpenseTransferAccountingLine() {
        List<String> defaultKey = new ArrayList<String>();

        defaultKey.add(KFSPropertyConstants.POSTING_YEAR);
        defaultKey.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        defaultKey.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        defaultKey.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        defaultKey.add(KFSPropertyConstants.BALANCE_TYPE_CODE);
        defaultKey.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        defaultKey.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        defaultKey.add(KFSPropertyConstants.EMPLID);
        defaultKey.add(KFSPropertyConstants.POSITION_NUMBER);

        defaultKey.add(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR);
        defaultKey.add(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);

        return defaultKey;
    }
    
    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
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
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
