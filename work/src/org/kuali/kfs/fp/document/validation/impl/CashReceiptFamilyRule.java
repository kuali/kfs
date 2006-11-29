/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.KeyConstants.CashReceipt;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.ERROR_PATH.DOCUMENT_ERROR_PREFIX;

/**
 * Business rule(s) shared amongst to CashReceipt-related documents.
 * 
 * 
 */
public class CashReceiptFamilyRule extends TransactionalDocumentRuleBase implements CashReceiptDocumentRuleConstants {

    /**
     * Cash Receipt documents allow both positive and negative values, so we only need to check for zero amounts.
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isAmountValid(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isAmountValid(TransactionalDocument document, AccountingLine accountingLine) {
        KualiDecimal amount = accountingLine.getAmount();

        if (Constants.ZERO.compareTo(amount) == 0) { // amount == 0
            GlobalVariables.getErrorMap().putError(Constants.AMOUNT_PROPERTY_NAME, KeyConstants.ERROR_ZERO_AMOUNT, "an accounting line");
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

            String unitName = SpringServiceLocator.getCashReceiptService().getCashReceiptVerificationUnitForCampusCode(crd.getCampusLocationCode());
            CashDrawer cd = SpringServiceLocator.getCashDrawerService().getByWorkgroupName(unitName, false);
            if (cd == null) {
                throw new IllegalStateException("There is no cash drawer associated with unitName '" + unitName + "' from cash receipt " + crd.getDocumentNumber());
            }
            else if (cd.isClosed()) {
                GlobalVariables.getErrorMap().putError(Constants.GLOBAL_ERRORS, KeyConstants.CashReceipt.MSG_CASH_DRAWER_CLOSED_VERIFICATION_NOT_ALLOWED, cd.getWorkgroupName());
                valid = false;
            }
        }

        return valid;
    }

    /**
     * For Cash Receipt documents, the document is balanced if the sum total of checks and cash and coin equals the sum total of the
     * accounting lines. In addition, the sum total of checks and cash and coin must be greater than zero.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(TransactionalDocument transactionalDocument) {
        CashReceiptFamilyBase cr = (CashReceiptFamilyBase) transactionalDocument;

        // make sure that cash reconciliation total is greater than zero
        boolean isValid = cr.getSumTotalAmount().compareTo(Constants.ZERO) > 0;
        if (!isValid) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + PropertyConstants.SUM_TOTAL_AMOUNT, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_NO_CASH_RECONCILIATION_TOTAL);
        }

        if (isValid) {
            // make sure the document is in balance
            isValid = cr.getSourceTotal().compareTo(cr.getSumTotalAmount()) == 0;

            if (!isValid) {
                GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + PropertyConstants.SUM_TOTAL_AMOUNT, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_BALANCE);
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
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING, RESTRICTED_OBJECT_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            if (rule.failsRule(objectCode.getFinancialObjectTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_TYPE_CODE_FOR_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectTypeCode() });
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
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING, RESTRICTED_CONSOLIDATED_OBJECT_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            ObjLevel objectLevel = objectCode.getFinancialObjectLevel();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            String consolidatedObjectCode = objectLevel.getConsolidatedObjectCode();

            if (rule.failsRule(consolidatedObjectCode)) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_CONSOLIDATED_OBJECT_CODE, new String[] { objectCode.getFinancialObjectCode(), objectLevel.getFinancialObjectLevelCode(), consolidatedObjectCode });
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
            KualiParameterRule rule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(KUALI_TRANSACTION_PROCESSING_CASH_RECEIPT_SECURITY_GROUPING, RESTRICTED_OBJECT_SUB_TYPE_CODES);

            ObjectCode objectCode = accountingLine.getObjectCode();
            if (ObjectUtils.isNull(objectCode)) {
                accountingLine.refreshReferenceObject(PropertyConstants.OBJECT_CODE);
            }

            if (rule.failsRule(objectCode.getFinancialObjectSubTypeCode())) {
                valid = false;

                // add message
                GlobalVariables.getErrorMap().putError(PropertyConstants.FINANCIAL_OBJECT_CODE, KeyConstants.CashReceipt.ERROR_DOCUMENT_CASH_RECEIPT_INVALID_OBJECT_SUB_TYPE_CODE, new String[] { objectCode.getFinancialObjectCode(), objectCode.getFinancialObjectSubTypeCode() });
            }
        }

        return valid;
    }

    /**
     * Cash receipt documents do not utilize the target accounting line list. A CR doc is one sided, so this method should always
     * return true.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isTargetAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isTargetAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        return true;
    }

    /**
     * Cash receipt documents need at least one accounting line. Had to override to supply a Cash Receipt specific method.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#isSourceAccountingLinesRequiredNumberForRoutingMet(org.kuali.core.document.TransactionalDocument)
     */
    @Override
    protected boolean isSourceAccountingLinesRequiredNumberForRoutingMet(TransactionalDocument transactionalDocument) {
        if (0 == transactionalDocument.getSourceAccountingLines().size()) {
            GlobalVariables.getErrorMap().putError(DOCUMENT_ERROR_PREFIX + PropertyConstants.SOURCE_ACCOUNTING_LINES, KeyConstants.ERROR_DOCUMENT_SINGLE_SECTION_NO_ACCOUNTING_LINES);
            return false;
        }
        else {
            return true;
        }
    }

    /**
     * Overrides to set the entry's description to the description from the accounting line, if a value exists.
     * 
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#customizeExplicitGeneralLedgerPendingEntry(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void customizeExplicitGeneralLedgerPendingEntry(TransactionalDocument transactionalDocument, AccountingLine accountingLine, GeneralLedgerPendingEntry explicitEntry) {
        String accountingLineDescription = accountingLine.getFinancialDocumentLineDescription();
        if (StringUtils.isNotBlank(accountingLineDescription)) {
            explicitEntry.setTransactionLedgerEntryDescription(accountingLineDescription);
        }
    }

    /**
     * @see IsDebitUtils#isDebitConsideringType(TransactionalDocumentRuleBase, TransactionalDocument, AccountingLine)
     * 
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.TransactionalDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    public boolean isDebit(TransactionalDocument transactionalDocument, AccountingLine accountingLine) {
        // error corrections are not allowed
        IsDebitUtils.disallowErrorCorrectionDocumentCheck(this, transactionalDocument);
        return IsDebitUtils.isDebitConsideringType(this, transactionalDocument, accountingLine);
    }


    /**
     * @param crdoc
     * @return true if the sum of the accountingLine values is non-zero
     */
    protected boolean validateAccountingLineTotal(CashReceiptFamilyBase crdoc) {
        boolean isValid = true;

        if (crdoc.getSourceTotal().isZero()) {
            String errorProperty = DOCUMENT_ERROR_PREFIX + PropertyConstants.SOURCE_ACCOUNTING_LINES;

            isValid = false;
            GlobalVariables.getErrorMap().putError(errorProperty, CashReceipt.ERROR_ZERO_TOTAL, "Accounting Line Total");
        }

        return isValid;
    }
}
