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
import org.kuali.core.bo.Note;
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
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.web.struts.form.PaymentRequestForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
 */
public class PaymentRequestAction extends AccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestAction.class);
    
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
        Map editMode = preqForm.getEditingMode();
        
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
       
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();     
        PaymentRequestService paymentRequestService = SpringServiceLocator.getPaymentRequestService();
        
        Integer poId = paymentRequestDocument.getPurchaseOrderIdentifier();
        PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier());
        
        if (ObjectUtils.isNotNull(purchaseOrderDocument)) {
            HashMap<String, String> duplicateMessages = paymentRequestService.paymentRequestDuplicateMessages(paymentRequestDocument);
            
            if (!duplicateMessages.isEmpty()){
      
                if (question == null) {
                  // ask question if not already asked
                  return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessages.get(PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION) , KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
    
                } 
                
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
               
                if ((PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                    
                    // if no button clicked just reload the doc in the INITIATE status and let the user to change the input values
                   
                    paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
                    //editMode.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "TRUE");
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                 }
            }
        }
        
        // If we are here either there was no duplicate or there was a duplicate and the user hits continue, in either case we need to validate the business rules
        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription("dummy data to pass the business rule");
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new ContinueAccountsPayableEvent(paymentRequestDocument));        
        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription(null);
        
        if (rulePassed) {           
            paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument);
            paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
            paymentRequestDocument.refreshAllReferences();

            //editMode.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "FALSE");    
        } else {
            paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
        }
        
        //If the list of closed/expired accounts is not empty add a warning and add a note for the close / epired accounts which get replaced
        //HashMap<String, String> expiredOrClosedAccounts = paymentRequestService.expiredOrClosedAccountsList(paymentRequestDocument);
        //TODO: Chris finish above method for now just set to empty
        HashMap<String, String> expiredOrClosedAccounts = new HashMap<String,String>();
        
        if (!expiredOrClosedAccounts.isEmpty()){
            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED);
            paymentRequestService.addContinuationAccountsNote(paymentRequestDocument, expiredOrClosedAccounts);
        }
                
        return super.refresh(mapping, form, request, response);
        //return mapping.findForward(KFSConstants.MAPPING_PORTAL);
  
    }
    
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearInitValues() method");

        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) preqForm.getDocument();
        paymentRequestDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
        //return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
        preqForm.showButtons();
        
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
        LOG.debug("addHoldOnPayment() method");

        String operation = "Hold ";
        return askHoldQuestion(mapping, form, request, response, PREQDocumentsStrings.HOLD_PREQ_QUESTION, PREQDocumentsStrings.CONFIRM_HOLD_QUESTION, PurapConstants.PAYMENT_REQUEST_DOCUMENT, PREQDocumentsStrings.HOLD_NOTE_PREFIX, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_HOLD_DOCUMENT, operation);
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
        
        LOG.debug("removeHoldFromPayment() method");

        PaymentRequestForm preqForm = (PaymentRequestForm) form;
        PaymentRequestDocument document = (PaymentRequestDocument) preqForm.getDocument();

        SpringServiceLocator.getPaymentRequestService().removeHoldOnPaymentRequest(document);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method prompts for a reason to hold a preq.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param questionType
     * @param confirmType
     * @param noteTextIntro
     * @param messageType
     * @param notePrefixType
     * @param mappingType
     * @return
     * @throws Exception
     */
    private ActionForward askHoldQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {

        LOG.debug("askHoldQuestion started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PaymentRequestDocument preqDocument = (PaymentRequestDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Start in logic for confirming the close.
        if (question == null) {
            String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PAYMENT_REQUEST_QUESTION_DOCUMENT);
            String message = StringUtils.replace(key, "{0}", operation);

            // Ask question if not already asked.
            return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                // If 'No' is the button clicked, just reload the doc
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {
                // This is the case when the user clicks on "OK" in the end.
                // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
                return mapping.findForward(KFSConstants.MAPPING_PORTAL);
            }
            else {
                // Have to check length on value entered.
                String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

                // Build out full message.
                noteText = introNoteMessage + reason;
                int noteTextLength = noteText.length();

                // Get note text max length from DD.
                int noteTextMaxLength = SpringServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

                if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                    // Figure out exact number of characters that the user can enter.
                    int reasonLimit = noteTextMaxLength - noteTextLength;

                    if (reason == null) {
                        // Prevent a NPE by setting the reason to a blank string.
                        reason = "";
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PAYMENT_REQUEST_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PAYMENT_REQUEST_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                }
            }
        }
        
        //set the hold indicator on the preq
        SpringServiceLocator.getPaymentRequestService().addHoldOnPaymentRequest(preqDocument, noteText);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
    LOG.debug("requestCancelOnPayment() method");

    String operation = "Hold ";
    return askCancelQuestion(mapping, form, request, response, PREQDocumentsStrings.CANCEL_PREQ_QUESTION, PREQDocumentsStrings.CONFIRM_CANCEL_QUESTION, PurapConstants.PAYMENT_REQUEST_DOCUMENT, PREQDocumentsStrings.CANCEL_NOTE_PREFIX, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_CANCEL_DOCUMENT, operation);
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
    
    LOG.debug("removeCancelRequestFromPayment() method");

    String operation = "Cancel ";
    return askRemoveCancelQuestion(mapping, form, request, response, PREQDocumentsStrings.REMOVE_CANCEL_PREQ_QUESTION, PREQDocumentsStrings.CONFIRM_REMOVE_CANCEL_QUESTION, PurapConstants.PAYMENT_REQUEST_DOCUMENT, PREQDocumentsStrings.REMOVE_CANCEL_NOTE_PREFIX, PurapKeyConstants.PAYMENT_REQUEST_MESSAGE_REMOVE_CANCEL_DOCUMENT, operation);
}

