/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_TRVL_ADV_T")
public class TravelAdvance extends PersistableBusinessObjectBase {
    private String documentNumber;
    private String travelDocumentIdentifier;

    private KualiDecimal travelAdvanceRequested;
    private KualiDecimal amountDue;

    private String arCustomerId;
    private String arInvoiceDocNumber;
    private Date dueDate;
    private Date taxRamificationNotificationDate;
    private String advancePaymentReasonCode;
    private Boolean travelAdvancePolicy = Boolean.FALSE;
    private String additionalJustification;
    private AdvancePaymentReason advancePaymentReason;

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    @Column(name = "FDOC_NBR")
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
     * @return the trip id for the travel document this is associated with.  We know this breaks normalization, but it will make it much easier for
     * travel reimbursement to find all related travel advances
     */
    @Column(name="TRVL_ID")
    public String getTravelDocumentIdentifier() {
        return travelDocumentIdentifier;
    }

    /**
     * Sets the trip id for the travel document this advance is associated with
     * @param travelDocumentIdentifier the travel document identifier/travel id/trip id
     */
    public void setTravelDocumentIdentifier(String travelDocumentIdentifier) {
        this.travelDocumentIdentifier = travelDocumentIdentifier;
    }

    /**
     * This method returns the travel advance request
     *
     * @return travel advance requested amount
     */
    @Column(name = "TVL_ADV_REQ", precision = 19, scale = 2)
    public KualiDecimal getTravelAdvanceRequested() {
        return travelAdvanceRequested;
    }

    /**
     * This method sets the amount of advance being requested
     *
     * @param travelAdvanceRequested
     */
    public void setTravelAdvanceRequested(KualiDecimal travelAdvanceRequested) {
        this.travelAdvanceRequested = travelAdvanceRequested;
    }


    /**
     * Gets the amountDue attribute.
     * @return Returns the amountDue.
     */
    public KualiDecimal getAmountDue() {
        if (arInvoiceDocNumber != null){
            amountDue = getTravelDocumentService().getAmountDueFromInvoice(arInvoiceDocNumber,this.getTravelAdvanceRequested());
        }
        else{
            amountDue = this.getTravelAdvanceRequested();
        }
        return amountDue;
    }

    public void setAmountDue(KualiDecimal amountDue) {
        this.amountDue = amountDue;
    }

    /**
     * This method returns the Accounts Receivable Customer ID associated with this traveler
     *
     * @return customer ID
     */
    @Column(name = "AR_CUST_ID")
    public String getArCustomerId() {
        return arCustomerId;
    }

    /**
     * This method sets the Accounts Receivable Customer ID associated with this traveler
     *
     * @param arCustomerId
     */
    public void setArCustomerId(String arCustomerId) {
        this.arCustomerId = arCustomerId;
    }


    /**
     * Gets the arInvoiceDocNumber attribute.
     *
     * @return Returns the arInvoiceDocNumber.
     */
    @Column(name = "AR_INV_DOC_NBR")
    public String getArInvoiceDocNumber() {
        return arInvoiceDocNumber;
    }

    /**
     * Sets the arInvoiceDocNumber attribute value.
     *
     * @param arInvoiceDocNumber The arInvoiceDocNumber to set.
     */
    public void setArInvoiceDocNumber(String arInvoiceDocNumber) {
        this.arInvoiceDocNumber = arInvoiceDocNumber;
    }

    /**
     * Gets the dueDate attribute.
     *
     * @return Returns the dueDate.
     */
    @Column(name = "DUE_DT")
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * Sets the dueDate attribute value.
     *
     * @param dueDate The dueDate to set.
     */
    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Gets the advancePaymentReasonCode attribute.
     * @return Returns the advancePaymentReasonCode.
     */
    public String getAdvancePaymentReasonCode() {
        return advancePaymentReasonCode;
    }

    /**
     * Sets the advancePaymentReasonCode attribute value.
     * @param advancePaymentReasonCode The advancePaymentReasonCode to set.
     */
    public void setAdvancePaymentReasonCode(String advancePaymentReasonCode) {
        this.advancePaymentReasonCode = advancePaymentReasonCode;
    }

    /**
     * Gets the advancePaymentReason attribute.
     * @return Returns the advancePaymentReason.
     */
    public AdvancePaymentReason getAdvancePaymentReason() {
        return advancePaymentReason;
    }

    /**
     * Sets the advancePaymentReason attribute value.
     * @param advancePaymentReason The advancePaymentReason to set.
     */
    public void setAdvancePaymentReason(AdvancePaymentReason advancePaymentReason) {
        this.advancePaymentReason = advancePaymentReason;
    }

    /**
     * Gets the travelAdvancePolicy attribute.
     * @return Returns the travelAdvancePolicy.
     */
    public boolean getTravelAdvancePolicy() {
        return travelAdvancePolicy;
    }

    /**
     * Sets the travelAdvancePolicy attribute value.
     * @param travelAdvancePolicy The travelAdvancePolicy to set.
     */
    public void setTravelAdvancePolicy(boolean travelAdvancePolicy) {
        this.travelAdvancePolicy = travelAdvancePolicy;
    }

    /**
     * Gets the additionalJustification attribute.
     * @return Returns the additionalJustification.
     */
    public String getAdditionalJustification() {
        return additionalJustification;
    }

    /**
     * Sets the additionalJustification attribute value.
     * @param additionalJustification The additionalJustification to set.
     */
    public void setAdditionalJustification(String additionalJustification) {
        this.additionalJustification = additionalJustification;
    }

    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the taxRamificationNotificationDate attribute.
     * @return Returns the taxRamificationNotificationDate.
     */
    @Column(name = "TAX_RAM_NTF_DT")
    public Date getTaxRamificationNotificationDate() {
        return taxRamificationNotificationDate;
    }

    /**
     * Sets the taxRamificationNotificationDate attribute value.
     * @param taxRamificationNotificationDate The taxRamificationNotificationDate to set.
     */
    public void setTaxRamificationNotificationDate(Date taxRamificationNotificationDate) {
        this.taxRamificationNotificationDate = taxRamificationNotificationDate;
    }

    /**
     * Determines if any user-writable fields on the advance have been filled in
     * @return true if any user-writable field on the advance has been written to; false otherwise
     */
    public boolean isAtLeastPartiallyFilledIn() {
        return this.getTravelAdvanceRequested() != null || this.getDueDate() != null || this.getTravelAdvancePolicy() || !StringUtils.isBlank(this.getAdditionalJustification());
    }

    /**
     * Removes the user-filled in data from this advance, thereby making it blank
     */
    public void clear() {
        setTravelAdvanceRequested(null);
        setDueDate(null);
        setTravelAdvancePolicy(false);
        setAdditionalJustification(null);
    }
}
