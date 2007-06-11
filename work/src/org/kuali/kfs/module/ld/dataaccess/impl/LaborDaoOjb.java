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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.AccountStatusCurrentFunds;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.dao.LaborDao;

/**
 * This class is for Labor Distribution DAO balance inquiries
 */
public class LaborDaoOjb extends PlatformAwareDaoBaseOjb implements LaborDao {
    private LaborDaoOjb dao;

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getCSFTrackerData(java.util.Map)
     */
    public Collection getCSFTrackerData(Map fieldValues) {
        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new CalculatedSalaryFoundationTracker()));
        LookupUtils.applySearchResultsLimit(criteria, getDbPlatform());
        QueryByCriteria query = QueryFactory.newQuery(CalculatedSalaryFoundationTracker.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getCSFTrackerData(java.util.Map)
     */
    public Object getCSFTrackerTotal(Map fieldValues) {

        Criteria criteria = new Criteria();
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new CalculatedSalaryFoundationTracker()));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(CalculatedSalaryFoundationTracker.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);

        query.setAttributes(new String[] { "sum(" + KFSPropertyConstants.CSF_AMOUNT + ")" });
        query.addGroupBy(groupBy);

        Object[] csf = null;

        Iterator<Object[]> calculatedSalaryFoundationTracker = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (calculatedSalaryFoundationTracker != null && calculatedSalaryFoundationTracker.hasNext()) {
            csf = calculatedSalaryFoundationTracker.next();
        }
        KualiDecimal csfAmount = new KualiDecimal("0");
        if (csf != null)
            csfAmount = new KualiDecimal(csf[0].toString());
        return csfAmount;
    }

    /**
     * 
     * @see org.kuali.module.labor.dao.LaborDao#getEncumbranceTotal(java.util.Map)
     */
    public Object getEncumbranceTotal(Map fieldValues) {

        Criteria criteria = new Criteria();
        criteria.addEqualToField("financialBalanceTypeCode", LaborConstants.BalanceInquiries.ENCUMBERENCE_CODE);  // Encumberance Balance Type
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatusCurrentFunds()));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(AccountStatusCurrentFunds.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);

        query.setAttributes(new String[] { "sum(" + LaborConstants.BalanceInquiries.ANNUAL_BALANCE + ") + sum(" + LaborConstants.BalanceInquiries.CONTRACT_GRANT_BB_AMOUNT +")"});
        query.addGroupBy(groupBy);

        Object[] encumbrances = null;

        Iterator<Object[]> accountStatusCurrentFunds = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
        while (accountStatusCurrentFunds != null && accountStatusCurrentFunds.hasNext()) {
            encumbrances = accountStatusCurrentFunds.next();
             }
        KualiDecimal encumbranceTotal = new KualiDecimal("0");
        if (encumbrances != null)
            encumbranceTotal = new KualiDecimal(encumbrances[0].toString());
        return encumbranceTotal;
        }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getBaseFunds(java.util.Map)
     */
    public Collection getBaseFunds(Map fieldValues) {
        Criteria criteria = new Criteria();
        criteria.addEqualToField("financialBalanceTypeCode", LaborConstants.BalanceInquiries.BALANCE_CODE);
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatusBaseFunds()));

        QueryByCriteria query = QueryFactory.newQuery(AccountStatusBaseFunds.class, criteria);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborDao#getCurrentFunds(java.util.Map)
     */
    public Collection getCurrentFunds(Map fieldValues) {
        Criteria criteria = new Criteria();
        criteria.addEqualToField("financialBalanceTypeCode", LaborConstants.BalanceInquiries.ACTUALS_CODE);
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new AccountStatusCurrentFunds()));
        QueryByCriteria query = QueryFactory.newQuery(AccountStatusCurrentFunds.class, criteria);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }
    /**
     * @see org.kuali.module.labor.dao.LaborDao#getEmployeeFunding(java.util.Map)
     */
    public Iterator getEmployeeFunding(Map fieldValues) {

        ArrayList objectTypeCodes = new ArrayList();
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_EXPENSE_OBJECT_TYPE_CODE);
        objectTypeCodes.add(LaborConstants.BalanceInquiries.EMPLOYEE_FUNDING_NORMAL_OP_EXPENSE_OBJECT_TYPE_CODE);

        Criteria criteria = new Criteria();
        criteria.addEqualToField("financialBalanceTypeCode", LaborConstants.BalanceInquiries.ACTUALS_CODE);
        criteria.addIn("financialObjectTypeCode", objectTypeCodes);
        criteria.addAndCriteria(OJBUtility.buildCriteriaFromMap(fieldValues, new EmployeeFunding()));
        ReportQueryByCriteria query = QueryFactory.newReportQuery(EmployeeFunding.class, criteria);

        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        groupByList.add(KFSPropertyConstants.POSITION_NUMBER);
        groupByList.add(KFSPropertyConstants.EMPLID);
        groupByList.add("accountLineAnnualBalanceAmount");
        
        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        List<String> attributeList = new ArrayList<String>(groupByList);
        attributeList.add(0, "sum(" + KFSPropertyConstants.ACCOUNTING_LINE_ANNUAL_BALANCE_AMOUNT + ")");
        query.setAttributes((String[]) attributeList.toArray(new String[attributeList.size()]));

        query.addGroupBy(groupBy);
        OJBUtility.limitResultSize(query);
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

}