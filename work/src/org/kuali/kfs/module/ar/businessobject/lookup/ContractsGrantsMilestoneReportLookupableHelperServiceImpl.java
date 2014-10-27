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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsMilestoneReport;
import org.kuali.kfs.module.ar.businessobject.Milestone;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Defines a custom lookup for a Milestone Reports.
 */
public class ContractsGrantsMilestoneReportLookupableHelperServiceImpl extends ContractsGrantsReportLookupableHelperServiceImplBase {
    protected DateTimeService dateTimeService;
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
        Map lookupFormFields = lookupForm.getFieldsForLookup();

        setBackLocation((String) lookupForm.getFieldsForLookup().get(KRADConstants.BACK_LOCATION));
        setDocFormKey((String) lookupForm.getFieldsForLookup().get(KRADConstants.DOC_FORM_KEY));

        Collection<ContractsGrantsMilestoneReport> displayList = new ArrayList<ContractsGrantsMilestoneReport>();
        Map<String, String> lookupCriteria = buildCriteriaForLookup(lookupFormFields);
        Collection<Milestone> milestones = getLookupService().findCollectionBySearchHelper(Milestone.class, lookupCriteria, true);

        final String awardChartOfAccountsCode = (String)lookupFormFields.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        final String awardAccountNumber = (String)lookupFormFields.get(KFSPropertyConstants.ACCOUNT_NUMBER);

        // build search result fields
        for (Milestone milestone : milestones) {

            if (isMilestoneMatchesChart(milestone, awardChartOfAccountsCode) && isMilestoneMatchesAccount(milestone, awardAccountNumber)){
                ContractsGrantsMilestoneReport cgMilestoneReport = new ContractsGrantsMilestoneReport();
                cgMilestoneReport.setProposalNumber(milestone.getProposalNumber());

                ContractsAndGrantsBillingAward award = milestone.getAward();
                List<ContractsAndGrantsBillingAwardAccount> awardAccounts = (ObjectUtils.isNull(award)) ? new ArrayList() : award.getActiveAwardAccounts();
                String accountNumber = (awardAccounts.size() > 0) ? awardAccounts.get(0).getAccountNumber() : "";

                cgMilestoneReport.setAccountNumber(accountNumber);
                cgMilestoneReport.setMilestoneNumber(milestone.getMilestoneNumber());
                cgMilestoneReport.setMilestoneExpectedCompletionDate(milestone.getMilestoneExpectedCompletionDate());
                cgMilestoneReport.setMilestoneAmount(milestone.getMilestoneAmount());
                cgMilestoneReport.setBilled(milestone.isBilled());
                cgMilestoneReport.setActive(milestone.isActive());

                displayList.add(cgMilestoneReport);
            }
        }

        buildResultTable(lookupForm, displayList, resultTable);

