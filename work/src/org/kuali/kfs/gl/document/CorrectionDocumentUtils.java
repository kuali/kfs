/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.util;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.bo.CorrectionChange;
import org.kuali.module.gl.bo.CorrectionChangeGroup;
import org.kuali.module.gl.bo.CorrectionCriteria;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.web.optionfinder.OriginEntryFieldFinder;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.web.optionfinder.LaborOriginEntryFieldFinder;

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
    
    public static int getRecordCountFunctionalityLimit() {
        String limitString = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.GL_NAMESPACE, KFSConstants.Components.GENERAL_LEDGER_CORRECTION_PROCESS_DOC, 
                KFSConstants.GeneralLedgerCorrectionProcessApplicationParameterKeys.RECORD_COUNT_FUNCTIONALITY_LIMIT);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        
        return DEFAULT_RECORD_COUNT_FUNCTIONALITY_LIMIT;
    }
    
    public static int getRecordsPerPage() {
        String limitString = SpringContext.getBean(KualiConfigurationService.class).getParameterValue(KFSConstants.GL_NAMESPACE,
                KFSConstants.Components.GENERAL_LEDGER_CORRECTION_PROCESS_DOC,
                KFSConstants.GeneralLedgerCorrectionProcessApplicationParameterKeys.RECORDS_PER_PAGE);
        if (limitString != null) {
            return Integer.valueOf(limitString);
        }
        return DEFAULT_RECORDS_PER_PAGE;
    }
    
    /**
     * 
     * @param correctionForm
     * @return
     */
    public static boolean isRestrictedFunctionalityMode(int inputGroupSize, int recordCountFunctionalityLimit) {
        return (recordCountFunctionalityLimit != CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_UNLIMITED && inputGroupSize >= recordCountFunctionalityLimit) ||
                recordCountFunctionalityLimit == CorrectionDocumentUtils.RECORD_COUNT_FUNCTIONALITY_LIMIT_IS_NONE;
    }
    
    /**
     * When a correction criterion is about to be added to a group, this will check if it is valid, meaning that
     * the field name is not blank
     * 
     * @param correctionCriteria
     * @return
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
     * @param correctionCriteria
     * @return
     */
    public static boolean validCorrectionCriteriaForSaving(CorrectionCriteria correctionCriteria) {
        return correctionCriteria == null || 
                (StringUtils.isBlank(correctionCriteria.getCorrectionFieldName()) &&  StringUtils.isBlank(correctionCriteria.getCorrectionFieldValue()));
    }
    
    /**
     * When a correction change is about to be added to a group, this will check if it is valid, meaning that
     * the field name is not blank
     * 
     * @param correctionCriteria
     * @return
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
     * @param correctionCriteria
     * @return
     */
    public static boolean validCorrectionChangeForSaving(CorrectionChange correctionChange) {
        return correctionChange == null || 
                (StringUtils.isBlank(correctionChange.getCorrectionFieldName()) &&  StringUtils.isBlank(correctionChange.getCorrectionFieldValue()));
    }
    
    /**
     * Sets all origin entries' entry IDs to null within the collection.
     * 
     * @param originEntries
     */
    public static void setAllEntryIdsToNull(Collection<OriginEntryFull> originEntries) {
        for (OriginEntryFull entry : originEntries) {
            entry.setEntryId(null);
        }
    }
    
    /**
     * Sets all origin entries' entry IDs to be sequential starting from 0 in the collection
     * 
     * @param originEntries
     */
    public static void setSequentialEntryIds(Collection<OriginEntryFull> originEntries) {
        int index = 0;
        for (OriginEntryFull entry : originEntries) {
            entry.setEntryId(new Integer(index));
            index++;
        }
    }
    
    /**
     * Returns whether an origin entry matches the passed in criteria.  If both the criteria and actual value are both String types and are empty, null, or whitespace only,
     * then they will match.
     * 
     * @param cc
     * @param oe
     * @return
     */
    public static boolean entryMatchesCriteria(CorrectionCriteria cc, OriginEntryFull oe) {
        OriginEntryFieldFinder oeff = new OriginEntryFieldFinder();
        Object fieldActualValue = oe.getFieldValue(cc.getCorrectionFieldName());
        String fieldTestValue = StringUtils.isBlank(cc.getCorrectionFieldValue()) ? "" : cc.getCorrectionFieldValue();
        String fieldType = oeff.getFieldType(cc.getCorrectionFieldName());
        String fieldActualValueString = convertToString(fieldActualValue, fieldType);

        if ("String".equals(fieldType) && StringUtils.isBlank(fieldActualValueString)) {
            fieldActualValueString = "";
        }
        
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
        throw new IllegalArgumentException("Unknown operator: " + cc.getCorrectionOperatorCode());
    }
    
    /**
     * Returns whether an origin entry matches the passed in criteria.  If both the criteria and actual value are both String types and are empty, null, or whitespace only,
     * then they will match.
     * 
     * @param cc
     * @param oe
     * @return
     */
    public static boolean laborEntryMatchesCriteria(CorrectionCriteria cc, OriginEntryFull oe) {
        LaborOriginEntryFieldFinder loeff = new LaborOriginEntryFieldFinder();
        LaborOriginEntry loe = (LaborOriginEntry) oe;
        Object fieldActualValue = loe.getFieldValue(cc.getCorrectionFieldName());
        String fieldTestValue = StringUtils.isBlank(cc.getCorrectionFieldValue()) ? "" : cc.getCorrectionFieldValue();
        String fieldType = loeff.getFieldType(cc.getCorrectionFieldName());
        String fieldActualValueString = convertToString(fieldActualValue, fieldType);

        if ("String".equals(fieldType) && StringUtils.isBlank(fieldActualValueString)) {
            fieldActualValueString = "";
        }
        
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
        throw new IllegalArgumentException("Unknown operator: " + cc.getCorrectionOperatorCode());
    }
    
    /**
     * Converts the value into a string, with the appropriate formatting
     * 
     * @param fieldActualValue
     * @param fieldType
     * @return
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
        else if ("Date".equals(fieldType)) {
            Date d = (Date) fieldActualValue;
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            return df.format(d);
        }
        return "";
    }
    
    /**
     * Applies a list of change criteria groups to an origin entry.  Note that the returned value, if not null, is a reference to the same
     * instance as the origin entry passed in (i.e. intentional side effect)
     * 
     * @param entry
     * @param matchCriteriaOnly if true and no criteria match, then this method will return null 
     * @param changeCriteriaGroups
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
     * @param entry
     * @param groups
     * @return
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
     * Returns whether the entry matches any of the criteria groups
     * 
     * @param entry
     * @param groups
     * @return
     */
    public static boolean doesLaborEntryMatchAnyCriteriaGroups(OriginEntryFull entry, Collection<CorrectionChangeGroup> groups) {
        boolean anyGroupMatch = false;
        for (CorrectionChangeGroup ccg : groups) {
            int matches = 0;
            for (CorrectionCriteria cc : ccg.getCorrectionCriteria()) {
                if (CorrectionDocumentUtils.laborEntryMatchesCriteria(cc, entry)) {
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
     * @param entries
     * @return
     */
    public static OriginEntryStatistics getStatistics(Collection<OriginEntryFull> entries) {
        OriginEntryStatistics oes = new OriginEntryStatistics();

        for (OriginEntryFull oe : entries) {
            updateStatisticsWithEntry(oe, oes);
        }
        return oes;
    }

    /**
     * Returns whether the origin entry represents a debit budget
     * @param oe
     * @return
     */
    public static boolean isDebitBudget(OriginEntryFull oe) {
        return (oe.getTransactionDebitCreditCode() == null || KFSConstants.GL_BUDGET_CODE.equals(oe.getTransactionDebitCreditCode()) || KFSConstants.GL_DEBIT_CODE.equals(oe.getTransactionDebitCreditCode()));
    }
    
    /**
     * Given an instance of statistics, it adds infromation from the passed in entry to the statistics
     * @param entry
     * @param statistics
     */
    public static void updateStatisticsWithEntry(OriginEntryFull entry, OriginEntryStatistics statistics) {
        statistics.incrementCount();
        if (isDebitBudget(entry)) {
            statistics.addDebit(entry.getTransactionLedgerEntryAmount());
        }
        else {
            statistics.addCredit(entry.getTransactionLedgerEntryAmount());
        }
    }
    
    /**
     * Sets document with the statistics data
     * @param statistics
     * @param document
     */
    public static void copyStatisticsToDocument(OriginEntryStatistics statistics, CorrectionDocument document) {
        document.setCorrectionCreditTotalAmount(statistics.getCreditTotalAmount());
        document.setCorrectionDebitTotalAmount(statistics.getDebitTotalAmount());
        document.setCorrectionRowCount(statistics.getRowCount());
    }
}
