/*
 * Copyright 2006 The Kuali Foundation
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

import java.io.ByteArrayOutputStream;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.kfs.module.purap.PurapAuthorizationConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.SingleConfirmationQuestion;
import org.kuali.kfs.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.kfs.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteList;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderQuoteListVendor;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderSensitiveData;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorStipulation;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.businessobject.SensitiveDataAssignment;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderRetransmitDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.kfs.module.purap.document.service.FaxService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAddVendorToQuoteEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedAssignSensitiveDataEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedSplitPurchaseOrderEvent;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants.AddressTypes;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.BlankFormFile;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Struts Action for Purchase Order document.
 */
public class PurchaseOrderAction extends PurchasingActionBase {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PurchaseOrderForm poForm = (PurchaseOrderForm) form;

        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Handling lookups for alternate vendor for non-primary vendor payment that are only specific to Purchase Order.
        if (request.getParameter("document.alternateVendorHeaderGeneratedIdentifier") != null && request.getParameter("document.alternateVendorDetailAssignedIdentifier") != null) {
            Integer alternateVendorDetailAssignedId = document.getAlternateVendorDetailAssignedIdentifier();
            Integer alternateVendorHeaderGeneratedId = document.getAlternateVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(alternateVendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(alternateVendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
            document.templateAlternateVendor(refreshVendorDetail);
        }

        // Handling lookups for quote list that is specific to Purchase Order.
        if (request.getParameter("document.purchaseOrderQuoteListIdentifier") != null) {
            // do a lookup and add all the vendors!
            Integer poQuoteListIdentifier = document.getPurchaseOrderQuoteListIdentifier();
            PurchaseOrderQuoteList poQuoteList = new PurchaseOrderQuoteList();
            poQuoteList.setPurchaseOrderQuoteListIdentifier(poQuoteListIdentifier);
            poQuoteList = (PurchaseOrderQuoteList) businessObjectService.retrieve(poQuoteList);
            if (poQuoteList.isActive()) {
                for (PurchaseOrderQuoteListVendor poQuoteListVendor : poQuoteList.getQuoteListVendors()) {
                    if ( poQuoteListVendor.isActive() ) {
                        VendorDetail newVendor = poQuoteListVendor.getVendorDetail();
                        if (newVendor.isActiveIndicator() && !newVendor.isVendorDebarred()) {
                            PurchaseOrderVendorQuote newPOVendorQuote = SpringContext.getBean(PurchaseOrderService.class).populateQuoteWithVendor(newVendor.getVendorHeaderGeneratedIdentifier(), newVendor.getVendorDetailAssignedIdentifier(), document.getDocumentNumber());
                            document.getPurchaseOrderVendorQuotes().add(newPOVendorQuote);
                        }
                    }
                }
            }
        }

        // Handling lookups for quote vendor search that is specific to Purchase Order.
        String newVendorHeaderGeneratedIdentifier = request.getParameter("newPurchaseOrderVendorQuote.vendorHeaderGeneratedIdentifier");
        String newVendorDetailAssignedIdentifier = request.getParameter("newPurchaseOrderVendorQuote.vendorDetailAssignedIdentifier");
        if (newVendorHeaderGeneratedIdentifier != null && newVendorDetailAssignedIdentifier != null) {

            PurchaseOrderVendorQuote newPOVendorQuote = SpringContext.getBean(PurchaseOrderService.class).populateQuoteWithVendor(new Integer(newVendorHeaderGeneratedIdentifier), new Integer(newVendorDetailAssignedIdentifier), document.getDocumentNumber());

            poForm.setNewPurchaseOrderVendorQuote(newPOVendorQuote);
        }

        String newStipulation = request.getParameter(KFSPropertyConstants.DOCUMENT + "." + PurapPropertyConstants.VENDOR_STIPULATION_DESCRIPTION);
        if (StringUtils.isNotEmpty(newStipulation)) {
            poForm.getNewPurchaseOrderVendorStipulationLine().setVendorStipulationDescription(newStipulation);
        }

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Inactivate an item from the purchase order document.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward inactivateItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingAccountsPayableFormBase purchasingForm = (PurchasingAccountsPayableFormBase) form;

        PurchaseOrderDocument purDocument = (PurchaseOrderDocument) purchasingForm.getDocument();
        List items = purDocument.getItems();
        PurchaseOrderItem item = (PurchaseOrderItem) items.get(getSelectedLine(request));
        item.setItemActiveIndicator(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * For use with a specific set of methods of this class that create new purchase order-derived document types in response to
     * user actions, including <code>closePo</code>, <code>reopenPo</code>, <code>paymentHoldPo</code>, <code>removeHoldPo</code>,
     * <code>splitPo</code>, <code>amendPo</code>, and <code>voidPo</code>. It employs the question framework to ask
     * the user for a response before creating and routing the new document. The response should consist of a note detailing a
     * reason, and either yes or no. This method can be better understood if it is noted that it will be gone through twice (via the
     * question framework); when each question is originally asked, and again when the yes/no response is processed, for
     * confirmation.
     *
     * @param mapping These are boiler-plate.
     * @param form "
     * @param request "
     * @param response "
     * @param questionType A string identifying the type of question being asked.
     * @param confirmType A string identifying which type of question is being confirmed.
     * @param documentType A string, the type of document to create
     * @param notePrefix A string to appear before the note in the BO Notes tab
     * @param messageType A string to appear on the PO once the question framework is done, describing the action taken
     * @param operation A string, the verb to insert in the original question describing the action to be taken
     * @return An ActionForward
     * @throws Exception
     */
    protected ActionForward askQuestionsAndPerformDocumentAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {
        LOG.debug("askQuestionsAndPerformDocumentAction started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        try {
            ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

            // Start in logic for confirming the proposed operation.
            if (ObjectUtils.isNull(question)) {
                String message = "";
                if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT)) {
                    message = kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_SPLIT_QUESTION_TEXT);
                }
                else {
                    String key = kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT);
                    message = StringUtils.replace(key, "{0}", operation);
                }
                // Ask question if not already asked.
                return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            else {
                Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
                if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {

                    // If 'No' is the button clicked, just reload the doc
                    return returnToPreviousPage(mapping, kualiDocumentFormBase);
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
                    int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

                    if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                        // Figure out exact number of characters that the user can enter.
                        int reasonLimit = noteTextMaxLength - noteTextLength;

                        if (ObjectUtils.isNull(reason)) {
                            // Prevent a NPE by setting the reason to a blank string.
                            reason = "";
                        }

                        String message = "";
                        String key = kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT);
                        message = StringUtils.replace(key, "{0}", operation);

                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
            }
            // Below used as a place holder to allow code to specify actionForward to return if not a 'success question'
            ActionForward returnActionForward = null;
            if (!po.isPendingActionIndicator()) {
                /*
                 * Below if-else code block calls PurchaseOrderService methods that will throw ValidationException objects if errors
                 * occur during any process in the attempt to perform its actions. Assume, if these return successfully, that the
                 * PurchaseOrderDocument object returned from each is the newly created document and that all actions in the method
                 * were run correctly. NOTE: IF BELOW IF-ELSE IS EDITED THE NEW METHODS CALLED MUST THROW ValidationException OBJECT
                 * IF AN ERROR IS ADDED TO THE GlobalVariables
                 */
                if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT)) {
                    po.setPendingSplit(true);
                    // Save adding the note for after the items are picked.
                    ((PurchaseOrderForm)kualiDocumentFormBase).setSplitNoteText(noteText);
                    returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else {
                    String newStatus = null;
                    if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.APPDOC_AMENDMENT;
                        po = SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), documentType, newStatus);
                        returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                    }
                    else {
                        if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT)) {
                            newStatus = PurchaseOrderStatuses.APPDOC_PENDING_CLOSE;
                        }
                        else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT)) {
                            newStatus = PurchaseOrderStatuses.APPDOC_PENDING_REOPEN;
                        }
                        else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT)) {
                            newStatus = PurchaseOrderStatuses.APPDOC_PENDING_VOID;
                        }
                        else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT)) {
                            newStatus = PurchaseOrderStatuses.APPDOC_PENDING_PAYMENT_HOLD;
                        }
                        else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT)) {
                            newStatus = PurchaseOrderStatuses.APPDOC_PENDING_REMOVE_HOLD;
                        }
                        else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)) {
                            newStatus = PurchaseOrderStatuses.APPDOC_PENDING_RETRANSMIT;
                        }
                        po = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), newStatus);
                    }
                    if (!GlobalVariables.getMessageMap().hasNoErrors()) {
                        throw new ValidationException("errors occurred during new PO creation");
                    }

                    String previousDocumentId = kualiDocumentFormBase.getDocId();
                    // Assume at this point document was created properly and 'po' variable is new PurchaseOrderDocument created
                    kualiDocumentFormBase.setDocument(po);
                    kualiDocumentFormBase.setDocId(po.getDocumentNumber());
                    kualiDocumentFormBase.setDocTypeName(po.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

                    Note newNote = new Note();
                    if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                        noteText = noteText + " (Previous Document Id is " + previousDocumentId + ")";
                    }
                    newNote.setNoteText(noteText);
                    newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                    kualiDocumentFormBase.setNewNote(newNote);

                    kualiDocumentFormBase.setAttachmentFile(new BlankFormFile());

                    insertBONote(mapping, kualiDocumentFormBase, request, response);

                    //the newly created notes needed to be set to the docuemnt
                    //on the oldest purchase order otherwise, in POA when you try to save
                    //the POA, it will throw Ojblockexception error.
                    //KFSMI-8394
                    PurchaseOrderDocument oldestPurchaseOrder = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
                    List<Note> newNotes = getNoteService().getByRemoteObjectId(oldestPurchaseOrder.getObjectId());

                    oldestPurchaseOrder.setNotes(newNotes);
                }
                if (StringUtils.isNotEmpty(messageType)) {
                    KNSGlobalVariables.getMessageList().add(messageType);
                }
            }
            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else {

                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyValueAsString(messageType), PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
            }
        }
        catch (ValidationException ve) {
            throw ve;
        }
    }

    /**
     * Invoked when the user pressed on the Close Order button on a Purchase Order page to Close the PO. It will
     * display the question page to the user to ask whether the user really wants to close the PO and ask the user to enter a reason
     * in the text area. If the user has entered the reason, it will invoke a service method to do the processing for closing a PO,
     * then display a Single Confirmation page to inform the user that the PO Close Document has been routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward closePo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("ClosePO started.");
        String operation = "Close ";
        PurchaseOrderDocument po = ((PurchaseOrderForm)form).getPurchaseOrderDocument();

        if (po.canClosePOForTradeIn()) {
            return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.CLOSE_QUESTION, PODocumentsStrings.CLOSE_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PODocumentsStrings.CLOSE_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT, operation);
        }
        else {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
    }

    /**
     * Is invoked when the user pressed on the Payment Hold button on a Purchase Order page to put the PO on hold. It
     * will display the question page to the user to ask whether the user really wants to put the PO on hold and ask the user to
     * enter a reason in the text area. If the user has entered the reason, it will invoke a service method to do the processing for
     * putting a PO on hold, then display a Single Confirmation page to inform the user that the PO Payment Hold Document has been
     * routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward paymentHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("PaymentHoldPO started.");
        String operation = "Hold Payment ";

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.PAYMENT_HOLD_QUESTION, PODocumentsStrings.PAYMENT_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, PODocumentsStrings.PAYMENT_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_PAYMENT_HOLD, operation);
    }

    /**
     * Is invoked when the user pressed on the Remove Hold button on a Payment Hold PO page to remove the PO from hold.
     * It will display the question page to the user to ask whether the user really wants to remove the PO from hold and ask the
     * user to enter a reason in the text area. If the user has entered the reason, it will invoke a service method to do the
     * processing for removing a PO from hold, then display a Single Confirmation page to inform the user that the PO Remove Hold
     * Document has been routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward removeHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("RemoveHoldPO started.");
        String operation = "Remove Payment Hold ";
        ActionForward forward = askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.REMOVE_HOLD_QUESTION, PODocumentsStrings.REMOVE_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT, PODocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REMOVE_HOLD, operation);

        return forward;
    }

    /**
     * Is invoked when the user pressed on the Open Order button on a Purchase Order page that has status "Close" to
     * reopen the PO. It will display the question page to the user to ask whether the user really wants to reopen the PO and ask
     * the user to enter a reason in the text area. If the user has entered the reason, it will invoke the a service method to do
     * the processing for reopening a PO, then display a Single Confirmation page to inform the user that the
     * <code>PurchaseOrderReopenDocument</code> has been routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderReopenDocument
     */
    public ActionForward reopenPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Reopen PO started");
        String operation = "Reopen ";

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.REOPEN_PO_QUESTION, PODocumentsStrings.CONFIRM_REOPEN_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, PODocumentsStrings.REOPEN_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REOPEN_DOCUMENT, operation);
    }

    /**
     * Is invoked when the user pressed on the Amend button on a Purchase Order page to amend the PO. It will display
     * the question page to the user to ask whether the user really wants to amend the PO and ask the user to enter a reason in the
     * text area. If the user has entered the reason, it will invoke a service method to do the processing for amending the PO, then
     * display a Single Confirmation page to inform the user that the <code>PurchaseOrderAmendmentDocument</code> has been routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderAmendmentDocument
     */
    public ActionForward amendPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Amend PO started");
        String operation = "Amend ";

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.AMENDMENT_PO_QUESTION, PODocumentsStrings.CONFIRM_AMENDMENT_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PODocumentsStrings.AMENDMENT_NOTE_PREFIX, null, operation);
    }

    /**
     * Is invoked when the user pressed on the Void button on a Purchase Order page to void the PO. It will display the
     * question page to the user to ask whether the user really wants to void the PO and ask the user to enter a reason in the text
     * area. If the user has entered the reason, it will invoke a service method to do the processing for voiding the PO, then
     * display a Single Confirmation page to inform the user that the <code>PurchaseOrderVoidDocument</code> has been routed.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderVoidDocument
     */
    public ActionForward voidPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Void PO started");
        String operation = "Void ";

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.VOID_QUESTION, PODocumentsStrings.VOID_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, PODocumentsStrings.VOID_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_VOID_DOCUMENT, operation);
    }

    /**
     * Invoked to initiate the splitting of a Purchase Order.  Displays a question page to ask for a reason and confirmation
     * of the user's desire to split the Purchase Order, and, if confirmed, a page on which the Split PO tab only is showing,
     * and the items to move to the new PO are chosen. If that is done, and the user continues, a new Split Purchase Order document
     * will be created, with the chosen items.  That same set of items will be deleted from the original Purchase Order.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     * @see org.kuali.kfs.module.purap.document.PurchaseOrderSplitDocument
     */
    public ActionForward splitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Split PO started");
        String operation = "Split ";

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.SPLIT_QUESTION, PODocumentsStrings.SPLIT_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT,PODocumentsStrings.SPLIT_NOTE_PREFIX_OLD_DOC,PurapKeyConstants.PURCHASE_ORDER_MESSAGE_SPLIT_DOCUMENT,operation);
    }

    /**
     * Invoked when only the Split Purchase Order tab is showing to continue the process of splitting the PO, once items are chosen
     * to be moved to the new PO.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward continuePurchaseOrderSplit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception  {
        LOG.debug("Continue Purchase Order Split started");

        PurchaseOrderForm purchaseOrderForm = (PurchaseOrderForm) form;
        // This PO does not contain all data, but enough for our purposes; it has been reloaded with only the Split PO tab showing.
        PurchaseOrderDocument poToSplit = (PurchaseOrderDocument)purchaseOrderForm.getDocument();
        boolean copyNotes = poToSplit.isCopyingNotesWhenSplitting();

        // Check business rules before splitting.

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedSplitPurchaseOrderEvent(poToSplit));
        if (!rulePassed) {
            poToSplit.setPendingSplit(true);
        }
        else {
            HashMap<String, List<PurchaseOrderItem>> categorizedItems = SpringContext.getBean(PurchaseOrderService.class).categorizeItemsForSplit(poToSplit.getItems());
            List<PurchaseOrderItem> movingPOItems = categorizedItems.get(PODocumentsStrings.ITEMS_MOVING_TO_SPLIT);
            List<PurchaseOrderItem> remainingPOItems = categorizedItems.get(PODocumentsStrings.ITEMS_REMAINING);

            // Fetch the whole PO from the database, and reset and renumber the items on it.
            poToSplit = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(poToSplit.getPurapDocumentIdentifier());
            poToSplit.setItems(remainingPOItems);
            poToSplit.renumberItems(0);

            // Add the note that would normally have gone in after the confirmation page.
            String noteText = purchaseOrderForm.getSplitNoteText();
            try {
                Note splitNote = SpringContext.getBean(DocumentService.class).createNoteFromDocument(poToSplit, noteText);
                poToSplit.addNote(splitNote);
                SpringContext.getBean(NoteService.class).save(splitNote);
            } catch ( Exception e ) {
                throw new RuntimeException(e);
            }
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(poToSplit);

            PurchaseOrderSplitDocument splitPO = SpringContext.getBean(PurchaseOrderService.class).createAndSavePurchaseOrderSplitDocument(movingPOItems, poToSplit, copyNotes, noteText);

            Long nextLinkIdentifier = SpringContext.getBean(SequenceAccessorService.class).getNextAvailableSequenceNumber("AP_PUR_DOC_LNK_ID");
            splitPO.setAccountsPayablePurchasingDocumentLinkIdentifier(nextLinkIdentifier.intValue());

            purchaseOrderForm.setDocument(splitPO);
            purchaseOrderForm.setDocId(splitPO.getDocumentNumber());
            purchaseOrderForm.setDocTypeName(splitPO.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            try {
                loadDocument(purchaseOrderForm);
            }
            catch (WorkflowException we) {
                throw new RuntimeException(we);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invoked from the page on which the Split PO tab is showing to cancel the splitting of the PO and return it to its original state.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward cancelPurchaseOrderSplit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Cancel Purchase Order Split started");

        PurchaseOrderForm purchaseOrderForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)purchaseOrderForm.getDocument();

        po = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(po.getDocumentNumber());

        po.setPendingSplit(false);
        po.setCopyingNotesWhenSplitting(false);
        purchaseOrderForm.setDocument(po);
        reload(mapping, purchaseOrderForm, request, response);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invoked when an authorized user presses "Sensitive Data" button on the purchase order page.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward assignSensitiveData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Assign Sensitive Data started");

        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)poForm.getDocument();
        Integer poId = po.getPurapDocumentIdentifier();
        SensitiveDataService sdService = SpringContext.getBean(SensitiveDataService.class);

        // set the assignment flag and reset input fields
        po.setAssigningSensitiveData(true);
        poForm.setSensitiveDataAssignmentReason("");
        poForm.setNewSensitiveDataLine(new SensitiveData());

        // load previous assignment info
        SensitiveDataAssignment sda = sdService.getLastSensitiveDataAssignment(poId);
        poForm.setLastSensitiveDataAssignment(sda);

        // even though at this point, the sensitive data entries should have been loaded into the form already,
        // we still load them again in case that someone else has changed that during the time period
        List<SensitiveData> posds = sdService.getSensitiveDatasAssignedByPoId(poId);
        poForm.setSensitiveDatasAssigned(posds);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invoked when an authorized user presses "Submit" button on the "Assign Sensitive Data" page.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward submitSensitiveData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Submit Sensitive Data started");

        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)poForm.getDocument();
        Integer poId = po.getPurapDocumentIdentifier();
        List<SensitiveData> sds = poForm.getSensitiveDatasAssigned();
        String sdaReason = poForm.getSensitiveDataAssignmentReason();
        SensitiveDataService sdService = SpringContext.getBean(SensitiveDataService.class);

        // check business rules
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAssignSensitiveDataEvent("", po, sdaReason, sds));
        if (!rulePassed) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // update table SensitiveDataAssignment
        SensitiveDataAssignment sda = new SensitiveDataAssignment(poId, poForm.getSensitiveDataAssignmentReason(), GlobalVariables.getUserSession().getPerson().getPrincipalName(), poForm.getSensitiveDatasAssigned());
        SpringContext.getBean(BusinessObjectService.class).save(sda);

        // update table PurchaseOrderSensitiveData
        sdService.deletePurchaseOrderSensitiveDatas(poId);
        List<PurchaseOrderSensitiveData> posds = new ArrayList<PurchaseOrderSensitiveData>();
        for (SensitiveData sd : sds) {
            posds.add(new PurchaseOrderSensitiveData(poId, po.getRequisitionIdentifier(), sd.getSensitiveDataCode()));
        }
        SpringContext.getBean(BusinessObjectService.class).save(posds);

        // need this to update workflow doc for searching restrictions on sensitive data
        SpringContext.getBean(PurapService.class).saveRoutingDataForRelatedDocuments(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

        // reset the sensitive data related fields in the po form
        po.setAssigningSensitiveData(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invoked when an authorized user presses "Cancel" button on the "Assign Sensitive Data" page.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward cancelSensitiveData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Cancel Sensitive Data started");

        // reset the sensitive data flag in the po form, reload sensitive data from database to undo the canceled changes
        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)poForm.getDocument();
        po.setAssigningSensitiveData(false);
        List<SensitiveData> sds = SpringContext.getBean(SensitiveDataService.class).getSensitiveDatasAssignedByPoId(po.getPurapDocumentIdentifier());
        poForm.setSensitiveDatasAssigned(sds);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invoked when an authorized user presses "Add" button on the "Assign Sensitive Data" page.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward addSensitiveData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Add Sensitive Data started");

        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        SensitiveDataService sdService = SpringContext.getBean(SensitiveDataService.class);

        // retrieve new sensitive data by code, add the new line
        SensitiveData newsd = poForm.getNewSensitiveDataLine();
        newsd = sdService.getSensitiveDataByCode(newsd.getSensitiveDataCode());
        List<SensitiveData> sds = poForm.getSensitiveDatasAssigned();
        sds.add(newsd);

        // reset new line
        poForm.setNewSensitiveDataLine(new SensitiveData());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Invoked when an authorized user presses "Delete" button on the "Assign Sensitive Data" page.
     *
     * @param mapping       An ActionMapping
     * @param form          An ActionForm
     * @param request       The HttpServeletRequest
     * @param response      The HttpServeletResponse
     * @return              An ActionForward
     * @throws Exception
     */
    public ActionForward deleteSensitiveData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Delete Sensitive Data started");

        // remove the selected sensitive data line
        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        List<SensitiveData> sds = poForm.getSensitiveDatasAssigned();
        sds.remove(getSelectedLine(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This is a utility method used to prepare to and to return to a previous page, making sure that the buttons will be restored
     * in the process.
     *
     * @param kualiDocumentFormBase The Form, considered as a KualiDocumentFormBase, as it typically is here.
     * @return An actionForward mapping back to the original page.
     */
    protected ActionForward returnToPreviousPage(ActionMapping mapping, KualiDocumentFormBase kualiDocumentFormBase) {

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Is executed when the user clicks on the "print" button on a Purchase Order Print Document page. On a non
     * javascript enabled browser, it will display a page with 2 buttons. One is to display the PDF, the other is to view the PO
     * tabbed page where the PO document statuses, buttons, etc have already been updated (the updates of those occurred while the
     * <code>performPurchaseOrderFirstTransmitViaPrinting</code> method is invoked. On a javascript enabled browser, it will
     * display both the PO tabbed page containing the updated PO document info and the pdf on the next window/tab of the browser.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward firstTransmitPrintPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = ((PurchaseOrderForm)form).getDocId();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            SpringContext.getBean(PurchaseOrderService.class).performPurchaseOrderFirstTransmitViaPrinting(poDocId, baosPDF);
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        String basePath = getApplicationBaseUrl();
        String docId = ((PurchaseOrderForm) form).getDocId();
        String methodToCallPrintPurchaseOrderPDF = "printPurchaseOrderPDFOnly";
        String methodToCallDocHandler = "docHandler";
        String printPOPDFUrl = getUrlForPrintPO(basePath, docId, methodToCallPrintPurchaseOrderPDF);
        String displayPOTabbedPageUrl = getUrlForPrintPO(basePath, docId, methodToCallDocHandler);
        request.setAttribute("printPOPDFUrl", printPOPDFUrl);
        request.setAttribute("displayPOTabbedPageUrl", displayPOTabbedPageUrl);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER);
        request.setAttribute("purchaseOrderLabel", label);

        return mapping.findForward("printPurchaseOrderPDF");
    }

    /**
     * Creates a URL to be used in printing the purchase order.
     *
     * @param basePath String: The base path of the current URL
     * @param docId String: The document ID of the document to be printed
     * @param methodToCall String: The name of the method that will be invoked to do this particular print
     * @return The URL
     */
    protected String getUrlForPrintPO(String basePath, String docId, String methodToCall) {
        StringBuffer result = new StringBuffer(basePath);
        result.append("/purapPurchaseOrder.do?methodToCall=");
        result.append(methodToCall);
        result.append("&docId=");
        result.append(docId);
        result.append("&command=displayDocSearchView");

        return result.toString();
    }

    /**
     * Prints the PDF only, as opposed to <code>firstTransmitPrintPo</code>, which calls this method (indirectly) to print the
     * PDF, and calls the doc handler to display the PO tabbed page.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward printPurchaseOrderPDFOnly(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = request.getParameter("docId");
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            // will throw validation exception if errors occur
            SpringContext.getBean(PurchaseOrderService.class).performPrintPurchaseOrderPDFOnly(poDocId, baosPDF);

            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            String useJavascript = request.getParameter("useJavascript");
            if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                sbContentDispValue.append("attachment");
            }
            else {
                sbContentDispValue.append("inline");
            }
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(poDocId);
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }

        return null;
    }

    /**
     * Print a particular selected PO Quote as a PDF.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm -- The PO Quote must be selected here.
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward printPoQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // String poDocId = request.getParameter("docId");
        // PurchaseOrderDocument po = (PurchaseOrderDocument)
        // SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(poDocId);
        // Integer poSelectedVendorId = new Integer(request.getParameter("quoteVendorId"));
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        PurchaseOrderVendorQuote poVendorQuote = po.getPurchaseOrderVendorQuotes().get(getSelectedLine(request));
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        poVendorQuote.setTransmitPrintDisplayed(false);
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            boolean success = SpringContext.getBean(PurchaseOrderService.class).printPurchaseOrderQuotePDF(po, poVendorQuote, baosPDF);

            if (!success) {
                poVendorQuote.setTransmitPrintDisplayed(true);
                poVendorQuote.setPdfDisplayedToUserOnce(false);

                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            // sbContentDispValue.append("inline");
            sbContentDispValue.append("attachment");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }

        return null;
    }

    public ActionForward printPoQuoteList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = ((PurchaseOrderForm)form).getDocId();
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(po);
        String basePath = getApplicationBaseUrl();
        String methodToCallPrintPurchaseOrderPDF = "printPoQuoteListOnly";
        String methodToCallDocHandler = "docHandler";
        String printPOQuoteListPDFUrl = getUrlForPrintPO(basePath, poDocId, methodToCallPrintPurchaseOrderPDF);
        String displayPOTabbedPageUrl = getUrlForPrintPO(basePath, poDocId, methodToCallDocHandler);
        request.setAttribute("printPOQuoteListPDFUrl", printPOQuoteListPDFUrl);
        request.setAttribute("displayPOTabbedPageUrl", displayPOTabbedPageUrl);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER);
        request.setAttribute("purchaseOrderLabel", label);

        return mapping.findForward("printPOQuoteListPDF");
    }

    /**
     * Print the list of PO Quote requests.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward printPoQuoteListOnly(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = request.getParameter("docId");
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_LIST_");
            sbFilename.append(poDocId);
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            boolean success = SpringContext.getBean(PurchaseOrderService.class).printPurchaseOrderQuoteRequestsListPDF(poDocId, baosPDF);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_PORTAL);
            }
            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            String useJavascript = request.getParameter("useJavascript");
            if (useJavascript == null || useJavascript.equalsIgnoreCase("false")) {
                sbContentDispValue.append("attachment");
            }
            else {
                sbContentDispValue.append("inline");
            }
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }

        return null;
    }

    /**
     * Initiates transmission of a PO Quote request.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward transmitPurchaseOrderQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        PurchaseOrderVendorQuote vendorQuote = po.getPurchaseOrderVendorQuotes().get(getSelectedLine(request));
        if (PurapConstants.QuoteTransmitTypes.PRINT.equals(vendorQuote.getPurchaseOrderQuoteTransmitTypeCode())) {
            vendorQuote.setPurchaseOrderQuoteTransmitTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
            vendorQuote.setTransmitPrintDisplayed(true);
            vendorQuote.setPdfDisplayedToUserOnce(false);
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(po);
        }
        else if (PurapConstants.QuoteTransmitTypes.FAX.equals(vendorQuote.getPurchaseOrderQuoteTransmitTypeCode())) {
            // call fax service
            GlobalVariables.getMessageMap().clearErrorMessages();
            FaxService faxService = SpringContext.getBean(FaxService.class);
            faxService.faxPurchaseOrderPdf(po, false);
            if (GlobalVariables.getMessageMap().getNumberOfPropertiesWithErrors() == 0) {
                vendorQuote.setPurchaseOrderQuoteTransmitTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
                SpringContext.getBean(PurapService.class).saveDocumentNoValidation(po);
            }
        }
        else {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_TRANSMIT_TYPE_NOT_SELECTED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Is invoked when the user clicks on the Select All button on a Purchase Order Retransmit document. It will select
     * the checkboxes of all the items to be included in the retransmission of the PO.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward selectAllForRetransmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(true);
        }

        return returnToPreviousPage(mapping, kualiDocumentFormBase);
    }

    /**
     * Is invoked when the user clicks on the Deselect All button on a Purchase Order Retransmit document. It will
     * uncheck the checkboxes of all the items to be excluded from the retransmission of the PO.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward deselectAllForRetransmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(false);
        }

        return returnToPreviousPage(mapping, kualiDocumentFormBase);
    }

    /**
     * Is invoked when the user clicks on the Retransmit button on both the PO tabbed page and on the Purchase Order
     * Retransmit Document page, which is essentially a PO tabbed page with the other irrelevant tabs being hidden. If it was
     * invoked from the PO tabbed page, if the PO's pending indicator is false, this method will invoke a method in the
     * PurchaseOrderService to update the flags, create the PurchaseOrderRetransmitDocument and route it. If the routing was
     * successful, we'll display the Purchase Order Retransmit Document page to the user, containing the newly created and routed
     * PurchaseOrderRetransmitDocument and a retransmit button as well as a list of items that the user can select to be
     * retransmitted. If it was invoked from the Purchase Order Retransmit Document page, we'll invoke the
     * retransmitPurchaseOrderPDF method to create a PDF document based on the PO information and the items that were selected by
     * the user on the Purchase Order Retransmit Document page to be retransmitted, then display the PDF to the browser.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward retransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();

        boolean success;
        if (po.isPendingActionIndicator()) {
            success = false;
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
        }
        else {
            po = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), PurchaseOrderStatuses.APPDOC_PENDING_RETRANSMIT);
            ((PurchaseOrderRetransmitDocument)po).setShouldDisplayRetransmitTab(true);
        }

        kualiDocumentFormBase.setDocument(po);
        // we only need to set the editing mode to displayRetransmitTab if it's not yet
        // in the editingMode.
        if (!kualiDocumentFormBase.getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB)) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(po);
         // TODO this method is gone, fix for kim
//            kualiDocumentFormBase.populateAuthorizationFields(documentAuthorizer);
        }

        return returnToPreviousPage(mapping, kualiDocumentFormBase);
    }


    /**
     * Is invoked when the user clicks on the Retransmit button on both the PO tabbed page and on the Purchase Order
     * Retransmit Document page, which is essentially a PO tabbed page with the other irrelevant tabs being hidden. If it was
     * invoked from the PO tabbed page, if the PO's pending indicator is false, this method will invoke a method in the
     * PurchaseOrderService to update the flags, create the PurchaseOrderRetransmitDocument and route it. If the routing was
     * successful, we'll display the Purchase Order Retransmit Document page to the user, containing the newly created and routed
     * PurchaseOrderRetransmitDocument and a retransmit button as well as a list of items that the user can select to be
     * retransmitted. If it was invoked from the Purchase Order Retransmit Document page, we'll invoke the
     * retransmitPurchaseOrderPDF method to create a PDF document based on the PO information and the items that were selected by
     * the user on the Purchase Order Retransmit Document page to be retransmitted, then display the PDF to the browser.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward printingPreviewPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String poDocId = ((PurchaseOrderForm)form).getDocId();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            SpringContext.getBean(PurchaseOrderService.class).performPurchaseOrderPreviewPrinting(poDocId, baosPDF);
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        String basePath = getApplicationBaseUrl();
        String docId = ((PurchaseOrderForm) form).getDocId();
        String methodToCallPrintPurchaseOrderPDF = "printPurchaseOrderPDFOnly";
        String methodToCallDocHandler = "docHandler";
        String printPOPDFUrl = getUrlForPrintPO(basePath, docId, methodToCallPrintPurchaseOrderPDF);
        String displayPOTabbedPageUrl = getUrlForPrintPO(basePath, docId, methodToCallDocHandler);
        request.setAttribute("printPOPDFUrl", printPOPDFUrl);
        request.setAttribute("displayPOTabbedPageUrl", displayPOTabbedPageUrl);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER);
        request.setAttribute("purchaseOrderLabel", label);
        GlobalVariables.getUserSession().addObject("isPreview", new Boolean(true));

        return mapping.findForward("printPurchaseOrderPDF");
    }

    /**
     * Forwards to the RetransmitForward.jsp page so that we could open 2 windows for retransmit,
     * one is to display the PO tabbed page and the other one display the pdf document.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printingRetransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String basePath = getApplicationBaseUrl();
        String docId = ((PurchaseOrderForm) form).getPurchaseOrderDocument().getDocumentNumber();
        String methodToCallPrintRetransmitPurchaseOrderPDF = "printingRetransmitPoOnly";
        String methodToCallDocHandler = "docHandler";
        String printPOPDFUrl = getUrlForPrintPO(basePath, docId, methodToCallPrintRetransmitPurchaseOrderPDF);
        String displayPOTabbedPageUrl = getUrlForPrintPO(basePath, docId, methodToCallDocHandler);

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();

        StringBuffer itemIndexesBuffer = createSelectedItemIndexes(po.getItems());
        if (itemIndexesBuffer.length() > 0) {
            itemIndexesBuffer.deleteCharAt(itemIndexesBuffer.lastIndexOf(","));
            request.setAttribute("selectedItemIndexes", itemIndexesBuffer.toString());
        }

        request.setAttribute("printPOPDFUrl", printPOPDFUrl);
        request.setAttribute("displayPOTabbedPageUrl", displayPOTabbedPageUrl);
        request.setAttribute("docId", docId);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(KFSConstants.FinancialDocumentTypeCodes.PURCHASE_ORDER);
        request.setAttribute("purchaseOrderLabel", label);
        return mapping.findForward("retransmitPurchaseOrderPDF");
    }

    /**
     * Helper method to create a StringBuffer of the indexes of items that the user
     * has selected for retransmit to be passed in as an attribute to the RetransmitForward
     * page so that we could add these items later on to the pdf page.
     *
     * @param items The List of items on the PurchaseOrderDocument.
     * @return
     */
    protected StringBuffer createSelectedItemIndexes(List<PurchaseOrderItem>items) {
        StringBuffer itemIndexesBuffer = new StringBuffer();
        int i = 0;
        for (PurchaseOrderItem item : items) {
            if (item.isItemSelectedForRetransmitIndicator()) {
                itemIndexesBuffer.append(i);
                itemIndexesBuffer.append(',');
            }
            i++;
        }
        return itemIndexesBuffer;
    }

    /**
     * Creates a PDF document based on the PO information and the items that were selected by the user on the Purchase Order
     * Retransmit Document page to be retransmitted, then display the PDF to the browser.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward printingRetransmitPoOnly(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        String selectedItemIndexes = request.getParameter("selectedItemIndexes");
        String documentNumber = request.getParameter("poDocumentNumberForRetransmit");
        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(documentNumber);
        String retransmitHeader = request.getParameter("retransmitHeader");

        // setting the isItemSelectedForRetransmitIndicator items of the PO obtained from the database based on its value from
        // the po from the form

        setItemSelectedForRetransmitIndicatorFromPOInForm(selectedItemIndexes, po.getItems());
        po.setRetransmitHeader(retransmitHeader);
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            // below method will throw ValidationException if errors are found
            SpringContext.getBean(PurchaseOrderService.class).retransmitPurchaseOrderPDF(po, baosPDF);

            response.setHeader("Cache-Control", "max-age=30");
            response.setContentType("application/pdf");
            StringBuffer sbContentDispValue = new StringBuffer();
            sbContentDispValue.append("inline");
            sbContentDispValue.append("; filename=");
            sbContentDispValue.append(sbFilename);

            response.setHeader("Content-disposition", sbContentDispValue.toString());

            response.setContentLength(baosPDF.size());

            ServletOutputStream sos;

            sos = response.getOutputStream();

            baosPDF.writeTo(sos);

            sos.flush();

        }
        catch (ValidationException e) {
            LOG.warn("Caught ValidationException while trying to retransmit PO with doc id " + po.getDocumentNumber());
            return mapping.findForward(KFSConstants.MAPPING_ERROR);
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }

        return null;
    }

    /**
     * Sets the itemSelectedForRetransmitIndicator to true to the items that the
     * user has selected for retransmit.
     *
     * @param selectedItemIndexes  The String containing the indexes of items selected to be retransmitted, separated by comma.
     * @param itemsFromDB          The List of items of the PurchaseOrderDocument obtained from the database.
     */
    protected void setItemSelectedForRetransmitIndicatorFromPOInForm(String selectedItemIndexes, List itemsFromDB) {
        int i = 0;
        StringTokenizer tok = new StringTokenizer(selectedItemIndexes, ",");
        while (tok.hasMoreTokens()) {
            i = Integer.parseInt(tok.nextToken());
            ((PurchaseOrderItem) (itemsFromDB.get(i))).setItemSelectedForRetransmitIndicator(true);
        }
    }

    /**
     * Checks on a few conditions that would cause a warning message to be displayed on top of the Purchase Order page.
     *
     * @param po the PurchaseOrderDocument whose status and indicators are to be checked in the conditions
     * @return boolean true if the Purchase Order doesn't have any warnings and false otherwise.
     */
    protected void checkForPOWarnings(PurchaseOrderDocument po, ActionMessages messages) {
        // "This is not the current version of this Purchase Order." (curr_ind = N and doc status is not enroute)
        if (!po.isPurchaseOrderCurrentIndicator() && !po.getDocumentHeader().getWorkflowDocument().isEnroute()) {
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_NOT_CURRENT);
        }
        // "This document is a pending action. This is not the current version of this Purchase Order" (curr_ind = N and doc status
        // is enroute)
        if (!po.isPurchaseOrderCurrentIndicator() && po.getDocumentHeader().getWorkflowDocument().isEnroute()) {
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION_NOT_CURRENT);
        }
        // "There is a pending action on this Purchase Order." (pend_action = Y)
        if (po.isPendingActionIndicator()) {
            KNSGlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION);
        }

        if (!po.isPurchaseOrderCurrentIndicator()) {
            ActionMessage noteMessage = new ActionMessage(PurapKeyConstants.WARNING_PURCHASE_ORDER_ALL_NOTES);
            messages.add(PurapConstants.NOTE_TAB_WARNING, noteMessage);
        }
    }

    /**
     * Add a stipulation to the document.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward addStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        if (StringUtils.isBlank(poForm.getNewPurchaseOrderVendorStipulationLine().getVendorStipulationDescription())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.DOCUMENT_PROPERTY_NAME + "." + PurapPropertyConstants.VENDOR_STIPULATION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
        }
        else {
            PurchaseOrderVendorStipulation newStipulation = poForm.getAndResetNewPurchaseOrderVendorStipulationLine();
            document.getPurchaseOrderVendorStipulations().add(newStipulation);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Delete a stipulation from the document.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward deleteStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorStipulations().remove(getSelectedLine(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Overrides the docHandler method in the superclass. In addition to doing the normal process in the superclass and returning
     * its action forward from the superclass, it also invokes the <code>checkForPOWarnings</code> method to check on a few
     * conditions that could have caused warning messages to be displayed on top of Purchase Order page.
     *
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) poForm.getDocument();

        ActionMessages messages = new ActionMessages();
        checkForPOWarnings(po, messages);
        saveMessages(request, messages);
        return forward;
    }

    /**
     * Sets up the PO document for Quote processing.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward initiateQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        if (!PurchaseOrderStatuses.APPDOC_IN_PROCESS.equals(document.getApplicationDocumentStatus())) {
            // PO must be "in process" in order to initiate a quote
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NOT_IN_PROCESS);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        Calendar currentCalendar = dateTimeService.getCurrentCalendar();
        Date currentSqlDate = new java.sql.Date(currentCalendar.getTimeInMillis());
        document.setPurchaseOrderQuoteInitializationDate(currentSqlDate);
        document.updateAndSaveAppDocStatus(PurchaseOrderStatuses.APPDOC_QUOTE);

        document.setStatusChange(PurchaseOrderStatuses.APPDOC_QUOTE);

        //TODO this needs to be done better, and probably make it a parameter
        Calendar expCalendar = (Calendar) currentCalendar.clone();
        expCalendar.add(Calendar.DAY_OF_MONTH, 10);
        java.sql.Date expDate = new java.sql.Date(expCalendar.getTimeInMillis());

        document.setPurchaseOrderQuoteDueDate(expDate);
        document.getPurchaseOrderVendorQuotes().clear();
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(document);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Add to the Quotes a line to contain a Vendor.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward addVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        PurchaseOrderVendorQuote vendorQuote = poForm.getNewPurchaseOrderVendorQuote();
        String errorPrefix = PurapPropertyConstants.NEW_PURCHASE_ORDER_VENDOR_QUOTE_TEXT;
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedAddVendorToQuoteEvent(errorPrefix, document, vendorQuote));
        if (rulePassed) {
            poForm.getNewPurchaseOrderVendorQuote().setDocumentNumber(document.getDocumentNumber());
            document.getPurchaseOrderVendorQuotes().add(vendorQuote);
            poForm.setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Deletes a Vendor from the list of those from which a Quote should be obtained.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward deleteVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorQuotes().remove(getSelectedLine(request));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Once an awarded Vendor number is present on the PO, verifies the fact, asks the user for confirmation to complete the quoting
     * process with the awarded Vendor, and sets the Vendor information on the purchase order, if confirmation is obtained.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward completeQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        PurchaseOrderVendorQuote awardedQuote = new PurchaseOrderVendorQuote();

        // verify that all vendors have a quote status
        // also run dictionary validations to validate against the DD.
        boolean dictionaryValid = true;
        for (PurchaseOrderVendorQuote poQuote : document.getPurchaseOrderVendorQuotes()) {
            if (poQuote.getPurchaseOrderQuoteStatusCode() == null) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_STATUS_NOT_SELECTED);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            else {
                dictionaryValid &= SpringContext.getBean(DictionaryValidationService.class).isBusinessObjectValid(poQuote, PurapPropertyConstants.VENDOR_QUOTES);
            }
        }

        if (!dictionaryValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // verify quote status fields
        if (poForm.getAwardedVendorNumber() == null) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NO_VENDOR_AWARDED);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            awardedQuote = document.getPurchaseOrderVendorQuote(poForm.getAwardedVendorNumber().intValue());
            if (awardedQuote.getPurchaseOrderQuoteStatusCode() == null) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NOT_TRANSMITTED);

                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            else {
                VendorDetail awardedVendor = SpringContext.getBean(VendorService.class).getVendorDetail(awardedQuote.getVendorHeaderGeneratedIdentifier(), awardedQuote.getVendorDetailAssignedIdentifier());
                if (!awardedVendor.getVendorHeader().getVendorTypeCode().equals("PO")) {
                    GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_AWARD_NON_PO);

                    return mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
            }
        }

        // use question framework to make sure they REALLY want to complete the quote...
        // since the html table tags are not supported for now, the awarded vendor info is displayed without them.
//        String message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD);
//        String vendorRow = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD_ROW);
//
//        String tempRows = "";
//        for (PurchaseOrderVendorQuote poQuote : document.getPurchaseOrderVendorQuotes()) {
//            String tempRow = vendorRow;
//            tempRow = StringUtils.replace(tempRow, "{0}", poQuote.getVendorName());
//            if (poQuote.getPurchaseOrderQuoteAwardTimestamp() == null) {
//                if (awardedQuote.getVendorNumber().equals(poQuote.getVendorNumber())) {
//                    tempRow = StringUtils.replace(tempRow, "{1}", SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().toString());
//                }
//                else {
//                    tempRow = StringUtils.replace(tempRow, "{1}", "");
//                }
//            }
//            else {
//                tempRow = StringUtils.replace(tempRow, "{1}", poQuote.getPurchaseOrderQuoteAwardTimestamp().toString());
//            }
//            if (poQuote.getPurchaseOrderQuoteStatusCode() != null) {
//                poQuote.refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_QUOTE_STATUS);
//                tempRow = StringUtils.replace(tempRow, "{2}", poQuote.getPurchaseOrderQuoteStatus().getStatusDescription());
//            }
//            else {
//                tempRow = StringUtils.replace(tempRow, "{2}", "N/A");
//            }
//            if (poQuote.getPurchaseOrderQuoteRankNumber() != null) {
//                tempRow = StringUtils.replace(tempRow, "{3}", poQuote.getPurchaseOrderQuoteRankNumber());
//            }
//            else {
//                tempRow = StringUtils.replace(tempRow, "{3}", "N/A");
//            }
//            tempRows += tempRow;
//        }
//        message = StringUtils.replace(message, "{0}", tempRows);
        // without the html table tags
        StringBuffer awardedVendorInfo = new StringBuffer(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD));
        int awardNbr = 0;
        for (PurchaseOrderVendorQuote poQuote : document.getPurchaseOrderVendorQuotes()) {

            // vendor name
            awardedVendorInfo.append(++awardNbr + ". ").append("Vendor Name: ");
            awardedVendorInfo.append(poQuote.getVendorName()).append("[br]");

            // awarded date
            awardedVendorInfo.append("Awarded Date: ");
            if (poQuote.getPurchaseOrderQuoteAwardTimestamp() == null) {
                if (awardedQuote.getVendorNumber().equals(poQuote.getVendorNumber())) {
                    awardedVendorInfo.append(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().toString());
                }
            }
            else {
                awardedVendorInfo.append(poQuote.getPurchaseOrderQuoteAwardTimestamp().toString());
            }
            awardedVendorInfo.append("[br]");

            // quote status
            awardedVendorInfo.append("Quote Status: ");
            if (poQuote.getPurchaseOrderQuoteStatusCode() != null) {
                poQuote.refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_QUOTE_STATUS);
                awardedVendorInfo.append(poQuote.getPurchaseOrderQuoteStatus().getStatusDescription());
            }
            else {
                awardedVendorInfo.append("N/A");
            }
            awardedVendorInfo.append("[br]");

            // rank
            awardedVendorInfo.append("Rank: ");
            if (poQuote.getPurchaseOrderQuoteRankNumber() != null) {
                awardedVendorInfo.append(poQuote.getPurchaseOrderQuoteRankNumber());
            }
            else {
                awardedVendorInfo.append("N/A");
            }
            awardedVendorInfo.append("[br][br]");
        }

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) {
            // ask question if not already asked
            return performQuestionWithoutInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_AWARD_QUESTION, awardedVendorInfo.toString(), KFSConstants.CONFIRMATION_QUESTION, PODocumentsStrings.CONFIRM_AWARD_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_AWARD_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // set awarded date
                awardedQuote.setPurchaseOrderQuoteAwardTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());

                Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
                document.setPurchaseOrderQuoteAwardedDate(currentSqlDate);

                // PO vendor information updated with awarded vendor
                document.setVendorName(awardedQuote.getVendorName());
                document.setVendorNumber(awardedQuote.getVendorNumber());
                Integer headID = awardedQuote.getVendorHeaderGeneratedIdentifier();
                Integer detailID = awardedQuote.getVendorDetailAssignedIdentifier();
                document.setVendorHeaderGeneratedIdentifier(headID);
                document.setVendorDetailAssignedIdentifier(detailID);

                // use PO type address to fill in vendor address
                String campusCode = GlobalVariables.getUserSession().getPerson().getCampusCode();
                VendorAddress pova = SpringContext.getBean(VendorService.class).getVendorDefaultAddress(headID, detailID, AddressTypes.PURCHASE_ORDER, campusCode);
                document.setVendorLine1Address(pova.getVendorLine1Address());
                document.setVendorLine2Address(pova.getVendorLine2Address());
                document.setVendorCityName(pova.getVendorCityName());
                document.setVendorStateCode(pova.getVendorStateCode());
                document.setVendorPostalCode(pova.getVendorZipCode());
                document.setVendorCountryCode(pova.getVendorCountryCode());
                document.setVendorFaxNumber(pova.getVendorFaxNumber());

                document.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS);

                document.setStatusChange(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS);
                SpringContext.getBean(PurapService.class).saveDocumentNoValidation(document);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Cancels the process of obtaining quotes. Checks whether any of the quote requests have been transmitted. If none have, tries
     * to obtain confirmation from the user for the cancellation. If confirmation is obtained, clears out the list of Vendors from
     * which to obtain quotes and writes the given reason to a note on the PO.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward cancelQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        for (PurchaseOrderVendorQuote quotedVendors : document.getPurchaseOrderVendorQuotes()) {
            if (quotedVendors.getPurchaseOrderQuoteTransmitTimestamp() != null) {
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_ALREADY_TRASNMITTED);

                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        String message = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_CANCEL_QUOTE);
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {

            // ask question if not already asked
            return performQuestionWithInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_CANCEL_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, PODocumentsStrings.CONFIRM_CANCEL_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);

                if (StringUtils.isEmpty(reason)) {

                    return performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PODocumentsStrings.CONFIRM_CANCEL_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, PODocumentsStrings.CONFIRM_CANCEL_RETURN, "", "", PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "250");
                }
                document.getPurchaseOrderVendorQuotes().clear();
                Note cancelNote = new Note();
                cancelNote.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getPerson().getPrincipalId());
                String reasonPrefix = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_CANCEL_QUOTE_NOTE_TEXT);
                cancelNote.setNoteText(reasonPrefix + reason);
                cancelNote.setNoteTypeCode(document.getNoteType().getCode());
                cancelNote.setNotePostedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
                document.addNote(cancelNote);

                document.updateAndSaveAppDocStatus(PurapConstants.PurchaseOrderStatuses.APPDOC_IN_PROCESS);

                //being required to add notes about changing po status even though i'm not changing status
                document.setStatusChange(null);
                SpringContext.getBean(PurapService.class).saveDocumentNoValidation(document);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a protected instance variable
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        // logic for cancel question
        if (question == null) {

            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_CANCEL_QUESTION, kualiConfiguration.getPropertyValueAsString("document.question.cancel.text"), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CANCEL, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((KFSConstants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {

                // if no button clicked just reload the doc
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
            // else go to cancel logic below
        }

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        SpringContext.getBean(DocumentService.class).cancelDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation());

        return returnToSender(request, mapping, kualiDocumentFormBase);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument po = poForm.getPurchaseOrderDocument();

        if (StringUtils.isNotBlank(po.getApplicationDocumentStatus()) && StringUtils.isNotBlank(po.getStatusChange()) && (!StringUtils.equals(po.getApplicationDocumentStatus(), po.getStatusChange()))) {

            WorkflowDocument workflowDocument = po.getDocumentHeader().getWorkflowDocument();
            if (ObjectUtils.isNull(workflowDocument) || workflowDocument.isInitiated() || workflowDocument.isSaved()) {

                return this.askSaveQuestions(mapping, form, request, response, PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION);
            }
        }

        return super.save(mapping, form, request, response);
    }

    /**
     * Obtains confirmation and records reasons for the manual status changes which can take place before the purchase order has
     * been routed. If confirmation is given, changes the status, saves, and records the given reason in an note on the purchase
     * order.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @return An ActionForward
     */
    protected ActionForward askSaveQuestions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType) {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
        ActionForward forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
        String notePrefix = "";

        if (StringUtils.equals(questionType, PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION) && ObjectUtils.isNull(question)) {
            String message = kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE);
            try {

                return this.performQuestionWithInput(mapping, form, request, response, questionType, message, KFSConstants.CONFIRMATION_QUESTION, questionType, "");
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                // If 'No' is the button clicked, just reload the doc
                return forward;
            }

            // Build out full message.
            if (StringUtils.equals(questionType, PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION)) {
                Map<String, String> manuallyChangeableStatuses = new HashMap<String, String>();
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.APPDOC_IN_PROCESS, "In Process");
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.APPDOC_WAITING_FOR_VENDOR, "Waiting for Vendor");
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.APPDOC_WAITING_FOR_DEPARTMENT, "Waiting for Department");

                String key = kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_MANUAL_STATUS_CHANGE_NOTE_PREFIX);
                String oldStatus = manuallyChangeableStatuses.get(po.getApplicationDocumentStatus());
                String newStatus = manuallyChangeableStatuses.get(po.getStatusChange());
                key = StringUtils.replace(key, "{0}", (StringUtils.isBlank(oldStatus) ? " " : oldStatus));
                notePrefix = StringUtils.replace(key, "{1}", (StringUtils.isBlank(newStatus) ? " " : newStatus));
            }
            String noteText = notePrefix + KFSConstants.BLANK_SPACE + reason;
            int noteTextLength = noteText.length();

            // Get note text max length from DD.
            int noteTextMaxLength = SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(Note.class, KFSConstants.NOTE_TEXT_PROPERTY_NAME).intValue();

            if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                // Figure out exact number of characters that the user can enter.
                int reasonLimit = noteTextMaxLength - noteTextLength;

                if (ObjectUtils.isNull(reason)) {
                    // Prevent a NPE by setting the reason to a blank string.
                    reason = "";
                }

                try {
                    if (StringUtils.equals(questionType, PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION)) {

                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyValueAsString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            else if (StringUtils.equals(questionType, PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION)) {
                executeManualStatusChange(po);
                try {
                    forward = super.save(mapping, form, request, response);
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            Note newNote = new Note();
            newNote.setNoteText(noteText);
            newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
            kualiDocumentFormBase.setNewNote(newNote);
            try {
                insertBONote(mapping, kualiDocumentFormBase, request, response);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return forward;
    }

    /**
     * Applies a manual change of status to the given purchase order document.
     *
     * @param po A PurchaseOrderDocument
     */
    protected void executeManualStatusChange(PurchaseOrderDocument po) {
        try {
            po.updateAndSaveAppDocStatus(po.getStatusChange());
            SpringContext.getBean(PurapService.class).saveDocumentNoValidation(po);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.PurchasingAccountsPayableActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PurchaseOrderForm poForm = (PurchaseOrderForm) kualiDocumentFormBase;
        PurchaseOrderDocument po = (PurchaseOrderDocument) poForm.getDocument();
        po.setInternalPurchasingLimit(SpringContext.getBean(PurchaseOrderService.class).getInternalPurchasingDollarLimit(po));
    }

    /**
     * Adds a PurchasingItemCapitalAsset (a container for the Capital Asset Number) to the selected
     * item's list.
     *
     * @param mapping       An ActionMapping
     * @param form          The Form
     * @param request       An HttpServletRequest
     * @param response      The HttpServletResponse
     * @return      An ActionForward
     * @throws Exception
     */
    public ActionForward addAsset(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument document = (PurchaseOrderDocument)poForm.getDocument();
        PurchaseOrderItem item = (PurchaseOrderItem)document.getItemByLineNumber(getSelectedLine(request) + 1);
        //TODO: Add a new way to add assets to the system.
        //item.addAsset();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward removeAlternateVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        document.setAlternateVendorDetailAssignedIdentifier(null);
        document.setAlternateVendorHeaderGeneratedIdentifier(null);
        document.setAlternateVendorName(null);
        document.setAlternateVendorNumber(null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward createReceivingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        String basePath = getApplicationBaseUrl();
        String methodToCallDocHandler = "docHandler";
        String methodToCallReceivingLine = "initiate";

        //set parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCallDocHandler);
        parameters.put(KFSConstants.PARAMETER_COMMAND, methodToCallReceivingLine);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, KFSConstants.FinancialDocumentTypeCodes.LINE_ITEM_RECEIVING);
        parameters.put("purchaseOrderId", document.getPurapDocumentIdentifier().toString() );

        //create url
        String receivingUrl = UrlFactory.parameterizeUrl(basePath + "/" + "purapLineItemReceiving.do", parameters);

        //create forward
        ActionForward forward = new ActionForward(receivingUrl, true);

        return forward;
    }

    public ActionForward resendPoCxml(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderDocument po = (PurchaseOrderDocument) ((PurchaseOrderForm) form).getDocument();
        SpringContext.getBean(PurchaseOrderService.class).retransmitB2BPurchaseOrder(po);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}

