/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.kfs.sys.context.SpringContext; import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.kns.util.DateUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.api.util.type.TypeUtils;

/**
 * Defines a utility class used by Contracts and Grants Invoice Reports. *
 */
public class ContractsGrantsReportUtils {

    /**
     * @param fieldsForLookup
     * @param bo
     * @param className
     * @return
     */
    public static boolean doesMatchLookupFields(Map fieldsForLookup, BusinessObject bo, String className) {
        BusinessObjectEntry boe = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(className);
        List<String> lookupResultFields = boe.getLookupDefinition().getResultFieldNames();


        boolean returnBoolean = true;
        for (Object propertyName : fieldsForLookup.keySet()) {

            if (lookupResultFields.contains(propertyName.toString())) {
                Object propertyValueObject = ObjectUtils.getPropertyValue(bo, propertyName.toString());
                String lookupFieldValue = fieldsForLookup.get(propertyName).toString();
                if (ObjectUtils.isNull(lookupFieldValue) || lookupFieldValue.equals("")) {
                    continue;
                }

                PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);
                Class propertyType = ObjectUtils.getPropertyType(bo, propertyName.toString(), persistenceStructureService);
                if (propertyType == null) {
                    return false;
                }

                returnBoolean &= compareField(fieldsForLookup, propertyName.toString(), propertyValueObject, lookupFieldValue, propertyType, false, false);

            }
        }
        return returnBoolean;
    }


    /**
     * Checks if the date field is in range.
     * 
     * @param fieldsForLookup
     * @param dateData
     * @param fieldName
     * @return true if date field is within range, false otherwise.
     */
    public static boolean isDateFieldInRange(String dateFromFieldValues, String dateToFieldValues, Date propertyValue, String fieldName) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        Date dateFrom;
        Date dateTo;
        
        // Clearing time field for date only comparison
        propertyValue = DateUtils.clearTimeFields(propertyValue);
        
        try {
            // Both are blank or null
            if (dateToFieldValues.trim().equals("") && dateFromFieldValues.trim().equals("")) {
                return true;
            }

            // Only set Date from
            if (dateToFieldValues.trim().equals("") && !dateFromFieldValues.trim().equals("")) {
                dateFrom = new Date(format.parse(dateFromFieldValues).getTime());
                if (propertyValue.after(dateFrom) || propertyValue.equals(dateFrom)) {
                    return true;
                }
                else
                    return false;
            }

            // Only set Date to
            if (!dateToFieldValues.trim().equals("") && dateFromFieldValues.trim().equals("")) {
                dateTo = new Date(format.parse(dateToFieldValues).getTime());
                if (propertyValue.before(dateTo) || propertyValue.equals(dateTo)) {
                    return true;
                }
                else
                    return false;
            }

            dateTo = new Date(format.parse(dateToFieldValues).getTime());
            dateFrom = new Date(format.parse(dateFromFieldValues).getTime());
            if ((propertyValue.after(dateFrom) || propertyValue.equals(dateFrom)) && (propertyValue.before(dateTo) || propertyValue.equals(dateTo))) {
                return true;
            }
            else
                return false;
        }
        catch (ParseException ex) {
            throw new RuntimeException();
        }
    }

    /**
     * @param wildcard
     * @return
     */
    private static String wildcardToRegex(String wildcard) {
        StringBuffer s = new StringBuffer(wildcard.length());
        s.append('^');
        for (int i = 0, is = wildcard.length(); i < is; i++) {
            char c = wildcard.charAt(i);
            switch (c) {
                case '*':
                    s.append(".*");
                    break;
                case '?':
                    s.append(".");
                    break;
                // Escape special regex-characters
                case '(':
                case ')':
                case '[':
                case ']':
                case '$':
                case '^':
                case '.':
                case '{':
                case '}':
                case '|':
                case '\\':
                    s.append("\\");
                    s.append(c);
                    break;
                default:
                    s.append(c);
                    break;
            }
        }
        s.append('$');
        return (s.toString());
    }


    private static boolean compareField(Map fieldsForLookup, String propertyName, Object propertyValue, String lookupFieldValue, Class propertyType, boolean caseInsensitive, boolean treatWildcardsAndOperatorsAsLiteral) {
        boolean valid = true;

        if (!treatWildcardsAndOperatorsAsLiteral && StringUtils.contains(lookupFieldValue, KRADConstants.OR_LOGICAL_OPERATOR)) {
            return checkOrOperator(fieldsForLookup, propertyName, propertyValue, lookupFieldValue, propertyType, caseInsensitive);
        }

        if (!treatWildcardsAndOperatorsAsLiteral && StringUtils.contains(lookupFieldValue, KRADConstants.AND_LOGICAL_OPERATOR)) {
            return checkAndOperator(fieldsForLookup, propertyName, propertyValue, lookupFieldValue, propertyType, caseInsensitive);
        }

        if (TypeUtils.isStringClass(propertyType)) {
            if (caseInsensitive) {
                lookupFieldValue = lookupFieldValue.toUpperCase();
            }
            if (!treatWildcardsAndOperatorsAsLiteral && StringUtils.contains(lookupFieldValue, KRADConstants.NOT_LOGICAL_OPERATOR)) {
                valid = checkNotOperator(propertyName, propertyValue, lookupFieldValue, propertyType, caseInsensitive);
                if (!valid) {
                    return false;
                }
            }
            else if (!treatWildcardsAndOperatorsAsLiteral && propertyValue != null && (StringUtils.contains(lookupFieldValue, KRADConstants.BETWEEN_OPERATOR) || lookupFieldValue.startsWith(">") || lookupFieldValue.startsWith("<"))) {
                valid = checkStringRange(propertyName, propertyValue, lookupFieldValue, treatWildcardsAndOperatorsAsLiteral);
                if (!valid) {
                    return false;
                }
            }
            else {
                if (treatWildcardsAndOperatorsAsLiteral) {
                    lookupFieldValue = StringUtils.replace(lookupFieldValue, "*", "\\*");
                }
                valid = propertyValue.toString().toLowerCase().matches(wildcardToRegex(lookupFieldValue.toLowerCase()));
                if (!valid) {
                    return false;
                }
            }
        }
        else if (TypeUtils.isIntegralClass(propertyType) || TypeUtils.isDecimalClass(propertyType)) {
            valid = checkNumericRange(propertyName, propertyValue, lookupFieldValue, treatWildcardsAndOperatorsAsLiteral);
            if (!valid) {
                return false;
            }
        }
        else if (TypeUtils.isTemporalClass(propertyType)) {
            String dateFromFieldValues = ObjectUtils.isNull(fieldsForLookup.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + propertyName.toString())) ? "" : fieldsForLookup.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + propertyName.toString()).toString();
            String dateToFieldValues = ObjectUtils.isNull(fieldsForLookup.get(propertyName.toString())) ? "" : fieldsForLookup.get(propertyName.toString()).toString();
            valid = isDateFieldInRange(dateFromFieldValues, dateToFieldValues, (Date) propertyValue, propertyName.toString());

        }
        else if (TypeUtils.isBooleanClass(propertyType)) {
            String stringValue = (Boolean) propertyValue ? "Y" : "N";
            return stringValue.equals(ObjectUtils.clean(lookupFieldValue));
        }

        return valid;
    }


    private static boolean checkOrOperator(Map fieldsForLookup, String propertyName, Object propertyValue, String lookupFieldValue, Class propertyType, boolean caseInsensitive) {
        return checkLogicalOperator(fieldsForLookup, propertyName, propertyValue, lookupFieldValue, propertyType, caseInsensitive, KRADConstants.OR_LOGICAL_OPERATOR);
    }

    private static boolean checkAndOperator(Map fieldsForLookup, String propertyName, Object propertyValue, String lookupFieldValue, Class propertyType, boolean caseInsensitive) {
        return checkLogicalOperator(fieldsForLookup, propertyName, propertyValue, lookupFieldValue, propertyType, caseInsensitive, KRADConstants.AND_LOGICAL_OPERATOR);
    }

    /**
     * Builds a sub criteria object joined with an 'AND' or 'OR' (depending on splitValue) using the split values of propertyValue.
     * Then joins back the sub criteria to the main criteria using an 'AND'.
     */
    private static boolean checkLogicalOperator(Map fieldsForLookup, String propertyName, Object propertyValue, String lookupFieldValue, Class propertyType, boolean caseInsensitive, String splitValue) {
        boolean valid = true;

        String[] splitPropVal = StringUtils.split(lookupFieldValue, splitValue);

        for (int i = 0; i < splitPropVal.length; i++) {
            // first comparison should not be with logical
            if (i == 0) {
                if (splitValue == KRADConstants.OR_LOGICAL_OPERATOR) {
                    valid = compareField(fieldsForLookup, propertyName, propertyValue, splitPropVal[i], propertyType, caseInsensitive, false);
                }
                if (splitValue == KRADConstants.AND_LOGICAL_OPERATOR) {
                    valid = compareField(fieldsForLookup, propertyName, propertyValue, splitPropVal[i], propertyType, caseInsensitive, false);
                }

            }
            else {
                if (splitValue == KRADConstants.OR_LOGICAL_OPERATOR) {
                    valid |= compareField(fieldsForLookup, propertyName, propertyValue, splitPropVal[i], propertyType, caseInsensitive, false);
                    if (!valid) {
                        return false;
                    }
                }
                if (splitValue == KRADConstants.AND_LOGICAL_OPERATOR) {
                    valid &= compareField(fieldsForLookup, propertyName, propertyValue, splitPropVal[i], propertyType, caseInsensitive, false);
                    if (!valid) {
                        return false;
                    }
                }
            }
        }
        return valid;
    }

    private static boolean checkNotOperator(String propertyName, Object propertyValue, String lookupFieldValue, Class propertyType, boolean caseInsensitive) {

        String[] splitPropVal = StringUtils.split(lookupFieldValue, KRADConstants.NOT_LOGICAL_OPERATOR);

        int strLength = splitPropVal.length;

        boolean valid = splitPropVal[0].compareTo(propertyValue.toString()) != 0;
        // if more than one NOT operator assume an implicit and (i.e. !a!b = !a&!b)
        for (int i = 1; i < splitPropVal.length; i++) {
            valid &= splitPropVal[1].compareTo(propertyValue.toString()) != 0;
        }

        return valid;
    }


    private static boolean checkStringRange(String propertyName, Object propertyValue, String lookupFieldValue, boolean treatWildcardsAndOperatorsAsLiteral) {

        if (StringUtils.contains(lookupFieldValue, KRADConstants.BETWEEN_OPERATOR)) {
            String[] rangeValues = StringUtils.split(lookupFieldValue, KRADConstants.BETWEEN_OPERATOR);
            return rangeValues[0].compareTo(propertyValue.toString()) < 0 && rangeValues[1].compareTo(propertyValue.toString()) > 0;

        }
        else if (lookupFieldValue.startsWith(">=")) {
            // GreaterOrEqualThan(propertyName, ObjectUtils.clean(propertyValue));
            return propertyValue.toString().compareTo(ObjectUtils.clean(lookupFieldValue)) >= 0;

        }
        else if (lookupFieldValue.startsWith("<=")) {
            return propertyValue.toString().compareTo(ObjectUtils.clean(lookupFieldValue)) <= 0;
        }
        else if (lookupFieldValue.startsWith(">")) {
            return propertyValue.toString().compareTo(ObjectUtils.clean(lookupFieldValue)) > 0;
        }
        else if (lookupFieldValue.startsWith("<")) {
            return propertyValue.toString().compareTo(ObjectUtils.clean(lookupFieldValue)) < 0;
        }
        else {
            return lookupFieldValue.equals(propertyValue.toString());
        }

    }


    private static boolean checkNumericRange(String propertyName, Object propertyValue, String lookupFieldValue, boolean treatWildcardsAndOperatorsAsLiteral) {

        if (StringUtils.contains(lookupFieldValue, KRADConstants.BETWEEN_OPERATOR)) {
            if (treatWildcardsAndOperatorsAsLiteral)
                throw new RuntimeException("Cannot use wildcards and operators on numeric field " + propertyName);
            String[] rangeValues = StringUtils.split(lookupFieldValue, KRADConstants.BETWEEN_OPERATOR);

            return (cleanNumeric(rangeValues[0]).compareTo(cleanNumeric(propertyValue.toString())) < 0 && cleanNumeric(rangeValues[1]).compareTo(cleanNumeric(propertyValue.toString())) > 0);

        }
        else if (lookupFieldValue.startsWith(">=")) {
            if (treatWildcardsAndOperatorsAsLiteral)
                throw new RuntimeException("Cannot use wildcards and operators on numeric field " + propertyName);
            return cleanNumeric(propertyValue.toString()).compareTo(cleanNumeric(lookupFieldValue)) >= 0;

        }
        else if (lookupFieldValue.startsWith("<=")) {
            if (treatWildcardsAndOperatorsAsLiteral)
                throw new RuntimeException("Cannot use wildcards and operators on numeric field " + propertyName);
            return cleanNumeric(propertyValue.toString()).compareTo(cleanNumeric(lookupFieldValue)) <= 0;
        }
        else if (lookupFieldValue.startsWith(">")) {
            if (treatWildcardsAndOperatorsAsLiteral)
                throw new RuntimeException("Cannot use wildcards and operators on numeric field " + propertyName);
            return cleanNumeric(propertyValue.toString()).compareTo(cleanNumeric(lookupFieldValue)) > 0;
        }
        else if (lookupFieldValue.startsWith("<")) {
            if (treatWildcardsAndOperatorsAsLiteral)
                throw new RuntimeException("Cannot use wildcards and operators on numeric field " + propertyName);
            return cleanNumeric(propertyValue.toString()).compareTo(cleanNumeric(lookupFieldValue)) < 0;
        }
        else {
            return cleanNumeric(propertyValue.toString()).compareTo(cleanNumeric(lookupFieldValue)) == 0;
        }
    }

    private static BigDecimal cleanNumeric(String value) {
        String cleanedValue = value.replaceAll("[^-0-9.]", "");
        // ensure only one "minus" at the beginning, if any
        if (cleanedValue.lastIndexOf('-') > 0) {
            if (cleanedValue.charAt(0) == '-') {
                cleanedValue = "-" + cleanedValue.replaceAll("-", "");
            }
            else {
                cleanedValue = cleanedValue.replaceAll("-", "");
            }
        }
        // ensure only one decimal in the string
        int decimalLoc = cleanedValue.lastIndexOf('.');
        if (cleanedValue.indexOf('.') != decimalLoc) {
            cleanedValue = cleanedValue.substring(0, decimalLoc).replaceAll("\\.", "") + cleanedValue.substring(decimalLoc);
        }
        try {
            return new BigDecimal(cleanedValue);
        }
        catch (NumberFormatException ex) {
            GlobalVariables.getMessageMap().putError(KRADConstants.DOCUMENT_ERRORS, RiceKeyConstants.ERROR_CUSTOM, new String[] { "Invalid Numeric Input: " + value });
            return null;
        }
    }


    /**
     * @param paymentApplicationDoc
     * @return true if the applied amount is the same as the unapplied amount.
     */
    public static boolean isLocDraw(PaymentApplicationDocument paymentApplicationDoc) {

        List<InvoicePaidApplied> invoicePaidApplieds = paymentApplicationDoc.getInvoicePaidApplieds();
        List<NonInvoiced> nonInvoiceds = paymentApplicationDoc.getNonInvoiceds();

        KualiDecimal appliedAmount = KualiDecimal.ZERO;
        KualiDecimal unAppliedAmount = KualiDecimal.ZERO;

        for (InvoicePaidApplied paidAppliedInvoice : invoicePaidApplieds) {
            appliedAmount = appliedAmount.add(paidAppliedInvoice.getInvoiceItemAppliedAmount());
        }

        for (NonInvoiced nonInvoiced : nonInvoiceds) {
            unAppliedAmount = unAppliedAmount.add(nonInvoiced.getFinancialDocumentLineAmount());
        }

        return appliedAmount.equals(unAppliedAmount) ? true : false;
    }


}
