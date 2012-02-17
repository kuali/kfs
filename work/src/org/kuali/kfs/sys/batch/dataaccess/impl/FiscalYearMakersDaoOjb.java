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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.ObjectModification;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMakersDao;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakersDao
 */
public class FiscalYearMakersDaoOjb extends PlatformAwareDaoBaseOjb implements FiscalYearMakersDao {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearMakersDaoOjb.class);
    
    protected static final String KEY_STRING_DELIMITER = "|";

    /**
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakersDao#deleteNewYearRows(java.lang.Integer,
     *      org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper)
     */
    public void deleteNewYearRows(Integer baseYear, FiscalYearMaker objectFiscalYearMaker) {
        if ( LOG.isInfoEnabled() ) {
            LOG.info(String.format("\ndeleting %s for target year(s)", objectFiscalYearMaker.getBusinessObjectClass().getName()));
        }

        QueryByCriteria queryID = new QueryByCriteria(objectFiscalYearMaker.getBusinessObjectClass(), objectFiscalYearMaker.createDeleteCriteria(baseYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);

        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.kfs.sys.batch.dataaccess.FiscalYearMakersDao#createNewYearRows(java.lang.Integer,
     *      org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker, boolean, java.util.Map)
     */
    public Collection<String> createNewYearRows(Integer baseYear, FiscalYearMaker fiscalYearMaker, boolean replaceMode, Map<Class<? extends FiscalYearBasedBusinessObject>, Set<String>> parentKeysWritten, boolean isParentClass) throws Exception {
        if ( LOG.isInfoEnabled() ) {
            LOG.info(String.format("\n copying %s from %d to %d", fiscalYearMaker.getBusinessObjectClass().getName(), baseYear, baseYear + 1));
        }

        int rowsRead = 0;
        int rowsWritten = 0;
        int rowsFailingRI = 0;

        // list of copy error messages to be written out at end
        List<String> copyErrors = new ArrayList<String>();

        // Set of primary key strings written
        Set<String> keysWritten = new HashSet<String>();

        // retrieve the list of next-year PKs for the given object
        List<String> primaryKeyFields = fiscalYearMaker.getPrimaryKeyPropertyNames();

        Set<String> nextYearPrimaryKeys = new HashSet<String>(2000);
        LOG.info( "Loading Next Year's PKs for comparison");        
        ReportQueryByCriteria nextYearKeyQuery = new ReportQueryByCriteria(fiscalYearMaker.getBusinessObjectClass(), primaryKeyFields.toArray(new String[0]), fiscalYearMaker.createNextYearSelectionCriteria(baseYear) );
        Iterator<Object[]> nextYearRecords = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(nextYearKeyQuery);
        StringBuilder keyString = new StringBuilder(40);
        int numNextYearRecords = 0;
        while ( nextYearRecords.hasNext() ) {
            numNextYearRecords++;
            keyString.setLength(0);
            Object[] record = nextYearRecords.next();
            for ( Object f : record ) {
                keyString.append( f ).append( KEY_STRING_DELIMITER );
            }
            nextYearPrimaryKeys.add(keyString.toString());
            if ( numNextYearRecords % 10000 == 0 ) {
                if ( LOG.isInfoEnabled() ) {
                    LOG.info("Processing Record: " + numNextYearRecords);
                }
            }
        }
        if ( LOG.isInfoEnabled() ) {
            LOG.info( "Completed load of next year keys.  " + numNextYearRecords + " keys loaded.");
            LOG.info( "Starting processing of existing FY rows" );
        }
        // retrieve base year records to copy
        QueryByCriteria queryId = new QueryByCriteria(fiscalYearMaker.getBusinessObjectClass(), fiscalYearMaker.createSelectionCriteria(baseYear));
        // BIG QUERY - GET ALL RECORDS for the current FY 
        Iterator<FiscalYearBasedBusinessObject> recordsToCopy = getPersistenceBrokerTemplate().getIteratorByQuery(queryId);
        
        
        while ( recordsToCopy.hasNext() ) {
            FiscalYearBasedBusinessObject objectToCopy = recordsToCopy.next();
            rowsRead++;
            if ( LOG.isInfoEnabled() ) {
                if ( rowsRead % 1000 == 0 ) {
                    LOG.info( "*** Processing Record: " + rowsRead + " -- Written So Far: " + rowsWritten + " -- Failing RI: " + rowsFailingRI + " -- Keys Written: " + keysWritten.size() );
                }
            }

            // remove reference/collection fields so they will not cause an issue with the insert
            removeNonPrimitiveFields(fiscalYearMaker, objectToCopy);

            // set record fields for new year
            fiscalYearMaker.changeForNewYear(baseYear, objectToCopy);

            // determine if new year record already exists and if so do not overwrite
            if ( nextYearPrimaryKeys.contains(getKeyString(fiscalYearMaker, primaryKeyFields, objectToCopy)) ) {
                if (isParentClass) {
                    addToKeysWritten(fiscalYearMaker, primaryKeyFields, objectToCopy, keysWritten);
                }
                continue;
            }

            // check parent records exist so RI will be satisfied
            if (!validateParentRecordsExist(fiscalYearMaker, objectToCopy, parentKeysWritten, copyErrors)) {
                rowsFailingRI++;
                continue;
            }

            // store new record
            getPersistenceBroker(true).store(objectToCopy, ObjectModification.INSERT);
            rowsWritten++;
            if (isParentClass) {
                addToKeysWritten(fiscalYearMaker, primaryKeyFields, objectToCopy, keysWritten);
            }
        }

        if (isParentClass) {
            parentKeysWritten.put(fiscalYearMaker.getBusinessObjectClass(), keysWritten);
        }

        if ( LOG.isInfoEnabled() ) {
            LOG.info(String.format("\n%s:\n%d read = %d\n%d written = %d\nfailed RI = %d", fiscalYearMaker.getBusinessObjectClass(), baseYear, rowsRead, baseYear + 1, rowsWritten, rowsFailingRI));
        }

        getPersistenceBrokerTemplate().clearCache();

        return copyErrors;
    }

    /**
     * Sets all reference and collection fields defined in the persistence layer to null on the given object
     * 
     * @param businessObject object to set properties for
     */
    protected void removeNonPrimitiveFields( FiscalYearMaker fiscalYearMaker, FiscalYearBasedBusinessObject businessObject) {
        try {
            @SuppressWarnings("rawtypes")
            Map<String, Class> referenceFields = fiscalYearMaker.getReferenceObjectProperties();
            for (String fieldName : referenceFields.keySet()) {
                if (!fieldName.equals("extension")) {
                    PropertyUtils.setSimpleProperty(businessObject, fieldName, null);
                }
            }

            @SuppressWarnings("rawtypes")
            Map<String, Class> collectionFields = fiscalYearMaker.getCollectionProperties();
            for (String fieldName : collectionFields.keySet()) {
                PropertyUtils.setSimpleProperty(businessObject, fieldName, null);
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to set non primitive fields to null: " + e.getMessage(), e);
        }
    }

    /**
     * Checks all parents for the object we are copying has a corresponding record for the child record
     * 
     * @return true if all parent records exist, false otherwise
     */
    protected boolean validateParentRecordsExist(FiscalYearMaker objectFiscalYearMaker, FiscalYearBasedBusinessObject childRecord, Map<Class<? extends FiscalYearBasedBusinessObject>, Set<String>> parentKeysWritten, List<String> copyErrors) throws Exception {
        // iterate through all parents, get attribute reference name and attempt to retrieve
        for (Class<? extends FiscalYearBasedBusinessObject> parentClass : objectFiscalYearMaker.getParentClasses()) {
            if ( !validateChildParentReferencesExist(objectFiscalYearMaker,childRecord, parentClass, parentKeysWritten.get(parentClass), copyErrors) ) {
                return false;
            }
        }

        return true;
    }

    /**
     * Validates the parent record(s) exists for the child record by retrieving the OJB reference (if found and foreign keys have
     * value)
     * 
     * @param childRecord child record we are inserting
     * @param parentClass class for parent of child
     * @param parentKeys Set of parent key Strings that have been written
     * @param copyErrors Collection for adding error messages
     * @return true if the parent record(s) exist, false otherwise
     */
    protected boolean validateChildParentReferencesExist(FiscalYearMaker objectFiscalYearMaker,FiscalYearBasedBusinessObject childRecord, Class<? extends FiscalYearBasedBusinessObject> parentClass, Set<String> parentKeys, List<String> copyErrors) throws Exception {
        boolean allChildParentReferencesExist = true;
        boolean foundParentReference = false;

        // get all references for child class
        @SuppressWarnings("rawtypes")
        Map<String, Class> referenceObjects = objectFiscalYearMaker.getReferenceObjectProperties();

        // iterate through to find references with the parent class
        for (String referenceName : referenceObjects.keySet()) {
            Class<? extends PersistableBusinessObject> referenceClass = referenceObjects.get(referenceName);

            if (parentClass.isAssignableFrom(referenceClass)) {
                foundParentReference = true;

                String foreignKeyString = getForeignKeyStringForReference(objectFiscalYearMaker, childRecord, referenceName);
                if (StringUtils.isNotBlank(foreignKeyString) 
                        && !parentKeys.contains(foreignKeyString)) {
                    // attempt to retrieve the parent reference in case it already existed
                    getPersistenceBroker(true).retrieveReference(childRecord, referenceName);
                    PersistableBusinessObject reference = (PersistableBusinessObject) PropertyUtils.getSimpleProperty(childRecord, referenceName);
                    if (ObjectUtils.isNull(reference)) {
                        allChildParentReferencesExist = false;
                        writeMissingParentCopyError(childRecord, parentClass, foreignKeyString, copyErrors);
                        LOG.warn( "Missing Parent Object: " + copyErrors.get(copyErrors.size()-1));
                    } else {
                        parentKeys.add(foreignKeyString);
                    }
                }
            }
        }

        if (!foundParentReference) {
            LOG.warn(String.format("\n!!! NO relationships between child %s and parent %s found in OJB descriptor\n", childRecord.getClass().getName(), parentClass.getName()));
        }

        return allChildParentReferencesExist;
    }

    /**
     * Builds a String containing foreign key values for the given reference of the business object
     * 
     * @param businessObject business object instance with reference
     * @param referenceName name of reference
     * @return String of foreign key values or null if any of the foreign key values are null
     */
    protected String getForeignKeyStringForReference( FiscalYearMaker fiscalYearMaker, FiscalYearBasedBusinessObject businessObject, String referenceName) throws Exception {
        Map<String, String> foreignKeyToPrimaryKeyMap = fiscalYearMaker.getForeignKeyMappings( referenceName );

        StringBuilder foreignKeyString = new StringBuilder(80);
        for (String fkFieldName : foreignKeyToPrimaryKeyMap.keySet()) {
            Object fkFieldValue = PropertyUtils.getSimpleProperty(businessObject, fkFieldName);
            if (fkFieldValue != null) {
                foreignKeyString.append( fkFieldValue.toString() ).append( KEY_STRING_DELIMITER );
            } else {
                foreignKeyString.setLength(0);
                break;
            }
        }

        return foreignKeyString.toString();
    }

    /**
     * Builds an error message when a parent record was not found for the child
     * 
     * @param childRecord child record we are inserting
     * @param parentClass class for parent of child
     * @param foreignKeyString string of foreign key values that was not found in parent
     * @param copyErrors Collection for adding error messages
     */
    protected void writeMissingParentCopyError(FiscalYearBasedBusinessObject childRecord, Class<? extends FiscalYearBasedBusinessObject> parentClass, String foreignKeyString, Collection<String> copyErrors) {
        StringBuilder errorCopyFailedMessage = new StringBuilder(150);
        errorCopyFailedMessage.append(childRecord.getClass().getName());
        errorCopyFailedMessage.append(" row for " + childRecord.toString());
        errorCopyFailedMessage.append(" - " + foreignKeyString);
        errorCopyFailedMessage.append(" not in ");
        errorCopyFailedMessage.append(parentClass.getName());

        copyErrors.add(errorCopyFailedMessage.toString());
    }

    /**
     * Builds a string from the primary key values and adds to given set
     * 
     * @param copiedObject object to grab key values for
     * @param keysWritten Set containing all pk strings
     */
    protected void addToKeysWritten( FiscalYearMaker fiscalYearMaker, List<String> keyFieldNames, FiscalYearBasedBusinessObject copiedObject, Set<String> keysWritten) throws Exception {
        keysWritten.add(getKeyString(fiscalYearMaker, keyFieldNames, copiedObject));
    }

    protected String getKeyString( FiscalYearMaker fiscalYearMaker, List<String> keyFieldNames, FiscalYearBasedBusinessObject businessObject ) throws Exception {
        StringBuilder keyString = new StringBuilder(40);
        for (String keyFieldName : keyFieldNames) {
            keyString.append( PropertyUtils.getSimpleProperty(businessObject, keyFieldName) ).append( KEY_STRING_DELIMITER );
        }
        return keyString.toString();
    }
}
