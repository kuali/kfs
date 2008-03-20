/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.service.impl;

import org.kuali.core.service.DocumentService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.ArConstants;
import org.kuali.module.ar.bo.AccountsReceivableDocumentHeader;
import org.kuali.module.ar.bo.CashControlDetail;
import org.kuali.module.ar.bo.NonAppliedHolding;
import org.kuali.module.ar.document.CashControlDocument;
import org.kuali.module.ar.document.PaymentApplicationDocument;
import org.kuali.module.ar.service.AccountsReceivableDocumentHeaderService;
import org.kuali.module.ar.service.CashControlDocumentService;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.document.DistributionOfIncomeAndExpenseDocument;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class CashControlDocumentServiceImpl implements CashControlDocumentService {

    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private DocumentService documentService;

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createCashReceiptDocument(org.kuali.module.ar.document.CashControlDocument)
     */
    public String createCashReceiptDocument(CashControlDocument cashControlDocument) throws WorkflowException {
        String referenceDocumentNumber = "";

        CashReceiptDocument referenceDocument = (CashReceiptDocument) documentService.getNewDocument(CashReceiptDocument.class);
        referenceDocumentNumber = referenceDocument.getDocumentNumber();

        return referenceDocumentNumber;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createDistributionOfIncomeAndExpenseDocument(org.kuali.module.ar.document.CashControlDocument)
     */
    public String createDistributionOfIncomeAndExpenseDocument(CashControlDocument cashControlDocument) throws WorkflowException {
        String referenceDocumentNumber = "";

        DistributionOfIncomeAndExpenseDocument referenceDocument = (DistributionOfIncomeAndExpenseDocument) documentService.getNewDocument(DistributionOfIncomeAndExpenseDocument.class);
        referenceDocumentNumber = referenceDocument.getDocumentNumber();

        return referenceDocumentNumber;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createGeneralErrorCorrectionDocument(org.kuali.module.ar.document.CashControlDocument)
     */
    public String createGeneralErrorCorrectionDocument(CashControlDocument cashControlDocument) throws WorkflowException {
        String referenceDocumentNumber = "";

        GeneralErrorCorrectionDocument referenceDocument = (GeneralErrorCorrectionDocument) documentService.getNewDocument(GeneralErrorCorrectionDocument.class);
        referenceDocumentNumber = referenceDocument.getDocumentNumber();

        return referenceDocumentNumber;
    }


    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#createAndSavePaymentApplicationDocument(java.lang.String,
     *      org.kuali.module.ar.document.CashControlDocument, org.kuali.module.ar.bo.CashControlDetail)
     */
    public PaymentApplicationDocument createAndSavePaymentApplicationDocument(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException {

        // create a new PaymentApplicationdocument
        PaymentApplicationDocument doc = (PaymentApplicationDocument) documentService.getNewDocument(PaymentApplicationDocument.class);
        // set a description to say that this application document has been created by the CashControldocument
        doc.getDocumentHeader().setFinancialDocumentDescription(description);

        // set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(doc.getDocumentNumber());
        doc.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        // set the non applied holding
        NonAppliedHolding nonAppliedHolding = new NonAppliedHolding();
        nonAppliedHolding.setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
        doc.setNonAppliedHolding(nonAppliedHolding);

        // the line amount for the new PaymentApplicationDocument should be the line amount in the new cash control detail
        doc.getDocumentHeader().setFinancialDocumentTotalAmount(cashControlDetail.getFinancialDocumentLineAmount());

        // refresh nonupdatable references and save the PaymentApplicationDocument
        doc.refreshNonUpdateableReferences();
        documentService.saveDocument(doc);
        return doc;
    }

    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#hasAllApplicationDocumentsApproved(org.kuali.module.ar.document.CashControlDocument)
     */
    public boolean hasAllApplicationDocumentsApproved(CashControlDocument cashControlDocument) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * This method gets the accounts receivable header
     * @return the accounts receivable header
     */
    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    /**
     * This method sets the accounts receivable header
     * @param accountsReceivableDocumentHeaderService
     */
    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    /**
     * This method gets the document service
     * @return the document service
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * This method sets the document service
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }


    /**
     * @see org.kuali.module.ar.service.CashControlDocumentService#addNewCashControlDetail(java.lang.String,
     *      org.kuali.module.ar.document.CashControlDocument, org.kuali.module.ar.bo.CashControlDetail)
     */
    public void addNewCashControlDetail(String description, CashControlDocument cashControlDocument, CashControlDetail cashControlDetail) throws WorkflowException {
        
        CashControlDocumentService cashControlDocumentService = SpringContext.getBean(CashControlDocumentService.class);

        // create a new PaymentApplicationdocument
        PaymentApplicationDocument doc = createAndSavePaymentApplicationDocument(description, cashControlDocument, cashControlDetail);

        // update new cash control detail fields to refer to the new created PaymentApplicationDocument
        cashControlDetail.setReferenceFinancialDocument(doc);
        cashControlDetail.setReferenceFinancialDocumentNumber(doc.getDocumentNumber());
        // newCashControlDetail.setStatus(doc.getDocumentHeader().getWorkflowDocument().getStatusDisplayValue());

        // add cash control detail
        cashControlDocument.addCashControlDetail(cashControlDetail);

    }

}
