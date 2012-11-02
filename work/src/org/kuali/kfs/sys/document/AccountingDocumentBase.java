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
package org.kuali.kfs.sys.document;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.AccountingLineParserBase;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.sys.document.validation.event.AccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.ReviewAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.kfs.sys.service.AccountingLineService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.TransactionalDocument;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Base implementation class for financial edocs.
 */
public abstract class AccountingDocumentBase extends GeneralLedgerPostingDocumentBase implements AccountingDocument, GeneralLedgerPendingEntrySource {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountingDocumentBase.class);

    protected Integer nextSourceLineNumber;
    protected Integer nextTargetLineNumber;
    protected List sourceAccountingLines;
    protected List targetAccountingLines;

    protected transient FinancialSystemTransactionalDocumentEntry dataDictionaryEntry;
    protected transient Class sourceAccountingLineClass;
    protected transient Class targetAccountingLineClass;

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
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLines()
     */
    @Override
    public List getSourceAccountingLines() {
        return this.sourceAccountingLines;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#setSourceAccountingLines(java.util.List)
     */
    @Override
    public void setSourceAccountingLines(List sourceLines) {
        this.sourceAccountingLines = sourceLines;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLines()
     */
    @Override
    public List getTargetAccountingLines() {
        return this.targetAccountingLines;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#setTargetAccountingLines(java.util.List)
     */
    @Override
    public void setTargetAccountingLines(List targetLines) {
        this.targetAccountingLines = targetLines;
    }

    /**
     * This implementation sets the sequence number appropriately for the passed in source accounting line using the value that has
     * been stored in the nextSourceLineNumber variable, adds the accounting line to the list that is aggregated by this object, and
     * then handles incrementing the nextSourceLineNumber variable for you.
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#addSourceAccountingLine(SourceAccountingLine)
     */
    @Override
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
     * @see org.kuali.kfs.sys.document.AccountingDocument#addTargetAccountingLine(TargetAccountingLine)
     */
    @Override
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
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLine(int)
     */
    @Override
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
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLine(int)
     */
    @Override
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
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.SOURCE;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.TARGET;
    }

    /**
     * Since one side of the document should match the other and the document should balance, the total dollar amount for the
     * document should either be the expense line or the income line. This is the default implementation of this interface method so
     * it should be overridden appropriately if your document cannot make this assumption.
     *
     * @return if target total is zero, source total, otherwise target total
     */
    public KualiDecimal getTotalDollarAmount() {
        return getTargetTotal().equals(KualiDecimal.ZERO) ? getSourceTotal() : getTargetTotal();
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceTotal()
     */
    @Override
    public KualiDecimal getSourceTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
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
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetTotal()
     */
    @Override
    public KualiDecimal getTargetTotal() {
        KualiDecimal total = KualiDecimal.ZERO;
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
     * @see org.kuali.kfs.sys.document.AccountingDocument#getNextSourceLineNumber()
     */
    @Override
    public Integer getNextSourceLineNumber() {
        return this.nextSourceLineNumber;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#setNextSourceLineNumber(java.lang.Integer)
     */
    @Override
    public void setNextSourceLineNumber(Integer nextLineNumber) {
        this.nextSourceLineNumber = nextLineNumber;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#getNextTargetLineNumber()
     */
    @Override
    public Integer getNextTargetLineNumber() {
        return this.nextTargetLineNumber;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocument#setNextTargetLineNumber(java.lang.Integer)
     */
    @Override
    public void setNextTargetLineNumber(Integer nextLineNumber) {
        this.nextTargetLineNumber = nextLineNumber;
    }

    /**
     * Returns the default Source accounting line class.
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        if (sourceAccountingLineClass == null) {
            sourceAccountingLineClass = (getDataDictionaryEntry().getAccountingLineGroups() != null && getDataDictionaryEntry().getAccountingLineGroups().containsKey("source") && getDataDictionaryEntry().getAccountingLineGroups().get("source").getAccountingLineClass() != null) ? getDataDictionaryEntry().getAccountingLineGroups().get("source").getAccountingLineClass() : SourceAccountingLine.class;
        }
        return sourceAccountingLineClass;
    }

    /**
     * Returns the default Target accounting line class.
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        if (targetAccountingLineClass == null) {
            targetAccountingLineClass = (getDataDictionaryEntry().getAccountingLineGroups() != null && getDataDictionaryEntry().getAccountingLineGroups().containsKey("target") && getDataDictionaryEntry().getAccountingLineGroups().get("target").getAccountingLineClass() != null) ? getDataDictionaryEntry().getAccountingLineGroups().get("target").getAccountingLineClass() : TargetAccountingLine.class;
        }
        return targetAccountingLineClass;
    }

    /**
     * Used to get the appropriate <code>{@link AccountingLineParser}</code> for the <code>Document</code>
     *
     * @return AccountingLineParser
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        try {
            if (getDataDictionaryEntry().getImportedLineParserClass() != null) {
                return getDataDictionaryEntry().getImportedLineParserClass().newInstance();
            }
        }
        catch (InstantiationException ie) {
            throw new IllegalStateException("Accounting Line Parser class " + getDataDictionaryEntry().getImportedLineParserClass().getName() + " cannot be instantiated", ie);
        }
        catch (IllegalAccessException iae) {
            throw new IllegalStateException("Illegal Access Exception while attempting to instantiate Accounting Line Parser class " + getDataDictionaryEntry().getImportedLineParserClass().getName(), iae);
        }
        return new AccountingLineParserBase();
    }

    /**
     * @return the data dictionary entry for this document
     */
    public FinancialSystemTransactionalDocumentEntry getDataDictionaryEntry() {
        if (dataDictionaryEntry == null) {
            dataDictionaryEntry = (FinancialSystemTransactionalDocumentEntry) SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(SpringContext.getBean(DataDictionaryService.class).getValidDocumentTypeNameByClass(getClass()));
        }
        return dataDictionaryEntry;
    }

    @Override
    public String getSourceAccountingLineEntryName() {
        return this.getSourceAccountingLineClass().getName();
    }

    @Override
    public String getTargetAccountingLineEntryName() {
        return this.getTargetAccountingLineClass().getName();
    }

    @Override
    public List<GeneralLedgerPendingEntrySourceDetail> getGeneralLedgerPendingEntrySourceDetails() {
        List<GeneralLedgerPendingEntrySourceDetail> accountingLines = new ArrayList<GeneralLedgerPendingEntrySourceDetail>();
        if (getSourceAccountingLines() != null) {
            Iterator iter = getSourceAccountingLines().iterator();
            while (iter.hasNext()) {
                accountingLines.add((GeneralLedgerPendingEntrySourceDetail) iter.next());
            }
        }
        if (getTargetAccountingLines() != null) {
            Iterator iter = getTargetAccountingLines().iterator();
            while (iter.hasNext()) {
                accountingLines.add((GeneralLedgerPendingEntrySourceDetail) iter.next());
            }
        }
        return accountingLines;
    }

    public void customizeExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry) {
    }

    public boolean customizeOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySourceDetail accountingLine, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#toCopy()
     */
    @Override
    public void toCopy() throws WorkflowException {
        super.toCopy();
        copyAccountingLines(false);
        updatePostingYearForAccountingLines(getSourceAccountingLines());
        updatePostingYearForAccountingLines(getTargetAccountingLines());
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#toErrorCorrection()
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
     * Updates the posting year on accounting lines to be the current posting year
     *
     * @param lines a List of accounting lines to update
     */
    protected void updatePostingYearForAccountingLines(List<AccountingLine> lines) {
        if (lines != null) {
            for (AccountingLine line : lines) {
                if (!line.getPostingYear().equals(getPostingYear())) {
                    line.setPostingYear(getPostingYear());
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getSourceAccountingLines());
        managedLists.add(getTargetAccountingLines());

        return managedLists;
    }

    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        if (!(event instanceof AccountingDocumentSaveWithNoLedgerEntryGenerationEvent)) { // only generate entries if the rule event
                                                                                          // specifically allows us to
            if (!SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(this)) {
                logErrors();
                throw new ValidationException("general ledger GLPE generation failed");
            }
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
        List persistedSourceLines = getPersistedSourceAccountingLinesForComparison();
        List currentSourceLines = getSourceAccountingLinesForComparison();

        List sourceEvents = generateEvents(persistedSourceLines, currentSourceLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.SOURCE_ACCOUNTING_LINE_ERRORS, this);
        for (Iterator i = sourceEvents.iterator(); i.hasNext();) {
            AccountingLineEvent sourceEvent = (AccountingLineEvent) i.next();
            events.add(sourceEvent);
        }

        List persistedTargetLines = getPersistedTargetAccountingLinesForComparison();
        List currentTargetLines = getTargetAccountingLinesForComparison();

        List targetEvents = generateEvents(persistedTargetLines, currentTargetLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.TARGET_ACCOUNTING_LINE_ERRORS, this);
        for (Iterator i = targetEvents.iterator(); i.hasNext();) {
            AccountingLineEvent targetEvent = (AccountingLineEvent) i.next();
            events.add(targetEvent);
        }

        return events;
    }

    /**
     * This method gets the Target Accounting Lines that will be used in comparisons
     *
     * @return
     */
    protected List getTargetAccountingLinesForComparison() {
        return getTargetAccountingLines();
    }

    /**
     * This method gets the Persisted Target Accounting Lines that will be used in comparisons
     *
     * @return
     */
    protected List getPersistedTargetAccountingLinesForComparison() {
        return SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(getTargetAccountingLineClass(), getDocumentNumber());
    }

    /**
     * This method gets the Source Accounting Lines that will be used in comparisons
     *
     * @return
     */
    protected List getSourceAccountingLinesForComparison() {
        return getSourceAccountingLines();
    }

    /**
     * This method gets the Persisted Source Accounting Lines that will be used in comparisons
     *
     * @return
     */
    protected List getPersistedSourceAccountingLinesForComparison() {
        return SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(getSourceAccountingLineClass(), getDocumentNumber());
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
    protected List generateEvents(List persistedLines, List currentLines, String errorPathPrefix, TransactionalDocument document) {
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
    protected Map buildAccountingLineMap(List accountingLines) {
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

    /**
     * Perform business rules common to all transactional documents when generating general ledger pending entries.
     *
     * @see org.kuali.rice.krad.rule.GenerateGeneralLedgerPendingEntriesRule#processGenerateGeneralLedgerPendingEntries(org.kuali.rice.krad.document.AccountingDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - start");

        // handle the explicit entry
        // create a reference to the explicitEntry to be populated, so we can pass to the offset method later
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        processExplicitGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry);

        // increment the sequence counter
        sequenceHelper.increment();

        // handle the offset entry
        GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
        boolean success = processOffsetGeneralLedgerPendingEntry(sequenceHelper, glpeSourceDetail, explicitEntry, offsetEntry);

        LOG.debug("processGenerateGeneralLedgerPendingEntries(AccountingDocument, AccountingLine, GeneralLedgerPendingEntrySequenceHelper) - end");
        return success;
    }

    /**
     * This method processes all necessary information to build an explicit general ledger entry, and then adds that to the
     * document.
     *
     * @param accountingDocument
     * @param sequenceHelper
     * @param accountingLine
     * @param explicitEntry
     * @return boolean True if the explicit entry generation was successful, false otherwise.
     */
    protected void processExplicitGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntry explicitEntry) {
        LOG.debug("processExplicitGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) - start");

        // populate the explicit entry
        SpringContext.getBean(GeneralLedgerPendingEntryService.class).populateExplicitGeneralLedgerPendingEntry(this, glpeSourceDetail, sequenceHelper, explicitEntry);

        // hook for children documents to implement document specific GLPE field mappings
        customizeExplicitGeneralLedgerPendingEntry(glpeSourceDetail, explicitEntry);

        addPendingEntry(explicitEntry);

        LOG.debug("processExplicitGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry) - end");
    }

    /**
     * This method processes an accounting line's information to build an offset entry, and then adds that to the document.
     *
     * @param accountingDocument
     * @param sequenceHelper
     * @param accountingLine
     * @param explicitEntry
     * @param offsetEntry
     * @return boolean True if the offset generation is successful.
     */
    protected boolean processOffsetGeneralLedgerPendingEntry(GeneralLedgerPendingEntrySequenceHelper sequenceHelper, GeneralLedgerPendingEntrySourceDetail postable, GeneralLedgerPendingEntry explicitEntry, GeneralLedgerPendingEntry offsetEntry) {
        LOG.debug("processOffsetGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - start");

        // populate the offset entry
        boolean success = SpringContext.getBean(GeneralLedgerPendingEntryService.class).populateOffsetGeneralLedgerPendingEntry(getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);

        // hook for children documents to implement document specific field mappings for the GLPE
        success &= customizeOffsetGeneralLedgerPendingEntry(postable, explicitEntry, offsetEntry);

        addPendingEntry(offsetEntry);

        LOG.debug("processOffsetGeneralLedgerPendingEntry(AccountingDocument, GeneralLedgerPendingEntrySequenceHelper, AccountingLine, GeneralLedgerPendingEntry, GeneralLedgerPendingEntry) - end");
        return success;
    }

    /**
     * Returns one of the two given String's; if the preferred String is not null and has a length > 0, then it is returned,
     * otherwise the second String is returned
     *
     * @param preferredString the String you're hoping isn't blank so you can get it back
     * @param secondaryString the "rebound" String, which you'll end up with if the preferred String is blank
     * @return one of the String's
     */
    protected String getEntryValue(String preferredString, String secondaryString) {
        return (StringUtils.isNotBlank(preferredString) ? preferredString : secondaryString);
    }

    /**
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#isDebit(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail)
     */
    @Override
    public abstract boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable);

    /**
     * Most accounting documents don't need to generate document level GLPE's, so don't do anything in the default implementation
     *
     * @see org.kuali.kfs.document.GeneralLedgerPostingHelper#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     * @return always true, because we've always successfully not generating anything
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        return true;
    }

    /**
     * GLPE amounts are ALWAYS positive, so just take the absolute value of the accounting line's amount.
     *
     * @param accountingLine
     * @return KualiDecimal The amount that will be used to populate the GLPE.
     */
    @Override
    public KualiDecimal getGeneralLedgerPendingEntryAmountForDetail(GeneralLedgerPendingEntrySourceDetail postable) {
        LOG.debug("getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine) - start");

        KualiDecimal returnKualiDecimal = postable.getAmount().abs();
        LOG.debug("getGeneralLedgerPendingEntryAmountForAccountingLine(AccountingLine) - end");
        return returnKualiDecimal;
    }

    @Override
    public Class<? extends AccountingDocument> getDocumentClassForAccountingLineValueAllowedValidation() {
        return this.getClass();
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.AccountingDocument#isDocumentFinalOrProcessed()
     */
    @Override
    public boolean isDocumentFinalOrProcessed() {
        boolean isDocumentFinalOrProcessed = false;
        if(ObjectUtils.isNotNull(getDocumentHeader().getDocumentNumber())) {
           if(getDocumentHeader().hasWorkflowDocument()) {
               if(getDocumentHeader().getWorkflowDocument().isFinal() || getDocumentHeader().getWorkflowDocument().isProcessed()) {
                   isDocumentFinalOrProcessed = true;
               }
           }
           
        }

        return isDocumentFinalOrProcessed;

    }
}
