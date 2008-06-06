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
package org.kuali.module.ar.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.CustomerAddress;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.event.DiscountCustomerInvoiceDetailEvent;
import org.kuali.module.ar.rule.event.RecalculateCustomerInvoiceDetailEvent;
import org.kuali.module.ar.service.CustomerAddressService;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.service.CustomerInvoiceDocumentService;
import org.kuali.module.ar.web.struts.form.CustomerInvoiceDocumentForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerInvoiceDocumentAction extends KualiAccountingDocumentActionBase {


    /**
     * Overriding to make it easier to distinguish discount lines and lines that are associated to discounts
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateAccountReceivableObjectCodes();
        try {
            // proceed as usual
            customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
            ActionForward result = super.execute(mapping, form, request, response);
            return result;
        }
        finally {
            // update it again for display purposes
            customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
        }
    }

    /**
     * Called when customer invoice document is initiated.
     * 
     * Makes a call to parent's createDocument method, but also defaults values for customer invoice document. Line which inserts
     * Customer Invoice Detail (i.e. insertSourceLine) has its values defaulted by
     * CustomerInvoiceDocumentForm.createNewSourceAccountingLine()
     * 
     * @see org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);

        // set up the default values for customer invoice document
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        SpringContext.getBean(CustomerInvoiceDocumentService.class).setupDefaultValuesForNewCustomerInvoiceDocument(customerInvoiceDocument);
    }

    /**
     * All document-load operations get routed through here
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        CustomerInvoiceDocumentForm form = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        form.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
        SpringContext.getBean(CustomerInvoiceDocumentService.class).loadCustomerAddressesForCustomerInvoiceDocument(form.getCustomerInvoiceDocument());

    }

    /**
     * Method that will take the current document, copy it, replace all references to doc header id with a new one, clear pending
     * entries, clear notes, and reset version numbers
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward copy(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        
        // perform discount check
        ActionForward forward = performInvoiceWithDiscountsCheck(mapping, form, request, response, customerInvoiceDocument);
        if (forward != null) {
            return forward;
        }

        return super.copy(mapping, form, request, response);
    }


    /**
     * This method checks if the user wants to copy a document that contains a discount line.  If yes, this method returns null. If no,
     * this method returns the "basic" forward. 
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param customerInvoiceDocument
     * @return
     * @throws Exception
     */
    private ActionForward performInvoiceWithDiscountsCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, CustomerInvoiceDocument customerInvoiceDocument) throws Exception {
        ActionForward forward = null;

        if( customerInvoiceDocument.hasAtLeastOneDiscount() ){
            
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                return this.performQuestionWithoutInput(mapping, form, request, response, ArConstants.COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION, 
                        "This document contains a discount line.  Are you sure you want to copy this document?", KFSConstants.CONFIRMATION_QUESTION,
                        KFSConstants.ROUTE_METHOD, "");
            }
    
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (ArConstants.COPY_CUSTOMER_INVOICE_DOCUMENT_WITH_DISCOUNTS_QUESTION.equals(question) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    /**
     * This method is the action for refreshing the added source line (or customer invoice detail) based off a provided invoice item
     * code.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshNewSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        CustomerInvoiceDetail newCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();

        CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
        CustomerInvoiceDetail loadedCustomerInvoiceDetail = customerInvoiceDetailService.getCustomerInvoiceDetailFromCustomerInvoiceItemCodeForCurrentUser(newCustomerInvoiceDetail.getInvoiceItemCode());
        if (loadedCustomerInvoiceDetail == null) {
            loadedCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();
        }

        customerInvoiceDocumentForm.setNewSourceLine(loadedCustomerInvoiceDetail);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method is the action for recalculating the amount added line assuming that the unit price or quantity has changed
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculateSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        int index = getSelectedLine(request);
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(index);

        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";

        boolean rulePassed = true;
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerInvoiceDetailEvent(errorPath, customerInvoiceDocumentForm.getDocument(), customerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);
            customerInvoiceDetailService.updateAccountsForCorrespondingDiscount(customerInvoiceDetail);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * This method is used for inserting a discount line based on a selected source line.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward discountSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        int index = getSelectedLine(request);
        CustomerInvoiceDetail parentCustomerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(index);

        // document.sourceAccountingLine[0].invoiceItemUnitPrice
        String errorPath = KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME + "[" + index + "]";

        boolean rulePassed = true;
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new DiscountCustomerInvoiceDetailEvent(errorPath, customerInvoiceDocumentForm.getDocument(), parentCustomerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetail discountCustomerInvoiceDetail = SpringContext.getBean(CustomerInvoiceDetailService.class).getDiscountCustomerInvoiceDetailForCurrentYear(parentCustomerInvoiceDetail, customerInvoiceDocument);
            discountCustomerInvoiceDetail.refreshNonUpdateableReferences();
            insertAccountingLine(true, customerInvoiceDocumentForm, discountCustomerInvoiceDetail);

            // also set parent customer invoice detail line to have discount line seq number
            parentCustomerInvoiceDetail.setInvoiceItemDiscountLineNumber(discountCustomerInvoiceDetail.getSequenceNumber());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Removed salesTax checking. Need to verify if this check has be moved out later of the KualiAccountingDocumentActionBase
     * class. If so just use the parent class' insertSourceLine method.
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#insertSourceLine(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward insertSourceLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();

        // make sure amount is up to date before rules
        CustomerInvoiceDetailService service = SpringContext.getBean(CustomerInvoiceDetailService.class);
        service.recalculateCustomerInvoiceDetail(customerInvoiceDocument, customerInvoiceDetail);

        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, customerInvoiceDocumentForm.getDocument(), customerInvoiceDetail));

        if (rulePassed) {

            // add accountingLine
            customerInvoiceDetail.refreshNonUpdateableReferences();
            service.prepareCustomerInvoiceDetailForAdd(customerInvoiceDetail, customerInvoiceDocument);
            insertAccountingLine(true, customerInvoiceDocumentForm, customerInvoiceDetail);

            // clear the used newTargetLine
            customerInvoiceDocumentForm.setNewSourceLine(null);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Overrides method to delete accounting line. If line to be deleted has a corresponding discount line, the corresponding
     * discount line is also deleted. If the line to be delete is a discount line, set the reference for the parent to null
     * 
     * @param isSource
     * @param financialDocumentForm
     * @param deleteIndex
     */
    @Override
    protected void deleteAccountingLine(boolean isSource, KualiAccountingDocumentFormBase financialDocumentForm, int deleteIndex) {

        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceDocumentForm) financialDocumentForm).getCustomerInvoiceDocument();

        // if line to delete is a discount parent discountLine, remove discount line too
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocument.getSourceAccountingLine(deleteIndex);
        if (customerInvoiceDetail.isDiscountLineParent()) {
            customerInvoiceDocument.removeDiscountLineBasedOnParentLineIndex(deleteIndex);
        }
        else if (customerInvoiceDocument.isDiscountLineBasedOnSequenceNumber(customerInvoiceDetail.getSequenceNumber())) {

            // if line to delete is a discount line, set discount line reference for parent to null
            CustomerInvoiceDetail parentCustomerInvoiceDetail = customerInvoiceDetail.getParentDiscountCustomerInvoiceDetail();
            if (ObjectUtils.isNotNull(parentCustomerInvoiceDetail)) {
                parentCustomerInvoiceDetail.setInvoiceItemDiscountLineNumber(null);
            }
        }

        // delete line like normal
        super.deleteAccountingLine(isSource, financialDocumentForm, deleteIndex);
    }
    
    /**
     * This method refresh the ShipToAddress CustomerAddress object
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshBillToAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceDocumentForm) form).getCustomerInvoiceDocument();
        
        CustomerAddress customerBillToAddress = SpringContext.getBean(CustomerAddressService.class).getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerInvoiceDocument.getCustomerBillToAddressIdentifier());
        if (ObjectUtils.isNotNull(customerBillToAddress)) {
            customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * This method refresh the ShipToAddress CustomerAddress object
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward refreshShipToAddress(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CustomerInvoiceDocument customerInvoiceDocument = ((CustomerInvoiceDocumentForm) form).getCustomerInvoiceDocument();

        CustomerAddress customerShipToAddress = SpringContext.getBean(CustomerAddressService.class).getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerInvoiceDocument.getCustomerShipToAddressIdentifier());
        if (ObjectUtils.isNotNull(customerShipToAddress)) {
            customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }    

}
