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
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.CustomerCreditMemoDetail;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.CustomerCreditMemoDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.web.struts.form.CashControlDocumentForm;
import org.kuali.module.ar.web.struts.form.CustomerCreditMemoDocumentForm;
import org.kuali.module.ar.web.struts.form.CustomerInvoiceDocumentForm;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.rice.KNSServiceLocator;

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
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerCreditMemoDocumentForm customerCreditMemoDocumentForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument) customerCreditMemoDocumentForm.getDocument();

        /*
         1. perform validation of init tab: check if invoice ref. number is valid
         2. check if there is already an open customer credit memo for the referenced invoice
            (only one customer credit memo is allowed per invoice)
        */

        customerCreditMemoDocument.setStatusCode(ArConstants.CustomerCreditMemoStatuses.IN_PROCESS);
        
        /*
         Qiestions:
         1. try saving the document, to see if invoice reference is populated automatically
            SpringContext.getBean(DocumentService.class).saveDocument(customerCreditMemoDocument);
            ? // perform validation of init tab
            ? SpringContext.getBean(CreditMemoService.class).populateAndSaveCreditMemo(creditMemoDocument);
         2. explicitly refresh invoice object
         */
        
        // initialize creditMemoDetails
        List<SourceAccountingLine> invoiceDetails = customerCreditMemoDocument.getInvoice().getSourceAccountingLines();
        CustomerCreditMemoDetail customerCreditMemoDetail;
        KualiDecimal invItemTaxAmount;
        
        for( SourceAccountingLine invoiceDetail : invoiceDetails ){
            customerCreditMemoDetail = new CustomerCreditMemoDetail();
            
            // populate invoice item 'Total Amount'
            invItemTaxAmount = ((CustomerInvoiceDetail)invoiceDetail).getInvoiceItemTaxAmount();
            if (invItemTaxAmount == null) {
                invItemTaxAmount = KualiDecimal.ZERO;
                ((CustomerInvoiceDetail)invoiceDetail).setInvoiceItemTaxAmount(invItemTaxAmount);
            }
            
            customerCreditMemoDetail.setInvoiceLineTotalAmount(invItemTaxAmount,invoiceDetail.getAmount());
            
            customerCreditMemoDocument.getCreditMemoDetails().add(customerCreditMemoDetail);
        }
        //???
        //SpringContext.getBean(DocumentService.class).saveDocument(customerCreditMemoDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method refreshes a customer credit memo detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward refreshCustomerCreditMemoDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerCreditMemoDocumentForm customerCreditMemoDocForm = (CustomerCreditMemoDocumentForm) form;
        CustomerCreditMemoDocument customerCreditMemoDocument = (CustomerCreditMemoDocument)customerCreditMemoDocForm.getDocument();
        int indexOfLineToRefresh = getSelectedLine(request);
        
        /*
        CustomerInvoiceDetail customerInvoiceDetail = customerCreditMemoDetails.set(indexOfLineToRefresh,customerCreditMemoDetail);
        customerCreditMemoDocument.setCreditMemoDetails(customerCreditMemoDetails);
        */
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    
    
}
