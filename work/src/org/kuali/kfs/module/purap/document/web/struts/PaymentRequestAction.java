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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.rule.event.CalculateAccountsPayableEvent;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.PurQuestionCallback;
import org.kuali.module.purap.web.struts.form.PaymentRequestForm;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.vendor.bo.VendorDetail;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Struts Action for Payment Request document.
 */
public class PaymentRequestAction extends AccountsPayableActionBase {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestAction.class);

    /**
     * Do initialization for a new payment request.
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

    /**
     * Executes the continue action on a payment request. Populates and initializes the rest of the payment request besides what was
     * shown on the init screen.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
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
        SpringContext.getBean(PaymentRequestService.class).populateAndSavePaymentRequest(paymentRequestDocument);

        // force calculation
        preqForm.setCalculated(false);

        // sort below the line
        SpringContext.getBean(PurapService.class).sortBelowTheLine(paymentRequestDocument);

        // update the counts on the form
        preqForm.updateItemCounts();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) preqForm.getDocument();

        //set the last action performed to find out who was the last to route
        paymentRequestDocument.setLastActionPerformedByUniversalUserId( GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier() );
        
        return super.route(mapping, form, request, response);
    }
    
    /**
     * Clears the initial fields on the <code>PaymentRequestDocument</code> which should be accessible from the given form.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm, which must be a PaymentRequestForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearInitValues() method");
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) preqForm.getDocument();
        paymentRequestDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Calls <code>PaymentRequestService</code> to perform the duplicate payment request check. If one is found, a question is
     * setup and control is forwarded to the question action method. Coming back from the question prompt the button that was
     * clicked is checked and if 'no' was selected they are forward back to the page still in init mode.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param paymentRequestDocument The PaymentRequestDocument
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.module.purap.service.PaymentRequestService
     */
    private ActionForward performDuplicatePaymentRequestCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, PaymentRequestDocument paymentRequestDocument) throws Exception {
        ActionForward forward = null;
        HashMap<String, String> duplicateMessages = SpringContext.getBean(PaymentRequestService.class).paymentRequestDuplicateMessages(paymentRequestDocument);
        if (!duplicateMessages.isEmpty()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {

                return this.performQuestionWithoutInput(mapping, form, request, response, PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessages.get(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
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
     * Puts a payment on hold, prompting for a reason beforehand. This stops further approvals or routing.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward addHoldOnPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Hold ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                document = SpringContext.getBean(PaymentRequestService.class).addHoldOnPaymentRequest((PaymentRequestDocument) document, noteText);
                return document;
            }
        };

        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.HOLD_PREQ_QUESTION, PREQDocumentsStrings.HOLD_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_HOLD_DOCUMENT, callback);
    }

    /**
     * Removes a hold on the payment request.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward removeHoldFromPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Remove ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                document = SpringContext.getBean(PaymentRequestService.class).removeHoldOnPaymentRequest((PaymentRequestDocument) document, noteText);
                return document;
            }
        };

        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.REMOVE_HOLD_PREQ_QUESTION, PREQDocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_REMOVE_HOLD_DOCUMENT, callback);
    }

    /**
     * This action requests a cancel on a preq, prompting for a reason before hand. This stops further approvals or routing.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward requestCancelOnPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Cancel ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                SpringContext.getBean(PaymentRequestService.class).requestCancelOnPaymentRequest((PaymentRequestDocument) document, noteText);
                return document;
            }
        };

        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.CANCEL_PREQ_QUESTION, PREQDocumentsStrings.CANCEL_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_CANCEL_DOCUMENT, callback);
    }

    /**
     * @see org.kuali.module.purap.web.struts.action.AccountsPayableActionBase#cancelPOActionCallbackMethod()
     */
    @Override
    protected PurQuestionCallback cancelPOActionCallbackMethod() {

        return new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                PaymentRequestDocument preqDocument = (PaymentRequestDocument) document;
                preqDocument.setReopenPurchaseOrderIndicator(true);
                return preqDocument;
            }
        };
    }

    /**
     * Removes a request for cancel on a payment request.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward removeCancelRequestFromPayment(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Cancel ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                SpringContext.getBean(PaymentRequestService.class).removeRequestCancelOnPaymentRequest((PaymentRequestDocument) document, noteText);
                return document;
            }
        };

        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.REMOVE_CANCEL_PREQ_QUESTION, PREQDocumentsStrings.REMOVE_CANCEL_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_REMOVE_CANCEL_DOCUMENT, callback);
    }

    /**
     * Calls a service method to calculate for a payment request document.
     * 
     * @param apDoc The AccountsPayableDocument
     */
    @Override
    protected void customCalculate(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDoc = (PaymentRequestDocument) apDoc;
        // set amounts on any empty
        preqDoc.updateExtendedPriceOnItems();

        // notice we're ignoring whether the boolean, because these are just warnings they shouldn't halt anything
        SpringContext.getBean(KualiRuleService.class).applyRules(new CalculateAccountsPayableEvent(preqDoc));
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc, true);
    }

    /**
     * @see org.kuali.module.purap.web.struts.action.AccountsPayableActionBase#getActionName()
     */
    @Override
    public String getActionName() {
        return PurapConstants.PAYMENT_REQUEST_ACTION_NAME;
    }
    
    public ActionForward useAlternateVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) preqForm.getDocument();
                        
        SpringContext.getBean(PaymentRequestService.class).changeVendor(
                document, document.getAlternateVendorHeaderGeneratedIdentifier(), document.getAlternateVendorDetailAssignedIdentifier());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward useOriginalVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) preqForm.getDocument();

        SpringContext.getBean(PaymentRequestService.class).changeVendor(
                document, document.getOriginalVendorHeaderGeneratedIdentifier(), document.getOriginalVendorDetailAssignedIdentifier());
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}