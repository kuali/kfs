/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.web.struts;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.AddCashControlDetailEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.exception.UnknownDocumentIdException;
import org.kuali.rice.krad.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kew.api.WorkflowDocument;

public class CashControlDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {

        super.loadDocument(kualiDocumentFormBase);
        CashControlDocumentForm ccForm = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument cashControlDocument = ccForm.getCashControlDocument();

        // now that the form has been originally loaded, we need to set a few Form variables that are used by
        // JSP JSTL expressions because they are used directly and immediately upon initial form display
        if (cashControlDocument != null && cashControlDocument.getCustomerPaymentMediumCode() != null) {
            ccForm.setCashPaymentMediumSelected(ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(cashControlDocument.getCustomerPaymentMediumCode()));
        }

        if ( cashControlDocument != null ) {
            // get the PaymentApplicationDocuments by reference number
            for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {
                String docId = cashControlDetail.getReferenceFinancialDocumentNumber();
                PaymentApplicationDocument doc = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
                if (doc == null) {
                    throw new UnknownDocumentIdException("Document " + docId + " no longer exists.  It may have been cancelled before being saved.");
                }
    
                cashControlDetail.setReferenceFinancialDocument(doc);
                WorkflowDocument workflowDoc = doc.getDocumentHeader().getWorkflowDocument();
                // KualiDocumentFormBase.populate() needs this updated in the session
                SpringContext.getBean(SessionDocumentService.class).addDocumentToUserSession(GlobalVariables.getUserSession(), workflowDoc);
            }
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

        CashControlDocumentForm ccForm = (CashControlDocumentForm) form;
        CashControlDocument ccDoc = ccForm.getCashControlDocument();

        if (ccDoc != null) {
            ccForm.setCashPaymentMediumSelected(ArConstants.PaymentMediumCode.CASH.equalsIgnoreCase(ccDoc.getCustomerPaymentMediumCode()));
        }

        if (ccForm.hasDocumentId()) {
            ccDoc = ccForm.getCashControlDocument();
            ccDoc.refreshReferenceObject("customerPaymentMedium");
            // recalc b/c changes to the amounts could have happened
            ccDoc.recalculateTotals();
        }

        return super.execute(mapping, form, request, response);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CashControlDocumentForm form = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument document = form.getCashControlDocument();

        //get the default bank code for the given document type, which is CTRL for this document.
        
        document.setBankCode("");
        Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(form.getDocTypeName());
        if (defaultBank != null) {
            document.setBankCode(defaultBank.getBankCode());
        }
        
        // set up the default values for the AR DOC Header (SHOULD PROBABLY MAKE THIS A SERVICE)
        AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService = SpringContext.getBean(AccountsReceivableDocumentHeaderService.class);
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#cancel(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();

        // If the cancel works, proceed to canceling the cash control doc
        if (cancelLinkedPaymentApplicationDocuments(cashControlDocument)) {
            return super.cancel(mapping, form, request, response);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#disapprove(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward disapprove(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean success = true;
        CashControlDocumentForm cashControlDocForm = (CashControlDocumentForm) form;
        CashControlDocument cashControlDocument = cashControlDocForm.getCashControlDocument();

        success = cancelLinkedPaymentApplicationDocuments(cashControlDocument);

        if (!success) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        return super.disapprove(mapping, form, request, response);
    }

    /**
     * This method cancels all linked Payment Application documents that are not already in approved status.
     * 
     * @param cashControlDocument
     * @throws WorkflowException
     */
    protected boolean cancelLinkedPaymentApplicationDocuments(CashControlDocument cashControlDocument) throws WorkflowException {
        boolean success = true;
        List<CashControlDetail> details = cashControlDocument.getCashControlDetails();

        for (CashControlDetail cashControlDetail : details) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);

            PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());
            if (KFSConstants.DocumentStatusCodes.CANCELLED.equals(applicationDocument.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode())) {
                // Ignore this case, as it should not impact the ability to cancel a cash control doc.
            }
            else if (!KFSConstants.DocumentStatusCodes.APPROVED.equals(applicationDocument.getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode())) {
                documentService.cancelDocument(applicationDocument, ArKeyConstants.DOCUMENT_DELETED_FROM_CASH_CTRL_DOC);
            }
            else {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.CashControlDetailFields.CASH_CONTROL_DETAILS_TAB, ArKeyConstants.ERROR_CANT_CANCEL_CASH_CONTROL_DOC_WITH_ASSOCIATED_APPROVED_PAYMENT_APPLICATION);
                success = false;
            }
        }
        return success;
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
        ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);

        CashControlDetail newCashControlDetail = cashControlDocForm.getNewCashControlDetail();
        newCashControlDetail.setDocumentNumber(cashControlDocument.getDocumentNumber());

        String customerNumber = newCashControlDetail.getCustomerNumber();
        if (StringUtils.isNotEmpty(customerNumber)) {
            // force customer numbers to upper case, since its a primary key
            customerNumber = customerNumber.toUpperCase();
        }
        newCashControlDetail.setCustomerNumber(customerNumber);

        // save the document, which will run business rules and make sure the doc is ready for lines
        KualiRuleService ruleService = SpringContext.getBean(KualiRuleService.class);
        boolean rulePassed = true;

        // apply save rules for the doc
        rulePassed &= ruleService.applyRules(new SaveDocumentEvent(KFSConstants.DOCUMENT_HEADER_ERRORS, cashControlDocument));

        // apply rules for the new cash control detail
        rulePassed &= ruleService.applyRules(new AddCashControlDetailEvent(ArConstants.NEW_CASH_CONTROL_DETAIL_ERROR_PATH_PREFIX, cashControlDocument, newCashControlDetail));

        // add the new detail if rules passed
        if (rulePassed) {

            CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);

            // add cash control detail. implicitly saves the cash control document
            cashControlDocumentService.addNewCashControlDetail(kualiConfiguration.getPropertyValueAsString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDocument, newCashControlDetail);

            // set a new blank cash control detail
            cashControlDocForm.setNewCashControlDetail(new CashControlDetail());

        }

        // recalc totals, including the docHeader total
        cashControlDocument.recalculateTotals();

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
        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        PaymentApplicationDocument payAppDoc = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());

        // this if statement is to catch the situation where a person deletes the line, but doesnt save
        // and then reloads. This will bring the deleted line back on the screen. If they then click
        // the delete line, and this test isnt here, it will try to cancel the already cancelled
        // document, which will throw a workflow error and barf.
        if (!payAppDoc.getDocumentHeader().getWorkflowDocument().isCanceled()) {
            documentService.cancelDocument(payAppDoc, ArKeyConstants.DOCUMENT_DELETED_FROM_CASH_CTRL_DOC);
        }
        cashControlDocument.deleteCashControlDetail(indexOfLineToDelete);

        // recalc totals, including the docHeader total
        cashControlDocument.recalculateTotals();

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

        // refresh reference objects
        cashControlDocument.refreshReferenceObject("customerPaymentMedium");
        cashControlDocument.refreshReferenceObject("generalLedgerPendingEntries");

        // payment medium might have been changed meanwhile so we save first the document
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(cashControlDocument);

        // generate the GLPEs
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        boolean success = glpeService.generateGeneralLedgerPendingEntries(cashControlDocument);

        if (!success) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArKeyConstants.ERROR_GLPES_NOT_CREATED);
        }

        if (cashControlDocument.getDocumentHeader().getFinancialDocumentInErrorNumber() != null) {
            reverseDebitCreditForCorrectionDocument(cashControlDocument);
        }

        // approve the GLPEs
        cashControlDocument.changeGeneralLedgerPendingEntriesApprovedStatusCode();

        // save the GLPEs in the database
        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        cashControlDocumentService.saveGLPEs(cashControlDocument);

        // approve the document when the GLPEs are generated
        // DocumentService docService = SpringContext.getBean(DocumentService.class);
        // docService.approveDocument(cashControlDocument, "Automatically approved document with GLPE generation.", null);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * Reverse credit and debit code for Correction document
     */
    private void reverseDebitCreditForCorrectionDocument(CashControlDocument cashControlDocument) {
        for (GeneralLedgerPendingEntry generalLedgerPendingEntry : cashControlDocument.getGeneralLedgerPendingEntries()) {
            if (KFSConstants.GL_CREDIT_CODE.equals(generalLedgerPendingEntry.getTransactionDebitCreditCode())) {
                generalLedgerPendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            else if (KFSConstants.GL_DEBIT_CODE.equals(generalLedgerPendingEntry.getTransactionDebitCreditCode())) {
                generalLedgerPendingEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
        }
    }


    /**
     * Recalculates the cash control total since user could have changed it during their update.
     * 
     * @param cashControlDocument
     */
    protected KualiDecimal calculateCashControlTotal(CashControlDocument cashControlDocument) {
        KualiDecimal total = KualiDecimal.ZERO;
        for (CashControlDetail cashControlDetail : cashControlDocument.getCashControlDetails()) {

            total = total.add(cashControlDetail.getFinancialDocumentLineAmount());
        }
        return total;
    }

}
