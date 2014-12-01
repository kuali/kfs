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
