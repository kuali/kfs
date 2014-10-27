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
package org.kuali.kfs.module.ar.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;

/**
 * Service to support actions around cost categories
 */
public interface CostCategoryService {
    /**
     * Determines if a given cost category object consolidation would have an object consolidation unshared by any other cost categories
     * @param objectConsolidation the cost category object consolidation to check
     * @return null if the object consolidation is unique, otherwise the blocking cost category detail
     */
    public CostCategoryDetail isCostCategoryObjectConsolidationUnique(CostCategoryObjectConsolidation objectConsolidation);
    /**
     * Determines if a given cost category object level would have an object level unshared and uncontained by any other cost categories
     * @param objectLevel the cost category object level to check
     * @return null if the object level is unique, otherwise the blocking cost category detail
     */
    public CostCategoryDetail isCostCategoryObjectLevelUnique(CostCategoryObjectLevel objectLevel);

    /**
     * Determines if a given cost category object code would have an object code unshared and uncontained by any other cost categories
     * @param objectCode the cost category object code to check for uniqueness
     * @return null if the object code is unique, otherwise the blocking cost category detail
     */
    public CostCategoryDetail isCostCategoryObjectCodeUnique(CostCategoryObjectCode objectCode);

    /**
     * Retrieves matching balances for all object codes contained within the given cost category
     * @param fiscalYear the fiscal year of balances to find
     * @param chartOfAccountsCode the chart of account code of balances to find
     * @param accountNumber the account number of balances to find
     * @param balanceType the balance type of balances to find
     * @param objectType the object type of balances to find
     * @param costCategory the cost category of object codes to find
     * @return a List of all matching balances
     */
    public List<Balance> getBalancesForCostCategory(Integer fiscalYear, String chartOfAccountsCode, String accountNumber, String balanceType, Collection<String> objectTypeCodes, CostCategory costCategory);
}