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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
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
    private String billingFrequency;
    private boolean finalBillIndicator;
    private String billingPeriod;
    private String instrumentTypeCode;
    private KualiDecimal awardTotal = KualiDecimal.ZERO;
    private KualiDecimal newTotalBilled = KualiDecimal.ZERO;
    private KualiDecimal billedToDateAmount = KualiDecimal.ZERO;
    private KualiDecimal costShareAmount = KualiDecimal.ZERO;
    private Date lastBilledDate;
    private String dunningLetterTemplateAssigned;
    private Date dunningLetterTemplateSentDate;

    private ContractsGrantsInvoiceDocument invoiceDocument;

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
     * Gets the billingFrequency attribute.
     *
     * @return Returns the billingFrequency.
     */
    public String getBillingFrequency() {
        return billingFrequency;
    }


    /**
     * Sets the billingFrequency attribute value.
     *
     * @param billingFrequency The billingFrequency to set.
     */
    public void setBillingFrequency(String billingFrequency) {
        this.billingFrequency = billingFrequency;
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
     * Gets the newTotalBilled attribute.
     *
     * @return Returns the newTotalBilled.
     */
    public KualiDecimal getNewTotalBilled() {
        return newTotalBilled;
    }


    /**
     * Sets the newTotalBilled attribute value.
     *
     * @param newTotalBilled The newTotalBilled to set.
     */
    public void setNewTotalBilled(KualiDecimal newTotalBilled) {
        this.newTotalBilled = newTotalBilled;
    }

    /**
     * Gets the amountRemainingToBill attribute.
     *
     * @return Returns the amountRemainingToBill.
     */
    public KualiDecimal getAmountRemainingToBill() {
        KualiDecimal total = KualiDecimal.ZERO;
        total = getAwardTotal().subtract(getNewTotalBilled());
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
        m.put("billingFrequency", this.billingFrequency);
        m.put("billingPeriod", this.billingPeriod);
        m.put("instrumentTypeCode", this.instrumentTypeCode);
        if (ObjectUtils.isNotNull(this.awardTotal)) {
            m.put("awardTotal", this.awardTotal.toString());
        }
        if (ObjectUtils.isNotNull(this.newTotalBilled)) {
            m.put("newTotalBilled", this.newTotalBilled.toString());
        }
        if (ObjectUtils.isNotNull(this.billedToDateAmount)) {
            m.put("billedToDateAmount", this.billedToDateAmount.toString());
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
     * Gets the billedToDateAmount attribute.
     *
     * @return Returns the billedToDateAmount.
     */
    public KualiDecimal getBilledToDateAmount() {
        return billedToDateAmount;
    }

    /**
     * Sets the billedToDateAmount attribute value.
     *
     * @param billedToDateAmount The billedToDateAmount to set.
     */
    public void setBilledToDateAmount(KualiDecimal billedToDateAmount) {
        this.billedToDateAmount = billedToDateAmount;
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


}
