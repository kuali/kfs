/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to get the services for PDF generation and other services for CG Aging report.
 */
public class ContractsGrantsAgingReportServiceImpl implements ContractsGrantsAgingReportService {
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    protected PersonService personService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected DateTimeService dateTimeService;
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportServiceImpl.class);

    /**
     * Gets the businessObjectService attribute.
     *
     * @return Returns the businessObjectService.
     */
    @NonTransactional
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     *
     * @param businessObjectService The businessObjectService to set.
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    @NonTransactional
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the collectorHierarchyDao attribute value.
     *
     * @param collectorHierarchyDao The collectorHierarchyDao to set.
     */
    @NonTransactional
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
    @Transactional
    public Map<String, List<ContractsGrantsInvoiceDocument>> filterContractsGrantsAgingReport(Map fieldValues, java.sql.Date begin, java.sql.Date end) throws ParseException {
        Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer = null;

        String reportOption = (String) fieldValues.get(ArPropertyConstants.REPORT_OPTION);
        String orgCode = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FORM_ORGANIZATION_CODE);
        String chartCode = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.PROCESSING_OR_BILLING_CHART_CODE);
        String customerNumber = (String) fieldValues.get(KFSPropertyConstants.CUSTOMER_NUMBER);
        String customerName = (String) fieldValues.get(KFSPropertyConstants.CUSTOMER_NAME);
        String accountNumber = (String) fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String accountChartCode = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.ACCOUNT_CHART_CODE);
        String fundManager = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.FUND_MANAGER);
        String proposalNumber = (String) fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String collectorPrincName = null;

        if (fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME) != null) {
            collectorPrincName = (String) fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);
        }
        String collector = (String) fieldValues.get(KFSPropertyConstants.PRINCIPAL_ID);

        String awardDocumentNumber = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_DOCUMENT_NUMBER);
        String markedAsFinal = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.MARKED_AS_FINAL);
        String endDateString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE);

        String invoiceAmountFrom = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_AMT_FROM);
        String invoiceAmountTo = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_AMT_TO);
        String invoiceNumber = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_NUMBER);

        String invoiceDateFromString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_FROM);
        String invoiceDateToString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_TO);
        String responsibilityId = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.CG_ACCT_RESP_ID);

        String endDateFromString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE_FROM);
        String endDateToString = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE_TO);

        java.sql.Date awardEndFromDate = null;
        java.sql.Date awardEndToDate = null;

        Map<String,String> fieldValuesForInvoice = new HashMap<String,String>();
        fieldValuesForInvoice.put(ArPropertyConstants.OPEN_INVOICE_IND, "true");
        //Now to involve reportOption and handle chart and org
        if(ObjectUtils.isNotNull(reportOption)){
        if (reportOption.equalsIgnoreCase(ArConstants.ReportOptionFieldValues.PROCESSING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_ORGANIZATION_CODE, orgCode);
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_CHART_OF_ACCOUNT_CODE, chartCode);
        }
        if (reportOption.equalsIgnoreCase(ArConstants.ReportOptionFieldValues.BILLING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, orgCode);
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chartCode);
        }
        }
        if (StringUtils.isNotEmpty(customerNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (StringUtils.isNotEmpty(customerName)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CUSTOMER_NAME, customerName);
        }
        if (StringUtils.isNotEmpty(proposalNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (StringUtils.isNotEmpty(markedAsFinal)) {
            if (markedAsFinal.equalsIgnoreCase(KFSConstants.ParameterValues.YES)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_FINAL_BILL, "true");
            }
            else if (markedAsFinal.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_FINAL_BILL, "false");
            }
        }
        if (StringUtils.isNotEmpty(invoiceNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoiceNumber);
        }
        if (StringUtils.isNotEmpty(responsibilityId)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CG_ACCT_RESP_ID, responsibilityId);
        }


        if (ObjectUtils.isNotNull(endDateFromString) && StringUtils.isNotEmpty(endDateFromString.trim())) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);
            awardEndFromDate = new java.sql.Date(dateFormat.parse(endDateFromString).getTime());
        }

        if (ObjectUtils.isNotNull(endDateToString) && StringUtils.isNotEmpty(endDateToString.trim())) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);
            awardEndToDate = new java.sql.Date(dateFormat.parse(endDateToString).getTime());
        }

        // here put all criterias and find the docs
        Map<String, ContractsGrantsInvoiceDocument> documents = new HashMap<String, ContractsGrantsInvoiceDocument>();
        Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = retrieveAllCGInvoicesByCriteriaAndBillingDateRange(fieldValuesForInvoice, begin, end);
        contractsGrantsInvoiceDocs = contractsGrantsInvoiceDocumentService.attachWorkflowHeadersToCGInvoices(contractsGrantsInvoiceDocs);

        // Filter "CINV" docs and remove "INV" docs.
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            documents = new HashMap<String, ContractsGrantsInvoiceDocument>();
            for (Iterator iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                ContractsGrantsInvoiceDocument document = (ContractsGrantsInvoiceDocument) iter.next();
                String documentTypeName = document.getAccountsReceivableDocumentHeader().getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                if (!StringUtils.equals(documentTypeName, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE)) {
                    iter.remove();
                }
                else {
                    documents.put(document.getDocumentNumber(), document);
                }
            }
        }

        // filter Billing Org
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            Map<String, Boolean> billerOrgMap = new HashMap<String, Boolean>();
            boolean isBiller = false;
            for (Iterator iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                ContractsGrantsInvoiceDocument document = (ContractsGrantsInvoiceDocument) iter.next();
                String org = document.getBilledByOrganizationCode();
                if (ObjectUtils.isNotNull(org) && StringUtils.isNotEmpty(org)) {
                    // find in map
                    if (billerOrgMap.containsKey(org)) {
                        isBiller = billerOrgMap.get(org);
                    }
                    else {
                        isBiller = false;
                        Map<String, String> orgCriteria = new HashMap<String, String>();
                        orgCriteria.put(ArPropertyConstants.OrganizationOptionsFields.ORGANIZATION_CODE, document.getBilledByOrganizationCode());
                        // retrive Organization
                        OrganizationOptions orgOp = businessObjectService.findByPrimaryKey(OrganizationOptions.class, orgCriteria);
                        if (orgOp != null) {
                            isBiller = orgOp.isCgBillerIndicator();
                            billerOrgMap.put(org, isBiller);
                        }

                    }
                    // biller false remove from the list
                    if (!isBiller) {
                        iter.remove();
                    }
                }
            }
        }

        // filter by account number and account chart code
        List<CustomerInvoiceDetail> list = null;
        Map<String, List<CustomerInvoiceDetail>> documentWiseInvoiceDetail = null;
        boolean isFound = false;
        if (StringUtils.isNotEmpty(accountChartCode) || StringUtils.isNotEmpty(accountNumber)) {
            Collection<CustomerInvoiceDetail> invoiceDetails = this.getCustomerInvoiceDetailsByAccountNumber(accountChartCode, accountNumber);
            if (ObjectUtils.isNotNull(invoiceDetails) && !invoiceDetails.isEmpty()) {
                documentWiseInvoiceDetail = new HashMap<String, List<CustomerInvoiceDetail>>();
                isFound = true;
                for (CustomerInvoiceDetail invoiceDetail : invoiceDetails) {
                    if (documents.containsKey(invoiceDetail.getDocumentNumber())) {
                        if (documentWiseInvoiceDetail.containsKey(invoiceDetail.getDocumentNumber())) {
                            list = documentWiseInvoiceDetail.get(invoiceDetail.getDocumentNumber());
                        }
                        else {
                            list = new ArrayList<CustomerInvoiceDetail>();
                        }
                        list.add(invoiceDetail);
                        documentWiseInvoiceDetail.put(invoiceDetail.getDocumentNumber(), list);
                    }
                    else {
                        documents.remove(invoiceDetail.getDocumentNumber());
                    }
                }
            }
            else {
                documents = new HashMap<String, ContractsGrantsInvoiceDocument>();
                contractsGrantsInvoiceDocs = new ArrayList<ContractsGrantsInvoiceDocument>();
            }

            if (isFound) {
                contractsGrantsInvoiceDocs = new ArrayList<ContractsGrantsInvoiceDocument>();
            }

            if (ObjectUtils.isNotNull(documentWiseInvoiceDetail) && !documentWiseInvoiceDetail.isEmpty()) {
                for (String docNumber : documents.keySet()) {
                    if (documentWiseInvoiceDetail.containsKey(docNumber)) {
                        ContractsGrantsInvoiceDocument cgDoc = documents.get(docNumber);
                        contractsGrantsInvoiceDocs.add(cgDoc);
                    }
                }
            }
        }

        // filter by award
        if(awardDocumentNumber != null || awardEndFromDate != null || awardEndToDate != null || fundManager != null) {
            contractsGrantsInvoiceDocs = this.filterContractsGrantsDocsAccordingToAward(contractsGrantsInvoiceDocs, awardDocumentNumber, awardEndFromDate, awardEndToDate, fundManager);
        }

        // filter by amount from and to
        boolean includeFromAmt = false;
        if (StringUtils.isNotEmpty(invoiceAmountFrom)) {
            includeFromAmt = true;
        }

        boolean includeToAmt = false;
        if (StringUtils.isNotEmpty(invoiceAmountTo)) {
            includeToAmt = true;
        }

        // filter invoice according to amount
        if (includeFromAmt || includeToAmt) {
            contractsGrantsInvoiceDocs = this.filterInvoicesAccordingToAmount(contractsGrantsInvoiceDocs, includeFromAmt, includeToAmt, invoiceAmountFrom, invoiceAmountTo);
        }

        // filter by collector and user performing the search
        String collectorPrincipalId = null;
        if (ObjectUtils.isNotNull(collectorPrincName) && StringUtils.isNotEmpty(collectorPrincName.trim())) {
            Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
            if (ObjectUtils.isNotNull(collUser)) {
                collectorPrincipalId = collUser.getPrincipalId();
            }
        }
        Person user = GlobalVariables.getUserSession().getPerson();

        for (Iterator<ContractsGrantsInvoiceDocument> iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
            ContractsGrantsInvoiceDocument document = iter.next();
            if (StringUtils.isNotEmpty(collectorPrincipalId)) {
                if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, collectorPrincipalId)) {
                    iter.remove();
                    continue;
                }
            }

            if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, user.getPrincipalId())) {
                iter.remove();
            }
        }

        // filter by invoice date
        invoiceDateFromString = (ObjectUtils.isNull(invoiceDateFromString)) ? "" : invoiceDateFromString;
        invoiceDateToString = (ObjectUtils.isNull(invoiceDateToString)) ? "" : invoiceDateToString;

        contractsGrantsInvoiceDocs = this.filterInvoicesAccordingToInvoiceDate(contractsGrantsInvoiceDocs, invoiceDateFromString, invoiceDateToString);

        // prepare map of cgDocs by customer.
        List<ContractsGrantsInvoiceDocument> cgInvoiceDocs = null;
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            cgMapByCustomer = new HashMap<String, List<ContractsGrantsInvoiceDocument>>();
            for (ContractsGrantsInvoiceDocument cgDoc : contractsGrantsInvoiceDocs) {
                String customerNbr = cgDoc.getCustomer().getCustomerNumber();
                String customerNm = cgDoc.getCustomer().getCustomerName();
                String key = customerNbr + "-" + customerNm;
                if (cgMapByCustomer.containsKey(key)) {
                    cgInvoiceDocs = cgMapByCustomer.get(key);
                }
                else {
                    cgInvoiceDocs = new ArrayList<ContractsGrantsInvoiceDocument>();
                }
                cgInvoiceDocs.add(cgDoc);
                cgMapByCustomer.put(key, cgInvoiceDocs);
            }
        }

        return cgMapByCustomer;
    }

    /**
     * This method retrieves all CG invoice document that match the given field values and the date range.
     *
     * @param fieldValues field values to match against
     * @param beginningInvoiceBillingDate Beginning invoice billing date
     * @param endingInvoiceBillingDate Ending invoice billing date
     * @return a collection of CG Invoices that match the given parameters
     */
    protected Collection<ContractsGrantsInvoiceDocument> retrieveAllCGInvoicesByCriteriaAndBillingDateRange(Map fieldValues, java.sql.Date beginningInvoiceBillingDate, java.sql.Date endingInvoiceBillingDate) {
        Collection<ContractsGrantsInvoiceDocument> cgInvoices = getContractsGrantsInvoiceDocumentService().getMatchingInvoicesByCollectionAndDateRange(fieldValues, beginningInvoiceBillingDate, endingInvoiceBillingDate);
        if (CollectionUtils.isEmpty(cgInvoices)) {
            return null;
        }
        return cgInvoices;
    }

    /**
     * This method filters the Contracts Grants Invoice doc by Award related details
     *
     * @param contractsGrantsInvoiceDocs
     * @param awardDocumentNumber
     * @param awardEndDate
     * @param fundManager
     * @return Returns the list of ContractsGrantsInvoiceDocument.
     */
    @SuppressWarnings("unchecked")
    protected Collection<ContractsGrantsInvoiceDocument> filterContractsGrantsDocsAccordingToAward(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs, String awardDocumentNumber, java.sql.Date awardEndFromDate, java.sql.Date awardEndToDate, String fundManager) {
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            for (Iterator iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                boolean considerAwardDocNumber = (ObjectUtils.isNotNull(awardDocumentNumber) && !StringUtils.isEmpty(awardDocumentNumber)) ? false : true;
                boolean considerFundMngr = (ObjectUtils.isNotNull(fundManager) && !StringUtils.isEmpty(fundManager)) ? false : true;
                boolean considerAwardEndDate = false;
                ContractsGrantsInvoiceDocument document = (ContractsGrantsInvoiceDocument) iter.next();
                considerAwardDocNumber = !considerAwardDocNumber ? awardDocumentNumber.equals(document.getAward().getAwardDocumentNumber()) : considerAwardDocNumber;

                considerAwardEndDate = includeInvoiceAccordingToAwardDate(document.getAward().getAwardEndingDate(), awardEndFromDate, awardEndToDate);

                considerFundMngr = !considerFundMngr ? fundManager.equalsIgnoreCase(document.getAward().getAwardPrimaryFundManager().getFundManager().getPrincipalId()) : considerFundMngr;
                if (!(considerAwardDocNumber && considerFundMngr && considerAwardEndDate)) {
                    iter.remove();
                }
            }
        }
        return contractsGrantsInvoiceDocs;
    }

    /**
     * This method calculates if given invoice has award end date within the given award date range.
     *
     * @param awardDate
     * @param awardStartDate
     * @param awardEndDate
     * @return Returns true if award end date is within the given range.
     */
    protected boolean includeInvoiceAccordingToAwardDate(Date awardDate, Date awardStartDate, Date awardEndDate) {
        boolean includeInvoice = true;
        if (ObjectUtils.isNotNull(awardStartDate) && ObjectUtils.isNotNull(awardEndDate)) {
            includeInvoice = awardDate.compareTo(awardStartDate) >= 0 && awardDate.compareTo(awardEndDate) <= 0;
        }
        else {
            if (ObjectUtils.isNotNull(awardStartDate)) {
                includeInvoice = awardDate.compareTo(awardStartDate) >= 0;
            }
            if (ObjectUtils.isNotNull(awardEndDate) && includeInvoice) {
                includeInvoice = awardDate.compareTo(awardEndDate) <= 0;
            }
        }
        return includeInvoice;
    }

    /**
     * This method filters invoices according to the amount
     *
     * @param contractsGrantsInvoiceDocuments
     * @param includeFromAmt
     * @param includeToAmt
     * @return Returns the list of ContractsGrantsInvoiceDocument.
     */
    protected Collection<ContractsGrantsInvoiceDocument> filterInvoicesAccordingToAmount(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocuments, boolean includeFromAmt, boolean includeToAmt, String invoiceAmountFrom, String invoiceAmountTo) {
        List<ContractsGrantsInvoiceDocument> filteredInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument doc : contractsGrantsInvoiceDocuments) {
            if (includeFromAmt && includeToAmt) {
                if (doc.getSourceTotal().compareTo(new KualiDecimal(invoiceAmountFrom)) >= 0 && doc.getSourceTotal().compareTo(new KualiDecimal(invoiceAmountTo)) <= 0) {
                    filteredInvoices.add(doc);
                }
            }
            else if (includeFromAmt) {
                if (doc.getSourceTotal().compareTo(new KualiDecimal(invoiceAmountFrom)) >= 0) {
                    filteredInvoices.add(doc);
                }
            }
            else {
                if (doc.getSourceTotal().compareTo(new KualiDecimal(invoiceAmountTo)) <= 0) {
                    filteredInvoices.add(doc);
                }
            }
        }
        return filteredInvoices;
    }


    /**
     * This method is used to get the Invoice Details by account number and chart code
     *
     * @param accountChartCode
     * @param accountNumber
     * @return a List of the CustomerInvoiceDetails associated with a given Account Number
     */
    @SuppressWarnings("unchecked")
    protected Collection<CustomerInvoiceDetail> getCustomerInvoiceDetailsByAccountNumber(String accountChartCode, String accountNumber) {
        Map args = new HashMap();
        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotEmpty(accountNumber)) {
            args.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }
        if (ObjectUtils.isNotNull(accountChartCode) && StringUtils.isNotEmpty(accountChartCode)) {
            args.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountChartCode);
        }
        return businessObjectService.findMatching(CustomerInvoiceDetail.class, args);
    }

    /**
     * This method filters invoices according to the Invoice date
     *
     * @param contractsGrantsInvoiceDocuments
     * @param includeFromAmt
     * @param includeToAmt
     * @return Returns the list of ContractsGrantsInvoiceDocument.
     */
    protected Collection<ContractsGrantsInvoiceDocument> filterInvoicesAccordingToInvoiceDate(Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocuments, String invoiceFromDateString, String invoiceToDateString) {
        List<ContractsGrantsInvoiceDocument> filteredInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        Date docInvoiceDate = null;
        for (ContractsGrantsInvoiceDocument doc : contractsGrantsInvoiceDocuments) {
            docInvoiceDate = new Date(doc.getDocumentHeader().getWorkflowDocument().getDateCreated().getMillis());
            if (ContractsGrantsReportUtils.isDateFieldInRange(invoiceFromDateString, invoiceToDateString, docInvoiceDate, ArPropertyConstants.ContractsGrantsAgingReportFields.INVOICE_DATE_TO)) {
                filteredInvoices.add(doc);
            }
        }
        return filteredInvoices;
    }

    /**
     * Figures out the reportRunDate and then uses filterContractsGrantsAgingReport to look up the right documents
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#lookupContractsGrantsInvoiceDocumentsForAging(java.util.Map)
     */
    @Override
    public Map<String, List<ContractsGrantsInvoiceDocument>> lookupContractsGrantsInvoiceDocumentsForAging(Map<String, Object> fieldValues) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        dateFormat.setLenient(false);

        try {
            java.util.Date today = getDateTimeService().getCurrentDate();
            String reportRunDateStr = (String) fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE);

            java.util.Date reportRunDate = (ObjectUtils.isNull(reportRunDateStr) || reportRunDateStr.isEmpty()) ?
                                                today :
                                                dateFormat.parse(reportRunDateStr);

            // retrieve filtered data according to the lookup
            Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer = filterContractsGrantsAgingReport(fieldValues, null, new java.sql.Date(reportRunDate.getTime()));
            return cgMapByCustomer;

        }
        catch (NumberFormatException | ParseException ex) {
            throw new RuntimeException("Could not parse report run date for lookup",ex);
        }
    }

    /**
     * Copies all of the given documents from the Map into a single List
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#flattenContrantsGrantsInvoiceDocumentMap(java.util.Map)
     */
    @Override
    public List<ContractsGrantsInvoiceDocument> flattenContrantsGrantsInvoiceDocumentMap(Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer) {
        List<ContractsGrantsInvoiceDocument> invoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        // prepare list for pdf
        if (ObjectUtils.isNotNull(cgMapByCustomer) && !cgMapByCustomer.isEmpty()) {
            for (String customer : cgMapByCustomer.keySet()) {
                invoices.addAll(cgMapByCustomer.get(customer));
            }
        }
        return invoices;
    }

    @NonTransactional
    public PersonService getPersonService() {
        return personService;
    }

    @NonTransactional
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
