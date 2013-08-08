/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.coa.service;

import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.BalanceType;

/**
 * This service interface defines methods necessary for retrieving fully populated BalanceType business objects from the database
 * that are necessary for transaction processing in the application. This interface defines methods for each balance type that is
 * needed by the application. TODO - Continue to update this with new balance type as they are needed.
 */
public interface BalanceTypeService {

    static final String ACTUAL_BALANCE_TYPE = "AC";

    /**
     * This method retrieves all valid balance types in the system.
     *
     * @return A list of active balance types in Kuali.
     */
    public Collection<BalanceType> getAllBalanceTypes();

    /**
     * @return a List of balance types for encumbrances, regardless of how they're used in the current fiscal year
     */
    public Collection<BalanceType> getAllEncumbranceBalanceTypes();

    /**
     * This method retrieves a BalanceType instance from the Kuali database by its primary key - the balance type's code.
     *
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    public BalanceType getBalanceTypeByCode(String code);


    /**
     * Returns the list of encumbrance-related balance types from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return
     */
    public List<String> getEncumbranceBalanceTypes(Integer universityFiscalYear);

    /**
     * Returns the cost share encumbrance balance type from options table for a given university fiscal year
     *
     * @param universityFiscalYear
     * @return
     */
    public String getCostShareEncumbranceBalanceType(Integer universityFiscalYear);


    /**
     * Returns the list of encumbrance-related balance types from options table for the current university fiscal year
     *
     * @param universityFiscalYear
     * @return
     */
    public List<String> getCurrentYearEncumbranceBalanceTypes();

    /**
     * Returns the cost share encumbrance balance type from options table for the current university fiscal year
     *
     * @param universityFiscalYear
     * @return
     */
    public String getCurrentYearCostShareEncumbranceBalanceType();

    public List<String> getContinuationAccountBypassBalanceTypeCodes(Integer universityFiscalYear);
}
