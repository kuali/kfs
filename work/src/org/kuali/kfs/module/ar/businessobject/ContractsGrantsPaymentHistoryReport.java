/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Defines an entry in the Contracts and Grants Invoice Payment History Report.
 */
public class ContractsGrantsPaymentHistoryReport extends TransientBusinessObjectBase {

    private String paymentNumber;
    private Date paymentDate;
    private String customerNumber;
    private String customerName;
    private KualiDecimal paymentAmount;
    private String invoiceNumber;
    private KualiDecimal invoiceAmount;
    private String awardNumber;
    private boolean reversedIndicator;
    private boolean appliedIndicator;

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();

        if (this.paymentDate != null) {
            m.put("paymentDate", this.paymentDate.toString());
        }
        if (this.paymentAmount != null) {
            m.put("paymentAmount", this.paymentAmount.toString());
        }
        if (this.invoiceAmount != null) {
            m.put("invoiceAmount", this.invoiceAmount.toString());
        }

        m.put("paymentNumber", this.paymentNumber);
        m.put(KFSPropertyConstants.CUSTOMER_NUMBER, this.customerNumber);
        m.put("customerName", this.customerName);
        m.put("invoiceNumber", this.invoiceNumber);
        m.put("awardNumber", this.awardNumber);
//        m.put("reversedIndicator", this.reversedIndicator);
//        m.put("appliedIndicator", this.appliedIndicator);

        return m;
    }

    /**
     * Gets the paymentNumber attribute.
     *
     * @return Returns the paymentNumber 
     */
    public String getPaymentNumber() {
        return paymentNumber;
    }

    /**
     * Sets the paymentNumber attribute value.
     *
     * @param paymentNumber The paymentNumber to set.
     */
    public void setPaymentNumber(String paymentNumber) {
        this.paymentNumber = paymentNumber;
    }

    /**
     * Gets the paymentDate attribute.
     *
     * @return Returns the paymentDate 
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * Sets the paymentDate attribute value.
     *
     * @param paymentDate The paymentDate to set.
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber 
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    /**
     * Gets the customerName attribute.
     *
     * @return Returns the customerName 
     */
    public String getCustomerName() {
        return customerName;
    }

    /**
     * Sets the customerName attribute value.
     *
     * @param customerName The customerName to set.
     */
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * Gets the paymentAmount attribute.
     *
     * @return Returns the paymentAmount 
     */
    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the paymentAmount attribute value.
     *
     * @param paymentAmount The paymentAmount to set.
     */
    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Gets the invoiceNumber attribute.
     *
     * @return Returns the invoiceNumber 
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute value.
     *
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the invoiceAmount attribute.
     *
     * @return Returns the invoiceAmount 
     */
    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     *
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the awardNumber attribute.
     *
     * @return Returns the awardNumber 
     */
    public String getAwardNumber() {
        return awardNumber;
    }

    /**
     * Sets the awardNumber attribute value.
     *
     * @param awardNumber The awardNumber to set.
     */
    public void setAwardNumber(String awardNumber) {
        this.awardNumber = awardNumber;
    }

    /**
     * Gets the reversedIndicator attribute.
     * @return the reversedIndicator
     */
    public boolean isReversedIndicator() {
        return reversedIndicator;
    }

    /**
     * Sets the reversedIndicator attribute value.
     * @param reversedIndicator the reversedIndicator to set
     */
    public void setReversedIndicator(boolean reversedIndicator) {
        this.reversedIndicator = reversedIndicator;
    }

    /**
     * Gets the appliedIndicator attribute.
     * @return the appliedIndicator
     */
    public boolean isAppliedIndicator() {
        return appliedIndicator;
    }

    /**
     * Sets the appliedIndicator attribute value.
     * @param appliedIndicator the appliedIndicator to set
     */
    public void setAppliedIndicator(boolean appliedIndicator) {
        this.appliedIndicator = appliedIndicator;
    }

    
}
