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

import static org.kuali.kfs.bo.AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;
import org.kuali.module.labor.util.ObjectUtil;

/**
 * Business rule(s) applicable to Labor Expense Transfer documents.
 */
public class LaborExpenseTransferDocumentRules extends AccountingDocumentRuleBase implements GenerateLaborLedgerPendingEntriesRule<LaborLedgerPostingDocument> {

    /**
     * Constructor
     */
    public LaborExpenseTransferDocumentRules() {
        super();
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(accountingDocument, updatedAccountingLine);
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);

        if (!isValid) {
            return false;
        }

        // validate the accounting year
        if (!isValidPayFiscalYear(accountingLine)) {
            reportError(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, KFSKeyConstants.Labor.INVALID_PAY_YEAR);
            return false;
        }

        // validate the accounting period code
        if (!isValidPayFiscalPeriod(accountingLine)) {
            reportError(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, KFSKeyConstants.Labor.INVALID_PAY_PERIOD_CODE);
            return false;
        }

        // not allow the duplicate source accounting line in the document
        if (isDuplicateSourceAccountingLine(accountingDocument, accountingLine)) {
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_DUPLICATE_SOURCE_ACCOUNTING_LINE);
            return false;
        }

        // determine if an expired account can be used to accept amount transfer
        boolean canExpiredAccountBeUsed = canExpiredAccountBeUsed(accountingLine);
        if (!canExpiredAccountBeUsed) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        return true;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        LOG.info("started processCustomRouteDocumentBusinessRules");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) document;
        List sourceLines = expenseTransferDocument.getSourceAccountingLines();
        List targetLines = expenseTransferDocument.getTargetAccountingLines();

        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        // check to ensure totals of accounting lines in source and target sections match
        isValid = isValid & isAccountingLineTotalsMatch(sourceLines, targetLines);

        // check to ensure totals of accounting lines in source and target sections match by pay FY + pay period
        isValid = isValid & isAccountingLineTotalsMatchByPayFYAndPayPeriod(sourceLines, targetLines);

        // target accouting lines must have the same amounts as source accounting lines for each object code
        if (isValid) {
            boolean isValidAmountTransferredByObjectCode = isValidAmountTransferredByObjectCode(expenseTransferDocument);
            if (!isValidAmountTransferredByObjectCode) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT);
                isValid = false;
            }
        }

        // benefit transfers cannot be made between two different fringe benefit labor object codes.
        if (isValid) {
            boolean hasSameFringeBenefitObjectCodes = hasSameFringeBenefitObjectCodes(expenseTransferDocument);
            if (!hasSameFringeBenefitObjectCodes) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, KFSKeyConstants.Labor.DISTINCT_OBJECT_CODE_ERROR);
                isValid = false;
            }
        }

        // allow a negative amount to be moved from one account to another but do not allow a negative amount to be created when the
        // balance is positive
        Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap = this.getAccountingLineGroupMap(sourceLines, ExpenseTransferSourceAccountingLine.class);
        if (isValid) {
            boolean canNegtiveAmountBeTransferred = canNegtiveAmountBeTransferred(accountingLineGroupMap);
            if (!canNegtiveAmountBeTransferred) {
                reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_CANNOT_TRANSFER_NEGATIVE_AMOUNT);
                isValid = false;
            }
        }

        // determine if an expired account can be used to accept amount transfer
        if (isValid) {
            boolean canExpiredAccountBeUsed = canExpiredAccountBeUsed(expenseTransferDocument);
            if (!canExpiredAccountBeUsed) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
                isValid = false;
            }

            // verify if the accounts in target accounting lines accept fringe benefits
            if (!this.isAccountsAcceptFringeBenefit(expenseTransferDocument)) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_ACCOUNT_NOT_ACCEPT_FRINGES);
                return false;
            }
        }

        // only allow a transfer of benefit dollars up to the amount that already exist in labor ledger detail for a given pay
        // period
        if (isValid) {
            boolean isValidTransferAmount = isValidTransferAmount(accountingLineGroupMap);
            if (!isValidTransferAmount) {
                reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ERROR_TRANSFER_AMOUNT_EXCEED_MAXIMUM);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Determine whether the accounts in source/target accounting lines are valid
     * 
     * @param accountingDocument the given accounting document
     * @return true if the accounts in source/target accounting lines are valid; otherwise, false
     */
    private boolean isValidAccount(AccountingDocument accountingDocument) {
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        for (Object sourceAccountingLine : expenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            if (line.getAccount() == null) {
                reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { line.getChartOfAccountsCode(), line.getAccountNumber() });
                return false;
            }
        }

        for (Object targetAccountingLine : expenseTransferDocument.getTargetAccountingLines()) {
            AccountingLine line = (AccountingLine) targetAccountingLine;
            if (line.getAccount() == null) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { line.getChartOfAccountsCode(), line.getAccountNumber() });
                return false;
            }
        }
        return true;
    }

    /**
     * Performs validation on emplid
     * 
     * @param emplid - id to validate
     * @return boolean - true if id is valid, false if invalid
     */
    public boolean isValidEmplid(String emplid) {
        boolean isValid = true;

        // verify id was given
        if (StringUtils.isBlank(emplid)) {
            reportError(KFSConstants.EMPLOYEE_LOOKUP_ERRORS, KFSKeyConstants.Labor.MISSING_EMPLOYEE_ID, emplid);
            isValid = false;
        }

        return isValid;
    }

    /**
     * This method checks if the total sum amount of the source accounting line matches the total sum amount of the target
     * accounting line, return true if the totals match, false otherwise.
     * 
     * @param sourceLines
     * @param targetLines
     * @return
     */
    public boolean isAccountingLineTotalsMatch(List sourceLines, List targetLines) {
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
     * This method calls other methods to check if all source and target accounting lines match between each set by pay fiscal year
     * and pay period, returning true if the totals match, false otherwise.
     * 
     * @param sourceLines
     * @param targetLines
     * @return
     */
    protected boolean isAccountingLineTotalsMatchByPayFYAndPayPeriod(List sourceLines, List targetLines) {
        boolean isValid = true;

        // sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        Map sourceLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(sourceLines);

        // sum source lines by pay fy and pay period, store in map by key PayFY+PayPeriod
        Map targetLinesMap = sumAccountingLineAmountsByPayFYAndPayPeriod(targetLines);

        // if totals don't match by PayFY+PayPeriod categories, then add error message
        if (compareAccountingLineTotalsByPayFYAndPayPeriod(sourceLinesMap, targetLinesMap) == false) {
            isValid = false;
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.Labor.ACCOUNTING_LINE_TOTALS_BY_PAYFY_PAYPERIOD_MISMATCH_ERROR);
        }

        return isValid;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isAmountValid(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        LOG.debug("started isAmountValid");

        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero amount
        if (amount.isZero()) {
            reportError(KFSPropertyConstants.AMOUNT, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }
        return true;
    }

    /**
     * determine whether the pay fiscal year of the given accounting line is valid
     * 
     * @param accountingLine the given accouting line
     * @return true if the pay fiscal year of the given accounting line is valid; otherwise, false
     */
    protected boolean isValidPayFiscalYear(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        Integer payrollFiscalYear = expenseTransferAccountingLine.getPayrollEndDateFiscalYear();
        if (payrollFiscalYear == null) {
            return false;
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, payrollFiscalYear.toString());

        return SpringContext.getBean(BusinessObjectService.class).countMatching(AccountingPeriod.class, fieldValues) > 0;
    }

    /**
     * determine whether the period code of the given accounting line is valid
     * 
     * @param accountingLine the given accouting line
     * @return true if the period code of the given accounting line is valid; otherwise, false
     */
    protected boolean isValidPayFiscalPeriod(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        Integer payrollFiscalYear = expenseTransferAccountingLine.getPayrollEndDateFiscalYear();
        if (payrollFiscalYear == null) {
            return false;
        }

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, payrollFiscalYear.toString());
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, expenseTransferAccountingLine.getPayrollEndDateFiscalPeriodCode());

        return SpringContext.getBean(BusinessObjectService.class).countMatching(AccountingPeriod.class, fieldValues) > 0;
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
            boolean isExisting = ObjectUtil.compareObject(accountingLine, sourceAccountingLine, key);
            counter = isExisting ? counter + 1 : counter;

            if (counter > 1) {
                return true;
            }
        }
        return false;
    }

    /**
     * determine whether the amount to be tranferred is only up to the amount in ledger balance for a given pay period
     * 
     * @param accountingDocument the given accounting document
     * @return true if the amount to be tranferred is only up to the amount in ledger balance for a given pay period; otherwise,
     *         false
     */
    protected boolean isValidTransferAmount(Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap) {
        Set<Entry<String, ExpenseTransferAccountingLine>> entrySet = accountingLineGroupMap.entrySet();
        
        for(Entry<String, ExpenseTransferAccountingLine> entry : entrySet){
            ExpenseTransferAccountingLine accountingLine = entry.getValue();
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
     * Determine whether target accouting lines have the same amounts as source accounting lines for each object code
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same amounts as source accounting lines for each object code; otherwise,
     *         false
     */
    protected boolean isValidAmountTransferredByObjectCode(AccountingDocument accountingDocument) {
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        Map<String, KualiDecimal> amountsFromSourceLine = summerizeByObjectCode(expenseTransferDocument.getSourceAccountingLines());
        Map<String, KualiDecimal> amountsFromTargetLine = summerizeByObjectCode(expenseTransferDocument.getTargetAccountingLines());

        if (amountsFromSourceLine.size() != amountsFromTargetLine.size()) {
            return false;
        }

        for (String objectCode : amountsFromSourceLine.keySet()) {
            if (!amountsFromTargetLine.containsKey(objectCode)) {
                return false;
            }

            KualiDecimal sourceAmount = amountsFromSourceLine.get(objectCode);
            KualiDecimal targetAmount = amountsFromTargetLine.get(objectCode);
            if (!sourceAmount.equals(targetAmount)) {
                return false;
            }
        }
        return true;
    }

    /**
     * summerize the amounts of accounting lines by object codes
     * 
     * @param accountingLines the given accounting line list
     * @return the summerized amounts by object codes
     */
    private Map<String, KualiDecimal> summerizeByObjectCode(List accountingLines) {
        Map<String, KualiDecimal> amountByObjectCode = new HashMap<String, KualiDecimal>();

        for (Object accountingLine : accountingLines) {
            AccountingLine line = (AccountingLine) accountingLine;
            String objectCode = line.getFinancialObjectCode();
            KualiDecimal amount = line.getAmount();

            if (amountByObjectCode.containsKey(objectCode)) {
                amount = amount.add(amountByObjectCode.get(objectCode));
            }
            amountByObjectCode.put(objectCode, amount);
        }
        return amountByObjectCode;
    }

    /**
     * get the amount for a given period from a ledger balance that has the given values for specified fileds
     * 
     * @param fieldValues the given fields and their values
     * @param periodCode the given period
     * @return the amount for a given period from the qualified ledger balance
     */
    protected KualiDecimal getBalanceAmount(Map<String, Object> fieldValues, String periodCode) {
        if (periodCode == null) {
            return KualiDecimal.ZERO;
        }

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_ACTUAL);
        KualiDecimal actualBalanceAmount = this.getBalanceAmountOfGivenPeriod(fieldValues, periodCode);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSConstants.BALANCE_TYPE_A21);
        KualiDecimal effortBalanceAmount = this.getBalanceAmountOfGivenPeriod(fieldValues, periodCode);

        return actualBalanceAmount.add(effortBalanceAmount);
    }

    // get the balance amount for the given period
    private KualiDecimal getBalanceAmountOfGivenPeriod(Map<String, Object> fieldValues, String periodCode) {
        KualiDecimal balanceAmount = KualiDecimal.ZERO;
        List<LedgerBalance> ledgerBalances = (List<LedgerBalance>) SpringContext.getBean(BusinessObjectService.class).findMatching(LedgerBalance.class, fieldValues);
        if (!ledgerBalances.isEmpty()) {
            balanceAmount = ledgerBalances.get(0).getAmount(periodCode);
        }
        return balanceAmount;
    }

    /**
     * determine whether a negtive amount can be transferred from one account to another
     * 
     * @param accountingDocument the given accounting document
     * @return true if a negtive amount can be transferred from one account to another; otherwise, false
     */
    protected boolean canNegtiveAmountBeTransferred(Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap) {
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

    /**
     * group the accounting lines by the specified key fields
     * 
     * @param accountingLines the given accounting lines that are stored in a list
     * @param clazz the class type of given accounting lines
     * @return the accounting line groups
     */
    protected Map<String, ExpenseTransferAccountingLine> getAccountingLineGroupMap(List<ExpenseTransferAccountingLine> accountingLines, Class clazz) {
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
     * determine whether the accounts in the target accounting lines accept fringe benefits.
     * 
     * @param accountingDocument the given accounting document
     * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
     */
    protected boolean isAccountsAcceptFringeBenefit(AccountingDocument accountingDocument) {
        LOG.debug("started isAccountsAcceptFringeBenefit");
        List<AccountingLine> accountingLines = accountingDocument.getTargetAccountingLines();

        for (AccountingLine accountingLine : accountingLines) {
            Account account = accountingLine.getAccount();
            if (account != null && !account.isAccountsFringesBnftIndicator()) {
                String overrideCode = accountingLine.getOverrideCode();
                boolean canNonFringeAccountUsed = NON_FRINGE_ACCOUNT_USED.equals(overrideCode);
                canNonFringeAccountUsed = canNonFringeAccountUsed || EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED.equals(overrideCode);

                if (!canNonFringeAccountUsed) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determine whether target accouting lines have the same fringe benefit object codes as source accounting lines
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same fringe benefit object codes as source accounting lines; otherwise, false
     */
    protected boolean hasSameFringeBenefitObjectCodes(AccountingDocument accountingDocument) {
        LOG.debug("started hasSameFringeBenefitObjectCodes(accountingDocument)");
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        Set<String> objectCodesFromSourceLine = new HashSet<String>();
        for (Object sourceAccountingLine : expenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            objectCodesFromSourceLine.add(line.getFinancialObjectCode());
        }

        Set<String> objectCodesFromTargetLine = new HashSet<String>();
        for (Object targetAccountingLine : expenseTransferDocument.getTargetAccountingLines()) {
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
    protected boolean hasSameFringeBenefitObjectCodes(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        LOG.debug("started hasSameFringeBenefitObjectCodes(accountingDocument, accountingLine)");
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        List<String> objectCodesFromSourceLine = new ArrayList<String>();
        for (Object sourceAccountingLine : expenseTransferDocument.getSourceAccountingLines()) {
            AccountingLine line = (AccountingLine) sourceAccountingLine;
            objectCodesFromSourceLine.add(line.getFinancialObjectCode());
        }

        return objectCodesFromSourceLine.contains(accountingLine.getFinancialObjectCode());
    }

    /**
     * get the default key of ExpenseTransferAccountingLine
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

        defaultKey.add(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR);
        defaultKey.add(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);

        return defaultKey;
    }

    /**
     * This method returns a String that is a concatenation of pay fiscal year and pay period code.
     * 
     * @param payFiscalYear
     * @param payPeriodCode
     * @return
     */
    private String createPayFYPeriodKey(Integer payFiscalYear, String payPeriodCode) {

        StringBuffer payFYPeriodKey = new StringBuffer();

        payFYPeriodKey.append(payFiscalYear);
        payFYPeriodKey.append(payPeriodCode);

        return payFYPeriodKey.toString();
    }

    /**
     * This method sums the totals of each accounting line, making an entry in a map for each unique pay fiscal year and pay period.
     * 
     * @param accountingLines
     * @return
     */
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

    /**
     * This method checks that the total amount of labor ledger accounting lines in the document's FROM section is equal to the
     * total amount on the labor ledger accounting lines TO section for each unique combination of pay fiscal year and pay period. A
     * value of true is returned if all amounts for each unique combination between source and target accounting lines match, false
     * otherwise.
     * 
     * @param sourceLinesMap
     * @param targetLinesMap
     * @return
     */
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

                // return false if the matching key values do not total each other
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

                // return false if the matching key values do not total each other
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

    /**
     * Overriding hook into generate general ledger pending entries, so no GL pending entries are created.
     * 
     * @see org.kuali.core.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.AccountingDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateGeneralLedgerPendingEntries(AccountingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * This method is the starting point for creating labor ledger pending entries. The logic used to create the LLPEs resides in
     * this method.
     * 
     * @param accountingDocument is an instance of <code>{@link LaborLedgerPostingDocument}</code>
     * @param accountingLine
     * @param sequenceHelper
     * @return
     */
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * This is responsible for properly negating the sign on an accounting line's amount when its associated document is an error
     * correction.
     * 
     * @param accountingDocument
     * @param accountingLine
     */
    private final void handleDocumentErrorCorrection(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine) {
        // If the document corrects another document, make sure the accounting line has the correct sign.
        if ((null == accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isNegative()) || (null != accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() && accountingLine.getAmount().isPositive())) {
            accountingLine.setAmount(accountingLine.getAmount().multiply(new KualiDecimal(1)));
        }
    }

    /**
     * @see org.kuali.kfs.rule.AccountingLineRule#isDebit(org.kuali.kfs.document.AccountingDocument, org.kuali.kfs.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isDebit = false;
        if (accountingLine.isSourceAccountingLine()) {
            isDebit = IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingDocument, accountingLine);
        }
        else if (accountingLine.isTargetAccountingLine()) {
            isDebit = !IsDebitUtils.isDebitConsideringNothingPositiveOnly(this, accountingDocument, accountingLine);
        }
        else {
            throw new IllegalStateException(IsDebitUtils.isInvalidLineTypeIllegalArgumentExceptionMessage);
        }

        return isDebit;
    }

    /**
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#isCredit(org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.document.AccountingDocument)
     */
    @Override
    public boolean isCredit(AccountingLine accountingLine, AccountingDocument accountingDocument) {
        return false;
    }

    /**
     * determine whether the expired accounts in the target accounting lines can be used.
     * 
     * @param accountingDocument the given accounting document
     * @return true if the expired accounts in the target accounting lines can be used; otherwise, false
     */
    protected boolean canExpiredAccountBeUsed(AccountingDocument accountingDocument) {
        LOG.info("started canExpiredAccountBeUsed(accountingDocument)");
        List<AccountingLine> accountingLines = accountingDocument.getTargetAccountingLines();

        for (AccountingLine accountingLine : accountingLines) {
            boolean canExpiredAccountBeUsed = this.canExpiredAccountBeUsed(accountingLine);

            if (!canExpiredAccountBeUsed) {
                return false;
            }
        }
        return true;
    }

    /**
     * determine whether the expired account in the target accounting line can be used.
     * 
     * @param accountingDocument the given accounting line
     * @return true if the expired account in the target accounting line can be used; otherwise, false
     */
    protected boolean canExpiredAccountBeUsed(AccountingLine accountingLine) {
        LOG.debug("started canExpiredAccountBeUsed(accountingLine)");

        Account account = accountingLine.getAccount();
        if (account != null && account.isExpired()) {
            String overrideCode = accountingLine.getOverrideCode();
            boolean canExpiredAccountUsed = EXPIRED_ACCOUNT.equals(overrideCode);
            canExpiredAccountUsed = canExpiredAccountUsed || EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED.equals(overrideCode);

            if (!canExpiredAccountUsed) {
                return false;
            }
        }
        return true;
    }

    /**
     * build the field-value maps throught the given accouting line
     * 
     * @param accountingLine the given accounting line
     * @return the field-value maps built from the given accouting line
     */
    protected Map<String, Object> buildFieldValueMap(ExpenseTransferAccountingLine accountingLine) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();

        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, accountingLine.getPostingYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountingLine.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountingLine.getAccountNumber());

        String subAccountNumber = accountingLine.getSubAccountNumber();
        subAccountNumber = StringUtils.isBlank(subAccountNumber) ? KFSConstants.getDashSubAccountNumber() : subAccountNumber;
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);

        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, accountingLine.getBalanceTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingLine.getFinancialObjectCode());

        Options options = SpringContext.getBean(OptionsService.class).getOptions(accountingLine.getPostingYear());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, options.getFinObjTypeExpenditureexpCd());

        String subObjectCode = accountingLine.getFinancialSubObjectCode();
        subObjectCode = StringUtils.isBlank(subObjectCode) ? KFSConstants.getDashFinancialSubObjectCode() : subObjectCode;
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjectCode);

        fieldValues.put(KFSPropertyConstants.EMPLID, accountingLine.getEmplid());
        fieldValues.put(KFSPropertyConstants.POSITION_NUMBER, accountingLine.getPositionNumber());

        return fieldValues;
    }

    /**
     * This method is used to verify if the select labor object code is active.
     * 
     * @param accountingDocument
     * @return
     */
    protected boolean isActiveLaborObjectCode(AccountingDocument accountingDocument) {
        LOG.debug("started -- isActiveLaborObjectCode");
        LOG.debug("finished -- isActiveLaborObjectCode");
        return true;
    }

    /**
     * util class that contains common algorithms for determining debit amounts
     */
    protected static class IsDebitUtils {
        protected static final String isDebitCalculationIllegalStateExceptionMessage = "an invalid debit/credit check state was detected";
        protected static final String isErrorCorrectionIllegalStateExceptionMessage = "invalid (error correction) document not allowed";
        protected static final String isInvalidLineTypeIllegalArgumentExceptionMessage = "invalid accounting line type";

        /**
         * @param debitCreditCode
         * @return true if debitCreditCode equals the the debit constant
         */
        static boolean isDebitCode(String debitCreditCode) {
            return StringUtils.equals(KFSConstants.GL_DEBIT_CODE, debitCreditCode);
        }

        /**
         * <ol>
         * <li>object type is included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> <code>(isIncome || isLiability) && (lineAmount > 0)</code>
         * <li> <code>(isExpense || isAsset) && (lineAmount < 0)</code>
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> <code>(isIncome || isLiability) && (lineAmount < 0)</code>
         * <li> <code>(isExpense || isAsset) && (lineAmount > 0)</code>
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> <code>document isErrorCorrection</code>
         * <li> <code>lineAmount == 0</code>
         * <li> <code>! (isIncome || isLiability || isExpense || isAsset)</code>
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         * @throws IllegalStateException if <code>document isErrorCorrection</code> or <code>lineAmount == 0</code> or
         *         <code>! (isIncome || isLiability || isExpense || isAsset)</code>
         */
        static boolean isDebitConsideringType(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();

            // income/liability
            if (rule.isIncomeOrLiability(accountingLine)) {
                isDebit = !isPositiveAmount;
            }
            // expense/asset
            else {
                if (rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>object type is not included in determining if a line is debit or credit.
         * <li>accounting line section (source/target) is not included in determining if a line is debit or credit.
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> none
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> <code>(isIncome || isLiability || isExpense || isAsset) && (lineAmount > 0)</code>
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> <code>lineAmount <= 0</code>
         * <li> <code>! (isIncome || isLiability || isExpense || isAsset)</code>
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         * @throws IllegalStateException if <code>lineAmount <= 0</code> or
         *         <code>! (isIncome || isLiability || isExpense || isAsset)</code>
         */
        static boolean isDebitConsideringNothingPositiveOnly(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            // non error correction
            if (!isErrorCorrection(accountingDocument)) {
                boolean isPositiveAmount = amount.isPositive();
                // isDebit if income/liability/expense/asset and line amount is positive
                if (isPositiveAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                    isDebit = true;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // error correction
            else {
                boolean isNegativeAmount = amount.isNegative();
                // isDebit if income/liability/expense/asset and line amount is negative
                if (isNegativeAmount && (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine))) {
                    isDebit = false;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }

            }
            return isDebit;
        }

        /**
         * <ol>
         * <li>accounting line section (source/target) type is included in determining if a line is debit or credit.
         * <li> zero line amounts are never allowed
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> <code>isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)</code>
         * <li> <code>isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)</code>
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> <code>isSourceLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount < 0)</code>
         * <li> <code>isTargetLine && (isIncome || isExpense || isAsset || isLiability) && (lineAmount > 0)</code>
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> <code>lineAmount == 0</code>
         * <li> <code>! (isIncome || isLiability || isExpense || isAsset)</code>
         * </ol>
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         * @throws IllegalStateException if <code>lineAmount == 0</code> or
         *         <code>! (isIncome || isLiability || isExpense || isAsset)</code>
         */
        static boolean isDebitConsideringSection(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            KualiDecimal amount = accountingLine.getAmount();
            // zero amounts are not allowed
            if (amount.isZero()) {
                throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
            }
            boolean isDebit = false;
            boolean isPositiveAmount = accountingLine.getAmount().isPositive();
            // source line
            if (accountingLine.isSourceAccountingLine()) {
                // income/liability/expense/asset
                if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                    isDebit = !isPositiveAmount;
                }
                else {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
            }
            // target line
            else {
                if (accountingLine.isTargetAccountingLine()) {
                    if (rule.isIncomeOrLiability(accountingLine) || rule.isExpenseOrAsset(accountingLine)) {
                        isDebit = isPositiveAmount;
                    }
                    else {
                        throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                    }
                }
                else {
                    throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                }
            }
            return isDebit;
        }

        /**
         * accounting line section (source/target) and object type is included in determining if a line is debit or credit. negative
         * line amounts are <b>Only</b> allowed during error correction
         * 
         * @param rule
         * @param accountingDocument
         * @param accountingLine
         * @return boolean
         */
        static boolean isDebitConsideringSectionAndTypePositiveOnly(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument, AccountingLine accountingLine) {

            boolean isDebit = false;
            KualiDecimal amount = accountingLine.getAmount();
            // non error correction
            if (!isErrorCorrection(accountingDocument)) {
                boolean isPositiveAmount = amount.isPositive();
                // only allow amount >0
                if (!isPositiveAmount) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
                // source line
                if (accountingLine.isSourceAccountingLine()) {
                    isDebit = rule.isIncomeOrLiability(accountingLine);
                }
                // target line
                else {
                    if (accountingLine.isTargetAccountingLine()) {
                        isDebit = rule.isExpenseOrAsset(accountingLine);
                    }
                    else {
                        throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                    }
                }
            }
            // error correction document
            else {
                boolean isNegativeAmount = amount.isNegative();
                if (!isNegativeAmount) {
                    throw new IllegalStateException(isDebitCalculationIllegalStateExceptionMessage);
                }
                // source line
                if (accountingLine.isSourceAccountingLine()) {
                    isDebit = rule.isExpenseOrAsset(accountingLine);
                }
                // target line
                else {
                    if (accountingLine.isTargetAccountingLine()) {
                        isDebit = rule.isIncomeOrLiability(accountingLine);
                    }
                    else {
                        throw new IllegalArgumentException(isInvalidLineTypeIllegalArgumentExceptionMessage);
                    }
                }
            }

            return isDebit;
        }

        /**
         * throws an <code>IllegalStateException</code> if the document is an error correction. otherwise does nothing
         * 
         * @param rule
         * @param accountingDocument
         */
        static void disallowErrorCorrectionDocumentCheck(AccountingDocumentRuleBase rule, AccountingDocument accountingDocument) {
            if (isErrorCorrection(accountingDocument)) {
                throw new IllegalStateException(isErrorCorrectionIllegalStateExceptionMessage);
            }
        }

        /**
         * Convience method for determine if a document is an error correction document.
         * 
         * @param accountingDocument
         * @return true if document is an error correct
         */
        static boolean isErrorCorrection(AccountingDocument accountingDocument) {
            boolean isErrorCorrection = false;

            String correctsDocumentId = accountingDocument.getDocumentHeader().getFinancialDocumentInErrorNumber();
            if (StringUtils.isNotBlank(correctsDocumentId)) {
                isErrorCorrection = true;
            }

            return isErrorCorrection;
        }
    }
}