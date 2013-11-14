/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IteratorUtils;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceSummary;
import org.kuali.kfs.module.ld.businessobject.LaborTransaction;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerBalanceForYearEndBalanceForward;
import org.kuali.kfs.module.ld.dataaccess.LaborLedgerBalanceDao;
import org.kuali.kfs.module.ld.service.LaborCalculatedSalaryFoundationTrackerService;
import org.kuali.kfs.module.ld.service.LaborLedgerBalanceService;
import org.kuali.kfs.module.ld.util.DebitCreditUtil;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

public class LaborLedgerBalanceServiceImpl implements LaborLedgerBalanceService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceServiceImpl.class);

    private LaborLedgerBalanceDao laborLedgerBalanceDao;
    private LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService;

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findBalancesForFiscalYear(java.lang.Integer)
     */
    @Override
    @NonTransactional
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear) {
        return laborLedgerBalanceDao.findBalancesForFiscalYear(fiscalYear);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findBalancesForFiscalYear(Integer, Map, List)
     */

    @Override
    @NonTransactional
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, List<String> encumbranceBalanceTypes) {
      return laborLedgerBalanceDao.findBalancesForFiscalYear(fiscalYear, fieldValues, encumbranceBalanceTypes);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findBalance(Map, boolean, List)
     */
    @Override
    @NonTransactional
    public Iterator findBalance(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts) {
        LOG.debug("findBalance() started");
        return laborLedgerBalanceDao.findBalance(fieldValues, isConsolidated, encumbranceBalanceTypes, noZeroAmounts);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#getBalanceRecordCount(Map, boolean, List)
     */
    @Override
    @NonTransactional
    public Integer getBalanceRecordCount(Map fieldValues, boolean isConsolidated, List<String> encumbranceBalanceTypes, boolean noZeroAmounts) {
        LOG.debug("getBalanceRecordCount() started");

        Integer recordCount = null;
        if (!isConsolidated) {
            recordCount = OJBUtility.getResultSizeFromMap(fieldValues, new LedgerBalance()).intValue();
        }
        else {
            Iterator recordCountIterator = laborLedgerBalanceDao.getConsolidatedBalanceRecordCount(fieldValues, encumbranceBalanceTypes, noZeroAmounts);
            List recordCountList = IteratorUtils.toList(recordCountIterator);
            recordCount = recordCountList.size();
        }
        return recordCount;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findLedgerBalance(java.util.Collection,
     *      org.kuali.kfs.module.ld.businessobject.LaborTransaction)
     */
    @Override
    @NonTransactional
    public <T extends LedgerBalance> T findLedgerBalance(Collection<T> ledgerBalanceCollection, LaborTransaction transaction, List<String> keyList) {
        for (T ledgerBalance : ledgerBalanceCollection) {
            boolean found = ObjectUtil.equals(ledgerBalance, transaction, keyList);
            if (found) {
                return ledgerBalance;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findLedgerBalance(java.util.Collection,
     *      org.kuali.kfs.module.ld.businessobject.LaborTransaction)
     */
    @Override
    @NonTransactional
    public <T extends LedgerBalance> T findLedgerBalance(Collection<T> ledgerBalanceCollection, LaborTransaction transaction) {
        for (T ledgerBalance : ledgerBalanceCollection) {
            boolean found = ObjectUtil.equals(ledgerBalance, transaction, ledgerBalance.getPrimaryKeyList());
            if (found) {
                return ledgerBalance;
            }
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#updateLedgerBalance(org.kuali.kfs.module.ld.businessobject.LedgerBalance,
     *      org.kuali.kfs.module.ld.businessobject.LaborTransaction)
     */
    @Override
    @Transactional
    public <T extends LedgerBalance> void updateLedgerBalance(T ledgerBalance, LaborTransaction transaction) {
        String debitCreditCode = transaction.getTransactionDebitCreditCode();
        KualiDecimal amount = transaction.getTransactionLedgerEntryAmount();
        amount = DebitCreditUtil.getNumericAmount(amount, debitCreditCode);
        ledgerBalance.addAmount(transaction.getUniversityFiscalPeriodCode(), amount);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#addLedgerBalance(java.util.Collection,
     *      org.kuali.kfs.module.ld.businessobject.LaborTransaction)
     */
    @Override
    @Transactional
    public LedgerBalance addLedgerBalance(Collection<LedgerBalance> ledgerBalanceCollection, LaborTransaction transaction) {
        LedgerBalance ledgerBalance = this.findLedgerBalance(ledgerBalanceCollection, transaction);

        if (ledgerBalance == null) {
            LedgerBalance newLedgerBalance = new LedgerBalance();
            ObjectUtil.buildObject(newLedgerBalance, transaction);
            updateLedgerBalance(newLedgerBalance, transaction);

            ledgerBalanceCollection.add(newLedgerBalance);
            return newLedgerBalance;
        }
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findEmployeeFunding(java.util.Map)
     */
    @Override
    @NonTransactional
    public List<EmployeeFunding> findEmployeeFunding(Map fieldValues, boolean isConsolidated) {
        List<EmployeeFunding> currentFundsCollection = laborLedgerBalanceDao.findCurrentEmployeeFunds(fieldValues);
        List<EmployeeFunding> encumbranceFundsCollection = laborLedgerBalanceDao.findEncumbranceEmployeeFunds(fieldValues);

        // merge encumberance with the current funds
        for (EmployeeFunding encumbranceFunding : encumbranceFundsCollection) {
            KualiDecimal encumbrance = encumbranceFunding.getAccountLineAnnualBalanceAmount().add(encumbranceFunding.getContractsGrantsBeginningBalanceAmount());
            encumbranceFunding.setOutstandingEncumbrance(encumbrance);

            if (currentFundsCollection.contains(encumbranceFunding)) {
                int index = currentFundsCollection.indexOf(encumbranceFunding);
                currentFundsCollection.get(index).setOutstandingEncumbrance(encumbrance);
            }
            else if (encumbrance != null && encumbrance.isNonZero()) {
                currentFundsCollection.add(encumbranceFunding);
            }
        }

        // update the employee fundings
        for (EmployeeFunding employeeFunding : currentFundsCollection) {
            employeeFunding.setCurrentAmount(employeeFunding.getAccountLineAnnualBalanceAmount());
        }
        return currentFundsCollection;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findEmployeeFundingWithCSFTracker(java.util.Map)
     */
    @Override
    @NonTransactional
    public List<EmployeeFunding> findEmployeeFundingWithCSFTracker(Map fieldValues, boolean isConsolidated) {
        List<EmployeeFunding> currentFundsCollection = this.findEmployeeFunding(fieldValues, isConsolidated);
        List<EmployeeFunding> CSFTrackersCollection = laborCalculatedSalaryFoundationTrackerService.findCSFTrackersAsEmployeeFunding(fieldValues, isConsolidated);

        for (EmployeeFunding CSFTrackerAsEmployeeFunding : CSFTrackersCollection) {
            if (currentFundsCollection.contains(CSFTrackerAsEmployeeFunding)) {
                int index = currentFundsCollection.indexOf(CSFTrackerAsEmployeeFunding);
                EmployeeFunding currentFunds = currentFundsCollection.get(index);

                currentFunds.setCsfDeleteCode(CSFTrackerAsEmployeeFunding.getCsfDeleteCode());
                currentFunds.setCsfTimePercent(CSFTrackerAsEmployeeFunding.getCsfTimePercent());
                currentFunds.setCsfFundingStatusCode(CSFTrackerAsEmployeeFunding.getCsfFundingStatusCode());
                currentFunds.setCsfAmount(CSFTrackerAsEmployeeFunding.getCsfAmount());
                currentFunds.setCsfFullTimeEmploymentQuantity(CSFTrackerAsEmployeeFunding.getCsfFullTimeEmploymentQuantity());
            } else {
                /*KFSCNTRB-1534 It is possible for a CSF item to exist on its own without being related to
                  a current funds record.*/
                currentFundsCollection.add(CSFTrackerAsEmployeeFunding);            }
        }

        return currentFundsCollection;
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findBalanceSummary(java.lang.Integer, java.util.Collection)
     */
    @Override
    @NonTransactional
    public List<LaborBalanceSummary> findBalanceSummary(Integer fiscalYear, Collection<String> balanceTypes) {
        return laborLedgerBalanceDao.findBalanceSummary(fiscalYear, balanceTypes);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#save(org.kuali.kfs.module.ld.businessobject.LedgerBalance)
     */
    @Override
    @Transactional
    public void save(LedgerBalance ledgerBalance) {
        SpringContext.getBean(BusinessObjectService.class).save(ledgerBalance);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findBalancesForFiscalYear(java.lang.Integer, java.util.Map,
     *      java.util.List, java.util.List)
     */
    @NonTransactional
    @Override
    public Iterator<LedgerBalanceForYearEndBalanceForward> findBalancesForFiscalYear(Integer fiscalYear, Map<String, String> fieldValues, Collection<String> subFundGroupCodes, Collection<String> fundGroupCodes) {
        List<String> fundGroupCodesList = new ArrayList<String>(fundGroupCodes);
        Collections.sort(fundGroupCodesList);
        List<String> subFundGroupCodesList = new ArrayList<String>(subFundGroupCodes);
        Collections.sort(subFundGroupCodesList);
        return laborLedgerBalanceDao.findBalancesForFiscalYear(fiscalYear, fieldValues, subFundGroupCodesList, fundGroupCodesList);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findAccountsInFundGroups(java.lang.Integer, java.util.Map,
     *      java.util.List, java.util.List)
     */
    @Override
    @NonTransactional
    public List<List<String>> findAccountsInFundGroups(Integer fiscalYear, Map<String, String> fieldValues, List<String> subFundGroupCodes, List<String> fundGroupCodes) {
        return laborLedgerBalanceDao.findAccountsInFundGroups(fiscalYear, fieldValues, subFundGroupCodes, fundGroupCodes);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#findLedgerBalances(java.util.Map, java.util.Map,
     *      java.util.Set, java.util.List, java.util.List)
     */
    @Override
    @NonTransactional
    public Collection<LedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypeList, List<String> positionObjectGroupCodes) {
        return laborLedgerBalanceDao.findLedgerBalances(fieldValues, excludedFieldValues, fiscalYears, balanceTypeList, positionObjectGroupCodes);
    }

    /**
     * @see org.kuali.kfs.module.ld.service.LaborLedgerBalanceService#deleteLedgerBalancesPriorToYear(java.lang.Integer,
     *      java.lang.String)
     */
    @Override
    @Transactional
    public void deleteLedgerBalancesPriorToYear(Integer fiscalYear, String chartOfAccountsCode) {
        laborLedgerBalanceDao.deleteLedgerBalancesPriorToYear(fiscalYear, chartOfAccountsCode);
    }

    /**
     * Sets the laborLedgerBalanceDao attribute value.
     *
     * @param laborLedgerBalanceDao The laborLedgerBalanceDao to set.
     */
    @NonTransactional
    public void setLaborLedgerBalanceDao(LaborLedgerBalanceDao laborLedgerBalanceDao) {
        this.laborLedgerBalanceDao = laborLedgerBalanceDao;
    }

    /**
     * Sets the laborCalculatedSalaryFoundationTrackerService attribute value.
     *
     * @param laborCalculatedSalaryFoundationTrackerService The laborCalculatedSalaryFoundationTrackerService to set.
     */
    @NonTransactional
    public void setLaborCalculatedSalaryFoundationTrackerService(LaborCalculatedSalaryFoundationTrackerService laborCalculatedSalaryFoundationTrackerService) {
        this.laborCalculatedSalaryFoundationTrackerService = laborCalculatedSalaryFoundationTrackerService;
    }
}
