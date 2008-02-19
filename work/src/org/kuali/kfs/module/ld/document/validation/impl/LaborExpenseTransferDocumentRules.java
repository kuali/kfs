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
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborKeyConstants;
import org.kuali.module.labor.LaborPropertyConstants;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;
import org.kuali.kfs.util.ObjectUtil;

/**
 * Business rule(s) applicable to Labor Expense Transfer documents.
 */
public class LaborExpenseTransferDocumentRules extends AccountingDocumentRuleBase implements GenerateLaborLedgerPendingEntriesRule<LaborLedgerPostingDocument> {
    /**
     * Updates an accounting line
     * 
     * @param accountingDocument document to be processed
     * @param originalAccountingLine accounting line with old data
     * @param updatedAccountingLine accounting line with the new data
     * @return boolean
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine, org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine originalAccountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(accountingDocument, updatedAccountingLine);
    }

    /**
     * Adds an accounting line
     * 
     * @param accountingDocument document to be processed
     * @param originalAccountingLine accounting line with old data
     * @param updatedAccountingLine accounting line with the new data
     * @return boolean
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);

        if (!isValid) {
            return false;
        }

        // not allow the duplicate source accounting line in the document
        if (isDuplicateSourceAccountingLine(accountingDocument, accountingLine)) {
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_DUPLICATE_SOURCE_ACCOUNTING_LINE);
            return false;
        }

        // determine if an expired account can be used to accept amount transfer
        boolean canExpiredAccountBeUsed = canExpiredAccountBeUsed(accountingLine);
        if (!canExpiredAccountBeUsed) {
            reportError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
            return false;
        }

        // verify if the accounts in target accounting lines accept fringe benefits
        if (!isAccountAcceptFringeBenefit(accountingLine)) {
            reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_ACCEPT_FRINGES, accountingLine.getAccount().getReportsToChartOfAccountsCode(), accountingLine.getAccount().getReportsToAccountNumber());
            return false;
        }

        return true;
    }

    /**
     * Route a document
     * 
     * @param document the document to be routed
     * @return boolean
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

        // allow a negative amount to be moved from one account to another but do not allow a negative amount to be created when the
        // balance is positive
        Map<String, ExpenseTransferAccountingLine> accountingLineGroupMap = this.getAccountingLineGroupMap(sourceLines, ExpenseTransferSourceAccountingLine.class);
        if (isValid) {
            boolean canNegtiveAmountBeTransferred = canNegtiveAmountBeTransferred(accountingLineGroupMap);
            if (!canNegtiveAmountBeTransferred) {
                reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_CANNOT_TRANSFER_NEGATIVE_AMOUNT);
                isValid = false;
            }
        }

        // target accounting lines must have the same amounts as source accounting lines for each object code
        if (isValid) {
            isValid = isValidAmountTransferredByObjectCode(expenseTransferDocument);
        }

        // only allow a transfer of benefit dollars up to the amount that already exist in labor ledger detail for a given pay
        // period
        if (isValid) {
            boolean isValidTransferAmount = isValidTransferAmount(accountingLineGroupMap);
            if (!isValidTransferAmount) {
                reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_EXCEED_MAXIMUM);
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
            reportError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.MISSING_EMPLOYEE_ID, emplid);
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
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ACCOUNTING_LINE_TOTALS_MISMATCH_ERROR);
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
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ACCOUNTING_LINE_TOTALS_BY_PAYFY_PAYPERIOD_MISMATCH_ERROR);
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

        for (Entry<String, ExpenseTransferAccountingLine> entry : entrySet) {
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

        boolean isValid = true;

        Map<String, KualiDecimal> unbalancedObjectCodes = expenseTransferDocument.getUnbalancedObjectCodes();
        if (!unbalancedObjectCodes.isEmpty()) {
            reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT);
            isValid = false;
        }

        return isValid;
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

    /**
     * Gets the balance amount of a given period
     * 
     * @param fieldValues
     * @param periodCode
     * @return
     */
    private KualiDecimal getBalanceAmountOfGivenPeriod(Map<String, Object> fieldValues, String periodCode) {
        KualiDecimal balanceAmount = KualiDecimal.ZERO;
        List<LedgerBalance> ledgerBalances = (List<LedgerBalance>) SpringContext.getBean(BusinessObjectService.class).findMatching(LedgerBalance.class, fieldValues);
        if (!ledgerBalances.isEmpty()) {
            balanceAmount = ledgerBalances.get(0).getAmount(periodCode);
        }
        return balanceAmount;
    }

    /**
     * Determines whether a negtive amount can be transferred from one account to another
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
     * Groups the accounting lines by the specified key fields
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
     * Determines whether the account in the target line accepts fringe benefits.
     * 
     * @param accountingLine the line to check
     * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
     */
    protected boolean isAccountAcceptFringeBenefit(AccountingLine accountingLine) {
        boolean acceptsFringeBenefits = true;

        Account account = accountingLine.getAccount();
        if (account != null && !account.isAccountsFringesBnftIndicator()) {
            String overrideCode = accountingLine.getOverrideCode();
            boolean canNonFringeAccountUsed = NON_FRINGE_ACCOUNT_USED.equals(overrideCode);
            canNonFringeAccountUsed = canNonFringeAccountUsed || EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED.equals(overrideCode);

            if (!canNonFringeAccountUsed) {
                acceptsFringeBenefits = false;
            }
        }

        return acceptsFringeBenefits;
    }

    /**
     * Gets the default key of ExpenseTransferAccountingLine
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

        defaultKey.add(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR);
        defaultKey.add(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE);

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
     * @see org.kuali.kfs.rule.AccountingLineRule#isDebit(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument financialDocument, AccountingLine accountingLine) {
        return false;
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
}