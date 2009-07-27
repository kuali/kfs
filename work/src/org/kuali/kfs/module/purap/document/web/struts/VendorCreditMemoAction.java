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
package org.kuali.kfs.module.purap.document.web.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapConstants.CMDocumentsStrings;
import org.kuali.kfs.module.purap.document.AccountsPayableDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.document.validation.event.AttributedCalculateAccountsPayableEvent;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.util.PurQuestionCallback;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;

/**
 * Struts Action for Credit Memo document.
 */
public class VendorCreditMemoAction extends AccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(VendorCreditMemoAction.class);

    /**
     * Do initialization for a new credit memo.
     * 
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((VendorCreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }

    /**
     * Handles continue request. This request comes from the initial screen which gives indicates whether the type is payment
     * request, purchase order, or vendor. Based on that, the credit memo is initially populated and the remaining tabs shown.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        VendorCreditMemoForm cmForm = (VendorCreditMemoForm) form;
        VendorCreditMemoDocument creditMemoDocument = (VendorCreditMemoDocument) cmForm.getDocument();
        
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedContinuePurapEvent(creditMemoDocument));
        if (!rulePassed){
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        if (creditMemoDocument.isSourceDocumentPaymentRequest()) {
            PaymentRequestDocument preq = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestById(creditMemoDocument.getPaymentRequestIdentifier());
            if (ObjectUtils.isNotNull(preq)) {
                // TODO figure out a more straightforward way to do this.  ailish put this in so the link id would be set and the perm check would work
                creditMemoDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(preq.getAccountsPayablePurchasingDocumentLinkIdentifier());
                
                if (!SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(creditMemoDocument).isAuthorizedByTemplate(creditMemoDocument, KNSConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, GlobalVariables.getUserSession().getPrincipalId())) {
                    throw buildAuthorizationException("initiate document", creditMemoDocument);
                }
            }
        }
        else if (creditMemoDocument.isSourceDocumentPurchaseOrder()) {
            PurchaseOrderDocument po = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(creditMemoDocument.getPurchaseOrderIdentifier());
            if (ObjectUtils.isNotNull(po)) {
                // TODO figure out a more straightforward way to do this.  ailish put this in so the link id would be set and the perm check would work
                creditMemoDocument.setAccountsPayablePurchasingDocumentLinkIdentifier(po.getAccountsPayablePurchasingDocumentLinkIdentifier());
                
                if (!SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(creditMemoDocument).isAuthorizedByTemplate(creditMemoDocument, KNSConstants.KNS_NAMESPACE, KimConstants.PermissionTemplateNames.OPEN_DOCUMENT, GlobalVariables.getUserSession().getPrincipalId())) {
                    throw buildAuthorizationException("initiate document", creditMemoDocument);
                }
            }
        }
        else {
            //do nothing for credit memos against a vendor; no link to PO means no need to hide doc based on sensitive data
        }


        // preform duplicate check which will forward to a question prompt if one is found
        ActionForward forward = performDuplicateCreditMemoCheck(mapping, form, request, response, creditMemoDocument);
        if (forward != null) {
            return forward;
        }

        // perform validation of init tab
        SpringContext.getBean(CreditMemoService.class).populateAndSaveCreditMemo(creditMemoDocument);

        // sort below the line (doesn't really need to be done on CM, but will help if we ever bring btl from other docs)
        SpringContext.getBean(PurapService.class).sortBelowTheLine(creditMemoDocument);

        // update the counts on the form
        cmForm.updateItemCounts();

        //if source is (PREQ or PO) and the PO status is CLOSED, automatically reopen the PO
        PurchaseOrderDocument po = creditMemoDocument.getPurchaseOrderDocument();
        if ((creditMemoDocument.isSourceDocumentPaymentRequest() || creditMemoDocument.isSourceDocumentPurchaseOrder()) && PurapConstants.PurchaseOrderStatuses.CLOSED.equals(po.getStatusCode())) {
            initiateReopenPurchaseOrder(po, cmForm.getAnnotation());
        }
        
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears out fields of the init tab.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        VendorCreditMemoForm cmForm = (VendorCreditMemoForm) form;
        VendorCreditMemoDocument creditMemoDocument = (VendorCreditMemoDocument) cmForm.getDocument();
        creditMemoDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Calls <code>CreditMemoService</code> to perform the duplicate credit memo check. If one is found, a question is setup and
     * control is forwarded to the question action method. Coming back from the question prompt, the button that was clicked is
     * checked, and if 'no' was selected, they are forward back to the page still in init mode.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @param creditMemoDocument The CreditMemoDocument
     * @throws Exception
     * @return An ActionForward
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService
     */
    private ActionForward performDuplicateCreditMemoCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, VendorCreditMemoDocument creditMemoDocument) throws Exception {
        ActionForward forward = null;
        String duplicateMessage = SpringContext.getBean(CreditMemoService.class).creditMemoDuplicateMessages(creditMemoDocument);
        if (StringUtils.isNotBlank(duplicateMessage)) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {

                return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessage, KFSConstants.CONFIRMATION_QUESTION, "continueCreditMemo", "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

    /**
     * Calls methods to perform credit allowed calculation and total credit memo amount.
     * 
     * @param apDoc An AccountsPayableDocument
     */
    @Override
    protected void customCalculate(PurchasingAccountsPayableDocument apDoc) {
        VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) apDoc;

        // call service method to finish up calculation
        SpringContext.getBean(CreditMemoService.class).calculateCreditMemo(cmDocument);
        
        // notice we're ignoring the boolean because these are just warnings they shouldn't halt anything
        SpringContext.getBean(KualiRuleService.class).applyRules(new AttributedCalculateAccountsPayableEvent(cmDocument));
        // }
    }

    /**
     * Puts a credit memo on hold, prompting for a reason before hand. This stops further approvals or routing.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward addHoldOnCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Hold ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                VendorCreditMemoDocument cmDocument = SpringContext.getBean(CreditMemoService.class).addHoldOnCreditMemo((VendorCreditMemoDocument) document, noteText);
                return cmDocument;
            }
        };

        return askQuestionWithInput(mapping, form, request, response, CMDocumentsStrings.HOLD_CM_QUESTION, operation, CMDocumentsStrings.HOLD_NOTE_PREFIX, PurapKeyConstants.CREDIT_MEMO_QUESTION_HOLD_DOCUMENT, callback);
    }

    /**
     * Removes a hold on the credit memo.
     * 
     * @param mapping An ActionMapping
     * @param form An ActionForm
     * @param request The HttpServletRequest
     * @param response The HttpServletResponse
     * @throws Exception
     * @return An ActionForward
     */
    public ActionForward removeHoldFromCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String operation = "Remove Hold ";

        PurQuestionCallback callback = new PurQuestionCallback() {
            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
                VendorCreditMemoDocument cmDocument = SpringContext.getBean(CreditMemoService.class).removeHoldOnCreditMemo((VendorCreditMemoDocument) document, noteText);
                return cmDocument;
            }
        };

        return askQuestionWithInput(mapping, form, request, response, CMDocumentsStrings.REMOVE_HOLD_CM_QUESTION, operation, CMDocumentsStrings.REMOVE_HOLD_NOTE_PREFIX, PurapKeyConstants.CREDIT_MEMO_QUESTION_REMOVE_HOLD_DOCUMENT, callback);
    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.AccountsPayableActionBase#cancelPOActionCallbackMethod()
     */
