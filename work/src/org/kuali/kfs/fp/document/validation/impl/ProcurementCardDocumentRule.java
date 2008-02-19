/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.rules;

import static org.kuali.kfs.KFSConstants.ACCOUNTING_LINE_ERRORS;
import static org.kuali.kfs.KFSConstants.AMOUNT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.ZERO;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS;
import static org.kuali.kfs.KFSKeyConstants.ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL;
import static org.kuali.kfs.KFSKeyConstants.ERROR_ZERO_AMOUNT;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.ProcurementCardTargetAccountingLine;
import org.kuali.module.financial.bo.ProcurementCardTransactionDetail;
import org.kuali.module.financial.document.ProcurementCardDocument;
import org.kuali.workflow.KualiWorkflowUtils.RouteLevelNames;

import edu.iu.uis.eden.exception.WorkflowException;


/**
 * Business rule(s) applicable to Procurement Card document.
 */
public class ProcurementCardDocumentRule extends AccountingDocumentRuleBase {

    /**
     * This method inserts modifies the errorPath due to architecture variations necessary in the procurement card document
     * design.  Finally, this method calls the super method to complete the validation and business rule checks..
     * 
     * @param transactionalDocument The document the accounting line being updated resides within.
     * @param accountingLine The original accounting line.
     * @param updatedAccoutingLine The updated version of the accounting line.
     * @return True if the business rules all pass for the update, false otherwise.
     * 
     * @see org.kuali.core.rule.UpdateAccountingLineRule#processUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean processUpdateAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        fixErrorPath(transactionalDocument, accountingLine);

        return super.processUpdateAccountingLineBusinessRules(transactionalDocument, accountingLine, updatedAccountingLine);
    }

    /**
     * This method performs business rule checks on the accounting line being added to the document to ensure the accounting line
     * is valid and appropriate for the document.  Currently, this method validates the account number and object code
     * associated with the new accounting line.  Only target lines can be changed, so we only need to validate them.
     * 
     * @param transactionalDocument The document the new line is being added to.
     * @param accountingLine The new accounting line being added.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        boolean allow = true;

        if (accountingLine instanceof ProcurementCardTargetAccountingLine) {
            LOG.debug("validating accounting line # " + accountingLine.getSequenceNumber());

            // Somewhat of an ugly hack... the goal is to have fixErrorPath run for cases where it's _not_ a new accounting line.
            // If it is a new accounting line, all is well. But if it isn't then this might be a case where approve is called
            // and we need to run validation with proper errorPath for that.
            if (accountingLine.getSequenceNumber() != null) {
                fixErrorPath(transactionalDocument, accountingLine);
            }

            LOG.debug("beginning object code validation ");
            allow = validateObjectCode(transactionalDocument, accountingLine);

            LOG.debug("beginning account number validation ");
            allow = allow & validateAccountNumber(transactionalDocument, accountingLine);

            LOG.debug("end validating accounting line, has errors: " + allow);
        }

        return allow;
    }

    /**
     * This method performs business rule checks on the accounting line being updated to the document to ensure the accounting line
     * is valid and appropriate for the document.  Only target lines can be changed, so we only need to validate them.
     * 
     * @param transactionalDocument The document the accounting line being updated resides within.
     * @param accountingLine The original accounting line.
     * @param updatedAccoutingLine The updated version of the accounting line.
     * @return True if the business rules all pass for the update, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomUpdateAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomUpdateAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine, AccountingLine updatedAccountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, updatedAccountingLine);
    }

    /**
     * This method performs business rule checks on the accounting line being provided to ensure the accounting line
     * is valid and appropriate for the document.  Only target lines can be changed, so we only need to validate them.
     * 
     * @param transactionalDocument The document associated with the accounting line being validated.
     * @param accountingLine The accounting line being validated.
     * @return True if the business rules all pass, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processCustomReviewAccountingLineBusinessRules(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean processCustomReviewAccountingLineBusinessRules(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        return processCustomAddAccountingLineBusinessRules(transactionalDocument, accountingLine);
    }

    /**
     * This method validates the object code for a given accounting line by checking the  object codes restrictions, 
     * including any restrictions in parameters table.  Additional validation checks include:
     * <ul>
     * <li>Confirm object code is not null.</li>
     * <li>Confirm object code is active.</li>
     * <li>Confirm object code is permitted for the list of merchant category codes.</li>
     * <li>Confirm object code sub type is permitted for the list of merchant category codes.</li>
     * </ul>
     * 
     * @param transactionalDocument The transaction document to retrieve transactions from for validation.
     * @param accountingLine The accounting line containing the object code to be validated.
     * @return True if the object code is valid and permitted for this type of transaction, false otherwise.
     */
    public boolean validateObjectCode(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
        boolean objectCodeAllowed = true;

        /* object code exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        /* make sure object code is active */
        if (!accountingLine.getObjectCode().isFinancialObjectActiveCode()) {
            errors.putError(errorKey, KFSKeyConstants.ERROR_INACTIVE, "object code");
            objectCodeAllowed = false;
        }

        /* get merchant category code (mcc) restriction from transaction */
        String mccRestriction = "";
        ProcurementCardTargetAccountingLine line = (ProcurementCardTargetAccountingLine) accountingLine;
        List pcTransactions = pcDocument.getTransactionEntries();
        for (Iterator iter = pcTransactions.iterator(); iter.hasNext();) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iter.next();
            if (transactionEntry.getFinancialDocumentTransactionLineNumber().equals(line.getFinancialDocumentTransactionLineNumber())) {
                mccRestriction = transactionEntry.getProcurementCardVendor().getTransactionMerchantCategoryCode();
            }
        }

