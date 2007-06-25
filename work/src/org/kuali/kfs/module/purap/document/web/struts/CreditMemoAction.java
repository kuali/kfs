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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.rule.event.ContinueAccountsPayableEvent;
import org.kuali.module.purap.web.struts.form.CreditMemoForm;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class handles specific Actions requests for the Credit Memo document..
 */
public class CreditMemoAction extends AccountsPayableActionBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoAction.class);

    /**
     * Do initialization for a new credit memo
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        ((CreditMemoDocument) kualiDocumentFormBase.getDocument()).initiateDocument();
    }

    /**
     * Handles continue request. This request comes from the initial screen which gives indicates whether the type is preq, po, or
     * vendor. Based on that, the credit memo is initially populated and the remaining tabs shown.
     */
    public ActionForward continueCreditMemo(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CreditMemoForm preqForm = (CreditMemoForm) form;
        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) preqForm.getDocument();

        // preform duplicate check which will forward to a question prompt if one is found
        ActionForward forward = performDuplicateCreditMemoCheck(mapping, form, request, response, creditMemoDocument);
        if (forward != null) {
            return forward;
        }

        // perform validation of init tab
        boolean isValid = SpringServiceLocator.getKualiRuleService().applyRules(new ContinueAccountsPayableEvent(creditMemoDocument));
        if (!isValid) {
            creditMemoDocument.setStatusCode(PurapConstants.CreditMemoStatuses.INITIATE);
        }
        else {
            creditMemoDocument.setStatusCode(PurapConstants.CreditMemoStatuses.IN_PROCESS);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Clears out fields of the init tab.
     */
    public ActionForward clearInitFields(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CreditMemoForm preqForm = (CreditMemoForm) form;
        CreditMemoDocument creditMemoDocument = (CreditMemoDocument) preqForm.getDocument();
        creditMemoDocument.clearInitFields();

        return super.refresh(mapping, form, request, response);
    }

    /**
     * Calls CreditMemoService to perform the duplicate credit memo check. If one is found, a question is setup and control is forwarded to the question action 
     * method. Coming back from the question prompt the button that was clicked is checked and if 'no' was selected they are forward back to the page still in init mode.
     */
    private ActionForward performDuplicateCreditMemoCheck(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, CreditMemoDocument creditMemoDocument) throws Exception {
        ActionForward forward = null;
        String duplicateMessage = SpringServiceLocator.getCreditMemoService().creditMemoDuplicateMessages(creditMemoDocument);
        if (StringUtils.isNotBlank(duplicateMessage)) {
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                return this.performQuestionWithoutInput(mapping, form, request, response, PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION, duplicateMessage, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.ROUTE_METHOD, "");
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((PurapConstants.PREQDocumentsStrings.DUPLICATE_INVOICE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }
}