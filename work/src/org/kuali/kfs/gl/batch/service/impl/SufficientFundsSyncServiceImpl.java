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

import java.util.Iterator;

import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.kuali.module.gl.service.SufficientFundsSyncService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of SufficientFundsSyncService
 */
@Transactional
public class SufficientFundsSyncServiceImpl implements SufficientFundsSyncService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsSyncServiceImpl.class);

    private AccountService accountService;
    private SufficientFundRebuildDao sufficientFundRebuildDao;

    /**
     * Goes through all accounts in the database, and generates a sufficient fund rebuild record for each one!
     * @see org.kuali.module.gl.service.SufficientFundsSyncService#syncSufficientFunds()
     */
    public void syncSufficientFunds() {
        LOG.debug("syncSufficientFunds() started");

        Iterator i = accountService.getAllAccounts();
        while (i.hasNext()) {
            Account a = (Account) i.next();
            SufficientFundRebuild sfr = sufficientFundRebuildDao.getByAccount(a.getChartOfAccountsCode(), a.getAccountNumber());
            if (sfr == null) {
                sfr = new SufficientFundRebuild();
                sfr.setAccountFinancialObjectTypeCode("A");
                sfr.setAccountNumberFinancialObjectCode(a.getAccountNumber());
                sfr.setChartOfAccountsCode(a.getChartOfAccountsCode());
                sufficientFundRebuildDao.save(sfr);
            }
        }
    }

    public void setSufficientFundRebuildDao(SufficientFundRebuildDao sfd) {
        sufficientFundRebuildDao = sfd;
    }

    public void setAccountService(AccountService as) {
        accountService = as;
    }
}
