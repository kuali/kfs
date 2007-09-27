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

import static org.kuali.kfs.rules.AccountingDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSKeyConstants.CashReceipt;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rules.AccountingDocumentRuleBase;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashReceiptService;

/**
 * Business rule(s) shared amongst to CashReceipt-related documents.
 */
public class CashReceiptFamilyRule extends AccountingDocumentRuleBase implements CashReceiptDocumentRuleConstants {

    /**
     * Cash Receipt documents allow both positive and negative values, so we only need to check for zero amounts.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(AccountingDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        if (KFSConstants.ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
            return false;
        }

        return true;
    }

    /**
     * This overrides to call super, then to make sure that the cash drawer for the verification unit associated with this CR doc is
     * open. If it's not, the the rule fails.
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean valid = super.processCustomApproveDocumentBusinessRules(approveEvent);

        if (valid) {
            CashReceiptFamilyBase crd = (CashReceiptFamilyBase) approveEvent.getDocument();

            String unitName = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForCampusCode(crd.getCampusLocationCode());
            CashDrawer cd = SpringContext.getBean(CashDrawerService.class).getByWorkgroupName(unitName, false);
            if (cd == null) {
                throw new IllegalStateException("There is no cash drawer associated with unitName '" + unitName + "' from cash receipt " + crd.getDocumentNumber());
            }
            else if (cd.isClosed()) {
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.CashReceipt.MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED, cd.getWorkgroupName());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * For Cash Receipt documents, the document is balanced if the sum total of checks and cash and coin equals the sum total of the
     * accounting lines. In addition, the sum total of checks and cash and coin must be greater than zero.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument FinancialDocument) {
        CashReceiptFamilyBase cr = (CashReceiptFamilyBase) FinancialDocument;

        // make sure that cash reconciliation total is greater than zero
        boolean isValid = cr.getTotalDollarAmount().compareTo(KFSConstants.ZERO) > 0;
        if (!isValid) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SUM_TOTAL_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL);
        }

        if (isValid) {
            // make sure the document is in balance
            isValid = cr.getSourceTotal().compareTo(cr.getTotalDollarAmount()) == 0;

            if (!isValid) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SUM_TOTAL_AMOUNT, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_BALANCE);
            }
        }

        return isValid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to CashReceipt specific rules. This method leverages
     * the APC for checking restricted object type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectTypeAllowed(accountingLine);

        if (valid) {
            Parameter rule = getKualiConfigurationService().getParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.CASH_RECEIPT_DOC, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            }

            if (getKualiConfigurationService().failsRule(rule,objectCode.getFinancialObjectTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectTypeCode() });
            }
        }

        return valid;
    }

    /**
     * Overrides to validate specific object codes for the Cash Receipt document. This method leverages the APC for checking
     * restricted object consolidation values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectConsolidationAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectConsolidationAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectConsolidationAllowed(accountingLine);

        if (valid) {
            Parameter rule = getKualiConfigurationService().getParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.CASH_RECEIPT_DOC, RESTRICTED_CONSOLIDATED_OBJECT_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            }

            ObjLevel objectLevel = objectCode.getFinancialObjectLevel();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            }

            String consolidatedObjectCode = objectLevel.getConsolidatedObjectCode();

            if (getKualiConfigurationService().failsRule(rule,consolidatedObjectCode)) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_CONSOLIDATED_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectLevel.getFinancialObjectLevelCode(), consolidatedObjectCode });
            }
        }

        return valid;
    }

    /**
     * Overrides to perform the universal rule in the super class in addition to CashReceipt specific rules. This method leverages
     * the APC for checking restricted object sub type values.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isObjectSubTypeAllowed(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isObjectSubTypeAllowed(AccountingLine accountingLine) {
        boolean valid = true;

        valid &= super.isObjectSubTypeAllowed(accountingLine);

        if (valid) {
            Parameter rule = getKualiConfigurationService().getParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.CASH_RECEIPT_DOC, RESTRICTED_OBJECT_SUB_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE);
            }

            if (getKualiConfigurationService().failsRule(rule,objectCode.getFinancialObjectSubTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, KFSKeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
            }
        }

        return valid;
    }

    /**
     * Cash receipt documents do not utilize the target accounting line list. A CR doc is one sided, so this method should always
     * return true.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(AccountingDocument FinancialDocument) {
        return true;
    }

    /**
     * Cash receipt documents need at least one accounting line. Had to override to supply a Cash Receipt specific method.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(AccountingDocument FinancialDocument) {
        if (0 == FinancialDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides to set the entry's description to the description from the accounting line, if a value exists.
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    protected void customizeExplicitGeneralLedgerPendingEntry(AccountingDocument FinancialDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        String accountingLineDescription = accountingLine.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(accountingLineDescription)) {
            explicitEntry.setTransactionLedgerEntryDescription(accountingLineDescription);
        }
    }

    /**
     * @see IsDebitUtils#isDebitConsideringType(FinancialDocumentRuleBase, FinancialDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(AccountingDocument FinancialDocument, AccountingLine accountingLine) {
        // error corrections are not allowed
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, FinancialDocument);
        return IsDebitUtils.isDebitConsideringType(this, FinancialDocument, accountingLine);
    }


    /**
     * @param crdoc
     * @return true if the sum of the accountingLine values is non-zero
     */
    protected boolean validateAccountingLineTotal(CashReceiptFamilyBase crdoc) {
        boolean isValid = true;

        if (crdoc.getSourceTotal().isZero()) {
            String errorProperty = DOCUMENT_ERROR_PREFIX + KFSPropertyConstants.SOURCE_ACCOUNTING_LINES;

            isValid = false;
            GlobalVariables.getErrorMap().putError(errorProperty, CashReceipt.ERROR_ZERO_TOTAL, "Accounting Line Total");
        }

        return isValid;
    }
}
