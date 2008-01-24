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
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.gl.util.OJBUtility;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.AccountStatusBaseFunds;
import org.kuali.module.labor.bo.EmployeeFunding;
import org.kuali.module.labor.bo.LaborCalculatedSalaryFoundationTracker;
import org.kuali.module.labor.dao.LaborCalculatedSalaryFoundationTrackerDao;
import org.kuali.module.labor.util.ConsolidationUtil;

/**
 * This is the data access object for calculated salary foundation tracker
 * 
 * @see org.kuali.module.labor.bo.CalculatedSalaryFoundationTracker
 */
public class LaborCalculatedSalaryFoundationTrackerDaoOjb extends PlatformAwareDaoBaseOjb implements LaborCalculatedSalaryFoundationTrackerDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerDaoOjb.class);

    /**
     * @see org.kuali.module.labor.dao.LaborBaseFundsDao#findCSFTrackers(java.util.Map, boolean)
     */
    public List<LaborCalculatedSalaryFoundationTracker> findCSFTrackers(Map fieldValues, boolean isConsolidated) {
        LOG.info("Start findCSFTrackers()");

        List<LaborCalculatedSalaryFoundationTracker> csfTrackerCollection = new ArrayList<LaborCalculatedSalaryFoundationTracker>();
        if (isConsolidated) {
            List<String> groupByList = getGroupByList(isConsolidated);
            List<String> attributeList = getAttributeListForCSFTracker(isConsolidated, false);

            Iterator<Object[]> queryResults = this.findConsolidatedCSFTrackerRawData(fieldValues, groupByList, attributeList);

            while (queryResults != null && queryResults.hasNext()) {
                csfTrackerCollection.add(this.marshalCSFTracker(queryResults.next()));
            }
        }
        else {
            csfTrackerCollection.addAll(findDetailedCSFTrackerRawData(fieldValues));
        }
        return csfTrackerCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborBaseFundsDao#findCSFTrackersAsAccountStatusBaseFunds(java.util.Map, boolean)
     */
    public List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated) {
        LOG.info("Start findCSFTrackersAsAccountStatusBaseFunds()");

        List<String> groupByList = getGroupByList(isConsolidated);
        List<String> attributeList = getAttributeListForCSFTracker(isConsolidated, false);

        Iterator<Object[]> queryResults = this.findConsolidatedCSFTrackerRawData(fieldValues, groupByList, attributeList);
        List<AccountStatusBaseFunds> baseFundsCollection = new ArrayList<AccountStatusBaseFunds>();
        while (queryResults != null && queryResults.hasNext()) {
            baseFundsCollection.add(this.marshalCSFTrackerAsAccountStatusBaseFunds(queryResults.next()));
        }
        return baseFundsCollection;
    }

    /**
     * @see org.kuali.module.labor.dao.LaborCalculatedSalaryFoundationTrackerDao#findCSFTrackersAsEmployeeFunding(java.util.Map,
     *      boolean)
     */
    public List<EmployeeFunding> findCSFTrackersAsEmployeeFunding(Map fieldValues, boolean isConsolidated) {
        LOG.info("Start findCSFTrackersAsEmployeeFunding()");

        List<LaborCalculatedSalaryFoundationTracker> csfTrackerCollection = findCSFTrackers(fieldValues, isConsolidated);
        List<EmployeeFunding> employeeFundingCollection = new ArrayList<EmployeeFunding>();
        for (LaborCalculatedSalaryFoundationTracker csfTracker : csfTrackerCollection) {
            EmployeeFunding employeeFunding = new EmployeeFunding();
            ObjectUtil.buildObject(employeeFunding, csfTracker);
            employeeFundingCollection.add(employeeFunding);
        }
        return employeeFundingCollection;
    }

    // get the Consolidated CSF trackers according to the given criteria
    private Iterator<Object[]> findConsolidatedCSFTrackerRawData(Map fieldValues, List<String> groupByList, List<String> attributeList) {

        Criteria tempCriteria1 = new Criteria();
        tempCriteria1.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, LaborConstants.DASHES_DELETE_CODE);

        Criteria tempCriteria2 = new Criteria();
        tempCriteria2.addIsNull(KFSPropertyConstants.CSF_DELETE_CODE);

        /* KFSPropertyConstants.CSF_DELETE_CODE = "-" OR is null */
        tempCriteria2.addOrCriteria(tempCriteria1);

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LaborCalculatedSalaryFoundationTracker());
        criteria.addAndCriteria(tempCriteria2);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LaborCalculatedSalaryFoundationTracker.class, criteria);

        String[] groupBy = (String[]) groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        String[] attributes = (String[]) attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // get the detailed CSF trackers according to the given criteria
    private Collection<LaborCalculatedSalaryFoundationTracker> findDetailedCSFTrackerRawData(Map fieldValues) {

        Criteria tempCriteria1 = new Criteria();
        tempCriteria1.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, LaborConstants.DASHES_DELETE_CODE);

        Criteria tempCriteria2 = new Criteria();
        tempCriteria2.addIsNull(KFSPropertyConstants.CSF_DELETE_CODE);

        /* KFSPropertyConstants.CSF_DELETE_CODE = "-" OR is null */
        tempCriteria2.addOrCriteria(tempCriteria1);

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LaborCalculatedSalaryFoundationTracker());
        criteria.addAndCriteria(tempCriteria2);

        Query query = QueryFactory.newQuery(LaborCalculatedSalaryFoundationTracker.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    // marshal into CalculatedSalaryFoundationTracker from the query result
    private LaborCalculatedSalaryFoundationTracker marshalCSFTracker(Object[] queryResult) {
        LaborCalculatedSalaryFoundationTracker CSFTracker = new LaborCalculatedSalaryFoundationTracker();
        List<String> keyFields = this.getAttributeListForCSFTracker(false, true);

        ObjectUtil.buildObject(CSFTracker, queryResult, keyFields);
        return CSFTracker;
    }

    // marshal into AccountStatusBaseFunds from the query results
    private AccountStatusBaseFunds marshalCSFTrackerAsAccountStatusBaseFunds(Object[] queryResult) {
        AccountStatusBaseFunds baseFunds = new AccountStatusBaseFunds();
        List<String> keyFields = this.getAttributeListForCSFTracker(false, true);

        ObjectUtil.buildObject(baseFunds, queryResult, keyFields);
        return baseFunds;
    }

    // marshal into EmployeeFunding from the query results
    private EmployeeFunding marshalCSFTrackerAsEmployeeFunding(Object[] queryResult) {
        EmployeeFunding employeeFunding = new EmployeeFunding();
        List<String> keyFields = this.getAttributeListForCSFTracker(false, true);

        ObjectUtil.buildObject(employeeFunding, queryResult, keyFields);
        return employeeFunding;
    }

    // define a list of attributes that are used as the grouping criteria
    private List<String> getGroupByList(boolean isConsolidated) {
        List<String> groupByList = new ArrayList<String>();
        groupByList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        groupByList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        groupByList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        groupByList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);

        if (!isConsolidated) {
            groupByList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            groupByList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        }
        return groupByList;
    }

    // define the return attribute list
    private List<String> getAttributeList(boolean isConsolidated) {
        List<String> attributeList = getGroupByList(isConsolidated);

        if (isConsolidated) {
            attributeList.add("'" + Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER + "'");
            attributeList.add("'" + Constant.CONSOLIDATED_SUB_OBJECT_CODE + "'");
        }
        return attributeList;
    }

    // define the return attribute list for CSF traker query
    private List<String> getAttributeListForCSFTracker(boolean isConsolidated, boolean isAttributeNameNeeded) {
        List<String> attributeList = getAttributeList(isConsolidated);

        if (!isAttributeNameNeeded) {
            attributeList.add(ConsolidationUtil.sum(KFSPropertyConstants.CSF_FULL_TIME_EMPLOYMENT_QUANTITY));
            attributeList.add(ConsolidationUtil.sum(KFSPropertyConstants.CSF_AMOUNT));
        }
        else {
            attributeList.add(KFSPropertyConstants.CSF_FULL_TIME_EMPLOYMENT_QUANTITY);
            attributeList.add(KFSPropertyConstants.CSF_AMOUNT);
        }
        return attributeList;
    }
}
