/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: OriginEntryDaoOjb.java,v 1.24 2006-06-22 20:01:07 jsissom Exp $
 */

public class OriginEntryDaoOjb extends PersistenceBrokerDaoSupport implements OriginEntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryDaoOjb.class);

    /**
     * 
     */
    public OriginEntryDaoOjb() {
        super();
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#deleteEntry(org.kuali.module.gl.bo.OriginEntry)
     */
    public void deleteEntry(OriginEntry oe) {
        LOG.debug("deleteEntry() started");

        getPersistenceBrokerTemplate().delete(oe);
    }

    /**
     * 
     * @see org.kuali.module.gl.dao.OriginEntryDao#getDocumentsByGroup(org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public Iterator<OriginEntry> getDocumentsByGroup(OriginEntryGroup oeg) {
        LOG.debug("getDocumentsByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("entryGroupId", oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.setDistinct(true);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
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

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.addOrderByAscending("entryId");
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    public Iterator<OriginEntry> getBadBalanceEntries(Collection groups) {
        LOG.debug("getBadBalanceEntries() started");


        Collection ids = new ArrayList();
        for (Iterator iter = groups.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup)iter.next();
            ids.add(element.getId());
        }

        Criteria crit1 = new Criteria();
        crit1.addIn("entryGroupId", ids);

        Criteria crit2 = new Criteria();
        crit2.addIsNull("financialBalanceTypeCode");
        
        Criteria crit3 = new Criteria();
        crit3.addEqualTo("financialBalanceTypeCode", "  ");

        crit2.addOrCriteria(crit3);

        crit1.addAndCriteria(crit2);

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, crit1);
        qbc.addOrderByAscending("chartOfAccountsCode");
        qbc.addOrderByAscending("accountNumber");
        qbc.addOrderByAscending("subAccountNumber");

        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * This method is special because of the order by. It is used in the scrubber. The getMatchingEntries wouldn't work because of
     * the required order by.
     * 
     */
    public Iterator<OriginEntry> getEntriesByGroup(OriginEntryGroup oeg,int sort) {
        LOG.debug("getEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("entryGroupId", oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);

        if ( sort == OriginEntryDao.SORT_DOCUMENT ) {
            qbc.addOrderByAscending("financialDocumentTypeCode");
            qbc.addOrderByAscending("financialSystemOriginationCode");
            qbc.addOrderByAscending("financialDocumentNumber");
            qbc.addOrderByAscending("chartOfAccountsCode");
            qbc.addOrderByAscending("accountNumber");
            qbc.addOrderByAscending("subAccountNumber");
            qbc.addOrderByAscending("financialBalanceTypeCode");
            qbc.addOrderByAscending("financialDocumentReversalDate");
            qbc.addOrderByAscending("universityFiscalPeriodCode");
            qbc.addOrderByAscending("universityFiscalYear");
            // The above order by fields are required by the scrubber process. Adding this
            // field makes the data in the exact same order as the COBOL scrubber.
            qbc.addOrderByAscending("financialObjectCode");
        } else {
            qbc.addOrderByAscending("chartOfAccountsCode");
            qbc.addOrderByAscending("accountNumber");
            qbc.addOrderByAscending("subAccountNumber");
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
        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.addOrderByAscending("entryId");
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @param entry the entry to save.
     */
    public void saveOriginEntry(OriginEntry entry) {
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

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        getPersistenceBrokerTemplate().deleteByQuery(qbc);

        // This is required because deleteByQuery leaves the cache alone so future queries
        // could return origin entries that don't exist. Clearing the cache makes OJB
        // go back to the database for everything to make sure valid data is returned.
        getPersistenceBrokerTemplate().clearCache();
    }

    public Collection<OriginEntry> getMatchingEntriesByCollection(Map searchCriteria) {
        LOG.debug("getMatchingEntries() started");

        Criteria criteria = new Criteria();
        for (Iterator iter = searchCriteria.keySet().iterator(); iter.hasNext();) {
            String element = (String) iter.next();
            criteria.addEqualTo(element, searchCriteria.get(element));
        }

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
        qbc.addOrderByAscending("entryId");
        return getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    }

    /**
     * @see org.kuali.module.gl.dao.OriginEntryDao#getSummaryByGroupId(java.util.List)
     */
    public Iterator getSummaryByGroupId(Collection groupIdList) {
        LOG.debug("getSummaryByGroupId() started");

        Collection ids = new ArrayList();
        for (Iterator iter = groupIdList.iterator(); iter.hasNext();) {
            OriginEntryGroup element = (OriginEntryGroup)iter.next();
            ids.add(element.getId());
        }

        Criteria criteria = new Criteria();
        criteria.addIn(PropertyConstants.ENTRY_GROUP_ID, ids);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(OriginEntry.class, criteria);

        String attributeList[] = {
                PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE,
                PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE,
                PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE,
                PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE,
                "sum(" + PropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT + ")",
                "count(" + PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE + ")"
        };

        String groupList[] = {
                PropertyConstants.UNIVERSITY_FISCAL_YEAR,
                PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE,
                PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE,
                PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE,
                PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE
        };

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
        return (OriginEntry) getPersistenceBrokerTemplate().getObjectById(OriginEntry.class, entryId);
    }
}
