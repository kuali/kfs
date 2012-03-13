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
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the data access object for ledger entry.
 * 
 * @see org.kuali.kfs.module.ld.businessobject.LedgerEntry
 */
public class LaborLedgerEntryDaoOjb extends PlatformAwareDaoBaseOjb implements LaborLedgerEntryDao, LedgerEntryBalancingDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerEntryDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao#getMaxSquenceNumber(org.kuali.kfs.module.ld.businessobject.LedgerEntry)
     */
    public Integer getMaxSquenceNumber(LedgerEntry ledgerEntry) {
        Criteria criteria = new Criteria();

        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, ledgerEntry.getUniversityFiscalYear());
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, ledgerEntry.getChartOfAccountsCode());
        criteria.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, ledgerEntry.getAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, ledgerEntry.getSubAccountNumber());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, ledgerEntry.getFinancialObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, ledgerEntry.getFinancialSubObjectCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, ledgerEntry.getFinancialBalanceTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, ledgerEntry.getFinancialObjectTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, ledgerEntry.getUniversityFiscalPeriodCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, ledgerEntry.getFinancialDocumentTypeCode());
        criteria.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, ledgerEntry.getFinancialSystemOriginationCode());
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, ledgerEntry.getDocumentNumber());

        ReportQueryByCriteria query = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        query.setAttributes(new String[] { "max(" + KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER + ")" });

        Iterator iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        Integer maxSequenceNumber = Integer.valueOf(0);

        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            if (data[0] != null) {
                maxSequenceNumber = ((BigDecimal) data[0]).intValue();
            }
        }
        return maxSequenceNumber;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao#find(java.util.Map)
     */
    public Iterator<LedgerEntry> find(Map<String, String> fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LedgerEntry());

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao#findEmployeesWithPayType(java.util.Map, java.util.List, java.util.List)
     */
    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        Criteria criteria = this.buildPayTypeCriteria(payPeriods, balanceTypes, earnCodePayGroupMap);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        query.setAttributes(new String[] { KFSPropertyConstants.EMPLID });
        query.setDistinct(true);

        Iterator<Object[]> employees = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        List<String> employeeList = new ArrayList<String>();

        while (employees != null && employees.hasNext()) {
            Object[] emplid = employees.next();
            employeeList.add(emplid == null ? "" : emplid[0].toString());
        }

        return employeeList;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao#getLedgerEntriesForEmployeeWithPayType(java.lang.String, java.util.Map,
     *      java.util.List, java.util.Map)
     */
    public Collection<LedgerEntry> getLedgerEntriesForEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        Criteria criteria = this.buildPayTypeCriteria(payPeriods, balanceTypes, earnCodePayGroupMap);
        criteria.addEqualTo(KFSPropertyConstants.EMPLID, emplid);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao#isEmployeeWithPayType(java.lang.String, java.util.Map, java.util.List,
     *      java.util.Map)
     */
    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        Criteria criteria = this.buildPayTypeCriteria(payPeriods, balanceTypes, earnCodePayGroupMap);
        criteria.addEqualTo(KFSPropertyConstants.EMPLID, emplid);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getCount(query) > 0;
    }

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborLedgerEntryDao#deleteLedgerEntriesPriorToYear(java.lang.Integer, java.lang.String)
     */
    public void deleteLedgerEntriesPriorToYear(Integer fiscalYear, String chartOfAccountsCode) {
        LOG.debug("deleteLedgerEntriesPriorToYear() started");

        Criteria criteria = new Criteria();
        criteria.addLessThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
        criteria.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);

        QueryByCriteria query = new QueryByCriteria(this.getEntryClass(), criteria);
        getPersistenceBrokerTemplate().deleteByQuery(query);

    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao#findEntryByGroup(java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public Object[] findEntryByGroup(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode, String financialBalanceTypeCode, String universityFiscalPeriodCode, String transactionDebitCreditCode) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, universityFiscalYear);
        criteria.addEqualTo(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, chartOfAccountsCode);
        criteria.addEqualTo(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, financialObjectCode);
        criteria.addEqualTo(KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, financialBalanceTypeCode);
        criteria.addEqualTo(KFSConstants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, universityFiscalPeriodCode);
        criteria.addEqualTo(KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE, transactionDebitCreditCode);

        ReportQueryByCriteria reportQuery = QueryFactory.newReportQuery(this.getEntryClass(), criteria);
        reportQuery.setAttributes(new String[] { "count(*)", ConsolidationUtil.sum(KFSConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT)});
        reportQuery.addGroupBy(new String[] { KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, KFSConstants.FINANCIAL_BALANCE_TYPE_CODE_PROPERTY_NAME, KFSConstants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, KFSConstants.TRANSACTION_DEBIT_CREDIT_CODE});
        
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(reportQuery);
        Object[] returnResult = TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
        
        if (ObjectUtils.isNull(returnResult)) {
            // Do nothing, we'll return null. Data wasn't found.
        } else if (returnResult[0] instanceof BigDecimal) {
            returnResult[0] = ((BigDecimal) returnResult[0]).intValue();
        }
        else {
            returnResult[0] = ((Long) returnResult[0]).intValue();
        }
        
        return returnResult;
    }
    
    // build the pay type criteria
    protected Criteria buildPayTypeCriteria(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        Criteria criteria = new Criteria();

        Criteria criteriaForPayPeriods = new Criteria();
        for (Integer fiscalYear : payPeriods.keySet()) {
            Criteria criteriaForFiscalYear = new Criteria();

            criteriaForFiscalYear.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, fiscalYear);
            criteriaForFiscalYear.addIn(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, payPeriods.get(fiscalYear));

            criteriaForPayPeriods.addOrCriteria(criteriaForFiscalYear);
        }

        Criteria criteriaForBalanceTypes = new Criteria();
        criteriaForBalanceTypes.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, balanceTypes);

        Criteria criteriaForEarnCodePayGroup = new Criteria();
        for (String payGroup : earnCodePayGroupMap.keySet()) {
            Criteria criteriaForEarnPay = new Criteria();

            criteriaForEarnPay.addEqualTo(KFSPropertyConstants.PAY_GROUP, payGroup);
            criteriaForEarnPay.addIn(KFSPropertyConstants.EARN_CODE, earnCodePayGroupMap.get(payGroup));

            criteriaForEarnCodePayGroup.addOrCriteria(criteriaForEarnPay);
        }

        criteria.addAndCriteria(criteriaForPayPeriods);
        criteria.addAndCriteria(criteriaForBalanceTypes);
        criteria.addAndCriteria(criteriaForEarnCodePayGroup);

        return criteria;
    }

    /**
     * @see org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao#findCountGreaterOrEqualThan(java.lang.Integer)
     */
    public Integer findCountGreaterOrEqualThan(Integer year) {
        Criteria criteria = new Criteria();
        criteria.addGreaterOrEqualThan(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);
        
        ReportQueryByCriteria query = QueryFactory.newReportQuery(getEntryClass(), criteria);
        
        return getPersistenceBrokerTemplate().getCount(query);
    }
    
    /**
     * @return the Class type of the business object accessed and managed
     */
    protected Class getEntryClass() {
        return LedgerEntry.class;
    }
}
