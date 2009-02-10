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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.validation.GenerateLaborLedgerBenefitClearingPendingEntriesRule;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.TransactionalDocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;


/**
 * Business rule(s) applicable to Salary Expense Transfer documents.
 */
public class SalaryExpenseTransferDocumentRule extends LaborExpenseTransferDocumentRules implements GenerateLaborLedgerBenefitClearingPendingEntriesRule<LaborLedgerPostingDocument> {

    /**
     * Constructs a SalaryExpenseTransferDocumentRule.java.
     */
    public SalaryExpenseTransferDocumentRule() {
        super();
    }

    /**
     * Checks if user is allowed to edit the object code and check the object code balances match when document was inititated, else
     * check they balance
     * 
     * @param accountingDocument
     * @return boolean
     * @see org.kuali.kfs.module.ld.document.validation.impl.LaborExpenseTransferDocumentRules#isValidAmountTransferredByObjectCode(org.kuali.kfs.sys.document.AccountingDocument)
     */
    @Override
    protected boolean isValidAmountTransferredByObjectCode(AccountingDocument accountingDocument) {
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;

        boolean isAdmin = false;
        
        TransactionalDocumentAuthorizer documentAuthorizer = (TransactionalDocumentAuthorizer) SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(expenseTransferDocument);        
        
        isAdmin = documentAuthorizer.isAuthorized(expenseTransferDocument, LaborConstants.LABOR_MODULE_CODE, 
                 LaborConstants.PermissionNames.OVERRIDE_TRANSFER_IMPACTING_EFFORT_CERTIFICATION, 
                                  GlobalVariables.getUserSession().getPerson().getPrincipalId()); 
        
        if (isAdmin) {
            return true;
        }

        // if approving document, check the object code balances match when document was inititated, else check the balance
        boolean isValid = true;
        if (accountingDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            if (!isObjectCodeBalancesUnchanged(accountingDocument)) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_BY_OBJECT_APPROVAL_CHANGE);
                isValid = false;
            }
        }
        else {
            if (!expenseTransferDocument.getUnbalancedObjectCodes().isEmpty()) {
                reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_TRANSFER_AMOUNT_NOT_BALANCED_BY_OBJECT);
                isValid = false;
            }
        }

        return isValid;
    }

    /**
     * Saves document
     * 
     * @param document
     * @return boolean
     * @see org.kuali.rice.kns.rules.SaveDocumentRule#processCustomSaveDocumentBusinessRules(Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        // Validate that an employee ID is enterred.
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument) document;
        String emplid = salaryExpenseTransferDocument.getEmplid();
        if ((emplid == null) || (emplid.trim().length() == 0)) {
            reportError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.MISSING_EMPLOYEE_ID, emplid);
            return false;
        }

        // ensure the employee ids in the source accounting lines are same
        AccountingDocument accountingDocument = (AccountingDocument) document;
        if (!hasAccountingLinesSameEmployee(accountingDocument)) {
            return false;
        }

        return true;
    }

    /**
     * Adds an accounting line in the document and validates only salary labor object codes are allowed on the salary expense
     * transfer document
     * 
     * @param accountingDocument
     * @param accountingLine
     * @return boolean
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument accountingDocument, AccountingLine accountingLine) {
        boolean isValid = super.processCustomAddAccountingLineBusinessRules(accountingDocument, accountingLine);

        // only salary labor object codes are allowed on the salary expense transfer document
        if (!isSalaryObjectCode(accountingLine)) {
            reportError(KFSPropertyConstants.ACCOUNT, LaborKeyConstants.INVALID_SALARY_OBJECT_CODE_ERROR, accountingLine.getAccountNumber());
            return false;
        }

        return isValid;
    }

    /**
     * Process the routing of the document and validates that document must not have any pending labor ledger entries with same
     * emplId, periodCode, accountNumber, objectCode
     * 
     * @param document
     * @return boolean
     * @see org.kuali.kfs.module.ld.document.validation.impl.LaborExpenseTransferDocumentRules#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);
        SalaryExpenseTransferDocument expenseTransferDocument = (SalaryExpenseTransferDocument) document;

        // must not have any pending labor ledger entries with same emplId, periodCode, accountNumber, objectCode
        if (isValid) {
            isValid = !hasPendingLedgerEntry(expenseTransferDocument);
        }

        return isValid;
    }

    /**
     * Determines whether the object code of given accounting line is a salary labor object code
     * 
     * @param accountingLine the given accounting line
     * @return true if the object code of given accounting line is a salary; otherwise, false
     */
    private boolean isSalaryObjectCode(AccountingLine accountingLine) {
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        LaborObject laborObject = expenseTransferAccountingLine.getLaborObject();
        if (laborObject == null) {
            return false;
        }

        String fringeOrSalaryCode = laborObject.getFinancialObjectFringeOrSalaryCode();
        return StringUtils.equals(LaborConstants.SalaryExpenseTransfer.LABOR_LEDGER_SALARY_CODE, fringeOrSalaryCode);
    }

    /**
     * Checks the current object code balance map of the document against the balances captured before the document was returned for
     * approval.
     * 
     * @param accountingDocument SalaryExpenseTransferDocument to check
     * @return true if the balances have not changed, false if they have
     */
    private boolean isObjectCodeBalancesUnchanged(AccountingDocument accountingDocument) {
        boolean isUnchanged = true;

        Map<String, KualiDecimal> initiatedObjectCodeBalances = ((SalaryExpenseTransferDocument) accountingDocument).getApprovalObjectCodeBalances();
        Map<String, KualiDecimal> currentObjectCodeBalances = ((SalaryExpenseTransferDocument) accountingDocument).getUnbalancedObjectCodes();

        Set<Entry<String, KualiDecimal>> initiatedObjectCodes = initiatedObjectCodeBalances.entrySet();
        Set<Entry<String, KualiDecimal>> currentObjectCodes = currentObjectCodeBalances.entrySet();

        if (initiatedObjectCodes == null) {
            if (currentObjectCodes != null) {
                isUnchanged = false;
            }
        }
        else {
            if (!initiatedObjectCodes.equals(currentObjectCodes)) {
                isUnchanged = false;
            }
        }

        return isUnchanged;
    }

    /**
     * determine whether the employees in the source accouting lines are same
     * 
     * @param accountingDocument the given accouting document
     * @return true if the employees in the source accouting lines are same; otherwise, false
     */
    private boolean hasAccountingLinesSameEmployee(AccountingDocument accountingDocument) {
        LOG.debug("stared hasDocumentsSameEmployee");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();
        List<ExpenseTransferTargetAccountingLine> targetAccountingLines = expenseTransferDocument.getTargetAccountingLines();

        boolean sourceAccountingLinesValidationResult = true;
        boolean targetAccountingLinesValidationResult = true;

        String employeeID = expenseTransferDocument.getEmplid();
        String accountingLineEmplID = null;

        // Source Lines
        for (ExpenseTransferSourceAccountingLine sourceAccountingLine : sourceAccountingLines) {
            accountingLineEmplID = sourceAccountingLine.getEmplid();
            if (accountingLineEmplID == null) {
                sourceAccountingLinesValidationResult = false;
            }
            else if (!employeeID.equals(accountingLineEmplID)) {
                sourceAccountingLinesValidationResult = false;
            }
        }

        // Target lines
        for (ExpenseTransferTargetAccountingLine targetAccountingLine : targetAccountingLines) {
            accountingLineEmplID = targetAccountingLine.getEmplid();
            if (accountingLineEmplID == null) {
                targetAccountingLinesValidationResult = false;
            }
            else if (!employeeID.equals(accountingLineEmplID)) {
                targetAccountingLinesValidationResult = false;
            }
        }

        if (!sourceAccountingLinesValidationResult) {
            reportError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EMPLOYEE_ID_NOT_SAME);
        }

        if (!targetAccountingLinesValidationResult) {
            reportError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EMPLOYEE_ID_NOT_SAME_IN_TARGET);
        }

        return (sourceAccountingLinesValidationResult && targetAccountingLinesValidationResult);
    }


    /**
     * determine if there is any pending entry for the source accounting lines of the given document
     * 
     * @param accountingDocument the given accounting document
     * @return true if there is a pending entry for the source accounting lines of the given document; otherwise, false
     */
    public boolean hasPendingLedgerEntry(AccountingDocument accountingDocument) {
        LOG.info("started hasPendingLedgerEntry(accountingDocument)");

        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Map<String, String> fieldValues = new HashMap<String, String>();
        for (ExpenseTransferAccountingLine sourceAccountingLine : sourceAccountingLines) {
            String payPeriodCode = sourceAccountingLine.getPayrollEndDateFiscalPeriodCode();
            String accountNumber = sourceAccountingLine.getAccountNumber();
            String objectCode = sourceAccountingLine.getFinancialObjectCode();
            String emplid = sourceAccountingLine.getEmplid();
            String documentNumber = accountingDocument.getDocumentNumber();

            fieldValues.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, payPeriodCode);
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
            fieldValues.put(KFSPropertyConstants.EMPLID, emplid);
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, KFSConstants.NOT_LOGICAL_OPERATOR + documentNumber);

            if (SpringContext.getBean(LaborLedgerPendingEntryService.class).hasPendingLaborLedgerEntry(fieldValues)) {
                reportError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.PENDING_SALARY_TRANSFER_ERROR, emplid, payPeriodCode, accountNumber, objectCode);
                return true;
            }
        }

        return false;
    }

    /**
     * @param LaborLedgerPostingDocument the given labor ledger accounting document
     * @return true after creating a list of Expense Pending entries and Benefit pending Entries
     * @see org.kuali.kfs.module.ld.document.validation.impl.LaborExpenseTransferDocumentRules#processGenerateLaborLedgerPendingEntries(org.kuali.kfs.module.ld.document.LaborLedgerPostingDocument,
     *      org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean processGenerateLaborLedgerPendingEntries(LaborLedgerPostingDocument document, AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started processGenerateLaborLedgerPendingEntries()");

        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;

        List<LaborLedgerPendingEntry> expensePendingEntries = LaborPendingEntryGenerator.generateExpensePendingEntries(document, expenseTransferAccountingLine, sequenceHelper);
        document.getLaborLedgerPendingEntries().addAll(expensePendingEntries);

        List<LaborLedgerPendingEntry> benefitPendingEntries = LaborPendingEntryGenerator.generateBenefitPendingEntries(document, expenseTransferAccountingLine, sequenceHelper);
        document.getLaborLedgerPendingEntries().addAll(benefitPendingEntries);

        return true;
    }

    /**
     * @param LaborLedgerPostingDocument the given labor ledger accounting document
     * @return true after generate Benefit Clearing Pending Entries for the document
     * @see org.kuali.kfs.module.ld.document.validation.GenerateLaborLedgerBenefitClearingPendingEntriesRule#processGenerateLaborLedgerBenefitClearingPendingEntries(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean processGenerateLaborLedgerBenefitClearingPendingEntries(LaborLedgerPostingDocument document, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.info("started processGenerateLaborLedgerBenefitClearingPendingEntries()");

        String chartOfAccountsCode = SpringContext.getBean(ParameterService.class).getParameterValue(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.BENEFIT_CLEARING_CHART_PARM_NM);
        String accountNumber = SpringContext.getBean(ParameterService.class).getParameterValue(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.BENEFIT_CLEARING_ACCOUNT_PARM_NM);

        List<LaborLedgerPendingEntry> benefitClearingPendingEntries = LaborPendingEntryGenerator.generateBenefitClearingPendingEntries(document, sequenceHelper, accountNumber, chartOfAccountsCode);
        document.getLaborLedgerPendingEntries().addAll(benefitClearingPendingEntries);

        return true;
    }
}

