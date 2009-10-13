/*
 * Copyright 2009 The Kuali Foundation
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

import java.util.Comparator;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryAmountHolder;
import org.kuali.kfs.gl.businessobject.PosterOutputSummaryEntry;
import org.kuali.kfs.gl.businessobject.Transaction;

/**
 * Interface for service methods which support the poster output summary report
 */
public interface PosterOutputSummaryService {
    /**
     * adds a transaction amount to a given poster output summary amount holder
     * @param t the transaction with an amount to add
     * @param amountHolder the amount holder to add the amount to
     */
    public abstract void addAmountToAmountHolder(Transaction t, PosterOutputSummaryAmountHolder amountHolder);
    
    /**
     * adds an origin entry amount to a given poster output summary amount holder
     * @param oe the origin entry with an amount to add
     * @param amountHolder the amount holder to add the amount to
     */
    public abstract void addAmountToAmountHolder(OriginEntryInformation oe, PosterOutputSummaryAmountHolder amountHolder);
    
    /**
     * Returns an instance of the comparator to use when sorting poster output summary entries
     * @return an instance of a good comparator
     */
    public abstract Comparator<PosterOutputSummaryEntry> getEntryComparator();
    
    /**
     * Summarizes the given transaction to the map 
     * @param transaction the transaction to summarize
     * @param entries the map of entries
     */
    public abstract void summarize(Transaction transaction, Map<String, PosterOutputSummaryEntry> entries);
    
    /**
     * Summarizes the given origin entry to the map
     * @param originEntry the origin entry to summarize
     * @param entries the map of entries that holds all summarizations
     */
    public abstract void summarize(OriginEntryInformation originEntry, Map<String, PosterOutputSummaryEntry> entries);
    
}
