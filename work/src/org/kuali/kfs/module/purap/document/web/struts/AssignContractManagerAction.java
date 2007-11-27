/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.purap.web.struts.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DocumentService;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.bo.AssignContractManagerDetail;
import org.kuali.module.purap.document.AssignContractManagerDocument;
import org.kuali.module.purap.document.RequisitionDocument;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Struts Action for Contract Manager Assignment document.
 */
public class AssignContractManagerAction extends KualiTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssignContractManagerAction.class);

    /**
     * Do initialization for a new <code>AssignContractManagerDocument</code>.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        AssignContractManagerDocument acmDocument = (AssignContractManagerDocument) kualiDocumentFormBase.getDocument();
        acmDocument.getDocumentHeader().setFinancialDocumentDescription(PurapConstants.ASSIGN_CONTRACT_MANAGER_DEFAULT_DESC);
        acmDocument.populateDocumentWithRequisitions();
    }
    
    /**
     * Overrides the method in KualiDocumentActionBase to fetch a List of requisition documents for the
     * AssignContractManagerDocument from documentService, because we need the workflowDocument to get the
     * createDate. If we don't fetch the requisition documents from the documentService, the workflowDocument
     * in the requisition's documentHeader would be null and would cause the transient flexDoc is null error.
     * That's the reason we need this override.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument (KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        AssignContractManagerDocument document = (AssignContractManagerDocument)kualiDocumentFormBase.getDocument();
        List<String>documentHeaderIds = new ArrayList();
        Map<String, AssignContractManagerDetail>documentHeaderIdsAndDetails = new HashMap();
        
        //Compose a Map in which the keys are the document header ids of each requisition in this acm document and the values are the 
        //corresponding AssignContractManagerDetail object.
        for (AssignContractManagerDetail detail : (List<AssignContractManagerDetail>)document.getAssignContractManagerDetails()) {
            documentHeaderIdsAndDetails.put(detail.getRequisition().getDocumentNumber(), detail);
        }
        //Add all of the document header ids (which are the keys of the documentHeaderIdsAndDetails  map) to the 
        //documentHeaderIds List.
        documentHeaderIds.addAll(documentHeaderIdsAndDetails.keySet());
        
        //Get a List of requisition documents from documentService so that we can have the workflowDocument as well
        List<RequisitionDocument> requisitionDocumentsFromDocService = new ArrayList();
        try {
            if ( documentHeaderIds.size() > 0 )
                requisitionDocumentsFromDocService = SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(RequisitionDocument.class, documentHeaderIds);
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow Exception caught: " + we.getLocalizedMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
        
        //Set the documentHeader of the requisition of each of the AssignContractManagerDetail to the documentHeader of
        //the requisitions resulted from the documentService, so that we'll have workflowDocument in the documentHeader.
        for (RequisitionDocument req : requisitionDocumentsFromDocService) {
            AssignContractManagerDetail detail = (AssignContractManagerDetail)documentHeaderIdsAndDetails.get(req.getDocumentNumber());
            detail.getRequisition().setDocumentHeader(req.getDocumentHeader());
        }
    }
}
