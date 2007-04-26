package org.kuali.module.purap.bo;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.kuali.core.bo.Note;


/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CreditMemoView extends AbstractRelatedView {

    private String creditMemoNumber;    
    private Integer paymentRequestIdentifier;
    private Integer purchaseOrderIdentifier;
    private String creditMemoStatusCode;    
    private boolean creditHoldIndicator;
    private String vendorCustomerNumber;    
    private Date accountsPayableApprovalDate;
    private Date creditMemoExtractedDate;    
    private Timestamp creditMemoPaidTimestamp;
    private String vendorName;
    private BigDecimal totalAmount;    
   
    /**
     * Gets the accountsPayableApprovalDate attribute. 
     * @return Returns the accountsPayableApprovalDate.
     */
    public Date getAccountsPayableApprovalDate() {
        return accountsPayableApprovalDate;
    }

    /**
     * Sets the accountsPayableApprovalDate attribute value.
     * @param accountsPayableApprovalDate The accountsPayableApprovalDate to set.
     */
    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
    }

    /**
     * Gets the creditHoldIndicator attribute. 
     * @return Returns the creditHoldIndicator.
     */
    public boolean isCreditHoldIndicator() {
        return creditHoldIndicator;
    }

    /**
     * Sets the creditHoldIndicator attribute value.
     * @param creditHoldIndicator The creditHoldIndicator to set.
     */
    public void setCreditHoldIndicator(boolean creditHoldIndicator) {
        this.creditHoldIndicator = creditHoldIndicator;
    }

    /**
     * Gets the creditMemoExtractedDate attribute. 
     * @return Returns the creditMemoExtractedDate.
     */
    public Date getCreditMemoExtractedDate() {
        return creditMemoExtractedDate;
    }

    /**
     * Sets the creditMemoExtractedDate attribute value.
     * @param creditMemoExtractedDate The creditMemoExtractedDate to set.
     */
    public void setCreditMemoExtractedDate(Date creditMemoExtractedDate) {
        this.creditMemoExtractedDate = creditMemoExtractedDate;
    }

    /**
     * Gets the creditMemoNumber attribute. 
     * @return Returns the creditMemoNumber.
     */
    public String getCreditMemoNumber() {
        return creditMemoNumber;
    }

    /**
     * Sets the creditMemoNumber attribute value.
     * @param creditMemoNumber The creditMemoNumber to set.
     */
    public void setCreditMemoNumber(String creditMemoNumber) {
        this.creditMemoNumber = creditMemoNumber;
    }

    /**
     * Gets the creditMemoStatusCode attribute. 
     * @return Returns the creditMemoStatusCode.
     */
    public String getCreditMemoStatusCode() {
        return creditMemoStatusCode;
    }

    /**
     * Sets the creditMemoStatusCode attribute value.
     * @param creditMemoStatusCode The creditMemoStatusCode to set.
     */
    public void setCreditMemoStatusCode(String creditMemoStatusCode) {
        this.creditMemoStatusCode = creditMemoStatusCode;
    }

    /**
     * Gets the paymentRequestIdentifier attribute. 
     * @return Returns the paymentRequestIdentifier.
     */
    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    /**
     * Sets the paymentRequestIdentifier attribute value.
     * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
     */
    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
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
     * Gets the creditMemoPaidTimestamp attribute. 
     * @return Returns the creditMemoPaidTimestamp.
     */
    public Timestamp getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    /**
     * Sets the creditMemoPaidTimestamp attribute value.
     * @param creditMemoPaidTimestamp The creditMemoPaidTimestamp to set.
     */
    public void setCreditMemoPaidTimestamp(Timestamp creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
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
     * Gets the totalAmount attribute. 
     * @return Returns the totalAmount.
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * Sets the totalAmount attribute value.
     * @param totalAmount The totalAmount to set.
     */
    public void setTotalAmount(BigDecimal totalAmount) {
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
