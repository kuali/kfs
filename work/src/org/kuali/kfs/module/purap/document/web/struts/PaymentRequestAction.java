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
package org.kuali.module.purap.web.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.rule.event.CalculateAccountsPayableEvent;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.util.PurQuestionCallback;
import org.kuali.module.purap.web.struts.form.PaymentRequestForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
 */
public class PaymentRequestAction extends AccountsPayableActionBase {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestAction.class);
    
    /**
     * Do initialization for a new requisition
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        
        super.createDocument(kualiDocumentFormBase);
        
        ((PaymentRequestDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) preqForm.getDocument();
                
        return super.refresh(mapping, form, request, response);
    }
    
    
    public ActionForward continuePREQ(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("continuePREQ() method");

        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) preqForm.getDocument();

        // preform duplicate check which will forward to a question prompt if one is found
        ActionForward forward = performDuplicatePaymentRequestCheck(mapping, form, request, response, paymentRequestDocument);
        if (forward != null) {
            return forward;
        }

        // If we are here either there was no duplicate or there was a duplicate and the user hits continue, in either case we need
        // to validate the business rules
        /// paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription("dummy data to pass the business rule");
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new ContinueAccountsPayableEvent(paymentRequestDocument));

        if (rulePassed) {
            paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
            SpringServiceLocator.getPaymentRequestService().populatePaymentRequest(paymentRequestDocument);
           /// SpringServiceLocator.getPaymentRequestService().save(paymentRequestDocument);
            
        }
        else {
            paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
        }

        paymentRequestDocument.refreshAllReferences();
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }
    
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearInitValues() method");

        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) preqForm.getDocument();
        paymentRequestDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }
    
    
    /**
     * Calls PaymentRequestService to perform the duplicate payment request check. If one is found, a question is setup and control is
     * forwarded to the question action method. Coming back from the question prompt the button that was clicked is checked and if
     * 'no' was selected they are forward back to the page still in init mode.
     */
    private ActionForward performDuplicatePaymentRequestCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, PaymentRequestDocument paymentRequestDocument) throws Exception {
        ActionForward forward = null;
        HashMap<String, String> duplicateMessages = SpringServiceLocator.getPaymentRequestService().paymentRequestDuplicateMessages(paymentRequestDocument);
        if (!duplicateMessages.isEmpty()){
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                return this.performQuestionWithoutInput(mapping, form, request, response, PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessages.get(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION) , KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }
    
    
    
    /**
     * The execute method is being overriden to reevaluate on each call 
     * which extra buttons to display.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
                                       
        ActionForward action = super.execute(mapping, form, request, response);
                
        //generate the extra buttons
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument preq = preqForm.getPaymentRequestDocument();

        //We have to do this because otherwise the paymentrequest on the item is null
        //once we get the constraints removed to allow saving on continue
        //see KULPURAP-825 for some related comments
        preq.fixPreqItemReference();
        
        return action;
    }
    
    /**
     * This action puts a payment on hold, prompting for a reason before hand.
     * This stops further approvals or routing.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward addHoldOnPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {    
        String operation = "Hold ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public void doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                SpringServiceLocator.getPaymentRequestService().addHoldOnPaymentRequest((PaymentRequestDocument) document, noteText);
    }
        };
    
        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.HOLD_PREQ_QUESTION, PREQDocumentsStrings.HOLD_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_HOLD_DOCUMENT, callback);
    }
    
    /**
     * This action removes a hold on the PREQ.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeHoldFromPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {        
        String operation = "Remove ";
        
        PurQuestionCallback callback = new PurQuestionCallback() {
            public void doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                SpringServiceLocator.getPaymentRequestService().removeHoldOnPaymentRequest((PaymentRequestDocument) document, noteText);
    }
        };
        
        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.REMOVE_HOLD_PREQ_QUESTION, PREQDocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_REMOVE_HOLD_DOCUMENT, callback);
    }

    /**
     * This action requests a cancel on a preq, prompting for a reason before hand.
     * This stops further approvals or routing.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward requestCancelOnPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {    
        String operation = "Cancel ";
    
        PurQuestionCallback callback = new PurQuestionCallback() {
            public void doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                SpringServiceLocator.getPaymentRequestService().requestCancelOnPaymentRequest((PaymentRequestDocument) document, noteText);
    }
        };

        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.CANCEL_PREQ_QUESTION, PREQDocumentsStrings.CANCEL_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_CANCEL_DOCUMENT, callback);
    }

    /**
     * This action removes a request for cancel on a PREQ.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward removeCancelRequestFromPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Cancel ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public void doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                SpringServiceLocator.getPaymentRequestService().removeRequestCancelOnPaymentRequest((PaymentRequestDocument) document, noteText);
        }
        };
    
        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.REMOVE_CANCEL_PREQ_QUESTION, PREQDocumentsStrings.REMOVE_CANCEL_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_REMOVE_CANCEL_DOCUMENT, callback);
                    }
        
    /**
     * calls a service method to calculate for a payment request document
     */
    @Override
    protected void customCalculate(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDoc = (PaymentRequestDocument)apDoc;
        //set amounts on any empty
        preqDoc.updateExtendedPriceOnItems();
        //notice we're ignoring whether the boolean, because these are just warnings they shouldn't halt anything
        SpringServiceLocator.getKualiRuleService().applyRules(new CalculateAccountsPayableEvent(preqDoc));        

        SpringServiceLocator.getPaymentRequestService().calculatePaymentRequest(preqDoc, true);
    }

        

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    /*
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // TODO Auto-generated method stub
        LOG.debug("save() method");

        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) preqForm.getDocument();
        
        SpringServiceLocator.getPaymentRequestService().save(document);
        return super.save(mapping, form, request, response);
    }
    */
}