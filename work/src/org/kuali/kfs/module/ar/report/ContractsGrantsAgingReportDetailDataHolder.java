/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.ar.report;

import java.math.BigDecimal;
import java.sql.Date;

/**
 * Defines a data holder class for the Contracts and Grants Aging Report.
 */
public class ContractsGrantsAgingReportDetailDataHolder {

    private String agencyName;
    private String proposalNumber;
    private String documentNumber;
    private String invoiceType;
    private Date invoiceDate;
    private Date awardEndDate;
    private String customerNumber;
    private String sortedFieldValue;
    private BigDecimal invoiceAmount;
    private BigDecimal paymentAmount;
    private BigDecimal remainingAmount;
    private Long ageInDays;
    private BigDecimal invoiceSubTotal;
    private BigDecimal paymentSubTotal;
    private BigDecimal remainingSubTotal;
    public boolean displaySubtotalInd;
    public boolean displayTotalInd;
    private BigDecimal invoiceTotal;
    private BigDecimal paymentTotal;
    private BigDecimal remainingTotal;
    private Date lastEventDate;
    private String collectorName;
    private String agencyNumber;


    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
     */
    public String getProposalNumber() {
        return proposalNumber;
    }

    /**
     * Sets the proposalNumber attribute value.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(String proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the invoiceType attribute.
     * 
     * @return Returns the invoiceType
     */
    public String getInvoiceType() {
        return invoiceType;
    }

    /**
     * Sets the invoiceType attribute value.
     * 
     * @param invoiceType The invoiceType to set.
     */
    public void setInvoiceType(String invoiceType) {
        this.invoiceType = invoiceType;
    }

    /**
     * Gets the invoiceDate attribute.
     * 
     * @return Returns the invoiceDate
     */
    public Date getInvoiceDate() {
        return invoiceDate;
    }

