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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.document.ElectronicInvoiceRejectDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Struts Action for Electronic invoice document.
 */
public class ElectronicInvoiceRejectAction extends FinancialSystemTransactionalDocumentActionBase {

    public ActionForward startResearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ElectronicInvoiceRejectForm electronicInvoiceRejectForm = (ElectronicInvoiceRejectForm) form;
        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) electronicInvoiceRejectForm.getDocument();
        eirDocument.setInvoiceResearchIndicator(true);

        Note noteObj = getDocumentService().createNoteFromDocument(eirDocument, "Research started by: " + GlobalVariables.getUserSession().getPerson().getName());
        eirDocument.addNote(noteObj);
        getNoteService().save(noteObj);
        getDocumentService().saveDocument(eirDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward completeResearch(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ElectronicInvoiceRejectForm electronicInvoiceRejectForm = (ElectronicInvoiceRejectForm) form;
        ElectronicInvoiceRejectDocument eirDocument = (ElectronicInvoiceRejectDocument) electronicInvoiceRejectForm.getDocument();
        eirDocument.setInvoiceResearchIndicator(false);

        Note noteObj = getDocumentService().createNoteFromDocument(eirDocument, "Research completed by: " + GlobalVariables.getUserSession().getPerson().getName());
        eirDocument.addNote(noteObj);
        getNoteService().save(noteObj);
        getDocumentService().saveDocument(eirDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

}

