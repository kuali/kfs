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
