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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineParser;
import org.kuali.rice.kns.util.KualiDecimal;


public interface EndowmentTransactionLinesDocument extends EndowmentTransactionalDocument {

    /**
     * This method is used to return the title that a transactional document should give to it's source transaction line section.
     * 
     * @return The source transaction line section's title.
     */
    public String getSourceTransactionLinesSectionTitle();

    /**
     * This method is used to return the title that a transactional document should give to it's target transaction line section.
     * 
     * @return The target transaction line section's title.
     */
    public String getTargetTransactionLinesSectionTitle();

    /**
     * Sums up the amounts of all of the target transaction lines for income.
     */
    public KualiDecimal getTargetIncomeTotal();

    /**
     * Sums up the amounts of all of the target transaction lines for principal.
     */
    public KualiDecimal getTargetPrincipalTotal();

    /**
     * Sums up the amounts of all of the source transaction lines for income.
     */
    public KualiDecimal getSourceIncomeTotal();

    /**
     * Sums up the amounts of all of the source transaction lines for principal.
     */
    public KualiDecimal getSourcePrincipalTotal();

    /**
     * Sums up the units of all the source transaction lines for income.
     * 
     * @return the total income units
     */
    public KualiDecimal getSourceIncomeTotalUnits();

    /**
     * Sums up the units of all source transaction lines for principal.
     * 
     * @return the total principal units
     */
    public KualiDecimal getSourcePrincipalTotalUnits();

    /**
     * Sums up the units of all the target transaction lines for income.
     * 
     * @return the total income units
     */
    public KualiDecimal getTargetIncomeTotalUnits();

    /**
     * Sums up the units of all source transaction lines for principal.
     * 
     * @return the total principal units
     */
    public KualiDecimal getTargetPrincipalTotalUnits();

    /**
     * @return TransactionLineParser instance appropriate for importing TransactionLines for this document type
     */
    public EndowmentTransactionLineParser getTransactionLineParser();


    /**
     * Retrieves the next line sequence number for a transaction line in the Source transaction line section on an endowment
     * transactional document.
     * 
     * @return The next available source line number.
     */
    public Integer getNextSourceLineNumber();

    /**
     * @param nextLineNumber
     */
    public void setNextSourceLineNumber(Integer nextLineNumber);

    /**
     * Retrieves the next line sequence number for an transaction line in the Target transaction line section on an endowment
     * transactional document.
     * 
     * @return The next available target line number.
     */
    public Integer getNextTargetLineNumber();

    /**
     * @param nextLineNumber
     */
    public void setNextTargetLineNumber(Integer nextLineNumber);

    /**
     * This method adds a source transaction line.
     * 
     * @param line
     */
    public void addSourceTransactionLine(EndowmentSourceTransactionLine line);

    /**
     * This method returns a list of source transaction lines.
     * 
     * @return The list of source transaction lines.
     */
    public List<EndowmentTransactionLine> getSourceTransactionLines();

    /**
     * This method sets the list of source transaction lines for this document.
     * 
     * @param sourceLines
     */
    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceLines);

    /**
     * This method adds a target transaction line to the document.
     * 
     * @param line
     */
    public void addTargetTransactionLine(EndowmentTargetTransactionLine line);

    /**
     * This method retrieves all of the target transaction lines associated with this document.
     */
    public List<EndowmentTransactionLine> getTargetTransactionLines();

    /**
     * This method sets the list of target transaction lines for this document.
     * 
     * @param targetLines
     */
    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetLines);


}
