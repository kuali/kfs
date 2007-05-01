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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.BenefitExpenseTransferDocument;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 */
public class BenefitExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules {

    public BenefitExpenseTransferDocumentRule() {
    }

    protected boolean AddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);
    }

    /**
     * The following criteria will be validated here:
     * Account must be valid. Object code must be valid. Object code must be a labor object code.
     * The object code must exist in the ld_labor_obj_t table. 
     * The field finobj_frngslry_cd for the object code in the ld_labor_obj_t table must have a value of "F". 
     * Sub-account, if specified, must be valid for account. Sub-object, if
     * specified, must be valid for account and object code. Position must be valid for fiscal year. FIS enforces this by a direct
     * lookup of the PeopleSoft HRMS position data table. Kuali cannot do this. (See issue 12.) Amount must not be zero.
     * -------------------------------------------------------------------------------------------------------- 
     ! Only fringe benefit labor object codes are allowed on this document. 
     ! The document must have at least one “FROM” segment and one “TO” segment. 
     ! The total amount on the “FROM” side must equal the total amount on the “TO” side. 
     Testing - Transfers cannot be made between two different fringe benefit labor object codes. 
     ??  Only the “Account” and “Amount” fields may be edited in the “TO” zone. 
     * The Justification field is required and should include as much pertinent detail as possible. 
     * The Fiscal Year field on this eDoc is used differently as compared to other TP documents. In the Benefit Transfer document, this field is
     * used to load the appropriate data onto the Labor Ledger Balance screen. Pending Ledger Entries are created immediately as
     * part of the routing process. In addition to creating pending entries with a balance type of “AC” the Benefit Transfer
     * document requires that a pending entry be created with a balance type of “A2”. Only allow a transfer of benefit dollars up to
     * the amount that already exist in the labor ledger detail for a given pay period. Check must exist that verifies the “TO”
     * account accepts fringes. If no benefits allowed provide error message. Allow an override flag to allow the fringe to be
     * transferred to the account. Allow a negative amount to be moved from one account to another but do not allow a negative
     * amount to be created when the balance is positive.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     * @param TransactionalDocument
     * @param AccountingLine
     * @return
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {

        // Transfers cannot be made between two different fringe benefit labor object codes.
        AccountingDocument benefitDoc = (AccountingDocument) accountingDocument;
        List<AccountingLine> accountingLines = new ArrayList();
        Collection<PositionObjectBenefit> positionObjectBenefits;

        accountingLines.addAll(benefitDoc.getSourceAccountingLines());
        accountingLines.addAll(benefitDoc.getTargetAccountingLines());

        for (AccountingLine lines : accountingLines) {
            if ((accountingLine.getFinancialObjectCode() != null) && (lines.getFinancialObjectCode() != null)) {

                if (!accountingLine.getFinancialObjectCode().equals(lines.getFinancialObjectCode())) {
                    reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.DISTINCT_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
                    return false;
                }
            }
        }

        // Retrieve the labor object code to make sure it is fringe.
        // It must have a value of "F".

        ErrorMap errorMap = GlobalVariables.getErrorMap();
        Map fieldValues = new HashMap();
        fieldValues.put("financialObjectCode", accountingLine.getFinancialObjectCode().toString());
        ArrayList laborObjects = (ArrayList) SpringServiceLocator.getBusinessObjectService().findMatching(LaborObject.class, fieldValues);

        // Check if the object code is labor related
        if (laborObjects.size() == 0) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.LABOR_OBJECT_MISSING_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }

        // Check for fringe status
        LaborObject laborObject = (LaborObject) laborObjects.get(0);
        String FringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();

        if (!FringeOrSalaryCode.equals(LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }

        ExpenseTransferAccountingLine benefitExpenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        // Validate the accounting year
        fieldValues.clear();
        fieldValues.put("universityFiscalYear", benefitExpenseTransferAccountingLine.getPayrollEndDateFiscalYear());
        AccountingPeriod accountingPeriod = new AccountingPeriod();
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_PAY_YEAR);
            return false;
        }

        // Validate the accounting period code
        fieldValues.clear();
        fieldValues.put("universityFiscalPeriodCode", benefitExpenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());
        accountingPeriod = new AccountingPeriod();
        if (SpringServiceLocator.getBusinessObjectService().countMatching(AccountingPeriod.class, fieldValues) == 0) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_PAY_PERIOD_CODE);
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

        BenefitExpenseTransferDocument setDoc = (BenefitExpenseTransferDocument) document;

        List sourceLines = new ArrayList();
        List targetLines = new ArrayList();

        // set source and target accounting lines
        sourceLines.addAll(setDoc.getSourceAccountingLines());
        targetLines.addAll(setDoc.getTargetAccountingLines());

        // check to ensure totals of accounting lines in source and target sections match
        if (isValid) {
            isValid = isAccountingLineTotalsMatch(sourceLines, targetLines);
        }

        // check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
        if (isValid) {
            isValid = isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines);
        }

        return isValid;
    }

    /**
     * @param sourceLines
     * @param targetLines
     * @return
     */
    private boolean isAccountingLineTotalsMatch(List sourceLines, List targetLines) {

        boolean isValid = true;

        AccountingLine line = null;

        // totals for the from and to lines.
        KualiDecimal sourceLinesAmount = new KualiDecimal(0);
        KualiDecimal targetLinesAmount = new KualiDecimal(0);

        // sum source lines
        for (Iterator i = sourceLines.iterator(); i.hasNext();) {
            line = (ExpenseTransferAccountingLine) i.next();
            sourceLinesAmount = sourceLinesAmount.add(line.getAmount());
        }

        // sum target lines
        for (Iterator i = targetLines.iterator(); i.hasNext();) {
            line = (ExpenseTransferAccountingLine) i.next();
            targetLinesAmount = targetLinesAmount.add(line.getAmount());
        }

        // if totals don't match, then add error message
        if (sourceLinesAmount.compareTo(targetLinesAmount) != 0) {
            isValid = false;
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ACCOUNTING_LINE_TOTALS_MISMATCH_ERROR);
        }

        return isValid;
    }

    /**
     * @param sourceLines
     * @param targetLines
     * @return
     */
    private boolean isAccountingLineTotalsMatchByPayFYAndPayPeriod(List sourceLines, List targetLines) {

        boolean isValid = true;

        Map sourceLinesMap = new HashMap();
        Map targetLinesMap = new HashMap();

        // sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        sourceLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(sourceLines);

        // sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        targetLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(targetLines);

        // if totals don't match by PayFY+PayPeriod categories, then add error message
        if (compareAccountingLineTotalsByPayFYAndPayPeriod(sourceLinesMap, targetLinesMap) == false) {
            isValid = false;
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ACCOUNTING_LINE_TOTALS_BY_PAYFY_PAYPERIOD_MISMATCH_ERROR);
        }

        return isValid;
    }


    private boolean compareAccountingLineTotalsByPayFYAndPayPeriod(Map sourceLinesMap, Map targetLinesMap) {

        boolean isValid = true;
        Map.Entry entry = null;
        String currentKey = null;
        KualiDecimal sourceLinesAmount = new KualiDecimal(0);
        KualiDecimal targetLinesAmount = new KualiDecimal(0);


        // Loop through source lines comparing against target lines
        for (Iterator i = sourceLinesMap.entrySet().iterator(); i.hasNext() && isValid;) {
            // initialize
            entry = (Map.Entry) i.next();
            currentKey = (String) entry.getKey();
            sourceLinesAmount = (KualiDecimal) entry.getValue();

            if (targetLinesMap.containsKey(currentKey)) {
                targetLinesAmount = (KualiDecimal) targetLinesMap.get(currentKey);

                if (sourceLinesAmount.compareTo(targetLinesAmount) != 0) {
                    isValid = false;
                }

            }
            else {
                isValid = false;
            }
        }

        /*
         * Now loop through target lines comparing against source lines. This finds missing entries from either direction (source or
         * target)
         */
        for (Iterator i = targetLinesMap.entrySet().iterator(); i.hasNext() && isValid;) {
            // initialize
            entry = (Map.Entry) i.next();
            currentKey = (String) entry.getKey();
            targetLinesAmount = (KualiDecimal) entry.getValue();

            if (sourceLinesMap.containsKey(currentKey)) {
                sourceLinesAmount = (KualiDecimal) sourceLinesMap.get(currentKey);

                if (targetLinesAmount.compareTo(sourceLinesAmount) != 0) {
                    isValid = false;
                }

            }
            else {
                isValid = false;
            }
        }


        return isValid;
    }

    private Map sumAccountingLineAmountsByPayFYAndPayPeriod(List accountingLines) {

        ExpenseTransferAccountingLine line = null;
        KualiDecimal linesAmount = new KualiDecimal(0);
        Map linesMap = new HashMap();
        String payFYPeriodKey = null;

        // go through source lines adding amounts to appropriate place in map
        for (Iterator i = accountingLines.iterator(); i.hasNext();) {
            // initialize
            line = (ExpenseTransferAccountingLine) i.next();
            linesAmount = new KualiDecimal(0);

            // create hash key
            payFYPeriodKey = createPayFYPeriodKey(line.getPayrollEndDateFiscalYear(), line.getPayrollEndDateFiscalPeriodCode());

            // if entry exists, pull from hash
            if (linesMap.containsKey(payFYPeriodKey)) {
                linesAmount = (KualiDecimal) linesMap.get(payFYPeriodKey);
            }

            // update and store
            linesAmount = linesAmount.add(line.getAmount());
            linesMap.put(payFYPeriodKey, linesAmount);
        }

        return linesMap;
    }

    private String createPayFYPeriodKey(Integer payFiscalYear, String payPeriodCode) {

        StringBuffer payFYPeriodKey = new StringBuffer();

        payFYPeriodKey.append(payFiscalYear);
        payFYPeriodKey.append(payPeriodCode);

        return payFYPeriodKey.toString();
    }
}