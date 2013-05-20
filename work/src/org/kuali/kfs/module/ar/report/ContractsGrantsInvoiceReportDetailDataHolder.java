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
 * Defines a data holder class for the Contracts and Grants Invoice Report.
 */
public class ContractsGrantsInvoiceReportDetailDataHolder {
    private Long proposalNumber;
    private String documentNumber;
    private String invoiceType;
    private Date invoiceDate;
    private Date invoiceDueDate;
    private String openInvoiceIndicator;
    private String customerNumber;
    private String customerName;
    private String sortedFieldValue;
    private BigDecimal invoiceAmount;
    private BigDecimal paymentAmount;
    private BigDecimal remainingAmount;
    private Long ageInDays;
    private BigDecimal invoiceSubTotal;
    private BigDecimal paymentSubTotal;
    private BigDecimal remainingSubTotal;
    public boolean displaySubtotalInd;

    /**
     * Gets the proposalNumber attribute.
     * 
     * @return Returns the proposalNumber
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
     * Gets the invoiceDueDate attribute.
     * 
     * @return Returns the invoiceDueDate
     */
    public Date getInvoiceDueDate() {
        return invoiceDueDate;
    }

    /**
     * Sets the invoiceDueDate attribute value.
     * 
     * @param invoiceDueDate The invoiceDueDate to set.
     */
    public void setInvoiceDueDate(Date invoiceDueDate) {
        this.invoiceDueDate = invoiceDueDate;
    }

    /**
     * Gets the openInvoiceIndicator attribute.
     * 
     * @return Returns the openInvoiceIndicator
     */
    public String getOpenInvoiceIndicator() {
        return openInvoiceIndicator;
    }

    /**
     * Sets the openInvoiceIndicator attribute value.
     * 
     * @param openInvoiceIndicator The openInvoiceIndicator to set.
     */
    public void setOpenInvoiceIndicator(String openInvoiceIndicator) {
        this.openInvoiceIndicator = openInvoiceIndicator;
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
}
