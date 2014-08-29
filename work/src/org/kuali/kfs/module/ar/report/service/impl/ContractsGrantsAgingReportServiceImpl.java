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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.LookupService;
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
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected DateTimeService dateTimeService;
    protected LookupService lookupService;
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
        String accountChartOfAccountsCode = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.ACCOUNT_CHART_CODE);
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
        String invoiceDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(invoiceDateFromString, invoiceDateToString, true);

        String responsibilityId = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.CG_ACCT_RESP_ID);

        String awardEndFromDate = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE_FROM);
        String awardEndToDate = (String) fieldValues.get(ArPropertyConstants.ContractsGrantsAgingReportFields.AWARD_END_DATE_TO);

        Map<String,String> fieldValuesForInvoice = new HashMap<String,String>();
        fieldValuesForInvoice.put(ArPropertyConstants.OPEN_INVOICE_IND, KFSConstants.Booleans.TRUE);
        fieldValues.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_DOCUMENT_TYPE_NAME, ArConstants.ArDocumentTypeCodes.CONTRACTS_GRANTS_INVOICE);
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
        if (!StringUtils.isBlank(customerNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (!StringUtils.isBlank(customerName)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CUSTOMER_NAME, customerName);
        }
        if (!StringUtils.isBlank(proposalNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (!StringUtils.isBlank(markedAsFinal)) {
            if (markedAsFinal.equalsIgnoreCase(KFSConstants.ParameterValues.YES)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_FINAL_BILL, KFSConstants.Booleans.TRUE);
            }
            else if (markedAsFinal.equalsIgnoreCase(KFSConstants.ParameterValues.NO)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.INVOICE_DOCUMENT_FINAL_BILL, KFSConstants.Booleans.FALSE);
            }
        }
        if (!StringUtils.isBlank(invoiceNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.DOCUMENT_NUMBER, invoiceNumber);
        }
        if (!StringUtils.isBlank(responsibilityId)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CG_ACCT_RESP_ID, responsibilityId);
        }

        if (!StringUtils.isBlank(accountChartOfAccountsCode)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES+"."+KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountChartOfAccountsCode);
        }
        if (!StringUtils.isBlank(accountNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES+"."+KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }

        if (!StringUtils.isBlank(invoiceDateCriteria)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.DOCUMENT_HEADER+"."+KFSPropertyConstants.WORKFLOW_CREATE_DATE, invoiceDateCriteria);
        }

        final String sourceTotalCriteria = getAmountCriteria(invoiceAmountFrom, invoiceAmountTo);
        if (!StringUtils.isBlank(sourceTotalCriteria)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.SOURCE_TOTAL, sourceTotalCriteria);
        }

        String billingBeginDateString = null;
        if (begin != null) {
            billingBeginDateString = getDateTimeService().toDateString(begin);
        }
        String billingEndDateString = null;
        if (end != null) {
            billingEndDateString = getDateTimeService().toDateString(end);
        }
        final String billingDateCriteria = getContractsGrantsReportHelperService().fixDateCriteria(billingBeginDateString, billingEndDateString, false);
        if (!StringUtils.isBlank(billingDateCriteria)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLING_DATE, billingDateCriteria);
        }

        final Set<Long> awardIds = lookupBillingAwards(awardDocumentNumber, awardEndFromDate, awardEndToDate, fundManager);

        // here put all criterias and find the docs
        Collection<ContractsGrantsInvoiceDocument> contractsGrantsInvoiceDocs = getLookupService().findCollectionBySearch(ContractsGrantsInvoiceDocument.class, fieldValuesForInvoice);

        // filter by collector and user performing the search
        String collectorPrincipalId = null;
        if (!StringUtils.isBlank(collectorPrincName)) {
            Person collUser = personService.getPersonByPrincipalName(collectorPrincName);
            if (ObjectUtils.isNotNull(collUser)) {
                collectorPrincipalId = collUser.getPrincipalId();
            }
        }
        Person user = GlobalVariables.getUserSession().getPerson();

        if (!CollectionUtils.isEmpty(contractsGrantsInvoiceDocs)) {
            for (Iterator<ContractsGrantsInvoiceDocument> iter = contractsGrantsInvoiceDocs.iterator(); iter.hasNext();) {
                ContractsGrantsInvoiceDocument document = iter.next();
                if (awardIds == null || (!ObjectUtils.isNull(document.getAward()) && awardIds.contains(document.getAward().getProposalNumber()))) {
                } else if (StringUtils.isNotEmpty(collectorPrincipalId)) {
                    if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, collectorPrincipalId)) {
                        iter.remove();
                    }
                } else if (!contractsGrantsInvoiceDocumentService.canViewInvoice(document, user.getPrincipalId())) {
                    iter.remove();
                }
            }
        }

        // prepare map of cgDocs by customer.
        List<ContractsGrantsInvoiceDocument> cgInvoiceDocs = null;
        if (!CollectionUtils.isEmpty(contractsGrantsInvoiceDocs)) {
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
     * Generates a Set of proposal ids for awards which match the given criteria
     * @param awardDocumentNumber the document number of the award
     * @param awardEndFromDate the award ending date of the award
     * @param awardEndToDate the award ending date of the award
     * @param fundManager the principal name of the fund manager
     * @return a Set of Award ids to filter on, or null if no search was actually completed
     */
    protected Set<Long> lookupBillingAwards(String awardDocumentNumber, String awardEndFromDate, String awardEndToDate, String fundManager) {
        if (StringUtils.isBlank(awardDocumentNumber) && StringUtils.isBlank(awardEndFromDate) && StringUtils.isBlank(awardEndToDate) && StringUtils.isBlank(fundManager)) {
            return null; // nothing to search on?  then return null to note that no search was completed
        }

        final Set<String> fundManagerIds = getContractsGrantsReportHelperService().lookupPrincipalIds(fundManager);

        Map<String, String> fieldValues = new HashMap<>();
        if (!StringUtils.isBlank(awardDocumentNumber)) {
            fieldValues.put(KFSPropertyConstants.AWARD_DOCUMENT_NUMBER, awardDocumentNumber);
        }

        final String awardEnd = getContractsGrantsReportHelperService().fixDateCriteria(awardEndFromDate, awardEndToDate, false);
        if (!StringUtils.isBlank(awardEnd)) {
            fieldValues.put(KFSPropertyConstants.AWARD_ENDING_DATE, awardEnd);
        }
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        final List<? extends ContractsAndGrantsAward> awards = getContractsAndGrantsModuleBillingService().lookupAwards(fieldValues, true);

        Set<Long> billingAwardIds = new HashSet<>();
        for (ContractsAndGrantsAward award : awards) {
            if (award instanceof ContractsAndGrantsBillingAward) {
                final ContractsAndGrantsBillingAward cgbAward = (ContractsAndGrantsBillingAward)award;
                if (ObjectUtils.isNull(cgbAward.getAwardPrimaryFundManager()) || fundManagerIds.isEmpty() || fundManagerIds.contains(cgbAward.getAwardPrimaryFundManager().getPrincipalId())) {
                    billingAwardIds.add(cgbAward.getProposalNumber());
                }
            }
        }
        return billingAwardIds;
    }

    /**
     * Turns a from amount and to amount into a lookupable criteria
     * @param fromAmount the lower bound amount
     * @param toAmount the upper bound amount
     * @return a lookupable criteria
     */
    protected String getAmountCriteria(String fromAmount, String toAmount) {
        if (!StringUtils.isBlank(fromAmount)) {
            if (!StringUtils.isBlank(toAmount)) {
                return fromAmount+SearchOperator.BETWEEN.op()+toAmount;
            } else {
                return SearchOperator.GREATER_THAN_EQUAL.op()+fromAmount;
            }
        } else if (!StringUtils.isBlank(toAmount)) {
            return SearchOperator.LESS_THAN_EQUAL.op()+toAmount;
        }
        return null;
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
        Map<String, Object> args = new HashMap<>();
        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotEmpty(accountNumber)) {
            args.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        }
        if (ObjectUtils.isNotNull(accountChartCode) && StringUtils.isNotEmpty(accountChartCode)) {
            args.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, accountChartCode);
        }
        return businessObjectService.findMatching(CustomerInvoiceDetail.class, args);
    }

    /**
     * Figures out the reportRunDate and then uses filterContractsGrantsAgingReport to look up the right documents
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#lookupContractsGrantsInvoiceDocumentsForAging(java.util.Map)
     */
    @Override
    public Map<String, List<ContractsGrantsInvoiceDocument>> lookupContractsGrantsInvoiceDocumentsForAging(Map<String, Object> fieldValues) {
        try {
            java.util.Date today = getDateTimeService().getCurrentDate();
            String reportRunDateStr = (String) fieldValues.get(ArPropertyConstants.CustomerAgingReportFields.REPORT_RUN_DATE);

            java.util.Date reportRunDate = (ObjectUtils.isNull(reportRunDateStr) || reportRunDateStr.isEmpty()) ?
                                                today :
                                                getDateTimeService().convertToDate(reportRunDateStr);

            // retrieve filtered data according to the lookup
            Map<String, List<ContractsGrantsInvoiceDocument>> cgMapByCustomer = filterContractsGrantsAgingReport(fieldValues, null, new java.sql.Date(reportRunDate.getTime()));
            return cgMapByCustomer;

        }
        catch (ParseException ex) {
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

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public LookupService getLookupService() {
        return lookupService;
    }

    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }
}