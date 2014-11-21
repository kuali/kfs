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
