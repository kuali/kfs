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

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name="TEM_HIST_EXP_T")
public class HistoricalTravelExpense extends ImportedExpenseBase {

    //importDate is processingTimestamp from the agency import

    private Integer creditCardStagingDataId;
    private Integer agencyStagingDataId;
    private Integer profileId;

    private String tripId;
    private String documentType;
    private String creditCardOrAgencyCode;
    private String travelExpenseTypeString;

    private CreditCardStagingData creditCardStagingData;
    private AgencyStagingData agencyStagingData;
    private ExpenseType expenseType;
    /**
     * If CreditCardStagingDataId is null, this is agency data (CTS)
     *
     * @return
     */
    public boolean isAgencyTravelExpense(){
        return getCreditCardStagingDataId() == null;
    }

    /**
     * is credit card travel expense if it is not agency travel expense
     *
     * @return
     */
    public boolean isCreditCardTravelExpense(){
        return !isAgencyTravelExpense();
    }

    /**
     * Gets the creditCardStagingData attribute.
     * @return Returns the creditCardStagingData.
     */
    public CreditCardStagingData getCreditCardStagingData() {
        return creditCardStagingData;
    }

    /**
     * Sets the creditCardStagingData attribute value.
     * @param creditCardStagingData The creditCardStagingData to set.
     */
    public void setCreditCardStagingData(CreditCardStagingData creditCardStagingData) {
        this.creditCardStagingData = creditCardStagingData;
    }

    /**
     * Gets the agencyStagingData attribute.
     * @return Returns the agencyStagingData.
     */
    public AgencyStagingData getAgencyStagingData() {
        return agencyStagingData;
    }

    /**
     * Sets the agencyStagingData attribute value.
     * @param agencyStagingData The agencyStagingData to set.
     */
    public void setAgencyStagingData(AgencyStagingData agencyStagingData) {
        this.agencyStagingData = agencyStagingData;
    }

    /**
     * Gets the creditCardStagingDataId attribute.
     * @return Returns the creditCardStagingDataId.
     */
    @Column(name="CC_STAGE_ID")
    public Integer getCreditCardStagingDataId() {
        return creditCardStagingDataId;
    }

    /**
     * Sets the creditCardStagingDataId attribute value.
     * @param creditCardStagingDataId The creditCardStagingDataId to set.
     */
    public void setCreditCardStagingDataId(Integer creditCardStagingDataId) {
        this.creditCardStagingDataId = creditCardStagingDataId;
    }

    /**
     * Gets the agencyStagingDataId attribute.
     * @return Returns the agencyStagingDataId.
     */
    @Column(name="AGENCY_STAGE_ID")
    public Integer getAgencyStagingDataId() {
        return agencyStagingDataId;
    }

    /**
     * Sets the agencyStagingDataId attribute value.
     * @param agencyStagingDataId The agencyStagingDataId to set.
     */
    public void setAgencyStagingDataId(Integer agencyStagingDataId) {
        this.agencyStagingDataId = agencyStagingDataId;
    }

    /**
     * Gets the profileId attribute.
     * @return Returns the profileId.
     */
    @Column(name="PROFILE_ID")
    public Integer getProfileId() {
        return profileId;
    }

    /**
     * Sets the profileId attribute value.
     * @param profileId The profileId to set.
     */
    public void setProfileId(Integer profileId) {
        this.profileId = profileId;
    }

    /**
     * Gets the tripId attribute.
     * @return Returns the tripId.
     */
    @Column(name="TRIP_ID")
    public String getTripId() {
        return tripId;
    }

    /**
     * Sets the tripId attribute value.
     * @param tripId The tripId to set.
     */
    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    /**
     * Gets the documentType attribute.
     * @return Returns the documentType.
     */
    @Column(name="DOC_TYPE")
    public String getDocumentType() {
        return documentType;
    }

    /**
     * Sets the documentType attribute value.
     * @param documentType The documentType to set.
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the creditCardOrAgencyCode attribute.
     * @return Returns the creditCardOrAgencyCode.
     */
    public String getCreditCardOrAgencyCode() {
        return creditCardOrAgencyCode;
    }

    /**
     * Sets the creditCardOrAgencyCode attribute value.
     * @param creditCardOrAgencyCode The creditCardOrAgencyCode to set.
     */
    public void setCreditCardOrAgencyCode(String creditCardOrAgencyCode) {
        this.creditCardOrAgencyCode = creditCardOrAgencyCode;
    }

    /**
     * Gets the travelExpenseTypeString attribute.
     * @return Returns the travelExpenseTypeString.
     */
    public String getTravelExpenseTypeString() {
        return travelExpenseTypeString;
    }

    /**
     * Sets the travelExpenseTypeString attribute value.
     * @param travelExpenseTypeString The travelExpenseTypeString to set.
     */
    public void setTravelExpenseTypeString(String travelExpenseTypeString) {
        this.travelExpenseTypeString = travelExpenseTypeString;
    }

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }



}