        return displayList;
    }

    /**
     * Builds criteria which the Milestone lookup will be a bit happier with, as it uses names from Milestone, not the report BO
     * @param lookupFormFields the fields from the lookup itself
     * @return a converted Map of fields which can be used to search for Milestones
     */
    protected Map<String, String> buildCriteriaForLookup(Map lookupFormFields) {
        Map<String, String> lookupCriteria = new HashMap<String, String>();

        final String proposalNumber = (String)lookupFormFields.get(KFSPropertyConstants.PROPOSAL_NUMBER);
        if (!StringUtils.isBlank(proposalNumber)) {
            lookupCriteria.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentFields.PROPOSAL_NUMBER, proposalNumber);
        }

        final String lowerBoundMilestoneExpectedCompletionDate = (String)lookupFormFields.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE);
        final String upperBoundMilestoneExpectedCompletionDate = (String)lookupFormFields.get(ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE);
        final String milestoneExpectedCompletionDate = getContractsGrantsReportHelperService().fixDateCriteria(lowerBoundMilestoneExpectedCompletionDate, upperBoundMilestoneExpectedCompletionDate, false);
        if (!StringUtils.isBlank(milestoneExpectedCompletionDate)) {
            lookupCriteria.put(ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE, milestoneExpectedCompletionDate);
        }

        final String billed = (String)lookupFormFields.get(ArPropertyConstants.BILLED);
        if (!StringUtils.isBlank(billed)) {
            lookupCriteria.put(ArPropertyConstants.BILLED, billed);
        }

        final String active = (String)lookupFormFields.get(KFSPropertyConstants.ACTIVE);
        if (!StringUtils.isBlank(active)) {
            lookupCriteria.put(KFSPropertyConstants.ACTIVE, active);
        }

        return lookupCriteria;
    }

    /**
     * Determines if the given milestone matches the given chart
     * @param milestone the milestone to check
     * @param chartOfAccountsCode the chart to check
     * @return true if there is no chart to check, or if there are no award accounts to check against, or if it finds a match; false otherwise
     */
    protected boolean isMilestoneMatchesChart(Milestone milestone, String chartOfAccountsCode) {
        if (StringUtils.isBlank(chartOfAccountsCode)) { // no chart to check
            return true;
        }
        if (ObjectUtils.isNull(milestone.getAward()) || ObjectUtils.isNull(milestone.getAward().getActiveAwardAccounts()) || milestone.getAward().getActiveAwardAccounts().isEmpty()) { // oddly, no award accounts to check against
            return true;
        }
        Pattern chartRegex = convertWildcardsToPattern(chartOfAccountsCode);
        for (ContractsAndGrantsBillingAwardAccount awardAccount : milestone.getAward().getActiveAwardAccounts()) {
            Matcher chartMatch = chartRegex.matcher(awardAccount.getChartOfAccountsCode());
            if (chartMatch.matches()) {
                return true; // we found a match
            }
        }
        return false;
    }

    /**
     * Determines if the given milestone matches the given account
     * @param milestone the milestone to check against
     * @param accountNumber the account number to check
     * @return true if there is no account number to check, if there are no award accounts to check against, or if it finds a match; false otherwise
     */
    protected boolean isMilestoneMatchesAccount(Milestone milestone, String accountNumber) {
        if (StringUtils.isBlank(accountNumber)) {
            return true;
        }
        if (ObjectUtils.isNull(milestone.getAward()) || ObjectUtils.isNull(milestone.getAward().getActiveAwardAccounts()) || milestone.getAward().getActiveAwardAccounts().isEmpty()) { // oddly, no award accounts to check against
            return true;
        }
        Pattern accountRegex = convertWildcardsToPattern(accountNumber);
        for (ContractsAndGrantsBillingAwardAccount awardAccount : milestone.getAward().getActiveAwardAccounts()) {
            Matcher accountMatch = accountRegex.matcher(awardAccount.getAccountNumber());
            if (accountMatch.matches()) {
                return true; // we found a match
            }
        }
        return false;
    }

    /**
     * Converts the given String into a regex pattern, replacing "*" with ".+" for the regex
     * @param lookupFieldValue the field value to convert into a regex
     * @return the regex pattern
     */
    protected Pattern convertWildcardsToPattern(String lookupFieldValue) {
        final String lookupFieldValueRegex = lookupFieldValue.replaceAll("\\*", ".+");
        final Pattern pat = Pattern.compile(lookupFieldValueRegex);
        return pat;
    }

    @Override
    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;
        // iterate through result list and wrap rows with return url and action url
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
            boolean isRowReturnable = isResultReturnable(element);
            row.setRowReturnable(isRowReturnable);
            if (isRowReturnable) {
                hasReturnableRow = true;
            }
            resultTable.add(row);
        }
        lookupForm.setHasReturnableRow(hasReturnableRow);
    }

    /**
     * Validate the milestone expected completion date field as a date
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#validateSearchParameters(java.util.Map)
     */
    @Override
    public void validateSearchParameters(Map<String, String> fieldValues) {
        super.validateSearchParameters(fieldValues);

        final String lowerBoundMilestoneExpectedCompletionDate = fieldValues.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE);
        validateDateField(lowerBoundMilestoneExpectedCompletionDate, KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX+ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE, getDateTimeService());

        final String upperBoundMilestoneExpectedCompletionDate = fieldValues.get(ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE);
        validateDateField(upperBoundMilestoneExpectedCompletionDate, ArPropertyConstants.MILESTONE_EXPECTED_COMPLETION_DATE, getDateTimeService());
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