    /**
     * Sets the invoiceDate attribute value.
     * 
     * @param invoiceDate The invoiceDate to set.
     */
    public void setInvoiceDate(Date invoiceDate) {
        this.invoiceDate = invoiceDate;
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
     * Gets the sortedFieldValue attribute.
     * 
     * @return Returns the sortedFieldValue
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * Sets the sortedFieldValue attribute value.
     * 
     * @param sortedFieldValue The sortedFieldValue to set.
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * Gets the invoiceAmount attribute.
     * 
     * @return Returns the invoiceAmount
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute value.
     * 
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the paymentAmount attribute.
     * 
     * @return Returns the paymentAmount
     */
    public BigDecimal getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the paymentAmount attribute value.
     * 
     * @param paymentAmount The paymentAmount to set.
     */
    public void setPaymentAmount(BigDecimal paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    /**
     * Gets the remainingAmount attribute.
     * 
     * @return Returns the remainingAmount
     */
    public BigDecimal getRemainingAmount() {
        return remainingAmount;
    }

    /**
     * Sets the remainingAmount attribute value.
     * 
     * @param remainingAmount The remainingAmount to set.
     */
    public void setRemainingAmount(BigDecimal remainingAmount) {
        this.remainingAmount = remainingAmount;
    }

    /**
     * Gets the ageInDays attribute.
     * 
     * @return Returns the ageInDays
     */
    public Long getAgeInDays() {
        return ageInDays;
    }

    /**
     * Sets the ageInDays attribute value.
     * 
     * @param ageInDays The ageInDays to set.
     */
    public void setAgeInDays(Long ageInDays) {
        this.ageInDays = ageInDays;
    }

    /**
     * Gets the invoiceSubTotal attribute.
     * 
     * @return Returns the invoiceSubTotal
     */
    public BigDecimal getInvoiceSubTotal() {
        return invoiceSubTotal;
    }

    /**
     * Sets the invoiceSubTotal attribute value.
     * 
     * @param invoiceSubTotal The invoiceSubTotal to set.
     */
    public void setInvoiceSubTotal(BigDecimal invoiceSubTotal) {
        this.invoiceSubTotal = invoiceSubTotal;
    }

    /**
     * Gets the paymentSubTotal attribute.
     * 
     * @return Returns the paymentSubTotal
     */
    public BigDecimal getPaymentSubTotal() {
        return paymentSubTotal;
    }

    /**
     * Sets the paymentSubTotal attribute value.
     * 
     * @param paymentSubTotal The paymentSubTotal to set.
     */
    public void setPaymentSubTotal(BigDecimal paymentSubTotal) {
        this.paymentSubTotal = paymentSubTotal;
    }

    /**
     * Gets the remainingSubTotal attribute.
     * 
     * @return Returns the remainingSubTotal
     */
    public BigDecimal getRemainingSubTotal() {
        return remainingSubTotal;
    }

    /**
     * Sets the remainingSubTotal attribute value.
     * 
     * @param remainingSubTotal The remainingSubTotal to set.
     */
    public void setRemainingSubTotal(BigDecimal remainingSubTotal) {
        this.remainingSubTotal = remainingSubTotal;
    }

    /**
     * Gets the displaySubtotalInd attribute.
     * 
     * @return Returns the displaySubtotalInd
     */
    public boolean isDisplaySubtotalInd() {
        return displaySubtotalInd;
    }

    /**
     * Sets the displaySubtotalInd attribute value.
     * 
     * @param displaySubtotalInd The displaySubtotalInd to set.
     */
    public void setDisplaySubtotalInd(boolean displaySubtotalInd) {
        this.displaySubtotalInd = displaySubtotalInd;
    }

    /**
     * Gets the agencyName attribute.
     * 
     * @return Returns the agencyName
     */
    public String getAgencyName() {
        return agencyName;
    }

    /**
     * Sets the agencyName attribute value.
     * 
     * @param agencyName The agencyName to set.
     */
    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    /**
     * Gets the awardEndDate attribute.
     * 
     * @return Returns the awardEndDate
     */
    public Date getAwardEndDate() {
        return awardEndDate;
    }

    /**
     * Sets the awardEndDate attribute value.
     * 
     * @param awardEndDate The awardEndDate to set.
     */
    public void setAwardEndDate(Date awardEndDate) {
        this.awardEndDate = awardEndDate;
    }

    /**
     * Gets the displayTotalInd attribute.
     * 
     * @return Returns the displayTotalInd
     */
    public boolean isDisplayTotalInd() {
        return displayTotalInd;
    }

    /**
     * Sets the displayTotalInd attribute value.
     * 
     * @param displayTotalInd The displayTotalInd to set.
     */
    public void setDisplayTotalInd(boolean displayTotalInd) {
        this.displayTotalInd = displayTotalInd;
    }

    /**
     * Gets the invoiceTotal attribute.
     * 
     * @return Returns the invoiceTotal
     */
    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    /**
     * Sets the invoiceTotal attribute value.
     * 
     * @param invoiceTotal The invoiceTotal to set.
     */
    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    /**
     * Gets the paymentTotal attribute.
     * 
     * @return Returns the paymentTotal
     */
    public BigDecimal getPaymentTotal() {
        return paymentTotal;
    }

    /**
     * Sets the paymentTotal attribute value.
     * 
     * @param paymentTotal The paymentTotal to set.
     */
    public void setPaymentTotal(BigDecimal paymentTotal) {
        this.paymentTotal = paymentTotal;
    }

    /**
     * Gets the remainingTotal attribute.
     * 
     * @return Returns the remainingTotal
     */
    public BigDecimal getRemainingTotal() {
        return remainingTotal;
    }

    /**
     * Sets the remainingTotal attribute value.
     * 
     * @param remainingTotal The remainingTotal to set.
     */
    public void setRemainingTotal(BigDecimal remainingTotal) {
        this.remainingTotal = remainingTotal;
    }

    /**
     * Gets the lastEventDate attribute.
     * 
     * @return Returns the lastEventDate
     */
    public Date getLastEventDate() {
        return lastEventDate;
    }

    /**
     * Sets the lastEventDate attribute value.
     * 
     * @param lastEventDate The lastEventDate to set.
     */
    public void setLastEventDate(Date lastEventDate) {
        this.lastEventDate = lastEventDate;
    }

    /**
     * Gets the collectorName attribute.
     * 
     * @return Returns the collectorName
     */
    public String getCollectorName() {
        return collectorName;
    }

    /**
     * Sets the collectorName attribute value.
     * 
     * @param collectorName The collectorName to set.
     */
    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    /**
     * Gets the agnecyNumber attribute.
     * 
     * @return Returns the agnecyNumber
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agnecyNumber attribute value.
     * 
     * @param agnecyNumber The agnecyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

}
