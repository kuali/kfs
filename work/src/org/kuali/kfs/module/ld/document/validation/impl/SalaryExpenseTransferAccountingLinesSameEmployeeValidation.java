/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.List;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants; 
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument ;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase ;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;

/**
 * Validates that an accounting document's accounting lines have the same Employee ID 
 */
public class SalaryExpenseTransferAccountingLinesSameEmployeeValidation extends GenericValidation {
    private SalaryExpenseTransferDocument accountingDocumentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document have the same employee id 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = getAccountingDocumentForValidation() ;
        
        String employeeID = salaryExpenseTransferDocument.getEmplid() ;
        
        if (employeeID == null || employeeID.trim().length() == 0) {
            GlobalVariables.getErrorMap().putError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.MISSING_EMPLOYEE_ID) ;
            result = false ;
        }
        else {
            List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = salaryExpenseTransferDocument.getSourceAccountingLines() ;
            
            if (!hasAccountingLinesSameEmployee(sourceAccountingLines, employeeID)) {
                 GlobalVariables.getErrorMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EMPLOYEE_ID_NOT_SAME) ;
                 result = false ;
            }
            else {
                List<ExpenseTransferSourceAccountingLine> targetAccountingLines = salaryExpenseTransferDocument.getTargetAccountingLines() ;
                
                if (!hasAccountingLinesSameEmployee(targetAccountingLines, employeeID)) {
                     GlobalVariables.getErrorMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EMPLOYEE_ID_NOT_SAME_IN_TARGET) ;
                     result = false ;
                }
            }
        }        
        
        return result ;    
    }

    /**
     * Checks whether amounts by object codes are unchanged
     * 
     * @param accountingDocumentForValidation The accounting document from which the amounts by objects codes are checked
     * @return True if the given accounting documents amounts by object code are unchanged, false otherwise.
     */ 
    private boolean hasAccountingLinesSameEmployee(List<ExpenseTransferSourceAccountingLine> AccountingLines, String employeeID) {
        boolean sameEmployee  = true ;
        String accountingLineEmplID = null;
        
        for (ExpenseTransferSourceAccountingLine accountingLine : AccountingLines)
        {
            accountingLineEmplID = accountingLine.getEmplid();
            if (accountingLineEmplID == null || (!employeeID.equals(accountingLineEmplID))) {
                return false;
            }
        }
        return sameEmployee ;
        
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public SalaryExpenseTransferDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingLineForValidation(SalaryExpenseTransferDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
