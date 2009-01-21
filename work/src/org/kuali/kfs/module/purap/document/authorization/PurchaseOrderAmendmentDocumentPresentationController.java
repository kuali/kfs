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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.Document;


public class PurchaseOrderAmendmentDocumentPresentationController extends PurchaseOrderDocumentPresentationController {
    
    @Override
    protected boolean canSave(Document document) {
        return false;
    }

    @Override
    public Set<String> getEditModes(Document document) {
        Set<String> editModes = super.getEditModes(document);
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;

        if (PurchaseOrderStatuses.CHANGE_IN_PROCESS.equals(poDocument.getStatusCode())) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.AMENDMENT_ENTRY);
        }

        if (SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument) document, "New Unordered Items")) {
            editModes.add(PurapAuthorizationConstants.PurchaseOrderEditMode.UNORDERED_ITEM_ACCOUNT_ENTRY);
        }
        
        return editModes;
    }

}
