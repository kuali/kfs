/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.businessobject;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
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

    // REFERENCE OBJECTS
    private FinancialSystemDocumentHeader documentHeader;


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
