/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/gl/dataaccess/impl/EntryDaoOjb.java,v $
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
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EntryDao;
import org.kuali.PropertyConstants;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * 
 * 
 */
public class EntryDaoOjb extends PersistenceBrokerDaoSupport implements EntryDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryDaoOjb.class);

    private final static String UNIVERISITY_FISCAL_YEAR = "universityFiscalYear";
    private final static String CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    private final static String ACCOUNT_NUMBER = "accountNumber";
    private final static String SUB_ACCOUNT_NUMBER = "subAccountNumber";
    private final static String FINANCIAL_OBJECT_CODE = "financialObjectCode";
    private final static String FINANCIAL_SUB_OBJECT_CODE = "financialSubObjectCode";
    private final static String FINANCIAL_BALANCE_TYPE_CODE = "financialBalanceTypeCode";
    private final static String FINANCIAL_OBJECT_TYPE_CODE = "financialObjectTypeCode";
    private final static String UNIVERISTY_FISCAL_PERIOD_CODE = "universityFiscalPeriodCode";
    private final static String FINANCIAL_DOCUMENT_TYPE_CODE = "financialDocumentTypeCode";
    private final static String FINANCIAL_SYSTEM_ORIGINATION_CODE = "financialSystemOriginationCode";
    private final static String MAX_CONSTANT = "max(documentNumber)";


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
        crit.addEqualTo(UNIVERISITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(UNIVERISTY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
        crit.addEqualTo(FINANCIAL_SYSTEM_ORIGINATION_CODE, t.getFinancialSystemOriginationCode());
        crit.addEqualTo(PropertyConstants.DOCUMENT_NUMBER, t.getDocumentNumber());

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
        criteria.addEqualTo(CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        criteria.addLessThan(UNIVERISITY_FISCAL_YEAR, new Integer(year));

        getPersistenceBrokerTemplate().deleteByQuery(new QueryByCriteria(Entry.class, criteria));

        // This is required because if any deleted rows are in the cache, deleteByQuery doesn't
        // remove them from the cache so a future select will retrieve these deleted account balances from
        // the cache and return them. Clearing the cache forces OJB to go to the database again.
        getPersistenceBrokerTemplate().clearCache();
    }
}
