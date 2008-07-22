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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase;

public class CustomerInvoiceWriteoffDocument extends GeneralLedgerPostingDocumentBase {

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
    private KualiDecimal invoiceWriteoffAmount;

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

    public KualiDecimal getInvoiceWriteoffAmount() {
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
    
    //METHODS NEEDED TO GET ACCOUNTING LINES TO SHOW UP CORRECTLY
    
    public List getSourceAccountingLines(){
        return customerInvoiceDocument.getSourceAccountingLines();
    }
    
    public SourceAccountingLine getSourceAccountingLine(int index){
        return customerInvoiceDocument.getSourceAccountingLine(index);
    }
    
    public String getSourceAccountingLineEntryName(){
        return customerInvoiceDocument.getSourceAccountingLineEntryName();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.SOURCE;
    }    
}
