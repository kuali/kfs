/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.module.chart.bo.PriorYearAccount;

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
}
