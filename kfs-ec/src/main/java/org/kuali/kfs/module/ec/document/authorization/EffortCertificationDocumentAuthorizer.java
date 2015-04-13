/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
            if (KewApiConstants.ACTION_TAKEN_APPROVED_CD.equals(actionTaken.getActionTaken().getCode())) {
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
