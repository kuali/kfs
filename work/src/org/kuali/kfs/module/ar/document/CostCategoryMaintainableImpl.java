/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ar.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Overridden to add informative help message
 */
public class CostCategoryMaintainableImpl extends FinancialSystemMaintainable {
    public CostCategoryMaintainableImpl() {
        super();
        initiateInactiveRecordDisplay();
    }

    public CostCategoryMaintainableImpl(PersistableBusinessObject businessObject) {
        super(businessObject);
        initiateInactiveRecordDisplay();
    }

    /**
     * By default, the children record collections will hide inactive records
     */
    protected void initiateInactiveRecordDisplay() {
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_CODES, Boolean.FALSE);
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_LEVELS, Boolean.FALSE);
        inactiveRecordDisplay.put(ArPropertyConstants.OBJECT_CONSOLIDATIONS, Boolean.FALSE);
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

            if (!ObjectUtils.isNull(document.getOldMaintainableObject()) && !ObjectUtils.isNull(document.getOldMaintainableObject().getBusinessObject())) {
                CostCategory oldCostCategory = (CostCategory)document.getOldMaintainableObject().getBusinessObject();
                if (!sameSize(oldCostCategory.getObjectCodes(), costCategory.getObjectCodes())) {
                    if (oldCostCategory.getObjectCodes() == null) {
                        oldCostCategory.setObjectCodes(new ArrayList<CostCategoryObjectCode>());
                    }
                    while (oldCostCategory.getObjectCodes().size() < costCategory.getObjectCodes().size()) {
                        CostCategoryObjectCode paddingObjectCode = new CostCategoryObjectCode();
                        paddingObjectCode.setCategoryCode(oldCostCategory.getCategoryCode());
                        oldCostCategory.getObjectCodes().add(paddingObjectCode);
                    }
                }

                if (!sameSize(oldCostCategory.getObjectLevels(), costCategory.getObjectLevels())) {
                    if (oldCostCategory.getObjectLevels() == null) {
                        oldCostCategory.setObjectLevels(new ArrayList<CostCategoryObjectLevel>());
                    }
                    while (oldCostCategory.getObjectLevels().size() < costCategory.getObjectLevels().size()) {
                        CostCategoryObjectLevel paddingObjectLevel = new CostCategoryObjectLevel();
                        paddingObjectLevel.setCategoryCode(oldCostCategory.getCategoryCode());
                        oldCostCategory.getObjectLevels().add(paddingObjectLevel);
                    }
                }

                if (!sameSize(oldCostCategory.getObjectConsolidations(), costCategory.getObjectConsolidations())) {
                    if (oldCostCategory.getObjectConsolidations() == null) {
                        oldCostCategory.setObjectConsolidations(new ArrayList<CostCategoryObjectConsolidation>());
                    }
                    while (oldCostCategory.getObjectConsolidations().size() < costCategory.getObjectConsolidations().size()) {
                        CostCategoryObjectConsolidation paddingConsolidation = new CostCategoryObjectConsolidation();
                        paddingConsolidation.setCategoryCode(oldCostCategory.getCategoryCode());
                        oldCostCategory.getObjectConsolidations().add(paddingConsolidation);
                    }
                }
            }
        }
    }

    /**
     * Determines if two collections are the same size.  Being null and having no elements are treated as equivalent
     * @param a the first collection to check
     * @param b the second collection to check
     * @return true if the two Collections are the same size, false otherwise
     */
    protected boolean sameSize(Collection<?> a, Collection<?> b) {
        if (CollectionUtils.isEmpty(a)) {
            return CollectionUtils.isEmpty(b);
        }
        if (CollectionUtils.isEmpty(b)) {
            return false; // a isn't empty or we would have returned; therefore, a and b can't be the same size if we're here
        }
        return a.size() == b.size();
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
