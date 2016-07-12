package edu.arizona.kfs.module.purap.service;

import java.util.List;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;

/**
 * Need API to archive and retrieve archived tax entries.  See javadoc of 
 * AccountsPayableUseTaxArchivedEntry for more details
 */
public interface PurapUseTaxEntryArchiveService {
    public void deletePaymentRequestUseTaxArchivedEntries(PaymentRequestDocument preq);
    
    public void reversePaymentRequestUseTaxEntries(PaymentRequestDocument preq, GeneralLedgerPendingEntrySequenceHelper sequenceHelper);
    
    public void archivePaymentRequestUseTaxPendingEntries(PaymentRequestDocument preq, List<GeneralLedgerPendingEntry> useTaxPendingEntries);
}

