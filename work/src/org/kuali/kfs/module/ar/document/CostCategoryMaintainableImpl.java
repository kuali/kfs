/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Overridden to add informative help message
 */
public class CostCategoryMaintainableImpl extends FinancialSystemMaintainable {

    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.CostCategoryMaintenanceSections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.CostCategoryMaintenanceSections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterEdit(document, requestParameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.CostCategoryMaintenanceSections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> requestParameters) {
        super.processAfterNew(document, requestParameters);
        GlobalVariables.getMessageMap().putInfo(ArConstants.CostCategoryMaintenanceSections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    @Override
    public void processAfterAddLine(String colName, Class colClass) {
        super.processAfterAddLine(colName, colClass);
        GlobalVariables.getMessageMap().putInfo(ArConstants.CostCategoryMaintenanceSections.EDIT_CATEGORY, ArKeyConstants.ContractsGrantsCategoryConstants.CATEGORY_INFO);
    }

    /**
     * Overridden to push the cost category code to all child objects
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        if (StringUtils.equals(KFSConstants.MULTIPLE_VALUE, refreshCaller)) {
            final String collectionName = (String)fieldValues.get(KFSConstants.LOOKED_UP_COLLECTION_NAME);
            CostCategory costCategory = (CostCategory)document.getNewMaintainableObject().getBusinessObject();

            if (StringUtils.equals(collectionName, ArPropertyConstants.OBJECT_CODES) &&!CollectionUtils.isEmpty(costCategory.getObjectCodes())) {
                for (CostCategoryObjectCode objectCode : costCategory.getObjectCodes()) {
                    if (StringUtils.isBlank(objectCode.getCategoryCode())) {
                        objectCode.setCategoryCode(costCategory.getCategoryCode());
                    }
                }
            }
            if (StringUtils.equals(collectionName, ArPropertyConstants.OBJECT_LEVELS) && !CollectionUtils.isEmpty(costCategory.getObjectLevels())) {
                for (CostCategoryObjectLevel objectLevel : costCategory.getObjectLevels()) {
                    if (StringUtils.isBlank(objectLevel.getCategoryCode())) {
                        objectLevel.setCategoryCode(costCategory.getCategoryCode());
                    }
                }
            }
            if (StringUtils.equals(collectionName, ArPropertyConstants.OBJECT_CONSOLIDATIONS) && !CollectionUtils.isEmpty(costCategory.getObjectConsolidations())) {
                for (CostCategoryObjectConsolidation objectConsolidation : costCategory.getObjectConsolidations()) {
                    if (StringUtils.isBlank(objectConsolidation.getCategoryCode())) {
                        objectConsolidation.setCategoryCode(costCategory.getCategoryCode());
                    }
                }
            }
        }
    }

    /**
     * Overridden to filter down category code before cost category detail makes it to rules checking
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#processBeforeAddLine(java.lang.String, java.lang.Class, org.kuali.rice.krad.bo.BusinessObject)
     */
    @Override
    public void processBeforeAddLine(String colName, Class colClass, BusinessObject bo) {
        super.processBeforeAddLine(colName, colClass, bo);
        if (bo instanceof CostCategoryObjectCode) {
            ((CostCategoryObjectCode)bo).setCategoryCode(((CostCategory)getBusinessObject()).getCategoryCode());
        } else if (bo instanceof CostCategoryObjectLevel) {
            ((CostCategoryObjectLevel)bo).setCategoryCode(((CostCategory)getBusinessObject()).getCategoryCode());
        } else if (bo instanceof CostCategoryObjectConsolidation) {
            ((CostCategoryObjectConsolidation)bo).setCategoryCode(((CostCategory)getBusinessObject()).getCategoryCode());
        }
    }
}