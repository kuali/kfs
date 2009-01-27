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
package org.kuali.kfs.module.ar.document.web.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.document.CashControlDocument;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CashControlDocumentService;
import org.kuali.kfs.module.ar.document.validation.event.AddCashControlDetailEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.exception.UnknownDocumentIdException;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

public class CashControlDocumentAction extends FinancialSystemTransactionalDocumentActionBase {

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
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
            doc = (PaymentApplicationDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
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

        ActionForward forward = super.execute(mapping, form, request, response);

        return forward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#createDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void createDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.createDocument(kualiDocumentFormBase);
        CashControlDocumentForm form = (CashControlDocumentForm) kualiDocumentFormBase;
        CashControlDocument document = form.getCashControlDocument();

        // set up the default values for the AR DOC Header (SHOULD PROBABLY MAKE THIS A SERVICE)
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
        
        //  force customer numbers to upper case, since its a primary key
        newCashControlDetail.setCustomerNumber(newCashControlDetail.getCustomerNumber().toUpperCase());
        
        //  save the document, which will run business rules and make sure the doc is ready for lines
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
            cashControlDocumentService.addNewCashControlDetail(kualiConfiguration.getPropertyString(ArKeyConstants.CREATED_BY_CASH_CTRL_DOC), cashControlDocument, newCashControlDetail);

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
        DocumentService documentService = SpringContext.getBean(DocumentService.class);

        PaymentApplicationDocument applicationDocument = (PaymentApplicationDocument) documentService.getByDocumentHeaderId(cashControlDetail.getReferenceFinancialDocumentNumber());
        documentService.cancelDocument(applicationDocument, ArKeyConstants.DOCUMENT_DELETED_FROM_CASH_CTRL_DOC);
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

        // refresh reference objects
        cashControlDocument.refreshReferenceObject("customerPaymentMedium");
        cashControlDocument.refreshReferenceObject("generalLedgerPendingEntries");

        // payment medium might have been changed meanwhile so we save first the document
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(cashControlDocument);

        // generate the GLPEs
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        boolean success = glpeService.generateGeneralLedgerPendingEntries(cashControlDocument);

        if(cashControlDocument.getCustomerPaymentMedium().getCustomerPaymentMediumCode().equalsIgnoreCase(ArConstants.PaymentMediumCode.CHECK)) {
            // get associated bank
            Bank bank = (Bank)SpringContext.getBean(BankService.class).getByPrimaryId(cashControlDocument.getBankCode());
            GeneralLedgerPendingEntrySequenceHelper sequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
            GeneralLedgerPendingEntry bankOffsetEntry = new GeneralLedgerPendingEntry();
            // add additional GLPE's based on bank code
            if(!glpeService.populateBankOffsetGeneralLedgerPendingEntry(bank, cashControlDocument.getCashControlTotalAmount(), cashControlDocument, cashControlDocument.getPostingYear(), sequenceHelper, bankOffsetEntry, KFSConstants.CASH_CONTROL_DOCUMENT_ERRORS)) {
                success = false;
            }

            AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
            bankOffsetEntry.setTransactionLedgerEntryDescription(accountingDocumentRuleUtil.formatProperty(KFSKeyConstants.Bank.DESCRIPTION_GLPE_BANK_OFFSET)); 
            cashControlDocument.addPendingEntry(bankOffsetEntry);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(bankOffsetEntry);
            success &= glpeService.populateOffsetGeneralLedgerPendingEntry(cashControlDocument.getPostingYear(), bankOffsetEntry, sequenceHelper, offsetEntry);
            cashControlDocument.addPendingEntry(offsetEntry);
            sequenceHelper.increment();
        }
        
        if (!success) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, ArKeyConstants.ERROR_GLPES_NOT_CREATED);
        }
        // approve the GLPEs
        cashControlDocument.changeGeneralLedgerPendingEntriesApprovedStatusCode();

        // save the GLPEs in the database
        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);
        cashControlDocumentService.saveGLPEs(cashControlDocument);

        //  approve the document when the GLPEs are generated
        //DocumentService docService = SpringContext.getBean(DocumentService.class);
        //docService.approveDocument(cashControlDocument, "Automatically approved document with GLPE generation.", null);
        
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

