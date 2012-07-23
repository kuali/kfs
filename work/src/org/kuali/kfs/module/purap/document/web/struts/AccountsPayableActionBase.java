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

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.AccountsPayableDocumentStrings;
import org.kuali.kfs.module.purap.PurapConstants.CMDocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.SingleConfirmationQuestion;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.LogicContainer;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedPreCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.module.purap.util.PurQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.util.MessageList;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Struts Action for Accounts Payable documents.
 */
public class AccountsPayableActionBase extends PurchasingAccountsPayableActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountsPayableActionBase.class);

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase baseForm = (PurchasingAccountsPayableFormBase) form;
        
        ActionForward fwd = super.execute(mapping, form, request, response);
        
        AccountsPayableDocumentBase document = (AccountsPayableDocumentBase) baseForm.getDocument();
        boolean foundAccountExpiredWarning = false;
        for(int i=0;i<KNSGlobalVariables.getMessageList().size();i++){
            if (StringUtils.equals(KNSGlobalVariables.getMessageList().get(i).getErrorKey(),PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED)){
                foundAccountExpiredWarning = true;
            }   
        }
        
        if (!foundAccountExpiredWarning){
            SpringContext.getBean(AccountsPayableService.class).generateExpiredOrClosedAccountWarning(document);
        }
            
        return fwd;
        
    }
    /**
     * Performs refresh of objects after a lookup.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase baseForm = (PurchasingAccountsPayableFormBase) form;
        
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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
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
    @Override
    public ActionForward calculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) apForm.getDocument();
        
     //   //recalculate the amounts and percents on the accounting line.
        SpringContext.getBean(PurapAccountingService.class).updateAccountAmounts(apDoc);
        
        // call precalculate
        if (SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedPreCalculateAccountsPayableEvent(apDoc))) {
            customCalculate(apDoc);

            // set calculated flag according to document type and status
            if (apForm instanceof PaymentRequestForm && apDoc.getApplicationDocumentStatus().equals(PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW)) {
                // set calculated tax flag for tax area calculation 
                PaymentRequestForm preqForm = (PaymentRequestForm)apForm;
                preqForm.setCalculatedTax(true);
            }
            else {
                // set calculated flag for document calculation, whether or not the process calculation rule passes, since it only gives warning
                apForm.setCalculated(true);
            }
        }

        return super.calculate(mapping, form, request, response);
    }

    @Override
    public ActionForward clearAllTaxes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase payableForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) payableForm.getDocument();

        SpringContext.getBean(PurapService.class).clearAllTaxes(apDoc);

        return super.clearAllTaxes(mapping, form, request, response);
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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;

        // set the last update user id
        AccountsPayableDocumentBase document = (AccountsPayableDocumentBase) apForm.getDocument();
        document.setLastActionPerformedByPersonId(GlobalVariables.getUserSession().getPerson().getPrincipalId());

        // if form is not yet calculated, return and prompt user to calculate
        if (requiresCaculate(apForm)) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_APPROVE_REQUIRES_CALCULATE);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // recalculate
        customCalculate((AccountsPayableDocument) apForm.getDocument());

        // route
        ActionForward forward = super.route(mapping, form, request, response);

        // if successful, then redirect back to init
        boolean successMessageFound = false;
        MessageList messageList = KNSGlobalVariables.getMessageList();
        for (int i = 0; i < messageList.size(); i++) {
            if (StringUtils.equals(messageList.get(i).getErrorKey(), RiceKeyConstants.MESSAGE_ROUTE_SUCCESSFUL)) {
                successMessageFound = true;
                break;
            }
        }

        if (successMessageFound) {
            String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);

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
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase apForm = (AccountsPayableFormBase) form;

        if (!requiresCaculate(apForm)) {
            return super.save(mapping, form, request, response);
        }

        GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_ERRORS, PurapKeyConstants.ERROR_SAVE_REQUIRES_CALCULATE);
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
     * @param messagePrefix The most general part of a key to a message text to be retrieved from ConfigurationService,
     *        Describes a collection of questions.
     * @param redirect An ActionForward to return to if done with questions
     * @return An ActionForward
     * @throws Exception
     */
    protected ActionForward askQuestionWithInput(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String notePrefix, String operation, String messageKey, TreeMap<String, PurQuestionCallback> questionsAndCallbacks, String messagePrefix, ActionForward redirect) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        AccountsPayableDocumentBase apDocument = (AccountsPayableDocumentBase) kualiDocumentFormBase.getDocument();

        String question = (String) request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
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
     * Used to look up messages to be displayed, from the ConfigurationService, given either a whole key or two parts of a key
     * that may be concatenated together.
     * 
     * @param messageKey String. One of the message keys in PurapKeyConstants.
     * @param messagePrefix String. A prefix to the question key, such as "ap.question." that, concatenated with the question,
     *        comprises the whole key of the message.
     * @param kualiConfiguration An instance of ConfigurationService
     * @param question String. The most specific part of the message key in PurapKeyConstants.
     * @return The message to be displayed given the key
     */
    protected String getQuestionProperty(String messageKey, String messagePrefix, ConfigurationService kualiConfiguration, String question) {
        return kualiConfiguration.getPropertyValueAsString((StringUtils.isEmpty(messagePrefix)) ? messageKey : messagePrefix + question);
    }

    public ActionForward reopenPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Reopen PO started");
        return askQuestionsAndPerformReopenPurchaseOrder(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
    protected ActionForward askCancelQuestion(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase apForm = (PurchasingAccountsPayableFormBase) form;
        
        String operation = "Cancel ";
        PurQuestionCallback callback = cancelCallbackMethod();
        TreeMap<String, PurQuestionCallback> questionsAndCallbacks = new TreeMap<String, PurQuestionCallback>();
        questionsAndCallbacks.put("cancelAP", callback);

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
                SpringContext.getBean(AccountsPayableService.class).cancelAccountsPayableDocumentByCheckingDocumentStatus(document, noteText);
                return document;
            }
        };
    }

    protected ActionForward askQuestionsAndPerformReopenPurchaseOrder(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("askQuestionsAndPerformDocumentAction started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        AccountsPayableDocumentBase apDoc = (AccountsPayableDocumentBase) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String questionType = PODocumentsStrings.REOPEN_PO_QUESTION;
        String confirmType = PODocumentsStrings.CONFIRM_REOPEN_QUESTION;
        String messageType = PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REOPEN_DOCUMENT;
        String operation = "Reopen ";

        try {
            ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

            // Start in logic for confirming the proposed operation.
            if (ObjectUtils.isNull(question)) {
                String key = kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT);
                String message = StringUtils.replace(key, "{0}", operation);
                return this.performQuestionWithoutInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                    // If 'No' is the button clicked, just reload the doc
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {
                    // This is the case when the user clicks on "OK" in the end; redirect to the preq doc
                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }

            PurchaseOrderDocument po = apDoc.getPurchaseOrderDocument();
            if (!po.isPendingActionIndicator() && PurapConstants.PurchaseOrderStatuses.APPDOC_CLOSED.equals(po.getApplicationDocumentStatus())) {
                /*
                 * Below if-else code block calls PurchaseOrderService methods that will throw ValidationException objects if errors
                 * occur during any process in the attempt to perform its actions. Assume, if these return successfully, that the
                 * PurchaseOrderDocument object returned from each is the newly created document and that all actions in the method
                 * were run correctly. NOTE: IF BELOW IF-ELSE IS EDITED THE NEW METHODS CALLED MUST THROW ValidationException OBJECT
                 * IF AN ERROR IS ADDED TO THE GlobalVariables
                 */
                po = initiateReopenPurchaseOrder(po, kualiDocumentFormBase.getAnnotation());

                if (!GlobalVariables.getMessageMap().hasNoErrors()) {
                    throw new ValidationException("errors occurred during new PO creation");
                }

                if (StringUtils.isNotEmpty(messageType)) {
                    KNSGlobalVariables.getMessageList().add(messageType);
                }
                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyValueAsString(messageType), PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
            }
            else {
                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, "Unable to reopen the PO at this time due to the incorrect PO status or a pending PO change document.", PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
            }

        }
        catch (ValidationException ve) {
            throw ve;
        }
    }

    public PurchaseOrderDocument initiateReopenPurchaseOrder(PurchaseOrderDocument po, String annotation) {
        try {
            LogicContainer logicToRun = new LogicContainer() {
                public Object runLogic(Object[] objects) throws Exception {
                    PurchaseOrderDocument po = (PurchaseOrderDocument) objects[0];

                    Note cancelNote = new Note();
                    cancelNote.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
                    cancelNote.setNoteText(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.AP_REOPENS_PURCHASE_ORDER_NOTE));
                    cancelNote.setNotePostedTimestampToCurrent();
                    po.addNote(cancelNote);
                    SpringContext.getBean(PurapService.class).saveDocumentNoValidation(po);

                    return SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(po.getDocumentNumber(), PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, (String) objects[1], null, PurchaseOrderStatuses.APPDOC_PENDING_REOPEN);
                }
            };
            return (PurchaseOrderDocument) SpringContext.getBean(PurapService.class).performLogicWithFakedUserSession(KFSConstants.SYSTEM_USER, logicToRun, new Object[] { po, annotation });
        }
        catch (WorkflowException e) {
            String errorMsg = "Workflow Exception caught: " + e.getLocalizedMessage();
            LOG.error(errorMsg, e);
            throw new RuntimeException(errorMsg, e);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * gets the item from preq and restores the values from the original PO and then
     * redistributes the amounts based on extended cost and quantity
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return actionForward
     * @throws Exception
     */
    public ActionForward recalculateItemAccountsAmounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase payableForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) payableForm.getDocument();
        
        PurapAccountingService purapAccountingService = SpringContext.getBean(PurapAccountingService.class);
        
        String[] indexes = getSelectedItemNumber(request);
        int itemIndex = Integer.parseInt(indexes[0]);

        PurApItem item = (PurApItem) apDoc.getItem((itemIndex));
        
        //first reset the the corresponding po items accounts amounts to this item
        restoreItemAccountsAmounts(apDoc, item);

        item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
        
        final KualiDecimal itemExtendedPrice = (item.getExtendedPrice()==null)?KualiDecimal.ZERO:item.getExtendedPrice();;
        if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
            KualiDecimal newExtendedPrice = item.calculateExtendedPrice();
            item.setExtendedPrice(newExtendedPrice);
        }
        
        PaymentRequestDocument preqDoc = (PaymentRequestDocument) apDoc;

        // set amounts on any empty
        preqDoc.updateExtendedPriceOnItems();

        // calculation just for the tax area, only at tax review stage
        // by now, the general calculation shall have been done.
        if (preqDoc.getApplicationDocumentStatus().equals(PaymentRequestStatuses.APPDOC_AWAITING_TAX_REVIEW)) {
            SpringContext.getBean(PaymentRequestService.class).calculateTaxArea(preqDoc);
        }
        
        // notice we're ignoring whether the boolean, because these are just warnings they shouldn't halt anything
        //Calculate Payment request before rules since the rule check totalAmount.
        SpringContext.getBean(PaymentRequestService.class).calculatePaymentRequest(preqDoc, true);
        SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedCalculateAccountsPayableEvent(preqDoc));
        
        PurchasingAccountsPayableDocumentBase document = (PurchasingAccountsPayableDocumentBase) apDoc;
        String accountDistributionMethod = document.getAccountDistributionMethod();

        if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
            // update the accounts amounts for PREQ and distribution method = sequential
            purapAccountingService.updatePreqItemAccountAmounts(item);
        } 
        else {
                List<PurApAccountingLine> sourceAccountingLines = item.getSourceAccountingLines();
                for (PurApAccountingLine acctLine : sourceAccountingLines) {
                    acctLine.setAmount(KualiDecimal.ZERO);
                }
                
                purapAccountingService.updatePreqProportionalItemAccountAmounts(item);
             }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * gets the item from preq and restores the values from the original PO
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return actionForward
     * @throws Exception
     */
    public ActionForward restoreItemAccountsAmounts(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        AccountsPayableFormBase payableForm = (AccountsPayableFormBase) form;
        AccountsPayableDocument apDoc = (AccountsPayableDocument) payableForm.getDocument();
        
        String[] indexes = getSelectedItemNumber(request);
        int itemIndex = Integer.parseInt(indexes[0]);

        PurApItem item = (PurApItem) apDoc.getItem((itemIndex));

        //first reset the the corresponding po items accounts amounts to this item
        restoreItemAccountsAmounts(apDoc, item);
        
        item.setItemQuantity(null);
        item.setItemTaxAmount(null);
        
        item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);
        
        final KualiDecimal itemExtendedPrice = (item.getExtendedPrice()==null)?KualiDecimal.ZERO:item.getExtendedPrice();;
        if (item.getItemType().isQuantityBasedGeneralLedgerIndicator()) {
            KualiDecimal newExtendedPrice = item.calculateExtendedPrice();
            item.setExtendedPrice(newExtendedPrice);
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Will return an array of Strings containing 2 indexes, the first String is the item index and the second String is the account
     * index. These are obtained by parsing the method to call parameter from the request, between the word ".line" and "." The
     * indexes are separated by a semicolon (:)
     * 
     * @param request The HttpServletRequest
     * @return An array of Strings containing pairs of two indices, an item index
     */
    protected String[] getSelectedItemNumber(HttpServletRequest request) {
        String itemString = new String();
        String parameterName = (String) request.getAttribute(KFSConstants.METHOD_TO_CALL_ATTRIBUTE);
        if (StringUtils.isNotBlank(parameterName)) {
            itemString = StringUtils.substringBetween(parameterName, ".line", ".");
        }
        String[] result = StringUtils.split(itemString, ":");

        return result;
    }
    
    /**
     * restores the preq preqItem' accounts amounts with po's item's account lines
     * amounts.
     * 
     * @param apDoc
     * @param preqItem
     */
    protected void restoreItemAccountsAmounts(AccountsPayableDocument apDoc, PurApItem preqItem) {
        List<PurApItem> pOItems = apDoc.getPurchaseOrderDocument().getItems();

        PurApItem pOItem = getPOItem(pOItems, preqItem.getItemLineNumber());
        if (ObjectUtils.isNotNull(pOItem)) {
         //   preqItem.setItemUnitPrice(pOItem.getItemUnitPrice());
            List <PurApAccountingLine> preqAccountingLines = preqItem.getSourceAccountingLines();
            for (PurApAccountingLine lineAcct : preqAccountingLines) {
                updateItemAccountLine(pOItem, lineAcct);                
            }
        }
    }
    
    /**
     * returns the matching po item based on matching item identifier and item line numbner
     * from preq items.
     * 
     * @param purchaseItems
     * @param itemLineNumber
     * @return pOItem
     */
    protected PurApItem getPOItem(List<PurApItem> pOItems, Integer itemLineNumber) {
        PurApItem pOItem = null;
        
        for (PurApItem poItem : pOItems) {
            if (poItem.getItemLineNumber().compareTo(itemLineNumber) == 0) {
                //found the matching preqItem so return it...
                return poItem;
            }
        }
        
        return pOItem;
    }
    
    /**
     * finds the line with matching sequence number, chart code, account number, financial
     * object code and updates amount/percent on the account line.
     * 
     * @param pOItem
     * @param lineAcct
     */
    protected void updateItemAccountLine(PurApItem pOItem, PurApAccountingLine lineAcct) {
        List <PurApAccountingLine> pOAccountingLines = pOItem.getSourceAccountingLines();
        for (PurApAccountingLine pOLineAcct : pOAccountingLines) {
            if (lineAcct.getChartOfAccountsCode().equalsIgnoreCase(pOLineAcct.getChartOfAccountsCode()) &&
                    lineAcct.getAccountNumber().equalsIgnoreCase(pOLineAcct.getAccountNumber()) && 
                    lineAcct.getFinancialObjectCode().equalsIgnoreCase(pOLineAcct.getFinancialObjectCode())) {
                lineAcct.setAmount(pOLineAcct.getAmount());
                lineAcct.setAccountLinePercent(pOLineAcct.getAccountLinePercent());
            }
        }
    }
}
