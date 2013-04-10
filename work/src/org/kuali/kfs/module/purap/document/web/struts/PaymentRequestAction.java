/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedPreCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.PurQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

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

        boolean poNotNull = true;

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedContinuePurapEvent(paymentRequestDocument));
        if (!rulePassed){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);

      //check for a po id
        if (ObjectUtils.isNull(paymentRequestDocument.getPurchaseOrderIdentifier())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            poNotNull = false;
        }

        if (ObjectUtils.isNull(paymentRequestDocument.getInvoiceDate())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.INVOICE_DATE, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.INVOICE_DATE);
            poNotNull = false;
        }

        if (ObjectUtils.isNull(paymentRequestDocument.getInvoiceNumber())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.INVOICE_NUMBER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.INVOICE_NUMBER);
            poNotNull = false;
        }
        paymentRequestDocument.setInvoiceNumber(paymentRequestDocument.getInvoiceNumber().toUpperCase());

        if (ObjectUtils.isNull(paymentRequestDocument.getVendorInvoiceAmount())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_INVOICE_AMOUNT, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.VENDOR_INVOICE_AMOUNT);
            poNotNull = false;
        }

        //exit early as the po is null, no need to proceed further until this is taken care of
        if(poNotNull == false){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }


        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier());
        if (ObjectUtils.isNotNull(po)) {
            // TODO figure out a more straightforward way to do this.  ailish put this in so the link id would be set and the perm check would work
            paymentRequestDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

            //check to see if user is allowed to initiate doc based on PO sensitive data
            if (!SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(paymentRequestDocument).isAuthorizedByTemplate(paymentRequestDocument, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, GlobalVariables.getUserSession().getPrincipalId())) {
                throw buildAuthorizationException("initiate document", paymentRequestDocument);
            }
        }

        if(!SpringContext.getBean(PaymentRequestService.class).isPurchaseOrderValidForPaymentRequestDocumentCreation(paymentRequestDocument,po))
        {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // perform duplicate check which will forward to a question prompt if one is found
        ActionForward forward = performDuplicatePaymentRequestAndEncumberFiscalYearCheck(mapping, form, request, response, paymentRequestDocument);
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
     * This method runs two checks based on the user input on PREQ initiate screen: Encumber next fiscal year check and Duplicate
     * payment request check. Encumber next fiscal year is checked first and will display a warning message to the user if it's the
     * case. Duplicate payment request check calls <code>PaymentRequestService</code> to perform the duplicate payment request
     * check. If one is found, a question is setup and control is forwarded to the question action method. Coming back from the
     * question prompt the button that was clicked is checked and if 'no' was selected they are forward back to the page still in
     * init mode.
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
    protected ActionForward performDuplicatePaymentRequestAndEncumberFiscalYearCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, PaymentRequestDocument paymentRequestDocument) throws Exception {
        ActionForward forward = null;
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) {
            // perform encumber next fiscal year check and prompt warning message if needs
            if (isEncumberNextFiscalYear(paymentRequestDocument)) {
                String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.WARNING_ENCUMBER_NEXT_FY);
                return this.performQuestionWithoutInput(mapping, form, request, response, PREQDocumentsStrings.ENCUMBER_NEXT_FISCAL_YEAR_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }
            else {
                // perform duplicate payment request check
                HashMap<String, String> duplicateMessages = SpringContext.getBean(PaymentRequestService.class).paymentRequestDuplicateMessages(paymentRequestDocument);
                if (!duplicateMessages.isEmpty()) {
                    return this.performQuestionWithoutInput(mapping, form, request, response, PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessages.get(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
                }
            }
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            // If the user replies 'Yes' to the encumber-next-year-question, proceed with duplicate payment check
            if (PurapConstants.PREQDocumentsStrings.ENCUMBER_NEXT_FISCAL_YEAR_QUESTION.equals(question) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                HashMap<String, String> duplicateMessages = SpringContext.getBean(PaymentRequestService.class).paymentRequestDuplicateMessages(paymentRequestDocument);
                if (!duplicateMessages.isEmpty()) {
                    return this.performQuestionWithoutInput(mapping, form, request, response, PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessages.get(PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
                }
            }
            // If the user replies 'No' to either of the questions, redirect to the PREQ initiate page.
            else if ((PurapConstants.PREQDocumentsStrings.ENCUMBER_NEXT_FISCAL_YEAR_QUESTION.equals(question) || PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                paymentRequestDocument.updateAndSaveAppDocStatus(PurapConstants.PaymentRequestStatuses.APPDOC_INITIATE);
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    /**
     * Check if the current PREQ encumber next fiscal year from PO document.
     *
     * @param paymentRequestDocument
     * @return
     */
    protected boolean isEncumberNextFiscalYear(PaymentRequestDocument paymentRequestDocument) {
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        if (paymentRequestDocument.getPurchaseOrderDocument().getPostingYear().intValue() > fiscalYear) {
            return true;
        }
        return false;
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
            @Override
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
            @Override
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
            @Override
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
//    @Override
//    protected PurQuestionCallback cancelPOActionCallbackMethod() {
//
//        return new PurQuestionCallback() {
//            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
//                PaymentRequestDocument preqDocument = (PaymentRequestDocument) document;
//                preqDocument.setReopenPurchaseOrderIndicator(true);
//                return preqDocument;
//            }
//        };
//    }

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
            @Override
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

        // calculation just for the tax area, only at tax review stage
        // by now, the general calculation shall have been done.
        if (StringUtils.equals(preqDoc.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW)) {
            SpringContext.getBean(PaymentRequestService.class).calculateTaxArea(preqDoc);
            return;
        }

        // notice we're ignoring whether the boolean, because these are just warnings they shouldn't halt anything
        //Calculate Payment request before rules since the rule check totalAmount.
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc, true);
        SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedCalculateAccountsPayableEvent(preqDoc));
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

    @Override
    public ActionForward route (ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestDocument preq = ((PaymentRequestForm)form).getPaymentRequestDocument();
        SpringContext.getBean(PurapService.class).prorateForTradeInAndFullOrderDiscount(preq);
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(preq);
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
            return super.route(mapping, form, request, response);
        }
    }

    /**
     * Overrides to invoke the updateAccountAmounts so that the account percentage will be
     * correctly updated before validation for account percent is called.
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestDocument preq = ((PaymentRequestForm)form).getPaymentRequestDocument();

        SpringContext.getBean(PurapService.class).prorateForTradeInAndFullOrderDiscount(preq);
        // if tax is required but not yet calculated, return and prompt user to calculate
        if (requiresCalculateTax((PaymentRequestForm)form)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_APPROVE_REQUIRES_CALCULATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // enforce calculating tax again upon approval, just in case user changes tax data without calculation
        // other wise there will be a loophole, because the taxCalculated indicator is already set upon first calculation
        // and thus system wouldn't know it's not re-calculated after tax data are changed
        if (SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedPreCalculateAccountsPayableEvent(preq))) {
            // pre-calculation rules succeed, calculate tax again and go ahead with approval
            customCalculate(preq);
            SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(preq);
            return super.approve(mapping, form, request, response);
        }
        else {
            // pre-calculation rules fail, go back to same page with error messages
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        PaymentRequestDocument preq = (PaymentRequestDocument) preqForm.getDocument();
        boolean requiresCalculateTax = StringUtils.equals(preq.getApplicationDocumentStatus(), PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW) && !preqForm.isCalculatedTax();
        return requiresCalculateTax;
    }

    public ActionForward changeUseTaxIndicator(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableDocument document = (PurchasingAccountsPayableDocument) ((PurchasingAccountsPayableFormBase) form).getDocument();

        //clear/recalculate tax and recreate GL entries
        SpringContext.getBean(PurapService.class).updateUseTaxIndicator(document, !document.isUseTaxIndicator());
        SpringContext.getBean(PurapService.class).calculateTax(document);

        //TODO: add recalculate GL entries hook here

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Calls service to clear tax info.
     */
    public ActionForward clearTaxInfo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm prForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) prForm.getDocument();

        PaymentRequestService taxService = SpringContext.getBean(PaymentRequestService.class);

        /* call service to clear previous lines */
        taxService.clearTax(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;

        PaymentRequestDocument preqDocument = (PaymentRequestDocument) preqForm.getDocument();

        ActionForward forward = mapping.findForward(RiceConstants.MAPPING_BASIC);
        if(preqDocument.getPurchaseOrderDocument().isPendingActionIndicator()) {
            GlobalVariables.getMessageMap().putError(
                    KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.DOCUMENT_NUMBER,
                    PurapKeyConstants.ERROR_PAYMENT_REQUEST_CANNOT_BE_CANCELLED);
        }
        else {
            forward = super.cancel(mapping, form, request, response);
        }

        return forward;
    }

    /**
     * For each quantity based item, set item quantity to ZERO.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clearQty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;

        PaymentRequestDocument preqDocument = (PaymentRequestDocument) preqForm.getDocument();

        for (PaymentRequestItem item : (List<PaymentRequestItem>) preqDocument.getItems()) {
            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
                item.setItemQuantity(null);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * For each quantity based item, loads total outstanding quantity into item quantity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadQty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PaymentRequestForm preqForm = (PaymentRequestForm) form;

        PaymentRequestDocument preqDocument = (PaymentRequestDocument) preqForm.getDocument();

        for (PaymentRequestItem item : (List<PaymentRequestItem>) preqDocument.getItems()) {

            if (item.getItemType().isQuantityBasedGeneralLedgerIndicator() && (item.getPoOutstandingQuantity() != null) && (item.getPoOutstandingQuantity().isGreaterThan(KualiDecimal.ZERO))) {
                item.setItemQuantity(item.getPoOutstandingQuantity());
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
