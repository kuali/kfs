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
package org.kuali.kfs.module.endow.document.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

import org.kuali.kfs.module.endow.businessobject.Security;

/**
 * 
 */
public interface SecurityService {

    /**
     * Calculates market value or unit value based on the security valuation method
     * 
     * @param security the security for which we calculate the market value or unit value
     * @return
     */
    public void computeValueBasedOnValuationMethod(Security security);

    /**
     * This method will perform the following updates: 1) Copy current Security Unit Value to Previous Unit Value 2) Copy current
     * Security Value Date to Previous Unit Value Date 3) Copy newUnitValue to current Security Unit Value 4) Copy newValueDate to
     * current Security Unit Value Date 5) Copy newUnitValueSource to Unit Value Source
     * 
     * @param security the BO that needs to be updated.
     * @param newUnitValue the new unit value for that security
     * @param newValueDate the new value date for that security
     * @param newUnitValueSource the new unit value source for that security
     * @return Security the update security object
     */
    public Security updateUnitValue(Security security, BigDecimal newUnitValue, Date newValueDate, String newUnitValueSource);

    /**
     * This method will update the interest rate or amount
     * 
     * @return Security the update security object
     */
    public Security updateInterestRate(Security theSecurity, BigDecimal interestRate);

    /**
     * Updates incomeChangeDate to currentDate
     * 
     * @param id
     */
    public Security updateIncomeChangeDate(Security security);
    
    /**
     * Gets a Security by primary key.
     * 
     * @param id
     * @return a Security
     */
    public Security getByPrimaryKey(String id);

    /**
     * Gets all the Securities with the class code in the classCodes array and the units held greater than zero.
     * 
     * @param classCodes
     * @return All securities that meet the criteria
     */
    public List<Security> getSecuritiesByClassCodeWithUnitsGreaterThanZero(List<String> classCodes);

}
