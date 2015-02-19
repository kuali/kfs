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
package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Purap Accounting Line Interface.
 */
public interface PurApAccountingLine extends AccountingLine {

    public abstract Integer getAccountIdentifier();

    public abstract void setAccountIdentifier(Integer accountIdentifier);

    public abstract Integer getItemIdentifier();

    public abstract void setItemIdentifier(Integer itemIdentifier);

    public abstract BigDecimal getAccountLinePercent();

    public abstract void setAccountLinePercent(BigDecimal accountLinePercent);

    public abstract Integer getSequenceNumber();

    public abstract void setSequenceNumber(Integer sequenceNumber);
    
    public abstract Integer getPurApSequenceNumber();
    
    /**
     * Determines if the current purap accounting line is in an empty state.
     * 
     * @return boolean - true if empty state
     */
    public abstract boolean isEmpty();

    /**
     * Creates a copy of the current purap accounting line and sets the percentage and the amount to zero.
     * 
     * @return - purap accounting line copy with blank percent and amount
     */
    public abstract PurApAccountingLine createBlankAmountsCopy();

    /**
     * Compares the current accounting line values with a source accounting line to see if both accounting lines are equal.
     * 
     * @param accountingLine - accounting line to compare
     * @return boolean - true if passed in and current accounting line are equal, false otherwise
     */
    public abstract boolean accountStringsAreEqual(SourceAccountingLine accountingLine);

    /**
     * Compares the current accounting line values with a purap accounting line to see if both accounting lines are equal.
     * 
     * @param accountingLine - accounting line to compare
     * @return boolean - true if passed in and current accounting line are equal, false otherwise
     */
    public abstract boolean accountStringsAreEqual(PurApAccountingLine accountingLine);

    /**
     * Creates a source accounting line from the current purap accounting line.
     * 
     * @return - source accounting line based on current purap accounting line
     */
    public abstract SourceAccountingLine generateSourceAccountingLine();

    public KualiDecimal getAlternateAmountForGLEntryCreation();

    public void setAlternateAmountForGLEntryCreation(KualiDecimal alternateAmountForGLEntryCreation);

    public <T extends PurApItem> T getPurapItem();
   
    public void setPurapItem(PurApItem item);
    
    public String getString();

    public String getPostingPeriodCode();

    public void setPostingPeriodCode(String postingPeriodCode);
    
}
