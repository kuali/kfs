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
