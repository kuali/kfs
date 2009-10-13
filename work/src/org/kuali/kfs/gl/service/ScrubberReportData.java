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
