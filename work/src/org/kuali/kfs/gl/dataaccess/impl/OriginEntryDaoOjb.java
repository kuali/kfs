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

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id: OriginEntryDaoOjb.java,v 1.16 2006-06-03 21:37:51 jsissom Exp $
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
     * I'm not sure how this is supposed to work and since it wasn't implemented it, I did it this way. If you change it, let me
     * know. jsissom
     */
    public Iterator getMatchingEntries(Map searchCriteria) {
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

    /**
     * This method is special because of the order by.  It is used in
     * the scrubber.  The getMatchingEntries wouldn't work because of 
     * the required order by.
     * 
     * @param oeg Group
     * @return
     */
    public Iterator getEntriesByGroup(OriginEntryGroup oeg) {
        LOG.debug("getEntriesByGroup() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("entryGroupId", oeg.getId());

        QueryByCriteria qbc = QueryFactory.newQuery(OriginEntry.class, criteria);
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
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * This method should only be used in unit tests. It loads all the gl_origin_entry_t rows in memory into a collection. This
     * won't work for production because there would be too many rows to load into memory.
     * 
     * @return
     */
    public Collection testingGetAllEntries() {
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

        if (null != entry && null != entry.getTransactionLedgerEntryDescription()
                && 40 < entry.getTransactionLedgerEntryDescription().length()) {

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
    }
    
    
    public Collection getMatchingEntriesByCollection(Map searchCriteria) {
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
}
