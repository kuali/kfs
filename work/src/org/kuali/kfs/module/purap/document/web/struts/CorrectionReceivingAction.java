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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.CorrectionReceivingDocumentStrings;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.util.ReceivingQuestionCallback;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

public class CorrectionReceivingAction extends ReceivingBaseAction {

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {       
        super.createDocument(kualiDocumentFormBase);

        CorrectionReceivingForm rcf = (CorrectionReceivingForm)kualiDocumentFormBase;
        CorrectionReceivingDocument rcDoc = (CorrectionReceivingDocument)rcf.getDocument();
        
        //set identifier from form value
        rcDoc.setLineItemReceivingDocumentNumber( rcf.getReceivingLineDocId() );
        
        rcDoc.initiateDocument();
        
        //populate and save Receiving Line Document from Purchase Order        
        SpringContext.getBean(ReceivingService.class).populateCorrectionReceivingDocument(rcDoc);

    }
    
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        
        String operation = "AddCorrectionNote ";

        ReceivingQuestionCallback callback = new ReceivingQuestionCallback() {
            public boolean questionComplete = false;
            
            public ReceivingDocument doPostQuestion(ReceivingDocument document, String noteText) throws Exception {
                //add note to receiving line document
                LineItemReceivingDocument rlDoc = ((CorrectionReceivingDocument)document).getLineItemReceivingDocument();
                SpringContext.getBean(ReceivingService.class).addNoteToReceivingDocument(rlDoc,noteText);
                
                //mark question completed
                this.setQuestionComplete(true);
                
                return document;
            }
            
            public boolean isQuestionComplete(){
                return this.questionComplete;
            }
            
            public void setQuestionComplete(boolean questionComplete){
                this.questionComplete = questionComplete;
            }
        };

        //ask question
        ActionForward forward = askQuestionWithInput(mapping, form, request, response, CorrectionReceivingDocumentStrings.NOTE_QUESTION, CorrectionReceivingDocumentStrings.NOTE_PREFIX, operation, PurapKeyConstants.MESSAGE_RECEIVING_CORRECTION_NOTE, callback);
        
        //if question asked is complete, then route
        if(callback.isQuestionComplete()){
            forward = super.route(mapping,form,request,response);
        }
        
        return forward;
        
    }
}
