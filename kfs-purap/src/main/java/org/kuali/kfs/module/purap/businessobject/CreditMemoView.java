/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.core.web.format.DateFormatter;
import org.kuali.rice.krad.bo.Note;


/**
 * Credit Memo View Business Object.
 */
public class CreditMemoView extends AbstractRelatedView {
    private String creditMemoNumber;
    private Integer paymentRequestIdentifier;
    private Integer purchaseOrderIdentifier;
    private boolean creditHoldIndicator;
    private String vendorCustomerNumber;
    private Timestamp accountsPayableApprovalTimestamp;
    private Timestamp creditMemoExtractedTimestamp;
    private Timestamp creditMemoPaidTimestamp;
    private String vendorName;

    // GETTERS & SETTERS
    public Object getTotalAmount() {
        return (new CurrencyFormatter()).format(documentHeader.getFinancialDocumentTotalAmount());
    }

    public Object getAccountsPayableApprovalTimestamp() {
        return (new DateFormatter()).format(accountsPayableApprovalTimestamp);
    }

    public void setAccountsPayableApprovalTimestamp(Timestamp accountsPayableApprovalTimestamp) {
        this.accountsPayableApprovalTimestamp = accountsPayableApprovalTimestamp;
    }

    public boolean isCreditHoldIndicator() {
        return creditHoldIndicator;
    }

    public void setCreditHoldIndicator(boolean creditHoldIndicator) {
        this.creditHoldIndicator = creditHoldIndicator;
    }

    public Timestamp getCreditMemoExtractedTimestamp() {
        return creditMemoExtractedTimestamp;
    }

    public void setCreditMemoExtractedTimestamp(Timestamp creditMemoExtractedTimestamp) {
        this.creditMemoExtractedTimestamp = creditMemoExtractedTimestamp;
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
     * @return workflow document type for the VendorCreditMemoDocument
     */
    public String getDocumentType() {
        return KFSConstants.FinancialDocumentTypeCodes.VENDOR_CREDIT_MEMO;
    }

    /**
     * The next three methods are overridden but shouldnt be! If they arent overridden, they dont show up in the tag, not sure why
     * at this point! (AAP)
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    @Override
    public String getDocumentIdentifierString() {
        return super.getDocumentIdentifierString();
    }

    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }

    @Override
    public String getUrl() {
        return super.getUrl();
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.AbstractRelatedView#getDocumentTypeName()
     */
    @Override
    public String getDocumentTypeName() {
        return KFSConstants.FinancialDocumentTypeCodes.VENDOR_CREDIT_MEMO;
    }
}
