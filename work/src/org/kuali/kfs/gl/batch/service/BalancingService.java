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

import java.io.PrintStream;

import org.kuali.kfs.gl.TextReportHelper;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;


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
     * Deletes entries from balance (<T>) history table for given fiscal year.
     * @param universityFiscalYear the given university fiscal year
     */
    public void deleteBalanceHistory(Integer universityFiscalYear);
    
    /**
     * Deletes entries from entry (<S>) history table for given fiscal year.
     * @param universityFiscalYear the given university fiscal year
     */
    public void deleteEntryHistory(Integer universityFiscalYear);

    /**
     * Gets count for given fiscal year of entries from balance (<T>) history table.
     * @param fiscalYear parameter may be null which will get count for all years
     * @return count
     */
    public int getBalanceHistoryCount(Integer fiscalYear);
    
    /**
     * Gets count for given fiscal year of entries from entry (<T>) history table.
     * @param fiscalYear parameter may be null which will get count for all years
     * @return count
     */
    public int getEntryHistoryCount(Integer fiscalYear);
    
    /**
     * Compares entries in the Balance and BalanceHistory tables to ensure the amounts match.
     * @param REPORT_ps handle on PrintStream for report file
     * @param textReportHelper handle on TextReportHelper for fancy REPORT_ps printing
     * @return count is compare failures
     */
    public int compareBalanceHistory(PrintStream REPORT_ps, TextReportHelper textReportHelper);
    
    /**
     * Compares entries in the Entry and EntryHistory tables to ensure the amounts match.
     * @param REPORT_ps handle on PrintStream for report file
     * @param textReportHelper handle on TextReportHelper for fancy REPORT_ps printing
     * @return count is compare failures
     */
    public int compareEntryHistory(PrintStream REPORT_ps, TextReportHelper textReportHelper);
    
    /**
     * Counts the number of entries in the Balance and BalanceHistory table for either Labor or GL.
     * @param startUniversityFiscalYear fiscal year for which to start the comparision from
     * @param REPORT_ps handle on PrintStream for report file
     * @return whether the count matched or not
     */
    public boolean countBalanceCompare(Integer startUniversityFiscalYear, PrintStream REPORT_ps);

    /**
     * Counts the number of entries in the Entry and EntryHistory table for either Labor or GL.
     * @param startUniversityFiscalYear fiscal year for which to start the comparision from
     * @param REPORT_ps handle on PrintStream for report file
     * @return whether the count matched or not
     */
    public boolean countEntryCompare(Integer startUniversityFiscalYear, PrintStream REPORT_ps);
    
    /**
     * This is a helper method that wraps parsing poster entries for updateEntryHistory and updateBalanceHistory.
     * @param startUniversityFiscalYear fiscal year for which to accept the earlier parsed lines from the input file
     * @param REPORT_ps handle on PrintStream for report file
     * @param textReportHelper handle on TextReportHelper for fancy REPORT_ps printing
     * @return indicated whether records where ignored due to being older then startUniversityFiscalYear
     */
    public boolean updateHistoriesHelper(Integer startUniversityFiscalYear, PrintStream REPORT_ps, TextReportHelper textReportHelper);
    
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
