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

import static org.kuali.kfs.KFSConstants.DOCUMENT_PROPERTY_NAME;

import java.util.HashMap;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.document.CashReceiptFamilyBase;
import org.kuali.module.financial.document.CreditCardReceiptDocument;

/**
 * Business rules applicable to Credit Card Receipt documents.
 */
public class CreditCardReceiptDocumentRule extends CashReceiptFamilyRule {
    /**
     * For Credit Card Receipt documents, the document is balanced if the sum total of credit card receipts equals the sum total of
     * the accounting lines.
     * 
     * @param financialDocument submitted accoutting document
     * @return true if cash credit receipt document equals the cash credit document total dollar amount
     * 
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#isDocumentBalanceValid(org.kuali.core.document.FinancialDocument)
     */
    @Override
    protected boolean isDocumentBalanceValid(AccountingDocument finanacialDocument) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) finanacialDocument;

        // make sure the document is in balance
        boolean isValid = ccr.getSourceTotal().equals(ccr.getTotalDollarAmount());

        if (!isValid) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT, KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_OUT_OF_BALANCE);
        }

        return isValid;
    }

    /**
     * Overrides to call super and then make sure the minimum number of credit card receipt lines exist on this document.
     * 
     * @param document submitted document
     * @return true if super method returns true and there is at least one credit card receipt
     * 
     * @see org.kuali.core.rule.DocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = super.processCustomRouteDocumentBusinessRules(document);

        isValid &= isMinimumNumberOfCreditCardReceiptsMet(document);

        if (isValid) {
            isValid &= validateAccountingLineTotal((CashReceiptFamilyBase) document);
            isValid &= !CreditCardReceiptDocumentRuleUtil.areCashTotalsInvalid((CreditCardReceiptDocument) document);
        }

        if (isValid) {
            isValid &= validateCreditCardReceipts((CreditCardReceiptDocument) document);
        }

        return isValid;
    }

    /**
     * This method is a helper that checks to make sure that at least one credit card receipt line exists for the document.
     * 
     * @param document submitted document
     * @return boolean return true if there is at least one credit card receipt
     */
    private boolean isMinimumNumberOfCreditCardReceiptsMet(Document document) {
        CreditCardReceiptDocument ccr = (CreditCardReceiptDocument) document;

        if (ccr.getCreditCardReceipts().size() == 0) {
            GlobalVariables.getErrorMap().putError(KFSPropertyConstants.NEW_CREDIT_CARD_RECEIPT, KFSKeyConstants.CreditCardReceipt.ERROR_DOCUMENT_CREDIT_CARD_RECEIPT_REQ_NUMBER_RECEIPTS_NOT_MET);
            return false;
        }
        return true;
    }

    /**
     * Validates all the CreditCardReceipts in the given Document.
     * 
     * @param creditCardReceiptDocument submitted credit card receipt document
     * @return true if all credit cards are valid (i.e. each credit card receipt has a non-zero amount and has valid 
     *         credit card vendor and type references) 
     */
    private boolean validateCreditCardReceipts(CreditCardReceiptDocument creditCardReceiptDocument) {
        GlobalVariables.getErrorMap().addToErrorPath(DOCUMENT_PROPERTY_NAME);
        boolean isValid = true;
        for (int i = 0; i < creditCardReceiptDocument.getCreditCardReceipts().size(); i++) {
            String propertyName = KFSPropertyConstants.CREDIT_CARD_RECEIPT + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(propertyName);
            isValid &= CreditCardReceiptDocumentRuleUtil.validateCreditCardReceipt(creditCardReceiptDocument.getCreditCardReceipt(i));
            GlobalVariables.getErrorMap().removeFromErrorPath(propertyName);
        }
        GlobalVariables.getErrorMap().removeFromErrorPath(DOCUMENT_PROPERTY_NAME);
        return isValid;
    }

    /**
     * We are overriding here and always returning true since we don't care about 
     * cash drawers for this doc type but want the other rules in the super class.
     * @see org.kuali.module.financial.rules.CashReceiptFamilyRule#processCustomApproveDocumentBusinessRules(org.kuali.core.rule.event.ApproveDocumentEvent)
     */
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        return true;
}
}
