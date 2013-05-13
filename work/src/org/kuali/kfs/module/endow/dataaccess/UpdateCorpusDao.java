/*
 * Copyright 2010 The Kuali Foundation.
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
