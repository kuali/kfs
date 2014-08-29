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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.comparator.CellComparatorHelper;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.ResultRow;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Customized Lookupable Helper class for Contracts and Grants Reports.
 */
public abstract class ContractsGrantsReportLookupableHelperServiceImplBase extends KualiLookupableHelperServiceImpl {
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected Pattern numericPattern = Pattern.compile("[-+]?\\d+\\.?\\d*");

    protected void buildResultTable(LookupForm lookupForm, Collection displayList, Collection resultTable) {
        Person user = GlobalVariables.getUserSession().getPerson();
        boolean hasReturnableRow = false;

        // Iterate through result list and wrap rows with return url and action url
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

                // Add url when property is documentNumber and paymentNumber (paymentNumber is a document number.)
                if (col.getPropertyName().equals("documentNumber") || col.getPropertyName().equals("paymentNumber")) {
                    String url = contractsGrantsReportHelperService.getDocSearchUrl(propValue);

                    Map<String, String> fieldList = new HashMap<String, String>();
                    fieldList.put(KFSPropertyConstants.DOCUMENT_NUMBER, propValue);
                    AnchorHtmlData a = new AnchorHtmlData(url, KRADConstants.EMPTY_STRING);
                    a.setTitle(HtmlData.getTitleText(getContractsGrantsReportHelperService().createTitleText(getBusinessObjectClass()), getBusinessObjectClass(), fieldList));

                    col.setColumnAnchor(a);
                }
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
     * Inner class to capture an operator
     */
    protected class OperatorAndValue {
        protected SearchOperator operator;
        protected Double value; //we should be able to safely coerce all values to double

        private OperatorAndValue(SearchOperator operator, String valueAsString) {
            this.operator = operator;
            this.value = new Double(valueAsString);
        }

        protected boolean applyComparison(Number otherValue) {
            if (value == null) {
                return otherValue == null;
            }
            if (otherValue == null) {
                return false; // value is not null, so they're not equal
            }
            final Double otherValueDouble = new Double(otherValue.doubleValue());
            final int compareResult = otherValueDouble.compareTo(value);
            switch (this.operator) {
                case EQUAL: return compareResult == 0;
                case LESS_THAN: return compareResult < 0;
                case GREATER_THAN: return compareResult > 0;
                case LESS_THAN_EQUAL: return compareResult <= 0;
                case GREATER_THAN_EQUAL: return compareResult >= 0;
            }
            throw new IllegalStateException("The operator did not catch ");
        }
    }

    /**
     * Validates whether a given String can be parsed into an operator and a value
     * @param propertyName the name of the property that this operatorAndValue was entered into
     * @param operatorAndValue String which should represent both an operator and a value
     */
    protected boolean validateOperatorAndValue(String propertyName, String operatorAndValue) {
        if (StringUtils.isBlank(operatorAndValue)) {
            return true; // nothing to validate
        }
        String numericValue = operatorAndValue;
        if (operatorAndValue.charAt(0) == '>' || operatorAndValue.charAt(0) == '<') {
            if (operatorAndValue.charAt(1) == '=') {
                numericValue = operatorAndValue.substring(2);
            } else {
                numericValue = operatorAndValue.substring(1);
            }
        }
        final Matcher matcher = numericPattern.matcher(numericValue);
        if (!matcher.matches()) {
            GlobalVariables.getMessageMap().putError(propertyName, ArKeyConstants.ERROR_REPORT_INVALID_CALCULATED_PATTERN, operatorAndValue);
            return false;
        }
        return true;
    }

    /**
     * Performs a {@link #validateSearchParameters(Map)}-friendly validation of the given property name to make sure it is
     * a valid operator and value
     * @param fieldValues the fieldValues from the lookup form
     * @param propertyName the property to validate as an operator and value
     */
    protected void validateSearchParametersForOperatorAndValue(Map<String, String> fieldValues, String propertyName) {
        if (!StringUtils.isBlank(fieldValues.get(propertyName))) {
            if (!validateOperatorAndValue(propertyName, fieldValues.get(propertyName))) {
                throw new ValidationException("Error in criteria for "+propertyName);
            }
        }
    }

    /**
     * Parses a given String into a record with an Operator and a numeric value
     * @param operatorAndValue the String to parse into an operator and value
     * @return a build OperatorAndValue record
     */
    protected OperatorAndValue parseOperatorAndValue(String operatorAndValue) {
        if (StringUtils.isBlank(operatorAndValue)) {
            return null; // nothing to parse
        }
        if (operatorAndValue.startsWith(SearchOperator.LESS_THAN_EQUAL.op())) {
            final String valueOnly = operatorAndValue.substring(2);
            return new OperatorAndValue(SearchOperator.LESS_THAN_EQUAL, valueOnly);
        } else if (operatorAndValue.startsWith(SearchOperator.GREATER_THAN_EQUAL.toString())) {
            final String valueOnly = operatorAndValue.substring(2);
            return new OperatorAndValue(SearchOperator.GREATER_THAN_EQUAL, valueOnly);
        } else if (operatorAndValue.startsWith(SearchOperator.LESS_THAN.op())) {
            final String valueOnly = operatorAndValue.substring(1);
            return new OperatorAndValue(SearchOperator.LESS_THAN, valueOnly);
        } else if (operatorAndValue.startsWith(SearchOperator.GREATER_THAN.op())) {
            final String valueOnly = operatorAndValue.substring(1);
            return new OperatorAndValue(SearchOperator.GREATER_THAN, valueOnly);
        }
        return new OperatorAndValue(SearchOperator.EQUAL, operatorAndValue);
    }

    /**
     * Convenience method which removes a field value from the given lookup field map and turns it into an OperatorAndValue, or null if the field was not filled in
     * @param lookupFields the fields from the lookup form
     * @param propertyName the property name to lookup
     * @return the OperatorAndValue representing that field, or null if no value was entered
     */
    protected OperatorAndValue buildOperatorAndValueFromField(Map lookupFields, String propertyName) {
        OperatorAndValue operator = null;
        final String fieldFromLookup = (String)lookupFields.get(propertyName);
        if (!StringUtils.isBlank(fieldFromLookup)) {
            operator = parseOperatorAndValue(fieldFromLookup);
        }
        return operator;
    }

    /**
     * Translates the date criteria to a form which the LookupService will comprehend
     * @param dateLowerBound the lower bound of the date
     * @param dateUpperBound the upper bound of the date
     * @param includeTime denotes whether time should be included on the upper bound
     * @return the date criteria, or null if nothing could be constructed
     */
    protected String fixDateCriteria(String dateLowerBound, String dateUpperBound, boolean includeTime) {
        final String correctedUpperBound = includeTime && !StringUtils.isBlank(dateUpperBound) ? getContractsGrantsReportHelperService().correctEndDateForTime(dateUpperBound) : dateUpperBound;
        if (!StringUtils.isBlank(dateLowerBound)) {
            if (!StringUtils.isBlank(dateUpperBound)) {
                return dateLowerBound+SearchOperator.BETWEEN.op()+correctedUpperBound;
            } else {
                return SearchOperator.GREATER_THAN_EQUAL.op()+dateLowerBound;
            }
        } else {
            if (!StringUtils.isBlank(dateUpperBound)) {
                return SearchOperator.LESS_THAN_EQUAL.op()+correctedUpperBound;
            }
        }
        return null;
    }

    /**
     * Convenience method to validate a date from the lookup criteria
     * @param dateFieldValue the value of the field from the lookup criteria
     * @param dateFieldClass the class being looked up
     * @param dateFieldPropertyName the property name representing the date
     * @param dateTimeService an implementation of DateTimeService
     */
    protected void validateDateField(String dateFieldValue, String dateFieldPropertyName, DateTimeService dateTimeService) {
        if (!StringUtils.isBlank(dateFieldValue)) {
            try {
                dateTimeService.convertToDate(dateFieldValue);
            }
            catch (ParseException pe) {
                addDateTimeError(dateFieldPropertyName);
            }
        }
    }

    /**
     * Convenience method to validate a timestamp from the lookup criteria
     * @param dateFieldValue the value of the field from the lookup criteria
     * @param dateFieldClass the class being looked up
     * @param dateFieldPropertyName the property name representing the date
     * @param dateTimeService an implementation of DateTimeService
     */
    protected void validateTimestampField(String dateFieldValue, String dateFieldPropertyName, DateTimeService dateTimeService) {
        if (!StringUtils.isBlank(dateFieldValue)) {
            try {
                dateTimeService.convertToSqlTimestamp(dateFieldValue);
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

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}