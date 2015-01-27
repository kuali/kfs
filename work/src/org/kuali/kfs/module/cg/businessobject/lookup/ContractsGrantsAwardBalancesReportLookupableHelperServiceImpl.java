/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2014 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.ContractsGrantsAwardBalancesReport;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.mortbay.log.Log;

/**
 * Helper service class for Contracts & Grants Award Balances Report
 */
public class ContractsGrantsAwardBalancesReportLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final Log LOG = LogFactory.getLog(ContractsGrantsAwardBalancesReportLookupableHelperServiceImpl.class);

    protected AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;
    protected DateTimeService dateTimeService;

    /**
     * Validating date fields
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        final String awardBeginningDateFrom = fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+KFSPropertyConstants.AWARD_BEGINNING_DATE);
        validateDateField(awardBeginningDateFrom, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+KFSPropertyConstants.AWARD_BEGINNING_DATE);
        final String awardBeginningDateTo = fieldValues.get(KFSPropertyConstants.AWARD_BEGINNING_DATE);
        validateDateField(awardBeginningDateTo, KFSPropertyConstants.AWARD_BEGINNING_DATE);

        final String awardEndingDateFrom = fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+KFSPropertyConstants.AWARD_BEGINNING_DATE);
        validateDateField(awardEndingDateFrom, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+KFSPropertyConstants.AWARD_BEGINNING_DATE);
        final String awardEndingDateTo = fieldValues.get(KFSPropertyConstants.AWARD_ENDING_DATE);
        validateDateField(awardEndingDateTo, KFSPropertyConstants.AWARD_ENDING_DATE);
    }

    /**
     * This method performs the lookup and returns a collection of lookup items
     *
     * @param lookupForm
     * @param kualiLookupable
     * @param resultTable
     * @param bounded
     * @return
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean unbounded) {
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Map<String, String> lookupCriteria = buildCriteriaForLookup(lookupFormFields);

        Collection<ContractsGrantsAwardBalancesReport> displayList = new ArrayList<ContractsGrantsAwardBalancesReport>();

        Collection<Award> awards = getLookupService().findCollectionBySearchHelper(Award.class, lookupCriteria, true);

        // build search result fields

        for (Award award : awards) {
            ContractsGrantsAwardBalancesReport awardBalancesReportEntry = new ContractsGrantsAwardBalancesReport();

            awardBalancesReportEntry.setProposalNumber(award.getProposalNumber());
            awardBalancesReportEntry.setAgencyNumber(award.getAgencyNumber());
            awardBalancesReportEntry.setAgency(award.getAgency());
            awardBalancesReportEntry.setAwardProjectTitle(award.getAwardProjectTitle());
            awardBalancesReportEntry.setAwardStatusCode(award.getAwardStatusCode());
            awardBalancesReportEntry.setAwardBeginningDate(award.getAwardBeginningDate());
            awardBalancesReportEntry.setAwardEndingDate(award.getAwardEndingDate());
            String primaryProjectDirectorName = (ObjectUtils.isNull(award.getAwardPrimaryProjectDirector())) || (ObjectUtils.isNull(award.getAwardPrimaryProjectDirector().getProjectDirector())) ? "" : award.getAwardPrimaryProjectDirector().getProjectDirector().getName();
            awardBalancesReportEntry.setAwardPrimaryProjectDirectorName(primaryProjectDirectorName);

            String primaryFundManagerName = (ObjectUtils.isNull(award.getAwardPrimaryFundManager())) || (ObjectUtils.isNull(award.getAwardPrimaryFundManager().getFundManager())) ? "" : award.getAwardPrimaryFundManager().getFundManager().getName();
            awardBalancesReportEntry.setAwardPrimaryFundManagerName(primaryFundManagerName);

            awardBalancesReportEntry.setAwardTotalAmountForReport(award.getAwardTotalAmount());

            KualiDecimal awardBilledToDateAmount = getAccountsReceivableModuleBillingService().getAwardBilledToDateAmountByProposalNumber(award.getProposalNumber());
            awardBalancesReportEntry.setTotalBilledToDate(awardBilledToDateAmount);

            // calculate Total Payments To Date
            KualiDecimal totalPayments = getAccountsReceivableModuleBillingService().calculateTotalPaymentsToDateByAward(award.getProposalNumber());
            awardBalancesReportEntry.setTotalPaymentsToDate(totalPayments);
            awardBalancesReportEntry.setAmountCurrentlyDue(awardBilledToDateAmount.subtract(totalPayments));

            displayList.add(awardBalancesReportEntry);
        }
        buildResultTable(lookupForm, displayList, resultTable);
        return displayList;
    }

    /**
     * Translates the given lookup fields into a Map of criteria to send to the lookup service
     * @param lookupFormFields the Map of lookup fields straight from the form
     * @return a Map of criteria actually useful for the lookup
     */
    protected Map<String, String> buildCriteriaForLookup(Map lookupFormFields) {
        Map<String, String> criteria = new HashMap<>();

        final String proposalNumber = (String)lookupFormFields.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        criteria.put(KFSPropertyConstants.PROPOSAL_NUMBER, proposalNumber);

        final String agencyNumber = (String)lookupFormFields.get(KFSPropertyConstants.AGENCY_NUMBER);
        criteria.put(KFSPropertyConstants.AGENCY_NUMBER, agencyNumber);

        final String awardProjectTitle = (String)lookupFormFields.get(KFSPropertyConstants.AWARD_PROJECT_TITLE);
        criteria.put(KFSPropertyConstants.AWARD_PROJECT_TITLE, awardProjectTitle);

        final String awardBeginningDateFrom = (String)lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+KFSPropertyConstants.AWARD_BEGINNING_DATE);
        final String awardBeginningDateTo = (String)lookupFormFields.get(KFSPropertyConstants.AWARD_BEGINNING_DATE);
        final String awardBeginningDate = fixDateCriteria(awardBeginningDateFrom, awardBeginningDateTo);
        criteria.put(KFSPropertyConstants.AWARD_BEGINNING_DATE, awardBeginningDate);

        final String awardEndingDateFrom = (String)lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+KFSPropertyConstants.AWARD_BEGINNING_DATE);
        final String awardEndingDateTo = (String)lookupFormFields.get(KFSPropertyConstants.AWARD_ENDING_DATE);
        final String awardEndingDate = fixDateCriteria(awardEndingDateFrom, awardEndingDateTo);
        criteria.put(KFSPropertyConstants.AWARD_ENDING_DATE, awardEndingDate);

        final String awardStatusCode = (String)lookupFormFields.get(KFSPropertyConstants.AWARD_STATUS_CODE);
        criteria.put(KFSPropertyConstants.AWARD_STATUS_CODE, awardStatusCode);

        return criteria;
    }

    /**
     * This methods builds result table for the lookup results.
     *
     * @param lookupForm
     * @param displayList
     * @param resultTable
     */
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action
        // urls
        for (Iterator iter = displayList.iterator(); iter.hasNext();) {
            BusinessObject element = (BusinessObject) iter.next();

            BusinessObjectRestrictions businessObjectRestrictions = getBusinessObjectAuthorizationService().getLookupResultRestrictions(element, user);

            List<Column> columns = getColumns();
            for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
                Column col = (Column) iterator.next();

                String propValue = ObjectUtils.getFormattedPropertyValue(element, col.getPropertyName(), col.getFormatter());
                Class propClass = getPropertyClass(element, col.getPropertyName());

                col.setComparator(CellComparatorHelper.getAppropriateComparatorForPropertyClass(propClass));
                col.setValueComparator(CellComparatorHelper.getAppropriateValueComparatorForPropertyClass(propClass));

                String propValueBeforePotientalMasking = propValue;
                propValue = maskValueIfNecessary(element.getClass(), col.getPropertyName(), propValue, businessObjectRestrictions);
                col.setPropertyValue(propValue);
            }

            ResultRow row = new ResultRow(columns, "", ACTION_URLS_EMPTY);

            if (getBusinessObjectDictionaryService().isExportable(getBusinessObjectClass())) {
                row.setBusinessObject(element);
            }
            boolean rowReturnable = isResultReturnable(element);
            row.setRowReturnable(rowReturnable);
            if (rowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);
    }

    /**
     * This method provides title text for the report
     *
     * @param boClass
     * @return
     */
    protected String createTitleText(Class<? extends BusinessObject> boClass) {
        String titleText = "";

        final String titlePrefixProp = getKualiConfigurationService().getPropertyValueAsString("title.inquiry.url.value.prependtext");
        if (StringUtils.isNotBlank(titlePrefixProp)) {
            titleText += titlePrefixProp + " ";
        }

        final String objectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClass.getName()).getObjectLabel();
        if (StringUtils.isNotBlank(objectLabel)) {
            titleText += objectLabel + " ";
        }

        return titleText;
    }

    /**
     * Translates the date criteria to a form which the LookupService will comprehend
     * @param dateLowerBound the lower bound of the date
     * @param dateUpperBound the upper bound of the date
     * @return the date criteria, or null if nothing could be constructed
     */
    protected String fixDateCriteria(String dateLowerBound, String dateUpperBound) {
        if (!StringUtils.isBlank(dateLowerBound)) {
            if (!StringUtils.isBlank(dateUpperBound)) {
                return dateLowerBound+".."+dateUpperBound;
            } else {
                return ">="+dateLowerBound;
            }
        } else {
            if (!StringUtils.isBlank(dateUpperBound)) {
                return "<="+dateUpperBound;
            }
        }
        return null;
    }

    /**
     * Convenience method to validate a date from the lookup criteria
     * @param dateFieldValue the value of the field from the lookup criteria
     * @param dateFieldClass the class being looked up
     * @param dateFieldPropertyName the property name representing the date
     */
    protected void validateDateField(String dateFieldValue, String dateFieldPropertyName) {
        if (!StringUtils.isBlank(dateFieldValue)) {
            try {
                getDateTimeService().convertToDate(dateFieldValue);
            }
            catch (ParseException pe) {
                addDateTimeError(dateFieldPropertyName);
            }
        }
    }

    /**
     * Adds an appropriate error about the date or date time field, with the property name given by dateFieldPropertyName, being unparsable
     * @param dateFieldPropertyName the property name which has the error
     */
    protected void addDateTimeError(String dateFieldPropertyName) {
        final String attributeProperty = dateFieldPropertyName.startsWith(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX) ?
                dateFieldPropertyName.substring(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX.length()) :
                dateFieldPropertyName;
        final String label = getDataDictionaryService().getAttributeLabel(getBusinessObjectClass(), attributeProperty);
        GlobalVariables.getMessageMap().putError(dateFieldPropertyName, KFSKeyConstants.ERROR_DATE_TIME, label);
    }

    public AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        return accountsReceivableModuleBillingService;
    }


    public void setAccountsReceivableModuleBillingService(AccountsReceivableModuleBillingService accountsReceivableModuleBillingService) {
        this.accountsReceivableModuleBillingService = accountsReceivableModuleBillingService;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
