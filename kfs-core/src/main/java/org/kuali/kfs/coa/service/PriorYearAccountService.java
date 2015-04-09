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

import org.kuali.kfs.coa.businessobject.PriorYearAccount;

/**
 * 
 * This service interface defines methods necessary for retrieving fully populated PriorYearAccount business objects from the database
 * that are necessary for transaction processing in the application. It also defines a method for populating the account db table with prior year values
 */
public interface PriorYearAccountService {

    /**
     * @param chartCode
     * @param accountNumber
     * @return
     */
    public PriorYearAccount getByPrimaryKey(String chartCode, String accountNumber);

    /**
     * This method populates the prior year account table in the database with all the values from the current year account table.
     */
    public void populatePriorYearAccountsFromCurrent();
    
    /**
     * This method adds to the prior year account table the list of accounts defined in a parameter with all newly created accounts to be used for prior year,
     * and creates a report on the accounts that are added successfully, as well as those failed to be added.
     */
    public void addPriorYearAccountsFromParameter();
    
}

