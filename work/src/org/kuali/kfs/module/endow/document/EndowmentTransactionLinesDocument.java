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

import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineParser;
import org.kuali.kfs.module.endow.util.LineParser;
import org.kuali.rice.core.api.util.type.KualiDecimal;


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
     * Compute the total amount for the target transaction lines.
     * 
     * @return the total amount for the target transaction lines
     */
    public KualiDecimal getTargetTotalAmount();

    /**
     * Compute the total amount for the source transaction lines.
     * 
     * @return the total amount for the source transaction lines
     */
    public KualiDecimal getSourceTotalAmount();

    /**
     * Computes the total units for the source transaction lines.
     * 
     * @return the total units for the source transaction lines
     */
    public KualiDecimal getSourceTotalUnits();

    /**
     * Computes the total units for the target transaction lines.
     * 
     * @return the total units for the target transaction lines
     */
    public KualiDecimal getTargetTotalUnits();

    /**
     * Base implementation to compute the document total units. Documents that display the total units will implement the
     * UnitsTotaling interface and can override this method if needed.
     * 
     * @return the total units for the document
     */
    public KualiDecimal getTotalUnits();

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

    /**
     * This method returns the transaction line at a particular spot in the overall list of transaction lines.
     * 
     * @param index
     * @return The source transaction line at the specified index.
     */
    public EndowmentSourceTransactionLine getSourceTransactionLine(int index);

    /**
     * This method retrieves the target transaction line at the specified index.
     * 
     * @param index
     * @return The target transaction line at the passed in index.
     */
    public EndowmentTargetTransactionLine getTargetTransactionLine(int index);

    public Class getTranLineClass(boolean isSource); 
    
    public LineParser getLineParser();
}
