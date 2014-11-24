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
