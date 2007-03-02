/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborObject;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 * 
 * 
 */
public class BenefitExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules {

    public BenefitExpenseTransferDocumentRule() {
    }   
    
    protected boolean AddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);
    }
    
    /** 
     * The following criteria will be validated here:
     * Account must be valid.
     * Object code must be valid.
     * Object code must be a labor object code.
            Object code must exist in the ld_labor_obj_t table.
            The field finobj_frngslry_cd for the object code in the ld_labor_obj_t table must have a value of "S".
     * Sub-account, if specified, must be valid for account.
     * Sub-object, if specified, must be valid for account and object code.
     * Enforce the A21-report-related business rules for the "SAVE" action.
     * Position must be valid for fiscal year. FIS enforces this by a direct lookup of the PeopleSoft HRMS position data table. Kuali cannot do this. (See issue 12.)
     * Employee ID exists.
     * Employee does not have pending salary transfers.
     * Amount must not be zero. 
     * --------------------------------------------------------------------------------------------------------
     * Only fringe benefit labor object codes are allowed on this document.
     * The document must have at least one “FROM” segment and one “TO” segment.
     * The total amount on the “FROM” side must equal the total amount on the “TO” side.
     * Transfers cannot be made between two different fringe benefit labor object codes. Only the “Account” and “Amount” fields may be edited in the “TO” zone.
     * The Justification field is required and should include as much pertinent detail as possible.
     * The Fiscal Year field on this eDoc is used differently as compared to other TP documents. In the Benefit Transfer document, this field is used to load the appropriate data onto the Labor Ledger Balance screen.
     * Pending Ledger Entries are created immediately as part of the routing process. In addition to creating pending entries with a balance type of “AC” the Benefit Transfer document requires that a pending entry be created with a balance type of “A2”.
     * Only allow a transfer of benefit dollars up to the amount that already exist in the labor ledger detail for a given pay period.
     * Check must exist that verifies the “TO” account accepts fringes. If no benefits allowed provide error message. Allow an override flag to allow the fringe to be transferred to the account.
     * Allow a negative amount to be moved from one account to another but do not allow a negative amount to be created when the balance is positive.


     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     *      
     * @param TransactionalDocument
     * @param AccountingLine
     * @return
   */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {

        // Retrieve the labor object code to make sure it is fringe. 
        // It must have a value of "F".
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        Map fieldValues = new HashMap();
        fieldValues.put("financialObjectCode", accountingLine.getFinancialObjectCode().toString());
        ArrayList laborObjects = (ArrayList) SpringServiceLocator.getBusinessObjectService().findMatching(LaborObject.class, fieldValues);
        if (laborObjects.size() == 0) {
            reportError(PropertyConstants.ACCOUNT, KeyConstants.Labor.LABOR_OBJECT_MISSING_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }
        LaborObject laborObject = (LaborObject) laborObjects.get(0);    
        String FringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();

        if (!FringeOrSalaryCode.equals("F")) {
            LOG.info("FringeOrSalaryCode not equal F");
              reportError(PropertyConstants.ACCOUNT, KeyConstants.Labor.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }            
                                  
        // Make sure the employee does not have any pending salary transfers
//        if (!validatePendingSalaryTransfer(emplid))
  //          return false;
        
        ExpenseTransferAccountingLine benefitExpenseTransferAccountingLine = (ExpenseTransferAccountingLine)accountingLine;
        
        // Validate the accounting year
        fieldValues.clear();
        fieldValues.put("universityFiscalYear", benefitExpenseTransferAccountingLine.getPayrollEndDateFiscalYear());
        AccountingPeriod accountingPeriod = new AccountingPeriod();        
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(PropertyConstants.ACCOUNT,KeyConstants.Labor.INVALID_PAY_YEAR);
            return false;
        }
        
        // Validate the accounting period code
        fieldValues.clear();
        fieldValues.put("universityFiscalPeriodCode", benefitExpenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());
        accountingPeriod = new AccountingPeriod();        
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(PropertyConstants.ACCOUNT,KeyConstants.Labor.INVALID_PAY_PERIOD_CODE);
            return false;
        }
        return true;
    }
}