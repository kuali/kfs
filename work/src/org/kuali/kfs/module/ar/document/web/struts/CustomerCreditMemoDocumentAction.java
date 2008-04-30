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
package org.kuali.module.ar.web.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.web.struts.form.CustomerCreditMemoDocumentForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerCreditMemoDocumentAction extends KualiAccountingDocumentActionBase {
    
    public CustomerCreditMemoDocumentAction() {
        super();
    }
    
    /**
     * Do initialization for a new customer credit memo.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CustomerCreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
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
        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();
        customerCreditMemoDocument.clearInitFields();
        
        return super.refresh(mapping, form, request, response);
    }   
    
    /**
     * Handles continue request. This request comes from the initial screen which gives indicates whether the type is payment
     * request, purchase order, or vendor. Based on that, the credit memo is initially populated and the remaining tabs shown.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerCreditMemoDocumentForm cmForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) cmForm.getDocument();

        /*
        // preform duplicate check which will forward to a question prompt if one is found
        ActionForward forward = performDuplicateCreditMemoCheck(mapping, form, request, response, creditMemoDocument);
        if (forward != null) {

            return forward;
        }*/

        /*
        // perform validation of init tab
        SpringContext.getBean(CreditMemoService.class).populateAndSaveCreditMemo(creditMemoDocument);

        // sort below the line (doesn't really need to be done on CM, but will help if we ever bring btl from other docs)
        SpringContext.getBean(PurapService.class).sortBelowTheLine(creditMemoDocument);

        // update the counts on the form
        cmForm.updateItemCounts();
        */
        
        customerCreditMemoDocument.setStatusCode("INPR");
        
        //1. try saving the document, to see if invoice reference is populated automatically
        //SpringContext.getBean(DocumentService.class).saveDocument(customerCreditMemoDocument);
        //2. explicitly refresh invoice object
        customerCreditMemoDocument.refreshReferenceObject("invoice");
        
        List<SourceAccountingLine> invoiceDetails = customerCreditMemoDocument.getInvoice().getSourceAccountingLines();
        
        CustomerCreditMemoDetail customerCreditMemoDetail;
        for( SourceAccountingLine invoiceDetail : invoiceDetails ){
            customerCreditMemoDetail = new CustomerCreditMemoDetail();
            customerCreditMemoDocument.getCreditMemoDetails().add(customerCreditMemoDetail);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    
}
