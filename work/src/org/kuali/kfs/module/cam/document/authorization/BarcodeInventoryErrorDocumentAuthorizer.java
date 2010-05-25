/*
 * Copyright 2008-2009 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.document.authorization;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kew.actionrequest.ActionRequestValue;
import org.kuali.rice.kew.actionrequest.dao.ActionRequestDAO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.web.session.UserSession;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * Barcode Inventory Error Document Authorizer to edit.
 */
public class BarcodeInventoryErrorDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase {

    /**
     * Overridden to check if document error correction can be allowed here.
     * 
     * @see org.kuali.rice.kns.document.authorization.DocumentAuthorizerBase#getDocumentActions(org.kuali.rice.kns.document.Document,
     *      org.kuali.rice.kim.bo.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);

        Long routeHeaderId;    
        
        try {
             routeHeaderId = document.getDocumentHeader().getWorkflowDocument().getRouteHeaderId();
        } catch (WorkflowException wfe) {
            throw new RuntimeException("ParseException: BarcodeInventoryErrorDocumentAuthorizer stopped because routeHeaderId is invaid.");            
        }
        
        String principalId = UserSession.getAuthenticatedUser().getPrincipalId();
        
        if (document.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            //retrieve all future actions records sitting in table: KREW_ACTN_RQST_T
            List<ActionRequestValue> futureActions = SpringContext.getBean(ActionRequestDAO.class).findAllByDocId(routeHeaderId);
           
            for (Iterator<ActionRequestValue> futureAction = futureActions.iterator(); futureAction.hasNext();) {
                //if logged in principal id is same as the one in future actions record then add the edit permission
                // check jira: KFSMI-5698
                if (futureAction.next().getPrincipalId().equals(principalId)) {
                    documentActionsToReturn.add(KNSConstants.KUALI_ACTION_CAN_EDIT);
                }
            }
        }
            
        return documentActionsToReturn;
    }

}
