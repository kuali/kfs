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
package org.kuali.kfs.module.tem.document;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.tem.TravelEntertainmentMovingTravelDocument;
import org.kuali.kfs.module.tem.businessobject.GroupTraveler;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.ImportedExpense;
import org.kuali.kfs.module.tem.businessobject.ActualExpense;
import org.kuali.kfs.module.tem.businessobject.PerDiemExpense;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.businessobject.SpecialCircumstances;
import org.kuali.kfs.module.tem.businessobject.TEMExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Abstract Travel Document Base
 */
public interface TravelDocument extends AccountingDocument, TravelEntertainmentMovingTravelDocument {

    /**
     * Determines if this document should be able to return to the fiscal officer node again. This can happen
     * if the user has rights to reroute and also if the document is already ENROUTE.
     * 
     * @return true if the doucment is currently enroute and reroutable
     */
    boolean canReturn();

    void initiateDocument();

    @Override
    String getTravelDocumentIdentifier();

    void setTravelDocumentIdentifier(String travelDocumentIdentifier);

    String getAppDocStatus();

    Integer getTravelDocumentLinkIdentifier();

    void setTravelDocumentLinkIdentifier(Integer travelDocumentLinkIdentifier);

    @Override
    /**
     * Gets the traveler attribute.
     * 
     * @return Returns the traveler.
     */
    TravelerDetail getTraveler();

    /**
     * Sets the traveler attribute value.
     * 
     * @param traveler The traveler to set.
     */
    void setTraveler(TravelerDetail traveler);


    /**
     * Gets the travelerDetailId attribute.
     * 
     * @return Returns the travelerDetailId.
     */
    Integer getTravelerDetailId();

    /**
     * Sets the travelerDetailId attribute value.
     * 
     * @param travelerDetailId The travelerDetailId to set.
     */
    void setTravelerDetailId(Integer travelerDetailId);

    /**
     * This method sets the trip description for this request
     * 
     * @param tripDescription
     */
    void setTripDescription(String tripDescription);

    public Integer getPrimaryDestinationId();

    public void setPrimaryDestinationId(Integer primaryDestinationId);

    public PrimaryDestination getPrimaryDestination();

    public void setPrimaryDestination(PrimaryDestination primaryDestination);
    
    @Override
    public String getPrimaryDestinationName();
    
    public void setPrimaryDestinationName(String primaryDestinationName);
    
    public String getPrimaryDestinationCountryState();
    
    public void setPrimaryDestinationCountryState(String primaryDestinationCountryState);
    
    public String getPrimaryDestinationCounty();
    
    public void setPrimaryDestinationCounty(String primaryDestinationCounty);
    
    public Boolean getPrimaryDestinationIndicator();
    
    public void setPrimaryDestinationIndicator(Boolean primaryDestinationIndicator);
    
    /**
     * This method returns the trip type associated with this Travel Request document
     * 
     * @return trip type code
     */
    TripType getTripType();

    /**
     * This method sets the trip type should only be used by the ojb retrieval
     * 
     * @param tripType
     */
    void setTripType(TripType tripType);

    /**
     * This method returns the trip type code associated with the travel request document
     * 
     * @return trip type code
     */
    String getTripTypeCode();

    /**
     * This method returns the trip type code for this travel request document
     * 
     * @param tripTypeCode
     */
    void setTripTypeCode(String tripTypeCode);

    @Override
    /**
     * This method gets the begin date for this trip
     * 
     * @return trip begin date
     */
    Timestamp getTripBegin();

    /**
     * This method sets the trip begin date for this request
     * 
     * @param tripBegin
     */
    void setTripBegin(Timestamp tripBegin);

    /**
     * This method returns the trip end date for this request
     * 
     * @return trip end date
     */
    Timestamp getTripEnd();


    /**
     * This method sets the trip end date for this request
     * 
     * @param tripEnd
     */
    void setTripEnd(Timestamp tripEnd);

    KualiDecimal getExpenseLimit();

    void setExpenseLimit(KualiDecimal expenseLimit);

