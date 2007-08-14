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
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;
import static org.kuali.kfs.bo.AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED;
import static org.kuali.module.labor.LaborConstants.LABOR_LEDGER_CHART_OF_ACCOUNT_CODE;

import java.sql.Timestamp;
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
import org.kuali.core.exceptions.ReferentialIntegrityException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.HomeOriginationService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborConstants.LABOR_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.labor.bo.BenefitsCalculation;
import org.kuali.module.labor.bo.BenefitsType;
import org.kuali.module.labor.bo.ExpenseTransferAccountingLine;
import org.kuali.module.labor.bo.ExpenseTransferSourceAccountingLine;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.PositionObjectBenefit;
import org.kuali.module.labor.document.LaborExpenseTransferDocumentBase;
import org.kuali.module.labor.document.LaborLedgerPostingDocument;
import org.kuali.module.labor.rule.GenerateLaborLedgerPendingEntriesRule;
import org.kuali.module.labor.service.LaborBenefitsCalculationService;
import org.kuali.module.labor.service.LaborBenefitsTypeService;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;
import org.kuali.module.labor.service.LaborPositionObjectBenefitService;
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
     * @see org.kuali.kfs.rules.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.document.AccountingDocument,
     *      org.kuali.kfs.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
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
        
        // We must not have any pending labor ledger entries with same emplId, periodCode, accountNumber, objectCode
        isValid = validatePendingExpenseTransfer(expenseTransferDocument.getEmplid(), sourceLines);

        return isValid;
    }

    /**
     * Base rules for the <code>{@link GenerateBenefitClearingLaborLedgerPendingEntriesEvent}</code>.
     * 
     * @param accountingDocument that can post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitClearingEntry
     * @return boolean
     */
    protected boolean processBenefitClearingLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, LaborLedgerPendingEntry benefitClearingEntry) {

        // populate the entry
        populateBenefitClearingLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitClearingEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitClearingLaborLedgerPendingEntry(accountingDocument, accountingLine, benefitClearingEntry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(benefitClearingEntry);

        return true;
    }

    /**
     * Here to develop custom rules for <code>{@link GenerateBenefitClearingLaborLedgerPendingEntriesEvent}</code>.
     * 
     * @param accountingDocument that can post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitClearingEntry
     * @return boolean
     */
    protected boolean customizeBenefitClearingLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, LaborLedgerPendingEntry benefitClearingEntry) {
        return true;
    }

    /**
     * Populates the benefit clearing entry
     * 
     * @param accountingDocument that can post labor ledger pending entries
     * @param accountingLine
     * @param sequenceHelper
     * @param benefitClearingEntry
     */
    protected void populateBenefitClearingLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry benefitClearingEntry) {
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
     * Determine whether target accouting lines have the same amounts as source accounting lines for each object code
     * 
     * @param accountingDocument the given accounting document
     * @return true if target accouting lines have the same amounts as source accounting lines for each object code; otherwise,
     *         false
     */
    private boolean isValidAmountTransferredByObjectCode(AccountingDocument accountingDocument) {
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
        return processGenerateLaborLedgerPendingEntries(accountingDocument, (ExpenseTransferAccountingLine) accountingLine, sequenceHelper);
    }

    /**
     * This method is the starting point for creating labor ledger pending entries. The logic used to create the LLPEs resides in
     * this method.
     * 
     * @param accountingDocument is an instance of <code>{@link LaborLedgerPostingDocument}</code>
     * @param accountingLine is an instance of <code>{@link ExpenseTransferAccountingLine}</code>
     * @param sequenceHelper
     * @return
     */
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started processGenerateLaborLedgerPendingEntries");

        Collection<PositionObjectBenefit> positionObjectBenefits;

        // setup default values, so they don't have to be set multiple times
        LaborLedgerPendingEntry defaultEntry = new LaborLedgerPendingEntry();
        populateDefaultLaborLedgerPendingEntry(accountingDocument, accountingLine, defaultEntry);

        // Generate orig entry
        LaborLedgerPendingEntry originalEntry = (LaborLedgerPendingEntry) ObjectUtils.deepCopy(defaultEntry);
        processOriginalLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, originalEntry);

        // if the AL's pay FY and period do not match the University fiscal year and period
        if (!isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(accountingDocument, accountingLine)) {
            // Generate A21
            LaborLedgerPendingEntry a21Entry = (LaborLedgerPendingEntry) ObjectUtils.deepCopy(defaultEntry);
            processA21LaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, a21Entry);
        }

        // Generate A21 rev
        LaborLedgerPendingEntry a21RevEntry = (LaborLedgerPendingEntry) ObjectUtils.deepCopy(defaultEntry);
        processA21RevLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, a21RevEntry);

        // retrieve the labor object if null
        if (ObjectUtils.isNull(accountingLine.getLaborObject())) {
            accountingLine.refreshReferenceObject("laborObject");
        }

        // if AL object code is a salary object code
        if (StringUtils.equals(accountingLine.getLaborObject().getFinancialObjectFringeOrSalaryCode(), LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE)) {
            // get benefits
            positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getPositionObjectBenefits(accountingLine.getPayrollEndDateFiscalYear(), accountingLine.getChartOfAccountsCode(), accountingLine.getFinancialObjectCode());

            // for each row in the ld_lbr_obj_bene_t table for the labor ledger AL's pay FY, chart and object code
            for (PositionObjectBenefit pob : positionObjectBenefits) {

                // fringe benefit code
                String fringeBenefitObjectCode = pob.getBenefitsCalculation().getPositionFringeBenefitObjectCode();

                // calculate the benefit amount (ledger amt * (benfit pct/100) )
                KualiDecimal benefitAmount = pob.getBenefitsCalculation().getPositionFringeBenefitPercent();
                benefitAmount = benefitAmount.divide(new KualiDecimal(100));
                benefitAmount = benefitAmount.multiply(accountingLine.getAmount());

                // Generate Benefit
                LaborLedgerPendingEntry benefitEntry = (LaborLedgerPendingEntry) ObjectUtils.deepCopy(defaultEntry);
                processBenefitLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, benefitEntry, benefitAmount, fringeBenefitObjectCode);

                // if the AL's pay FY and period do not match the University fiscal year and period
                if (!isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(accountingDocument, accountingLine)) {
                    // Generate Benefit A21
                    LaborLedgerPendingEntry benefitA21Entry = (LaborLedgerPendingEntry) ObjectUtils.deepCopy(defaultEntry);
                    processBenefitA21LaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, benefitA21Entry, benefitAmount, fringeBenefitObjectCode);
                }

                // Generate Benefit A21 rev
                LaborLedgerPendingEntry benefitA21RevEntry = (LaborLedgerPendingEntry) ObjectUtils.deepCopy(defaultEntry);
                processBenefitA21RevLaborLedgerPendingEntry(accountingDocument, sequenceHelper, accountingLine, benefitA21RevEntry, benefitAmount, fringeBenefitObjectCode);
            }

        }

        LOG.info("completed processGenerateLaborLedgerPendingEntries");

        return true;
    }

    /**
     * This method generates benefit clearing and pending entries when the sum of the amount for the source accounting lines by
     * benefit type does not match the sum of the amount for the target accountine lines by benefit type.
     * 
     * @param AccountingDocument
     * @param sequenceHelper
     * @return
     */
    public boolean processGenerateLaborLedgerBenefitClearingPendingEntries(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        LOG.info("started processGenerateLaborLedgerBenefitClearingPendingEntries");

        Collection<PositionObjectBenefit> positionObjectBenefits;

        Map sourceBenefitAmountSumByBenefitType = new HashMap();
        Map targetBenefitAmountSumByBenefitType = new HashMap();

        ExpenseTransferAccountingLine sourceAL = null;
        ExpenseTransferAccountingLine targetAL = null;

        List<ExpenseTransferAccountingLine> lines = new ArrayList<ExpenseTransferAccountingLine>();

        // set source and target accounting lines
        lines.addAll(accountingDocument.getSourceAccountingLines());
        lines.addAll(accountingDocument.getTargetAccountingLines());

        Collection<BenefitsType> benefitsType;

        // retrieve all benefits type
        benefitsType = SpringContext.getBean(LaborBenefitsTypeService.class).getBenefitsType();

        KualiDecimal amount = new KualiDecimal(0);


        if (benefitsType == null) {
            return false;
        }

        // loop through all source lines, and add to array where benefit type matches
        for (BenefitsType bt : benefitsType) {

            for (ExpenseTransferAccountingLine line : lines) {
                Map benefitAmountSumByBenefitType = targetBenefitAmountSumByBenefitType;

                if (line instanceof SourceAccountingLine) {
                    benefitAmountSumByBenefitType = sourceBenefitAmountSumByBenefitType;
                }

                // get related benefit objects
                positionObjectBenefits = SpringContext.getBean(LaborPositionObjectBenefitService.class).getPositionObjectBenefits(line.getPayrollEndDateFiscalYear(), line.getChartOfAccountsCode(), line.getFinancialObjectCode());

                // loop through all of this accounting lines benefit type objects, matching with the outer benefit object
                for (PositionObjectBenefit pob : positionObjectBenefits) {

                    if (StringUtils.equals(pob.getFinancialObjectBenefitsTypeCode(), bt.getPositionBenefitTypeCode())) {

                        // take out existing amount and add to it, or store amount if not in the map yet
                        if (benefitAmountSumByBenefitType.containsKey(pob.getFinancialObjectBenefitsTypeCode())) {
                            amount = (KualiDecimal) benefitAmountSumByBenefitType.get(pob.getFinancialObjectBenefitsTypeCode());
                            amount = amount.add(line.getAmount());
                        }
                        else {
                            amount = line.getAmount();
                        }

                        // add amount with object code key back into map
                        benefitAmountSumByBenefitType.put(pob.getFinancialObjectBenefitsTypeCode(), amount);
                    }
                }
            }
        }

        // with arrays filled with amounts by benefit type,
        // generate benefit clearing entries for each benefit type with the amounts from target and source
        KualiDecimal sourceBenefitAmount = new KualiDecimal(0);
        KualiDecimal targetBenefitAmount = new KualiDecimal(0);
        String currentKey = "";
        Map.Entry entry = null;

        // Loop through source amounts
        for (Iterator i = sourceBenefitAmountSumByBenefitType.entrySet().iterator(); i.hasNext();) {
            // initialize
            entry = (Map.Entry) i.next();
            currentKey = (String) entry.getKey();
            sourceBenefitAmount = (KualiDecimal) entry.getValue();

            // if the target map has an entry for the current benefit type, process both amounts
            if (targetBenefitAmountSumByBenefitType.containsKey(currentKey)) {
                targetBenefitAmount = (KualiDecimal) targetBenefitAmountSumByBenefitType.get(currentKey);
            }
            else {
                targetBenefitAmount = new KualiDecimal(0);
            }

            // only process if amounts are not the same
            if (sourceBenefitAmount.equals(targetBenefitAmount) == false) {
                // process for each source amount and possibly a target amount
                processBenefitClearingLaborLedgerPendingEntry(accountingDocument, sequenceHelper, currentKey, sourceBenefitAmount, targetBenefitAmount);
            }
        }

        // Loop through target amounts
        for (Iterator i = targetBenefitAmountSumByBenefitType.entrySet().iterator(); i.hasNext();) {
            // initialize
            entry = (Map.Entry) i.next();
            currentKey = (String) entry.getKey();
            targetBenefitAmount = (KualiDecimal) entry.getValue();

            // if the source map has an entry for the current benefit type, process both amounts
            if (sourceBenefitAmountSumByBenefitType.containsKey(currentKey)) {
                // Do nothing, we've already processed the case of both matching
            }
            else {
                sourceBenefitAmount = new KualiDecimal(0);

                // only process if amounts are not the same
                if (sourceBenefitAmount.equals(targetBenefitAmount) == false) {
                    // process only the target amounts that don't match a source for completeness
                    processBenefitClearingLaborLedgerPendingEntry(accountingDocument, sequenceHelper, currentKey, sourceBenefitAmount, targetBenefitAmount);
                }
            }
        }

        LOG.info("completed processGenerateLaborLedgerBenefitClearingPendingEntries");

        return true;
    }

    /**
     * This method compares the pay fiscal year and period from the accounting line and the university values. A true is returned if
     * the values match.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @return
     */
    private boolean isAccountingLinePayFYPeriodMatchesUniversityPayFYPeriod(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine) {
        boolean success = true;

        AccountingPeriod ap = accountingDocument.getAccountingPeriod();
        ExpenseTransferAccountingLine al = (ExpenseTransferAccountingLine) accountingLine;

        // if the AL's pay FY and period do not match the University fiscal year and period
        if (!(ap.getUniversityFiscalYear().equals(al.getPayrollEndDateFiscalYear()) && ap.getUniversityFiscalPeriodCode().equals(al.getPayrollEndDateFiscalPeriodCode()))) {
            success = false;
        }

        return success;
    }

    /**
     * This method returns the accounting line's chart code if it accepts fringe benefits, otherwise the report to chart is
     * returned.
     * 
     * @param accountingLine
     * @return
     */
    private String getLaborLedgerPendingEntryBenefitChart(AccountingLine accountingLine) {
        String chart = null;

        if (accountingLine.getAccount().isAccountsFringesBnftIndicator()) {
            chart = accountingLine.getChartOfAccountsCode();
        }
        else {
            chart = accountingLine.getAccount().getReportsToChartOfAccountsCode();
        }

        return chart;
    }

    /**
     * This method returns the accounting line's account number if it accepts fringe benefits,
     * 
     * @param accountingLine
     * @return
     */
    private String getLaborLedgerPendingEntryBenefitAccount(AccountingLine accountingLine) {
        String accountNumber = null;

        if (accountingLine.getAccount().isAccountsFringesBnftIndicator()) {
            accountNumber = accountingLine.getAccountNumber();
        }
        else {
            accountNumber = accountingLine.getAccount().getReportsToAccountNumber();
        }

        return accountNumber;
    }

    /**
     * This method populates common fields amongst the different LLPE use cases.
     * 
     * @param transactionalDocument
     * @param accountingLine
     * @param sequenceHelper
     * @param originalEntry
     */
    protected void populateDefaultLaborLedgerPendingEntry(LaborLedgerPostingDocument transactionalDocument, AccountingLine accountingLine, LaborLedgerPendingEntry defaultEntry) {

        // the same across all types
        ObjectCode objectCode = accountingLine.getObjectCode();
        if (ObjectUtils.isNull(objectCode)) {
            accountingLine.refreshReferenceObject("objectCode");
        }
        defaultEntry.setFinancialObjectTypeCode(accountingLine.getObjectCode().getFinancialObjectTypeCode());
        defaultEntry.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(transactionalDocument.getClass()));
        defaultEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        defaultEntry.setDocumentNumber(accountingLine.getDocumentNumber());
        defaultEntry.setTransactionLedgerEntryDescription(getEntryValue(accountingLine.getFinancialDocumentLineDescription(), transactionalDocument.getDocumentHeader().getFinancialDocumentDescription()));
        defaultEntry.setOrganizationDocumentNumber(transactionalDocument.getDocumentHeader().getOrganizationDocumentNumber());
        defaultEntry.setFinancialDocumentReversalDate(null);
        defaultEntry.setReferenceFinancialSystemOriginationCode(null);
        defaultEntry.setReferenceFinancialDocumentNumber(null);
        defaultEntry.setReferenceFinancialDocumentTypeCode(null);

    }

    /**
     * Base functionality for handling the original pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param originalEntry
     * @return boolean
     */
    protected boolean processOriginalLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, AccountingLine accountingLine, LaborLedgerPendingEntry originalEntry) {

        // populate the entry
        populateOriginalLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, originalEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeOriginalLaborLedgerPendingEntry(accountingDocument, accountingLine, originalEntry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(originalEntry);

        return true;
    }

    /**
     * Custom functionality for handling the original pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param originalEntry
     * @return boolean
     */
    protected boolean customizeOriginalLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, LaborLedgerPendingEntry originalEntry) {
        return true;
    }

    /**
     * This method gets the next sequence number and increments.
     * 
     * @param sequenceHelper
     * @return
     */
    private Integer getNextSequenceNumber(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        // get sequence number and increment
        Integer next = sequenceHelper.getSequenceCounter();
        sequenceHelper.increment();

        return next;
    }

    protected void populateOriginalLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry originalEntry) {

        originalEntry.setUniversityFiscalYear(null);
        originalEntry.setUniversityFiscalPeriodCode(null);
        originalEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        originalEntry.setAccountNumber(accountingLine.getAccountNumber());
        originalEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        originalEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        originalEntry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        originalEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL); // this is the default that most documents use
        originalEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
        originalEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        originalEntry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        originalEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        originalEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        originalEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        originalEntry.setPositionNumber(((ExpenseTransferAccountingLine) accountingLine).getPositionNumber());
        originalEntry.setEmplid(((ExpenseTransferAccountingLine) accountingLine).getEmplid());
        originalEntry.setPayrollEndDateFiscalYear(((ExpenseTransferAccountingLine) accountingLine).getPayrollEndDateFiscalYear());
        originalEntry.setPayrollEndDateFiscalPeriodCode(((ExpenseTransferAccountingLine) accountingLine).getPayrollEndDateFiscalPeriodCode());
        originalEntry.setTransactionTotalHours(((ExpenseTransferAccountingLine) accountingLine).getPayrollTotalHours());

        originalEntry.setReferenceFinancialSystemOriginationCode(null);
        originalEntry.setReferenceFinancialDocumentNumber(null);
        originalEntry.setReferenceFinancialDocumentTypeCode(null);

        // TODO wait for core budget year data structures to be put in place
        // originalEntry.setBudgetYear(accountingLine.getBudgetYear());
        // originalEntry.setBudgetYearFundingSourceCode(budgetYearFundingSourceCode);
    }

    /**
     * Base functionality for handling the A21 pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param a21
     * @return boolean
     */
    protected boolean processA21LaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry a21Entry) {

        // populate the entry
        populateA21LaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, a21Entry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeA21LaborLedgerPendingEntry(accountingDocument, accountingLine, a21Entry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(a21Entry);

        return true;
    }

    /**
     * Custom functionality for handling the A21 pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param a21Entry
     * @return boolean
     */
    protected boolean customizeA21LaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry a21Entry) {
        return true;
    }

    /**
     * Default population method for the A21 pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param a21Entry
     */
    protected void populateA21LaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry a21Entry) {
        a21Entry.setUniversityFiscalYear(null);
        a21Entry.setUniversityFiscalPeriodCode(null);
        a21Entry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        a21Entry.setAccountNumber(accountingLine.getAccountNumber());
        a21Entry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        a21Entry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        a21Entry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        a21Entry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        a21Entry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
        a21Entry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));
        a21Entry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        a21Entry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        a21Entry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        a21Entry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        a21Entry.setPositionNumber(accountingLine.getPositionNumber());
        a21Entry.setEmplid(accountingLine.getEmplid());
        a21Entry.setPayrollEndDateFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        a21Entry.setPayrollEndDateFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        a21Entry.setTransactionTotalHours(accountingLine.getPayrollTotalHours());

        a21Entry.setReferenceFinancialSystemOriginationCode(null);
        a21Entry.setReferenceFinancialDocumentNumber(null);
        a21Entry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * Base functionality for handling the A21 revision pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param a21RevEntry
     * @return boolean
     */
    protected boolean processA21RevLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry a21RevEntry) {

        // populate the entry
        populateA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, a21RevEntry);

        // hook for children documents to implement document specific LLPE field mappings
        customizeA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, a21RevEntry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(a21RevEntry);

        return true;
    }

    /**
     * Custom functionality for handling the A21 revision pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param a21RevEntry
     * @return boolean
     */
    protected boolean customizeA21RevLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry a21RevEntry) {
        return true;
    }

    /**
     * Populates the A21 Revision entry with defaults
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param a21RevEntry
     */
    protected void populateA21RevLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry a21RevEntry) {

        a21RevEntry.setUniversityFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        a21RevEntry.setUniversityFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        a21RevEntry.setChartOfAccountsCode(accountingLine.getChartOfAccountsCode());
        a21RevEntry.setAccountNumber(accountingLine.getAccountNumber());
        a21RevEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        a21RevEntry.setFinancialObjectCode(accountingLine.getFinancialObjectCode());
        a21RevEntry.setFinancialSubObjectCode(getEntryValue(accountingLine.getFinancialSubObjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE));
        a21RevEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        a21RevEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
        a21RevEntry.setTransactionLedgerEntryAmount(getGeneralLedgerPendingEntryAmountForAccountingLine(accountingLine));

        // Jira KULLAB-224
        if (a21RevEntry.getFinancialObject() != null && a21RevEntry.getFinancialObject().getFinancialObjectSubTypeCode() != null && a21RevEntry.getFinancialObject().getFinancialObjectSubTypeCode().equals("FR")) {
            a21RevEntry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        }
        else {
            a21RevEntry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
        }

        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        a21RevEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        a21RevEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        a21RevEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        a21RevEntry.setPositionNumber(accountingLine.getPositionNumber());
        a21RevEntry.setEmplid(accountingLine.getEmplid());
        a21RevEntry.setPayrollEndDateFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        a21RevEntry.setPayrollEndDateFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        a21RevEntry.setTransactionTotalHours(accountingLine.getPayrollTotalHours());

        a21RevEntry.setReferenceFinancialSystemOriginationCode(null);
        a21RevEntry.setReferenceFinancialDocumentNumber(null);
        a21RevEntry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * Base functionality for handling the Benefit pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitEntry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     * @return boolean
     */
    protected boolean processBenefitLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry benefitEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {

        // populate the entry
        populateBenefitLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitEntry, benefitAmount, fringeBenefitObjectCode);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitLaborLedgerPendingEntry(accountingDocument, accountingLine, benefitEntry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(benefitEntry);

        return true;
    }

    /**
     * Custom functionality for handling the Benefit pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitEntry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     * @return boolean
     */
    protected boolean customizeBenefitLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry benefitEntry) {
        return true;
    }

    /**
     * Populates the benefit pending entry with default values
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitEntry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     */
    protected void populateBenefitLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry benefitEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        benefitEntry.setUniversityFiscalYear(null);
        benefitEntry.setUniversityFiscalPeriodCode(null);

        // special handling
        benefitEntry.setChartOfAccountsCode(getLaborLedgerPendingEntryBenefitChart(accountingLine));
        benefitEntry.setAccountNumber(getLaborLedgerPendingEntryBenefitAccount(accountingLine));

        // set benefit amount and fringe object code
        benefitEntry.setTransactionLedgerEntryAmount(benefitAmount);
        benefitEntry.setFinancialObjectCode(fringeBenefitObjectCode);

        benefitEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        benefitEntry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        benefitEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
        benefitEntry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_CREDIT_CODE : KFSConstants.GL_DEBIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        benefitEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        benefitEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        benefitEntry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitEntry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitEntry.setPayrollEndDateFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        benefitEntry.setPayrollEndDateFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        benefitEntry.setTransactionTotalHours(accountingLine.getPayrollTotalHours());

        benefitEntry.setReferenceFinancialSystemOriginationCode(null);
        benefitEntry.setReferenceFinancialDocumentNumber(null);
        benefitEntry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * Base functionality for handling the Benefit A21 pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitA21Entry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     * @return boolean
     */
    protected boolean processBenefitA21LaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry benefitA21Entry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {

        // populate the entry
        populateBenefitA21LaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitA21Entry, benefitAmount, fringeBenefitObjectCode);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitA21LaborLedgerPendingEntry(accountingDocument, accountingLine, benefitA21Entry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(benefitA21Entry);

        return true;
    }

    /**
     * Custom functionality for handling the Benefit A21 pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitA21Entry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     * @return boolean
     */
    protected boolean customizeBenefitA21LaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry benefitA21Entry) {
        return true;
    }

    /**
     * Populates the benefit A21 pending entry with default values
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitA21Entry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     */
    protected void populateBenefitA21LaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry benefitA21Entry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        benefitA21Entry.setUniversityFiscalYear(null);
        benefitA21Entry.setUniversityFiscalPeriodCode(null);

        // special handling
        benefitA21Entry.setChartOfAccountsCode(getLaborLedgerPendingEntryBenefitChart(accountingLine));
        benefitA21Entry.setAccountNumber(getLaborLedgerPendingEntryBenefitAccount(accountingLine));

        // set benefit amount and fringe object code
        benefitA21Entry.setTransactionLedgerEntryAmount(benefitAmount);
        benefitA21Entry.setFinancialObjectCode(fringeBenefitObjectCode);

        benefitA21Entry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        benefitA21Entry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitA21Entry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        benefitA21Entry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
        benefitA21Entry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        benefitA21Entry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitA21Entry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        benefitA21Entry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        benefitA21Entry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitA21Entry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitA21Entry.setPayrollEndDateFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        benefitA21Entry.setPayrollEndDateFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        benefitA21Entry.setTransactionTotalHours(accountingLine.getPayrollTotalHours());

        benefitA21Entry.setReferenceFinancialSystemOriginationCode(null);
        benefitA21Entry.setReferenceFinancialDocumentNumber(null);
        benefitA21Entry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * Base functionality for handling the Benefit A21 rev pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitA21RevEntry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     * @return boolean
     */
    protected boolean processBenefitA21RevLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry benefitA21RevEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {

        // populate the entry
        populateBenefitA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, sequenceHelper, benefitA21RevEntry, benefitAmount, fringeBenefitObjectCode);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitA21RevLaborLedgerPendingEntry(accountingDocument, accountingLine, benefitA21RevEntry);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(benefitA21RevEntry);

        return true;
    }

    /**
     * Custom functionality for handling the Benefit A21 rev pending entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitA21RevEntry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     * @return boolean
     */
    protected boolean customizeBenefitA21RevLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, LaborLedgerPendingEntry benefitA21RevEntry) {
        return true;
    }

    /**
     * Populates the Benefit A21 Rev Pending Entry with default values
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param accountingLine
     * @param benefitA21RevEntry
     * @param benefitAmount
     * @param fringeBenefitObjectCode
     */
    protected void populateBenefitA21RevLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, ExpenseTransferAccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry benefitA21RevEntry, KualiDecimal benefitAmount, String fringeBenefitObjectCode) {
        ExpenseTransferAccountingLine al = (ExpenseTransferAccountingLine) accountingLine;

        benefitA21RevEntry.setUniversityFiscalYear(al.getPayrollEndDateFiscalYear());
        benefitA21RevEntry.setUniversityFiscalPeriodCode(al.getPayrollEndDateFiscalPeriodCode());

        // special handling
        benefitA21RevEntry.setChartOfAccountsCode(getLaborLedgerPendingEntryBenefitChart(accountingLine));
        benefitA21RevEntry.setAccountNumber(getLaborLedgerPendingEntryBenefitAccount(accountingLine));

        // set benefit amount and fringe object code
        benefitA21RevEntry.setTransactionLedgerEntryAmount(benefitAmount);
        benefitA21RevEntry.setFinancialObjectCode(fringeBenefitObjectCode);

        benefitA21RevEntry.setSubAccountNumber(getEntryValue(accountingLine.getSubAccountNumber(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER));
        benefitA21RevEntry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitA21RevEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_A21);
        benefitA21RevEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));
        benefitA21RevEntry.setTransactionDebitCreditCode(accountingLine.isSourceAccountingLine() ? KFSConstants.GL_DEBIT_CODE : KFSConstants.GL_CREDIT_CODE);
        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        benefitA21RevEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitA21RevEntry.setProjectCode(getEntryValue(accountingLine.getProjectCode(), LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING));
        benefitA21RevEntry.setOrganizationReferenceId(accountingLine.getOrganizationReferenceId());
        benefitA21RevEntry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitA21RevEntry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitA21RevEntry.setPayrollEndDateFiscalYear(accountingLine.getPayrollEndDateFiscalYear());
        benefitA21RevEntry.setPayrollEndDateFiscalPeriodCode(accountingLine.getPayrollEndDateFiscalPeriodCode());
        benefitA21RevEntry.setTransactionTotalHours(accountingLine.getPayrollTotalHours());

        benefitA21RevEntry.setReferenceFinancialSystemOriginationCode(null);
        benefitA21RevEntry.setReferenceFinancialDocumentNumber(null);
        benefitA21RevEntry.setReferenceFinancialDocumentTypeCode(null);
    }

    /**
     * Base functionality for handling the Benefit Clearing Pending Entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param benefitTypeCode
     * @param fromAmount
     * @param toAmount
     * @return boolean
     */
    protected boolean processBenefitClearingLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String benefitTypeCode, KualiDecimal fromAmount, KualiDecimal toAmount) {

        LaborLedgerPendingEntry benefitClearingEntry = new LaborLedgerPendingEntry();

        // populate the entry
        populateBenefitClearingLaborLedgerPendingEntry(accountingDocument, sequenceHelper, benefitClearingEntry, benefitTypeCode, fromAmount, toAmount);

        // hook for children documents to implement document specific LLPE field mappings
        customizeBenefitClearingLaborLedgerPendingEntry(accountingDocument, benefitClearingEntry, fromAmount, toAmount);

        // add the new entry to the document now
        accountingDocument.getLaborLedgerPendingEntries().add(benefitClearingEntry);

        return true;
    }

    /**
     * Custom functionality for handling the Benefit Clearing Pending Entry
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param benefitTypeCode
     * @param fromAmount
     * @param toAmount
     * @return boolean
     */
    protected boolean customizeBenefitClearingLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, LaborLedgerPendingEntry benefitClearingEntry, KualiDecimal fromAmount, KualiDecimal toAmount) {
        return true;
    }

    /**
     * Populates a Benefit Clearing Pending Entry with default values
     * 
     * @param accountingDocument must be able to post labor ledger pending entries
     * @param sequenceHelper
     * @param benefitTypeCode
     * @param fromAmount
     * @param toAmount
     */
    protected void populateBenefitClearingLaborLedgerPendingEntry(LaborLedgerPostingDocument accountingDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, LaborLedgerPendingEntry benefitClearingEntry, String benefitTypeCode, KualiDecimal fromAmount, KualiDecimal toAmount) {

        benefitClearingEntry.setUniversityFiscalYear(null);
        benefitClearingEntry.setUniversityFiscalPeriodCode(null);

        // special handling
        benefitClearingEntry.setChartOfAccountsCode("UA");
        benefitClearingEntry.setAccountNumber("9712700");

        benefitClearingEntry.setSubAccountNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_ACCOUNT_NUMBER);

        // special handling
        AccountingPeriod ap = accountingDocument.getAccountingPeriod();
        BenefitsCalculation bc = SpringContext.getBean(LaborBenefitsCalculationService.class).getBenefitsCalculation(ap.getUniversityFiscalYear(), "UA", benefitTypeCode);
        benefitClearingEntry.setFinancialObjectCode(bc.getPositionFringeBenefitObjectCode());

        benefitClearingEntry.setFinancialSubObjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_SUB_OBJECT_CODE);
        benefitClearingEntry.setFinancialBalanceTypeCode(BALANCE_TYPE_ACTUAL);
        benefitClearingEntry.setTransactionLedgerEntrySequenceNumber(getNextSequenceNumber(sequenceHelper));

        // special handling, set the transaction amount to the absolute value of the from minus the to amount
        KualiDecimal amount = fromAmount.subtract(toAmount);
        benefitClearingEntry.setTransactionLedgerEntryAmount(amount.abs());

        // special handling
        String debitCreditCode = KFSConstants.GL_CREDIT_CODE;
        if (fromAmount.isGreaterThan(toAmount)) {
            debitCreditCode = KFSConstants.GL_DEBIT_CODE;
        }
        benefitClearingEntry.setTransactionDebitCreditCode(debitCreditCode);

        Timestamp transactionTimestamp = new Timestamp(SpringContext.getBean(DateTimeService.class, "dateTimeService").getCurrentDate().getTime());
        benefitClearingEntry.setTransactionDate(new java.sql.Date(transactionTimestamp.getTime()));
        benefitClearingEntry.setProjectCode(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_PROJECT_STRING);
        benefitClearingEntry.setOrganizationReferenceId(null);
        benefitClearingEntry.setPositionNumber(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_POSITION_NUMBER);
        benefitClearingEntry.setEmplid(LABOR_LEDGER_PENDING_ENTRY_CODE.BLANK_EMPL_ID);
        benefitClearingEntry.setPayrollEndDateFiscalYear(ap.getUniversityFiscalYear());
        benefitClearingEntry.setPayrollEndDateFiscalPeriodCode(ap.getUniversityFiscalPeriodCode());
        benefitClearingEntry.setTransactionTotalHours(null);
        benefitClearingEntry.setReferenceFinancialSystemOriginationCode(null);
        benefitClearingEntry.setReferenceFinancialDocumentNumber(null);
        benefitClearingEntry.setReferenceFinancialDocumentTypeCode(null);

        // special handling
        ObjectCode oc = SpringContext.getBean(ObjectCodeService.class).getByPrimaryId(ap.getUniversityFiscalYear(), LABOR_LEDGER_CHART_OF_ACCOUNT_CODE, bc.getPositionFringeBenefitObjectCode());
        benefitClearingEntry.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());

        // defaults
        benefitClearingEntry.setFinancialDocumentTypeCode(SpringContext.getBean(DocumentTypeService.class).getDocumentTypeCodeByClass(accountingDocument.getClass()));
        benefitClearingEntry.setFinancialSystemOriginationCode(SpringContext.getBean(HomeOriginationService.class).getHomeOrigination().getFinSystemHomeOriginationCode());
        benefitClearingEntry.setDocumentNumber(accountingDocument.getDocumentNumber());
        benefitClearingEntry.setTransactionLedgerEntryDescription(accountingDocument.getDocumentHeader().getFinancialDocumentDescription());
        benefitClearingEntry.setOrganizationDocumentNumber(accountingDocument.getDocumentHeader().getOrganizationDocumentNumber());
        benefitClearingEntry.setFinancialDocumentReversalDate(null);
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
     * Applies the given flexible offset account to the given offset entry. Does nothing if flexibleOffsetAccount is null or its COA
     * and account number are the same as the offset entry's.
     * 
     * @param flexibleOffsetAccount may be null
     * @param offsetEntry may be modified
     */
    private static void flexOffsetAccountIfNecessary(OffsetAccount flexibleOffsetAccount, LaborLedgerPendingEntry offsetEntry) {
        if (flexibleOffsetAccount == null) {
            return; // They are not required and may also be disabled.
        }
        String flexCoa = flexibleOffsetAccount.getFinancialOffsetChartOfAccountCode();
        String flexAccountNumber = flexibleOffsetAccount.getFinancialOffsetAccountNumber();
        if (flexCoa.equals(offsetEntry.getChartOfAccountsCode()) && flexAccountNumber.equals(offsetEntry.getAccountNumber())) {
            return; // no change, so leave sub-account as is
        }
        if (ObjectUtils.isNull(flexibleOffsetAccount.getFinancialOffsetAccount())) {
            throw new ReferentialIntegrityException("flexible offset account " + flexCoa + "-" + flexAccountNumber);
        }
        offsetEntry.setChartOfAccountsCode(flexCoa);
        offsetEntry.setAccountNumber(flexAccountNumber);
        // COA and account number are part of the sub-account's key, so the original sub-account would be invalid.
        offsetEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
    }


    /**
     * Is the accounting line a debit accounting line?
     * 
     * @param accountingDocument
     * @param accountingLine
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
     * Is the accounting line a credit accounting line?
     * 
     * @param accountingDocument
     * @param accountingLine
     */
    public boolean isCredit(AccountingLine accountingLine, AccountingDocument accountingDocument) {
        return false;
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
         * <ol>
         * <li>accounting line section (source/target) and object type is included in determining if a line is debit or credit.
         * <li> negative line amounts are <b>Only</b> allowed during error correction
         * </ol>
         * the following are credits (return false)
         * <ol>
         * <li> <code>isSourceLine && (isExpense || isAsset) && (lineAmount > 0)</code>
         * <li> <code>isTargetLine && (isIncome || isLiability) && (lineAmount > 0)</code>
         * <li> <code>isErrorCorrection && isSourceLine && (isIncome || isLiability) && (lineAmount < 0)</code>
         * <li> <code>isErrorCorrection && isTargetLine && (isExpense || isAsset) && (lineAmount < 0)</code>
         * </ol>
         * the following are debits (return true)
         * <ol>
         * <li> <code>isSourceLine && (isIncome || isLiability) && (lineAmount > 0)</code>
         * <li> <code>isTargetLine && (isExpense || isAsset) && (lineAmount > 0)</code>
         * <li> <code>isErrorCorrection && (isExpense || isAsset) && (lineAmount < 0)</code>
         * <li> <code>isErrorCorrection && (isIncome || isLiability) && (lineAmount < 0)</code>
         * </ol>
         * the following are invalid ( throws an <code>IllegalStateException</code>)
         * <ol>
         * <li> <code>!isErrorCorrection && !(lineAmount > 0)</code>
         * <li> <code>isErrorCorrection && !(lineAmount < 0)</code>
         * </ol>
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

    /**
     * Verify that the selected employee does not have other pending salary transfers that have not been processed.
     * 
     * @param Employee ID, sourceLines
     * @return true if the employee does not have any pending salary transfers.
     */
    public boolean validatePendingExpenseTransfer(String emplid, List sourceLines) {

        // We must not have any pending labor ledger entries

        for (Object oj : sourceLines) {
            ExpenseTransferAccountingLine etal = (ExpenseTransferAccountingLine) oj;
            String payPeriod = etal.getPayrollEndDateFiscalPeriodCode();
            String accountNumber = etal.getAccountNumber();
            String objectCode = etal.getObjectCode().getCode();

            if (SpringContext.getBean(LaborLedgerPendingEntryService.class).hasPendingLaborLedgerEntry(emplid, payPeriod, accountNumber, objectCode)) {
                reportError(KFSConstants.EMPLOYEE_LOOKUP_ERRORS, KFSKeyConstants.Labor.PENDING_SALARY_TRANSFER_ERROR, emplid, payPeriod, accountNumber, objectCode);
                return false;
            }

        }

        return true;
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
     * 
     * This method is used to verify if the select labor object code is active.
     * @param accountingDocument
     * @return
     */
    protected boolean isActiveLaborObjectCode(AccountingDocument accountingDocument) {
        LOG.debug("started -- isActiveLaborObjectCode");
        LOG.debug("finished -- isActiveLaborObjectCode");
        return true;
    }
  
}
