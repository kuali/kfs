/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.reporter;

public class ReportStatistics {

    protected int numberOfDocuments        = 0;
    protected int numberOfTransactionLines = 0;
    protected int numberOfErrors           = 0;
    
    public void incrementNumberOfDocuments() {
        this.numberOfDocuments++;
    }
    
    public void addNumberOfTransactionLines(int numberOfTransactionLines) {
        this.numberOfTransactionLines += numberOfTransactionLines;
    }
    
    public void incrementNumberOfErrors() {
        this.numberOfErrors++;
    }

    /**
     * Gets the numberOfDocuments attribute. 
     * @return Returns the numberOfDocuments.
     */
    public int getNumberOfDocuments() {
        return numberOfDocuments;
    }

    /**
     * Sets the numberOfDocuments attribute value.
     * @param numberOfDocuments The numberOfDocuments to set.
     */
    public void setNumberOfDocuments(int numberOfDocuments) {
        this.numberOfDocuments = numberOfDocuments;
    }

    /**
     * Gets the numberOfTransactionLines attribute. 
     * @return Returns the numberOfTransactionLines.
     */
    public int getNumberOfTransactionLines() {
        return numberOfTransactionLines;
    }

    /**
     * Sets the numberOfTransactionLines attribute value.
     * @param numberOfTransactionLines The numberOfTransactionLines to set.
     */
    public void setNumberOfTransactionLines(int numberOfTransactionLines) {
        this.numberOfTransactionLines = numberOfTransactionLines;
    }

    /**
     * Gets the numberOfErrors attribute. 
     * @return Returns the numberOfErrors.
     */
    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    /**
     * Sets the numberOfErrors attribute value.
     * @param numberOfErrors The numberOfErrors to set.
     */
    public void setNumberOfErrors(int numberOfErrors) {
        this.numberOfErrors = numberOfErrors;
    }
    
}
