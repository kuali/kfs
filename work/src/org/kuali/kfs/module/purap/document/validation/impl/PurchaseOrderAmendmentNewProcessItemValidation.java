/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.List;

import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.document.AccountingDocument;

public class PurchaseOrderAmendmentNewProcessItemValidation extends PurchasingNewProcessItemValidation {

    private PurapService purapService;
    private PurchaseOrderService purchaseOrderService;
    
    /**
     * Overrides to disable account validation for PO amendments when there are new unordered items
     * added to the PO.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingAccountsPayableDocumentRuleBase#requiresAccountValidationOnAllEnteredItems(org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument)
     */
    @Override
    public boolean requiresAccountValidationOnAllEnteredItems(PurchasingAccountsPayableDocument document) {
                
        boolean requiresAccountValidation = false;
        
        //if a new unordered item has been added to the purchase order, this is due to Receiving Line document,
        // and items should not have account validation performed initially, until new unordered items review.
        if( purchaseOrderService.hasNewUnorderedItem((PurchaseOrderDocument)document) &&
            !purapService.isDocumentStoppedInRouteNode(document, "New Unordered Items") ){
            requiresAccountValidation = false;
        }else{
            requiresAccountValidation = super.requiresAccountValidationOnAllEnteredItems(document);
        }
        
        return requiresAccountValidation;
    }
    
    /**
     * Overrides the method in PurchasingDocumentRuleBase to provide additional
     * validation condition. If the accounts on the item are editable in the amendment document then
     * we should continue doing the processAccountValidation in the superclass, otherwise
     * we should just return true so that the account won't be validated, because if
     * the items contain accounts that aren't editable, it doesn't make sense to give
     * the user account validation errors.
     * 
     * @see org.kuali.kfs.module.purap.document.validation.impl.PurchasingDocumentRuleBase#processAccountValidation(org.kuali.kfs.sys.document.AccountingDocument, java.util.List, java.lang.String)
     */
    @Override
    public boolean processAccountValidation(AccountingDocument accountingDocument, List<PurApAccountingLine> purAccounts, String itemLineNumber) {
        PurchaseOrderDocument document = (PurchaseOrderDocument)accountingDocument;
        PurchaseOrderItem itemLine = (PurchaseOrderItem) document.getItemByStringIdentifier(itemLineNumber);
        if (itemLine.isItemActiveIndicator() && (! (document.getContainsUnpaidPaymentRequestsOrCreditMemos() && itemLine.getItemInvoicedTotalAmount() != null))) {
            //This means the accounts on the item are editable, so we'll call super's processAccountValidation.
            return super.processAccountValidation(accountingDocument, purAccounts, itemLineNumber);
        }
        else {
            //This means the accounts on the item are not editable, so we'll return true so that it won't do any further validations on the accounts.
            return true;
        }
    }
    
    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }    
     

}
