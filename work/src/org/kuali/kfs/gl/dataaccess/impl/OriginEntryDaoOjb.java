/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.gl.dataaccess.impl;

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
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.dataaccess.OriginEntryDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implementation of the OriginEntryDao
 */
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

    /**
     * Sets the class of the origin entries this class deals with.  This makes this particular
     * class very flexible; instances of it can deal with OriginEntryLites as well as they deal
     * with OriginEntryFulls.
     * 
     * @param entryClass the class of OriginEntries this instance will use for OJB operations
     */
    public void setEntryClass(Class entryClass) {
        this.entryClass = entryClass;
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
     * Constructs a OriginEntryDaoOjb instance
     */
    public OriginEntryDaoOjb() {
        super();
    }

    /**
     * Get the total amount of transactions in a group
     * @param the id of the origin entry group to total
     * @param isCredit whether the total should be of credits or not
     * @return the sum of all queried origin entries
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getGroupTotal(java.lang.Integer, boolean)
     */
    public KualiDecimal getGroupTotal(Integer groupId, boolean isCredit) {
        LOG.debug("getGroupTotal() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(OriginEntryDaoOjb.ENTRY_GROUP_ID, groupId);
        if (isCredit) {
            crit.addEqualTo(OriginEntryDaoOjb.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
        }
        else {
            crit.addNotEqualTo(OriginEntryDaoOjb.TRANSACTION_DEBIT_CREDIT_CODE, KFSConstants.GL_CREDIT_CODE);
        }

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, crit);
        q.setAttributes(new String[] { "SUM(" + OriginEntryDaoOjb.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" });

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (i.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(i);
            return (KualiDecimal) data[0];
        }
        else {
            return null;
        }
    }

    /**
     * Counts the number of entries in a group
     * @param the id of an origin entry group
     * @return the count of the entries in that group
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getGroupCount(java.lang.Integer)
     */
    public Integer getGroupCount(Integer groupId) {
        LOG.debug("getGroupCount() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(OriginEntryDaoOjb.ENTRY_GROUP_ID, groupId);

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, crit);
        q.setAttributes(new String[] { "count(*)" });

        Iterator i = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (i.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(i);

            if (data[0] instanceof BigDecimal) {
                return ((BigDecimal) data[0]).intValue();
            }
            else {
                return ((Long) data[0]).intValue();
            }
        }
        else {
            return null;
        }
    }

    /**
     * Counts of rows of all the origin entry groups
     * 
     * @return iterator of Object[] {[BigDecimal id,BigDecimal count]}
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getGroupCounts()
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
     * Delete an entry from the database
     * @param oe the entry to delete
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#deleteEntry(org.kuali.kfs.gl.businessobject.OriginEntryInformation)
     */
    public void deleteEntry(OriginEntryInformation oe) {
        LOG.debug("deleteEntry() started");

        getPersistenceBrokerTemplate().delete(oe);
    }

    /**
     * Return an iterator of keys of all documents referenced by origin entries in a given group
     * 
     * @param oeg Group the origin entry group to find entries in, by origin entry
     * @return Iterator of java.lang.Object[] with report data about all of the distinct document numbers/type code/origination code combinations of origin entries in the group
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getDocumentsByGroup(org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public Iterator getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(ENTRY_GROUP_ID, oeg.getId());

        ReportQueryByCriteria q = QueryFactory.newReportQuery(entryClass, criteria);
        q.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER, "financialDocumentTypeCode", "financialSystemOriginationCode" });

        q.setDistinct(true);

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
    }

    /**
     * Iterator of entries that match criteria
     * 
     * @param searchCriteria Map of field, value pairs
     * @return collection of entries
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getMatchingEntries(java.util.Map)
     */
    public Iterator<OriginEntryFull> getMatchingEntries(Map searchCriteria) {
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

    /**
     * Get bad balance entries
     * 
     * @param groups a Collection of groups to remove bad entries in
     * @return an Iterator of no good, won't use, bad balance entries
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getBadBalanceEntries(java.util.Collection)
     */
    public Iterator<OriginEntryFull> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");

        if (groups.size() <= 0) {
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
     * @param OriginEntryGroup the originEntryGroup that holds the origin entries to find
     * @param sort the sort order to sort entries by, defined in OriginEntryDao
     * 
     * @return an Iterator of whichever flavor of OriginEntries this instance uses
     */
    public <T> Iterator<T> getEntriesByGroup(OriginEntryGroup oeg, int sort) {
        LOG.debug("getEntriesByGroup() started");

        // clear cache because the GLCP document class saves to the origin entry table and
        // reads from it (via this method) in the same transaction. If the clearCache line is
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
        }
        else if (sort == OriginEntryDao.SORT_REPORT) {
            qbc.addOrderByAscending(FINANCIAL_DOCUMENT_TYPE_CODE);
            qbc.addOrderByAscending(FINANCIAL_SYSTEM_ORIGINATION_CODE);
            qbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
            qbc.addOrderByAscending(TRANSACTION_DEBIT_CREDIT_CODE);
            qbc.addOrderByAscending(CHART_OF_ACCOUNTS_CODE);
            qbc.addOrderByAscending(ACCOUNT_NUMBER);
            qbc.addOrderByAscending(FINANCIAL_OBJECT_CODE);
        }
        else if (sort == OriginEntryDao.SORT_LISTING_REPORT) {
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
     * @return a collection of OriginEntryFulls
     */
    public Collection<OriginEntryFull> testingGetAllEntries() {
        LOG.debug("testingGetAllEntries() started");

        Criteria criteria = new Criteria();
        QueryByCriteria qbc = QueryFactory.newQuery(entryClass, criteria);
        qbc.addOrderByAscending(ENTRY_GROUP_ID);
        qbc.addOrderByAscending(ENTRY_ID);
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * Delete entries matching searchCriteria search criteria.
     * 
     * @param searchCriteria a map of criteria to use as keys for building a query
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
     * Delete all the groups in the list. This will delete the entries. The OriginEntryGroupDao has a method to delete the groups,
     * and one has to use both to really delete the whole group
     * 
     * @param groups a Collection of Origin Entry Groups to delete entries in
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#deleteGroups(java.util.Collection)
     */
    public void deleteGroups(Collection<OriginEntryGroup> groups) {
        LOG.debug("deleteGroups() started");

        if (groups == null || groups.size() <= 0) {
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
     * Collection of entries that match criteria
     * 
     * @param searchCriteria Map of field, value pairs
     * @return collection of entries
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getMatchingEntriesByCollection(java.util.Map)
     */
    public Collection<OriginEntryFull> getMatchingEntriesByCollection(Map searchCriteria) {
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
     * get the summarized information of the entries that belong to the entry groups with the given group ids
     * 
     * @param groupIdList the ids of origin entry groups
     * @return a set of summarized information of the entries within the specified groups
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getSummaryByGroupId(java.util.List)
     */
    public Iterator getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        if (groupIdList == null || groupIdList.size() <= 0) {
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

    /**
     * Fetches an entry for the given entryId, or returns a newly created on
     * 
     * @param entryId an entry id to find an entry for
     * @return the entry for the given entry id, or a newly created entry
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getExactMatchingEntry(java.lang.Integer)
     */
    public OriginEntryFull getExactMatchingEntry(Integer entryId) {
        LOG.debug("getMatchingEntries() started");
        OriginEntryFull oe = new OriginEntryFull();
        // in case of no matching entry
        try {
            oe = (OriginEntryFull) getPersistenceBrokerTemplate().getObjectById(entryClass, entryId);

        }
        catch (Exception e) {
        }

        return oe;
    }

    /**
     * get the summarized information of poster input entries that belong to the entry groups with the given group id list
     * 
     * @param groups the origin entry groups
     * @return a set of summarized information of poster input entries within the specified groups
     * @see org.kuali.kfs.gl.dataaccess.OriginEntryDao#getPosterOutputSummaryByGroupId(java.util.Collection)
     */
    public Iterator getPosterOutputSummaryByGroupId(Collection groups) {
        LOG.debug("getPosterInputSummaryByGroupId() started");

        if (groups == null || groups.size() <= 0) {
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

        String attributeList[] = { KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, fundGroupCode, KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE, "sum(" + KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")" };

        String groupList[] = { KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, fundGroupCode, KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE };

        query.setAttributes(attributeList);
        query.addGroupBy(groupList);

        // add the sorting criteria
        for (int i = 0; i < groupList.length; i++) {
            query.addOrderByAscending(groupList[i]);
        }

        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
    }
}
