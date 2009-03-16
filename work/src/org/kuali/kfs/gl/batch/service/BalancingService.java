/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch.service;

import org.kuali.kfs.gl.TextReportHelper;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;


/**
 * Interface for BalancingService
 */
public interface BalancingService {    
    /**
     * Handle for batch processes to kick off balancing
     * @return boolean true when success
     */
    public boolean runBalancing();
    
    /**
     * Deletes data for the given fiscal year of entries from persistentClass.
     * @param universityFiscalYear the given university fiscal year
     * @param persistentClass table for which to delete the history
     */
    public void deleteHistory(Integer universityFiscalYear, Class<? extends PersistableBusinessObjectBase> persistentClass);
    
    /**
     * Gets count for given fiscal year of entries from persistentClass.
     * @param fiscalYear parameter may be null which will get count for all years
     * @param persistentClass table for which to get the count
     * @return count
     */
    public int getHistoryCount(Integer fiscalYear, Class<? extends PersistableBusinessObjectBase> persistentClass);    
    
    /**
     * Compares entries in the Balance and BalanceHistory tables to ensure the amounts match.
     * @param textReportHelper handle on TextReportHelper for fancy printing
     * @return count is compare failures
     */
    public Integer compareBalanceHistory(TextReportHelper textReportHelper);
    
    /**
     * Compares entries in the Entry and EntryHistory tables to ensure the amounts match.
     * @param textReportHelper handle on TextReportHelper for fancy printing
     * @return count is compare failures
     */
    public Integer compareEntryHistory(TextReportHelper textReportHelper);
    
    /**
     * This is a helper method that wraps parsing poster entries for updateEntryHistory and updateBalanceHistory.
     * @param startUniversityFiscalYear fiscal year for which to accept the earlier parsed lines from the input file
     * @param textReportHelper handle on TextReportHelper for fancy printing
     * @return indicated whether records where ignored due to being older then startUniversityFiscalYear
     */
    public int updateHistoriesHelper(Integer startUniversityFiscalYear, TextReportHelper textReportHelper);
    
    /**
     * @return filename for the report
     */
    public abstract String getReportFilename();
    
    /**
     * @return title for the report
     */
    public abstract String getReportTitle();
    
    /**
     * @return input filename to the poster
     */
    public abstract String getPosterInputFilename();

    /**
     * @return output error filename from the poster
     */
    public abstract String getPosterErrorOutputFilename();
    
    /**
     * @return functional label for balance table
     */
    public abstract String getBalanceLabel();
    
    /**
     * @return functional label for entry table
     */
    public abstract String getEntryLabel();
    
    /**
     * @param startUniversityFiscalYear university fiscal year for which the process starts on
     * @return verbal instructions for a user on how to generate data for the process to properly run
     */
    public abstract String getTableCreationInstructions(int startUniversityFiscalYear);
    
    /**
     * Gets an OriginEntry for the parsed line. This needs to be handled separately for GL and Labor because Labor is a special case
     * of GL (positionNumber + emplid). Could be done with an OriginEntryHistory interface but in the interest of not mucking with
     * OriginEntries the is done with delegation.
     * @param inputLine line that was read from getPosterInputFilename
     * @param lineNumber line number we are currently reading from getPosterInputFilename
     * @return parsed line into an object as per inputLine parameter
     */
    public abstract OriginEntry getOriginEntry(String inputLine, int lineNumber);
    
    /**
     * Update the entry history table
     * @param originEntry representing the update details
     */
    public abstract void updateEntryHistory(OriginEntry originEntry);
    
    /**
     * Updates the balance history table
     * @param originEntry representing the update details
     */
    public abstract void updateBalanceHistory(OriginEntry originEntry);
    
    /**
     * Returns a Balance object for the parameters of the passed in LedgerBalanceHistory. Necessary for generic amount comparision since
     * it may be either labor or gl.
     * @param ledgerBalanceHistory to retrieve the Balance object for
     * @return balance object adhereing to the Balance interface
     */
    public abstract Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory);
}
