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

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.ObjectType;
import org.kuali.module.chart.bo.ProjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.service.OriginEntryLookupService;
import org.kuali.module.gl.util.CachingLookup;

/**
 * This class retrieves the important references related to the OriginEntryFull family of business objects;
 * it uses a cache to store records its seen before, which hopefully improves performance
 */
public class OriginEntryLookupServiceImpl implements OriginEntryLookupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryLookupServiceImpl.class);

    private PersistenceStructureService persistenceStructureService;
    private ThreadLocal<CachingLookup> localLookupService = new ThreadLocal<CachingLookup>();
    private Map<String, List> primaryKeyLists = new HashMap<String, List>();

    /**
     * Get A21SubAccount for given origin entryable
     * 
     * @param entry the origin entry to retrieve the A21 sub account of
     * @return the related A21 SubAccount record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getA21SubAccount(org.kuali.module.gl.bo.OriginEntry)
     */
    public A21SubAccount getA21SubAccount(OriginEntry entry) {
        return lookupReference(entry, A21SubAccount.class);
    }

    /**
     * Retrieve account for given origin entry
     * 
     * @param entry the origin entry to retrieve the account of
     * @return the related account record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getAccount(org.kuali.module.gl.bo.OriginEntry)
     */
    public Account getAccount(OriginEntry entry) {
        return lookupReference(entry, Account.class);
    }

    /**
     * Retrieves the accounting period for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the accounting period of
     * @return the related AccountingPeriod record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getAccountingPeriod(org.kuali.module.gl.bo.OriginEntry)
     */
    public AccountingPeriod getAccountingPeriod(OriginEntry entry) {
        return lookupReference(entry, AccountingPeriod.class);
    }

    /**
     * Retrieve balance type, or, evidently, balance typ, for given origin entry
     * 
     * @param entry the origin entry to retrieve the balance type of
     * @return the related balance typ record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getBalanceType(org.kuali.module.gl.bo.OriginEntry)
     */
    public BalanceTyp getBalanceType(OriginEntry entry) {
        return lookupReference(entry, BalanceTyp.class, "code:financialBalanceTypeCode");
    }

    /**
     * Retrieve a chart for the given origin entry
     * 
     * @param entry the origin entry to get the chart for
     * @return the related Chart record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getChart(org.kuali.module.gl.bo.OriginEntry)
     */
    public Chart getChart(OriginEntry entry) {
        return lookupReference(entry, Chart.class);
    }

    /**
     * Get document type for given origin entryable
     * 
     * @param entry the origin entry to retrieve the document type of
     * @return the related document type record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getDocumentType(org.kuali.module.gl.bo.OriginEntry)
     */
    public DocumentType getDocumentType(OriginEntry entry) {
        return lookupReference(entry, DocumentType.class);
    }

    /**
     * Get the reference document type for the given origin entryable
     * 
     * @param entry origin entryable to lookup the reference document type for
     * @return the related reference DocumentType record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getReferenceDocumentType(org.kuali.module.gl.bo.OriginEntry)
     */
    public DocumentType getReferenceDocumentType(OriginEntry entry) {
        return lookupReference(entry, DocumentType.class, "financialDocumentTypeCode:referenceFinancialDocumentTypeCode");
    }

    /**
     * Retrieve financial object for given origin entry
     * 
     * @param entry the origin entry to retrieve the financial object of
     * @return the related financial object record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getFinancialObject(org.kuali.module.gl.bo.OriginEntry)
     */
    public ObjectCode getFinancialObject(OriginEntry entry) {
        return lookupReference(entry, ObjectCode.class);
    }

    /**
     * Get financial sub object for given origin entryable
     * 
     * @param entry the origin entry to retrieve the financial sub object of
     * @return the related financial sub object record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getFinancialSubObject(org.kuali.module.gl.bo.OriginEntry)
     */
    public SubObjCd getFinancialSubObject(OriginEntry entry) {
        return lookupReference(entry, SubObjCd.class);
    }

    /**
     * Get object type for given origin entry
     * 
     * @param entry the origin entry to retrieve the object type of
     * @return the related object type record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getObjectType(org.kuali.module.gl.bo.OriginEntry)
     */
    public ObjectType getObjectType(OriginEntry entry) {
        return lookupReference(entry, ObjectType.class, "code:financialObjectTypeCode");
    }

    /**
     * Retrieve option for given origin entry
     * 
     * @param entry the origin entry to retrieve the related options record of
     * @return the related Options record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getOption(org.kuali.module.gl.bo.OriginEntry)
     */
    public Options getOption(OriginEntry entry) {
        return lookupReference(entry, Options.class);
    }

    /**
     * Retrieves the origination code for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the origin code of
     * @return the related OriginationCode record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getOriginationCode(org.kuali.module.gl.bo.OriginEntry)
     */
    public OriginationCode getOriginationCode(OriginEntry entry) {
        return lookupReference(entry, OriginationCode.class, "originationCode:referenceFinancialSystemOriginationCode");
    }

    /**
     * Retrieves the project code for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the project code of
     * @return the related ProjectCode record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getProjectCode(org.kuali.module.gl.bo.OriginEntry)
     */
    public ProjectCode getProjectCode(OriginEntry entry) {
        return lookupReference(entry, ProjectCode.class, "code:projectCode");
    }

    /**
     * Get sub account for given origin entry
     * 
     * @param entry the origin entry to retrieve the sub account of
     * @return the related SubAccount record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getSubAccount(org.kuali.module.gl.bo.OriginEntry)
     */
    public SubAccount getSubAccount(OriginEntry entry) {
        return lookupReference(entry, SubAccount.class);
    }

    /**
     * This method takes in an origin entry and returns the primary key map for the related class, based on values of that origin
     * entry.
     * 
     * @param entry the entry to perform the lookup on
     * @param referenceClassToRetrieve the class of a related object
     * @param fieldNameOverrides if the name of a field in the entry is not the name of the field in a related class, this map can override the entry's field names for the sake of the lookup
     * @return a Map with the key of the object to lookup in it
     */
    private Map<String, Object> getKeyMapFromEntry(OriginEntry entry, Class referenceClassToRetrieve, Map<String, String> fieldNameOverrides) {
        Map<String, Object> keyMap = new TreeMap<String, Object>();

        List keyFields = getPrimaryKeyFields(referenceClassToRetrieve);
        for (Object keyFieldAsObject : keyFields) {
            String keyField = (String) keyFieldAsObject;
            String originalKeyField = keyField;
            if (fieldNameOverrides.containsKey(keyField)) {
                keyField = fieldNameOverrides.get(keyField);
            }
            try {
                Object property = PropertyUtils.getProperty(entry, keyField);
                if (property != null) {
                    keyMap.put(originalKeyField, property);
                }
                else {
                    keyMap = null;
                    break;
                }
            }
            catch (IllegalAccessException e) {
                LOG.fatal("Illegal Access Exception trying to access field: " + keyField);
                throw new RuntimeException(e);
            }
            catch (InvocationTargetException e) {
                LOG.fatal("Illegal Target Exception trying to access field: " + keyField);
                throw new RuntimeException(e);
            }
            catch (NoSuchMethodException e) {
                LOG.fatal("No such method exception trying to access field: " + keyField);
                throw new RuntimeException(e);
            }
        }

        return keyMap;
    }

    /**
     * This method looks up a class reference by an origin entry
     * 
     * @param <T> the class of the object that needs to be looked up
     * @param entry an entry to perform the lookup on
     * @param type the class of the related object to lookup
     * @return the related object or null if not found in the cache or persistence store
     */
    private <T> T lookupReference(OriginEntry entry, Class<T> type) {
        return lookupReference(entry, type, null);
    }

    /**
     * This method looks up a class reference by an origin entry, with certain primary key field names overridden
     * 
     * @param <T> the class of the object that needs to be looked up
     * @param entry an entry to perform the lookup on
     * @param type the class of the related object to lookup
     * @param fieldNameOverrides if the name of a field in the entry is not the name of the field in a related class, this map can override the entry's field names for the sake of the lookup 
     * @return the related object or null if not found in the cache or persistence store
     */
    private <T> T lookupReference(OriginEntry entry, Class<T> type, String fieldNameOverrides) {
        Map<String, Object> pk = getKeyMapFromEntry(entry, type, convertFieldsToMap(fieldNameOverrides));
        return (T) localLookupService.get().get(type, pk);
    }

    /**
     * Converts the field name overrides string and turns it into objects
     * 
     * @param fieldNameOverrides if the name of a field in the entry is not the name of the field in a related class, this map can override the entry's field names for the sake of the lookup 
     * @return a Map where the key is the name of the field in the entry and the value is the name of the field in the related class
     */
    private Map<String, String> convertFieldsToMap(String fieldNameOverrides) {
        Map<String, String> overrides = new HashMap<String, String>();
        if (fieldNameOverrides != null && fieldNameOverrides.length() > 0) {
            String[] fieldConversionEntries = fieldNameOverrides.split(";");
            if (fieldConversionEntries != null && fieldConversionEntries.length > 0) {
                for (String entry : fieldConversionEntries) {
                    String[] splitEntry = entry.split(":");
                    if (splitEntry != null && splitEntry.length == 2) {
                        overrides.put(splitEntry[0], splitEntry[1]);
                    }
                }
            }
        }
        return overrides;
    }

    /**
     * This method gets the list of primary key fields for a class, with some caching involved
     * 
     * @param clazz the class to get primary key fields for
     * @return a List of String names of the fields for the primary key
     */
    private List getPrimaryKeyFields(Class clazz) {
        List keyFields = primaryKeyLists.get(clazz.getName());
        if (keyFields == null) {
            keyFields = persistenceStructureService.listPrimaryKeyFieldNames(clazz);
            primaryKeyLists.put(clazz.getName(), keyFields);
        }
        return keyFields;
    }

    /**
     * Gets the persistenceStructureService attribute.
     * 
     * @return Returns the persistenceStructureService.
     */
    public PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
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
     * Gets the lookupService attribute.
     * 
     * @return Returns the lookupService.
     */
    public CachingLookup getLookupService() {
        return localLookupService.get();
    }

    /**
     * Sets the lookupService attribute value.
     * 
     * @param lookupService The lookupService to set.
     */
    public void setLookupService(CachingLookup lookupService) {
        this.localLookupService.set(lookupService);
    }
}
