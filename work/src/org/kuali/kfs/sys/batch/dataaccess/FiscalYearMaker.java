/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.dataaccess;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;

/**
 * Defines methods that must be implemented for a DAO making an entity for a new fiscal year
 */
public interface FiscalYearMaker {

    /**
     * Does any necessary changes on the base record (for base fiscal year) for storing as a record of the new fiscal year. The
     * fiscal year field should be updated an minimum.
     * 
     * @param baseFiscalYear fiscal year of the base record
     * @param currentRecord business object of type (@see org.kuali.kfs.coa.dataaccess.FiscalYearMakerDao.getBusinessObjectClass())
     *        populated with the current year record data
     * @return business object of type (@see org.kuali.kfs.coa.dataaccess.FiscalYearMakerDao.getBusinessObjectClass()) populated
     *         with data for the new fiscal year record
     */
    public void changeForNewYear(Integer baseFiscalYear, FiscalYearBasedBusinessObject currentRecord);

    /**
     * Creates OJB Criteria object that will be used to query for records to copy
     * 
     * @param baseFiscalYear fiscal year of the base record
     * @return OJB criteria object
     * @see org.apache.ojb.broker.query.Criteria
     */
    public Criteria createSelectionCriteria(Integer baseFiscalYear);

    /**
     * Creates OJB Criteria object that will be used to delete records in the target year
     * 
     * @param baseFiscalYear fiscal year of the base record
     * @return OJB criteria object
     * @see org.apache.ojb.broker.query.Criteria
     */
    public Criteria createDeleteCriteria(Integer baseFiscalYear);

    /**
     * Hook to do custom new population for a business object
     * 
     * @param baseFiscalYear fiscal year of the base record
     * @param firstCopyYear boolean that indicates whether this is the first year being copied (useful for two year copies)
     */
    public void performCustomProcessing(Integer baseFiscalYear, boolean firstCopyYear);

    /**
     * Indicator for determining whether we should do normal FYM process and call custom hook or only custom
     * 
     * @return true if only custom processing should be done, false if both normal FYM process and custom should be performed
     */
    public boolean doCustomProcessingOnly();

    /**
     * Returns the class for the business object the fiscal year maker implementation operates on
     * 
     * @return business object class
     */
    public Class<? extends FiscalYearBasedBusinessObject> getBusinessObjectClass();

    /**
     * Returns Set of Class objects that are parents to this business object. Parents will be copied before this object to satisfy
     * referential integrity in the database
     * 
     * @return Set of Class objects that extend PersistableBusinessObject
     */
    public Set<Class<? extends FiscalYearBasedBusinessObject>> getParentClasses();

    /**
     * Indicates whether records should be created for two fiscal years out as opposed to just one
     * 
     * @return true if two years should be copied, false otherwise (only the default one)
     */
    public boolean isTwoYearCopy();

    /**
     * Indicates whether records of this type can be cleared for target year (if Override parameter is set to true). Clearing
     * records for some tables causes RI issues therefore they cannot be safely deleted once created
     * 
     * @return true if target year data can be cleared, false if not
     */
    public boolean isAllowOverrideTargetYear();

    Criteria createNextYearSelectionCriteria(Integer fiscalYear);

    List<String> getPrimaryKeyPropertyNames();
    List<String> getPropertyNames();
    @SuppressWarnings("rawtypes")
    Map<String,Class> getReferenceObjectProperties();
    @SuppressWarnings("rawtypes")
    Map<String,Class> getCollectionProperties();
    Map<String,String> getForeignKeyMappings( String referenceName );
}
