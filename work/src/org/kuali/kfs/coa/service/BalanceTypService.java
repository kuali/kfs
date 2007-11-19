/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.chart.service;

import java.util.Collection;
import java.util.List;

import org.kuali.module.chart.bo.codes.BalanceTyp;

/**
 * This service interface defines methods necessary for retrieving fully populated BalanceType business objects from the database
 * that are necessary for transaction processing in the application. This interface defines methods for each balance type that is
 * needed by the application. TODO - Continue to update this with new balance type as they are needed.
 */
public interface BalanceTypService {
    /**
     * This method retrieves a full instance of the appropriate BalanceType instance - Actual.
     * 
     * @return
     */
    public BalanceTyp getActualBalanceTyp();

    /**
     * This method retrieves all valid balance types in the system.
     * 
     * @return A list of active balance types in Kuali.
     */
    public Collection getAllBalanceTyps();

    /**
     * Get encumbrance balance TYPES
     * 
     * @return
     */
    public Collection getEncumbranceBalanceTypes();

    /**
     * This method retrieves a BalanceTyp instance from the Kuali database by its primary key - the balance typ's code.
     * 
     * @param code The primary key in the database for this data type.
     * @return A fully populated object instance.
     */
    public BalanceTyp getBalanceTypByCode(String code);


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
