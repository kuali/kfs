package edu.arizona.kfs.fp.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.document.validation.event.AccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.DeleteAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.ReviewAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.event.UpdateAccountingLineEvent;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.document.TransactionalDocument;

import edu.arizona.kfs.fp.businessobject.ErrorCertification;
import edu.arizona.kfs.gl.businessobject.GecEntryRelationship;
import edu.arizona.kfs.sys.KFSConstants;

/**
 * This is the business object that represents the UA modifications for the GeneralErrorCorrectionDocument. This is a transactional document that
 * will eventually post transactions to the G/L. It integrates with workflow and also contains two groupings of accounting lines:
 * from and to. From lines are the source lines, to lines are the target lines.
 */

public class GeneralErrorCorrectionDocument extends org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument {
    private static final long serialVersionUID = 3559591546723165167L;
    private static  final Set<String>  GEC_ACTIVE_ROUTE_STATUS_CODES = KFSConstants.GEC_ACTIVE_ROUTE_STATUS_CODES;

    private Set<GecEntryRelationship> gecEntryRelationships;
    private ErrorCertification errorCertification;
    private Integer errorCertID;
    private DebitDeterminerService debitService;


    public GeneralErrorCorrectionDocument() {
    	super();
    }


    public ErrorCertification getErrorCertification() {
    	return errorCertification;
    }


    public void setErrorCertification(ErrorCertification errorCertification) {
    	this.errorCertification = errorCertification;
    }


    public Integer getErrorCertID() {
    	return errorCertID;
    }


    public void setErrorCertID(Integer errorCertID) {
    	this.errorCertID = errorCertID;
    	this.errorCertification.setErrorCertID(errorCertID);
    }


    public void toCopy() throws WorkflowException {
    	super.toCopy();
    	
    	errorCertID = null;
    	ErrorCertification oldErrorCertification = errorCertification;
    	errorCertification = new ErrorCertification();
    	errorCertification.setExpenditureDescription(oldErrorCertification.getExpenditureDescription());
    	errorCertification.setExpenditureProjectBenefit(oldErrorCertification.getExpenditureProjectBenefit());
    	errorCertification.setErrorDescription(oldErrorCertification.getErrorDescription());
    	errorCertification.setErrorCorrectionReason(oldErrorCertification.getErrorCorrectionReason());
    }


    /**
     * The map key is a combination of the accounting line sequence number and type (e.g. 1F)
     *
     * @return
     */
    public Map<String, Entry> getEntries() {
        // populate Entries on document load
        Map<String, Entry> entries = new TreeMap<String, Entry>();
        if (getGecEntryRelationships() != null && !getGecEntryRelationships().isEmpty()) {
            for (GecEntryRelationship rel : getGecEntryRelationships()) {
                entries.put(rel.getGecAcctLineSeqNumber() + rel.getGecFdocLineTypeCode(), rel.getEntry());
            }
        }

        return entries;
    }


