/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document.validation;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.module.ar.service.CostCategoryService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validates content of a <code>{@link AccountDelegate}</code> maintenance document upon triggering of a approve, save, or route
 * event.
 */
public class CostCategoryRule extends KfsMaintenanceDocumentRuleBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CostCategoryRule.class);

    protected CostCategory oldCategories;
    protected CostCategory newCategories;

    private volatile static CostCategoryService costCategoryService;

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.debug("Entering processCustomSaveDocumentBusinessRules()");
        setupConvenienceObjects(document);

        // check simple rules
        boolean success = checkSimpleRules();

        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.debug("Entering processCustomRouteDocumentBusinessRules()");

        setupConvenienceObjects(document);

        // check simple rules
        boolean success = checkSimpleRules();

        return success;
    }

    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldCategories = (CostCategory) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newCategories = (CostCategory) super.getNewBo();
    }

    /**
     * Checks that there are details on the cost control and that none of the details are used by other cost categories
     * @return true if validations passed, false otherwise
     */
    protected boolean checkSimpleRules() {
        boolean success = true;

        // check that there is something - at least one object code or level or consolidation
        if (CollectionUtils.isEmpty(newCategories.getObjectCodes()) && CollectionUtils.isEmpty(newCategories.getObjectLevels()) && CollectionUtils.isEmpty(newCategories.getObjectConsolidations())) {
            putGlobalError(ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_NO_DETAILS);
            success = false;
        } else {
            if (!CollectionUtils.isEmpty(newCategories.getObjectCodes())) {
                int count = 0;

                for (CostCategoryObjectCode objectCode : newCategories.getObjectCodes()) {
                    final boolean objectCodeExists = checkObjectCodeExists(objectCode);

                    if (objectCodeExists) {
                        final CostCategoryDetail conflictingDetail = getCostCategoryService().isCostCategoryObjectCodeUnique(objectCode);
                        if (!ObjectUtils.isNull(conflictingDetail)) {
                            final String conflictingDetailObjectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(conflictingDetail.getClass().getName()).getObjectLabel();
                            putFieldError(ArPropertyConstants.OBJECT_CODES+"["+count+"]."+KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_OBJECT_CODE_NOT_UNIQUE, new String[] {objectCode.getChartOfAccountsCode(), objectCode.getFinancialObjectCode(), conflictingDetailObjectLabel, conflictingDetail.getCategoryCode()});
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                    count += 1;
                }
            }
            if (!CollectionUtils.isEmpty(newCategories.getObjectLevels())) {
                int count = 0;
                for (CostCategoryObjectLevel objectLevel : newCategories.getObjectLevels()) {
                    final boolean objectLevelExists = checkObjectLevelExists(objectLevel);

                    if (objectLevelExists) {
                        final CostCategoryDetail conflictingDetail = getCostCategoryService().isCostCategoryObjectLevelUnique(objectLevel);
                        if (!ObjectUtils.isNull(conflictingDetail)) {
                            final String conflictingDetailObjectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(conflictingDetail.getClass().getName()).getObjectLabel();
                            putFieldError(ArPropertyConstants.OBJECT_CODES+"["+count+"]."+KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_OBJECT_CODE_NOT_UNIQUE, new String[] {objectLevel.getChartOfAccountsCode(), objectLevel.getFinancialObjectLevelCode(), conflictingDetailObjectLabel, conflictingDetail.getCategoryCode()});
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                    count += 1;
                }
            }
            if (!CollectionUtils.isEmpty(newCategories.getObjectConsolidations())) {
                int count = 0;
                for (CostCategoryObjectConsolidation objectConsolidation : newCategories.getObjectConsolidations()) {
                    final boolean objectConsolidationExists = checkObjectConsolidationExists(objectConsolidation);

                    if (objectConsolidationExists) {
                        final CostCategoryDetail conflictingDetail = getCostCategoryService().isCostCategoryObjectConsolidationUnique(objectConsolidation);
                        if (!ObjectUtils.isNull(conflictingDetail)) {
                            final String conflictingDetailObjectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(conflictingDetail.getClass().getName()).getObjectLabel();
                            putFieldError(ArPropertyConstants.OBJECT_CODES+"["+count+"]."+KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_OBJECT_CODE_NOT_UNIQUE, new String[] {objectConsolidation.getChartOfAccountsCode(), objectConsolidation.getFinConsolidationObjectCode(), conflictingDetailObjectLabel, conflictingDetail.getCategoryCode()});
                            success = false;
                        }
                    } else {
                        success = false;
                    }
                    count += 1;
                }
            }
        }

        return success;
    }

    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {
        boolean success = super.processCustomAddCollectionLineBusinessRules(document, collectionName, line);

        if (line instanceof CostCategoryObjectCode) {
            CostCategoryObjectCode newObjectCode = (CostCategoryObjectCode)line;
            success &= checkObjectCodeExists(newObjectCode);

            if (success) {
                final CostCategoryDetail conflictingDetail = getCostCategoryService().isCostCategoryObjectCodeUnique(newObjectCode);
                if (!ObjectUtils.isNull(conflictingDetail)) {
                    final String conflictingDetailObjectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(conflictingDetail.getClass().getName()).getObjectLabel();
                    GlobalVariables.getMessageMap().putErrorForSectionId(ArConstants.CostCategoryMaintenanceSections.EDIT_OBJECT_CODES, ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_OBJECT_CODE_NOT_UNIQUE, new String[] {newObjectCode.getChartOfAccountsCode(), newObjectCode.getFinancialObjectCode(), conflictingDetailObjectLabel, conflictingDetail.getCategoryCode()});
                    success = false;
                }
            }
        } else if (line instanceof CostCategoryObjectLevel) {
            CostCategoryObjectLevel newObjectLevel = (CostCategoryObjectLevel)line;
            success &= checkObjectLevelExists(newObjectLevel);

            if (success) {
                final CostCategoryDetail conflictingDetail = getCostCategoryService().isCostCategoryObjectLevelUnique(newObjectLevel);
                if (!ObjectUtils.isNull(conflictingDetail)) {
                    final String conflictingDetailObjectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(conflictingDetail.getClass().getName()).getObjectLabel();
                    GlobalVariables.getMessageMap().putErrorForSectionId(ArConstants.CostCategoryMaintenanceSections.EDIT_OBJECT_LEVELS, ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_OBJECT_CODE_NOT_UNIQUE, new String[] {newObjectLevel.getChartOfAccountsCode(), newObjectLevel.getFinancialObjectLevelCode(), conflictingDetailObjectLabel, conflictingDetail.getCategoryCode()});
                    success = false;
                }
            }
        } else if (line instanceof CostCategoryObjectConsolidation) {
            CostCategoryObjectConsolidation newObjectConsolidation = (CostCategoryObjectConsolidation)line;
            success &= checkObjectConsolidationExists(newObjectConsolidation);

            if (success) {
                final CostCategoryDetail conflictingDetail = getCostCategoryService().isCostCategoryObjectConsolidationUnique(newObjectConsolidation);
                if (!ObjectUtils.isNull(conflictingDetail)) {
                    final String conflictingDetailObjectLabel = getDataDictionaryService().getDataDictionary().getBusinessObjectEntryForConcreteClass(conflictingDetail.getClass().getName()).getObjectLabel();
                    GlobalVariables.getMessageMap().putErrorForSectionId(ArConstants.CostCategoryMaintenanceSections.EDIT_OBJECT_CONSOLIDATIONS, ArKeyConstants.ERROR_DOCUMENT_COST_CATEGORY_OBJECT_CODE_NOT_UNIQUE, new String[] {newObjectConsolidation.getChartOfAccountsCode(), newObjectConsolidation.getFinConsolidationObjectCode(), conflictingDetailObjectLabel, conflictingDetail.getCategoryCode()});
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * Determines if the object code on a cost category object code actually exists
     * @param objectCode the cost category object code to check
     * @return true if the object code exists, false (and an error message) otherwise
     */
    protected boolean checkObjectCodeExists(CostCategoryObjectCode objectCode) {
        boolean success = true;

        objectCode.refreshReferenceObject(KFSPropertyConstants.OBJECT_CODE_CURRENT);
        if (ObjectUtils.isNull(objectCode.getObjectCodeCurrent())) {
            final String label = getDataDictionaryService().getAttributeLabel(CostCategoryObjectCode.class, KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
            success = false;
        }

        return success;
    }

    /**
     * Determines if the object level on a cost category object level actually exists
     * @param objectLevel the cost category object level to check
     * @return true if the object level exists, false (and an error message) otherwise
     */
    protected boolean checkObjectLevelExists(CostCategoryObjectLevel objectLevel) {
        boolean success = true;

        objectLevel.refreshReferenceObject(ArPropertyConstants.OBJECT_LEVEL);
        if (ObjectUtils.isNull(objectLevel.getObjectLevel())) {
            final String label = getDataDictionaryService().getAttributeLabel(CostCategoryObjectLevel.class, KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
            success = false;
        }

        return success;
    }

    /**
     * Determines if the object code on a cost category object code actually exists
     * @param objectCode the cost category object code to check
     * @return true if the object code exists, false (and an error message) otherwise
     */
    protected boolean checkObjectConsolidationExists(CostCategoryObjectConsolidation objectConsolidation) {
        boolean success = true;

        objectConsolidation.refreshReferenceObject(ArPropertyConstants.OBJECT_CONSOLIDATION);
        if (ObjectUtils.isNull(objectConsolidation.getObjectConsolidation())) {
            final String label = getDataDictionaryService().getAttributeLabel(CostCategoryObjectConsolidation.class, KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE);
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.FIN_CONSOLIDATION_OBJECT_CODE, RiceKeyConstants.ERROR_EXISTENCE, label);
            success = false;
        }

        return success;
    }

    public static CostCategoryService getCostCategoryService() {
        if (costCategoryService == null) {
            costCategoryService = SpringContext.getBean(CostCategoryService.class);
        }
        return costCategoryService;
    }

}