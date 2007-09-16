/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.purap.rules;

import org.kuali.RiceConstants;
import org.kuali.RicePropertyConstants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.ItemFields;
import org.kuali.module.purap.PurapWorkflowConstants.PaymentRequestDocument.NodeDetailEnum;
import org.kuali.module.purap.bo.PaymentRequestItem;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.CalculateAccountsPayableRule;
import org.kuali.module.purap.rule.CancelAccountsPayableRule;
import org.kuali.module.purap.rule.ContinueAccountsPayableRule;

public abstract class AccountsPayableDocumentRuleBase extends PurchasingAccountsPayableDocumentRuleBase  implements ContinueAccountsPayableRule, CalculateAccountsPayableRule, CancelAccountsPayableRule {
    
    @Override
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processValidation(purapDocument);
        
        valid &= processApprovalAtAccountsPayableReviewAllowed((AccountsPayableDocument)purapDocument);
        
        return valid;
    }

    private boolean processApprovalAtAccountsPayableReviewAllowed(AccountsPayableDocument apDocument) {
        boolean valid = true;
        GlobalVariables.getErrorMap().clearErrorPath();
        GlobalVariables.getErrorMap().addToErrorPath(RicePropertyConstants.DOCUMENT);
        
        if (apDocument.isDocumentStoppedInRouteNode(NodeDetailEnum.ACCOUNTS_PAYABLE_REVIEW)) {
            if(!apDocument.approvalAtAccountsPayableReviewAllowed()) {
                valid &= false;
                GlobalVariables.getErrorMap().putError(RiceConstants.GLOBAL_ERRORS, PurapKeyConstants.ERROR_AP_REQUIRES_ATTACHMENT);
            }
        }
        GlobalVariables.getErrorMap().clearErrorPath();
        return valid;
    }

    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = super.processItemValidation(purapDocument);
       
        for (PurApItem item : purapDocument.getItems()) {
            String identifierString = (item.getItemType().isItemTypeAboveTheLineIndicator() ? "Item " + item.getItemLineNumber().toString() : item.getItemType().getItemTypeDescription());
            if (item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE)) {
                valid &= validateItemTypeItems((PaymentRequestItem) item, identifierString);
            }
        }
        
        return valid;
    }

    private boolean validateItemTypeItems(PaymentRequestItem item, String identifierString) {
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
     * @see org.kuali.module.purap.rule.CancelAccountsPayableRule#processCancelAccountsPayableBusinessRules(org.kuali.module.purap.document.AccountsPayableDocument)
     */
    public abstract boolean processCancelAccountsPayableBusinessRules(AccountsPayableDocument document);
}
