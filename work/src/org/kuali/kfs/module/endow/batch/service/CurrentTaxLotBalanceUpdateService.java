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
package org.kuali.kfs.module.endow.batch.service;

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;

/**
 * This interface defines the batch job that would to update current tax lot balance records
 */
public interface CurrentTaxLotBalanceUpdateService {

    /**
     * At the end of each business processing cycle and in preparation for the next day's business, 
     * the system will compile the information in the Holding Tax Lot Table along with calculations
     * of market value and estimated income into a new table called the Current Tax Lot Balances.
     * This table is used as the basis for balance inquiries as well as the source of data for 
     * creating the Holding Archive records.
     */
    public boolean updateCurrentTaxLotBalances();
    
    /**
     * Removes the all the records from the CurrentTaxLot table (END_CURR_TAX_LOT_BAL_T table).
     */
    
    public abstract void clearAllCurrentTaxLotRecords();

    /**
     * Saves the CurrentTaxLot record to the table (END_CURR_TAX_LOT_BAL_T).
     */
    public abstract void saveCurrentTaxLotRecord(CurrentTaxLotBalance currentTaxLotBalance);
}
