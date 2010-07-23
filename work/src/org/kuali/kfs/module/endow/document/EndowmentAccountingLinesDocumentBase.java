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
import org.kuali.kfs.module.endow.businessobject.SourceEndowmentAccountingLine;
import org.kuali.kfs.module.endow.businessobject.TargetEndowmentAccountingLine;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Provides a base class for the Endowment Transfer of Funds documents.
 */
public abstract class EndowmentAccountingLinesDocumentBase extends EndowmentSecurityDetailsDocumentBase implements EndowmentAccountingLinesDocument {
    protected Integer nextSourceAccountingLineNumber;
    protected Integer nextTargetAccountingLineNumber;
    protected List<EndowmentAccountingLine> sourceAccountingLines;
    protected List<EndowmentAccountingLine> targetAccountingLines;

    /**
     * Constructs a EndowmentTransferOfFundsDocument.
     */
    public EndowmentAccountingLinesDocumentBase() {
        super();
        this.nextSourceAccountingLineNumber = new Integer(1);
        this.nextTargetAccountingLineNumber = new Integer(1);
        sourceAccountingLines = new TypedArrayList(SourceEndowmentAccountingLine.class);
        targetAccountingLines = new TypedArrayList(TargetEndowmentAccountingLine.class);
    }


    /**
     * Gets the nextSourceAccountingLineNumber.
     * 
     * @return nextSourceAccountingLineNumber
     */
    public Integer getNextSourceAccountingLineNumber() {
        return nextSourceAccountingLineNumber;
    }

    /**
     * Sets the nextSourceAccountingLineNumber.
     * 
     * @param nextSourceAccountingLineNumber
     */
    public void setNextSourceAccountingLineNumber(Integer nextSourceAccountingLineNumber) {
        this.nextSourceAccountingLineNumber = nextSourceAccountingLineNumber;
    }

    /**
     * Gets the nextTargetAccountingLineNumber.
     * 
     * @return nextTargetAccountingLineNumber
     */
    public Integer getNextTargetAccountingLineNumber() {
        return nextTargetAccountingLineNumber;
    }

    /**
     * Sets the nextTargetAccountingLineNumber.
     * 
     * @param nextTargetAccountingLineNumber
     */
    public void setNextTargetAccountingLineNumber(Integer nextTargetAccountingLineNumber) {
        this.nextTargetAccountingLineNumber = nextTargetAccountingLineNumber;
    }

    /**
     * Gets the sourceAccountingLines.
     * 
     * @return sourceAccountingLines
     */
    public List<EndowmentAccountingLine> getSourceAccountingLines() {
        return sourceAccountingLines;
    }

    /**
     * Sets the sourceAccountingLines.
     * 
     * @param sourceAccountingLines
     */
    public void setSourceAccountingLines(List<EndowmentAccountingLine> sourceAccountingLines) {
        this.sourceAccountingLines = sourceAccountingLines;
    }

    /**
     * Gets the targetAccountingLines.
     * 
     * @return targetAccountingLines
     */
    public List<EndowmentAccountingLine> getTargetAccountingLines() {
        return targetAccountingLines;
    }

    /**
     * Sets the targetAccountingLines.
     * 
     * @param targetAccountingLines
     */
    public void setTargetAccountingLines(List<EndowmentAccountingLine> targetAccountingLines) {
        this.targetAccountingLines = targetAccountingLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#addSourceTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine)
     */
    public void addSourceAccountingLine(SourceEndowmentAccountingLine line) {
        line.setAccountingLineNumber(this.getNextSourceAccountingLineNumber());
        this.sourceAccountingLines.add(line);
        this.nextSourceAccountingLineNumber = new Integer(this.getNextSourceAccountingLineNumber().intValue() + 1);

    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#addTargetTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine)
     */
    public void addTargetAccountingLine(TargetEndowmentAccountingLine line) {
        line.setAccountingLineNumber(this.getNextTargetAccountingLineNumber());
        this.targetAccountingLines.add(line);
        this.nextTargetAccountingLineNumber = new Integer(this.getNextTargetAccountingLineNumber().intValue() + 1);
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument#getSourceAccountingLine(int)
     */
    public SourceEndowmentAccountingLine getSourceAccountingLine(int index) {
        if (index >= getSourceTransactionLines().size()) {
            return (SourceEndowmentAccountingLine) getSourceAccountingLines().get(index);
        }
        else
            return null;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentAccountingLinesDocument#getTargetAccountingLine(int)
     */
    public TargetEndowmentAccountingLine getTargetAccountingLine(int index) {
        if (index >= getTargetTransactionLines().size()) {
            return (TargetEndowmentAccountingLine) getTargetAccountingLines().get(index);
        }
        else
            return null;
    }

    /**
     * Gets the total amount of the accounting lines on this document.
     * 
     * @return total amount of the accounting lines
     */
    public KualiDecimal getTotalAccountingLinesAmount() {

        KualiDecimal totalAmount = KualiDecimal.ZERO;

        if (sourceAccountingLines.size() > 0) {
            for (EndowmentAccountingLine accountingLine : sourceAccountingLines) {
                if (accountingLine.getAmount() != null) {
                    totalAmount = totalAmount.add(accountingLine.getAmount());
                }
            }
        }
        else if (targetAccountingLines.size() > 0) {
            for (EndowmentAccountingLine accountingLine : targetAccountingLines) {
                if (accountingLine.getAmount() != null) {
                    totalAmount = totalAmount.add(accountingLine.getAmount());
                }
            }
        }

        return totalAmount;
    }


    /**
     * @see org.kuali.rice.kns.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    public List buildListOfDeletionAwareLists() {
        List managedList = super.buildListOfDeletionAwareLists();

        managedList.add(getTargetAccountingLines());
        managedList.add(getSourceAccountingLines());

        return managedList;
    }
}
