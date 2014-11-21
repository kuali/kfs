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

/**
 * A class which encapsulates statistics about a scrubber run
 */
public class ScrubberReportData {
    /**
     * Constructs a ScrubberReportData instance
     */
    public ScrubberReportData() {
    }

    private int numberOfUnscrubbedRecordsRead = 0;
    private int numberOfScrubbedRecordsWritten = 0;
    private int numberOfErrorRecordsWritten = 0;
    private int numberOfOffsetEntriesGenerated = 0;
    private int numberOfCapitalizationEntriesGenerated = 0;
    private int numberOfLiabilityEntriesGenerated = 0;
    private int numberOfPlantIndebtednessEntriesGenerated = 0;
    private int numberOfCostShareEntriesGenerated = 0;
    private int numberOfCostShareEncumbrancesGenerated = 0;
    private int numberOfExpiredAccountsFound = 0;

    /**
     * Adds the values from the parameter report data into this object.
     * 
     * @param anotherReport another set of scrubber report data to add to this scrubber report data
     */
    public void incorporateReportData(ScrubberReportData anotherReport) {
        numberOfUnscrubbedRecordsRead += anotherReport.numberOfUnscrubbedRecordsRead;
        numberOfScrubbedRecordsWritten += anotherReport.numberOfScrubbedRecordsWritten;
        numberOfErrorRecordsWritten += anotherReport.numberOfErrorRecordsWritten;
        numberOfOffsetEntriesGenerated += anotherReport.numberOfOffsetEntriesGenerated;
        numberOfCapitalizationEntriesGenerated += anotherReport.numberOfCapitalizationEntriesGenerated;
        numberOfLiabilityEntriesGenerated += anotherReport.numberOfLiabilityEntriesGenerated;
        numberOfPlantIndebtednessEntriesGenerated += anotherReport.numberOfPlantIndebtednessEntriesGenerated;
        numberOfCostShareEntriesGenerated += anotherReport.numberOfCostShareEntriesGenerated;
        numberOfCostShareEncumbrancesGenerated += anotherReport.numberOfCostShareEncumbrancesGenerated;
        numberOfExpiredAccountsFound += anotherReport.numberOfExpiredAccountsFound;
    }

    /**
     * Increments the error records written count by 1
     */
    public void incrementErrorRecordWritten() {
        numberOfErrorRecordsWritten++;
    }

    /**
     * Increments the expired account found count by 1
     */
    public void incrementExpiredAccountFound() {
        numberOfExpiredAccountsFound++;
    }

    /**
     * Increments the scrubbed records written count by 1
     */
    public void incrementScrubbedRecordWritten() {
        numberOfScrubbedRecordsWritten++;
    }

    /**
     * Increments the offset entry generated count by 1
     */
    public void incrementOffsetEntryGenerated() {
        numberOfOffsetEntriesGenerated++;
    }

    /**
     * Increments the capitalization entry generated count by 1
     */
    public void incrementCapitalizationEntryGenerated() {
        numberOfCapitalizationEntriesGenerated++;
    }

    /**
     * Increments the liability entry generated count by 1
     */
    public void incrementLiabilityEntryGenerated() {
        numberOfLiabilityEntriesGenerated++;
    }

    /**
     * Increments the plant indebtedness entry count by 1
     */
    public void incrementPlantIndebtednessEntryGenerated() {
        numberOfPlantIndebtednessEntriesGenerated++;
    }

    /**
     * Increments the cost share entry generated count by 1
     */
    public void incrementCostShareEntryGenerated() {
        numberOfCostShareEntriesGenerated++;
    }

    /**
     * Increments the cost share encumbranace generated count by 1
     */
    public void incrementCostShareEncumbranceGenerated() {
        numberOfCostShareEncumbrancesGenerated++;
    }

    /**
     * Increments the unscrubbed records read count by 1
     */
    public void incrementUnscrubbedRecordsRead() {
        numberOfUnscrubbedRecordsRead++;
    }

    /**
     * @return Returns the numberOfUnscrubbedRecordsRead.
     */
    public int getNumberOfUnscrubbedRecordsRead() {
        return numberOfUnscrubbedRecordsRead;
    }

    /**
     * @return Returns the numberOfScrubbedRecordsWritten.
     */
    public int getNumberOfScrubbedRecordsWritten() {
        return numberOfScrubbedRecordsWritten;
    }

    /**
     * @return Returns the numberOfErrorRecordsWritten.
     */
    public int getNumberOfErrorRecordsWritten() {
        return numberOfErrorRecordsWritten;
    }

    /**
     * @return Returns the numberOfOffsetEntriesGenerated.
     */
    public int getNumberOfOffsetEntriesGenerated() {
        return numberOfOffsetEntriesGenerated;
    }

    /**
     * @return Returns the numberOfCapitalizationEntriesGenerated.
     */
    public int getNumberOfCapitalizationEntriesGenerated() {
        return numberOfCapitalizationEntriesGenerated;
    }

    /**
     * @return Returns the numberOfLiabilityEntriesGenerated.
     */
    public int getNumberOfLiabilityEntriesGenerated() {
        return numberOfLiabilityEntriesGenerated;
    }

    /**
     * @return Returns the numberOfPlantIndebtednessEntriesGenerated.
     */
    public int getNumberOfPlantIndebtednessEntriesGenerated() {
        return numberOfPlantIndebtednessEntriesGenerated;
    }

    /**
     * @return Returns the numberOfCostShareEntriesGenerated.
     */
    public int getNumberOfCostShareEntriesGenerated() {
        return numberOfCostShareEntriesGenerated;
    }

    /**
     * @return Returns the numberOfCostShareEncumbrancesGenerated.
     */
    public int getNumberOfCostShareEncumbrancesGenerated() {
        return numberOfCostShareEncumbrancesGenerated;
    }

    /**
     * @return Returns the totalNumberOfRecordsWritten.
     */
    public int getTotalNumberOfRecordsWritten() {
        return numberOfScrubbedRecordsWritten + numberOfErrorRecordsWritten + numberOfOffsetEntriesGenerated + numberOfCapitalizationEntriesGenerated + numberOfLiabilityEntriesGenerated + numberOfPlantIndebtednessEntriesGenerated + numberOfCostShareEntriesGenerated + numberOfCostShareEncumbrancesGenerated;
    }

    /**
     * @return Returns the numberOfExpiredAccountsFound.
     */
    public int getNumberOfExpiredAccountsFound() {
        return numberOfExpiredAccountsFound;
    }

}
