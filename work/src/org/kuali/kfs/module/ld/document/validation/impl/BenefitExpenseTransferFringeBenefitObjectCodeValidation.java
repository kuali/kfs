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
