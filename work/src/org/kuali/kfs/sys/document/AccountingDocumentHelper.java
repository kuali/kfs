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

import static org.apache.commons.beanutils.PropertyUtils.getProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.rule.event.AccountingLineEvent;
import org.kuali.kfs.rule.event.AddAccountingLineEvent;
import org.kuali.kfs.rule.event.DeleteAccountingLineEvent;
import org.kuali.kfs.rule.event.ReviewAccountingLineEvent;
import org.kuali.kfs.rule.event.UpdateAccountingLineEvent;
import org.kuali.kfs.service.AccountingLineService;


/**
 * Helper class used for delegating tasks of an <code>{@link AccountingDocument}</code> that effect a
 * parallel hierarchy. Basically, this is here because it is the alternative to duplicating code. 
 * Rather than having the encapsulated methods exist in multiple documents that use it, but aren't necessarily
 * <code>{@link AccountingDocument}</code> instances, use the <code>{@link AccountingDocumentHelper}</code>.<br/>
 *
 * <p>This is the result of having <code>{@link SourceAccountingLine}</code> and <code>{@link TargetAccountingLine}</code> 
 * parallel hierarchies that may need to be extended at the <code>{@link AccountingLine}</code> hierarchical level and the
 * higher.
 *
 * @see org.kuali.kfs.bo.SourceAccountingLine
 * @see org.kuali.kfs.bo.TargetAccountingLine
 * @see org.kuali.kfs.document.AccountingDocument
 * @see org.kuali.kfs.document.AccountingDocumentBase
 */
public class AccountingDocumentHelper<KfsDocument extends GeneralLedgerPostingDocument> implements java.io.Serializable {
    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(AccountingDocumentHelper.class);

    private KfsDocument document;

    /**
     * Since documents that use this may not necessarily need to be <code>{@link AccountingDocument}</code> instances,
     * they must be at least <code>{@link GeneralLedgerPostingDocument}</code> instances.
     */
    public AccountingDocumentHelper(KfsDocument document) {
        setDocument(document);
    }
    
    public void setDocument(KfsDocument document) {
        this.document = document;
    }
    
    public KfsDocument getDocument() {
        return document;
    }

    /**
     * Wrapper for getTargetAccountingLineClass
     *
     * @return Class
     */
    private Class getTargetAccountingLineClass() {
        try {
            return (Class) getProperty(getDocument(), "targetAccountingLineClass");
        }
        catch (Exception e) {
            LOG.warn("Something went very wrong when trying to get the targetAccountingLineClass property from the "
                     + getDocument().getClass() + " class");
            return null;
        }
    }

    /**
     * Wrapper for getSourceAccountingLineClass
     *
     * @return Class
     */
    private Class getSourceAccountingLineClass() {
        try {
            return (Class) getProperty(getDocument(), "sourceAccountingLineClass");
        }
        catch (Exception e) {
            LOG.warn("Something went very wrong when trying to get the sourceAccountingLineClass property from the "
                     + getDocument().getClass() + " class");
            return null;
        }
    }

    /**
     * Wrapper for getTargetAccountingLines
     *
     * @return List
     */
    private List getTargetAccountingLines() {
        try {
            return (List) getProperty(getDocument(), "targetAccountingLines");
        }
        catch (Exception e) {
            LOG.warn("Something went very wrong when trying to get the targetAccountingLines property from the "
                     + getDocument().getClass() + " class");
            return null;
        }
    }

    /**
     * Wrapper for getSourceAccountingLines
     *
     * @return List
     */
    private List getSourceAccountingLines() {
        try {
            return (List) getProperty(getDocument(), "sourceAccountingLines    ");
        }
        catch (Exception e) {
            LOG.warn("Something went very wrong when trying to get the sourceAccountingLines property from the "
                     + getDocument().getClass() + " class");
            return null;
        }
    }

    /**
     * Local <code>{@link AccountingLineService}</code> delegation. To override which <code>{@link AccountingLineService}</code>
     * is used, just override this.
     *
     * @return AccountingLineService;
     */
    protected AccountingLineService getAccountingLineService() {
        return SpringContext.getBean(AccountingLineService.class);
    }
    
    public List generateSaveEvents() {
        List events = new ArrayList();

        // foreach (source, target)
        // 1. retrieve persisted accountingLines for document
        // 2. retrieve current accountingLines from given document
        // 3. compare, creating add/delete/update events as needed
        // 4. apply rules as appropriate returned events
        LOG.debug("Getting persisted source lines");
        List persistedSourceLines = getAccountingLineService().getByDocumentHeaderId(getSourceAccountingLineClass(), getDocument().getDocumentNumber());
        LOG.debug("Done getting persisted source lines");
        LOG.debug(persistedSourceLines.toString());
        List currentSourceLines = getSourceAccountingLines();

        List sourceEvents = generateEvents(persistedSourceLines, currentSourceLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_SOURCE_ACCT_LINE_PROPERTY_NAME);
        for (Object event : sourceEvents) {
            AccountingLineEvent sourceEvent = (AccountingLineEvent) event;
            events.add(sourceEvent);
        }

        List persistedTargetLines = getAccountingLineService().getByDocumentHeaderId(getTargetAccountingLineClass(), getDocument().getDocumentNumber());
        List currentTargetLines = getTargetAccountingLines();

        List targetEvents = generateEvents(persistedTargetLines, currentTargetLines, KFSConstants.DOCUMENT_PROPERTY_NAME + "." + KFSConstants.EXISTING_TARGET_ACCT_LINE_PROPERTY_NAME);
        for (Object event: targetEvents) {
            AccountingLineEvent targetEvent = (AccountingLineEvent) event;
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
    protected List generateEvents(List persistedLines, List currentLines, String errorPathPrefix) {
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
                    UpdateAccountingLineEvent updateEvent = new UpdateAccountingLineEvent(indexedErrorPathPrefix, getDocument(), persistedLine, currentLine);
                    updateEvents.add(updateEvent);
                }
                else {
                    ReviewAccountingLineEvent reviewEvent = new ReviewAccountingLineEvent(indexedErrorPathPrefix, getDocument(), currentLine);
                    reviewEvents.add(reviewEvent);
                }

                persistedLineMap.remove(key);
            }
            else {
                // it must be a new addition
                AddAccountingLineEvent addEvent = new AddAccountingLineEvent(indexedErrorPathPrefix, getDocument(), currentLine);
                addEvents.add(addEvent);
            }
        }

        // detect deletions
        for (Iterator i = persistedLineMap.entrySet().iterator(); i.hasNext();) {
            // the deleted line is not displayed on the page, so associate the error with the whole group
            String groupErrorPathPrefix = errorPathPrefix + KFSConstants.ACCOUNTING_LINE_GROUP_SUFFIX;
            Map.Entry e = (Map.Entry) i.next();
            AccountingLine persistedLine = (AccountingLine) e.getValue();
            DeleteAccountingLineEvent deleteEvent = new DeleteAccountingLineEvent(groupErrorPathPrefix, getDocument(), persistedLine, true);
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
}
