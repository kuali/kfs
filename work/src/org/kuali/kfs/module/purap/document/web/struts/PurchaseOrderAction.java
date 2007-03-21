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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.Note;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.bo.PurchaseOrderVendorStipulation;
import org.kuali.module.purap.bo.VendorDetail;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;

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
        if (request.getParameter("document.alternateVendorHeaderGeneratedIdentifier") != null &&
            request.getParameter("document.alternateVendorDetailAssignedIdentifier") != null) {
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

    public ActionForward closePO(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(Constants.QUESTION_REASON_ATTRIBUTE_NAME);
        String closingNoteText = "";

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // start in logic for confirming the close
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, PurapConstants.PODocumentsStrings.PURCHASE_ORDER_CLOSE_QUESTION, 
                    kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CLOSE_DOCUMENT), 
                    Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CLOSE, "");
        }
        else {
            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if( PurapConstants.PODocumentsStrings.PURCHASE_ORDER_CLOSE_QUESTION.equals(question) ) { 
                if( ConfirmationQuestion.NO.equals(buttonClicked) ) {
                    //If 'No' is the button clicked, just reload the doc
                    return mapping.findForward(Constants.MAPPING_BASIC);
                }
                else {
                    // have to check length on value entered
                    String introNoteMessage = kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_CLOSE_NOTE_TEXT_INTRO) 
                        + Constants.BLANK_SPACE;

                    // build out full message
                    closingNoteText = introNoteMessage + reason;
                    int closingNoteTextLength = closingNoteText.length();

                    // get note text max length from DD
                    int noteTextMaxLength = SpringServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, 
                        Constants.NOTE_TEXT_PROPERTY_NAME).intValue();

                    if (StringUtils.isBlank(reason) || (closingNoteTextLength > noteTextMaxLength)) {
                        // figure out exact number of characters that the user can enter
                        int reasonLimit = noteTextMaxLength - closingNoteTextLength;

                        if (reason == null) {
                            // prevent a NPE by setting the reason to a blank string
                            reason = "";
                        }
                        return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PurapConstants.PODocumentsStrings.PURCHASE_ORDER_CLOSE_QUESTION, 
                                kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_QUESTION_CLOSE_DOCUMENT), 
                                Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CLOSE, "", reason, 
                                PurapKeyConstants.ERROR_PURCHASE_ORDER_CLOSE_REASON_REQUIRED, Constants.QUESTION_REASON_ATTRIBUTE_NAME, 
                                new Integer(reasonLimit).toString());
                    }
                    PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
                    SpringServiceLocator.getPurchaseOrderService().updateFlagsAndRoute(po, "KualiPurchaseOrderCloseDocument", 
                            kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));                    
                    GlobalVariables.getMessageList().add(PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT);
                    kualiDocumentFormBase.setAnnotation("");
                    return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PODocumentsStrings.PURCHASE_ORDER_CLOSE_CONFIRM, 
                            kualiConfiguration.getPropertyString(PurapKeyConstants.PURCHASE_ORDER_MESSAGE_CLOSE_DOCUMENT), 
                            Constants.CONFIRMATION_QUESTION, Constants.MAPPING_CLOSE, "");
                }
            }
        }
        return returnToSender(mapping, kualiDocumentFormBase);
    }
    
    /**
     * 
     * This method is invoked when the user pressed on the Open Order button on a Purchase Order
     * page that has status "Close" to reopen the PO.
     * It will display the question page to the user to ask whether the user really wants to
     * reopen the PO and ask the user to enter a reason in the text area. If the user has entered
     * the reason, it will invoke the updateFlagsAndRoute service method to do the processing for
     * reopening a PO, then display a Single Confirmation page to inform the user that the PO Reopen
     * Document has been routed.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward reopenPo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        String reason = request.getParameter(Constants.QUESTION_REASON_ATTRIBUTE_NAME);

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // start in logic for confirming the PO Reopen
        if (question == null) {
            // ask question if not already asked
            return this.performQuestionWithInput(mapping, form, request, response, PurapConstants.PODocumentsStrings.REOPEN_PO_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.QUESTION_REOPEN_PO_DOCUMENT), Constants.CONFIRMATION_QUESTION, "CreatePOReopenDocument", "");
        } 
        else {
            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.PODocumentsStrings.REOPEN_PO_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // if no button clicked just reload the doc
                return mapping.findForward(Constants.MAPPING_BASIC);
            }
            else if ((PurapConstants.PODocumentsStrings.CONFIRM_REOPEN_QUESTION.equals(question)) && buttonClicked.equals(SingleConfirmationQuestion.OK)) {
                // This is the case when the user clicks on "OK" in the end, after we inform the user that the reopen has been rerouted,
                // so we'll redirect to the portal page ?
                return mapping.findForward(Constants.MAPPING_PORTAL);
            }
            else {
                // have to check length on value entered
                String introNoteMessage = kualiConfiguration.getPropertyString(PurapKeyConstants.QUESTION_REOPEN_PO_DOCUMENT) + Constants.BLANK_SPACE;

                int introNoteMessageLength = introNoteMessage.length();

                // get note text max length from DD
                int noteTextMaxLength = SpringServiceLocator.getDataDictionaryService().getAttributeMaxLength(Note.class, Constants.NOTE_TEXT_PROPERTY_NAME).intValue();

                if (StringUtils.isBlank(reason) || (introNoteMessageLength > noteTextMaxLength)) {
                    // figure out exact number of characters that the user can enter
                    int reasonLimit = noteTextMaxLength - introNoteMessageLength;

                    if (reason == null) {
                        // prevent a NPE by setting the reason to a blank string
                        reason = "";
                    }
                    return this.performQuestionWithInputAgainBecauseOfErrors(mapping, form, request, response, PurapConstants.PODocumentsStrings.REOPEN_PO_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.QUESTION_REOPEN_PO_DOCUMENT), Constants.CONFIRMATION_QUESTION, "CreatePOReopenDocument", "", reason, KeyConstants.ERROR_DOCUMENT_DISAPPROVE_REASON_REQUIRED, Constants.QUESTION_REASON_ATTRIBUTE_NAME, new Integer(reasonLimit).toString());
                } 
            }
        }

        SpringServiceLocator.getPurchaseOrderService().updateFlagsAndRoute(po, PurapConstants.PurchaseOrderDocTypes.PURCHASE_ORDER_REOPEN_DOCUMENT, kualiDocumentFormBase.getAnnotation(), combineAdHocRecipients(kualiDocumentFormBase));
        GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_ROUTE_REOPENED);
        kualiDocumentFormBase.setAnnotation("");
        return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PODocumentsStrings.CONFIRM_REOPEN_QUESTION, kualiConfiguration.getPropertyString(PurapKeyConstants.MESSAGE_ROUTE_REOPENED), PurapConstants.PODocumentsStrings.SINGLE_CONFIRMATION_QUESTION, Constants.ROUTE_METHOD, "");
    }
    
    /**
     * 
     * This method is to check on a few conditions that would cause a warning message to be
     * displayed on top of the Purchase Order page.
     * 
     * @param po the PurchaseOrderDocument whose status and indicators are to be checked in the 
     *           conditions
     * 
     * @return boolean true if the Purchase Order doesn't have any warnings and false otherwise.
     */
    private void checkForPOWarnings(PurchaseOrderDocument po) {
        // "This is not the current version of this Purchase Order." (curr_ind = N and doc status is not enroute)
        if (!po.isPurchaseOrderCurrentIndicator() &&
            !po.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_NOT_CURRENT);
        }
        // "This document is a pending action. This is not the current version of this Purchase Order" (curr_ind = N and doc status is enroute)
        if (!po.isPurchaseOrderCurrentIndicator() &&
            po.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION_NOT_CURRENT);
        }
        // "There is a pending action on this Purchase Order." (pend_action = Y)
        if (po.isPendingActionIndicator()) {
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_PENDING_ACTION);
        }   
        if (!po.isPurchaseOrderCurrentIndicator()) {
            // Status History: "This includes the entire status history of the PO, not just up to this document" (curr_ind = N)
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_ENTIRE_STATUS_HISTORY);
            // Notes: "This includes all notes on the PO, not just up to this document" (curr_ind = N)
            GlobalVariables.getMessageList().add(PurapKeyConstants.WARNING_PURCHASE_ORDER_ALL_NOTES);
        }
        /* Uncomment the following if we decide to show the warnings on the note tab
        if (!po.isPurchaseOrderCurrentIndicator()) {
            ErrorMap errorMap = GlobalVariables.getErrorMap();
            errorMap.putError(Constants.DOCUMENT_NOTES_ERRORS, PurapKeyConstants.WARNING_PURCHASE_ORDER_ALL_NOTES);
        }
        */
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
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     * 
     * This method overrides the docHandler method in the superclass. In addition to doing the
     * normal process in the superclass and returning its action forward from the superclass, it
     * also invokes the checkForPOWarnings method to check on a few conditions that could have
     * caused warning messages to be displayed on top of Purchase Order page.
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);
        KualiDocumentFormBase kualiDocumentFormBase = (KualiDocumentFormBase) form;
        PurchaseOrderDocument po = (PurchaseOrderDocument)kualiDocumentFormBase.getDocument();
        checkForPOWarnings(po);
      
        return forward;
    }
}