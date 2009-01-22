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
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.BalanceTypService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
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
import org.kuali.kfs.sys.service.GeneralLedgerInputTypeService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PaymentApplicationDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentApplicationDocument.class);

    private String hiddenFieldForErrors;
    private List<InvoicePaidApplied> invoicePaidApplieds;
    private List<NonInvoiced> nonInvoiceds;
    private Collection<NonInvoicedDistribution> nonInvoicedDistributions;
    private Collection<NonAppliedDistribution> nonAppliedDistributions;
    private NonAppliedHolding nonAppliedHolding;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private transient PaymentApplicationDocumentService paymentApplicationDocumentService;
    private transient CashControlDetail cashControlDetail;

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

    /**
     * @return
     * @throws WorkflowException
     */
    public CashControlDocument getCashControlDocument() throws WorkflowException {
        CashControlDocument cashControlDocument = 
            getPaymentApplicationDocumentService().getCashControlDocumentForPaymentApplicationDocument(this);
        return cashControlDocument;
    }

    public boolean hasCashControlDocument() {
        try {
            return (null != getCashControlDocument());
        }
        catch (WorkflowException e) {
            throw new RuntimeException("WorkflowException thrown when trying to retrieve CashControlDocument for this PayAppDocument.", e);
        }
    }
    
    /**
     * @return
     * @throws WorkflowException
     */
    public CashControlDetail getCashControlDetail() throws WorkflowException {
        if(null != cashControlDetail) {
            return cashControlDetail;
        } else {
            return getPaymentApplicationDocumentService().getCashControlDetailForPaymentApplicationDocument(this);
        }
    }
    
    public void setCashControlDetail(CashControlDetail cashControlDetail) {
        this.cashControlDetail = cashControlDetail;
    }
    
    /**
     * @return
     * @throws WorkflowException
     */
    public KualiDecimal getTotalFromCashControl() throws WorkflowException {
        CashControlDetail cashControlDetail = getCashControlDetail();
        return null == cashControlDetail ? KualiDecimal.ZERO : cashControlDetail.getFinancialDocumentLineAmount();
    }
    
    /**
     * @return the sum of all invoice paid applieds.
     */
    public KualiDecimal getSumOfInvoicePaidApplieds() {
        KualiDecimal amount = new KualiDecimal(0);
        for(InvoicePaidApplied payment : getInvoicePaidApplieds()) {
            amount = amount.add(payment.getInvoiceItemAppliedAmount());
        }
        return amount;
    }
    
    /**
     * @return the sum of all non-invoiced amounts
     */
    public KualiDecimal getSumOfNonInvoiceds() {
        KualiDecimal total = new KualiDecimal(0);
        for(NonInvoiced payment : getNonInvoiceds()) {
            total = total.add(payment.getFinancialDocumentLineAmount());
        }
        return total;
    }
    
    /**
     * @return the sum of all non-invoiced distributions
     */
    public KualiDecimal getSumOfNonInvoicedDistributions() {
        KualiDecimal amount = new KualiDecimal(0);
        for(NonInvoicedDistribution nonInvoicedDistribution : getNonInvoicedDistributions()) {
            amount = amount.add(nonInvoicedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }
    
    /**
     * @return the sum of all non-applied distributions
     */
    public KualiDecimal getSumOfNonAppliedDistributions() {
        KualiDecimal amount = new KualiDecimal(0);
        for(NonAppliedDistribution nonAppliedDistribution : getNonAppliedDistributions()) {
            amount = amount.add(nonAppliedDistribution.getFinancialDocumentLineAmount());
        }
        return amount;
    }
    
    /**
     * @return the non-applied holding total.
     */
    public KualiDecimal getNonAppliedHoldingAmount() {
        return null == nonAppliedHolding ? KualiDecimal.ZERO : nonAppliedHolding.getFinancialDocumentLineAmount();
    }
    
    /**
     * This method returns the total amount allocated against the cash
     * control total.
     * 
     * @return
     */
    public KualiDecimal getTotalApplied() {
        KualiDecimal amount = KualiDecimal.ZERO;
        try {
            // The amount received via the cash control document
            KualiDecimal ccta = getTotalFromCashControl();
            
            // The amount received via the cash control document minus the amount applied.
            KualiDecimal btba = getUnallocatedBalance();
            
            // The difference between the two is the amount applied.
            amount = ccta.subtract(btba);
        } catch(WorkflowException w) {
            LOG.error("Failed to calculate total applied amount.", w);
        }
        return amount;
    }
    
    /**
     * This method subtracts the sum of the invoice paid applieds, non-ar and 
     * unapplied totals from the outstanding amount received via the cash
     * control document.
     * 
     * @return
     * @throws WorkflowException
     */
    public KualiDecimal getUnallocatedBalance() throws WorkflowException {
        
        //  if this payapp doc isnt based on a cash control doc, then there 
        // will be no cash control details, so no balance to be applied
        CashControlDetail cashControlDetail = getCashControlDetail();
        if (cashControlDetail == null) {
            return KualiDecimal.ZERO;
        }
        
        // KULAR-504: "Balance" must equal line item amount from Cash Control - not the total Cash Control
        KualiDecimal amount = getCashControlDetail().getFinancialDocumentLineAmount();
        
        KualiDecimal subtrahend = getSumOfInvoicePaidApplieds();
        if(ObjectUtils.isNotNull(subtrahend)) {
            amount = amount.subtract(subtrahend);
        }
        subtrahend = getSumOfNonInvoiceds();
        if(ObjectUtils.isNotNull(subtrahend)) {
            amount = amount.subtract(subtrahend);
        }
        subtrahend = getNonAppliedHoldingAmount();
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
     * @param ipa
     * @return
     */
    private SystemInformation getSystemInformation(InvoicePaidApplied ipa) throws WorkflowException {
        CustomerInvoiceDocument invoice = ipa.getCustomerInvoiceDocument();
        String processingOrgCode = invoice.getAccountsReceivableDocumentHeader().getProcessingOrganizationCode();
        String processingChartCode = invoice.getAccountsReceivableDocumentHeader().getProcessingChartOfAccountCode();
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        SystemInformation systemInformation = 
            SpringContext.getBean(SystemInformationService.class).getByProcessingChartOrgAndFiscalYear(processingChartCode, processingOrgCode, currentFiscalYear);
        return systemInformation;
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
        return cashControlDocument;
    }
    
    /**
     * @param sequenceHelper
     * @return the pending entries for the document
     */
    private List<GeneralLedgerPendingEntry> createPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) throws WorkflowException {
        
        // Collection of all generated entries
        List<GeneralLedgerPendingEntry> generatedEntries = new ArrayList<GeneralLedgerPendingEntry>();
        
        // Get handles to the services we need
        GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        GeneralLedgerInputTypeService generalLedgerInputTypeService = SpringContext.getBean(GeneralLedgerInputTypeService.class);
        BalanceTypService balanceTypeService = SpringContext.getBean(BalanceTypService.class);
        UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
        SystemInformationService systemInformationService = SpringContext.getBean(SystemInformationService.class);
        OffsetDefinitionService offsetDefinitionService = SpringContext.getBean(OffsetDefinitionService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        
        // Current fiscal year
        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        
        // Document type codes
        String cashControlDocumentTypeCode = generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(CashControlDocument.class).getInputTypeCode();
        String paymentApplicationDocumentTypeCode = generalLedgerInputTypeService.getGeneralLedgerInputTypeByDocumentClass(PaymentApplicationDocument.class).getInputTypeCode(); 
        
        // The processing chart and org comes from the current user.
        // It will be the same as the chart and org on the cash control document if there is one.
        // If the payment application document is created from scratch though it's not easy to get 
        // an appropriate cash control document. So we just take it from the current user instead.
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        String usersDepartment = currentUser.getPrimaryDepartmentCode();
        String[] deptParts = usersDepartment.split("-");
        String processingChartCode = deptParts[0];
        String processingOrganizationCode = deptParts[1];

        // Some information comes from the cash control document
        CashControlDocument cashControlDocument = getCashControlDocument(this);
        
        // Get the university clearing account
        SystemInformation unappliedSystemInformation = 
            systemInformationService.getByProcessingChartOrgAndFiscalYear(
                    processingChartCode, processingOrganizationCode, currentFiscalYear);
        
        // Get the university clearing account
        unappliedSystemInformation.refreshReferenceObject("universityClearingAccount");
        Account universityClearingAccount = unappliedSystemInformation.getUniversityClearingAccount();
        
        // Get the university clearing sub-object code
        String unappliedSubObjectCode = unappliedSystemInformation.getUniversityClearingSubObjectCode();
        
        // Get the object code for the university clearing account.
        SystemInformation universityClearingAccountSystemInformation = 
            systemInformationService.getByProcessingChartOrgAndFiscalYear(
                    processingChartCode, processingOrganizationCode, currentFiscalYear);
        String universityClearingAccountObjectCode = universityClearingAccountSystemInformation.getUniversityClearingObjectCode();
        
        // Generate glpes for unapplied
        NonAppliedHolding holding = getNonAppliedHolding();
        if(ObjectUtils.isNotNull(holding)) {
            GeneralLedgerPendingEntry actualCreditUnapplied = new GeneralLedgerPendingEntry();
            actualCreditUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            actualCreditUnapplied.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualCreditUnapplied.setAccountNumber(universityClearingAccount.getAccountNumber());
            actualCreditUnapplied.setFinancialObjectCode(unappliedSystemInformation.getUniversityClearingObjectCode());
            actualCreditUnapplied.setTransactionLedgerEntryAmount(holding.getFinancialDocumentLineAmount());
            actualCreditUnapplied.setFinancialSubObjectCode(unappliedSubObjectCode);
            if(ObjectUtils.isNull(actualCreditUnapplied.getFinancialObject())) {
                boolean breakFour = true;
            } else {
                actualCreditUnapplied.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(actualCreditUnapplied);
            
            GeneralLedgerPendingEntry actualDebitUnapplied = new GeneralLedgerPendingEntry();
            actualDebitUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            actualDebitUnapplied.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            actualDebitUnapplied.setAccountNumber(universityClearingAccount.getAccountNumber());
            actualDebitUnapplied.setFinancialObjectCode(universityClearingAccountObjectCode);
            actualDebitUnapplied.setTransactionLedgerEntryAmount(holding.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(actualDebitUnapplied.getFinancialObject())) {
                boolean breakThree = true;
            } else {
                actualDebitUnapplied.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(actualDebitUnapplied);
            
            Integer fiscalYearForUnappliedOffsetDefinition = null == cashControlDocument ? currentFiscalYear : cashControlDocument.getPostingYear();
            OffsetDefinition unappliedOffsetDefinition = 
                offsetDefinitionService.getByPrimaryId(
                        fiscalYearForUnappliedOffsetDefinition, processingChartCode, 
                        cashControlDocumentTypeCode, ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            
            GeneralLedgerPendingEntry offsetCreditUnapplied = new GeneralLedgerPendingEntry();
            offsetCreditUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            offsetCreditUnapplied.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            offsetCreditUnapplied.setAccountNumber(universityClearingAccount.getAccountNumber());
            offsetCreditUnapplied.setFinancialObjectCode(unappliedOffsetDefinition.getFinancialObjectCode());
            offsetCreditUnapplied.setTransactionLedgerEntryAmount(holding.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(offsetCreditUnapplied.getFinancialObject())) {
                boolean breakOne = true;
            } else {
                offsetCreditUnapplied.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(offsetCreditUnapplied);
            
            GeneralLedgerPendingEntry offsetDebitUnapplied = new GeneralLedgerPendingEntry();
            offsetDebitUnapplied.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offsetDebitUnapplied.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            offsetDebitUnapplied.setAccountNumber(universityClearingAccount.getAccountNumber());
            offsetDebitUnapplied.setFinancialObjectCode(unappliedOffsetDefinition.getFinancialObjectCode());
            offsetDebitUnapplied.setTransactionLedgerEntryAmount(holding.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(offsetDebitUnapplied.getFinancialObject())) {
                boolean breakTwo = true;
            } else {
                offsetDebitUnapplied.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(offsetDebitUnapplied);
        }
        
        // Generate glpes for non-ar
        for(NonInvoiced nonInvoiced : getNonInvoiceds()) {
            // Actual entries
            GeneralLedgerPendingEntry creditEntryOne = new GeneralLedgerPendingEntry();
            creditEntryOne.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditEntryOne.setChartOfAccountsCode(nonInvoiced.getChartOfAccountsCode());
            creditEntryOne.setAccountNumber(nonInvoiced.getAccountNumber());
            creditEntryOne.setFinancialObjectCode(nonInvoiced.getFinancialObjectCode());
            creditEntryOne.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(creditEntryOne.getFinancialObject())) {
                boolean breakFive = true;
            } else {
                creditEntryOne.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(creditEntryOne);
            
            GeneralLedgerPendingEntry debitEntryTwo = new GeneralLedgerPendingEntry();
            debitEntryTwo.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitEntryTwo.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            debitEntryTwo.setAccountNumber(universityClearingAccount.getAccountNumber());
            debitEntryTwo.setFinancialObjectCode(universityClearingAccountObjectCode);
            debitEntryTwo.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(debitEntryTwo.getFinancialObject())) {
                boolean breakSix = true;
            } else {
                debitEntryTwo.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(debitEntryTwo);

            // Offset entries
            GeneralLedgerPendingEntry debitEntryOne = new GeneralLedgerPendingEntry();
            debitEntryOne.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitEntryOne.setChartOfAccountsCode(nonInvoiced.getChartOfAccountsCode());
            debitEntryOne.setAccountNumber(nonInvoiced.getAccountNumber());
            
            OffsetDefinition debitOffsetDefinition = 
                offsetDefinitionService.getByPrimaryId(
                    getPostingYear(), nonInvoiced.getChartOfAccountsCode(), 
                    paymentApplicationDocumentTypeCode, ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            debitOffsetDefinition.refreshReferenceObject("financialObject");
            debitEntryOne.setFinancialObjectCode(debitOffsetDefinition.getFinancialObjectCode());
            debitEntryOne.setFinancialObjectTypeCode(debitOffsetDefinition.getFinancialObject().getFinancialObjectTypeCode());
            debitEntryOne.setFinancialBalanceTypeCode(ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            debitEntryOne.setFinancialDocumentTypeCode(paymentApplicationDocumentTypeCode);
            debitEntryOne.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(debitEntryOne.getFinancialObject())) {
                boolean breakSeven = true;
            } else {
                debitEntryOne.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(debitEntryOne);
            
            GeneralLedgerPendingEntry creditEntryTwo = new GeneralLedgerPendingEntry();
            creditEntryTwo.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditEntryTwo.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            creditEntryTwo.setAccountNumber(universityClearingAccount.getAccountNumber());
            Integer fiscalYearForCreditOffsetDefinition = null == cashControlDocument ? currentFiscalYear : cashControlDocument.getUniversityFiscalYear();
            OffsetDefinition creditOffsetDefinition = 
                offsetDefinitionService.getByPrimaryId(
                        fiscalYearForCreditOffsetDefinition, processingChartCode, 
                        cashControlDocumentTypeCode, ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            creditOffsetDefinition.refreshReferenceObject("financialObject");
            creditEntryTwo.setFinancialObjectCode(creditOffsetDefinition.getFinancialObjectCode());
            creditEntryTwo.setFinancialObjectTypeCode(creditOffsetDefinition.getFinancialObject().getFinancialObjectTypeCode());
            creditEntryTwo.setTransactionLedgerEntryAmount(nonInvoiced.getFinancialDocumentLineAmount());
            if(ObjectUtils.isNull(creditEntryTwo.getFinancialObject())) {
                boolean breakEight = true;
            } else {
                creditEntryTwo.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(creditEntryTwo);
            
        }
        
        // Generate GLPEs for applied payments
        List<InvoicePaidApplied> appliedPayments = getInvoicePaidApplieds();
        for(InvoicePaidApplied ipa : appliedPayments) {
            
            // Skip payments for 0 dollar amount
            if(KualiDecimal.ZERO.equals(ipa.getInvoiceItemAppliedAmount())) {
                continue;
            }
            
            ipa.refreshNonUpdateableReferences();
            Account billingOrganizationAccount = getBillingOrganizationAccount(ipa);
            ObjectCode invoiceObjectCode = getInvoiceObjectCode(ipa);
            ObjectUtils.isNull(invoiceObjectCode); // Refresh 
            ObjectCode accountsReceivableObjectCode = getAccountsReceivableObjectCode(ipa);
            ObjectCode unappliedCashObjectCode = getUnappliedCashObjectCode(ipa);
            
            GeneralLedgerPendingEntry debitGLPE_1 = new GeneralLedgerPendingEntry();
            debitGLPE_1.setUniversityFiscalYear(getPostingYear());
            debitGLPE_1.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            debitGLPE_1.setAccountNumber(universityClearingAccount.getAccountNumber());
            debitGLPE_1.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            debitGLPE_1.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            debitGLPE_1.setFinancialObjectCode(unappliedCashObjectCode.getFinancialObjectCode());
            debitGLPE_1.setFinancialObjectTypeCode(unappliedCashObjectCode.getFinancialObjectTypeCode());
            debitGLPE_1.setFinancialBalanceTypeCode(ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            debitGLPE_1.setFinancialDocumentTypeCode(paymentApplicationDocumentTypeCode);
            if(ObjectUtils.isNull(debitGLPE_1.getFinancialObject())) {
                boolean breakNine = true;
            } else {
                debitGLPE_1.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(debitGLPE_1);
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry creditGLPE_1 = new GeneralLedgerPendingEntry();
            creditGLPE_1.setUniversityFiscalYear(getPostingYear());
            creditGLPE_1.setChartOfAccountsCode(universityClearingAccount.getChartOfAccountsCode());
            creditGLPE_1.setAccountNumber(universityClearingAccount.getAccountNumber());
            creditGLPE_1.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            creditGLPE_1.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            creditGLPE_1.setFinancialObjectCode(invoiceObjectCode.getFinancialObjectCode());
            creditGLPE_1.setFinancialObjectTypeCode(invoiceObjectCode.getFinancialObjectTypeCode());
            creditGLPE_1.setFinancialBalanceTypeCode(ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            creditGLPE_1.setFinancialDocumentTypeCode(paymentApplicationDocumentTypeCode);
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), debitGLPE_1, sequenceHelper, creditGLPE_1);
            if(ObjectUtils.isNull(creditGLPE_1.getFinancialObject())) {
                boolean breakTen = true;
            } else {
                creditGLPE_1.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(creditGLPE_1);
            sequenceHelper.increment();
            
            GeneralLedgerPendingEntry debitGLPE_2 = new GeneralLedgerPendingEntry();
            debitGLPE_2.setUniversityFiscalYear(getPostingYear());
            debitGLPE_2.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            debitGLPE_2.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            debitGLPE_2.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            debitGLPE_2.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            debitGLPE_2.setFinancialObjectCode(invoiceObjectCode.getFinancialObjectCode());
            debitGLPE_2.setFinancialObjectTypeCode(invoiceObjectCode.getFinancialObjectTypeCode());
            debitGLPE_2.setFinancialBalanceTypeCode(ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            debitGLPE_2.setFinancialDocumentTypeCode(paymentApplicationDocumentTypeCode);
            if(ObjectUtils.isNull(debitGLPE_2.getFinancialObject())) {
                boolean breakEleven = true;
            } else {
                debitGLPE_2.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(debitGLPE_2);
            sequenceHelper.increment();

            GeneralLedgerPendingEntry creditGLPE_2 = new GeneralLedgerPendingEntry();
            creditGLPE_2.setUniversityFiscalYear(getPostingYear());
            creditGLPE_2.setAccountNumber(billingOrganizationAccount.getAccountNumber());
            creditGLPE_2.setChartOfAccountsCode(billingOrganizationAccount.getChartOfAccountsCode());
            creditGLPE_2.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            creditGLPE_2.setTransactionLedgerEntryAmount(ipa.getInvoiceItemAppliedAmount());
            creditGLPE_2.setFinancialObjectCode(accountsReceivableObjectCode.getFinancialObjectCode());
            creditGLPE_2.setFinancialObjectTypeCode(accountsReceivableObjectCode.getFinancialObjectTypeCode());
            creditGLPE_2.setFinancialBalanceTypeCode(ArConstants.ACTUALS_BALANCE_TYPE_CODE);
            creditGLPE_2.setFinancialDocumentTypeCode(paymentApplicationDocumentTypeCode);
            creditGLPE_2.refreshNonUpdateableReferences();
            glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), debitGLPE_2, sequenceHelper, creditGLPE_2);
            if(ObjectUtils.isNull(creditGLPE_2.getFinancialObject())) {
                boolean breakTwelve = true;
            } else {
                creditGLPE_2.getFinancialObject().refreshNonUpdateableReferences();
            }
            generatedEntries.add(creditGLPE_2);
            sequenceHelper.increment();
        }
        
        return generatedEntries;
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
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
            
            //  get the now time to stamp invoices with
            java.sql.Date today = new java.sql.Date(dateTimeService.getCurrentDate().getTime());
            
            for(InvoicePaidApplied ipa : getInvoicePaidApplieds()) {
                String invoiceDocumentNumber = ipa.getFinancialDocumentReferenceInvoiceNumber();
                CustomerInvoiceDocument invoice = null;
                
                //  attempt to retrieve the invoice doc
                try {
                     invoice = (CustomerInvoiceDocument) documentService.getByDocumentHeaderId(invoiceDocumentNumber);
                } catch(WorkflowException we) {
                    LOG.error("Failed to load the Invoice document due to a WorkflowException.", we);
                }
                if (invoice == null) {
                    throw new RuntimeException("DocumentService returned a Null CustomerInvoice Document for Doc# " + invoiceDocumentNumber + ".");
                }
                
                // KULAR-384 - close the invoice if the open amount is zero
                if (KualiDecimal.ZERO.equals(invoice.getOpenAmount())) {
                    invoice.setClosedDate(today);
                    invoice.setOpenInvoiceIndicator(false);
                    documentService.updateDocument(invoice);
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

    public String getHiddenFieldForErrors() {
        return hiddenFieldForErrors;
    }

    public void setHiddenFieldForErrors(String hiddenFieldForErrors) {
        this.hiddenFieldForErrors = hiddenFieldForErrors;
    }

}
