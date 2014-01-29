/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
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
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.AccountsReceivableDocumentHeader;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerBillingStatement;
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
import org.kuali.kfs.module.ar.document.service.ReceivableAccountingLineService;
import org.kuali.kfs.module.ar.report.util.CustomerStatementResultHolder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.ChartOrgHolder;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.dao.DocumentDao;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerInvoiceDocumentServiceImpl implements CustomerInvoiceDocumentService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceDocumentServiceImpl.class);

    protected AccountsReceivableDocumentHeaderService accountsReceivableDocumentHeaderService;
    protected BusinessObjectService businessObjectService;
    protected CustomerAddressService customerAddressService;
    protected CustomerInvoiceDetailService customerInvoiceDetailService;
    protected CustomerInvoiceDocumentDao customerInvoiceDocumentDao;
    protected CustomerInvoiceRecurrenceDetails customerInvoiceRecurrenceDetails;
    protected DateTimeService dateTimeService;
    protected DocumentService documentService;
    protected DocumentDao documentDao;
    protected FinancialSystemUserService financialSystemUserService;
    protected InvoicePaidAppliedService<CustomerInvoiceDetail> invoicePaidAppliedService;
    protected NonInvoicedDistributionService nonInvoicedDistributionService;
    protected ParameterService parameterService;
    protected PersonService personService;
    protected ReceivableAccountingLineService receivableAccountingLineService;
    protected UniversityDateService universityDateService;

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#convertDiscountsToPaidApplieds(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
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

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getAllOpenCustomerInvoiceDocuments()
     */
    @Override
    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocuments() {
        return getAllOpenCustomerInvoiceDocuments(true);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getAllOpenCustomerInvoiceDocumentsWithoutWorkflow()
     */
    @Override
    public Collection<CustomerInvoiceDocument> getAllOpenCustomerInvoiceDocumentsWithoutWorkflow() {
        return getAllOpenCustomerInvoiceDocuments(false);
    }

    /**
     * @param includeWorkflowHeaders
     * @return
     */
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
            for ( Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds) ) {
                docs.add( (CustomerInvoiceDocument) doc );
            }
        } catch (WorkflowException e) {
            throw new RuntimeException("Unable to retrieve Customer Invoice Documents", e);
        }

        return docs;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#attachWorkflowHeadersToTheInvoices(java.util.Collection)
     */
    @Override
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
            for ( Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds) ) {
                docs.add( (CustomerInvoiceDocument) doc );
            }
        } catch (WorkflowException e) {
            throw new RuntimeException("Unable to retrieve Customer Invoice Documents", e);
        }

        return docs;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOpenInvoiceDocumentsByCustomerNumber(java.lang.String)
     */
    @Override
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

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOpenInvoiceDocumentsByCustomerNameByCustomerType(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerNameByCustomerType(String customerName, String customerTypeCode) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // trim and force-caps the customer name
        customerName = StringUtils.replace(customerName, KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN);
        customerName = customerName.trim();
        if (customerName.indexOf("%") < 0) {
            customerName += "%";
        }

        // trim and force-caps
        customerTypeCode = customerTypeCode.trim().toUpperCase();

        invoices.addAll(customerInvoiceDocumentDao.getOpenByCustomerNameByCustomerType(customerName, customerTypeCode));
        return invoices;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOpenInvoiceDocumentsByCustomerName(java.lang.String)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getOpenInvoiceDocumentsByCustomerName(String customerName) {
        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        // trim and force-caps the customer name
        customerName = StringUtils.replace(customerName, KFSConstants.WILDCARD_CHARACTER, KFSConstants.PERCENTAGE_SIGN);
        customerName = customerName.trim();
        if (customerName.indexOf("%") < 0) {
            customerName += "%";
        }

        invoices.addAll(customerInvoiceDocumentDao.getOpenByCustomerName(customerName));
        return invoices;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOpenInvoiceDocumentsByCustomerType(java.lang.String)
     */
    @Override
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
    @Override
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        return getCustomerInvoiceDetailsForCustomerInvoiceDocument(customerInvoiceDocument.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocumentWithCaching(CustomerInvoiceDocument customerInvoiceDocument) {
        return customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoiceWithCaching(customerInvoiceDocument.getDocumentNumber());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDetailsForCustomerInvoiceDocument(java.lang.String)
     */
    @Override
    public Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        return customerInvoiceDetailService.getCustomerInvoiceDetailsForInvoice(customerInvoiceDocumentNumber);
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOpenAmountForCustomerInvoiceDocument(java.lang.String)
     */
    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(String customerInvoiceDocumentNumber) {
        if (null == customerInvoiceDocumentNumber) {
            return null;
        }
        return getOpenAmountForCustomerInvoiceDocument(getInvoiceByInvoiceDocumentNumber(customerInvoiceDocumentNumber));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOpenAmountForCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    public KualiDecimal getOpenAmountForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        KualiDecimal total = new KualiDecimal(0);
        if (customerInvoiceDocument.isOpenInvoiceIndicator()) {
            Collection<CustomerInvoiceDetail> customerInvoiceDetails = customerInvoiceDocument.getCustomerInvoiceDetailsWithoutDiscounts();
            for (CustomerInvoiceDetail detail : customerInvoiceDetails) {
                // note that we're now dealing with conditionally applying discounts
                // depending on whether the doc is saved or approved one level down,
                // in the CustomerInvoiceDetail.getAmountOpen()
                detail.setCustomerInvoiceDocument(customerInvoiceDocument);
                total = total.add(detail.getAmountOpen());
            }
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getOriginalTotalAmountForCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    public KualiDecimal getOriginalTotalAmountForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        LOG.info("\n\n\n\t\t invoice: " + customerInvoiceDocument.getDocumentNumber() + "\n\t\t 111111111 HEADER TOTAL AMOUNT (should be null): " + customerInvoiceDocument.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount() + "\n\n");
        customerInvoiceDocument.getDocumentNumber();
        HashMap criteria = new HashMap();
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, customerInvoiceDocument.getDocumentHeader().getDocumentTemplateNumber());
        FinancialSystemDocumentHeader financialSystemDocumentHeader = businessObjectService.findByPrimaryKey(FinancialSystemDocumentHeader.class, criteria);
        KualiDecimal originalTotalAmount = KualiDecimal.ZERO;
        originalTotalAmount = financialSystemDocumentHeader.getFinancialDocumentTotalAmount();

        LOG.info("\n\n\n\t\t invoice: " + customerInvoiceDocument.getDocumentNumber() + "\n\t\t 333333333333 HEADER TOTAL AMOUNT (should be set now): " + customerInvoiceDocument.getFinancialSystemDocumentHeader().getFinancialDocumentTotalAmount() + "\n\n");
        return originalTotalAmount;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoicesByCustomerNumber(java.lang.String)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByCustomerNumber(String customerNumber) {

        Collection<CustomerInvoiceDocument> invoices = new ArrayList<CustomerInvoiceDocument>();

        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put("customerNumber", customerNumber);

        Collection<AccountsReceivableDocumentHeader> documentHeaders = businessObjectService.findMatching(AccountsReceivableDocumentHeader.class, fieldValues);

        List<String> documentHeaderIds = new ArrayList<String>();
        for (AccountsReceivableDocumentHeader header : documentHeaders) {
            documentHeaderIds.add(header.getDocumentHeader().getDocumentNumber());
        }

        if (0 < documentHeaderIds.size()) {
            try {
                for ( Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds) ) {
                    invoices.add( (CustomerInvoiceDocument) doc );
                }
            } catch (WorkflowException e) {
                LOG.error("getCustomerInvoiceDocumentsByCustomerNumber " + customerNumber + " failed", e);
            }
        }
        return invoices;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerByOrganizationInvoiceNumber(java.lang.String)
     */
    @Override
    public Customer getCustomerByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        CustomerInvoiceDocument invoice = getInvoiceByOrganizationInvoiceNumber(organizationInvoiceNumber);
        return invoice.getAccountsReceivableDocumentHeader().getCustomer();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoiceByOrganizationInvoiceNumber(java.lang.String)
     */
    @Override
    public CustomerInvoiceDocument getInvoiceByOrganizationInvoiceNumber(String organizationInvoiceNumber) {
        return customerInvoiceDocumentDao.getInvoiceByOrganizationInvoiceNumber(organizationInvoiceNumber);
    }

    /**
     * @param invoiceDocumentNumber
     * @return
     */
    @Override
    public Customer getCustomerByInvoiceDocumentNumber(String invoiceDocumentNumber) {
        CustomerInvoiceDocument invoice = getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
        return invoice.getAccountsReceivableDocumentHeader().getCustomer();
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getInvoiceByInvoiceDocumentNumber(java.lang.String)
     */
    @Override
    public CustomerInvoiceDocument getInvoiceByInvoiceDocumentNumber(String invoiceDocumentNumber) {
        return customerInvoiceDocumentDao.getInvoiceByInvoiceDocumentNumber(invoiceDocumentNumber);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getPrintableCustomerInvoiceDocumentsByInitiatorPrincipalName(java.lang.String)
     */
    @Override
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
        Principal initiator = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(initiatorPrincipalName);
        if (initiator == null) {
            throw new IllegalArgumentException("The parameter value for initiatorPrincipalName [" + initiatorPrincipalName + "] passed in doesnt map to a person.");
        }

        // retrieve all the ready-to-print docs in the user-queue for all users
        List<String> printableUserQueueDocNumbers = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersFromUserQueue();

        // get all the documents that might be right, but this set includes documents generated
        // by the wrong user
        List<CustomerInvoiceDocument> customerInvoiceDocumentsSuperSet = new ArrayList<CustomerInvoiceDocument>();
        if (printableUserQueueDocNumbers.size() > 0) {
            try {
                for ( Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, printableUserQueueDocNumbers) ) {
                    customerInvoiceDocumentsSuperSet.add( (CustomerInvoiceDocument) doc );
                }
            }
            catch (WorkflowException e) {
                throw new RuntimeException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        else {
            customerInvoiceDocumentsSuperSet = new ArrayList<CustomerInvoiceDocument>();
        }

        // filter only the ones initiated by the correct user
        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        for (CustomerInvoiceDocument superSetDocument : customerInvoiceDocumentsSuperSet) {
            if ( StringUtils.equalsIgnoreCase(superSetDocument.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId(), initiator.getPrincipalId())) {
                customerInvoiceDocuments.add(superSetDocument);
            }
        }
        return customerInvoiceDocuments;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        List<String> documentHeaderIds = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersByBillingChartAndOrg(chartOfAccountsCode, organizationCode);

        return getCustomerInvoiceDocumentsByDocumentNumbers(documentHeaderIds);
    }

    protected List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByDocumentNumbers( List<String> documentHeaderIds ) {
        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>(documentHeaderIds.size());
        if (documentHeaderIds != null && !documentHeaderIds.isEmpty()) {
            try {
                for ( Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds) ) {
                    customerInvoiceDocuments.add( (CustomerInvoiceDocument) doc );
                }
            } catch (WorkflowException e) {
                throw new RuntimeException("Unable to retrieve Customer Invoice Documents", e);
            }
        }
        return customerInvoiceDocuments;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getPrintableCustomerInvoiceDocumentsForBillingStatementByBillingChartAndOrg(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsForBillingStatementByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        List<String> documentHeaderIds = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersForBillingStatementByBillingChartAndOrg(chartOfAccountsCode, organizationCode);

        List<CustomerInvoiceDocument> customerInvoiceDocuments = new ArrayList<CustomerInvoiceDocument>();
        if (documentHeaderIds != null && !documentHeaderIds.isEmpty()) {
            try {
                for (Document doc : documentService.getDocumentsByListOfDocumentHeaderIds(CustomerInvoiceDocument.class, documentHeaderIds)) {
                    customerInvoiceDocuments.add((CustomerInvoiceDocument) doc);
                }
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
    @Override
    public List<CustomerInvoiceDocument> getPrintableCustomerInvoiceDocumentsByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode) {

        List<String> documentHeaderIds = customerInvoiceDocumentDao.getPrintableCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(chartOfAccountsCode, organizationCode);

        return getCustomerInvoiceDocumentsByDocumentNumbers(documentHeaderIds);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDocumentsByAccountNumber(java.lang.String)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByAccountNumber(String accountNumber) {
          List<String> documentHeaderIds = customerInvoiceDetailService.getCustomerInvoiceDocumentNumbersByAccountNumber(accountNumber);

        return getCustomerInvoiceDocumentsByDocumentNumbers(documentHeaderIds);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getCustomerInvoiceDocumentsByBillingChartAndOrg(java.lang.String,
     *      java.lang.String)
     */
    @Override
    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByBillingChartAndOrg(String chartOfAccountsCode, String organizationCode) {
        List<String> documentHeaderIds = customerInvoiceDocumentDao.getCustomerInvoiceDocumentNumbersByBillingChartAndOrg(chartOfAccountsCode, organizationCode);

        return getCustomerInvoiceDocumentsByDocumentNumbers(documentHeaderIds);
    }

    /**
     * @see org.kuali.module.ar.service.CustomerInvoiceDocumentService#getCustomerInvoiceDocumentsByCustomerNumber(java.lang.String)
     */
    @Override
    public List<CustomerInvoiceDocument> getCustomerInvoiceDocumentsByProcessingChartAndOrg(String chartOfAccountsCode, String organizationCode) {

        List<String> documentHeaderIds = customerInvoiceDocumentDao.getCustomerInvoiceDocumentNumbersByProcessingChartAndOrg(chartOfAccountsCode, organizationCode);

        return getCustomerInvoiceDocumentsByDocumentNumbers(documentHeaderIds);
    }

    /**
     * Refactor to have all the setters in here.
     *
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#setupDefaultValuesForNewCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
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
        criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, document.getBillByChartOfAccountCode());
        criteria.put(KFSPropertyConstants.ORGANIZATION_CODE, document.getBilledByOrganizationCode());
        OrganizationOptions organizationOptions = businessObjectService.findByPrimaryKey(OrganizationOptions.class, criteria);

        if (ObjectUtils.isNotNull(organizationOptions)) {
            document.setPrintInvoiceIndicator(organizationOptions.getPrintInvoiceIndicator());
            document.setInvoiceTermsText(organizationOptions.getOrganizationPaymentTermsText());
        }

        // If document is using receivable option, set receivable accounting line for customer invoice document
        String receivableOffsetOption = parameterService.getParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD);
        boolean isUsingReceivableFAU = ArConstants.GLPE_RECEIVABLE_OFFSET_GENERATION_METHOD_FAU.equals(receivableOffsetOption);
        if (isUsingReceivableFAU) {
            receivableAccountingLineService.setReceivableAccountingLineForCustomerInvoiceDocument(document);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#loadCustomerAddressesForCustomerInvoiceDocument(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
    public void loadCustomerAddressesForCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        // if address identifier is provided, try to refresh customer address data
        if (ObjectUtils.isNotNull(customerInvoiceDocument.getAccountsReceivableDocumentHeader())) {
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
    @Override
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
        document.setBillingDate(dateTimeService.getCurrentSqlDateMidnight());
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getNonInvoicedDistributionsForInvoice(java.lang.String)
     */
    @Override
    public Collection<NonInvoicedDistribution> getNonInvoicedDistributionsForInvoice(String documentNumber) {
        return nonInvoicedDistributionService.getNonInvoicedDistributionsForInvoice(documentNumber);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getNonInvoicedTotalForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
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
    @Override
    public KualiDecimal getNonInvoicedTotalForInvoice(String documentNumber) {
        return getNonInvoicedTotalForInvoice(getInvoiceByInvoiceDocumentNumber(documentNumber));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getPaidAppliedTotalForInvoice(org.kuali.kfs.module.ar.document.CustomerInvoiceDocument)
     */
    @Override
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
    @Override
    public KualiDecimal getPaidAppliedTotalForInvoice(String documentNumber) {
        return getPaidAppliedTotalForInvoice(getInvoiceByInvoiceDocumentNumber(documentNumber));
    }

    /**
     * @param document
     */
    protected void setupBasicDefaultValuesForCustomerInvoiceDocument(CustomerInvoiceDocument document) {
        ChartOrgHolder currentUser = financialSystemUserService.getPrimaryOrganization(GlobalVariables.getUserSession().getPerson(), ArConstants.AR_NAMESPACE_CODE);
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
    protected Date getDefaultInvoiceDueDate() {
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

    @Override
    public void closeCustomerInvoiceDocument(CustomerInvoiceDocument customerInvoiceDocument) {
        customerInvoiceDocument.setOpenInvoiceIndicator(false);
        customerInvoiceDocument.setClosedDate(dateTimeService.getCurrentSqlDate());
        businessObjectService.save(customerInvoiceDocument);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#updateReportedDate(String)
     */
    @Override
    public void updateReportedDate(String docNumber) {
        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("documentNumber", docNumber);
        CustomerInvoiceDocument customerInvoiceDocument = businessObjectService.findByPrimaryKey(CustomerInvoiceDocument.class, criteria);
        Date reportedDate = dateTimeService.getCurrentSqlDate();
        if (ObjectUtils.isNotNull(customerInvoiceDocument)) {
            customerInvoiceDocument.setReportedDate(reportedDate);
            businessObjectService.save(customerInvoiceDocument);
        }
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#updateReportedInvoiceInfo(CustomerStatementResultHolder)
     */
    @Override
    public void updateReportedInvoiceInfo(CustomerStatementResultHolder data) {
        HashMap<String, String> criteria = new HashMap<String, String>();
        criteria.put("customerNumber", data.getCustomerNumber());
        CustomerBillingStatement customerBillingStatement = businessObjectService.findByPrimaryKey(CustomerBillingStatement.class, criteria);
        if (ObjectUtils.isNotNull(customerBillingStatement)) {
            customerBillingStatement.setPreviouslyBilledAmount(data.getCurrentBilledAmount());
            customerBillingStatement.setReportedDate(dateTimeService.getCurrentSqlDate());
        } else {
            customerBillingStatement = new CustomerBillingStatement();
            customerBillingStatement.setCustomerNumber(data.getCustomerNumber());
            customerBillingStatement.setPreviouslyBilledAmount(data.getCurrentBilledAmount());
            customerBillingStatement.setReportedDate(dateTimeService.getCurrentSqlDate());
        }
        businessObjectService.save(customerBillingStatement);
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

    /**
     * @return Returns the personService.
     */
    public PersonService getPersonService() {
        return personService;
    }


    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#checkIfInvoiceNumberIsFinal(java.lang.String)
     */
    @Override
    public boolean checkIfInvoiceNumberIsFinal(String invDocumentNumber) {
        boolean isSuccess = true;
        if (StringUtils.isBlank(invDocumentNumber)) {
            isSuccess &= false;
        }
        else {
            CustomerInvoiceDocument customerInvoiceDocument = getInvoiceByInvoiceDocumentNumber(invDocumentNumber);

            if (ObjectUtils.isNull(customerInvoiceDocument)) {
                isSuccess &= false;
            }
            else {
                Document doc = null;
                try {
                    doc = documentService.getByDocumentHeaderId(invDocumentNumber);
                }
                catch (WorkflowException e) {
                    isSuccess &= false;
                }
                if (ObjectUtils.isNull(doc) || ObjectUtils.isNull(doc.getDocumentHeader()) || doc.getDocumentHeader().getWorkflowDocument() == null || !(doc.getDocumentHeader().getWorkflowDocument().isApproved() || doc.getDocumentHeader().getWorkflowDocument().isProcessed())) {
                    isSuccess &= false;
                }
            }
        }
        return isSuccess;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getAllAgingInvoiceDocumentsByBilling(java.util.List, java.util.List, java.lang.Integer)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByBilling(List<String> charts, List<String> organizations, Integer invoiceAge) {
        Date invoiceBillingDateFrom = null;
        Date invoiceBillingDateTo = this.getPastDate(invoiceAge - 1) ;

        return customerInvoiceDocumentDao.getAllAgingInvoiceDocumentsByBilling(charts, organizations, invoiceBillingDateFrom, invoiceBillingDateTo);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getAllAgingInvoiceDocumentsByAccounts(java.util.List, java.util.List, java.lang.Integer)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByAccounts(List<String> charts, List<String> accounts, Integer invoiceAge) {
        Date invoiceBillingDateFrom = null;
        Date invoiceBillingDateTo = this.getPastDate(invoiceAge - 1) ;

        return customerInvoiceDocumentDao.getAllAgingInvoiceDocumentsByAccounts(charts, accounts, invoiceBillingDateFrom, invoiceBillingDateTo);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getAllAgingInvoiceDocumentsByProcessing(java.util.List, java.util.List, java.lang.Integer)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByProcessing(List<String> charts, List<String> organizations, Integer invoiceAge) {
        Date invoiceBillingDateFrom = null;
        Date invoiceBillingDateTo = this.getPastDate(invoiceAge - 1) ;

        return customerInvoiceDocumentDao.getAllAgingInvoiceDocumentsByProcessing(charts, organizations, invoiceBillingDateFrom, invoiceBillingDateTo);
    }

    /**
     * get the date before the given amount of days
     */
    protected Date getPastDate(Integer amount){
        Integer pastDateAmount = -1 * amount;

        java.util.Date today = this.getDateTimeService().getCurrentDate();
        java.util.Date pastDate = DateUtils.addDays(today, pastDateAmount);

        return KfsDateUtils.convertToSqlDate(pastDate);
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService#getAllAgingInvoiceDocumentsByCustomerTypes(java.util.List, java.lang.Integer, java.sql.Date)
     */
    @Override
    public Collection<CustomerInvoiceDocument> getAllAgingInvoiceDocumentsByCustomerTypes(List<String> customerTypes, Integer invoiceAge, Date invoiceDueDateFrom) {
        Date pastDate = this.getPastDate(invoiceAge - 1) ;
        Date invoiceDueDateTo = KfsDateUtils.convertToSqlDate(DateUtils.addDays(pastDate, 1));
        LOG.info("invoiceDueDateTo" + invoiceDueDateTo);

        return customerInvoiceDocumentDao.getAllAgingInvoiceDocumentsByCustomerTypes(customerTypes, invoiceDueDateFrom, invoiceDueDateTo);
    }



    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }


}
