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
package org.kuali.kfs.coa.service.impl;


import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.batch.AddPriorYearAccountsStep;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.businessobject.PriorYearIndirectCostRecoveryAccount;
import org.kuali.kfs.coa.dataaccess.PriorYearAccountDao;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.ChartApcParms;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the PriorYearAccountService interface.
 */
@Transactional
public class PriorYearAccountServiceImpl implements PriorYearAccountService {
    private static final Logger LOG = Logger.getLogger(PriorYearAccountServiceImpl.class);

    protected PriorYearAccountDao priorYearAccountDao;
    protected AccountService accountService;
    protected ReportWriterService reportWriterService;
    protected PersistenceStructureService persistenceStructureService;
    protected BusinessObjectService businessObjectService;
    protected ParameterService parameterService;
    
    /**
     * @see org.kuali.kfs.coa.service.PriorYearAccountService#getByPrimaryKey(java.lang.String, java.lang.String)
     */
    @Override
    public PriorYearAccount getByPrimaryKey(String chartCode, String accountNumber) {
        Map<String, Object> keys = new HashMap<String, Object>();
        keys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartCode);
        keys.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
        return businessObjectService.findByPrimaryKey(PriorYearAccount.class, keys);
    }

    /**
     * @see org.kuali.kfs.coa.service.PriorYearAccountService#populatePriorYearAccountsFromCurrent()
     */
    @Override
    public void populatePriorYearAccountsFromCurrent() {
        final String priorYrAcctTableName = persistenceStructureService.getTableName(PriorYearAccount.class);
        int purgedCount = priorYearAccountDao.purgePriorYearAccounts(priorYrAcctTableName);
        if (LOG.isInfoEnabled()) {
            LOG.info("number of prior year accounts purged : " + purgedCount);
        }

        final String acctTableName = persistenceStructureService.getTableName(Account.class);
        int copiedCount = priorYearAccountDao.copyCurrentAccountsToPriorYearTable(priorYrAcctTableName, acctTableName);
        if (LOG.isInfoEnabled()) {
            LOG.info("number of current year accounts copied to prior year : " + copiedCount);
        }

        //copy prior year ICR accounts
        final String priorYrIcrAcctTableName = persistenceStructureService.getTableName(PriorYearIndirectCostRecoveryAccount.class);
        purgedCount = priorYearAccountDao.purgePriorYearAccounts(priorYrIcrAcctTableName);
        if (LOG.isInfoEnabled()) {
            LOG.info("number of prior year indirect cost recovery accounts purged : " + purgedCount);
        }

        final String icrAcctTableName = persistenceStructureService.getTableName(IndirectCostRecoveryAccount.class);
        copiedCount = priorYearAccountDao.copyCurrentICRAccountsToPriorYearTable(priorYrIcrAcctTableName, icrAcctTableName);
        if (LOG.isInfoEnabled()) {
            LOG.info("number of current year indirect cost recovery accounts copied to prior year : " + copiedCount);
        }

    }

    /**
     * @see org.kuali.kfs.coa.service.PriorYearAccountService#addPriorYearAccountsFromParameter()
     */
    public void addPriorYearAccountsFromParameter() {
        /*
        Collection<String> accountsColl = new ArrayList<String>();
        accountsColl.add("0000000");
        accountsColl.add("BL-0000000");
        accountsColl.add("UA-2131401");      
        accountsColl.add("BA-6044909");
        accountsColl.add("BA-6044901");
        accountsColl.add("UA-7014960");
        */
        String param = ChartApcParms.PRIOR_YEAR_ACCOUNTS_TO_BE_ADDED;
        Collection<String> accountsColl = parameterService.getParameterValuesAsString(AddPriorYearAccountsStep.class, param);
        Iterator<String> accountsIter = accountsColl.iterator();
        List<PriorYearAccount> priorAccounts = new ArrayList<PriorYearAccount>();
        int countError = 0;
        String errmsg = "";
        String failmsg = "Failed to add account ";
        
        LOG.info("Adding Accounts to Prior Year Account table from parameter " + param);
        reportWriterService.writeSubTitle("Accounts failed to be added to Prior Year Account table from parameter " + param);

        while (accountsIter.hasNext()) {
            // retrieve chart code and account number from parameter
            String accountStr = accountsIter.next();
            String chartCode = StringUtils.substringBefore(accountStr, "-");
            String accountNumber = StringUtils.substringAfter(accountStr, "-");       
            
            // if account format is invalid, report error      
            if (StringUtils.isEmpty(chartCode) || StringUtils.isEmpty(accountNumber)) {
                countError++;                
                errmsg = accountStr + " : invalid format. Correct account format: coaCode-accountNumber."; 
                reportWriterService.writeFormattedMessageLine("%s", errmsg);
                LOG.error(failmsg + errmsg);
                continue;
            }

            // check whether account exists, report error if not      
            Account account = accountService.getByPrimaryId(chartCode, accountNumber);
            if (ObjectUtils.isNull(account)) {
                countError++;
                errmsg = accountStr + " : doesn't exist in Account table."; 
                reportWriterService.writeFormattedMessageLine("%s", errmsg);
                LOG.error(failmsg + errmsg);
            }            
            // check whether account already exists in prior year, report error if yes 
            else if (ObjectUtils.isNotNull(getByPrimaryKey(chartCode, accountNumber))) {
                countError++;
                errmsg = accountStr + " : already exists in Prior Year Account table."; 
                reportWriterService.writeFormattedMessageLine("%s", errmsg);
                LOG.error(failmsg + errmsg);
            }
            // otherwise, add account to prior year table 
            else {
                PriorYearAccount priorAccount = new PriorYearAccount(account);
                businessObjectService.save(priorAccount);                
                priorAccounts.add(priorAccount);
                LOG.info("Successfully added account " + accountStr);
            }
        }
        
        String totalSuccessMsg = "Total number of accounts successfully added to prior year: " + priorAccounts.size();
        String totalFailureMsg = "Total number of accounts failed to be added to prior year: " + countError;
        reportWriterService.writeSubTitle("Accounts successfully added to Prior Year Account table:");
        reportWriterService.writeTable(priorAccounts, true, false);
        reportWriterService.writeStatisticLine("%s", totalSuccessMsg);
        reportWriterService.writeStatisticLine("%s", totalFailureMsg);
        LOG.info(totalSuccessMsg);
        LOG.info(totalFailureMsg);
    }
    
    public void setPriorYearAccountDao(PriorYearAccountDao priorYearAccountDao) {
        this.priorYearAccountDao = priorYearAccountDao;
    }
    
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
}
