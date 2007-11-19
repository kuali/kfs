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
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.EntryDao;

/**
 * An OJB implementation of EntryDao
 */
public class EntryDaoOjb extends PlatformAwareDaoBaseOjb implements EntryDao {
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


    /**
     * Constructs a EntryDaoOjb instance
     */
    public EntryDaoOjb() {
        super();
    }

    /**
     * Turns the given transaction into an entry and then saves that entry in the database
     * 
     * @param t the transaction to save
     * @param postDate the officially reported posting date
     * @see org.kuali.module.gl.dao.EntryDao#addEntry(org.kuali.module.gl.bo.Transaction, java.util.Date)
     */
    public void addEntry(Transaction t, Date postDate) {
        LOG.debug("addEntry() started");

        Entry e = new Entry(t, postDate);

        getPersistenceBrokerTemplate().store(e);
    }

    /**
     * Find the maximum transactionLedgerEntrySequenceNumber in the entry table for a specific transaction. This is used to make
     * sure that rows added have a unique primary key.
     * 
     * @param t the transaction to check
     * @return the max sequence number
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
        crit.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, t.getDocumentNumber());

        ReportQueryByCriteria q = QueryFactory.newReportQuery(Entry.class, crit);
        q.setAttributes(new String[] { "max(transactionLedgerEntrySequenceNumber)" });

        Iterator iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        // would this work better? max = (BigDecimal) getPersistenceBrokerTemplate().getObjectByQuery(q);
        BigDecimal max = null;
        while (iter.hasNext()) {
            Object[] data = (Object[]) iter.next();
            max = (BigDecimal) data[0]; // Don't know why OJB returns a BigDecimal, but it does
        }
        if (max == null) {
            return 0;
        }
        else {
            return max.intValue();
        }
    }

    /**
     * Purge the entry table by chart/year
     * 
     * @param chart the chart of accounts code of entries to purge
     * @param year the university fiscal year of entries to purge
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
