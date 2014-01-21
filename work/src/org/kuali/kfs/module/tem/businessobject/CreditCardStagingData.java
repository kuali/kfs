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
package org.kuali.kfs.module.tem.businessobject;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImport;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_CREDIT_CARD_STAGING_T")
public class CreditCardStagingData extends PersistableBusinessObjectBase {

    private Integer id;
    private String creditCardKey;
    private Integer travelerId;
    private String airNumber;
    private Date billingCycleDate;
    private String creditCardOrAgencyCode;
    private Integer creditCardAgencyId;
    private Date creationDate;
    private Timestamp creationTimestamp;
    private Date departureDate;
    private String identifier;
    private Date reconciliationDate;
    private String referenceNumber;
    private String sequenceNumber;
    private String ticketNumber;
    private KualiDecimal transactionAmount;
    private String transactionCode;
    private Date transactionDate;
    private String travelerName;
    private String typeCode;
    private String expenseTypeCode;
    private KualiInteger paymentGroupId;
    private String merchantName;
    private Date bankPostDate;
    private String creditCardNumber;
    private Timestamp processingTimestamp; // this is the import date
    private boolean moveToHistoryIndicator;
    private String location;
    private String errorCode;
    private String serviceFeeNumber;
    private String itineraryNumber;
    private String importBy;
    private Integer temProfileId;
    private String stagingFileName;

    private TemProfile profile;
    private CreditCardAgency creditCardAgency;

    /**
     * Gets the id attribute.
     *
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(generator = "TEM_CREDIT_CARD_STAGING_ID_SEQ")
    @SequenceGenerator(name = "TEM_CREDIT_CARD_STAGING_ID_SEQ", sequenceName = "TEM_CREDIT_CARD_STAGING_ID_SEQ", allocationSize = 5)
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return id;
    }


    /**
     * Sets the id attribute value.
     *
     * @param id The id to set.
     */
    public void setId(Integer id) {

        this.id = id;
    }


    @Column(name = "AIR_NUMBER", length = 3, nullable = true)
    public String getAirNumber() {

        return airNumber;
    }


    public void setAirNumber(String airNumber) {

        this.airNumber = airNumber;
    }


    @Column(name = "BILLING_CYCLE_DT", nullable = true)
    public Date getBillingCycleDate() {
        return billingCycleDate;
    }


    public void setBillingCycleDate(Date billingCycleDate) {
        this.billingCycleDate = billingCycleDate;
    }


    @Column(name = "CREDIT_AGENCY_CD", length = 4, nullable = true)
    public String getCreditCardOrAgencyCode() {
        return creditCardOrAgencyCode;
    }


    public void setCreditCardOrAgencyCode(String creditCardOrAgencyCode) {
        this.creditCardOrAgencyCode = creditCardOrAgencyCode;
    }


