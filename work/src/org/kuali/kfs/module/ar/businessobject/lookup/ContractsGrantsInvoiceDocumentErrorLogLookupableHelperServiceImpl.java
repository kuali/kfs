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

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class ContractsGrantsInvoiceDocumentErrorLogLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    protected DateTimeService dateTimeService;

    /**
     * Overridden so we can manipulate the search criteria fields.
     *
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        return getSearchResultsHelper(
                org.kuali.rice.krad.lookup.LookupUtils.forceUppercase(getBusinessObjectClass(), updateFieldValuesForSearchCriteria(fieldValues)), false);
    }


    /**
     * Overridden so we can manipulate the search criteria fields.
     *
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResultsUnbounded(Map<String, String> fieldValues) {
        return getSearchResultsHelper(
                org.kuali.rice.krad.lookup.LookupUtils.forceUppercase(getBusinessObjectClass(), updateFieldValuesForSearchCriteria(fieldValues)), true);
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject, java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject bo, String propertyName) {
        AnchorHtmlData hRef = (AnchorHtmlData)super.getInquiryUrl(bo, propertyName);
        if (StringUtils.isNotBlank(hRef.getHref())) {
            hRef.setHref(KFSConstants.RICE_PATH_PREFIX + hRef.getHref());
        }
        return hRef;
    }

    /**
     * Manipulate fields for search criteria in order to get the results the user really wants.
     *
     * @param fieldValues
     * @return updated search criteria fieldValues
     */
    protected Map<String, String> updateFieldValuesForSearchCriteria(Map<String, String> fieldValues) {
        Map<String, String> newFieldValues = new HashMap<>();
        newFieldValues.putAll(fieldValues);

        // Add wildcard character to start and end of accounts field so users can search for single account
        // within the delimited list of accounts without having to add the wildcards explicitly themselves.
        String accounts = newFieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ACCOUNTS);
        if (StringUtils.isNotBlank(accounts)) {
            // only add wildcards if they haven't already been added (for some reason this method gets called twice when generating the pdf report)
            if (!StringUtils.startsWith(accounts, KFSConstants.WILDCARD_CHARACTER)) {
                accounts = KFSConstants.WILDCARD_CHARACTER + accounts;
            }
            if (!StringUtils.endsWith(accounts, KFSConstants.WILDCARD_CHARACTER)) {
                accounts += KFSConstants.WILDCARD_CHARACTER;
            }
        }
        newFieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ACCOUNTS, accounts);

        // Increment to error date by one day so that the correct results are retrieved.
        // Since the error date is stored as both a date and time in the database records with an error date
        // the same as the error date the user enters on the search criteria aren't retrieved without this modification.
        String errorDate = newFieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO);

        int index = StringUtils.indexOf(errorDate, SearchOperator.LESS_THAN_EQUAL.toString());
        if (index == StringUtils.INDEX_NOT_FOUND) {
            index = StringUtils.indexOf(errorDate, SearchOperator.BETWEEN.toString());
            if (index != StringUtils.INDEX_NOT_FOUND) {
                incrementErrorDate(newFieldValues, errorDate, index);
            }
        } else {
            incrementErrorDate(newFieldValues, errorDate, index);
        }

        return newFieldValues;
    }

    /**
     * Add one day to the error date field.
     *
     * @param newFieldValues Map of field values for search criteria to be modified
     * @param errorDate String date from original fieldValues
     * @param index index of date prefix used to get actual date portion of errorDate string
     */
    protected void incrementErrorDate(Map<String, String> newFieldValues, String errorDate, int index) {
        String errorDatePrefix = errorDate.substring(0, index + 2);
        String errorDateString = errorDate.substring(index + 2);
        try {
            Date newDate = DateUtils.addDays(dateTimeService.convertToDate(errorDateString), 1);
            String newDateString = errorDatePrefix + dateTimeService.toString(newDate, KFSConstants.MONTH_DAY_YEAR_DATE_FORMAT);
            newFieldValues.put(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO, newDateString);
        }
        catch (ParseException ex) {
            LOG.warn("invalid date format for errorDate: " + errorDate);
        }
    }

    @Override
    public void validateSearchParameters(Map<String,String> fieldValues) {
        super.validateSearchParameters(fieldValues);
        String proposalNumber = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PROPOSAL_NUMBER);

        String awardBeginningDateFromString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_FROM);
        String awardBeginningDateToString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_TO);
        String awardEndingpDateFromString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_FROM);
        String awardEndingDateToString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_TO);
        String awardTotalAmount = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_TOTAL_AMOUNT);
        String cumulativeExpensesAmount = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.CUMULATIVE_EXPENSES_AMOUNT);
        String errorDateFromString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_FROM);
        String errorDateToString = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO);
        String primaryFundManagerPrincipalId = fieldValues.get(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PRIMARY_FUND_MANAGER_PRINCIPAL_NAME);

        if (!StringUtils.isNumeric(proposalNumber)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PROPOSAL_NUMBER, KFSKeyConstants.ERROR_NUMERIC, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PROPOSAL_NUMBER_LABEL);
        }

        validateDate(awardBeginningDateFromString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_FROM, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_FROM_LABEL);
        validateDate(awardBeginningDateToString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_TO, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_BEGINNING_DATE_TO_LABEL);

        validateDate(awardEndingpDateFromString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_FROM, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_FROM_LABEL);
        validateDate(awardEndingDateToString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_TO, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_ENDING_DATE_TO_LABEL);

        if (!KualiDecimal.isNumeric(awardTotalAmount)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_TOTAL_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.AWARD_TOTAL_AMOUNT_LABEL);
        }

        if (!KualiDecimal.isNumeric(cumulativeExpensesAmount)) {
            GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.CUMULATIVE_EXPENSES_AMOUNT, KFSKeyConstants.ERROR_NUMERIC, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.CUMULATIVE_EXPENSES_AMOUNT_LABEL);
        }

        validateDate(errorDateFromString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_FROM, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_FROM_LABEL);
        validateDate(errorDateToString, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.ERROR_DATE_TO_LABEL);

        if (StringUtils.isNotEmpty(primaryFundManagerPrincipalId)) {
            Person person = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(primaryFundManagerPrincipalId);
            if (ObjectUtils.isNull(person)) {
                GlobalVariables.getMessageMap().putError(ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PRIMARY_FUND_MANAGER_PRINCIPAL_NAME, ArKeyConstants.NO_PRINCIPAL_NAME_FOUND, ArPropertyConstants.ContractsGrantsInvoiceDocumentErrorLogLookupFields.PRIMARY_FUND_MANAGER_PRINCIPAL_NAME_LABEL);
            }
        }

        if (GlobalVariables.getMessageMap().hasErrors()) {
            throw new ValidationException("Error(s) in search criteria");
        }
    }


    /**
     * Validate date, adding error message to global message map with appropriate field name and field label if invalid.
     *
     * @param dateString
     * @param fieldName
     * @param fieldLabel
     */
    protected void validateDate(String dateString, String fieldName, String fieldLabel) {
        if (StringUtils.isNotBlank(dateString)) {
            try {
                dateTimeService.convertToDate(dateString);
            }
            catch (ParseException e) {
                GlobalVariables.getMessageMap().putError(fieldName, KFSKeyConstants.ERROR_DATE_TIME, fieldLabel);
            }
        }
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
