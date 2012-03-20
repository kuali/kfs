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


import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.businessobject.PriorYearAccount;
import org.kuali.kfs.coa.businessobject.PriorYearIndirectCostRecoveryAccount;
import org.kuali.kfs.coa.dataaccess.PriorYearAccountDao;
import org.kuali.kfs.coa.service.PriorYearAccountService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class implements the PriorYearAccountService interface.
 */
@Transactional
public class PriorYearAccountServiceImpl implements PriorYearAccountService {
    private static final Logger LOG = Logger.getLogger(PriorYearAccountServiceImpl.class);

    protected PriorYearAccountDao priorYearAccountDao;
    protected PersistenceStructureService persistenceStructureService;
    protected BusinessObjectService businessObjectService;

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

    public void setPriorYearAccountDaoJdbc(PriorYearAccountDao priorYearAccountDao) {
        this.priorYearAccountDao = priorYearAccountDao;
    }
    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
