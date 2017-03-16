package edu.arizona.kfs.fp.document;

import java.util.Collection;
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
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.krad.bo.PersistableBusinessObject;

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

}
