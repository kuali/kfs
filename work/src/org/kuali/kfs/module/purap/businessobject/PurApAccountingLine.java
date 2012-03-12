/*
 * Copyright 2007 The Kuali Foundation
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
