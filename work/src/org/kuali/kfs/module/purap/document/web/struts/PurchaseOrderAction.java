/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.io.ByteArrayOutputStream;
import java.sql.Date;
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
import org.kuali.core.bo.Note;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentAuthorizationService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.BlankFormFile;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.PurapConstants.PurchaseOrderStatuses;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderQuoteList;
import org.kuali.module.purap.bo.PurchaseOrderQuoteListVendor;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchaseOrderSplitDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
import org.kuali.module.purap.service.FaxService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorConstants.AddressTypes;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorPhoneNumber;
import org.kuali.module.vendor.service.VendorService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Struts Action for Purchase Order document.
 */
public class PurchaseOrderAction extends PurchasingActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Handling lookups for alternate vendor for escrow payment that are only specific to Purchase Order.
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
            for (PurchaseOrderQuoteListVendor poQuoteListVendor : poQuoteList.getQuoteListVendors()) {
                VendorDetail newVendor = poQuoteListVendor.getVendorDetail();
                PurchaseOrderVendorQuote newPOVendorQuote = new PurchaseOrderVendorQuote();
                newPOVendorQuote.setVendorName(newVendor.getVendorName());
                newPOVendorQuote.setVendorHeaderGeneratedIdentifier(newVendor.getVendorHeaderGeneratedIdentifier());
                newPOVendorQuote.setVendorDetailAssignedIdentifier(newVendor.getVendorDetailAssignedIdentifier());
                newPOVendorQuote.setDocumentNumber(document.getDocumentNumber());
                boolean foundAddress = false;
                for (VendorAddress address : newVendor.getVendorAddresses()) {
                    if (AddressTypes.QUOTE.equals(address.getVendorAddressTypeCode())) {
                        newPOVendorQuote.setVendorCityName(address.getVendorCityName());
                        newPOVendorQuote.setVendorCountryCode(address.getVendorCountryCode());
                        newPOVendorQuote.setVendorLine1Address(address.getVendorLine1Address());
                        newPOVendorQuote.setVendorLine2Address(address.getVendorLine2Address());
                        newPOVendorQuote.setVendorPostalCode(address.getVendorZipCode());
                        newPOVendorQuote.setVendorStateCode(address.getVendorStateCode());
                        foundAddress = true;
                        break;
                    }
                }
                if (!foundAddress) {
                    newPOVendorQuote.setVendorCityName(newVendor.getDefaultAddressCity());
                    newPOVendorQuote.setVendorCountryCode(newVendor.getDefaultAddressCountryCode());
                    newPOVendorQuote.setVendorLine1Address(newVendor.getDefaultAddressLine1());
                    newPOVendorQuote.setVendorLine2Address(newVendor.getDefaultAddressLine2());
                    newPOVendorQuote.setVendorPostalCode(newVendor.getDefaultAddressPostalCode());
                    newPOVendorQuote.setVendorStateCode(newVendor.getDefaultAddressStateCode());
                }

                String tmpPhoneNumber = null;
                for (VendorPhoneNumber phone : newVendor.getVendorPhoneNumbers()) {
                    if (VendorConstants.PhoneTypes.PO.equals(phone.getVendorPhoneTypeCode())) {
                        newPOVendorQuote.setVendorPhoneNumber(phone.getVendorPhoneNumber());
                    }
                    if (VendorConstants.PhoneTypes.FAX.equals(phone.getVendorPhoneTypeCode())) {
                        newPOVendorQuote.setVendorFaxNumber(phone.getVendorPhoneNumber());
                    }
                    if (VendorConstants.PhoneTypes.PHONE.equals(phone.getVendorPhoneTypeCode())) {
                        tmpPhoneNumber = phone.getVendorPhoneNumber();
                    }
                }
                if (StringUtils.isEmpty(newPOVendorQuote.getVendorPhoneNumber()) && !StringUtils.isEmpty(tmpPhoneNumber)) {
                    newPOVendorQuote.setVendorPhoneNumber(tmpPhoneNumber);
                }
                document.getPurchaseOrderVendorQuotes().add(newPOVendorQuote);
                document.refreshNonUpdateableReferences();
            }
        }

        // Handling lookups for quote vendor search that is specific to Purchase Order.
        String newVendorHeaderGeneratedIdentifier = request.getParameter("newPurchaseOrderVendorQuote.vendorHeaderGeneratedIdentifier");
        String newVendorDetailAssignedIdentifier = request.getParameter("newPurchaseOrderVendorQuote.vendorDetailAssignedIdentifier");
        if (newVendorHeaderGeneratedIdentifier != null && newVendorDetailAssignedIdentifier != null) {
            // retrieve this vendor from DB and add it to the end of the list
            VendorDetail newVendor = SpringContext.getBean(VendorService.class).getVendorDetail(Integer.parseInt(newVendorHeaderGeneratedIdentifier), Integer.parseInt(newVendorDetailAssignedIdentifier));
            PurchaseOrderVendorQuote newPOVendorQuote = poForm.getNewPurchaseOrderVendorQuote();
            newPOVendorQuote.setDocumentNumber(document.getDocumentNumber());
            for (VendorAddress address : newVendor.getVendorAddresses()) {
                if (AddressTypes.QUOTE.equals(address.getVendorAddressTypeCode())) {
                    newPOVendorQuote.setVendorCityName(address.getVendorCityName());
                    newPOVendorQuote.setVendorCountryCode(address.getVendorCountryCode());
                    newPOVendorQuote.setVendorLine1Address(address.getVendorLine1Address());
                    newPOVendorQuote.setVendorLine2Address(address.getVendorLine2Address());
                    newPOVendorQuote.setVendorPostalCode(address.getVendorZipCode());
                    newPOVendorQuote.setVendorStateCode(address.getVendorStateCode());
                    break;
                }
            }

            for (VendorPhoneNumber phone : newVendor.getVendorPhoneNumbers()) {
                if (VendorConstants.PhoneTypes.FAX.equals(phone.getVendorPhoneTypeCode())) {
                    newPOVendorQuote.setVendorFaxNumber(phone.getVendorPhoneNumber());
                } else if (StringUtils.isEmpty(newPOVendorQuote.getVendorPhoneNumber())) { 
                    newPOVendorQuote.setVendorPhoneNumber(phone.getVendorPhoneNumber());
                    break;
                }
            }

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
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        PurchaseOrderDocument purDocument = (PurchaseOrderDocument) purchasingForm.getDocument();
        PurchaseOrderItem item = (PurchaseOrderItem) purDocument.getItem(getSelectedLine(request));
        item.setItemActiveIndicator(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * For use with a specific set of methods of this class that create new purchase order-derived document types in response to
     * user actions, including <code>closePo</code>, <code>reopenPo</code>, <code>paymentHoldPo</code>,
     * <code>removeHoldPo</code>, <code>amendPo</code>, and <code>voidPo</code>. It employs the question framework to ask
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
    private ActionForward askQuestionsAndPerformDocumentAction(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {
        LOG.debug("askQuestionsAndPerformDocumentAction started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        try {
            KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

            // Start in logic for confirming the close.
            if (ObjectUtils.isNull(question)) {
                String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT);
                String message = StringUtils.replace(key, "{0}", operation);

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

                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                    }
                }
            }
            // below used as placeholder to allow code to specify actionForward to return if not a 'success question'
            ActionForward returnActionForward = null;
            if (!po.isPendingActionIndicator()) {
                /*
                 * Below if-else code block calls PurchaseOrderService methods that will throw ValidationException objects if errors
                 * occur during any process in the attempt to perform its actions. Assume, if these return successfully, that the
                 * PurchaseOrderDocument object returned from each is the newly created document and that all actions in the method
                 * were run correctly. NOTE: IF BELOW IF-ELSE IS EDITED THE NEW METHODS CALLED MUST THROW ValidationException OBJECT
                 * IF AN ERROR IS ADDED TO THE GlobalVariables
                 */
                String newStatus = null;
                if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                    newStatus = PurchaseOrderStatuses.AMENDMENT;
                    po = SpringContext.getBean(PurchaseOrderService.class).createAndSavePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), documentType, newStatus);
                    returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT)) {
                    po.setPendingSplit(true);
                    returnActionForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
                }
                else {
                    if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.PENDING_CLOSE;
                    }
                    else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.PENDING_REOPEN;
                    }
                    else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.PENDING_VOID;
                    }
                    else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.PENDING_PAYMENT_HOLD;
                    }
                    else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.PENDING_REMOVE_HOLD;
                    }
                    else if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)) {
                        newStatus = PurchaseOrderStatuses.PENDING_RETRANSMIT;
                    }
                    po = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), newStatus);
                }
                if (!GlobalVariables.getErrorMap().isEmpty()) {
                    throw new ValidationException("errors occurred during new PO creation");
                }
                
                String previousDocumentId = kualiDocumentFormBase.getDocId();
                if (!documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT)) {                   
                    // assume at this point document was created properly and 'po' variable is new PurchaseOrderDocument created
                    kualiDocumentFormBase.setDocument(po);
                    kualiDocumentFormBase.setDocId(po.getDocumentNumber());
                    kualiDocumentFormBase.setDocTypeName(po.getDocumentHeader().getWorkflowDocument().getDocumentType());
                }

                Note newNote = new Note();
                if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
                    noteText = noteText + " (Previous Document Id is " + previousDocumentId + ")";
                }
                newNote.setNoteText(noteText);
                newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());
                kualiDocumentFormBase.setNewNote(newNote);
                // see KULPURAP-1984 for an explanation of why this is required and another way to do it.
                kualiDocumentFormBase.setAttachmentFile(new BlankFormFile());

                insertBONote(mapping, kualiDocumentFormBase, request, response);
                if (StringUtils.isNotEmpty(messageType)) {
                    GlobalVariables.getMessageList().add(messageType);
                }
            }
            if (ObjectUtils.isNotNull(returnActionForward)) {
                return returnActionForward;
            }
            else {

                return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyString(messageType), PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
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

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.CLOSE_QUESTION, PODocumentsStrings.CLOSE_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PODocumentsStrings.CLOSE_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT, operation);
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

        // Also need to send an FYI to the AP workgroup.
        // KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        // PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        // WorkgroupVO workgroupVO =
        // SpringContext.getBean(WorkflowGroupService.class).getWorkgroupByGroupName(PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        // SpringContext.getBean(PurchaseOrderService.class).sendFYItoWorkgroup(po, kualiDocumentFormBase.getAnnotation(),
        // workgroupVO.getWorkgroupId() );

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
     * @see org.kuali.module.purap.document.PurchaseOrderReopenDocument
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
     * @see org.kuali.module.purap.document.PurchaseOrderAmendmentDocument
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
     * @see org.kuali.module.purap.document.PurchaseOrderVoidDocument
     */
    public ActionForward voidPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Void PO started");
        String operation = "Void ";

        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.VOID_QUESTION, PODocumentsStrings.VOID_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, PODocumentsStrings.VOID_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_VOID_DOCUMENT, operation);
    }
    
    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     * @see org.kuali.module.purap.document.PurchaseOrderSplitDocument
     */
    public ActionForward splitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Split PO started");
        String operation = "Split ";

        // Extract the PO id for the note prefix.
        PurchaseOrderForm poForm = (PurchaseOrderForm)form;
        PurchaseOrderDocument poDocument = (PurchaseOrderDocument)poForm.getDocument();
        String poID = poDocument.getDocumentNumber();
        
        return askQuestionsAndPerformDocumentAction(mapping, form, request, response, PODocumentsStrings.SPLIT_QUESTION, PODocumentsStrings.SPLIT_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_SPLIT_DOCUMENT, PODocumentsStrings.SPLIT_NOTE_PREFIX_OLD_DOC, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_SPLIT_DOCUMENT, operation);
    }
    
    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward continuePurchaseOrderSplit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception  {
        LOG.debug("Continue Purchase Order Split started");
        
        //TODO: Implement business rules.
        
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument poToSplit = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = (List<PurchaseOrderItem>)poToSplit.getItems();
        TypedArrayList movingPOItems = new TypedArrayList(PurchaseOrderItem.class);
        TypedArrayList remainingPOItems = new TypedArrayList(PurchaseOrderItem.class);
        for (PurchaseOrderItem item : items) {
            if(item.isMovingToSplit()) {
                movingPOItems.add(item);
            }          
            else {
                remainingPOItems.add(item);
            }
        }
        poToSplit = (PurchaseOrderDocument)SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(poToSplit.getPurapDocumentIdentifier());
        poToSplit.setItems(remainingPOItems);
        poToSplit.renumberItems(0);
        SpringContext.getBean(PurapService.class).saveDocumentNoValidation(poToSplit);
        
        PurchaseOrderSplitDocument splitPO = SpringContext.getBean(PurchaseOrderService.class).createAndSavePurchaseOrderSplitDocument(movingPOItems, poToSplit.getDocumentNumber());
        
        kualiDocumentFormBase.setDocument(splitPO);
        kualiDocumentFormBase.setDocId(splitPO.getDocumentNumber());
        kualiDocumentFormBase.setDocTypeName(splitPO.getDocumentHeader().getWorkflowDocument().getDocumentType());
        try {
            loadDocument(kualiDocumentFormBase);
        }
        catch (WorkflowException we) {
            throw new RuntimeException(we);
        }
                      
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * 
     * This method...
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancelPurchaseOrderSplit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Cancel Purchase Order Split started");
        
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
        
        po.setPendingSplit(false);
        po.setCopyingNotesWhenSplitting(false);       
        ActionForward forward = reload(mapping, kualiDocumentFormBase, request, response);
        List<Note> notes = po.getBoNotes();
        po.deleteNote(notes.get(notes.size() - 1));
        kualiDocumentFormBase.setDocument(po);
        //SpringContext.getBean(PurapService.class).saveDocumentNoValidation(po);
        
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
        String poDocId = request.getParameter("docId");
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            SpringContext.getBean(PurchaseOrderService.class).performPurchaseOrderFirstTransmitViaPrinting(poDocId, baosPDF);
        }
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        String basePath = getBasePath(request);
        String docId = ((PurchaseOrderForm) form).getDocId();
        String methodToCallPrintPurchaseOrderPDF = "printPurchaseOrderPDFOnly";
        String methodToCallDocHandler = "docHandler";
        String printPOPDFUrl = getUrlForPrintPO(basePath, docId, methodToCallPrintPurchaseOrderPDF);
        String displayPOTabbedPageUrl = getUrlForPrintPO(basePath, docId, methodToCallDocHandler);
        request.setAttribute("printPOPDFUrl", printPOPDFUrl);
        request.setAttribute("displayPOTabbedPageUrl", displayPOTabbedPageUrl);
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(PurchaseOrderDocument.class);
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
    private String getUrlForPrintPO(String basePath, String docId, String methodToCall) {
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
    public ActionForward printPoQuoteList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_QUOTE_LIST_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            boolean success = SpringContext.getBean(PurchaseOrderService.class).printPurchaseOrderQuoteRequestsListPDF(po, baosPDF);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_PORTAL);
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
        PurchaseOrderVendorQuote vendorQuote = (PurchaseOrderVendorQuote) po.getPurchaseOrderVendorQuotes().get(getSelectedLine(request));
        if (PurapConstants.QuoteTransmitTypes.PRINT.equals(vendorQuote.getPurchaseOrderQuoteTransmitTypeCode())) {
            vendorQuote.setPurchaseOrderQuoteTransmitDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            vendorQuote.setTransmitPrintDisplayed(true);
            SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(po);
        }
        else if (PurapConstants.QuoteTransmitTypes.FAX.equals(vendorQuote.getPurchaseOrderQuoteTransmitTypeCode())) {
            // call fax service
            FaxService faxService = SpringContext.getBean(FaxService.class);
            if (faxService.faxPO(po)) {
                vendorQuote.setPurchaseOrderQuoteTransmitDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
                SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(po);
            }
        }
        else {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_TRANSMIT_TYPE_NOT_SELECTED);
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
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
        }
        else {
            po = SpringContext.getBean(PurchaseOrderService.class).createAndRoutePotentialChangeDocument(kualiDocumentFormBase.getDocument().getDocumentNumber(), PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), PurchaseOrderStatuses.PENDING_RETRANSMIT);
        }

        kualiDocumentFormBase.setDocument(po);
        // we only need to set the editing mode to displayRetransmitTab if it's not yet
        // in the editingMode.
        if (!kualiDocumentFormBase.getEditingMode().containsKey(PurapAuthorizationConstants.PurchaseOrderEditMode.DISPLAY_RETRANSMIT_TAB)) {
            DocumentAuthorizer documentAuthorizer = SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer(po);
            kualiDocumentFormBase.populateAuthorizationFields(documentAuthorizer);
        }

        return returnToPreviousPage(mapping, kualiDocumentFormBase);
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
        String basePath = getBasePath(request);
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
        String label = SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(PurchaseOrderDocument.class);
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
    private StringBuffer createSelectedItemIndexes(List<PurchaseOrderItem>items) {
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

        String selectedItemIndexes = (String)request.getParameter("selectedItemIndexes");
        String documentNumber = (String)request.getParameter("poDocumentNumberForRetransmit");
        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderByDocumentNumber(documentNumber);
        String retransmitHeader = (String)request.getParameter("retransmitHeader");
        
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
    private void setItemSelectedForRetransmitIndicatorFromPOInForm(String selectedItemIndexes, List itemsFromDB) {
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
    private void checkForPOWarnings(PurchaseOrderDocument po, ActionMessages messages) {
        // "This is not the current version of this Purchase Order." (curr_ind = N and doc status is not enroute)
        if (!po.isPurchaseOrderCurrentIndicator() && !po.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_NOT_CURRENT);
        }
        // "This document is a pending action. This is not the current version of this Purchase Order" (curr_ind = N and doc status
        // is enroute)
        if (!po.isPurchaseOrderCurrentIndicator() && po.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION_NOT_CURRENT);
        }
        // "There is a pending action on this Purchase Order." (pend_action = Y)
        if (po.isPendingActionIndicator()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION);
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
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_STIPULATION, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
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
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
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
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        if (!PurapConstants.PurchaseOrderStatuses.IN_PROCESS.equals(document.getStatusCode())) {
            // PO must be "in process" in order to initiate a quote
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NOT_IN_PROCESS);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        document.setPurchaseOrderQuoteInitializationDate(new Date(currentSqlDate.getTime()));
        document.setStatusCode(PurapConstants.PurchaseOrderStatuses.QUOTE);
        Date expDate = new Date(currentSqlDate.getTime() + (10 * 24 * 60 * 60 * 1000)); // add 10 days - TODO: make this a parameter!!!
        document.setPurchaseOrderQuoteDueDate(expDate);
        document.getPurchaseOrderVendorQuotes().clear();
        SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(document);

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
        poForm.getNewPurchaseOrderVendorQuote().setDocumentNumber(document.getDocumentNumber());
        document.getPurchaseOrderVendorQuotes().add(poForm.getNewPurchaseOrderVendorQuote());
        poForm.setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        document.refreshNonUpdateableReferences();

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
        document.refreshNonUpdateableReferences();

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
        for (PurchaseOrderVendorQuote poQuote : document.getPurchaseOrderVendorQuotes()) {
            if (poQuote.getPurchaseOrderQuoteStatusCode() == null) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_STATUS_NOT_SELECTED);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);                
            }
        }

        // verify quote status fields
        if (poForm.getAwardedVendorNumber() == null) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NO_VENDOR_AWARDED);

            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            awardedQuote = document.getPurchaseOrderVendorQuote(poForm.getAwardedVendorNumber().intValue());
            if (awardedQuote.getPurchaseOrderQuoteStatusCode() == null) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NOT_TRANSMITTED);

                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        
        // use question framework to make sure they REALLY want to complete the quote...
        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD);
        String vendorRow = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD_ROW);

        String tempRows = "";
        for (PurchaseOrderVendorQuote poQuote : document.getPurchaseOrderVendorQuotes()) {
            String tempRow = vendorRow;
            tempRow = StringUtils.replace(tempRow, "{0}", poQuote.getVendorName());
            if (poQuote.getPurchaseOrderQuoteAwardDate() == null) {
                if (awardedQuote.getVendorNumber().equals(poQuote.getVendorNumber())) {
                    tempRow = StringUtils.replace(tempRow, "{1}", SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().toString());
                }
                else {
                    tempRow = StringUtils.replace(tempRow, "{1}", "");
                }
            }
            else {
                tempRow = StringUtils.replace(tempRow, "{1}", poQuote.getPurchaseOrderQuoteAwardDate().toString());
            }
            if (poQuote.getPurchaseOrderQuoteStatusCode() != null) {
                poQuote.refreshReferenceObject(PurapPropertyConstants.PURCHASE_ORDER_QUOTE_STATUS);
                tempRow = StringUtils.replace(tempRow, "{2}", poQuote.getPurchaseOrderQuoteStatus().getStatusDescription());
            }
            else {
                tempRow = StringUtils.replace(tempRow, "{2}", "N/A");
            }
            if (poQuote.getPurchaseOrderQuoteRankNumber() != null) {
                tempRow = StringUtils.replace(tempRow, "{3}", poQuote.getPurchaseOrderQuoteRankNumber());
            }
            else {
                tempRow = StringUtils.replace(tempRow, "{3}", "N/A");
            }
            tempRows += tempRow;
        }
        message = StringUtils.replace(message, "{0}", tempRows);

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {

            // ask question if not already asked
            return performQuestionWithoutInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_AWARD_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, PODocumentsStrings.CONFIRM_AWARD_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_AWARD_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // set awarded date
                awardedQuote.setPurchaseOrderQuoteAwardDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

                Date currentSqlDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
                document.setPurchaseOrderQuoteAwardedDate(new Date(currentSqlDate.getTime()));

                // PO vendor information updated with awarded vendor
                document.setVendorName(awardedQuote.getVendorName());
                document.setVendorNumber(awardedQuote.getVendorNumber());
                document.setVendorHeaderGeneratedIdentifier(awardedQuote.getVendorHeaderGeneratedIdentifier());
                document.setVendorDetailAssignedIdentifier(awardedQuote.getVendorDetailAssignedIdentifier());
                // document.setVendorDetail(poQuote.getVendorDetail());
                document.setVendorLine1Address(awardedQuote.getVendorLine1Address());
                document.setVendorLine2Address(awardedQuote.getVendorLine2Address());
                document.setVendorCityName(awardedQuote.getVendorCityName());
                document.setVendorStateCode(awardedQuote.getVendorStateCode());
                document.setVendorCountryCode(awardedQuote.getVendorCountryCode());
                document.setVendorPhoneNumber(awardedQuote.getVendorPhoneNumber());
                document.setVendorFaxNumber(awardedQuote.getVendorFaxNumber());

                document.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
                SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(document);
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
            if (quotedVendors.getPurchaseOrderQuoteTransmitDate() != null) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_ALREADY_TRASNMITTED);

                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        String message = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_CANCEL_QUOTE);
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
                cancelNote.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
                String reasonPrefix = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(PurapKeyConstants.PURCHASE_ORDER_CANCEL_QUOTE_NOTE_TEXT);
                cancelNote.setNoteText(reasonPrefix + reason);
                document.addNote(cancelNote);
                document.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
                SpringContext.getBean(PurchaseOrderService.class).saveDocumentNoValidation(document);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a private instance variable
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        // logic for cancel question
        if (question == null) {

            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_CANCEL_QUESTION, kualiConfiguration.getPropertyString("document.question.cancel.text"), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CANCEL, "");
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

        return returnToSender(mapping, kualiDocumentFormBase);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument po = poForm.getPurchaseOrderDocument();

        if (StringUtils.isNotBlank(po.getStatusCode()) && StringUtils.isNotBlank(po.getStatusChange()) && (!StringUtils.equals(po.getStatusCode(), po.getStatusChange()))) {

            KualiWorkflowDocument workflowDocument = po.getDocumentHeader().getWorkflowDocument();
            if (ObjectUtils.isNull(workflowDocument) || workflowDocument.stateIsInitiated() || workflowDocument.stateIsSaved()) {

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
    private ActionForward askSaveQuestions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType) {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
        ActionForward forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
        String notePrefix = "";

        if (StringUtils.equals(questionType, PODocumentsStrings.MANUAL_STATUS_CHANGE_QUESTION) && ObjectUtils.isNull(question)) {
            String message = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE);
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
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.IN_PROCESS, "In Process");
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.WAITING_FOR_VENDOR, "Waiting for Vendor");
                manuallyChangeableStatuses.put(PurchaseOrderStatuses.WAITING_FOR_DEPARTMENT, "Waiting for Department");

                String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_MANUAL_STATUS_CHANGE_NOTE_PREFIX);
                String oldStatus = manuallyChangeableStatuses.get(po.getStatusCode());
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

                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_MANUAL_STATUS_CHANGE), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
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
    private void executeManualStatusChange(PurchaseOrderDocument po) {
        try {
            SpringContext.getBean(PurapService.class).updateStatus(po, po.getStatusChange());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.module.purap.web.struts.action.PurchasingAccountsPayableActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        PurchaseOrderForm form = (PurchaseOrderForm) kualiDocumentFormBase;
        PurchaseOrderDocument po = (PurchaseOrderDocument) form.getDocument();
        form.setPurchaseOrderIdentifier(po.getPurapDocumentIdentifier());
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
        item.addAsset();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward removeAlternateVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        
        document.setAlternateVendorDetailAssignedIdentifier(null);
        document.setAlternateVendorHeaderGeneratedIdentifier(null);
        document.setAlternateVendorName(null);
        document.setAlternateVendorNumber(null);
        
        document.refreshNonUpdateableReferences();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward createReceivingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();        
        
        String basePath = getBasePath(request);
        String methodToCallDocHandler = "docHandler";
        String methodToCallReceivingLine = "initiate";
                        
        //set parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCallDocHandler);
        parameters.put(KFSConstants.PARAMETER_COMMAND, methodToCallReceivingLine);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, "ReceivingLineDocument");        
        parameters.put("purchaseOrderId", document.getPurapDocumentIdentifier().toString() );
        
        //create url
        String receivingUrl = UrlFactory.parameterizeUrl(basePath + "/" + "purapReceivingLine.do", parameters);
        
        //create forward
        ActionForward forward = new ActionForward(receivingUrl, true);
        
        return forward;
        
    }
}
