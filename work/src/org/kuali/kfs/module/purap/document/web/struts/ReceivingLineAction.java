/*
 * Copyright 2008 The Kuali Foundation.
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
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapConstants.PREQDocumentsStrings;
import org.kuali.module.purap.bo.PurApItem;
import org.kuali.module.purap.bo.ReceivingLineItem;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.purap.document.ReceivingLineDocument;
import org.kuali.module.purap.rule.event.AddPurchasingAccountsPayableItemEvent;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.PurchaseOrderService;
import org.kuali.module.purap.service.ReceivingService;
import org.kuali.module.purap.service.impl.ReceivingServiceImpl;
import org.kuali.module.purap.web.struts.form.PaymentRequestForm;
import org.kuali.module.purap.web.struts.form.PurchaseOrderForm;
import org.kuali.module.purap.web.struts.form.PurchasingFormBase;
import org.kuali.module.purap.web.struts.form.ReceivingLineForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class ReceivingLineAction extends ReceivingBaseAction {

    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {       
        
        super.createDocument(kualiDocumentFormBase);

        ReceivingLineForm rlf = (ReceivingLineForm)kualiDocumentFormBase;
        ReceivingLineDocument rlDoc = (ReceivingLineDocument)rlf.getDocument();
        
        //set identifier from form value
        rlDoc.setPurchaseOrderIdentifier( rlf.getPurchaseOrderId() );
        
        rlDoc.initiateDocument();
        
    }

    public ActionForward continueReceivingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    
        ReceivingLineForm rlf = (ReceivingLineForm)form;
        ReceivingLineDocument rlDoc = (ReceivingLineDocument)rlf.getDocument();
        
        //perform duplicate check
        ActionForward forward = performDuplicateReceivingLineCheck(mapping, form, request, response, rlDoc);
        if( forward != null ){
            return forward;
        }
        
        //populate and save Receiving Line Document from Purchase Order        
        SpringContext.getBean(ReceivingService.class).populateAndSaveReceivingLineDocument(rlDoc);
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward createReceivingCorrection(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReceivingLineForm rlForm = (ReceivingLineForm) form;
        ReceivingLineDocument document = (ReceivingLineDocument) rlForm.getDocument();        
        
        String basePath = getBasePath(request);
        String methodToCallDocHandler = "docHandler";
        String methodToCallReceivingCorrection = "initiate";
                        
        //set parameters
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCallDocHandler);
        parameters.put(KFSConstants.PARAMETER_COMMAND, methodToCallReceivingCorrection);
        parameters.put(KFSConstants.DOCUMENT_TYPE_NAME, "ReceivingCorrectionDocument");        
        parameters.put("receivingLineDocId", document.getDocumentHeader().getDocumentNumber() );
        
        //create url
        String receivingCorrectionUrl = UrlFactory.parameterizeUrl(basePath + "/" + "purapReceivingCorrection.do", parameters);
        
        //create forward
        ActionForward forward = new ActionForward(receivingCorrectionUrl, true);
        
        return forward;
        
    }

    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ReceivingLineForm rlForm = (ReceivingLineForm) form;
        ReceivingLineDocument rlDocument = (ReceivingLineDocument) rlForm.getDocument();
        rlDocument.clearInitFields(rlForm.isFromPurchaseOrder());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    private ActionForward performDuplicateReceivingLineCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, ReceivingLineDocument receivingLineDocument) throws Exception {
        ActionForward forward = null;
        HashMap<String, String> duplicateMessages = SpringContext.getBean(ReceivingService.class).receivingLineDuplicateMessages(receivingLineDocument);
        if (duplicateMessages != null && !duplicateMessages.isEmpty()) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {

                return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.ReceivingLineDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION, duplicateMessages.get(PurapConstants.ReceivingLineDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION), KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.ReceivingLineDocumentStrings.DUPLICATE_RECEIVING_LINE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {                
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
        ReceivingLineForm receivingLineForm = (ReceivingLineForm) form;
        ReceivingLineItem item = receivingLineForm.getNewReceivingLineItemLine();
        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument) receivingLineForm.getDocument();
        boolean rulePassed = true; //SpringContext.getBean(KualiRuleService.class).applyRules(new AddPurchasingAccountsPayableItemEvent("", purDocument, item));

        if (rulePassed) {
            item = receivingLineForm.getAndResetNewReceivingItemLine();                       
            receivingLineDocument.addItem(item);                       
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
        ReceivingLineForm receivingLineForm = (ReceivingLineForm) form;

        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument) receivingLineForm.getDocument();
        
        for(ReceivingLineItem item : (List <ReceivingLineItem>)receivingLineDocument.getItems()){
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
        ReceivingLineForm receivingLineForm = (ReceivingLineForm) form;

        ReceivingLineDocument receivingLineDocument = (ReceivingLineDocument) receivingLineForm.getDocument();

        for(ReceivingLineItem item : (List <ReceivingLineItem>)receivingLineDocument.getItems()){            
            if( item.getItemOrderedQuantity().subtract(item.getItemReceivedPriorQuantity()).isGreaterEqual(KualiDecimal.ZERO)  ){
                item.setItemReceivedTotalQuantity( item.getItemOrderedQuantity().subtract(item.getItemReceivedPriorQuantity()) );
            }else{
                item.setItemReceivedTotalQuantity(KualiDecimal.ZERO);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
