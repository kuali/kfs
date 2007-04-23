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
import org.kuali.core.bo.Note;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PODocumentsStrings;
import org.kuali.module.purap.PurapConstants.PurchaseOrderDocTypes;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.vendor.bo.VendorDetail;

import edu.iu.uis.eden.clientapp.vo.WorkgroupVO;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
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

        //Handling lookups for alternate vendor for escrow payment that are only specific to Purchase Order
        if (request.getParameter("document.alternateVendorHeaderGeneratedIdentifier") != null && request.getParameter("document.alternateVendorDetailAssignedIdentifier") != null) {
            Integer alternateVendorDetailAssignedId = document.getAlternateVendorDetailAssignedIdentifier();
            Integer alternateVendorHeaderGeneratedId = document.getAlternateVendorHeaderGeneratedIdentifier();
            VendorDetail refreshVendorDetail = new VendorDetail();
            refreshVendorDetail.setVendorDetailAssignedIdentifier(alternateVendorDetailAssignedId);
            refreshVendorDetail.setVendorHeaderGeneratedIdentifier(alternateVendorHeaderGeneratedId);
            refreshVendorDetail = (VendorDetail)businessObjectService.retrieve(refreshVendorDetail);
            document.templateAlternateVendor(refreshVendorDetail);
        } 
        
        String newStipulation = request.getParameter("document.vendorStipulationDescription");
        if (StringUtils.isNotEmpty(newStipulation)) {
            poForm.getNewPurchaseOrderVendorStipulationLine().setVendorStipulationDescription(newStipulation);
        }    
        return super.refresh(mapping, form, request, response);
    }

    public ActionForward viewRelatedDocuments(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewRelatedDocuments() enter action");

        //TODO add code

        return mapping.findForward("viewRelatedDocuments");
    }

    public ActionForward viewPaymentHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("viewPaymentHistory() enter action");

        //TODO add code

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
    private ActionForward askQuestionsAndRoute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String questionType, String confirmType, String documentType, String notePrefix, String messageType) throws Exception {
    
        LOG.debug("AskQuestionsAndRoute started.");
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(Constants.QUESTION_REASON_ATTRIBUTE_NAME);
        String noteText = "";

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Start in logic for confirming the close.
        if (question == null) {
            // Ask question if not already asked.
            return this.performQuestionWithInput(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT), Constants.CONFIRMATION_QUESTION, questionType, "");
        }
        else {
            Object buttonClicked = request.getParameter( Constants.QUESTION_CLICKED_BUTTON );
            if (question.equals(questionType) && buttonClicked.equals(ConfirmationQuestion.NO)) {
                //If 'No' is the button clicked, just reload the doc
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            else if (question.equals(confirmType) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {
                // This is the case when the user clicks on "OK" in the end. 
                // After we inform the user that the close has been rerouted, we'll redirect to the portal page.
                return mapping.findForward(Constants.MAPPING_PORTAL);
            }
            else {
                // Have to check length on value entered.
                String introNoteMessage = notePrefix + Constants.BLANK_SPACE;

                // Build out full message.
                noteText = introNoteMessage + reason;
                int noteTextLength = noteText.length();

                // Get note text max length from DD.
                int noteTextMaxLength = SpringServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, Constants.NOTE_TEXT_PROPERTY_NAME).intValue();

                if (StringUtils.isBlank(reason) || (noteTextLength > noteTextMaxLength)) {
                    // Figure out exact number of characters that the user can enter.
                    int reasonLimit = noteTextMaxLength - noteTextLength;

                    if (reason == null) {
                        // Prevent a NPE by setting the reason to a blank string.
                        reason = "";
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, questionType, kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_DOCUMENT), Constants.CONFIRMATION_QUESTION, questionType, "", reason, PurapKeyConstants.ERROR_PURCHASE_ORDER_REASON_REQUIRED, Constants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                }
            }   
        }
        boolean success = SpringServiceLocator.getPurchaseOrderService().updateFlagsAndRoute(po, documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
                    
        if (!success) {
            return mapping.findForward(Constants.MAPPING_ERROR);
        }

        Note newNote = new Note();
        newNote.setNoteText(noteText);
        newNote.setNoteTypeCode("BO");
        kualiDocumentFormBase.setNewNote(newNote);
        insertBONote(mapping, form, request, response);
                
        GlobalVariables.getMessageList().add(messageType);
        return this.performQuestionWithoutInput(mapping, form, request, response, confirmType, kualiConfiguration.getPropertyString(messageType), PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, questionType, "");
    }
    
    public ActionForward closePo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("ClosePO started.");
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.CLOSE_QUESTION, PODocumentsStrings.CLOSE_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_CLOSE_DOCUMENT, PODocumentsStrings.CLOSE_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT);
    }

    public ActionForward paymentHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("PaymentHoldPO started.");
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.PAYMENT_HOLD_QUESTION, PODocumentsStrings.PAYMENT_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_PAYMENT_HOLD_DOCUMENT, PODocumentsStrings.PAYMENT_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_PAYMENT_HOLD);        
    }

    public ActionForward removeHoldPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("RemoveHoldPO started.");
        ActionForward forward = askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.REMOVE_HOLD_QUESTION, PODocumentsStrings.REMOVE_HOLD_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_REMOVE_HOLD_DOCUMENT, PODocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_REMOVE_HOLD);

        // Also need to send an FYI to the AP workgroup.
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        WorkgroupVO workgroupVO = SpringServiceLocator.getWorkflowGroupService().getWorkgroupByGroupName(PurapConstants.Workgroups.WORKGROUP_ACCOUNTS_PAYABLE);
        SpringServiceLocator.getPurchaseOrderService().sendFYItoWorkgroup(po, kualiDocumentFormBase.getAnnotation(), workgroupVO.getWorkgroupId() );
    
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
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.REOPEN_PO_QUESTION, PODocumentsStrings.CONFIRM_REOPEN_QUESTION, PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, PODocumentsStrings.REOPEN_NOTE_PREFIX, PurapKeyConstants.MESSAGE_ROUTE_REOPENED);
    } 

    public ActionForward voidPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("Void PO started");
        return askQuestionsAndRoute(mapping, form, request, response, PODocumentsStrings.VOID_QUESTION, PODocumentsStrings.VOID_CONFIRM, PurchaseOrderDocTypes.PURCHASE_ORDER_VOID_DOCUMENT, PODocumentsStrings.VOID_NOTE_PREFIX, PurapKeyConstants.PURCHASE_ORDER_MESSAGE_VOID_DOCUMENT);
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
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");
            
            //for testing Generate PO PDF, set the APO to true
            po.setPurchaseOrderAutomaticIndicator(true);
            boolean success = SpringServiceLocator.getPurchaseOrderService().printPurchaseOrderPDF(po, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), baosPDF);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(Constants.MAPPING_ERROR);
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
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
        ByteArrayOutputStream baosPDF = new ByteArrayOutputStream();
        try {
            String environment = "dev";
            StringBuffer sbFilename = new StringBuffer();
            sbFilename.append("PURAP_PO_");
            sbFilename.append(po.getPurapDocumentIdentifier());
            sbFilename.append("_");
            sbFilename.append(System.currentTimeMillis());
            sbFilename.append(".pdf");
            
            //for testing Generate PO PDF, set the APO to true
            po.setPurchaseOrderAutomaticIndicator(true);
            boolean success = SpringServiceLocator.getPurchaseOrderService().firstPurchaseOrderTransmitViaPrint(po, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_PRINT_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase), baosPDF, environment);

            if (!success) {
                if (baosPDF != null) {
                    baosPDF.reset();
                }
                return mapping.findForward(Constants.MAPPING_ERROR);
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
     * 
     * This method is invoked when the user clicks on the Select All button on a Purchase Order Retransmit
     * document. It will select the checkboxes of all the items to be included in the retransmission of the
     * PO.
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
        List <PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(true);
        }
        ((PurchaseOrderForm) kualiDocumentFormBase).addButtons();
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    /**
     * This method is invoked when the user clicks on the Deselect All button on a Purchase Order Retransmit
     * document. It will uncheck the checkboxes of all the items to be excluded from the retransmission of the
     * PO.
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
        List <PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem item : items) {
            item.setItemSelectedForRetransmitIndicator(false);
        }
        ((PurchaseOrderForm) kualiDocumentFormBase).addButtons();
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
    
    public ActionForward retransmitPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument) kualiDocumentFormBase.getDocument();
        if (!po.getDocumentHeader().getWorkflowDocument().getDocumentType().equals(PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT)) {
            String documentType = PurchaseOrderDocTypes.PURCHASE_ORDER_RETRANSMIT_DOCUMENT;
            boolean success = SpringServiceLocator.getPurchaseOrderService().updateFlagsAndRoute(po, documentType, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));

            if (!success) {
                return mapping.findForward(Constants.MAPPING_ERROR);
            }
            else {
                ((PurchaseOrderForm) kualiDocumentFormBase).addButtons();
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
        }
        else {
            // This is a PurchaseOrderRetransmitDocument, so we'll display the pdf now
            List items = po.getItems();
            po = SpringServiceLocator.getPurchaseOrderService().getPurchaseOrderByDocumentNumber(po.getDocumentNumber());
            po.setItems(items);
            
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
                    return mapping.findForward(Constants.MAPPING_ERROR);
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
     * TODO: Remove this dummy method when we have the actual PrintAction working
     * 
     * @return
     */
    private PurchaseOrderVendorQuote createDummyPOVQ(PurchaseOrderDocument po) {
        PurchaseOrderVendorQuote povq = new PurchaseOrderVendorQuote();
        povq.setPurchaseOrderVendorQuoteIdentifier(1000);
        povq.setVendorName("Dusty's Cellar");
        povq.setVendorHeaderGeneratedIdentifier(1000);
        povq.setVendorCityName("Okemos");
        povq.setVendorCountryCode("USA");
        povq.setVendorLine1Address("1 Dobie Rd");
        povq.setVendorFaxNumber("517-111-1FAX");
        povq.setVendorPhoneNumber("1-800-DUSTY-CELL");
        povq.setVendorPostalCode("48864");
        po.setPurchaseOrderQuoteDueDate(new Date(System.currentTimeMillis()));
        povq.setPurchaseOrder(po);
        return povq;
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

        return mapping.findForward(Constants.MAPPING_BASIC);
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
        return mapping.findForward(Constants.MAPPING_BASIC);
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
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        ((PurchaseOrderForm)kualiDocumentFormBase).addButtons();
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
        ActionMessages messages = new ActionMessages();
        checkForPOWarnings(po, messages);
        saveMessages(request, messages);
        return forward;
    }
}