package edu.arizona.kfs.fp.service;

import org.kuali.kfs.fp.document.ProcurementCardDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;

public interface ProcurementCardService {

    /**
     * Is the PCDO a use tax transaction? Has no sales tax or is set as exempt and is the vendor
     * @param procurementCardDocument the PCDO to check
     * @return true if the PCDO is a use tax transaction, otherwise return false
     */
    boolean isUseTaxTransaction(ProcurementCardDocument procurementCardDocument);
    
    /**
     * Generate use tax entries for this PCDO if applicable.
     * @param procurementCardDocument the given PCDO
     * @param sequenceHelper helper class to keep track of GLPE sequence
     * @return true if use tax pending entry generation was successful; false if an error condition prevented the successful generation of the pending entries
     */
    boolean generateUseTaxPendingEntries(ProcurementCardDocument procurementCardDocument, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
}
