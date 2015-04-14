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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.document.validation.event.ContinueCustomerInvoiceWriteoffDocumentEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.KualiRuleService;

public class CustomerInvoiceWriteoffDocumentAction extends FinancialSystemTransactionalDocumentActionBase {
    
    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        CustomerInvoiceWriteoffDocumentForm form = (CustomerInvoiceWriteoffDocumentForm) kualiDocumentFormBase;
        CustomerInvoiceWriteoffDocument document = (CustomerInvoiceWriteoffDocument) form.getDocument();
        document.populateCustomerNote();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#blanketApprove(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.blanketApprove(mapping, form, request, response);
        saveCustomerNote(form);
        return actionForward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.route(mapping, form, request, response);
        saveCustomerNote(form);
        return actionForward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward actionForward = super.save(mapping, form, request, response);
        saveCustomerNote(form);
        return actionForward;
    }
    
    
    protected void saveCustomerNote(ActionForm form) {
        CustomerService customerService = SpringContext.getBean(CustomerService.class);
        
        CustomerInvoiceWriteoffDocumentForm customerInvoiceWriteoffDocumentForm = (CustomerInvoiceWriteoffDocumentForm) form;
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument) customerInvoiceWriteoffDocumentForm.getDocument();
        
        String customerNumber = customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getCustomer().getCustomerNumber();
        String customerNote = customerInvoiceWriteoffDocument.getCustomerNote();
        
        customerService.createCustomerNote(customerNumber, customerNote);
    }

    /**
     * Do initialization for a new customer invoice writeoff document
     * 
     * TODO This initation stuff does the exact same thing as customer credit memo. this should really be abstracted out...
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
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
        
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new ContinueCustomerInvoiceWriteoffDocumentEvent(errorPath,customerInvoiceWriteoffDocument));
        if (rulePassed){
            SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class).setupDefaultValuesForNewCustomerInvoiceWriteoffDocument(customerInvoiceWriteoffDocument);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    

}
