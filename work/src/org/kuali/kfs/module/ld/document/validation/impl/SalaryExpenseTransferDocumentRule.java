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

import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_A21;
import static org.kuali.kfs.KFSConstants.BALANCE_TYPE_ACTUAL;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.BenefitsType;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerBenefitClearingPendingEntriesRule;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;


/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 * 
 * 
 */

public class SalaryExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules implements GenerateLaborLedgerPendingEntriesRule<AccountingDocument>, GenerateLaborLedgerBenefitClearingPendingEntriesRule<AccountingDocument>{

    // LLPE KFSConstants
    public static final class LABOR_LEDGER_PENDING_ENTRY_CODE {
        public static final String NO = "N";
        public static final String YES = "Y";
        public static final String BLANK_PROJECT_STRING = "----------"; // Max length is 10 for this field
        public static final String BLANK_SUB_OBJECT_CODE = "---"; // Max length is 3 for this field
        public static final String BLANK_SUB_ACCOUNT_NUMBER = "-----"; // Max length is 5 for this field
        public static final String BLANK_OBJECT_CODE = "----"; // Max length is 4 for this field
        public static final String BLANK_OBJECT_TYPE_CODE = "--"; // Max length is 4 for this field
        public static final String BLANK_POSITION_NUMBER = "--------"; // Max length is 8 for this field
        public static final String BLANK_EMPL_ID = "-----------"; // Max length is 11 for this field
        public static final String LL_PE_OFFSET_STRING = "TP Generated Offset";
        public static final int LLPE_DESCRIPTION_MAX_LENGTH = 40;
    }

    public static final String LABOR_LEDGER_ACCOUNT_NUMBER = "9712700";
   
        
    public SalaryExpenseTransferDocumentRule() {
        super();        
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

        // Retrieve the Fringe or Salary Code for the object code in the ld_labor_obj_t table. 
        // It must have a value of "S".
        
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        Map fieldValues = new HashMap();
        fieldValues.put("financialObjectCode", accountingLine.getFinancialObjectCode().toString());
        ArrayList laborObjects = (ArrayList) SpringServiceLocator.getBusinessObjectService().findMatching(LaborObject.class, fieldValues);
        if (laborObjects.size() == 0) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.LABOR_OBJECT_MISSING_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }
        LaborObject laborObject = (LaborObject) laborObjects.get(0);    
        String FringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();

        if (!FringeOrSalaryCode.equals(LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE)) {
              reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_SALARY_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }            
            
        // Validate that an employee ID is enterred.
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument)accountingDocument;
        String emplid = salaryExpenseTransferDocument.getEmplid();
        if ((emplid == null) || (emplid.trim().length() == 0)) {
            reportError(KFSConstants.EMPLOYEE_LOOKUP_ERRORS,KFSKeyConstants.Labor.MISSING_EMPLOYEE_ID, emplid);
            return false;
        }
        
        // Make sure the employee does not have any pending salary transfers
        if (!validatePendingSalaryTransfer(emplid))
            return false;
        
        // Save the employee ID in all accounting related lines       
        ExpenseTransferAccountingLine salaryExpenseTransferAccountingLine = (ExpenseTransferAccountingLine)accountingLine;
        salaryExpenseTransferAccountingLine.setEmplid(emplid); 

        // Validate the accounting year
        fieldValues.clear();
        fieldValues.put("universityFiscalYear", salaryExpenseTransferAccountingLine.getPayrollEndDateFiscalYear());
        AccountingPeriod accountingPeriod = new AccountingPeriod();        
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(KFSPropertyConstants.ACCOUNT,KFSKeyConstants.Labor.INVALID_PAY_YEAR, emplid);
            return false;
        }
        
        // Validate the accounting period code
        fieldValues.clear();
        fieldValues.put("universityFiscalPeriodCode", salaryExpenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());
        accountingPeriod = new AccountingPeriod();        
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(KFSPropertyConstants.ACCOUNT,KFSKeyConstants.Labor.INVALID_PAY_PERIOD_CODE, emplid);
            return false;
        }
        return true;
    }
    
    /**
     * This document specific routing business rule check calls the check that makes sure that the budget year is consistent for all
     * accounting lines.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        SalaryExpenseTransferDocument setDoc = (SalaryExpenseTransferDocument) document;

        List sourceLines = new ArrayList();
        List targetLines = new ArrayList();

        //set source and target accounting lines
        sourceLines.addAll(setDoc.getSourceAccountingLines());
        targetLines.addAll(setDoc.getTargetAccountingLines());

        //check to ensure totals of accounting lines in source and target sections match
        if (isValid) {
            isValid = isAccountingLineTotalsMatch(sourceLines, targetLines);            
        }

        //check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
        if (isValid) {
            isValid = isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines);
        }
        
        return isValid;        
    }
  
    
}