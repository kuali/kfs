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
package org.kuali.kfs.module.ar.businessobject.lookup;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.DunningCampaign;
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistribution;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.GenerateDunningLettersLookupResult;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.web.ui.ContractsGrantsLookupResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.util.BeanPropertyComparator;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a lookupable helper service class for Generate Dunning Letters.
 */
public class GenerateDunningLettersLookupableHelperServiceImpl extends AccountsReceivableLookupableHelperServiceImplBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GenerateDunningLettersLookupableHelperServiceImpl.class);
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected AccountService accountService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected DunningLetterService dunningLetterService;
    protected PersonService personService;

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        // Call search method to get results - always use unbounded to get the entire set of results.
        Collection<GenerateDunningLettersLookupResult> displayList = (Collection<GenerateDunningLettersLookupResult>) getSearchResultsUnbounded(lookupForm.getFieldsForLookup());

        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();
        Person user = GlobalVariables.getUserSession().getPerson();

        // Iterate through result list and wrap rows with return url and action urls
        for (GenerateDunningLettersLookupResult result : displayList) {
            List<String> invoiceAttributesForDisplay = result.getInvoiceAttributesForDisplay();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(result, user);
            // add list of awards to sub Result rows
            List<ResultRow> subResultRows = new ArrayList<ResultRow>();
            for (ContractsGrantsInvoiceDocument invoice : result.getInvoices()) {

                List<Column> subResultColumns = new ArrayList<Column>();
                InvoiceAccountDetail invoiceAccountDetail = new InvoiceAccountDetail();

                // set invoice account detail
                if (CollectionUtils.isNotEmpty(invoice.getAccountDetails())){
                    invoiceAccountDetail = invoice.getAccountDetails().get(0);
                }

                for (String propertyName : invoiceAttributesForDisplay) {
                    if (propertyName.equalsIgnoreCase(KFSPropertyConstants.ACCOUNT_NUMBER)) {
                        Account account = getAccountService().getByPrimaryId(invoiceAccountDetail.getChartOfAccountsCode(), invoiceAccountDetail.getAccountNumber());
                        subResultColumns.add(setupResultsColumn(account, propertyName, businessObjectRestrictions));
                    }
                    else if (propertyName.equalsIgnoreCase("dunningLetterTemplateSentDate")) {
                        InvoiceGeneralDetail invoiceGeneralDetail = invoice.getInvoiceGeneralDetail();
                        subResultColumns.add(setupResultsColumn(invoiceGeneralDetail, propertyName, businessObjectRestrictions));
                    }
                    else {
                        subResultColumns.add(setupResultsColumn(invoice, propertyName, businessObjectRestrictions));
                    }
                }

                ResultRow subResultRow = new ResultRow(subResultColumns, "", "");
                subResultRow.setObjectId(((PersistableBusinessObjectBase) invoice).getObjectId());
                subResultRows.add(subResultRow);
            }

            // Create main customer header row
            Collection<Column> columns = getColumns(result, businessObjectRestrictions);
            HtmlData returnUrl = getReturnUrl(result, lookupForm, returnKeys, businessObjectRestrictions);
            ContractsGrantsLookupResultRow row = new ContractsGrantsLookupResultRow((List<Column>) columns, subResultRows, returnUrl.constructCompleteHtmlTag(), getActionUrls(result, pkNames, businessObjectRestrictions));
            resultTable.add(row);
        }

        return displayList;
    }

    /**
     * overriding this method to convert the list of awards to a list of ContratcsGrantsInvoiceLookupResult
     *
     * @see org.kuali.core.lookup.Lookupable#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        Collection searchResultsCollection;
        // Get the list of invoices
        searchResultsCollection = getInvoiceDocumentsForDunningLetterLookup(fieldValues);
        return this.buildSearchResultList(searchResultsCollection, new Long(searchResultsCollection.size()));
    }

    /**
     * To retrieve invoices matching the dunning letter distribution lookup values.
     *
     * @param fieldValues
     * @return collection of DunningLetterDistributionLookupResult
     */
    protected Collection<GenerateDunningLettersLookupResult> getInvoiceDocumentsForDunningLetterLookup(Map<String, String> fieldValues) {
        // to get the search criteria
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String invoiceDocumentNumber = fieldValues.get(ArPropertyConstants.INVOICE_DOCUMENT_NUMBER);
        String awardTotal = fieldValues.get(ArConstants.AWARD_TOTAL);
        String accountNumber = fieldValues.get(KFSPropertyConstants.ACCOUNT_NUMBER);
        String reportOption = fieldValues.get(ArPropertyConstants.REPORT_OPTION);
        String chartCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String orgCode = fieldValues.get(KFSPropertyConstants.ORGANIZATION_CODE);

        Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments;
        Map<String, String> fieldValuesForInvoice = new HashMap<String, String>();
        if (ObjectUtils.isNotNull(proposalNumber) && StringUtils.isNotBlank(proposalNumber.toString()) && StringUtils.isNotEmpty(proposalNumber.toString())) {
            fieldValuesForInvoice.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);
        }
        if (ObjectUtils.isNotNull(customerNumber) && StringUtils.isNotBlank(customerNumber) && StringUtils.isNotEmpty(customerNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.CUSTOMER_NUMBER, customerNumber);
        }
        if (ObjectUtils.isNotNull(invoiceDocumentNumber) && StringUtils.isNotBlank(invoiceDocumentNumber) && StringUtils.isNotEmpty(invoiceDocumentNumber)) {
            fieldValuesForInvoice.put(KFSPropertyConstants.DOCUMENT_NUMBER, invoiceDocumentNumber);
        }
        if (ObjectUtils.isNotNull(awardTotal) && StringUtils.isNotBlank(awardTotal) && StringUtils.isNotEmpty(awardTotal)) {
            fieldValuesForInvoice.put(ArPropertyConstants.INVOICE_GENERAL_DETAIL+"."+ArConstants.AWARD_TOTAL, awardTotal);
        }
        if (ObjectUtils.isNotNull(accountNumber) && StringUtils.isNotBlank(accountNumber) && StringUtils.isNotEmpty(accountNumber)) {
            fieldValuesForInvoice.put(ArPropertyConstants.ACCOUNT_DETAILS_ACCOUNT_NUMBER, accountNumber);
        }
        fieldValuesForInvoice.put(ArPropertyConstants.OPEN_INVOICE_IND, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        fieldValuesForInvoice.put(ArPropertyConstants.DOCUMENT_STATUS_CODE, KFSConstants.DocumentStatusCodes.APPROVED);

        if (StringUtils.equalsIgnoreCase(reportOption, ArConstants.ReportOptionFieldValues.PROCESSING_ORG)) {
            if (StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_ORGANIZATION_CODE, orgCode);
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.PROCESSING_CHART_OF_ACCOUNT_CODE, chartCode);
            }
        } else {
            if (StringUtils.isNotBlank(chartCode) && StringUtils.isNotBlank(orgCode)) {
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILLED_BY_ORGANIZATION_CODE, orgCode);
                fieldValuesForInvoice.put(ArPropertyConstants.CustomerInvoiceDocumentFields.BILL_BY_CHART_OF_ACCOUNT_CODE, chartCode);
            }
        }

        cgInvoiceDocuments = contractsGrantsInvoiceDocumentService.retrieveAllCGInvoicesByCriteria(fieldValuesForInvoice);

        // To validate the invoices for any additional parameters.
        Collection<ContractsGrantsInvoiceDocument> eligibleInvoiceDocuments = validateInvoicesForDunningLetters(fieldValues, cgInvoiceDocuments);

        return getDunningLetterService().getPopulatedGenerateDunningLettersLookupResults(eligibleInvoiceDocuments);
    }

    protected Collection<ContractsGrantsInvoiceDocument> validateInvoicesForDunningLetters(Map<String, String> fieldValues, Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments) {
        Integer agingBucketStartValue = null;
        Integer agingBucketEndValue = null;

        // To get value for FINAL days past due.
        String stateAgencyFinalCutOffDate = parameterService.getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL_PARM, "0");
        String finalCutOffDate = parameterService.getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_FINAL_PARM, "0");
        Integer cutoffdateFinal = new Integer(finalCutOffDate);

        String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String campaignID = fieldValues.get(ArPropertyConstants.DunningCampaignFields.DUNNING_CAMPAIGN_ID);
        String collector = fieldValues.get(KFSPropertyConstants.PRINCIPAL_ID);
        String agingBucket = fieldValues.get(ArPropertyConstants.AGING_BUCKET);
        String collectorPrincName = fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);

        final Integer[] agingBucketStartAndEnd = getAgingBucketStartAndEnd(agingBucket, cutoffdateFinal);
        if (!ObjectUtils.isNull(agingBucketStartAndEnd)) {
            agingBucketStartValue = agingBucketStartAndEnd[0];
            agingBucketEndValue = agingBucketStartAndEnd[1];
        }
        Person user = GlobalVariables.getUserSession().getPerson();

        // walk through what we have, and do any extra filtering based on age and dunning campaign, if necessary
        Collection<ContractsGrantsInvoiceDocument> eligibleInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument invoice : cgInvoiceDocuments) {
            if (isInvoiceGenerallyEligibleForDunningLetter(invoice)
                    && doesInvoiceMatchCollectorCriteria(invoice, collector, collectorPrincName)
                    && contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, user.getPrincipalId())
                    && doesInvoiceMatchAwardCriteria(invoice, agencyNumber, campaignID)
                    && doesInvoiceFitWithinAgingBucket(invoice, agingBucket, agingBucketStartValue, agingBucketEndValue, stateAgencyFinalCutOffDate)) {

                final String foundDunningLetterTemplate = findMatchingDunningLetterTemplate(invoice, cutoffdateFinal, stateAgencyFinalCutOffDate);

                if (!StringUtils.isBlank(foundDunningLetterTemplate)) {
                    invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(foundDunningLetterTemplate);
                    businessObjectService.save(invoice.getInvoiceGeneralDetail());
                    eligibleInvoices.add(invoice);
                }

            }
        }
        return eligibleInvoices;
    }

    /**
     * Parses the agingBucket to determine the start value and end value for the bucket
     * @param agingBucket the aging bucket value to parse
     * @param cutoffDateFinal the final cutoff date, from a parameter
     * @return an array with the start value of the bucket in the first entry and the end value in the second, or null if the aging bucket value could not be parsed
     */
    protected Integer[] getAgingBucketStartAndEnd(String agingBucket, Integer cutoffDateFinal) {
        if (StringUtils.isNotBlank(agingBucket)) {
            Integer agingBucketStartValue = null;
            Integer agingBucketEndValue = null;
            if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)) {
                agingBucketStartValue = 0;
                agingBucketEndValue = 30;
            }
            // Including State agency final here just to get some default value in place. The value will be overridden later after
            // checking with the agency.
            else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL) || agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                agingBucketStartValue = cutoffDateFinal + 1;
                agingBucketEndValue = 0;
            }
            else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {
                agingBucketStartValue = 121;
                agingBucketEndValue = cutoffDateFinal;
            }
            else {
                agingBucketStartValue = new Integer(agingBucket.split("-")[0]);
                agingBucketEndValue = new Integer(agingBucket.split("-")[1]);
            }
            if (agingBucketStartValue != null && agingBucketEndValue != null) {
                Integer[] returnContainer = new Integer[2];
                returnContainer[0] = agingBucketStartValue;
                returnContainer[1] = agingBucketEndValue;
                return returnContainer;
            }
        }
        return null;
    }

    /**
     * Checks if the given contracts & grants invoice passes general rules about whether it is eligible for dunning letter matching
     * @param invoice the contracts & grants invoice to check
     * @return true if the invoice is eligigle based on general rules, false otherwise
     */
    protected boolean isInvoiceGenerallyEligibleForDunningLetter(ContractsGrantsInvoiceDocument invoice) {
        if (ObjectUtils.isNull(invoice.getAge())) {
            return false;
        }

        if (invoice.getAward() == null || invoice.getAward().getDunningCampaign() == null) {
            return false;
        }

        String dunningCampaignCode = invoice.getAward().getDunningCampaign();
        DunningCampaign dunningCampaign = businessObjectService.findBySinglePrimaryKey(DunningCampaign.class, dunningCampaignCode);
        if (ObjectUtils.isNull(dunningCampaign) || !dunningCampaign.isActive()) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the given contracts & grants invoice matches given collector criteria
     * @param invoice the contracts & grants invoice to check
     * @param collectorPrincipalIdParameter the collector principal id from the lookuop
     * @param collectorPrincipalNameParameter the collector principal name from the lookup
     * @return true if the invoice matches, false otherwise
     */
    protected boolean doesInvoiceMatchCollectorCriteria(ContractsGrantsInvoiceDocument invoice, String collectorPrincipalIdParameter, String collectorPrincipalNameParameter) {
        String collectorPrincipalId = collectorPrincipalIdParameter;
        boolean checkCollector = StringUtils.isNotBlank(collectorPrincipalId);
        boolean isCollector = true;

        if (!StringUtils.isBlank(collectorPrincipalNameParameter)) {
            checkCollector = true;
            Person collectorObj = personService.getPersonByPrincipalName(collectorPrincipalNameParameter);
            if (collectorObj != null) {
                collectorPrincipalId = collectorObj.getPrincipalId();
            }
            else {
                isCollector = false;
            }
        }

        if (checkCollector && (!isCollector || !contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, collectorPrincipalId))) {
            return false;
        }

        return true;
    }

    /**
     * Determines if the given contracts & grants invoice matches criteria associated with the invoice's award
     * @param invoice the contracts & grants invoice to check
     * @param agencyNumberParameter the agency number from the lookup
     * @param campaignIdParameter the campaign id from the lookup
     * @return true if the invoice matches the criteria, false otherwise
     */
    protected boolean doesInvoiceMatchAwardCriteria(ContractsGrantsInvoiceDocument invoice, String agencyNumberParameter, String campaignIdParameter) {
        if (StringUtils.isNotBlank(agencyNumberParameter) && (!StringUtils.equals(invoice.getAward().getAgencyNumber(), agencyNumberParameter))) {
            return false;
        }
        if (StringUtils.isNotBlank(campaignIdParameter) && (!StringUtils.equals(invoice.getAward().getDunningCampaign(), campaignIdParameter))) {
            return false;
        }
        return true;
    }

    /**
     * Determines if the given invoice matches the criteria given associated with the aging bucket specified in the lookup
     * @param invoice the contracts & grants invoice to check
     * @param agingBucket the type of the specified aging bucket
     * @param agingBucketStartValue the start age in days of the given aging bucket
     * @param agingBucketEndValue the end age in days of the given aging bucket
     * @param stateAgencyFinalCutOffDate value, from a parameter, for a different cutoff date which applies only to state agencies
     * @return true if the invoice matches, false otherwise
     */
    protected boolean doesInvoiceFitWithinAgingBucket(ContractsGrantsInvoiceDocument invoice, String agingBucket, Integer agingBucketStartValue, Integer agingBucketEndValue, String stateAgencyFinalCutOffDate) {
        if (agingBucketStartValue == null || agingBucketEndValue == null) {
            return true; // just skip
        }
        int agingBucketStart = agingBucketStartValue.intValue();
        int agingBucketEnd = agingBucketEndValue.intValue();
        if (invoice.getAward().getAgency().isStateAgencyIndicator()) {
            if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                agingBucketStart = new Integer(stateAgencyFinalCutOffDate) + 1;
                agingBucketEnd = 0;
            }
            else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {
                agingBucketStart = 121;
                agingBucketEnd = new Integer(stateAgencyFinalCutOffDate);
            }
        }

        if (StringUtils.equalsIgnoreCase(agingBucket, ArConstants.DunningLetters.DYS_PST_DUE_FINAL)) {
            if (invoice.getAward().getAgency().isStateAgencyIndicator() || invoice.getAge().intValue() < agingBucketStart) {
                return false;
            }
        }
        else if (StringUtils.equalsIgnoreCase(agingBucket, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
            if (!invoice.getAward().getAgency().isStateAgencyIndicator() || invoice.getAge().intValue() < agingBucketStart) {
                return false;
            }
        }
        else if (invoice.getAge().intValue() < agingBucketStart || invoice.getAge().intValue() > agingBucketEnd) {
            return false;
        }

        return true;
    }

    /**
     * Finds a matching dunning letter template from the distributions in the given campaign for the given invoice, or null if a matching dunning letter distribution could not be found
     * @param invoice invoice to find dunning letter template for
     * @param cutoffDateFinal the final cut-off age for contracts & grants invoices
     * @param stateAgencyFinalCutOffDate the state final cut-off age for contracts & grants invoices, which may be different from cutoffDateFinal
     * @return the name of the matching dunning letter template or null if no suitable template could be found
     */
    protected String findMatchingDunningLetterTemplate(ContractsGrantsInvoiceDocument invoice, Integer cutoffDateFinal, String stateAgencyFinalCutOffDate) {
        ContractsAndGrantsBillingAgency agency = invoice.getAward().getAgency();

        String dunningCampaignCode = invoice.getAward().getDunningCampaign();
        DunningCampaign dunningCampaign = businessObjectService.findBySinglePrimaryKey(DunningCampaign.class, dunningCampaignCode);

        List<DunningLetterDistribution> dunningLetterDistributions = dunningCampaign.getDunningLetterDistributions();
        if (CollectionUtils.isEmpty(dunningLetterDistributions)) {
            return null;
        }

        for (DunningLetterDistribution dunningLetterDistribution : dunningLetterDistributions) {
            DunningLetterTemplate dunningLetterTemplate = getBusinessObjectService().findBySinglePrimaryKey(DunningLetterTemplate.class, dunningLetterDistribution.getDunningLetterTemplate());

            String foundDunningLetterTemplate = null;
            if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)) {
                if ((invoice.getAge().intValue() >= 0) && (invoice.getAge().intValue() <= 30)) {
                    if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                        return dunningLetterDistribution.getDunningLetterTemplate();
                    }
                }
            }
            else if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_31_60)) {
                if ((invoice.getAge().intValue() > 30) && (invoice.getAge().intValue() <= 60)) {
                    if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                        return dunningLetterDistribution.getDunningLetterTemplate();
                    }
                }
            }
            else if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_61_90)) {
                if ((invoice.getAge().intValue() > 60) && (invoice.getAge().intValue() <= 90)) {
                    if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                        return dunningLetterDistribution.getDunningLetterTemplate();
                    }
                }
            }
            else if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_91_120)) {
                if ((invoice.getAge().intValue() > 90) && (invoice.getAge().intValue() <= 120)) {
                    if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                        return dunningLetterDistribution.getDunningLetterTemplate();
                    }
                }
            }
            else if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_121)) {
                int cutoffDate = cutoffDateFinal.intValue();
                if (agency.isStateAgencyIndicator()) {// To replace final with state agency final value
                    cutoffDate = Integer.parseInt(stateAgencyFinalCutOffDate);
                }
                if ((invoice.getAge().intValue() > 120) && (invoice.getAge().intValue() <= cutoffDate)) {
                    if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                        return dunningLetterDistribution.getDunningLetterTemplate();
                    }
                }
            }
            else if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_FINAL)) {
                if (!agency.isStateAgencyIndicator() && invoice.getAge().intValue() > cutoffDateFinal.intValue()) {
                    if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                        return dunningLetterDistribution.getDunningLetterTemplate();
                    }
                }
            }
            else if (StringUtils.equalsIgnoreCase(dunningLetterDistribution.getDaysPastDue(), ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                int cutoffDate = cutoffDateFinal.intValue();
                if (agency.isStateAgencyIndicator()) {// to replace final with state agency final value
                    cutoffDate = Integer.parseInt(stateAgencyFinalCutOffDate);
                    if ((invoice.getAge().intValue() > cutoffDate)) {
                        if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                            return dunningLetterDistribution.getDunningLetterTemplate();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * build the search result list from the given collection and the number of all qualified search results
     *
     * @param searchResultsCollection the given search results, which may be a subset of the qualified search results
     * @param actualSize the number of all qualified search results
     * @return the search result list with the given results and actual size
     */
    protected List buildSearchResultList(Collection searchResultsCollection, Long actualSize) {
        CollectionIncomplete results = new CollectionIncomplete(searchResultsCollection, actualSize);

        // Sort list if default sort column given
        List searchResults = results;
        List defaultSortColumns = getDefaultSortColumns();
        if (defaultSortColumns.size() > 0) {
            Collections.sort(results, new BeanPropertyComparator(defaultSortColumns, true));
        }
        return searchResults;
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsHelper(java.util.Map, boolean)
     */
    @Override
    protected List<? extends BusinessObject> getSearchResultsHelper(Map<String, String> fieldValues, boolean unbounded) {
        return getContractsAndGrantsModuleBillingService().lookupAwards(fieldValues, unbounded);
    }

    /**
     * @param element
     * @param attributeName
     * @return Column
     */
    protected Column setupResultsColumn(BusinessObject element, String attributeName, BusinessObjectRestrictions businessObjectRestrictions) {
        Column col = new Column();

        col.setPropertyName(attributeName);


        String columnTitle = getDataDictionaryService().getAttributeLabel(element.getClass(), attributeName);
        if (StringUtils.isBlank(columnTitle)) {
            columnTitle = getDataDictionaryService().getCollectionLabel(element.getClass(), attributeName);
        }
        col.setColumnTitle(columnTitle);
        col.setMaxLength(getDataDictionaryService().getAttributeMaxLength(element.getClass(), attributeName));

        try {
            Class formatterClass = getDataDictionaryService().getAttributeFormatter(element.getClass(), attributeName);
            Formatter formatter = null;
            if (ObjectUtils.isNotNull(formatterClass)) {
                formatter = (Formatter) formatterClass.newInstance();
                col.setFormatter(formatter);
            }

            // Pick off result column from result list, do formatting
            String propValue = KFSConstants.EMPTY_STRING;
            Object prop = ObjectUtils.getPropertyValue(element, attributeName);

            // Set comparator and formatter based on property type
            Class propClass = null;
            PropertyDescriptor propDescriptor = PropertyUtils.getPropertyDescriptor(element, col.getPropertyName());
            if (ObjectUtils.isNotNull(propDescriptor)) {
                propClass = propDescriptor.getPropertyType();
            }

            // Formatters
            if (ObjectUtils.isNotNull(prop)) {
                propValue = getContractsGrantsReportHelperService().formatByType(prop, formatter);
            }

            // Comparator
            col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
            col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));
            propValue = super.maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
            col.setPropertyValue(propValue);

            if (StringUtils.isNotBlank(propValue)) {
                col.setColumnAnchor(getInquiryUrl(element, col.getPropertyName()));
            }
        }
        catch (InstantiationException ie) {
            throw new RuntimeException("Unable to get new instance of formatter class for property " + col.getPropertyName(), ie);
        }
        catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            throw new RuntimeException("Cannot access PropertyType for property " + "'" + col.getPropertyName() + "' " + " on an instance of '" + element.getClass().getName() + "'.", ex);
        }
        return col;
    }


    /**
     * Constructs the list of columns for the search results. All properties for the column objects come from the DataDictionary.
     *
     * @param bo
     * @return Collection<Column>
     */
    protected Collection<Column> getColumns(BusinessObject bo, BusinessObjectRestrictions businessObjectRestrictions) {
        Collection<Column> columns = new ArrayList<Column>();

        for (String attributeName : getBusinessObjectDictionaryService().getLookupResultFieldNames(bo.getClass())) {
            columns.add(setupResultsColumn(bo, attributeName, businessObjectRestrictions));
        }
        return columns;
    }

    /**
     * Gets the contractsGrantsInvoiceDocumentService attribute.
     *
     * @return Returns the contractsGrantsInvoiceDocumentService.
     */
    public ContractsGrantsInvoiceDocumentService getContractsGrantsInvoiceDocumentService() {
        return contractsGrantsInvoiceDocumentService;
    }

    /**
     * Sets the contractsGrantsInvoiceDocumentService attribute value.
     *
     * @param contractsGrantsInvoiceDocumentService The contractsGrantsInvoiceDocumentService to set.
     */
    public void setContractsGrantsInvoiceDocumentService(ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService) {
        this.contractsGrantsInvoiceDocumentService = contractsGrantsInvoiceDocumentService;
    }

    public AccountService getAccountService() {
        return accountService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public ContractsAndGrantsModuleBillingService getContractsAndGrantsModuleBillingService() {
        return contractsAndGrantsModuleBillingService;
    }

    public void setContractsAndGrantsModuleBillingService(ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService) {
        this.contractsAndGrantsModuleBillingService = contractsAndGrantsModuleBillingService;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }

    public DunningLetterService getDunningLetterService() {
        return dunningLetterService;
    }

    public void setDunningLetterService(DunningLetterService dunningLetterService) {
        this.dunningLetterService = dunningLetterService;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}