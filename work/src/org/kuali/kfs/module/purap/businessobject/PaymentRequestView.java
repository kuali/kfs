package org.kuali.module.purap.bo;

import java.sql.Date;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
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

    /**
     * Gets the invoiceNumber attribute. 
     * @return Returns the invoiceNumber.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute value.
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the paymentExtractedDate attribute. 
     * @return Returns the paymentExtractedDate.
     */
    public Date getPaymentExtractedDate() {
        return paymentExtractedDate;
    }

    /**
     * Sets the paymentExtractedDate attribute value.
     * @param paymentExtractedDate The paymentExtractedDate to set.
     */
    public void setPaymentExtractedDate(Date paymentExtractedDate) {
        this.paymentExtractedDate = paymentExtractedDate;
    }

    /**
     * Gets the paymentHoldIndicator attribute. 
     * @return Returns the paymentHoldIndicator.
     */
    public boolean isPaymentHoldIndicator() {
        return paymentHoldIndicator;
    }

    /**
     * Sets the paymentHoldIndicator attribute value.
     * @param paymentHoldIndicator The paymentHoldIndicator to set.
     */
    public void setPaymentHoldIndicator(boolean paymentHoldIndicator) {
        this.paymentHoldIndicator = paymentHoldIndicator;
    }

    /**
     * Gets the paymentPaidDate attribute. 
     * @return Returns the paymentPaidDate.
     */
    public Date getPaymentPaidDate() {
        return paymentPaidDate;
    }

    /**
     * Sets the paymentPaidDate attribute value.
     * @param paymentPaidDate The paymentPaidDate to set.
     */
    public void setPaymentPaidDate(Date paymentPaidDate) {
        this.paymentPaidDate = paymentPaidDate;
    }

    /**
     * Gets the paymentRequestedCancelIndicator attribute. 
     * @return Returns the paymentRequestedCancelIndicator.
     */
    public boolean isPaymentRequestedCancelIndicator() {
        return paymentRequestedCancelIndicator;
    }

    /**
     * Sets the paymentRequestedCancelIndicator attribute value.
     * @param paymentRequestedCancelIndicator The paymentRequestedCancelIndicator to set.
     */
    public void setPaymentRequestedCancelIndicator(boolean paymentRequestedCancelIndicator) {
        this.paymentRequestedCancelIndicator = paymentRequestedCancelIndicator;
    }

    /**
     * Gets the paymentRequestPayDate attribute. 
     * @return Returns the paymentRequestPayDate.
     */
    public Date getPaymentRequestPayDate() {
        return paymentRequestPayDate;
    }

    /**
     * Sets the paymentRequestPayDate attribute value.
     * @param paymentRequestPayDate The paymentRequestPayDate to set.
     */
    public void setPaymentRequestPayDate(Date paymentRequestPayDate) {
        this.paymentRequestPayDate = paymentRequestPayDate;
    }

    /**
     * Gets the purchaseOrderIdentifier attribute. 
     * @return Returns the purchaseOrderIdentifier.
     */
    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    /**
     * Sets the purchaseOrderIdentifier attribute value.
     * @param purchaseOrderIdentifier The purchaseOrderIdentifier to set.
     */
    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
    }

    /**
     * Gets the statusCode attribute. 
     * @return Returns the statusCode.
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     * Sets the statusCode attribute value.
     * @param statusCode The statusCode to set.
     */
    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Gets the totalAmount attribute. 
     * @return Returns the totalAmount.
     */
    public KualiDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the totalAmount attribute value.
     * @param totalAmount The totalAmount to set.
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * Gets the vendorCustomerNumber attribute. 
     * @return Returns the vendorCustomerNumber.
     */
    public String getVendorCustomerNumber() {
        return vendorCustomerNumber;
    }

    /**
     * Sets the vendorCustomerNumber attribute value.
     * @param vendorCustomerNumber The vendorCustomerNumber to set.
     */
    public void setVendorCustomerNumber(String vendorCustomerNumber) {
        this.vendorCustomerNumber = vendorCustomerNumber;
    }

    /**
     * Gets the vendorName attribute. 
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute value.
     * @param vendorName The vendorName to set.
     */
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

    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }
    
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
