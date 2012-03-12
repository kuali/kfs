/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.options;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;

/**
 * An extension of KeyValuesBase that 
 */
public class OriginEntryFieldFinder extends KeyValuesBase {

    /**
     * Returns a list of all field names and display field names for the Origin Entry class
     * @return a List of key/value pair options
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    public List getKeyValues() {
        List activeLabels = new ArrayList();
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, "Fiscal Year"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "Chart Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.ACCOUNT_NUMBER, "Account Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, "Sub-Account Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "Object Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, "Sub-Object Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, "Balance Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, "Object Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, "Fiscal Period"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, "Document Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, "Origin code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.DOCUMENT_NUMBER, "Document Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, "Sequence Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC, "Description"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT, "Amount"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, "Debit Credit Indicator"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_DATE, "Transaction Date"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER, "Org Doc Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.PROJECT_CODE, "Project Code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.ORGANIZATION_REFERENCE_ID, "Org Ref ID"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.REFERENCE_FIN_DOCUMENT_TYPE_CODE, "Ref Doc Type"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FIN_SYSTEM_REF_ORIGINATION_CODE, "Ref Origin code"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_DOCUMENT_REFERENCE_NBR, "Ref Doc Number"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, "Reversal Date"));
        activeLabels.add(new ConcreteKeyValue(KFSPropertyConstants.TRANSACTION_ENCUMBRANCE_UPDT_CD, "Enc Update Code"));
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
            KeyValue klp = (KeyValue) iter.next();
            if (klp.getKey().equals(fieldName)) {
                return klp.getValue();
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
            KeyValue klp = (KeyValue) iter.next();
            if (klp.getValue().equals(fieldDisplayName)) {
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
        if (fieldName.equals(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            return "Integer";
        }
        if (fieldName.equals(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER)) {
            return "Integer";
        }
        if (fieldName.equals(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT)) {
            return "KualiDecimal";
        }
        if (fieldName.equals(KFSPropertyConstants.TRANSACTION_DATE)) {
            return "Date";
        }
        if (fieldName.equals(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE)) {
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
        if (fieldName.equals(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT)) {
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
        DataDictionaryService dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        int fieldLength = 0;
        fieldLength = dataDictionaryService.getAttributeMaxLength(OriginEntryFull.class, fieldName);
        return fieldLength;
    }
    
}
