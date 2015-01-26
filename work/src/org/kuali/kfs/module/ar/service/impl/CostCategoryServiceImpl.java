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
package org.kuali.kfs.module.ar.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.ObjectCodeCurrent;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.module.ar.dataaccess.CostCategoryDao;
import org.kuali.kfs.module.ar.service.CostCategoryService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the CostCategoryService, basically wrapping CostCategoryDao service methods
 */
@Transactional
public class CostCategoryServiceImpl implements CostCategoryService {
    protected CostCategoryDao costCategoryDao;
    protected BusinessObjectService businessObjectService;

    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CostCategoryServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.ar.service.CostCategoryService#isCostCategoryObjectConsolidationUnique(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation)
     */
    @Override
    public CostCategoryDetail isCostCategoryObjectConsolidationUnique(CostCategoryObjectConsolidation objectConsolidation) {
        CostCategoryDetail detail = getCostCategoryDao().retrieveMatchingCostCategoryConsolidationAmongConsolidations(objectConsolidation);
        if (!ObjectUtils.isNull(detail)) {
            return detail;
        }
        detail = getCostCategoryDao().retrieveMatchingCostCategoryConsolidationAmongLevels(objectConsolidation);
        if (!ObjectUtils.isNull(detail)) {
            return detail;
        }
        return getCostCategoryDao().retrieveMatchingCostCategoryConsolidationAmongCodes(objectConsolidation);
    }

    /**
     * @see org.kuali.kfs.module.ar.service.CostCategoryService#isCostCategoryObjectLevelUnique(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel)
     */
    @Override
    public CostCategoryDetail isCostCategoryObjectLevelUnique(CostCategoryObjectLevel objectLevel) {
        CostCategoryDetail detail = getCostCategoryDao().retrieveMatchingCostCategoryLevelAmongLevels(objectLevel);
        if (!ObjectUtils.isNull(detail)) {
            return detail;
        }
        detail = getCostCategoryDao().retrieveMatchingCostCategoryLevelAmongConsolidations(objectLevel);
        if (!ObjectUtils.isNull(detail)) {
            return detail;
        }
        return getCostCategoryDao().retrieveMatchingCostCategoryLevelAmongCodes(objectLevel);
    }

    /**
     * @see org.kuali.kfs.module.ar.service.CostCategoryService#isCostCategoryObjectCodeUnique(org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode)
     */
    @Override
    public CostCategoryDetail isCostCategoryObjectCodeUnique(CostCategoryObjectCode objectCode) {
        CostCategoryDetail detail = getCostCategoryDao().retrieveMatchingCostCategoryObjectCodeAmongCodes(objectCode);
        if (!ObjectUtils.isNull(detail)) {
            return detail;
        }
        detail = getCostCategoryDao().retrieveMatchingCostCategoryObjectCodeAmongLevels(objectCode);
        if (!ObjectUtils.isNull(detail)) {
            return detail;
        }
        return getCostCategoryDao().retrieveCostCategoryObjectCodeAmongConsolidations(objectCode);
    }

