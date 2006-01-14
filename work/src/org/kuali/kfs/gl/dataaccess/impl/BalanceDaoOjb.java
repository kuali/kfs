/*
 * Created on Oct 14, 2005
 *
 */
package org.kuali.module.gl.dao.ojb;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.kuali.module.gl.bo.Balance;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.BalanceDao;
import org.springframework.orm.ojb.support.PersistenceBrokerDaoSupport;

/**
 * @author jsissom
 *
 */
public class BalanceDaoOjb extends PersistenceBrokerDaoSupport implements BalanceDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceDaoOjb.class);

  public BalanceDaoOjb() {
    super();
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.dao.BalanceDao#getBalanceByTransaction(org.kuali.module.gl.bo.Transaction)
   */
  public Balance getBalanceByTransaction(Transaction t) {
    LOG.debug("getBalanceByTransaction() started");

    Criteria crit = new Criteria();
    crit.addEqualTo("universityFiscalYear",t.getUniversityFiscalYear());
    crit.addEqualTo("chartOfAccountsCode",t.getChartOfAccountsCode());
    crit.addEqualTo("accountNumber",t.getAccountNumber());
    crit.addEqualTo("subAccountNumber",t.getSubAccountNumber());
    crit.addEqualTo("objectCode",t.getObjectCode());
    crit.addEqualTo("subObjectCode",t.getSubObjectCode());
    crit.addEqualTo("balanceTypeCode",t.getBalanceTypeCode());
    crit.addEqualTo("objectTypeCode",t.getObjectTypeCode());

    QueryByCriteria qbc = QueryFactory.newQuery(Balance.class,crit);
    return (Balance)getPersistenceBrokerTemplate().getObjectByQuery(qbc);
  }

  /* (non-Javadoc)
   * @see org.kuali.module.gl.dao.BalanceDao#save(org.kuali.module.gl.bo.Balance)
   */
  public void save(Balance b) {
    LOG.debug("save() started");

    getPersistenceBrokerTemplate().store(b);
  }

}
