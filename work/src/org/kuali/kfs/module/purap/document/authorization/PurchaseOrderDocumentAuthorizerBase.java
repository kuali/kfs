/*
 * Copyright 2009 The Kuali Foundation.
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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.identity.PurapKimAttributes;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;
import org.kuali.rice.kns.workflow.service.WorkflowDocumentService;

public class PurchaseOrderDocumentAuthorizerBase extends PurchasingAccountsPayableTransactionalDocumentAuthorizerBase {

    /**
     * Overridden to prevent the FYI button from appearing when the user
     * is authorized to print the PO and the status is pending print.
     * @see org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase#getDocumentActions(org.kuali.rice.kns.document.Document, org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActions = super.getDocumentActions(document, user, documentActionsFromPresentationController);
        
        
        if (documentActions.contains(KNSConstants.KUALI_ACTION_CAN_FYI)) {
            if (!canFYI(document)) {
                documentActions.remove(KNSConstants.KUALI_ACTION_CAN_FYI);
            }
        }
        return documentActions;
    }
    
    private boolean canFYI(Document document) {
        boolean result = true;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)document;
        try {
            KualiWorkflowDocument workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).createWorkflowDocument(document.getDocumentHeader().getWorkflowDocument().getRouteHeaderId(), GlobalVariables.getUserSession().getPerson());
            result &= workflowDocument.isFYIRequested();
        }
        catch (WorkflowException we) {
            throw new RuntimeException (we);
        }
        if (result && PurchaseOrderStatuses.PENDING_PRINT.equals(poDocument.getStatusCode())) {
            return false;
        }
        return true;
    }
}
