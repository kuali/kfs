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
import org.kuali.core.UserSession;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.rule.event.CalculateAccountsPayableEvent;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurapAccountingService;
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
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new ContinueAccountsPayableEvent(paymentRequestDocument));

        if (rulePassed) {
            SpringContext.getBean(PaymentRequestService.class).populateAndSavePaymentRequest(paymentRequestDocument);
        }

        //force calculation
        preqForm.setCalculated(false);

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
                SpringContext.getBean(PaymentRequestService.class).addHoldOnPaymentRequest((PaymentRequestDocument) document, noteText);
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
                SpringContext.getBean(PaymentRequestService.class).removeHoldOnPaymentRequest((PaymentRequestDocument) document, noteText);
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
                SpringContext.getBean(PaymentRequestService.class).requestCancelOnPaymentRequest((PaymentRequestDocument) document, noteText);
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
                SpringContext.getBean(PaymentRequestService.class).removeRequestCancelOnPaymentRequest((PaymentRequestDocument) document, noteText);
            }
        };

        return askQuestionWithInput(mapping, form, request, response, PREQDocumentsStrings.REMOVE_CANCEL_PREQ_QUESTION, PREQDocumentsStrings.REMOVE_CANCEL_NOTE_PREFIX, operation, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_REMOVE_CANCEL_DOCUMENT, callback);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) preqForm.getDocument();
        if (document.getDocumentHeader().hasWorkflowDocument()) {
            if ((document.getDocumentHeader().getWorkflowDocument().stateIsProcessed()) || (document.getDocumentHeader().getWorkflowDocument().stateIsFinal())) {
                // TODO delyea - call custom cancel in the service
            }
            else if (document.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
                // one way or another we will have to fake the user session so get the current one
                UserSession originalUserSession = GlobalVariables.getUserSession();
                if (document.isPaymentRequestedCancelIndicator()) {
                    // need to run a disapprove to cancel the document
                    UniversalUser user = document.getLastActionPerformedByUser();
                    if (ObjectUtils.isNull(user)) {
                        // throw error
                        String errorMsg = "Could not find valid 'last action performed' user for universal user id " + document.getLastActionPerformedByUniversalUserId();
                        LOG.error("cancel() " + errorMsg);
                        throw new RuntimeException(errorMsg);
                    }
                    ActionForward actionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                    try {
                        // need to run the disapprove as the user who requested the document be canceled
                        GlobalVariables.setUserSession(new UserSession(user.getPersonUserIdentifier()));
                        PaymentRequestForm newPreqForm = (PaymentRequestForm)ObjectUtils.deepCopy(preqForm);
                        newPreqForm.setDocument(SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber()));
                        actionForward = super.disapprove(mapping, newPreqForm, request, response);
                    }
                    finally {
                        GlobalVariables.setUserSession(originalUserSession);
                    }
                    return actionForward;
                }
                else {
                    ActionForward actionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                    try {
                        // need to run a super user cancel since person canceling may not have an action requested on the document
                        GlobalVariables.setUserSession(new UserSession(PurapConstants.SYSTEM_AP_USER));
                        SpringContext.getBean(DocumentService.class).superUserCancelDocument(SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(document.getDocumentNumber()), "Document Cancelled by user " + originalUserSession.getUniversalUser().getPersonName() + " (" + originalUserSession.getUniversalUser().getPersonUserIdentifier() + ")");
                    }
                    finally {
                        GlobalVariables.setUserSession(originalUserSession);
                    }
                    return actionForward;
                }
            }
        }
        return super.cancel(mapping, form, request, response);
    }

    /**
     * calls a service method to calculate for a payment request document
     */
    @Override
    protected void customCalculate(AccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDoc = (PaymentRequestDocument) apDoc;
        //set amounts on any empty
        preqDoc.updateExtendedPriceOnItems();
        //notice we're ignoring whether the boolean, because these are just warnings they shouldn't halt anything
        SpringContext.getBean(KualiRuleService.class).applyRules(new CalculateAccountsPayableEvent(preqDoc));

        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc, true);
        // TODO Chris - an updateAccountAmounts is done at the end of the above method... need it here? (I don't think so)
//        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(apDoc);
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
     
     SpringContext.getBean(PaymentRequestService.class).save(document);
     return super.save(mapping, form, request, response);
     }
     */
}