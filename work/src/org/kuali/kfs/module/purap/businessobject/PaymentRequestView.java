package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;

/**
 * Payment Request View Business Object.
 */
public class PaymentRequestView extends AbstractRelatedView {

    private String invoiceNumber;
    private Integer purchaseOrderIdentifier;    
    private String statusCode;
    private boolean paymentHoldIndicator;    
    private boolean paymentRequestedCancelIndicator;
    private String vendorName;
    private String vendorCustomerNumber; 
    private Date paymentRequestPayDate;    
    private Date paymentExtractedDate;
    private Date paymentPaidDate;
    private KualiDecimal totalAmount;    

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public Date getPaymentExtractedDate() {
        return paymentExtractedDate;
    }

    public void setPaymentExtractedDate(Date paymentExtractedDate) {
        this.paymentExtractedDate = paymentExtractedDate;
    }

    public boolean isPaymentHoldIndicator() {
        return paymentHoldIndicator;
    }

    public void setPaymentHoldIndicator(boolean paymentHoldIndicator) {
        this.paymentHoldIndicator = paymentHoldIndicator;
    }

    public Date getPaymentPaidDate() {
        return paymentPaidDate;
    }

    public void setPaymentPaidDate(Date paymentPaidDate) {
        this.paymentPaidDate = paymentPaidDate;
    }

    public boolean isPaymentRequestedCancelIndicator() {
        return paymentRequestedCancelIndicator;
    }

    public void setPaymentRequestedCancelIndicator(boolean paymentRequestedCancelIndicator) {
        this.paymentRequestedCancelIndicator = paymentRequestedCancelIndicator;
    }

    public Date getPaymentRequestPayDate() {
        return paymentRequestPayDate;
    }

    public void setPaymentRequestPayDate(Date paymentRequestPayDate) {
        this.paymentRequestPayDate = paymentRequestPayDate;
    }

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getVendorCustomerNumber() {
        return vendorCustomerNumber;
    }

    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * The next three methods are overridden but shouldnt be!
     * If they arent overridden, they dont show up in the tag, not sure why at this point! (AAP)
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    /**
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }
    
    /**
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getUrl()
     */
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
