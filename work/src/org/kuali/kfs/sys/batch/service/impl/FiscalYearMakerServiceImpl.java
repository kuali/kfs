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
package org.kuali.kfs.sys.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.FinancialSystemModuleConfiguration;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.FiscalYearMakerStep;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMaker;
import org.kuali.kfs.sys.batch.dataaccess.FiscalYearMakersDao;
import org.kuali.kfs.sys.batch.service.FiscalYearMakerService;
import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.kfs.coa.batch.service.FiscalYearMakerService
 */
@Transactional
public class FiscalYearMakerServiceImpl implements FiscalYearMakerService {
    private static final Logger LOG = org.apache.log4j.Logger.getLogger(FiscalYearMakerServiceImpl.class);

    protected FiscalYearMakersDao fiscalYearMakersDao;
    protected ParameterService parameterService;
    protected KualiModuleService kualiModuleService;

    protected List<FiscalYearMaker> fiscalYearMakers;

    /**
     * @see org.kuali.kfs.coa.batch.service.FiscalYearMakerService#runProcess()
     */
    public void runProcess() {
        String parmBaseYear = parameterService.getParameterValueAsString(FiscalYearMakerStep.class, KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_SOURCE_FISCAL_YEAR);
        if (StringUtils.isBlank(parmBaseYear)) {
            throw new RuntimeException("Required fiscal year parameter " + KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_SOURCE_FISCAL_YEAR + " has not been set.");
        }

        Integer baseYear = Integer.parseInt(parmBaseYear);
        boolean replaceMode = parameterService.getParameterValueAsBoolean(FiscalYearMakerStep.class, KFSConstants.ChartApcParms.FISCAL_YEAR_MAKER_REPLACE_MODE);

        if (fiscalYearMakers == null || fiscalYearMakers.isEmpty()) {
            this.initialize();
        }

        validateFiscalYearMakerConfiguration();

        // get correct order to do copy
        List<FiscalYearMaker> copyList = getFiscalYearMakerHelpersInCopyOrder();

        // if configured to replace existing records first clear out any records in target year
        if (replaceMode) {
            List<FiscalYearMaker> deleteList = getFiscalYearMakerHelpersInDeleteOrder(copyList);
            for (FiscalYearMaker fiscalYearMakerHelper : deleteList) {
                if (fiscalYearMakerHelper.isAllowOverrideTargetYear()) {
                    fiscalYearMakersDao.deleteNewYearRows(baseYear, fiscalYearMakerHelper);
                }
            }
        }

        // Map to hold parent primary key values written to use for child RI checks
        Map<Class<? extends FiscalYearBasedBusinessObject>, Set<String>> parentKeysWritten = new HashMap<Class<? extends FiscalYearBasedBusinessObject>, Set<String>>();

        // do copy process on each setup business object
        for (FiscalYearMaker fiscalYearMaker : copyList) {
            try {
                boolean isParent = isParentClass(fiscalYearMaker.getBusinessObjectClass());
                if (!fiscalYearMaker.doCustomProcessingOnly()) {
                    Collection<String> copyErrors = fiscalYearMakersDao.createNewYearRows(baseYear, fiscalYearMaker, replaceMode, parentKeysWritten, isParent);
                    writeCopyFailureMessages(copyErrors);
                }
    
                fiscalYearMaker.performCustomProcessing(baseYear, true);
    
                // if copy two years call copy procedure again to copy records from base year + 1 to base year + 2
                if (fiscalYearMaker.isTwoYearCopy()) {
                    if (!fiscalYearMaker.doCustomProcessingOnly()) {
                        Collection<String> copyErrors = fiscalYearMakersDao.createNewYearRows(baseYear + 1, fiscalYearMaker, replaceMode, parentKeysWritten, isParent);
                        writeCopyFailureMessages(copyErrors);
                    }
    
                    fiscalYearMaker.performCustomProcessing(baseYear + 1, false);
                }
            } catch ( Exception ex ) {
                throw new RuntimeException( "Internal exception while processing fiscal year for " + fiscalYearMaker.getBusinessObjectClass(), ex );
            }
        }
    }