    @Column(name = "CREATION_DT", nullable = true)
    public Date getCreationDate() {
        return creationDate;
    }


    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }


    @Column(name = "CREATION_TIMESTAMP", nullable = true)
    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public void setCreationTimestamp(String creationTimestamp) {
        if(creationTimestamp!=null) {
            creationTimestamp=creationTimestamp.replace('T', ' ');
        }
        this.creationTimestamp =java.sql.Timestamp.valueOf(creationTimestamp);
    }

    /**
     * Gets the processingTimestamp attribute.
     * @return Returns the processingTimestamp.
     */
    @Column(name = "PROCESSING_TS", nullable = true)
    public Timestamp getProcessingTimestamp() {
        return processingTimestamp;
    }

    /**
     * Sets the processingTimestamp attribute value.
     * @param processingTimestamp The processingTimestamp to set.
     */
    public void setProcessingTimestamp(Timestamp processingTimestamp) {
        this.processingTimestamp = processingTimestamp;
    }


    @Column(name = "DEPARTURE_DT", nullable = true)
    public Date getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(Date departureDate) {

        this.departureDate = departureDate;
    }

    @Column(name = "CREDIT_CARD_KEY", length = 22, nullable = false)
    public String getCreditCardKey() {
        return creditCardKey;
    }


    public void setCreditCardKey(String creditCardKey) {
        this.creditCardKey = creditCardKey;
    }

    @Column(name = "TRAVELER_ID", length = 16, nullable = false)
    public Integer getTravelerId() {
        return travelerId;
    }


    public void setTravelerId(Integer travelerId) {
        this.travelerId = travelerId;
    }


    @Column(name = "IDENTIFIER", length = 1, nullable = true)
    public String getIdentifier() {
        return identifier;
    }


    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }


    @Column(name = "RECONCILIATION_DT", nullable = true)
    public Date getReconciliationDate() {
        return reconciliationDate;
    }


    public void setReconciliationDate(Date reconciliationDate) {
        this.reconciliationDate = reconciliationDate;
    }


    @Column(name = "REFERENCE_NBR", length = 23, nullable = true)
    public String getReferenceNumber() {
        return referenceNumber;
    }


    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }


    @Column(name = "SEQUENCE_NBR", length = 3, nullable = true)
    public String getSequenceNumber() {
        return sequenceNumber;
    }


    public void setSequenceNumber(String sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }


    @Column(name = "TICKET_NBR", length = 16, nullable = true)
    public String getTicketNumber() {
        return ticketNumber;
    }


    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }


    @Column(name = "TRANSACTION_AMOUNT", precision = 15, scale = 4, nullable = true)
    public KualiDecimal getTransactionAmount() {
        return transactionAmount;
    }


    public void setTransactionAmount(KualiDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    /**
     * Sets the setTransactionAmount attribute.
     *
     * @param setTransactionAmount The setTransactionAmount to set.
     */
    public void setTransactionAmount(String transactionAmount) {
        if (StringUtils.isNotBlank(transactionAmount)) {
            this.transactionAmount = new KualiDecimal(transactionAmount);
        }
        else {
            this.transactionAmount = KualiDecimal.ZERO;
        }
    }


    @Column(name = "TRANSACTION_CD", length = 1, nullable = true)
    public String getTransactionCode() {
        return transactionCode;
    }


    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }


    @Column(name = "TRANSACTION_DT", nullable = true)
    public Date getTransactionDate() {
        return transactionDate;
    }


    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }


    @Column(name = "TRAVELER_NM", length = 40, nullable = true)
    public String getTravelerName() {
        return travelerName;
    }


    public void setTravelerName(String travelerName) {
        this.travelerName = travelerName;
    }


    @Column(name = "TYPE_CD", length = 1, nullable = true)
    public String getTypeCode() {
        return typeCode;
    }


    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Column(name = "DV_EXP_CD", length = 2, nullable = true)
    public String getExpenseTypeCode() {
        return expenseTypeCode;
    }


    public void setExpenseTypeCode(String expenseTypeCode) {
        this.expenseTypeCode = expenseTypeCode;
    }

    public KualiInteger getPaymentGroupId() {
        return paymentGroupId;
    }

    public void setPaymentGroupId(KualiInteger paymentGroupId) {
        this.paymentGroupId = paymentGroupId;
    }

    @Column(name = "MERCHANT_NM", length = 40, nullable = true)
    public String getMerchantName() {
        return merchantName;
    }


    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }


    @Column(name = "BANK_POST_DT", nullable = true)
    public Date getBankPostDate() {
        return bankPostDate;
    }


    public void setBankPostDate(Date bankPostDate) {
        this.bankPostDate = bankPostDate;
    }


    public String getCreditCardNumber() {
        return creditCardNumber;
    }


    public void setCreditCardNumber(String creditCardNumber) {
        this.creditCardNumber = creditCardNumber;
    }

    /**
     * Gets the moveToHistoryIndicator attribute.
     * @return Returns the moveToHistoryIndicator.
     */
    @Column(name = "MV_TO_HISTORY", length = 1, nullable = true)
    public boolean getMoveToHistoryIndicator() {
        return moveToHistoryIndicator;
    }

    /**
     * Sets the moveToHistoryIndicator attribute value.
     * @param moveToHistoryIndicator The moveToHistoryIndicator to set.
     */
    public void setMoveToHistoryIndicator(boolean moveToHistoryIndicator) {
        this.moveToHistoryIndicator = moveToHistoryIndicator;
    }

    /**
     * Gets the location attribute.
     * @return Returns the location.
     */
    @Column(name = "LOC", length = 20, nullable = true)
    public String getLocation() {
        return location;
    }

    /**
     * Sets the location attribute value.
     * @param location The location to set.
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * Gets the errorCode attribute.
     * @return Returns the errorCode.
     */
    @Column(name = "ERROR_CD", length = 40, nullable = true)
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the errorCode attribute value.
     * @param errorCode The errorCode to set.
     */
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the serviceFeeNumber attribute.
     * @return Returns the serviceFeeNumber.
     */
    @Column(name = "SRVC_FEE_NBR", length = 20, nullable = true)
    public String getServiceFeeNumber() {
        return serviceFeeNumber;
    }


    /**
     * Sets the serviceFeeNumber attribute value.
     * @param serviceFeeNumber The serviceFeeNumber to set.
     */
    public void setServiceFeeNumber(String serviceFeeNumber) {
        this.serviceFeeNumber = serviceFeeNumber;
    }


    /**
     * Gets the itineraryNumber attribute.
     * @return Returns the itineraryNumber.
     */
    @Column(name = "ITN_NBR", length = 20, nullable = true)
    public String getItineraryNumber() {
        return itineraryNumber;
    }


    /**
     * Sets the itineraryNumber attribute value.
     * @param itineraryNumber The itineraryNumber to set.
     */
    public void setItineraryNumber(String itineraryNumber) {
        this.itineraryNumber = itineraryNumber;
    }

    /**
     * Gets the importBy attribute.
     * @return Returns the importBy.
     */
    @Column(name = "IMPORT_BY", length = 3, nullable = true)
    public String getImportBy() {
        return importBy;
    }

    public ExpenseImport getExpenseImport() {
        return ExpenseImport.getExpenseImportByCode(importBy);
    }

    /**
     * Sets the importBy attribute value.
     * @param importBy The importBy to set.
     */
    public void setImportBy(String importBy) {
        this.importBy = importBy;
    }


    /**
     * Gets the temProfileId attribute.
     * @return Returns the temProfileId.
     */
    public Integer getTemProfileId() {
        return temProfileId;
    }


    /**
     * Sets the temProfileId attribute value.
     * @param temProfileId The temProfileId to set.
     */
    public void setTemProfileId(Integer temProfileId) {
        this.temProfileId = temProfileId;
    }


    public TemProfile getProfile() {
        return profile;
    }


    public void setProfile(TemProfile profile) {
        this.profile = profile;
    }


    public CreditCardAgency getCreditCardAgency() {
        return creditCardAgency;
    }


    public void setCreditCardAgency(CreditCardAgency creditCardAgency) {
        this.creditCardAgency = creditCardAgency;
    }


    public Integer getCreditCardAgencyId() {
        return creditCardAgencyId;
    }


    public void setCreditCardAgencyId(Integer creditCardAgencyId) {
        this.creditCardAgencyId = creditCardAgencyId;
    }


    public String getStagingFileName() {
        return stagingFileName;
    }


    public void setStagingFileName(String stagingFileName) {
        this.stagingFileName = stagingFileName;
    }
}
