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

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.document.authorization.FinancialSystemTransactionalDocumentAuthorizerBase;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.action.ActionTaken;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
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
        UserSession userSession = GlobalVariables.getUserSession();
        String principalId =  userSession.getPrincipalId();
        if (document.getDocumentHeader().getWorkflowDocument().isEnroute()) {
                Set<Person> priorApprovers = this.getPriorApprovers(document.getDocumentHeader().getWorkflowDocument());
                  for (Person priorApprover : priorApprovers) {
                      if (principalId.equals(priorApprover.getPrincipalId())) {
                          documentActionsToReturn.add(KRADConstants.KUALI_ACTION_CAN_EDIT);
                          documentActionsToReturn.add(KRADConstants.KUALI_ACTION_CAN_SAVE);
                      }
                  }
        }
        
        
        return documentActionsToReturn;
    }
    protected Set<Person> getPriorApprovers(WorkflowDocument workflowDocument) {
        PersonService personService = KimApiServiceLocator.getPersonService();
        List<ActionTaken> actionsTaken = workflowDocument.getActionsTaken();
        Set<String> principalIds = new HashSet<String>();
        Set<Person> persons = new HashSet<Person>();

        for (ActionTaken actionTaken : actionsTaken) {
            if (KewApiConstants.ACTION_TAKEN_APPROVED_CD.equals(actionTaken.getActionTaken())) {
                String principalId = actionTaken.getPrincipalId();
                if (!principalIds.contains(principalId)) {
                    principalIds.add(principalId);
                    persons.add(personService.getPerson(principalId));
                }
            }
        }
        return persons;
    }

    public boolean doPermissionExistsByTemplate(
            BusinessObject businessObject, String namespaceCode,
            String permissionTemplateName, Map<String, String> permissionDetails) {

        return permissionExistsByTemplate(businessObject, namespaceCode, permissionTemplateName, permissionDetails);
        
    }

    
}
