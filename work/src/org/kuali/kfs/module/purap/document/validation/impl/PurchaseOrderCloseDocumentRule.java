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
package org.kuali.module.purap.rules;

import org.kuali.core.document.Document;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rules.TransactionalDocumentRuleBase;
import org.kuali.module.purap.document.PurchaseOrderDocument;

public class PurchaseOrderCloseDocumentRule extends TransactionalDocumentRuleBase {
    
    /**
     * @see org.kuali.module.financial.rules.TransactionalDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.Document)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(poDocument);
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(Document document) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) document;
        return isValid &= processValidation(poDocument);
    }

    @Override
    protected boolean processCustomApproveDocumentBusinessRules(ApproveDocumentEvent approveEvent) {
        boolean isValid = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument) approveEvent.getDocument();
        return isValid;
    }

    private boolean processValidation(PurchaseOrderDocument document) {
        boolean valid = true;
        //return true for now, we need to check EPIC to see if there are any rules for this doc
        return valid;
    }
}
