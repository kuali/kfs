/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.Check;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyBase;
import org.kuali.kfs.fp.document.service.CashReceiptService;
import org.kuali.kfs.fp.document.validation.AddCheckRule;
import org.kuali.kfs.fp.document.validation.DeleteCheckRule;
import org.kuali.kfs.fp.document.validation.UpdateCheckRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * Business rule(s) applicable to CashReceipt documents.
 */
public class CashReceiptDocumentRule extends CashReceiptFamilyRule implements AddCheckRule, DeleteCheckRule, UpdateCheckRule {
    /**
     * Implements Cash Receipt specific rule checks for the cash reconciliation section, to make sure that the cash, check, and coin
     * totals are not negative.
     * 
     * @param document submitted cash receipt document
     * @return true if cash, check, and coin totals are not negative
     * 
     * @see org.kuali.rice.kns.rule.DocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.Document)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomSaveDocumentBusinessRules(document);

        if (isValid) {
            isValid &= validateAccountingLineTotal((CashReceiptFamilyBase) document);
            isValid &= !SpringContext.getBean(CashReceiptService.class).areCashTotalsInvalid((CashReceiptDocument) document);
            isValid &= validateAccountAndObjectCodeAllLines((CashReceiptFamilyBase) document);
        }

        return isValid;
    }

    /**
     * Returns true if account and object code are valid in an accounting line.
     * 
     * @param financialDocument submitted accounting document 
     * @param accountingLine
     * @return true if account and object code are valid in an accounting line.
     * 
     * @see org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleBase#processCustomAddAccountingLineBusinessRules(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine)
     */
    @Override
    protected boolean processCustomAddAccountingLineBusinessRules(AccountingDocument financialDocument, AccountingLine accountingLine) {
        boolean isValid = true;
        isValid &= validateAccountAndObjectCode(accountingLine, accountingLine.isSourceAccountingLine(), true, 0);
        return isValid;
    }


    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @param financialDocument submitted financial document
     * @param check check added to cash receipt document
     * @return true if check is valid (i.e. non-zero and not negative)
     * 
     * @see org.kuali.rice.kns.rule.AddCheckRule#processAddCheckBusinessRules(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.kfs.fp.businessobject.Check)
     */
    public boolean processAddCheckBusinessRules(AccountingDocument financialDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * Default implementation does nothing now.
     * 
     * @param financialDocument submitted financial document
     * @param check check deleted from cash receipt document
     * @return true 
     * 
     * @see org.kuali.rice.kns.rule.DeleteCheckRule#processDeleteCheckBusinessRules(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.kfs.fp.businessobject.Check)
     */
    public boolean processDeleteCheckBusinessRules(AccountingDocument financialDocument, Check check) {
        boolean processed = true;

        return processed;
    }

    /**
     * Checks to make sure that the check passed in passes all data dictionary validation and that the amount is positive.
     * 
     * @param financialDocument submitted financial document
     * @param check check updated from cash receipt document
     * @return true if updated check is valid (i.e. non-zero and not negative)
     * 
     * @see org.kuali.rice.kns.rule.UpdateCheckRule#processUpdateCheckRule(org.kuali.rice.kns.document.FinancialDocument,
     *      org.kuali.kfs.fp.businessobject.Check)
     */
    public boolean processUpdateCheckRule(AccountingDocument financialDocument, Check check) {
        boolean isValid = validateCheck(check);

        return isValid;
    }

    /**
     * This method validates checks for a CR document.
     * 
     * @param check validated check
     * @return true if check is non-zero and not negative
     */
    private boolean validateCheck(Check check) {
        // validate the specific check coming in
        SpringContext.getBean(DictionaryValidationService.class).validateBusinessObject(check);

        boolean isValid = GlobalVariables.getErrorMap().isEmpty();

        // check to make sure the amount is also valid
        if (check.getAmount().isZero()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHECK_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_ZERO_CHECK_AMOUNT, KFSPropertyConstants.CHECKS);
            isValid = false;
        }
        else if (check.getAmount().isNegative()) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.CHECK_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_NEGATIVE_CHECK_AMOUNT, KFSPropertyConstants.CHECKS);
            isValid = false;
        }

        return isValid;
    }

    /**
     * Method used by <code>{@link org.kuali.kfs.fp.document.service.CashReceiptCoverSheetService}</code> to determine of the
     * <code>{@link CashReceiptDocument}</code> validates business rules for generating a cover page. <br/> <br/> Rule is the
     * <code>{@link Document}</code> must be ENROUTE.
     * 
     * @param document submitted cash receipt document
     * @return true if state is not cancelled, initiated, disapproved, saved, or exception
     */
    public boolean isCoverSheetPrintable(CashReceiptFamilyBase document) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        return !(workflowDocument.stateIsCanceled() || workflowDocument.stateIsInitiated() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsException() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsSaved());
    }

    /**
     * This method validates all the accounting lines for the right account/object code pairings, if the account is a sales tax
     * account
     * 
     * @param document submitted cash receipt document
     * @return true if all accounting lines have valid account and object codes
     */
    private boolean validateAccountAndObjectCodeAllLines(AccountingDocument document) {
        boolean isValid = true;
        List<AccountingLine> sourceLines = document.getSourceAccountingLines();
        List<AccountingLine> targetLines = document.getTargetAccountingLines();
        int index = 0;
        for (AccountingLine accountingLine : sourceLines) {
            boolean source = false;
            source = accountingLine.isSourceAccountingLine();
            validateAccountAndObjectCode(accountingLine, source, false, index);
            index++;
        }

        return isValid;
    }

    /**
     * This method processes the accounting line to make sure if a sales tax account is used the right object code is used with it
     * 
     * @param accountingLine accounting line from accounting document
     * @param source true if accounting line is a source line
     * @param newLine true if new line
     * @param index index of accounting line
     * @return true if accounting line has a non-empty sales tax account account and a corresponding non-empty object code 
     */
    private boolean validateAccountAndObjectCode(AccountingLine accountingLine, boolean source, boolean newLine, int index) {
        boolean isValid = true;
        // not evaluating, just want to retrieve the evaluator to get the values of the parameter
        // get the object code and account
        String objCd = accountingLine.getFinancialObjectCode();
        String account = accountingLine.getAccountNumber();
        if (!StringUtils.isEmpty(objCd) && !StringUtils.isEmpty(account)) {
            String[] params = SpringContext.getBean(ParameterService.class).getParameterValues(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES).toArray(new String[] {});
            boolean acctsMatched = false;
            for (int i = 0; i < params.length; i++) {
                String paramAcct = params[i].split(":")[0];
                if (account.equalsIgnoreCase(paramAcct)) {
                    acctsMatched = true;
                }
            }
            if (acctsMatched) {
                String compare = account + ":" + objCd;
                ParameterEvaluator evaluator = SpringContext.getBean(ParameterService.class).getParameterEvaluator(KfsParameterConstants.FINANCIAL_PROCESSING_DOCUMENT.class, APPLICATION_PARAMETER.SALES_TAX_APPLICABLE_ACCOUNTS_AND_OBJECT_CODES, compare);
                if (!evaluator.evaluationSucceeds()) {
                    isValid = false;
                }
            }

        }
        if (!isValid) {
            String pathPrefix = "";
            if (source && !newLine) {
                pathPrefix = "document." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
            }
            else if (!source && !newLine) {
                pathPrefix = "document." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";
            }
            else if (source && newLine) {
                pathPrefix = KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME;
            }
            else if (!source && newLine) {
                pathPrefix = KFSConstants.NEW_TARGET_ACCT_LINE_PROPERTY_NAME;
            }
            GlobalVariables.getErrorMap().addToErrorPath(pathPrefix);

            GlobalVariables.getErrorMap().putError("accountNumber", ERROR_DOCUMENT_ACCOUNTING_LINE_INVALID_ACCT_OBJ_CD, account, objCd);

            GlobalVariables.getErrorMap().removeFromErrorPath(pathPrefix);
        }
        return isValid;
    }
}
