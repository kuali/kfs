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
        crit.addEqualTo("financialDocumentReversalDate", t.getFinancialDocumentReversalDate());
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
        crit.addEqualTo("transactionLedgerEntrySequenceNumber", t.getTransactionLedgerEntrySequenceNumber());

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
        crit.addLessOrEqualThan("financialDocumentReversalDate", new java.sql.Date(before.getTime()));

        QueryByCriteria qbc = QueryFactory.newQuery(Reversal.class, crit);
        return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
    }

    public void delete(Reversal re) {
        LOG.debug("delete() started");

        getPersistenceBrokerTemplate().delete(re);
    }
}
