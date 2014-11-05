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
package org.kuali.kfs.module.ar.dataaccess;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.module.ar.businessobject.CostCategory;
import org.kuali.kfs.module.ar.businessobject.CostCategoryDetail;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectCode;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectConsolidation;
import org.kuali.kfs.module.ar.businessobject.CostCategoryObjectLevel;

/**
 * A DAO which doesn't select cost categories so much as do complex queries to check uniqueness of cost category information as well
 * as grabbing balances
 */
public interface CostCategoryDao {
    /**
     * Determines if a given cost category object consolidation would have an object consolidation unshared by any other cost categories
     * @param objectConsolidation the cost category object consolidation to check
     * @return null if the object consolidation is unique, otherwise the blocking cost category object consolidation
     */
    public CostCategoryDetail retrieveMatchingCostCategoryConsolidationAmongConsolidations(CostCategoryObjectConsolidation objectConsolidation);

    /**
     * Determines if a given cost category object consolidation would have an object consolidation unshared by any cost category object levels
     * @param consolidation the cost category object consolidation to check
     * @return null if the object consolidation is unique among cost category object levels, otherwise the blocking cost category object level
     */
    public CostCategoryDetail retrieveMatchingCostCategoryConsolidationAmongLevels(CostCategoryObjectConsolidation consolidation);

    /**
     * Determines if a given cost category object consolidation would have an object consolidation unshared by any cost category object codes
     * @param consolidation the cost category object consolidation to check
     * @return null if the object consolidation is unique among cost category object codes, otherwise the blocking cost category object code
     */
    public CostCategoryDetail retrieveMatchingCostCategoryConsolidationAmongCodes(CostCategoryObjectConsolidation consolidation);

    /**
     * Determines if a given cost category object level would have an object level unshared by any other cost categories
     * @param objectLevel the cost category object level to check
     * @return null if the object level is unique, otherwise the blocking cost category detail
     */
    public CostCategoryDetail retrieveMatchingCostCategoryLevelAmongLevels(CostCategoryObjectLevel objectLevel);

    /**
     * Determines if a given cost category object level would be uncontained by any other cost category object consolidations
     * @param level the cost category object level to check
     * @return null if the object level is uncontained by any object consolidations on other cost categories, otherwise the blocking cost category object consolidation
     */
    public CostCategoryDetail retrieveMatchingCostCategoryLevelAmongConsolidations(CostCategoryObjectLevel level);

    /**
     * Determines if a given cost category object level would have an object level unshared by any cost category object codes
     * @param level the cost category object level to check
     * @return null if the object level is unique among cost category object codes, otherwise the blocking cost category object code
     */
    public CostCategoryDetail retrieveMatchingCostCategoryLevelAmongCodes(CostCategoryObjectLevel level);

    /**
     * Determines if a given cost category object code would have an object code unshared by any other cost categories object codes
     * @param objectCode the cost category object code to check for uniqueness
     * @return null if the object code is unique among all cost category object codes, otherwise the blocking cost category object code
     */
    public CostCategoryDetail retrieveMatchingCostCategoryObjectCodeAmongCodes(CostCategoryObjectCode objectCode);

    /**
     * Determines if a given cost category object code would not be contained by a cost category object level on a different cost category
     * @param objectCode the cost category object code to check
     * @return null if the object code is uncontained by any other cost categories' object levels, otherwise the blocking cost category object level
     */
    public CostCategoryDetail retrieveMatchingCostCategoryObjectCodeAmongLevels(CostCategoryObjectCode objectCode);

    /**
     * Determines if a given cost category object code would not be contained by a cost category object consolidation on a different cost category
     * @param objectCode the cost category object code to check
     * @return null if the object code is uncontained by any other cost categories' object consolidations, otherwise the blocking cost category object consolidation
     */
    public CostCategoryDetail retrieveCostCategoryObjectCodeAmongConsolidations(CostCategoryObjectCode objectCode);

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
    public List<Balance> getBalancesForCostCategory(Integer fiscalYear, String chartOfAccountsCode, String accountNumber, String balanceType, Collection<String> objectType, CostCategory costCategory);

    /**
     * Looks up the cost category which best matches the given fiscal year, chart of accounts, and financial object code of an object code
     * @param universityFiscalYear the fiscal year of the object code to find a cost category for
     * @param chartOfAccountsCode the chart of accounts code of an object code to find a cost category for
     * @param financialObjectCode the financial object code of a financial object to find a cost category for
     * @return the matching cost category, or null if no matching cost category could be found
     */
    public CostCategory getCostCategoryForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);
}