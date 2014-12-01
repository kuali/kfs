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

import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates that an accounting line has fringe benefit object code 
 */
public class BenefitExpenseTransferFringeBenefitObjectCodeValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * only fringe benefit labor object codes are allowed on the benefit expense transfer document
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        AccountingLine accountingLine = getAccountingLineForValidation();
        if (!isFringeBenefitObjectCode(accountingLine)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, LaborKeyConstants.INVALID_FRINGE_OBJECT_CODE_ERROR );
            result = false;
        }
        return result;
    }

    /**
     * Checks whether the given AccountingLine's Object Code is a fringe benefit object code.
     * 
     * @param accountingLine The accounting line the fringe benefit object code will be retrieved from.
     * @return True if the given accounting line's object code is a fringe benefit object code, false otherwise.
     */ 
    protected boolean isFringeBenefitObjectCode(AccountingLine accountingLine) {
        boolean fringeObjectCode = true ;
        
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        expenseTransferAccountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT); 
        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (ObjectUtils.isNull(laborObject)) {
            return false;
        }
        boolean isItFringeObjectCode = LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE.equals(laborObject.getFinancialObjectFringeOrSalaryCode());
        if (!isItFringeObjectCode) {
            fringeObjectCode = false ;
        }
        
        return fringeObjectCode ;
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
