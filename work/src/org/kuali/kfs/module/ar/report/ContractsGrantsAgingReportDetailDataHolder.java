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
    public boolean displaySubtotal;
    public boolean displayTotal;
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
     * Gets the displaySubtotal attribute.
     * 
     * @return Returns the displaySubtotal
     */
    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    /**
     * Sets the displaySubtotal attribute value.
     * 
     * @param displaySubtotal The displaySubtotal to set.
     */
    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
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
     * Gets the displayTotal attribute.
     * 
     * @return Returns the displayTotal
     */
    public boolean isDisplayTotal() {
        return displayTotal;
    }

    /**
     * Sets the displayTotal attribute value.
     * 
     * @param displayTotal The displayTotal to set.
     */
    public void setDisplayTotal(boolean displayTotal) {
        this.displayTotal = displayTotal;
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
