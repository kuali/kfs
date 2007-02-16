/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.kfs.document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.AccountingLineParserBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * Base implementation class for financial edocs.
 */
public abstract class AccountingDocumentBase extends GeneralLedgerPostingDocumentBase implements AccountingDocument {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingDocumentBase.class);

    protected Integer nextSourceLineNumber;
    protected Integer nextTargetLineNumber;
    protected List sourceAccountingLines;
    protected List targetAccountingLines;
    
    /**
     * Default constructor.
     */
    public AccountingDocumentBase() {
        super();
        this.nextSourceLineNumber = new Integer(1);
        this.nextTargetLineNumber = new Integer(1);
        setSourceAccountingLines(new ArrayList());
        setTargetAccountingLines(new ArrayList());
    }
    
    /**
     * @see org.kuali.core.document.FinancialDocument#getSourceAccountingLines()
     */
    public List getSourceAccountingLines() {
        return this.sourceAccountingLines;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#setSourceAccountingLines(java.util.List)
     */
    public void setSourceAccountingLines(List sourceLines) {
        this.sourceAccountingLines = sourceLines;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getTargetAccountingLines()
     */
    public List getTargetAccountingLines() {
        return this.targetAccountingLines;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#setTargetAccountingLines(java.util.List)
     */
    public void setTargetAccountingLines(List targetLines) {
        this.targetAccountingLines = targetLines;
    }

    /**
     * This implementation sets the sequence number appropriately for the passed in source accounting line using the value that has
     * been stored in the nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and
     * then handles incrementing the nextSourceLineNumber variable for you.
     * 
     * @see org.kuali.core.document.FinancialDocument#addSourceAccountingLine(org.kuali.core.bo.SourceAccountingLine)
     */
    public void addSourceAccountingLine(SourceAccountingLine line) {
        line.setSequenceNumber(this.getNextSourceLineNumber());
        this.sourceAccountingLines.add(line);
        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);
    }

    /**
     * This implementation sets the sequence number appropriately for the passed in target accounting line using the value that has
     * been stored in the nextTargetLineNumber variable, adds the accounting line to the list that is aggregated by this object, and
     * then handles incrementing the nextTargetLineNumber variable for you.
     * 
     * @see org.kuali.core.document.FinancialDocument#addTargetAccountingLine(org.kuali.core.bo.TargetAccountingLine)
     */
    public void addTargetAccountingLine(TargetAccountingLine line) {
        line.setSequenceNumber(this.getNextTargetLineNumber());
        this.targetAccountingLines.add(line);
        this.nextTargetLineNumber = new Integer(this.getNextTargetLineNumber().intValue() + 1);
    }

    /**
     * This implementation is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects
     * get instantiated within lists. The first three lines are required otherwise when the PojoProcessor tries to automatically
     * inject values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated. So changing the code below will cause adding lines to break
     * if you add more than one item to the list.
     * 
     * @see org.kuali.core.document.FinancialDocument#getSourceAccountingLine(int)
     */
    public SourceAccountingLine getSourceAccountingLine(int index) {
        while (getSourceAccountingLines().size() <= index) {
            try {
                getSourceAccountingLines().add(getSourceAccountingLineClass().newInstance());
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Unable to get class");
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to get class");
            }
        }
        return (SourceAccountingLine) getSourceAccountingLines().get(index);
    }

    /**
     * This implementation is coupled tightly with some underlying issues that the Struts PojoProcessor plugin has with how objects
     * get instantiated within lists. The first three lines are required otherwise when the PojoProcessor tries to automatically
     * inject values into the list, it will get an index out of bounds error if the instance at an index is being called and prior
     * instances at indices before that one are not being instantiated. So changing the code below will cause adding lines to break
     * if you add more than one item to the list.
     * 
     * @see org.kuali.core.document.FinancialDocument#getTargetAccountingLine(int)
     */
    public TargetAccountingLine getTargetAccountingLine(int index) {
        while (getTargetAccountingLines().size() <= index) {
            try {
                getTargetAccountingLines().add(getTargetAccountingLineClass().newInstance());
            }
            catch (InstantiationException e) {
                throw new RuntimeException("Unable to get class");
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("Unable to get class");
            }
        }
        return (TargetAccountingLine) getTargetAccountingLines().get(index);
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.SOURCE;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.TARGET;
    }

    /**
     * Since one side of the document should match the other and the document should balance, the total dollar amount for the
     * document should either be the expense line or the income line. This is the default implementation of this interface method so
     * it should be overridden appropriately if your document cannot make this assumption.
     * 
     * @see org.kuali.core.document.FinancialDocument#getTotalDollarAmount()
     */
    public KualiDecimal getTotalDollarAmount() {
        return getTargetTotal().equals(new KualiDecimal(0)) ? getSourceTotal() : getTargetTotal();
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getSourceTotal()
     */
    public KualiDecimal getSourceTotal() {
        KualiDecimal total = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = getSourceAccountingLines().iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();

            KualiDecimal amount = al.getAmount();
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getTargetTotal()
     */
    public KualiDecimal getTargetTotal() {
        KualiDecimal total = new KualiDecimal(0);
        AccountingLineBase al = null;
        Iterator iter = getTargetAccountingLines().iterator();
        while (iter.hasNext()) {
            al = (AccountingLineBase) iter.next();

            KualiDecimal amount = al.getAmount();
            if (amount != null) {
                total = total.add(amount);
            }
        }
        return total;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getNextSourceLineNumber()
     */
    public Integer getNextSourceLineNumber() {
        return this.nextSourceLineNumber;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#setNextSourceLineNumber(java.lang.Integer)
     */
    public void setNextSourceLineNumber(Integer nextLineNumber) {
        this.nextSourceLineNumber = nextLineNumber;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#getNextTargetLineNumber()
     */
    public Integer getNextTargetLineNumber() {
        return this.nextTargetLineNumber;
    }

    /**
     * @see org.kuali.core.document.FinancialDocument#setNextTargetLineNumber(java.lang.Integer)
     */
    public void setNextTargetLineNumber(Integer nextLineNumber) {
        this.nextTargetLineNumber = nextLineNumber;
    }

    /**
     * Returns the default Source accounting line class.
     * 
     * @see org.kuali.core.document.FinancialDocument#getSourceAccountingLineClass()
     */
    public Class getSourceAccountingLineClass() {
        return SourceAccountingLine.class;
    }

    /**
     * Returns the default Target accounting line class.
     * 
     * @see org.kuali.core.document.FinancialDocument#getTargetAccountingLineClass()
     */
    public Class getTargetAccountingLineClass() {
        return TargetAccountingLine.class;
    }

    /**
     * Used to get the appropriate <code>{@link AccountingLineParser}</code> for the <code>Document</code>
     * 
     * @return AccountingLineParser
     */
    public AccountingLineParser getAccountingLineParser() {
        return new AccountingLineParserBase();
    }
    
    
    /**
     * @see org.kuali.module.gl.document.GeneralLedgerPostingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        copyAccountingLines(false);
    }

    /**
     * @see org.kuali.module.gl.document.GeneralLedgerPostingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        copyAccountingLines(true);
    }
    
    /**
     * Copies accounting lines but sets new document number and version
     * If error correction, reverses line amount.
     */
    protected void copyAccountingLines(boolean isErrorCorrection) {
        if (getSourceAccountingLines() != null) {
            for (Iterator iter = getSourceAccountingLines().iterator(); iter.hasNext();) {
                AccountingLineBase sourceLine = (AccountingLineBase) iter.next();
                sourceLine.setDocumentNumber(getDocumentNumber());
                sourceLine.setVersionNumber(new Long(1));
                if (isErrorCorrection) {
                    sourceLine.setAmount(sourceLine.getAmount().negated());
                }
            }
        }

        if (getTargetAccountingLines() != null) {
            for (Iterator iter = getTargetAccountingLines().iterator(); iter.hasNext();) {
                AccountingLineBase targetLine = (AccountingLineBase) iter.next();
                targetLine.setDocumentNumber(getDocumentNumber());
                targetLine.setVersionNumber(new Long(1));
                if (isErrorCorrection) {
                    targetLine.setAmount(targetLine.getAmount().negated());
                }
            }
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getSourceAccountingLines());
        managedLists.add(getTargetAccountingLines());

        return managedLists;
    }

}
