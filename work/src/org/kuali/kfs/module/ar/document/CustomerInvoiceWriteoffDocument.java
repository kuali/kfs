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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.SalesTaxCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.WriteoffCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.WriteoffTaxCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableTaxService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.TaxService;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

public class CustomerInvoiceWriteoffDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource, AmountTotaling {

    private static final String REQUIRES_APPROVAL_NODE = "RequiresApproval";
    private static final KualiDecimal WRITEOFF_DFLT_APPRVL_AMOUNT = new KualiDecimal(50);
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String projectCode;
    private String organizationReferenceIdentifier;
    private String financialDocumentReferenceInvoiceNumber;
    private String statusCode;

    private String customerNote;

    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private ObjectCode financialObject;
    private SubObjectCode financialSubObject;
    private ProjectCode project;
    private CustomerInvoiceDocument customerInvoiceDocument;
    private AccountsReceivableDocumentHeader accountsReceivableDocumentHeader;
    private KualiDecimal invoiceWriteoffAmount;

    public AccountsReceivableDocumentHeader getAccountsReceivableDocumentHeader() {
        return accountsReceivableDocumentHeader;
    }

    public void setAccountsReceivableDocumentHeader(AccountsReceivableDocumentHeader accountsReceivableDocumentHeader) {
        this.accountsReceivableDocumentHeader = accountsReceivableDocumentHeader;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public void setProjectCode(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getOrganizationReferenceIdentifier() {
        return organizationReferenceIdentifier;
    }

    public void setOrganizationReferenceIdentifier(String organizationReferenceIdentifier) {
        this.organizationReferenceIdentifier = organizationReferenceIdentifier;
    }

    public String getFinancialDocumentReferenceInvoiceNumber() {
        return financialDocumentReferenceInvoiceNumber;
    }

    public void setFinancialDocumentReferenceInvoiceNumber(String financialDocumentReferenceInvoiceNumber) {
        this.financialDocumentReferenceInvoiceNumber = financialDocumentReferenceInvoiceNumber;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Chart getChartOfAccounts() {
        return chartOfAccounts;
    }

    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    public SubAccount getSubAccount() {
        return subAccount;
    }

    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public ObjectCode getFinancialObject() {
        return financialObject;
    }

    public void setFinancialObject(ObjectCode financialObject) {
        this.financialObject = financialObject;
    }

    public SubObjectCode getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjectCode financialSubObject) {
        this.financialSubObject = financialSubObject;
    }

    public ProjectCode getProject() {
        return project;
    }

    public void setProject(ProjectCode project) {
        this.project = project;
    }

    public CustomerInvoiceDocument getCustomerInvoiceDocument() {
        if (ObjectUtils.isNull(customerInvoiceDocument) && StringUtils.isNotEmpty(financialDocumentReferenceInvoiceNumber)) {
            refreshReferenceObject("customerInvoiceDocument");
        }

        return customerInvoiceDocument;
    }

    public void setCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        this.customerInvoiceDocument = customerInvoiceDocument;
    }

    /**
     * This method returns all the applicable invoice details for writeoff. This method also sets the writeoff document number on
     * each of the invoice details for making retrieval of the actual writeoff amount easier.
     * 
     * @return
     */
    public List<CustomerInvoiceDetail> getCustomerInvoiceDetailsForWriteoff() {
        List<CustomerInvoiceDetail> customerInvoiceDetailsForWriteoff = new TypedArrayList(CustomerInvoiceDetail.class);
        for (CustomerInvoiceDetail customerInvoiceDetail : getCustomerInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts()) {
            customerInvoiceDetail.setCustomerInvoiceWriteoffDocumentNumber(this.documentNumber);
            customerInvoiceDetailsForWriteoff.add(customerInvoiceDetail);
        }

        return customerInvoiceDetailsForWriteoff;
    }

    /**
     * This method returns the total amount to be written off
     * 
     * @return
     */
    public KualiDecimal getInvoiceWriteoffAmount() {
        if (ObjectUtils.isNull(invoiceWriteoffAmount) && ObjectUtils.isNotNull(customerInvoiceDocument)) {
            invoiceWriteoffAmount = customerInvoiceDocument.getOpenAmount();
        }
        return invoiceWriteoffAmount;
    }

    public void setInvoiceWriteoffAmount(KualiDecimal invoiceWriteoffAmount) {
        this.invoiceWriteoffAmount = invoiceWriteoffAmount;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Initializes the values for a new document.
     */
    public void initiateDocument() {
        setStatusCode(ArConstants.CustomerInvoiceWriteoffStatuses.INITIATE);
    }

    /**
     * Clear out the initially populated fields.
     */
    public void clearInitFields() {
        setFinancialDocumentReferenceInvoiceNumber(null);
    }

    /**
     * When document is processed do the following: 1) Apply amounts to writeoff invoice 2) Mark off invoice indiciator
     * 
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {

            // apply writeoff amounts by only retrieving only the invoice details that ARE NOT discounts
            SpringContext.getBean(InvoicePaidAppliedService.class).saveInvoicePaidApplieds(this.customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts(), documentNumber);
            KualiDecimal totalAppliedByCustomerInvoiceWriteoffDocument = SpringContext.getBean(InvoicePaidAppliedService.class).getTotalAmountApplied(this.customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts());
            SpringContext.getBean(CustomerInvoiceDocumentService.class).closeCustomerInvoiceDocumentIfFullyPaidOff(customerInvoiceDocument, totalAppliedByCustomerInvoiceWriteoffDocument);
        }
    }

    /**
     * do all the calculations before the document gets saved gets called for 'Submit', 'Save', and 'Blanket Approved'
     * 
     * @see org.kuali.rice.kns.document.Document#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    public void prepareForSave(KualiDocumentEvent event) {
        // generate GLPEs
        if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }
        super.prepareForSave(event);
    }

    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    protected KualiDecimal getCustomerInvoiceDetailOpenPretaxAmount(CustomerInvoiceDetail customerInvoiceDetail) {
        String postalCode = SpringContext.getBean(AccountsReceivableTaxService.class).getPostalCodeForTaxation(getCustomerInvoiceDocument());
        Date dateOfTransaction = getCustomerInvoiceDocument().getBillingDate();
        KualiDecimal pretaxAmount = SpringContext.getBean(TaxService.class).getPretaxAmount(dateOfTransaction, postalCode, customerInvoiceDetail.getAmountOpen());

        return pretaxAmount;
    }

    /**
     * This method creates the following GLPE's for the customer invoice writeoff 1. C Receivable object code with remaining amount
     * 2. D Writeoff object code (or Writeoff FAU) with remaining amount 3. C Receivable object code for tax on state tax account 4.
     * D Sales Tax object code for tax on the state tax account 5. C Receivable object code for tax on district tax account 6. D
     * District tax object code for $1.00 on the district tax account
     * 
     * @see org.kuali.kfs.service.impl.GenericGeneralLedgerPendingEntryGenerationProcessImpl#processGenerateGeneralLedgerPendingEntries(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) glpeSourceDetail;

        // if invoice item open amount <= 0 -> do not generate GLPEs for this glpeSourceDetail
        if (!customerInvoiceDetail.getAmountOpen().isPositive())
            return true;

        KualiDecimal amount;

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean hasReceivableClaimOnCashOffset = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);

        String writeoffOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        boolean hasWriteoffClaimOnCashOffset = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffOffsetOption);

        String writeoffTaxGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_TAX_GENERATION_METHOD);
        boolean hasWriteoffTaxClaimOnCashOffset = ArConstants.GLPE_WRITEOFF_TAX_GENERATION_METHOD_DISALLOW.equals( writeoffTaxGenerationOption );

        boolean hasClaimOnCashOffset = hasReceivableClaimOnCashOffset || hasWriteoffClaimOnCashOffset;

        // if sales tax is enabled generate tax GLPEs
        if (SpringContext.getBean(AccountsReceivableTaxService.class).isCustomerInvoiceDetailTaxable(getCustomerInvoiceDocument(), customerInvoiceDetail)) {
            amount = getCustomerInvoiceDetailOpenPretaxAmount(customerInvoiceDetail);
            addReceivableGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset, amount);
            sequenceHelper.increment();
            addWriteoffGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset, amount);
            addSalesTaxGLPEs( sequenceHelper, glpeSourceDetail, hasWriteoffTaxClaimOnCashOffset, amount);
        }
        else {
            amount = customerInvoiceDetail.getAmountOpen();
            addReceivableGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset, amount);
            sequenceHelper.increment();
            addWriteoffGLPEs(sequenceHelper, glpeSourceDetail, hasClaimOnCashOffset, amount);
        }
        return true;
    }

    /**
     * This method creates the receivable GLPEs for customer invoice details using the remaining amount
     * 
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addReceivableGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset, KualiDecimal amount) {

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) glpeSourceDetail;
        ReceivableCustomerInvoiceDetail receivableCustomerInvoiceDetail = new ReceivableCustomerInvoiceDetail(customerInvoiceDetail, getCustomerInvoiceDocument());
        boolean isDebit = false;

        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, receivableCustomerInvoiceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, amount);
    }

    /**
     * This method adds writeoff GLPE's for the customer invoice details using the remaining amount.
     * 
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addWriteoffGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset, KualiDecimal amount) {

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) glpeSourceDetail;
        WriteoffCustomerInvoiceDetail writeoffCustomerInvoiceDetail = new WriteoffCustomerInvoiceDetail(customerInvoiceDetail, this);
        boolean isDebit = true;

        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, writeoffCustomerInvoiceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, amount);
    }

    protected void addSalesTaxGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasWriteoffTaxClaimOnCashOffset, KualiDecimal amount){
        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail) glpeSourceDetail;
        WriteoffCustomerInvoiceDetail writeoffCustomerInvoiceDetail = new WriteoffCustomerInvoiceDetail(customerInvoiceDetail, this);

        boolean isDebit = true;

        String postalCode = SpringContext.getBean(AccountsReceivableTaxService.class).getPostalCodeForTaxation(getCustomerInvoiceDocument());
        Date dateOfTransaction = getCustomerInvoiceDocument().getBillingDate();

        List<TaxDetail> salesTaxDetails = SpringContext.getBean(TaxService.class).getSalesTaxDetails(dateOfTransaction, postalCode, amount);

        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        SalesTaxCustomerInvoiceDetail salesTaxCustomerInvoiceDetail;
        ReceivableCustomerInvoiceDetail receivableCustomerInvoiceDetail;
        WriteoffTaxCustomerInvoiceDetail writeoffTaxCustomerInvoiceDetail;
        for (TaxDetail salesTaxDetail : salesTaxDetails) {

            salesTaxCustomerInvoiceDetail = new SalesTaxCustomerInvoiceDetail(salesTaxDetail, customerInvoiceDetail);
            receivableCustomerInvoiceDetail = new ReceivableCustomerInvoiceDetail(salesTaxCustomerInvoiceDetail, getCustomerInvoiceDocument());
            writeoffTaxCustomerInvoiceDetail = new WriteoffTaxCustomerInvoiceDetail(salesTaxCustomerInvoiceDetail, this);

            sequenceHelper.increment();
            service.createAndAddGenericInvoiceRelatedGLPEs(this, receivableCustomerInvoiceDetail, sequenceHelper, !isDebit, hasWriteoffTaxClaimOnCashOffset, salesTaxDetail.getTaxAmount());

            sequenceHelper.increment();
            service.createAndAddGenericInvoiceRelatedGLPEs(this, writeoffTaxCustomerInvoiceDetail, sequenceHelper, isDebit, hasWriteoffTaxClaimOnCashOffset, salesTaxDetail.getTaxAmount());
        }
    }

    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> generalLedgerPendingEntrySourceDetails = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        generalLedgerPendingEntrySourceDetails.addAll(getCustomerInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts());
        return generalLedgerPendingEntrySourceDetails;

    }

    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        // TODO Auto-generated method stub
        return false;
    }

    public KualiDecimal getTotalDollarAmount() {
        return getInvoiceWriteoffAmount();
    }

    /**
     * Gets the customerNote attribute.
     * 
     * @return Returns the customerNote.
     */
    public String getCustomerNote() {
        /*
         * ArrayList boNotes = (ArrayList) this.getCustomerInvoiceDocument().getCustomer().getBoNotes(); if (boNotes.size() > 0)
         * customerNote = boNotes.toString(); else customerNote = "";
         */
        return customerNote;
    }

    /**
     * Sets the customerNote attribute value.
     * 
     * @param customerNote The customerNote to set.
     */
    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public void populateCustomerNote() {
        customerNote = "";
        ArrayList boNotes = (ArrayList) this.getCustomerInvoiceDocument().getCustomer().getBoNotes();
        if (boNotes.size() > 0) {
            for (int i = 0; i < boNotes.size(); i++)
                customerNote += ((Note) boNotes.get(i)).getNoteText() + " ";
            customerNote.trim();
        }
    }

    /**
     * Answers true when invoice write off amount is greater than default approved amount ($50???)
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        // FIXME add a new system parameter for WRITEOFF_DFLT_APPRVL_AMOUNT
        if (REQUIRES_APPROVAL_NODE.equals(nodeName) && WRITEOFF_DFLT_APPRVL_AMOUNT.isLessThan(getInvoiceWriteoffAmount())) {
            return true;
        }
        return false;
    }


}
