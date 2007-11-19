/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.optionfinder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.lookup.keyvalues.KeyValuesBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.web.ui.KeyLabelPair;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * An extension of KeyValuesBase that 
 */
public class OriginEntryFieldFinder extends KeyValuesBase {

    /**
     * Returns a list of all field names and display field names for the Origin Entry class
     * @return a List of key/value pair options
     * @see org.kuali.core.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new KeyLabelPair("universityFiscalYear", "Fiscal Year"));
        activeLabels.add(new KeyLabelPair("chartOfAccountsCode", "Chart Code"));
        activeLabels.add(new KeyLabelPair("accountNumber", "Account Number"));
        activeLabels.add(new KeyLabelPair("subAccountNumber", "Sub-Account Number"));
        activeLabels.add(new KeyLabelPair("financialObjectCode", "Object Code"));
        activeLabels.add(new KeyLabelPair("financialSubObjectCode", "Sub-Object Code"));
        activeLabels.add(new KeyLabelPair("financialBalanceTypeCode", "Balance Type"));
        activeLabels.add(new KeyLabelPair("financialObjectTypeCode", "Object Type"));
        activeLabels.add(new KeyLabelPair("universityFiscalPeriodCode", "Fiscal Period"));
        activeLabels.add(new KeyLabelPair("financialDocumentTypeCode", "Document Type"));
        activeLabels.add(new KeyLabelPair("financialSystemOriginationCode", "Origin code"));
        activeLabels.add(new KeyLabelPair(KFSPropertyConstants.DOCUMENT_NUMBER, "Document Number"));
        activeLabels.add(new KeyLabelPair("transactionLedgerEntrySequenceNumber", "Sequence Number"));
        activeLabels.add(new KeyLabelPair("transactionLedgerEntryDescription", "Description"));
        activeLabels.add(new KeyLabelPair("transactionLedgerEntryAmount", "Amount"));
        activeLabels.add(new KeyLabelPair("transactionDebitCreditCode", "Debit Credit Indicator"));
        activeLabels.add(new KeyLabelPair("transactionDate", "Transaction Date"));
        activeLabels.add(new KeyLabelPair("organizationDocumentNumber", "Org Doc Number"));
        activeLabels.add(new KeyLabelPair("projectCode", "Project Code"));
        activeLabels.add(new KeyLabelPair("organizationReferenceId", "Org Ref ID"));
        activeLabels.add(new KeyLabelPair("referenceFinancialDocumentTypeCode", "Ref Doc Type"));
        activeLabels.add(new KeyLabelPair("referenceFinancialSystemOriginationCode", "Ref Origin code"));
        activeLabels.add(new KeyLabelPair("referenceFinancialDocumentNumber", "Ref Doc Number"));
        activeLabels.add(new KeyLabelPair("financialDocumentReversalDate", "Reversal Date"));
        activeLabels.add(new KeyLabelPair("transactionEncumbranceUpdateCode", "Enc Update Code"));
        return activeLabels;
    }

    /**
     * Given the property field name for a field, returns the display name
     * 
     * @param fieldName the property field name for a field
     * @return the display field name of that field
     */
    public String getFieldDisplayName(String fieldName) {
        for (Iterator iter = getKeyValues().iterator(); iter.hasNext();) {
            KeyLabelPair klp = (KeyLabelPair) iter.next();
            if (klp.getKey().equals(fieldName)) {
                return klp.getLabel();
            }
        }
        return "Error";
    }

    /**
     * Given the display name of a field, returns the property field name
     * 
     * @param fieldDisplayName the display name of the field
     * @return the property field name for that field
     */
    public String getFieldName(String fieldDisplayName) {
        for (Iterator iter = getKeyValues().iterator(); iter.hasNext();) {
            KeyLabelPair klp = (KeyLabelPair) iter.next();
            if (klp.getLabel().equals(fieldDisplayName)) {
                return (String) klp.getKey();
            }
        }
        return "Error";
    }

