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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.apache.ojb.broker.util.ObjectModification;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMakersDao;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakersDao
 */
public class FiscalYearMakersDaoOjb extends PlatformAwareDaoBaseOjb implements FiscalYearMakersDao {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearMakersDaoOjb.class);
    
    private static final String KEY_STRING_DELIMITER = "|";

    private PersistenceStructureService persistenceStructureService;
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakersDao#deleteNewYearRows(java.lang.Integer,
     *      org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper)
     */
    public void deleteNewYearRows(Integer baseYear, FiscalYearMaker objectFiscalYearMaker) {
        LOG.info(String.format("\ndeleting %s for %d", objectFiscalYearMaker.getBusinessObjectClass().getName(), baseYear + 1));

        QueryByCriteria queryID = new QueryByCriteria(objectFiscalYearMaker.getBusinessObjectClass(), objectFiscalYearMaker.createDeleteCriteria(baseYear));
        getPersistenceBrokerTemplate().deleteByQuery(queryID);

        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * @see org.kuali.kfs.sys.batch.dataaccess.FiscalYearMakersDao#createNewYearRows(java.lang.Integer,
     *      org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker, boolean, java.util.Map)
     */
    public Collection<String> createNewYearRows(Integer baseYear, FiscalYearMaker objectFiscalYearMaker, boolean replaceMode, Map<Class<? extends PersistableBusinessObject>, Set<String>> parentKeysWritten, boolean isParentClass) {
        LOG.info(String.format("\n copying %s from %d to %d", objectFiscalYearMaker.getBusinessObjectClass(), baseYear, baseYear + 1));

        Integer rowsRead = new Integer(0);
        Integer rowsWritten = new Integer(0);
        Integer rowsFailingRI = new Integer(0);

        // list of copy error messages to be written out at end
        Collection<String> copyErrors = new ArrayList<String>();

        // Set of primary key strings written
        Set<String> keysWritten = new HashSet<String>();

        // retrieve base year records to copy
        QueryByCriteria queryId = new QueryByCriteria(objectFiscalYearMaker.getBusinessObjectClass(), objectFiscalYearMaker.createSelectionCriteria(baseYear));
        Collection<PersistableBusinessObject> recordsToCopy = getPersistenceBrokerTemplate().getCollectionByQuery(queryId);
        for (PersistableBusinessObject objectToCopy : recordsToCopy) {
            rowsRead = rowsRead + 1;

            // remove reference/collection fields so they will not cause an issue with the insert
            removeNonPrimitiveFields(objectToCopy);

            // set record fields for new year
            objectFiscalYearMaker.changeForNewYear(baseYear, objectToCopy);

            // determine if new year record already exists and if so do not overwrite
            PersistableBusinessObject foundRecord = businessObjectService.retrieve(objectToCopy);
            if (foundRecord != null) {
                addToKeysWritten(objectToCopy, keysWritten);
                continue;
            }

            // check parent records exist so RI will be satisfied
            boolean allParentRecordsExist = validateParentRecordsExist(objectFiscalYearMaker, objectToCopy, parentKeysWritten, copyErrors);
            if (!allParentRecordsExist) {
                rowsFailingRI = rowsFailingRI + 1;
                continue;
            }

            // store new record
            getPersistenceBroker(true).store(objectToCopy, ObjectModification.INSERT);
            rowsWritten = rowsWritten + 1;

            addToKeysWritten(objectToCopy, keysWritten);
        }

        if (isParentClass) {
            parentKeysWritten.put(objectFiscalYearMaker.getBusinessObjectClass(), keysWritten);
        }

        LOG.warn(String.format("\n%s:\n%d read = %d\n%d written = %d\nfailed RI = %d", objectFiscalYearMaker.getBusinessObjectClass(), baseYear, rowsRead, baseYear + 1, rowsWritten, rowsFailingRI));

        getPersistenceBrokerTemplate().clearCache();

        return copyErrors;
    }

    /**
     * Sets all reference and collection fields defined in the persistence layer to null on the given object
     * 
     * @param businessObject object to set properties for
     */
    protected void removeNonPrimitiveFields(PersistableBusinessObject businessObject) {
        try {
            Map<String, Class> referenceFields = persistenceStructureService.listReferenceObjectFields(businessObject);
            for (String fieldName : referenceFields.keySet()) {
                ObjectUtils.setObjectProperty(businessObject, fieldName, null);
            }

            Map<String, Class> collectionFields = persistenceStructureService.listCollectionObjectTypes(businessObject);
            for (String fieldName : collectionFields.keySet()) {
                ObjectUtils.setObjectProperty(businessObject, fieldName, null);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to set non primitive fields to null: " + e.getMessage(), e);
        }
    }

    /**
     * Checks all parents for the object we are copying has a corresponding record for the child record
     * 
     * @return true if all parent records exist, false otherwise
     */
    protected boolean validateParentRecordsExist(FiscalYearMaker objectFiscalYearMaker, PersistableBusinessObject childRecord, Map<Class<? extends PersistableBusinessObject>, Set<String>> parentKeysWritten, Collection<String> copyErrors) {
        boolean allParentRecordsExist = true;

        // iterate through all parents, get attribute reference name and attempt to retrieve
        for (Class<? extends PersistableBusinessObject> parentClass : objectFiscalYearMaker.getParentClasses()) {
            allParentRecordsExist &= validateChildParentReferencesExist(childRecord, parentClass, parentKeysWritten.get(parentClass), copyErrors);
        }

        return allParentRecordsExist;
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
    protected boolean validateChildParentReferencesExist(PersistableBusinessObject childRecord, Class<? extends PersistableBusinessObject> parentClass, Set<String> parentKeys, Collection<String> copyErrors) {
        boolean allChildParentReferencesExist = true;
        boolean foundParentReference = false;

        // get all references for child class
        HashMap<String, Class<? extends PersistableBusinessObject>> referenceObjects = (HashMap<String, Class<? extends PersistableBusinessObject>>) persistenceStructureService.listReferenceObjectFields(childRecord.getClass());

        // iterate through to find references with the parent class
        for (String referenceName : referenceObjects.keySet()) {
            Class<? extends PersistableBusinessObject> referenceClass = referenceObjects.get(referenceName);

            if (parentClass.isAssignableFrom(referenceClass)) {
                foundParentReference = true;

                String foreignKeyString = getForeignKeyStringForReference(childRecord, referenceName);
                if (StringUtils.isNotBlank(foreignKeyString) && !parentKeys.contains(foreignKeyString)) {
                    // attempt to retrieve the parent reference in case it already existed
                    childRecord.refreshReferenceObject(referenceName);
                    PersistableBusinessObject reference = (PersistableBusinessObject) ObjectUtils.getPropertyValue(childRecord, referenceName);
                    if (ObjectUtils.isNull(reference)) {
                        allChildParentReferencesExist = false;
                        writeMissingParentCopyError(childRecord, parentClass, foreignKeyString, copyErrors);
                    }
                    else {
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
    protected String getForeignKeyStringForReference(PersistableBusinessObject businessObject, String referenceName) {
        Map<String, String> foreignKeyToPrimaryKeyMap = persistenceStructureService.getForeignKeysForReference(businessObject.getClass(), referenceName);

        String foreignKeyString = "";
        for (String fkFieldName : foreignKeyToPrimaryKeyMap.keySet()) {
            Object fkFieldValue = ObjectUtils.getPropertyValue(businessObject, fkFieldName);
            if (fkFieldValue != null) {
                foreignKeyString += fkFieldValue.toString() + KEY_STRING_DELIMITER;
            }
            else {
                foreignKeyString = null;
                break;
            }
        }

        return foreignKeyString;
    }

    /**
     * Builds an error message when a parent record was not found for the child
     * 
     * @param childRecord child record we are inserting
     * @param parentClass class for parent of child
     * @param foreignKeyString string of foreign key values that was not found in parent
     * @param copyErrors Collection for adding error messages
     */
    protected void writeMissingParentCopyError(PersistableBusinessObject childRecord, Class<? extends PersistableBusinessObject> parentClass, String foreignKeyString, Collection<String> copyErrors) {
        StringBuilder errorCopyFailedMessage = new StringBuilder();
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
    protected void addToKeysWritten(PersistableBusinessObject copiedObject, Set<String> keysWritten) {
        String keyString = "";

        List<String> keyFieldNames = persistenceStructureService.getPrimaryKeys(copiedObject.getClass());
        for (String keyFieldName : keyFieldNames) {
            keyString += ObjectUtils.getPropertyValue(copiedObject, keyFieldName) + KEY_STRING_DELIMITER;
        }

        keysWritten.add(keyString);
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
