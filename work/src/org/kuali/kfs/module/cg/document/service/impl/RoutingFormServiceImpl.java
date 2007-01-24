/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.kra.routingform.service.impl;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.kuali.module.kra.routingform.service.RoutingFormService;

import edu.iu.uis.eden.exception.WorkflowException;

public class RoutingFormServiceImpl implements RoutingFormService {

    private DocumentService documentService;
    
    public void initializeRoutingForm(RoutingFormDocument routingFormDocument) throws WorkflowException {
        //TODO
    }

    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormService#prepareRoutingFormForSave(org.kuali.module.kra.routingform.document.RoutingFormDocument)
     */
    public void prepareRoutingFormForSave(RoutingFormDocument routingFormDocument) throws WorkflowException {
        // TODO
        
        /* TODO write cleanse grants.gov (sf424) method. See Budget for samples. 
         * Also see RoutingFormDocumentPreRules.confirmDeleteGrantsGovSubmission for Main Page confirm message. */
    }

    public BudgetDocument retrieveBudgetForLinking(String budgetDocumentNumber) throws WorkflowException {
        Document document = documentService.getByDocumentHeaderId(budgetDocumentNumber);

        if (document != null && document instanceof BudgetDocument) {
            return (BudgetDocument)document;
        } else {
            return null;
        }
    }
    
    public void linkImportBudgetDataToRoutingForm(RoutingFormDocument routingFormDocument, String budgetDocumentHeaderId) throws WorkflowException {
        
        BudgetDocument budgetDocument = retrieveBudgetForLinking(budgetDocumentHeaderId);
        
        routingFormDocument.getRoutingFormAgency().setAgencyNumber(budgetDocument.getBudget().getBudgetAgencyNumber());
        routingFormDocument.setRoutingFormAgencyToBeNamedIndicator(budgetDocument.getBudget().isAgencyToBeNamedIndicator());
        
        
        
    }
    
    /**
     * @see org.kuali.module.kra.routingform.service.RoutingFormService#isGrantsGovModified(org.kuali.module.kra.routingform.document.RoutingFormDocument)
     */
    public boolean isGrantsGovModified(RoutingFormDocument routingFormDocument) {
        RoutingFormDocument databaseRoutingFormDocument;
        try {
            databaseRoutingFormDocument = (RoutingFormDocument) documentService.getByDocumentHeaderId(routingFormDocument.getDocumentNumber());
        } catch (WorkflowException e) {
            throw new RuntimeException("Exception retrieving document: " + e);
        }
        if (databaseRoutingFormDocument == null) {
            return false;
        }

        return databaseRoutingFormDocument.isGrantsGovernmentSubmissionIndicator() && !routingFormDocument.isGrantsGovernmentSubmissionIndicator();
    }
    
    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}