    /**
     * Returns List of <code>FiscalYearMaker</code> objects in the order they should be copied. Ordered by Parent classes first then
     * children. This is necessary to ensure referential integrity is satisfied when the new record is inserted.
     * 
     * @return List<FiscalYearMaker> in copy order
     */
    protected List<FiscalYearMaker> getFiscalYearMakerHelpersInCopyOrder() {
        List<Class<? extends FiscalYearBasedBusinessObject>> classCopyOrder = new ArrayList<Class<? extends FiscalYearBasedBusinessObject>>();

        // build map of parents and their children
        Map<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>> parentChildren = getParentChildrenMap();

        // figure out correct order among parents by picking off levels of hierarchy
        while (!parentChildren.isEmpty()) {
            Set<Class<? extends FiscalYearBasedBusinessObject>> parents = parentChildren.keySet();
            Set<Class<? extends FiscalYearBasedBusinessObject>> children = getChildren(parentChildren);

            Set<Class<? extends FiscalYearBasedBusinessObject>> rootParents = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>(CollectionUtils.subtract(parents, children));

            // if there are no root parents, then we must have a circular reference
            if (rootParents.isEmpty()) {
                findCircularReferenceAndThrowException(parentChildren);
            }

            for (Class<? extends FiscalYearBasedBusinessObject> rootParent : rootParents) {
                classCopyOrder.add(rootParent);
                parentChildren.remove(rootParent);
            }
        }

        // now add remaining objects (those that are not parents)
        for (FiscalYearMaker fiscalYearMakerHelper : this.fiscalYearMakers) {
            if (!classCopyOrder.contains(fiscalYearMakerHelper.getBusinessObjectClass())) {
                classCopyOrder.add(fiscalYearMakerHelper.getBusinessObjectClass());
            }
        }

        // finally build list of FiscalYearMaker objects by the correct class order
        List<FiscalYearMaker> fiscalYearMakerHelpersCopyOrder = new ArrayList<FiscalYearMaker>();

        Map<Class<? extends FiscalYearBasedBusinessObject>, FiscalYearMaker> copyMap = getFiscalYearMakerMap();
        for (Class<? extends FiscalYearBasedBusinessObject> copyClass : classCopyOrder) {
            fiscalYearMakerHelpersCopyOrder.add(copyMap.get(copyClass));
        }

        return fiscalYearMakerHelpersCopyOrder;
    }

    /**
     * Returns List of <code>FiscalYearMaker</code> objects in the order they should be deleted. Ordered by Child classes first then
     * Parents. This is necessary to ensure referential integrity is satisfied when the new record is deleted.
     * 
     * @param fiscalYearMakerHelpersCopyOrder list of fiscal year makers in copy order
     * @return List<FiscalYearMaker> in delete order
     */
    protected List<FiscalYearMaker> getFiscalYearMakerHelpersInDeleteOrder(List<FiscalYearMaker> fiscalYearMakerHelpersCopyOrder) {
        List<FiscalYearMaker> fiscalYearMakerHelpersDeleteOrder = new ArrayList<FiscalYearMaker>();
        for (int i = fiscalYearMakerHelpersCopyOrder.size() - 1; i >= 0; i--) {
            fiscalYearMakerHelpersDeleteOrder.add(fiscalYearMakerHelpersCopyOrder.get(i));
        }

        return fiscalYearMakerHelpersDeleteOrder;
    }

    /**
     * Finds circular references (class which is a child to itself) and throws exception indicating the invalid parent-child
     * configuration
     * 
     * @param parents Set of parent classes to check
     * @param parentChildren Map with parent class as the key and its children classes as value
     */
    protected void findCircularReferenceAndThrowException(Map<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>> parentChildren) {
        Set<Class<? extends FiscalYearBasedBusinessObject>> classesWithCircularReference = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>();

        // resolve children for each parent and verify the parent does not appear as a child to itself
        for (Class<? extends FiscalYearBasedBusinessObject> parent : parentChildren.keySet()) {
            boolean circularReferenceFound = checkChildrenForParentReference(parent, parentChildren.get(parent), parentChildren, new HashSet<Class<? extends FiscalYearBasedBusinessObject>>());
            if (circularReferenceFound) {
                classesWithCircularReference.add(parent);
            }
        }

        if (!classesWithCircularReference.isEmpty()) {
            String error = "Circular reference found for class(s): " + StringUtils.join(classesWithCircularReference, ", ");
            LOG.error(error);
            throw new RuntimeException(error);
        }
    }

