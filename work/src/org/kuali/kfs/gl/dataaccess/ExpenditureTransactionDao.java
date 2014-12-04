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
package org.kuali.kfs.gl.dataaccess;

import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * An DAO interface to deal help expenditure transactions to deal with the database 
 */
public interface ExpenditureTransactionDao {
    /**
     * Returns the expenditure transaction in the database that would be affected if the given transaction is posted
     * 
     * @param t a transaction to find a related expenditure transaction for
     * @return the expenditure transaction if found, null otherwise
     */
    public ExpenditureTransaction getByTransaction(Transaction t);

    /**
     * Returns all expenditure transactions currently in the database
     * 
     * @return an Iterator with all expenditure transactions from the database
     */
    public Iterator getAllExpenditureTransactions();

    /**
     * Deletes the given expenditure transaction
     * 
     * @param et the expenditure transaction that will be removed, as such, from the database
     */
    public void delete(ExpenditureTransaction et);

    /**
     * Since expenditure transactions are temporary, this method removes all of the currently existing
     * expenditure transactions from the database
     */
    public void deleteAllExpenditureTransactions();
}
