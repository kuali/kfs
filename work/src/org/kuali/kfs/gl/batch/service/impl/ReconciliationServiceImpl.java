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
package org.kuali.module.gl.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypeUtils;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.dao.ReconciliationDao;
import org.kuali.module.gl.exception.LoadException;
import org.kuali.module.gl.service.ReconciliationService;
import org.kuali.module.gl.util.ColumnReconciliation;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.ReconciliationBlock;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of ReconciliationService
 */
@Transactional
public class ReconciliationServiceImpl implements ReconciliationService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReconciliationServiceImpl.class);

    private ReconciliationDao reconciliationDao;
    private Class<? extends OriginEntryFull> originEntryClass;

    /**
     * A wrapper around {@link ColumnReconciliation} objects to provide it with information specific to the java beans representing
     * each BO. <br/><br/> In the default implementation of {@link org.kuali.module.gl.service.ReconciliationParserService}, each
     * {@link ColumnReconciliation} object may actually represent the sum of multiple fields across all origin entries (i.e.
     * ColumnReconciliation.getTokenizedFieldNames().length may be > 1). <br/><br/> Furthermore, the parser service returns
     * database field names as the identifier. This service requires the use of java bean names, so this class is used to maintain a
     * mapping between the DB names (in columnReconciliation.getTokenizedFieldNames()) and the java bean names (in
     * javaAttributeNames). These lists/arrays are the same size, and each element at the same position in both lists are mapped to
     * each other.
     */
    protected class JavaAttributeAugmentedColumnReconciliation {
        protected ColumnReconciliation columnReconciliation;
        protected List<String> javaAttributeNames;

        protected JavaAttributeAugmentedColumnReconciliation() {
            columnReconciliation = null;
            javaAttributeNames = null;
        }

        /**
         * Gets the columnReconciliation attribute.
         * 
         * @return Returns the columnReconciliation.
         */
        protected ColumnReconciliation getColumnReconciliation() {
            return columnReconciliation;
        }

        /**
         * Sets the columnReconciliation attribute value.
         * 
         * @param columnReconciliation The columnReconciliation to set.
         */
        protected void setColumnReconciliation(ColumnReconciliation columnReconciliation) {
            this.columnReconciliation = columnReconciliation;
        }

        /**
         * Sets the javaAttributeNames attribute value.
         * 
         * @param javaAttributeNames The javaAttributeNames to set.
         */
        protected void setJavaAttributeNames(List<String> javaAttributeNames) {
            this.javaAttributeNames = javaAttributeNames;
        }

        protected String getJavaAttributeName(int index) {
            return javaAttributeNames.get(index);
        }

        /**
         * Returns the number of attributes this object is holing
         * 
         * @return the count of attributes this holding
         */
        protected int size() {
            return javaAttributeNames.size();
        }
    }

    /**
     * Performs the reconciliation on origin entries using the data from the {@link ReconciliationBlock} parameter
     * 
     * @param entries origin entries
     * @param reconBlock reconciliation data
     * @param errorMessages a non-null list onto which error messages will be appended. This list will be modified by reference.
     * @see org.kuali.module.gl.service.ReconciliationService#reconcile(java.util.Iterator,
     *      org.kuali.module.gl.util.ReconciliationBlock, java.util.List)
     */
    public void reconcile(Iterator<OriginEntryFull> entries, ReconciliationBlock reconBlock, List<Message> errorMessages) {
        List<ColumnReconciliation> columns = reconBlock.getColumns();

        int numEntriesSuccessfullyLoaded = 0;

        // this value gets incremented every time the hasNext method of the iterator is called
        int numEntriesAttemptedToLoad = 1;

        // precompute the DB -> java name mappings so that we don't have to recompute them once for each row
        List<JavaAttributeAugmentedColumnReconciliation> javaAttributeNames = resolveJavaAttributeNames(columns);
        KualiDecimal[] columnSums = createColumnSumsArray(columns.size());

        // because of the way the OriginEntryFileIterator works (which is likely to be the type passed in as a parameter),
        // there are 2 primary causes of exceptions to be thrown by the Iterator.hasNext method:
        // 
        // - Underlying IO exception, this is a fatal error (i.e. we no longer attempt to continue parsing the file)
        // - Misformatted origin entry line, which is not fatal (i.e. continue parsing the file and report further misformatted
        // lines), but if it occurs, we don't want to do the final reconciliation step after this loop

        // operator short-circuiting is utilized to ensure that if there's a fatal error then we don't try to keep reading

        boolean entriesFullyIterated = false;

        // set to true if there's a problem parsing origin entry line(s)
        boolean loadExceptionEncountered = false;

        while (!entriesFullyIterated) {
            try {
                while (entries.hasNext()) {
                    numEntriesAttemptedToLoad++;
                    OriginEntryFull entry = entries.next();
                    for (int c = 0; c < columns.size(); c++) {
                        // this is for each definition of the "S" line in the reconciliation file
                        KualiDecimal columnValue = KualiDecimal.ZERO;

                        for (int f = 0; f < javaAttributeNames.get(c).size(); f++) {
                            String javaAttributeName = javaAttributeNames.get(c).getJavaAttributeName(f);
                            Object fieldValue = entry.getFieldValue(javaAttributeName);

                            if (fieldValue == null) {
                                // what to do about nulls?
                            }
                            else {
                                if (TypeUtils.isIntegralClass(fieldValue.getClass()) || TypeUtils.isDecimalClass(fieldValue.getClass())) {
                                    KualiDecimal castValue;
                                    if (fieldValue instanceof KualiDecimal) {
                                        castValue = (KualiDecimal) fieldValue;
                                    }
                                    else {
                                        castValue = new KualiDecimal(fieldValue.toString());
                                    }
                                    columnValue = columnValue.add(castValue);
                                }
                                else {
                                    throw new LoadException("The value for " + columns.get(c).getTokenizedFieldNames()[f] + " is not a numeric value.");
                                }
                            }
                        }
                        columnSums[c] = columnSums[c].add(columnValue);
                    }
                    numEntriesSuccessfullyLoaded++;
                }
            }
            catch (LoadException e) {
                loadExceptionEncountered = true;

                Message newMessage = new Message("Line " + numEntriesAttemptedToLoad + " parse error: " + e.getMessage(), Message.TYPE_FATAL);
                errorMessages.add(newMessage);

                numEntriesAttemptedToLoad++;
                continue;
            }
            catch (Exception e) {
                // entriesFullyIterated will stay false when we break out

                // encountered a potentially serious problem, abort reading of the data
                LOG.error("Error encountered trying to iterate through origin entry iterator", e);

                Message newMessage = new Message(e.getMessage(), Message.TYPE_FATAL);
                errorMessages.add(newMessage);

                break;
            }
            entriesFullyIterated = true;
        }

        if (entriesFullyIterated) {
            if (loadExceptionEncountered) {
                // generate a message saying reconcilation check did not continue
                Message newMessage = new Message("Reconciliation check failed because some origin entry lines could not be parsed.", Message.TYPE_FATAL);
                errorMessages.add(newMessage);
            }
            else {
                // see if the rowcount matches
                if (numEntriesSuccessfullyLoaded != reconBlock.getRowCount()) {
                    Message newMessage = generateRowCountMismatchMessage(reconBlock, numEntriesSuccessfullyLoaded);
                    errorMessages.add(newMessage);
                }

                // now that we've computed the statistics for all of the origin entries in the iterator,
                // compare the actual statistics (in the columnSums array) with the stats provided in the
                // reconciliation file (in the "columns" List attribute reconBlock object). Both of these
                // array/lists should have the same size
                for (int i = 0; i < columns.size(); i++) {
                    if (!columnSums[i].equals(columns.get(i).getDollarAmount())) {
                        Message newMessage = generateColumnSumErrorMessage(columns.get(i), columnSums[i]);
                        errorMessages.add(newMessage);
                    }
                }
            }
        }
    }

    /**
     * Generates the error message for the sum of column(s) not matching the reconciliation value
     * 
     * @param column the column reconciliation data (recall that this "column" can be the sum of several columns)
     * @param actualValue the value of the column(s)
     * @return the message
     */
    protected Message generateColumnSumErrorMessage(ColumnReconciliation column, KualiDecimal actualValue) {
        // TODO: if the kualiConfiguration service were to implement message params from ApplicationResources.properties, this would
        // be ideal for that
        StringBuilder buf = new StringBuilder();
        buf.append("Reconciliation failed for field value(s) \"");
        buf.append(column.getFieldName());
        buf.append("\", expected ");
        buf.append(column.getDollarAmount());
        buf.append(", found value ");
        buf.append(actualValue);
        buf.append(".");

        Message newMessage = new Message(buf.toString(), Message.TYPE_FATAL);
        return newMessage;
    }

    /**
     * Generates the error message for the number of entries reconciled being unequal to the expected value
     * 
     * @param block The file reconciliation data
     * @param actualRowCount the number of rows encountered
     * @return the message
     */
    protected Message generateRowCountMismatchMessage(ReconciliationBlock block, int actualRowCount) {
        // TODO: if the kualiConfiguration service were to implement message params from ApplicationResources.properties, this would
        // be ideal for that
        StringBuilder buf = new StringBuilder();
        buf.append("Reconciliation failed because an incorrect number of origin entry rows were successfully parsed.  Expected ");
        buf.append(block.getRowCount());
        buf.append(" row(s), parsed ");
        buf.append(actualRowCount);
        buf.append(" row(s).");

        Message newMessage = new Message(buf.toString(), Message.TYPE_FATAL);
        return newMessage;
    }

    /**
     * Performs basic checking to ensure that values are set up so that reconciliation can proceed
     * 
     * @param columns the columns generated by the {@link org.kuali.module.gl.service.ReconciliationParserService}
     * @param javaAttributeNames the java attribute names corresponding to each field in columns. (see
     *        {@link #resolveJavaAttributeNames(List)})
     * @param columnSums a list of KualiDecimals used to store column sums as reconciliation iterates through the origin entries
     * @param errorMessages a list to which error messages will be appended.
     * @return true if there are no problems, false otherwise
     */
    private boolean performSanityChecks(List<ColumnReconciliation> columns, List<JavaAttributeAugmentedColumnReconciliation> javaAttributeNames, KualiDecimal[] columnSums, List<Message> errorMessages) {
        boolean success = true;

        if (javaAttributeNames.size() != columnSums.length || javaAttributeNames.size() != columns.size()) {
            // sanity check
            errorMessages.add(new Message("Reconciliation error: Sizes of lists do not match", Message.TYPE_FATAL));
            success = false;
        }
        for (int i = 0; i < columns.size(); i++) {
            if (columns.get(i).getTokenizedFieldNames().length != javaAttributeNames.get(i).size()) {
                errorMessages.add(new Message("Reconciliation error: Error tokenizing column elements.  The number of database fields and java fields do not match.", Message.TYPE_FATAL));
                success = false;
            }
            for (int fieldIdx = 0; fieldIdx < javaAttributeNames.get(i).size(); i++) {
                if (StringUtils.isBlank(javaAttributeNames.get(i).getJavaAttributeName(fieldIdx))) {
                    errorMessages.add(new Message("Reconciliation error: javaAttributeName is blank for DB column: " + columns.get(i).getTokenizedFieldNames()[fieldIdx], Message.TYPE_FATAL));
                    success = false;
                }
            }
        }
        return success;
    }

    /**
     * Creates an array of {@link KualiDecimal}s of a given size, and initializes all elements to {@link KualiDecimal#ZERO}
     * 
     * @param size the size of the constructed array
     * @return the array, all initialized to {@link KualiDecimal#ZERO}
     */
    private KualiDecimal[] createColumnSumsArray(int size) {
        KualiDecimal[] array = new KualiDecimal[size];
        for (int i = 0; i < array.length; i++) {
            array[i] = KualiDecimal.ZERO;
        }
        return array;
    }

    /**
     * Resolves a mapping between the database columns and the java attribute name (i.e. bean property names)
     * 
     * @param columns columns parsed by the {@link org.kuali.module.gl.service.ReconciliationParserService}
     * @return a list of {@link JavaAttributeAugmentedColumnReconciliation} (see class description) objects. The returned list will
     *         have the same size as the parameter, and each element in one list corresponds to the element at the same position in
     *         the other list
     */
    private List<JavaAttributeAugmentedColumnReconciliation> resolveJavaAttributeNames(List<ColumnReconciliation> columns) {
        List<JavaAttributeAugmentedColumnReconciliation> attributes = new ArrayList<JavaAttributeAugmentedColumnReconciliation>();
        for (ColumnReconciliation column : columns) {
            JavaAttributeAugmentedColumnReconciliation c = new JavaAttributeAugmentedColumnReconciliation();
            c.setColumnReconciliation(column);
            c.setJavaAttributeNames(reconciliationDao.convertDBColumnNamesToJavaName(getOriginEntryClass(), column.getTokenizedFieldNames(), true));
            attributes.add(c);
        }
        return attributes;
    }

    /**
     * Gets the reconciliationDao attribute.
     * 
     * @return Returns the reconciliationDao.
     */
    protected ReconciliationDao getReconciliationDao() {
        return reconciliationDao;
    }

    /**
     * Sets the reconciliationDao attribute value.
     * 
     * @param reconciliationDao The reconciliationDao to set.
     */
    public void setReconciliationDao(ReconciliationDao reconciliationDao) {
        this.reconciliationDao = reconciliationDao;
    }

    /**
     * Gets the originEntryClass attribute.
     * 
     * @return Returns the originEntryClass.
     */
    protected Class<? extends OriginEntryFull> getOriginEntryClass() {
        return originEntryClass;
    }

    /**
     * Sets the originEntryClass attribute value.
     * 
     * @param originEntryClass The originEntryClass to set.
     */
    public void setOriginEntryClass(Class<? extends OriginEntryFull> originEntryClass) {
        this.originEntryClass = originEntryClass;
    }
}
