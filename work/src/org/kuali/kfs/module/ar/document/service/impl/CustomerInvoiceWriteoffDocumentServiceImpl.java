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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.batch.service.CustomerInvoiceWriteoffBatchService;
import org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO;
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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.GlobalVariables;
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
    private CustomerInvoiceWriteoffBatchService invoiceWriteoffBatchService;
    private DateTimeService dateTimeService;
    
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

        String writeoffTaxGenerationOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceWriteoffDocument.class, ArConstants.GLPE_WRITEOFF_TAX_GENERATION_METHOD);
        boolean isUsingTaxGenerationMethodDisallow = ArConstants.GLPE_WRITEOFF_TAX_GENERATION_METHOD_DISALLOW.equals( writeoffTaxGenerationOption );
        if (isUsingOrgAcctDefaultWriteoffFAU || isUsingTaxGenerationMethodDisallow) {

            Integer currentUniversityFiscalYear = universityDateService.getCurrentFiscalYear();
            ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getOrganizationByNamespaceCode(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);

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
/*
    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceDocumentsForInvoiceWriteoffLookup() {
        // change this service call to a service method that actually takes in the lookup parameters
        Collection<CustomerInvoiceDocument> customerInvoiceDocuments = customerInvoiceDocumentService.getAllCustomerInvoiceDocumentsWithoutWorkflowInfo();
        
        Collection<CustomerInvoiceDocument> customerInvoiceDocumentsWithOpenBalance = new ArrayList<CustomerInvoiceDocument>();
        for (Iterator itr = customerInvoiceDocuments.iterator(); itr.hasNext();) {
            CustomerInvoiceDocument invoice = (CustomerInvoiceDocument) itr.next();
            if (invoice.getOpenAmount().isGreaterThan(KualiDecimal.ZERO))
                customerInvoiceDocumentsWithOpenBalance.add(invoice);
        }

        return CustomerInvoiceWriteoffLookupUtil.getPopulatedCustomerInvoiceWriteoffLookupResults(customerInvoiceDocumentsWithOpenBalance);
    }
    */
    public Collection<CustomerInvoiceWriteoffLookupResult> getCustomerInvoiceDocumentsForInvoiceWriteoffLookup(Map<String, String> fieldValues) {
        
        //  only one of these four will be used, based on priority
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String customerName = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NAME);
        String customerTypeCode = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_TYPE_CODE);
        String customerInvoiceNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_INVOICE_NUMBER);
        
        //  this may be combined with any of the four above
        String age = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.AGE);

        //  this is the priority order for searching if multiples are entered
        Collection<CustomerInvoiceDocument> customerInvoiceDocuments;
        if (StringUtils.isNotEmpty(customerInvoiceNumber)) {
            customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
            CustomerInvoiceDocument customerInvoiceDocument = customerInvoiceDocumentService.getInvoiceByInvoiceDocumentNumber(customerInvoiceNumber);
            if (customerInvoiceDocument.isOpenInvoiceIndicator())
                customerInvoiceDocuments.add(customerInvoiceDocument);
            else
                customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        }
        else if (StringUtils.isNotEmpty(customerNumber)) {
            customerInvoiceDocuments = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNumber(customerNumber);
        }
        else if (StringUtils.isNotEmpty(customerName) && StringUtils.isNotEmpty(customerTypeCode)) {
            customerInvoiceDocuments = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerNameByCustomerType(customerName, customerTypeCode);
        }
        else if (StringUtils.isNotEmpty(customerName)) {
            customerInvoiceDocuments = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerName(customerName);
        }
        else if (StringUtils.isNotEmpty(customerTypeCode)) {
            customerInvoiceDocuments = customerInvoiceDocumentService.getOpenInvoiceDocumentsByCustomerType(customerTypeCode);
        }
        else {
             customerInvoiceDocuments = customerInvoiceDocumentService.getAllOpenCustomerInvoiceDocumentsWithoutWorkflow();
        }
        
        //  if no age value was specified, then we're done!
        if (StringUtils.isEmpty(age)) {
            return CustomerInvoiceWriteoffLookupUtil.getPopulatedCustomerInvoiceWriteoffLookupResults(customerInvoiceDocuments);
        }
        
        // walk through what we have, and do any extra filtering based on age, if necessary
        boolean eligibleInvoiceFlag;
        Collection<CustomerInvoiceDocument> eligibleInvoices = new ArrayList<CustomerInvoiceDocument>();
        for (CustomerInvoiceDocument invoice : customerInvoiceDocuments) {
            eligibleInvoiceFlag = true;
            
            if (ObjectUtils.isNotNull(invoice.getAge())) {
                eligibleInvoiceFlag &=((new Integer(age)).compareTo(invoice.getAge()) <= 0);
            } else {
                eligibleInvoiceFlag = false;
            }
        
            if (eligibleInvoiceFlag) {
                eligibleInvoices.add(invoice);
            }
        }

        return CustomerInvoiceWriteoffLookupUtil.getPopulatedCustomerInvoiceWriteoffLookupResults(eligibleInvoices);
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService#sendCustomerInvoiceWriteoffDocumentsToBatch(org.kuali.rice.kim.bo.Person, java.util.Collection)
     */
    public String sendCustomerInvoiceWriteoffDocumentsToBatch(Person person, Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults) {
        
        CustomerInvoiceWriteoffBatchVO batch = new CustomerInvoiceWriteoffBatchVO(person.getPrincipalName());
        
        //  add the date
        batch.setSubmittedOn(dateTimeService.getCurrentTimestamp().toString());
        
        //  add the customer note, if one was added
        String note = null;
        for( CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult : customerInvoiceWriteoffLookupResults ){
            note = customerInvoiceWriteoffLookupResult.getCustomerNote();
        }
        if (StringUtils.isNotBlank(note)) {
            batch.setNote(note);
        }
        
        //  add the document numbers
        for( CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult : customerInvoiceWriteoffLookupResults ){
            for( CustomerInvoiceDocument customerInvoiceDocument : customerInvoiceWriteoffLookupResult.getCustomerInvoiceDocuments() ){
                batch.addInvoiceNumber(customerInvoiceDocument.getDocumentNumber());
            }
        }
        
        // use the batch service to create the XML and drop it in the directory
        return invoiceWriteoffBatchService.createBatchDrop(person, batch);
    }
    
    @Deprecated 
    public void createCustomerInvoiceWriteoffDocuments(String personId, Collection<CustomerInvoiceWriteoffLookupResult> customerInvoiceWriteoffLookupResults) throws WorkflowException {
        
        //create customer writeoff documents
        for( CustomerInvoiceWriteoffLookupResult customerInvoiceWriteoffLookupResult : customerInvoiceWriteoffLookupResults ){
            
            //TODO this needs to go in the batch service
            customerService.createCustomerNote(customerInvoiceWriteoffLookupResult.getCustomerNumber(), customerInvoiceWriteoffLookupResult.getCustomerNote());
            
            for( CustomerInvoiceDocument customerInvoiceDocument : customerInvoiceWriteoffLookupResult.getCustomerInvoiceDocuments() ){
                //createCustomerInvoiceWriteoffDocument(customerInvoiceDocument);
            }
        }
    }    
    
    public String createCustomerInvoiceWriteoffDocument(Person initiator, String invoiceNumber, String note) throws WorkflowException {

        //  force the initiating user into the header
        GlobalVariables.getUserSession().setBackdoorUser(initiator.getPrincipalName());
        
        //  create the new writeoff document
        CustomerInvoiceWriteoffDocument document = (CustomerInvoiceWriteoffDocument) documentService.getNewDocument(CustomerInvoiceWriteoffDocument.class);
        
        //  setup the defaults and tie it to the Invoice document
        document.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
        setupDefaultValuesForNewCustomerInvoiceWriteoffDocument( document );
        document.getDocumentHeader().setDocumentDescription(ArConstants.CUSTOMER_INVOICE_WRITEOFF_DOCUMENT_DESCRIPTION + " " + invoiceNumber + ".");
        
        //  satisfy silly > 10 chars explanation rule
        if (StringUtils.isBlank(note)) {
            note = "Document created by batch process.";
        }
        else if (note.length() <= 10) {
            note = "Document created by batch process.  " + note;
        }
        document.getDocumentHeader().setExplanation(note);
        
        //  route the document
        documentService.routeDocument(document, "Routed by Customer Invoice Writeoff Document Batch Service", null);

        //  clear the user overrid
        GlobalVariables.getUserSession().clearBackdoorUser();
        
        return document.getDocumentNumber();
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


    public void setInvoiceWriteoffBatchService(CustomerInvoiceWriteoffBatchService invoiceWriteoffBatchService) {
        this.invoiceWriteoffBatchService = invoiceWriteoffBatchService;
    }


    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


}

