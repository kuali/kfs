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
package org.kuali.kfs.module.ar.document;

import org.kuali.kfs.module.ar.businessobject.InvoiceRecurrence;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class InvoiceRecurrenceMaintainable extends FinancialSystemMaintainable {

    private static final String INACTIVATING_NODE_NAME = "InvoiceRecurrenceIsInactivating";
    private static final String INITIATED_BY_SYSTEM_USER = "InitiatedBySystemUser";
    
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        FinancialSystemMaintenanceDocument maintDoc = getParentMaintDoc();
        
        //  return true if the doc is flipping form Active to Inactive, false otherwise
        if (INACTIVATING_NODE_NAME.equalsIgnoreCase(nodeName)) { 
            //  go through some contortions to get the oldMaintainable to compare against
            boolean oldIsActive = ((InvoiceRecurrence)maintDoc.getOldMaintainableObject().getBusinessObject()).isActive();
            boolean newIsActive = ((InvoiceRecurrence)this.getBusinessObject()).isActive();
            
            //  return true if the invoicerecurrence is being deactivated, otherwise return false
            return oldIsActive && !newIsActive;
        }

        //  return true if the document was initiated by the SYSTEM_USER, false otherwise
        if (INITIATED_BY_SYSTEM_USER.equalsIgnoreCase(nodeName)) {
            KualiWorkflowDocument workflowDoc = maintDoc.getDocumentHeader().getWorkflowDocument();
            String initiatorPrincipalId = workflowDoc.getInitiatorPrincipalId();
            PersonService<Person> personService = SpringContext.getBean(PersonService.class);
            Person initiatorPerson = personService.getPerson(initiatorPrincipalId);
            return (KFSConstants.SYSTEM_USER.equalsIgnoreCase(initiatorPerson.getPrincipalName()));
        }

        throw new UnsupportedOperationException("InvoiceRecurrenceMaintainable does not implement the answerSplitNodeQuestion method. Node name specified was: " + nodeName);
        
    }

    private FinancialSystemMaintenanceDocument getParentMaintDoc() {
        //  how I wish for the ability to directly access the parent object
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;
        try {
            maintDoc =(FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(this.documentNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return maintDoc;
    }
    
}
