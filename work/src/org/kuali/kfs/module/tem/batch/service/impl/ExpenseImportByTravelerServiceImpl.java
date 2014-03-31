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
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataValidation;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.AgencyDataImportStep;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ObjectUtils;

public class ExpenseImportByTravelerServiceImpl extends ExpenseImportServiceBase implements ExpenseImportByTravelerService {

    public static Logger LOG = Logger.getLogger(ExpenseImportByTravelerServiceImpl.class);

    private TemProfileService temProfileService;
    private AccountService accountService;
    private SubAccountService subAccountService;
    private ProjectCodeService projectCodeService;
    private ParameterService parameterService;
    private ObjectCodeService objectCodeService;
    private SubObjectCodeService subObjectCodeService;
    private BusinessObjectService businessObjectService;
    private ImportedExpensePendingEntryService importedExpensePendingEntryService;
    private DateTimeService dateTimeService;
    private TravelExpenseService travelExpenseService;
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private UniversityDateService universityDateService;

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#areMandatoryFieldsPresent(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateMandatoryFieldsPresent(final AgencyStagingData agencyData) {

        boolean requiredFieldsValid = true;

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (StringUtils.isEmpty(agencyData.getTravelerId())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.TRAVELER_DATA);
            errorMessages.add(error);
        }
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
        if (StringUtils.isEmpty(agencyData.getCreditCardOrAgencyCode())) {
            ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS, TemPropertyConstants.TravelAgencyAuditReportFields.CREDIT_CARD_OR_AGENCY_CODE);
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
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateAgencyData(final AgencyStagingData agencyData) {

        LOG.info("Validating agency data.");

        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_REQUIRED_FIELDS);
            return errorMessages;
        }

        // Perform a duplicate check first.
        errorMessages = validateDuplicateData(agencyData);
        if (!errorMessages.isEmpty()) {
            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_DUPLICATE_DATA);
            return errorMessages;
        }

        // At this point the agencyData record is considered good. Any validation errors will be
        // added to the errorCode in agencyData.

        agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);

        errorMessages = validateTraveler(agencyData);

        errorMessages.addAll(validateAccountingInfo(agencyData));

        errorMessages.addAll(validateCreditCardAgency(agencyData));

        LOG.info("Finished validating agency data.");
        agencyData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
        if (ObjectUtils.isNull(agencyData.getCreationTimestamp())) {
            agencyData.setCreationTimestamp(dateTimeService.getCurrentTimestamp());
        }
        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateTraveler(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateTraveler(final AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
        TemProfile profile = getTraveler(agencyData);

        if (ObjectUtils.isNotNull(profile)) {
            agencyData.setTemProfileId(profile.getProfileId());
        }
        else {
            LOG.error("Invalid Traveler in Agency Data record. travelerId: "+ agencyData.getTravelerId());
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRAVELER, agencyData.getTravelerId()));
            setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_TRAVELER);
        }

        return errorMessages;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#getTraveler(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public TemProfile getTraveler(final AgencyStagingData agencyData) {

        // First try and get a profile by employee id
        TemProfile profile = temProfileService.findTemProfileByEmployeeId(agencyData.getTravelerId());
        if (ObjectUtils.isNotNull(profile)) {
            LOG.info("Traveler is an Employee: " + agencyData.getTravelerId());
            agencyData.setTemProfileId(profile.getProfileId());
            return profile;
        }

        // Couldn't find a profile matching the employee id, see if its a customer number
        profile = temProfileService.findTemProfileByCustomerNumber(agencyData.getTravelerId());
        if (ObjectUtils.isNotNull(profile)) {
            LOG.info("Traveler is a Customer: " + agencyData.getTravelerId());
            agencyData.setTemProfileId(profile.getProfileId());
            return profile;
        }

        LOG.error("Invalid Traveler in Agency Data record. travelerId: "+ agencyData.getTravelerId());
        return null;
    }


    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TemProfile, org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateAccountingInfo(final AgencyStagingData agencyData) {
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        final List<TripAccountingInformation> accountingInfos = agencyData.getTripAccountingInformation();

        // Get ACCOUNTING_LINE_VALIDATION parameter to determine which fields to validate
        Collection<String> validationParameters = getParameterService().getParameterValuesAsString(AgencyDataImportStep.class, TravelParameters.ACCOUNTING_LINE_VALIDATION);
        //don't need to validate any accounting information
        if (ObjectUtils.isNull(validationParameters)) {
            return errorMessages;
        }

        for (final TripAccountingInformation account : accountingInfos) {

            errorMessages.addAll(validateAccountingInfoLine(agencyData, account, validationParameters).values());
        }

        return errorMessages;
    }

    /**
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validatAccountingInfoLine(org.kuali.kfs.module.tem.businessobject.TripAccountingInformation)
     */
    @Override
    public Map<String,ErrorMessage> validateAccountingInfoLine(TripAccountingInformation accountingLine) {
        return validateAccountingInfoLine(null, accountingLine, null);
    }

    protected Map<String,ErrorMessage> validateAccountingInfoLine(AgencyStagingData agencyData, TripAccountingInformation accountingLine, Collection<String> validationParameters) {
        Map<String,ErrorMessage> errorMap = new HashMap<String,ErrorMessage>();

        if (ObjectUtils.isNull(validationParameters)) {
            // Get ACCOUNTING_LINE_VALIDATION parameter to determine which fields to validate
            validationParameters = getParameterService().getParameterValuesAsString(AgencyDataImportStep.class, TravelParameters.ACCOUNTING_LINE_VALIDATION);
            //don't need to validate any accounting information
            if (ObjectUtils.isNull(validationParameters)) {
                return errorMap;
            }
        }

        boolean setAgencyDataErrorCode = false;

        Integer profileId = null;
        String travelerId = "";
        if (ObjectUtils.isNotNull(agencyData)) {
            setAgencyDataErrorCode = true;
            travelerId = agencyData.getTravelerId();
            profileId = agencyData.getTemProfileId();
        }

        if (validationParameters.contains(AgencyStagingDataValidation.VALIDATE_ACCOUNT)) {
            if (!isAccountNumberValid(accountingLine.getTripChartCode(), accountingLine.getTripAccountNumber())) {
                if (setAgencyDataErrorCode) {
                    LOG.error("Invalid Account in Tem Profile or Agency Data record. travelerId: " + travelerId +" temProfileId: " + profileId +" chart code: " + accountingLine.getTripChartCode() + " account: " + accountingLine.getTripAccountNumber());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
                }
                errorMap.put(TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_ACCOUNT_NUMBER, new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM, accountingLine.getTripChartCode(), accountingLine.getTripAccountNumber()));
            }
        }

        if (validationParameters.contains(AgencyStagingDataValidation.VALIDATE_SUBACCOUNT)) {
            // sub account is optional
            if (StringUtils.isNotEmpty(accountingLine.getTripSubAccountNumber()) &&
                !isSubAccountNumberValid(accountingLine.getTripChartCode(),accountingLine.getTripAccountNumber(), accountingLine.getTripSubAccountNumber())) {
                if (setAgencyDataErrorCode) {
                    LOG.error("Invalid SubAccount in Tem Profile or Agency Data record. travelerId: "+ travelerId +" temProfileId: "+ profileId +" chart code: "+ accountingLine.getTripChartCode()+ " account: "+ accountingLine.getTripAccountNumber()+ " subaccount: "+ accountingLine.getTripSubAccountNumber());
                    setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
                }
                errorMap.put(TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_SUBACCOUNT_NUMBER, new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT, accountingLine.getTripSubAccountNumber()));
            }
        }

        // project code is optional
        if (StringUtils.isNotEmpty(accountingLine.getProjectCode()) &&
            !isProjectCodeValid(accountingLine.getProjectCode())) {
            if (setAgencyDataErrorCode) {
                LOG.error("Invalid Project in Tem Profile or Agency Data record. travelerId: "+ travelerId +" temProfileId: "+ profileId +" project code: "+ accountingLine.getProjectCode());
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
            }
            errorMap.put(TemPropertyConstants.TravelAgencyAuditReportFields.TRIP_PROJECT_CODE, new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE, accountingLine.getProjectCode()));
        }

        return errorMap;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateDuplicateData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateDuplicateData(final AgencyStagingData agencyData) {

        //make sure all the mandatory fields are present before checking for duplicates
        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        List<AgencyStagingData> agencyDataList = checkDuplicateData(agencyData);
        if (ObjectUtils.isNotNull(agencyDataList) || agencyDataList.size() != 0) {

            boolean isDuplicate = false;
            String errorMessage = "Found a duplicate entry for Agency Staging Data: Duplicate Ids ";

            for(AgencyStagingData duplicate : agencyDataList) {
                Integer duplicateId = duplicate.getId();
                if (ObjectUtils.isNotNull(agencyData.getId()) && (duplicateId.intValue() != agencyData.getId().intValue())) {
                    errorMessage += duplicate.getId() +" ";
                    isDuplicate = true;
                }
            }
            if (isDuplicate) {
                LOG.error(errorMessage);

                ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRAVELER_DUPLICATE_RECORD,
                    agencyData.getTravelerId(), agencyData.getItineraryDataString(), agencyData.getCreditCardOrAgencyCode(),
                    agencyData.getTransactionPostingDate().toString(), agencyData.getTripExpenseAmount().toString(), agencyData.getTripInvoiceNumber());
                errorMessages.add(error);

            }
        }

        return errorMessages;
    }

    protected List<AgencyStagingData> checkDuplicateData(AgencyStagingData agencyStagingData) {
        // Verify that this isn't a duplicate entry based on the following:
        // Traveler ID, Ticket Number (or Lodging or Rental Car Itinerary Number), Agency Name, Transaction Date, Transaction Amount, Invoice Number

        final Map<String, Object> fieldValues = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(agencyStagingData.getTravelerId())) {
            fieldValues.put(TemPropertyConstants.TRAVELER_ID, agencyStagingData.getTravelerId());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getAirTicketNumber())) {
            fieldValues.put(TemPropertyConstants.AIR_TICKET_NUMBER, agencyStagingData.getAirTicketNumber());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getLodgingItineraryNumber())) {
            fieldValues.put(TemPropertyConstants.LODGING_ITINERARY_NUMBER, agencyStagingData.getLodgingItineraryNumber());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getRentalCarItineraryNumber())) {
            fieldValues.put(TemPropertyConstants.RENTAL_CAR_ITINERARY_NUMBER, agencyStagingData.getRentalCarItineraryNumber());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getCreditCardOrAgencyCode())) {
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, agencyStagingData.getCreditCardOrAgencyCode());
        }
        if (ObjectUtils.isNotNull(agencyStagingData.getTransactionPostingDate())) {
            fieldValues.put(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyStagingData.getTransactionPostingDate());
        }
        if (ObjectUtils.isNotNull(agencyStagingData.getTripExpenseAmount())) {
            fieldValues.put(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyStagingData.getTripExpenseAmount());
        }
        if (StringUtils.isNotEmpty(agencyStagingData.getTripInvoiceNumber())) {
            fieldValues.put(TemPropertyConstants.TRIP_INVOICE_NUMBER, agencyStagingData.getTripInvoiceNumber());
        }

        List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
        return agencyDataList;
    }

    @Override
    public List<ErrorMessage> validateCreditCardAgency(AgencyStagingData agencyData) {
        List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();

        if (!isCreditCardAgencyValid(agencyData)) {
            //setErrorCode is already done in isCreditCardAgencyValid()
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_CREDIT_CARD_DATA_INVALID_CCA, agencyData.getCreditCardOrAgencyCode()));
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#distributeExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData+ org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public List<ErrorMessage> distributeExpense(final AgencyStagingData agencyData) {

        LOG.info("Distributing expense for agency data: "+ agencyData.getId());

        List<ErrorMessage> errors = new ArrayList<ErrorMessage>();
        if (agencyData.isActive()) {

            if (AgencyStagingDataErrorCodes.AGENCY_NO_ERROR.equals(agencyData.getErrorCode())) {

                final HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(agencyData);

                businessObjectService.save(expense);
                agencyData.setMoveToHistoryIndicator(true);
                agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);

                // nota bene: agency staging data object can NOT be saved here b/c the AgencyStagingDataMaint doc calls this method and will save it itself once processing completes.
                // batch steps which call this method need to save the business object after calling this method
            }
            else {
                LOG.info("Agency Data: "+ agencyData.getId() +"; expected errorCode="+ AgencyStagingDataErrorCodes.AGENCY_NO_ERROR +", received errorCode="+ agencyData.getErrorCode() +". Will not attempt to distribute expense.");
                errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_DISTRIBUTE_INVALID_ERROR_CODE, AgencyStagingDataErrorCodes.AGENCY_NO_ERROR, agencyData.getErrorCode()));
            }
        }
        else {
            LOG.info("Agency Data: "+ agencyData.getId() +", is not active. Will not distribute.");
            errors.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_DISTRIBUTE_ACTIVE));
        }

        LOG.info("Finished distributing expense for agency data: "+ agencyData.getId() +". Agency data "+ (errors.isEmpty() ? "was":"was not") +" distributed.");
        return errors;
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
     * Gets the accountService attribute.
     * @return Returns the accountService.
     */
    @Override
    public AccountService getAccountService() {
        return accountService;
    }

    /**
     * Sets the accountService attribute value.
     * @param accountService The accountService to set.
     */
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Gets the subAccountService attribute.
     * @return Returns the subAccountService.
     */
    @Override
    public SubAccountService getSubAccountService() {
        return subAccountService;
    }

    /**
     * Sets the subAccountService attribute value.
     * @param subAccountService The subAccountService to set.
     */
    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    /**
     * Gets the projectCodeService attribute.
     * @return Returns the projectCodeService.
     */
    @Override
    public ProjectCodeService getProjectCodeService() {
        return projectCodeService;
    }

    /**
     * Sets the projectCodeService attribute value.
     * @param projectCodeService The projectCodeService to set.
     */
    public void setProjectCodeService(ProjectCodeService projectCodeService) {
        this.projectCodeService = projectCodeService;
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */
    @Override
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Gets the objectCodeService attribute.
     * @return Returns the objectCodeService.
     */
    @Override
    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    /**
     * Gets the subObjectCodeService attribute.
     * @return Returns the subObjectCodeService.
     */
    @Override
    public SubObjectCodeService getSubObjectCodeService() {
        return subObjectCodeService;
    }

    /**
     * Sets the subObjectCodeService attribute value.
     * @param subObjectCodeService The subObjectCodeService to set.
     */
    public void setSubObjectCodeService(SubObjectCodeService subObjectCodeService) {
        this.subObjectCodeService = subObjectCodeService;
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

    /**
     * Gets the generalLedgerPendingEntryService attribute.
     * @return Returns the generalLedgerPendingEntryService.
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    /**
     * Sets the generalLedgerPendingEntryService attribute value.
     * @param generalLedgerPendingEntryService The generalLedgerPendingEntryService to set.
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    /**
     * Gets the universityDateService attribute.
     * @return Returns the universityDateService.
     */
    public UniversityDateService getUniversityDateService() {
        return universityDateService;
    }

    /**
     * Sets the universityDateService attribute value.
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setImportedExpensePendingEntryService(ImportedExpensePendingEntryService importedExpensePendingEntryService) {
        this.importedExpensePendingEntryService = importedExpensePendingEntryService;
    }

}
