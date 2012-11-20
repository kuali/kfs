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
import org.kuali.kfs.module.tem.TemConstants.AgencyMatchProcessParameter;
import org.kuali.kfs.module.tem.TemConstants.AgencyStagingDataErrorCodes;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService;
import org.kuali.kfs.module.tem.batch.service.ImportedExpensePendingEntryService;
import org.kuali.kfs.module.tem.businessobject.AgencyStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TripAccountingInformation;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ErrorMessage;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

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

    protected List<ErrorMessage> errorMessages = new ArrayList<ErrorMessage>();
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#areMandatoryFieldsPresent(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public boolean areMandatoryFieldsPresent(final AgencyStagingData agencyData) {
        
        boolean requiredFieldsValid = true;
        
        if (StringUtils.isEmpty(agencyData.getTravelerId())) {
            LOG.error("Agency Data Missing Required Field: travelerId.");
            requiredFieldsValid = false;
        }
        if (isAmountEmpty(agencyData.getTripExpenseAmount())) {
            LOG.error("Agency Data Missing Required Field: tripExpenseAmount. travelerId: "+ agencyData.getTravelerId());
            requiredFieldsValid = false;
        }
        if (StringUtils.isEmpty(agencyData.getTripInvoiceNumber())) {
            LOG.error("Agency Data Missing Required Field: tripInvoiceNumber. travelerId: "+ agencyData.getTravelerId());
            requiredFieldsValid = false;
        }
        if (ObjectUtils.isNull(agencyData.getTransactionPostingDate())) {
            LOG.error("Agency Data Missing Required Field: transactionPostingDate. travelerId: "+ agencyData.getTravelerId());
            requiredFieldsValid = false;
        }
        if (StringUtils.isEmpty(agencyData.getCreditCardOrAgencyCode())) {
            LOG.error("Agency Data Missing Required Field: creditCardOrAgencyCode. travelerId: "+ agencyData.getTravelerId());
            requiredFieldsValid = false;
        }
        if (StringUtils.isEmpty(agencyData.getAirTicketNumber())) {
            LOG.error("Agency Data Missing Required Field: airTicketNumber. travelerId: "+ agencyData.getTravelerId());
            requiredFieldsValid = false;
        }
        
        if (!requiredFieldsValid) {
            LOG.error("Missing one or more required fields.");
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_NO_MANDATORY_FIELDS));
        }
        
        return requiredFieldsValid;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateAgencyData(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public AgencyStagingData validateAgencyData(final AgencyStagingData agencyData) {
        
        LOG.info("Validating agency data.");

        errorMessages.clear();
        
        // Perform a duplicate check first. 
        if (isDuplicate(agencyData)) {
            LOG.error("Duplicate Agency Data record");
            return null;
        }
        
        // At this point the agencyData record is considered good. Any validation errors will be 
        // added to the errorCode in agencyData.
        
        agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_NO_ERROR);
        
        final TEMProfile profile = validateTraveler(agencyData);
        if (ObjectUtils.isNotNull(profile)) {
            
            // Found a TEM Profile for the traveler, now check the accounting info
            validateAccountingInfo(profile, agencyData);
        }
        
        if (!isCreditCardAgencyValid(agencyData)) {
            errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_CREDIT_CARD_DATA_INVALID_CCA));
        }
        
        LOG.info("Finished validating agency data.");
        agencyData.setProcessingTimestamp(dateTimeService.getCurrentTimestamp());
        return agencyData;
    }
   
    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateTraveler(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public TEMProfile validateTraveler(final AgencyStagingData agencyData) {
        final Map<String,String> criteria = new HashMap<String,String>(1);
        TEMProfile profile = null;        
        
        // First try and get a profile by employee id
        criteria.put(TemPropertyConstants.EMPLOYEE_ID, agencyData.getTravelerId());
        profile = temProfileService.findTemProfile(criteria);
        if (ObjectUtils.isNotNull(profile)) {
            LOG.info("Traveler is an Employee: " + agencyData.getTravelerId());
            agencyData.setTemProfileId(profile.getProfileId());
            return profile;
        }
        
        // Couldn't find a profile matching the employee id, see if its a customer number
        criteria.clear();
        criteria.put(TemPropertyConstants.CUSTOMER_NUMBER, agencyData.getTravelerId());
        profile = temProfileService.findTemProfile(criteria);
        if (ObjectUtils.isNotNull(profile)) {
            LOG.info("Traveler is a Customer: " + agencyData.getTravelerId());
            agencyData.setTemProfileId(profile.getProfileId());
            return profile;
        }

        LOG.error("Invalid Traveler in Agency Data record. travelerId: "+ agencyData.getTravelerId());
        errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_TRAVELER));
        setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_TRAVELER);
        return null;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#validateAccountingInfo(org.kuali.kfs.module.tem.businessobject.TEMProfile, org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public AgencyStagingData validateAccountingInfo(final TEMProfile profile, final AgencyStagingData agencyData) {
        final TripAccountingInformation accountingInfo = new TripAccountingInformation();
        accountingInfo.setTripChartCode(profile.getDefaultChartCode());
        accountingInfo.setTripAccountNumber(profile.getDefaultAccount());
        accountingInfo.setTripSubAccountNumber(profile.getDefaultSubAccount());
        accountingInfo.setProjectCode(profile.getDefaultProjectCode());
        accountingInfo.setObjectCode(getObjectCode(agencyData));
        // there are currently no requirements to get sub-object parameter
        
        agencyData.addTripAccountingInformation(accountingInfo);
        
        final List<TripAccountingInformation> accountingInfos = agencyData.getTripAccountingInformation();
        for (final TripAccountingInformation account : accountingInfos) {

            account.setTripChartCode(profile.getDefaultChartCode());
        
            if (!isAccountNumberValid(account.getTripChartCode(), account.getTripAccountNumber())) {
                LOG.error("Invalid Account in Tem Profile or Agency Data record. travelerId: " + agencyData.getTravelerId()+ " temProfileId: " + agencyData.getTemProfileId() + " chart code: " + account.getTripChartCode() + " account: " + account.getTripAccountNumber());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_ACCOUNT_NUM));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_ACCOUNT);
            }
            
            // subaccount is optional
            if (StringUtils.isNotEmpty(account.getTripSubAccountNumber()) &&
                !isSubAccountNumberValid(account.getTripChartCode(),account.getTripAccountNumber(), account.getTripSubAccountNumber())) {
                LOG.error("Invalid SubAccount in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " chart code: "+ account.getTripChartCode()+ " account: "+ account.getTripAccountNumber()+ " subaccount: "+ account.getTripSubAccountNumber());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUBACCOUNT));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBACCOUNT);
            }
            
            // project code is optional
            if (StringUtils.isNotEmpty(account.getProjectCode()) &&
                !isProjectCodeValid(account.getProjectCode())) {
                LOG.error("Invalid Project in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " project code: "+ account.getProjectCode());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_PROJECT_CODE));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_PROJECT);
            }
    
            // object code is optional
            if (StringUtils.isNotEmpty(account.getObjectCode()) &&
                !isObjectCodeValid(account.getTripChartCode(), account.getObjectCode())) {                
                LOG.error("Invalid Object Code in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " chart code: "+ account.getTripChartCode()+ " object code: "+ account.getObjectCode());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_OBJECT_CODE));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_OBJECT);
            }
            
            // sub object code is optional
            if (StringUtils.isNotEmpty(account.getSubObjectCode()) && 
                !isSubObjectCodeValid(account.getTripChartCode(), account.getTripAccountNumber(), account.getSubObjectCode(), account.getSubObjectCode())) {
                LOG.error("Invalid SubObject Code in Tem Profile or Agency Data record. travelerId: "+ agencyData.getTravelerId()+ " temProfileId: "+ agencyData.getTemProfileId()+ " chart code: "+ account.getTripChartCode()+ " object code: "+ account.getObjectCode()+ " subobject code: "+ account.getSubObjectCode());
                errorMessages.add(new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_INVALID_SUB_OBJECT_CODE));
                setErrorCode(agencyData, AgencyStagingDataErrorCodes.AGENCY_INVALID_SUBOBJECT);
            }
        }
        
        return agencyData;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#isDuplicate(org.kuali.kfs.module.tem.businessobject.AgencyStagingData)
     */
    @Override
    public boolean isDuplicate(final AgencyStagingData agencyData) {
        
        // Verify that this isn't a duplicate entry based on the following: 
        // Traveler ID, Ticket Number, Agency Name, Transaction Date, Transaction Amount, Invoice Number
        
        final Map<String, Object> fieldValues = new HashMap<String, Object>();
        
        if (StringUtils.isNotEmpty(agencyData.getTravelerId())) {
            fieldValues.put(TemPropertyConstants.TRAVELER_ID, agencyData.getTravelerId());
        }
        if (StringUtils.isNotEmpty(agencyData.getAirTicketNumber())) {
            fieldValues.put(TemPropertyConstants.AIR_TICKET_NUMBER, agencyData.getAirTicketNumber());
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
                
        List<AgencyStagingData> agencyDataList = (List<AgencyStagingData>) businessObjectService.findMatching(AgencyStagingData.class, fieldValues);
        if (ObjectUtils.isNull(agencyDataList) || agencyDataList.size() == 0) {
            return false;
        }
        LOG.error("Found a duplicate entry for agencyData with travelerId: "+ agencyData.getTravelerId()+ " matching agency id: "+ agencyDataList.get(0).getId());
        
        ErrorMessage error = new ErrorMessage(TemKeyConstants.MESSAGE_AGENCY_DATA_TRAVELER_DUPLICATE_RECORD, 
                agencyData.getTravelerId(), agencyData.getAirTicketNumber(), agencyData.getCreditCardOrAgencyCode(), 
                agencyData.getTransactionPostingDate().toString(), agencyData.getTripExpenseAmount().toString(), agencyData.getTripInvoiceNumber());
        errorMessages.add(error);
        return true;
    }

    /**
     * 
     * @see org.kuali.kfs.module.tem.batch.service.ExpenseImportByTravelerService#distributeExpense(org.kuali.kfs.module.tem.businessobject.AgencyStagingData+ org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean distributeExpense(final AgencyStagingData agencyData, final GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {

        LOG.info("Distributing expense for agency data: "+ agencyData.getId());

        final HistoricalTravelExpense expense = travelExpenseService.createHistoricalTravelExpense(agencyData);
        
        final List<GeneralLedgerPendingEntry> entries = new ArrayList<GeneralLedgerPendingEntry>();
        
        final List<TripAccountingInformation> accountingInfo = agencyData.getTripAccountingInformation();
        
        // Need to split up the amounts if there are multiple accounts
        KualiDecimal remainingAmount = agencyData.getTripExpenseAmount();
        KualiDecimal numAccounts = new KualiDecimal(accountingInfo.size());
        KualiDecimal currentAmount = agencyData.getTripExpenseAmount().divide(numAccounts);

        final String creditObjectCode = getParameter(AgencyMatchProcessParameter.AP_CLEARING_CTS_PAYMENT_OBJECT_CODE, AgencyMatchProcessParameter.AGENCY_MATCH_DTL_TYPE);

        boolean allGlpesCreated = true;

        for (int i = 0; i < accountingInfo.size(); i++) {
            TripAccountingInformation info = accountingInfo.get(i);
            
            // If its the last account, use the remainingAmount to resolve rounding
            if (i < accountingInfo.size() - 1) {
                remainingAmount = remainingAmount.subtract(currentAmount);
            }
            else {
                currentAmount = remainingAmount;
            }

            // set the amount on the accounting info for by documents pulling in imported expenses
            info.setAmount(currentAmount);
            
            final boolean generateOffset = true;
            List<GeneralLedgerPendingEntry> pendingEntries = importedExpensePendingEntryService.buildDebitPendingEntry(agencyData, info, sequenceHelper, info.getObjectCode(), currentAmount, generateOffset);
            allGlpesCreated = importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, false, generateOffset);
            
            pendingEntries = importedExpensePendingEntryService.buildCreditPendingEntry(agencyData, info, sequenceHelper, creditObjectCode, currentAmount, generateOffset);
            allGlpesCreated &= importedExpensePendingEntryService.checkAndAddPendingEntriesToList(pendingEntries, entries, agencyData, true, generateOffset);
       }
        
        if (entries.size() > 0 && allGlpesCreated) {
            businessObjectService.save(expense);
            businessObjectService.save(entries);
            agencyData.setMoveToHistoryIndicator(true);
            agencyData.setErrorCode(AgencyStagingDataErrorCodes.AGENCY_MOVED_TO_HISTORICAL);
        }
        else {
            LOG.error("An errror occured while creating GLPEs for agency: "+ agencyData.getId()+ " travelerId"+ agencyData.getTravelerId()+ " Not creating historical expense for this agency record.");
            return false;
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

    @Override
    public List<ErrorMessage> getErrorMessages() {
        return errorMessages;
    }

    @Override
    public void setErrorMessages(List<ErrorMessage> errorMessages) {
        this.errorMessages = errorMessages;
    }

    public void setImportedExpensePendingEntryService(ImportedExpensePendingEntryService importedExpensePendingEntryService) {
        this.importedExpensePendingEntryService = importedExpensePendingEntryService;
    }

}