//    @Override
//    protected PurQuestionCallback cancelPOActionCallbackMethod() {
//        return new PurQuestionCallback() {
//            public AccountsPayableDocument doPostQuestion(AccountsPayableDocument document, String noteText) throws Exception {
//                VendorCreditMemoDocument cmDocument = (VendorCreditMemoDocument) document;
//                cmDocument.setClosePurchaseOrderIndicator(true);
//                return cmDocument;
//            }
//        };
//    }

    /**
     * @see org.kuali.kfs.module.purap.document.web.struts.AccountsPayableActionBase#getActionName()
     */
    @Override
    public String getActionName() {
        return PurapConstants.CREDIT_MEMO_ACTION_NAME;
    }
    
    @Override
    protected void populateAdHocActionRequestCodes(KualiDocumentFormBase formBase){
        Document document = formBase.getDocument();
        DocumentAuthorizer documentAuthorizer = getDocumentHelperService().getDocumentAuthorizer(document);
        Map<String,String> adHocActionRequestCodes = new HashMap<String,String>();

        if (documentAuthorizer.canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_FYI_REQ, GlobalVariables.getUserSession().getPerson())) {
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_FYI_REQ, KEWConstants.ACTION_REQUEST_FYI_REQ_LABEL);
        }
        if ( (document.getDocumentHeader().getWorkflowDocument().stateIsInitiated()
              || document.getDocumentHeader().getWorkflowDocument().stateIsSaved()
              || document.getDocumentHeader().getWorkflowDocument().stateIsEnroute()
              )&& documentAuthorizer.canSendAdHocRequests(document, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, GlobalVariables.getUserSession().getPerson())) {
                adHocActionRequestCodes.put(KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ, KEWConstants.ACTION_REQUEST_ACKNOWLEDGE_REQ_LABEL);
        } 
        formBase.setAdHocActionRequestCodes(adHocActionRequestCodes);

    }
}
