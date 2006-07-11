/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl.scrubber;

public class ScrubberReportData {
    public ScrubberReportData() {
    }

    int numberOfUnscrubbedRecordsRead = 0;
    int numberOfScrubbedRecordsWritten = 0;
    int numberOfErrorRecordsWritten = 0;
    int numberOfOffsetEntriesGenerated = 0;
    int numberOfCapitalizationEntriesGenerated = 0;
    int numberOfLiabilityEntriesGenerated = 0;
    int numberOfPlantIndebtednessEntriesGenerated = 0;
    int numberOfCostShareEntriesGenerated = 0;
    int numberOfCostShareEncumbrancesGenerated = 0;
    int numberOfExpiredAccountsFound = 0;

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