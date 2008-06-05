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
package org.kuali.module.purap.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.PurapConstants.ReceivingCorrectionDocumentStrings;
import org.kuali.module.purap.document.ReceivingCorrectionDocument;
import org.kuali.module.purap.document.ReceivingDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.service.ReceivingService;
import org.kuali.module.purap.util.ReceivingQuestionCallback;
import org.kuali.module.purap.web.struts.form.ReceivingCorrectionForm;
import org.kuali.module.purap.web.struts.form.ReceivingLineForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class ReceivingCorrectionAction extends ReceivingBaseAction {

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {       
        super.createDocument(kualiDocumentFormBase);

        ReceivingCorrectionForm rcf = (ReceivingCorrectionForm)kualiDocumentFormBase;
        ReceivingCorrectionDocument rcDoc = (ReceivingCorrectionDocument)rcf.getDocument();
        
        //set identifier from form value
        rcDoc.setReceivingLineDocumentNumber( rcf.getReceivingLineDocId() );
        
        rcDoc.initiateDocument();
        
        //populate and save Receiving Line Document from Purchase Order        
        SpringContext.getBean(ReceivingService.class).populateReceivingCorrectionDocument(rcDoc);

    }
    
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        
        String operation = "AddCorrectionNote ";

        ReceivingQuestionCallback callback = new ReceivingQuestionCallback() {
            public boolean questionComplete = false;
            
            public ReceivingDocument doPostQuestion(ReceivingDocument document, String noteText) throws Exception {
                //add note to receiving line document
                ReceivingLineDocument rlDoc = ((ReceivingCorrectionDocument)document).getReceivingLineDocument();
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
        ActionForward forward = askQuestionWithInput(mapping, form, request, response, ReceivingCorrectionDocumentStrings.RECEIVING_CORRECTION_NOTE_QUESTION, ReceivingCorrectionDocumentStrings.RECEIVING_CORRECTION_NOTE_PREFIX, operation, PurapKeyConstants.MESSAGE_RECEIVING_CORRECTION_NOTE, callback);
        
        //if question asked is complete, then route
        if(callback.isQuestionComplete()){
            forward = super.route(mapping,form,request,response);
        }
        
        return forward;
        
    }
}
