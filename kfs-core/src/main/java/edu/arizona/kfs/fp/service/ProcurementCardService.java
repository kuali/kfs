package edu.arizona.kfs.fp.service;

import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;

public interface ProcurementCardService {

    /**
     * This method iterates through all the transactions entries for this pcard document and returns true ONLY if none of them satisfies one of the four conditions: 
     * AZ vendor, Sales tax already supplied, Sales Tax was edited/entered, or tax exempt indicator for the current transaction is on.
     * @param procurementCardDocument the PCDO to check
     * @return true if the PCDO is a use tax transaction, otherwise return false
     */
    boolean hasUseTaxTransaction(ProcurementCardDocument procurementCardDocument);
    
    /**
     * Generate use tax entries for this PCDO if applicable.
     * @param procurementCardDocument the given PCDO
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @return true if use tax pending entry generation was successful; false if an error condition prevented the successful generation of the pending entries
     */
    boolean generateUseTaxPendingEntries(ProcurementCardDocument procurementCardDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
}
