/*
 * Copyright 2007 The Kuali Foundation.
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
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.document.Document;

/**
 * Struts Action for Credit Memo document.
 */
public class ElectronicInvoiceRejectAction extends FinancialSystemTransactionalDocumentActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicInvoiceRejectAction.class);

    public ActionForward startResearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ElectronicInvoiceRejectForm electronicInvoiceRejectForm = (ElectronicInvoiceRejectForm) form;
        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) electronicInvoiceRejectForm.getDocument();
        eirDocument.setInvoiceResearchIndicator(true);

         Note noteObj = SpringContext.getBean(DocumentService.class).createNoteFromDocument(eirDocument, "Research started by: " + GlobalVariables.getUserSession().getPerson().getName());
         //eirDocument.getDocumentHeader().addNote(noteObj);
         PersistableBusinessObject noteParent = getNoteParent(eirDocument, noteObj);
         noteParent.addNote(noteObj);
         this.getNoteService().save(noteObj);

//         if (SpringContext.getBean(DocumentService.class).addNoteToDocument(eirDocument, noteObj)) {
//             SpringContext.getBean(NoteService.class).save(noteObj);
//             SpringContext.getBean(PurapService.class).saveDocumentNoValidation(eirDocument);
//         }
         getDocumentService().saveDocument(eirDocument);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward completeResearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ElectronicInvoiceRejectForm electronicInvoiceRejectForm = (ElectronicInvoiceRejectForm) form;
        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) electronicInvoiceRejectForm.getDocument();
        eirDocument.setInvoiceResearchIndicator(false);

        Note noteObj = SpringContext.getBean(DocumentService.class).createNoteFromDocument(eirDocument, "Research completed by: " + GlobalVariables.getUserSession().getPerson().getName());
        //eirDocument.getDocumentHeader().addNote(noteObj);
        PersistableBusinessObject noteParent = getNoteParent(eirDocument, noteObj);
        noteParent.addNote(noteObj);
        this.getNoteService().save(noteObj);
//        if (SpringContext.getBean(DocumentService.class).addNoteToDocument(eirDocument, noteObj)) {
//            SpringContext.getBean(NoteService.class).save(noteObj);
//            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(eirDocument);
//        }
        getDocumentService().saveDocument(eirDocument);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }
    
    private PersistableBusinessObject getNoteParent(ElectronicInvoiceRejectDocument document, Note newNote) {
        //get the property name to set (this assumes this is a document type note)
        String propertyName = getNoteService().extractNoteProperty(newNote);
        //get BO to set
        PersistableBusinessObject noteParent = (PersistableBusinessObject)ObjectUtils.getPropertyValue(document, propertyName);
        return noteParent;
    }

}

