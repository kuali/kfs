/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.document.authorization;

import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Document Authorizer for the Effort Certification document.
 */
public class EffortCertificationDocumentAuthorizer extends FinancialSystemTransactionalDocumentAuthorizerBase { 

    /**
     * Overridden to check if document error correction can be allowed here.
     * 
     * @see org.kuali.rice.krad.document.authorization.DocumentAuthorizerBase#getDocumentActions(org.kuali.rice.krad.document.Document,
     *      org.kuali.rice.kim.api.identity.Person, java.util.Set)
     */
    @Override
    public Set<String> getDocumentActions(Document document, Person user, Set<String> documentActionsFromPresentationController) {
        Set<String> documentActionsToReturn = super.getDocumentActions(document, user, documentActionsFromPresentationController);
        
        String principalId = UserSession.getAuthenticatedUser().getPrincipalId();
        try {
            if (document.getDocumentHeader().getWorkflowDocument().isEnroute()) {
                Set<Person> priorApprovers = document.getDocumentHeader().getWorkflowDocument().getAllPriorApprovers();
                for (Person priorApprover : priorApprovers) {
                    if (principalId.equals(priorApprover.getPrincipalId())) {
                        documentActionsToReturn.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
                        documentActionsToReturn.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
                    }
                 }
            }
        }
        catch (WorkflowException wfe) {
            throw new RuntimeException("Unable to retrieve prior Approvers list");
        }
        
        
        return documentActionsToReturn;
    }

    public boolean doPermissionExistsByTemplate(
            BusinessObject businessObject, String namespaceCode,
            String permissionTemplateName, Map<String, String> permissionDetails) {

        return permissionExistsByTemplate(businessObject, namespaceCode, permissionTemplateName, permissionDetails);
        
    }

    
}
