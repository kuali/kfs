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
import java.util.Date;

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsReport;

/**
 * Data holder class for Referral To Collections Report Report.
 */
public class ReferralToCollectionsReportDetailDataHolder {

    private String agencyNumber;
    private Long proposalNumber;
    private String accountNumber;
    private String invoiceNumber;
    private Date billingDate;
    private BigDecimal invoiceAmount;
    private BigDecimal openAmount;
    private String referredTo;
    private String finalDisposition;
    private String sortedFieldValue;
    private boolean displaySubtotal;
    private boolean displayTotal;
    private BigDecimal invoiceTotal;
    private BigDecimal openTotal;
    private BigDecimal invoiceSubTotal;
    private BigDecimal openSubTotal;
    private String documentNumber;

    /**
     * Default constructor.
     */
    public ReferralToCollectionsReportDetailDataHolder() {
    }

    /**
     * Constructor to initialize other values from given object.
     * 
     * @param tr ReferralToCollectionsReport object from which values to be set in data holder object.
     */
    public ReferralToCollectionsReportDetailDataHolder(ReferralToCollectionsReport rc) {
        this.agencyNumber = rc.getAgencyNumber();
        this.proposalNumber = rc.getProposalNumber();
        this.accountNumber = rc.getAccountNumber();
        this.invoiceNumber = rc.getInvoiceNumber();
        this.billingDate = rc.getBillingDate();
        this.invoiceAmount = rc.getInvoiceAmount();
        this.openAmount = rc.getOpenAmount();
        this.referredTo = rc.getReferredTo();
        this.finalDisposition = rc.getFinalDisposition();
        this.documentNumber = rc.getDocumentNumber();
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agency number.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agency number to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
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
     * Sets the proposalNumber attribute.
     * 
     * @param proposalNumber The proposalNumber to set.
     */
    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    /**
     * Gets the accountNumber attribute.
     * 
     * @return Returns the accountNumber.
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Sets the accountNumber attribute.
     * 
     * @param accountNumber The accountNumber to set.
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /**
     * Gets the invoiceNumber attribute.
     * 
     * @return Returns the invoiceNumber.
     */
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    /**
     * Sets the invoiceNumber attribute.
     * 
     * @param invoiceNumber The invoiceNumber to set.
     */
    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    /**
     * Gets the billingDate attribute.
     * 
     * @return Returns the billingDate.
     */
    public Date getBillingDate() {
        return billingDate;
    }

    /**
     * Sets the billingDate attribute.
     * 
     * @param billingDate The billingDate to set.
     */
    public void setBillingDate(Date billingDate) {
        this.billingDate = billingDate;
    }

    /**
     * Gets the invoiceAmount attribute.
     * 
     * @return Returns the invoiceAmount.
     */
    public BigDecimal getInvoiceAmount() {
        return invoiceAmount;
    }

    /**
     * Sets the invoiceAmount attribute.
     * 
     * @param invoiceAmount The invoiceAmount to set.
     */
    public void setInvoiceAmount(BigDecimal invoiceAmount) {
        this.invoiceAmount = invoiceAmount;
    }

    /**
     * Gets the openAmount attribute.
     * 
     * @return Returns the openAmount.
     */
    public BigDecimal getOpenAmount() {
        return openAmount;
    }

    /**
     * Sets the openAmount attribute.
     * 
     * @param openAmount The openAmount to set.
     */
    public void setOpenAmount(BigDecimal openAmount) {
        this.openAmount = openAmount;
    }

    /**
     * Gets the referredTo attribute.
     * 
     * @return Returns the referredTo.
     */
    public String getReferredTo() {
        return referredTo;
    }

    /**
     * Sets the referredTo attribute.
     * 
     * @param referredTo The referredTo to set.
     */
    public void setReferredTo(String referredTo) {
        this.referredTo = referredTo;
    }

    /**
     * Gets the finalDisposition attribute.
     * 
     * @return Returns the finalDisposition.
     */
    public String getFinalDisposition() {
        return finalDisposition;
    }

    /**
     * Sets the finalDisposition attribute.
     * 
     * @param finalDisposition The finalDisposition to set.
     */
    public void setFinalDisposition(String finalDisposition) {
        this.finalDisposition = finalDisposition;
    }

    /**
     * Gets the sortedFieldValue attribute.
     * 
     * @return Returns the sortedFieldValue.
     */
    public String getSortedFieldValue() {
        return sortedFieldValue;
    }

    /**
     * Sets the sortedFieldValue attribute.
     * 
     * @param sortedFieldValue The sortedFieldValue to set.
     */
    public void setSortedFieldValue(String sortedFieldValue) {
        this.sortedFieldValue = sortedFieldValue;
    }

    /**
     * Gets the displaySubtotal attribute.
     * 
     * @return Returns the displaySubtotal.
     */
    public boolean isDisplaySubtotal() {
        return displaySubtotal;
    }

    /**
     * Sets the displaySubtotal attribute.
     * 
     * @param displaySubtotal The displaySubtotal to set.
     */
    public void setDisplaySubtotal(boolean displaySubtotal) {
        this.displaySubtotal = displaySubtotal;
    }

    /**
     * Gets the displayTotal attribute.
     * 
     * @return Returns the displayTotal.
     */
    public boolean isDisplayTotal() {
        return displayTotal;
    }

    /**
     * Sets the displayTotal attribute.
     * 
     * @param displayTotal The displayTotal to set.
     */
    public void setDisplayTotal(boolean displayTotal) {
        this.displayTotal = displayTotal;
    }

    /**
     * Gets the invoiceTotal attribute.
     * 
     * @return Returns the invoiceTotal.
     */
    public BigDecimal getInvoiceTotal() {
        return invoiceTotal;
    }

    /**
     * Sets the invoiceTotal attribute.
     * 
     * @param invoiceTotal The invoiceTotal to set.
     */
    public void setInvoiceTotal(BigDecimal invoiceTotal) {
        this.invoiceTotal = invoiceTotal;
    }

    /**
     * Gets the invoiceSubTotal attribute.
     * 
     * @return Returns the invoiceSubTotal.
     */
    public BigDecimal getInvoiceSubTotal() {
        return invoiceSubTotal;
    }

    /**
     * Sets the invoiceSubTotal attribute.
     * 
     * @param invoiceSubTotal The invoiceSubTotal to set.
     */
    public void setInvoiceSubTotal(BigDecimal invoiceSubTotal) {
        this.invoiceSubTotal = invoiceSubTotal;
    }

    /**
     * Gets the openSubTotal attribute.
     * 
     * @return Returns the openSubTotal.
     */
    public BigDecimal getOpenSubTotal() {
        return openSubTotal;
    }

    /**
     * Sets the openSubTotal attribute.
     * 
     * @param openSubTotal The openSubTotal to set.
     */
    public void setOpenSubTotal(BigDecimal openSubTotal) {
        this.openSubTotal = openSubTotal;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the openTotal attribute.
     * 
     * @return Returns the openTotal.
     */
    public BigDecimal getOpenTotal() {
        return openTotal;
    }

    /**
     * Sets the openTotal attribute.
     * 
     * @param openTotal The openTotal to set.
     */
    public void setOpenTotal(BigDecimal openTotal) {
        this.openTotal = openTotal;
    }

}
