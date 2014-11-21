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

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * This interface declares methods needed for posting transactions to the appropriate balance records.
 */
public interface BalanceCalculator {
    /**
     * Given a collection of balance records, returns the balance that the given transaction would post to
     * or creates a new balance record
     * 
     * @param balanceList a Collection of balance records
     * @param t the transaction to post
     * @return the balance to post against
     */
    public Balance findBalance(Collection balanceList, Transaction t);

    /**
     * Updates the balance based on the given transaction
     * 
     * @param t the transaction to post
     * @param b the balance being posted against
     */
    public void updateBalance(Transaction t, Balance b);
}
