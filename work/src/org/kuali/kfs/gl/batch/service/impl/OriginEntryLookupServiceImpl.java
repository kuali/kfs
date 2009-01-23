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
package org.kuali.kfs.gl.batch.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.coa.businessobject.ProjectCode;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.kfs.gl.batch.service.OriginEntryLookupService;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.UniversityDate;
import org.kuali.kfs.gl.dataaccess.CachingDao;
import org.kuali.kfs.gl.service.impl.CachingLookup;
import org.kuali.kfs.sys.businessobject.GeneralLedgerInputType;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.service.PersistenceStructureService;

/**
 * This class retrieves the important references related to the OriginEntryFull family of business objects; it uses a cache to store
 * records its seen before, which hopefully improves performance
 */
@NonTransactional
public class OriginEntryLookupServiceImpl implements OriginEntryLookupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryLookupServiceImpl.class);

    private PersistenceStructureService persistenceStructureService;
    private ThreadLocal<CachingLookup> localLookupService = new ThreadLocal<CachingLookup>();
    private Map<String, List> primaryKeyLists = new HashMap<String, List>();
    // TODO: maybe temporary - could use CachingLookup instead and put this there, but was quicker to do this way
    private CachingDao cachingDao;


    /**
     * Get A21SubAccount for given origin entryable
     * 
     * @param entry the origin entry to retrieve the A21 sub account of
     * @return the related A21 SubAccount record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getA21SubAccount(org.kuali.module.gl.bo.OriginEntry)
     */
    public A21SubAccount getA21SubAccount(OriginEntry entry) {
        return cachingDao.getA21SubAccount(entry);
    }

    /**
     * Retrieve account for given origin entry
     * 
     * @param entry the origin entry to retrieve the account of
     * @return the related account record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getAccount(org.kuali.module.gl.bo.OriginEntry)
     */
    public Account getAccount(OriginEntry entry) {
        return cachingDao.getAccount(entry);
    }


    /**
     * Retrieves the accounting period for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the accounting period of
     * @return the related AccountingPeriod record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getAccountingPeriod(org.kuali.module.gl.bo.OriginEntry)
     */
    public AccountingPeriod getAccountingPeriod(OriginEntry entry) {
        return cachingDao.getAccountingPeriod(entry);
    }

    public AccountingPeriod getAccountingPeriod(Integer universityFiscalYear, String universityFiscalPeriodCode) {
        return cachingDao.getAccountingPeriod(universityFiscalYear, universityFiscalPeriodCode);
    }


    /**
     * Retrieve balance type, or, evidently, balance typ, for given origin entry
     * 
     * @param entry the origin entry to retrieve the balance type of
     * @return the related balance typ record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getBalanceType(org.kuali.module.gl.bo.OriginEntry)
     */
    public BalanceType getBalanceType(OriginEntry entry) {
        return cachingDao.getBalanceType(entry);
    }

    /**
     * Retrieve a chart for the given origin entry
     * 
     * @param entry the origin entry to get the chart for
     * @return the related Chart record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getChart(org.kuali.module.gl.bo.OriginEntry)
     */
    public Chart getChart(OriginEntry entry) {
        return cachingDao.getChart(entry);
    }

    /**
     * Get GL input type for given origin entryable
     * 
     * @param entry the origin entry to retrieve the GL input type of
     * @return the related GeneralLedgerInputType record, or null if not found
     */
    public GeneralLedgerInputType getGeneralLedgerInputType(OriginEntry entry) {
        return cachingDao.getGeneralLedgerInputType(entry);
    }

    /**
     * Get the reference GL input type for the given origin entryable
     * 
     * @param entry origin entryable to lookup the reference GL input type for
     * @return the related reference GeneralLedgerInputType record, or null if not found
     */
    public GeneralLedgerInputType getReferenceGeneralLedgerInputType(OriginEntry entry) {
        return cachingDao.getReferenceGeneralLedgerInputType(entry);
    }

    /**
     * Retrieve financial object for given origin entry
     * 
     * @param entry the origin entry to retrieve the financial object of
     * @return the related financial object record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getFinancialObject(org.kuali.module.gl.bo.OriginEntry)
     */
    public ObjectCode getFinancialObject(OriginEntry entry) {
        return cachingDao.getFinancialObject(entry);
    }

    /**
     * Get financial sub object for given origin entryable
     * 
     * @param entry the origin entry to retrieve the financial sub object of
     * @return the related financial sub object record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getFinancialSubObject(org.kuali.module.gl.bo.OriginEntry)
     */
    public SubObjectCode getFinancialSubObject(OriginEntry entry) {
        return cachingDao.getFinancialSubObject(entry);
    }

    /**
     * Get object type for given origin entry
     * 
     * @param entry the origin entry to retrieve the object type of
     * @return the related object type record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getObjectType(org.kuali.module.gl.bo.OriginEntry)
     */
    public ObjectType getObjectType(OriginEntry entry) {
        return cachingDao.getObjectType(entry);
    }

    /**
     * Retrieve option for given origin entry
     * 
     * @param entry the origin entry to retrieve the related options record of
     * @return the related Options record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getOption(org.kuali.module.gl.bo.OriginEntry)
     */
    public SystemOptions getSystemOptions(OriginEntry entry) {
        return cachingDao.getSystemOptions(entry);
    }

    public SystemOptions getSystemOptions(Integer fiscalYear) {
        return cachingDao.getSystemOptions(fiscalYear);
    }

    //TODO: should fail the modularization test -- will be moved to Labor
