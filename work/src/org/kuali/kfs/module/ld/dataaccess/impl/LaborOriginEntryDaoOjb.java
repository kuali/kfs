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
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.ojb.OriginEntryDaoOjb;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.dao.LaborOriginEntryDao;


public class LaborOriginEntryDaoOjb extends OriginEntryDaoOjb implements LaborOriginEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborOriginEntryDaoOjb.class);

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
        criteria.addIn(KFSPropertyConstants.ENTRY_GROUP_ID, groupIds);

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
        criteria.addIn(KFSPropertyConstants.ENTRY_GROUP_ID, groupIds);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getCount(query);
    }

    /**
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getConsolidatedEntriesByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<Object[]> getConsolidatedEntriesByGroup(OriginEntryGroup group) {
        LOG.debug("getConsolidatedEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.ENTRY_GROUP_ID, group.getId());

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
        attributeList.add("sum(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")");
        return attributeList;
    }

    private List<String> buildGroupByList() {
        List<String> groupByList = new ArrayList<String>(LaborConstants.consolidationAttributesOfOriginEntry());
        groupByList.remove(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
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
        criteria.addEqualTo(KFSPropertyConstants.ENTRY_GROUP_ID, oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(getEntryClass(), criteria);

        if (sort == OriginEntryDao.SORT_DOCUMENT) {
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE);
            qbc.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            // The above order by fields are required by the scrubber process. Adding these
            // fields makes the data in the exact same order as the COBOL scrubber.
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        }
        else if (sort == OriginEntryDao.SORT_REPORT) {
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        }
        else if (sort == OriginEntryDao.SORT_LISTING_REPORT) {
            qbc.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        }
        else {
            qbc.addOrderByAscending(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.ACCOUNT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }
    
    
    /**
     * 
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(this.getEntryClass(), criteria);
        qbc.addOrderByAscending("entryGroupId");
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    /**
     * @param entry the entry to save.
     */
    public void saveOriginEntry(LaborOriginEntry entry) {
        LOG.debug("saveOriginEntry() started");

        if ((entry != null) && (entry.getTransactionLedgerEntryDescription() != null) && (entry.getTransactionLedgerEntryDescription().length() > 40)) {
            entry.setTransactionLedgerEntryDescription(entry.getTransactionLedgerEntryDescription().substring(0, 40));
        }
        getPersistenceBrokerTemplate().store(entry);
    }
    
    /**
     * @see org.kuali.module.labor.dao.LaborOriginEntryDao#getSummaryByGroupId(java.util.List)
     */
    public Iterator getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");
        
        if(groupIdList == null || groupIdList.size()<=0) {
            return null;
        }

        Collection ids = new ArrayList();
        for (Iterator iter = groupIdList.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.ENTRY_GROUP_ID, ids);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(getEntryClass(), criteria);

        String attributeList[] = { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, "sum(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")", "count(*)" };

        String groupList[] = { KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE };

        query.setAttributes(attributeList);
        query.addGroupBy(groupList);

        // add the sorting criteria
        for (int i = 0; i < groupList.length; i++) {
            query.addOrderByAscending(groupList[i]);
        }

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }
}