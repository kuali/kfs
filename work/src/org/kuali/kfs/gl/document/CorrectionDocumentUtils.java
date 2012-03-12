/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.document;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.CorrectionChange;
import org.kuali.kfs.gl.businessobject.CorrectionChangeGroup;
import org.kuali.kfs.gl.businessobject.CorrectionCriteria;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.businessobject.options.OriginEntryFieldFinder;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class provides utility methods for the correction document
 */
public class CorrectionDocumentUtils {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorrectionDocumentUtils.class);
    public static final int DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT = 1000;

    /**
     * The GLCP document will always be on restricted functionality mode, regardless of input group size
     */
    public static final int RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE = 0;

    /**
     * The GLCP document will never be on restricted functionality mode, regardless of input group size
     */
    public static final int RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED = -1;

    public static final int DEFAULT_RECORDS_PER_PAGE = 10;

    /**
     * This method returns the limit for record count functionality
     * 
     * @return limit for record count functionality
     */
    public static int getRecordCountFunctionalityLimit() {
        String limitString = SpringContext.getBean(ParameterService.class).getParameterValueAsString(GeneralLedgerCorrectionProcessDocument.class, KFSConstants.GeneralLedgerCorrectionProcessApplicationParameterKeys.RECORD_COUNT_FUNCTIONALITY_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }

        return DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT;
    }

    /**
     * This method returns the number of records per page
     * 
     * @return number of records per page
     */
    public static int getRecordsPerPage() {
        String limitString = SpringContext.getBean(ParameterService.class).getParameterValueAsString(GeneralLedgerCorrectionProcessDocument.class, KFSConstants.GeneralLedgerCorrectionProcessApplicationParameterKeys.RECORDS_PER_PAGE);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return DEFAULT_RECORDS_PER_PAGE;
    }

    /**
     * This method returns true if input group size is greater than or equal to record count functionality limit
     * 
     * @param inputGroupSize size of input groups
     * @param recordCountFunctionalityLimit limit for record count functionality
     * @return true if input group size is greater than or equal to record count functionality limit
     */
    public static boolean isRestrictedFunctionalityMode(int inputGroupSize, int recordCountFunctionalityLimit) {
        return (recordCountFunctionalityLimit != CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED && inputGroupSize >= recordCountFunctionalityLimit) || recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE;
    }

    /**
     * When a correction criterion is about to be added to a group, this will check if it is valid, meaning that the field name is
     * not blank
     * 
     * @param correctionCriteria validated correction criteria
     * @return true if correction criteria is valid for adding
     */
    public static boolean validCorrectionCriteriaForAdding(CorrectionCriteria correctionCriteria) {
        String fieldName = correctionCriteria.getCorrectionFieldName();
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        return true;
    }

    /**
     * When a document is about to be saved, this will check if it is valid, meaning that the field name and value are both blank
     * 
     * @param correctionCriteria validated correction criteria
     * @return true if correction criteria is valid for saving
     */
    public static boolean validCorrectionCriteriaForSaving(CorrectionCriteria correctionCriteria) {
        return correctionCriteria == null || (StringUtils.isBlank(correctionCriteria.getCorrectionFieldName()) && StringUtils.isBlank(correctionCriteria.getCorrectionFieldValue()));
    }

    /**
     * When a correction change is about to be added to a group, this will check if it is valid, meaning that the field name is not
     * blank
     * 
     * @param correctionChange validated correction change
     * @return true is correction change is valid for adding
     */
    public static boolean validCorrectionChangeForAdding(CorrectionChange correctionChange) {
        String fieldName = correctionChange.getCorrectionFieldName();
        if (StringUtils.isBlank(fieldName)) {
            return false;
        }
        return true;
    }

    /**
     * When a document is about to be saved, this will check if it is valid, meaning that the field name and value are both blank
     * 
     * @param correctionCriteria validated correction criteria
     * @return true if correction change is valid for saving (i.e. correction change is null or correction field name and field
     *         value are blank)
     */
    public static boolean validCorrectionChangeForSaving(CorrectionChange correctionChange) {
        return correctionChange == null || (StringUtils.isBlank(correctionChange.getCorrectionFieldName()) && StringUtils.isBlank(correctionChange.getCorrectionFieldValue()));
    }

    /**
     * Sets all origin entries' entry IDs to null within the collection.
     * 
     * @param originEntries collection of origin entries
     */
    public static void setAllEntryIdsToNull(Collection<OriginEntryFull> originEntries) {
        for (OriginEntryFull entry : originEntries) {
            entry.setEntryId(null);
        }
    }

    /**
     * Sets all origin entries' entry IDs to be sequential starting from 0 in the collection
     * 
     * @param originEntries collection of origin entries
     */
    public static void setSequentialEntryIds(Collection<OriginEntryFull> originEntries) {
        int index = 0;
        for (OriginEntryFull entry : originEntries) {
            entry.setEntryId(new Integer(index));
            index++;
        }
    }

    /**
     * Returns whether an origin entry matches the passed in criteria. If both the criteria and actual value are both String types
     * and are empty, null, or whitespace only, then they will match.
     * 
     * @param cc correction criteria to test against origin entry
     * @param oe origin entry to test
     * @return true if origin entry matches the passed in criteria
     */
    public static boolean entryMatchesCriteria(CorrectionCriteria cc, OriginEntryFull oe) {
        OriginEntryFieldFinder oeff = new OriginEntryFieldFinder();
        Object fieldActualValue = oe.getFieldValue(cc.getCorrectionFieldName());
        String fieldTestValue = StringUtils.isBlank(cc.getCorrectionFieldValue()) ? "" : cc.getCorrectionFieldValue();
        String fieldType = oeff.getFieldType(cc.getCorrectionFieldName());

        String fieldActualValueString = convertToString(fieldActualValue, fieldType);

        if ("String".equals(fieldType) || "sw".equals(cc.getCorrectionOperatorCode()) || "ew".equals(cc.getCorrectionOperatorCode()) || "ct".equals(cc.getCorrectionOperatorCode())) {
            return compareStringData(cc, fieldTestValue, fieldActualValueString);
        }
        int compareTo = 0;
        try {
            if (fieldActualValue == null) {
                return false;
            }
            if ("Integer".equals(fieldType)) {
                compareTo = ((Integer) fieldActualValue).compareTo(Integer.parseInt(fieldTestValue));
            }
            if ("KualiDecimal".equals(fieldType)) {
                compareTo = ((KualiDecimal) fieldActualValue).compareTo(new KualiDecimal(Double.parseDouble(fieldTestValue)));
            }
            if ("BigDecimal".equals(fieldType)) {
                compareTo = ((BigDecimal) fieldActualValue).compareTo(new BigDecimal(Double.parseDouble(fieldTestValue)));

            }
            if ("Date".equals(fieldType)) {
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                compareTo = ((Date) fieldActualValue).compareTo(df.parse(fieldTestValue));
            }
        }
        catch (Exception e) {
            // any exception while parsing data return false
            return false;
        }
        return compareTo(compareTo, cc.getCorrectionOperatorCode());
    }


    /**
     * Compares string data
     * 
     * @param cc criteria
     * @param fieldTestValue test value
     * @param fieldActualValueString actual value
     * @return flag true if matches with criteria
     */
    public static boolean compareStringData(CorrectionCriteria cc, String fieldTestValue, String fieldActualValueString) {
        if ("eq".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.equals(fieldTestValue);
        }
        else if ("ne".equals(cc.getCorrectionOperatorCode())) {
            return (!fieldActualValueString.equals(fieldTestValue));
        }
        else if ("sw".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.startsWith(fieldTestValue);
        }
        else if ("ew".equals(cc.getCorrectionOperatorCode())) {
            return fieldActualValueString.endsWith(fieldTestValue);
        }
        else if ("ct".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.indexOf(fieldTestValue) > -1);
        }
        else if ("lt".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.compareTo(fieldTestValue) < 0);
        }
        else if ("le".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.compareTo(fieldTestValue) <= 0);
        }
        else if ("gt".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.compareTo(fieldTestValue) > 0);
        }
        else if ("ge".equals(cc.getCorrectionOperatorCode())) {
            return (fieldActualValueString.compareTo(fieldTestValue) >= 0);
        }
        throw new IllegalArgumentException("Unknown operator: " + cc.getCorrectionOperatorCode());
    }

    /**
     * Returns true is compared indicator matches
     * 
     * @param compareTo
     * @param operatorCode
     * @return
     */
    public static boolean compareTo(int compareTo, String operatorCode) {
        if ("eq".equals(operatorCode)) {
            return (compareTo == 0);
        }
        else if ("ne".equals(operatorCode)) {
            return (compareTo != 0);
        }
        else if ("lt".equals(operatorCode)) {
            return (compareTo < 0);
        }
        else if ("le".equals(operatorCode)) {
            return (compareTo <= 0);
        }
        else if ("gt".equals(operatorCode)) {
            return (compareTo > 0);
        }
        else if ("ge".equals(operatorCode)) {
            return (compareTo >= 0);
        }
        throw new IllegalArgumentException("Unknown operator: " + operatorCode);
    }

    /**
     * Converts the value into a string, with the appropriate formatting
     * 
     * @param fieldActualValue actual field value
     * @param fieldType field type (i.e. "String", "Integer", "Date")
     * @return String object value as a string
     */
    public static String convertToString(Object fieldActualValue, String fieldType) {
        if (fieldActualValue == null) {
            return "";
        }
        if ("String".equals(fieldType)) {
            return (String) fieldActualValue;
        }
        else if ("Integer".equals(fieldType)) {
            Integer i = (Integer) fieldActualValue;
            return i.toString();
        }
        else if ("KualiDecimal".equals(fieldType)) {
            KualiDecimal kd = (KualiDecimal) fieldActualValue;
            return kd.toString();
        }
        else if ("BigDecimal".equals(fieldType)) {
            BigDecimal bd = (BigDecimal) fieldActualValue;
            return bd.toString();
        }
        else if ("Date".equals(fieldType)) {
            Date d = (Date) fieldActualValue;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(d);
        }
        return "";
    }

    /**
     * Applies a list of change criteria groups to an origin entry. Note that the returned value, if not null, is a reference to the
     * same instance as the origin entry passed in (i.e. intentional side effect)
     * 
     * @param entry origin entry
     * @param matchCriteriaOnly if true and no criteria match, then this method will return null
     * @param changeCriteriaGroups list of change criteria groups to apply
     * @return the passed in entry instance, or null (see above)
     */
    public static OriginEntryFull applyCriteriaToEntry(OriginEntryFull entry, boolean matchCriteriaOnly, List<CorrectionChangeGroup> changeCriteriaGroups) {
        if (matchCriteriaOnly && !doesEntryMatchAnyCriteriaGroups(entry, changeCriteriaGroups)) {
            return null;
        }

        for (CorrectionChangeGroup ccg : changeCriteriaGroups) {
            int matches = 0;
            for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                if (entryMatchesCriteria(cc, entry)) {
                    matches++;
                }
            }

            // If they all match, change it
            if (matches == ccg.getCorrectionCriteria().size()) {
                for (CorrectionChange change : ccg.getCorrectionChange()) {
                    // Change the row
                    entry.setFieldValue(change.getCorrectionFieldName(), change.getCorrectionFieldValue());
                }
            }
        }
        return entry;
    }

    /**
     * Returns whether the entry matches any of the criteria groups
     * 
     * @param entry origin entry
     * @param groups collection of correction change group
     * @return true if origin entry matches any of the criteria groups
     */
    public static boolean doesEntryMatchAnyCriteriaGroups(OriginEntryFull entry, Collection<CorrectionChangeGroup> groups) {
        boolean anyGroupMatch = false;
        for (CorrectionChangeGroup ccg : groups) {
            int matches = 0;
            for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                if (CorrectionDocumentUtils.entryMatchesCriteria(cc, entry)) {
                    matches++;
                }
            }

            // If they all match, change it
            if (matches == ccg.getCorrectionCriteria().size()) {
                anyGroupMatch = true;
                break;
            }
        }
        return anyGroupMatch;
    }

    /**
     * Computes the statistics (credit amount, debit amount, row count) of a collection of origin entries.
     * 
     * @param entries list of orgin entry entries
     * @return {@link OriginEntryStatistics} statistics (credit amount, debit amount, row count) of a collection of origin entries.
     */
    public static OriginEntryStatistics getStatistics(Collection<OriginEntryFull> entries) {
        OriginEntryStatistics oes = new OriginEntryStatistics();

        for (OriginEntryFull oe : entries) {
            updateStatisticsWithEntry(oe, oes);
        }
        return oes;
    }

    /**
     * Returns whether the origin entry represents a debit
     * 
     * @param oe origin entry
     * @return true if origin entry represents a debit
     */
    public static boolean isDebit(OriginEntryFull oe) {
        return (KFSConstants.GL_DEBIT_CODE.equals(oe.getTransactionDebitCreditCode()));
    }

    /**
     * Returns whether the origin entry represents a budget
     * 
     * @param oe origin entry
     * @return true if origin entry represents a budget
     */
    public static boolean isBudget(OriginEntryFull oe) {
        return KFSConstants.GL_BUDGET_CODE.equals(oe.getTransactionDebitCreditCode());
    }

    /**
     * Returns whether the origin entry represents a credit
     * 
     * @param oe origin entry
     * @return true if origin entry represents a credit
     */
    public static boolean isCredit(OriginEntryFull oe) {
        return KFSConstants.GL_CREDIT_CODE.equals(oe.getTransactionDebitCreditCode());
    }

    /**
     * Given an instance of statistics, it adds information from the passed in entry to the statistics
     * 
     * @param entry origin entry
     * @param statistics adds statistics from the passed in origin entry to the passed in statistics
     */
    public static void updateStatisticsWithEntry(OriginEntryFull entry, OriginEntryStatistics statistics) {
        statistics.incrementCount();
        if (isDebit(entry)) {
            statistics.addDebit(entry.getTransactionLedgerEntryAmount());
        }
        else if (isCredit(entry)) {
            statistics.addCredit(entry.getTransactionLedgerEntryAmount());
        }
        else {
            statistics.addBudget(entry.getTransactionLedgerEntryAmount());
        }
    }

    /**
     * Sets document with the statistics data
     * 
     * @param statistics origin entry statistics that are being used to set document
     * @param document document with statistic information being set
     */
    public static void copyStatisticsToDocument(OriginEntryStatistics statistics, GeneralLedgerCorrectionProcessDocument document) {
        document.setCorrectionCreditTotalAmount(statistics.getCreditTotalAmount());
        document.setCorrectionDebitTotalAmount(statistics.getDebitTotalAmount());
        document.setCorrectionBudgetTotalAmount(statistics.getBudgetTotalAmount());
        document.setCorrectionRowCount(statistics.getRowCount());
    }
}
