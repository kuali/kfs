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
