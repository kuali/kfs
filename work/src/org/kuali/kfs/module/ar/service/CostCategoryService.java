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

    /**
     * Looks up the cost category which best matches the given fiscal year, chart of accounts, and financial object code of an object code
     * @param universityFiscalYear the fiscal year of the object code to find a cost category for
     * @param chartOfAccountsCode the chart of accounts code of an object code to find a cost category for
     * @param financialObjectCode the financial object code of a financial object to find a cost category for
     * @return the matching cost category, or null if no matching cost category could be found
     */
    public CostCategory getCostCategoryForObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode);
}
