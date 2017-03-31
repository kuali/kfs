package edu.arizona.kfs.module.purap.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestAccount;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.validation.event.PurchasingAccountsPayableItemPreCalculateEvent;
import org.kuali.kfs.module.purap.util.PurApItemUtils;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.fp.service.PaymentMethodGeneralLedgerPendingEntryService;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;

public class PaymentRequestServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PaymentRequestServiceImpl {
	
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestServiceImpl.class);
    protected PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService;

    
    /**
     * This method filters the payment requests given to just those which will be processed by PDP.
     * 
     * This will be entries with payment methods with the PDP_IND set to "Y".
     * 
     * @param baseResults The entire list of payment requests valid for extraction.
     * @return A filtered subset of the passed in list.
     */
    protected Collection<PaymentRequestDocument> filterPaymentRequests( Collection<PaymentRequestDocument> baseResults ) {
        ArrayList<PaymentRequestDocument> filteredResults = new ArrayList<PaymentRequestDocument>( baseResults.size() );
        for ( PaymentRequestDocument doc : baseResults ) {
            //PREQ with Overages Disapproved is uneditable by initiator
            if (!doc.getDocumentHeader().hasWorkflowDocument()) {
                WorkflowDocument workflowDocument = null;
                try {
                	workflowDocument = workflowDocumentService.createWorkflowDocument(doc.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), GlobalVariables.getUserSession().getPerson());
                }                
                catch (WorkflowException we) {
                    throw new RuntimeException(we);
                }
                doc.getDocumentHeader().setWorkflowDocument(workflowDocument);             
            }
           
            if ( doc instanceof edu.arizona.kfs.module.purap.document.PaymentRequestDocument ) {
                if ( getPaymentMethodGeneralLedgerPendingEntryService().isPaymentMethodProcessedUsingPdp( ((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)doc).getPaymentMethodCode() ) ) {
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
    
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtract(Date onOrBeforePaymentRequestPayDate) {
        Collection<PaymentRequestDocument> baseResults = super.getPaymentRequestsToExtract(onOrBeforePaymentRequestPayDate);
        return filterPaymentRequests(baseResults);
    }
    
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractByCM(String campusCode, VendorCreditMemoDocument cmd) {
        throw new UnsupportedOperationException( "This method is not in use." );
    }
    
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractByVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate) {
        Collection<PaymentRequestDocument> baseResults = super.getPaymentRequestsToExtractByVendor(campusCode, vendor, onOrBeforePaymentRequestPayDate);
        return filterPaymentRequests(baseResults);
    }
    
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractSpecialPayments(String chartCode, Date onOrBeforePaymentRequestPayDate) {
        Collection<PaymentRequestDocument> baseResults = super.getPaymentRequestsToExtractSpecialPayments(chartCode, onOrBeforePaymentRequestPayDate);
        return filterPaymentRequests(baseResults);
    }
    
    //PREQ with Overages Disapproved is uneditable by initiator
    @Override
    public Collection<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        Collection<PaymentRequestDocument> baseResults = super.getImmediatePaymentRequestsToExtract(chartCode);
        return filterPaymentRequests(baseResults);      
    }
    
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestToExtractByChart(String chartCode, Date onOrBeforePaymentRequestPayDate) {
        Collection<PaymentRequestDocument> baseResults = super.getPaymentRequestToExtractByChart(chartCode, onOrBeforePaymentRequestPayDate);
        return filterPaymentRequests(baseResults);
    }

    protected PaymentMethodGeneralLedgerPendingEntryService getPaymentMethodGeneralLedgerPendingEntryService() {
    	if(paymentMethodGeneralLedgerPendingEntryService == null) {
    		paymentMethodGeneralLedgerPendingEntryService = SpringContext.getBean(PaymentMethodGeneralLedgerPendingEntryService.class);
    	}
        return paymentMethodGeneralLedgerPendingEntryService;
    }
    
    public void setPaymentMethodGeneralLedgerPendingEntryService(PaymentMethodGeneralLedgerPendingEntryService paymentMethodGeneralLedgerPendingEntryService) {
        this.paymentMethodGeneralLedgerPendingEntryService = paymentMethodGeneralLedgerPendingEntryService;
    }

    /**
     * Distributes accounts for a payment request document.
     *
     * @param paymentRequestDocument
     */
    @Override     
    protected void distributeAccounting(PaymentRequestDocument paymentRequestDocument) {
        // update the account amounts before doing any distribution
        purapAccountingService.updateAccountAmounts(paymentRequestDocument);

        String accountDistributionMethod = paymentRequestDocument.getAccountDistributionMethod();

        for (PaymentRequestItem item : (List<PaymentRequestItem>) paymentRequestDocument.getItems()) {
            KualiDecimal totalAmount = KualiDecimal.ZERO;
            List<PurApAccountingLine> distributedAccounts = null;
            List<SourceAccountingLine> summaryAccounts = null;
            Set excludedItemTypeCodes = new HashSet();
            excludedItemTypeCodes.add(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE);

            // skip above the line
            if (item.getItemType().isLineItemIndicator()) {
                continue;
            }

            if ((item.getSourceAccountingLines().isEmpty()) && (ObjectUtils.isNotNull(item.getExtendedPrice())) && (KualiDecimal.ZERO.compareTo(item.getExtendedPrice()) != 0)) {
                if ((StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_PMT_TERMS_DISCOUNT_CODE, item.getItemType().getItemTypeCode())) && (paymentRequestDocument.getGrandTotal() != null) && ((KualiDecimal.ZERO.compareTo(paymentRequestDocument.getGrandTotal()) != 0))) {

                    // No discount is applied to other item types other than item line
                    // See KFSMI-5210 for details

                    // total amount should be the line item total, not the grand total
                    totalAmount = paymentRequestDocument.getLineItemTotal();

                    // prorate item line accounts only
                    Set includedItemTypeCodes = new HashSet();
                    includedItemTypeCodes.add(PurapConstants.ItemTypeCodes.ITEM_TYPE_ITEM_CODE);
                    includedItemTypeCodes.add(PurapConstants.ItemTypeCodes.ITEM_TYPE_SERVICE_CODE);

                    summaryAccounts = purapAccountingService.generateSummaryIncludeItemTypesAndNoZeroTotals(paymentRequestDocument.getItems(), includedItemTypeCodes);
                    //if summaryAccount is empty then do not call generateAccountDistributionForProration as
                    //there is a check in that method to throw NPE if accounts percents == 0..
                    //KFSMI-8487
                    if (summaryAccounts != null) {
                  	    distributedAccounts = ((edu.arizona.kfs.module.purap.service.PurapAccountingService)purapAccountingService).generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, PaymentRequestAccount.class, paymentRequestDocument.getItems());
                    }
                    if (PurapConstants.AccountDistributionMethodCodes.SEQUENTIAL_CODE.equalsIgnoreCase(accountDistributionMethod)) {
                        purapAccountingService.updatePreqAccountAmountsWithTotal(distributedAccounts, item.getTotalAmount());

                    } else {
                        boolean rulePassed = true;
                        // check any business rules
                        rulePassed &= kualiRuleService.applyRules(new PurchasingAccountsPayableItemPreCalculateEvent(paymentRequestDocument, item));

                        if (rulePassed) {
                            purapAccountingService.updatePreqProporationalAccountAmountsWithTotal(distributedAccounts, item.getTotalAmount());
                        }
                    }
                }
                else {
                    PurchaseOrderItem poi = item.getPurchaseOrderItem();
                    if ((poi != null) && (poi.getSourceAccountingLines() != null) && (!(poi.getSourceAccountingLines().isEmpty())) && (poi.getExtendedPrice() != null) && ((KualiDecimal.ZERO.compareTo(poi.getExtendedPrice())) != 0)) {
                        // use accounts from purchase order item matching this item
                        // account list of current item is already empty
                        item.generateAccountListFromPoItemAccounts(poi.getSourceAccountingLines());
                    }
                    else {
                    	if ((poi.getExtendedPrice() != null) && ((KualiDecimal.ZERO.compareTo(poi.getExtendedPrice())) == 0)) {
                            // if Purchase Order is Zero Dollar i.e Unencumbered then use the contents of the Payment Requisition instead 
                            totalAmount = paymentRequestDocument.getTotalDollarAmountAboveLineItems();
                            purapAccountingService.updateAccountAmounts(paymentRequestDocument);
                            summaryAccounts = purapAccountingService.generateSummary(PurApItemUtils.getAboveTheLineOnly(paymentRequestDocument.getItems()));                    		
                    	}
                    	else {
                            totalAmount = paymentRequestDocument.getPurchaseOrderDocument().getTotalDollarAmountAboveLineItems();
                            purapAccountingService.updateAccountAmounts(paymentRequestDocument.getPurchaseOrderDocument());
                            summaryAccounts = purapAccountingService.generateSummary(PurApItemUtils.getAboveTheLineOnly(paymentRequestDocument.getPurchaseOrderDocument().getItems()));
                    	}
                    	
                        //if summaryAccount is empty then do not call generateAccountDistributionForProration as
                        //there is a check in that method to throw NPE if accounts percents == 0..
                        //KFSMI-8487
                        if (summaryAccounts != null) {
                      	    distributedAccounts = ((edu.arizona.kfs.module.purap.service.PurapAccountingService)purapAccountingService).generateAccountDistributionForProration(summaryAccounts, totalAmount, new Integer("6"), PaymentRequestAccount.class, paymentRequestDocument.getItems());
                        }
                    }
                }
                if (CollectionUtils.isNotEmpty(distributedAccounts) && CollectionUtils.isEmpty(item.getSourceAccountingLines())) {
                    item.setSourceAccountingLines(distributedAccounts);
                }
            }
        }

        // update again now that distribute is finished. (Note: we may not need this anymore now that I added updateItem line above
        //leave the call below since we need to this when sequential method is used on the document.
        purapAccountingService.updateAccountAmounts(paymentRequestDocument);
    }
    
    /**
     * Update to baseline method to additionally set the payment method when the vendor is changed.
     */
    @Override
    public void changeVendor(PaymentRequestDocument preq, Integer headerId, Integer detailId) {
        super.changeVendor(preq, headerId, detailId);
        if ( preq instanceof edu.arizona.kfs.module.purap.document.PaymentRequestDocument ) {
            VendorDetail vd = vendorService.getVendorDetail(headerId, detailId);
            if (vd != null
                    && ObjectUtils.isNotNull(vd.getExtension()) ) {
                if ( vd.getExtension() instanceof VendorDetailExtension
                        && StringUtils.isNotBlank( ((VendorDetailExtension)vd.getExtension()).getDefaultB2BPaymentMethodCode() ) ) {
                    ((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)preq).setPaymentMethodCode(
                            ((VendorDetailExtension)vd.getExtension()).getDefaultB2BPaymentMethodCode() );
                }
            }
        }
    }
    
    @Override
    public void processPaymentRequestInReceivingStatus() {
        List<PaymentRequestDocument> preqs = paymentRequestDao.getPaymentRequestInReceivingStatus();

        List<PaymentRequestDocument> preqsAwaitingReceiving = new ArrayList<PaymentRequestDocument>();
        for (PaymentRequestDocument preq : preqs) {

            if (ObjectUtils.isNotNull(preq)) {
                preqsAwaitingReceiving.add(preq);
            }
        }
        for (PaymentRequestDocument preqDoc : preqsAwaitingReceiving) {
        	// UAF-2837 : Removed doc status check of APPDOC_AWAITING_RECEIVING_REVIEW because it is handled in getcollection query criteria
            if (preqDoc.isReceivingRequirementMet()) {
                try {
                    documentService.approveDocument(preqDoc, "Approved by Receiving Required PREQ job", null);
                }
                catch (WorkflowException e) {
                    LOG.error("processPaymentRequestInReceivingStatus() Error approving payment request document from awaiting receiving", e);
                    throw new RuntimeException("Error approving payment request document from awaiting receiving", e);
                }
            }
        }
    }

}