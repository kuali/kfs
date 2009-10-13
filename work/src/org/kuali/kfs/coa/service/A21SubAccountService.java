/*
 * Copyright 2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.A21SubAccount;

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
    
    /**
     * build a CG ICR account
     * @param chartOfAccountsCode the given chart of account
     * @param accountNumber the given account number
     * @param subAccountNumber the given sub account number
     * @param subAccountTypeCode the type of the CG ICR account
     * @return a CG ICR account built from the given information
     */
    public A21SubAccount buildCgIcrAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber, String subAccountTypeCode);
    
    /**
     * populate the a21 sub account with the given account
     * 
     * @param a21SubAccount the a21 sub account needed to be populated
     * @param chartOfAccountsCode the given chart of account
     * @param accountNumber the given account number
     */
    public void populateCgIcrAccount(A21SubAccount a21SubAccount, String chartOfAccountsCode, String accountNumber);
}
