/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

/**
 * Defines an entry in the Contracts and Grants Invoice Lookup Result.
 */
public class CollectionActivityInvoiceLookup extends TransientBusinessObjectBase {


    private Long proposalNumber;
    private Long agencyNumber;
    private String agencyName;
    private Long customerNumber;
    private String customerName;


    private String invoiceNumber;
    private String accountNumber;
    private Date invoiceDate;
    private String billingPeriod;
    private KualiDecimal invoiceAmount;
    private String billingFrequency;
    private KualiDecimal paymentAmount = KualiDecimal.ZERO;
    private KualiDecimal balanceDue = KualiDecimal.ZERO;
    private Integer age;
    private Date lastPaymentDate;

    public CollectionActivityInvoiceLookup() {

    }


    /**
     * Gets the proposalNumber attribute.
     *
     * @return Returns the proposalNumber.
     */
    public Long getProposalNumber() {
        return proposalNumber;
    }


    /**
     * Sets the proposalNumber attribute value.
     *
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }



    public Long getAgencyNumber() {
        return agencyNumber;
    }


    public void setAgencyNumber(Long agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    public String getAgencyName() {
        return agencyName;
    }


    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }


    public Long getCustomerNumber() {
        return customerNumber;
    }


    public void setCustomerNumber(Long customerNumber) {
        this.customerNumber = customerNumber;
    }


    public String getCustomerName() {
        return customerName;
    }


    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }


    public KualiDecimal getPaymentAmount() {
        return paymentAmount;
    }


    public void setPaymentAmount(KualiDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }


    public KualiDecimal getBalanceDue() {
        return balanceDue;
    }


    public void setBalanceDue(KualiDecimal balanceDue) {
        this.balanceDue = balanceDue;
    }


    public String getInvoiceNumber() {
        return invoiceNumber;
    }


    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }


    public String getAccountNumber() {
        return accountNumber;
    }


    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }


    public Date getInvoiceDate() {
        return invoiceDate;
    }


    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
    }



    public String getBillingPeriod() {
        return billingPeriod;
    }


    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }


    public KualiDecimal getInvoiceAmount() {
        return invoiceAmount;
    }


    public void setInvoiceAmount(KualiDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }


    public String getBillingFrequency() {
        return billingFrequency;
    }


    public void setBillingFrequency(String billingFrequency) {
        this.billingFrequency = billingFrequency;
    }


    public Integer getAge() {
        return age;
    }


    public void setAge(Integer age) {
        this.age = age;
    }


    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }


    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }


    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        if (this.proposalNumber != null) {
            m.put(KFSPropertyConstants.PROPOSAL_NUMBER, this.proposalNumber.toString());
        }
                return m;
    }

    public List<String> getAwardAttributesForDisplay() {
        List<String> awardAttributesForDisplay = new ArrayList<String>();
        awardAttributesForDisplay.add(KFSPropertyConstants.PROPOSAL_NUMBER);
        return awardAttributesForDisplay;
    }
}
