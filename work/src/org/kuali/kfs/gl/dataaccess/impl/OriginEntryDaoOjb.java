/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.dao.ojb;

import java.math.BigDecimal;
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
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.KFSUtils;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryable;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;

public class OriginEntryDaoOjb extends PlatformAwareDaoBaseOjb implements OriginEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryDaoOjb.class);

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
    
    private Class entryClass;

    public void setEntryClass(Class entryClass) {
        this.entryClass = entryClass;
    }

    /**
     * Gets the entryClass attribute. 
     * @return Returns the entryClass.
     */
    public Class getEntryClass() {
        return entryClass;
    }

    public OriginEntryDaoOjb() {
        super();
    }

    public KualiDecimal getGroupTotal(Integer groupId, boolean isCredit) {
        LOG.debug("getGroupTotal() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(OriginEntryDaoOjb.ENTRY_GROUP_ID, groupId);
        if ( isCredit ) {
            crit.addEqualTo(OriginEntryDaoOjb.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
        } else {
            crit.addNotEqualTo(OriginEntryDaoOjb.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
        }

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, crit);
        q.setAttributes(new String[] { "SUM(" + OriginEntryDaoOjb.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if ( i.hasNext() ) {
            Object[] data = (Object[]) KFSUtils.retrieveFirstAndExhaustIterator(i);
            return (KualiDecimal)data[0];
        } else {
            return null;
        }
    }

    public Integer getGroupCount(Integer groupId) {
        LOG.debug("getGroupCount() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(OriginEntryDaoOjb.ENTRY_GROUP_ID, groupId);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, crit);
        q.setAttributes(new String[] { "count(*)" });

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if ( i.hasNext() ) {
            Object[] data = (Object[]) KFSUtils.retrieveFirstAndExhaustIterator(i);

            if (data[0] instanceof BigDecimal) {
                return ((BigDecimal)data[0]).intValue();
            } else {
                return ((Long)data[0]).intValue();
            }
        } else {
            return null;
        }
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getGroupCounts()
     */
    public Iterator getGroupCounts() {
        LOG.debug("getGroupCounts() started");

        Criteria crit = new Criteria();

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, crit);
        q.setAttributes(new String[] { ENTRY_GROUP_ID, "count(*)" });
        q.addGroupBy(ENTRY_GROUP_ID);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#deleteEntry(org.kuali.module.gl.bo.OriginEntryable)
     */
    public void deleteEntry(OriginEntryable oe) {
        LOG.debug("deleteEntry() started");

        getPersistenceBrokerTemplate().delete(oe);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ENTRY_GROUP_ID, oeg.getId());

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, criteria);
        q.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER,"financialDocumentTypeCode","financialSystemOriginationCode" });

        q.setDistinct(true);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getMatchingEntries(java.util.Map)
     */
    public Iterator<OriginEntry> getMatchingEntries(Map searchCriteria) {
        LOG.debug("getMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    public Iterator<OriginEntry> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");

        if(groups.size()<=0) {
            return null;
        }
        
        Collection ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria crit1 = new Criteria();
        crit1.addIn(ENTRY_GROUP_ID, ids);

        Criteria crit2 = new Criteria();
        crit2.addIsNull(FINANCIAL_BALANCE_TYPE_CODE);

        Criteria crit3 = new Criteria();
        crit3.addEqualTo(FINANCIAL_BALANCE_TYPE_CODE, "  ");

        crit2.addOrCriteria(crit3);

        crit1.addAndCriteria(crit2);

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, crit1);
        qbc.addOrderByAscending(UNIVERSITY_FISCAL_YEAR);
        qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
        qbc.addOrderByAscending(ACCOUNT_NUMBER);
        qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
        qbc.addOrderByAscending(FINANCIAL_OBJECT_TYPE_CODE);
        qbc.addOrderByAscending(FINANCIAL_BALANCE_TYPE_CODE);
        qbc.addOrderByAscending(UNIVERSITY_FISCAL_PERIOD_CODE);
        qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
        qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
        qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * This method is special because of the order by. It is used in the scrubber. The getMatchingEntries wouldn't work because of
     * the required order by.
     * 
     */
    public <T> Iterator<T> getEntriesByGroup(OriginEntryGroup oeg, int sort) {
        LOG.debug("getEntriesByGroup() started");

        // clear cache because the GLCP document class saves to the origin entry table and
        // reads from it (via this method) in the same transaction.  If the clearCache line is
        // deleted, then the references to OriginEntries returned by this method will be null.
        getPersistenceBrokerTemplate().clearCache();
        
        Criteria criteria = new Criteria();
        criteria.addEqualTo(ENTRY_GROUP_ID, oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);

        if (sort == OriginEntryDao.SORT_DOCUMENT) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
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
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_SEQUENCE_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_AMOUNT);
            qbc.addOrderByAscending(TRANSACTION_DEBIT_CREDIT_CODE);
        } else if (sort == OriginEntryDao.SORT_REPORT ) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
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
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
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
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_LEDGER_ENTRY_DESCRIPTION);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * This method should only be used in unit tests. It loads all the gl_origin_entry_t rows in memory into a collection. This
     * won't work for production because there would be too many rows to load into memory.
     * 
     * @return
     */
    public Collection<OriginEntry> testingGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        qbc.addOrderByAscending(ENTRY_ID);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @param entry the entry to save.
     */
    public void saveOriginEntry(OriginEntryable entry) {
        LOG.debug("saveOriginEntry() started");

        if ((entry != null) && (entry.getTransactionLedgerEntryDescription() != null) && (entry.getTransactionLedgerEntryDescription().length() > 40)) {
            entry.setTransactionLedgerEntryDescription(entry.getTransactionLedgerEntryDescription().substring(0, 40));
        }
        getPersistenceBrokerTemplate().store(entry);
    }

    /**
     * Delete entries matching searchCriteria search criteria.
     * 
     * @param searchCriteria
     */
    public void deleteMatchingEntries(Map searchCriteria) {
        LOG.debug("deleteMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // This is required because deleteByQuery leaves the cache alone so future queries
        // could return origin entries that don't exist. Clearing the cache makes OJB
        // go back to the database for everything to make sure valid data is returned.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#deleteGroups(java.util.Collection)
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("deleteGroups() started");
        
        if(groups == null || groups.size()<=0) {
            return;
        }

        List ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(ENTRY_GROUP_ID, ids);

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // This is required because deleteByQuery leaves the cache alone so future queries
        // could return origin entries that don't exist. Clearing the cache makes OJB
        // go back to the database for everything to make sure valid data is returned.
        getPersistenceBrokerTemplate().clearCache();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection<OriginEntry> getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.module.gl.dao.OriginEntryDao#getSummaryByGroupId(java.util.List)
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

        ReportQueryByCriteria query = QueryFactory.newReportQuery(entryClass, criteria);

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

    public OriginEntry getExactMatchingEntry(Integer entryId) {
        LOG.debug("getMatchingEntries() started");
        OriginEntry oe = new OriginEntry();
        // in case of no matching entry
        try {
            oe = (OriginEntry) getPersistenceBrokerTemplate().getObjectById(entryClass, entryId);

        }
        catch (Exception e) {
        }

        return oe;
    }

    /**
     * @see org.kuali.module.gl.dao.OriginEntryDao#getPosterOutputSummaryByGroupId(java.util.Collection)
     */
    public Iterator getPosterOutputSummaryByGroupId(Collection groups) {
        LOG.debug("getPosterInputSummaryByGroupId() started");

        if(groups == null || groups.size()<=0) {
            return null;
        }

        Collection ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup) iter.next();
            ids.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.ENTRY_GROUP_ID, ids);
        String fundGroupCode = KFSPropertyConstants.ACCOUNT + "." + KFSPropertyConstants.SUB_FUND_GROUP + "." + KFSPropertyConstants.FUND_GROUP_CODE;

        ReportQueryByCriteria query = QueryFactory.newReportQuery(entryClass, criteria);

        String attributeList[] = {
                KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE,
                KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, 
                KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, 
                fundGroupCode, 
                KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, 
                KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, 
                "sum(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")"
        };

        String groupList[] = {
                KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, 
                KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, 
                KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, 
                fundGroupCode, 
                KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, 
                KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE
        };

        query.setAttributes(attributeList);
        query.addGroupBy(groupList);

        // add the sorting criteria
        for (int i = 0; i < groupList.length; i++) {
            query.addOrderByAscending(groupList[i]);
        }

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }

    /**
     * @see org.kuali.module.gl.dao.OriginEntryDao#clearCache()
     */
    public void clearCache() {
        LOG.info("PersistenceBroker "+getPersistenceBrokerTemplate().getPbKey().toString()+" is clearing cache");
        getPersistenceBrokerTemplate().clearCache();
    }
}
