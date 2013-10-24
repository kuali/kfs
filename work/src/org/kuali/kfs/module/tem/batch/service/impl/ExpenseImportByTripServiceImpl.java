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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataValidation;
import org.kuali.kfs.module.tem.TemConstants.CreditCardStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.ExpenseTypes;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.AgencyDataImportStep;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyServiceFee;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.ExpenseTypeObjectCode;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelAuthorizationService;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Service for handling imported expenses using the IU method or "By Trip Id" method.
 *
 * @see org.kuali.kfs.module.tem.document.validation.impl.AgencyStagingDataValidation
 */
public class ExpenseImportByTripServiceImpl extends ExpenseImportServiceBase implements ExpenseImportByTripService  {

    public static Logger LOG = Logger.getLogger(ExpenseImportByTripServiceImpl.class);

    private TravelAuthorizationService travelAuthorizationService;
    private TemProfileService temProfileService;
    private BusinessObjectService businessObjectService;
    private DateTimeService dateTimeService;
    private TravelExpenseService travelExpenseService;
    private ImportedExpensePendingEntryService importedExpensePendingEntryService;
    private TravelDocumentService travelDocumentService;

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#areMandatoryFieldsPresent(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateMandatoryFieldsPresent(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (StringUtils.isEmpty(agencyData.getTripId())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_ID);
            errorMessages.add(error);
        }

        errorMessages.addAll(validateMissingAccountingInfo(agencyData));

        if (isAmountEmpty(agencyData.getTripExpenseAmount())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_EXPENSE_AMOUNT);
            errorMessages.add(error);
        }
        if (StringUtils.isEmpty(agencyData.getTripInvoiceNumber())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_INVOICE_NUMBER);
            errorMessages.add(error);
        }
        if (ObjectUtils.isNull(agencyData.getTransactionPostingDate())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRANSACTION_POSTING_DATE);
            errorMessages.add(error);
        }
        if (isTripDataMissing(agencyData)) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_AIR_LODGING_RENTAL_MISSING);
            errorMessages.add(error);
        }

        if (!errorMessages.isEmpty()) {
            LOG.error("Missing one or more required fields.");
        }

        return errorMessages;
    }

    /**
     *
     * This method loops through the {@link TripAccountingInformation} and
     * verifies that each one has a Chart Code and Account Number.
     * @param agencyData
     * @return
     */
    @Override
    public List<ErrorMessage> validateMissingAccountingInfo(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        boolean accountInfoMissing = false;
        List<TripAccountingInformation> accountingInfoList = agencyData.getTripAccountingInformation();

        if (accountingInfoList.isEmpty()) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_REQUIRED_ACCOUNT_INFO);
            errorMessages.add(error);
        }
        else {
            for (TripAccountingInformation account : accountingInfoList) {
                if (StringUtils.isEmpty(account.getTripChartCode())) {
                    ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_CHART_CODE);
                    errorMessages.add(error);
                }
                if (StringUtils.isEmpty(account.getTripAccountNumber())) {
                   ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_ACCOUNT_NUMBER);
                   errorMessages.add(error);
                }
            }
        }

        return errorMessages;
    }


    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateAgencyData(AgencyStagingData agencyData) {
        LOG.info("Validating agency data. tripId: "+ agencyData.getTripId());

        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        errorMessages = validateDuplicateData(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        errorMessages = validateTripId(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }
        else {
            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
            errorMessages = validateAccountingInfo(agencyData);
        }

        if (!isCreditCardAgencyValid(agencyData)) {
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_CREDIT_CARD_DATA_INVALID_CCA));
        }

        LOG.info("Finished validating agency data. tripId:"+ agencyData.getTripId());
        agencyData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateTripId(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateTripId(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        TravelDocument travelDocument = getTravelDocumentService().getTravelDocument(agencyData.getTripId());
        if (ObjectUtils.isNotNull(travelDocument)) {
            return errorMessages;
        }

        LOG.error("Unable to retrieve a travel document for tripId: "+ agencyData.getTripId());
        setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_TRIPID);
        errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID));

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateAccountingInfo(AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        TravelDocument travelDocument = getTravelDocumentService().getTravelDocument(agencyData.getTripId());
        if (ObjectUtils.isNull(travelDocument)) {
            LOG.error("Unable to retrieve a travel document for the tripId: "+ agencyData.getTripId());
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRIP_ID));
            return errorMessages;
        }

        TEMProfile profile = travelDocument.getTemProfile();
        if (ObjectUtils.isNull(profile)) {
            LOG.error("Found null profile on TravelDocument: "+ travelDocument.getDocumentNumber());
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TEM_PROFILE,travelDocument.getDocumentNumber()));
            return errorMessages;
        }

        // Get ACCOUNTING_LINE_VALIDATION parameter to determine which fields to validate
        Collection<String> validationParams = getParameterService().getParameterValuesAsString(AgencyDataImportStep.class, TravelParameters.ACCOUNTING_LINE_VALIDATION);
        if (ObjectUtils.isNull(validationParams)) {
            return errorMessages;
        }

        List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
        ErrorMessage error = null;
        for (TripAccountingInformation account : accountingInfo) {

            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_ACCOUNT)) {
                if (!isAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber())) {

                    LOG.error("Invalid Account in Agency Data record. tripId: "+ agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber());
                    account.setTripAccountNumber(profile.getDefaultAccount());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM, account.getTripChartCode(), account.getTripAccountNumber());
                    errorMessages.add(error);
                }
            }

            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_SUBACCOUNT)) {
                // sub account is optional
                if (StringUtils.isNotEmpty(account.getTripSubAccountNumber()) &&
                    !isSubAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getTripSubAccountNumber())) {

                    LOG.error("Invalid SubAccount in Agency Data record. tripId: "+ agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber() + " subaccount: " + account.getTripSubAccountNumber());

                    account.setTripSubAccountNumber(profile.getDefaultSubAccount());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT, account.getTripSubAccountNumber());
                    errorMessages.add(error);
                }
            }

            // project code is optional
            if (StringUtils.isNotEmpty(account.getProjectCode()) &&
                !isProjectCodeValid(account.getProjectCode())) {

                LOG.error("Invalid Project in Agency Data record. tripId: "+ agencyData.getTripId()+ " project code: " + account.getProjectCode());

                account.setProjectCode(profile.getDefaultProjectCode());
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
                error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE, account.getProjectCode());
                errorMessages.add(error);
            }

            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_OBJECT_CODE)) {
                // object code is optional
                if (StringUtils.isNotEmpty(account.getObjectCode()) &&
                    !isObjectCodeValid(account.getTripChartCode(), account.getObjectCode())) {

                    LOG.error("Invalid Object Code in Agency Data record. tripId: "+ agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " object code: " + account.getObjectCode());

                    account.setObjectCode(getObjectCode(agencyData));
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_OBJECT);

                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_OBJECT_CODE, account.getTripChartCode(), account.getObjectCode());
                    errorMessages.add(error);
                }
            }

            if (validationParams.contains(AgencyStagingDataValidation.VALIDATE_SUBOBJECT_CODE)) {
                // sub object code is optional
                if (StringUtils.isNotEmpty(account.getSubObjectCode()) &&
                    !isSubObjectCodeValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode())) {

                    LOG.error("Invalid SubObject Code in Agency Data record. tripId: "+ agencyData.getTripId() + " chart code: " + account.getTripChartCode() + " object code: " + account.getObjectCode() + " subobject code: " + account.getSubObjectCode());

                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBOBJECT);
                    error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUB_OBJECT_CODE, account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode());
                    errorMessages.add(error);
                }
            }
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#isDuplicate(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateDuplicateData(AgencyStagingData agencyData) {

        // Verify that this isn't a duplicate entry based on the following:
        // Trip ID, Invoice Number, Traveler Type Id, Agency Name, Transaction Date, Transaction Amount

        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        Map<String, Object> fieldValues = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(agencyData.getTripId())) {
            fieldValues.put(TemPropertyConstants.TRIP_ID, agencyData.getTripId());
        }
        if (StringUtils.isNotEmpty(agencyData.getCreditCardOrAgencyCode())) {
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, agencyData.getCreditCardOrAgencyCode());
        }
        if (ObjectUtils.isNotNull(agencyData.getTransactionPostingDate())) {
            fieldValues.put(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyData.getTransactionPostingDate());
        }
        if (ObjectUtils.isNotNull(agencyData.getTripExpenseAmount())) {
            fieldValues.put(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyData.getTripExpenseAmount());
        }
        if (StringUtils.isNotEmpty(agencyData.getAirTicketNumber())) {
            fieldValues.put(TemPropertyConstants.AIR_TICKET_NUMBER, agencyData.getAirTicketNumber());
        }
        if (StringUtils.isNotEmpty(agencyData.getLodgingItineraryNumber())) {
            fieldValues.put(TemPropertyConstants.LODGING_ITINERARY_NUMBER, agencyData.getLodgingItineraryNumber());
        }
        if (StringUtils.isNotEmpty(agencyData.getRentalCarItineraryNumber())) {
            fieldValues.put(TemPropertyConstants.RENTAL_CAR_ITINERARY_NUMBER, agencyData.getRentalCarItineraryNumber());
        }

        if (fieldValues.isEmpty()) {
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS_GENERIC));
        }
        else {

            List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
            if (ObjectUtils.isNotNull(agencyDataList) && !agencyDataList.isEmpty()) {

                boolean isDuplicate = false;
                String errorMessage = "Found a duplicate entry for Agency Staging Data: Duplicate Ids ";

                for(AgencyStagingData duplicate : agencyDataList) {
                    Integer duplicateId = duplicate.getId();
                    if (ObjectUtils.isNotNull(agencyData.getId()) && (agencyData.getId().intValue() != duplicateId.intValue())) {
                        errorMessage += duplicate.getId() +" ";
                        isDuplicate = true;
                    }
                }
                if (isDuplicate) {
                    LOG.error(errorMessage);

                    String itineraryData = "";
                    if (StringUtils.isNotEmpty(agencyData.getAirTicketNumber())) {
                        itineraryData = "AIR-"+ agencyData.getAirTicketNumber();
                    }
                    else if (StringUtils.isNotEmpty(agencyData.getLodgingItineraryNumber())) {
                        itineraryData = "LODGING-"+ agencyData.getLodgingItineraryNumber();
                    }
                    else if (StringUtils.isNotEmpty(agencyData.getRentalCarItineraryNumber())) {
                        itineraryData = "RENTAL CAR-"+ agencyData.getRentalCarItineraryNumber();
                    }

                    ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRIP_DUPLICATE_RECORD, agencyData.getTripId(), agencyData.getAgency(),
                            agencyData.getTransactionPostingDate().toString(), agencyData.getTripExpenseAmount().toString());
                    errorMessages.add(error);
                }
            }
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTripService#reconciliateExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @SuppressWarnings("null")
    @Override
    public boolean reconciliateExpense(AgencyStagingData agencyData, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        LOG.info("Reconciling expense for agency data: "+ agencyData.getId()+ " tripId: "+ agencyData.getTripId());

        //only reconcile the record if it is active
        if (agencyData.isActive()) {

            if (AgencyStagingDataErrorCodes.AGENCY_NO_ERROR.equals(agencyData.getErrorCode())) {

                String expenseType = agencyData.getExpenseType();
                TemConstants.ExpenseTypeMetaCategory expenseTypeCategory = agencyData.getExpenseTypeCategory();

                // This is the "match process" - see if there's credit card data that matches the agency data
                CreditCardStagingData ccData = null;
                if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.AIRFARE)) {
                    // see if there's a CC that matches ticket number, service fee number, amount
                    ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getAirTicketNumber(), agencyData.getAirServiceFeeNumber());
                }
                else if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.LODGING)) {
                    // see if there's a CC that matches lodging itinerary number and amount
                    ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getLodgingItineraryNumber());
                }
                else if (StringUtils.equalsIgnoreCase(expenseType, ExpenseTypes.RENTAL_CAR)) {
                    // see if there's a CC that matches rental car itinerary number and amount
                    ccData = travelExpenseService.findImportedCreditCardExpense(agencyData.getTripExpenseAmount(), agencyData.getRentalCarItineraryNumber());
                }

                if (ObjectUtils.isNotNull(ccData)) {
                    LOG.debug("Found a match for Agency: "+ agencyData.getId()+ " Credit Card: "+ ccData.getId()+ " tripId: "+ agencyData.getTripId());
                    ExpenseTypeObjectCode travelExpenseType = getTravelExpenseType(expenseTypeCategory, agencyData.getTripId());
                    if (travelExpenseType != null) {
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

                        String creditObjectCode = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_ALL.class, AgencyMatchProcessParameter.TRAVEL_CREDIT_CARD_CLEARING_OBJECT_CODE);

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
                                LOG.info("Processing Service Fee GLPE for agency: "+ agencyData.getId()+ " tripId: "+ agencyData.getTripId());

                                final boolean generateOffset = true;
                                List<GeneralLedgerPendingEntry> pendingEntries = importedExpensePendingEntryService.buildDebitPendingEntry(agencyData, info, sequenceHelper, objectCode, currentFeeAmount, generateOffset);
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
                            ccData.setMoveToHistoryIndicator(true);
                            businessObjectService.save(ccData);
                            agencyData.setMoveToHistoryIndicator(true);
                            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);
                        }
                        else {
                            LOG.error("An errror occured while creating GLPEs for agency: "+ agencyData.getId()+ " tripId"+ agencyData.getTripId()+ " Not creating historical expense for this agency record.");
                            return false;
                        }
                    } else {
                        LOG.info("No expense type object code could be found for "+agencyData.getId()+" tripId: "+agencyData.getTripId());
                    }
                }
                else {
                    LOG.info("No match found for agency data: "+ agencyData.getId()+ " tripId:"+ agencyData.getTripId());
                }
            }
            else {
                LOG.info("Agency Data: "+ agencyData.getId() +"; expected errorCode="+ AgencyStagingDataErrorCodes.AGENCY_NO_ERROR +", received errorCode="+ agencyData.getErrorCode() +". Will not attempt to reconcile expense.");
            }
        }
        else {
            LOG.info("Agency Data: "+ agencyData.getId() +", is not active. Will not try to reconcile");
        }

        LOG.info("Finished reconciling expense for agency data: "+ agencyData.getId()+ " tripId: "+ agencyData.getTripId());
        return true;
    }

    /**
     *
     * This method gets the {@link TemTravelExpenseTypeCode} associated with the expense type and travel document
     * @param expenseTypeParamCode
     * @param travelDocumentIdentifier
     * @return
     */
    protected ExpenseTypeObjectCode getTravelExpenseType(TemConstants.ExpenseTypeMetaCategory expenseCategory, String travelDocumentIdentifier) {
        // get the default expense type for category
        final ExpenseType expenseType = getTravelExpenseService().getDefaultExpenseTypeForCategory(expenseCategory);
        // Is there a document associated with this trip?  If so, let's grab the trip type and traveler type from that
        // Note the tripId has already been validated at this point, so the TA should be there (if the imported expense is for a TA)
        TravelDocument travelDoc = getTravelDocumentService().getTravelDocument(travelDocumentIdentifier);
        if (ObjectUtils.isNotNull(travelDoc)) {
            return travelExpenseService.getExpenseType(expenseType.getCode(), travelDoc.getDocumentTypeName(), travelDoc.getTripTypeCode(), travelDoc.getTraveler().getTravelerTypeCode());
        }
        LOG.error("Unable to retrieve TemTravelExpenseTypeCode");
        return null; // we shouldn't ever get here if the trip id was validated, but hey.  You never know.
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

    public ImportedExpensePendingEntryService getImportedExpensePendingEntryService() {
        return importedExpensePendingEntryService;
    }

    public void setImportedExpensePendingEntryService(ImportedExpensePendingEntryService importedExpensePendingEntryService) {
        this.importedExpensePendingEntryService = importedExpensePendingEntryService;
    }

    public TravelDocumentService getTravelDocumentService() {
        return this.travelDocumentService;
    }

    public void setTravelDocumentService(TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

}
