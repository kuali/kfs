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

import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.ZERO;
import static org.kuali.kfs.KFSKeyConstants.ERROR_INVALID_NEGATIVE_AMOUNT_NON_CORRECTION;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferTargetAccountingLine;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.BenefitExpenseTransferDocument;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase;

/**
 * Business rule(s) applicable to Benefit Expense Transfer documents.
 */
public class BenefitExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BenefitExpenseTransferDocumentRule.class);
    
    private BusinessObjectService businessObjectService = SpringServiceLocator.getBusinessObjectService();

    /**
     * Constructs a BenefitExpenseTransferDocumentRule.java.
     */
    public BenefitExpenseTransferDocumentRule() {
        super();
    }

    /**
     * @see org.kuali.core.rules.SaveDocumentRule#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        return true;
    }

    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.info("started processCustomAddAccountingLineBusinessRules");

        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        if (!this.hasSameFringeBenefitObjectCodes(accountingDocument, accountingLine)) {
            reportError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.Labor.DISTINCT_OBJECT_CODE_ERROR);
            return false;
        }

        // only fringe benefit labor object codes are allowed on the befefit expense transfer document
        if (!this.isFringeBenefitObjectCode(accountingLine)) {
            reportError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.Labor.INVALID_FRINGE_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }

        // validate the accounting year
        if (!this.isValidPayFiscalYear(accountingLine)) {
            reportError(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, KFSKeyConstants.Labor.INVALID_PAY_YEAR);
            return false;
        }

        // validate the accounting period code
        if (!this.isValidPayFiscalPeriod(accountingLine)) {
            reportError(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, KFSKeyConstants.Labor.INVALID_PAY_PERIOD_CODE);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("started processCustomRouteDocumentBusinessRules");

        BenefitExpenseTransferDocument benefitExpenseTransferDocument = (BenefitExpenseTransferDocument) document;
        List sourceLines = benefitExpenseTransferDocument.getSourceAccountingLines();
        List targetLines = benefitExpenseTransferDocument.getTargetAccountingLines();

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        // check to ensure totals of accounting lines in source and target sections match
        isValid = isValid && isAccountingLineTotalsMatch(sourceLines, targetLines);

        // check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
        isValid = isValid && isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines);

        // verify if the accounts in target accounting lines accept fringe benefits
        if (!this.isAccountsAcceptFringeBenefit(benefitExpenseTransferDocument)) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.Labor.ERROR_ACCOUNT_NOT_ACCEPT_FRINGES);
            return false;
        }

        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        boolean hasSameFringeBenefitObjectCodes = this.hasSameFringeBenefitObjectCodes(benefitExpenseTransferDocument);
        if (!hasSameFringeBenefitObjectCodes) {
            reportError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.Labor.DISTINCT_OBJECT_CODE_ERROR);
            isValid = false;
        }

        // only allow a transfer of benefit dollars up to the amount that already exist in labor ledger detail for a given pay
        // period
        Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap = this.getAccountingLineGroupMap(sourceLines, ExpenseTransferSourceAccountingLine.class);
        boolean isValidTransferAmount = this.isValidTransferAmount(accountingLineGroupMap);
        if (!isValidTransferAmount) {
            reportError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.Labor.ERROR_TRANSFER_AMOUNT_EXCEED_MAXIMUM);
            isValid = false;
        }

        // allow a negative amount to be moved from one account to another but do not allow a negative amount to be created when the
        // balance is positive
        boolean canNegtiveAmountBeTransferred = canNegtiveAmountBeTransferred(accountingLineGroupMap);
        if (!canNegtiveAmountBeTransferred) {
            reportError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.Labor.ERROR_CANNOT_TRANSFER_NEGATIVE_AMOUNT);
            isValid = false;
        }

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

        // Generate original entry
        PendingLedgerEntry originalEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
        boolean success = processOriginalLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, originalEntry);

        // Generate A21 entry
        PendingLedgerEntry a21Entry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
        success &= processA21LaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, a21Entry);

        // Generate A21 reversal entry
        PendingLedgerEntry a21RevEntry = (PendingLedgerEntry) ObjectUtils.deepCopy(defaultEntry);
        success &= processA21RevLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, a21RevEntry);

        return success;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        LOG.debug("started isAmountValid");

        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero amount, or negative on original (non-correction) document; no sign check for documents that are
        // corrections to previous documents
        if (amount.isZero()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.AMOUNT, ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }
        return true;
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

    /**
     * determine whether the amount to be tranferred is only up to the amount in ledger balance for a given pay period
     * 
     * @param accountingDocument the given accounting document
     * @return true if the amount to be tranferred is only up to the amount in ledger balance for a given pay period; otherwise,
     *         false
     */
    private boolean isValidTransferAmount(Map<String, ? extends ExpenseTransferAccountingLine> accountingLineGroupMap) {
        for (String key : accountingLineGroupMap.keySet()) {
            ExpenseTransferAccountingLine accountingLine = accountingLineGroupMap.get(key);
            Map<String, Object> fieldValues = this.buildFieldValueMap(accountingLine);

            KualiDecimal balanceAmount = getBalanceAmount(fieldValues, accountingLine.getPayrollEndDateFiscalPeriodCode());
            KualiDecimal transferAmount = accountingLine.getAmount();

            // the tranferred amount cannot greater than the balance amount
            if (balanceAmount.abs().isLessThan(transferAmount.abs())) {
                return false;
            }
                       
            // a positive amount cannot be transferred if the balance amount is negative
            if (balanceAmount.isNegative() && transferAmount.isPositive()) {
                return false;
            }
        }
        return true;
    }

    /**
     * determine whether a negtive amount can be transferred from one account to another
     * 
     * @param accountingDocument the given accounting document
     * @return true if a negtive amount can be transferred from one account to another; otherwise, false
     */
    private boolean canNegtiveAmountBeTransferred(Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap) {
        for (String key : accountingLineGroupMap.keySet()) {
            ExpenseTransferAccountingLine accountingLine = accountingLineGroupMap.get(key);
            Map<String, Object> fieldValues = this.buildFieldValueMap(accountingLine);

            KualiDecimal balanceAmount = getBalanceAmount(fieldValues, accountingLine.getPayrollEndDateFiscalPeriodCode());
            KualiDecimal transferAmount = accountingLine.getAmount();

            // a negtive amount cannot be transferred if the balance amount is positive
            if (transferAmount.isNegative() && balanceAmount.isPositive()) {
                return false;
            }
        }
        return true;
    }
    
    private KualiDecimal getBalanceAmount(Map<String, Object> fieldValues, String periodCode){
        List<LedgerBalance> ledgerBalances = (List<LedgerBalance>) SpringServiceLocator.getBusinessObjectService().findMatching(LedgerBalance.class, fieldValues);
        
        if(!ledgerBalances.isEmpty() && periodCode != null){
            return ledgerBalances.get(0).getAmount(periodCode);
        }
        return KualiDecimal.ZERO;
    }

    private Map<String, ExpenseTransferAccountingLine> getAccountingLineGroupMap(List<ExpenseTransferAccountingLine> accountingLines, Class clazz) {
        Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap = new HashMap<String, ExpenseTransferAccountingLine>();

        for (ExpenseTransferAccountingLine accountingLine : accountingLines) {
            String stringKey = ObjectUtil.buildPropertyMap(accountingLine, defaultKeyOfExpenseTransferAccountingLine()).toString();
            ExpenseTransferAccountingLine line = null;

            if (accountingLineGroupMap.containsKey(stringKey)) {
                line = accountingLineGroupMap.get(stringKey);
                KualiDecimal amount = line.getAmount();
                line.setAmount(amount.add(accountingLine.getAmount()));
            }
            else {
                try {
                    line = (ExpenseTransferAccountingLine) clazz.newInstance();
                    ObjectUtil.buildObject(line, accountingLine);
                    accountingLineGroupMap.put(stringKey, line);
                }
                catch (Exception e) {
                    LOG.error("Cannot create a new instance of ExpenseTransferAccountingLine" + e);
                }
            }
        }
        return accountingLineGroupMap;
    }

    /**
     * get the default key of ExpenseTransferAccountingLine
     * 
     * @return the default key of ExpenseTransferAccountingLine
     */
    private List<String> defaultKeyOfExpenseTransferAccountingLine() {
        List<String> defaultKey = new ArrayList<String>();

        defaultKey.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        defaultKey.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        defaultKey.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        defaultKey.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);

        defaultKey.add(KFSPropertyConstants.BALANCE_TYPE_CODE);
        defaultKey.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        defaultKey.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        defaultKey.add(KFSPropertyConstants.EMPLID);
        defaultKey.add(KFSPropertyConstants.POSITION_NUMBER);

        defaultKey.add(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);

        return defaultKey;
    }

    private Map<String, Object> buildFieldValueMap(ExpenseTransferAccountingLine accountingLine) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();

        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, accountingLine.getPostingYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountingLine.getAccountNumber());
        
        String subAccountNumber = accountingLine.getSubAccountNumber();
        subAccountNumber = StringUtils.isBlank(subAccountNumber) ? KFSConstants.DASHES_SUB_ACCOUNT_NUMBER : subAccountNumber;
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, accountingLine.getBalanceTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingLine.getFinancialObjectCode());
        
        String subObjectCode = accountingLine.getFinancialSubObjectCode();
        subObjectCode = StringUtils.isBlank(subObjectCode) ? KFSConstants.DASHES_SUB_OBJECT_CODE : subObjectCode;
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjectCode);

        fieldValues.put(KFSPropertyConstants.EMPLID, accountingLine.getEmplid());
        fieldValues.put(KFSPropertyConstants.POSITION_NUMBER, accountingLine.getPositionNumber());

        return fieldValues;
    }
}