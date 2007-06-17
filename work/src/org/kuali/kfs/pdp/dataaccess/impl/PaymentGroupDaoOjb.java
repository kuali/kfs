/*
 * Created on Sep 28, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentGroupHistory;
import org.kuali.module.pdp.bo.PaymentProcess;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.PaymentGroupDao;


/**
 * @author jsissom
 *
 */
public class PaymentGroupDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentGroupDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupDaoOjb.class);

  private UniversalUserService userService;

  public PaymentGroupDaoOjb() {
    super();
  }

  // Inject
  public void setUniversalUserService(UniversalUserService us) {
    userService = us;
  }

  public Iterator getByProcess(PaymentProcess p) {
    LOG.debug("getByProcess() started");

    Criteria criteria = new Criteria();
    criteria.addEqualTo("processId",p.getId());

    QueryByCriteria qbc = new QueryByCriteria(PaymentGroup.class,criteria);
    qbc.addOrderBy("sortValue",true);
    qbc.addOrderBy("payeeName",true);
    qbc.addOrderBy("line1Address",true);

    return getPersistenceBrokerTemplate().getIteratorByQuery(qbc);
  }

  public PaymentGroup get(Integer id) {
    LOG.debug("get(id) started");
    List data = new ArrayList();

    Criteria criteria = new Criteria();
    criteria.addEqualTo("id", id);

    PaymentGroup cp = (PaymentGroup)getPersistenceBrokerTemplate().getObjectByQuery(
        new QueryByCriteria(PaymentGroup.class, criteria));

    if (cp.getBatch() != null) {
      updateBatchUser(cp.getBatch());
    }
    if (cp.getProcess() != null) {
      updateProcessUser(cp.getProcess());
    }
    if (cp.getPaymentGroupHistory() != null) {
      updatePaymentGroupHistoryList(cp.getPaymentGroupHistory());
    }
    return cp;
  
  }

  public void save(PaymentGroup pg) {
    LOG.debug("save() started");

    getPersistenceBrokerTemplate().store(pg);
  }

  public List getByDisbursementNumber(Integer disbursementNbr) {
    LOG.debug("getByDisbursementNumber() enter method");
    Criteria criteria = new Criteria();
    criteria.addEqualTo("disbursementNbr", disbursementNbr);
    return (List)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class,criteria));
  }
  
  public List getByBatchId(Integer batchId) {
    LOG.debug("getByBatchId() enter method");
    Criteria criteria = new Criteria();
    criteria.addEqualTo("batchId", batchId);
    return (List)getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(PaymentGroup.class,criteria));
  }
  
  private void updatePaymentGroupHistoryList(List l) {
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      PaymentGroupHistory element = (PaymentGroupHistory) iter.next();
      updateChangeUser(element);
    }
  }

  private void updateChangeUser(PaymentGroupHistory b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setChangeUser(null);
      }
  }

  private void updateBatchUser(Batch b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setSubmiterUser(null);
      }
  }

  private void updateProcessUser(PaymentProcess b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setProcessUser(null);
      }
  }
}
