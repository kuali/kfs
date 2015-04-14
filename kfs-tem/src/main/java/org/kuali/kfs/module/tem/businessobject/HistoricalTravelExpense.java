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

    public ExpenseType getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(ExpenseType expenseType) {
        this.expenseType = expenseType;
    }



}
