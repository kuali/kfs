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

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceRecurrenceDetails;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonInvoicedDistribution;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.module.ar.document.dataaccess.CustomerInvoiceDocumentDao;
import org.kuali.kfs.module.ar.document.service.AccountsReceivableDocumentHeaderService;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDetailService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.InvoicePaidAppliedService;
import org.kuali.kfs.module.ar.document.service.NonInvoicedDistributionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.dao.DocumentDao;
import org.kuali.rice.kns.exception.InfrastructureException;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerInvoiceDocumentServiceImpl implements CustomerInvoiceDocumentService {

    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private ReceivableAccountingLineService receivableAccountingLineService;
    private AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    private CustomerAddressService customerAddressService;
    private CustomerInvoiceDocumentDao customerInvoiceDocumentDao;
    private DocumentService documentService;
    private DocumentDao documentDao;
    private InvoicePaidAppliedService<CustomerInvoiceDetail> invoicePaidAppliedService;
    private NonInvoicedDistributionService nonInvoicedDistributionService;
    private CustomerInvoiceDetailService customerInvoiceDetailService;
    private CustomerInvoiceRecurrenceDetails customerInvoiceRecurrenceDetails;
    private UniversityDateService universityDateService;
    private PersonService<Person> personService;

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentServiceImpl.class);

    public void convertDiscountsToPaidApplieds(CustomerInvoiceDocument invoice) {

        // this needs a little explanation. we have to calculate manually
        // whether we've written off the whole thing, because the regular
        // code uses the invoice paid applieds to discount, but since those
        // are added but not committed in this transaction, they're also not
        // visible in this transaction, so we do it manually.
        KualiDecimal openAmount = invoice.getOpenAmount();

        String invoiceNumber = invoice.getDocumentNumber();
        List<CustomerInvoiceDetail> discounts = invoice.getDiscounts();

        // retrieve the number of current paid applieds, so we dont have item number overlap
        Integer paidAppliedItemNumber = 0;

        for (CustomerInvoiceDetail discount : discounts) {

            // if credit amount is zero, do nothing
            if (KualiDecimal.ZERO.equals(discount.getAmount())) {
                continue;
            }

            if (paidAppliedItemNumber == 0) {
                paidAppliedItemNumber = invoicePaidAppliedService.getNumberOfInvoicePaidAppliedsForInvoiceDetail(invoiceNumber, discount.getInvoiceItemNumber());
            }

            // create and save the paidApplied
            InvoicePaidApplied invoicePaidApplied = new InvoicePaidApplied();
            invoicePaidApplied.setDocumentNumber(invoiceNumber);
            invoicePaidApplied.setPaidAppliedItemNumber(paidAppliedItemNumber++);
            invoicePaidApplied.setFinancialDocumentReferenceInvoiceNumber(invoiceNumber);
            invoicePaidApplied.setInvoiceItemNumber(discount.getInvoiceItemNumber());
            invoicePaidApplied.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
            invoicePaidApplied.setUniversityFiscalPeriodCode(universityDateService.getCurrentUniversityDate().getUniversityFiscalAccountingPeriod());
            invoicePaidApplied.setInvoiceItemAppliedAmount(discount.getAmount().abs());
            openAmount = openAmount.subtract(discount.getAmount().abs());
            businessObjectService.save(invoicePaidApplied);
        }

        // if its open, but now with a zero openamount, then close it
        if (KualiDecimal.ZERO.equals(openAmount)) {
            invoice.setOpenInvoiceIndicator(false);
            invoice.setClosedDate(dateTimeService.getCurrentSqlDate());
            documentService.updateDocument(invoice);
        }
    }

    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocuments() {
        return getAllOpenCustomerInvoiceDocuments(true);
    }

    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocumentsWithoutWorkflow() {
        return getAllOpenCustomerInvoiceDocuments(false);
    }

    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocuments(boolean includeWorkflowHeaders) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // retrieve the set of documents without workflow headers
        invoices = customerInvoiceDocumentDao.getAllOpen();

        // if we dont need workflow headers, then we're done
        if (!includeWorkflowHeaders) {
            return invoices;
        }

        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        for (CustomerInvoiceDocument invoice : invoices) {
            documentHeaderIds.add(invoice.getDocumentNumber());
        }

        // get all of our docs with full workflow headers
        List<CustomerInvoiceDocument> docs = new ArrayList<CustomerInvoiceDocument>();
        try {
            docs = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
        }

        return docs;
    }

    public Collection<CustomerInvoiceDocument> attachWorkflowHeadersToTheInvoices(Collection<CustomerInvoiceDocument> invoices) {
        List<CustomerInvoiceDocument> docs = new ArrayList<CustomerInvoiceDocument>();
        if (invoices == null || invoices.isEmpty()) {
            return docs;
        }

        // make a list of necessary workflow docs to retrieve
        List<String> documentHeaderIds = new ArrayList<String>();
        for (CustomerInvoiceDocument invoice : invoices) {
            documentHeaderIds.add(invoice.getDocumentNumber());
        }

        // get all of our docs with full workflow headers
        try {
            docs = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
        }
        catch (WorkflowException e) {
            throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
        }

        return docs;
    }

    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerNumber(String customerNumber) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // customer number is not required to be populated, so we need to check that it's not null first
        if (StringUtils.isNotEmpty(customerNumber)) {
            // trim and force-caps the customer number
            customerNumber = customerNumber.trim().toUpperCase();
        }

        invoices.addAll(customerInvoiceDocumentDao.getOpenByCustomerNumber(customerNumber));
        return invoices;
    }

    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerNameByCustomerType(String customerName, String customerTypeCode) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // trim and force-caps the customer name
        customerName = StringUtils.replace(customerName, KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN);
        customerName = customerName.trim();
        if (customerName.indexOf("%") < 0)
            customerName += "%";

        // trim and force-caps
        customerTypeCode = customerTypeCode.trim().toUpperCase();

        invoices.addAll(customerInvoiceDocumentDao.getOpenByCustomerNameByCustomerType(customerName, customerTypeCode));
        return invoices;
    }

    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerName(String customerName) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // trim and force-caps the customer name
        customerName = StringUtils.replace(customerName, KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN);
        customerName = customerName.trim();
        if (customerName.indexOf("%") < 0)
            customerName += "%";

        invoices.addAll(customerInvoiceDocumentDao.getOpenByCustomerName(customerName));
        return invoices;
    }

    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerType(String customerTypeCode) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // trim and force-caps
        customerTypeCode = customerTypeCode.trim().toUpperCase();

        invoices.addAll(customerInvoiceDocumentDao.getOpenByCustomerType(customerTypeCode));
        return invoices;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        return getCustomerInvoiceDetailsForCustomerInvoiceDocument(customerInvoiceDocument.getDocumentNumber());
    }
    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(CustomerInvoiceDocument customerInvoiceDocument) {
        return customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoiceWithCaching(customerInvoiceDocument.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocument(java.lang.String)
     */
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        return customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(customerInvoiceDocumentNumber);
    }


    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        if (null == customerInvoiceDocumentNumber) {
            return null;
        }
        return getOpenAmountForCustomerInvoiceDocument(getInvoiceByInvoiceDocumentNumber(customerInvoiceDocumentNumber));
    }

    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        KualiDecimal total = new KualiDecimal(0);
        if (customerInvoiceDocument.isOpenInvoiceIndicator()) {
            Collection<CustomerInvoiceDetail> customerInvoiceDetails = customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts();
            for (CustomerInvoiceDetail detail : customerInvoiceDetails) {
                // note that we're now dealing with conditionally applying discounts
                // depending on whether the doc is saved or approved one level down,
                // in the CustomerInvoiceDetail.getAmountOpen()
                // FIXME - added by harsha
                detail.setCustomerInvoiceDocument(customerInvoiceDocument);
                total = total.add(detail.getAmountOpen());
            }
        }
        return total;
    }

    public KualiDecimal getOriginalTotalAmountForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        LOG.info("\n\n\n\t\t invoice: " + customerInvoiceDocument.getDocumentNumber() + "\n\t\t 111111111 HEADER TOTAL AMOUNT (should be null): " + customerInvoiceDocument.getDocumentHeader().getFinancialDocumentTotalAmount() + "\n\n");
        customerInvoiceDocument.getDocumentNumber();
        // original-amount = SpringContext.getBean(FinancialSystemDocumentService.class).get
        HashMap criteria = new HashMap();
        criteria.put("documentNumber", customerInvoiceDocument.getDocumentHeader().getDocumentTemplateNumber());
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        FinancialSystemDocumentHeader financialSystemDocumentHeader = (FinancialSystemDocumentHeader) businessObjectService.findByPrimaryKey(FinancialSystemDocumentHeader.class, criteria);
        KualiDecimal originalTotalAmount = KualiDecimal.ZERO;
        originalTotalAmount = financialSystemDocumentHeader.getFinancialDocumentTotalAmount();

        LOG.info("\n\n\n\t\t invoice: " + customerInvoiceDocument.getDocumentNumber() + "\n\t\t 333333333333 HEADER TOTAL AMOUNT (should be set now): " + customerInvoiceDocument.getDocumentHeader().getFinancialDocumentTotalAmount() + "\n\n");
        return originalTotalAmount;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoicesByCustomerNumber(java.lang.String)
     */
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerNumber(String customerNumber) {

        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("customerNumber", customerNumber);

        Collection<AccountsReceivableDocumentHeader> documentHeaders = businessObjectService.findMatching(AccountsReceivableDocumentHeader.class, fieldValues);

        List<String> documentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader header : documentHeaders) {
            String documentNumber = null;
            try {
                Long.parseLong(header.getDocumentHeader().getDocumentNumber());
                documentNumber = header.getDocumentHeader().getDocumentNumber();
                documentHeaderIds.add(documentNumber);
            }
            catch (NumberFormatException nfe) {
            }
        }

        if (0 < documentHeaderIds.size()) {
            try {
                invoices = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                LOG.error(e.getMessage(), e);
            }
        }
        return invoices;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerByOrganizationInvoiceNumber(java.lang.String)
     */
    public Customer getCustomerByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        CustomerInvoiceDocument invoice = getInvoiceByOrganizationInvoiceNumber(organizationInvoiceNumber);
        return invoice.getAccountsReceivableDocumentHeader().getCustomer();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoiceByOrganizationInvoiceNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        return customerInvoiceDocumentDao.getInvoiceByOrganizationInvoiceNumber(organizationInvoiceNumber);
    }

    /**
     * @param invoiceDocumentNumber
     * @return
     */
    public Customer getCustomerByInvoiceDocumentNumber(String invoiceDocumentNumber) {
        CustomerInvoiceDocument invoice = getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
        return invoice.getAccountsReceivableDocumentHeader().getCustomer();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoiceByInvoiceDocumentNumber(java.lang.String)
     */
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String invoiceDocumentNumber) {
        return customerInvoiceDocumentDao.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
    }

    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByInitiatorPrincipalName(String initiatorPrincipalName) {
        if (StringUtils.isBlank(initiatorPrincipalName)) {
            throw new IllegalArgumentException("The parameter [initiatorPrincipalName] passed in was null or blank.");
        }

        // IMPORTANT NOTES ABOUT THIS METHOD
        //
        // This method behaves differently than the other invoice printing methods. This is
        // because there's no way from within KFS to do a direct DB call to get all the invoices
        // you want. This is because workflow holds the document initiator, and you cant guarantee
        // that in a given implementation that you have access to that other db. It could be on
        // another box in another network, and you only have web-services access to the Rice box.
        //
        // Given that, we try to minimize the resource hit of this call as much as possible. First
        // we retrieve all invoices that havent been printed (ie, dont have a print date) and that
        // are marked for the USER print queue. At any given time that should be a manageable number of
        // documents.
        //
        // Then we walk through them, retrieve the full workflow-populated version of it, and only
        // return the ones that match the initiator.
        //
        // This isnt as performant a solution as the other getPrintableCustomerInvoiceBy...
        // methods, but its the best we can do in this release, and it should be manageable.

        // 
        // attempt to retrieve the initiator person specified, and puke if not found
        Person initiator = personService.getPersonByPrincipalName(initiatorPrincipalName);
        if (initiator == null) {
            throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + initiatorPrincipalName + "] passed in doesnt map to a person.");
        }

        // retrieve all the ready-to-print docs in the user-queue for all users
        List<String> printableUserQueueDocNumbers = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersFromUserQueue();

        // get all the documents that might be right, but this set includes documents generated
        // by the wrong user
        List<CustomerInvoiceDocument> customerInvoiceDocumentsSuperSet;
        if (printableUserQueueDocNumbers.size() > 0) {
            try {
                customerInvoiceDocumentsSuperSet = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, printableUserQueueDocNumbers);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        else {
            customerInvoiceDocumentsSuperSet = new ArrayList<CustomerInvoiceDocument>();
        }

        // filter only the ones initiated by the correct user
        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        for (CustomerInvoiceDocument superSetDocument : customerInvoiceDocumentsSuperSet) {
            if (superSetDocument.getDocumentHeader().getWorkflowDocument().userIsInitiator(initiator)) {
                customerInvoiceDocuments.add(superSetDocument);
            }
        }
        return customerInvoiceDocuments;
    }

    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        List<String> documentHeaderIds = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersByBillingChartAndOrg(chartOfAccountsCode, organizationCode);

        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        if (documentHeaderIds != null && !documentHeaderIds.isEmpty()) {
            try {
                customerInvoiceDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        return customerInvoiceDocuments;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerInvoiceDocumentsByCustomerNumber(java.lang.String)
     */
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode) {

        List<String> documentHeaderIds = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(chartOfAccountsCode, organizationCode);

        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        if (documentHeaderIds != null && !documentHeaderIds.isEmpty()) {
            try {
                customerInvoiceDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        return customerInvoiceDocuments;
    }

    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByAccountNumber(String accountNumber) {

        List<String> docNumbers = customerInvoiceDetailService.getCustomerInvoiceDocumentNumbersByAccountNumber(accountNumber);

        Collection<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();

        if (docNumbers.size() != 0) {
            System.out.println(docNumbers);
            // customerInvoiceDocuments.clear();
            try {
                customerInvoiceDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, docNumbers);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        return customerInvoiceDocuments;
    }

    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        List<String> documentHeaderIds = customerInvoiceDocumentDao.getCustomerInvoiceDocumentNumbersByBillingChartAndOrg(chartOfAccountsCode, organizationCode);

        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        if (documentHeaderIds != null && !documentHeaderIds.isEmpty()) {
            try {
                customerInvoiceDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        return customerInvoiceDocuments;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerInvoiceDocumentsByCustomerNumber(java.lang.String)
     */
    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode) {

        List<String> documentHeaderIds = customerInvoiceDocumentDao.getCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(chartOfAccountsCode, organizationCode);

        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        if (documentHeaderIds != null && !documentHeaderIds.isEmpty()) {
            try {
                customerInvoiceDocuments = documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        return customerInvoiceDocuments;
    }

    /**
     * Refactor to have all the setters in here.
     * 
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#setupDefaultValuesForNewCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public void setupDefaultValuesForNewCustomerInvoiceDocument(CustomerInvoiceDocument document) {

        setupBasicDefaultValuesForCustomerInvoiceDocument(document);

        // set up the default values for the AR DOC Header

        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        // set up the primary key for AR_INV_RCURRNC_DTL_T
        CustomerInvoiceRecurrenceDetails recurrenceDetails = new CustomerInvoiceRecurrenceDetails();
        recurrenceDetails.setInvoiceNumber(document.getDocumentNumber());
        // recurrenceDetails.setCustomerNumber(document.getCustomer().getCustomerNumber());
        document.setCustomerInvoiceRecurrenceDetails(recurrenceDetails);

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("chartOfAccountsCode", document.getBillByChartOfAccountCode());
        criteria.put("organizationCode", document.getBilledByOrganizationCode());
        OrganizationOptions organizationOptions = (OrganizationOptions) businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNotNull(organizationOptions)) {
            document.setPrintInvoiceIndicator(organizationOptions.getPrintInvoiceIndicator());
            document.setInvoiceTermsText(organizationOptions.getOrganizationPaymentTermsText());
        }

        // If document is using receivable option, set receivable accounting line for customer invoice document
        String receivableOffsetOption = SpringContext.getBean(ParameterService.class).getParameterValue(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        if (isUsingReceivableFAU) {
            receivableAccountingLineService.setReceivableAccountingLineForCustomerInvoiceDocument(document);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#loadCustomerAddressesForCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public void loadCustomerAddressesForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        // if address identifier is provided, try to refresh customer address data
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader())) {
            CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
            CustomerAddress customerShipToAddress = customerAddressService.getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerInvoiceDocument.getCustomerShipToAddressIdentifier());
            CustomerAddress customerBillToAddress = customerAddressService.getByPrimaryKey(customerInvoiceDocument.getAccountsReceivableDocumentHeader().getCustomerNumber(), customerInvoiceDocument.getCustomerBillToAddressIdentifier());

            if (ObjectUtils.isNotNull(customerShipToAddress)) {
                customerInvoiceDocument.setCustomerShipToAddress(customerShipToAddress);
                customerInvoiceDocument.setCustomerShipToAddressOnInvoice(customerShipToAddress);
            }

            if (ObjectUtils.isNotNull(customerBillToAddress)) {
                customerInvoiceDocument.setCustomerBillToAddress(customerBillToAddress);
                customerInvoiceDocument.setCustomerBillToAddressOnInvoice(customerBillToAddress);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#setupDefaultValuesForCopiedCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public void setupDefaultValuesForCopiedCustomerInvoiceDocument(CustomerInvoiceDocument document) {

        setupBasicDefaultValuesForCustomerInvoiceDocument(document);

        // Save customer number since it will get overwritten when we retrieve the accounts receivable document header from service
        String customerNumber = document.getAccountsReceivableDocumentHeader().getCustomerNumber();

        // Set up the default values for the AR DOC Header
        AccountsReceivableDocumentHeader accountsReceivableDocumentHeader = accountsReceivableDocumentHeaderService.getNewAccountsReceivableDocumentHeaderForCurrentUser();
        accountsReceivableDocumentHeader.setDocumentNumber(document.getDocumentNumber());
        accountsReceivableDocumentHeader.setCustomerNumber(customerNumber);
        document.setAccountsReceivableDocumentHeader(accountsReceivableDocumentHeader);

        // set up the primary key for AR_INV_RCURRNC_DTL_T
        CustomerInvoiceRecurrenceDetails recurrenceDetails = new CustomerInvoiceRecurrenceDetails();
        recurrenceDetails.setInvoiceNumber(document.getDocumentNumber());
        // recurrenceDetails.setCustomerNumber(document.getAccountsReceivableDocumentHeader().getCustomerNumber());
        document.setCustomerInvoiceRecurrenceDetails(recurrenceDetails);

        // make open invoice indicator to true
        document.setOpenInvoiceIndicator(true);
        document.setPrintDate(null);
        document.setBillingDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDateMidnight());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoicePaidAppliedsForCustomerInvoiceDocument(java.lang.String)
     */
    // public Collection<InvoicePaidApplied> getInvoicePaidAppliedsForInvoice(String documentNumber) {
    // return invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(documentNumber);
    // }
    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getNonInvoicedDistributionsForInvoice(java.lang.String)
     */
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber) {
        return nonInvoicedDistributionService.getNonInvoicedDistributionsForInvoice(documentNumber);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getNonInvoicedTotalForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(CustomerInvoiceDocument invoice) {
        Collection<NonInvoicedDistribution> payments = this.nonInvoicedDistributionService.getNonInvoicedDistributionsForInvoice(invoice);
        KualiDecimal total = new KualiDecimal(0);
        for (NonInvoicedDistribution payment : payments) {
            total = total.add(payment.getFinancialDocumentLineAmount());
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getNonInvoicedTotalForInvoice(java.lang.String)
     */
    public KualiDecimal getNonInvoicedTotalForInvoice(String documentNumber) {
        return getNonInvoicedTotalForInvoice(getInvoiceByInvoiceDocumentNumber(documentNumber));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getPaidAppliedTotalForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(CustomerInvoiceDocument invoice) {
        Collection<InvoicePaidApplied> payments = invoicePaidAppliedService.getInvoicePaidAppliedsForInvoice(invoice);
        KualiDecimal total = new KualiDecimal(0);
        for (InvoicePaidApplied payment : payments) {
            total = total.add(payment.getInvoiceItemAppliedAmount());
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getPaidAppliedTotalForInvoice(java.lang.String)
     */
    public KualiDecimal getPaidAppliedTotalForInvoice(String documentNumber) {
        return getPaidAppliedTotalForInvoice(getInvoiceByInvoiceDocumentNumber(documentNumber));
    }

    /**
     * @param document
     */
    private void setupBasicDefaultValuesForCustomerInvoiceDocument(CustomerInvoiceDocument document) {
        ChartOrgHolder currentUser = SpringContext.getBean(FinancialSystemUserService.class).getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
        if (currentUser != null) {
            document.setBillByChartOfAccountCode(currentUser.getChartOfAccountsCode());
            document.setBilledByOrganizationCode(currentUser.getOrganizationCode());
        }
        document.setInvoiceDueDate(getDefaultInvoiceDueDate());
        document.setOpenInvoiceIndicator(true);
    }

    /**
     * This method sets due date equal to todays date +30 days by default
     * 
     * @param dateTimeService
     */
    private Date getDefaultInvoiceDueDate() {
        Calendar cal = dateTimeService.getCurrentCalendar();
        cal.add(Calendar.DATE, 30);
        Date sqlDueDate = null;
        try {
            sqlDueDate = dateTimeService.convertToSqlDate(new Timestamp(cal.getTime().getTime()));
        }
        catch (ParseException e) {
            // TODO: throw an error here, but don't die
        }
        return sqlDueDate;
    }

    public void closeCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        customerInvoiceDocument.setOpenInvoiceIndicator(false);
        customerInvoiceDocument.setClosedDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save(customerInvoiceDocument);
    }

    public CustomerInvoiceDocumentDao getCustomerInvoiceDocumentDao() {
        return customerInvoiceDocumentDao;
    }

    public void setCustomerInvoiceDocumentDao(CustomerInvoiceDocumentDao customerInvoiceDocumentDao) {
        this.customerInvoiceDocumentDao = customerInvoiceDocumentDao;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public ReceivableAccountingLineService getReceivableAccountingLineService() {
        return receivableAccountingLineService;
    }

    public void setReceivableAccountingLineService(ReceivableAccountingLineService receivableAccountingLineService) {
        this.receivableAccountingLineService = receivableAccountingLineService;
    }

    public AccountsReceivableDocumentHeaderService getAccountsReceivableDocumentHeaderService() {
        return accountsReceivableDocumentHeaderService;
    }

    public void setAccountsReceivableDocumentHeaderService(AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService) {
        this.accountsReceivableDocumentHeaderService = accountsReceivableDocumentHeaderService;
    }

    public CustomerAddressService getCustomerAddressService() {
        return customerAddressService;
    }

    public void setCustomerAddressService(CustomerAddressService customerAddressService) {
        this.customerAddressService = customerAddressService;
    }

    public void setDocumentDao(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    public void setInvoicePaidAppliedService(InvoicePaidAppliedService invoicePaidAppliedService) {
        this.invoicePaidAppliedService = invoicePaidAppliedService;
    }

    public void setNonInvoicedDistributionService(NonInvoicedDistributionService nonInvoicedDistributionService) {
        this.nonInvoicedDistributionService = nonInvoicedDistributionService;
    }

    public void setCustomerInvoiceDetailService(CustomerInvoiceDetailService customerInvoiceDetailService) {
        this.customerInvoiceDetailService = customerInvoiceDetailService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setPersonService(PersonService<Person> personService) {
        this.personService = personService;
    }

}
