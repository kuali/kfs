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
import org.kuali.module.labor.LaborConstants.LABOR_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.BenefitsType;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerBenefitClearingPendingEntriesRule;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;


/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 * 
 * 
 */

public class SalaryExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules implements GenerateLaborLedgerBenefitClearingPendingEntriesRule<LaborLedgerPostingDocument>{

    // LLPE KFSConstants
    public static final String LABOR_LEDGER_ACCOUNT_NUMBER = "9712700";
   
        
    /**
     * Constructor
     */
    public SalaryExpenseTransferDocumentRule() {
        super();        
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
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingLine.getFinancialObjectCode().toString());
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

        ExpenseTransferAccountingLine salaryExpenseTransferAccountingLine = (ExpenseTransferAccountingLine)accountingLine;
        // Validate the accounting year
        fieldValues.clear();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, salaryExpenseTransferAccountingLine.getPayrollEndDateFiscalYear());
        AccountingPeriod accountingPeriod = new AccountingPeriod();        
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(KFSPropertyConstants.ACCOUNT,KFSKeyConstants.Labor.INVALID_PAY_YEAR, salaryExpenseTransferAccountingLine.getPayrollEndDateFiscalYear().toString());
            return false;
        }
       
        // Validate the accounting period code
        fieldValues.clear();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, salaryExpenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());
        accountingPeriod = new AccountingPeriod();        
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(KFSPropertyConstants.ACCOUNT,KFSKeyConstants.Labor.INVALID_PAY_PERIOD_CODE, salaryExpenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());
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
