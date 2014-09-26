/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.question.ConfirmationQuestion;

/**
 * Action class for ContractsGrantsInvoiceDocument
 */
public class ContractsGrantsInvoiceDocumentAction extends CustomerInvoiceDocumentAction {


    /**
     * Recalculates the Total Expenditures in the Invoice Detail section and also the New Total Billed using the Invoice Object
     * Codes BO
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculateNewTotalBilled(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceDocumentForm contractsGrantsInvoiceDocumentForm = (ContractsGrantsInvoiceDocumentForm) form;
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocumentForm.getContractsGrantsInvoiceDocument();
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        contractsGrantsInvoiceDocumentService.recalculateNewTotalBilled(contractsGrantsInvoiceDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Recalculates the Total Expenditures in the Invoice Detail section due to reaching limit of the total award.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward prorateBill(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceDocumentForm contractsGrantsInvoiceDocumentForm = (ContractsGrantsInvoiceDocumentForm) form;
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocumentForm.getContractsGrantsInvoiceDocument();
        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        contractsGrantsInvoiceDocumentService.prorateBill(contractsGrantsInvoiceDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward approve(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = updateSuspensionCategoriesOnDocument(form);

        ActionForward forward = promptForSuspensionCategories(mapping, form, request, response, contractsGrantsInvoiceDocument, KFSConstants.APPROVE_METHOD);
        if (forward != null) {
            return forward;
        }

        return super.approve(mapping, form, request, response);
    }

    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#route(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = updateSuspensionCategoriesOnDocument(form);

        ActionForward forward = promptForSuspensionCategories(mapping, form, request, response, contractsGrantsInvoiceDocument, KFSConstants.ROUTE_METHOD);
        if (forward != null) {
            return forward;
        }

        return super.route(mapping, form, request, response);
    }


    /**
     * @see org.kuali.kfs.sys.web.struts.KualiAccountingDocumentActionBase#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        updateSuspensionCategoriesOnDocument(form);

        return super.save(mapping, form, request, response);
    }

    /**
     * This method gets the ContractsGrantsInvoiceDocument from the form and updates the suspension categories
     * on the document in case circumstances have changed and the suspension categories should be different than when
     * the document was created.
     *
     * @param form
     * @return ContractsGrantsInvoiceDocument
     */
    protected ContractsGrantsInvoiceDocument updateSuspensionCategoriesOnDocument(ActionForm form) {
        ContractsGrantsInvoiceDocumentForm contractsGrantsInvoiceDocumentForm = (ContractsGrantsInvoiceDocumentForm) form;
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocumentForm.getContractsGrantsInvoiceDocument();

        ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        contractsGrantsInvoiceDocumentService.updateSuspensionCategoriesOnDocument(contractsGrantsInvoiceDocument);

        return contractsGrantsInvoiceDocument;
    }

    /**
     * This method checks if there are suspension categories on the Contracts & Grants Invoice document, and if there are,
     * prompts the user to make sure they want to continue. If yes, this method returns null. If no, this method returns
     * the "basic" forward.
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @param contractsGrantsInvoiceDocument
     * @param caller
     * @return
     * @throws Exception
     */
    protected ActionForward promptForSuspensionCategories(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument, String caller) throws Exception {
        ActionForward forward = null;

        if(contractsGrantsInvoiceDocument.getInvoiceSuspensionCategories().size() > 0){
            Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
            if (question == null) {
                String questionText = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(ArKeyConstants.WARNING_SUSPENSION_CATEGORIES_PRESENT);
                return performQuestionWithoutInput(mapping, form, request, response, ArConstants.SUSPENSION_CATEGORIES_PRESENT_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, caller, StringUtils.EMPTY);
            }

            Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
            if (ArConstants.SUSPENSION_CATEGORIES_PRESENT_QUESTION.equals(question) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                forward = mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        return forward;
    }

}
