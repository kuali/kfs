/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapConstants.CorrectionReceivingDocumentStrings;
import org.kuali.kfs.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.LineItemReceivingItem;
import org.kuali.kfs.module.purap.document.LineItemReceivingDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.ReceivingDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.service.ReceivingService;
import org.kuali.kfs.module.purap.document.validation.event.AddReceivingItemEvent;
import org.kuali.kfs.module.purap.util.ReceivingQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
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
import org.kuali.rice.krad.util.UrlFactory;

public class LineItemReceivingAction extends ReceivingBaseAction {

    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {

        super.createDocument(kualiDocumentFormBase);

        LineItemReceivingForm rlf = (LineItemReceivingForm)kualiDocumentFormBase;
        LineItemReceivingDocument rlDoc = (LineItemReceivingDocument)rlf.getDocument();

        //set identifier from form value
        rlDoc.setPurchaseOrderIdentifier( rlf.getPurchaseOrderId() );

        rlDoc.initiateDocument();

    }

    public ActionForward continueReceivingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm rlf = (LineItemReceivingForm)form;
        LineItemReceivingDocument rlDoc = (LineItemReceivingDocument)rlf.getDocument();

        GlobalVariables.getMessageMap().clearErrorPath();
        GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT);
        boolean valid = true;
        boolean poNotNull = true;

        //check for a po id
        if (ObjectUtils.isNull(rlDoc.getPurchaseOrderIdentifier())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, KFSKeyConstants.ERROR_REQUIRED, PREQDocumentsStrings.PURCHASE_ORDER_ID);
            poNotNull = false;
        }

        if (ObjectUtils.isNull(rlDoc.getShipmentReceivedDate())) {
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.SHIPMENT_RECEIVED_DATE, KFSKeyConstants.ERROR_REQUIRED, PurapConstants.LineItemReceivingDocumentStrings.VENDOR_DATE);
        }

        //exit early as the po is null, no need to proceed further until this is taken care of
        if(poNotNull == false){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(rlDoc.getPurchaseOrderIdentifier());
        if (ObjectUtils.isNotNull(po)) {
            // TODO figure out a more straightforward way to do this.  ailish put this in so the link id would be set and the perm check would work
            rlDoc.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());

            //TODO hjs-check to see if user is allowed to initiate doc based on PO sensitive data (add this to all other docs except acm doc)
            if (!SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(rlDoc).isAuthorizedByTemplate(rlDoc, KRADConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, GlobalVariables.getUserSession().getPrincipalId())) {
                throw buildAuthorizationException("initiate document", rlDoc);
            }
        }else{
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_PURCHASE_ORDER_NOT_EXIST);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        //perform duplicate check
        ActionForward forward = performDuplicateReceivingLineCheck(mapping, form, request, response, rlDoc);
        if( forward != null ){
            return forward;
        }

        if (!SpringContext.getBean(ReceivingService.class).isPurchaseOrderActiveForLineItemReceivingDocumentCreation(rlDoc.getPurchaseOrderIdentifier())){
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_RECEIVING_LINE_DOCUMENT_PO_NOT_ACTIVE, rlDoc.getPurchaseOrderIdentifier().toString());
            valid &= false;
        }

        if( SpringContext.getBean(ReceivingService.class).canCreateLineItemReceivingDocument(rlDoc.getPurchaseOrderIdentifier(), rlDoc.getDocumentNumber()) == false){
            String inProcessDocNum = "";
            List<String> inProcessDocNumbers = SpringContext.getBean(ReceivingService.class).getLineItemReceivingDocumentNumbersInProcessForPurchaseOrder(rlDoc.getPurchaseOrderIdentifier(), rlDoc.getDocumentNumber());
            if (!inProcessDocNumbers.isEmpty()) {    // should not be empty if we reach this point
                inProcessDocNum = inProcessDocNumbers.get(0);
            }
            GlobalVariables.getMessageMap().putError(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, PurapKeyConstants.ERROR_RECEIVING_LINE_DOCUMENT_ACTIVE_FOR_PO, inProcessDocNum, rlDoc.getPurchaseOrderIdentifier().toString());
            valid &= false;
        }

        //populate and save Receiving Line Document from Purchase Order, only if we passed all the rules
        if(valid){
            SpringContext.getBean(ReceivingService.class).populateAndSaveLineItemReceivingDocument(rlDoc);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward createReceivingCorrection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm rlForm = (LineItemReceivingForm) form;
        LineItemReceivingDocument document = (LineItemReceivingDocument) rlForm.getDocument();

        String operation = "AddCorrectionNote ";

        ReceivingQuestionCallback callback = new ReceivingQuestionCallback() {
            public boolean questionComplete = false;
            protected String correctionDocumentnoteText;

            @Override
            public ReceivingDocument doPostQuestion(ReceivingDocument document, String noteText) throws Exception {
                //mark question completed
                this.setQuestionComplete(true);
                this.setCorrectionDocumentCreationNoteText(noteText);
                return document;
            }

            @Override
            public boolean isQuestionComplete(){
                return this.questionComplete;
            }

            @Override
            public void setQuestionComplete(boolean questionComplete){
                this.questionComplete = questionComplete;
            }

            @Override
            public String getCorrectionDocumentCreationNoteText() {
                return correctionDocumentnoteText;
            }

            @Override
            public void setCorrectionDocumentCreationNoteText(String noteText) {
                correctionDocumentnoteText = noteText;
            }
        };

        //ask question
        ActionForward forward = askQuestionWithInput(mapping, form, request, response, CorrectionReceivingDocumentStrings.NOTE_QUESTION, CorrectionReceivingDocumentStrings.NOTE_PREFIX, operation, PurapKeyConstants.MESSAGE_RECEIVING_CORRECTION_NOTE, callback);

        //if question asked is complete, then route
        if(callback.isQuestionComplete()){

            //set parameters
            String basePath = getApplicationBaseUrl();
            String methodToCallDocHandler = "docHandler";
            String methodToCallReceivingCorrection = "initiate";

            Properties parameters = new Properties();
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCallDocHandler);
            parameters.put(KFSConstants.PARAMETER_COMMAND, methodToCallReceivingCorrection);
            parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, "RCVC");
            parameters.put("receivingLineDocId", document.getDocumentHeader().getDocumentNumber() );
            parameters.put(PurapConstants.CorrectionReceivingDocumentStrings.CORRECTION_RECEIVING_CREATION_NOTE_PARAMETER, callback.getCorrectionDocumentCreationNoteText());

            //create url
            String receivingCorrectionUrl = UrlFactory.parameterizeUrl(basePath + "/" + "purapCorrectionReceiving.do", parameters);
            //create forward
            forward = new ActionForward(receivingCorrectionUrl, true);
        }

        return forward;

    }

    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm rlForm = (LineItemReceivingForm) form;
        LineItemReceivingDocument rlDocument = (LineItemReceivingDocument) rlForm.getDocument();
        rlDocument.clearInitFields(rlForm.isFromPurchaseOrder());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    protected ActionForward performDuplicateReceivingLineCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, LineItemReceivingDocument lineItemReceivingDocument) throws Exception {
        ActionForward forward = null;
        HashMap<String, String> duplicateMessages = SpringContext.getBean(ReceivingService.class).receivingLineDuplicateMessages(lineItemReceivingDocument);
        if (duplicateMessages != null && !duplicateMessages.isEmpty()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {

                return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.LineItemReceivingDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION, duplicateMessages.get(PurapConstants.LineItemReceivingDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.LineItemReceivingDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }
    /**
     * Add a new item to the document.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward addItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm lineItemReceivingForm = (LineItemReceivingForm) form;
        LineItemReceivingItem item = lineItemReceivingForm.getNewLineItemReceivingItemLine();
        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument) lineItemReceivingForm.getDocument();

        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddReceivingItemEvent(PurapPropertyConstants.NEW_LINE_ITEM_RECEIVING_ITEM_LINE, lineItemReceivingDocument, item));
        if (rulePassed) {
            lineItemReceivingForm.setHideAddUnorderedItem(true); // hide the add unordered item line once an item is added
            item = lineItemReceivingForm.getAndResetNewReceivingItemLine();
            lineItemReceivingDocument.addItem(item);
            //TODO: we need to set the line number correctly to match up to PO
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * For each item, it's quantity received value is set to zero.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clearQty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm lineItemReceivingForm = (LineItemReceivingForm) form;

        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument) lineItemReceivingForm.getDocument();

        for(LineItemReceivingItem item : (List <LineItemReceivingItem>)lineItemReceivingDocument.getItems()){
            item.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * For each item, loads total order quantity minus prior received quantity into total received quantity.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward loadQty(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm lineItemReceivingForm = (LineItemReceivingForm) form;

        LineItemReceivingDocument lineItemReceivingDocument = (LineItemReceivingDocument) lineItemReceivingForm.getDocument();

        for(LineItemReceivingItem item : (List <LineItemReceivingItem>)lineItemReceivingDocument.getItems()){
            if (item.isOrderedItem()){
                if( item.getItemOrderedQuantity().subtract(item.getItemReceivedPriorQuantity()).isGreaterEqual(KualiDecimal.ZERO)  ){
                    item.setItemReceivedTotalQuantity( item.getItemOrderedQuantity().subtract(item.getItemReceivedPriorQuantity()) );
                }else{
                    item.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
                }
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Gives a warning before showing the add new unordered line item; if the user confirms the action, proceeds;
     * otherwise cancels the action and returns to the current LineItemReceivingDocument.
     *
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward showAddUnorderedItem(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LineItemReceivingForm lineItemReceivingForm = (LineItemReceivingForm)form;

        boolean shouldGiveWarning = lineItemReceivingForm.shouldGiveAddUnorderedItemWarning();
        if (!shouldGiveWarning) {
            lineItemReceivingForm.setHideAddUnorderedItem(false);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        String msgkey = PurapKeyConstants.WARNING_RECEIVING_LINEITEM_ADD_UNORDERED;
        String msgtxt = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(msgkey);
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);

        if (question == null) {
            return performQuestionWithoutInput(mapping, form, request, response, msgkey, msgtxt, KFSConstants.CONFIRMATION_QUESTION, "showAddUnorderedItem", "");
        }

        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if ((msgkey.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
            lineItemReceivingForm.setHideAddUnorderedItem(false);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
