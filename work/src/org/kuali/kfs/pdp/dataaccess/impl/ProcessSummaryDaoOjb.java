/*
 * Created on Oct 1, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.ProcessSummary;
import org.kuali.module.pdp.dao.ProcessSummaryDao;


/**
 * @author jsissom
 *
 */
public class ProcessSummaryDaoOjb extends PlatformAwareDaoBaseOjb implements ProcessSummaryDao  {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessSummaryDaoOjb.class);

  public ProcessSummaryDaoOjb() {
  }

  public List getByPaymentProcess(PaymentProcess fp) {
    LOG.debug("getByPaymentProcess() started");

    Criteria criteria = new Criteria();
    criteria.addEqualTo("processId",fp.getId());

    QueryByCriteria qbc = new QueryByCriteria(ProcessSummary.class,criteria);
    qbc.addOrderByDescending("disbursementTypeCode");
    qbc.addOrderByAscending("sortGroupId");
    qbc.addOrderByAscending("customerId");

    return (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
  }

  public List getByProcessId(Integer id) {
    LOG.debug("getByPaymentProcess() started");

    Criteria criteria = new Criteria();
    criteria.addEqualTo("processId",id);

    QueryByCriteria qbc = new QueryByCriteria(ProcessSummary.class,criteria);
    qbc.addOrderByDescending("disbursementTypeCode");
    qbc.addOrderByAscending("sortGroupId");
    qbc.addOrderByAscending("customerId");

    return (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
  }
  public void save(ProcessSummary ps) {
    LOG.debug("save() started");

    getPersistenceBrokerTemplate().store(ps);
  }
}
