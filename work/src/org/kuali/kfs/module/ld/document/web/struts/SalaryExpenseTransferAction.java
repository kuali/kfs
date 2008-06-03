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
package org.kuali.module.labor.web.struts.action;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMessage;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborKeyConstants;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.document.SalaryExpenseTransferDocument;
import org.kuali.module.labor.service.SalaryTransferPeriodValidationService;
import org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase;
import org.kuali.module.labor.web.struts.form.SalaryExpenseTransferForm;

/**
 * Struts action class for Salary Expense Transfer Document. This class extends the parent KualiTransactionalDocumentActionBase
 * class, which contains all common action methods. Since the SEP follows the basic transactional document pattern, there are no
 * specific actions that it has to implement; however, this empty class is necessary for integrating into the framework.
 */
public class SalaryExpenseTransferAction extends ExpenseTransferDocumentActionBase {
    /**
     * Resets lookup fields for salary expense transfer action
     * 
     * @see org.kuali.module.labor.web.struts.action.ExpenseTransferDocumentActionBase#resetLookupFields(org.kuali.module.labor.web.struts.form.ExpenseTransferDocumentFormBase,
     *      org.kuali.module.labor.bo.LedgerBalance)
     */
    @Override
    protected void resetLookupFields(ExpenseTransferDocumentFormBase expenseTransferDocumentForm, LedgerBalance balance) {
        SalaryExpenseTransferForm benefitExpenseTransferForm = (SalaryExpenseTransferForm) expenseTransferDocumentForm;
        ObjectUtil.buildObject(benefitExpenseTransferForm, balance);
    }

    /**
     * If user is approving document, capture the object code balances for comparison in business rules on route
     * 
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#docHandler(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.docHandler(mapping, form, request, response);

        SalaryExpenseTransferDocument salaryExpenseDocument = (SalaryExpenseTransferDocument) ((KualiDocumentFormBase) form).getDocument();
        if (salaryExpenseDocument.getDocumentHeader().getWorkflowDocument().isApprovalRequested()) {
            salaryExpenseDocument.setApprovalObjectCodeBalances(salaryExpenseDocument.getUnbalancedObjectCodes());
        }

        return forward;
    }

    /**
     * @see org.kuali.kfs.web.struts.action.KualiAccountingDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = doEffortCertificationValidation(mapping, form, request, response, KFSConstants.ROUTE_METHOD);
        if (forward != null) {
            return forward;
        }

        return super.route(mapping, form, request, response);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#approve(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = doEffortCertificationValidation(mapping, form, request, response, KFSConstants.APPROVE_METHOD);
        if (forward != null) {
            return forward;
        }

        return super.approve(mapping, form, request, response);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#blanketApprove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward blanketApprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = doEffortCertificationValidation(mapping, form, request, response, KFSConstants.BLANKET_APPROVE_METHOD);
        if (forward != null) {
            return forward;
        }

        return super.blanketApprove(mapping, form, request, response);
    }

    /**
     * Calls service to verify the salary transfer does not conflict with effort certifications and handle any errors returned.
     * 
     * @return ActionForward which is null if everything was OK, the question redirect if errors found and user is admin, the basic
     *         mapping (which goes back to document) if errors were found and the user is not admin and document is not enroute, or
     *         finally redirect back to portal if document was disapproved due to errors
     */
    protected ActionForward doEffortCertificationValidation(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String caller) throws Exception {
        SalaryExpenseTransferDocument salaryExpenseDocument = (SalaryExpenseTransferDocument) ((KualiDocumentFormBase) form).getDocument();

        // check sys parameter indicating if we should check effort certification rules
        boolean doEffortValidation = SpringContext.getBean(ParameterService.class).getIndicatorParameter(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.VALIDATE_AGAINST_EFFORT_PARM_NM);
        if (!doEffortValidation) {
            return null;
        }

        // check if we are returning from a question, in which case we previously check effort and asked admin to confirm. If not,
        // we need to perform validation.
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (question == null) { // question hasn't been asked
            boolean transferValid = SpringContext.getBean(SalaryTransferPeriodValidationService.class).validateTransfers(salaryExpenseDocument);
            if (!transferValid) {
                return handleEffortValidationErrors(mapping, form, request, response, caller, false);
            }
        }
        else {
            // check if admin wants to continue, or not in which case we return to document or cancel document (in enroute)
            String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if ((LaborConstants.SalaryExpenseTransfer.EFFORT_VALIDATION_OVERRIDE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                return handleEffortValidationErrors(mapping, form, request, response, caller, true);
            }
        }

        return null;
    }

    /**
     * If the user is an effort administrator ask if they want to override the errors (if not already asked). Otherwise if the
     * document is enroute it will be disapproved or if being initiated just return to doc.
     * 
     * @return ActionForward which is question redirect, portal redirect, or basic mapping (back to doc)
     * @throws Exception
     */
    protected ActionForward handleEffortValidationErrors(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, String caller, boolean questionAsked) throws Exception {
        SalaryExpenseTransferDocument salaryExpenseDocument = (SalaryExpenseTransferDocument) ((KualiDocumentFormBase) form).getDocument();

        String adminGroupName = SpringContext.getBean(ParameterService.class).getParameterValue(SalaryExpenseTransferDocument.class, LaborConstants.SalaryExpenseTransfer.EFFORT_ADMIN_WORKGROUP_PARM_NM);
        boolean isAdmin = GlobalVariables.getUserSession().getUniversalUser().isMember(adminGroupName);

        if (isAdmin && !questionAsked) {
            // error found, ask admin user if they want to override
            KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);

            // build question text (contains error messages found)
            String message = "";
            for (Object errorMessage : GlobalVariables.getErrorMap().getMessages(KFSPropertyConstants.TARGET_ACCOUNTING_LINES)) {
                message += kualiConfigurationService.getPropertyString(((ErrorMessage) errorMessage).getErrorKey());
            }
            message += " " + kualiConfigurationService.getPropertyString(LaborKeyConstants.EFFORT_VALIDATION_OVERRIDE_MESSAGE);

            return this.performQuestionWithoutInput(mapping, form, request, response, LaborConstants.SalaryExpenseTransfer.EFFORT_VALIDATION_OVERRIDE_QUESTION, message, KFSConstants.CONFIRMATION_QUESTION, caller, "");
        }

        // errors found, return to document if it is being initiated, or disapproved if enroute
        if (salaryExpenseDocument.getDocumentHeader().getWorkflowDocument().stateIsEnroute()) {
            SpringContext.getBean(SalaryTransferPeriodValidationService.class).disapproveSalaryExpenseDocument(salaryExpenseDocument);

            return returnToSender(mapping, (KualiDocumentFormBase) form);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

}
