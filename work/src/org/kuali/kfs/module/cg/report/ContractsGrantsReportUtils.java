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
package org.kuali.kfs.module.cg.report;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.kuali.kfs.module.ar.businessobject.InvoicePaidApplied;
import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.module.ar.document.PaymentApplicationDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.CollectionUtils;

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
        org.kuali.rice.kns.datadictionary.BusinessObjectEntry boe = (org.kuali.rice.kns.datadictionary.BusinessObjectEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(className);
        List<String> lookupResultFields = boe.getLookupDefinition().getResultFieldNames();
        Set invalidKeySet = new HashSet<Object>();
       //to filter the fields for lookup if its value is null.
        for(Object propNm: fieldsForLookup.keySet()){
            if(fieldsForLookup.get(propNm).toString().equals("")){
                invalidKeySet.add(propNm);
            }
        }

        for(Object invalidKey: invalidKeySet){
            fieldsForLookup.remove(invalidKey);
        }
        //also remove the back location and docformkey vlaues, so we have the list empty for now.
        fieldsForLookup.remove(KRADConstants.BACK_LOCATION);
        fieldsForLookup.remove(KRADConstants.DOC_FORM_KEY);


        //To return true if none of the lookup fields are filled in.
        if(CollectionUtils.isEmpty(fieldsForLookup)){
            return true;
        }


        boolean returnBoolean = true;
        for (Object propertyName : fieldsForLookup.keySet()) {

            if (lookupResultFields.contains(propertyName.toString())) {
                Object propertyValueObject = ObjectUtils.getPropertyValue(bo, propertyName.toString());
                String lookupFieldValue = fieldsForLookup.get(propertyName).toString();

                // Special case for active indicator
                // This is only for award report that has active indicator.
                if (propertyName.toString().equals("active")) {
                    String activeInd = propertyValueObject.toString().toLowerCase().equals("true") ? "y" : "n";
                    if (activeInd.equals(lookupFieldValue.toString().toLowerCase())) {
                        returnBoolean &= true;
                        continue;
                    }
                    else {
                        return false;
                    }
                }

                if (!(propertyValueObject instanceof Date)) {

                    // propertyValueObject is null: means there is no such field in BO
                    // lookupFieldValueObject is blank: means the fields is not specified with search
                    if (ObjectUtils.isNotNull(propertyValueObject) && !lookupFieldValue.equals("")) {
                        if (Pattern.matches(wildcardToRegex(lookupFieldValue.toString().toLowerCase()), propertyValueObject.toString().toLowerCase())) {
                            returnBoolean &= true;
                        }
                        else {
                            return false;
                        }
                    }
                    else {
                        return false;
                    }

                }
                else {
                    if (ObjectUtils.isNotNull(propertyValueObject) && isDateFieldInRange(fieldsForLookup, (Date) propertyValueObject, propertyName.toString())) {
                        returnBoolean &= true;
                    }
                    else {
                        return false;
                    }
                }
            }
        }
        return returnBoolean;
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


    /**
     * Checks if the date field is in range.
     *
     * @param fieldsForLookup
     * @param dateData
     * @param fieldName
     * @return true if date field is within range, false otherwise.
     */
    private static boolean isDateFieldInRange(Map fieldsForLookup, Date dateData, String fieldName) {
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");

        String dateFromFieldValues = ObjectUtils.isNull(fieldsForLookup.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldName)) ? "" : fieldsForLookup.get(KRADConstants.LOOKUP_RANGE_LOWER_BOUND_PROPERTY_PREFIX + fieldName).toString();
        String dateToFieldValues = ObjectUtils.isNull(fieldsForLookup.get(fieldName)) ? "" : fieldsForLookup.get(fieldName).toString();
        Date dateFrom;
        Date dateTo;
        try {
            // Both are blank or null
            if (dateToFieldValues.trim().equals("") && dateFromFieldValues.trim().equals("")) {
                return true;
            }

            // Only set Date from
            if (dateToFieldValues.trim().equals("") && !dateFromFieldValues.trim().equals("")) {
                dateFrom = new Date(format.parse(dateFromFieldValues).getTime());
                if (dateData.after(dateFrom) || dateData.equals(dateFrom)) {
                    return true;
                }
                else {
                    return false;
                }
            }

            // Only set Date to
            if (!dateToFieldValues.trim().equals("") && dateFromFieldValues.trim().equals("")) {
                dateTo = new Date(format.parse(dateToFieldValues).getTime());
                if (dateData.before(dateTo) || dateData.equals(dateTo)) {
                    return true;
                }
                else {
                    return false;
                }
            }

            dateTo = new Date(format.parse(dateToFieldValues).getTime());
            dateFrom = new Date(format.parse(dateFromFieldValues).getTime());
            if ((dateData.after(dateFrom) || dateData.equals(dateFrom)) && (dateData.before(dateTo) || dateData.equals(dateTo))) {
                return true;
            }
            else {
                return false;
            }
        }
        catch (ParseException ex) {
            throw new RuntimeException();
        }
    }

    /**
     *
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

}
