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
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.ProjectCodeService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.coa.service.SubObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
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

        if (!isCreditCardAgencyValid(agencyData)){
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_CREDIT_CARD_DATA_INVALID_CCA));
        }

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

        for (final TripAccountingInformation account : accountingInfos) {

            if (!isAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber())) {
                LOG.error("Invalid Account in Tem Profile or Agency Data record. travelerId: " + agencyData.getTravelerId()+ " temProfileId: " + agencyData.getTemProfileId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM, account.getTripChartCode(), account.getTripAccountNumber()));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
            }

            // sub account is optional
            if (StringUtils.isNotEmpty(account.getTripSubAccountNumber()) &&
                !isSubAccountNumberValid(account.getTripChartCode(),account.getTripAccountNumber(), account.getTripSubAccountNumber())) {
                LOG.error("Invalid SubAccount in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " chart code: "+ account.getTripChartCode()+ " account: "+ account.getTripAccountNumber()+ " subaccount: "+ account.getTripSubAccountNumber());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT, account.getTripSubAccountNumber()));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
            }

            // project code is optional
            if (StringUtils.isNotEmpty(account.getProjectCode()) &&
                !isProjectCodeValid(account.getProjectCode())) {
                LOG.error("Invalid Project in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " project code: "+ account.getProjectCode());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE, account.getProjectCode()));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
            }

            // object code is optional
            if (StringUtils.isNotEmpty(account.getObjectCode()) &&
                !isObjectCodeValid(account.getTripChartCode(), account.getObjectCode())) {
                LOG.error("Invalid Object Code in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " chart code: "+ account.getTripChartCode()+ " object code: "+ account.getObjectCode());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_OBJECT_CODE, account.getTripChartCode(), account.getObjectCode()));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_OBJECT);
            }

            // sub object code is optional
            if (StringUtils.isNotEmpty(account.getSubObjectCode()) &&
                !isSubObjectCodeValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode())) {
                LOG.error("Invalid SubObject Code in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " chart code: "+ account.getTripChartCode()+ " object code: "+ account.getObjectCode()+ " subobject code: "+ account.getSubObjectCode());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUB_OBJECT_CODE, account.getTripChartCode(), account.getTripAccountNumber(), account.getObjectCode(), account.getSubObjectCode()));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBOBJECT);
            }
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateDuplicateData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public List<ErrorMessage> validateDuplicateData(final AgencyStagingData agencyData) {

        List<ErrorMessage> errorMessages = validateMandatoryFieldsPresent(agencyData);
        if (!errorMessages.isEmpty()) {
            return errorMessages;
        }

        // Verify that this isn't a duplicate entry based on the following:
        // Traveler ID, Ticket Number, Agency Name, Transaction Date, Transaction Amount, Invoice Number

        final Map<String, Object> fieldValues = new HashMap<String, Object>();

        if (StringUtils.isNotEmpty(agencyData.getTravelerId())) {
            fieldValues.put(TemPropertyConstants.TRAVELER_ID, agencyData.getTravelerId());
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
        if (StringUtils.isNotEmpty(agencyData.getCreditCardOrAgencyCode())) {
            fieldValues.put(TemPropertyConstants.CREDIT_CARD_AGENCY_CODE, agencyData.getCreditCardOrAgencyCode());
        }
        if (ObjectUtils.isNotNull(agencyData.getTransactionPostingDate())) {
            fieldValues.put(TemPropertyConstants.TRANSACTION_POSTING_DATE, agencyData.getTransactionPostingDate());
        }
        if (ObjectUtils.isNotNull(agencyData.getTripExpenseAmount())) {
            fieldValues.put(TemPropertyConstants.TRIP_EXPENSE_AMOUNT, agencyData.getTripExpenseAmount());
        }
        if (StringUtils.isNotEmpty(agencyData.getTripInvoiceNumber())) {
            fieldValues.put(TemPropertyConstants.TRIP_INVOICE_NUMBER, agencyData.getTripInvoiceNumber());
        }

        if (fieldValues.isEmpty()) {
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS_GENERIC));
        }
        else {

            List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
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

                    ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRAVELER_DUPLICATE_RECORD,
                        agencyData.getTravelerId(), itineraryData, agencyData.getCreditCardOrAgencyCode(),
                        agencyData.getTransactionPostingDate().toString(), agencyData.getTripExpenseAmount().toString(), agencyData.getTripInvoiceNumber());
                    errorMessages.add(error);

                }
            }
        }

        return errorMessages;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#distributeExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData+ org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean distributeExpense(final AgencyStagingData agencyData, final GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        LOG.info("Distributing expense for agency data: "+ agencyData.getId());

        if (agencyData.isActive()) {

            if (AgencyStagingDataErrorCodes.AGENCY_NO_ERROR.equals(agencyData.getErrorCode())) {

                final HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(agencyData);

                businessObjectService.save(expense);
                agencyData.setMoveToHistoryIndicator(true);
                agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);
            }
            else {
                LOG.info("Agency Data: "+ agencyData.getId() +"; expected errorCode="+ AgencyStagingDataErrorCodes.AGENCY_NO_ERROR +", received errorCode="+ agencyData.getErrorCode() +". Will not attempt to distribute expense.");
            }
        }
        else {
            LOG.info("Agency Data: "+ agencyData.getId() +", is not active. Will not distribute.");
        }

        LOG.info("Finished distributing expense for agency data: "+ agencyData.getId());
        return true;
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
