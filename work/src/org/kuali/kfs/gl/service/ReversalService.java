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
