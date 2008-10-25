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
package org.kuali.kfs.module.purap.document.authorization;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * Document Authorizer for the PO Amendment document.
 */
public class PurchaseOrderAmendmentDocumentAuthorizer extends PurchaseOrderDocumentAuthorizer {

    /**
     * @see org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizer#getEditMode(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person, java.util.List, java.util.List)
     */
    @Override
    public Map getEditMode(Document d, Person u, List sourceAccountingLines, List targetAccountingLines) {
        Map editModeMap = new HashMap();
        String editMode = AuthorizationConstants.EditMode.VIEW_ONLY;

        KualiWorkflowDocument workflowDocument = d.getDocumentHeader().getWorkflowDocument();
        if (((PurchasingAccountsPayableDocument) d).getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.CHANGE_IN_PROCESS) && (workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved())
             && super.hasInitiateAuthorization(d, u))  {
            editMode = PurapAuthorizationConstants.PurchaseOrderEditMode.AMENDMENT_ENTRY;
        }
        editModeMap.put(editMode, "TRUE");

        if(SpringContext.getBean(PurapService.class).isDocumentStoppedInRouteNode((PurchasingAccountsPayableDocument)d, "New Unordered Items")){
            editModeMap.put(PurapAuthorizationConstants.PurchaseOrderEditMode.UNORDERED_ITEM_ACCOUNT_ENTRY, "TRUE");
        }
        
        return editModeMap;
    }
    
}

