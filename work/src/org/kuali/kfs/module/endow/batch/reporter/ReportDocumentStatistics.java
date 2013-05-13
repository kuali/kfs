/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.batch.reporter;

public class ReportDocumentStatistics {

    protected int numberOfSourceTransactionLines = 0;
    protected int numberOfTargetTransactionLines = 0;
    
    protected int numberOfDocuments = 0;
    protected int numberOfErrors    = 0;
    
    protected String documentTypeName;
    
    /**
     * Constructs a ReportDocumentStatistics.java.
     */
    public ReportDocumentStatistics(String documentTypeName) {
        this.documentTypeName = documentTypeName;
    }
    
    /**
     * 
     * This returns the total number of source and target transaction lines.
     *
     * @return
     */
    public int getTotalNumberOfTransactionLines() {
        return numberOfSourceTransactionLines + numberOfTargetTransactionLines;
    }
    
    /**
     * 
     * Increments the number of documents that were generated for this
     * by 1.
     *
     */
    public void incrementNumberOfDocuments() {
        this.numberOfDocuments++;
    }
    
    /**
     * 
     * Adds the specified number of source transaction lines for this document.
     *
     * @param numberOfSourceTransactionLines
     */
    public void addNumberOfSourceTransactionLines(int numberOfSourceTransactionLines) {
        this.numberOfSourceTransactionLines += numberOfSourceTransactionLines;
    }
    
    /**
     * 
     * Adds the specified number of target transaction lines for this document.
     *
     * @param numberOfTargetTransactionLines
     */
    public void addNumberOfTargetTransactionLines(int numberOfTargetTransactionLines) {
        this.numberOfTargetTransactionLines += numberOfTargetTransactionLines;
    }
    
    /**
     * 
     * Increments the number of errors for this document by 1.
     *
     */
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
     * Gets the numberOfErrors attribute. 
     * @return Returns the numberOfErrors.
     */
    public int getNumberOfErrors() {
        return numberOfErrors;
    }

    /**
     * Gets the numberOfSourceTransactionLines attribute. 
     * @return Returns the numberOfSourceTransactionLines.
     */
    public int getNumberOfSourceTransactionLines() {
        return numberOfSourceTransactionLines;
    }

    /**
     * Gets the numberOfTargetTransactionLines attribute. 
     * @return Returns the numberOfTargetTransactionLines.
     */
    public int getNumberOfTargetTransactionLines() {
        return numberOfTargetTransactionLines;
    }

    /**
     * Gets the documentTypeName attribute. 
     * @return Returns the documentTypeName.
     */
    public String getDocumentTypeName() {
        return documentTypeName;
    }
    
}
