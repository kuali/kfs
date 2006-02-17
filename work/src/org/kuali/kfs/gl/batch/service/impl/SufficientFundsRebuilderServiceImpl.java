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
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.KeyConstants;
import org.kuali.core.bo.user.Options;
import org.kuali.core.dao.OptionsDao;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.ObjLevel;
import org.kuali.module.chart.dao.ObjectLevelDao;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.BalanceDao;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.kuali.module.gl.service.SufficientFundsRebuilderService;

/**
 * @author Anthony Potts
 */

public class SufficientFundsRebuilderServiceImpl implements SufficientFundsRebuilderService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsRebuilderServiceImpl.class);

    private DateTimeService dateTimeService;
    private KualiConfigurationService kualiConfigurationService;
    private BalanceDao balanceDao;
    private SufficientFundBalancesDao sufficientFundBalancesDao;
    private SufficientFundRebuildDao sufficientFundRebuildDao;
    private OptionsDao optionsDao;
    private PersistenceService persistenceService;
    private ObjectLevelDao objectLevelDao;

    private Date runDate;
    private Calendar runCal;
    private Options options;
    
    Map batchError;
    List transactionErrors;
    private int sfrbRecordsConvertedCount;
    private Integer universityFiscalYear;

    private int sfrbRecordsReadCount;
    private int warningCount;
    private int sfrbNotDeletedCount;
    private boolean isContractGrants;

    private SufficientFundBalances currentSfb;
    
    public SufficientFundsRebuilderServiceImpl() {
      super();
    }

    public void rebuildSufficientFunds(Integer fiscalYear) { // driver
        LOG.debug("beginning sufficient funds rebuild process");

        universityFiscalYear = fiscalYear;
        initService();

        for (Iterator iter = sufficientFundRebuildDao.getAll().iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();
            transactionErrors = new ArrayList();
            convertSfrb(sfrb);
        }
        
        for (Iterator iter = sufficientFundRebuildDao.getAll().iterator(); iter.hasNext();) {
            SufficientFundRebuild sfrb = (SufficientFundRebuild) iter.next();

            ++sfrbRecordsReadCount;
        
            if(!"A".equalsIgnoreCase(sfrb.getAccountFinancialObjectTypeCode())) {
                addTransactionError("ACCOUNT/FINANCIAL OBJECT TYPE CODE MUST='A' OR 'O'", sfrb.getAccountFinancialObjectTypeCode());
                ++warningCount;
                ++sfrbNotDeletedCount;
            } else {
                processSfrb(sfrb);
            }
        }

    }

    private void initService() {
        batchError = new HashMap();

        runDate = new Date(dateTimeService.getCurrentDate().getTime());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);
        
        options = optionsDao.getByPrimaryId(universityFiscalYear);
        
        if (options == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }
    }
    
    private void convertSfrb(SufficientFundRebuild sfrb) {
        if("A".equalsIgnoreCase(sfrb.getAccountFinancialObjectTypeCode())) {
            return;
        }
        if("O".equalsIgnoreCase(sfrb.getAccountFinancialObjectTypeCode())) {
            processSfrbObject(sfrb);
            ++sfrbRecordsConvertedCount;
        } else {
            addTransactionError("ACCOUNT/FINANCIAL OBJECT TYPE CODE MUST='A' OR 'O'", sfrb.getAccountFinancialObjectTypeCode());
        }
    }

    private void processSfrbObject(SufficientFundRebuild sfrb) {
        Collection fundBalances = sufficientFundBalancesDao.getByObjectCode(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());

        for (Iterator fundBalancesIter = fundBalances.iterator(); fundBalancesIter.hasNext();) {
            currentSfb = (SufficientFundBalances) fundBalancesIter.next();
            convertObjtToAcct(sfrb, currentSfb);
        }
    }

    private void convertObjtToAcct(SufficientFundRebuild sfrb, SufficientFundBalances sfb) {
        SufficientFundRebuild altSfrb = sufficientFundRebuildDao.get(sfb.getChartOfAccountsCode(), "A", sfb.getAccountNumber());
        
        if (altSfrb == null) {
            sufficientFundRebuildDao.save(altSfrb);
            sufficientFundRebuildDao.delete(sfrb);
        }
    }

    private void processSfrb(SufficientFundRebuild sfrb) {
        Balance balance = balanceDao.getBalanceByPrimaryId(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrb.getAccountNumberFinancialObjectCode());
        
        if (balance == null) {
            addTransactionError("Balance not found in database", universityFiscalYear + " - " + sfrb.getChartOfAccountsCode() + " - " + sfrb.getAccountNumberFinancialObjectCode());
            ++warningCount;
            ++sfrbNotDeletedCount;
            return;
        }
        
        balance.setTimestamp(new Date(dateTimeService.getCurrentTimestamp().getTime()));
        
        Account sfrbAccount = balance.getAccount();
        
        if (sfrbAccount == null) {
            addTransactionError("Record not found in Account table", sfrb.getChartOfAccountsCode() + " - " + sfrb.getAccountNumberFinancialObjectCode());
            ++warningCount;
            ++sfrbNotDeletedCount;
            return;
        }

        if (sfrbAccount.getSubFundGroup() == null) {
            addTransactionError("Sub Fund group for this Account not found", sfrb.getAccountNumberFinancialObjectCode());
            ++warningCount;
            ++sfrbNotDeletedCount;
            return;
        }
        
        isContractGrants = "CG".equalsIgnoreCase(sfrbAccount.getSubFundGroupCode());

        if ("OLCAH".indexOf(sfrbAccount.getAccountSufficientFundsCode()) == -1) {
            addTransactionError("AccountSufficientFundsCode invalid for this Chart and Account", sfrb.getChartOfAccountsCode() + " - " + sfrb.getAccountNumberFinancialObjectCode() + " - " + sfrbAccount.getAccountSufficientFundsCode());
            ++warningCount;
            ++sfrbNotDeletedCount;
            return;
        }
  
        sufficientFundBalancesDao.deleteByAccountNumber(universityFiscalYear, sfrb.getChartOfAccountsCode(), sfrbAccount.getAccountNumber());
        
        if ("N".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
            // set a switch
        } else {
            processSfbl(sfrb, sfrbAccount, balance);
        }
    }

    private void processSfbl(SufficientFundRebuild sfrb, Account sfrbAccount, Balance balance) {
        String tempFinObjectCd = "";
        if("O".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
            tempFinObjectCd = balance.getObjectCode();
        } else if("L".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
            tempFinObjectCd = balance.getFinancialObject().getFinancialObjectLevelCode();
        } else if("C".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
            ObjLevel objLevel = objectLevelDao.getByPrimaryId(sfrbAccount.getChartOfAccountsCode(), balance.getFinancialObject().getFinancialObjectLevelCode());
            tempFinObjectCd = objLevel.getConsolidatedObjectCode();
        } else if("H".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode()) ||
                "A".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
            tempFinObjectCd = "";
        }
        
        currentSfb = sufficientFundBalancesDao.getByPrimaryId(universityFiscalYear, sfrbAccount.getChartOfAccountsCode(), sfrbAccount.getAccountNumber(), tempFinObjectCd);
        
        if (isContractGrants) {
            balance.setAccountLineAnnualBalanceAmount(balance.getAccountLineAnnualBalanceAmount().add(balance.getContractsGrantsBeginningBalanceAmount()));
        }
        
        if ("H".equalsIgnoreCase(sfrbAccount.getAccountSufficientFundsCode())) {
            processCash(sfrbAccount, balance);
        } else {
            processObjectOrAccount(sfrbAccount, balance);
        }
        
        persistenceService.getPersistenceBroker().store(balance);
    }

    private void processObjectOrAccount(Account sfrbAccount, Balance balance) {
        if (options.getFinObjTypeExpenditureexpCd().equalsIgnoreCase(balance.getObjectTypeCode()) ||
                "TE".equalsIgnoreCase(options.getFinObjTypeExpenditureexpCd()) ||
                "ES".equalsIgnoreCase(options.getFinObjTypeExpenditureexpCd())) {
            if (options.getActualFinancialBalanceTypeCd().equalsIgnoreCase(balance.getBalanceTypeCode())) {
                processObjtAcctActual(balance);
            } else if (options.getExtrnlEncumFinBalanceTypCd().equalsIgnoreCase(balance.getBalanceTypeCode()) ||
                    options.getIntrnlEncumFinBalanceTypCd().equalsIgnoreCase(balance.getBalanceTypeCode()) ||
                    options.getPreencumbranceFinBalTypeCd().equalsIgnoreCase(balance.getBalanceTypeCode()) ||
                    "CE".equalsIgnoreCase(balance.getBalanceTypeCode())) {
                processObjtAcctEncmbrnc(balance);
            } else if (options.getBudgetCheckingBalanceTypeCd().equalsIgnoreCase(balance.getBalanceTypeCode())) {
                processObjtAcctBudget(balance);
            }
        }
    }

    private void processObjtAcctActual(Balance balance) {
        currentSfb.setAccountActualExpenditureAmt(currentSfb.getAccountActualExpenditureAmt().add(balance.getAccountLineAnnualBalanceAmount()));
    }

    private void processObjtAcctEncmbrnc(Balance balance) {
        currentSfb.setAccountEncumbranceAmount(currentSfb.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfb.setAccountEncumbranceAmount(currentSfb.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    private void processObjtAcctBudget(Balance balance) {
        currentSfb.setCurrentBudgetBalanceAmount(currentSfb.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfb.setCurrentBudgetBalanceAmount(currentSfb.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    private void processCash(Account sfrbAccount, Balance balance) {
        if (balance.getBalanceTypeCode().equalsIgnoreCase(options.getActualFinancialBalanceTypeCd())) {
            if (balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinancialCashObjectCode()) ||
                    balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinAccountsPayableObjectCode())) {
                processCashActual(sfrbAccount, balance);
            }
        } else if(balance.getBalanceTypeCode().equalsIgnoreCase(options.getExtrnlEncumFinBalanceTypCd()) ||
                balance.getBalanceTypeCode().equalsIgnoreCase(options.getIntrnlEncumFinBalanceTypCd()) ||
                balance.getBalanceTypeCode().equalsIgnoreCase(options.getPreencumbranceFinBalTypeCd()) ||
                "CE".equalsIgnoreCase(balance.getBalanceTypeCode())) {
            if (balance.getObjectTypeCode().equalsIgnoreCase(options.getFinObjTypeExpenditureexpCd()) ||
                    balance.getObjectTypeCode().equalsIgnoreCase(options.getFinObjTypeExpendNotExpCode()) ||
                    "TE".equalsIgnoreCase(balance.getObjectTypeCode()) ||
                    "ES".equalsIgnoreCase(balance.getObjectTypeCode())) {
                processCashEncumbrance(balance);
            }
        }
    }

    private void processCashActual(Account sfrbAccount, Balance balance) {
        if (balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinancialCashObjectCode())) {
            currentSfb.setCurrentBudgetBalanceAmount(currentSfb.getCurrentBudgetBalanceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
            currentSfb.setCurrentBudgetBalanceAmount(currentSfb.getCurrentBudgetBalanceAmount().add(balance.getBeginningBalanceLineAmount()));
        }
        if (balance.getObjectCode().equalsIgnoreCase(sfrbAccount.getChartOfAccounts().getFinAccountsPayableObjectCode())) {
            currentSfb.setCurrentBudgetBalanceAmount(currentSfb.getCurrentBudgetBalanceAmount().subtract(balance.getAccountLineAnnualBalanceAmount()));
            currentSfb.setCurrentBudgetBalanceAmount(currentSfb.getCurrentBudgetBalanceAmount().subtract(balance.getBeginningBalanceLineAmount()));
        }
    }

    private void processCashEncumbrance(Balance balance) {
        currentSfb.setAccountEncumbranceAmount(currentSfb.getAccountEncumbranceAmount().add(balance.getAccountLineAnnualBalanceAmount()));
        currentSfb.setAccountEncumbranceAmount(currentSfb.getAccountEncumbranceAmount().add(balance.getBeginningBalanceLineAmount()));
    }

    /**
     * @param errorMessage
     */
    private void addTransactionError(String errorMessage, String errorValue) {
        transactionErrors.add(errorMessage + " (" + errorValue + ")");
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

    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sufficientFundRebuildDao) {
        this.sufficientFundRebuildDao = sufficientFundRebuildDao;
    }

    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    public void setObjectLevelDao(ObjectLevelDao objectLevelDao) {
        this.objectLevelDao = objectLevelDao;
    }

}
