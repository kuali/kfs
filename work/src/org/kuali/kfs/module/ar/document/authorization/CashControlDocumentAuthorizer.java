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
package org.kuali.module.ar.document.authorization;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.authorization.DocumentActionFlags;
import org.kuali.core.document.authorization.TransactionalDocumentAuthorizerBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;

import edu.iu.uis.eden.clientapp.vo.ValidActionsVO;

public class CashControlDocumentAuthorizer extends TransactionalDocumentAuthorizerBase {
    
    /**
     * @see org.kuali.core.document.DocumentAuthorizerBase#getDocumentActionFlags(org.kuali.core.document.Document,
     *      org.kuali.core.bo.user.KualiUser)
     */
    @Override
    public DocumentActionFlags getDocumentActionFlags(Document document, UniversalUser user) {
        DocumentActionFlags flags = super.getDocumentActionFlags(document, user);

        CashControlDocument ccDoc = (CashControlDocument) document;
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        ValidActionsVO validActions = workflowDocument.getRouteHeader().getValidActions();

        boolean atLeastOneAppDocProccessed = false;
        
        for(CashControlDetail cashControlDetail : ccDoc.getCashControlDetails())
        {
            PaymentApplicationDocument applicationDocument = cashControlDetail.getReferenceFinancialDocument();
            if( applicationDocument.getDocumentHeader().getWorkflowDocument().stateIsProcessed())
            {
                atLeastOneAppDocProccessed = true;
                break;
            }
        }
        
        if (atLeastOneAppDocProccessed) {
            flags.setCanDisapprove(false);
        }

        return flags;
    }

}
