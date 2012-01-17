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
package org.kuali.kfs.gl.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.SufficientFundsAccountUpdateStep;
import org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateService;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.SufficientFundBalances;
import org.kuali.kfs.gl.businessobject.SufficientFundRebuild;
import org.kuali.kfs.gl.dataaccess.BalanceDao;
import org.kuali.kfs.gl.dataaccess.SufficientFundBalancesDao;
import org.kuali.kfs.gl.dataaccess.SufficientFundRebuildDao;
import org.kuali.kfs.gl.service.SufficientFundsService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The default implementation of SufficientFundsAccountUpdateService
 */
@Transactional
public class SufficientFundsAccountUpdateServiceImpl implements SufficientFundsAccountUpdateService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsAccountUpdateServiceImpl.class);

    private DateTimeService dateTimeService;
    private ConfigurationService kualiConfigurationService;
    private BalanceDao balanceDao;
    private SufficientFundBalancesDao sufficientFundBalancesDao;
    private SufficientFundRebuildDao sufficientFundRebuildDao;
    private SufficientFundsService sufficientFundsService;
    private AccountService accountService;
    private ReportWriterService reportWriterService;
    private BusinessObjectService boService;

    private Date runDate;
    private SystemOptions options;

    Map batchError;
    List reportSummary;
    List<Message> transactionErrors;

    private Integer universityFiscalYear;
    private int sfrbRecordsConvertedCount = 0;
    private int sfrbRecordsReadCount = 0;
    private int sfrbRecordsDeletedCount = 0;
    private int sfrbNotDeletedCount = 0;
    private int sfblDeletedCount = 0;
    private int sfblInsertedCount = 0;
    private int sfblUpdatedCount = 0;
    private int warningCount = 0;


    private SufficientFundBalances currentSfbl = null;

    /**
     * Constructs a SufficientFundsAccountUpdateServiceImpl instance
     */
    public SufficientFundsAccountUpdateServiceImpl() {
        super();
    }

    /**
     * Returns the fiscal year, set in a parameter, of sufficient funds to rebuild
     * 
     * @return the fiscal year
     */
    protected Integer getFiscalYear() {
        String val = SpringContext.getBean(ParameterService.class).getParameterValueAsString(SufficientFundsAccountUpdateStep.class, GeneralLedgerConstants.FISCAL_YEAR_PARM);
        return Integer.parseInt(val);
    }

    /**
     * Rebuilds all necessary sufficient funds balances.
     * @see org.kuali.kfs.gl.batch.service.SufficientFundsAccountUpdateService#rebuildSufficientFunds()
     */
    public void rebuildSufficientFunds() { // driver
        List <SufficientFundRebuild> rebuildSfrbList = new ArrayList<SufficientFundRebuild>(); 
        
        LOG.debug("rebuildSufficientFunds() started");

        universityFiscalYear = getFiscalYear();
        initService();
        
        //need to add time info - batch util?
        runDate = dateTimeService.getCurrentSqlDate();
        
        // Get all the O types and convert them to A types
        if (LOG.isDebugEnabled()) {
            LOG.debug("rebuildSufficientFunds() Converting O types to A types");
        }
        Map criteria = new HashMap();
        criteria.put(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, KFSConstants.SF_TYPE_OBJECT);
        
        for (Iterator iter = boService.findMatching(SufficientFundRebuild.class, criteria).iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();
            ++sfrbRecordsReadCount;

            transactionErrors = new ArrayList<Message>();

            convertOtypeToAtypes(sfrb);

            if (transactionErrors.size() > 0) {
                reportWriterService.writeError(sfrb, transactionErrors);
                rebuildSfrbList.add(sfrb);
            }
          }
        criteria.clear();
        
        // Get all the A types and process them
        LOG.debug("rebuildSufficientFunds() Calculating SF balances for all A types");
        
        criteria.put(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, KFSConstants.SF_TYPE_ACCOUNT);

        for (Iterator iter = boService.findMatching(SufficientFundRebuild.class, criteria).iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();
            ++sfrbRecordsReadCount;

            transactionErrors = new ArrayList<Message>();

            calculateSufficientFundsByAccount(sfrb);

            if (transactionErrors.size() > 0) {
                reportWriterService.writeError(sfrb, transactionErrors);
                rebuildSfrbList.add(sfrb);
            }

        }
        sufficientFundRebuildDao.purgeSufficientFundRebuild();
        boService.save( rebuildSfrbList);

        // Look at all the left over rows. There shouldn't be any left if all are O's and A's without error.
        // Write out error messages for any that aren't A or O
        LOG.debug("rebuildSufficientFunds() Handle any non-A and non-O types");
        for (Iterator iter = boService.findAll(SufficientFundRebuild.class).iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();

            if ((!KFSConstants.SF_TYPE_ACCOUNT.equals(sfrb.getAccountFinancialObjectTypeCode())) && (!KFSConstants.SF_TYPE_OBJECT.equals(sfrb.getAccountFinancialObjectTypeCode()))) {
                ++sfrbRecordsReadCount;
                transactionErrors = new ArrayList<Message>();
                addTransactionError(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_INVALID_SF_OBJECT_TYPE_CODE));
                ++warningCount;
                ++sfrbNotDeletedCount;
                reportWriterService.writeError(sfrb, transactionErrors);
            }
        }

        // write out report and errors
        if (LOG.isDebugEnabled()) {
            LOG.debug("rebuildSufficientFunds() Create report");
        }
        
        // write out statistics
        reportWriterService.writeStatisticLine("                                   SFRB RECORDS CONVERTED FROM OBJECT TO ACCOUNT  %,9d\n", sfrbRecordsConvertedCount);
        reportWriterService.writeStatisticLine("                                   POST CONVERSION SFRB RECORDS READ              %,9d\n", sfrbRecordsReadCount);
        reportWriterService.writeStatisticLine("                                   SFRB RECORDS DELETED                           %,9d\n", sfrbRecordsDeletedCount);
        reportWriterService.writeStatisticLine("                                   SFRB RECORDS KEPT DUE TO ERRORS                %,9d\n", sfrbNotDeletedCount);
        reportWriterService.writeStatisticLine("                                   SFBL RECORDS DELETED                           %,9d\n", sfblDeletedCount);
        reportWriterService.writeStatisticLine("                                   SFBL RECORDS ADDED                             %,9d\n", sfblInsertedCount);
        reportWriterService.writeStatisticLine("                                   SFBL RECORDS UDPATED                           %,9d\n", sfblUpdatedCount);
    }

    /**
     * Initializes the process at the beginning of a run.
     */
    protected void initService() {
        batchError = new HashMap();
        reportSummary = new ArrayList();

        runDate = new Date(dateTimeService.getCurrentDate().getTime());

        options = (SystemOptions)boService.findBySinglePrimaryKey(SystemOptions.class, universityFiscalYear);

        if (options == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }
    }

    /**
     * Given an O SF rebuild type, it will look up all of the matching balances in the table and add each account it finds as an A
     * SF rebuild type.
     * 
     * @param sfrb the sufficient fund rebuild record to convert
     */
    public void convertOtypeToAtypes(SufficientFundRebuild sfrb) {
        ++sfrbRecordsConvertedCount;
        Collection fundBalances = sufficientFundBalancesDao.getByObjectCode(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());
        Map criteria = new HashMap();
        
        for (Iterator fundBalancesIter = fundBalances.iterator(); fundBalancesIter.hasNext();) {
            SufficientFundBalances sfbl = (SufficientFundBalances) fundBalancesIter.next();
            criteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, sfbl.getChartOfAccountsCode());
            criteria.put(KFSPropertyConstants.ACCOUNT_FINANCIAL_OBJECT_TYPE_CODE, KFSConstants.SF_TYPE_ACCOUNT);
            criteria.put(KFSPropertyConstants.ACCOUNT_NUMBER_FINANCIAL_OBJECT_CODE, sfbl.getAccountNumber());
            
            SufficientFundRebuild altSfrb = (SufficientFundRebuild)boService.findByPrimaryKey(SufficientFundRebuild.class, criteria);
            if (altSfrb == null) {
                altSfrb = new SufficientFundRebuild();
                altSfrb.setAccountFinancialObjectTypeCode(KFSConstants.SF_TYPE_ACCOUNT);
                altSfrb.setAccountNumberFinancialObjectCode(sfbl.getAccountNumber());
                altSfrb.setChartOfAccountsCode(sfbl.getChartOfAccountsCode());
                boService.save(altSfrb);
            }
            criteria.clear();
        }
    }

    /**
     * Updates sufficient funds balances for the given account
     * 
     * @param sfrb the sufficient fund rebuild record, with a chart and account number
     */
    public void calculateSufficientFundsByAccount(SufficientFundRebuild sfrb) {
        Account sfrbAccount = accountService.getByPrimaryId(sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());
        if (sfrbAccount == null) {
            String msg = "Account found in SufficientFundsRebuild table that is not in Accounts table [" + sfrb.getChartOfAccountsCode() + "-" + sfrb.getAccountNumberFinancialObjectCode() + "].";
            LOG.error(msg);
            throw new RuntimeException(msg);
        }
        if ((sfrbAccount.getAccountSufficientFundsCode() != null) 
                && (KFSConstants.SF_TYPE_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode()) 
                        || KFSConstants.SF_TYPE_CASH_AT_ACCOUNT.equals(sfrbAccount.getAccountSufficientFundsCode()) 
                        || KFSConstants.SF_TYPE_CONSOLIDATION.equals(sfrbAccount.getAccountSufficientFundsCode()) 
                        || KFSConstants.SF_TYPE_LEVEL.equals(sfrbAccount.getAccountSufficientFundsCode()) 
                        || KFSConstants.SF_TYPE_OBJECT.equals(sfrbAccount.getAccountSufficientFundsCode()) 
                        || KFSConstants.SF_TYPE_NO_CHECKING.equals(sfrbAccount.getAccountSufficientFundsCode()))) {
            ++sfrbRecordsDeletedCount;
             sfblDeletedCount += sufficientFundBalancesDao.deleteByAccountNumber(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrbAccount.getAccountNumber());

            if (KFSConstants.SF_TYPE_NO_CHECKING.equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
                // nothing to do here, no errors either, just return
                return;
            }

            Iterator balancesIterator = balanceDao.findAccountBalances(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode(), sfrbAccount.getAccountSufficientFundsCode());

            if (balancesIterator == null) {
                addTransactionError(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_BALANCE_NOT_FOUND_FOR) + universityFiscalYear + ")");
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
                        boService.save(currentSfbl);
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
                boService.save(currentSfbl);
                ++sfblInsertedCount;
            }
         }
        else {
            addTransactionError(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_INVALID_ACCOUNT_SF_CODE_FOR));
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
    protected boolean amountsAreNonZero(SufficientFundBalances sfbl) {
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
    protected void processObjectOrAccount(Account sfrbAccount, Balance balance) {
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
    protected void processObjtAcctActual(Balance balance) {
        currentSfbl.setAccountActualExpenditureAmt(currentSfbl.getAccountActualExpenditureAmt().add(balance.getAccountLineAnnualBalanceAmount()));
    }

    /**
     * Updates the current sufficient fund balance record with a non-cash encumbrance balance
     * 
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    protected void processObjtAcctEncmbrnc(Balance balance) {
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * Updates the current sufficient fund balance record with a non-cash budget balance
     * 
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    protected void processObjtAcctBudget(Balance balance) {
        currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setCurrentBudgetBalanceAmount(currentSfbl.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * Determines how best to process a cash balance
     * 
     * @param sfrbAccount the account of the current sufficient funds balance record
     * @param balance the cash encumbrance balance to update the sufficient funds balance with
     */
    protected void processCash(Account sfrbAccount, Balance balance) {
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
    protected void processCashActual(Account sfrbAccount, Balance balance) {
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
    protected void processCashEncumbrance(Balance balance) {
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfbl.setAccountEncumbranceAmount(currentSfbl.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * Adds an error message to this instance's List of error messages
     * @param errorMessage the error message to keep
     */
    protected void addTransactionError(String errorMessage) {
        transactionErrors.add(new Message(errorMessage, Message.TYPE_WARNING));
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setBalanceDao(BalanceDao balanceDao) {
        this.balanceDao = balanceDao;
    }

    public void setSufficientFundBalancesDao(SufficientFundBalancesDao sufficientFundBalancesDao) {
        this.sufficientFundBalancesDao = sufficientFundBalancesDao;
    }

    public void setReportWriterService(ReportWriterService sfrs) {
        reportWriterService = sfrs;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setSufficientFundsService(SufficientFundsService sfs) {
        sufficientFundsService = sfs;
    }
    
    public void setBusinessObjectService(BusinessObjectService bos) {
        boService = bos;
    }
    
    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sufficientFundRebuildDao) {
        this.sufficientFundRebuildDao = sufficientFundRebuildDao;
    }
}
