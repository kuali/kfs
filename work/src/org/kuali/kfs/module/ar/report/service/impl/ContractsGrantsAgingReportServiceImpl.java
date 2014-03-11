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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.businessobject.OrganizationOptions;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.ContractsGrantsAgingReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportUtils;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is used to get the services for PDF generation and other services for CG Aging report.
 */
public class ContractsGrantsAgingReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ContractsGrantsAgingReportService {

    private ReportInfo cgAgingReportInfo;
    private ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected BusinessObjectService businessObjectService;
    private PersonService personService;
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ContractsGrantsAgingReportServiceImpl.class);

    /**
     * Gets the cgAgingReportInfo attribute.
     *
     * @return Returns the cgAgingReportInfo.
     */
    @NonTransactional
    public ReportInfo getCgAgingReportInfo() {
        return cgAgingReportInfo;
    }

    /**
     * Sets the cgAgingReportInfo attribute value.
     *
     * @param cgAgingReportInfo The cgAgingReportInfo to set.
     */
    @NonTransactional
    public void setCgAgingReportInfo(ReportInfo cgAgingReportInfo) {
        this.cgAgingReportInfo = cgAgingReportInfo;
    }

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
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    @NonTransactional
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        return generateReport(reportDataHolder, cgAgingReportInfo, baos);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#filterContractsGrantsAgingReport(java.util.Map)
     */
    @Override
    @Transactional
    public Map<String, List<ContractsGrantsInvoiceDocument>> filterContractsGrantsAgingReport(Map fieldValues, java.sql.Date begin, java.sql.Date end) throws ParseException {
        Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer = null;

        String reportOption = (String) fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_OPTION);
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
        java.sql.Date invoiceFromDate = null;
        java.sql.Date invoiceToDate = null;

        Map<String,String> fieldValuesForInvoice = new HashMap<String,String>();
        fieldValuesForInvoice.put(ArPropertyConstants.OPEN_INVOICE_IND, "true");
