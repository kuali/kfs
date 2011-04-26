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
package org.kuali.kfs.gl.dataaccess;

import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * An interface that declares the methods needed for reversal services to interact with the database
 */
public interface ReversalDao {

    /**
     * Find the maximum transactionLedgerEntrySequenceNumber in the entry table for a specific transaction. This is used to make
     * sure that rows added have a unique primary key.
     * 
     * @param t a transaction to find the maximum sequence number for
     * @return the max sequence number for the given transaction
     */
    public int getMaxSequenceNumber(Transaction t);

    /**
     * Looks up the reversal that matches the keys from the given transaction
     * 
     * @param t the given transaction
     * @return the reversal that matches the keys of that transaction
     */
    public Reversal getByTransaction(Transaction t);

    /**
     * Returns all reversals that should have reversed on or before the given date
     * 
     * @param before the date that reversals retrieved should reverse on or before
     * @return an iterator of reversal records
     */
    public Iterator getByDate(Date before);

    /**
     * Deletes a reversal record
     * 
     * @param re a reversal to delete
     */
    public void delete(Reversal re);
}
