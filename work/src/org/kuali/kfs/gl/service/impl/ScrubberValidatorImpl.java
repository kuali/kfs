/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.OriginationCode;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.OriginationCodeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.service.impl.scrubber.Message;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.StringHelper;
import org.springframework.util.StringUtils;


/**
 */

public class ScrubberValidatorImpl implements ScrubberValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberValidatorImpl.class);

    private KualiConfigurationService kualiConfigurationService;
    private PersistenceService persistenceService;
    private UniversityDateDao universityDateDao;
    private AccountService accountService;
    private OriginationCodeService originationCodeService;
    private DateTimeService dateTimeService;

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    private static String[] debitOrCredit = new String[] { Constants.GL_DEBIT_CODE, Constants.GL_CREDIT_CODE };
    private static String[] continuationAccountBypassOriginationCodes = new String[] { "EU", "PL" };
    private static String[] continuationAccountBypassBalanceTypeCodes = new String[] { "EX", "IE", "PE" };
    private static String[] continuationAccountBypassDocumentTypeCodes = new String[] { "PREQ", "ACHC", "ACHD", "ACHR", "CHKC", "CHKD", "CHKR", "TOPS", "CD", "LOCR" };

    public ScrubberValidatorImpl() {
    }

    private static int count = 0;

    public void validateForInquiry(GeneralLedgerPendingEntry entry) {
        LOG.debug("validateForInquiry() started");

        UniversityDate today = null;

        if (entry.getUniversityFiscalYear() == null) {
            today = dateTimeService.getCurrentUniversityDate();
            entry.setUniversityFiscalYear(today.getUniversityFiscalYear());
        }

        if (entry.getUniversityFiscalPeriodCode() == null) {
            if (today == null) {
                today = dateTimeService.getCurrentUniversityDate();
            }
            entry.setUniversityFiscalPeriodCode(today.getUniversityFiscalAccountingPeriod());
        }

        if ((entry.getSubAccountNumber() == null) || (!StringUtils.hasText(entry.getSubAccountNumber()))) {
            entry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }
        if ((entry.getFinancialSubObjectCode() == null) || (!StringUtils.hasText(entry.getFinancialSubObjectCode()))) {
            entry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        }
        if ((entry.getProjectCode() == null) || (!StringUtils.hasText(entry.getProjectCode()))) {
            entry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        }
    }

    public List<Message> validateTransaction(OriginEntry originEntry, OriginEntry scrubbedEntry, UniversityDate universityRunDate) {
        LOG.debug("validateTransaction() started");

        List<Message> errors = new ArrayList<Message>();

        count++;
        if (count % 100 == 0) {
            System.out.println(count + " " + originEntry.getLine());
        }

        // The cobol checks fdoc_nbr, trn_ldgr_entr_desc, org_doc_nbr, org_reference_id, and fdoc_ref_nbr for characters less than
        // ascii 32 or '~'. If found, it replaces that character with a space and reports a warning. This code does the ~, but not
        // the
        // less than 32 part.
        if ((originEntry.getDocumentNumber() != null) && (originEntry.getDocumentNumber().indexOf("~") > -1)) {
            String d = originEntry.getDocumentNumber();
            scrubbedEntry.setDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getTransactionLedgerEntryDescription() != null) && (originEntry.getTransactionLedgerEntryDescription().indexOf("~") > -1)) {
            String d = originEntry.getTransactionLedgerEntryDescription();
            scrubbedEntry.setTransactionLedgerEntryDescription(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getOrganizationDocumentNumber() != null) && (originEntry.getOrganizationDocumentNumber().indexOf("~") > -1)) {
            String d = originEntry.getOrganizationDocumentNumber();
            scrubbedEntry.setOrganizationDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getOrganizationReferenceId() != null) && (originEntry.getOrganizationReferenceId().indexOf("~") > -1)) {
            String d = originEntry.getOrganizationReferenceId();
            scrubbedEntry.setOrganizationReferenceId(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }
        if ((originEntry.getReferenceFinancialDocumentNumber() != null) && (originEntry.getReferenceFinancialDocumentNumber().indexOf("~") > -1)) {
            String d = originEntry.getReferenceFinancialDocumentNumber();
            scrubbedEntry.setReferenceFinancialDocumentNumber(d.replaceAll("~", " "));
            errors.add(new Message("** INVALID CHARACTER EDITED", Message.TYPE_WARNING));
        }

        // It's important that this check come before the checks for object, sub-object and accountingPeriod
        // because this validation method will set the fiscal year and reload those three objects if the fiscal
        // year was invalid. This will also set originEntry.getOption and workingEntry.getOption. So, it's
        // probably a good idea to validate the fiscal year first thing.
        Message err = validateFiscalYear(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateBalanceType(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateTransactionDate(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateTransactionAmount(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateChart(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateAccount(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateSubAccount(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateProjectCode(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateDocumentType(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateOrigination(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateDocumentNumber(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateObjectCode(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        // If object code is invalid, we can't check the object type
        if (err == null) {
            err = validateObjectType(originEntry, scrubbedEntry);
            if (err != null) {
                errors.add(err);
            }
        }

        err = validateSubObjectCode(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateReferenceDocumentFields(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        err = validateUniversityFiscalPeriodCode(originEntry, scrubbedEntry, universityRunDate);
        if (err != null) {
            errors.add(err);
        }

        err = validateReversalDate(originEntry, scrubbedEntry);
        if (err != null) {
            errors.add(err);
        }

        if (errors.size() == 0) {
            persistenceService.retrieveNonKeyFields(originEntry);
            persistenceService.retrieveNonKeyFields(scrubbedEntry);
        }

        return errors;
    }

    /**
     * 
     * @param originEntry
     * @param workingEntry
     */
    private Message validateAccount(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateAccount() started");

        if (originEntry.getAccount() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNT_NOT_FOUND) + "(" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + ")", Message.TYPE_FATAL);
        }

        if (kualiConfigurationService.getApplicationParameterValue(Constants.ParameterGroups.SYSTEM, Constants.SystemGroupParameterNames.GL_ANNAL_CLOSING_DOC_TYPE).equals(originEntry.getFinancialDocumentTypeCode())) {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            workingEntry.setAccount(originEntry.getAccount());
            return null;
        }

        Account account = originEntry.getAccount();

        if ((account.getAccountExpirationDate() == null) && !account.isAccountClosedIndicator()) {
            // account is neither closed nor expired
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            workingEntry.setAccount(originEntry.getAccount());
            return null;
        }

        // Has an expiration date or is closed
        if ((org.apache.commons.lang.StringUtils.isNumeric(originEntry.getFinancialSystemOriginationCode()) || ObjectHelper.isOneOf(originEntry.getFinancialSystemOriginationCode(), continuationAccountBypassOriginationCodes)) && account.isAccountClosedIndicator()) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_CANNOT_HAVE_CLOSED_ACCOUNT) + " (" + originEntry.getAccount().getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + ")", Message.TYPE_FATAL);
        }

        if ((org.apache.commons.lang.StringUtils.isNumeric(originEntry.getFinancialSystemOriginationCode()) || 
                ObjectHelper.isOneOf(originEntry.getFinancialSystemOriginationCode(), continuationAccountBypassOriginationCodes) || 
                ObjectHelper.isOneOf(originEntry.getFinancialBalanceTypeCode(), continuationAccountBypassBalanceTypeCodes) || 
                ObjectHelper.isOneOf(originEntry.getFinancialDocumentTypeCode().trim(), continuationAccountBypassDocumentTypeCodes)) && 
                !account.isAccountClosedIndicator()) {
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            workingEntry.setAccount(originEntry.getAccount());
            return null;
        }

        Calendar today = Calendar.getInstance();
        today.setTime(universityRunDate.getUniversityDate());

        adjustAccountIfContractsAndGrants(account);

        if (isExpired(account, today) || account.isAccountClosedIndicator()) {
            Message error = continuationAccountLogic(originEntry, workingEntry, today);
            if (error != null) {
                return error;
            }
        }

        workingEntry.setAccountNumber(originEntry.getAccountNumber());
        workingEntry.setAccount(originEntry.getAccount());
        return null;
    }


    private Message continuationAccountLogic(OriginEntry originEntry, OriginEntry workingEntry, Calendar today) {

        List checkedAccountNumbers = new ArrayList();

        Account account = null;

        String chartCode = originEntry.getAccount().getContinuationFinChrtOfAcctCd();
        String accountNumber = originEntry.getAccount().getContinuationAccountNumber();

        for (int i = 0; i < 10; ++i) {
            if (checkedAccountNumbers.contains(chartCode + accountNumber)) {
                // Something is really wrong with the data because this account has already been evaluated.
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CIRCULAR_DEPENDENCY_IN_CONTINUATION_ACCOUNT_LOGIC), Message.TYPE_FATAL);
            }

            if ((chartCode == null) || (accountNumber == null)) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND), Message.TYPE_FATAL);
            }

            // Lookup the account
            account = accountService.getByPrimaryId(chartCode, accountNumber);
            if (null == account) {
                // account not found
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_NOT_FOUND), Message.TYPE_FATAL);
            }
            else {
                // the account exists
                if (account.getAccountExpirationDate() == null) {
                    // No expiration date
                    workingEntry.setAccount(account);
                    workingEntry.setAccountNumber(accountNumber);
                    workingEntry.setChartOfAccountsCode(chartCode);

                    workingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_AUTO_FORWARD) + " " + originEntry.getChartOfAccountsCode() + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
                    return new Message(kualiConfigurationService.getPropertyString(KeyConstants.MSG_ACCOUNT_CLOSED_TO) + " " + workingEntry.getChartOfAccountsCode() + workingEntry.getAccountNumber(), Message.TYPE_WARNING);
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
                        workingEntry.setAccount(account);
                        workingEntry.setAccountNumber(accountNumber);
                        workingEntry.setChartOfAccountsCode(chartCode);

                        workingEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_AUTO_FORWARD) + originEntry.getChartOfAccountsCode() + originEntry.getAccountNumber() + originEntry.getTransactionLedgerEntryDescription());
                        return new Message(kualiConfigurationService.getPropertyString(KeyConstants.MSG_ACCOUNT_CLOSED_TO) + workingEntry.getChartOfAccountsCode() + workingEntry.getAccountNumber(), Message.TYPE_WARNING);
                    }
                }
            }
        }

        // We failed to find a valid continuation account.
        return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CONTINUATION_ACCOUNT_LIMIT_REACHED), Message.TYPE_FATAL);
    }

    private void adjustAccountIfContractsAndGrants(Account account) {
        if (account.isInCg() && (!account.isAccountClosedIndicator())) {
            Calendar tempCal = Calendar.getInstance();
            tempCal.setTimeInMillis(account.getAccountExpirationDate().getTime());
            tempCal.add(Calendar.MONTH, 3); // TODO: make this configurable
            account.setAccountExpirationDate(new Timestamp(tempCal.getTimeInMillis()));
        }
        return;
    }

    private Message validateReversalDate(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateReversalDate() started");

        if (originEntry.getFinancialDocumentReversalDate() != null) {
            UniversityDate universityDate = universityDateDao.getByPrimaryKey(originEntry.getFinancialDocumentReversalDate());
            if (universityDate == null) {
                Date reversalDate = originEntry.getFinancialDocumentReversalDate();
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REVERSAL_DATE_NOT_FOUND) + "(" + format.format(reversalDate) + ")", Message.TYPE_FATAL);
            }
            else {
                workingEntry.setFinancialDocumentReversalDate(originEntry.getFinancialDocumentReversalDate());
            }
        }
        return null;
    }

    private Message validateSubAccount(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateSubAccount() started");

        // If the sub account number is empty, set it to dashes.
        // Otherwise set the workingEntry sub account number to the
        // sub account number of the input origin entry.
        if (StringUtils.hasText(originEntry.getSubAccountNumber())) {
            // sub account IS specified
            if (!Constants.DASHES_SUB_ACCOUNT_NUMBER.equals(originEntry.getSubAccountNumber())) {
                if (originEntry.getSubAccount() == null) {
                    // sub account is not valid
                    return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_FOUND) + "(" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" + originEntry.getSubAccountNumber() + ")", Message.TYPE_FATAL);
                }
                else {
                    // sub account IS valid
                    if (originEntry.getSubAccount().isSubAccountActiveIndicator()) {
                        // sub account IS active
                        workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
                        workingEntry.setSubAccount(originEntry.getSubAccount());
                    }
                    else {
                        // sub account IS NOT active
                        if (kualiConfigurationService.getApplicationParameterValue(Constants.ParameterGroups.SYSTEM, Constants.SystemGroupParameterNames.GL_ANNAL_CLOSING_DOC_TYPE).equals(originEntry.getFinancialDocumentTypeCode())) {
                            // document IS annual closing
                            workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
                            workingEntry.setSubAccount(originEntry.getSubAccount());
                        }
                        else {
                            // document is NOT annual closing
                            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_SUB_ACCOUNT_NOT_ACTIVE) + "(" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getAccountNumber() + "-" + originEntry.getSubAccountNumber() + ")", Message.TYPE_FATAL);
                        }
                    }
                }
            }
            else {
                // the sub account is dashes
                workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
                workingEntry.setSubAccount(null);
            }
        }
        else {
            // No sub account is specified.
            workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
            workingEntry.setSubAccount(null);
        }
        return null;
    }

    private Message validateProjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateProjectCode() started");

        if (StringUtils.hasText(originEntry.getProjectCode()) && !Constants.DASHES_PROJECT_CODE.equals(originEntry.getProjectCode())) {
            if (originEntry.getProject() == null) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_PROJECT_CODE_NOT_FOUND) + " (" + originEntry.getProjectCode() + ")", Message.TYPE_FATAL);
            }
            else {
                if (originEntry.getProject().isActive()) {
                    workingEntry.setProjectCode(originEntry.getProjectCode());
                    workingEntry.setProject(originEntry.getProject());
                }
                else {
                    return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_PROJECT_CODE_MUST_BE_ACTIVE) + " (" + originEntry.getProjectCode() + ")", Message.TYPE_FATAL);
                }
            }
        }
        else {
            workingEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        }

        return null;
    }

    private Message validateFiscalYear(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateFiscalYear() started");

        if ((originEntry.getUniversityFiscalYear() == null) || (originEntry.getUniversityFiscalYear().intValue() == 0)) {
            originEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());
            workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());

            // Retrieve these objects because the fiscal year is the primary key for them
            persistenceService.retrieveReferenceObject(originEntry, PropertyConstants.FINANCIAL_SUB_OBJECT);
            persistenceService.retrieveReferenceObject(originEntry, PropertyConstants.FINANCIAL_OBJECT);
            persistenceService.retrieveReferenceObject(originEntry, PropertyConstants.ACCOUNTING_PERIOD);
            persistenceService.retrieveReferenceObject(originEntry, PropertyConstants.OPTION);
        }
        else {
            workingEntry.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
            workingEntry.setOption(originEntry.getOption());
        }

        if (originEntry.getOption() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_FISCAL_YR_NOT_FOUND) + " (" + originEntry.getUniversityFiscalYear() + ")", Message.TYPE_FATAL);
        }
        return null;
    }

    private Message validateTransactionDate(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateTransactionDate() started");

        if (originEntry.getTransactionDate() == null) {
            Date transactionDate = new Date(universityRunDate.getUniversityDate().getTime());

            // Set the transaction date to the run date.
            originEntry.setTransactionDate(transactionDate);
            workingEntry.setTransactionDate(transactionDate);
        }
        else {
            workingEntry.setTransactionDate(originEntry.getTransactionDate());
        }

        // Next, we have to validate the transaction date against the university date table.
        if (universityDateDao.getByPrimaryKey(originEntry.getTransactionDate()) == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_TRANSACTION_DATE_INVALID) + " (" + originEntry.getTransactionDate() + ")", Message.TYPE_FATAL);
        }
        return null;
    }

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    private Message validateDocumentType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateDocumentType() started");

        if (originEntry.getDocumentType() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_TYPE_NOT_FOUND) + " (" + originEntry.getFinancialDocumentTypeCode() + ")", Message.TYPE_FATAL);
        }

        workingEntry.setFinancialDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
        workingEntry.setDocumentType(originEntry.getDocumentType());
        return null;
    }

    private Message validateOrigination(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateOrigination() started");

        if (StringUtils.hasText(originEntry.getFinancialSystemOriginationCode())) {
            if (originEntry.getOrigination() == null) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND) + " (" + originEntry.getFinancialSystemOriginationCode() + ")", Message.TYPE_FATAL);
            }
            else {
                workingEntry.setFinancialSystemOriginationCode(originEntry.getFinancialSystemOriginationCode());
                workingEntry.setOrigination(originEntry.getOrigination());
            }
        }
        else {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ORIGIN_CODE_NOT_FOUND) + " (" + originEntry.getFinancialSystemOriginationCode() + ")", Message.TYPE_FATAL);
        }
        return null;
    }

    private Message validateDocumentNumber(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateDocumentNumber() started");

        if (!StringUtils.hasText(originEntry.getDocumentNumber())) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DOCUMENT_NUMBER_REQUIRED), Message.TYPE_FATAL);
        }
        else {
            workingEntry.setDocumentNumber(originEntry.getDocumentNumber());
            return null;
        }
    }

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    private Message validateChart(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateChart() started");

        if (originEntry.getChart() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_FOUND) + " (" + originEntry.getChartOfAccountsCode() + ")", Message.TYPE_FATAL);
        }

        if (!originEntry.getChart().isFinChartOfAccountActiveIndicator()) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_CHART_NOT_ACTIVE) + " (" + originEntry.getChartOfAccountsCode() + ")", Message.TYPE_FATAL);
        }

        workingEntry.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        workingEntry.setChart(originEntry.getChart());
        return null;
    }

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    private Message validateObjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateObjectCode() started");

        if (!StringUtils.hasText(originEntry.getFinancialObjectCode())) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_EMPTY), Message.TYPE_FATAL);
        }

        // We're checking the object code based on the year & chart from the working entry.
        workingEntry.setFinancialObjectCode(originEntry.getFinancialObjectCode());
        persistenceService.retrieveReferenceObject(workingEntry, "financialObject");

        if (workingEntry.getFinancialObject() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND) + " (" + originEntry.getUniversityFiscalYear() + "-" + originEntry.getChartOfAccountsCode() + "-" + originEntry.getFinancialObjectCode() + ")", Message.TYPE_FATAL);
        }

        return null;
    }

    /**
     * We're assuming that object code has been validated first
     * 
     * @see org.kuali.module.gl.service.ScrubberValidator#validateObjectType(org.kuali.module.gl.bo.OriginEntry,
     *      org.kuali.module.gl.bo.OriginEntry)
     */
    private Message validateObjectType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateObjectType() started");

        if (!StringUtils.hasText(originEntry.getFinancialObjectTypeCode())) {
            // If not specified, use the object type from the object code
            workingEntry.setFinancialObjectTypeCode(workingEntry.getFinancialObject().getFinancialObjectTypeCode());
            workingEntry.setObjectType(workingEntry.getFinancialObject().getFinancialObjectType());
        }
        else {
            workingEntry.setFinancialObjectTypeCode(originEntry.getFinancialObjectTypeCode());
            workingEntry.setObjectType(originEntry.getObjectType());
        }

        if (workingEntry.getObjectType() == null) {
            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_TYPE_NOT_FOUND) + " (" + originEntry.getFinancialObjectTypeCode() + ")", Message.TYPE_FATAL);
        }
        return null;
    }

    private Message validateSubObjectCode(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateFinancialSubObjectCode() started");

        if (!StringUtils.hasText(originEntry.getFinancialSubObjectCode())) {
            workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            workingEntry.setFinancialSubObject(null);
            return null;
        }

        if (!Constants.DASHES_SUB_OBJECT_CODE.equals(originEntry.getFinancialSubObjectCode())) {
            if (originEntry.getFinancialSubObject() != null) {
                // Exists
                if (!originEntry.getFinancialSubObject().isFinancialSubObjectActiveIndicator()) {
                    // if NOT active, set it to dashes
                    workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
                    workingEntry.setFinancialSubObject(null);
                    return null;
                }
            }
            else {
                // Doesn't exist
                workingEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
                workingEntry.setFinancialSubObject(null);
                return null;
            }
        }
        workingEntry.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode());
        workingEntry.setFinancialSubObject(originEntry.getFinancialSubObject());
        return null;
    }

    private Message validateBalanceType(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateBalanceType() started");

        if (StringUtils.hasText(originEntry.getFinancialBalanceTypeCode())) {
            // balance type IS NOT empty
            if (originEntry.getBalanceType() == null) {
                // balance type IS NOT valid
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_BALANCE_TYPE_NOT_FOUND) + " (" + originEntry.getFinancialBalanceTypeCode() + ")", Message.TYPE_FATAL);
            }
            else {
                // balance type IS valid
                if (originEntry.getBalanceType().isFinancialOffsetGenerationIndicator()) {
                    // entry IS an offset
                    if (originEntry.getTransactionLedgerEntryAmount().isNegative()) {
                        // it's an INVALID non-budget transaction
                        return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_TRANS_CANNOT_BE_NEGATIVE_IF_OFFSET), Message.TYPE_FATAL);
                    }
                    else {
                        // it's a VALID non-budget transaction
                        if (!originEntry.isCredit() && !originEntry.isDebit()) { // entries requiring an offset must be either a
                            // debit or a credit
                            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_D_OR_C) + " (" + originEntry.getTransactionDebitCreditCode() + ")", Message.TYPE_FATAL);
                        }
                        else {
                            workingEntry.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
                            workingEntry.setBalanceType(originEntry.getBalanceType());
                        }
                    }
                }
                else {
                    // entry IS NOT an offset, means it's a budget transaction
                    if (StringUtils.hasText(originEntry.getTransactionDebitCreditCode())) {
                        return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_EMPTY) + " (" + originEntry.getTransactionDebitCreditCode() + ")", Message.TYPE_FATAL);
                    }
                    else {
                        if (originEntry.isCredit() || originEntry.isDebit()) {
                            // budget transactions must be neither debit nor credit
                            return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DC_INDICATOR_MUST_BE_NEITHER_D_NOR_C) + " (" + originEntry.getTransactionDebitCreditCode() + ")", Message.TYPE_FATAL);
                        }
                        else {
                            // it's a valid budget transaction
                            workingEntry.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
                            workingEntry.setBalanceType(originEntry.getBalanceType());
                        }
                    }
                }
            }
        }
        else {
            // balance type IS empty. We can't set it if the year isn't set
            if (workingEntry.getOption() != null) {
                workingEntry.setFinancialBalanceTypeCode(workingEntry.getOption().getActualFinancialBalanceTypeCd());
                workingEntry.setBalanceType(workingEntry.getOption().getActualFinancialBalanceType());
            }
            else {
                return new Message("Unable to set balance type code when year is unknown: " + workingEntry.getUniversityFiscalYear(), Message.TYPE_FATAL);
            }
        }
        return null;
    }

    private Message validateUniversityFiscalPeriodCode(OriginEntry originEntry, OriginEntry workingEntry, UniversityDate universityRunDate) {
        LOG.debug("validateUniversityFiscalPeriodCode() started");

        if (!StringUtils.hasText(originEntry.getUniversityFiscalPeriodCode())) {
            workingEntry.setUniversityFiscalPeriodCode(universityRunDate.getUniversityFiscalAccountingPeriod());
            workingEntry.setUniversityFiscalYear(universityRunDate.getUniversityFiscalYear());

            // Retrieve these objects because the fiscal year is the primary key for them
            persistenceService.retrieveReferenceObject(originEntry, "financialSubObject");
            persistenceService.retrieveReferenceObject(originEntry, "financialObject");
            persistenceService.retrieveReferenceObject(originEntry, "accountingPeriod");
            persistenceService.retrieveReferenceObject(originEntry, "option");
        }
        else {
            if (originEntry.getAccountingPeriod() == null) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ACCOUNTING_PERIOD_NOT_FOUND) + " (" + originEntry.getUniversityFiscalPeriodCode() + ")", Message.TYPE_FATAL);
            }
            else if (Constants.ACCOUNTING_PERIOD_STATUS_CLOSED.equals(originEntry.getAccountingPeriod().getUniversityFiscalPeriodStatusCode())) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_FISCAL_PERIOD_CLOSED) + " (" + originEntry.getUniversityFiscalPeriodCode() + ")", Message.TYPE_FATAL);
            }

            workingEntry.setUniversityFiscalPeriodCode(originEntry.getUniversityFiscalPeriodCode());
        }

        return null;
    }

    /**
     * If the encumbrance update code = R, ref doc number must exist, ref doc type must be valid and ref origin code must be valid.
     * If encumbrance update code is not R, and ref doc number is empty, make sure ref doc number, ref doc type and ref origin code
     * are null. If encumbrance update code is not R and the ref doc number has a value, ref doc type must be valid and ref origin
     * code must be valid.
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    private Message validateReferenceDocumentFields(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateReferenceDocument() started");

        // 3148 of cobol

        boolean editReference = true;
        if (!StringUtils.hasText(originEntry.getReferenceFinancialDocumentNumber())) {
            workingEntry.setReferenceFinancialDocumentNumber(null);
            workingEntry.setReferenceFinancialDocumentTypeCode(null);
            workingEntry.setReferenceFinancialSystemOriginationCode(null);

            if (Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode())) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REF_DOC_NOT_BE_SPACE), Message.TYPE_FATAL);
            }
        }
        else {
            workingEntry.setReferenceFinancialDocumentNumber(originEntry.getReferenceFinancialDocumentNumber());

            // Validate reference document type
            if (originEntry.getReferenceDocumentType() != null) {
                workingEntry.setReferenceFinancialDocumentTypeCode(originEntry.getReferenceFinancialDocumentTypeCode());
            }
            else {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_DOCUMENT_TYPE_NOT_FOUND) + " (" + originEntry.getReferenceFinancialDocumentTypeCode() + ")", Message.TYPE_FATAL);
            }

            // Validate reference origin code
            OriginationCode oc = originationCodeService.getByPrimaryKey(originEntry.getReferenceFinancialSystemOriginationCode());
            if (oc != null) {
                workingEntry.setReferenceFinancialSystemOriginationCode(originEntry.getReferenceFinancialSystemOriginationCode());
            }
            else {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_REFERENCE_ORIGINATION_CODE_NOT_FOUND) + " (" + originEntry.getReferenceFinancialSystemOriginationCode() + ")", Message.TYPE_FATAL);
            }
        }

        if ((workingEntry.getBalanceType() == null) || (workingEntry.getObjectType() == null)) {
            // We are unable to check this because the balance type or object type is invalid.
            // It would be nice if we could still validate the entry, but we can't.
            return null;
        }

        if (workingEntry.getBalanceType().isFinBalanceTypeEncumIndicator() && !workingEntry.getObjectType().isFundBalanceIndicator()) {
            if ((Constants.ENCUMB_UPDT_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode())) || (Constants.ENCUMB_UPDT_NO_ENCUMBRANCE_CD.equals(originEntry.getTransactionEncumbranceUpdateCode())) || (Constants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(originEntry.getTransactionEncumbranceUpdateCode()))) {
                workingEntry.setTransactionEncumbranceUpdateCode(originEntry.getTransactionEncumbranceUpdateCode());
            }
            else {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_ENC_UPDATE_CODE_NOT_DRN) + " (" + originEntry.getTransactionEncumbranceUpdateCode() + ")", Message.TYPE_FATAL);
            }
        }
        else {
            workingEntry.setTransactionEncumbranceUpdateCode(null);
        }
        return null;
    }

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    private Message validateTransactionAmount(OriginEntry originEntry, OriginEntry workingEntry) {
        LOG.debug("validateTransactionAmount() started");

        KualiDecimal amount = originEntry.getTransactionLedgerEntryAmount();
        if (originEntry.getBalanceType() == null) {
            // We can't validate the amount without a balance type code
            return null;
        }

        if (originEntry.getBalanceType().isFinancialOffsetGenerationIndicator()) {
            if (amount.isPositive() || amount.isZero()) {
                workingEntry.setTransactionLedgerEntryAmount(originEntry.getTransactionLedgerEntryAmount());
            }
            else {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NEGATIVE_AMOUNT) + " (" + amount.toString() + ")", Message.TYPE_FATAL);
            }
            if (StringHelper.isEmpty(originEntry.getTransactionDebitCreditCode())) {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C) + " (" + originEntry.getTransactionDebitCreditCode() + ")", Message.TYPE_FATAL);
            }
            if (ObjectHelper.isOneOf(originEntry.getTransactionDebitCreditCode(), debitOrCredit)) {
                workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            }
            else {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_NEITHER_D_NOR_C) + " (" + originEntry.getTransactionDebitCreditCode() + ")", Message.TYPE_FATAL);
            }
        }
        else {
            if ((originEntry.getTransactionDebitCreditCode() == null) || (" ".equals(originEntry.getTransactionDebitCreditCode()))) {
                workingEntry.setTransactionDebitCreditCode(Constants.GL_BUDGET_CODE);
            }
            else {
                return new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_DEBIT_CREDIT_INDICATOR_MUST_BE_SPACE) + " (" + originEntry.getTransactionDebitCreditCode() + ")", Message.TYPE_FATAL);
            }
        }
        return null;
    }

    private boolean isExpired(Account account, Calendar runCalendar) {

        Calendar expirationDate = Calendar.getInstance();
        expirationDate.setTimeInMillis(account.getAccountExpirationDate().getTime());

        int expirationYear = expirationDate.get(Calendar.YEAR);
        int runYear = runCalendar.get(Calendar.YEAR);
        int expirationDoy = expirationDate.get(Calendar.DAY_OF_YEAR);
        int runDoy = runCalendar.get(Calendar.DAY_OF_YEAR);

        return (expirationYear < runYear) || (expirationYear == runYear && expirationDoy < runDoy);
    }

    public void setUniversityDateDao(UniversityDateDao udd) {
        universityDateDao = udd;
    }

    public void setKualiConfigurationService(KualiConfigurationService service) {
        kualiConfigurationService = service;
    }

    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }

    public void setAccountService(AccountService as) {
        accountService = as;
    }

    public void setOriginationCodeService(OriginationCodeService ocs) {
        originationCodeService = ocs;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }


}
