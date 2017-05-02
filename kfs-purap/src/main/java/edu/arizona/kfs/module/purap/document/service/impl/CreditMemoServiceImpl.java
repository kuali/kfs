package edu.arizona.kfs.module.purap.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoAccount;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.validation.event.AttributedContinuePurapEvent;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.BankService;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;


public class CreditMemoServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.CreditMemoServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CreditMemoServiceImpl.class);

    protected PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;
    
    /**
     * This method filters the payment requests given to just those which will be processed by PDP.
     * 
     * This will be entries with payment methods with PDP_IND = "Y".
     * 
     * @param baseResults The entire list of payment requests valid for extraction.
     * @return A filtered subset of the passed in list.
     */
    protected Collection<VendorCreditMemoDocument> filterPaymentRequests( Collection<VendorCreditMemoDocument> baseResults ) {
        return filterPaymentRequests(baseResults.iterator());
    }
    
    /**
     * This method filters the payment requests given to just those which will be processed by PDP.
     * 
     * This will be entries with payment methods with PDP_IND = "Y".
     * 
     * @param baseResults An iterator over a list of payment requests valid for extraction.
     * @return A filtered subset of the passed in list.
     */
    protected Collection<VendorCreditMemoDocument> filterPaymentRequests( Iterator<VendorCreditMemoDocument> baseResults ) {
        ArrayList<VendorCreditMemoDocument> filteredResults = new ArrayList<VendorCreditMemoDocument>();
        while ( baseResults.hasNext() ) {
            VendorCreditMemoDocument doc = baseResults.next();
            if ( doc instanceof edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument ) {
                if ( getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp( ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)doc).getPaymentMethodCode() ) ) {
                    filteredResults.add(doc);
                }
            } else {
                // if not the UA modification for some reason, assume that the payment method has not
                // been set and is therefore check
                filteredResults.add(doc);
            }
        }
        return filteredResults;
    }
    
    /**
     * Replace superclass method to copy the payment method code from the payment request.
     */
    @Override
    public void populateAndSaveCreditMemo(VendorCreditMemoDocument document) {
        try {
        	document.updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_IN_PROCESS);
            
            if (document.isSourceDocumentPaymentRequest()) {
                document.setBankCode(document.getPaymentRequestDocument().getBankCode());
                document.setBank(document.getPaymentRequestDocument().getBank());
                // if this is a UA CM document and the source is a new UA payment request document, copy the payment method code
                if ( document instanceof edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument && document.getPaymentRequestDocument() instanceof PaymentRequestDocument ) {
                    ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)document).setPaymentMethodCode(((PaymentRequestDocument)document.getPaymentRequestDocument()).getPaymentMethodCode());
                    ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)document).synchronizeBankCodeWithPaymentMethod();
                }
            }
            else {
                // set bank code to default bank code in the system parameter
                Bank defaultBank = SpringContext.getBean(BankService.class).getDefaultBankByDocType(VendorCreditMemoDocument.class);
                if (defaultBank != null) {
                    document.setBankCode(defaultBank.getBankCode());
                    document.setBank(defaultBank);
                }
            }
            
            documentService.saveDocument(document, AttributedContinuePurapEvent.class);
        } catch (ValidationException ve) {
        	try {
        		document.updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_INITIATE);
        	} 
        	catch (WorkflowException we) {
        		
        	}
            
        } catch (WorkflowException we) {
            // set the status back to initiate
        	try {
        		document.updateAndSaveAppDocStatus(PurapConstants.CreditMemoStatuses.APPDOC_INITIATE);
        	}
        	catch (WorkflowException w) {
        		
        	}
            String errorMsg = "Error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            LOG.error(errorMsg, we);
            throw new RuntimeException(errorMsg, we);
        }
    }
    
    @Override
    public List<VendorCreditMemoDocument> getCreditMemosToExtract(String chartCode) {
        List<VendorCreditMemoDocument> baseResults = super.getCreditMemosToExtract(chartCode);
        return (List<VendorCreditMemoDocument>) filterPaymentRequests(baseResults);
    }
    
    @Override
    public Collection<VendorCreditMemoDocument> getCreditMemosToExtractByVendor(String chartCode, VendorGroupingHelper vendor) {
        Collection<VendorCreditMemoDocument> baseResults = super.getCreditMemosToExtractByVendor(chartCode, vendor);
        return filterPaymentRequests(baseResults);
    }

    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
        return paymentMethodGeneralLedgerPendingEntryService;
    }

    public void setPaymentMethodGeneralLedgerPendingEntryService(PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService) {
        this.paymentMethodGeneralLedgerPendingEntryService = paymentMethodGeneralLedgerPendingEntryService;
    }
    
    /**
     * Update to baseline method to additionally set the payment method when building the document.
     * This method calls the needed method for each of the init methods.  After which, the vendor will
     * be set and we can get the payment method code.
     */
    @Override
    public void populateDocumentAfterInit(VendorCreditMemoDocument cmDocument) {
        super.populateDocumentAfterInit(cmDocument);
        if ( cmDocument instanceof edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument ) {
            // if there is a payment request document, pull the payment method from there
            if ( ObjectUtils.isNotNull( cmDocument.getPaymentRequestDocument() )
                    && cmDocument.getPaymentRequestDocument() instanceof PaymentRequestDocument ) {
                ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)cmDocument).setPaymentMethodCode(
                        ((PaymentRequestDocument)cmDocument.getPaymentRequestDocument()).getPaymentMethodCode() );
                ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)cmDocument).synchronizeBankCodeWithPaymentMethod();
            } else { // otherwise, go back to the vendor
                VendorDetail vd = vendorService.getVendorDetail(cmDocument.getVendorHeaderGeneratedIdentifier(), cmDocument.getVendorDetailAssignedIdentifier());
                if (vd != null
                        && ObjectUtils.isNotNull(vd.getExtension()) ) {
                    if ( vd.getExtension() instanceof VendorDetailExtension
                            && StringUtils.isNotBlank( ((VendorDetailExtension)vd.getExtension()).getDefaultB2BPaymentMethodCode() ) ) {
                        ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)cmDocument).setPaymentMethodCode(
                                ((VendorDetailExtension)vd.getExtension()).getDefaultB2BPaymentMethodCode() );
                        ((edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument)cmDocument).synchronizeBankCodeWithPaymentMethod();
                    }
                }
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.purap.document.service.CreditMemoService#calculateCreditMemo(org.kuali.kfs.module.purap.document.CreditMemoDocument)
     */
    @SuppressWarnings("unchecked")
    @Override
    public void calculateCreditMemo(VendorCreditMemoDocument cmDocument) {

        cmDocument.updateExtendedPriceOnItems();

        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {
            // make sure restocking fee is negative
            if (StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_RESTCK_FEE_CODE, item.getItemTypeCode())) {
                if (item.getItemUnitPrice() != null) {
                    item.setExtendedPrice(item.getExtendedPrice().abs().negated());
                    item.setItemUnitPrice(item.getItemUnitPrice().abs().negate());
                }
            }
        }

        //calculate tax if cm not based on vendor
        if (cmDocument.isSourceVendor() == false) {
            purapService.calculateTax(cmDocument);
        }

        // proration
        if (cmDocument.isSourceVendor()) {
            // no proration on vendor
            return;
        }

        for (CreditMemoItem item : (List<CreditMemoItem>) cmDocument.getItems()) {

            // skip above the line
            item.refreshReferenceObject(PurapPropertyConstants.ITEM_TYPE);

            if (item.getItemType().isLineItemIndicator()) {
                continue;
            }

            if ((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) && (KualiDecimal.ZERO.compareTo(item.getExtendedPrice()) != 0)) {
              	KualiDecimal totalAmount = KualiDecimal.ZERO;
              	List<PurApAccountingLine> distributedAccounts = null;
              	List<SourceAccountingLine> summaryAccounts = null;
                                
              	totalAmount = cmDocument.getPurApSourceDocumentIfPossible().getTotalDollarAmount();                
              	if ((totalAmount != null) && ((KualiDecimal.ZERO.compareTo(totalAmount)) == 0)) {
              		// UNENCUMBERED CASE : if Purchase Order is Zero Dollar i.e Unencumbered then use the contents of the Credit Memo itself instead 
              		PurchaseOrderDocument poDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
              		List<PurchaseOrderItem> poItems =  poDocument.getItems();
              		totalAmount = KualiDecimal.ZERO;
              		for (PurchaseOrderItem poItem : poItems){																
              			// Total Only Above the Line
              			if (poItem.getItemType().isLineItemIndicator()) {
              				totalAmount = totalAmount.add(poItem.getItemInvoicedTotalAmount()); 
              			}							              				              			
              		}    

              		// PO is zero dollar unencumbered hence has no line detail amounts to prorate so use Credit memo itself line detail instead					
              		purapAccountingService.updateAccountAmounts(cmDocument);					
              		summaryAccounts = purapAccountingService.generateSummary(cmDocument.getItems());
              	    distributedAccounts = ((edu.arizona.kfs.module.purap.service.PurapAccountingService)purapAccountingService).generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, CreditMemoAccount.class, cmDocument.getItems());
              	}
              	else {
              		// ENCUMBERED CASE : Not UnEncumbered i.e IS ENCUMBERED hence safe to use SourceDocument for accounting line fill and pro-ration
              		purapAccountingService.updateAccountAmounts(cmDocument.getPurApSourceDocumentIfPossible());
              		summaryAccounts = purapAccountingService.generateSummary(cmDocument.getPurApSourceDocumentIfPossible().getItems());
              		// totalAmount != 0
              	    distributedAccounts = ((edu.arizona.kfs.module.purap.service.PurapAccountingService)purapAccountingService).generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, CreditMemoAccount.class, cmDocument.getItems());
              	}

              	if (CollectionUtils.isNotEmpty(distributedAccounts) && CollectionUtils.isEmpty(item.getSourceAccountingLines())) {
                    item.setSourceAccountingLines(distributedAccounts);
                }
                
                // update again now that distribute is finished. Needed for UnEncumbered Additional Items to re-proportion
                purapAccountingService.updateAccountAmounts(cmDocument);                                
            }  
        }
        // end proration
    }

    // This method fixes a bug that was discovered in 3, supposedly fixed in 4, and reintroduced sometime afterward
    // (UA:KFSI-3305, Foundation:KFSMI-6530)
    @Override
    protected void populateDocumentFromPO(VendorCreditMemoDocument cmDocument, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super.populateDocumentFromPO(cmDocument, expiredOrClosedAccountList);
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(cmDocument.getPurchaseOrderIdentifier());
        cmDocument.setUseTaxIndicator(purchaseOrderDocument.isUseTaxIndicator());
    }

}