    public Entry getEntryByAccountingLine(AccountingLine line) {
        if (line != null) {
            return getEntries().get(line.getSequenceNumber() + line.getFinancialDocumentLineTypeCode());
        }

        return null;
    }


    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        if (getGecEntryRelationships() != null) {
            String docRouteStatusCode = this.getDocumentHeader().getWorkflowDocument().getStatus().getCode();
            for (GecEntryRelationship rel : getGecEntryRelationships()) {
                rel.setGecDocRouteStatus(docRouteStatusCode);
            }
            updateEntryGecDocNumber(docRouteStatusCode);
        }
        super.doRouteStatusChange(statusChangeEvent);
    }


    /* If we go into a status with live relationships, then the entry should be associated
     * to this document, so that subsequent GEC docs can't pick them up. Similarly, if this
     * GEC is being cancelled or disapproved, then the enry needs to be disosciated with this
     * doc. This is done by virtue of if any GecEntryRelationship records exist, and the actual
     * status being entered.
     */
    private void updateEntryGecDocNumber(String docRouteStatusCode) {

        if (KewApiConstants.ROUTE_HEADER_FINAL_CD.equals(docRouteStatusCode)) {
            /*
             * Exceptional Case:
             * When a GEC goes to FINAL, that GEC doc should forever be tied to
             * its GLEs, since after going FINAL, those GLEs should not be able to
             * have another GEC against it. This means we make sure when the FINAL
             * transition occurs, we leave the gecDocNumber alone on the GLEs of this
             * doc. This effectively blocks selection of the GLE in the
             * GecEntryLookupAction, thus blocks re-association. Note, that the
             * gecDocumentNumber will have been already been set on the GLEs from
             * previous transitions, so we simply stop here.
             */
            return;
        }

        if (getGecEntryRelationships() == null) {
            // If no relationships have been made, nothing to update
            return;
        }

        // This is what we will persist
        List<Entry> entries = new LinkedList<Entry>();

        // Go through entries and either associate or dissassociate the doc from the entry
        for (GecEntryRelationship relationship : getGecEntryRelationships()) {
            Entry entry = relationship.getEntry();
            if (GEC_ACTIVE_ROUTE_STATUS_CODES.contains(docRouteStatusCode)) {
                // The status warrants locking the Entry to this doc
                entry.setGecDocumentNumber(relationship.getGecDocumentNumber());
            } else {
                // Doc has been cancelled or disapproved, need to dissociate entry from doc
                entry.setGecDocumentNumber(null);
            }
            entries.add(entry);
        }

        // Persist any change
        if (entries.size() > 0) {
            getBusinessObjectService().save(entries);
        }

    }


    public Set<GecEntryRelationship> getGecEntryRelationships() {
        return gecEntryRelationships;
    }


    public void setGecEntryRelationships(Set<GecEntryRelationship> gecEntryRelationships) {
        this.gecEntryRelationships = gecEntryRelationships;
    }


    @SuppressWarnings("unchecked")//List<?> from super
    @Override
    public List<Collection<PersistableBusinessObject>> buildListOfDeletionAwareLists() {
        List managedCollections = super.buildListOfDeletionAwareLists();
        managedCollections.add(this.getGecEntryRelationships());
        return managedCollections;
    }


    /*
     * GLPE generation service calls this on parent, but it does not handle the
     * unnatural balance cases corectly (resulting GLPEs have their debit/credit
     * flags always flipped to the natural balance 'normal' state).
     *
     * Since we know the source line's debit/credit flag is directly from a GLE but
     * flipped, and we also know the target line's debit/credit flag is then directly
     * from the source line's debit credit flag but flipped, then we have a transitive
     * chain that tells us exactly what each flag should be along the way, without
     * having to consult any other properties of the GLE or converted lines (aside from
     * what the original GLE debit/credit flag is). This means we only need really
     * simple logic to decide our debit/credit flag.
     *
     * @see org.kuali.kfs.fp.document.CapitalAssetInformationDocumentBase#isDebit()
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return getDebitService().isDebitCode(((AccountingLine) postable).getDebitCreditCode());
    }


    private DebitDeterminerService getDebitService() {
        if (debitService == null) {
            debitService = SpringContext.getBean(DebitDeterminerService.class);
        }
        return debitService;
    }

    /*
     * This is overridden in order to process a GEC deletion of a source line, which is different than the rest of the
     * system. Great care was taken to leave the original parent logic as is, detect the GEC edge case, and process the
     * case separately.
     */
    @Override
    protected List generateEvents(List persistedLines, List currentLines, String errorPathPrefix, TransactionalDocument document) {
        List<AccountingLineEvent> addEvents = new ArrayList<AccountingLineEvent>();
        List<AccountingLineEvent> updateEvents = new ArrayList<AccountingLineEvent>();
        List<AccountingLineEvent> reviewEvents = new ArrayList<AccountingLineEvent>();
        List<AccountingLineEvent> deleteEvents = new ArrayList<AccountingLineEvent>();

        // generate events
        Map persistedLineMap = buildAccountingLineMap(persistedLines);
        Map<String, AccountingLine> gecLineMap = buildGecAccountingLineMap(persistedLines);

        // (iterate through current lines to detect additions and updates, removing affected lines from persistedLineMap as we go
        // so deletions can be detected by looking at whatever remains in persistedLineMap)
        int index = 0;
        for (Iterator i = currentLines.iterator(); i.hasNext(); index++) {
            String indexedErrorPathPrefix = errorPathPrefix + "[" + index + "]";
            AccountingLine currentLine = (AccountingLine) i.next();

            //****** START GEC deviation from foundation (see UAF-3819) ******************/
            // Special GEC case, deleting source lines breaks using sequence numbers as main identifier
            if (currentLine != null && persistedLineMap != null && !persistedLineMap.isEmpty() && currentLine.isSourceAccountingLine()) {
                String gecKey = buildGecLineCompositeKey(currentLine); // same key maker as used in building gecLineMap
                AccountingLine persistedLine = gecLineMap.get(gecKey);
                if (persistedLine != null) {
                    // We got an exact match on a source line, which should just end up as a review after submit.
                    // Note: This short circuit works due to this being a source line, and the fact that source
                    //        lines are read-only in GEC. Thus, we can implicitly rule out an update event on any
                    //        fields not considered in this.buildGecLineCompositeKey(...).
                    ReviewAccountingLineEvent reviewEvent = new ReviewAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                    reviewEvents.add(reviewEvent);
                    persistedLineMap.remove(currentLine.getSequenceNumber());
                    gecLineMap.remove(gecKey);
                    continue;
                }
            }
            //****** END GEC deviation from foundation ******************/

            Integer key = currentLine.getSequenceNumber();
            AccountingLine persistedLine = (AccountingLine) persistedLineMap.get(key);
            // if line is both current and persisted...
            if (persistedLine != null) {
                // ...check for updates
                if (!currentLine.isLike(persistedLine)) {
                    UpdateAccountingLineEvent updateEvent = new UpdateAccountingLineEvent(indexedErrorPathPrefix, document, persistedLine, currentLine);
                    updateEvents.add(updateEvent);
                } else {
                    ReviewAccountingLineEvent reviewEvent = new ReviewAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                    reviewEvents.add(reviewEvent);
                }

                persistedLineMap.remove(key);
            } else {
                // it must be a new addition
                AddAccountingLineEvent addEvent = new AddAccountingLineEvent(indexedErrorPathPrefix, document, currentLine);
                addEvents.add(addEvent);
            }
        }

        // detect deletions
        for (Iterator i = persistedLineMap.entrySet().iterator(); i.hasNext(); ) {
            // the deleted line is not displayed on the page, so associate the error with the whole group
            String groupErrorPathPrefix = errorPathPrefix + org.kuali.kfs.sys.KFSConstants.ACCOUNTING_LINE_GROUP_SUFFIX;
            Map.Entry e = (Map.Entry) i.next();
            AccountingLine persistedLine = (AccountingLine) e.getValue();
            DeleteAccountingLineEvent deleteEvent = new DeleteAccountingLineEvent(groupErrorPathPrefix, document, persistedLine, true);
            deleteEvents.add(deleteEvent);
        }


        //
        // merge the lists
        List<AccountingLineEvent> lineEvents = new ArrayList<AccountingLineEvent>();
        lineEvents.addAll(reviewEvents);
        lineEvents.addAll(updateEvents);
        lineEvents.addAll(addEvents);
        lineEvents.addAll(deleteEvents);

        return lineEvents;
    }


    // Stolen from super, necessary in order to build a composite key, as opposed to just
    // using the line's sequence number as a key.
    private Map<String, AccountingLine> buildGecAccountingLineMap(List accountingLines) {
        Map<String, AccountingLine> lineMap = new HashMap<String, AccountingLine>();

        for (Object o : accountingLines) {
            AccountingLine accountingLine = (AccountingLine) o;
            String compositeKey = buildGecLineCompositeKey(accountingLine);

            Object oldLine = lineMap.put(compositeKey, accountingLine);

            // verify that sequence numbers are unique...
            if (oldLine != null) {
                throw new IllegalStateException("AccountingLine map collision detected for composite key: " + compositeKey);
            }
        }

        return lineMap;
    }


    /*
     * This will build a unique String based on three line values; this differs
     * from the 3-field PK that the DD yields -- we want amount to be
     * considered as well, but then to also exclude the sequence number. We don't
     * want to key on sequence, since the sequence changes with a line deletion, a
     * state introduced by the new GEC specification.
     *
     * Also, we want to consider amount, which is safe to do in GEC so long as we
     * are only dealing with source lines. Said another way, source lines are immutable
     * under GEC, so we can use amount to detect an add/delete in that collection.
     */
    private String buildGecLineCompositeKey(AccountingLine accountingLine) {
        StringBuilder sb = new StringBuilder();

        sb.append(accountingLine.isSourceAccountingLine() ? "sourceLine" : "targetLine");
        sb.append("_docNum-");
        sb.append(accountingLine.getDocumentNumber());
        sb.append("_accountNum-");
        sb.append(accountingLine.getAccount().getAccountNumber());
        sb.append("_amount-");
        sb.append(accountingLine.getAmount());

        return sb.toString();
    }


}
