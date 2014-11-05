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
package org.kuali.kfs.module.ar.service.impl;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;
import org.kuali.kfs.module.ar.dataaccess.CostCategoryDao;
import org.kuali.kfs.module.ar.service.CostCategoryService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * Default implementation of the CostCategoryService, basically wrapping CostCategoryDao service methods
 */
@Transactional
public class CostCategoryServiceImpl implements CostCategoryService {
    protected CostCategoryDao costCategoryDao;

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

    public CostCategoryDao getCostCategoryDao() {
        return costCategoryDao;
    }

    public void setCostCategoryDao(CostCategoryDao costCategoryDao) {
        this.costCategoryDao = costCategoryDao;
    }
}
