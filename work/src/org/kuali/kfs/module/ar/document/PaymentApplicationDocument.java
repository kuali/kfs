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
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.businessobject.SystemInformation;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.PaymentApplicationDocumentService;
import org.kuali.kfs.module.ar.document.service.SystemInformationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentApplicationDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocument.class);

    private List<InvoicePaidApplied> appliedPayments;
    private List<NonInvoiced> nonInvoicedPayments;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private NonAppliedHolding nonAppliedHolding;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private transient PaymentApplicationDocumentService paymentApplicationDocumentService;

    public PaymentApplicationDocument() {
        super();
        this.appliedPayments = new ArrayList<InvoicePaidApplied>();
        this.nonInvoicedPayments = new ArrayList<NonInvoiced>();
        this.nonInvoicedDistributions = new ArrayList<NonInvoicedDistribution>();
        this.nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
        this.paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
    }

    public KualiDecimal getTotalUnappliedFunds() {
        return paymentApplicationDocumentService.getTotalUnappliedFundsForPaymentApplicationDocument(this);
    }

    public KualiDecimal getTotalUnappliedFundsToBeApplied() {
        return paymentApplicationDocumentService.getTotalUnappliedFundsToBeAppliedForPaymentApplicationDocument(this);
    }

    public KualiDecimal getDocumentTotal() throws WorkflowException {
        KualiDecimal amount = null;
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        String documentNumber = getDocumentHeader().getOrganizationDocumentNumber();
        if(null == documentNumber) {
            return KualiDecimal.ZERO;
        } else {
            Document referenceDocument = documentService.getByDocumentHeaderId(documentNumber);
            if(referenceDocument instanceof CashControlDocument) {
                CashControlDocument cashControlDocument = (CashControlDocument) referenceDocument;
                amount = cashControlDocument.getDocumentHeader().getFinancialDocumentTotalAmount();
            }
        }
        return amount;
    }
    
    public KualiDecimal getTotalToBeApplied() throws WorkflowException {
        KualiDecimal totalAppliedAmount = getTotalAppliedAmount();
        return getDocumentTotal().subtract(totalAppliedAmount);
    }

    public KualiDecimal getTotalAppliedAmount() {
        return paymentApplicationDocumentService.getTotalAppliedAmountForPaymentApplicationDocument(this);
    }

    public List<InvoicePaidApplied> getAppliedPayments() {
        return appliedPayments;
    }

    public void setAppliedPayments(List<InvoicePaidApplied> appliedPayments) {
        this.appliedPayments = appliedPayments;
    }

    public void setNonInvoicedPayments(List<NonInvoiced> nonInvoicedPayments) {
        this.nonInvoicedPayments = nonInvoicedPayments;
    }

    public Collection<NonInvoicedDistribution> getNonInvoicedDistributions() {
        return nonInvoicedDistributions;
    }

    public void setNonInvoicedDistributions(Collection<NonInvoicedDistribution> nonInvoicedDistributions) {
        this.nonInvoicedDistributions = nonInvoicedDistributions;
    }

    public Collection<NonAppliedDistribution> getNonAppliedDistributions() {
        return nonAppliedDistributions;
    }

    public void setNonAppliedDistributions(Collection<NonAppliedDistribution> nonAppliedDistributions) {
        this.nonAppliedDistributions = nonAppliedDistributions;
    }

    public NonAppliedHolding getNonAppliedHolding() {
        return nonAppliedHolding;
    }

    public void setNonAppliedHolding(NonAppliedHolding nonAppliedHolding) {
        this.nonAppliedHolding = nonAppliedHolding;
    }

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    /**
     * This method retrieves a specific applied payment from the list, by array index
     * 
     * @param index the index of the applied payment to retrieve
     * @return an InvoicePaidApplied
     */
    public InvoicePaidApplied getAppliedPayment(int index) {
        if (index >= appliedPayments.size()) {
            for (int i = appliedPayments.size(); i <= index; i++) {
                appliedPayments.add(new InvoicePaidApplied());
            }
        }
        return appliedPayments.get(index);
    }

    /**
     * This method retrieves a specific non invoiced payment from the list, by array index
     * 
     * @param index the index of the non invoiced payment to retrieve
     * @return an NonInvoiced
     */
    public NonInvoiced getNonInvoicedPayment(int index) {
        if (index >= nonInvoicedPayments.size()) {
            for (int i = nonInvoicedPayments.size(); i <= index; i++) {
                nonInvoicedPayments.add(new NonInvoiced());
            }
        }
        return nonInvoicedPayments.get(index);
    }

    /**
     * @return the non-invoiced payments
     */
    public List<NonInvoiced> getNonInvoicedPayments() {
        return nonInvoicedPayments;
    }

    /**
     * @return the sum of the non invoiced payments on the document
     */
    public KualiDecimal getNonInvoicedTotalAmount() {
        List<NonInvoiced> payments = getNonInvoicedPayments();
        KualiDecimal total = new KualiDecimal(0);
        if(null == payments || 1 > payments.size()) {
            total = KualiDecimal.ZERO;
        } else {
            for(NonInvoiced payment : payments) {
                total = total.add(payment.getFinancialDocumentLineAmount());
            }
        }
        return total;
    }