/**
 * This method prompts for a reason to request to cancel a preq.
 * 
 * @param mapping
 * @param form
 * @param request
 * @param response
 * @param questionType
 * @param confirmType
 * @param noteTextIntro
 * @param messageType
 * @param notePrefixType
 * @param mappingType
 * @return
 * @throws Exception
 */
private ActionForward askCancelQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {

    LOG.debug("askCancelQuestion started.");
    KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
    PaymentRequestDocument preqDocument = (PaymentRequestDocument) kualiDocumentFormBase.getDocument();
    Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
    String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
    String noteText = "";

    KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

    // Start in logic for confirming the close.
    if (question == null) {
        String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PAYMENT_REQUEST_QUESTION_DOCUMENT);
        String message = StringUtils.replace(key, "{0}", operation);

        // Ask question if not already asked.
        return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
    }
    else {
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
            // If 'No' is the button clicked, just reload the doc
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {
            // This is the case when the user clicks on "OK" in the end.
            // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
            return mapping.findForward(KFSConstants.MAPPING_PORTAL);
        }
        else {
            // Have to check length on value entered.
            String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

            // Build out full message.
            noteText = introNoteMessage + reason;
            int noteTextLength = noteText.length();

            // Get note text max length from DD.
            int noteTextMaxLength = SpringServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

            if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                // Figure out exact number of characters that the user can enter.
                int reasonLimit = noteTextMaxLength - noteTextLength;

                if (reason == null) {
                    // Prevent a NPE by setting the reason to a blank string.
                    reason = "";
                }
                return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PAYMENT_REQUEST_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PAYMENT_REQUEST_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }
    }
    
    //set the cancel indicator on the preq
    SpringServiceLocator.getPaymentRequestService().requestCancelOnPaymentRequest(preqDocument, noteText);
    
    return mapping.findForward(KFSConstants.MAPPING_BASIC);
}

/**
 * This method prompts for a reason to remove a request to cancel a preq.
 * 
 * @param mapping
 * @param form
 * @param request
 * @param response
 * @param questionType
 * @param confirmType
 * @param noteTextIntro
 * @param messageType
 * @param notePrefixType
 * @param mappingType
 * @return
 * @throws Exception
 */
private ActionForward askRemoveCancelQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {

    LOG.debug("askRemoveCancelQuestion started.");
    KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
    PaymentRequestDocument preqDocument = (PaymentRequestDocument) kualiDocumentFormBase.getDocument();
    Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
    String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
    String noteText = "";

    KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

    // Start in logic for confirming the close.
    if (question == null) {
        String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PAYMENT_REQUEST_QUESTION_DOCUMENT);
        String message = StringUtils.replace(key, "{0}", operation);

        // Ask question if not already asked.
        return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
    }
    else {
        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
            // If 'No' is the button clicked, just reload the doc
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {
            // This is the case when the user clicks on "OK" in the end.
            // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
            return mapping.findForward(KFSConstants.MAPPING_PORTAL);
        }
        else {
            // Have to check length on value entered.
            String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

            // Build out full message.
            noteText = introNoteMessage + reason;
            int noteTextLength = noteText.length();

            // Get note text max length from DD.
            int noteTextMaxLength = SpringServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

            if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                // Figure out exact number of characters that the user can enter.
                int reasonLimit = noteTextMaxLength - noteTextLength;

                if (reason == null) {
                    // Prevent a NPE by setting the reason to a blank string.
                    reason = "";
                }
                return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PAYMENT_REQUEST_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PAYMENT_REQUEST_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }
    }
    
    //unset the cancel indicator on the preq
    SpringServiceLocator.getPaymentRequestService().removeRequestCancelOnPaymentRequest(preqDocument, noteText);
    
    return mapping.findForward(KFSConstants.MAPPING_BASIC);
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