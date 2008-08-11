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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.ReceivableCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.WriteoffCustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceGLPEService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.ParameterService;

public class CustomerInvoiceWriteoffDocument extends GeneralLedgerPostingDocumentBase implements GeneralLedgerPendingEntrySource, AmountTotaling {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String projectCode;
    private String organizationReferenceIdentifier;
    private String financialDocumentReferenceInvoiceNumber;
    private String statusCode;

    private Account account;
    private Chart chartOfAccounts;
    private SubAccount subAccount;
    private ObjectCode financialObject;
    private SubObjCd financialSubObject;
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

    public SubObjCd getFinancialSubObject() {
        return financialSubObject;
    }

    public void setFinancialSubObject(SubObjCd financialSubObject) {
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
     * This method returns all the applicable invoice details for writeoff.  This method also
     * sets the writeoff document number on each of the invoice details for making 
     * retrieval of the actual writeoff amount easier.
     * 
     * @return
     */
    public List<CustomerInvoiceDetail> getCustomerInvoiceDetailsForWriteoff(){
        List<CustomerInvoiceDetail> customerInvoiceDetailsForWriteoff = new ArrayList<CustomerInvoiceDetail>();
        for( CustomerInvoiceDetail customerInvoiceDetail : getCustomerInvoiceDocument().getCustomerInvoiceDetailsWithoutDiscounts() ){
            customerInvoiceDetail.setCustomerInvoiceWriteoffDocumentNumber(this.documentNumber);
            customerInvoiceDetailsForWriteoff.add(customerInvoiceDetail);
        }
        
        return customerInvoiceDetailsForWriteoff;
    }

    /**
     * This method returns the total amount to be written off
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
     * When document is processed do the following:
     * 
     * 1) Apply amounts to writeoff invoice
     * 2) Mark off invoice indiciator
     *
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange(){
        super.handleRouteStatusChange();
        if (getDocumentHeader().getWorkflowDocument().stateIsProcessed()) {
            
            // apply writeoff amounts by only retrieving only the invoice details that ARE NOT discounts
            SpringContext.getBean(InvoicePaidAppliedService.class).saveInvoicePaidApplieds(this.customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts(), documentNumber);
            //SpringContext.getBean(CustomerInvoiceDocumentService.class).closeCustomerInvoiceDocumentIfFullyPaidOff(customerInvoiceDocument);
        }
    }    
    
    /**
     * do all the calculations before the document gets saved
     * gets called for 'Submit', 'Save', and 'Blanket Approved'
     * @see org.kuali.core.document.Document#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
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

    /**
     * This method creates the following GLPE's for the customer invoice writeoff
     *
     * 1. C Receivable object code with remaining amount
     * 2. D Writeoff object code (or Writeoff FAU) with remaining amount
     * 3. C Receivable object code for tax on state tax account
     * 4. D Sales Tax object code for tax on the state tax account
     * 5. C Receivable object code for tax on district tax account
     * 6. D District tax object code for $1.00 on the district tax account
     *
     * @see org.kuali.kfs.service.impl.GenericGeneralLedgerPendingEntryGenerationProcessImpl#processGenerateGeneralLedgerPendingEntries(org.kuali.kfs.sys.document.GeneralLedgerPendingEntrySource, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */

    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean hasReceivableClaimOnCashOffset = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        
        String writeoffOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        boolean hasWriteoffClaimOnCashOffset = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffOffsetOption);
        
        boolean hasStateSalesTax = false;
        boolean hasDistrictSalesTax = false;
        
        //if the writeoff 
        addReceivableGLPEs(sequenceHelper, glpeSourceDetail, hasReceivableClaimOnCashOffset || hasWriteoffClaimOnCashOffset);
        sequenceHelper.increment();
        addWriteoffGLPEs(sequenceHelper, glpeSourceDetail, hasReceivableClaimOnCashOffset || hasWriteoffClaimOnCashOffset);
        
        if( hasStateSalesTax ){
            sequenceHelper.increment();
            addStateSalesTaxGLPEs(sequenceHelper, glpeSourceDetail, hasWriteoffClaimOnCashOffset);
        }
        
        if( hasDistrictSalesTax ){
            sequenceHelper.increment();
            addDistrictSalesTaxGLPEs(sequenceHelper, glpeSourceDetail, hasWriteoffClaimOnCashOffset);
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
    protected void addReceivableGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset) {

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail)glpeSourceDetail;
        ReceivableCustomerInvoiceDetail receivableCustomerInvoiceDetail = new ReceivableCustomerInvoiceDetail(customerInvoiceDetail, getCustomerInvoiceDocument());
        boolean isDebit = false;       
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, receivableCustomerInvoiceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, customerInvoiceDetail.getOpenAmount());
    }
    
    /**
     * This method adds writeoff GLPE's for the customer invoice details using the remaining amount.
     *
     * @param poster
     * @param sequenceHelper
     * @param postable
     * @param explicitEntry
     */
    protected void addWriteoffGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset) {

        CustomerInvoiceDetail customerInvoiceDetail = (CustomerInvoiceDetail)glpeSourceDetail;
        WriteoffCustomerInvoiceDetail writeoffCustomerInvoiceDetail = new WriteoffCustomerInvoiceDetail(customerInvoiceDetail, this);
        boolean isDebit = true;
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createAndAddGenericInvoiceRelatedGLPEs(this, writeoffCustomerInvoiceDetail, sequenceHelper, isDebit, hasClaimOnCashOffset, customerInvoiceDetail.getOpenAmount());
    }
    
    protected void addStateSalesTaxGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset){
        /*
        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;   
        boolean isDebit = false;
        KualiDecimal creditMemoDetailStateTaxAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount().multiply(getStateTaxPercent());
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createStateSalesTaxGLPEs(this, customerCreditMemoDetail, sequenceHelper, isDebit, hasOffset, creditMemoDetailStateTaxAmount);
        //Add state sales tax receivable too
        */
    }
    
    protected void addDistrictSalesTaxGLPEs(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, boolean hasClaimOnCashOffset){
        /*
        CustomerCreditMemoDetail customerCreditMemoDetail = (CustomerCreditMemoDetail)glpeSourceDetail;        
        boolean isDebit = true;
        KualiDecimal creditMemoDetailDistrictTaxAmount = customerCreditMemoDetail.getCreditMemoItemTotalAmount().multiply(getDistrictTaxPercent());
        
        CustomerInvoiceGLPEService service = SpringContext.getBean(CustomerInvoiceGLPEService.class);
        service.createDistrictSalesTaxGLPEs(this, customerCreditMemoDetail, sequenceHelper, isDebit, hasOffset, creditMemoDetailDistrictTaxAmount);
        //Add district sales tax receivable too
        */
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
}
