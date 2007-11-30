/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.bo;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Note;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapPropertyConstants;


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
    
    // REFERENCE OBJECTS
    private Status status;

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
    
    public Status getStatus() {
        if (ObjectUtils.isNull(this.status) && StringUtils.isNotEmpty(this.getCreditMemoStatusCode())) {
            this.refreshReferenceObject(PurapPropertyConstants.STATUS);
        }
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
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
     * The next three methods are overridden but shouldnt be! If they arent overridden, they dont show up in the tag, not sure why
     * at this point! (AAP)
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