    /**
     * @see org.kuali.kfs.module.ar.service.CostCategoryService#getBalancesForCostCategory(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, org.kuali.kfs.module.ar.businessobject.CostCategory)
     */
    @Override
    public List<Balance> getBalancesForCostCategory(Integer fiscalYear, String chartOfAccountsCode, String accountNumber, String balanceType, Collection<String> objectTypeCodes, CostCategory costCategory) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Retrieving balances for cost category: "+costCategory.getCategoryCode()+"; fiscal year = "+fiscalYear+"; chart: "+chartOfAccountsCode+"; account number: "+accountNumber+"; balance type: "+balanceType+"; object type codes: "+objectTypeCodes.toString());
        }
        return getCostCategoryDao().getBalancesForCostCategory(fiscalYear, chartOfAccountsCode, accountNumber, balanceType, objectTypeCodes, costCategory);
    }

    /**
     * @see org.kuali.kfs.module.ar.service.CostCategoryService#getCostCategoryForObjectCode(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public CostCategory getCostCategoryForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        return getCostCategoryDao().getCostCategoryForObjectCode(universityFiscalYear, chartOfAccountsCode, financialObjectCode);
    }

    /**
     * Look up the given category; if found, then look up an object code with the given chart within any object code, level, or consolidation within the category
     * @see org.kuali.kfs.module.ar.service.CostCategoryService#findObjectCodeForChartAndCategory(java.lang.String, java.lang.String)
     */
    @Override
    public ObjectCodeCurrent findObjectCodeForChartAndCategory(String chartOfAccountsCode, String categoryCode) {
        final CostCategory costCategory = getBusinessObjectService().findBySinglePrimaryKey(CostCategory.class, categoryCode);

        if (ObjectUtils.isNull(costCategory)) {
            return null;
        }

        ObjectCodeCurrent foundObjectCode = null;
        if (!CollectionUtils.isEmpty(costCategory.getObjectCodes())) {
            foundObjectCode = findMatchingObjectCodeByObjectCode(chartOfAccountsCode, costCategory.getObjectCodes());
        }

        if (!ObjectUtils.isNull(foundObjectCode) && !CollectionUtils.isEmpty(costCategory.getObjectLevels())) {
            foundObjectCode = findMatchingObjectCodeByObjectLevel(chartOfAccountsCode, costCategory.getObjectLevels());
        }

        if (!ObjectUtils.isNull(foundObjectCode) && !CollectionUtils.isEmpty(costCategory.getObjectConsolidations())) {
            foundObjectCode = findMatchingObjectCodeByObjectConsolidation(chartOfAccountsCode, costCategory.getObjectConsolidations());
        }

        return foundObjectCode;
    }

    /**
     * Finds an object code which matches the given chart of accounts within the list of cost category object codes
     * @param chartOfAccountsCode the chart of accounts code to find an object code for
     * @param categoryObjectCodes a list of cost category object codes
     * @return a matching object code, or null if one could not be found
     */
    protected ObjectCodeCurrent findMatchingObjectCodeByObjectCode(String chartOfAccountsCode, List<CostCategoryObjectCode> categoryObjectCodes) {
        for (CostCategoryObjectCode costCategoryObjectCode : categoryObjectCodes) {
            if (StringUtils.equals(costCategoryObjectCode.getChartOfAccountsCode(), chartOfAccountsCode)) {
                return costCategoryObjectCode.getObjectCodeCurrent();
            }
        }
        return null;
    }

    /**
     * Finds an object code which matches the given chart of accounts within the list of cost category object levels
     * @param chartOfAccountsCode the chart of accounts code to find an object code for
     * @param categoryObjectLevels a list of cost category object levels
     * @return a matching object code, or null if one could not be found
     */
    protected ObjectCodeCurrent findMatchingObjectCodeByObjectLevel(String chartOfAccountsCode, List<CostCategoryObjectLevel> categoryObjectLevels) {
        for (CostCategoryObjectLevel costCategoryObjectLevel : categoryObjectLevels) {
            if (StringUtils.equals(costCategoryObjectLevel.getChartOfAccountsCode(), chartOfAccountsCode)) {
                Map<String, Object> fieldValues = new HashMap<>();
                fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
                fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, costCategoryObjectLevel.getFinancialObjectLevelCode());
                Collection<ObjectCodeCurrent> objectCodes = getBusinessObjectService().findMatching(ObjectCodeCurrent.class, fieldValues);
                if (!CollectionUtils.isEmpty(objectCodes)) {
                    return TransactionalServiceUtils.retrieveFirstAndExhaustIterator(objectCodes.iterator());
                }
            }
        }
        return null;
    }

    /**
     * Finds an object code which matches the given chart of accounts within the list of cost category object consolidations
     * @param chartOfAccountsCode the chart of accounts code to find an object code for
     * @param categoryObjectConsolidations a list of cost category object consolidations
     * @return a matching object code, or null if one could not be found
     */
    protected ObjectCodeCurrent findMatchingObjectCodeByObjectConsolidation(String chartOfAccountsCode, List<CostCategoryObjectConsolidation> categoryObjectConsolidations) {
        for (CostCategoryObjectConsolidation costCategoryObjectConsolidation : categoryObjectConsolidations) {
            if (StringUtils.equals(costCategoryObjectConsolidation.getChartOfAccountsCode(), chartOfAccountsCode)) {
                Map<String, Object> fieldValues = new HashMap<>();
                fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
                fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL+"."+KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE, costCategoryObjectConsolidation.getFinConsolidationObjectCode());
                Collection<ObjectCodeCurrent> objectCodes = getBusinessObjectService().findMatching(ObjectCodeCurrent.class, fieldValues);
                if (!CollectionUtils.isEmpty(objectCodes)) {
                    return TransactionalServiceUtils.retrieveFirstAndExhaustIterator(objectCodes.iterator());
                }
            }
        }
        return null;
    }

    public CostCategoryDao getCostCategoryDao() {
        return costCategoryDao;
    }

    public void setCostCategoryDao(CostCategoryDao costCategoryDao) {
        this.costCategoryDao = costCategoryDao;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
