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
package org.kuali.kfs.sys.batch.dataaccess.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.core.proxy.ProxyHelper;
import org.apache.ojb.broker.query.Criteria;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Default implementation of fiscal year maker process for an entity. This implementation can be used for a table in the fiscal year
 * maker process by defining a spring bean and setting the businessObjectClass property.
 */
public class FiscalYearMakerImpl extends PlatformAwareDaoBaseOjb implements FiscalYearMaker {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearMakerImpl.class);

    protected static final Long ONE = new Long(1);

    protected PersistenceStructureService persistenceStructureService;
    protected BusinessObjectService businessObjectService;

    protected Class<? extends FiscalYearBasedBusinessObject> businessObjectClass;
    protected Set<Class<? extends FiscalYearBasedBusinessObject>> parentClasses;

    protected boolean fiscalYearOneBehind;
    protected boolean fiscalYearOneAhead;
    protected boolean twoYearCopy;
    protected boolean carryForwardInactive;
    protected boolean allowOverrideTargetYear;

    protected Boolean hasExtension = null;
    protected List<String> primaryKeyPropertyNames = null;
    protected List<String> propertyNames = null;
    @SuppressWarnings("rawtypes")
    protected Map<String, Class> referenceObjects = null;
    @SuppressWarnings("rawtypes")
    protected Map<String, Class> collectionObjects = null;
    protected Map<String,Map<String,String>> referenceForeignKeys = new HashMap<String, Map<String,String>>();

    /**
     * Constructs a FiscalYearMakerImpl.java.
     */
    public FiscalYearMakerImpl() {
        fiscalYearOneBehind = false;
        fiscalYearOneAhead = false;
        twoYearCopy = false;
        carryForwardInactive = false;
        allowOverrideTargetYear = true;
        parentClasses = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>();
    }

    protected boolean hasExtension() {
        if ( hasExtension == null ) {
            hasExtension = persistenceStructureService.hasReference(businessObjectClass, KFSPropertyConstants.EXTENSION);
        }
        return hasExtension.booleanValue();
    }

    @Override
    public List<String> getPrimaryKeyPropertyNames() {
        if ( primaryKeyPropertyNames == null ) {
            primaryKeyPropertyNames = persistenceStructureService.listPrimaryKeyFieldNames(businessObjectClass);
        }
        return primaryKeyPropertyNames;
    }

    @Override
    public List<String> getPropertyNames() {
        if ( propertyNames == null ) {
            propertyNames = persistenceStructureService.listFieldNames(businessObjectClass);
        }
        return propertyNames;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<String,Class> getReferenceObjectProperties() {
        if ( referenceObjects == null ) {
            referenceObjects = persistenceStructureService.listReferenceObjectFields(businessObjectClass);
        }
        return referenceObjects;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Map<String,Class> getCollectionProperties() {
        if ( collectionObjects == null ) {
            collectionObjects = persistenceStructureService.listCollectionObjectTypes(businessObjectClass);
        }
        return collectionObjects;
    }

    @Override
    public Map<String,String> getForeignKeyMappings( String referenceName ) {
        if ( !referenceForeignKeys.containsKey(referenceName) ) {
            referenceForeignKeys.put(referenceName, persistenceStructureService.getForeignKeysForReference(businessObjectClass, referenceName) );
        }
        return referenceForeignKeys.get(referenceName);
    }

    /**
     * Sets fiscal year field up one, resets version number and assigns a new Guid for the object id
     *
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#changeForNewYear(java.lang.Integer,
     *      org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public void changeForNewYear(Integer baseFiscalYear, FiscalYearBasedBusinessObject currentRecord) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("starting changeForNewYear() for bo class " + businessObjectClass.getName());
        }

        try {
            // increment fiscal year by 1
            Integer newFiscalYear = currentRecord.getUniversityFiscalYear() + 1;

            // update extension, must be done before updating main record so we can retrieve the extension record by reference
            updateExtensionRecord(newFiscalYear, currentRecord);

            // update main record fields
            currentRecord.setUniversityFiscalYear(newFiscalYear);

            currentRecord.setVersionNumber(ONE);
            currentRecord.setObjectId(java.util.UUID.randomUUID().toString());
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
    protected void updateExtensionRecord(Integer newFiscalYear, PersistableBusinessObject currentRecord) throws Exception {
        // check if reference is mapped up
        if ( !hasExtension() ) {
            return;
        }

        // try to retrieve extension record
        currentRecord.refreshReferenceObject(KFSPropertyConstants.EXTENSION);
        PersistableBusinessObject extension = currentRecord.getExtension();

        // if found then update fields
        if (ObjectUtils.isNotNull(extension)) {
            extension = (PersistableBusinessObject)ProxyHelper.getRealObject(extension);
            extension.setVersionNumber(ONE);
            extension.setObjectId(java.util.UUID.randomUUID().toString());

            // since this could be a new object (no extension object present on the source record)
            // we need to set the keys
            // But...we only need to do this if this was a truly new object, which we can tell by checking
            // the fiscal year field
            if ( ((FiscalYearBasedBusinessObject)extension).getUniversityFiscalYear() == null ) {
                for ( String pkField : getPrimaryKeyPropertyNames() ) {
                    PropertyUtils.setSimpleProperty(extension, pkField, PropertyUtils.getSimpleProperty(currentRecord, pkField));
                }
            }
            ((FiscalYearBasedBusinessObject)extension).setUniversityFiscalYear(newFiscalYear);
        }
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#createSelectionCriteria(java.lang.Integer)
     */
    @Override
    public Criteria createNextYearSelectionCriteria(Integer baseFiscalYear) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("starting createNextYearSelectionCriteria() for bo class " + businessObjectClass.getName());
        }

        Criteria criteria = new Criteria();
        addYearCriteria(criteria, baseFiscalYear + 1, twoYearCopy);

        return criteria;
    }

    /**
     * Selects records for the given base year or base year minus one if this is a lagging copy. If this is a two year copy base
     * year plus one records will be selected as well. In addition will only select active records if the business object class
     * implements the MutableInactivatable interface and has the active property.
     *
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#createSelectionCriteria(java.lang.Integer)
     */
    @Override
    public Criteria createSelectionCriteria(Integer baseFiscalYear) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("starting createSelectionCriteria() for bo class " + businessObjectClass.getName());
        }

        Criteria criteria = new Criteria();
        addYearCriteria(criteria, baseFiscalYear, false);

        // add active criteria if the business object class supports the inactivateable interface
        List<String> fields = getPropertyNames();
        if (MutableInactivatable.class.isAssignableFrom(businessObjectClass) && fields.contains(KFSPropertyConstants.ACTIVE) && !carryForwardInactive) {
            criteria.addEqualTo(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);
        }

        return criteria;
    }

    /**
     * Selects records to delete for base year + 1 (or base year for lagging, and base year + 2 for two year)
     *
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#createDeleteCriteria(java.lang.Integer)
     */
    @Override
    public Criteria createDeleteCriteria(Integer baseFiscalYear) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("starting createDeleteCriteria() for bo class " + businessObjectClass.getName());
        }

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
     * Default implementation does nothing
     *
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#performCustomProcessing(java.lang.Integer)
     */
    @Override
    public void performCustomProcessing(Integer baseFiscalYear, boolean firstCopyYear) {

    }

    /**
     * Default to doing both normal FYM process and custom
     *
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#doCustomProcessingOnly()
     */
    @Override
    public boolean doCustomProcessingOnly() {
        return false;
    }

    /**
     * @see org.kuali.kfs.coa.dataaccess.FiscalYearMaker#getBusinessObjectClass()
     */
    @Override
    public Class<? extends FiscalYearBasedBusinessObject> getBusinessObjectClass() {
        return businessObjectClass;
    }

    /**
     * <code>Options</code> is the parent for univFiscalYear which all our copy objects should have. Added to list here by default.
     *
     * @see org.kuali.kfs.coa.batch.dataaccess.FiscalYearMakerHelper#getParentClasses()
     * @see org.kuali.kfs.sys.businessobject.Options
     */
    @Override
    public Set<Class<? extends FiscalYearBasedBusinessObject>> getParentClasses() {
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
    public void setBusinessObjectClass(Class<? extends FiscalYearBasedBusinessObject> businessObjectClass) {
        this.businessObjectClass = businessObjectClass;
    }

    /**
     * Sets the parentClasses attribute value.
     *
     * @param parentClasses The parentClasses to set.
     */
    public void setParentClasses(Set<Class<? extends FiscalYearBasedBusinessObject>> parentClasses) {
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
    @Override
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
    @Override
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

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
