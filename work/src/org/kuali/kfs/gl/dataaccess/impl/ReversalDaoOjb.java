/*
 * Copyright 2006 The Kuali Foundation
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
/*
 * Created on Jan 11, 2006
 *
 */
package org.kuali.kfs.gl.dataaccess.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.ReversalDao;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

/**
 * An OJB implementation of the Reversal DAO
 */
public class ReversalDaoOjb extends PlatformAwareDaoBaseOjb implements ReversalDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReversalDaoOjb.class);

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
     * Constructs a ReversalDaoOjb instance
     */
    public ReversalDaoOjb() {
        super();
    }

    /**
     * Find the maximum transactionLedgerEntrySequenceNumber in the entry table for a specific transaction. This is used to make
     * sure that rows added have a unique primary key.
     * 
     * @param t a transaction to find the maximum sequence number for
     * @return the max sequence number for the given transaction
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
        if (iter.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iter);
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
     * Fetches the reversal record that would affected by the posting of the given transaction
     * 
     * @param t the transaction to find the related reversal for
     * @return the reversal affected by the given transaction
     * @see org.kuali.kfs.gl.dataaccess.ReversalDao#getByTransaction(org.kuali.kfs.gl.businessobject.Transaction)
     */
    public Reversal getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, t.getFinancialDocumentReversalDate());
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(KFSPropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
        crit.addEqualTo(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, t.getFinancialSystemOriginationCode());
        crit.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, t.getDocumentNumber());
        crit.addEqualTo(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, t.getTransactionLedgerEntrySequenceNumber());

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return (Reversal) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    /**
     * Fetches all reversals that have been set to reverse on or before the given date - that is to say,
     * returns all the reversal records ready to be reversed!
     * 
     * @param before the date that reversals must reverse on or before
     * @return an Iterator of reversal records to reverse
     * @see org.kuali.kfs.gl.dataaccess.ReversalDao#getByDate(java.util.Date)
     */
    public Iterator getByDate(Date before) {
        LOG.debug("getByDate() started");

        Criteria crit = new Criteria();
        crit.addLessOrEqualThan(KFSPropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, new java.sql.Date(before.getTime()));

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    /**
     * Deletes a reversal record
     * 
     * @param re reversal to delete
     * @see org.kuali.kfs.gl.dataaccess.ReversalDao#delete(org.kuali.kfs.gl.businessobject.Reversal)
     */
    public void delete(Reversal re) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(re);
    }
}
