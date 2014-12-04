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
package org.kuali.kfs.gl.batch.service;

import java.io.File;

import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.LedgerBalanceHistory;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;


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
     * @return input poster file. Returns null if no file found.
     */
    public abstract File getPosterInputFile();

    /**
     * @return output poster error file. Returns null if no file found.
     */
    public abstract File getPosterErrorOutputFile();

    /**
     * @return input reversal file. Returns null if no file found.
     */
    public abstract File getReversalInputFile();

    /**
     * @return output reversal error file. Returns null if no file found.
     */
    public abstract File getReversalErrorOutputFile();

    /**
     * @return input ICR file. Returns null if no file found.
     */
    public abstract File getICRInputFile();

    /**
     * @return output ICR error file. Returns null if no file found.
     */
    public abstract File getICRErrorOutputFile();

    /**
     * @return input ICR Encumbrance file. Returns null if no file found.
     */
    public abstract File getICREncumbranceInputFile();

    /**
     * @return output ICR Encumbrance error file. Returns null if no file found.
     */
    public abstract File getICREncumbranceErrorOutputFile();

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
     * Gets an OriginEntryInformation for the parsed line. This needs to be handled separately for GL and Labor because Labor is a special case
     * of GL (positionNumber + emplid). Could be done with an OriginEntryHistory interface but in the interest of not mucking with
     * OriginEntries the is done with delegation.
     * @param inputLine line that was read from getPosterInputFilename
     * @param lineNumber line number we are currently reading from getPosterInputFilename
     * @return parsed line into an object as per inputLine parameter
     */
    public abstract OriginEntryInformation getOriginEntry(String inputLine, int lineNumber);

    /**
     * Update the entry history table
     * @param mode of post, e.g. MODE_REVERSAL
     * @param originEntry representing the update details
     */
    public abstract void updateEntryHistory(Integer postMode, OriginEntryInformation originEntry);

    /**
     * Updates the balance history table
     * @param originEntry representing the update details
     */
    public abstract void updateBalanceHistory(Integer postMode, OriginEntryInformation originEntry);

    /**
     * Returns a Balance object for the parameters of the passed in LedgerBalanceHistory. Necessary for generic amount comparision since
     * it may be either labor or gl.
     * @param ledgerBalanceHistory to retrieve the Balance object for
     * @return balance object adhereing to the Balance interface
     */
    public abstract Balance getBalance(LedgerBalanceHistory ledgerBalanceHistory);

    /**
     * In order to avoid file system scans this class caches poster input and poster error filenames. In rare cases they may want to be reset.
     */
    public abstract void clearPosterFileCache();

    /**
     * Removes the data from the History tables.
     */
    public abstract void clearHistories();

    /**
     * Returns filenames used by process. Comma separated
     */
    public abstract String getFilenames();
}
