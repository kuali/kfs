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
package org.kuali.kfs.gl.batch.service;

import java.util.Collection;

import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * This interface declares the methods needed to update an account balance, based on a transaction.
 */
public interface AccountBalanceCalculator {
    /**
     * Given a collection of account balance records, returns either the account balance the given
     * transaction should post against or a new account balance record
     * 
     * @param balanceList a Collection of AccountBalance records
     * @param t the transaction to post
     * @return
     */
    public AccountBalance findAccountBalance(Collection balanceList, Transaction t);

    /**
     * Updates the given account balance record, based on the given transaction
     * 
     * @param t the transaction to post
     * @param ab the account balance being posted against
     */
    public void updateAccountBalance(Transaction t, AccountBalance ab);

}
