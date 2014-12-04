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
package org.kuali.kfs.module.bc.document.service;

import org.kuali.kfs.module.bc.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;

/**
 * This provides methods specific to system parameters for the Budget module
 */
public interface BudgetParameterService {

    /**
     * determines if a BudgetConstructionDocument's account is a salary setting only account returns
     * AccountSalarySettingOnlyCause.NONE if not and if both system parameters don't exist
     *
     * @param bcDoc
     * @return
     */
    public AccountSalarySettingOnlyCause isSalarySettingOnlyAccount(BudgetConstructionDocument bcDoc);

    /**
     * returns a string containing the allowed revenue or expenditure object types setup in the Budget Construction parameter space.
     * this string is typically used in the lookup search criteria
     *
     * @param isRevenue
     * @return
     */
    public String getLookupObjectTypes(boolean isRevenue);
}
