/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.document.CorrectionReceivingDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;


public class CorrectionReceivingAction extends ReceivingBaseAction {

    @Override
    public ActionForward docHandler(ActionMapping mapping, 
                                    ActionForm form, 
                                    HttpServletRequest request, 
                                    HttpServletResponse response) 
    throws Exception {
        
        ActionForward forward = super.docHandler(mapping,form,request,response);
        
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        String command = kualiDocumentFormBase.getCommand();
        
        if (StringUtils.equals("initiate",command)) {
            CorrectionReceivingForm rcf = (CorrectionReceivingForm)form;
            CorrectionReceivingDocument rcDoc = (CorrectionReceivingDocument)rcf.getDocument();
            
            String noteText = request.getParameter(PurapConstants.CorrectionReceivingDocumentStrings.CORRECTION_RECEIVING_CREATION_NOTE_PARAMETER);
            
            if (StringUtils.isNotBlank(noteText)){
                //Document should be saved before adding note to escape from DataIntegrityViolationException
                SpringContext.getBean(PurapService.class).saveDocumentNoValidation(rcDoc);
                SpringContext.getBean(ReceivingService.class).addNoteToReceivingDocument(rcDoc,noteText);
            }
            
        }
        
        return forward;
    }
    
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
    
}
