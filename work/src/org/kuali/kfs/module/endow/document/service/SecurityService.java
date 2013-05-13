/*
 * Copyright 2009 The Kuali Foundation.
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
