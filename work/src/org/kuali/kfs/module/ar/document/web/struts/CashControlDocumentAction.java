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
package org.kuali.module.ar.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiRuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.struts.action.KualiTransactionalDocumentActionBase;
import org.kuali.core.web.struts.form.KualiDocumentFormBase;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.rule.event.AddCashControlDetailEvent;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CashControlDocumentService;
import org.kuali.module.ar.web.struts.form.CashControlDocumentForm;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.lookup.valuefinder.ValueFinderUtil;
import org.kuali.rice.KNSServiceLocator;

import edu.iu.uis.eden.exception.WorkflowException;

public class CashControlDocumentAction extends KualiTransactionalDocumentActionBase {

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {

        super.loadDocument(kualiDocumentFormBase);
        CashControlDocumentForm ccForm = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument cashControlDocument = ccForm.getCashControlDocument();

        // get the PaymentApplicationDocuments by reference number
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {
            String docId = cashControlDetail.getReferenceFinancialDocumentNumber();
            PaymentApplicationDocument doc = null;
            doc = (PaymentApplicationDocument) KNSServiceLocator.getDocumentService().getByDocumentHeaderId(docId);
            if (doc == null) {
                throw new UnknownDocumentIdException("Document no longer exists.  It may have been cancelled before being saved.");
            }

            cashControlDetail.setReferenceFinancialDocument(doc);
            KualiWorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
            // KualiDocumentFormBase.populate() needs this updated in the session
            GlobalVariables.getUserSession().setWorkflowDocument(workflowDoc);
        }

    }

    /**
     * Adds handling for cash control detail amount updates.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.execute(mapping, form, request, response);
        CashControlDocumentForm ccForm = (CashControlDocumentForm) form;
        CashControlDocument ccDoc = ccForm.getCashControlDocument();

        if (ccDoc != null) {
            ccForm.setCashPaymentMediumSelected(ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(ccDoc.getCustomerPaymentMediumCode()));
        }

        if (ccForm.hasDocumentId()) {
            ccDoc = ccForm.getCashControlDocument();
            ccDoc.refreshReferenceObject("customerPaymentMedium");
            // recalc b/c changes to the amounts could have happened
            ccDoc.setCashControlTotalAmount(calculateCashControlTotal(ccDoc));
        }

        // proceed as usual
        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.core.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CashControlDocumentForm form = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument document = form.getCashControlDocument();

        // set up the default values for the AR DOC Header (SHOULD PROBABLY MAKE THIS A SERVICE)
        ChartUser currentUser = ValueFinderUtil.getCurrentChartUser();
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

    }

    /**
     * This method adds a new cash control detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return forward action
     * @throws Exception
     */
    public ActionForward addCashControlDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        CashControlDetail newCashControlDetail = cashControlDocForm.getNewCashControlDetail();
        newCashControlDetail.setDocumentNumber(cashControlDocument.getDocumentNumber());


        // apply rules for the new cash control detail
        boolean rulePassed = SpringContext.getBean(KualiRuleService.class).applyRules(new AddCashControlDetailEvent(ArConstants.NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX, cashControlDocument, newCashControlDetail));

        // add the new detail if rules passed
        if (rulePassed) {

            CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);

            // add cash control detail
            cashControlDocumentService.addNewCashControlDetail(kualiConfiguration.getPropertyString(ArConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDocument, newCashControlDetail);

            // set a new blank cash control detail
            cashControlDocForm.setNewCashControlDetail(new CashControlDetail());

        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * This method deletes a cash control detail
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward deleteCashControlDetail(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();

        int indexOfLineToDelete = getLineToDelete(request);
        CashControlDetail cashControlDetail = cashControlDocument.getCashControlDetail(indexOfLineToDelete);
        DocumentService documentService = KNSServiceLocator.getDocumentService();

        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());
        documentService.cancelDocument(applicationDocument, ArConstants.DOCUMENT_DELETED_FROM_CASH_CTRL_DOC);
        cashControlDocument.deleteCashControlDetail(indexOfLineToDelete);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method generates the GLPEs.
     * 
     * @param mapping action mapping
     * @param form action form
     * @param request
     * @param response
     * @return action forward
     * @throws Exception
     */
    public ActionForward generateGLPEs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();
        String paymentMediumCode = cashControlDocument.getCustomerPaymentMediumCode();

        // refreshh reference objects
        cashControlDocument.refreshReferenceObject("customerPaymentMedium");
        cashControlDocument.refreshReferenceObject("generalLedgerPendingEntries");

        // payment medium might have been changed meanwhile so we save first the document
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(cashControlDocument);

        // generate the GLPEs
        GeneralLedgerPendingEntryService generalLedgerPendingEntryService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        boolean success = generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(cashControlDocument);

        if (!success) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArConstants.ERROR_GLPES_NOT_CREATED);
        }
        // approve the GLPEs
        cashControlDocument.changeGeneralLedgerPendingEntriesApprovedStatusCode();

        // save the GLPEs in the database
        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        cashControlDocumentService.saveGLPEs(cashControlDocument);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Recalculates the cash control total since user could have changed it during their update.
     * 
     * @param cashControlDocument
     */
    private KualiDecimal calculateCashControlTotal(CashControlDocument cashControlDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {

            total = total.add(cashControlDetail.getFinancialDocumentLineAmount());
        }
        return total;
    }

}