        if (StringUtils.isBlank(mccRestriction)) {
            return objectCodeAllowed;
        }

        /* check object code is in permitted list for merchant category code (mcc) */
        if (objectCodeAllowed) {
            ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(ProcurementCardDocument.class, ProcurementCardDocumentRuleConstants.VALID_OBJECTS_BY_MCC_CODE_PARM_NM, ProcurementCardDocumentRuleConstants.INVALID_OBJECTS_BY_MCC_CODE_PARM_NM, mccRestriction, accountingLine.getFinancialObjectCode());
            objectCodeAllowed = evaluator.evaluateAndAddError(SourceAccountingLine.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }

        /* check object sub type is in permitted list for merchant category code (mcc) */
        if (objectCodeAllowed) {
            ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(ProcurementCardDocument.class, ProcurementCardDocumentRuleConstants.VALID_OBJ_SUB_TYPE_BY_MCC_CODE_PARM_NM, ProcurementCardDocumentRuleConstants.INVALID_OBJ_SUB_TYPE_BY_MCC_CODE_PARM_NM, mccRestriction, accountingLine.getObjectCode().getFinancialObjectSubTypeCode());
            objectCodeAllowed = evaluator.evaluateAndAddError(SourceAccountingLine.class, "objectCode.financialObjectSubTypeCode", KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        return objectCodeAllowed;
    }

    /**
     * This method validates the account number for a given accounting line by checking the  object codes restrictions, 
     * including any restrictions in parameters table.  
     * 
     * @param transactionalDocument The transaction document to retrieve transactions from for validation.
     * @param accountingLine The accounting line containing the account number to be validated.
     * @return True if the account number is valid and permitted for this type of transaction, false otherwise.
     */
    public boolean validateAccountNumber(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) transactionalDocument;
        ErrorMap errors = GlobalVariables.getErrorMap();

        String errorKey = KFSPropertyConstants.ACCOUNT_NUMBER;
        boolean accountNumberAllowed = true;

        /* account exist and object exist done in super, check we have a valid object */
        if (ObjectUtils.isNull(accountingLine.getAccount()) || ObjectUtils.isNull(accountingLine.getObjectCode())) {
            return false;
        }

        return accountNumberAllowed;
    }

    /**
     * Overrides FinancialDocumentRuleBase.isDocumentBalanceValid() and changes the default debit/credit comparison 
     * to checking the target total against the total balance. If they don't balance, and error message is produced 
     * that is more appropriate for procurement card documents.
     * 
     * @param transactionalDocument The document balance will be retrieved from.
     * @return True if the document is balanced, false otherwise.
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument transactionalDocument) {
        ProcurementCardDocument pcDocument = (ProcurementCardDocument) transactionalDocument;

        KualiDecimal targetTotal = pcDocument.getTargetTotal();
        KualiDecimal sourceTotal = pcDocument.getSourceTotal();

        boolean isValid = targetTotal.compareTo(sourceTotal) == 0;

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_BALANCE_CONSIDERING_SOURCE_AND_TARGET_AMOUNTS, new String[] { sourceTotal.toString(), targetTotal.toString() });
        }

        List<ProcurementCardTransactionDetail> pcTransactionEntries = pcDocument.getTransactionEntries();

        for (ProcurementCardTransactionDetail pcTransactionDetail : pcTransactionEntries) {
            isValid &= isTransactionBalanceValid(pcTransactionDetail);
        }

        return isValid;
    }

    /**
     * This method validates the balance of the transaction given.  A procurement card transaction is in balance if 
     * the total amount of the transaction equals the total of the target accounting lines corresponding to the transaction.
     * 
     * @param pcTransaction The transaction detail used to retrieve the procurement card transaction and target accounting 
     *                      lines used to check for in balance.
     * @return True if the amounts are equal and the transaction is in balance, false otherwise.
     */
    protected boolean isTransactionBalanceValid(ProcurementCardTransactionDetail pcTransactionDetail) {
        boolean inBalance = true;
        KualiDecimal transAmount = pcTransactionDetail.getTransactionTotalAmount();
        List<ProcurementCardTargetAccountingLine> targetAcctingLines = pcTransactionDetail.getTargetAccountingLines();

        KualiDecimal targetLineTotal = new KualiDecimal(0.00);

        for (TargetAccountingLine targetLine : targetAcctingLines) {
            targetLineTotal = targetLineTotal.add(targetLine.getAmount());
        }

        // perform absolute value check because current system has situations where amounts may be opposite in sign
        // This will no longer be necessary following completion of KULFDBCK-1290
        inBalance = transAmount.abs().equals(targetLineTotal.abs());

        if (!inBalance) {
            GlobalVariables.getErrorMap().putError(ACCOUNTING_LINE_ERRORS, ERROR_DOCUMENT_PC_TRANSACTION_TOTAL_ACCTING_LINE_TOTAL_NOT_EQUAL, new String[] { transAmount.toString(), targetLineTotal.toString() });
        }

        return inBalance;
    }

    /**
     * This method determines if an account associated with the given accounting line is accessible (ie. editable).  
     * 
     * This method performs an additional check by looking at the status of the document passed in if the 
     * document is 'enroute' and there is an active route node equal to RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT, 
     * then the account is declared accessible.  If the prior criteria are not met, then the method simply calls the super 
     * method and returns the results.  
     * 
     * @param transactionalDocument The document the accounting line is located in.
     * @param accountingLine The accounting line which contains the account to be analyzed.
     * @return True if the document is 'enroute' and will pass through the account review full edit node or if the super 
     *         method returns true, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#accountIsAccessible(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    protected boolean accountIsAccessible(AccountingDocument transactionalDocument, AccountingLine accountingLine) {
        KualiWorkflowDocument workflowDocument = transactionalDocument.getDocumentHeader().getWorkflowDocument();
        List activeNodes = null;
        try {
            activeNodes = Arrays.asList(workflowDocument.getNodeNames());
        }
        catch (WorkflowException e) {
            LOG.error("Error getting active nodes " + e.getMessage());
            throw new RuntimeException("Error getting active nodes " + e.getMessage());
        }

        if (workflowDocument.stateIsEnroute() && activeNodes.contains(RouteLevelNames.ACCOUNT_REVIEW_FULL_EDIT)) {
            return true;
        }

        return super.accountIsAccessible(transactionalDocument, accountingLine);
    }

    /**
     * This method validates an amount for an accounting line given.  The following checks are performed to ensure 
     * validity:
     * <ul>
     * <li>Checks that an amount is not zero.</li>
     * </ul>
     * 
     * @param document The document that the accounting line being validated is contained within.
     * @param accountingLine The accountingline the amount will be retrieved from.
     * @return True if the amount is not zero, false otherwise.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isAmountValid(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        // Check for zero
        if (ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(AMOUNT_PROPERTY_NAME, ERROR_ZERO_AMOUNT, "an accounting line");
            LOG.info("failing isAmountValid - zero check");
            return false;
        }

        return true;
    }

    /**
     * This method is being overridden to avoid seeing ERROR_DOCUMENT_SINGLE_ACCOUNTING_LINE_SECTION_TOTAL_CHANGED 
     * error message on procurement card documents.  
     * 
     * @param propertyName The property name the error will be linked to.
     * @param persistedSourceLineTotal The total amount that has already been persisted to the database.
     * @param currentSourceLineTotal The new total amount being set.
     */
    @Override
    protected void buildTotalChangeErrorMessage(String propertyName, KualiDecimal persistedSourceLineTotal, KualiDecimal currentSourceLineTotal) {
        return;
    }

    /**
     * Fix the GlobalVariables.getErrorMap errorPath for how procurement card documents needs them in order 
     * to properly display errors on the interface.  This is different from other financial document accounting 
     * lines because instead procurement card documents have accounting lines insides of transactions. 
     * Hence the error path is slightly different.
     * 
     * @param financialDocument The financial document the errors will be posted to.
     * @param accountingLine The accounting line the error will be posted on.
     */
    private void fixErrorPath(AccountingDocument financialDocument, AccountingLine accountingLine) {
        List transactionEntries = ((ProcurementCardDocument) financialDocument).getTransactionEntries();
        ProcurementCardTargetAccountingLine targetAccountingLineToBeFound = (ProcurementCardTargetAccountingLine) accountingLine;

        String errorPath = KFSPropertyConstants.DOCUMENT;

        // originally I used getFinancialDocumentTransactionLineNumber to determine the appropriate transaction, unfortunately
        // this makes it dependent on the order of transactionEntries in FP_PRCRMNT_DOC_T. Hence we have two loops below.
        boolean done = false;
        int transactionLineIndex = 0;
        for (Iterator iterTransactionEntries = transactionEntries.iterator(); !done && iterTransactionEntries.hasNext(); transactionLineIndex++) {
            ProcurementCardTransactionDetail transactionEntry = (ProcurementCardTransactionDetail) iterTransactionEntries.next();

            // Loop over the transactionEntry to find the accountingLine's location. Keep another counter handy.
            int accountingLineCounter = 0;
            for (Iterator iterTargetAccountingLines = transactionEntry.getTargetAccountingLines().iterator(); !done && iterTargetAccountingLines.hasNext(); accountingLineCounter++) {
                ProcurementCardTargetAccountingLine targetAccountingLine = (ProcurementCardTargetAccountingLine) iterTargetAccountingLines.next();

                if (targetAccountingLine.getSequenceNumber().equals(targetAccountingLineToBeFound.getSequenceNumber())) {
                    // Found the item, capture error path, and set boolean (break isn't enough for 2 loops).
                    errorPath = errorPath + "." + KFSPropertyConstants.TRANSACTION_ENTRIES + "[" + transactionLineIndex + "]." + KFSPropertyConstants.TARGET_ACCOUNTING_LINES + "[" + accountingLineCounter + "]";
                    done = true;
                }
            }
        }

        if (!done) {
            LOG.warn("fixErrorPath failed to locate item accountingLine=" + accountingLine.toString());
        }

        // Clearing the error path is not a universal solution but should work for PCDO. In this case it's the only choice
        // because KualiRuleService.applyRules will miss to remove the previous transaction added error path (only this
        // method knows how it is called).
        ErrorMap errorMap = GlobalVariables.getErrorMap();
        errorMap.clearErrorPath();
        errorMap.addToErrorPath(errorPath);
    }
}