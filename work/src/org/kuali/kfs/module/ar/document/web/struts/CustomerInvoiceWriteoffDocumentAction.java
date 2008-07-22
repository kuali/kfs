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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerInvoiceWriteoffDocumentAction extends FinancialSystemTransactionalDocumentActionBase {
    
    /**
     * Do initialization for a new customer invoice writeoff document
     * 
     * TODO This initation stuff does the exact same thing as customer credit memo. this should really be abstracted out...
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CustomerInvoiceWriteoffDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }
    
    /**
     * Clears out init tab.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerInvoiceWriteoffDocumentForm customerInvoiceWriteoffDocumentForm = (CustomerInvoiceWriteoffDocumentForm) form;
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) customerInvoiceWriteoffDocumentForm.getDocument();
        customerInvoiceWriteoffDocument.clearInitFields();
        
        return super.refresh(mapping, form, request, response);
    }   
    
    /**
     * Handles continue request. This request comes from the initial screen which gives ref. invoice number.
     * Based on that, the customer credit memo is initially populated.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueCustomerInvoiceWriteoff(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceWriteoffDocumentForm customerInvoiceWriteoffDocumentForm = (CustomerInvoiceWriteoffDocumentForm) form;
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) customerInvoiceWriteoffDocumentForm.getDocument();
        
        //String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = true; //TODO Put rule...
        if (rulePassed){
            //update status
            customerInvoiceWriteoffDocument.setStatusCode(ArConstants.CustomerInvoiceWriteoffStatuses.IN_PROCESS);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    

}
