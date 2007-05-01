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

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapAuthorizationConstants;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.question.SingleConfirmationQuestion;
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
        //String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

     
        
        PaymentRequestService paymentRequestService = SpringServiceLocator.getPaymentRequestService();
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
        
        // If we are here either there was no duplicate or there was a duplicate and the user hits continue, in either case we need to validate the business rules
        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription("dummy data to pass the business rule");
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new SaveDocumentEvent(paymentRequestDocument)); 
        paymentRequestDocument.getDocumentHeader().setFinancialDocumentDescription(null);
        if (rulePassed) {
            
            Integer poId = paymentRequestDocument.getPurchaseOrderIdentifier();
            PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(paymentRequestDocument.getPurchaseOrderIdentifier());
            paymentRequestDocument.populatePaymentRequestFromPurchaseOrder(purchaseOrderDocument);
            paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.IN_PROCESS);
            paymentRequestDocument.refreshAllReferences();

            //editMode.put(PurapAuthorizationConstants.PaymentRequestEditMode.DISPLAY_INIT_TAB, "FALSE");
            
        } else {
            paymentRequestDocument.setStatusCode(PurapConstants.PaymentRequestStatuses.INITIATE);
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