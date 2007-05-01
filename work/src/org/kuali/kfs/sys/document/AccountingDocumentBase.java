/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.rule.event.ApproveDocumentEvent;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.RouteDocumentEvent;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.AccountingLineParserBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.rule.event.AccountingLineEvent;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.rule.event.DeleteAccountingLineEvent;
import org.kuali.kfs.rule.event.ReviewAccountingLineEvent;
import org.kuali.kfs.rule.event.UpdateAccountingLineEvent;
import org.kuali.kfs.util.SpringServiceLocator;

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
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLines()
     */
    public List getSourceAccountingLines() {
        return this.sourceAccountingLines;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#setSourceAccountingLines(java.util.List)
     */
    public void setSourceAccountingLines(List sourceLines) {
        this.sourceAccountingLines = sourceLines;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLines()
     */
    public List getTargetAccountingLines() {
        return this.targetAccountingLines;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#setTargetAccountingLines(java.util.List)
     */
    public void setTargetAccountingLines(List targetLines) {
        this.targetAccountingLines = targetLines;
    }

    /**
     * This implementation sets the sequence number appropriately for the passed in source accounting line using the value that has
     * been stored in the nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and
     * then handles incrementing the nextSourceLineNumber variable for you.
     * 
     * @see org.kuali.kfs.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
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
     * @see org.kuali.kfs.document.AccountingDocument#addTargetAccountingLine(TargetAccountingLine)
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
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLine(int)
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
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLine(int)
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
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.SOURCE;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TARGET;
    }

    /**
     * Since one side of the document should match the other and the document should balance, the total dollar amount for the
     * document should either be the expense line or the income line. This is the default implementation of this interface method so
     * it should be overridden appropriately if your document cannot make this assumption.
     * @return if target total is zero, source total, otherwise target total
     */
    public KualiDecimal getTotalDollarAmount() {
        return getTargetTotal().equals(new KualiDecimal(0)) ? getSourceTotal() : getTargetTotal();
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getSourceTotal()
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
     * @see org.kuali.kfs.document.AccountingDocument#getTargetTotal()
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
     * @see org.kuali.kfs.document.AccountingDocument#getNextSourceLineNumber()
     */
    public Integer getNextSourceLineNumber() {
        return this.nextSourceLineNumber;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#setNextSourceLineNumber(java.lang.Integer)
     */
    public void setNextSourceLineNumber(Integer nextLineNumber) {
        this.nextSourceLineNumber = nextLineNumber;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#getNextTargetLineNumber()
     */
    public Integer getNextTargetLineNumber() {
        return this.nextTargetLineNumber;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocument#setNextTargetLineNumber(java.lang.Integer)
     */
    public void setNextTargetLineNumber(Integer nextLineNumber) {
        this.nextTargetLineNumber = nextLineNumber;
    }

    /**
     * Returns the default Source accounting line class.
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getSourceAccountingLineClass()
     */
    public Class getSourceAccountingLineClass() {
        return SourceAccountingLine.class;
    }

    /**
     * Returns the default Target accounting line class.
     * 
     * @see org.kuali.kfs.document.AccountingDocument#getTargetAccountingLineClass()
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

    public String getSourceAccountingLineEntryName() {
        return this.getSourceAccountingLineClass().getName();
    }

    public String getTargetAccountingLineEntryName() {
        return this.getTargetAccountingLineClass().getName();
    }

    /**
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        copyAccountingLines(false);
    }

    /**
     * @see org.kuali.kfs.document.GeneralLedgerPostingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();
        copyAccountingLines(true);
    }

    /**
     * Copies accounting lines but sets new document number and version If error correction, reverses line amount.
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

    public void prepareForSave(KualiDocumentEvent event) {
        if (!SpringServiceLocator.getGeneralLedgerPendingEntryService().generateGeneralLedgerPendingEntries(this)) {
            logErrors();
            throw new ValidationException("general ledger GLPE generation failed");
        }
        super.prepareForSave(event);
    }

    @Override
    public List generateSaveEvents() {
        List events = new ArrayList();

        // foreach (source, target)
        // 1. retrieve persisted accountingLines for document
        // 2. retrieve current accountingLines from given document
        // 3. compare, creating add/delete/update events as needed
        // 4. apply rules as appropriate returned events
        List persistedSourceLines = SpringServiceLocator.getAccountingLineService().getByDocumentHeaderId(getSourceAccountingLineClass(), getDocumentNumber());
        List currentSourceLines = getSourceAccountingLines();

        List sourceEvents = generateEvents(persistedSourceLines, currentSourceLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME, this);
        for (Iterator i = sourceEvents.iterator(); i.hasNext();) {
            AccountingLineEvent sourceEvent = (AccountingLineEvent) i.next();
            events.add(sourceEvent);
        }

        List persistedTargetLines = SpringServiceLocator.getAccountingLineService().getByDocumentHeaderId(getTargetAccountingLineClass(), getDocumentNumber());
        List currentTargetLines = getTargetAccountingLines();

        List targetEvents = generateEvents(persistedTargetLines, currentTargetLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME, this);
        for (Iterator i = targetEvents.iterator(); i.hasNext();) {
            AccountingLineEvent targetEvent = (AccountingLineEvent) i.next();
            events.add(targetEvent);
        }

        return events;
    }
    
    /**
     * Generates a List of instances of AccountingLineEvent subclasses, one for each accountingLine in the union of the
     * persistedLines and currentLines lists. Events in the list will be grouped in order by event-type (review, update, add,
     * delete).
     * 
     * @param persistedLines
     * @param currentLines
     * @param errorPathPrefix
     * @param document
     * @return List of AccountingLineEvent subclass instances
     */
    private List generateEvents(List persistedLines, List currentLines, String errorPathPrefix, TransactionalDocument document) {
        List addEvents = new ArrayList();
        List updateEvents = new ArrayList();
        List reviewEvents = new ArrayList();
        List deleteEvents = new ArrayList();

        //
        // generate events
        Map persistedLineMap = buildAccountingLineMap(persistedLines);

        // (iterate through current lines to detect additions and updates, removing affected lines from persistedLineMap as we go
        // so deletions can be detected by looking at whatever remains in persistedLineMap)
        int index = 0;
        for (Iterator i = currentLines.iterator(); i.hasNext(); index++) {
            String indexedErrorPathPrefix = errorPathPrefix + "[" + index + "]";
            AccountingLine currentLine = (AccountingLine) i.next();
            Integer key = currentLine.getSequenceNumber();

            AccountingLine persistedLine = (AccountingLine) persistedLineMap.get(key);
            // if line is both current and persisted...
            if (persistedLine != null) {
                // ...check for updates
                if (!currentLine.isLike(persistedLine)) {
                    UpdateAccountingLineEvent updateEvent = new UpdateAccountingLineEvent(indexedErrorPathPrefix, document, persistedLine, currentLine);
                    updateEvents.add(updateEvent);
                }
                else {
                    ReviewAccountingLineEvent reviewEvent = new ReviewAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                    reviewEvents.add(reviewEvent);
                }

                persistedLineMap.remove(key);
            }
            else {
                // it must be a new addition
                AddAccountingLineEvent addEvent = new AddAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                addEvents.add(addEvent);
            }
        }

        // detect deletions
        for (Iterator i = persistedLineMap.entrySet().iterator(); i.hasNext();) {
            // the deleted line is not displayed on the page, so associate the error with the whole group
            String groupErrorPathPrefix = errorPathPrefix + KFSConstants.ACCOUNTING_LINE_GROUP_SUFFIX;
            Map.Entry e = (Map.Entry) i.next();
            AccountingLine persistedLine = (AccountingLine) e.getValue();
            DeleteAccountingLineEvent deleteEvent = new DeleteAccountingLineEvent(groupErrorPathPrefix, document, persistedLine, true);
            deleteEvents.add(deleteEvent);
        }


        //
        // merge the lists
        List lineEvents = new ArrayList();
        lineEvents.addAll(reviewEvents);
        lineEvents.addAll(updateEvents);
        lineEvents.addAll(addEvents);
        lineEvents.addAll(deleteEvents);

        return lineEvents;
    }

    
    /**
     * @param accountingLines
     * @return Map containing accountingLines from the given List, indexed by their sequenceNumber
     */
    private Map buildAccountingLineMap(List accountingLines) {
        Map lineMap = new HashMap();

        for (Iterator i = accountingLines.iterator(); i.hasNext();) {
            AccountingLine accountingLine = (AccountingLine) i.next();
            Integer sequenceNumber = accountingLine.getSequenceNumber();

            Object oldLine = lineMap.put(sequenceNumber, accountingLine);

            // verify that sequence numbers are unique...
            if (oldLine != null) {
                throw new IllegalStateException("sequence number collision detected for sequence number " + sequenceNumber);
            }
        }

        return lineMap;
    }
}
