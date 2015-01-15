/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.DocumentStatusCategory;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Action class for ContractsGrantsInvoiceDocument
 */
public class ContractsGrantsInvoiceDocumentAction extends CustomerInvoiceDocumentAction {
    protected static volatile ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected static volatile FinancialSystemDocumentService financialSystemDocumentService;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        final ContractsGrantsInvoiceDocumentForm cinvForm = (ContractsGrantsInvoiceDocumentForm)form;
        if (!ObjectUtils.isNull(cinvForm.getContractsGrantsInvoiceDocument())) {
            final ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = cinvForm.getContractsGrantsInvoiceDocument();
            if (getFinancialSystemDocumentService().getPendingDocumentStatuses().contains(contractsGrantsInvoiceDocument.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode())) {
                getContractsGrantsInvoiceDocumentService().recalculateSourceAccountingLineTotals(contractsGrantsInvoiceDocument);
            }
        }
        return super.execute(mapping, form, request, response);
    }

    /**
     * Overridden to recheck the suspension categories when the document is opened
     * @see org.kuali.kfs.module.ar.document.web.struts.CustomerInvoiceDocumentAction#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);
        ContractsGrantsInvoiceDocumentForm cgInvoiceForm = (ContractsGrantsInvoiceDocumentForm)kualiDocumentFormBase;
        final ContractsGrantsInvoiceDocument cgInvoice = cgInvoiceForm.getContractsGrantsInvoiceDocument();
        if (shouldUpdateSuspensionCategoriesAndRecalculateNewTotalBilled(cgInvoice)) {
            if (!StringUtils.equalsIgnoreCase(cgInvoice.getInvoiceGeneralDetail().getBillingFrequencyCode(), ArConstants.MILESTONE_BILLING_SCHEDULE_CODE) && !StringUtils.equalsIgnoreCase(cgInvoice.getInvoiceGeneralDetail().getBillingFrequencyCode(), ArConstants.PREDETERMINED_BILLING_SCHEDULE_CODE)) {
                ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
                contractsGrantsInvoiceDocumentService.recalculateNewTotalBilled(cgInvoice);
            }
            updateSuspensionCategoriesOnDocument(cgInvoiceForm); // in memory CINV has had the suspension categories updated
        }
    }

    /**
     * Determines if the given c&g invoice should have its suspension categories updated and new total billed
     * recalculated or not
     * @param cgInvoice the invoice to determine the suspension category updatability of
     * @return true if suspension categories should be updated and new total bill should be recalculated, false otherwise
     */
    protected boolean shouldUpdateSuspensionCategoriesAndRecalculateNewTotalBilled(ContractsGrantsInvoiceDocument cgInvoice) {
        final DocumentStatus documentStatus = DocumentStatus.fromCode(cgInvoice.getFinancialSystemDocumentHeader().getWorkflowDocumentStatusCode());
        return documentStatus.getCategory() != DocumentStatusCategory.SUCCESSFUL && documentStatus.getCategory() != DocumentStatusCategory.UNSUCCESSFUL && documentStatus != DocumentStatus.EXCEPTION;
    }

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
     * Clears the initial transmission date for the corresponding invoice transmission detail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward clearInitialTransmissionDate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int index = getSelectedLine(request);

        ContractsGrantsInvoiceDocumentForm contractsGrantsInvoiceDocumentForm = (ContractsGrantsInvoiceDocumentForm) form;
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocumentForm.getContractsGrantsInvoiceDocument();

        contractsGrantsInvoiceDocument.getInvoiceAddressDetails().get(index).setInitialTransmissionDate(null);
        SpringContext.getBean(BusinessObjectService.class).save(contractsGrantsInvoiceDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * Sets the initial transmission date for the corresponding invoice transmission detail
     *
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward setInitialTransmissionDate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        int index = getSelectedLine(request);

        ContractsGrantsInvoiceDocumentForm contractsGrantsInvoiceDocumentForm = (ContractsGrantsInvoiceDocumentForm) form;
        ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument = contractsGrantsInvoiceDocumentForm.getContractsGrantsInvoiceDocument();

        contractsGrantsInvoiceDocument.getInvoiceAddressDetails().get(index).setInitialTransmissionDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
        SpringContext.getBean(BusinessObjectService.class).save(contractsGrantsInvoiceDocument);

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

    public static ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        if (contractsGrantsInvoiceDocumentService == null) {
            contractsGrantsInvoiceDocumentService = SpringContext.getBean(ContractsGrantsInvoiceDocumentService.class);
        }
        return contractsGrantsInvoiceDocumentService;
    }

    public static FinancialSystemDocumentService getFinancialSystemDocumentService() {
        if (financialSystemDocumentService == null) {
            financialSystemDocumentService = SpringContext.getBean(FinancialSystemDocumentService.class);
        }
        return financialSystemDocumentService;
    }

}
