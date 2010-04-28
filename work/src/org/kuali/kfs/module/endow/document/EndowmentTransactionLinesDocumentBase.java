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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLineParser;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;

public abstract class EndowmentTransactionLinesDocumentBase extends EndowmentTransactionalDocumentBase implements EndowmentTransactionLinesDocument {

    protected Integer nextSourceLineNumber;
    protected Integer nextTargetLineNumber;
    protected List<EndowmentTransactionLine> sourceTransactionLines;
    protected List<EndowmentTransactionLine> targetTransactionLines;

    /**
     * Constructs a EndowmentTransactionLinesDocumentBase.java.
     */
    public EndowmentTransactionLinesDocumentBase() {
        super();
        this.nextSourceLineNumber = new Integer(1);
        this.nextTargetLineNumber = new Integer(1);
        sourceTransactionLines = new TypedArrayList(EndowmentSourceTransactionLine.class);
        targetTransactionLines = new TypedArrayList(EndowmentTargetTransactionLine.class);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourceTransactionLines()
     */
    public List<EndowmentTransactionLine> getSourceTransactionLines() {
        return sourceTransactionLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#setSourceTransactionLines(java.util.List)
     */
    public void setSourceTransactionLines(List<EndowmentTransactionLine> sourceTransactionLines) {
        this.sourceTransactionLines = sourceTransactionLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetTransactionLines()
     */
    public List<EndowmentTransactionLine> getTargetTransactionLines() {
        return targetTransactionLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#setTargetTransactionLines(java.util.List)
     */
    public void setTargetTransactionLines(List<EndowmentTransactionLine> targetTransactionLines) {
        this.targetTransactionLines = targetTransactionLines;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourceTransactionLinesSectionTitle()
     */
    public String getSourceTransactionLinesSectionTitle() {
        return KFSConstants.SOURCE;

    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetTransactionLinesSectionTitle()
     */
    public String getTargetTransactionLinesSectionTitle() {
        return KFSConstants.TARGET;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getNextSourceLineNumber()
     */
    public Integer getNextSourceLineNumber() {
        return nextSourceLineNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#setNextSourceLineNumber(java.lang.Integer)
     */
    public void setNextSourceLineNumber(Integer nextSourceLineNumber) {
        this.nextSourceLineNumber = nextSourceLineNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getNextTargetLineNumber()
     */
    public Integer getNextTargetLineNumber() {
        return nextTargetLineNumber;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#setNextTargetLineNumber(java.lang.Integer)
     */
    public void setNextTargetLineNumber(Integer nextTargetLineNumber) {
        this.nextTargetLineNumber = nextTargetLineNumber;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#addSourceTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentSourceTransactionLine)
     */
    public void addSourceTransactionLine(EndowmentSourceTransactionLine line) {
        line.setTransactionLineNumber(this.getNextSourceLineNumber());
        this.sourceTransactionLines.add(line);
        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);

    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#addTargetTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentTargetTransactionLine)
     */
    public void addTargetTransactionLine(EndowmentTargetTransactionLine line) {
        line.setTransactionLineNumber(this.getNextTargetLineNumber());
        this.targetTransactionLines.add(line);
        this.nextTargetLineNumber = new Integer(this.getNextTargetLineNumber().intValue() + 1);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourceIncomeTotal()
     */
    public KualiDecimal getSourceIncomeTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getSourceTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionAmount());
            }
        }

        return total;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourcePrincipalTotal()
     */
    public KualiDecimal getSourcePrincipalTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getSourceTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionAmount());
            }
        }

        return total;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetIncomeTotal()
     */
    public KualiDecimal getTargetIncomeTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getTargetTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionAmount());
            }
        }

        return total;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetPrincipalTotal()
     */
    public KualiDecimal getTargetPrincipalTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getTargetTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionAmount());
            }
        }

        return total;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourceIncomeTotalUnits()
     */
    public KualiDecimal getSourceIncomeTotalUnits() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getSourceTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionUnits());
            }
        }

        return total;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourcePrincipalTotalUnits()
     */
    public KualiDecimal getSourcePrincipalTotalUnits() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getSourceTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionUnits());
            }
        }

        return total;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetIncomeTotalUnits()
     */
    public KualiDecimal getTargetIncomeTotalUnits() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getTargetTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionUnits());
            }
        }

        return total;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetPrincipalTotalUnits()
     */
    public KualiDecimal getTargetPrincipalTotalUnits() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (EndowmentTransactionLine tl : getTargetTransactionLines()) {
            if (tl.getTransactionAmount() != null && EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(tl.getTransactionIPIndicatorCode())) {
                total = total.add(tl.getTransactionUnits());
            }
        }

        return total;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTransactionLineParser()
     */
    public EndowmentTransactionLineParser getTransactionLineParser() {
        // TODO Auto-generated method stub
        return null;
    }


    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getSourceTransactionLine(int)
     */
    public EndowmentSourceTransactionLine getSourceTransactionLine(int index) {
        if (index >= getSourceTransactionLines().size()) {
            return (EndowmentSourceTransactionLine) getSourceTransactionLines().get(index);
        }
        else
            return null;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.EndowmentTransactionLinesDocument#getTargetTransactionLine(int)
     */
    public EndowmentTargetTransactionLine getTargetTransactionLine(int index) {
        if (index >= getTargetTransactionLines().size()) {
            return (EndowmentTargetTransactionLine) getTargetTransactionLines().get(index);
        }
        else
            return null;
    }

}
