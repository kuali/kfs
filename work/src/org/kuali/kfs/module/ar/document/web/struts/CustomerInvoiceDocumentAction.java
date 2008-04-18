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

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.Timer;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.module.ar.rule.event.DiscountCustomerInvoiceDetailEvent;
import org.kuali.module.ar.rule.event.RecalculateCustomerInvoiceDetaiEvent;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CustomerInvoiceDetailService;
import org.kuali.module.ar.web.struts.form.CustomerInvoiceDocumentForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class CustomerInvoiceDocumentAction extends KualiAccountingDocumentActionBase {

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
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) kualiDocumentFormBase;
        CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentForm.getCustomerInvoiceDocument();

        // set up the default values for customer invoice document
        customerInvoiceDocument.setupDefaultValues();

        // set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(customerInvoiceDocument.getDocumentNumber());
        customerInvoiceDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);
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

    }


    /**
     * Overriding to populate references for discounts and parent line details
     * 
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CustomerInvoiceDocumentForm customerInvoiceDocumentForm = (CustomerInvoiceDocumentForm) form;
        customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
        
        try {
            // proceed as usual
            customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
            ActionForward result = super.execute(mapping, form, request, response);
            return result;
        }
        finally {
            //update it again for display purposes
            customerInvoiceDocumentForm.getCustomerInvoiceDocument().updateDiscountAndParentLineReferences();
        }

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
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new RecalculateCustomerInvoiceDetaiEvent(errorPath, customerInvoiceDocumentForm.getDocument(), customerInvoiceDetail));
        if (rulePassed) {

            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDetail);
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

            CustomerInvoiceDetail discountCustomerInvoiceDetail = SpringContext.getBean(CustomerInvoiceDetailService.class).getDiscountCustomerInvoiceDetailForCurrentYear(parentCustomerInvoiceDetail);
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(discountCustomerInvoiceDetail);
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
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) customerInvoiceDocumentForm.getNewSourceLine();

        // make sure amount is up to date before rules
        customerInvoiceDetail.updateAmountBasedOnQuantityAndUnitPrice();

        boolean rulePassed = true;
        // check any business rules
        rulePassed &= SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(KFSConstants.NEW_SOURCE_ACCT_LINE_PROPERTY_NAME, customerInvoiceDocumentForm.getDocument(), customerInvoiceDetail));

        if (rulePassed) {

            // add accountingLine
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(customerInvoiceDetail);
            CustomerInvoiceDetailService customerInvoiceDetailService = SpringContext.getBean(CustomerInvoiceDetailService.class);
            customerInvoiceDetailService.recalculateCustomerInvoiceDetail(customerInvoiceDetail);            
            customerInvoiceDetail.updateARObjectCode();
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
}
