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
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.web.struts.form.CreditMemoForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Requisition.
 * 
 */
public class CreditMemoAction extends AccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoAction.class);
    
    /**
     * Do initialization for a new requisition
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        
        super.createDocument(kualiDocumentFormBase);
        
        ((CreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();

    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
 
        CreditMemoForm preqForm = (CreditMemoForm) form;
        CreditMemoDocument document = (CreditMemoDocument) preqForm.getDocument();
                
        return super.refresh(mapping, form, request, response);
    }
    
   
    public ActionForward continueCM(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("continueCM() method");

        CreditMemoForm preqForm = (CreditMemoForm) form;
        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) preqForm.getDocument();
        Map editMode = preqForm.getEditingMode();
        
        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        //String reason = request.getParameter(KFSConstants.QUESTION_REASON_ATTRIBUTE_NAME);

        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

  
        
         CreditMemoService creditMemoService = SpringServiceLocator.getCreditMemoService();
         HashMap<String, String> duplicateMessages = creditMemoService.creditMemoDuplicateMessages(creditMemoDocument);
        
       if (!duplicateMessages.isEmpty()){
  
            if (question == null) {
              // ask question if not already asked
              return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessages.get(PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION) , KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");

            } 
            
            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
           
            if ((PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                
                // if no button clicked just reload the doc in the INITIATE status and let the user to change the input values
               
                creditMemoDocument.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
                //editMode.put(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB, "TRUE");
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
             }
       }
        
        // If we are here either there was no duplicate or there was a duplicate and the user hits continue, in either case we need to validate the business rules
        creditMemoDocument.getDocumentHeader().setFinancialDocumentDescription("dummy data to pass the business rule");
       // boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new SaveDocumentEvent(creditMemoDocument));
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new ContinueAccountsPayableEvent(creditMemoDocument)); 
        creditMemoDocument.getDocumentHeader().setFinancialDocumentDescription(null);
        if (rulePassed) {
            
            Integer poId = creditMemoDocument.getPurchaseOrderIdentifier();
            PurchaseOrderDocument purchaseOrderDocument = SpringServiceLocator.getPurchaseOrderService().getCurrentPurchaseOrder(creditMemoDocument.getPurchaseOrderIdentifier());
            String vendorNbr = creditMemoDocument.getVendorNumber();
        
         ///   creditMemoDocument.populateCreditMemoVendorFileds(vendorNbr);
            creditMemoDocument.setStatusCode(PurapConstants.CreditMemoStatuses.IN_PROCESS);
            creditMemoDocument.refreshAllReferences();

            //editMode.put(PurapAuthorizationConstants.CreditMemoEditMode.DISPLAY_INIT_TAB, "FALSE");
            
        } else {
            creditMemoDocument.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
        }
       
        //If the list of closed/expired accounts is not empty add a warning and  add a note for the close / epired accounts which get replaced
      /*
        HashMap<String, String> expiredOrClosedAccounts = creditMemoService.ExpiredOrClosedAccountsList(creditMemoDocument);
 
        if (!expiredOrClosedAccounts.isEmpty()){
            GlobalVariables.getMessageList().add(PurapKeyConstants.MESSAGE_CLOSED_OR_EXPIRED_ACCOUNTS_REPLACED);
            creditMemoService.addContinuationAccountsNote(creditMemoDocument, expiredOrClosedAccounts);
        }
        
        */
        
        return super.refresh(mapping, form, request, response);
        //return mapping.findForward(KFSConstants.MAPPING_PORTAL);
  
    }
    
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("clearInitValues() method");

        CreditMemoForm preqForm = (CreditMemoForm) form;
        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) preqForm.getDocument();
        creditMemoDocument.clearInitFields();

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

        CreditMemoForm preqForm = (CreditMemoForm) form;
        CreditMemoDocument document = (CreditMemoDocument) preqForm.getDocument();
        
        SpringServiceLocator.getCreditMemoService().save(document);
        return super.save(mapping, form, request, response);
    }
    */
}