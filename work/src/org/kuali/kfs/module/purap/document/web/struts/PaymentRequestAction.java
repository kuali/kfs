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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.CalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.util.PurQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * Struts Action for Payment Request document.
 */
public class PaymentRequestAction extends AccountsPayableActionBase {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestAction.class);

    /**
     * Do initialization for a new payment request.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((PaymentRequestDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
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
        // TODO figure out a more straightforward way to do this.  ailish put this in so the link id would be set and the perm check would work
        paymentRequestDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier()).getAccountsPayablePurchasingDocumentLinkIdentifier());

        //TODO hjs-check to see if user is allowed to initiate doc based on PO sensitive data (add this to all other docs except acm doc)
        if (!SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(paymentRequestDocument).isAuthorizedByTemplate(paymentRequestDocument, KNSConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, GlobalVariables.getUserSession().getPrincipalId())) {
            throw buildAuthorizationException("initiate document", paymentRequestDocument);
        }
        
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

        //TODO if better, move this to the action just before preq goes into ATAX status
        // force calculation for tax
        preqForm.setCalculatedTax(false);
        
        // sort below the line
        SpringContext.getBean(PurapService.class).sortBelowTheLine(paymentRequestDocument);

        // update the counts on the form
        preqForm.updateItemCounts();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
     * @see org.kuali.kfs.module.purap.document.service.PaymentRequestService
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
     * @see org.kuali.kfs.module.purap.document.web.struts.AccountsPayableActionBase#cancelPOActionCallbackMethod()
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
    protected void customCalculate(PurchasingAccountsPayableDocument apDoc) {
        PaymentRequestDocument preqDoc = (PaymentRequestDocument) apDoc;
        // set amounts on any empty
        preqDoc.updateExtendedPriceOnItems();

        // notice we're ignoring whether the boolean, because these are just warnings they shouldn't halt anything
        SpringContext.getBean(KualiRuleService.class).applyRules(new CalculateAccountsPayableEvent(preqDoc));
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc, true);

    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.AccountsPayableActionBase#getActionName()
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
    
    public ActionForward route (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestDocument preq = ((PaymentRequestForm)form).getPaymentRequestDocument();
        
        if (preq.isClosePurchaseOrderIndicator()) {
            PurchaseOrderDocument po = preq.getPurchaseOrderDocument();
            if (po.canClosePOForTradeIn()) {
                return super.route(mapping, form, request, response);
            }
            else {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        else {
            // if tax is not yet calculated, return and prompt user to calculate
            if (requiresCalculateTax((PaymentRequestForm)form)) {
                GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_APPROVE_REQUIRES_CALCULATE);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            return super.route(mapping, form, request, response);
        }
    }

    /**
     * Checks if tax calculation is required. 
     * Currently it is required when preq is awaiting for tax approval and tax has not already been calculated. 
     * 
     * @param apForm A Form, which must inherit from <code>AccountsPayableFormBase</code>
     * @return true if calculation is required, false otherwise
     */
    protected boolean requiresCalculateTax(PaymentRequestForm preqForm) {
        boolean requiresCalculateTax = true;
        PaymentRequestDocument preq = (PaymentRequestDocument) preqForm.getDocument();
        requiresCalculateTax = !preqForm.isCalculatedTax() && preq.getStatusCode().equalsIgnoreCase("ATAX");
        return requiresCalculateTax;
    }
    
}
