/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.dao.OptionsDao;
import org.kuali.core.exceptions.ApplicationParameterException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.batch.sufficientFunds.SufficientFundsReport;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.service.SufficientFundRebuildService;
import org.kuali.module.gl.service.SufficientFundsRebuilderService;
import org.kuali.module.gl.util.Summary;

/**
 * @author Anthony Potts
 */

public class SufficientFundsRebuilderServiceImpl implements SufficientFundsRebuilderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsRebuilderServiceImpl.class);

    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
    private BalanceDao balanceDao;
    private SufficientFundBalancesDao sufficientFundBalancesDao;
    private SufficientFundRebuildService sufficientFundRebuildService;
    private OptionsDao optionsDao;
    private SufficientFundsReport sufficientFundsReportService;
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
    // private int sfblRecordsDeletedCount;
    private int sfblInsertedCount;
    private int sfblUpdatedCount;
    private int warningCount;

    private SufficientFundBalances currentSfbl = null;

    public SufficientFundsRebuilderServiceImpl() {
        super();
    }

    private Integer getFiscalYear() {
        String val;
        try {
            val = kualiConfigurationService.getApplicationParameterValue("fis_sf_rebuild.sh", "UNIV_FISCAL_YR");
        }
        catch (ApplicationParameterException e) {
            LOG.error("getFiscalYear() Unable to get UNIV_FISCAL_YR from kualiConfigurationService");
            throw new RuntimeException("Unable to get fiscal year from kualiConfigurationService", e);
        }

        int yr = Integer.parseInt(val);
        return new Integer(yr);
    }

    public void rebuildSufficientFunds() { // driver
        LOG.debug("beginning sufficient funds rebuild process");

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

            if ((!"A".equals(sfrb.getAccountFinancialObjectTypeCode())) && (!"O".equals(sfrb.getAccountFinancialObjectTypeCode()))) {
                ++sfrbRecordsReadCount;
                transactionErrors = new ArrayList();
                addTransactionError("ACCOUNT/FINANCIAL OBJECT TYPE CODE MUST='A' OR 'O'");
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
        // reportSummary.add(new Summary(5, "SFBL records deleted", new Integer(sfblRecordsDeletedCount)));
        reportSummary.add(new Summary(6, "SFBL records added", new Integer(sfblInsertedCount)));
        reportSummary.add(new Summary(7, "SFBL records updated", new Integer(sfblUpdatedCount)));
        sufficientFundsReportService.generateReport(batchError, reportSummary, runDate, 0);
    }

    private void initService() {
        batchError = new HashMap();
        reportSummary = new ArrayList();

        runDate = new Date(dateTimeService.getCurrentDate().getTime());

        options = optionsDao.getByPrimaryId(universityFiscalYear);

        if (options == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }
    }

    /**
     * Given an O SF rebuild type, it will look up all of the matching balances in the table and add each account it finds as an A
     * SF rebuild type.
     * 
     * @param sfrb
     */
    private void convertOtypeToAtypes(SufficientFundRebuild sfrb) {
        ++sfrbRecordsConvertedCount;
        Collection fundBalances = sufficientFundBalancesDao.getByObjectCode(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());

        for (Iterator fundBalancesIter = fundBalances.iterator(); fundBalancesIter.hasNext();) {
            SufficientFundBalances sfbl = (SufficientFundBalances) fundBalancesIter.next();

            SufficientFundRebuild altSfrb = sufficientFundRebuildService.get(sfbl.getChartOfAccountsCode(), "A", sfbl.getAccountNumber());
            if (altSfrb == null) {
                altSfrb = new SufficientFundRebuild();
                altSfrb.setAccountFinancialObjectTypeCode("A");
                altSfrb.setAccountNumberFinancialObjectCode(sfbl.getAccountNumber());
                altSfrb.setChartOfAccountsCode(sfbl.getChartOfAccountsCode());
                sufficientFundRebuildService.save(altSfrb);
            }
        }
    }

    private void calculateSufficientFundsByAccount(SufficientFundRebuild sfrb) {
        Account sfrbAccount = accountService.getByPrimaryId(sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());

        if ((sfrbAccount.getAccountSufficientFundsCode() != null) && ("ALCOHN".indexOf(sfrbAccount.getAccountSufficientFundsCode()) > -1)) {
            ++sfrbRecordsDeletedCount;
            sufficientFundBalancesDao.deleteByAccountNumber(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrbAccount.getAccountNumber());

            if ((!sfrbAccount.isPendingAcctSufficientFundsIndicator()) || ("N".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode()))) {
                // nothing to do here, no errors either, just return
                return;
            }

            Iterator balancesIterator = balanceDao.findAccountBalances(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode(), sfrbAccount.getAccountSufficientFundsCode());

            if (balancesIterator == null) {
                addTransactionError("Balances not found in database for this COA/Account/fiscal year (" + universityFiscalYear + ")");
                ++warningCount;
                ++sfrbNotDeletedCount;
                return;
            }

            String currentFinObjectCd = "";

            while (balancesIterator.hasNext()) {
                Balance balance = (Balance) balancesIterator.next();

                String tempFinObjectCd = "";
                if (Constants.SF_TYPE_OBJECT.equals(sfrbAccount.getAccountSufficientFundsCode())) {
                    tempFinObjectCd = balance.getObjectCode();
                }
                else if (Constants.SF_TYPE_LEVEL.equals(sfrbAccount.getAccountSufficientFundsCode())) {
                    tempFinObjectCd = balance.getFinancialObject().getFinancialObjectLevelCode();
                }
                else if (Constants.SF_TYPE_CONSOLIDATION.equals(sfrbAccount.getAccountSufficientFundsCode())) {
                    tempFinObjectCd = balance.getFinancialObject().getFinancialObjectLevel().getFinancialConsolidationObjectCode();
                }
                else if (Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode()) || Constants.SF_TYPE_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode())) {
                    tempFinObjectCd = "    ";
                }

                if (!tempFinObjectCd.equals(currentFinObjectCd)) {
                    // we have a change or are on the last record, write out the data if there is any
                    currentFinObjectCd = tempFinObjectCd;

                    if (currentSfbl != null) {
                        // Only save if there is a balance in one of the 3 buckets
                        if ((currentSfbl.getAccountActualExpenditureAmt().compareTo(KualiDecimal.ZERO) != 0) || (currentSfbl.getAccountEncumbranceAmount().compareTo(KualiDecimal.ZERO) != 0) || (currentSfbl.getCurrentBudgetBalanceAmount().compareTo(KualiDecimal.ZERO) != 0)) {

                            sufficientFundBalancesDao.save(currentSfbl);
                            ++sfblInsertedCount;
                        }
                    }

                    currentSfbl = new SufficientFundBalances();
                    currentSfbl.setUniversityFiscalYear(universityFiscalYear);
                    currentSfbl.setChartOfAccountsCode(sfrb.getChartOfAccountsCode());
                    currentSfbl.setAccountNumber(sfrbAccount.getAccountNumber());
                    currentSfbl.setFinancialObjectCode(currentFinObjectCd);
                    currentSfbl.setAccountSufficientFundsCode(sfrbAccount.getAccountSufficientFundsCode());
                    currentSfbl.setAccountActualExpenditureAmt(new KualiDecimal(0.0));
                    currentSfbl.setAccountEncumbranceAmount(new KualiDecimal(0.0));
                    currentSfbl.setCurrentBudgetBalanceAmount(new KualiDecimal(0.0));
                }

                if ("CG".equalsIgnoreCase(sfrbAccount.getSubFundGroup().getFundGroupCode())) {
                    balance.setAccountLineAnnualBalanceAmount(balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()));
                }

                if (Constants.SF_TYPE_CASH_AT_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode())) {
                    processCash(sfrbAccount, balance);
                }
                else {
                    processObjectOrAccount(sfrbAccount, balance);
                }
            }

            // save the last one
            if (currentSfbl != null) {
                if ((currentSfbl.getAccountActualExpenditureAmt().compareTo(KualiDecimal.ZERO) != 0) || (currentSfbl.getAccountEncumbranceAmount().compareTo(KualiDecimal.ZERO) != 0) || (currentSfbl.getCurrentBudgetBalanceAmount().compareTo(KualiDecimal.ZERO) != 0)) {
                    sufficientFundBalancesDao.save(currentSfbl);
                    ++sfblInsertedCount;
                }
            }
        }
        else {
            addTransactionError("AccountSufficientFundsCode invalid for this Chart and Account");
            ++warningCount;
            ++sfrbNotDeletedCount;
            return;
        }
    }

    private void processObjectOrAccount(Account sfrbAccount, Balance balance) {
        if (options.getFinObjTypeExpenditureexpCd().equalsIgnoreCase(balance.getObjectTypeCode()) || "TE".equalsIgnoreCase(options.getFinObjTypeExpenditureexpCd()) || "ES".equalsIgnoreCase(options.getFinObjTypeExpenditureexpCd())) {
            if (options.getActualFinancialBalanceTypeCd().equalsIgnoreCase(balance.getBalanceTypeCode())) {
                processObjtAcctActual(balance);
            }
            else if (options.getExtrnlEncumFinBalanceTypCd().equalsIgnoreCase(balance.getBalanceTypeCode()) || options.getIntrnlEncumFinBalanceTypCd().equalsIgnoreCase(balance.getBalanceTypeCode()) || options.getPreencumbranceFinBalTypeCd().equalsIgnoreCase(balance.getBalanceTypeCode()) || "CE".equalsIgnoreCase(balance.getBalanceTypeCode())) {
                processObjtAcctEncmbrnc(balance);
            }
            else if (options.getBudgetCheckingBalanceTypeCd().equalsIgnoreCase(balance.getBalanceTypeCode())) {
                processObjtAcctBudget(balance);
            }
        }
    }

    private void processObjtAcctActual(Balance balance) {
        currentSfbl.setAccountActualExpenditureAmt(currentSfbl.getAccountActualExpenditureAmt().add(balance.getAccountLineAnnualBalanceAmount()));
    }

    private void processObjtAcctEncmbrnc(Balance balance) {
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    private void processObjtAcctBudget(Balance balance) {
        currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    private void processCash(Account sfrbAccount, Balance balance) {
        if (balance.getBalanceTypeCode().equalsIgnoreCase(options.getActualFinancialBalanceTypeCd())) {
            if (balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinancialCashObjectCode()) || balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinAccountsPayableObjectCode())) {
                processCashActual(sfrbAccount, balance);
            }
        }
        else if (balance.getBalanceTypeCode().equalsIgnoreCase(options.getExtrnlEncumFinBalanceTypCd()) || balance.getBalanceTypeCode().equalsIgnoreCase(options.getIntrnlEncumFinBalanceTypCd()) || balance.getBalanceTypeCode().equalsIgnoreCase(options.getPreencumbranceFinBalTypeCd()) || "CE".equalsIgnoreCase(balance.getBalanceTypeCode())) {
            if (balance.getObjectTypeCode().equalsIgnoreCase(options.getFinObjTypeExpenditureexpCd()) || balance.getObjectTypeCode().equalsIgnoreCase(options.getFinObjTypeExpendNotExpCode()) || "TE".equalsIgnoreCase(balance.getObjectTypeCode()) || "ES".equalsIgnoreCase(balance.getObjectTypeCode())) {
                processCashEncumbrance(balance);
            }
        }
    }

    private void processCashActual(Account sfrbAccount, Balance balance) {
        if (balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinancialCashObjectCode())) {
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
        }
        if (balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinAccountsPayableObjectCode())) {
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().subtract(balance.getAccountLineAnnualBalanceAmount()));
            currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().subtract(balance.getBeginningBalanceLineAmount()));
        }
    }

    private void processCashEncumbrance(Balance balance) {
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * @param errorMessage
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

    public void setSufficientFundsReport(SufficientFundsReport sfrs) {
        sufficientFundsReportService = sfrs;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

}