    void setSpecialCircumstances(final List<SpecialCircumstances> specialCircumstances);

    List<SpecialCircumstances> getSpecialCircumstances();

    List<PerDiemExpense> getPerDiemExpenses();

    public void setPerDiemExpenses(List<PerDiemExpense> perDiemExpenses);

    public KualiDecimal getEncumbranceTotal();

    void enableExpenseTypeSpecificFields(final List<ActualExpense> actualExpenses);

    KualiDecimal getTotalPendingAmount(ActualExpense actualExpense);

    public KualiDecimal getParentExpenseAmount(List<ActualExpense> actualExpenses, Long id);

    public KualiDecimal getTotalDetailExpenseAmount(ActualExpense actualExpense);

    public ActualExpense getParentExpenseRecord(List<ActualExpense> actualExpenses, Long id);

    public KualiDecimal getActualExpensesTotal();

    void addActualExpense(final ActualExpense line);

    void removeActualExpense(final Integer index);
    
    public String getDelinquentAction();
    
    public boolean canDisplayAgencySitesUrl();

    public String getAgencySitesUrl();
    
    public boolean canPassTripIdToAgencySites();
    
    /**
     * 
     * This method provides the same getter call for travel doc and tem profile's profileId.
     * @return
     */
    public Integer getProfileId();

    /**
     * 
     * This method provides additional support to populate profile.
     * @return
     */
    public void setProfileId(Integer profileId);
    
    public Integer getTemProfileId();

    public void setTemProfileId(Integer temProfileId);

    public TEMProfile getTemProfile();

    /**
     * Sets the temProfile attribute value.
     * @param temProfile The temProfile to set.
     */
    public void setTemProfile(TEMProfile temProfile);
    public List<TransportationModeDetail> getTransportationModes();

    public void setTransportationModes(List<TransportationModeDetail> transportationModes);
    
    public List<GroupTraveler> getGroupTravelers();
    
    public void setGroupTravelers(List<GroupTraveler> groupTravelers);
    
    public List<TravelAdvance> getTravelAdvances();
    
    public void setTravelAdvances(List<TravelAdvance> travelAdvances);
    
    public List<ActualExpense> getActualExpenses();
    
    public void setActualExpenses(List<ActualExpense> actualExpenses);
    
    public List<ImportedExpense> getImportedExpenses();
    
    public void setImportedExpenses(List<ImportedExpense> importedExpenses);
    
    public KualiDecimal getTotalFor(final String financialObjectCode);

    public KualiDecimal getDocumentGrandTotal();
    
    public KualiDecimal getDailyTotalGrandTotal();
    
    public KualiDecimal getReimbursableTotal();
    
    public KualiDecimal getNonReimbursableTotal();
    
    public KualiDecimal getApprovedAmount();
    
    public void addExpense(TEMExpense line);
    
    public void addExpenseDetail(TEMExpense line, Integer index);

    void removeExpense(TEMExpense line, Integer index);
    
    void removeExpenseDetail(TEMExpense line, Integer index);
    
    public KualiDecimal getCTSTotal();
    
    public KualiDecimal getCorporateCardTotal();
    
    public AccountingDistributionService getAccountingDistributionService();
    
    public List<HistoricalTravelExpense> getHistoricalTravelExpenses();

    public void setHistoricalTravelExpenses(List<HistoricalTravelExpense> historicalTravelExpenses);

    public String getMealWithoutLodgingReason();   
    
    public String getDocumentTypeName();
    
    public String getReportPurpose();
    
    public void populateVendorPayment(DisbursementVoucherDocument disbursementVoucherDocument);
    
    public KualiDecimal getPerDiemAdjustment();

    public void setPerDiemAdjustment(KualiDecimal perDiemAdjustment);
    
    /**
     * Populate the fields from the Travel document to create the DisbursementVoucherDocument
     * 
     * @param disbursementVoucherDocument
     */
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument);
    
    /**
     * Return the source accounting lines which will be used for reimbursement in DV
     * 
     * @return
     */
    public List<SourceAccountingLine> getReimbursableSourceAccountingLines();
}
