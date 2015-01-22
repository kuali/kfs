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
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAward;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleBillingService;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Invoice Document for Contracts and Grants
 */
public class InvoiceGeneralDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String comment;
    private String awardDateRange;
    private String agencyNumber;
    private String billingFrequencyCode;
    private boolean finalBillIndicator;
    private String billingPeriod;
    private String instrumentTypeCode;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private KualiDecimal totalAmountBilledToDate = KualiDecimal.ZERO;
    private KualiDecimal totalPreviouslyBilled = KualiDecimal.ZERO;
    private KualiDecimal costShareAmount = KualiDecimal.ZERO;
    private Date lastBilledDate;
    private String dunningLetterTemplateAssigned;
    private Date dunningLetterTemplateSentDate;
    private Long proposalNumber;
    private String letterOfCreditCreationType;// To categorize the CG Invoices based on Award LOC Type
    private String letterOfCreditFundGroupCode;
    private String letterOfCreditFundCode;

    private ContractsGrantsInvoiceDocument invoiceDocument;
    private ContractsAndGrantsBillingAward award;

    /**
     * Gets the comment attribute.
     *
     * @return Returns the comment.
     */
    public String getComment() {
        return comment;
    }

    /**
     * Sets the comment attribute value.
     *
     * @param comment The comment to set.
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber.
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute value.
     *
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the awardDateRange attribute.
     *
     * @return Returns the awardDateRange.
     */
    public String getAwardDateRange() {
        return awardDateRange;
    }


    /**
     * Sets the awardDateRange attribute value.
     *
     * @param awardDateRange The awardDateRange to set.
     */
    public void setAwardDateRange(String awardDateRange) {
        this.awardDateRange = awardDateRange;
    }


    /**
     * Gets the billingFrequencyCode attribute.
     *
     * @return Returns the billingFrequencyCode.
     */
    public String getBillingFrequencyCode() {
        return billingFrequencyCode;
    }


    /**
     * Sets the billingFrequencyCode attribute value.
     *
     * @param billingFrequencyCode The billingFrequencyCode to set.
     */
    public void setBillingFrequencyCode(String billingFrequencyCode) {
        this.billingFrequencyCode = billingFrequencyCode;
    }


    /**
     * Gets the finalBillIndicator attribute.
     *
     * @return Returns the finalBillIndicator.
     */
    public boolean isFinalBillIndicator() {
        return finalBillIndicator;
    }


    /**
     * Sets the finalBillIndicator attribute value.
     *
     * @param finalBillIndicator The finalBillIndicator to set.
     */
    public void setFinalBillIndicator(boolean finalBillIndicator) {
        this.finalBillIndicator = finalBillIndicator;
    }


    /**
     * Gets the billingPeriod attribute.
     *
     * @return Returns the billingPeriod.
     */
    public String getBillingPeriod() {
        return billingPeriod;
    }

    /**
     * Sets the billingPeriod attribute value.
     *
     * @param billingPeriod The billingPeriod to set.
     */
    public void setBillingPeriod(String billingPeriod) {
        this.billingPeriod = billingPeriod;
    }


    /**
     * Gets the instrumentTypeCode attribute.
     *
     * @return Returns the instrumentTypeCode.
     */
    public String getInstrumentTypeCode() {
        return instrumentTypeCode;
    }


    /**
     * Sets the instrumentTypeCode attribute value.
     *
     * @param instrumentTypeCode The instrumentTypeCode to set.
     */
    public void setInstrumentTypeCode(String instrumentTypeCode) {
        this.instrumentTypeCode = instrumentTypeCode;
    }


    /**
     * Gets the awardTotal attribute.
     *
     * @return Returns the awardTotal.
     */
    public KualiDecimal getAwardTotal() {
        return awardTotal;
    }


    /**
     * Sets the awardTotal attribute value.
     *
     * @param awardTotal The awardTotal to set.
     */
    public void setAwardTotal(KualiDecimal awardTotal) {
        this.awardTotal = awardTotal;
    }


    /**
     * Gets the totalAmountBilledToDate attribute.
     *
     * @return Returns the totalAmountBilledToDate.
     */
    public KualiDecimal getTotalAmountBilledToDate() {
        return totalAmountBilledToDate;
    }


    /**
     * Sets the totalAmountBilledToDate attribute value.
     *
     * @param totalAmountBilledToDate The totalAmountBilledToDate to set.
     */
    public void setTotalAmountBilledToDate(KualiDecimal newTotalBilled) {
        this.totalAmountBilledToDate = newTotalBilled;
    }

    /**
     * Gets the amountRemainingToBill attribute.
     *
     * @return Returns the amountRemainingToBill.
     */
    public KualiDecimal getAmountRemainingToBill() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = getAwardTotal().subtract(getTotalAmountBilledToDate());
        return total;
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
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("comment", this.comment);
        m.put("awardDateRange", this.awardDateRange);
        m.put("billingFrequency", this.billingFrequencyCode);
        m.put("billingPeriod", this.billingPeriod);
        m.put("instrumentTypeCode", this.instrumentTypeCode);
        if (ObjectUtils.isNotNull(this.awardTotal)) {
            m.put("awardTotal", this.awardTotal.toString());
        }
        if (ObjectUtils.isNotNull(this.totalAmountBilledToDate)) {
            m.put("totalAmountBilledToDate", this.totalAmountBilledToDate.toString());
        }
        if (ObjectUtils.isNotNull(this.totalPreviouslyBilled)) {
            m.put("totalPreviouslyBilled", this.totalPreviouslyBilled.toString());
        }
        if (ObjectUtils.isNotNull(this.costShareAmount)) {
            m.put("costShareAmount", this.costShareAmount.toString());
        }
        if (ObjectUtils.isNotNull(this.lastBilledDate)) {
            m.put("lastBilledDate", this.lastBilledDate.toString());
        }
        return m;
    }

    /**
     * Gets the totalPreviouslyBilled attribute.
     *
     * @return Returns the totalPreviouslyBilled.
     */
    public KualiDecimal getTotalPreviouslyBilled() {
        return totalPreviouslyBilled;
    }

    /**
     * Sets the totalPreviouslyBilled attribute value.
     *
     * @param totalPreviouslyBilled The totalPreviouslyBilled to set.
     */
    public void setTotalPreviouslyBilled(KualiDecimal billedToDateAmount) {
        this.totalPreviouslyBilled = billedToDateAmount;
    }

    /**
     * Gets the costShareAmount attribute.
     *
     * @return Returns the costShareAmount.
     */
    public KualiDecimal getCostShareAmount() {
        return costShareAmount;
    }

    /**
     * Sets the costShareAmount attribute value.
     *
     * @param costShareAmount The costShareAmount to set.
     */
    public void setCostShareAmount(KualiDecimal costShareAmount) {
        this.costShareAmount = costShareAmount;
    }

    /**
     * Gets the invoiceDocument attribute.
     *
     * @return Returns the invoiceDocument.
     */
    public ContractsGrantsInvoiceDocument getInvoiceDocument() {
        return invoiceDocument;
    }

    /**
     * Sets the invoiceDocument attribute value.
     *
     * @param invoiceDocument The invoiceDocument to set.
     */

    public void setInvoiceDocument(ContractsGrantsInvoiceDocument invoiceDocument) {
        this.invoiceDocument = invoiceDocument;
    }

    /**
     * Gets the lastBilledDate attribute.
     *
     * @return Returns the lastBilledDate.
     */
    public Date getLastBilledDate() {
        return lastBilledDate;
    }

    /**
     * Sets the lastBilledDate attribute value.
     *
     * @param lastBilledDate The lastBilledDate to set.
     */
    public void setLastBilledDate(Date lastBilledDate) {
        this.lastBilledDate = lastBilledDate;
    }

    /**
     * Gets the dunningLetterTemplateAssigned attribute.
     *
     * @return Returns the dunningLetterTemplateAssigned.
     */
    public String getDunningLetterTemplateAssigned() {
        return dunningLetterTemplateAssigned;
    }

    /**
     * Sets the dunningLetterTemplateAssigned attribute value.
     *
     * @param dunningLetterTemplateAssigned The dunningLetterTemplateAssigned to set.
     */
    public void setDunningLetterTemplateAssigned(String dunningLetterTemplateAssigned) {
        this.dunningLetterTemplateAssigned = dunningLetterTemplateAssigned;
    }

    /**
     * Gets the dunningLetterTemplateSentDate attribute.
     *
     * @return Returns the dunningLetterTemplateSentDate.
     */
    public Date getDunningLetterTemplateSentDate() {
        return dunningLetterTemplateSentDate;
    }

    /**
     * Sets the dunningLetterTemplateSentDate attribute value.
     *
     * @param dunningLetterTemplateSentDate The dunningLetterTemplateSentDate to set.
     */
    public void setDunningLetterTemplateSentDate(Date dunningLetterTemplateSentDate) {
        this.dunningLetterTemplateSentDate = dunningLetterTemplateSentDate;
    }

    public Long getProposalNumber() {
        return proposalNumber;
    }

    public void setProposalNumber(Long proposalNumber) {
        this.proposalNumber = proposalNumber;
    }

    public String getLetterOfCreditCreationType() {
        return letterOfCreditCreationType;
    }

    public void setLetterOfCreditCreationType(String letterOfCreditCreationType) {
        this.letterOfCreditCreationType = letterOfCreditCreationType;
    }

    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    /**
     * Gets the award attribute.
     *
     * @return Returns the award.
     */
    public ContractsAndGrantsBillingAward getAward() {
        award = SpringContext.getBean(ContractsAndGrantsModuleBillingService.class).updateAwardIfNecessary(proposalNumber, award);
        return award;
    }

    /**
     * Sets the award attribute value.
     *
     * @param award The award to set.
     */
    public void setAward(ContractsAndGrantsBillingAward award) {
        this.award = award;
    }

}
