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

import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.RiceKeyConstants;
import org.kuali.core.UserSession;
import org.kuali.core.bo.Note;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.AccountsPayableDocumentStrings;
import org.kuali.module.purap.PurapConstants.CMDocumentsStrings;
import org.kuali.module.purap.document.AccountsPayableDocument;
import org.kuali.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.module.purap.rule.event.CancelAccountsPayableEvent;
import org.kuali.module.purap.rule.event.PreCalculateAccountsPayableEvent;
import org.kuali.module.purap.service.AccountsPayableDocumentSpecificService;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.PurQuestionCallback;
import org.kuali.module.purap.web.struts.form.AccountsPayableFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Struts Action for Accounts Payable documents.
 */
public class AccountsPayableActionBase extends PurchasingAccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableActionBase.class);

    /**
     * Performs refresh of objects after a lookup.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase baseForm = (AccountsPayableFormBase) form;
        AccountsPayableDocumentBase document = (AccountsPayableDocumentBase) baseForm.getDocument();

        if (StringUtils.equals(baseForm.getRefreshCaller(), VendorConstants.VENDOR_ADDRESS_LOOKUPABLE_IMPL)) {
            if (StringUtils.isNotBlank(request.getParameter(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_ADDRESS_ID))) {
                Integer vendorAddressGeneratedId = document.getVendorAddressGeneratedIdentifier();
                VendorAddress refreshVendorAddress = new VendorAddress();
                refreshVendorAddress.setVendorAddressGeneratedIdentifier(vendorAddressGeneratedId);
                refreshVendorAddress = (VendorAddress) SpringContext.getBean(BusinessObjectService.class).retrieve(refreshVendorAddress);
                document.templateVendorAddress(refreshVendorAddress);
            }
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Checks the continuation account indicator and generates warnings if continuation accounts were used to replace original
     * accounts on the document.
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        AccountsPayableDocument document = (AccountsPayableDocument) kualiDocumentFormBase.getDocument();

        SpringContext.getBean(AccountsPayableService.class).generateExpiredOrClosedAccountWarning(document);
        SpringContext.getBean(AccountsPayableService.class).updateItemList(document);
        ((AccountsPayableFormBase) kualiDocumentFormBase).updateItemCounts();
    }

    /**
     * Perform calculation on item line.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return An ActionForward
     */
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) apForm.getDocument();

        // call precalculate
        if (SpringContext.getBean(KualiRuleService.class).applyRules(new PreCalculateAccountsPayableEvent(apDoc))) {
            customCalculate(apDoc);

            // doesn't really matter what happens above we still reset the calculate flag
            apForm.setCalculated(true);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * An overridable area to do calculate-specific tasks.
     * 
     * @param apDoc An AccountsPayableDocument
     */
    protected void customCalculate(AccountsPayableDocument apDoc) {
        // do nothing by default
    }

    /**
     * Checks if calculation is required. Currently it is required when it has not already been calculated and full document entry
     * status has not already passed.
     * 
     * @param apForm A Form, which must inherit from <code>AccountsPayableFormBase</code>
     * @return true if calculation is required, false otherwise
     */
    protected boolean requiresCaculate(AccountsPayableFormBase apForm) {
        boolean requiresCalculate = true;
        PurchasingAccountsPayableDocument purapDocument = (PurchasingAccountsPayableDocument) apForm.getDocument();
        requiresCalculate = !apForm.isCalculated() && !SpringContext.getBean(PurapService.class).isFullDocumentEntryCompleted(purapDocument);

        return requiresCalculate;
    }

    /**
     * Returns the current action name.
     * 
     * @return A String. Set to null!
     */
    public String getActionName() {

        return null;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;

        // if form is not yet calculated, return and prompt user to calculate
        if (requiresCaculate(apForm)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_APPROVE_REQUIRES_CALCULATE);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // recalculate
        customCalculate((AccountsPayableDocument) apForm.getDocument());

        // route
        ActionForward forward = super.route(mapping, form, request, response);

        // if successful, then redirect back to init
        if (GlobalVariables.getMessageList().contains(RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL)) {
            String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.DOC_HANDLER_METHOD);
            parameters.put(KFSConstants.PARAMETER_COMMAND, "initiate");
            parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, apForm.getDocTypeName());

            String lookupUrl = UrlFactory.parameterizeUrl(basePath + "/" + "purap" + this.getActionName() + ".do", parameters);

            forward = new ActionForward(lookupUrl, true);
        }

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        if (!requiresCaculate(apForm)) {

            return super.save(mapping, form, request, response);
        }
        GlobalVariables.getErrorMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_SAVE_REQUIRES_CALCULATE);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * A wrapper method which prompts for a reason to hold a payment request or credit memo.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param questionType A String used to distinguish which question is being asked
     * @param notePrefix A String explaining what action was taken, to be prepended to the note containing the reason, which gets
     *        written to the document
     * @param operation A one-word String description of the action to be taken, to be substituted into the message. (Can be an
     *        empty String for some messages.)
     * @param messageKey A key to the message which will appear on the question screen
     * @param callback A PurQuestionCallback
     * @return An ActionForward
     * @throws Exception
     */
    protected ActionForward askQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String notePrefix, String operation, String messageKey, PurQuestionCallback callback) throws Exception {
        TreeMap<String, PurQuestionCallback> questionsAndCallbacks = new TreeMap<String, PurQuestionCallback>();
        questionsAndCallbacks.put(questionType, callback);

        return askQuestionWithInput(mapping, form, request, response, questionType, notePrefix, operation, messageKey, questionsAndCallbacks, "", mapping.findForward(KFSConstants.MAPPING_BASIC));
    }

    /**
     * Builds and asks questions which require text input by the user for a payment request or a credit memo.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param questionType A String used to distinguish which question is being asked
     * @param notePrefix A String explaining what action was taken, to be prepended to the note containing the reason, which gets
     *        written to the document
     * @param operation A one-word String description of the action to be taken, to be substituted into the message. (Can be an
     *        empty String for some messages.)
     * @param messageKey A (whole) key to the message which will appear on the question screen
     * @param questionsAndCallbacks A TreeMap associating the type of question to be asked and the type of callback which should
     *        happen in that case
     * @param messagePrefix The most general part of a key to a message text to be retrieved from KualiConfigurationService,
     *        Describes a collection of questions.
     * @param redirect An ActionForward to return to if done with questions
     * @return An ActionForward
     * @throws Exception
     */
    private ActionForward askQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String notePrefix, String operation, String messageKey, TreeMap<String, PurQuestionCallback> questionsAndCallbacks, String messagePrefix, ActionForward redirect) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        AccountsPayableDocumentBase apDocument = (AccountsPayableDocumentBase) kualiDocumentFormBase.getDocument();

        String question = (String) request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
        String firstQuestion = questionsAndCallbacks.firstKey();
        PurQuestionCallback callback = null;
        Iterator questions = questionsAndCallbacks.keySet().iterator();
        String mapQuestion = null;
        String key = null;

        // Start in logic for confirming the close.
        if (question == null) {
            key = getQuestionProperty(messageKey, messagePrefix, kualiConfiguration, firstQuestion);
            String message = StringUtils.replace(key, "{0}", operation);

            // Ask question if not already asked.
            return this.performQuestionWithInput(mapping, form, request, response, firstQuestion, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
        }
        else {
            // find callback for this question
            while (questions.hasNext()) {
                mapQuestion = (String) questions.next();

                if (StringUtils.equals(mapQuestion, question)) {
                    callback = questionsAndCallbacks.get(mapQuestion);
                    break;
                }
            }
            key = getQuestionProperty(messageKey, messagePrefix, kualiConfiguration, mapQuestion);

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (question.equals(mapQuestion) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                // If 'No' is the button clicked, just reload the doc

                String nextQuestion = null;
                // ask another question if more left
                if (questions.hasNext()) {
                    nextQuestion = (String) questions.next();
                    key = getQuestionProperty(messageKey, messagePrefix, kualiConfiguration, nextQuestion);

                    return this.performQuestionWithInput(mapping, form, request, response, nextQuestion, key, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
                }
                else {

                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }
            // Have to check length on value entered.
            String introNoteMessage = notePrefix + KFSConstants.BLANK_SPACE;

            // Build out full message.
            noteText = introNoteMessage + reason;
            int noteTextLength = noteText.length();

            // Get note text max length from DD.
            int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();
            if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                // Figure out exact number of characters that the user can enter.
                int reasonLimit = noteTextMaxLength - noteTextLength;
                if (reason == null) {
                    // Prevent a NPE by setting the reason to a blank string.
                    reason = "";
                }

                return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, mapQuestion, key, KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PAYMENT_REQUEST_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
            }
        }

        // make callback
        if (ObjectUtils.isNotNull(callback)) {
            AccountsPayableDocument refreshedApDocument = callback.doPostQuestion(apDocument, noteText);
            kualiDocumentFormBase.setDocument(refreshedApDocument);
        }
        String nextQuestion = null;
        // ask another question if more left
        if (questions.hasNext()) {
            nextQuestion = (String) questions.next();
            key = getQuestionProperty(messageKey, messagePrefix, kualiConfiguration, nextQuestion);

            return this.performQuestionWithInput(mapping, form, request, response, nextQuestion, key, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
        }

        return redirect;
    }

    /**
     * Used to look up messages to be displayed, from the KualiConfigurationService, given either a whole key or two parts of a key
     * that may be concatenated together.
     * 
     * @param messageKey String. One of the message keys in PurapKeyConstants.
     * @param messagePrefix String. A prefix to the question key, such as "ap.question." that, concatenated with the question,
     *        comprises the whole key of the message.
     * @param kualiConfiguration An instance of KualiConfigurationService
     * @param question String. The most specific part of the message key in PurapKeyConstants.
     * @return The message to be displayed given the key
     */
    private String getQuestionProperty(String messageKey, String messagePrefix, KualiConfigurationService kualiConfiguration, String question) {

        return kualiConfiguration.getPropertyString((StringUtils.isEmpty(messagePrefix)) ? messageKey : messagePrefix + question);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument document = (AccountsPayableDocument) apForm.getDocument();

        // validate cancel rules
        boolean rulePassed = KNSServiceLocator.getKualiRuleService().applyRules(new CancelAccountsPayableEvent(document));

        if (!rulePassed) {

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        return askCancelQuestion(mapping, form, request, response);
    }

    /**
     * Constructs and asks the question as to whether the user wants to cancel, for payment requests and credit memos.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return An ActionForward
     * @throws Exception
     */
    private ActionForward askCancelQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        String operation = "Cancel ";
        PurQuestionCallback callback = cancelCallbackMethod();
        TreeMap<String, PurQuestionCallback> questionsAndCallbacks = new TreeMap<String, PurQuestionCallback>();
        questionsAndCallbacks.put("cancelAP", callback);
        AccountsPayableDocument apDoc = (AccountsPayableDocument) apForm.getDocument();
        // check to see whether we should ask close/reopen question
        if (apDoc.getDocumentSpecificService().shouldPurchaseOrderBeReversed(apDoc)) {
            PurQuestionCallback callback2 = cancelPOActionCallbackMethod();
            questionsAndCallbacks.put("actionOnPoCancel", callback2);
        }

        return askQuestionWithInput(mapping, form, request, response, CMDocumentsStrings.CANCEL_CM_QUESTION, AccountsPayableDocumentStrings.CANCEL_NOTE_PREFIX, operation, PurapKeyConstants.CREDIT_MEMO_QUESTION_CANCEL_DOCUMENT, questionsAndCallbacks, PurapKeyConstants.AP_QUESTION_PREFIX, mapping.findForward(KFSConstants.MAPPING_PORTAL));
    }

    /**
     * Returns a question callback for the Cancel Purchase Order action.
     * 
     * @return A PurQuestionCallback with a post-question activity appropriate to the Cancel PO action
     */
    protected PurQuestionCallback cancelPOActionCallbackMethod() {

        return new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                // base impl do nothing
                return document;
            }
        };
    }

    /**
     * Returns a question callback for the Cancel action.
     * 
     * @return A PurQuestionCallback which does post-question tasks appropriate to Cancellation.
     */
    protected PurQuestionCallback cancelCallbackMethod() {

        return new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                DocumentService documentService = SpringContext.getBean(DocumentService.class);
                AccountsPayableDocumentSpecificService apDocumentSpecificService = document.getDocumentSpecificService();
                // one way or another we will have to fake the user session so get the current one
                UserSession originalUserSession = GlobalVariables.getUserSession();

                // If state is approved or final or processed
                if (document.getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
                    //call gl method here (no reason for post processing since workflow done)
                    SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocument(document, "");
                }else if (document.getDocumentHeader().getWorkflowDocument().stateIsInitiated() ||
                    document.getDocumentHeader().getWorkflowDocument().stateIsSaved()){
                    documentService.cancelDocument(document, "");
                }else if (document.getDocumentHeader().getWorkflowDocument().stateIsEnroute()){
                    try{
                        //need to run a super user cancel since person canceling may not have an action requested on the document
                        GlobalVariables.setUserSession(new UserSession(PurapConstants.SYSTEM_AP_USER));
                        documentService.superUserCancelDocument(documentService.getByDocumentHeaderId(document.getDocumentNumber()), "Document Cancelled by user " + originalUserSession.getUniversalUser().getPersonName() + " (" + originalUserSession.getUniversalUser().getPersonUserIdentifier() + ")");
                    }
                    finally {
                        GlobalVariables.setUserSession(originalUserSession);
                    }
                }
                        
                Note noteObj = documentService.createNoteFromDocument(document, noteText);
                documentService.addNoteToDocument(document, noteObj);
                SpringContext.getBean(NoteService.class).save(noteObj);
                return document;
            }
        };
    }
}
