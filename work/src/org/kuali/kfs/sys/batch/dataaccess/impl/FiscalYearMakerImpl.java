/*
 * Copyright 2008 The Kuali Foundation.
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.dao.impl.PlatformAwareDaoBaseOjb;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.Guid;

/**
 * Default implementation of fiscal year maker process for an entity. This implementation can be used for a table in the fiscal year
 * maker process by defining a spring bean and setting the businessObjectClass property.
 */
public class FiscalYearMakerImpl extends PlatformAwareDaoBaseOjb implements FiscalYearMaker {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearMakerImpl.class);

    private PersistenceStructureService persistenceStructureService;

    private Class<? extends PersistableBusinessObject> businessObjectClass;
    private Set<Class<? extends PersistableBusinessObject>> parentClasses;

    private boolean fiscalYearOneBehind;
    private boolean fiscalYearOneAhead;
    private boolean twoYearCopy;
    private boolean carryForwardInactive;
    private boolean allowOverrideTargetYear;

    /**
     * Constructs a FiscalYearMakerImpl.java.
     */
    public FiscalYearMakerImpl() {
        fiscalYearOneBehind = false;
        fiscalYearOneAhead = false;
        twoYearCopy = false;
        carryForwardInactive = false;
        allowOverrideTargetYear = true;
        parentClasses = new HashSet<Class<? extends PersistableBusinessObject>>();
    }

    /**
     * Sets fiscal year field up one, resets version number and assigns a new Guid for the object id
     * 
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#changeForNewYear(java.lang.Integer,
     *      org.kuali.rice.kns.bo.PersistableBusinessObject)
     */
    public void changeForNewYear(Integer baseFiscalYear, PersistableBusinessObject currentRecord) {
        LOG.debug("starting changeForNewYear() for bo class " + businessObjectClass.getName());

        try {
            // increment fiscal year by 1
            Integer fiscalYear = (Integer) PropertyUtils.getProperty(currentRecord, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            Integer newFiscalYear = fiscalYear + 1;

            // update extension, must be done before updating main record so we can retrieve the extension record by reference
            updateExtensionRecord(newFiscalYear, currentRecord);

            // update main record fields
            PropertyUtils.setSimpleProperty(currentRecord, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, newFiscalYear);

            currentRecord.setVersionNumber(new Long(1));
            currentRecord.setObjectId(new Guid().toString());
        }
        catch (Exception e) {
            String msg = String.format("Failed to set properties for class %s due to %s", businessObjectClass.getName(), e.getMessage());
            LOG.error(msg);
            throw new RuntimeException(msg, e);
        }
    }

    /**
     * Determines if an extension record is mapped up and exists for the current record. If so then updates the version number,
     * object id, and clears the primary keys so they will be relinked when storing the main record
     * 
     * @param newFiscalYear fiscal year to set
     * @param currentRecord main record with possible extension reference
     */
    protected void updateExtensionRecord(Integer newFiscalYear, PersistableBusinessObject currentRecord) {
        // check if reference is mapped up
        if (!persistenceStructureService.hasReference(businessObjectClass, KFSPropertyConstants.EXTENSION)) {
            return;
        }

        // try to retrieve extension record
        currentRecord.refreshReferenceObject(KFSPropertyConstants.EXTENSION);
        PersistableBusinessObject extension = currentRecord.getExtension();

        // if found then update fields
        if (extension != null) {
            extension.setVersionNumber(new Long(1));
            extension.setObjectId(new Guid().toString());

            // clear pk fields so they will be relinked
            persistenceStructureService.clearPrimaryKeyFields(extension);
        }
    }

    /**
     * Selects records for the given base year or base year minus one if this is a lagging copy. If this is a two year copy base
     * year plus one records will be selected as well. In addition will only select active records if the business object class
     * implements the Inactivateable interface and has the active property.
     * 
     * @see org.kuali.rice.kns.bo.Inactivateable
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#createSelectionCriteria(java.lang.Integer)
     */
    public Criteria createSelectionCriteria(Integer baseFiscalYear) {
        LOG.debug("starting createSelectionCriteria() for bo class " + businessObjectClass.getName());

        Criteria criteria = new Criteria();
        addYearCriteria(criteria, baseFiscalYear, false);

        // add active criteria if the business object class supports the inactivateable interface
        List<String> fields = persistenceStructureService.listFieldNames(businessObjectClass);
        if (Inactivateable.class.isAssignableFrom(businessObjectClass) && fields.contains(KFSPropertyConstants.ACTIVE) && !carryForwardInactive) {
            criteria.addEqualTo(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);
        }

        return criteria;
    }

    /**
     * Selects records to delete for base year + 1 (or base year for lagging, and base year + 2 for two year)
     * 
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#createDeleteCriteria(java.lang.Integer)
     */
    public Criteria createDeleteCriteria(Integer baseFiscalYear) {
        LOG.debug("starting createDeleteCriteria() for bo class " + businessObjectClass.getName());

        Criteria criteria = new Criteria();
        addYearCriteria(criteria, baseFiscalYear + 1, twoYearCopy);

        return criteria;
    }

    /**
     * Adds fiscal year criteria based on the configuration (copy two years, lagging, or normal)
     * 
     * @param criteria OJB Criteria object
     * @param baseFiscalYear Fiscal year for critiera
     * @param createTwoYears indicates whether two years of fiscal year criteria should be added
     */
    protected void addYearCriteria(Criteria criteria, Integer baseFiscalYear, boolean createTwoYears) {
        verifyUniversityFiscalYearPropertyExists();

        if (fiscalYearOneBehind) {
            baseFiscalYear = baseFiscalYear - 1;
        }
        else if (fiscalYearOneAhead) {
            baseFiscalYear = baseFiscalYear + 1;
        }

        if (createTwoYears) {
            List<Integer> copyYears = new ArrayList<Integer>();
            copyYears.add(baseFiscalYear);
            copyYears.add(baseFiscalYear + 1);

            criteria.addIn(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, copyYears);
        }
        else {
            criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, baseFiscalYear);
        }
    }

    /**
     * Verifies the given business object class has the university fiscal year property necessary for setting default criteria
     * 
     * @see org.kuali.kfs.sys.KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR
     */
    private void verifyUniversityFiscalYearPropertyExists() {
        List<String> fields = persistenceStructureService.listFieldNames(businessObjectClass);
        if (fields == null || !fields.contains(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)) {
            String msg = String.format("No %s property in business object class %s", KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, businessObjectClass.getName());
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
    }

    /**
     * Default implementation does nothing
     * 
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#performCustomProcessing(java.lang.Integer)
     */
    public void performCustomProcessing(Integer baseFiscalYear, boolean firstCopyYear) {

    }

    /**
     * Default to doing both normal FYM process and custom
     * 
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#doCustomProcessingOnly()
     */
    public boolean doCustomProcessingOnly() {
        return false;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#getBusinessObjectClass()
     */
    public Class<? extends PersistableBusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * <code>Options</code> is the parent for univFiscalYear which all our copy objects should have. Added to list here by default.
     * 
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#getParentClasses()
     * @see org.kuali.kfs.sys.businessobject.Options
     */
    public Set<Class<? extends PersistableBusinessObject>> getParentClasses() {
        if (!businessObjectClass.equals(SystemOptions.class) && !parentClasses.contains(SystemOptions.class)) {
            parentClasses.add(SystemOptions.class);
        }

        return parentClasses;
    }

    /**
     * Sets the businessObjectClass attribute value.
     * 
     * @param businessObjectClass The businessObjectClass to set.
     */
    public void setBusinessObjectClass(Class<? extends PersistableBusinessObject> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    /**
     * Sets the parentClasses attribute value.
     * 
     * @param parentClasses The parentClasses to set.
     */
    public void setParentClasses(Set<Class<? extends PersistableBusinessObject>> parentClasses) {
        this.parentClasses = parentClasses;
    }

    /**
     * Gets the fiscalYearOneBehind attribute.
     * 
     * @return Returns the fiscalYearOneBehind.
     */
    public boolean isFiscalYearOneBehind() {
        return fiscalYearOneBehind;
    }

    /**
     * Sets the fiscalYearOneBehind attribute value.
     * 
     * @param fiscalYearOneBehind The fiscalYearOneBehind to set.
     */
    public void setFiscalYearOneBehind(boolean fiscalYearOneBehind) {
        this.fiscalYearOneBehind = fiscalYearOneBehind;
    }

    /**
     * Gets the fiscalYearOneAhead attribute.
     * 
     * @return Returns the fiscalYearOneAhead.
     */
    public boolean isFiscalYearOneAhead() {
        return fiscalYearOneAhead;
    }

    /**
     * Sets the fiscalYearOneAhead attribute value.
     * 
     * @param fiscalYearOneAhead The fiscalYearOneAhead to set.
     */
    public void setFiscalYearOneAhead(boolean fiscalYearOneAhead) {
        this.fiscalYearOneAhead = fiscalYearOneAhead;
    }

    /**
     * Gets the twoYearCopy attribute.
     * 
     * @return Returns the twoYearCopy.
     */
    public boolean isTwoYearCopy() {
        return twoYearCopy;
    }

    /**
     * Sets the twoYearCopy attribute value.
     * 
     * @param twoYearCopy The twoYearCopy to set.
     */
    public void setTwoYearCopy(boolean twoYearCopy) {
        this.twoYearCopy = twoYearCopy;
    }

    /**
     * Gets the carryForwardInactive attribute.
     * 
     * @return Returns the carryForwardInactive.
     */
    public boolean isCarryForwardInactive() {
        return carryForwardInactive;
    }

    /**
     * Sets the carryForwardInactive attribute value.
     * 
     * @param carryForwardInactive The carryForwardInactive to set.
     */
    public void setCarryForwardInactive(boolean carryForwardInactive) {
        this.carryForwardInactive = carryForwardInactive;
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
     * Gets the allowOverrideTargetYear attribute.
     * 
     * @return Returns the allowOverrideTargetYear.
     */
    public boolean isAllowOverrideTargetYear() {
        return allowOverrideTargetYear;
    }

    /**
     * Sets the allowOverrideTargetYear attribute value.
     * 
     * @param allowOverrideTargetYear The allowOverrideTargetYear to set.
     */
    public void setAllowOverrideTargetYear(boolean allowOverrideTargetYear) {
        this.allowOverrideTargetYear = allowOverrideTargetYear;
    }

}