//    public LaborObject getLaborObject(OriginEntry entry) {
//        return cachingDao.getLaborObject(entry);
//    }

    /**
     * Retrieves the origination code for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the origin code of
     * @return the related OriginationCode record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getOriginationCode(org.kuali.module.gl.bo.OriginEntry)
     */
    public OriginationCode getOriginationCode(OriginEntry entry) {
        return cachingDao.getOriginationCode(entry);
    }

    public OriginationCode getOriginationCode(String financialSystemOriginationCode) {
        return cachingDao.getOriginationCode(financialSystemOriginationCode);
    }

    /**
     * Retrieves the project code for the given origin entryable
     * 
     * @param entry the origin entry to retrieve the project code of
     * @return the related ProjectCode record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getProjectCode(org.kuali.module.gl.bo.OriginEntry)
     */
    public ProjectCode getProjectCode(OriginEntry entry) {
        return cachingDao.getProjectCode(entry);
    }

    /**
     * Get sub account for given origin entry
     * 
     * @param entry the origin entry to retrieve the sub account of
     * @return the related SubAccount record, or null if not found
     * @see org.kuali.module.gl.service.OriginEntryLookupService#getSubAccount(org.kuali.module.gl.bo.OriginEntry)
     */
    public SubAccount getSubAccount(OriginEntry entry) {
        return cachingDao.getSubAccount(entry);
    }

    public SubFundGroup getSubFundGroup(String subFundGroupCode) {
        return cachingDao.getSubFundGroup(subFundGroupCode);
    }

    public Account getAccount(String chartCode, String accountNumber) {
        return cachingDao.getAccount(chartCode, accountNumber);
    }

    public UniversityDate getUniversityDate(Date date) {
        return cachingDao.getUniversityDate(date);
    }

    public OffsetDefinition getOffsetDefinition(Integer universityFiscalYear, String chartOfAccountsCode, String financialDocumentTypeCode, String financialBalanceTypeCode) {
        return cachingDao.getOffsetDefinition(universityFiscalYear, chartOfAccountsCode, financialDocumentTypeCode, financialBalanceTypeCode);
    }

    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        return cachingDao.getObjectCode(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    public Organization getOrg(String chartOfAccountsCode, String organizationCode) {
        return cachingDao.getOrg(chartOfAccountsCode, organizationCode);
    }

    /**
     * This method takes in an origin entry and returns the primary key map for the related class, based on values of that origin
     * entry.
     * 
     * @param entry the entry to perform the lookup on
     * @param referenceClassToRetrieve the class of a related object
     * @param fieldNameOverrides if the name of a field in the entry is not the name of the field in a related class, this map can
     *        override the entry's field names for the sake of the lookup
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
     * @param fieldNameOverrides if the name of a field in the entry is not the name of the field in a related class, this map can
     *        override the entry's field names for the sake of the lookup
     * @return the related object or null if not found in the cache or persistence store
     */
    private <T> T lookupReference(OriginEntry entry, Class<T> type, String fieldNameOverrides) {
        Map<String, Object> pk = getKeyMapFromEntry(entry, type, convertFieldsToMap(fieldNameOverrides));
        return (T) localLookupService.get().get(type, pk);
    }

    /**
     * Converts the field name overrides string and turns it into objects
     * 
     * @param fieldNameOverrides if the name of a field in the entry is not the name of the field in a related class, this map can
     *        override the entry's field names for the sake of the lookup
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

    public CachingDao getCachingDao() {
        return cachingDao;
    }

    public void setCachingDao(CachingDao cachingDao) {
        this.cachingDao = cachingDao;
    }

}
