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
package org.kuali.kfs.gl.service;

import java.util.Date;
import java.util.Iterator;

import org.kuali.kfs.gl.businessobject.LedgerEntryHolder;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * An interface with methods to interact with reversals
 */
public interface ReversalService {
    /**
     * Saves a reversal to the database
     * 
     * @param re the reversal to save
     */
    public void save(Reversal re);

    /**
     * Fetches or generates a reversal record, based on the given transaction
     * 
     * @param t a transaction to find a reversal record for
     * @return a reversal record for the transaction
     */
    public Reversal getByTransaction(Transaction t);

    /**
     * Fetches all of the reversals that are set to reverse before or on the given date
     * 
     * @param before the date returned reversals should reverse on or before
     * @return an Iterator of reversals
     */
    public Iterator getByDate(Date before);

    /**
     * Summarizes all of the reversal records set to reverse before or on the given date
     * 
     * @param before the date summarized reversals should reverse on or before
     * @return a LedgerEntryHolder with the summary date
     */
    public LedgerEntryHolder getSummaryByDate(Date before);

    /**
     * Removes a reversal record from the persistence store
     * 
     * @param re the reversal to send to the happy reversal farm in the sky
     */
    public void delete(Reversal re);
}
