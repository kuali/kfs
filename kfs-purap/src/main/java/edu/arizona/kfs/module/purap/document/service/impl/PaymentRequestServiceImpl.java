package edu.arizona.kfs.module.purap.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;

import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;

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
}