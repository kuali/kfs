/*
 * Copyright 2005 The Kuali Foundation
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
