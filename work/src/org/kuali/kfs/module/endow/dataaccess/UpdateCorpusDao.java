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
package org.kuali.kfs.module.endow.dataaccess;

import java.sql.Date;

public interface UpdateCorpusDao {

    /**
     * Updates all prior year fields with the current year values
     * in the KEMID Corpus Val table. 
     * 
     */
    public void updateKemIdCorpusPriorYearValues();
    
    /**
     * Summarizes and updates the transaction archive table and 
     * adds the totals to the Current Endowment Corpus table.
     * Then taking the grand totals from Current Endowment Corpus table
     * the KEMID Corpus Values table is updated.
     *     
     */
    public void updateCorpusAmountsFromTransactionArchive(Date currentDate);
    
    /**
     * Updates all KEM ID Corpus records's principal market value
     * with the value PRIN_AT_MARKET from KEM ID Current Balance View.
     * 
     */
    public void updateKemIdCorpusPrincipalMarketValue();
    
    /**
     * Inserts records into Endowment Corpus from the 
     * Current Endowment Corpus table.
     * 
     */
    public void updateEndowmentCorpusWithCurrentEndowmentCorpus(Date currentDate);
}
