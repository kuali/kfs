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
package org.kuali.module.purap.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.purap.bo.PurchasingApItem;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.AddPurchasingAccountsPayableItemRule;

public class PurchasingAccountsPayableDocumentRuleBase extends TransactionalDocumentRuleBase implements AddPurchasingAccountsPayableItemRule {

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
        return isValid &= processValidation(purapDocument);
    }

    //TODO should we call our validation here?
//    @Override
//    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
//        boolean isValid = true;
//        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) document;
//        return isValid &= processValidation(purapDocument);
//    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) approveEvent.getDocument();
        return isValid &= processValidation(purapDocument);
    }

    /**
     * This method calls each tab specific validation.  Tabs included on all PURAP docs are:
     *   DocumentOverview
     *   Vendor
     *   Item
     * 
     * @param purapDocument
     * @return
     */
    public boolean processValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        valid &= processDocumentOverviewValidation(purapDocument);
        valid &= processVendorValidation(purapDocument);
        valid &= processItemValidation(purapDocument);
        return valid;
    }

    /**
     * This method performs any validation for the Document Overview tab.
     * 
     * @param purapDocument
     * @return
     */
    public boolean processDocumentOverviewValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    /**
     * This method performs any validation for the Vendor tab.
     * 
     * @param purapDocument
     * @return
     */
    public boolean processVendorValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    /**
     * This method performs any validation for the Item tab.
     * 
     * @param purapDocument
     * @return
     */
    public boolean processItemValidation(PurchasingAccountsPayableDocument purapDocument) {
        boolean valid = true;
        // TODO code validation
        return valid;
    }

    public boolean processAddItemBusinessRules(AccountingDocument financialDocument, PurchasingApItem item) {
        // TODO Auto-generated method stub
        return true;
    }

}
