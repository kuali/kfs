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
