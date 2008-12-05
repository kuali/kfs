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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceTyp;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.BalanceTypService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CashControlDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonAppliedDistribution;
import org.kuali.kfs.module.ar.businessobject.NonAppliedHolding;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerInvoiceDetail;
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
import org.kuali.rice.kns.bo.DocumentType;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.DocumentTypeService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentApplicationDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocument.class);

    private List<InvoicePaidApplied> invoicePaidApplieds;
    private List<NonInvoiced> nonInvoiceds;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private NonAppliedHolding nonAppliedHolding;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private transient PaymentApplicationDocumentService paymentApplicationDocumentService;

    public PaymentApplicationDocument() {
        super();
        this.invoicePaidApplieds = new ArrayList<InvoicePaidApplied>();
        this.nonInvoiceds = new ArrayList<NonInvoiced>();
        this.nonInvoicedDistributions = new ArrayList<NonInvoicedDistribution>();
        this.nonAppliedDistributions = new ArrayList<NonAppliedDistribution>();
    }

    /**
     * The payment number is the PaymentMediumID for the single CashControlDetail
     * on a CashControlDocument related to a PaymentApplicationDocument.
     * 
     * @return
     */
    public String getPaymentNumber() {
        String paymentNumber = "";
        try {
            CashControlDocument cashControlDocument = getCashControlDocument();
            if(!ObjectUtils.isNull(cashControlDocument)) {
                List<CashControlDetail> cashControlDetails = cashControlDocument.getCashControlDetails();
                if(0 < cashControlDetails.size()) {
                    CashControlDetail firstDetail = cashControlDetails.iterator().next();
                    paymentNumber = firstDetail.getCustomerPaymentMediumIdentifier();
                }
            }
        } catch(WorkflowException we) {
            LOG.error("Failed to retrieve CashControlDocument", we);
        }
        return paymentNumber;
    }
    
    public CashControlDocument getCashControlDocument() throws WorkflowException {
        CashControlDocument cashControlDocument = 
            getPaymentApplicationDocumentService().getCashControlDocumentForPaymentApplicationDocument(this);
        return cashControlDocument;
    }
    
    public CashControlDetail getCashControlDetail() throws WorkflowException {
        CashControlDetail cashControlDetail = 
            getPaymentApplicationDocumentService().getCashControlDetailForPaymentApplicationDocument(this);
        return cashControlDetail;
    }
    
    public KualiDecimal getCashControlTotalAmount() throws WorkflowException {
        CashControlDocument cashControlDocument = 
            getPaymentApplicationDocumentService().getCashControlDocumentForPaymentApplicationDocument(this);
        CashControlDetail cashControlDetail = 
            getPaymentApplicationDocumentService().getCashControlDetailForPaymentApplicationDocument(this);
        KualiDecimal amount = KualiDecimal.ZERO;
        if(null != cashControlDocument) {
            amount = cashControlDetail.getFinancialDocumentLineAmount();
        }
        return amount;
    }
    
    public KualiDecimal getInvoicePaidAppliedsTotal() {
        KualiDecimal amount = new KualiDecimal(0);
        for(InvoicePaidApplied payment : getInvoicePaidApplieds()) {
            amount = amount.add(payment.getInvoiceItemAppliedAmount());
        }
        return amount;
    }
    
    public KualiDecimal getNonInvoicedDistributionsTotal() {
        KualiDecimal amount = new KualiDecimal(0);
        for(NonInvoicedDistribution nonInvoicedDistribution : getNonInvoicedDistributions()) {
            amount = amount.add(nonInvoicedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }
    
    public KualiDecimal getNonAppliedDistributionsTotal() {
        KualiDecimal amount = new KualiDecimal(0);
        for(NonAppliedDistribution nonAppliedDistribution : getNonAppliedDistributions()) {
            amount = amount.add(nonAppliedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }
    
    public KualiDecimal getNonAppliedHoldingTotal() {
        KualiDecimal amount = KualiDecimal.ZERO;
        NonAppliedHolding nonAppliedHolding = getNonAppliedHolding();
        if(!ObjectUtils.isNull(nonAppliedHolding)) {
            nonAppliedHolding.refresh();
            KualiDecimal lineAmount = 
                nonAppliedHolding.getFinancialDocumentLineAmount();
            if(ObjectUtils.isNotNull(lineAmount)) {
                amount = lineAmount;
            }
        }
        return amount;
    }
    
    public KualiDecimal getNonInvoicedTotal() {
        KualiDecimal amount = new KualiDecimal(0);
        for(NonInvoiced nonInvoiced : getNonInvoiceds()) {
            amount = amount.add(nonInvoiced.getFinancialDocumentLineAmount());
        }
        return amount;
    }
    
    public KualiDecimal getTotalNonAr() {
        return getNonInvoicedTotal();
    }
    
    public KualiDecimal getTotalApplied() {
        KualiDecimal amount = KualiDecimal.ZERO;
        try {
            KualiDecimal ccta = getCashControlTotalAmount();
            KualiDecimal btba = getBalanceToBeApplied();
            amount = ccta.subtract(btba);
        } catch(WorkflowException w) {
            LOG.error("Failed to calculate total applied amount.", w);
        }
        return amount;
    }
    
    public KualiDecimal getTotalUnapplied() {
        return getNonAppliedHoldingTotal();
    }
    
    public KualiDecimal getBalanceToBeApplied() throws WorkflowException {
        
        // KULAR-504: "Balance" must equal line item amount from Cash Control - not the total Cash Control
        KualiDecimal amount = getCashControlDetail().getFinancialDocumentLineAmount();
        
        KualiDecimal subtrahend = getInvoicePaidAppliedsTotal();
        if(ObjectUtils.isNotNull(subtrahend)) {
            amount = amount.subtract(subtrahend);
        }
        subtrahend = getTotalNonAr();
        if(ObjectUtils.isNotNull(subtrahend)) {
            amount = amount.subtract(subtrahend);
        }
        subtrahend = getTotalUnapplied();
        if(ObjectUtils.isNotNull(subtrahend)) {
            amount = amount.subtract(subtrahend);
        }
        return amount;
    }
    
    public List<InvoicePaidApplied> getInvoicePaidApplieds() {
        return invoicePaidApplieds;
    }

    public void setInvoicePaidApplieds(List<InvoicePaidApplied> appliedPayments) {
        this.invoicePaidApplieds = appliedPayments;
    }

    public List<NonInvoiced> getNonInvoiceds() {
        return nonInvoiceds;
    }

    public void setNonInvoiceds(List<NonInvoiced> nonInvoiceds) {
        this.nonInvoiceds = nonInvoiceds;
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
    public InvoicePaidApplied getInvoicePaidApplied(int index) {
        if (index >= invoicePaidApplieds.size()) {
            for (int i = invoicePaidApplieds.size(); i <= index; i++) {
                invoicePaidApplieds.add(new InvoicePaidApplied());
            }
        }
        return invoicePaidApplieds.get(index);
    }

    /**
     * This method retrieves a specific non invoiced payment from the list, by array index
     * 
     * @param index the index of the non invoiced payment to retrieve
     * @return an NonInvoiced
     */
    public NonInvoiced getNonInvoiced(int index) {
        if (index >= nonInvoiceds.size()) {
            for (int i = nonInvoiceds.size(); i <= index; i++) {
                nonInvoiceds.add(new NonInvoiced());
            }
        }
        return nonInvoiceds.get(index);
    }

    /**
     * @return the sum of the non invoiced payments on the document
     */
    public KualiDecimal getNonInvoicedTotalAmount() {
        List<NonInvoiced> payments = getNonInvoiceds();
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
     * 
     * This method...
     * @param cashControlDocument
     * @return
     * @throws WorkflowException
     */
    private SystemInformation getSystemInformation(CashControlDocument cashControlDocument) throws WorkflowException {
        if(ObjectUtils.isNull(cashControlDocument)) {
            return null;
        }
        String processingOrgCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        String processingChartCode = cashControlDocument.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        SystemInformation systemInformation = 
            SpringContext.getBean(SystemInformationService.class).getByProcessingChartAndOrg(processingChartCode, processingOrgCode);
        return systemInformation;
    }

    /**
     * 
     * @param cashControlDocument
     * @return
     * @throws WorkflowException
     */
    private Account getUniversityClearingAccount(CashControlDocument cashControlDocument) throws WorkflowException {
        SystemInformation systemInformation = getSystemInformation(cashControlDocument);
        if(ObjectUtils.isNull(systemInformation)) {
            return null;
        }
        systemInformation.refresh();
        Account universityClearingAccount = null;
        try {
            universityClearingAccount = systemInformation.getUniversityClearingAccount();
        } catch(Throwable t) {
            t.printStackTrace();
        }
        universityClearingAccount.refresh();
        return universityClearingAccount;
    }
    
    /**
     * @param ipa
     * @return the university clearing account
     */
    private Account getUniversityClearingAccount(InvoicePaidApplied ipa) throws WorkflowException {
        SystemInformation systemInformation = getSystemInformation(ipa);
        systemInformation.refresh();
        Account universityClearingAccount = null;
        try {
            universityClearingAccount = systemInformation.getUniversityClearingAccount();
        } catch(Throwable t) {
            t.printStackTrace();
        }
        universityClearingAccount.refresh();
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
        detail.refresh();
        detail.refreshNonUpdateableReferences();
        String parameterName = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD;
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        String parameterValue = parameterService.getParameterValue(CustomerInvoiceDocument.class, parameterName);
        
        ObjectCode objectCode = null;
        if("1".equals(parameterValue) || "2".equals(parameterValue)) {
            detail.refreshReferenceObject("objectCode");
            objectCode = detail.getObjectCode();
        } else if ("3".equals(parameterValue)) {
            detail.refreshReferenceObject("customerInvoiceDocument");
            CustomerInvoiceDocument customerInvoiceDocument = detail.getCustomerInvoiceDocument();
            customerInvoiceDocument.refreshReferenceObject("paymentFinancialObject");
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
        invoicePaidApplied.refresh();
        CustomerInvoiceDetail detail = invoicePaidApplied.getInvoiceItem();
        detail.refresh();
        Chart chart = detail.getChart();
        chart.refresh();
        chart.refreshReferenceObject("financialCashObject");
        return chart.getFinancialCashObject();
    }
    
    /**
     * This method gets an ObjectCode from an invoice document.
     * 
     * @param invoicePaidApplied
     * @return
     * @throws WorkflowException
     */
    private ObjectCode getInvoiceObjectCode(InvoicePaidApplied invoicePaidApplied) throws WorkflowException {
        CustomerInvoiceDocument customerInvoiceDocument = invoicePaidApplied.getCustomerInvoiceDocument();
        CustomerInvoiceDetail customerInvoiceDetail = invoicePaidApplied.getInvoiceItem();
        ReceivableCustomerInvoiceDetail receivableInvoiceDetail = new ReceivableCustomerInvoiceDetail(customerInvoiceDetail, customerInvoiceDocument);
        ObjectCode objectCode = null;
        if(ObjectUtils.isNotNull(receivableInvoiceDetail) && ObjectUtils.isNotNull(receivableInvoiceDetail.getFinancialObjectCode())) {
            objectCode = receivableInvoiceDetail.getObjectCode();
        }
        return objectCode;
    }
    
    /**
     * This method returns the CashControlDocument for a PaymentApplicationDocument.
     * If the PaymentApplicationDocument does not have a CashControlDocument itself,
     * it will walk up through the tree of parent PaymentApplicationDocuments in
     * order to find one that does. The only way for a PaymentApplicationDocument to
     * be created without a CashControlDocument is via an unapplied line on 
     * another PaymentApplicationDocument. So we have to find that one.
     * 
     * @param paymentApplicationDocument
     * @return
     * @throws WorkflowException
     */
    private CashControlDocument getCashControlDocument(PaymentApplicationDocument paymentApplicationDocument) throws WorkflowException {
        CashControlDocument cashControlDocument = paymentApplicationDocument.getCashControlDocument();
        if(null == cashControlDocument) {
            // FIXME Add in the recursive parent search to find a CashControlDocument 
            // if one isn't present on the PaymentApplicationDocument passed in.
        }
        return paymentApplicationDocument.getCashControlDocument();
    }
    
    /**
     * @param sequenceHelper
     * @return the pending entries for the document
     */
    private List<GeneralLedgerPendingEntry> createPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) throws WorkflowException {
        
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        String documentTypeCode = "APP";
        DocumentType documentType = null;
        DocumentTypeService documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        String actualsBalanceTypeCode = "AC";
        BalanceTypService balanceTypeService = SpringContext.getBean(BalanceTypService.class);
        BalanceTyp balanceType = balanceTypeService.getBalanceTypByCode(actualsBalanceTypeCode);
        CashControlDocument cashControlDocument = getCashControlDocument(this);
        Account clearingAccount = getUniversityClearingAccount(cashControlDocument);
        
        List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        List<InvoicePaidApplied> appliedPayments = getInvoicePaidApplieds();
        for(InvoicePaidApplied ipa : appliedPayments) {
            ipa.refreshNonUpdateableReferences();
            Account billingOrganizationAccount = getBillingOrganizationAccount(ipa);
            ObjectCode invoiceObjectCode = getInvoiceObjectCode(ipa);
            ObjectUtils.isNull(invoiceObjectCode); // Refresh 
            ObjectCode accountsReceivableObjectCode = getAccountsReceivableObjectCode(ipa);
            ObjectCode unappliedCashObjectCode = getUnappliedCashObjectCode(ipa);
            
            GeneralLedgerPendingEntry debitGLPE_1 = new GeneralLedgerPendingEntry();
            debitGLPE_1.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitGLPE_1.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            debitGLPE_1.setAccountNumber(clearingAccount.getAccountNumber());
            debitGLPE_1.setChartOfAccountsCode(clearingAccount.getChartOfAccountsCode());
            debitGLPE_1.setFinancialObjectCode(unappliedCashObjectCode.getFinancialObjectCode());
            debitGLPE_1.setFinancialObjectTypeCode(unappliedCashObjectCode.getFinancialObjectTypeCode());
            debitGLPE_1.setFinancialBalanceTypeCode(actualsBalanceTypeCode);
            debitGLPE_1.setFinancialDocumentTypeCode(documentTypeCode);
            debitGLPE_1.setUniversityFiscalYear(getPostingYear());
            entries.add(debitGLPE_1);
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry creditGLPE_1 = new GeneralLedgerPendingEntry();
            creditGLPE_1.setUniversityFiscalYear(getPostingYear());
            creditGLPE_1.setChartOfAccountsCode(debitGLPE_1.getChartOfAccountsCode());
            creditGLPE_1.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditGLPE_1.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            creditGLPE_1.setAccountNumber(clearingAccount.getAccountNumber());
            creditGLPE_1.setFinancialObjectCode(invoiceObjectCode.getFinancialObjectCode());
            creditGLPE_1.setFinancialObjectTypeCode(invoiceObjectCode.getFinancialObjectTypeCode());
            creditGLPE_1.setFinancialBalanceTypeCode(actualsBalanceTypeCode);
            creditGLPE_1.setFinancialDocumentTypeCode(documentTypeCode);
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), debitGLPE_1, sequenceHelper, creditGLPE_1);
            entries.add(creditGLPE_1);
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry debitGLPE_2 = new GeneralLedgerPendingEntry();
            debitGLPE_2.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitGLPE_2.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            debitGLPE_2.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            debitGLPE_2.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            debitGLPE_2.setFinancialObjectCode(invoiceObjectCode.getFinancialObjectCode());
            debitGLPE_2.setFinancialObjectTypeCode(invoiceObjectCode.getFinancialObjectTypeCode());
            debitGLPE_2.setFinancialBalanceTypeCode(actualsBalanceTypeCode);
            debitGLPE_2.setFinancialDocumentTypeCode(documentTypeCode);
            debitGLPE_2.setDocumentType(documentType);
            debitGLPE_2.setUniversityFiscalYear(getPostingYear());
            entries.add(debitGLPE_2);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry creditGLPE_2 = new GeneralLedgerPendingEntry();
            creditGLPE_2.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditGLPE_2.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            creditGLPE_2.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            creditGLPE_2.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            creditGLPE_2.setFinancialObjectCode(accountsReceivableObjectCode.getFinancialObjectCode());
            creditGLPE_2.setFinancialObjectTypeCode(accountsReceivableObjectCode.getFinancialObjectTypeCode());
            creditGLPE_2.setUniversityFiscalYear(getPostingYear());
            creditGLPE_2.setFinancialBalanceTypeCode(actualsBalanceTypeCode);
            creditGLPE_2.setFinancialDocumentTypeCode(documentTypeCode);
            creditGLPE_2.refreshNonUpdateableReferences();
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), debitGLPE_2, sequenceHelper, creditGLPE_2);
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
            // This map structure should allow me to keep unique documents.a
            Map<String,Document> documents = new HashMap<String,Document>();
            for(InvoicePaidApplied ipa : getInvoicePaidApplieds()) {
                String invoiceDocumentNumber = ipa.getFinancialDocumentReferenceInvoiceNumber();
                try {
                    CustomerInvoiceDocument invoice = 
                        (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceDocumentNumber);
                    
                    // KULAR-384
                    invoice.setClosedDate(today);
                    invoice.setOpenInvoiceIndicator(false);
                    
                    documents.put(invoiceDocumentNumber,invoice);
                    
                    // Don't save the documents here. It'll cause OptimisticLockExceptions.
                    
                } catch(WorkflowException we) {
                    LOG.error("Failed to update closed date on Invoice.", we);
                }
            }
            
            // Save each document.
            for(Document d : documents.values()) {
                try {
                    documentService.saveDocument(d);
                } catch(WorkflowException we) {
                    LOG.error("Failed to update closed date on Invoice.", we);
                }
            }
        }
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // set primary key for NonAppliedHolding if data entered
        if (ObjectUtils.isNotNull(this.nonAppliedHolding)) {
            if (ObjectUtils.isNull(this.nonAppliedHolding.getReferenceFinancialDocumentNumber())) {
                this.nonAppliedHolding.setReferenceFinancialDocumentNumber(this.documentNumber);
            }
        }
        // generate GLPEs
        GeneralLedgerPendingEntryService generalLedgerPendingEntryService = 
            SpringContext.getBean(GeneralLedgerPendingEntryService.class); 
        if (!generalLedgerPendingEntryService.generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }
        super.prepareForSave(event);  
    }

    public PaymentApplicationDocumentService getPaymentApplicationDocumentService() {
        if(null == paymentApplicationDocumentService) {
            paymentApplicationDocumentService = SpringContext.getBean(PaymentApplicationDocumentService.class);
        }
        return paymentApplicationDocumentService;
    }

    public void setPaymentApplicationDocumentService(PaymentApplicationDocumentService paymentApplicationDocumentService) {
        this.paymentApplicationDocumentService = paymentApplicationDocumentService;
    }
    
}
