package edu.arizona.kfs.module.purap.service.impl;

import edu.arizona.kfs.sys.KFSConstants;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.HomeOriginationService;
import org.kuali.rice.krad.service.BusinessObjectService;
import edu.arizona.kfs.module.purap.businessobject.AccountsPayableUseTaxArchivedEntry;
import edu.arizona.kfs.module.purap.service.PurapUseTaxEntryArchiveService;

/**
 * Archives and retrieves archived tax entries.  See javadoc of 
 * AccountsPayableUseTaxArchivedEntry for more details
 */
public class PurapUseTaxEntryArchiveServiceImpl implements PurapUseTaxEntryArchiveService {
    protected HomeOriginationService homeOriginationService = null;
    protected BusinessObjectService businessObjectService = null;
    
    public void reversePaymentRequestUseTaxEntries(PaymentRequestDocument preq, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        List<AccountsPayableUseTaxArchivedEntry> taxGlpes = retrievePaymentRequestUseTaxArchivedEntries(preq);
        
        for (AccountsPayableUseTaxArchivedEntry taxGlpe : taxGlpes) {
            GeneralLedgerPendingEntry reversingTaxGlpe = taxGlpe.toGeneralLedgerPendingEntry();
            if (KFSConstants.GL_CREDIT_CODE.equals(reversingTaxGlpe.getTransactionDebitCreditCode())) {
                reversingTaxGlpe.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            else {
                reversingTaxGlpe.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            reversingTaxGlpe.setTransactionLedgerEntrySequenceNumber(new Integer(sequenceHelper.getSequenceCounter()));
            sequenceHelper.increment();
            reversingTaxGlpe.setVersionNumber(null);
            
            reversingTaxGlpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
            preq.getGeneralLedgerPendingEntries().add(reversingTaxGlpe);
        }
    }
    
    protected List<AccountsPayableUseTaxArchivedEntry> retrievePaymentRequestUseTaxArchivedEntries(PaymentRequestDocument preq) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, preq.getDocumentNumber());
        Collection<AccountsPayableUseTaxArchivedEntry> records = (Collection<AccountsPayableUseTaxArchivedEntry>) getBusinessObjectService().findMatchingOrderBy(AccountsPayableUseTaxArchivedEntry.class, fieldValues, KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, true);
        return new ArrayList<AccountsPayableUseTaxArchivedEntry>(records);
    }
    
    public void deletePaymentRequestUseTaxArchivedEntries(PaymentRequestDocument preq) {
        Map<String, String> fieldValues = new HashMap<String, String>();
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, getHomeOriginationService().getHomeOrigination().getFinSystemHomeOriginationCode());
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, preq.getDocumentNumber());
        getBusinessObjectService().deleteMatching(AccountsPayableUseTaxArchivedEntry.class, fieldValues);
    }
    
    public void archivePaymentRequestUseTaxPendingEntries(PaymentRequestDocument preq, List<GeneralLedgerPendingEntry> useTaxPendingEntries) {
        // delete the existing records
        deletePaymentRequestUseTaxArchivedEntries(preq);
        
        for (GeneralLedgerPendingEntry glpe : useTaxPendingEntries) {
            AccountsPayableUseTaxArchivedEntry entryToArchive = new AccountsPayableUseTaxArchivedEntry(glpe);
            getBusinessObjectService().save(entryToArchive);
        }
    }
    
    public HomeOriginationService getHomeOriginationService() {
        if (homeOriginationService == null) {
            homeOriginationService = SpringContext.getBean(HomeOriginationService.class);
        }
        return homeOriginationService;
    }

    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
}
