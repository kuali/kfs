/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.labor.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.kfs.util.Message;
import org.kuali.kfs.util.MessageBuilder;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.gl.batch.ScrubberStep;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.OriginEntryLookupService;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.LaborKeyConstants;
import org.kuali.module.labor.batch.LaborScrubberStep;
import org.kuali.module.labor.bo.LaborObject;
import org.kuali.module.labor.bo.LaborOriginEntry;

/**
 * Service implementation of ScrubberValidator.
 */
public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private BusinessObjectService businessObjectService;
    private ParameterService parameterService;
    private AccountService accountService;
    private BalanceTypService balanceTypService;

    private PersistenceService persistenceService;
    private ScrubberValidator scrubberValidator;
    private PersistenceStructureService persistenceStructureService;
    private ThreadLocal<OriginEntryLookupService> referenceLookup = new ThreadLocal<OriginEntryLookupService>();

    /**
     * @see org.kuali.module.labor.service.LaborScrubberValidator#validateTransaction(owrg.kuali.module.labor.bo.LaborOriginEntry,
     *      org.kuali.module.labor.bo.LaborOriginEntry, org.kuali.module.gl.bo.UniversityDate)
     */
    public List<Message> validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate, boolean laborIndicator) {
        LOG.debug("validateTransaction() started");
        List<Message> errors = new ArrayList<Message>();

        LaborOriginEntry laborOriginEntry = (LaborOriginEntry) originEntry;
        LaborOriginEntry laborScrubbedEntry = (LaborOriginEntry) scrubbedEntry;

        scrubberValidator.setReferenceLookup(referenceLookup.get());

        // gl scrubber validation
        errors = scrubberValidator.validateTransaction(laborOriginEntry, laborScrubbedEntry, universityRunDate, laborIndicator);
        
        refreshOriginEntryReferences(laborOriginEntry);
        refreshOriginEntryReferences(laborScrubbedEntry);

        if (StringUtils.isBlank(laborOriginEntry.getEmplid())) {
            laborScrubbedEntry.setEmplid(LaborConstants.getDashEmplId());
        }

        if (StringUtils.isBlank(laborOriginEntry.getPositionNumber())) {
            laborScrubbedEntry.setPositionNumber(LaborConstants.getDashPositionNumber());
        }

        Message err = null;
        
        err = this.validateClosedPeriodCode(laborOriginEntry, laborScrubbedEntry);
        if(err != null) {
            errors.add(err);
        }

        err = validatePayrollEndFiscalYear(laborOriginEntry, laborScrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validatePayrollEndFiscalPeriodCode(laborOriginEntry, laborScrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateAccount(laborOriginEntry, laborScrubbedEntry, universityRunDate);
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
    private Message validateClosedPeriodCode(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry) {
        LOG.debug("validateClosedPeriodCode() started");

        String periodCode = laborOriginEntry.getUniversityFiscalPeriodCode();
        if (StringUtils.isBlank(periodCode)) {
            return null;
        }
                
        // Scrubber accepts closed fiscal periods for A21 Balance
        AccountingPeriod accountingPeriod = referenceLookup.get().getAccountingPeriod(laborOriginEntry);
        if (accountingPeriod != null && KFSConstants.ACCOUNTING_PERIOD_STATUS_CLOSED.equals(accountingPeriod.getUniversityFiscalPeriodStatusCode())) {
            String bypassBalanceType = parameterService.getParameterValue(LaborScrubberStep.class, LaborConstants.Scrubber.CLOSED_FISCAL_PERIOD_BYPASS_BALANCE_TYPES);
            
            if (!laborWorkingEntry.getFinancialBalanceTypeCode().equals(bypassBalanceType)) {
                String errorMessage = kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_FISCAL_PERIOD_CLOSED); 
                return new Message(errorMessage + " (" + laborOriginEntry.getUniversityFiscalPeriodCode() + ")", Message.TYPE_FATAL);
            }
            
            laborWorkingEntry.setUniversityFiscalPeriodCode(periodCode);
        }

        return null;
    }

    /**
     * This method is for validation of payrollEndFiscalYear
     */
    private Message validatePayrollEndFiscalYear(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateFiscalYear() started");

        if ((laborOriginEntry.getPayrollEndDateFiscalYear() == null) || laborOriginEntry.getUniversityFiscalYear() == 0) {
            laborOriginEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
            laborWorkingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());

            // Retrieve these objects because the fiscal year is the primary key for them
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.FINANCIAL_SUB_OBJECT);
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.FINANCIAL_OBJECT);
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.ACCOUNTING_PERIOD);
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.OPTION);
        }
        else {
            laborWorkingEntry.setUniversityFiscalYear(laborOriginEntry.getUniversityFiscalYear());
            laborWorkingEntry.setOption(laborOriginEntry.getOption());
        }

        if (laborOriginEntry.getOption() == null) {
            return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND, "" + laborOriginEntry.getUniversityFiscalYear(), Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * This method is for validation of PayrollEndFiscalPeriodCode
     */
    private Message validatePayrollEndFiscalPeriodCode(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");

        if (StringUtils.isNotBlank(laborOriginEntry.getUniversityFiscalPeriodCode())) {
            laborWorkingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
            laborWorkingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());

            // Retrieve these objects because the fiscal year is the primary key for them
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.FINANCIAL_SUB_OBJECT);
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.FINANCIAL_OBJECT);
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.ACCOUNTING_PERIOD);
            persistenceService.retrieveReferenceObject(laborOriginEntry, KFSPropertyConstants.OPTION);
        }
        else {
            if (laborOriginEntry.getAccountingPeriod() == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND, laborOriginEntry.getUniversityFiscalPeriodCode(), Message.TYPE_FATAL);
            }
            laborWorkingEntry.setUniversityFiscalPeriodCode(laborOriginEntry.getUniversityFiscalPeriodCode());
        }

        return null;
    }

    /**
     * Performs Account Validation.
     */
    private Message validateAccount(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateAccount() started");

        Account account = laborOriginEntry.getAccount();
        boolean suspenseAccountLogicInd = parameterService.getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
        if (account == null) {
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
        String glAnnualClosingType = parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE);
        if (glAnnualClosingType.equals(laborOriginEntry.getFinancialDocumentTypeCode())) {
            return null;
        }

        // Sub-Fund Wage Exclusion
        String orginationCode = laborOriginEntry.getFinancialSystemOriginationCode();
        List<String> nonWageSubfundBypassOriginationCodes = parameterService.getParameterValues(LaborScrubberStep.class, LaborConstants.Scrubber.NON_WAGE_SUB_FUND_BYPASS_ORIGINATIONS);
        boolean subfundWageExclusionInd = parameterService.getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER);

        if (subfundWageExclusionInd && !account.getSubFundGroup().isSubFundGroupWagesIndicator() && !nonWageSubfundBypassOriginationCodes.contains(orginationCode)) {
            if (suspenseAccountLogicInd) {
                return useSuspenseAccount(laborWorkingEntry);
            }

            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_SUN_FUND_NOT_ACCEPT_WAGES, Message.TYPE_FATAL);
        }

        // Account Fringe Validation
        List<String> nonFringeAccountBypassOriginationCodes = parameterService.getParameterValues(LaborScrubberStep.class, LaborConstants.Scrubber.NON_FRINGE_ACCOUNT_BYPASS_ORIGINATIONS);
        boolean accountFringeExclusionInd = parameterService.getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER);

        if (accountFringeExclusionInd && !nonFringeAccountBypassOriginationCodes.contains(orginationCode)) {
            return checkAccountFringeIndicator(laborOriginEntry, laborWorkingEntry, account, universityRunDate);
        }

        // Expired/Closed Validation
        return handleExpiredClosedAccount(laborOriginEntry.getAccount(), laborOriginEntry, laborWorkingEntry, universityRunDate);
    }

    /**
     * Checks the continuation account system indicator. If on checks whether the account is expired or closed, and if so calls the
     * contination logic.
     */
    private Message handleExpiredClosedAccount(Account account, LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        List<String> continuationAccountBypassBalanceTypeCodes = balanceTypService.getContinuationAccountBypassBalanceTypeCodes(universityRunDate.getUniversityFiscalYear());
        List<String> continuationAccountBypassOriginationCodes = parameterService.getParameterValues(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES);
        List<String> continuationAccountBypassDocumentTypeCodes = parameterService.getParameterValues(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES);

        Calendar today = Calendar.getInstance();
        today.setTime(universityRunDate.getUniversityDate());

        long offsetAccountExpirationTime = getAdjustedAccountExpirationDate(account);
        boolean isAccountExpiredOrClosed = (account.getAccountExpirationDate() != null && isExpired(offsetAccountExpirationTime, today)) || account.isAccountClosedIndicator();
        boolean continuationAccountLogicInd = parameterService.getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER);
        
        if (continuationAccountLogicInd && isAccountExpiredOrClosed) {
            // special checks for origination codes that have override ability
            boolean isOverrideOriginCode = continuationAccountBypassOriginationCodes.contains(laborOriginEntry.getFinancialSystemOriginationCode());
            if (isOverrideOriginCode && account.isAccountClosedIndicator()) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT, laborOriginEntry.getChartOfAccountsCode() + "-" + laborOriginEntry.getAccountNumber(), Message.TYPE_FATAL);
            }

            boolean canBypass = isOverrideOriginCode || continuationAccountBypassBalanceTypeCodes.contains(laborOriginEntry.getFinancialBalanceTypeCode()) || continuationAccountBypassDocumentTypeCodes.contains(laborOriginEntry.getFinancialDocumentTypeCode().trim());
            if (!account.isAccountClosedIndicator() && canBypass) {
                return null;
            }

            return continuationAccountLogic(account, laborOriginEntry, laborWorkingEntry, today);
        }

        return null;
    }

    /**
     * Loops through continuation accounts for 10 tries or until it finds an account that is not expired.
     */
    private Message continuationAccountLogic(Account expiredClosedAccount, LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, Calendar today) {
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
            if (account == null) {
                return MessageBuilder.buildMessage(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND, Message.TYPE_FATAL);
            }

            // check account expiration
            long offsetAccountExpirationTime = getAdjustedAccountExpirationDate(account);
            if (account.getAccountExpirationDate() != null && isExpired(offsetAccountExpirationTime, today)) {
                chartCode = account.getContinuationFinChrtOfAcctCd();
                accountNumber = account.getContinuationAccountNumber();
            }
            else {
                laborWorkingEntry.setAccount(account);
                laborWorkingEntry.setAccountNumber(accountNumber);
                laborWorkingEntry.setChartOfAccountsCode(chartCode);
                laborWorkingEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                laborWorkingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_AUTO_FORWARD) + " " + expiredClosedAccount.getChartOfAccountsCode() + expiredClosedAccount.getAccountNumber() + laborOriginEntry.getTransactionLedgerEntryDescription());

                return MessageBuilder.buildMessage(KFSKeyConstants.MSG_ACCOUNT_CLOSED_TO, laborWorkingEntry.getChartOfAccountsCode() + "-" + laborWorkingEntry.getAccountNumber(), Message.TYPE_WARNING);
            }
        }

        // We failed to find a valid continuation account.
        boolean suspenseAccountLogicInd = parameterService.getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
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
    private Message checkAccountFringeIndicator(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, Account account, UniversityDate universityRunDate) {
        // check for fringe tranaction type
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, laborOriginEntry.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, laborOriginEntry.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, laborOriginEntry.getFinancialObjectCode());

        LaborObject laborObject = (LaborObject) businessObjectService.findByPrimaryKey(LaborObject.class, fieldValues);
        boolean isFringeTransaction = laborObject != null && org.apache.commons.lang.StringUtils.equals(LaborConstants.BenefitExpenseTransfer.LABOR_LEDGER_BENEFIT_CODE, laborObject.getFinancialObjectFringeOrSalaryCode());

        // alternative account handling for non fringe accounts
        if (isFringeTransaction && !account.isAccountsFringesBnftIndicator()) {
            Account altAccount = accountService.getByPrimaryId(laborOriginEntry.getAccount().getReportsToChartOfAccountsCode(), laborOriginEntry.getAccount().getReportsToAccountNumber());
            if (altAccount != null) {
                laborWorkingEntry.setAccount(altAccount);
                laborWorkingEntry.setAccountNumber(altAccount.getAccountNumber());
                laborWorkingEntry.setChartOfAccountsCode(altAccount.getChartOfAccountsCode());

                return handleExpiredClosedAccount(altAccount, laborOriginEntry, laborWorkingEntry, universityRunDate);
            }

            // no alt acct, use suspense acct if active
            boolean suspenseAccountLogicInd = parameterService.getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
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
    private long getAdjustedAccountExpirationDate(Account account) {
        long offsetAccountExpirationTime = 0;

        if (account.getAccountExpirationDate() != null) {
            offsetAccountExpirationTime = account.getAccountExpirationDate().getTime();

            if (account.isForContractsAndGrants() && (!account.isAccountClosedIndicator())) {
                String daysOffset = parameterService.getParameterValue(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                int daysOffsetInt = 3 * 30; // default to 90 days (approximately 3 months)

                if (daysOffset.trim().length() > 0) {
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
     * Checking whether or not the account is expired
     */
    private boolean isExpired(long offsetAccountExpirationTime, Calendar runCalendar) {
        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(offsetAccountExpirationTime);

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDoy = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDoy = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDoy < runDoy);
    }

    /**
     * This method changes account to suspenseAccount
     */
    private Message useSuspenseAccount(LaborOriginEntry workingEntry) {
        String suspenseAccountNumber = parameterService.getParameterValue(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT);
        String suspenseCOAcode = parameterService.getParameterValue(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_CHART);
        String suspenseSubAccountNumber = parameterService.getParameterValue(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_SUB_ACCOUNT);
        
        Account account = accountService.getByPrimaryId(suspenseCOAcode, suspenseAccountNumber);

        if (account == null) {
            return MessageBuilder.buildMessage(LaborKeyConstants.ERROR_INVALID_SUSPENSE_ACCOUNT, Message.TYPE_FATAL);
        }

        workingEntry.setAccount(account);
        workingEntry.setAccountNumber(suspenseAccountNumber);
        workingEntry.setChartOfAccountsCode(suspenseCOAcode);
        workingEntry.setSubAccountNumber(suspenseSubAccountNumber);

        return MessageBuilder.buildMessageWithPlaceHolder(LaborKeyConstants.MESSAGE_SUSPENSE_ACCOUNT_APPLIED, Message.TYPE_WARNING, suspenseCOAcode, suspenseAccountNumber, suspenseSubAccountNumber);
    }

    /**
     * Sets the referenceLookup attribute value.
     * 
     * @param referenceLookup The referenceLookup to set.
     */
    public void setReferenceLookup(OriginEntryLookupService originEntryLookupService) {
        this.referenceLookup.set(originEntryLookupService);
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
    public void setKualiConfigurationService(KualiConfigurationService service) {
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
    public void setBalanceTypService(BalanceTypService balanceTypService) {
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
}