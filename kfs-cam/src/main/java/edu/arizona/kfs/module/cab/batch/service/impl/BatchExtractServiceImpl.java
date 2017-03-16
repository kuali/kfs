package edu.arizona.kfs.module.cab.batch.service.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.Entry;

import edu.arizona.kfs.module.cab.CabConstants;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;

import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.purap.document.AccountsPayableDocumentBase;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;

public class BatchExtractServiceImpl extends org.kuali.kfs.module.cab.batch.service.impl.BatchExtractServiceImpl {
	
	/** 			
	* Retrieves a PRNC document for a specific document number
	*
	* @param entry GL Line
	* @return PaymentRequestDocument
	*/ 			
	protected PaymentRequestDocument findPrncDocument(Entry entry) {
		PaymentRequestDocument PRNCDocument = null;
		Map<String, String> keys = new LinkedHashMap<String, String>();
		keys.put(CabPropertyConstants.DOCUMENT_NUMBER, entry.getDocumentNumber());
		Collection<PaymentRequestDocument> matchingPreqs = businessObjectService.findMatching(PaymentRequestDocument.class, keys);
		if (matchingPreqs != null && matchingPreqs.size() == 1) {
			PRNCDocument = matchingPreqs.iterator().next();
		}
		return PRNCDocument;
	}
	
	
    @Override
    protected PurchasingAccountsPayableDocument createPurchasingAccountsPayableDocument(Entry entry) {
        AccountsPayableDocumentBase apDoc = null;
        PurchasingAccountsPayableDocument cabPurapDoc = null;
        // If document is not in CAB, create a new document to save in CAB
        if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode())) {
            // find PREQ
            apDoc = findPaymentRequestDocument(entry);
        }
        else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
            // find CM
            apDoc = findCreditMemoDocument(entry);
        }
    	else if (CabConstants.PRNC.equals(entry.getFinancialDocumentTypeCode())) {
	    	// find PRNC
	    	apDoc = findPrncDocument(entry);
    	}
    	
        if (apDoc == null) {
            LOG.error("A valid Purchasing Document (PREQ or CM) could not be found for this document number " + entry.getDocumentNumber());
        }
        else {
            cabPurapDoc = new PurchasingAccountsPayableDocument();
            cabPurapDoc.setDocumentNumber(entry.getDocumentNumber());
            cabPurapDoc.setPurapDocumentIdentifier(apDoc.getPurapDocumentIdentifier());
            cabPurapDoc.setPurchaseOrderIdentifier(apDoc.getPurchaseOrderIdentifier());
            cabPurapDoc.setDocumentTypeCode(entry.getFinancialDocumentTypeCode());
            cabPurapDoc.setActivityStatusCode(CabConstants.ActivityStatusCode.NEW);
            cabPurapDoc.setVersionNumber(0L);
        }
        return cabPurapDoc;
    }
    
    @Override
    public void separatePOLines(List<Entry> fpLines, List<Entry> purapLines, Collection<Entry> elgibleGLEntries) {
        for (Entry entry : elgibleGLEntries) {
            if (CabConstants.PREQ.equals(entry.getFinancialDocumentTypeCode()) || CabConstants.PRNC.equals(entry.getFinancialDocumentTypeCode())) {
                purapLines.add(entry);
            }
            else if (!CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                fpLines.add(entry);
            }
            else if (CabConstants.CM.equals(entry.getFinancialDocumentTypeCode())) {
                Map<String, String> fieldValues = new HashMap<String, String>();
                fieldValues.put(CabPropertyConstants.GeneralLedgerEntry.DOCUMENT_NUMBER, entry.getDocumentNumber());
                // check if vendor credit memo, then include as FP line
                Collection<VendorCreditMemoDocument> matchingCreditMemos = businessObjectService.findMatching(VendorCreditMemoDocument.class, fieldValues);
                for (VendorCreditMemoDocument creditMemoDocument : matchingCreditMemos) {
                    if (creditMemoDocument.getPurchaseOrderIdentifier() == null) {
                        fpLines.add(entry);
                    }
                    else {
                        purapLines.add(entry);
                    }
                }
            }
        }
    }
}
