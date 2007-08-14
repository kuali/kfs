/*
 * Copyright 2007 The Kuali Foundation.
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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.IteratorUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.dao.LaborLedgerBalanceDao;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.util.ObjectUtil;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is the OJB implementation of the Balance Service
 */
@Transactional
public class LaborLedgerBalanceServiceImpl implements LaborLedgerBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceServiceImpl.class);

    private LaborLedgerBalanceDao laborLedgerBalanceDao;

    /**
     * @see org.kuali.module.labor.service.LaborLedgerBalanceService#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear) {
        return (Iterator<LedgerBalance>) TransactionalServiceUtils.copyToExternallyUsuableIterator(laborLedgerBalanceDao.findBalancesForFiscalYear(fiscalYear));
    }

    /**
     * @see org.kuali.module.labor.service.LaborLedgerBalanceService#findBalance(java.util.Map, boolean)
     */
    public Iterator findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");
        return TransactionalServiceUtils.copyToExternallyUsuableIterator(laborLedgerBalanceDao.findBalance(fieldValues, isConsolidated));
    }

    /**
     * @see org.kuali.module.labor.service.LaborLedgerBalanceService#getBalanceRecordCount(java.util.Map, boolean)
     */
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceRecordCount() started");

        Integer recordCount = null;
        if (!isConsolidated) {
            recordCount = OJBUtility.getResultSizeFromMap(fieldValues, new LedgerBalance()).intValue();
        }
        else {
            Iterator recordCountIterator = laborLedgerBalanceDao.getConsolidatedBalanceRecordCount(fieldValues);
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
    }

    /**
     * @see org.kuali.module.labor.service.LaborLedgerBalanceService#findLedgerBalance(java.util.Collection,
     *      org.kuali.module.gl.bo.Transaction)
     */
    public LedgerBalance findLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, Transaction transaction) {
        for (LedgerBalance ledgerBalance : ledgerBalanceCollection) {
            boolean found = ObjectUtil.compareObject(ledgerBalance, transaction, ledgerBalance.getPrimaryKeyList());
            if (found) {
                return ledgerBalance;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.module.labor.service.LaborLedgerBalanceService#updateBalance(org.kuali.module.labor.bo.LedgerBalance,
     *      org.kuali.module.gl.bo.Transaction)
     */
    public void updateLedgerBalance(LedgerBalance ledgerBalance, Transaction transaction) {
        String debitCreditCode = transaction.getTransactionDebitCreditCode();
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        amount = debitCreditCode.equals(KFSConstants.GL_CREDIT_CODE) ? amount.negated() : amount;

        ledgerBalance.addAmount(transaction.getUniversityFiscalPeriodCode(), amount);
    }

    /**
     * @see org.kuali.module.labor.service.LaborLedgerBalanceService#addLedgerBalance(java.util.Collection,
     *      org.kuali.module.gl.bo.Transaction)
     */
    public boolean addLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, Transaction transaction) {
        LedgerBalance ledgerBalance = this.findLedgerBalance(ledgerBalanceCollection, transaction);

        if (ledgerBalance == null) {
            LedgerBalance newLedgerBalance = new LedgerBalance();
            ObjectUtil.buildObject(newLedgerBalance, transaction);
            ledgerBalanceCollection.add(newLedgerBalance);
            return true;
        }
        return false;
    }

    /**
     * Sets the laborLedgerBalanceDao attribute value.
     * @param laborLedgerBalanceDao The laborLedgerBalanceDao to set.
     */
    public void setLaborLedgerBalanceDao(LaborLedgerBalanceDao laborLedgerBalanceDao) {
        this.laborLedgerBalanceDao = laborLedgerBalanceDao;
    }
}
