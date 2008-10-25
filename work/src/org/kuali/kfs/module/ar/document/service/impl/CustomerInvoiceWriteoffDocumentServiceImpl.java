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
package org.kuali.kfs.module.ar.document.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceWriteoffLookupResult;
import org.kuali.kfs.module.ar.businessobject.OrganizationAccountingDefault;
import org.kuali.kfs.module.ar.businessobject.lookup.CustomerInvoiceWriteoffLookupUtil;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerInvoiceWriteoffDocumentServiceImpl implements CustomerInvoiceWriteoffDocumentService {

    private ParameterService parameterService;
    private UniversityDateService universityDateService;
    private PersonService personService;
    private BusinessObjectService businessObjectService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private CustomerInvoiceDocumentService customerInvoiceDocumentService;
    private CustomerService customerService;
    private DocumentService documentService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService#setupDefaultValuesForNewCustomerInvoiceWriteoffDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceWriteoffDocument)
     */
    public void setupDefaultValuesForNewCustomerInvoiceWriteoffDocument(CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument) {

        // update status
        customerInvoiceWriteoffDocument.setStatusCode(ArConstants.CustomerInvoiceWriteoffStatuses.IN_PROCESS);

        // set accounts receivable document header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(customerInvoiceWriteoffDocument.getDocumentNumber());
        accountsReceivableDocumentHeader.setCustomerNumber(customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getAccountsReceivableDocumentHeader().getCustomerNumber());
        customerInvoiceWriteoffDocument.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        // if writeoffs are generated based on organization accounting default, populate those fields now
        String writeoffGenerationOption = parameterService.getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_GENERATION_METHOD);
        boolean isUsingOrgAcctDefaultWriteoffFAU = ArConstants.GLPE_WRITEOFF_GENERATION_METHOD_ORG_ACCT_DEFAULT.equals(writeoffGenerationOption);
        if (isUsingOrgAcctDefaultWriteoffFAU) {

            Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
            ChartOrgHolder currentUser = org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.KNSAuthorizationService.class).getOrganizationByModuleId(KFSConstants.Modules.CHART);

            Map<String, Object> criteria = new HashMap<String, Object>();
            criteria.put("universityFiscalYear", currentUniversityFiscalYear);
            criteria.put("chartOfAccountsCode", customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getBillByChartOfAccountCode());
            criteria.put("organizationCode", customerInvoiceWriteoffDocument.getCustomerInvoiceDocument().getBilledByOrganizationCode());

            OrganizationAccountingDefault organizationAccountingDefault = (OrganizationAccountingDefault) businessObjectService.findByPrimaryKey(OrganizationAccountingDefault.class, criteria);
            if (ObjectUtils.isNotNull(organizationAccountingDefault)) {
                customerInvoiceWriteoffDocument.setChartOfAccountsCode(organizationAccountingDefault.getWriteoffChartOfAccountsCode());
                customerInvoiceWriteoffDocument.setAccountNumber(organizationAccountingDefault.getWriteoffAccountNumber());
                customerInvoiceWriteoffDocument.setSubAccountNumber(organizationAccountingDefault.getWriteoffSubAccountNumber());
                customerInvoiceWriteoffDocument.setFinancialObjectCode(organizationAccountingDefault.getWriteoffFinancialObjectCode());
                customerInvoiceWriteoffDocument.setFinancialSubObjectCode(organizationAccountingDefault.getWriteoffFinancialSubObjectCode());
                customerInvoiceWriteoffDocument.setProjectCode(organizationAccountingDefault.getWriteoffProjectCode());
                customerInvoiceWriteoffDocument.setOrganizationReferenceIdentifier(organizationAccountingDefault.getWriteoffOrganizationReferenceIdentifier());
            }
        }
    }
    

    public boolean isCustomerInvoiceWriteoffDocumentApproved(String customerInvoiceWriteoffDocumentNumber) {
        Map criteria = new HashMap();
        criteria.put("documentNumber", customerInvoiceWriteoffDocumentNumber);
        criteria.put("documentHeader.financialDocumentStatusCode", KFSConstants.DocumentStatusCodes.APPROVED);
        return businessObjectService.countMatching(CustomerInvoiceWriteoffDocument.class, criteria) == 1;
    }    

    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceDocumentsForInvoiceWriteoffLookup() {
        // change this service call to a service method that actually takes in the lookup parameters
        Collection<CustomerInvoiceDocument> customerInvoiceDocuments = customerInvoiceDocumentService.getAllCustomerInvoiceDocumentsWithoutWorkflowInfo(); 
        return CustomerInvoiceWriteoffLookupUtil.getPopulatedCustomerInvoiceWriteoffLookupResults(customerInvoiceDocuments);
    }
    
    public void createCustomerInvoiceWriteoffDocuments(Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults) throws WorkflowException {
        
        //create customer writeoff documents
        for( CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult : customerInvoiceWriteoffLookupResults ){
            
            customerService.createCustomerNote(customerInvoiceWriteoffLookupResult.getCustomerNumber(), customerInvoiceWriteoffLookupResult.getCustomerNote());
            
            for( CustomerInvoiceDocument customerInvoiceDocument : customerInvoiceWriteoffLookupResult.getCustomerInvoiceDocuments() ){
                createCustomerInvoiceWriteoffDocument(customerInvoiceDocument);
            }
        }
    }    
    
    protected void createCustomerInvoiceWriteoffDocument(CustomerInvoiceDocument customerInvoiceDocument) throws WorkflowException {
        CustomerInvoiceWriteoffDocument customerInvoiceWriteoffDocument = (CustomerInvoiceWriteoffDocument)documentService.getNewDocument(CustomerInvoiceWriteoffDocument.class);
        customerInvoiceWriteoffDocument.setFinancialDocumentReferenceInvoiceNumber(customerInvoiceDocument.getDocumentNumber());
        setupDefaultValuesForNewCustomerInvoiceWriteoffDocument( customerInvoiceWriteoffDocument );
        customerInvoiceWriteoffDocument.getDocumentHeader().setDocumentDescription(ArConstants.CUSTOMER_INVOICE_WRITEOFF_DOCUMENT_DESCRIPTION + " " + customerInvoiceDocument.getDocumentNumber());
        documentService.saveDocument(customerInvoiceWriteoffDocument);        
    }
    
    
    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public CustomerInvoiceDocumentService getCustomerInvoiceDocumentService() {
        return customerInvoiceDocumentService;
    }

    public void setCustomerInvoiceDocumentService(CustomerInvoiceDocumentService customerInvoiceDocumentService) {
        this.customerInvoiceDocumentService = customerInvoiceDocumentService;
    }

    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }


    public CustomerService getCustomerService() {
        return customerService;
    }


    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }


    public DocumentService getDocumentService() {
        return documentService;
    }


    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }
}