    /**
     * Given a field name and a value, determines if that value is valid for the field
     * 
     * @param fieldName the name of a field to inquire on
     * @param value the value that the field will potentially be set to
     * @return true if the value is valid, false if otherwise
     */
    public boolean isValidValue(String fieldName, String value) {
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        String fieldType = getFieldType(fieldName);
        int fieldLength = getFieldLength(fieldName);

        if (allowNull(fieldName) && (value == null || value.length() == 0)) {
            return true;
        }
        if (!allowNull(fieldName) && (value == null || value.length() == 0)) {
            return false;
        }
        if (value.length() > fieldLength) {
            return false;
        }
        if ("KualiDecimal".equals(fieldType)) {
            try {
                KualiDecimal d = new KualiDecimal(value);
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        else if ("Integer".equals(fieldType)) {
            try {
                Integer d = new Integer(value);
                return true;
            }
            catch (NumberFormatException nfe) {
                return false;
            }
        }
        else if ("Date".equals(fieldType)) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date d = df.parse(value);
                return true;
            }
            catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a String with the name of the type of the given field
     * 
     * @param fieldName the name of the field to inquire on
     * @return a String with the name of the class that field returns
     */
    public String getFieldType(String fieldName) {
        if (fieldName.equals("universityFiscalYear")) {
            return "Integer";
        }
        if (fieldName.equals("transactionLedgerEntrySequenceNumber")) {
            return "Integer";
        }
        if (fieldName.equals("transactionLedgerEntryAmount")) {
            return "KualiDecimal";
        }
        if (fieldName.equals("transactionDate")) {
            return "Date";
        }
        if (fieldName.equals("financialDocumentReversalDate")) {
            return "Date";
        }
        return "String";
    }

    /**
     * Returns whether the given field can be set to null or not
     * 
     * @param fieldName the name of the field to inquire about
     * @return true if it can be set to null, false otherwise
     */
    public boolean allowNull(String fieldName) {
        if (fieldName.equals("transactionLedgerEntryAmount")) {
            return false;
        }
        return true;
    }

    /**
     * Returns the length of a given field in Origin Entry
     * 
     * @param fieldName the name of the Origin Entry field to get a length for
     * @return the length of the field
     */
    public int getFieldLength(String fieldName) {
        // TODO  AUGH!!!!!  BASE THIS ON THE DATA DICTIONARY!!!!!
        if (fieldName.equals("universityFiscalYear")) {
            return 4;
        }
        else if (fieldName.equals("transactionLedgerEntrySequenceNumber")) {
            return 5;
        }
        else if (fieldName.equals("transactionLedgerEntryAmount")) {
            return 19;
        }
        else if (fieldName.equals("transactionDate")) {
            return 10;
        }
        else if (fieldName.equals("financialDocumentReversalDate")) {
            return 10;
        }
        else if (fieldName.equals("chartOfAccountsCode")) {
            return 2;
        }
        else if (fieldName.equals("accountNumber")) {
            return 7;
        }
        else if (fieldName.equals("subAccountNumber")) {
            return 5;
        }
        else if (fieldName.equals("financialObjectCode")) {
            return 4;
        }
        else if (fieldName.equals("financialSubObjectCode")) {
            return 3;
        }
        else if (fieldName.equals("financialBalanceTypeCode")) {
            return 2;
        }
        else if (fieldName.equals("financialObjectTypeCode")) {
            return 2;
        }
        else if (fieldName.equals("universityFiscalPeriodCode")) {
            return 2;
        }
        else if (fieldName.equals("financialDocumentTypeCode")) {
            return 4;
        }
        else if (fieldName.equals("financialSystemOriginationCode")) {
            return 2;
        }
        else if (fieldName.equals(KFSPropertyConstants.DOCUMENT_NUMBER)) {
            return 14;
        }
        else if (fieldName.equals("transactionLedgerEntryDescription")) {
            return 40;
        }
        else if (fieldName.equals("transactionDebitCreditCode")) {
            return 1;
        }
        else if (fieldName.equals("organizationDocumentNumber")) {
            return 10;
        }
        else if (fieldName.equals("projectCode")) {
            return 10;
        }
        else if (fieldName.equals("organizationReferenceId")) {
            return 8;
        }
        else if (fieldName.equals("referenceFinancialDocumentTypeCode")) {
            return 4;
        }
        else if (fieldName.equals("referenceFinancialSystemOriginationCode")) {
            return 2;
        }
        else if (fieldName.equals("referenceFinancialDocumentNumber")) {
            return 14;
        }
        else if (fieldName.equals("transactionEncumbranceUpdateCode")) {
            return 1;
        }
        return 0;
    }
}
