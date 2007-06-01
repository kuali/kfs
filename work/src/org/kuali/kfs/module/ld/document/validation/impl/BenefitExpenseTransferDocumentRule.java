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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
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
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.BenefitExpenseTransferDocument;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 */
public class BenefitExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules {
    private BusinessObjectService businessObjectService = SpringServiceLocator.getBusinessObjectService();

    /**
     * Constructs a BenefitExpenseTransferDocumentRule.java.
     */
    public BenefitExpenseTransferDocumentRule() {
        super();
    }

    /**
     * @see org.kuali.module.labor.rules.LaborExpenseTransferDocumentRules#processCustomSaveDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        if (!this.hasSameFringeBenefitObjectCodes((AccountingDocument) document)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.DISTINCT_OBJECT_CODE_ERROR);
            return false;
        }
        return true;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        if (!this.hasSameFringeBenefitObjectCodes(accountingDocument, accountingLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.DISTINCT_OBJECT_CODE_ERROR);
            return false;
        }

        // only fringe benefit labor object codes are allowed on the befefit expense transfer document
        if (!this.isFringeBenefitObjectCode(accountingLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }

        // verify if the accounts in target accounting lines accept fringe benefits
        if (!this.isAccountsAcceptFringeBenefit(accountingDocument)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.ERROR_ACCOUNT_NOT_ACCEPT_FRINGES);
            return false;
        }

        // validate the accounting year
        if (!this.isValidPayFiscalYear(accountingLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_PAY_YEAR);
            return false;
        }

        // validate the accounting period code
        if (!this.isValidPayFiscalPeriod(accountingLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.INVALID_PAY_PERIOD_CODE);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        BenefitExpenseTransferDocument setDoc = (BenefitExpenseTransferDocument) document;
        List sourceLines = new ArrayList(setDoc.getSourceAccountingLines());
        List targetLines = new ArrayList(setDoc.getTargetAccountingLines());

        // check to ensure totals of accounting lines in source and target sections match
        isValid = isValid && isAccountingLineTotalsMatch(sourceLines, targetLines);

        // check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
        isValid = isValid && isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines);

        return isValid;
    }

    /**
     * @see org.kuali.module.labor.rules.LaborExpenseTransferDocumentRules#processGenerateLaborLedgerPendingEntries(org.kuali.module.labor.document.LaborLedgerPostingDocument,
     *      org.kuali.module.labor.bo.ExpenseTransferAccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started processGenerateLaborLedgerPendingEntries");
        
        // setup default values, so they don't have to be set multiple times
        PendingLedgerEntry defaultEntry = new PendingLedgerEntry();
        populateDefaultLaborLedgerPendingEntry(accountingDocument, accountingLine, defaultEntry);

        // Generate orig entry
        PendingLedgerEntry originalEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
        boolean success = processOriginalLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, originalEntry);

        System.out.println("completed processGenerateLaborLedgerPendingEntries: " + success + ":" + accountingDocument.getLaborLedgerPendingEntries().size());
        LOG.info("completed processGenerateLaborLedgerPendingEntries: " + success + ":" + accountingDocument.getLaborLedgerPendingEntries().size());

        return success;
    }

    /**
     * Determine whether target accouting lines have the same fringe benefit object codes as source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same fringe benefit object codes as source accounting lines; otherwise, false
     */
    private boolean hasSameFringeBenefitObjectCodes(AccountingDocument accountingDocument) {
        BenefitExpenseTransferDocument benefitExpenseTransferDocument = (BenefitExpenseTransferDocument) accountingDocument;

        Set<String> objectCodesFromSourceLine = new HashSet<String>();
        for (Object sourceAccountingLine : benefitExpenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            objectCodesFromSourceLine.add(line.getFinancialObjectCode());
        }

        Set<String> objectCodesFromTargetLine = new HashSet<String>();
        for (Object targetAccountingLine : benefitExpenseTransferDocument.getTargetAccountingLines()) {
            AccountingLine line = (AccountingLine) targetAccountingLine;
            objectCodesFromTargetLine.add(line.getFinancialObjectCode());
        }

        if (objectCodesFromSourceLine.size() != objectCodesFromTargetLine.size()) {
            return false;
        }
        return objectCodesFromSourceLine.containsAll(objectCodesFromTargetLine);
    }

    /**
     * Determine whether the object code of the given accouting line is one of fringe benefit objects of source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @param accountingLine the given accounting line
     * @return true if the object code of the given accouting line is one of fringe benefit objects of source accounting lines;
     *         otherwise, false
     */
    private boolean hasSameFringeBenefitObjectCodes(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        BenefitExpenseTransferDocument benefitExpenseTransferDocument = (BenefitExpenseTransferDocument) accountingDocument;

        List<String> objectCodesFromSourceLine = new ArrayList<String>();
        for (Object sourceAccountingLine : benefitExpenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            objectCodesFromSourceLine.add(line.getFinancialObjectCode());
        }

        return objectCodesFromSourceLine.contains(accountingLine.getFinancialObjectCode());
    }

    /**
     * Determine whether the object code of given accounting line is a fringe benefit labor object code
     * 
     * @param accountingLine the given accounting line
     * @return true if the object code of given accounting line is a fringe benefit labor object code; otherwise, false
     */
    private boolean isFringeBenefitObjectCode(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (laborObject == null) {
            return false;
        }

        String fringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();
        return StringUtils.equals(LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE, fringeOrSalaryCode);
    }

    /**
     * determine whether the pay fiscal year of the given accounting line is valid
     * 
     * @param accountingLine the given accouting line
     * @return true if the pay fiscal year of the given accounting line is valid; otherwise, false
     */
    private boolean isValidPayFiscalYear(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        Integer payrollFiscalYear = expenseTransferAccountingLine.getPayrollEndDateFiscalYear();
        if (payrollFiscalYear == null) {
            return false;
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, payrollFiscalYear.toString());

        return businessObjectService.countMatching(AccountingPeriod.class, fieldValues) > 0;
    }

    /**
     * determine whether the period code of the given accounting line is valid
     * 
     * @param accountingLine the given accouting line
     * @return true if the period code of the given accounting line is valid; otherwise, false
     */
    private boolean isValidPayFiscalPeriod(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        Integer payrollFiscalYear = expenseTransferAccountingLine.getPayrollEndDateFiscalYear();
        if (payrollFiscalYear == null) {
            return false;
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, payrollFiscalYear.toString());
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, expenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());

        return businessObjectService.countMatching(AccountingPeriod.class, fieldValues) > 0;
    }

    /**
     * determine whether the accounts in the target accounting lines accept fringe benefits.
     * 
     * @param accountingDocument the given accounting document
     * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
     */
    private boolean isAccountsAcceptFringeBenefit(AccountingDocument accountingDocument) {
        List<AccountingLine> accountingLines = accountingDocument.getTargetAccountingLines();

        for (AccountingLine accountingLine : accountingLines) {
            if (!accountingLine.getAccount().isAccountsFringesBnftIndicator()) {
                return false;
            }
        }
        return true;
    }
}