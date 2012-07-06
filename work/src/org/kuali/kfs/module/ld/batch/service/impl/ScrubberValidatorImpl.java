/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.batch.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.BalanceTypeService;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.batch.LaborScrubberStep;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborObject;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.MessageBuilder;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Service implementation of ScrubberValidator.
 */
public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private ConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private AccountService accountService;
    private BalanceTypeService balanceTypService;
    private OptionsService optionsService;

    private PersistenceService persistenceService;
    private ScrubberValidator scrubberValidator;
    private PersistenceStructureService persistenceStructureService;
    private boolean continuationAccountIndicator;
    

    /**
     * @see org.kuali.module.labor.service.LaborScrubberValidator#validateTransaction(owrg.kuali.module.labor.bo.LaborOriginEntry,
     *      org.kuali.kfs.module.ld.businessobject.LaborOriginEntry, org.kuali.kfs.gl.businessobject.UniversityDate)
     */
    public List<Message> validateTransaction(OriginEntryInformation originEntry, OriginEntryInformation scrubbedEntry, UniversityDate universityRunDate, boolean laborIndicator, AccountingCycleCachingService laborAccountingCycleCachingService) {
        LOG.debug("validateTransaction() started");
        List<Message> errors = new ArrayList<Message>();
        continuationAccountIndicator = false;
        
        LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
        LaborOriginEntry laborScrubbedEntry = (LaborOriginEntry) scrubbedEntry;

        // gl scrubber validation
        errors = scrubberValidator.validateTransaction(laborOriginEntry, laborScrubbedEntry, universityRunDate, laborIndicator, laborAccountingCycleCachingService);

        refreshOriginEntryReferences(laborOriginEntry);
        refreshOriginEntryReferences(laborScrubbedEntry);

        if (StringUtils.isBlank(laborOriginEntry.getEmplid())) {
            laborScrubbedEntry.setEmplid(LaborConstants.getDashEmplId());
        }

        if (StringUtils.isBlank(laborOriginEntry.getPositionNumber())) {
            laborScrubbedEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
        }

        Message err = null;

        //this validation is duplicated.  This is in ScrubberValidatorImpl under GL
//        err = this.validateClosedPeriodCode(laborOriginEntry, laborScrubbedEntry);
//        if (err != null) {
//            errors.add(err);
//        }

        err = validatePayrollEndFiscalYear(laborOriginEntry, laborScrubbedEntry, universityRunDate, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validatePayrollEndFiscalPeriodCode(laborOriginEntry, laborScrubbedEntry, universityRunDate, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        err = validateAccount(laborOriginEntry, laborScrubbedEntry, universityRunDate, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }
        
        err = validateSubAccount(laborOriginEntry, laborScrubbedEntry, (LaborAccountingCycleCachingService) laborAccountingCycleCachingService);
        if (err != null) {
            errors.add(err);
        }

        return errors;
    }

    /**
     * This method is for refreshing References of Origin Entry
     */
    protected void refreshOriginEntryReferences(OriginEntryFull originEntry) {
        Map<String, Class> referenceClasses = persistenceStructureService.listReferenceObjectFields(originEntry.getClass());
        for (String reference : referenceClasses.keySet()) {
            if (KFSPropertyConstants.PROJECT.equals(reference)) {
                if (KFSConstants.getDashProjectCode().equals(originEntry.getProjectCode())) {
                    originEntry.setProject(null);
                }
                else {
                    persistenceService.retrieveReferenceObject(originEntry, reference);
                }
            }
            else if (KFSPropertyConstants.FINANCIAL_SUB_OBJECT.equals(reference)) {
                if (KFSConstants.getDashFinancialSubObjectCode().equals(originEntry.getFinancialSubObjectCode())) {
                    originEntry.setFinancialSubObject(null);
                }
                else {
                    persistenceService.retrieveReferenceObject(originEntry, reference);
                }
            }
            else if (KFSPropertyConstants.SUB_ACCOUNT.equals(reference)) {
                if (KFSConstants.getDashSubAccountNumber().equals(originEntry.getSubAccountNumber())) {
                    originEntry.setSubAccount(null);
                }
                else {
                    persistenceService.retrieveReferenceObject(originEntry, reference);
                }
            }
            else {
                persistenceService.retrieveReferenceObject(originEntry, reference);
            }
        }
    }

    /**
     * Validates the closed period code of the origin entry. Scrubber accepts closed fiscal periods for the specified balance type.
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */
    //this validation is duplicated.  This is in ScrubberValidatorImpl under GL
//    protected Message validateClosedPeriodCode(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry) {
//        LOG.debug("validateClosedPeriodCode() started");
//
//        String periodCode = laborOriginEntry.getUniversityFiscalPeriodCode();
//        if (StringUtils.isBlank(periodCode)) {
//            return null;
//        }
//
//        // Scrubber accepts closed fiscal periods for A21 Balance
//        AccountingPeriod accountingPeriod = referenceLookup.get().getAccountingPeriod(laborOriginEntry);
//        if (ObjectUtils.isNotNull(accountingPeriod) && !accountingPeriod.isActive()) {
//            String bypassBalanceType = parameterService.getParameterValueAsString(LaborScrubberStep.class, LaborConstants.Scrubber.CLOSED_FISCAL_PERIOD_BYPASS_BALANCE_TYPES);
//
//            if (!laborWorkingEntry.getFinancialBalanceTypeCode().equals(bypassBalanceType)) {
//                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_FISCAL_PERIOD_CLOSED, periodCode, Message.TYPE_FATAL);
//            }
//
//            laborWorkingEntry.setUniversityFiscalPeriodCode(periodCode);
//        }
//
//        return null;
//    }

    /**
     * This method is for validation of payrollEndFiscalYear
     */
    protected Message validatePayrollEndFiscalYear(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate, LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        LOG.debug("validatePayrollEndFiscalYear() started");
        SystemOptions scrubbedEntryOption = null;
        if (laborOriginEntry.getPayrollEndDateFiscalYear() != null){
            scrubbedEntryOption = laborAccountingCycleCachingService.getSystemOptions(laborOriginEntry.getPayrollEndDateFiscalYear());
            
            if (scrubbedEntryOption == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_PAYROLL_END_DATE_FISCAL_YEAR, "" + laborOriginEntry.getPayrollEndDateFiscalYear(), Message.TYPE_FATAL);
            }
            
        }

        return null;
    }

    /**
     * This method is for validation of PayrollEndFiscalPeriodCode
     */
    protected Message validatePayrollEndFiscalPeriodCode(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate, LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");

        AccountingPeriod accountingPeriod = null;
        Integer tempPayrollFiscalYear = 0;
        if (laborOriginEntry.getPayrollEndDateFiscalYear()== null ){
            tempPayrollFiscalYear = universityRunDate.getUniversityFiscalYear();
        } else {
            tempPayrollFiscalYear = laborOriginEntry.getPayrollEndDateFiscalYear();
        }
        
        if (!laborOriginEntry.getPayrollEndDateFiscalPeriodCode().equals("")  ){
            accountingPeriod = laborAccountingCycleCachingService.getAccountingPeriod(tempPayrollFiscalYear, laborOriginEntry.getPayrollEndDateFiscalPeriodCode());
            if (accountingPeriod == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_PAYROLL_END_DATE_FISCAL_PERIOD, laborOriginEntry.getPayrollEndDateFiscalPeriodCode(), Message.TYPE_FATAL);
            }
        }


        return null;
    }

    /**
     * Performs Account Validation.
     */
    protected Message validateAccount(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate, LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        LOG.debug("validateAccount() started");

        Account account = laborOriginEntry.getAccount();
        boolean suspenseAccountLogicInd = parameterService.getParameterValueAsBoolean(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
        if (ObjectUtils.isNull(account)) {
            if (suspenseAccountLogicInd) {
                return useSuspenseAccount(laborWorkingEntry);
            }
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND, laborOriginEntry.getChartOfAccountsCode() + "-" + laborOriginEntry.getAccountNumber(), Message.TYPE_FATAL);
        }

        // default
        laborWorkingEntry.setAccount(account);
        laborWorkingEntry.setChartOfAccountsCode(account.getChartOfAccountsCode());
        laborWorkingEntry.setAccountNumber(account.getAccountNumber());

        // no further validation for gl annual doc type
        String glAnnualClosingType = parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
        if (glAnnualClosingType.equals(laborOriginEntry.getFinancialDocumentTypeCode())) {
            return null;
        }

        // Sub-Fund Wage Exclusion
        String orginationCode = laborOriginEntry.getFinancialSystemOriginationCode();
        List<String> nonWageSubfundBypassOriginationCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(LaborScrubberStep.class, LaborConstants.Scrubber.NON_WAGE_SUB_FUND_BYPASS_ORIGINATIONS) );
        boolean subfundWageExclusionInd = parameterService.getParameterValueAsBoolean(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER);

        if (subfundWageExclusionInd && !account.getSubFundGroup().isSubFundGroupWagesIndicator() && !nonWageSubfundBypassOriginationCodes.contains(orginationCode)) {
            if (suspenseAccountLogicInd) {
                return useSuspenseAccount(laborWorkingEntry);
            }

            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_SUN_FUND_NOT_ACCEPT_WAGES, Message.TYPE_FATAL);
        }

        // Account Fringe Validation
        List<String> nonFringeAccountBypassOriginationCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(LaborScrubberStep.class, LaborConstants.Scrubber.NON_FRINGE_ACCOUNT_BYPASS_ORIGINATIONS) );
        boolean accountFringeExclusionInd = parameterService.getParameterValueAsBoolean(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER);

        if (accountFringeExclusionInd && !nonFringeAccountBypassOriginationCodes.contains(orginationCode)) {
            return checkAccountFringeIndicator(laborOriginEntry, laborWorkingEntry, account, universityRunDate, laborAccountingCycleCachingService);
        }

        // Expired/Closed Validation
        return handleExpiredClosedAccount(laborOriginEntry.getAccount(), laborOriginEntry, laborWorkingEntry, universityRunDate);
    }

    /**
     * Checks the continuation account system indicator. If on checks whether the account is expired or closed, and if so calls the
     * contination logic.
     */
    protected Message handleExpiredClosedAccount(Account account, LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        List<String> continuationAccountBypassBalanceTypeCodes = balanceTypService.getContinuationAccountBypassBalanceTypeCodes(universityRunDate.getUniversityFiscalYear());
        List<String> continuationAccountBypassOriginationCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES) );
        List<String> continuationAccountBypassDocumentTypeCodes = new ArrayList<String>( parameterService.getParameterValuesAsString(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES) );

        Calendar today = Calendar.getInstance();
        today.setTime(universityRunDate.getUniversityDate());

        long offsetAccountExpirationTime = getAdjustedAccountExpirationDate(account);
        boolean isAccountExpiredOrClosed = (account.getAccountExpirationDate() != null && isAccountExpired(account, universityRunDate)) || !account.isActive();
        boolean continuationAccountLogicInd = parameterService.getParameterValueAsBoolean(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER);

        if (continuationAccountLogicInd && isAccountExpiredOrClosed) {
            // special checks for origination codes that have override ability
            boolean isOverrideOriginCode = continuationAccountBypassOriginationCodes.contains(laborOriginEntry.getFinancialSystemOriginationCode());
            if (isOverrideOriginCode && !account.isActive()) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT, laborOriginEntry.getChartOfAccountsCode() + "-" + laborOriginEntry.getAccountNumber(), Message.TYPE_FATAL);
            }

            boolean canBypass = isOverrideOriginCode || continuationAccountBypassBalanceTypeCodes.contains(laborOriginEntry.getFinancialBalanceTypeCode()) || continuationAccountBypassDocumentTypeCodes.contains(laborOriginEntry.getFinancialDocumentTypeCode().trim());
            if (account.isActive() && canBypass) {
                return null;
            }

            return continuationAccountLogic(account, laborOriginEntry, laborWorkingEntry, universityRunDate);
        }

        return null;
    }

    /**
     * Loops through continuation accounts for 10 tries or until it finds an account that is not expired.
     */
    protected Message continuationAccountLogic(Account expiredClosedAccount, LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        String chartCode = expiredClosedAccount.getContinuationFinChrtOfAcctCd();
        String accountNumber = expiredClosedAccount.getContinuationAccountNumber();

        List<String> checkedAccountNumbers = new ArrayList<String>();
        for (int i = 0; i < 10; ++i) {
            if (checkedAccountNumbers.contains(chartCode + accountNumber)) {
                // Something is really wrong with the data because this account has already been evaluated.
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC, Message.TYPE_FATAL);
            }

            checkedAccountNumbers.add(chartCode + accountNumber);

            if (chartCode == null || accountNumber == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND, Message.TYPE_FATAL);
            }

            // Lookup the account
            Account account = accountService.getByPrimaryId(chartCode, accountNumber);
            if (ObjectUtils.isNull(account)) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND, Message.TYPE_FATAL);
            }

            // check account expiration
            long offsetAccountExpirationTime = getAdjustedAccountExpirationDate(account);
            if (ObjectUtils.isNotNull(account.getAccountExpirationDate()) && isAccountExpired(account, universityRunDate)) {
                chartCode = account.getContinuationFinChrtOfAcctCd();
                accountNumber = account.getContinuationAccountNumber();
            }
            else {
                
                // set continuationAccountLogicIndi
                continuationAccountIndicator = true;
                
                laborWorkingEntry.setAccount(account);
                laborWorkingEntry.setAccountNumber(accountNumber);
                laborWorkingEntry.setChartOfAccountsCode(chartCode);
                laborWorkingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                laborWorkingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_AUTO_FORWARD) + " " + expiredClosedAccount.getChartOfAccountsCode() + expiredClosedAccount.getAccountNumber() + laborOriginEntry.getTransactionLedgerEntryDescription());

                return MessageBuilder.buildMessage(KFSKeyConstants.MSG_ACCOUNT_CLOSED_TO, laborWorkingEntry.getChartOfAccountsCode() + "-" + laborWorkingEntry.getAccountNumber(), Message.TYPE_WARNING);
            }
        }

        // We failed to find a valid continuation account.
        boolean suspenseAccountLogicInd = parameterService.getParameterValueAsBoolean(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
        if (suspenseAccountLogicInd) {
            return useSuspenseAccount(laborWorkingEntry);
        }
        else {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED, Message.TYPE_FATAL);
        }
    }

    /**
     * For fringe transaction types checks if the account accepts fringe benefits. If not, retrieves the alternative account, then
     * calls expiration checking on either the alternative account or the account passed in.
     */
    protected Message checkAccountFringeIndicator(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, Account account, UniversityDate universityRunDate, LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        // check for fringe tranaction type
        //LaborObject laborObject = (LaborObject) businessObjectService.findByPrimaryKey(LaborObject.class, fieldValues);
        LaborObject laborObject = laborAccountingCycleCachingService.getLaborObject(laborOriginEntry.getUniversityFiscalYear(), laborOriginEntry.getChartOfAccountsCode(), laborOriginEntry.getFinancialObjectCode());
        boolean isFringeTransaction = laborObject != null && org.apache.commons.lang.StringUtils.equals(LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE, laborObject.getFinancialObjectFringeOrSalaryCode());

        // alternative account handling for non fringe accounts
        if (isFringeTransaction && !account.isAccountsFringesBnftIndicator()) {
            Account altAccount = accountService.getByPrimaryId(laborOriginEntry.getAccount().getReportsToChartOfAccountsCode(), laborOriginEntry.getAccount().getReportsToAccountNumber());
            if (ObjectUtils.isNotNull(altAccount)) {
                laborWorkingEntry.setAccount(altAccount);
                laborWorkingEntry.setAccountNumber(altAccount.getAccountNumber());
                laborWorkingEntry.setChartOfAccountsCode(altAccount.getChartOfAccountsCode());
                Message err = handleExpiredClosedAccount(altAccount, laborOriginEntry, laborWorkingEntry, universityRunDate);
                if (err == null) {
                    err = MessageBuilder.buildMessageWithPlaceHolder(LaborKeyConstants.MESSAGE_FRINGES_MOVED_TO, Message.TYPE_WARNING, new Object[] { altAccount.getAccountNumber() } );
                }
                return err;
            }

            // no alt acct, use suspense acct if active
            boolean suspenseAccountLogicInd = parameterService.getParameterValueAsBoolean(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
            if (suspenseAccountLogicInd) {
                return useSuspenseAccount(laborWorkingEntry);
            }

            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_NON_FRINGE_ACCOUNT_ALTERNATIVE_NOT_FOUND, Message.TYPE_FATAL);
        }

        return handleExpiredClosedAccount(account, laborOriginEntry, laborWorkingEntry, universityRunDate);
    }

    /**
     * Adjustment of Account if it is contracts and grants
     */
    protected long getAdjustedAccountExpirationDate(Account account) {
        long offsetAccountExpirationTime = 0;

        if (account.getAccountExpirationDate() != null) {
            offsetAccountExpirationTime = account.getAccountExpirationDate().getTime();

            if (account.isForContractsAndGrants() && account.isActive()) {
                String daysOffset = parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                int daysOffsetInt = 0; // default to 0

                if (!org.apache.commons.lang.StringUtils.isBlank(daysOffset)) {
                    daysOffsetInt = new Integer(daysOffset).intValue();
                }

                Calendar tempCal = Calendar.getInstance();
                tempCal.setTimeInMillis(offsetAccountExpirationTime);
                tempCal.add(Calendar.DAY_OF_MONTH, daysOffsetInt);
                offsetAccountExpirationTime = tempCal.getTimeInMillis();
            }
        }

        return offsetAccountExpirationTime;
    }

    /**
     * This method changes account to suspenseAccount
     */
    protected Message useSuspenseAccount(LaborOriginEntry workingEntry) {
        String suspenseAccountNumber = parameterService.getParameterValueAsString(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT);
        String suspenseCOAcode = parameterService.getParameterValueAsString(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_CHART);
        String suspenseSubAccountNumber = parameterService.getParameterValueAsString(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_SUB_ACCOUNT);

        Account account = accountService.getByPrimaryId(suspenseCOAcode, suspenseAccountNumber);

        if (ObjectUtils.isNull(account)) {
            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_INVALID_SUSPENSE_ACCOUNT, Message.TYPE_FATAL);
        }

        workingEntry.setAccount(account);
        workingEntry.setAccountNumber(suspenseAccountNumber);
        workingEntry.setChartOfAccountsCode(suspenseCOAcode);
        workingEntry.setSubAccountNumber(suspenseSubAccountNumber);

        return MessageBuilder.buildMessageWithPlaceHolder(LaborKeyConstants.MESSAGE_SUSPENSE_ACCOUNT_APPLIED, Message.TYPE_WARNING, new Object[] { suspenseCOAcode, suspenseAccountNumber, suspenseSubAccountNumber } );
    }
    
    /**
     * Validates the sub account of the origin entry
     * 
     * @param originEntry the origin entry being scrubbed
     * @param workingEntry the scrubbed version of the origin entry
     * @return a Message if an error was encountered, otherwise null
     */

    protected Message validateSubAccount(LaborOriginEntry originEntry, LaborOriginEntry workingEntry, LaborAccountingCycleCachingService laborAccountingCycleCachingService) {
        LOG.debug("validateSubAccount() started");

        // when continuationAccount used, the subAccountNumber should be changed to dashes and skip validation subAccount process
        if (continuationAccountIndicator) {
            workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            return null;
        }
                
        // If the sub account number is empty, set it to dashes.
        // Otherwise set the workingEntry sub account number to the
        // sub account number of the input origin entry.
        if (org.springframework.util.StringUtils.hasText(originEntry.getSubAccountNumber())) {
            // sub account IS specified
            if (!KFSConstants.getDashSubAccountNumber().equals(originEntry.getSubAccountNumber())) {
              SubAccount originEntrySubAccount = laborAccountingCycleCachingService.getSubAccount(originEntry.getChartOfAccountsCode(), originEntry.getAccountNumber(), originEntry.getSubAccountNumber());
              //SubAccount originEntrySubAccount = getSubAccount(originEntry);
                if (originEntrySubAccount == null) {
                    // sub account is not valid
                    return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND, originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" + originEntry.getSubAccountNumber(), Message.TYPE_FATAL);
                }
                else {
                    // sub account IS valid
                    if (originEntrySubAccount.isActive()) {
                        // sub account IS active
                        workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
                    }
                    else {
                        // sub account IS NOT active
                        if (parameterService.getParameterValueAsString(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE).equals(originEntry.getFinancialDocumentTypeCode())) {
                            // document IS annual closing
                            workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
                        }
                        else {
                            // document is NOT annual closing
                            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE, originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" + originEntry.getSubAccountNumber(), Message.TYPE_FATAL);
                        }
                    }
                }
            }
            else {
                // the sub account is dashes
                workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            }
        }
        else {
            // No sub account is specified.
            workingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        
        
        return null;
        
    }
    

    /**
     * @see org.kuali.kfs.gl.service.ScrubberValidator#isAccountExpired(org.kuali.kfs.coa.businessobject.Account, org.kuali.kfs.sys.businessobject.UniversityDate)
     */
    public boolean isAccountExpired(Account account, UniversityDate universityRunDate) {
        return scrubberValidator.isAccountExpired(account, universityRunDate);
    }

    public void validateForInquiry(GeneralLedgerPendingEntry entry) {
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the kualiConfigurationService attribute value.
     * 
     * @param service The kualiConfigurationService to set.
     */
    public void setConfigurationService(ConfigurationService service) {
        kualiConfigurationService = service;
    }

    /**
     * Sets the accountService attribute value.
     * 
     * @param as The accountService to set.
     */
    public void setAccountService(AccountService as) {
        accountService = as;
    }

    /**
     * Sets the persistenceService attribute value.
     * 
     * @param ps The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the balanceTypService attribute value.
     * 
     * @param balanceTypService The balanceTypService to set.
     */
    public void setBalanceTypService(BalanceTypeService balanceTypService) {
        this.balanceTypService = balanceTypService;
    }

    /**
     * Sets the scrubberValidator attribute value.
     * 
     * @param sv The scrubberValidator to set.
     */
    public void setScrubberValidator(ScrubberValidator sv) {
        scrubberValidator = sv;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Sets the optionsService attribute value.
     * 
     * @param optionsService The optionsService to set.
     */
    public void setOptionsService(OptionsService optionsService) {
        this.optionsService = optionsService;
    }
    
    
}
