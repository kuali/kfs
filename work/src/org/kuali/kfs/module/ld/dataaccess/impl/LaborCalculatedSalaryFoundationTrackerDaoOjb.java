/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.dataaccess.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.OJBUtility;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.AccountStatusBaseFunds;
import org.kuali.kfs.module.ld.businessobject.EmployeeFunding;
import org.kuali.kfs.module.ld.businessobject.LaborCalculatedSalaryFoundationTracker;
import org.kuali.kfs.module.ld.dataaccess.LaborCalculatedSalaryFoundationTrackerDao;
import org.kuali.kfs.module.ld.util.ConsolidationUtil;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * This is the data access object for calculated salary foundation tracker
 *
 * @see org.kuali.kfs.module.ld.businessobject.CalculatedSalaryFoundationTracker
 */
public class LaborCalculatedSalaryFoundationTrackerDaoOjb extends PlatformAwareDaoBaseOjb implements LaborCalculatedSalaryFoundationTrackerDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerDaoOjb.class);

    /**
     * @see org.kuali.kfs.module.ld.dataaccess.LaborBaseFundsDao#findCSFTrackers(java.util.Map, boolean)
     */
    @Override
    public List<LaborCalculatedSalaryFoundationTracker> findCSFTrackers(Map fieldValues, boolean isConsolidated) {
        LOG.debug("Start findCSFTrackers()");

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
     * @see org.kuali.kfs.module.ld.dataaccess.LaborBaseFundsDao#findCSFTrackersAsAccountStatusBaseFunds(java.util.Map, boolean)
     */
    @Override
    public List<AccountStatusBaseFunds> findCSFTrackersAsAccountStatusBaseFunds(Map fieldValues, boolean isConsolidated) {
        LOG.debug("Start findCSFTrackersAsAccountStatusBaseFunds()");

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
     * @see org.kuali.kfs.module.ld.dataaccess.LaborCalculatedSalaryFoundationTrackerDao#findCSFTrackersAsEmployeeFunding(java.util.Map,
     *      boolean)
     */
    @Override
    public List<EmployeeFunding> findCSFTrackersAsEmployeeFunding(Map fieldValues, boolean isConsolidated) {
        LOG.debug("Start findCSFTrackersAsEmployeeFunding()");

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
    protected Iterator<Object[]> findConsolidatedCSFTrackerRawData(Map fieldValues, List<String> groupByList, List<String> attributeList) {

        Criteria tempCriteria1 = new Criteria();
        tempCriteria1.addEqualTo(KFSPropertyConstants.CSF_DELETE_CODE, LaborConstants.DASHES_DELETE_CODE);

        Criteria tempCriteria2 = new Criteria();
        tempCriteria2.addIsNull(KFSPropertyConstants.CSF_DELETE_CODE);

        /* KFSPropertyConstants.CSF_DELETE_CODE = "-" OR is null */
        tempCriteria2.addOrCriteria(tempCriteria1);

        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LaborCalculatedSalaryFoundationTracker());
        criteria.addAndCriteria(tempCriteria2);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(LaborCalculatedSalaryFoundationTracker.class, criteria);

        String[] groupBy = groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        String[] attributes = attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    // get the detailed CSF trackers according to the given criteria
    protected Collection<LaborCalculatedSalaryFoundationTracker> findDetailedCSFTrackerRawData(Map fieldValues) {
        Criteria criteria = OJBUtility.buildCriteriaFromMap(fieldValues, new LaborCalculatedSalaryFoundationTracker());
        Query query = QueryFactory.newQuery(LaborCalculatedSalaryFoundationTracker.class, criteria);
        return getPersistenceBrokerTemplate().getCollectionByQuery(query);
    }

    // marshal into CalculatedSalaryFoundationTracker from the query result
    protected LaborCalculatedSalaryFoundationTracker marshalCSFTracker(Object[] queryResult) {
        LaborCalculatedSalaryFoundationTracker CSFTracker = new LaborCalculatedSalaryFoundationTracker();
        List<String> keyFields = this.getAttributeListForCSFTracker(false, true);

        ObjectUtil.buildObject(CSFTracker, queryResult, keyFields);
        return CSFTracker;
    }

    // marshal into AccountStatusBaseFunds from the query results
    protected AccountStatusBaseFunds marshalCSFTrackerAsAccountStatusBaseFunds(Object[] queryResult) {
        AccountStatusBaseFunds baseFunds = new AccountStatusBaseFunds();
        List<String> keyFields = this.getAttributeListForCSFTracker(false, true);

        ObjectUtil.buildObject(baseFunds, queryResult, keyFields);
        return baseFunds;
    }

    // marshal into EmployeeFunding from the query results
    protected EmployeeFunding marshalCSFTrackerAsEmployeeFunding(Object[] queryResult) {
        EmployeeFunding employeeFunding = new EmployeeFunding();
        List<String> keyFields = this.getAttributeListForCSFTracker(false, true);

        ObjectUtil.buildObject(employeeFunding, queryResult, keyFields);
        return employeeFunding;
    }

    // define a list of attributes that are used as the grouping criteria
    protected List<String> getGroupByList(boolean isConsolidated) {
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
    protected List<String> getAttributeList(boolean isConsolidated) {
        List<String> attributeList = getGroupByList(isConsolidated);

        if (isConsolidated) {
            attributeList.add("'" + Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER + "'");
            attributeList.add("'" + Constant.CONSOLIDATED_SUB_OBJECT_CODE + "'");
        }
        return attributeList;
    }

    // define the return attribute list for CSF traker query
    protected List<String> getAttributeListForCSFTracker(boolean isConsolidated, boolean isAttributeNameNeeded) {
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
