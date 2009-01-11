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
package org.kuali.kfs.module.purap.document.validation.impl;

import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.ItemFields;
import org.kuali.kfs.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.validation.CalculateAccountsPayableRule;
import org.kuali.kfs.module.purap.document.validation.CancelAccountsPayableRule;
import org.kuali.kfs.module.purap.document.validation.ContinuePurapRule;
import org.kuali.kfs.module.purap.document.validation.PreCalculateAccountsPayableRule;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business Rule(s) applicable to Accounts Payable documents.
 */
public abstract class AccountsPayableDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase implements ContinuePurapRule, CalculateAccountsPayableRule, PreCalculateAccountsPayableRule, CancelAccountsPayableRule {

    /**
     * Determines if approval at accounts payable review is allowed.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);

        AccountsPayableDocument apDocument = (AccountsPayableDocument) purapDocument;
        valid &= processApprovalAtAccountsPayableReviewAllowed(apDocument);
        valid &= SpringContext.getBean(CapitalAssetBuilderModuleService.class).validateAccountsPayableData(apDocument);

        return valid;
    }

    /**
     * If at the node accounts payable review, checks if approval at accounts payable review is allowed.
     * Returns true if it is, false otherwise.
     * 
     * @param apDocument - accounts payable document
     * @return
     */
    private boolean processApprovalAtAccountsPayableReviewAllowed(AccountsPayableDocument apDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

        if (apDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW)) {
            if (!apDocument.approvalAtAccountsPayableReviewAllowed()) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_AP_REQUIRES_ATTACHMENT);
            }
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    /**
     * Performs additional validation on above the line indicator items.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#processItemValidation(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);

        for (PurApItem item : purapDocument.getItems()) {
            String identifierString = (item.getItemType().isLineItemIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
            if (item.getItemType().isLineItemIndicator()) {
                valid &= validateAboveTheLineItems((PaymentRequestItem) item, identifierString);
            }
        }

        return valid;
    }

    /**
     * Performs validation for above the line item types.
     * 
     * @param item - a payment request item to be validated
     * @param identifierString - identifier used to add to error map
     * @return
     */
    private boolean validateAboveTheLineItems(PaymentRequestItem item, String identifierString) {
        boolean valid = true;

        // Currently Invoice Unit Price is not allowed to be NULL on screen;
        // must be either a positive number or NULL for DB
        if (ObjectUtils.isNotNull(item.getItemUnitPrice()) && item.getItemUnitPrice().signum() == -1) {
            // if unit price is negative give an error
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.UNIT_COST, identifierString);
        }
        if (ObjectUtils.isNotNull(item.getExtendedPrice()) && item.getExtendedPrice().isNegative()) {
            valid = false;
            GlobalVariables.getErrorMap().putError(PurapConstants.ITEM_TAB_ERROR_PROPERTY, PurapKeyConstants.ERROR_ITEM_AMOUNT_BELOW_ZERO, ItemFields.INVOICE_EXTENDED_PRICE, identifierString);
        }
        return valid;
    }

    /**
     * @see org.kuali.kfs.module.purap.document.validation.CancelAccountsPayableRule#processCancelAccountsPayableBusinessRules(org.kuali.kfs.module.purap.document.AccountsPayableDocument)
     */
    public abstract boolean processCancelAccountsPayableBusinessRules(AccountsPayableDocument document);

}
