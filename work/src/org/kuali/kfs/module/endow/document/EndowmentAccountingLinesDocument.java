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
package org.kuali.kfs.module.endow.document;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLineParser;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;

public interface EndowmentAccountingLinesDocument extends EndowmentTransactionLinesDocument {

    /**
     * Gets the nextSourceAccountingLineNumber.
     * 
     * @return nextSourceAccountingLineNumber
     */
    public Integer getNextSourceAccountingLineNumber();

    /**
     * Sets the nextSourceAccountingLineNumber.
     * 
     * @param nextSourceAccountingLineNumber
     */
    public void setNextSourceAccountingLineNumber(Integer nextSourceAccountingLineNumber);

    /**
     * Gets the nextTargetAccountingLineNumber.
     * 
     * @return nextTargetAccountingLineNumber
     */
    public Integer getNextTargetAccountingLineNumber();

    /**
     * Sets the nextTargetAccountingLineNumber.
     * 
     * @param nextTargetAccountingLineNumber
     */
    public void setNextTargetAccountingLineNumber(Integer nextTargetAccountingLineNumber);

    /**
     * Gets the sourceAccountingLines.
     * 
     * @return sourceAccountingLines
     */
    public List<SourceEndowmentAccountingLine> getSourceAccountingLines();

    /**
     * Sets the sourceAccountingLines.
     * 
     * @param sourceAccountingLines
     */
    public void setSourceAccountingLines(List<SourceEndowmentAccountingLine> sourceAccountingLines);

    /**
     * Gets the targetAccountingLines.
     * 
     * @return targetAccountingLines
     */
    public List<TargetEndowmentAccountingLine> getTargetAccountingLines();

    /**
     * Sets the targetAccountingLines.
     * 
     * @param targetAccountingLines
     */
    public void setTargetAccountingLines(List<TargetEndowmentAccountingLine> targetAccountingLines);


    /**
     * Adds a new Source accounting line.
     * 
     * @param line
     */
    public void addSourceAccountingLine(SourceEndowmentAccountingLine line);


    /**
     * Adds a new Target Accounting line.
     * 
     * @param line
     */
    public void addTargetAccountingLine(TargetEndowmentAccountingLine line);

    /**
     * This method returns the accounting line at a particular spot in the overall list of accounting lines.
     * 
     * @param index
     * @return The source accounting line at the specified index.
     */
    public SourceEndowmentAccountingLine getSourceAccountingLine(int index);

    /**
     * This method retrieves the target accounting line at the specified index.
     * 
     * @param index
     * @return The target accounting line at the passed in index.
     */
    public TargetEndowmentAccountingLine getTargetAccountingLine(int index);

    /**
     * @return EndowmentAccountingLineParser instance appropriate for importing AccountingLines for this document type
     */
    public EndowmentAccountingLineParser getEndowmentAccountingLineParser();

    /*
     * @return Class of the document's source accounting lines
     */
    public Class getSourceAccountingLineClass();


    /*
     * @return Class of the document's target accounting lines
     */
    public Class getTargetAccountingLineClass();

}
