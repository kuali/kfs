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
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.ExpenseImport;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypeMetaCategory;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_AGENCY_STAGING_T")
public class AgencyStagingData extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer id;
    private String errorCode;
    private Integer duplicateRecordId;
    private Integer temProfileId;
    private TEMProfile profile;

    private String importBy;

    // Agency Information
    private Integer agencyDataId;
    private String creditCardOrAgencyCode;
    private Integer creditCardAgencyId;
    private String agency;
    private String otherCompanyName;
    private String agencyFileName;
    private String stagingFileName;
    private String merchantName;
    private Date billingCycleDate;

    private CreditCardAgency creditCardAgency;

    // Trip Information
    private String tripId;
    private String tripInvoiceNumber;
    private String tripTravelerTypeId;
    private KualiDecimal otherAmount;

    // Traveler Information
    private String travelerName;
    private String travelerId;
    private String travelerNetworkId;
    private KualiDecimal tripExpenseAmount;
    private String alternateTripId;
    private String tripArrangerName;

    // Accounting Information
    private ArrayList<TripAccountingInformation> tripAccountingInformation;
    private String groupObjectCode;
    private String distributionCode; // diCode


    //Used strictly for searching, never stored in the db
    private String searchChartOfAccountsCode;
    private String searchAccountNumber;
    private String searchSubAccountNumber;

    private Chart searchChart;
    private Account searchAccount;
    private SubAccount searchSubAccount;

    // Airline Information
    private Date tripDepartureDate;
    private Date tripReturnDate;
    private String fareSaverCode;
    private Date airBookDate;
    private String airCarrierCode;
    private String airTicketNumber;
    private String pnrNumber;
    private String airTicketClass;
    private KualiDecimal airTransactionAmount;
    private KualiDecimal airBaseFareAmount;
    private KualiDecimal airTaxAmount;
    private KualiDecimal airLowFareAmount;
    private String airReasonCode;
    private String airSegmentId;
    private String airDestinationCode;
    private String airServiceFeeNumber;
    private KualiDecimal airServiceFeeAmount;
    private String transactionUniqueId;

    // Lodging Information
    private String lodgingItineraryNumber;
    private Date lodgingPrepayDate;
    private KualiDecimal lodgingAmount;
    private String lodgingPrepayDaysNumber;
    private String lodgingPropertyName;
    private Date tripLodgingArrivalDate;
    private Date lodgingDepartureDate;
    private Date lodgingBookingDate;
    private String lodgingPropertyCityName;
    private String lodgingPropertyStateCode;
    private String lodgingCountryName;

    // Rental Car Information
    private String rentalCarItineraryNumber;
    private KualiDecimal rentalCarAmount;
    private String rentalCarNumberOfDays;
    private String rentalCarCompanyName;
    private Date rentalCarOpenDate;
    private Date rentalCarCloseDate;
    private KualiDecimal rentalCarFuelAmount;
    private KualiDecimal rentalCarAdditionalAmount;
    private KualiDecimal rentalCarTaxAmount;
    private KualiDecimal rentalCarSurchargeAmount;
    private KualiDecimal rentalCarGovernmentSurchargeAmount;
    private KualiDecimal rentalCarBillAmount;
    private String rentalCarDetailText;

    // Conference Registration
    private String registrationCompanyName;
    private KualiDecimal registrationAmount;

    // Processing Information
    private Date transactionPostingDate;
    private String objectVerNumber;
    private Timestamp creationTimestamp;
    private Timestamp processingTimestamp; // this is the import date
    private boolean moveToHistoryIndicator;

    private Boolean active = Boolean.TRUE;
    private Boolean manualCreated = Boolean.FALSE;
    private Integer copiedFromId;

    public AgencyStagingData() {
        tripAccountingInformation = new ArrayList<TripAccountingInformation>();
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

    /**
     * Gets the id attribute.
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(generator = "TEM_AGENCY_STAGING_ID_SEQ")
    @SequenceGenerator(name = "TEM_AGENCY_STAGING_ID_SEQ", sequenceName = "TEM_AGENCY_STAGING_ID_SEQ", allocationSize = 5)
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return id;
    }

    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
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


    /**
     * Gets the duplicateRecordId attribute.
     * @return Returns the duplicateRecordId.
     */
    @Column(name = "DUP_REC_ID", nullable = false)
    public Integer getDuplicateRecordId() {
        return duplicateRecordId;
    }

    /**
     * Sets the duplicateRecordId attribute value.
     * @param duplicateRecordId The duplicateRecordId to set.
     */
    public void setDuplicateRecordId(Integer duplicateRecordId) {
        this.duplicateRecordId = duplicateRecordId;
    }

    /**
     * Gets the temProfileId attribute.
     * @return Returns the temProfileId.
     */
    @Column(name = "PROFILE_ID", length = 19, nullable = true)
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

    /**
     * Gets the agencyDataId attribute.
     * @return Returns the agencyDataId.
     */
    @Column(name = "AGENCY_DATA_ID", length = 10, nullable = true)
    public Integer getAgencyDataId() {
        return agencyDataId;
    }

    /**
     * Sets the agencyDataId attribute value.
     * @param agencyDataId The agencyDataId to set.
     */
    public void setAgencyDataId(Integer agencyDataId) {
        this.agencyDataId = agencyDataId;
    }

    /**
     * Gets the creditCardOrAgencyCode attribute.
     * @return Returns the creditCardOrAgencyCode.
     */
    @Column(name = "CREDIT_AGENCY_CD", length = 4, nullable = true)
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
     * Gets the agency attribute.
     * @return Returns the agency.
     */
    @Column(name = "AGENCY", length = 20, nullable = true)
    public String getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute value.
     * @param agency The agency to set.
     */
    public void setAgency(String agency) {
        this.agency = agency;
    }

    /**
     * Gets the otherCompanyName attribute.
     * @return Returns the otherCompanyName.
     */
    @Column(name = "OTHER_CO_NM", length = 30, nullable = true)
    public String getOtherCompanyName() {
        return otherCompanyName;
    }

    /**
     * Sets the otherCompanyName attribute value.
     * @param otherCompanyName The otherCompanyName to set.
     */
    public void setOtherCompanyName(String otherCompanyName) {
        this.otherCompanyName = otherCompanyName;
    }

    /**
     * Gets the agencyFileName attribute.
     * @return Returns the agencyFileName.
     */
    @Column(name = "AGENCY_FL_NM", length = 50, nullable = true)
    public String getAgencyFileName() {
        return agencyFileName;
    }

    /**
     * Sets the agencyFileName attribute value.
     * @param agencyFileName The agencyFileName to set.
     */
    public void setAgencyFileName(String agencyFileName) {
        this.agencyFileName = agencyFileName;
    }

    /**
     * Gets the merchantName attribute.
     * @return Returns the merchantName.
     */
    @Column(name = "MERCH_NM", length = 40, nullable = true)
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Sets the merchantName attribute value.
     * @param merchantName The merchantName to set.
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }

    /**
     * Gets the billingCycleDate attribute.
     * @return Returns the billingCycleDate.
     */
    @Column(name = "BILLING_CYCLE_DT", nullable = true)
    public Date getBillingCycleDate() {
        return billingCycleDate;
    }

    /**
     * Sets the billingCycleDate attribute value.
     * @param billingCycleDate The billingCycleDate to set.
     */
    public void setBillingCycleDate(Date billingCycleDate) {
        this.billingCycleDate = billingCycleDate;
    }

    /**
     * Gets the tripId attribute.
     * @return Returns the tripId.
     */
    @Column(name = "TRIP_ID", length = 12, nullable = true)
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
     * Gets the tripInvoiceNumber attribute.
     * @return Returns the tripInvoiceNumber.
     */
    @Column(name = "TRIP_INV_NBR", length = 20, nullable = true)
    public String getTripInvoiceNumber() {
        return tripInvoiceNumber;
    }

    /**
     * Sets the tripInvoiceNumber attribute value.
     * @param tripInvoiceNumber The tripInvoiceNumber to set.
     */
    public void setTripInvoiceNumber(String tripInvoiceNumber) {
        this.tripInvoiceNumber = tripInvoiceNumber;
    }

    /**
     * Gets the tripTravelerTypeId attribute.
     * @return Returns the tripTravelerTypeId.
     */
    @Column(name = "TRIP_TRV_TYP_ID", length = 1, nullable = true)
    public String getTripTravelerTypeId() {
        return tripTravelerTypeId;
    }

    /**
     * Sets the tripTravelerTypeId attribute value.
     * @param tripTravelerTypeId The tripTravelerTypeId to set.
     */
    public void setTripTravelerTypeId(String tripTravelerTypeId) {
        this.tripTravelerTypeId = tripTravelerTypeId;
    }

    /**
     * Gets the otherAmount attribute.
     * @return Returns the otherAmount.
     */
    @Column(name = "OTHER_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getOtherAmount() {
        return otherAmount;
    }

    /**
     * Sets the otherAmount attribute value.
     * @param otherAmount The otherAmount to set.
     */
    public void setOtherAmount(KualiDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }
    public void setOtherAmount(String otherAmount) {
        this.otherAmount = new KualiDecimal(otherAmount);
    }

    /**
     * Gets the travelerName attribute.
     * @return Returns the travelerName.
     */
    @Column(name = "TRAVELER_NM", length = 50, nullable = false)
    public String getTravelerName() {
        return travelerName;
    }

    /**
     * Sets the travelerName attribute value.
     * @param travelerName The travelerName to set.
     */
    public void setTravelerName(String travelerName) {
        this.travelerName = travelerName;
    }

    /**
     * Gets the travelerId attribute.
     * @return Returns the travelerId.
     */
    @Column(name = "TRAVELER_ID", length = 40, nullable = true)
    public String getTravelerId() {
        return travelerId;
    }

    /**
     * Sets the travelerId attribute value.
     * @param travelerId The travelerId to set.
     */
    public void setTravelerId(String travelerId) {
        this.travelerId = travelerId;
    }

    /**
     * Gets the travelerNetworkId attribute.
     * @return Returns the travelerNetworkId.
     */
    @Column(name = "TRAVELER_NTWK_ID", length = 9, nullable = true)
    public String getTravelerNetworkId() {
        return travelerNetworkId;
    }

    /**
     * Sets the travelerNetworkId attribute value.
     * @param travelerNetworkId The travelerNetworkId to set.
     */
    public void setTravelerNetworkId(String travelerNetworkId) {
        this.travelerNetworkId = travelerNetworkId;
    }

    /**
     * Gets the tripExpenseAmount attribute.
     * @return Returns the tripExpenseAmount.
     */
    @Column(name = "TRP_EXP_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getTripExpenseAmount() {
        return tripExpenseAmount;
    }

    /**
     * Sets the tripExpenseAmount attribute value.
     * @param tripExpenseAmount The tripExpenseAmount to set.
     */
    public void setTripExpenseAmount(KualiDecimal tripExpenseAmount) {
        this.tripExpenseAmount = tripExpenseAmount;
    }
    public void setTripExpenseAmount(String tripExpenseAmount) {
        this.tripExpenseAmount = new KualiDecimal(tripExpenseAmount);
    }


    /**
     * Gets the alternateTripId attribute.
     * @return Returns the alternateTripId.
     */
    @Column(name = "ALT_TRP_ID", length = 20, nullable = true)
    public String getAlternateTripId() {
        return alternateTripId;
    }

    /**
     * Sets the alternateTripId attribute value.
     * @param alternateTripId The alternateTripId to set.
     */
    public void setAlternateTripId(String alternateTripId) {
        this.alternateTripId = alternateTripId;
    }

    /**
     * Gets the tripArrangerName attribute.
     * @return Returns the tripArrangerName.
     */
    @Column(name = "TRP_ARR_NM", length = 50, nullable = true)
    public String getTripArrangerName() {
        return tripArrangerName;
    }

    /**
     * Sets the tripArrangerName attribute value.
     * @param tripArrangerName The tripArrangerName to set.
     */
    public void setTripArrangerName(String tripArrangerName) {
        this.tripArrangerName = tripArrangerName;
    }

    /**
     * Gets the tripAccountingInformation attribute.
     * @return Returns the tripAccountingInformation.
     */
    public ArrayList<TripAccountingInformation> getTripAccountingInformation() {
        return tripAccountingInformation;
    }

    /**
     * Sets the tripAccountingInformation attribute value.
     * @param tripAccountingInformation The tripAccountingInformation to set.
     */
    public void setTripAccountingInformation(ArrayList<TripAccountingInformation> tripAccountingInformation) {
        this.tripAccountingInformation = tripAccountingInformation;
    }

    /**
     *
     * This method adds a TripAccountingInformation to the list. Needed for agencyDataDigestorRules.xml.
     * @param accountingInfo
     */
    public void addTripAccountingInformation(TripAccountingInformation accountingInfo) {
        getTripAccountingInformation().add(accountingInfo);
    }

    /**
     * Gets the groupObjectCode attribute.
     * @return Returns the groupObjectCode.
     */
    @Column(name = "GRP_OBJ_CD", length = 20, nullable = true)
    public String getGroupObjectCode() {
        return groupObjectCode;
    }

    /**
     * Sets the groupObjectCode attribute value.
     * @param groupObjectCode The groupObjectCode to set.
     */
    public void setGroupObjectCode(String groupObjectCode) {
        this.groupObjectCode = groupObjectCode;
    }

    /**
     * Gets the distributionCode attribute.
     * @return Returns the distributionCode.
     */
    @Column(name = "DI_CD", length = 4, nullable = true)
    public String getDistributionCode() {
        return distributionCode;
    }

    /**
     * Sets the distributionCode attribute value.
     * @param distributionCode The distributionCode to set.
     */
    public void setDistributionCode(String distributionCode) {
        this.distributionCode = distributionCode;
    }

    /**
     * Gets the tripDepartureDate attribute.
     * @return Returns the tripDepartureDate.
     */
    @Column(name = "TRP_DPT_DT", nullable = true)
    public Date getTripDepartureDate() {
        return tripDepartureDate;
    }

    /**
     * Sets the tripDepartureDate attribute value.
     * @param tripDepartureDate The tripDepartureDate to set.
     */
    public void setTripDepartureDate(Date tripDepartureDate) {
        this.tripDepartureDate = tripDepartureDate;
    }

    /**
     * Gets the tripReturnDate attribute.
     * @return Returns the tripReturnDate.
     */
    @Column(name = "TRP_RTRN_DT", nullable = true)
    public Date getTripReturnDate() {
        return tripReturnDate;
    }

    /**
     * Sets the tripReturnDate attribute value.
     * @param tripReturnDate The tripReturnDate to set.
     */
    public void setTripReturnDate(Date tripReturnDate) {
        this.tripReturnDate = tripReturnDate;
    }

    /**
     * Gets the fareSaverCode attribute.
     * @return Returns the fareSaverCode.
     */
    @Column(name = "FARE_SVR_CD", length = 1, nullable = true)
    public String getFareSaverCode() {
        return fareSaverCode;
    }

    /**
     * Sets the fareSaverCode attribute value.
     * @param fareSaverCode The fareSaverCode to set.
     */
    public void setFareSaverCode(String fareSaverCode) {
        this.fareSaverCode = fareSaverCode;
    }

    /**
     * Gets the airBookDate attribute.
     * @return Returns the airBookDate.
     */
    @Column(name = "AIR_BK_DT", nullable = true)
    public Date getAirBookDate() {
        return airBookDate;
    }

    /**
     * Sets the airBookDate attribute value.
     * @param airBookDate The airBookDate to set.
     */
    public void setAirBookDate(Date airBookDate) {
        this.airBookDate = airBookDate;
    }

    /**
     * Gets the airCarrierCode attribute.
     * @return Returns the airCarrierCode.
     */
    @Column(name = "AIR_CARR_CD", length = 3, nullable = true)
    public String getAirCarrierCode() {
        return airCarrierCode;
    }

    /**
     * Sets the airCarrierCode attribute value.
     * @param airCarrierCode The airCarrierCode to set.
     */
    public void setAirCarrierCode(String airCarrierCode) {
        this.airCarrierCode = airCarrierCode;
    }

    /**
     * Gets the airTicketNumber attribute.
     * @return Returns the airTicketNumber.
     */
    @Column(name = "AIR_TKT_NBR", length = 20, nullable = true)
    public String getAirTicketNumber() {
        return airTicketNumber;
    }

    /**
     * Sets the airTicketNumber attribute value.
     * @param airTicketNumber The airTicketNumber to set.
     */
    public void setAirTicketNumber(String airTicketNumber) {
        this.airTicketNumber = airTicketNumber;
    }

    /**
     * Gets the pnrNumber attribute.
     * @return Returns the pnrNumber.
     */
    @Column(name = "PNR_NBR", length = 20, nullable = true)
    public String getPnrNumber() {
        return pnrNumber;
    }

    /**
     * Sets the pnrNumber attribute value.
     * @param pnrNumber The pnrNumber to set.
     */
    public void setPnrNumber(String pnrNumber) {
        this.pnrNumber = pnrNumber;
    }

    /**
     * Gets the airTicketClass attribute.
     * @return Returns the airTicketClass.
     */
    @Column(name = "AIR_TKT_CLASS", length = 20, nullable = true)
    public String getAirTicketClass() {
        return airTicketClass;
    }

    /**
     * Sets the airTicketClass attribute value.
     * @param airTicketClass The airTicketClass to set.
     */
    public void setAirTicketClass(String airTicketClass) {
        this.airTicketClass = airTicketClass;
    }

    /**
     * Gets the airTransactionAmount attribute.
     * @return Returns the airTransactionAmount.
     */
    @Column(name = "AIR_TRANS_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getAirTransactionAmount() {
        return airTransactionAmount;
    }

    /**
     * Sets the airTransactionAmount attribute value.
     * @param airTransactionAmount The airTransactionAmount to set.
     */
    public void setAirTransactionAmount(KualiDecimal airTransactionAmount) {
        this.airTransactionAmount = airTransactionAmount;
    }
    public void setAirTransactionAmount(String airTransactionAmount) {
        this.airTransactionAmount = new KualiDecimal(airTransactionAmount);
    }


    /**
     * Gets the airBaseFareAmount attribute.
     * @return Returns the airBaseFareAmount.
     */
    @Column(name = "AIR_BASE_FARE_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getAirBaseFareAmount() {
        return airBaseFareAmount;
    }

    /**
     * Sets the airBaseFareAmount attribute value.
     * @param airBaseFareAmount The airBaseFareAmount to set.
     */
    public void setAirBaseFareAmount(KualiDecimal airBaseFareAmount) {
        this.airBaseFareAmount = airBaseFareAmount;
    }
    public void setAirBaseFareAmount(String airBaseFareAmount) {
        this.airBaseFareAmount = new KualiDecimal(airBaseFareAmount);
    }

    /**
     * Gets the airTaxAmount attribute.
     * @return Returns the airTaxAmount.
     */
    @Column(name = "AIR_TX_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getAirTaxAmount() {
        return airTaxAmount;
    }

    /**
     * Sets the airTaxAmount attribute value.
     * @param airTaxAmount The airTaxAmount to set.
     */
    public void setAirTaxAmount(KualiDecimal airTaxAmount) {
        this.airTaxAmount = airTaxAmount;
    }
    public void setAirTaxAmount(String airTaxAmount) {
        this.airTaxAmount = new KualiDecimal(airTaxAmount);
    }

    /**
     * Gets the airLowFareAmount attribute.
     * @return Returns the airLowFareAmount.
     */
    @Column(name = "AIR_LOW_FARE_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getAirLowFareAmount() {
        return airLowFareAmount;
    }

    /**
     * Sets the airLowFareAmount attribute value.
     * @param airLowFareAmount The airLowFareAmount to set.
     */
    public void setAirLowFareAmount(KualiDecimal airLowFareAmount) {
        this.airLowFareAmount = airLowFareAmount;
    }
    public void setAirLowFareAmount(String airLowFareAmount) {
        this.airLowFareAmount = new KualiDecimal(airLowFareAmount);
    }

    /**
     * Gets the airReasonCode attribute.
     * @return Returns the airReasonCode.
     */
    @Column(name = "AIR_RSN_CD", length = 1, nullable = true)
    public String getAirReasonCode() {
        return airReasonCode;
    }

    /**
     * Sets the airReasonCode attribute value.
     * @param airReasonCode The airReasonCode to set.
     */
    public void setAirReasonCode(String airReasonCode) {
        this.airReasonCode = airReasonCode;
    }

    /**
     * Gets the airSegmentId attribute.
     * @return Returns the airSegmentId.
     */
    @Column(name = "AIR_SEG_ID", length = 100, nullable = true)
    public String getAirSegmentId() {
        return airSegmentId;
    }

    /**
     * Sets the airSegmentId attribute value.
     * @param airSegmentId The airSegmentId to set.
     */
    public void setAirSegmentId(String airSegmentId) {
        this.airSegmentId = airSegmentId;
    }

    /**
     * Gets the airDestinationCode attribute.
     * @return Returns the airDestinationCode.
     */
    @Column(name = "AIR_DEST_CD", length = 3, nullable = true)
    public String getAirDestinationCode() {
        return airDestinationCode;
    }

    /**
     * Sets the airDestinationCode attribute value.
     * @param airDestinationCode The airDestinationCode to set.
     */
    public void setAirDestinationCode(String airDestinationCode) {
        this.airDestinationCode = airDestinationCode;
    }

    /**
     * Gets the airServiceFeeNumber attribute.
     * @return Returns the airServiceFeeNumber.
     */
    @Column(name = "AIR_SRVC_FEE_NBR", length = 20, nullable = true)
    public String getAirServiceFeeNumber() {
        return airServiceFeeNumber;
    }

    /**
     * Sets the airServiceFeeNumber attribute value.
     * @param airServiceFeeNumber The airServiceFeeNumber to set.
     */
    public void setAirServiceFeeNumber(String airServiceFeeNumber) {
        this.airServiceFeeNumber = airServiceFeeNumber;
    }

    /**
     * Gets the airServiceFeeAmount attribute.
     * @return Returns the airServiceFeeAmount.
     */
    @Column(name = "AIR_SRVC_FEE_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getAirServiceFeeAmount() {
        return airServiceFeeAmount;
    }

    /**
     * Sets the airServiceFeeAmount attribute value.
     * @param airServiceFeeAmount The airServiceFeeAmount to set.
     */
    public void setAirServiceFeeAmount(KualiDecimal airServiceFeeAmount) {
        this.airServiceFeeAmount = airServiceFeeAmount;
    }
    public void setAirServiceFeeAmount(String airServiceFeeAmount) {
        this.airServiceFeeAmount = new KualiDecimal(airServiceFeeAmount);
    }

    /**
     * Gets the transactionUniqueId attribute.
     * @return Returns the transactionUniqueId.
     */
    @Column(name = "TRANS_ID", length = 40, nullable = true)
    public String getTransactionUniqueId() {
        return transactionUniqueId;
    }

    /**
     * Sets the transactionUniqueId attribute value.
     * @param transactionUniqueId The transactionUniqueId to set.
     */
    public void setTransactionUniqueId(String transactionUniqueId) {
        this.transactionUniqueId = transactionUniqueId;
    }

    /**
     * Gets the lodgingItineraryNumber attribute.
     * @return Returns the lodgingItineraryNumber.
     */
    @Column(name = "LDG_ITN_NBR", length = 20, nullable = true)
    public String getLodgingItineraryNumber() {
        return lodgingItineraryNumber;
    }

    /**
     * Sets the lodgingItineraryNumber attribute value.
     * @param lodgingItineraryNumber The lodgingItineraryNumber to set.
     */
    public void setLodgingItineraryNumber(String lodgingItineraryNumber) {
        this.lodgingItineraryNumber = lodgingItineraryNumber;
    }

    /**
     * Gets the lodgingPrepayDate attribute.
     * @return Returns the lodgingPrepayDate.
     */
    @Column(name = "LDG_PRPY_DT", nullable = true)
    public Date getLodgingPrepayDate() {
        return lodgingPrepayDate;
    }

    /**
     * Sets the lodgingPrepayDate attribute value.
     * @param lodgingPrepayDate The lodgingPrepayDate to set.
     */
    public void setLodgingPrepayDate(Date lodgingPrepayDate) {
        this.lodgingPrepayDate = lodgingPrepayDate;
    }

    /**
     * Gets the lodgingAmount attribute.
     * @return Returns the lodgingAmount.
     */
    @Column(name = "LDG_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getLodgingAmount() {
        return lodgingAmount;
    }

    /**
     * Sets the lodgingAmount attribute value.
     * @param lodgingAmount The lodgingAmount to set.
     */
    public void setLodgingAmount(KualiDecimal lodgingAmount) {
        this.lodgingAmount = lodgingAmount;
    }
    public void setLodgingAmount(String lodgingAmount) {
        this.lodgingAmount = new KualiDecimal(lodgingAmount);
    }

    /**
     * Gets the lodgingPrepayDaysNumber attribute.
     * @return Returns the lodgingPrepayDaysNumber.
     */
    @Column(name = "LDG_PRPY_NBR", length = 3, nullable = true)
    public String getLodgingPrepayDaysNumber() {
        return lodgingPrepayDaysNumber;
    }

    /**
     * Sets the lodgingPrepayDaysNumber attribute value.
     * @param lodgingPrepayDaysNumber The lodgingPrepayDaysNumber to set.
     */
    public void setLodgingPrepayDaysNumber(String lodgingPrepayDaysNumber) {
        this.lodgingPrepayDaysNumber = lodgingPrepayDaysNumber;
    }

    /**
     * Gets the lodgingPropertyName attribute.
     * @return Returns the lodgingPropertyName.
     */
    @Column(name = "LDG_PRP_NM", length = 30, nullable = true)
    public String getLodgingPropertyName() {
        return lodgingPropertyName;
    }

    /**
     * Sets the lodgingPropertyName attribute value.
     * @param lodgingPropertyName The lodgingPropertyName to set.
     */
    public void setLodgingPropertyName(String lodgingPropertyName) {
        this.lodgingPropertyName = lodgingPropertyName;
    }

    /**
     * Gets the tripLodgingArrivalDate attribute.
     * @return Returns the tripLodgingArrivalDate.
     */
    @Column(name = "TRP_LDG_ARRV_DT", nullable = true)
    public Date getTripLodgingArrivalDate() {
        return tripLodgingArrivalDate;
    }

    /**
     * Sets the tripLodgingArrivalDate attribute value.
     * @param tripLodgingArrivalDate The tripLodgingArrivalDate to set.
     */
    public void setTripLodgingArrivalDate(Date tripLodgingArrivalDate) {
        this.tripLodgingArrivalDate = tripLodgingArrivalDate;
    }

    /**
     * Gets the lodgingDepartureDate attribute.
     * @return Returns the lodgingDepartureDate.
     */
    @Column(name = "LDG_DEPT_DT", nullable = true)
    public Date getLodgingDepartureDate() {
        return lodgingDepartureDate;
    }

    /**
     * Sets the lodgingDepartureDate attribute value.
     * @param lodgingDepartureDate The lodgingDepartureDate to set.
     */
    public void setLodgingDepartureDate(Date lodgingDepartureDate) {
        this.lodgingDepartureDate = lodgingDepartureDate;
    }

    /**
     * Gets the lodgingBookingDate attribute.
     * @return Returns the lodgingBookingDate.
     */
    @Column(name = "LDG_BK_DT", nullable = true)
    public Date getLodgingBookingDate() {
        return lodgingBookingDate;
    }

    /**
     * Sets the lodgingBookingDate attribute value.
     * @param lodgingBookingDate The lodgingBookingDate to set.
     */
    public void setLodgingBookingDate(Date lodgingBookingDate) {
        this.lodgingBookingDate = lodgingBookingDate;
    }

    /**
     * Gets the lodgingPropertyCityName attribute.
     * @return Returns the lodgingPropertyCityName.
     */
    @Column(name = "LDG_PRP_CITY_NM", length = 30, nullable = true)
    public String getLodgingPropertyCityName() {
        return lodgingPropertyCityName;
    }

    /**
     * Sets the lodgingPropertyCityName attribute value.
     * @param lodgingPropertyCityName The lodgingPropertyCityName to set.
     */
    public void setLodgingPropertyCityName(String lodgingPropertyCityName) {
        this.lodgingPropertyCityName = lodgingPropertyCityName;
    }

    /**
     * Gets the lodgingPropertyStateCode attribute.
     * @return Returns the lodgingPropertyStateCode.
     */
    @Column(name = "LDG_PRP_STATE_CD", length = 40, nullable = true)
    public String getLodgingPropertyStateCode() {
        return lodgingPropertyStateCode;
    }

    /**
     * Sets the lodgingPropertyStateCode attribute value.
     * @param lodgingPropertyStateCode The lodgingPropertyStateCode to set.
     */
    public void setLodgingPropertyStateCode(String lodgingPropertyStateCode) {
        this.lodgingPropertyStateCode = lodgingPropertyStateCode;
    }

    /**
     * Gets the lodgingCountryName attribute.
     * @return Returns the lodgingCountryName.
     */
    @Column(name = "LDG_PRP_COUNTRY_NM", length = 2, nullable = true)
    public String getLodgingCountryName() {
        return lodgingCountryName;
    }

    /**
     * Sets the lodgingCountryName attribute value.
     * @param lodgingCountryName The lodgingCountryName to set.
     */
    public void setLodgingCountryName(String lodgingCountryName) {
        this.lodgingCountryName = lodgingCountryName;
    }

    /**
     * Gets the rentalCarItineraryNumber attribute.
     * @return Returns the rentalCarItineraryNumber.
     */
    @Column(name = "RNT_CAR_ITN_NBR", nullable = true)
    public String getRentalCarItineraryNumber() {
        return rentalCarItineraryNumber;
    }

    /**
     * Sets the rentalCarItineraryNumber attribute value.
     * @param rentalCarItineraryNumber The rentalCarItineraryNumber to set.
     */
    public void setRentalCarItineraryNumber(String rentalCarItineraryNumber) {
        this.rentalCarItineraryNumber = rentalCarItineraryNumber;
    }

    /**
     * Gets the rentalCarAmount attribute.
     * @return Returns the rentalCarAmount.
     */
    @Column(name = "RNT_CAR_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRentalCarAmount() {
        return rentalCarAmount;
    }

    /**
     * Sets the rentalCarAmount attribute value.
     * @param rentalCarAmount The rentalCarAmount to set.
     */
    public void setRentalCarAmount(KualiDecimal rentalCarAmount) {
        this.rentalCarAmount = rentalCarAmount;
    }
    public void setRentalCarAmount(String rentalCarAmount) {
        this.rentalCarAmount = new KualiDecimal(rentalCarAmount);
    }

    /**
     * Gets the rentalCarNumberOfDays attribute.
     * @return Returns the rentalCarNumberOfDays.
     */
    @Column(name = "RNT_CAR_NMR_DAYS", length = 3, nullable = true)
    public String getRentalCarNumberOfDays() {
        return rentalCarNumberOfDays;
    }

    /**
     * Sets the rentalCarNumberOfDays attribute value.
     * @param rentalCarNumberOfDays The rentalCarNumberOfDays to set.
     */
    public void setRentalCarNumberOfDays(String rentalCarNumberOfDays) {
        this.rentalCarNumberOfDays = rentalCarNumberOfDays;
    }

    /**
     * Gets the rentalCarCompanyName attribute.
     * @return Returns the rentalCarCompanyName.
     */
    @Column(name = "RNT_CAR_CO_NM", length = 30, nullable = true)
    public String getRentalCarCompanyName() {
        return rentalCarCompanyName;
    }

    /**
     * Sets the rentalCarCompanyName attribute value.
     * @param rentalCarCompanyName The rentalCarCompanyName to set.
     */
    public void setRentalCarCompanyName(String rentalCarCompanyName) {
        this.rentalCarCompanyName = rentalCarCompanyName;
    }

    /**
     * Gets the rentalCarOpenDate attribute.
     * @return Returns the rentalCarOpenDate.
     */
    @Column(name = "RNT_CAR_OPN_DT", nullable = true)
    public Date getRentalCarOpenDate() {
        return rentalCarOpenDate;
    }

    /**
     * Sets the rentalCarOpenDate attribute value.
     * @param rentalCarOpenDate The rentalCarOpenDate to set.
     */
    public void setRentalCarOpenDate(Date rentalCarOpenDate) {
        this.rentalCarOpenDate = rentalCarOpenDate;
    }

    /**
     * Gets the rentalCarCloseDate attribute.
     * @return Returns the rentalCarCloseDate.
     */
    @Column(name = "RNT_CAR_CLOSE_DT", nullable = true)
    public Date getRentalCarCloseDate() {
        return rentalCarCloseDate;
    }

    /**
     * Sets the rentalCarCloseDate attribute value.
     * @param rentalCarCloseDate The rentalCarCloseDate to set.
     */
    public void setRentalCarCloseDate(Date rentalCarCloseDate) {
        this.rentalCarCloseDate = rentalCarCloseDate;
    }

    /**
     * Gets the rentalCarFuelAmount attribute.
     * @return Returns the rentalCarFuelAmount.
     */
    @Column(name = "RNT_CAR_FUEL_AMT", precision=8, scale=2,  nullable = true)
    public KualiDecimal getRentalCarFuelAmount() {
        return rentalCarFuelAmount;
    }

    /**
     * Sets the rentalCarFuelAmount attribute value.
     * @param rentalCarFuelAmount The rentalCarFuelAmount to set.
     */
    public void setRentalCarFuelAmount(KualiDecimal rentalCarFuelAmount) {
        this.rentalCarFuelAmount = rentalCarFuelAmount;
    }
    public void setRentalCarFuelAmount(String rentalCarFuelAmount) {
        this.rentalCarFuelAmount = new KualiDecimal(rentalCarFuelAmount);
    }

    /**
     * Gets the rentalCarAdditionalAmount attribute.
     * @return Returns the rentalCarAdditionalAmount.
     */
    @Column(name = "RNT_CAR_ADD_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRentalCarAdditionalAmount() {
        return rentalCarAdditionalAmount;
    }

    /**
     * Sets the rentalCarAdditionalAmount attribute value.
     * @param rentalCarAdditionalAmount The rentalCarAdditionalAmount to set.
     */
    public void setRentalCarAdditionalAmount(KualiDecimal rentalCarAdditionalAmount) {
        this.rentalCarAdditionalAmount = rentalCarAdditionalAmount;
    }
    public void setRentalCarAdditionalAmount(String rentalCarAdditionalAmount) {
        this.rentalCarAdditionalAmount = new KualiDecimal(rentalCarAdditionalAmount);
    }

    /**
     * Gets the rentalCarTaxAmount attribute.
     * @return Returns the rentalCarTaxAmount.
     */
    @Column(name = "RNT_CAR_TX_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRentalCarTaxAmount() {
        return rentalCarTaxAmount;
    }

    /**
     * Sets the rentalCarTaxAmount attribute value.
     * @param rentalCarTaxAmount The rentalCarTaxAmount to set.
     */
    public void setRentalCarTaxAmount(KualiDecimal rentalCarTaxAmount) {
        this.rentalCarTaxAmount = rentalCarTaxAmount;
    }
    public void setRentalCarTaxAmount(String rentalCarTaxAmount) {
        this.rentalCarTaxAmount = new KualiDecimal(rentalCarTaxAmount);
    }

    /**
     * Gets the rentalCarSurchargeAmount attribute.
     * @return Returns the rentalCarSurchargeAmount.
     */
    @Column(name = "RNT_CAR_SRCHRG_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRentalCarSurchargeAmount() {
        return rentalCarSurchargeAmount;
    }

    /**
     * Sets the rentalCarSurchargeAmount attribute value.
     * @param rentalCarSurchargeAmount The rentalCarSurchargeAmount to set.
     */
    public void setRentalCarSurchargeAmount(KualiDecimal rentalCarSurchargeAmount) {
        this.rentalCarSurchargeAmount = rentalCarSurchargeAmount;
    }
    public void setRentalCarSurchargeAmount(String rentalCarSurchargeAmount) {
        this.rentalCarSurchargeAmount = new KualiDecimal(rentalCarSurchargeAmount);
    }

    /**
     * Gets the rentalCarGovernmentSurchargeAmount attribute.
     * @return Returns the rentalCarGovernmentSurchargeAmount.
     */
    @Column(name = "RNT_CAR_GOVR_SRCHRG_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRentalCarGovernmentSurchargeAmount() {
        return rentalCarGovernmentSurchargeAmount;
    }

    /**
     * Sets the rentalCarGovernmentSurchargeAmount attribute value.
     * @param rentalCarGovernmentSurchargeAmount The rentalCarGovernmentSurchargeAmount to set.
     */
    public void setRentalCarGovernmentSurchargeAmount(KualiDecimal rentalCarGovernmentSurchargeAmount) {
        this.rentalCarGovernmentSurchargeAmount = rentalCarGovernmentSurchargeAmount;
    }
    public void setRentalCarGovernmentSurchargeAmount(String rentalCarGovernmentSurchargeAmount) {
        this.rentalCarGovernmentSurchargeAmount = new KualiDecimal(rentalCarGovernmentSurchargeAmount);
    }

    /**
     * Gets the rentalCarBillAmount attribute.
     * @return Returns the rentalCarBillAmount.
     */
    @Column(name = "RNT_CAR_BILL_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRentalCarBillAmount() {
        return rentalCarBillAmount;
    }

    /**
     * Sets the rentalCarBillAmount attribute value.
     * @param rentalCarBillAmount The rentalCarBillAmount to set.
     */
    public void setRentalCarBillAmount(KualiDecimal rentalCarBillAmount) {
        this.rentalCarBillAmount = rentalCarBillAmount;
    }
    public void setRentalCarBillAmount(String rentalCarBillAmount) {
        this.rentalCarBillAmount = new KualiDecimal(rentalCarBillAmount);
    }

    /**
     * Gets the rentalCarDetailText attribute.
     * @return Returns the rentalCarDetailText.
     */
    @Column(name = "RNT_CAR_DTL", length = 100, nullable = true)
    public String getRentalCarDetailText() {
        return rentalCarDetailText;
    }

    /**
     * Sets the rentalCarDetailText attribute value.
     * @param rentalCarDetailText The rentalCarDetailText to set.
     */
    public void setRentalCarDetailText(String rentalCarDetailText) {
        this.rentalCarDetailText = rentalCarDetailText;
    }

    /**
     * Gets the registrationCompanyName attribute.
     * @return Returns the registrationCompanyName.
     */
    @Column(name = "REG_CO_NM", length = 50, nullable = true)
    public String getRegistrationCompanyName() {
        return registrationCompanyName;
    }

    /**
     * Sets the registrationCompanyName attribute value.
     * @param registrationCompanyName The registrationCompanyName to set.
     */
    public void setRegistrationCompanyName(String registrationCompanyName) {
        this.registrationCompanyName = registrationCompanyName;
    }

    /**
     * Gets the registrationAmount attribute.
     * @return Returns the registrationAmount.
     */
    @Column(name = "REG_AMT", precision=8, scale=2, nullable = true)
    public KualiDecimal getRegistrationAmount() {
        return registrationAmount;
    }

    /**
     * Sets the registrationAmount attribute value.
     * @param registrationAmount The registrationAmount to set.
     */
    public void setRegistrationAmount(KualiDecimal registrationAmount) {
        this.registrationAmount = registrationAmount;
    }
    public void setRegistrationAmount(String registrationAmount) {
        this.registrationAmount = new KualiDecimal(registrationAmount);
    }

    /**
     * Gets the transactionPostingDate attribute.
     * @return Returns the transactionPostingDate.
     */
    @Column(name = "TRANS_POST_DT", nullable = true)
    public Date getTransactionPostingDate() {
        return transactionPostingDate;
    }

    /**
     * Sets the transactionPostingDate attribute value.
     * @param transactionPostingDate The transactionPostingDate to set.
     */
    public void setTransactionPostingDate(Date transactionPostingDate) {
        this.transactionPostingDate = transactionPostingDate;
    }

    /**
     * Gets the objectVerNumber attribute.
     * @return Returns the objectVerNumber.
     */
    @Column(name = "OBJ_VER_NBR", nullable = true)
    public String getObjectVerNumber() {
        return objectVerNumber;
    }

    /**
     * Sets the objectVerNumber attribute value.
     * @param objectVerNumber The objectVerNumber to set.
     */
    public void setObjectVerNumber(String objectVerNumber) {
        this.objectVerNumber = objectVerNumber;
    }

    /**
     * Gets the creationTimestamp attribute.
     * @return Returns the creationTimestamp.
     */
    @Column(name = "CREATION_TS", nullable = true)
   public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    /**
     * Sets the creationTimestamp attribute value.
     * @param creationTimestamp The creationTimestamp to set.
     */
    public void setCreationTimestamp(Timestamp creationTimestamp) {
        this.creationTimestamp = creationTimestamp;
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
    *
    * This method returns the expense type category based on expense type.
    * @return
    */
   public ExpenseTypeMetaCategory getExpenseTypeCategory() {
       ExpenseTypeMetaCategory expenseTypeCategory = null;

       if (StringUtils.isNotEmpty(this.getAirTicketNumber())) {
           expenseTypeCategory = TemConstants.ExpenseTypeMetaCategory.AIRFARE;
       }
       else if (StringUtils.isNotEmpty(this.getLodgingItineraryNumber())) {
           expenseTypeCategory = TemConstants.ExpenseTypeMetaCategory.LODGING;
       }
       else if (StringUtils.isNotEmpty(this.getRentalCarItineraryNumber())) {
           expenseTypeCategory = TemConstants.ExpenseTypeMetaCategory.RENTAL_CAR;
       }

       return expenseTypeCategory;
   }

    /**
     * Gets the profile attribute.
     * @return Returns the profile.
     */
    public TEMProfile getProfile() {
        return profile;
    }

    /**
     * Sets the profile attribute value.
     * @param profile The profile to set.
     */
    public void setProfile(TEMProfile profile) {
        this.profile = profile;
    }

    /**
     * Gets the importBy attribute.
     * @return Returns the importBy.
     */
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
     * Gets the creditCardAgency attribute.
     * @return Returns the creditCardAgency.
     */
    public CreditCardAgency getCreditCardAgency() {
        return creditCardAgency;
    }

    /**
     * Sets the creditCardAgency attribute value.
     * @param creditCardAgency The creditCardAgency to set.
     */
    public void setCreditCardAgency(CreditCardAgency creditCardAgency) {
        this.creditCardAgency = creditCardAgency;
        if (creditCardAgency != null){
            setCreditCardAgencyId(creditCardAgency.getId());
            setAgency(creditCardAgency.getCreditCardOrAgencyName());
        }
    }

    /**
     * Gets the creditCardAgencyId attribute.
     * @return Returns the creditCardAgencyId.
     */
    public Integer getCreditCardAgencyId() {
        return creditCardAgencyId;
    }

    /**
     * Sets the cardId attribute value.
     * @param cardId The cardId to set.
     */
    public void setCreditCardAgencyId(Integer creditCardAgencyId) {
        this.creditCardAgencyId = creditCardAgencyId;
    }

    /**
     * Gets the searchChartOfAccountsCode attribute.
     * @return Returns the searchChartOfAccountsCode.
     */
    public String getSearchChartOfAccountsCode() {
        return searchChartOfAccountsCode;
    }

    /**
     * Sets the searchChartOfAccountsCode attribute value.
     * @param searchChartOfAccountsCode The searchChartOfAccountsCode to set.
     */
    public void setSearchChartOfAccountsCode(String searchChartOfAccountsCode) {
        this.searchChartOfAccountsCode = searchChartOfAccountsCode;
    }

    /**
     * Gets the searchAccountNumber attribute.
     * @return Returns the searchAccountNumber.
     */
    public String getSearchAccountNumber() {
        return searchAccountNumber;
    }

    /**
     * Sets the searchAccountNumber attribute value.
     * @param searchAccountNumber The searchAccountNumber to set.
     */
    public void setSearchAccountNumber(String searchAccountNumber) {
        this.searchAccountNumber = searchAccountNumber;
    }

    /**
     * Gets the searchSubAccountNumber attribute.
     * @return Returns the searchSubAccountNumber.
     */
    public String getSearchSubAccountNumber() {
        return searchSubAccountNumber;
    }

    /**
     * Sets the searchSubAccountNumber attribute value.
     * @param searchSubAccountNumber The searchSubAccountNumber to set.
     */
    public void setSearchSubAccountNumber(String searchSubAccountNumber) {
        this.searchSubAccountNumber = searchSubAccountNumber;
    }

    /**
     * Gets the searchChart attribute.
     * @return Returns the searchChart.
     */
    public Chart getSearchChart() {
        return searchChart;
    }

    /**
     * Sets the searchChart attribute value.
     * @param searchChart The searchChart to set.
     */
    public void setSearchChart(Chart searchChart) {
        this.searchChart = searchChart;
    }

    /**
     * Gets the searchAccount attribute.
     * @return Returns the searchAccount.
     */
    public Account getSearchAccount() {
        return searchAccount;
    }

    /**
     * Sets the searchAccount attribute value.
     * @param searchAccount The searchAccount to set.
     */
    public void setSearchAccount(Account searchAccount) {
        this.searchAccount = searchAccount;
    }

    /**
     * Gets the searchSubAccount attribute.
     * @return Returns the searchSubAccount.
     */
    public SubAccount getSearchSubAccount() {
        return searchSubAccount;
    }

    /**
     * Sets the searchSubAccount attribute value.
     * @param searchSubAccount The searchSubAccount to set.
     */
    public void setSearchSubAccount(SubAccount searchSubAccount) {
        this.searchSubAccount = searchSubAccount;
    }

    public String getStagingFileName() {
        return stagingFileName;
    }

    public void setStagingFileName(String stagingFileName) {
        this.stagingFileName = stagingFileName;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    public Boolean getManualCreated() {
        return manualCreated;
    }

    public void setManualCreated(Boolean manualCreated) {
        this.manualCreated = manualCreated;
    }

    public Integer getCopiedFromId() {
        return copiedFromId;
    }

    public void setCopiedFromId(Integer copiedFromId) {
        this.copiedFromId = copiedFromId;
    }

}
