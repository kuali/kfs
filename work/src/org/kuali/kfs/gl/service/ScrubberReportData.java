/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl.scrubber;

public class ScrubberReportData {
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
     * @param anotherReport
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
        numberOfExpiredAccountsFound += numberOfExpiredAccountsFound;
    }

    public void incrementErrorRecordWritten() {
        numberOfErrorRecordsWritten++;
    }

    public void incrementExpiredAccountFound() {
        numberOfExpiredAccountsFound++;
    }

    public void incrementScrubbedRecordWritten() {
        numberOfScrubbedRecordsWritten++;
    }

    public void incrementOffsetEntryGenerated() {
        numberOfOffsetEntriesGenerated++;
    }

    public void incrementCapitalizationEntryGenerated() {
        numberOfCapitalizationEntriesGenerated++;
    }

    public void incrementLiabilityEntryGenerated() {
        numberOfLiabilityEntriesGenerated++;
    }

    public void incrementPlantIndebtednessEntryGenerated() {
        numberOfPlantIndebtednessEntriesGenerated++;
    }

    public void incrementCostShareEntryGenerated() {
        numberOfCostShareEntriesGenerated++;
    }

    public void incrementCostShareEncumbranceGenerated() {
        numberOfCostShareEncumbrancesGenerated++;
    }

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