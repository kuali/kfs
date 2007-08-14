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
package org.kuali.module.labor.dao.ojb;

import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.APRIL;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.AUGUST;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.DECEMBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.FEBRUARY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.JANUARY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.JULY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.JUNE;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.MARCH;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.MAY;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.NOVEMBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.OCTOBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.SEPTEMBER;
import static org.kuali.module.labor.LaborPropertyConstants.AccountingPeriodProperties.YEAR_END;
import static org.kuali.module.labor.util.ConsolidationUtil.buildConsolidatedQuery;
import static org.kuali.module.labor.util.ConsolidationUtil.buildGroupByCollection;
import static org.kuali.module.labor.util.ConsolidationUtil.sum;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.dao.LaborLedgerBalanceDao;

public class LaborLedgerBalanceDaoOjb extends PlatformAwareDaoBaseOjb implements LaborLedgerBalanceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborLedgerBalanceDaoOjb.class);
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findBalancesForFiscalYear(java.lang.Integer)
     */
    public Iterator<LedgerBalance> findBalancesForFiscalYear(Integer year) {
        LOG.debug("findBalancesForFiscalYear() started");

        Criteria c = new Criteria();
        c.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, year);

        QueryByCriteria query = QueryFactory.newQuery(LedgerBalance.class, c);
        query.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        query.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        query.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#findBalance(java.util.Map, boolean)
     */
    public Iterator<LedgerBalance> findBalance(Map fieldValues, boolean isConsolidated) {
        LOG.debug("findBalance() started");

        Query query = this.getBalanceQuery(fieldValues, isConsolidated);
        OJBUtility.limitResultSize(query);

        if (isConsolidated) {
            return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        }
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborLedgerBalanceDao#getConsolidatedBalanceRecordCount(java.util.Map)
     */
    public Iterator getConsolidatedBalanceRecordCount(Map fieldValues) {
        LOG.debug("getBalanceRecordCount() started");

        ReportQueryByCriteria query = this.getBalanceCountQuery(fieldValues);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // build the query for balance search
    private Query getBalanceQuery(Map fieldValues, boolean isConsolidated) {
        LOG.debug("getBalanceQuery(Map, boolean) started");
        LOG.debug("Building criteria from map fields: " + fieldValues.keySet());

        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // if consolidated, then ignore subaccount number and balance type code
        if (isConsolidated) {
            buildConsolidatedQuery(query, sum(JULY.propertyName), sum(AUGUST.propertyName), sum(SEPTEMBER.propertyName), sum(OCTOBER.propertyName), sum(NOVEMBER.propertyName), sum(DECEMBER.propertyName), sum(JANUARY.propertyName), sum(FEBRUARY.propertyName), sum(MARCH.propertyName), sum(APRIL.propertyName), sum(MAY.propertyName), sum(JUNE.propertyName), sum(YEAR_END.propertyName));
        }

        return query;
    }

    // build the query for balance search
    private ReportQueryByCriteria getBalanceCountQuery(Map fieldValues) {
        Criteria criteria = buildCriteriaFromMap(fieldValues, new LedgerBalance());
        ReportQueryByCriteria query = QueryFactory.newReportQuery(LedgerBalance.class, criteria);

        // set the selection attributes
        query.setAttributes(new String[] { "count(*)" });

        Collection<String> groupByList = buildGroupByCollection();
        groupByList.remove(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.remove(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);

        // add the group criteria into the selection statement
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);
        return query;
    }

    /**
     * This method builds the query criteria based on the input field map
     * 
     * @param fieldValues
     * @param balance
     * @return a query criteria
     */
    private Criteria buildCriteriaFromMap(Map fieldValues, LedgerBalance balance) {
        Map localFieldValues = new HashMap();
        localFieldValues.putAll(fieldValues);

        Criteria criteria = new Criteria();

        // handle encumbrance balance type
        String propertyName = KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE;
        if (localFieldValues.containsKey(propertyName)) {
            String propertyValue = (String) localFieldValues.get(propertyName);
            if (KFSConstants.AGGREGATE_ENCUMBRANCE_BALANCE_TYPE_CODE.equals(propertyValue)) {
                localFieldValues.remove(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
                criteria.addIn(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getEncumbranceBalanceTypeCodeList());
            }
        }

        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(localFieldValues, new LedgerBalance()));
        return criteria;
    }

    private List<String> getEncumbranceBalanceTypeCodeList() {
        String[] balanceTypesAsArray = kualiConfigurationService.getApplicationParameterValues("Kuali.GeneralLedger.AvailableBalanceInquiry", "GeneralLedger.BalanceInquiry.AvailableBalances.EncumbranceDrillDownBalanceTypes");
        return Arrays.asList(balanceTypesAsArray);
    }

    /**
     * @param kualiConfigurationService
     */
    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }
}