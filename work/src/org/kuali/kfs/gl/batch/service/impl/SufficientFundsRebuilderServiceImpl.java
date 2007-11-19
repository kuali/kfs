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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.dao.OptionsDao;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.SufficientFundRebuildService;
import org.kuali.module.gl.service.SufficientFundsRebuilderService;
import org.kuali.module.gl.service.SufficientFundsService;
import org.kuali.module.gl.util.Summary;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of SufficientFundsRebuilderService
 */
@Transactional
public class SufficientFundsRebuilderServiceImpl implements SufficientFundsRebuilderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsRebuilderServiceImpl.class);

    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
    private BalanceDao balanceDao;
    private SufficientFundBalancesDao sufficientFundBalancesDao;
    private SufficientFundsService sufficientFundsService;
    private SufficientFundRebuildService sufficientFundRebuildService;
    private OptionsDao optionsDao;
    private ReportService reportService;
    private AccountService accountService;

    private Date runDate;
    private Options options;

    Map batchError;
    List reportSummary;
    List transactionErrors;

    private Integer universityFiscalYear;
    private int sfrbRecordsConvertedCount;
    private int sfrbRecordsReadCount;
    private int sfrbRecordsDeletedCount;
    private int sfrbNotDeletedCount;
    private int sfblInsertedCount;
    private int sfblUpdatedCount;
    private int warningCount;

    private SufficientFundBalances currentSfbl = null;

    /**
     * Constructs a SufficientFundsRebuilderServiceImpl instance
     */
    public SufficientFundsRebuilderServiceImpl() {
        super();
    }

    /**
     * Returns the fiscal year, set in a parameter, of sufficient funds to rebuild
     * 
     * @return the fiscal year
     */
    private Integer getFiscalYear() {
        String val = SpringContext.getBean(ParameterService.class).getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM);
        int yr = Integer.parseInt(val);
        return new Integer(yr);
    }

    /**
     * Rebuilds all necessary sufficient funds balances.
     * @see org.kuali.module.gl.service.SufficientFundsRebuilderService#rebuildSufficientFunds()
     */
    public void rebuildSufficientFunds() { // driver
        LOG.debug("rebuildSufficientFunds() started");

        universityFiscalYear = getFiscalYear();
        initService();

        // Get all the O types and convert them to A types
        LOG.debug("rebuildSufficientFunds() Converting O types to A types");
        for (Iterator iter = sufficientFundRebuildService.getAllObjectEntries().iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();
            ++sfrbRecordsReadCount;

            transactionErrors = new ArrayList();

            convertOtypeToAtypes(sfrb);

            if (transactionErrors.size() > 0) {
                batchError.put(sfrb, transactionErrors);
            }
            else {
                sufficientFundRebuildService.delete(sfrb);
            }
        }

        // Get all the A types and process them
        LOG.debug("rebuildSufficientFunds() Calculating SF balances for all A types");
        for (Iterator iter = sufficientFundRebuildService.getAllAccountEntries().iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();
            ++sfrbRecordsReadCount;

            transactionErrors = new ArrayList();

            calculateSufficientFundsByAccount(sfrb);

            if (transactionErrors.size() > 0) {
                batchError.put(sfrb, transactionErrors);
            }

            sufficientFundRebuildService.delete(sfrb);

        }

        // Look at all the left over rows. There shouldn't be any left if all are O's and A's without error.
        // Write out error messages for any that aren't A or O
        LOG.debug("rebuildSufficientFunds() Handle any non-A and non-O types");
        for (Iterator iter = sufficientFundRebuildService.getAll().iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();

            if ((!KFSConstants.SF_TYPE_ACCOUNT.equals(sfrb.getAccountFinancialObjectTypeCode())) && (!KFSConstants.SF_TYPE_OBJECT.equals(sfrb.getAccountFinancialObjectTypeCode()))) {
                ++sfrbRecordsReadCount;
                transactionErrors = new ArrayList();
                addTransactionError(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_INVALID_SF_OBJECT_TYPE_CODE));
                ++warningCount;
                ++sfrbNotDeletedCount;
                batchError.put(sfrb, transactionErrors);
            }
        }

        // write out report and errors
        LOG.debug("rebuildSufficientFunds() Create report");
        reportSummary.add(new Summary(1, "SFRB records converted from Object to Account", new Integer(sfrbRecordsConvertedCount)));
        reportSummary.add(new Summary(2, "Post conversion SFRB records read", new Integer(sfrbRecordsReadCount)));
        reportSummary.add(new Summary(3, "SFRB records deleted", new Integer(sfrbRecordsDeletedCount)));
        reportSummary.add(new Summary(4, "SFRB records kept due to errors", new Integer(sfrbNotDeletedCount)));
        reportSummary.add(new Summary(6, "SFBL records added", new Integer(sfblInsertedCount)));
        reportSummary.add(new Summary(7, "SFBL records updated", new Integer(sfblUpdatedCount)));
        reportService.generateSufficientFundsReport(batchError, reportSummary, runDate, 0);
    }

    /**
     * Initializes the process at the beginning of a run.
     */
    private void initService() {
        batchError = new HashMap();
        reportSummary = new ArrayList();

        runDate = new Date(dateTimeService.getCurrentDate().getTime());

        options = optionsDao.getByPrimaryId(universityFiscalYear);

        if (options == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }
    }

    /**
     * Given an O SF rebuild type, it will look up all of the matching balances in the table and add each account it finds as an A
     * SF rebuild type.
     * 
     * @param sfrb the sufficient fund rebuild record to convert
     */
    private void convertOtypeToAtypes(SufficientFundRebuild sfrb) {
        ++sfrbRecordsConvertedCount;
        Collection fundBalances = sufficientFundBalancesDao.getByObjectCode(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());

        for (Iterator fundBalancesIter = fundBalances.iterator(); fundBalancesIter.hasNext();) {
            SufficientFundBalances sfbl = (SufficientFundBalances) fundBalancesIter.next();

            SufficientFundRebuild altSfrb = sufficientFundRebuildService.get(sfbl.getChartOfAccountsCode(), KFSConstants.SF_TYPE_ACCOUNT, sfbl.getAccountNumber());
            if (altSfrb == null) {
                altSfrb = new SufficientFundRebuild();
                altSfrb.setAccountFinancialObjectTypeCode(KFSConstants.SF_TYPE_ACCOUNT);
                altSfrb.setAccountNumberFinancialObjectCode(sfbl.getAccountNumber());
                altSfrb.setChartOfAccountsCode(sfbl.getChartOfAccountsCode());
                sufficientFundRebuildService.save(altSfrb);
            }
        }
    }

    /**
     * Updates sufficient funds balances for the given account
     * 
     * @param sfrb the sufficient fund rebuild record, with a chart and account number
     */
    private void calculateSufficientFundsByAccount(SufficientFundRebuild sfrb) {
        Account sfrbAccount = accountService.getByPrimaryId(sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());

        if ((sfrbAccount.getAccountSufficientFundsCode() != null) && (KFSConstants.SF_TYPE_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_CONSOLIDATION.equals(sfrbAccount.getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_LEVEL.equals(sfrbAccount.getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_OBJECT.equals(sfrbAccount.getAccountSufficientFundsCode()) || KFSConstants.SF_TYPE_NO_CHECKING.equals(sfrbAccount.getAccountSufficientFundsCode()))) {
            ++sfrbRecordsDeletedCount;
            sufficientFundBalancesDao.deleteByAccountNumber(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrbAccount.getAccountNumber());

            if ((!sfrbAccount.isPendingAcctSufficientFundsIndicator()) || (KFSConstants.SF_TYPE_NO_CHECKING.equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode()))) {
                // nothing to do here, no errors either, just return
                return;
            }

            Iterator balancesIterator = balanceDao.findAccountBalances(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode(), sfrbAccount.getAccountSufficientFundsCode());

            if (balancesIterator == null) {
                addTransactionError(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_BALANCE_NOT_FOUND_FOR) + universityFiscalYear + ")");
                ++warningCount;
                ++sfrbNotDeletedCount;
                return;
            }

            String currentFinObjectCd = "";

            while (balancesIterator.hasNext()) {
                Balance balance = (Balance) balancesIterator.next();

                String tempFinObjectCd = sufficientFundsService.getSufficientFundsObjectCode(balance.getFinancialObject(), sfrbAccount.getAccountSufficientFundsCode());

                if (!tempFinObjectCd.equals(currentFinObjectCd)) {
                    // we have a change or are on the last record, write out the data if there is any
                    currentFinObjectCd = tempFinObjectCd;

                    if (currentSfbl != null && amountsAreNonZero(currentSfbl)) {
                        sufficientFundBalancesDao.save(currentSfbl);
                        ++sfblInsertedCount;
                    }

                    currentSfbl = new SufficientFundBalances();
                    currentSfbl.setUniversityFiscalYear(universityFiscalYear);
                    currentSfbl.setChartOfAccountsCode(sfrb.getChartOfAccountsCode());
                    currentSfbl.setAccountNumber(sfrbAccount.getAccountNumber());
                    currentSfbl.setFinancialObjectCode(currentFinObjectCd);
                    currentSfbl.setAccountSufficientFundsCode(sfrbAccount.getAccountSufficientFundsCode());
                    currentSfbl.setAccountActualExpenditureAmt(KualiDecimal.ZERO);
                    currentSfbl.setAccountEncumbranceAmount(KualiDecimal.ZERO);
                    currentSfbl.setCurrentBudgetBalanceAmount(KualiDecimal.ZERO);
                }

                if (sfrbAccount.isForContractsAndGrants()) {
                    balance.setAccountLineAnnualBalanceAmount(balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()));
                }

                if (KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode())) {
                    processCash(sfrbAccount, balance);
                }
                else {
                    processObjectOrAccount(sfrbAccount, balance);
                }
            }

            // save the last one
            if (currentSfbl != null && amountsAreNonZero(currentSfbl)) {
                sufficientFundBalancesDao.save(currentSfbl);
                ++sfblInsertedCount;
            }
        }
        else {
            addTransactionError(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_INVALID_ACCOUNT_SF_CODE_FOR));
            ++warningCount;
            ++sfrbNotDeletedCount;
            return;
        }
    }

    /**
     * Determines if all sums associated with a sufficient funds balance are zero
     * 
     * @param sfbl the sufficient funds balance to check
     * @return true if all sums in the balance are zero, false otherwise
     */
    private boolean amountsAreNonZero(SufficientFundBalances sfbl) {
        boolean zero = true;
        zero &= KualiDecimal.ZERO.equals(sfbl.getAccountActualExpenditureAmt());
        zero &= KualiDecimal.ZERO.equals(sfbl.getAccountEncumbranceAmount());
        zero &= KualiDecimal.ZERO.equals(sfbl.getCurrentBudgetBalanceAmount());
        return !zero;
    }

    /**
     * Determines how best to process the given balance
     * 
     * @param sfrbAccount the account of the current sufficient funds balance rebuild record
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processObjectOrAccount(Account sfrbAccount, Balance balance) {
        if (options.getFinObjTypeExpenditureexpCd().equals(balance.getObjectTypeCode()) || options.getFinObjTypeExpendNotExpCode().equals(balance.getObjectTypeCode()) || options.getFinObjTypeExpNotExpendCode().equals(balance.getObjectTypeCode()) || options.getFinancialObjectTypeTransferExpenseCd().equals(balance.getObjectTypeCode())) {
            if (options.getActualFinancialBalanceTypeCd().equals(balance.getBalanceTypeCode())) {
                processObjtAcctActual(balance);
            }
            else if (options.getExtrnlEncumFinBalanceTypCd().equals(balance.getBalanceTypeCode()) || options.getIntrnlEncumFinBalanceTypCd().equals(balance.getBalanceTypeCode()) || options.getPreencumbranceFinBalTypeCd().equals(balance.getBalanceTypeCode()) || options.getCostShareEncumbranceBalanceTypeCd().equals(balance.getBalanceTypeCode())) {
                processObjtAcctEncmbrnc(balance);
            }
            else if (options.getBudgetCheckingBalanceTypeCd().equals(balance.getBalanceTypeCode())) {
                processObjtAcctBudget(balance);
            }
        }
    }

    /**
     * Updates the current sufficient fund balance record with a non-cash actual balance
     * 
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processObjtAcctActual(Balance balance) {
        currentSfbl.setAccountActualExpenditureAmt(currentSfbl.getAccountActualExpenditureAmt().add(balance.getAccountLineAnnualBalanceAmount()));
    }

    /**
     * Updates the current sufficient fund balance record with a non-cash encumbrance balance
     * 
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processObjtAcctEncmbrnc(Balance balance) {
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * Updates the current sufficient fund balance record with a non-cash budget balance
     * 
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processObjtAcctBudget(Balance balance) {
        currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * Determines how best to process a cash balance
     * 
     * @param sfrbAccount the account of the current sufficient funds balance record
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processCash(Account sfrbAccount, Balance balance) {
        if (balance.getBalanceTypeCode().equals(options.getActualFinancialBalanceTypeCd())) {
            if (balance.getObjectCode().equals(sfrbAccount.getChartOfAccounts().getFinancialCashObjectCode()) || balance.getObjectCode().equals(sfrbAccount.getChartOfAccounts().getFinAccountsPayableObjectCode())) {
                processCashActual(sfrbAccount, balance);
            }
        }
        else if (balance.getBalanceTypeCode().equals(options.getExtrnlEncumFinBalanceTypCd()) || balance.getBalanceTypeCode().equals(options.getIntrnlEncumFinBalanceTypCd()) || balance.getBalanceTypeCode().equals(options.getPreencumbranceFinBalTypeCd()) || options.getCostShareEncumbranceBalanceTypeCd().equals(balance.getBalanceTypeCode())) {
            if (balance.getObjectTypeCode().equals(options.getFinObjTypeExpenditureexpCd()) || balance.getObjectTypeCode().equals(options.getFinObjTypeExpendNotExpCode()) || options.getFinancialObjectTypeTransferExpenseCd().equals(balance.getObjectTypeCode()) || options.getFinObjTypeExpNotExpendCode().equals(balance.getObjectTypeCode())) {
                processCashEncumbrance(balance);
            }
        }
    }

    /**
     * Updates the current sufficient fund balance record with a cash actual balance
     * 
     * @param sfrbAccount the account of the current sufficient funds balance record
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processCashActual(Account sfrbAccount, Balance balance) {
        if (balance.getObjectCode().equals(sfrbAccount.getChartOfAccounts().getFinancialCashObjectCode())) {
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
        }
        if (balance.getObjectCode().equals(sfrbAccount.getChartOfAccounts().getFinAccountsPayableObjectCode())) {
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().subtract(balance.getAccountLineAnnualBalanceAmount()));
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().subtract(balance.getBeginningBalanceLineAmount()));
        }
    }

    /**
     * Updates the current sufficient funds balance with a cash encumbrance balance
     * 
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    private void processCashEncumbrance(Balance balance) {
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * Adds an error message to this instance's List of error messages
     * @param errorMessage the error message to keep
     */
    private void addTransactionError(String errorMessage) {
        transactionErrors.add(errorMessage);
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    public void setSufficientFundBalancesDao(SufficientFundBalancesDao sufficientFundBalancesDao) {
        this.sufficientFundBalancesDao = sufficientFundBalancesDao;
    }

    public void setSufficientFundRebuildService(SufficientFundRebuildService sufficientFundRebuildService) {
        this.sufficientFundRebuildService = sufficientFundRebuildService;
    }

    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    public void setReportService(ReportService sfrs) {
        reportService = sfrs;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setSufficientFundsService(SufficientFundsService sfs) {
        sufficientFundsService = sfs;
    }
}
