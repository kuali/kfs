/*
 * Copyright 2006 The Kuali Foundation.
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

import org.kuali.module.chart.bo.A21SubAccount;

/**
 * 
 * This interface defines the methods for retrieving fully populated A21SubAccount objects
 */
public interface A21SubAccountService {
    
    /**
     * 
     * This retrieves an A21SubAccount by its primary keys of chart of accounts code, account number and 
     * sub account number
     * @param chartOfAccountsCode
     * @param accountNumber
     * @param subAccountNumber
     * @return the A21SubAccount that matches this primary key
     */
    public A21SubAccount getByPrimaryKey(String chartOfAccountsCode, String accountNumber, String subAccountNumber);
}
