/*
 * Created on Jan 11, 2006
 *
 */
package org.kuali.module.gl.dao.ojb;

import java.util.Date;
import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.PropertyConstants;
import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.ReversalDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 * 
 */
public class ReversalDaoOjb extends PersistenceBrokerDaoSupport implements ReversalDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReversalDaoOjb.class);

    public ReversalDaoOjb() {
        super();
    }

    public Reversal getByTransaction(Transaction t) {
        LOG.debug("getByTransaction() started");

        Criteria crit = new Criteria();
        crit.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, t.getFinancialDocumentReversalDate());
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_YEAR, t.getUniversityFiscalYear());
        crit.addEqualTo(PropertyConstants.CHART_OF_ACCOUNTS_CODE, t.getChartOfAccountsCode());
        crit.addEqualTo(PropertyConstants.ACCOUNT_NUMBER, t.getAccountNumber());
        crit.addEqualTo(PropertyConstants.SUB_ACCOUNT_NUMBER, t.getSubAccountNumber());
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_CODE, t.getFinancialObjectCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_SUB_OBJECT_CODE, t.getFinancialSubObjectCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, t.getFinancialBalanceTypeCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, t.getFinancialObjectTypeCode());
        crit.addEqualTo(PropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE, t.getUniversityFiscalPeriodCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE, t.getFinancialDocumentTypeCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, t.getFinancialSystemOriginationCode());
        crit.addEqualTo(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, t.getFinancialDocumentNumber());
        crit.addEqualTo(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER, t.getTransactionLedgerEntrySequenceNumber());

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return (Reversal) getPersistenceBrokerTemplate().getObjectByQuery(qbc);
    }

    public void save(Reversal re) {
        LOG.debug("save() started");

        getPersistenceBrokerTemplate().store(re);
    }

    public Iterator getByDate(Date before) {
        LOG.debug("getByDate() started");

        Criteria crit = new Criteria();
        crit.addLessOrEqualThan(PropertyConstants.FINANCIAL_DOCUMENT_REVERSAL_DATE, new java.sql.Date(before.getTime()));

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    public void delete(Reversal re) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(re);
    }
}
