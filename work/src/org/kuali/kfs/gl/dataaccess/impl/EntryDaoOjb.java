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

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EntryDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * 
 */
public class EntryDaoOjb extends PersistenceBrokerDaoSupport implements EntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryDaoOjb.class);

    public EntryDaoOjb() {
        super();
    }

    public void addEntry(Transaction t, Date postDate) {
        LOG.debug("addEntry() started");

        Entry e = new Entry(t, postDate);

        getPersistenceBrokerTemplate().store(e);
    }

    /**
     * Find the maximum transactionLedgerEntrySequenceNumber in the entry table for a specific transaction. This is used to make
     * sure that rows added have a unique primary key.
     */
    public int getMaxSequenceNumber(Transaction t) {
        LOG.debug("getSequenceNumber() ");

        Criteria crit = new Criteria();
        crit.addEqualTo("universityFiscalYear", t.getUniversityFiscalYear());
        crit.addEqualTo("chartOfAccountsCode", t.getChartOfAccountsCode());
        crit.addEqualTo("accountNumber", t.getAccountNumber());
        crit.addEqualTo("subAccountNumber", t.getSubAccountNumber());
        crit.addEqualTo("financialObjectCode", t.getFinancialObjectCode());
        crit.addEqualTo("financialSubObjectCode", t.getFinancialSubObjectCode());
        crit.addEqualTo("financialBalanceTypeCode", t.getFinancialBalanceTypeCode());
        crit.addEqualTo("financialObjectTypeCode", t.getFinancialObjectTypeCode());
        crit.addEqualTo("universityFiscalPeriodCode", t.getUniversityFiscalPeriodCode());
        crit.addEqualTo("financialDocumentTypeCode", t.getFinancialDocumentTypeCode());
        crit.addEqualTo("financialSystemOriginationCode", t.getFinancialSystemOriginationCode());
        crit.addEqualTo("financialDocumentNumber", t.getFinancialDocumentNumber());

        ReportQueryByCriteria q = QueryFactory.newReportQuery(Entry.class, crit);
        q.setAttributes(new String[] { "max(transactionLedgerEntrySequenceNumber)" });

        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        if (iter.hasNext()) {
            Object[] data = (Object[]) iter.next();
            BigDecimal max = (BigDecimal) data[0]; // Don't know why OJB returns a BigDecimal, but it does
            if (max == null) {
                return 0;
            }
            else {
                return max.intValue();
            }
        }
        else {
            return 0;
        }
    }

    /**
     * Purge the entry table by chart/year
     * 
     * @param chart
     * @param year
     */
    public void purgeYearByChart(String chartOfAccountsCode, int year) {
        LOG.debug("purgeYearByChart() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("chartOfAccountsCode", chartOfAccountsCode);
        criteria.addLessThan("universityFiscalYear", new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Entry.class, criteria));

        // This is required because if any deleted rows are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }
}
