/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.BulkReceivingDocument;
import org.kuali.kfs.module.purap.document.service.BulkReceivingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

import edu.iu.uis.eden.exception.WorkflowException;

public class BulkReceivingAction extends KualiTransactionalDocumentActionBase {

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {       
        
        super.createDocument(kualiDocumentFormBase);

        BulkReceivingForm blkForm = (BulkReceivingForm)kualiDocumentFormBase;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument)blkForm.getDocument();
        
        //set identifier from form value
        blkRecDoc.setPurchaseOrderIdentifier( blkForm.getPurchaseOrderId() );
        if (blkForm.getPurchaseOrderId() != null){
            blkForm.setPOAvailable(true);
            blkRecDoc.setPOAvailable(true);
        }
        
        blkRecDoc.initiateDocument();
        
    }

    public ActionForward continueBulkReceiving(ActionMapping mapping, 
                                               ActionForm form, 
                                               HttpServletRequest request, 
                                               HttpServletResponse response) 
    throws Exception {
    
        BulkReceivingForm blkForm = (BulkReceivingForm)form;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument)blkForm.getDocument();
        
        //perform duplicate check
        ActionForward forward = isDuplicateDocumentEntry(mapping, form, request, response, blkRecDoc);
        if( forward != null ){
            return forward;
        }
        
        //populate and save Receiving Line Document from Purchase Order        
        SpringContext.getBean(BulkReceivingService.class).populateAndSaveBulkReceivingDocument(blkRecDoc);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward clearInitFields(ActionMapping mapping, 
                                         ActionForm form, 
                                         HttpServletRequest request, 
                                         HttpServletResponse response) 
    throws Exception {
        
        BulkReceivingForm blkRecForm = (BulkReceivingForm) form;
        BulkReceivingDocument blkRecDoc = (BulkReceivingDocument) blkRecForm.getDocument();
        blkRecDoc.clearInitFields();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
        
    }

    private ActionForward isDuplicateDocumentEntry(ActionMapping mapping, 
                                                   ActionForm form, 
                                                   HttpServletRequest request, 
                                                   HttpServletResponse response, 
                                                   BulkReceivingDocument bulkReceivingDocument) 
    throws Exception {
        
        ActionForward forward = null;
        HashMap<String, String> duplicateMessages = SpringContext.getBean(BulkReceivingService.class).bulkReceivingDuplicateMessages(bulkReceivingDocument);
        
        if (duplicateMessages != null && !duplicateMessages.isEmpty()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {

                return this.performQuestionWithoutInput(mapping, 
                                                        form, 
                                                        request, 
                                                        response, 
                                                        PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION,
                                                        duplicateMessages.get(PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION), 
                                                        KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.BulkReceivingDocumentStrings.DUPLICATE_BULK_RECEIVING_DOCUMENT_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {                
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }
}    
