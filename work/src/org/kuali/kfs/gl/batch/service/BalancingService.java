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
     * @return system parameter for NUMBER_OF_PAST_FISCAL_YEARS_TO_CONSIDER
     */
    public abstract int getPastFiscalYearsToConsider();
    
    /**
     * @return system parameter for NUMBER_OF_COMPARISON_FAILURES_TO_PRINT_PER_REPORT
     */
    public abstract int getComparisonFailuresToPrintPerReport();
    
    /**
     * @param businessObjectName name of the BO for which to return the label
     * @return functional short labels for tables affected in this process
     */
    public abstract String getShortTableLabel(String businessObjectName);
    
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