//    private boolean isForLockbox(CashControlDocument cashControl) {
//        return cashControl.getDocumentHeader().getDocumentDescription().startsWith(ArConstants.LOCKBOX_DOCUMENT_DESCRIPTION);
//    }
//
//    private boolean isForCreditCard(CashControlDocument cashControl) {
//        return ArConstants.PaymentMediumCode.CREDIT_CARD.equals(cashControl.getCustomerPaymentMediumCode());
//    }
//
//    private boolean isForWireTransfer(CashControlDocument cashControl) {
//        return ArConstants.PaymentMediumCode.WIRE_TRANSFER.equals(cashControl.getCustomerPaymentMediumCode());
//    }
    
    /**
     * @param ipa
     * @return
     */
    private SystemInformation getSystemInformation(InvoicePaidApplied ipa) throws WorkflowException {
        CustomerInvoiceDocument invoice = ipa.getCustomerInvoiceDocument();
        String processingOrgCode = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        String processingChartCode = invoice.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        SystemInformation systemInformation = 
            SpringContext.getBean(SystemInformationService.class).getByProcessingChartAndOrg(processingChartCode, processingOrgCode);
        return systemInformation;
    }
    
    /**
     * @param ipa
     * @return the university clearing account
     */
    private Account getUniversityClearingAccount(InvoicePaidApplied ipa) throws WorkflowException {
        SystemInformation systemInformation = getSystemInformation(ipa);
        Account universityClearingAccount = systemInformation.getUniversityClearingAccount();
        return universityClearingAccount;
    }
    
    /**
     * @param ipa
     * @return the billing organization account
     */
    private Account getBillingOrganizationAccount(InvoicePaidApplied ipa) {
        String invoiceDocumentNumber = ipa.getFinancialDocumentReferenceInvoiceNumber();
        CustomerInvoiceDocumentService invoices =
            SpringContext.getBean(CustomerInvoiceDocumentService.class);
        CustomerInvoiceDocument invoice = 
            invoices.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
        CustomerInvoiceDetail invoiceDetail = ipa.getInvoiceItem();
        return invoiceDetail.getAccount();
    }

    /**
     * @param ipa
     * @return the accounts receivable object code
     */
    public ObjectCode getAccountsReceivableObjectCode(InvoicePaidApplied ipa) throws WorkflowException {
        CustomerInvoiceDetail detail = ipa.getInvoiceItem();
        String parameterName = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD;
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String parameterValue = parameterService.getParameterValue(CustomerInvoiceDocument.class, parameterName);
        
        ObjectCode objectCode = null;
        if("1".equals(parameterValue) || "2".equals(parameterValue)) {
            objectCode = detail.getObjectCode();
        } else if ("3".equals(parameterValue)) {
            objectCode = detail.getCustomerInvoiceDocument().getPaymentFinancialObject();
        }
        
        return objectCode;
    }

    /**
     * @param ipa
     * @return the unapplied object code
     * @throws WorkflowException
     */
    public ObjectCode getUnappliedCashObjectCode(InvoicePaidApplied ipa) throws WorkflowException {
        SystemInformation systemInformation = getSystemInformation(ipa);
        return systemInformation.getUniversityClearingObject();
    }

    /**
     *
     * @param ipa
     * @return the credit card charges object code
     * @throws WorkflowException
     */
    public ObjectCode getCreditCardChargesObjectCode(InvoicePaidApplied ipa) throws WorkflowException {
        SystemInformation systemInformation = getSystemInformation(ipa);
        return systemInformation.getCreditCardFinancialObject();
    }
    
    /**
     * @param invoicePaidApplied
     * @return the cash object code
     */
    private ObjectCode getCashObjectCode(InvoicePaidApplied invoicePaidApplied) {
        return invoicePaidApplied.getInvoiceItem().getChart().getFinancialCashObject();
    }
    
    /**
     * @param sequenceHelper
     * @return the pending entries for the document
     */
    private List<GeneralLedgerPendingEntry> createPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) throws WorkflowException {
        
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        List<InvoicePaidApplied> appliedPayments = getAppliedPayments();
        for(InvoicePaidApplied ipa : appliedPayments) {
            Account clearingAccount = getUniversityClearingAccount(ipa);
            Account billingOrganizationAccount = getBillingOrganizationAccount(ipa);
            ObjectCode cashObjectCode = getCashObjectCode(ipa);
            ObjectCode accountsReceivableObjectCode = getAccountsReceivableObjectCode(ipa);
            ObjectCode unappliedCashObjectCode = getUnappliedCashObjectCode(ipa);
            
            GeneralLedgerPendingEntry debitGLPE_1 = new GeneralLedgerPendingEntry();
            debitGLPE_1.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitGLPE_1.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            debitGLPE_1.setAccount(clearingAccount);
            debitGLPE_1.setAccountNumber(clearingAccount.getAccountNumber());
            debitGLPE_1.setChartOfAccountsCode(clearingAccount.getChartOfAccountsCode());
            debitGLPE_1.setFinancialObject(unappliedCashObjectCode);
            debitGLPE_1.setFinancialObjectCode(unappliedCashObjectCode.getFinancialObjectCode());
            entries.add(debitGLPE_1);
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry creditGLPE_1 = new GeneralLedgerPendingEntry();
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), debitGLPE_1, sequenceHelper, creditGLPE_1);
            creditGLPE_1.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditGLPE_1.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            creditGLPE_1.setAccount(clearingAccount);
            creditGLPE_1.setChartOfAccountsCode(clearingAccount.getChartOfAccountsCode());
            creditGLPE_1.setAccountNumber(clearingAccount.getAccountNumber());
            creditGLPE_1.setFinancialObject(cashObjectCode);
            creditGLPE_1.setFinancialObjectCode(cashObjectCode.getFinancialObjectCode());
            entries.add(creditGLPE_1);
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry debitGLPE_2 = new GeneralLedgerPendingEntry();
            debitGLPE_2.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitGLPE_2.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            debitGLPE_2.setAccount(billingOrganizationAccount);
            debitGLPE_2.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            debitGLPE_2.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            debitGLPE_2.setFinancialObject(cashObjectCode);
            debitGLPE_2.setFinancialObjectCode(cashObjectCode.getFinancialObjectCode());
            entries.add(debitGLPE_2);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry creditGLPE_2 = new GeneralLedgerPendingEntry();
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), debitGLPE_2, sequenceHelper, creditGLPE_2);
            creditGLPE_2.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditGLPE_2.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            creditGLPE_2.setAccount(billingOrganizationAccount);
            creditGLPE_2.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            creditGLPE_2.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            creditGLPE_2.setFinancialObject(accountsReceivableObjectCode);
            creditGLPE_2.setFinancialObjectCode(accountsReceivableObjectCode.getFinancialObjectCode());
            entries.add(creditGLPE_2);
            sequenceHelper.increment();
        }
        
        return entries;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource#generateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        try {
            List<GeneralLedgerPendingEntry> entries = createPendingEntries(sequenceHelper);
            for(GeneralLedgerPendingEntry entry : entries) {
                addPendingEntry(entry);
            }
        } catch(Throwable t) {
            LOG.error("Exception encountered while generating pending entries.",t);
            return false;
        }
        
        return true;
    }
    
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        // Auto-generated method stub
        return true;
    }

    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail) {
        // Auto-generated method stub
        return null;
    }

    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        // Auto-generated method stub
        return new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
    }

    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // Auto-generated method stub
        return false;
    }

    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        
        if(getDocumentHeader().getWorkflowDocument().stateIsApproved()) {
            java.util.Date _today = SpringContext.getBean(DateTimeService.class).getCurrentDate();
            java.sql.Date today = new java.sql.Date(_today.getTime());
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            for(InvoicePaidApplied ipa : getAppliedPayments()) {
                String invoiceDocumentNumber = ipa.getFinancialDocumentReferenceInvoiceNumber();
                CustomerInvoiceDocumentService invoices =
                    SpringContext.getBean(CustomerInvoiceDocumentService.class);
                CustomerInvoiceDocument invoice = 
                    invoices.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
                // KULAR-384
                invoice.setClosedDate(today);
                invoice.setOpenInvoiceIndicator(false);
                try {
                    documentService.saveDocument(invoice);
                } catch(WorkflowException we) {
                    LOG.error("Failed to update closed date on Invoice.", we);
                }
            }
        }
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // generate GLPEs
        GeneralLedgerPendingEntryService generalLedgerPendingEntryService = 
            SpringContext.getBean(GeneralLedgerPendingEntryService.class); 
        if (!generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }
        super.prepareForSave(event);  
    }
    
}
