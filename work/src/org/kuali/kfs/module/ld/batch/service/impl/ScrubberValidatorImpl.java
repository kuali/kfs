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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.ScrubberStep;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.OriginEntryLookupService;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.batch.LaborScrubberStep;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.springframework.util.StringUtils;

/**
 * Service implementation of ScrubberValidator.
 */
public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private AccountService accountService;
    private PersistenceService persistenceService;
    private ScrubberValidator scrubberValidator;
    private PersistenceStructureService persistenceStructureService;
    private ThreadLocal<OriginEntryLookupService> referenceLookup = new ThreadLocal<OriginEntryLookupService>();
    
    private int numOfAttempts = 10;
    private int numOfExtraMonthsForCGAccount = 3;

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

        // For labor scrubber.

        errors = scrubberValidator.validateTransaction(laborOriginEntry, laborScrubbedEntry, universityRunDate, laborIndicator);
        refreshOriginEntryReferences(laborOriginEntry);
        refreshOriginEntryReferences(laborScrubbedEntry);

        Message err = null;

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

        err = validateEmplId(laborOriginEntry, laborScrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        return errors;
    }

    /**
     * This method is for refreshing References of Origin Entry
     * 
     * @param originEntry
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
     * Sets the scrubberValidator attribute value.
     * 
     * @param sv The scrubberValidator to set.
     */
    public void setScrubberValidator(ScrubberValidator sv) {
        scrubberValidator = sv;
    }

    /**
     * This method is for validation of payrollEndFiscalYear
     * 
     * @param laborOriginEntry
     * @param laborWorkingEntry
     * @param universityRunDate
     * @return
     */
    private Message validatePayrollEndFiscalYear(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateFiscalYear() started");

        if ((laborOriginEntry.getPayrollEndDateFiscalYear() == null) || (laborOriginEntry.getUniversityFiscalYear().intValue() == 0)) {
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
            return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND) + " (" + laborOriginEntry.getUniversityFiscalYear() + ")", Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * This method is for validation of PayrollEndFiscalPeriodCode
     * 
     * @param laborOriginEntry
     * @param laborWorkingEntry
     * @param universityRunDate
     * @return
     */
    private Message validatePayrollEndFiscalPeriodCode(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");

        if (!StringUtils.hasText(laborOriginEntry.getUniversityFiscalPeriodCode())) {
            laborWorkingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
            laborWorkingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());

            // Retrieve these objects because the fiscal year is the primary key for them
            persistenceService.retrieveReferenceObject(laborOriginEntry, "financialSubObject");
            persistenceService.retrieveReferenceObject(laborOriginEntry, "financialObject");
            persistenceService.retrieveReferenceObject(laborOriginEntry, "accountingPeriod");
            persistenceService.retrieveReferenceObject(laborOriginEntry, "option");
        }
        else {
            if (laborOriginEntry.getAccountingPeriod() == null) {
                return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND) + " (" + laborOriginEntry.getUniversityFiscalPeriodCode() + ")", Message.TYPE_FATAL);
            }
            // don't need to be open.
            /*
             * else if
             * (Constants.ACCOUNTING_PERIOD_STATUS_CLOSED.equals(originEntry.getAccountingPeriod().getUniversityFiscalPeriodStatusCode())) {
             * return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_FISCAL_PERIOD_CLOSED) + " (" +
             * originEntry.getUniversityFiscalPeriodCode() + ")", Message.TYPE_FATAL); }
             */
            laborWorkingEntry.setUniversityFiscalPeriodCode(laborOriginEntry.getUniversityFiscalPeriodCode());
        }

        return null;
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
     * Sets the persistenceStructureService attribute value.
     * 
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * This method is for validation of Account
     * 
     * @param laborOriginEntry
     * @param laborWorkingEntry
     * @param universityRunDate
     * @return
     */
    private Message validateAccount(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateAccount() started");

        Calendar today = Calendar.getInstance();
        today.setTime(universityRunDate.getUniversityDate());
                                                                                                                    
        String[] continuationAccountBypassOriginationCodes = SpringContext.getBean(ParameterService.class).getParameterValues(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_BYPASS_ORIGINATION_CODES).toArray(new String[] {});
        String[] continuationAccountBypassBalanceTypeCodes = SpringContext.getBean(BalanceTypService.class).getContinuationAccountBypassBalanceTypeCodes(universityRunDate.getUniversityFiscalYear()).toArray(new String[] {});
        String[] continuationAccountBypassDocumentTypeCodes = SpringContext.getBean(ParameterService.class).getParameterValues(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_BYPASS_DOCUMENT_TYPE_CODES).toArray(new String[] {});
        
        if (laborOriginEntry.getAccount() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_ACCOUNT_NOT_FOUND) + "(" + laborOriginEntry.getChartOfAccountsCode() + "-" + laborOriginEntry.getAccountNumber() + ")", Message.TYPE_FATAL);
        }

        if (SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ANNUAL_CLOSING_DOC_TYPE).equals(laborOriginEntry.getFinancialDocumentTypeCode())) {
            laborWorkingEntry.setAccountNumber(laborOriginEntry.getAccountNumber());
            laborWorkingEntry.setAccount(laborOriginEntry.getAccount());
            return null;
        }

        Account account = laborOriginEntry.getAccount();

        if ((account.getAccountExpirationDate() == null) && !account.isAccountClosedIndicator()) {
            // account is neither closed nor expired
            laborWorkingEntry.setAccountNumber(laborOriginEntry.getAccountNumber());
            laborWorkingEntry.setAccount(laborOriginEntry.getAccount());
            return null;
        }

        // Has an expiration date or is closed
        if ((org.apache.commons.lang.StringUtils.isNumeric(laborOriginEntry.getFinancialSystemOriginationCode()) || ObjectHelper.isOneOf(laborOriginEntry.getFinancialSystemOriginationCode(), continuationAccountBypassOriginationCodes)) && account.isAccountClosedIndicator()) {
            return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT) + " (" + laborOriginEntry.getAccount().getChartOfAccountsCode() + "-" + laborOriginEntry.getAccountNumber() + ")", Message.TYPE_FATAL);
        }

        if ((org.apache.commons.lang.StringUtils.isNumeric(laborOriginEntry.getFinancialSystemOriginationCode()) || ObjectHelper.isOneOf(laborOriginEntry.getFinancialSystemOriginationCode(), continuationAccountBypassOriginationCodes) || ObjectHelper.isOneOf(laborOriginEntry.getFinancialBalanceTypeCode(), continuationAccountBypassBalanceTypeCodes) || ObjectHelper.isOneOf(laborOriginEntry.getFinancialDocumentTypeCode().trim(), continuationAccountBypassDocumentTypeCodes)) && !account.isAccountClosedIndicator()) {
            laborWorkingEntry.setAccountNumber(laborOriginEntry.getAccountNumber());
            laborWorkingEntry.setAccount(laborOriginEntry.getAccount());
            return null;
        }


        adjustAccountIfContractsAndGrants(account);

        // Labor's new features
        boolean subfundWageExclusionInd = SpringContext.getBean(ParameterService.class).getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER);
        boolean accountFringeExclusionInd = SpringContext.getBean(ParameterService.class).getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER);
        boolean suspenseAccountLogicInd = SpringContext.getBean(ParameterService.class).getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
        boolean continuationAccountLogicInd = SpringContext.getBean(ParameterService.class).getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER);

        // checking Sub-Fund Wage Exclusion indicator
        if (subfundWageExclusionInd) {

            // checking Sub-Fund accept wage
            if (laborOriginEntry.getAccount().getSubFundGroup().isSubFundGroupWagesIndicator()) {

                // checking Account Fringe Exclusion indicator
                if (accountFringeExclusionInd) {

                    // checking existence of alternative account
                    account = accountService.getByPrimaryId(laborOriginEntry.getAccount().getReportsToChartOfAccountsCode(), laborOriginEntry.getAccount().getReportsToAccountNumber());
                    if (account != null) {
                        laborWorkingEntry.setAccount(account);
                        laborWorkingEntry.setAccountNumber(account.getAccountNumber());
                        laborWorkingEntry.setChartOfAccountsCode(account.getChartOfAccountsCode());

                        // checking Continuation Account Logic Indicator
                        if (continuationAccountLogicInd) {
                            if (isExpired(account, today) || account.isAccountClosedIndicator()) {
                                // Do continuation Account logic with laborWorkingEntry
                                // because laborOriginEntry shouldn't be changed.
                                Message error = continuationAccountLogic(laborWorkingEntry, laborWorkingEntry, today);
                                if (error != null) {
                                    return error;
                                }
                            }
                            // when Continuation Account Logic indicator is off
                        }
                        else {
                            if (suspenseAccountLogicInd) {
                                useSuspenseAccount(laborWorkingEntry);
                                return null;
                                // when Suspense Account Logic Indicator is off
                            }
                            else {
                                // Change error message later
                                return new Message("SUB FUND WAGE EXCLUSION", Message.TYPE_FATAL);
                            }
                        }


                    }
                    else {
                        if (suspenseAccountLogicInd) {
                            useSuspenseAccount(laborWorkingEntry);
                            return null;
                        }
                        else {
                            // Change error message later
                            // - not existence of alternative account from Reports To Account

                            return new Message("Not existence of alternative account from Reports To Account ", Message.TYPE_FATAL);
                        }

                    }

                    // when Account Fringe Exclusion Indicator is off
                }
                else {
                    if (continuationAccountLogicInd) {
                        if (isExpired(account, today) || account.isAccountClosedIndicator()) {
                            Message error = continuationAccountLogic(laborOriginEntry, laborWorkingEntry, today);
                            if (error != null) {
                                return error;
                            }
                        }
                    }
                    else {
                        if (suspenseAccountLogicInd) {
                            useSuspenseAccount(laborWorkingEntry);
                            return null;
                        }
                        else {
                            // Change error message later
                            return new Message("SUB FUND WAGE EXCLUSION", Message.TYPE_FATAL);
                        }
                    }
                }

                // When Sub-Fund not accept wage

            }
            else {
                if (suspenseAccountLogicInd) {
                    useSuspenseAccount(laborWorkingEntry);
                    return null;
                }
                else {
                    // Change error message later
                    // - Sub-fund not accepting Wage
                    return new Message("SUB FUND WAGE EXCLUSION", Message.TYPE_FATAL);
                }

            }


            // When Sub-Fund Wage Exclusion indicator is off
        }
        else {
            if (continuationAccountLogicInd) {
                if (isExpired(account, today) || account.isAccountClosedIndicator()) {
                    Message error = continuationAccountLogic(laborOriginEntry, laborWorkingEntry, today);
                    if (error != null) {
                        return error;
                    }
                }
            }
            else {
                if (suspenseAccountLogicInd) {
                    useSuspenseAccount(laborWorkingEntry);
                    return null;
                }
                else {
                    // Change error message later
                    return new Message("SUB FUND WAGE EXCLUSION", Message.TYPE_FATAL);
                }
            }
        }

        laborWorkingEntry.setAccountNumber(laborOriginEntry.getAccountNumber());
        laborWorkingEntry.setAccount(laborOriginEntry.getAccount());
        return null;
    }

    /**
     * This method is for logic of continuationAccountLogic
     * 
     * @param laborOriginEntry
     * @param laborWorkingEntry
     * @param today
     * @return
     */
    private Message continuationAccountLogic(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, Calendar today) {

        List checkedAccountNumbers = new ArrayList();

        Account account = null;

        String chartCode = laborOriginEntry.getAccount().getContinuationFinChrtOfAcctCd();
        String accountNumber = laborOriginEntry.getAccount().getContinuationAccountNumber();

        for (int i = 0; i < 10; ++i) {
            if (checkedAccountNumbers.contains(chartCode + accountNumber)) {
                // Something is really wrong with the data because this account has already been evaluated.
                return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC), Message.TYPE_FATAL);
            }

            if ((chartCode == null) || (accountNumber == null)) {
                return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND), Message.TYPE_FATAL);
            }

            // Lookup the account
            account = accountService.getByPrimaryId(chartCode, accountNumber);
            if (null == account) {
                // account not found
                return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND), Message.TYPE_FATAL);
            }
            else {
                // the account exists
                if (account.getAccountExpirationDate() == null) {
                    // No expiration date
                    laborWorkingEntry.setAccount(account);
                    laborWorkingEntry.setAccountNumber(accountNumber);
                    laborWorkingEntry.setChartOfAccountsCode(chartCode);

                    laborWorkingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_AUTO_FORWARD) + " " + laborOriginEntry.getChartOfAccountsCode() + laborOriginEntry.getAccountNumber() + laborOriginEntry.getTransactionLedgerEntryDescription());
                    return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_ACCOUNT_CLOSED_TO) + " " + laborWorkingEntry.getChartOfAccountsCode() + laborWorkingEntry.getAccountNumber(), Message.TYPE_WARNING);
                }
                else {
                    // the account does have an expiration date.
                    // This is the only case in which we might go
                    // on for another iteration of the loop.
                    checkedAccountNumbers.add(chartCode + accountNumber);

                    // Add 3 months to the expiration date if it's a contract and grant account.
                    adjustAccountIfContractsAndGrants(account);

                    // Check that the account has not expired.

                    // If the account has expired go around for another iteration.
                    if (isExpired(account, today)) {
                        chartCode = account.getContinuationFinChrtOfAcctCd();
                        accountNumber = account.getContinuationAccountNumber();
                    }
                    else {
                        laborWorkingEntry.setAccount(account);
                        laborWorkingEntry.setAccountNumber(accountNumber);
                        laborWorkingEntry.setChartOfAccountsCode(chartCode);

                        laborWorkingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_AUTO_FORWARD) + laborOriginEntry.getChartOfAccountsCode() + laborOriginEntry.getAccountNumber() + laborOriginEntry.getTransactionLedgerEntryDescription());
                        return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_ACCOUNT_CLOSED_TO) + laborWorkingEntry.getChartOfAccountsCode() + laborWorkingEntry.getAccountNumber(), Message.TYPE_WARNING);
                    }
                }
            }
        }

        // We failed to find a valid continuation account.
        boolean suspenseAccountLogicInd = SpringContext.getBean(ParameterService.class).getIndicatorParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER);
        if (suspenseAccountLogicInd) {
            useSuspenseAccount(laborWorkingEntry);
            return null;
        }
        else {
            // Change error message later
            // - not found a valid continuation account and suspense account logic indicator was off
            return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED), Message.TYPE_FATAL);
        }


    }

    /**
     * This method is for adjustment of Account if it is contracts and grants
     * 
     * @param account
     */
    private void adjustAccountIfContractsAndGrants(Account account) {
        if (account.isForContractsAndGrants() && (!account.isAccountClosedIndicator())) {

            String daysOffset = SpringContext.getBean(ParameterService.class).getParameterValue(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
            int daysOffsetInt = 3 * 30; // default to 90 days (approximately 3 months)

            if (daysOffset.trim().length() > 0) {

                daysOffsetInt = new Integer(daysOffset).intValue();
            }

            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(account.getAccountExpirationDate().getTime());
            tempCal.add(Calendar.DAY_OF_MONTH, daysOffsetInt);
            account.setAccountExpirationDate(new Timestamp(tempCal.getTimeInMillis()));
        }
        return;
    }

    /**
     * This method is checking whether or not the account is expired
     * 
     * @param account
     * @param runCalendar
     * @return
     */
    private boolean isExpired(Account account, Calendar runCalendar) {

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(account.getAccountExpirationDate().getTime());

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDoy = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDoy = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDoy < runDoy);
    }

    /**
     * This method changes account to suspenseAccount
     * 
     * @param workingEntry
     */
    private void useSuspenseAccount(LaborOriginEntry workingEntry) {

        // Get suspense account
        String suspenseAccountNumber = SpringContext.getBean(ParameterService.class).getParameterValue(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_NUMBER);
        String suspenseCOAcode = SpringContext.getBean(ParameterService.class).getParameterValue(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_CHART); 
        Account account = accountService.getByPrimaryId(suspenseCOAcode, suspenseAccountNumber);

        workingEntry.setAccount(account);
        workingEntry.setAccountNumber(suspenseAccountNumber);
        workingEntry.setChartOfAccountsCode(suspenseCOAcode);


    }

    /**
     * This method is for validation of Employee Id
     * 
     * @param laborOriginEntry
     * @param laborWorkingEntry
     * @param universityRunDate
     * @return
     */
    private Message validateEmplId(LaborOriginEntry laborOriginEntry, LaborOriginEntry laborWorkingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateEmplId() started");
        if (laborOriginEntry.getEmplid() == null || StringUtils.trimWhitespace(laborOriginEntry.getEmplid()).equals("")) {
            return new Message(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_EMPLID_NOT_BE_NULL), Message.TYPE_FATAL);
        }

        return null;
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


}