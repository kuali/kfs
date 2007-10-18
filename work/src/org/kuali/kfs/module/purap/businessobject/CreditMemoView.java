package org.kuali.module.purap.bo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;


/**
 * Credit Memo View Business Object.
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
    private KualiDecimal totalAmount;    
   
    public Date getAccountsPayableApprovalDate() {
        return accountsPayableApprovalDate;
    }

    public void setAccountsPayableApprovalDate(Date accountsPayableApprovalDate) {
        this.accountsPayableApprovalDate = accountsPayableApprovalDate;
    }

    public boolean isCreditHoldIndicator() {
        return creditHoldIndicator;
    }

    public void setCreditHoldIndicator(boolean creditHoldIndicator) {
        this.creditHoldIndicator = creditHoldIndicator;
    }

    public Date getCreditMemoExtractedDate() {
        return creditMemoExtractedDate;
    }

    public void setCreditMemoExtractedDate(Date creditMemoExtractedDate) {
        this.creditMemoExtractedDate = creditMemoExtractedDate;
    }

    public String getCreditMemoNumber() {
        return creditMemoNumber;
    }

    public void setCreditMemoNumber(String creditMemoNumber) {
        this.creditMemoNumber = creditMemoNumber;
    }

    public Timestamp getCreditMemoPaidTimestamp() {
        return creditMemoPaidTimestamp;
    }

    public void setCreditMemoPaidTimestamp(Timestamp creditMemoPaidTimestamp) {
        this.creditMemoPaidTimestamp = creditMemoPaidTimestamp;
    }

    public String getCreditMemoStatusCode() {
        return creditMemoStatusCode;
    }

    public void setCreditMemoStatusCode(String creditMemoStatusCode) {
        this.creditMemoStatusCode = creditMemoStatusCode;
    }

    public Integer getPaymentRequestIdentifier() {
        return paymentRequestIdentifier;
    }

    public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
        this.paymentRequestIdentifier = paymentRequestIdentifier;
    }

    public Integer getPurchaseOrderIdentifier() {
        return purchaseOrderIdentifier;
    }

    public void setPurchaseOrderIdentifier(Integer purchaseOrderIdentifier) {
        this.purchaseOrderIdentifier = purchaseOrderIdentifier;
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

    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }
    
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
