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
package org.kuali.kfs.module.endow.document;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.EndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;

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
    public List<EndowmentAccountingLine> getSourceAccountingLines();

    /**
     * Sets the sourceAccountingLines.
     * 
     * @param sourceAccountingLines
     */
    public void setSourceAccountingLines(List<EndowmentAccountingLine> sourceAccountingLines);

    /**
     * Gets the targetAccountingLines.
     * 
     * @return targetAccountingLines
     */
    public List<EndowmentAccountingLine> getTargetAccountingLines();

    /**
     * Sets the targetAccountingLines.
     * 
     * @param targetAccountingLines
     */
    public void setTargetAccountingLines(List<EndowmentAccountingLine> targetAccountingLines);


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

}
