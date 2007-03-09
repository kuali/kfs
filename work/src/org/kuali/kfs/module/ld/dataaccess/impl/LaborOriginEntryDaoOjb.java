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

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.ojb.OriginEntryDaoOjb;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;


/**
 * This class...
 */
public class LaborOriginEntryDaoOjb extends OriginEntryDaoOjb implements LaborOriginEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborOriginEntryDaoOjb.class);

    /**
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroup(OriginEntryGroup group) {
        LOG.debug("getEntriesByGroup() started");
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.ENTRY_GROUP_ID, group.getId());
        
        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getEntriesByGroups(java.util.Collection)
     */
    public Iterator<LaborOriginEntry> getEntriesByGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("getEntriesByGroups() started");

        // extract the group ids of the given groups
        List<Integer> groupIds = new ArrayList<Integer>();
        for (OriginEntryGroup group : groups) {
            groupIds.add(group.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(PropertyConstants.ENTRY_GROUP_ID, groupIds);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getCountOfEntriesInGroups(java.util.Collection)
     */
    public int getCountOfEntriesInGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("getCountOfEntriesInGroups() started");
        
        if(groups.size()==0) return 0;

        // extract the group ids of the given groups
        List<Integer> groupIds = new ArrayList<Integer>();
        for (OriginEntryGroup group : groups) {
            groupIds.add(group.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(PropertyConstants.ENTRY_GROUP_ID, groupIds);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getConsolidatedEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<Object[]> getConsolidatedEntriesByGroup(OriginEntryGroup group) {
        LOG.debug("getConsolidatedEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.ENTRY_GROUP_ID, group.getId());

        ReportQueryByCriteria query = QueryFactory.newReportQuery(this.getEntryClass(), criteria);

        // set the selection attributes
        List<String> attributeList = buildConsolidationAttributeList();
        String[] attributes = attributeList.toArray(new String[attributeList.size()]);
        query.setAttributes(attributes);

        // add the group criteria into the selection statement
        List<String> groupByList = buildGroupByList();
        String[] groupBy = groupByList.toArray(new String[groupByList.size()]);
        query.addGroupBy(groupBy);

        // add the sorting criteria into the selection statement
        for (String attribute : groupByList) {
            query.addOrderByAscending(attribute);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    private List<String> buildConsolidationAttributeList() {
        List<String> attributeList = this.buildGroupByList();
        attributeList.add("sum(" + PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")");
        return attributeList;
    }

    private List<String> buildGroupByList() {
        List<String> groupByList = new ArrayList<String>(LaborConstants.consolidationAttributesOfOriginEntry());
        groupByList.remove(PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
        return groupByList;
    }

    public Collection<LaborOriginEntry> testingLaborGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(getEntryClass(), criteria);
        qbc.addOrderByAscending("entryGroupId");
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    public Iterator<LaborOriginEntry> getLaborEntriesByGroup(OriginEntryGroup oeg, int sort) {
        LOG.debug("getEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(PropertyConstants.ENTRY_GROUP_ID, oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(getEntryClass(), criteria);

        if (sort == OriginEntryDao.SORT_DOCUMENT) {
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE);
            qbc.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
            // The above order by fields are required by the scrubber process. Adding these
            // fields makes the data in the exact same order as the COBOL scrubber.
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        }
        else if (sort == OriginEntryDao.SORT_REPORT) {
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
            qbc.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        else if (sort == OriginEntryDao.SORT_LISTING_REPORT) {
            qbc.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
            qbc.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        }
        else {
            qbc.addOrderByAscending(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(PropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(PropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }
}