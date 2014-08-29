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
import org.kuali.kfs.module.ar.businessobject.DunningLetterDistributionLookupResult;
import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.businessobject.InvoiceAccountDetail;
import org.kuali.kfs.module.ar.businessobject.InvoiceGeneralDetail;
import org.kuali.kfs.module.ar.businessobject.inquiry.DunningLetterDistributionLookupResultInquirableImpl;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.service.ContractsGrantsInvoiceDocumentService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.module.ar.web.ui.DunningLetterDistributionResultRow;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
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
 * Defines a lookupable helper service class for Dunning Letter Distribution.
 */
public class DunningLetterDistributionLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DunningLetterDistributionLookupableHelperServiceImpl.class);
    protected ContractsGrantsInvoiceDocumentService contractsGrantsInvoiceDocumentService;
    protected AccountService accountService;
    protected ContractsAndGrantsModuleBillingService contractsAndGrantsModuleBillingService;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
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
        Collection<DunningLetterDistributionLookupResult> displayList = (Collection<DunningLetterDistributionLookupResult>) getSearchResultsUnbounded(lookupForm.getFieldsForLookup());

        List pkNames = getBusinessObjectMetaDataService().listPrimaryKeyFieldNames(getBusinessObjectClass());
        List returnKeys = getReturnKeys();
        Person user = GlobalVariables.getUserSession().getPerson();

        // Iterate through result list and wrap rows with return url and action urls
        for (DunningLetterDistributionLookupResult result : displayList) {
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
            DunningLetterDistributionResultRow row = new DunningLetterDistributionResultRow((List<Column>) columns, subResultRows, returnUrl.constructCompleteHtmlTag(), getActionUrls(result, pkNames, businessObjectRestrictions));
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
    protected Collection<DunningLetterDistributionLookupResult> getInvoiceDocumentsForDunningLetterLookup(Map<String, String> fieldValues) {
        // to get the search criteria
        String proposalNumber = fieldValues.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        String customerNumber = fieldValues.get(ArPropertyConstants.CustomerInvoiceWriteoffLookupResultFields.CUSTOMER_NUMBER);
        String invoiceDocumentNumber = fieldValues.get(ArPropertyConstants.INVOICE_DOCUMENT_NUMBER);
        String awardTotal = fieldValues.get("awardTotal");
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
            fieldValuesForInvoice.put("invoiceGeneralDetail.awardTotal", awardTotal);
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
        cgInvoiceDocuments = contractsGrantsInvoiceDocumentService.attachWorkflowHeadersToCGInvoices(cgInvoiceDocuments);

        // To validate the invoices for any additional parameters.
        Collection<ContractsGrantsInvoiceDocument> eligibleInvoiceDocuments = validateInvoicesForDunningLetters(fieldValues, cgInvoiceDocuments);

        return DunningLetterDistributionLookupUtil.getPopulatedDunningLetterDistributionLookupResults(eligibleInvoiceDocuments);
    }

    protected Collection<ContractsGrantsInvoiceDocument> validateInvoicesForDunningLetters(Map<String, String> fieldValues, Collection<ContractsGrantsInvoiceDocument> cgInvoiceDocuments) {
        Integer agingBucketStartValue = null;
        Integer agingBucketEndValue = null;
        Integer cutoffdate0 = 0;
        Integer cutoffdate30 = 30;
        Integer cutoffdate60 = 60;
        Integer cutoffdate90 = 90;
        Integer cutoffdate120 = 120;

        // To get value for FINAL days past due.
        String stateAgencyFinalCutOffDate = null;
        String finalCutOffDate = parameterService.getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_FINAL_PARM);
        if (ObjectUtils.isNull(finalCutOffDate)) {
            finalCutOffDate = "0";
        }
        Integer cutoffdateFinal = new Integer(finalCutOffDate);
        String agencyNumber = fieldValues.get(KFSPropertyConstants.AGENCY_NUMBER);
        String campaignID = fieldValues.get(ArPropertyConstants.DunningCampaignFields.DUNNING_CAMPAIGN_ID);
        String collector = fieldValues.get(KFSPropertyConstants.PRINCIPAL_ID);
        String agingBucket = fieldValues.get("agingBucket");
        String collectorPrincName = fieldValues.get(ArPropertyConstants.COLLECTOR_PRINC_NAME);

        if (StringUtils.isNotBlank(agingBucket)) {
            if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)) {
                agingBucketStartValue = 0;
                agingBucketEndValue = 30;
            }
            // Including State agency final here just to get some default value in place. The value will be overridden later after
            // checking with the agency.
            else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL) || agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                agingBucketStartValue = cutoffdateFinal + 1;
                agingBucketEndValue = 0;
            }
            else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {
                agingBucketStartValue = 121;
                agingBucketEndValue = cutoffdateFinal;
            }
            else if (StringUtils.isNotBlank(agingBucket)) {
                agingBucketStartValue = new Integer(agingBucket.split("-")[0]);
                agingBucketEndValue = new Integer(agingBucket.split("-")[1]);
            }
        }

        // check other categories
        boolean checkCollector = StringUtils.isNotBlank(collector);
        boolean isCollector = true;

        if (ObjectUtils.isNotNull(collectorPrincName) && StringUtils.isNotEmpty(collectorPrincName.trim())) {
            checkCollector = true;
            Person collectorObj = personService.getPersonByPrincipalName(collectorPrincName);
            if (collectorObj != null) {
                collector = collectorObj.getPrincipalId();
            }
            else {
                isCollector = false;
            }
        }

        // walk through what we have, and do any extra filtering based on age and dunning campaign, if necessary
        boolean eligibleInvoiceFlag;
        Collection<ContractsGrantsInvoiceDocument> eligibleInvoices = new ArrayList<ContractsGrantsInvoiceDocument>();
        for (ContractsGrantsInvoiceDocument invoice : cgInvoiceDocuments) {
            eligibleInvoiceFlag = false;
            if (ObjectUtils.isNotNull(invoice.getAge())) {

                if (invoice.getAward() == null || invoice.getAward().getDunningCampaign() == null) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                String dunningCampaignCode = invoice.getAward().getDunningCampaign();

                DunningCampaign dunningCampaign = businessObjectService.findBySinglePrimaryKey(DunningCampaign.class, dunningCampaignCode);
                if (ObjectUtils.isNull(dunningCampaign) || !dunningCampaign.isActive()) {
                    eligibleInvoiceFlag = false;
                    continue;
                }

                if (checkCollector) {
                    if (isCollector) {
                        if (!contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, collector)) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                    } else {
                        eligibleInvoiceFlag = false;
                        continue;
                    }
                }

                Person user = GlobalVariables.getUserSession().getPerson();

                if (!contractsGrantsInvoiceDocumentService.canViewInvoice(invoice, user.getPrincipalId())) {
                    eligibleInvoiceFlag = false;
                    continue;
                }

                if (StringUtils.isNotBlank(agencyNumber) && (!StringUtils.equals(invoice.getAward().getAgencyNumber(), agencyNumber))) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                if (StringUtils.isNotBlank(campaignID) && (!StringUtils.equals(invoice.getAward().getDunningCampaign(), campaignID))) {
                    eligibleInvoiceFlag = false;
                    continue;
                }

                // To override agingBucketStartValue and agingBucketEndValue if State Agency Final is true.

                ContractsAndGrantsBillingAgency agency = invoice.getAward().getAgency();
                if (agency.isStateAgencyIndicator()) {
                    stateAgencyFinalCutOffDate = parameterService.getParameterValueAsString(DunningCampaign.class, ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL_PARM);
                }
                if (ObjectUtils.isNotNull(stateAgencyFinalCutOffDate) && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {

                    agingBucketStartValue = new Integer(stateAgencyFinalCutOffDate) + 1;
                    agingBucketEndValue = 0;
                }
                else if (ObjectUtils.isNotNull(stateAgencyFinalCutOffDate) && agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {

                    agingBucketStartValue = 121;
                    agingBucketEndValue = new Integer(stateAgencyFinalCutOffDate);
                }
                // Now to validate based on agingbucket and make sure the agency = stateagency is applied.
                if (ObjectUtils.isNotNull(agingBucketStartValue)) {
                    if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL)) {
                        if (agency.isStateAgencyIndicator()) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                        else {
                            if ((invoice.getAge().compareTo(agingBucketStartValue) < 0)) {
                                eligibleInvoiceFlag = false;
                                continue;
                            }
                        }
                    }
                    else if (agingBucket.equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                        if (!agency.isStateAgencyIndicator()) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                        else {
                            if ((invoice.getAge().compareTo(agingBucketStartValue) < 0)) {
                                eligibleInvoiceFlag = false;
                                continue;
                            }
                        }
                    }
                    else {
                        if ((invoice.getAge().compareTo(agingBucketStartValue) < 0) || (invoice.getAge().compareTo(agingBucketEndValue) > 0)) {
                            eligibleInvoiceFlag = false;
                            continue;
                        }
                    }
                }

                List<DunningLetterDistribution> dunningLetterDistributions = dunningCampaign.getDunningLetterDistributions();
                if (dunningLetterDistributions.isEmpty()) {
                    eligibleInvoiceFlag = false;
                    continue;
                }
                for (DunningLetterDistribution dunningLetterDistribution : dunningLetterDistributions) {

                    DunningLetterTemplate dunningLetterTemplate = businessObjectService.findBySinglePrimaryKey(DunningLetterTemplate.class, dunningLetterDistribution.getDunningLetterTemplate());

                    if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_CURRENT)) {
                        if ((invoice.getAge().compareTo(cutoffdate0) >= 0) && (invoice.getAge().compareTo(cutoffdate30) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_31_60)) {
                        if ((invoice.getAge().compareTo(cutoffdate30) > 0) && (invoice.getAge().compareTo(cutoffdate60) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_61_90)) {
                        if ((invoice.getAge().compareTo(cutoffdate60) > 0) && (invoice.getAge().compareTo(cutoffdate90) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_91_120)) {
                        if ((invoice.getAge().compareTo(cutoffdate90) > 0) && (invoice.getAge().compareTo(cutoffdate120) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_121)) {
                        if (agency.isStateAgencyIndicator()) {// To replace final with state agency final value
                            cutoffdateFinal = new Integer(stateAgencyFinalCutOffDate);
                        }
                        if ((invoice.getAge().compareTo(cutoffdate120) > 0) && (invoice.getAge().compareTo(cutoffdateFinal) <= 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }

                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_FINAL)) {
                        if (agency.isStateAgencyIndicator()) {// to proceed only if agency is not state agency
                            continue;
                        }
                        else {
                            if ((invoice.getAge().compareTo(cutoffdateFinal) > 0)) {
                                if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                    eligibleInvoiceFlag = true;
                                    invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                    break;
                                }
                            }
                        }
                    }
                    else if (dunningLetterDistribution.getDaysPastDue().equalsIgnoreCase(ArConstants.DunningLetters.DYS_PST_DUE_STATE_AGENCY_FINAL)) {
                        if (agency.isStateAgencyIndicator()) {// to replace final with state agency final value
                            cutoffdateFinal = new Integer(stateAgencyFinalCutOffDate);
                        }
                        else {// If the agency is not state agency - nothing to calculate.
                            continue;
                        }
                        if ((invoice.getAge().compareTo(cutoffdateFinal) > 0)) {
                            if (dunningLetterDistribution.isActiveIndicator() && dunningLetterDistribution.isSendDunningLetterIndicator() && dunningLetterTemplate.isActive() && ObjectUtils.isNotNull(dunningLetterTemplate.getFilename())) {
                                eligibleInvoiceFlag = true;
                                invoice.getInvoiceGeneralDetail().setDunningLetterTemplateAssigned(dunningLetterDistribution.getDunningLetterTemplate());
                                break;
                            }
                        }
                    }
                }
            }
            else {
                eligibleInvoiceFlag = false;
            }

            if (eligibleInvoiceFlag) {
                businessObjectService.save(invoice.getInvoiceGeneralDetail());
                eligibleInvoices.add(invoice);
            }
        }
        return eligibleInvoices;
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
     * Since there aren't that many fields for inquiry, just deal with each of them one by one for this lookup
     *
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        return (new DunningLetterDistributionLookupResultInquirableImpl()).getInquiryUrl(bo, propertyName);
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

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }
}