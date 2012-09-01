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
package org.kuali.kfs.module.tem.batch.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataValidation;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

import static org.kuali.kfs.module.tem.util.BufferedLogger.*;

/**
 * Service for handling imported expenses using the IU method or "By Trip Id" method. 
 * 
 * @see org.kuali.kfs.module.tem.document.validation.impl.TravelAgencyAuditValidation
 */
public class ExpenseImportByTripServiceImpl extends ExpenseImportServiceBase implements ExpenseImportByTripService  {    
    private TravelAuthorizationService travelAuthorizationService;
    private TemProfileService temProfileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private TravelExpenseService travelExpenseService;
    private ImportedExpensePendingEntryService importedExpensePendingEntryService;

    protected List<String> errorMessages;
    
    public ExpenseImportByTripServiceImpl() {
        errorMessages = new ArrayList<String>();        
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#areMandatoryFieldsPresent(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public boolean areMandatoryFieldsPresent(AgencyStagingData agencyData) {

        boolean requiredFieldsValid = true;
        ErrorMessage error = null;
        if (StringUtils.isEmpty(agencyData.getTripId())) {
            error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, "tripId");
            errorMessages.add(error.toString());
            requiredFieldsValid = false;
        }
        if (isAccountingInfoMissing(agencyData)) {
            requiredFieldsValid = false;
        }
        if (isAmountEmpty(agencyData.getTripExpenseAmount())) {
            error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, "tripExpenseAmount");
            errorMessages.add(error.toString());
            requiredFieldsValid = false;
        }
        if (StringUtils.isEmpty(agencyData.getTripInvoiceNumber())) {
            error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, "tripInvoiceNumber");
            errorMessages.add(error.toString());
            requiredFieldsValid = false;
        }
        if (ObjectUtils.isNull(agencyData.getTransactionPostingDate())) {
            error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, "transactionPostingDate");
            errorMessages.add(error.toString());
            requiredFieldsValid = false;
        }
        if (isTripDataMissing(agencyData)) {
            error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_AIR_LODGING_RENTAL_MISSING);
            errorMessages.add(error.toString());
            requiredFieldsValid = false;
        }
        
        if (!requiredFieldsValid) {
            error("Missing one or more required fields.");
            
        }
        
        return requiredFieldsValid;
    }

    /**
     * 
     * This method loops through the {@link TripAccountingInformation} and 
     * verifies that each one has a Chart Code and Account Number.
     * @param agencyData
     * @return
     */
    @Override
    public boolean isAccountingInfoMissing(AgencyStagingData agencyData) {
        
        boolean accountInfoMissing = false;
        List<TripAccountingInformation> accountingInfoList = agencyData.getTripAccountingInformation();
        for (TripAccountingInformation account : accountingInfoList) {
            if (StringUtils.isEmpty(account.getTripChartCode())) {
                ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, "tripChartCode");
                errorMessages.add(error.toString());
                accountInfoMissing = true;
                 
            }
            if (StringUtils.isEmpty(account.getTripAccountNumber())) {
               ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, "tripAccountNumber");
               errorMessages.add(error.toString());
               accountInfoMissing = true;
                
            }
        }
        
        return accountInfoMissing;
    }

    /**
     * 
     * This method verifies there is either Airfare, Lodging or Rental Car data in the Agency Data
     * @param agencyData
     * @return
     */
    @Override
    public boolean isTripDataMissing(AgencyStagingData agencyData) {

        if (StringUtils.isEmpty(agencyData.getAirTicketNumber()) && 
            StringUtils.isEmpty(agencyData.getLodgingItineraryNumber()) && 
            StringUtils.isEmpty(agencyData.getRentalCarItineraryNumber())) {
            
            return true;
        }
        return false;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public AgencyStagingData validateAgencyData(AgencyStagingData agencyData) {        
        info("Validating agency data. tripId: ", agencyData.getTripId());
        
        errorMessages.clear();

        // Perform a duplicate check first. 
        if (isDuplicate(agencyData)) {
            error("Duplicate Agency Data record. tripId: ", agencyData.getTripId());
            return null;
        }
        
        // see if there's a TA for trip id (travelDocumentIdentifier)
        final TravelAuthorizationDocument ta = validateTripId(agencyData);
        if (ObjectUtils.isNotNull(ta)) {
            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

            final TEMProfile profile = temProfileService.findTemProfileById(ta.getProfileId());
            agencyData.setTemProfileId(profile.getProfileId());
            agencyData = validateAccountingInfo(profile, agencyData, ta);
        }
        
        if (!isCreditCardAgencyValid(agencyData)) {
            errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_CCA);
        }
        
        info("Finished validating agency data. tripId:", agencyData.getTripId());
        agencyData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
        return agencyData;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateTripId(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public TravelAuthorizationDocument validateTripId(AgencyStagingData agencyData) {
        
        final TravelAuthorizationDocument ta = getTravelAuthorizationDocument(agencyData.getTripId());
        if (ObjectUtils.isNotNull(ta)) {
            return ta;
        }
        error("Unable to retrieve TA document for tripId: ", agencyData.getTripId());
        setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_TRIPID);
        errorMessages.add(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID);
        
        return null;
    }
    
    /**
     * 
     * This method gets the TA by travel document identifier
     * @param travelDocumentIdentifier
     * @return
     */
    protected TravelAuthorizationDocument getTravelAuthorizationDocument(String travelDocumentIdentifier) {
        List<TravelAuthorizationDocument> taDocs = (List<TravelAuthorizationDocument>) travelAuthorizationService.find(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(taDocs) && taDocs.size() == 1) {
            return taDocs.get(0);
        }
        return null;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.module.tem.document.TravelAuthorizationDocument)
     */
    @Override
    public AgencyStagingData validateAccountingInfo(TEMProfile profile, AgencyStagingData agencyData, TravelAuthorizationDocument ta) {

        // Get VALIDATION_ACCOUNTING_LINE parameter to determine which fields to validate
        List<String> validationParams = getParameterValues(TravelParameters.VALIDATION_ACCOUNTING_LINE, AgencyStagingDataValidation.AGENCY_DATA_VALIDATION_DTL);
        if (ObjectUtils.isNull(validationParams)) {
            return agencyData;
        }
        
        List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
        ErrorMessage error = null;
        for (TripAccountingInformation account : accountingInfo) {
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_ACCOUNT)) {
                if (!isAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber())) {
                    
                    error("Invalid Account in Agency Data record. tripId: ", agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber());
                    account.setTripAccountNumber(profile.getDefaultAccount());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM, account.getTripChartCode(), account.getTripAccountNumber());
                    errorMessages.add(error.toString());
                }
            }
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_SUBACCOUNT)) {
                // sub account is optional
                if (StringUtils.isNotEmpty(account.getTripSubAccountNumber()) && 
                    !isSubAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getTripSubAccountNumber())) {
                    
                    error("Invalid SubAccount in Agency Data record. tripId: ", agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber() + " subaccount: " + account.getTripSubAccountNumber());
                    
                    account.setTripSubAccountNumber(profile.getDefaultSubAccount());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT, account.getTripSubAccountNumber());
                    errorMessages.add(error.toString());
                }
            }
            
            // project code is optional
            if (StringUtils.isNotEmpty(account.getProjectCode()) &&
                !isProjectCodeValid(account.getProjectCode())) {
                
                error("Invalid Project in Agency Data record. tripId: ", agencyData.getTripId(), " project code: " + account.getProjectCode());
                
                account.setProjectCode(profile.getDefaultProjectCode());
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
                error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE, account.getProjectCode());
                errorMessages.add(error.toString());
            }
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_OBJECT_CODE)) {
                // object code is optional
                if (StringUtils.isNotEmpty(account.getObjectCode()) &&
                    !isObjectCodeValid(account.getTripChartCode(), account.getObjectCode())) {
                    
                    error("Invalid Object Code in Agency Data record. tripId: ", agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " object code: " + account.getObjectCode());
                    
                    account.setObjectCode(getObjectCode(agencyData));
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_OBJECT);
                    
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_OBJECT_CODE, account.getTripChartCode(), account.getObjectCode());
                    errorMessages.add(error.toString());
                }
            }
            
            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_SUBOBJECT_CODE)) {
                // sub object code is optional
                if (StringUtils.isNotEmpty(account.getSubObjectCode()) && 
                    !isSubObjectCodeValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode())) {
                    
                    error("Invalid SubObject Code in Agency Data record. tripId: ", agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " object code: " + account.getObjectCode() + " subobject code: " + account.getSubObjectCode());
                    
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBOBJECT);
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUB_OBJECT_CODE, account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode());
                    errorMessages.add(error.toString());
                }
            }
        }
                
        return agencyData;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#isDuplicate(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public boolean isDuplicate(AgencyStagingData agencyData) {
        
        // Verify that this isn't a duplicate entry based on the following: 
        // Trip ID, Agency Name, Transaction Date, Transaction Amount
        
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        
        if (StringUtils.isNotEmpty(agencyData.getTripId())) {
            fieldValues.put(TemPropertyConstants.TRIP_ID, agencyData.getTripId());
        }
        if (StringUtils.isNotEmpty(agencyData.getAgency())) {
            fieldValues.put(TemPropertyConstants.AGENCY, agencyData.getAgency());
        }
        if (ObjectUtils.isNotNull(agencyData.getTransactionPostingDate())) {
            fieldValues.put(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyData.getTransactionPostingDate());
        }
        if (ObjectUtils.isNotNull(agencyData.getTripExpenseAmount())) {
            fieldValues.put(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyData.getTripExpenseAmount());
        }
                
        List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
        if (ObjectUtils.isNull(agencyDataList) || agencyDataList.size() == 0) {
            return false;
        }
        error("Found a duplicate entry for agencyData with tripId: ", agencyData.getTripId() + " matching agency id: " + agencyDataList.get(0).getId());
        //grab the id of the record that this is a dupe of (just the first one if multiple
        Integer dupeId = agencyDataList.get(0).getId();
        agencyData.setDuplicateRecordId(dupeId);
        ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_DUPLICATE_RECORD, 
                agencyData.getTripId(), agencyData.getAgency(),agencyData.getTransactionPostingDate().toString(), agencyData.getTripExpenseAmount().toString());
        errorMessages.add(error.toString());
        return true;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#reconciliateExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @SuppressWarnings("null")
    @Override
    public boolean reconciliateExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        info("Reconciling expense for agency data: ", agencyData.getId(), " tripId: ", agencyData.getTripId());
        
        String expenseType = agencyData.getExpenseType();
        
        // If there's no object code, this param is set by expense type and used to lookup the expense type and get an object code based on it.
        String expenseTypeParamCode = null;
        
        // This is the "match process" - see if there's credit card data that matches the agency data
        CreditCardStagingData ccData = null;
        if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.AIRFARE)) {
            // see if there's a CC that matches ticket number, service fee number, amount
            ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getAirTicketNumber(), agencyData.getAirServiceFeeNumber());   
            expenseTypeParamCode = TravelParameters.EXPENSE_TYPE_FOR_AIRFARE;
        }
        else if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.LODGING)) {
            // see if there's a CC that matches lodging itinerary number and amount
            ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getLodgingItineraryNumber());
            expenseTypeParamCode = TravelParameters.EXPENSE_TYPE_FOR_LODGING;
        }
        else if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.RENTAL_CAR)) {
            // see if there's a CC that matches rental car itinerary number and amount
            ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getRentalCarItineraryNumber());            
            expenseTypeParamCode = TravelParameters.EXPENSE_TYPE_FOR_RENTAL_CAR;
        }
        
        if (ObjectUtils.isNotNull(ccData)) {
            debug("Found a match for Agency: ", agencyData.getId(), " Credit Card: ", ccData.getId(), " tripId: ", agencyData.getTripId());
            TemTravelExpenseTypeCode travelExpenseType = getTravelExpenseType(expenseTypeParamCode, agencyData.getTripId());
            HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(agencyData, ccData, travelExpenseType);
            AgencyServiceFee serviceFee = getAgencyServiceFee(agencyData.getDistributionCode());
            
            List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
            List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
            
            // Need to split up the amounts if there are multiple accounts
            KualiDecimal remainingAmount = agencyData.getTripExpenseAmount();
            KualiDecimal numAccounts = new KualiDecimal(accountingInfo.size());
            KualiDecimal currentAmount = agencyData.getTripExpenseAmount().divide(numAccounts);
            KualiDecimal remainingFeeAmount = new KualiDecimal(0);
            KualiDecimal currentFeeAmount = new KualiDecimal(0);
            if (ObjectUtils.isNotNull(serviceFee)) {
                remainingFeeAmount = serviceFee.getServiceFee();
                currentFeeAmount = serviceFee.getServiceFee().divide(numAccounts);
            }
            
            String creditObjectCode = getParameter(AgencyMatchProcessParameter.AP_CLEARING_CTS_PAYMENT_OBJECT_CODE, AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE);

            boolean allGlpesCreated = true;

            for (int i = 0; i < accountingInfo.size(); i++) {
                TripAccountingInformation info = accountingInfo.get(i);
                
                // If its the last account, use the remainingAmount to resolve rounding
                if (i < accountingInfo.size() - 1) {
                    remainingAmount = remainingAmount.subtract(currentAmount);
                    remainingFeeAmount = remainingFeeAmount.subtract(currentFeeAmount);
                }
                else {
                    currentAmount = remainingAmount;
                    currentFeeAmount = remainingFeeAmount;
                }
                String objectCode = info.getObjectCode();
                if (StringUtils.isEmpty(objectCode)) {
                    objectCode = travelExpenseType.getFinancialObjectCode();
                }

                // set the amount on the accounting info for documents pulling in imported expenses
                info.setAmount(currentAmount);
                
                if (ObjectUtils.isNotNull(serviceFee)) {
                    // Agency Data has a DI Code that maps to an Agency Service Fee, create GLPEs for it.
                    info("Processing Service Fee GLPE for agency: ", agencyData.getId(), " tripId: ", agencyData.getTripId());
                    
                    final boolean generateOffset = true;
                    List<GeneralLedgerPendingEntry> pendingEntries = importedExpensePendingEntryService.buildDebitPendingEntry(agencyData, info, sequenceHelper, info.getObjectCode(), currentFeeAmount, generateOffset);
                    allGlpesCreated = importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, false, generateOffset);
                    
                    pendingEntries = importedExpensePendingEntryService.buildServiceFeeCreditPendingEntry(agencyData, info, sequenceHelper, serviceFee, currentFeeAmount, generateOffset);
                    allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, true, generateOffset);
                }

                final boolean generateOffset = true;
                List<GeneralLedgerPendingEntry> pendingEntries = importedExpensePendingEntryService.buildDebitPendingEntry(agencyData, info, sequenceHelper, objectCode, currentAmount, generateOffset);
                allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, false, generateOffset);
                
                pendingEntries = importedExpensePendingEntryService.buildCreditPendingEntry(agencyData, info, sequenceHelper, creditObjectCode, currentAmount, generateOffset);
                allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, true, generateOffset);
            }
            
            if (entries.size() > 0 && allGlpesCreated) {
                businessObjectService.save(entries);
                businessObjectService.save(expense);
                ccData.setErrorCode(CreditCardStagingDataErrorCodes.CREDIT_CARD_MOVED_TO_HISTORICAL);
                businessObjectService.save(ccData);
                agencyData.setMoveToHistoryIndicator(true);
                agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);
            }
            else {
                error("An errror occured while creating GLPEs for agency: ", agencyData.getId(), " tripId", agencyData.getTripId(), " Not creating historical expense for this agency record.");
                return false;
            }
            
        }
        else {
            info("No match found for agency data: ", agencyData.getId(), " tripId:", agencyData.getTripId());
        }
        
        info("Finished reconciling expense for agency data: ", agencyData.getId(), " tripId: ", agencyData.getTripId());
        return true;
    }

    /**
     * 
     * This method gets the {@link TemTravelExpenseTypeCode} associated with the expense type and travel document
     * @param expenseTypeParamCode
     * @param travelDocumentIdentifier
     * @return
     */
    protected TemTravelExpenseTypeCode getTravelExpenseType(String expenseTypeParamCode, String travelDocumentIdentifier) {
        // get the expense type parameter
        String expenseTypeCode = getParameter(expenseTypeParamCode, TravelParameters.DOCUMENT_DTL_TYPE);
        if (StringUtils.isNotEmpty(expenseTypeCode)) {
        
            // Need to get the Doc Type, Traveler Type and Trip Type from the TA
            // Note the tripId has already been validated at this point, so the TA should be there.
            TravelAuthorizationDocument ta = getTravelAuthorizationDocument(travelDocumentIdentifier);
            if (ObjectUtils.isNotNull(ta)) {
                return travelExpenseService.getExpenseType(expenseTypeCode, ta.getDocumentTypeName(), ta.getTripTypeCode(), ta.getTraveler().getTravelerTypeCode());
            }
        }
        error("Unable to retrieve TemTravelExpenseTypeCode");
        return new TemTravelExpenseTypeCode();

    }
    
    /**
     * 
     * This method returns the {@link AgencyServiceFee} by distribution code (diCode).
     * @param distributionCode
     * @return
     */
    private AgencyServiceFee getAgencyServiceFee(String distributionCode) {
        if (StringUtils.isNotEmpty(distributionCode)) {
            Map<String,String> criteria = new HashMap<String,String>(1);
            criteria.put(TemPropertyConstants.DISTRIBUTION_CODE, distributionCode);
            List<AgencyServiceFee> serviceFee = (List<AgencyServiceFee>) getBusinessObjectService().findMatching(AgencyServiceFee.class, criteria);
            if (ObjectUtils.isNotNull(serviceFee) && serviceFee.size() > 0) {
                return serviceFee.get(0);
            }
        }
        return null;
    }

    /**
     * Gets the travelAuthorizationService attribute. 
     * @return Returns the travelAuthorizationService.
     */
    public TravelAuthorizationService getTravelAuthorizationService() {
        return travelAuthorizationService;
    }

    /**
     * Sets the travelAuthorizationService attribute value.
     * @param travelAuthorizationService The travelAuthorizationService to set.
     */
    public void setTravelAuthorizationService(TravelAuthorizationService travelAuthorizationService) {
        this.travelAuthorizationService = travelAuthorizationService;
    }

    /**
     * Gets the temProfileService attribute. 
     * @return Returns the temProfileService.
     */
    public TemProfileService getTemProfileService() {
        return temProfileService;
    }

    /**
     * Sets the temProfileService attribute value.
     * @param temProfileService The temProfileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    @Override
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Gets the travelExpenseService attribute. 
     * @return Returns the travelExpenseService.
     */
    @Override
    public TravelExpenseService getTravelExpenseService() {
        return travelExpenseService;
    }

    /**
     * Sets the travelExpenseService attribute value.
     * @param travelExpenseService The travelExpenseService to set.
     */
    public void setTravelExpenseService(TravelExpenseService travelExpenseService) {
        this.travelExpenseService = travelExpenseService;
    }

    @Override
    public List<String> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public void setErrorMessages(List<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

}