//Now to involve reportOption and handle chart and org
        if(ObjectUtils.isNotNull(reportOption)){
        if (reportOption.equalsIgnoreCase(ArConstants.CustomerAgingReportFields.PROCESSING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_ORGANIZATION_CODE, orgCode);
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_CHART_OF_ACCOUNT_CODE, chartCode);
        }
        if (reportOption.equalsIgnoreCase(ArConstants.CustomerAgingReportFields.BILLING_ORG) && StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
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
        Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesByCriteriaAndBillingDateRange(fieldValuesForInvoice, begin, end);
        contractsGrantsInvoiceDocs = contractsGrantsInvoiceDocumentService.attachWorkflowHeadersToCGInvoices(contractsGrantsInvoiceDocs);

        // Filter "CINV" docs and remove "INV" docs.
        if (ObjectUtils.isNotNull(contractsGrantsInvoiceDocs) && !contractsGrantsInvoiceDocs.isEmpty()) {
            documents = new HashMap<String, ContractsGrantsInvoiceDocument>();
            for (Iterator iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                try {
                    ContractsGrantsInvoiceDocument document = (ContractsGrantsInvoiceDocument) iter.next();
                    String documentTypeName = document.getAccountsReceivableDocumentHeader().getDocumentHeader().getWorkflowDocument().getDocumentTypeName();
                    if (!documentTypeName.equals(ArPropertyConstants.CONTRACTS_GRANTS_INV_DOC_TYPE)) {
                        iter.remove();
                    }
                    else {
                        documents.put(document.getDocumentNumber(), document);
                    }
                }
                catch (Exception wfe) {
                    LOG.error("problem during ContractsGrantsAgingReportServiceImpl.filterContractsGrantsAgingReport()", wfe);
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
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceReportService#generateCSVToExport(org.kuali.kfs.module.ar.document.ContractsGrantsLOCReviewDocument)
     */
    @Override
    @NonTransactional
    public byte[] generateCSVToExport(Map<String, List<ContractsGrantsAgingReportDetailDataHolder>> detailsMap, ContractsGrantsAgingReportDetailDataHolder totalDataHolder) {
        try {

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(baos);
            writer.append("Agency Name,");
            writer.append("Proposal Number,");
            writer.append("Customer Number,");
            writer.append("Award End Date,");
            writer.append("Age In Days,");
            writer.append("Invoice Number,");
            writer.append("Invoice Date,");
            writer.append("Last Event Date,");
            writer.append("Collector,");
            writer.append("Invoice Amount,");
            writer.append("Payment Amount,");
            writer.append("Balance");
            writer.append('\n');

            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            dateFormat.setLenient(false);

            if (ObjectUtils.isNotNull(detailsMap) && !detailsMap.isEmpty()) {
                for (String key : detailsMap.keySet()) {
                    List<ContractsGrantsAgingReportDetailDataHolder> details = detailsMap.get(key);
                    if (ObjectUtils.isNotNull(details) && !details.isEmpty()) {
                        for (ContractsGrantsAgingReportDetailDataHolder detail : details) {
                            if (!detail.isDisplaySubtotalInd()) {
                                String proposalNumber = returnProperStringValue(detail.getProposalNumber());
                                writer.append("\"" + detail.getAgencyName() + "\"");
                                writer.append(',');
                                writer.append("\"" + proposalNumber + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getCustomerNumber()) + "\"");
                                writer.append(',');
                                writer.append("\"" + (detail.getAwardEndDate() != null ? dateFormat.format(detail.getAwardEndDate()) : "") + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getAgeInDays()) + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getDocumentNumber()) + "\"");
                                writer.append(',');
                                writer.append("\"" + (detail.getInvoiceDate() != null ? dateFormat.format(detail.getInvoiceDate()) : "") + "\"");
                                writer.append(',');
                                writer.append("\"" + (detail.getLastEventDate() != null ? dateFormat.format(detail.getLastEventDate()) : "") + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getCollectorName()) + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getInvoiceAmount()) + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getPaymentAmount()) + "\"");
                                writer.append(',');
                                writer.append("\"" + returnProperStringValue(detail.getRemainingAmount()) + "\"");
                                writer.append('\n');
                            }
                            else {

                                for (int i = 0; i <= 7; i++) {
                                    writer.append("\"" + "" + "\"");
                                    writer.append(',');
                                }

                                writer.append("\"" + "Sub Total" + "\"");
                                writer.append(',');

                                writer.append("\"" + detail.getInvoiceSubTotal() + "\"");
                                writer.append(',');
                                writer.append("\"" + detail.getPaymentSubTotal() + "\"");
                                writer.append(',');
                                writer.append("\"" + detail.getRemainingSubTotal() + "\"");
                                writer.append('\n');
                                writer.append('\n');
                            }
                        }
                    }
                }
            }


            // Displaying total field
            writer.append('\n');
            for (int i = 0; i <= 7; i++) {
                writer.append("\"" + "" + "\"");
                writer.append(',');
            }

            writer.append("\"" + "Total" + "\"");
            writer.append(',');

            writer.append("\"" + totalDataHolder.getInvoiceTotal() + "\"");
            writer.append(',');
            writer.append("\"" + totalDataHolder.getPaymentTotal() + "\"");
            writer.append(',');
            writer.append("\"" + totalDataHolder.getRemainingTotal() + "\"");
            writer.append('\n');

            writer.flush();
            writer.close();
            return baos.toByteArray();
        }
        catch (IOException e) {
            LOG.error("problem during ContractsGrantsAgingReportServiceImpl.generateCSVToExport()", e);
        }
        return null;
    }


    /**
     * This method returns a proper String value for any given object.
     *
     * @param string
     * @return
     */
    protected String returnProperStringValue(Object string) {
        if (ObjectUtils.isNotNull(string)) {
            if (string instanceof KualiDecimal) {
                String amount = (new CurrencyFormatter()).format(string).toString();
                return "$" + (StringUtils.isEmpty(amount) ? "0.00" : amount);
            }
            return string.toString();
        }
        return "";
    }

    @NonTransactional
    public PersonService getPersonService() {
        return personService;
    }

    @NonTransactional
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}
