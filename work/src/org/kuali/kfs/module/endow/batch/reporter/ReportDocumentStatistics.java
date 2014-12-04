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
