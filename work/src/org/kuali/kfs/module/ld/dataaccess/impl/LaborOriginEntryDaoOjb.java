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
import org.kuali.module.gl.bo.OriginEntry;
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
    private Class entryClass = LaborOriginEntry.class;

    private static final String ENTRY_GROUP_ID = "entryGroupId";
    private static final String ENTRY_ID = "entryId";
    private static final String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
    private static final String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private static final String ACCOUNT_NUMBER = "accountNumber";
    private static final String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    private static final String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private static final String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
    private static final String FINANCIAL_DOCUMENT_REVERSAL_DATE = "financialDocumentReversalDate";
    private static final String UNIVERSITY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
    private static final String UNIVERSITY_FISCAL_YEAR = "universityFiscalYear";
    private static final String FINANCIAL_OBJECT_CODE = "financialObjectCode";
    private static final String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
    private static final String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
    private static final String TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER = "transactionLedgerEntrySequenceNumber";
    private static final String TRANSACTION_LEDGER_ENTRY_DESCRIPTION = "transactionLedgerEntryDescription";
    private static final String TRANSACTION_LEDGER_ENTRY_AMOUNT = "transactionLedgerEntryAmount";
    private static final String TRANSACTION_DEBIT_CREDIT_CODE = "transactionDebitCreditCode";
    
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
        for(OriginEntryGroup group : groups){
            groupIds.add(group.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(PropertyConstants.ENTRY_GROUP_ID, groupIds);

        QueryByCriteria query = QueryFactory.newQuery(this.getEntryClass(), criteria);
        return getPersistenceBrokerTemplate().getIteratorByQuery(query);
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

    /**
     * Gets the entryClass attribute.
     * 
     * @return Returns the entryClass.
     */
    public Class getEntryClass() {
        return entryClass;
    }

    /**
     * Sets the entryClass attribute value.
     * 
     * @param entryClass The entryClass to set.
     */
    public void setEntryClass(Class entryClass) {
        this.entryClass = entryClass;
    }
    
    public Collection<LaborOriginEntry> testingLaborGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        qbc.addOrderByAscending("entryGroupId");
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }
    
    public Iterator<LaborOriginEntry> getLaborEntriesByGroup(OriginEntryGroup oeg, int sort) {
        LOG.debug("getEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ENTRY_GROUP_ID, oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);

        if (sort == OriginEntryDao.SORT_DOCUMENT) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_REVERSAL_DATE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_YEAR);
            // The above order by fields are required by the scrubber process. Adding these
            // fields makes the data in the exact same order as the COBOL scrubber.
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(FINANCIAL_SUB_OBJECT_CODE);
            qbc.addOrderByAscending(FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_AMOUNT);
            qbc.addOrderByAscending(TRANSACTION_DEBIT_CREDIT_CODE);
        } else if (sort == OriginEntryDao.SORT_REPORT ) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_DEBIT_CREDIT_CODE);            
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
        } else if(sort == OriginEntryDao.SORT_LISTING_REPORT ) {
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_YEAR);
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_BALANCE_TYPE_CODE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_DESCRIPTION);            
        }
        else {
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(SUB_ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_TYPE_CODE);
            qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(PropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }
    
}