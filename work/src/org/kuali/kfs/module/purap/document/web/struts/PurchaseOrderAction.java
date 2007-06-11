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
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.kuali.Constants;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderQuoteList;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.bo.VendorPhoneNumber;

/**
 * This class handles specific Actions requests for the Requisition.
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
        BusinessObjectService businessObjectService = SpringServiceLocator.getBusinessObjectService();

        // Handling lookups for alternate vendor for escrow payment that are only specific to Purchase Order
        if (request.getParameter("document.alternateVendorHeaderGeneratedIdentifier") != null && request.getParameter("document.alternateVendorDetailAssignedIdentifier") != null) {
            Integer alternateVendorDetailAssignedId = document.getAlternateVendorDetailAssignedIdentifier();
            Integer alternateVendorHeaderGeneratedId = document.getAlternateVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(alternateVendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(alternateVendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail) businessObjectService.retrieve(refreshVendorDetail);
            document.templateAlternateVendor(refreshVendorDetail);
        }
        // Handling lookups for quote vendor search that is specific to Purchase Order
        if (request.getParameter("document.purchaseOrderQuoteListIdentifier") != null) {
            // do a lookup and add all the vendors!
            Integer poQuoteListIdentifier = document.getPurchaseOrderQuoteListIdentifier();
            PurchaseOrderQuoteList poQuoteList = new PurchaseOrderQuoteList();
            poQuoteList.setPurchaseOrderQuoteListIdentifier(poQuoteListIdentifier);
            poQuoteList = (PurchaseOrderQuoteList) businessObjectService.retrieve(poQuoteList);
//            for (PurchaseOrderQuoteList iterable_element : poQuoteList.get) {
                 //TODO: fill in once service/objects are done
//            }
        }
        
        // Handling lookups for quote vendor search that is specific to Purchase Order
        if (request.getParameter("document.newQuoteVendorHeaderGeneratedIdentifier") != null && request.getParameter("document.newQuoteVendorDetailAssignedIdentifier") != null) {
            // retrieve this vendor from DB and add it to the end of the list
            VendorDetail newVendor = SpringServiceLocator.getVendorService().getVendorDetail(document.getNewQuoteVendorHeaderGeneratedIdentifier(), document.getNewQuoteVendorDetailAssignedIdentifier());
            PurchaseOrderVendorQuote newPOVendorQuote = new PurchaseOrderVendorQuote();
            newPOVendorQuote.setVendorName(newVendor.getVendorName());
            newPOVendorQuote.setVendorHeaderGeneratedIdentifier(newVendor.getVendorHeaderGeneratedIdentifier());
            newPOVendorQuote.setVendorDetailAssignedIdentifier(newVendor.getVendorDetailAssignedIdentifier());
            newPOVendorQuote.setDocumentNumber(document.getDocumentNumber());
            for (VendorAddress address : newVendor.getVendorAddresses()) {
                if ("PO".equals(address.getVendorAddressTypeCode())) {
                    newPOVendorQuote.setVendorCityName(address.getVendorCityName());
                    newPOVendorQuote.setVendorCountryCode(address.getVendorCountryCode());
                    newPOVendorQuote.setVendorLine1Address(address.getVendorLine1Address());
                    newPOVendorQuote.setVendorLine2Address(address.getVendorLine2Address());
                    newPOVendorQuote.setVendorPostalCode(address.getVendorZipCode());
                    newPOVendorQuote.setVendorStateCode(address.getVendorStateCode());
                    break;
                }
            }

            String tmpPhoneNumber = null;
            for (VendorPhoneNumber phone : newVendor.getVendorPhoneNumbers()) {
                if ("PO".equals(phone.getVendorPhoneTypeCode())) {
                    newPOVendorQuote.setVendorPhoneNumber(phone.getVendorPhoneNumber());
                }
                if ("FX".equals(phone.getVendorPhoneTypeCode())) {
                    newPOVendorQuote.setVendorFaxNumber(phone.getVendorPhoneNumber());
                }
                if ("PH".equals(phone.getVendorPhoneTypeCode())) {
                    tmpPhoneNumber = phone.getVendorPhoneNumber();
                }
            }
            if (StringUtils.isEmpty(newPOVendorQuote.getVendorPhoneNumber()) && !StringUtils.isEmpty(tmpPhoneNumber)) {
                newPOVendorQuote.setVendorPhoneNumber(tmpPhoneNumber);
            }

            document.getPurchaseOrderVendorQuotes().add(newPOVendorQuote);
        }

        String newStipulation = request.getParameter("document.vendorStipulationDescription");
        if (StringUtils.isNotEmpty(newStipulation)) {
            poForm.getNewPurchaseOrderVendorStipulationLine().setVendorStipulationDescription(newStipulation);
        }
        return super.refresh(mapping, form, request, response);
    }

    /**
     * Inactivate an item from the po document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward inactivateItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchasingFormBase purchasingForm = (PurchasingFormBase) form;

        PurchaseOrderDocument purDocument = (PurchaseOrderDocument) purchasingForm.getDocument();
        PurchaseOrderItem item = (PurchaseOrderItem)purDocument.getItem(getSelectedLine(request));
        item.setItemActiveIndicator(false);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward viewRelatedDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewRelatedDocuments() enter action");

        // TODO add code

        return mapping.findForward("viewRelatedDocuments");
    }

    public ActionForward viewPaymentHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewPaymentHistory() enter action");

        // TODO add code

        return mapping.findForward("viewPaymentHistory");
    }

    /**
     * This method...
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
    private ActionForward askQuestionsAndRoute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType, String operation) throws Exception {

        LOG.debug("AskQuestionsAndRoute started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Start in logic for confirming the close.
        if (question == null) {
            String key = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT);
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
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT), KFSConstants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                }
            }
        }
        boolean success;
        if (po.isPendingActionIndicator()) {
            success = false;
        }
        else {
            success = SpringServiceLocator.getPurchaseOrderService().updateFlagsAndRoute(kualiDocumentFormBase, documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        }
        if (!success) {
            return mapping.findForward(KFSConstants.MAPPING_ERROR);
        }

        Note newNote = new Note();
        if (documentType.equalsIgnoreCase(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
            noteText = noteText + " (Previous Document Id is " + kualiDocumentFormBase.getDocId() + ")";
        }
        newNote.setNoteText(noteText);
        newNote.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.toString());
        kualiDocumentFormBase.setNewNote(newNote);
        insertBONote(mapping, form, request, response);

        GlobalVariables.getMessageList().add(messageType);
        if (documentType.equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT)) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyString(messageType), PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
        }
    }

    public ActionForward closePo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("ClosePO started.");
        String operation = "Close ";
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.CLOSE_QUESTION, PODocumentsStrings.CLOSE_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PODocumentsStrings.CLOSE_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT, operation);
    }

    public ActionForward paymentHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("PaymentHoldPO started.");
        String operation = "Hold Payment ";
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.PAYMENT_HOLD_QUESTION, PODocumentsStrings.PAYMENT_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, PODocumentsStrings.PAYMENT_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_PAYMENT_HOLD, operation);
    }

    public ActionForward removeHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("RemoveHoldPO started.");
        String operation = "Remove Payment Hold ";
        ActionForward forward = askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.REMOVE_HOLD_QUESTION, PODocumentsStrings.REMOVE_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT, PODocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REMOVE_HOLD, operation);

        // Also need to send an FYI to the AP workgroup.
        // KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        // PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        // WorkgroupVO workgroupVO =
        // SpringServiceLocator.getWorkflowGroupService().getWorkgroupByGroupName(PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        // SpringServiceLocator.getPurchaseOrderService().sendFYItoWorkgroup(po, kualiDocumentFormBase.getAnnotation(),
        // workgroupVO.getWorkgroupId() );

        return forward;
    }

    /**
     * This method is invoked when the user pressed on the Open Order button on a Purchase Order page that has status "Close" to
     * reopen the PO. It will display the question page to the user to ask whether the user really wants to reopen the PO and ask
     * the user to enter a reason in the text area. If the user has entered the reason, it will invoke the updateFlagsAndRoute
     * service method to do the processing for reopening a PO, then display a Single Confirmation page to inform the user that the
     * PO Reopen Document has been routed.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reopenPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Reopen PO started");
        String operation = "Reopen ";
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.REOPEN_PO_QUESTION, PODocumentsStrings.CONFIRM_REOPEN_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, PODocumentsStrings.REOPEN_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REOPEN_DOCUMENT, operation);
    }

    /**
     * This method...
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward amendPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Amend PO started");
        String operation = "Amend ";
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.AMENDMENT_PO_QUESTION, PODocumentsStrings.CONFIRM_AMENDMENT_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_AMENDMENT_DOCUMENT, PODocumentsStrings.AMENDMENT_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_AMEND_DOCUMENT, operation);
    }

    public ActionForward voidPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Void PO started");
        String operation = "Void ";
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.VOID_QUESTION, PODocumentsStrings.VOID_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, PODocumentsStrings.VOID_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_VOID_DOCUMENT, operation);
    }

    /**
     * This method is executed when the user click on the "print" button on a Purchase Order Print Document page. It will display
     * the PDF document on the browser window and set a few fields (transmission dates and statuses) of the original Purchase Order
     * Document and Purchase Order Print Document itself and save these fields to the database.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward printPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            // for testing Generate PO PDF, set the APO to true
            po.setPurchaseOrderAutomaticIndicator(true);
            boolean success = SpringServiceLocator.getPurchaseOrderService().printPurchaseOrderPDF(po, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), baosPDF);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_ERROR);
            }
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
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        return null;
    }

    public ActionForward firstTransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            String environment = "dev";
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");

            // for testing Generate PO PDF, set the APO to true
            po.setPurchaseOrderAutomaticIndicator(true);
            boolean success = SpringServiceLocator.getPurchaseOrderService().firstPurchaseOrderTransmitViaPrint(kualiDocumentFormBase, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), baosPDF, environment);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(KFSConstants.MAPPING_ERROR);
            }
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
        finally {
            if (baosPDF != null) {
                baosPDF.reset();
            }
        }
        return null;
    }

    /**
     * This method is invoked when the user clicks on the Select All button on a Purchase Order Retransmit document. It will select
     * the checkboxes of all the items to be included in the retransmission of the PO.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward selectAllForRetransmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(true);
        }
        ((PurchaseOrderForm) kualiDocumentFormBase).addButtons();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is invoked when the user clicks on the Deselect All button on a Purchase Order Retransmit document. It will
     * uncheck the checkboxes of all the items to be excluded from the retransmission of the PO.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deselectAllForRetransmit(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(false);
        }
        ((PurchaseOrderForm) kualiDocumentFormBase).addButtons();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method is invoked when the user clicks on the Retransmit button on both the PO tabbed page and on the Purchase Order
     * Retransmit Document page, which is essentially a PO tabbed page with the other irrelevant tabs being hidden. If it was
     * invoked from the PO tabbed page, if the PO's pending indicator is false, this method will invoke the updateFlagsAndRoute in
     * the PurchaseOrderService to update the flags, create the PurchaseOrderRetransmitDocument and route it. If the routing was
     * successful, we'll display the Purchase Order Retransmit Document page to the user, containing the newly created and routed
     * PurchaseOrderRetransmitDocument and a retransmit button as well as a list of items that the user can select to be
     * retransmitted. If it was invoked from the Purchase Order Retransmit Document page, we'll invoke the
     * retransmitPurchaseOrderPDF method to create a PDF document based on the PO information and the items that were selected by
     * the user on the Purchase Order Retransmit Document page to be retransmitted, then display the PDF to the browser.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward retransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        DocumentHeader oldDocumentHeader = po.getDocumentHeader();
        if (!po.getDocumentHeader().getWorkflowDocument().getDocumentType().equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)) {

            String documentType = PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT;

            boolean success;
            if (po.isPendingActionIndicator()) {
                success = false;
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_IS_PENDING);
            }
            else {
                success = SpringServiceLocator.getPurchaseOrderService().updateFlagsAndRoute(kualiDocumentFormBase, documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
            }
            if (!success) {
                return mapping.findForward(KFSConstants.MAPPING_ERROR);
            }
            else {
                DocumentHeader newDocumentHeader = kualiDocumentFormBase.getDocument().getDocumentHeader();
                kualiDocumentFormBase.getDocument().setDocumentHeader(oldDocumentHeader);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }
        else {
            // This is a PurchaseOrderRetransmitDocument, so we'll display the pdf now
            List items = po.getItems();
            String retransmitHeader = po.getRetransmitHeader();
            po = SpringServiceLocator.getPurchaseOrderService().getPurchaseOrderByDocumentNumber(po.getDocumentNumber());
            po.setItems(items);
            po.setRetransmitHeader(retransmitHeader);
            ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
            try {
                StringBuffer sbFilename = new StringBuffer();
                sbFilename.append("PURAP_PO_");
                sbFilename.append(po.getPurapDocumentIdentifier());
                sbFilename.append("_");
                sbFilename.append(System.currentTimeMillis());
                sbFilename.append(".pdf");

                boolean success = SpringServiceLocator.getPurchaseOrderService().retransmitPurchaseOrderPDF(po, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), baosPDF);

                if (!success) {
                    if (baosPDF != null) {
                        baosPDF.reset();
                    }
                    return mapping.findForward(KFSConstants.MAPPING_ERROR);
                }
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
            finally {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
            }
            return null;
        }
    }

    /**
     * This method is to check on a few conditions that would cause a warning message to be displayed on top of the Purchase Order
     * page.
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
            ActionMessage statusHistoryMessage = new ActionMessage(PurapKeyConstants.WARNING_PURCHASE_ORDER_ENTIRE_STATUS_HISTORY);
            messages.add(PurapConstants.NOTE_TAB_WARNING, noteMessage);
            messages.add(PurapConstants.STATUS_HISTORY_TAB_WARNING, statusHistoryMessage);
        }
    }

    /**
     * Add a stipulation to the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward addStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        // TODO: should this be calling the Rule class?
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
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward deleteStipulation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorStipulations().remove(getSelectedLine(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) This
     *      method overrides the docHandler method in the superclass. In addition to doing the normal process in the superclass and
     *      returning its action forward from the superclass, it also invokes the checkForPOWarnings method to check on a few
     *      conditions that could have caused warning messages to be displayed on top of Purchase Order page.
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        poForm.addButtons();
        PurchaseOrderDocument po = (PurchaseOrderDocument)poForm.getDocument();
        ActionMessages messages = new ActionMessages();
        checkForPOWarnings(po, messages);
        saveMessages(request, messages);
        
        return forward;
    }
    
    /**
     * Delete a stipulation from the document.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward initiateQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.setStatusCode(PurapConstants.PurchaseOrderStatuses.QUOTE);
        Date expDate = SpringServiceLocator.getDateTimeService().getCurrentSqlDate();
        expDate.setTime(expDate.getTime() + (10 * 24 * 60 * 60 * 1000)); //add 10 days - need to move this into a DB setting
        document.setPurchaseOrderQuoteDueDate(expDate);
        document.getPurchaseOrderVendorQuotes().clear();
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward printQuoteList(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
//        SpringServiceLocator.getPrintService().generatePurchaseOrderQuotePdf(po, povq, byteArrayOutputStream, environment)
        document.setStatusCode(PurapConstants.PurchaseOrderStatuses.CANCELLED);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        poForm.getNewPurchaseOrderVendorQuote().setDocumentNumber(document.getDocumentNumber());
        document.getPurchaseOrderVendorQuotes().add(poForm.getNewPurchaseOrderVendorQuote());
        poForm.setNewPurchaseOrderVendorQuote(new PurchaseOrderVendorQuote());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteVendor(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        document.getPurchaseOrderVendorQuotes().remove(getSelectedLine(request));
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward completeQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();
        PurchaseOrderVendorQuote poQuote = new PurchaseOrderVendorQuote();
        // verify quote status fields
        if (poForm.getAwardedVendorNumber() == null) {
            GlobalVariables.getErrorMap().putError(PurapPropertyConstants.VENDOR_QUOTES, PurapKeyConstants.ERROR_PURCHASE_ORDER_QUOTE_NO_VENDOR_AWARDED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        else {
            poQuote = document.getPurchaseOrderVendorQuote(poForm.getAwardedVendorNumber().intValue());
        }
        
        // use question framework to make sure they REALLY want to complete the quote...
        String message = SpringServiceLocator.getKualiConfigurationService().getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_AWARD);
        message = StringUtils.replace(message, "{0}", poQuote.getVendorName());
        message = StringUtils.replace(message, "{1}", poQuote.getPurchaseOrderQuoteAwardDate().toString());
        message = StringUtils.replace(message, "{2}", poQuote.getPurchaseOrderQuoteStatusCode());
        message = StringUtils.replace(message, "{3}", poQuote.getPurchaseOrderQuoteRankNumber());

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {
            // ask question if not already asked
            return performQuestionWithoutInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_AWARD_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION,  PODocumentsStrings.CONFIRM_AWARD_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_AWARD_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // set awarded date
                poQuote.setPurchaseOrderQuoteAwardDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());

                // PO vendor information updated with awarded vendor
                document.setVendorName(poQuote.getVendorName());
                document.setVendorNumber(poQuote.getVendorNumber());
                document.setVendorHeaderGeneratedIdentifier(poQuote.getVendorHeaderGeneratedIdentifier());
                document.setVendorDetailAssignedIdentifier(poQuote.getVendorDetailAssignedIdentifier());
//                document.setVendorDetail(poQuote.getVendorDetail());
                document.setVendorLine1Address(poQuote.getVendorLine1Address());
                document.setVendorLine2Address(poQuote.getVendorLine2Address());
                document.setVendorCityName(poQuote.getVendorCityName());
                document.setVendorStateCode(poQuote.getVendorStateCode());
                document.setVendorCountryCode(poQuote.getVendorCountryCode());
                document.setVendorPhoneNumber(poQuote.getVendorPhoneNumber());
                document.setVendorFaxNumber(poQuote.getVendorFaxNumber());

                document.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward cancelQuote(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PurchaseOrderForm poForm = (PurchaseOrderForm) form;
        PurchaseOrderDocument document = (PurchaseOrderDocument) poForm.getDocument();

        for (PurchaseOrderVendorQuote quotedVendors : document.getPurchaseOrderVendorQuotes()) {
            if (quotedVendors.getPurchaseOrderQuoteTransmitDate() != null) {
                GlobalVariables.getErrorMap().putError(PurapPropertyConstants.QUOTE_TRANSMITTED, PurapKeyConstants.ERROR_STIPULATION_DESCRIPTION);
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        String message = SpringServiceLocator.getKualiConfigurationService().getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CONFIRM_CANCEL_QUOTE);
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {
            // ask question if not already asked
            return performQuestionWithInput(mapping, form, request, response, PODocumentsStrings.CONFIRM_CANCEL_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION,  PODocumentsStrings.CONFIRM_CANCEL_RETURN, "");
        }
        else {
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PODocumentsStrings.CONFIRM_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);

                if (StringUtils.isEmpty(reason)) {
                    return performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PODocumentsStrings.CONFIRM_CANCEL_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION,  PODocumentsStrings.CONFIRM_CANCEL_RETURN, "", "", PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME, "250");
                }
                document.getPurchaseOrderVendorQuotes().clear();
                Note cancelNote = new Note();
                cancelNote.setAuthorUniversalIdentifier(GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier());
                String reasonPrefix = SpringServiceLocator.getKualiConfigurationService().getPropertyString(PurapKeyConstants.PURCHASE_ORDER_CANCEL_QUOTE_NOTE_TEXT);
                cancelNote.setNoteText(reasonPrefix + reason);
                document.addNote(cancelNote);
                document.setStatusCode(PurapConstants.PurchaseOrderStatuses.IN_PROCESS);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        // this should probably be moved into a private instance variable
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // logic for cancel question
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_CANCEL_QUESTION, kualiConfiguration.getPropertyString("document.question.cancel.text"), Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CANCEL, "");
        }
        else {
            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((Constants.DOCUMENT_CANCEL_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            // else go to cancel logic below
        }

        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        SpringServiceLocator.getPurchaseOrderService().cancelAmendment(kualiDocumentFormBase);
        SpringServiceLocator.getDocumentService().cancelDocument(kualiDocumentFormBase.getDocument(), kualiDocumentFormBase.getAnnotation());

        return returnToSender(mapping, kualiDocumentFormBase);
    }
}