    /**
     * Recursively checks all children of children who are parents for reference to the given parent class
     * 
     * @param parent Class of parent to check for
     * @param children Set of children classes to check
     * @param parentChildren Map with parent class as the key and its children classes as value
     * @param checkedParents Set of parent classes we have already checked (to prevent endless recursiveness)
     * @return true if the parent class was found in one of the children list (meaning we have a circular reference), false
     *         otherwise
     */
    protected boolean checkChildrenForParentReference(Class<? extends FiscalYearBasedBusinessObject> parent, Set<Class<? extends FiscalYearBasedBusinessObject>> children, Map<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>> parentChildren, Set<Class<? extends FiscalYearBasedBusinessObject>> checkedParents) {
        // if parent is in child list then we have a circular reference
        if (children.contains(parent)) {
            return true;
        }

        // iterate through children and check if the child is also a parent, if so then need to check its children
        for (Class<? extends FiscalYearBasedBusinessObject> child : children) {
            if (parentChildren.containsKey(child) && !checkedParents.contains(child)) {
                checkedParents.add(child);
                Set<Class<? extends FiscalYearBasedBusinessObject>> childChildren = parentChildren.get(child);

                boolean foundParent = checkChildrenForParentReference(parent, childChildren, parentChildren, checkedParents);
                if (foundParent) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Helper method to build a Map with Parent classes as the key and their Set of child classes as the value
     * 
     * @return Map<Class, Set<Class>> of parent to children classes
     */
    protected Map<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>> getParentChildrenMap() {
        Map<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>> parentChildren = new HashMap<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>>();

        for (FiscalYearMaker fiscalYearMakerHelper : fiscalYearMakers) {
            for (Class<? extends FiscalYearBasedBusinessObject> parentClass : fiscalYearMakerHelper.getParentClasses()) {
                Set<Class<? extends FiscalYearBasedBusinessObject>> children = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>();
                if (parentChildren.containsKey(parentClass)) {
                    children = parentChildren.get(parentClass);
                }
                children.add(fiscalYearMakerHelper.getBusinessObjectClass());

                parentChildren.put(parentClass, children);
            }
        }

        return parentChildren;
    }

    /**
     * Checks if the given class is a parent (to at least one other class)
     * 
     * @param businessObjectClass class to check
     * @return true if class is a parent, false otherwise
     */
    protected boolean isParentClass(Class<? extends FiscalYearBasedBusinessObject> businessObjectClass) {
        for (FiscalYearMaker fiscalYearMakerHelper : fiscalYearMakers) {
            for (Class<? extends FiscalYearBasedBusinessObject> parentClass : fiscalYearMakerHelper.getParentClasses()) {
                if (businessObjectClass.isAssignableFrom(parentClass)) {
                    return true;
                }
            }
        }

        return false;
    }


    /**
     * Gets all classes that are child of another class in the given Map
     * 
     * @param parentChildren Map with parent class as the key and its children classes as value
     * @return Set of classes that are a child of another class
     */
    protected Set<Class<? extends FiscalYearBasedBusinessObject>> getChildren(Map<Class<? extends FiscalYearBasedBusinessObject>, Set<Class<? extends FiscalYearBasedBusinessObject>>> parentChildren) {
        Set<Class<? extends FiscalYearBasedBusinessObject>> children = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>();

        for (Class<? extends FiscalYearBasedBusinessObject> parentClass : parentChildren.keySet()) {
            children.addAll(parentChildren.get(parentClass));
        }

        return children;
    }

    /**
     * Helper method to build a Map with the copy class as the key and its corresponding <code>FiscalYearMaker</code> as the Map
     * value
     * 
     * @return Map<Class, FiscalYearMaker> of copy classes to FiscalYearMaker objects
     */
    protected Map<Class<? extends FiscalYearBasedBusinessObject>, FiscalYearMaker> getFiscalYearMakerMap() {
        Map<Class<? extends FiscalYearBasedBusinessObject>, FiscalYearMaker> fiscalYearMakerMap = new HashMap<Class<? extends FiscalYearBasedBusinessObject>, FiscalYearMaker>();

        for (FiscalYearMaker fiscalYearMakerHelper : fiscalYearMakers) {
            fiscalYearMakerMap.put(fiscalYearMakerHelper.getBusinessObjectClass(), fiscalYearMakerHelper);
        }

        return fiscalYearMakerMap;
    }

    /**
     * Validates each configured fiscal year maker implementation
     */
    protected void validateFiscalYearMakerConfiguration() {
        Set<Class<? extends FiscalYearBasedBusinessObject>> businessObjectClasses = new HashSet<Class<? extends FiscalYearBasedBusinessObject>>();

        for (FiscalYearMaker fiscalYearMaker : fiscalYearMakers) {
            Class<? extends FiscalYearBasedBusinessObject> businessObjectClass = fiscalYearMaker.getBusinessObjectClass();
            if (businessObjectClass == null) {
                String error = "Business object class is null for fiscal year maker";
                LOG.error(error);
                throw new RuntimeException(error);
            }

            if (!FiscalYearBasedBusinessObject.class.isAssignableFrom(businessObjectClass)) {
                String error = String.format("Business object class %s does not implement %s", businessObjectClass.getName(), FiscalYearBasedBusinessObject.class.getName());
                LOG.error(error);
                throw new RuntimeException(error);
            }

            if (businessObjectClasses.contains(businessObjectClass)) {
                String error = String.format("Business object class %s has two fiscal year maker implementations defined", businessObjectClass.getName());
                LOG.error(error);
                throw new RuntimeException(error);
            }

            businessObjectClasses.add(businessObjectClass);
        }

        // validate parents are in copy list
        Set<Class<? extends PersistableBusinessObject>> parentsNotInCopyList = new HashSet<Class<? extends PersistableBusinessObject>>();
        for (FiscalYearMaker fiscalYearMaker : fiscalYearMakers) {
            parentsNotInCopyList.addAll(CollectionUtils.subtract(fiscalYearMaker.getParentClasses(), businessObjectClasses));
        }

        if (!parentsNotInCopyList.isEmpty()) {
            String error = "Parent classes not in copy list: " + StringUtils.join(parentsNotInCopyList, ",");
            LOG.error(error);
            throw new RuntimeException(error);
        }
    }

    /**
     * Write outs errors encountered while creating new records for an object to LOG.
     * 
     * @param copyErrors Collection of error messages to write
     */
    protected void writeCopyFailureMessages(Collection<String> copyErrors) {
        if (!copyErrors.isEmpty()) {
            LOG.warn("\n");
            for (String copyError : copyErrors) {
                LOG.warn(String.format("\n%s", copyError));
            }
            LOG.warn("\n");
        }
    }

    /**
     * Populates the fiscal year maker list from the installed modules
     */
    public void initialize() {
        fiscalYearMakers = new ArrayList<FiscalYearMaker>();
        for (ModuleService moduleService : kualiModuleService.getInstalledModuleServices()) {
            if (moduleService.getModuleConfiguration() instanceof FinancialSystemModuleConfiguration) {
                fiscalYearMakers.addAll(((FinancialSystemModuleConfiguration) moduleService.getModuleConfiguration()).getFiscalYearMakers());
            }
        }
    }

    /**
     * Sets the fiscalYearMakers attribute value.
     * 
     * @param fiscalYearMakers The fiscalYearMakers to set.
     */
    protected void setFiscalYearMakers(List<FiscalYearMaker> fiscalYearMakers) {
        this.fiscalYearMakers = fiscalYearMakers;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the fiscalYearMakersDao attribute value.
     * 
     * @param fiscalYearMakersDao The fiscalYearMakersDao to set.
     */
    public void setFiscalYearMakersDao(FiscalYearMakersDao fiscalYearMakersDao) {
        this.fiscalYearMakersDao = fiscalYearMakersDao;
    }

    /**
     * Sets the kualiModuleService attribute value.
     * 
     * @param kualiModuleService The kualiModuleService to set.
     */
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    /**
     * Gets the fiscalYearMakersDao attribute.
     * 
     * @return Returns the fiscalYearMakersDao.
     */
    protected FiscalYearMakersDao getFiscalYearMakersDao() {
        return fiscalYearMakersDao;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Gets the kualiModuleService attribute.
     * 
     * @return Returns the kualiModuleService.
     */
    protected KualiModuleService getKualiModuleService() {
        return kualiModuleService;
    }

    /**
     * Gets the fiscalYearMakers attribute.
     * 
     * @return Returns the fiscalYearMakers.
     */
    protected List<FiscalYearMaker> getFiscalYearMakers() {
        return fiscalYearMakers;
    }

}
