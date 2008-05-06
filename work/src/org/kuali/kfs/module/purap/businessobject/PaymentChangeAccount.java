/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;

/**
 * Payment Change Account Business Object.
 */
public class PaymentChangeAccount extends PersistableBusinessObjectBase {

    private Integer paymentChangeIdentifier;
    private Integer itemLineNumber;
    private String financialSystemOriginationCode;
    private String documentNumber;
    private Integer transactionLedgerEntrySequenceNumber;
    private KualiDecimal itemAccountTotalAmount;

    private GeneralLedgerPendingEntry generalLedgerPendingEntry;
    
    /**
     * Default constructor.
     */
    public PaymentChangeAccount() {

    }

    public Integer getPaymentChangeIdentifier() {
        return paymentChangeIdentifier;
    }

    public void setPaymentChangeIdentifier(Integer paymentChangeIdentifier) {
        this.paymentChangeIdentifier = paymentChangeIdentifier;
    }

    /**
     * Gets the documentNumber attribute. 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialSystemOriginationCode attribute. 
     * @return Returns the financialSystemOriginationCode.
     */
    public String getFinancialSystemOriginationCode() {
        return financialSystemOriginationCode;
    }

    /**
     * Sets the financialSystemOriginationCode attribute value.
     * @param financialSystemOriginationCode The financialSystemOriginationCode to set.
     */
    public void setFinancialSystemOriginationCode(String financialSystemOriginationCode) {
        this.financialSystemOriginationCode = financialSystemOriginationCode;
    }

    /**
     * Gets the transactionLedgerEntrySequenceNumber attribute. 
     * @return Returns the transactionLedgerEntrySequenceNumber.
     */
    public Integer getTransactionLedgerEntrySequenceNumber() {
        return transactionLedgerEntrySequenceNumber;
    }

    /**
     * Sets the transactionLedgerEntrySequenceNumber attribute value.
     * @param transactionLedgerEntrySequenceNumber The transactionLedgerEntrySequenceNumber to set.
     */
    public void setTransactionLedgerEntrySequenceNumber(Integer transactionLedgerEntrySequenceNumber) {
        this.transactionLedgerEntrySequenceNumber = transactionLedgerEntrySequenceNumber;
    }

    public KualiDecimal getItemAccountTotalAmount() {
        return itemAccountTotalAmount;
    }

    public void setItemAccountTotalAmount(KualiDecimal itemAccountTotalAmount) {
        this.itemAccountTotalAmount = itemAccountTotalAmount;
    }

    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }

    /**
     * Gets the generalLedgerPendingEntry attribute. 
     * @return Returns the generalLedgerPendingEntry.
     */
    public GeneralLedgerPendingEntry getGeneralLedgerPendingEntry() {
        return generalLedgerPendingEntry;
    }

    /**
     * Sets the generalLedgerPendingEntry attribute value.
     * @param generalLedgerPendingEntry The generalLedgerPendingEntry to set.
     * @deprecated
     */
    public void setGeneralLedgerPendingEntry(GeneralLedgerPendingEntry generalLedgerPendingEntry) {
        this.generalLedgerPendingEntry = generalLedgerPendingEntry;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.paymentChangeIdentifier != null) {
            m.put("paymentChangeIdentifier", this.paymentChangeIdentifier.toString());
        }
        if (this.itemLineNumber != null) {
            m.put("itemLineNumber", this.itemLineNumber.toString());
        }
        m.put("financialSystemOriginationCode", this.financialSystemOriginationCode);
        m.put("documentNumber", this.documentNumber);
        if (this.transactionLedgerEntrySequenceNumber != null) {
            m.put("transactionLedgerEntrySequenceNumber", this.transactionLedgerEntrySequenceNumber.toString());
        }
        return m;
    }
}
