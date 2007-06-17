/*
 * Created on Aug 7, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.BatchSearch;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.UserRequired;
import org.kuali.module.pdp.dao.BatchSearchDao;
import org.springmodules.orm.ojb.support.PersistenceBrokerDaoSupport;


/**
 * @author jsissom
 *
 */
public class BatchSearchDaoOjb extends PlatformAwareDaoBaseOjb implements BatchSearchDao {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchSearchDaoOjb.class);
  private UniversalUserService userService;

  public BatchSearchDaoOjb() {
    super();
  }

  public List getAllBatchesForSearchCriteria(BatchSearch bs, int bsl) {
    LOG.info("getAllBatchesForSearchCriteria(PaymentDetailSearch) starting");
    String dateToSearchOn = "customerFileCreateTimestamp";
    Criteria criteria = new Criteria();

    if ((!(bs.getBatchId() == null)) && (!(bs.getBatchId() == new Integer(0)))) {
      criteria.addEqualTo("id",bs.getBatchId());
    }
    if ((!(bs.getChartCode() == null)) && (!(bs.getChartCode().equals("")))) {
      criteria.addLike("customerProfile.chartCode","%" + bs.getChartCode() + "%");
    }
    if ((!(bs.getOrgCode() == null)) && (!(bs.getOrgCode().equals("")))) {
      criteria.addLike("customerProfile.orgCode","%" + bs.getOrgCode() + "%");
    }      
    if ((!(bs.getSubUnitCode() == null)) && (!(bs.getSubUnitCode().equals("")))) {
      criteria.addLike("customerProfile.subUnitCode","%" + bs.getSubUnitCode() + "%");
    }
    if ((!(bs.getPaymentCount() == null)) && (!(bs.getPaymentCount() == new Integer(0)))) {
      criteria.addEqualTo("paymentCount",bs.getPaymentCount());
    }
    if ((!(bs.getPaymentTotalAmount() == null)) && (!(bs.getPaymentTotalAmount() == new BigDecimal(0.00)))) {
      criteria.addEqualTo("paymentTotalAmount",bs.getPaymentTotalAmount());
    }
    if (!(bs.getBeginDate() == null)) {
      criteria.addGreaterOrEqualThan(dateToSearchOn,bs.getBeginDate());
    }
    if (!(bs.getEndDate() == null)) {
      GregorianCalendar gc = new GregorianCalendar();
      gc.setTime(bs.getEndDate());
      gc.add(Calendar.DATE,1);
      criteria.addLessOrEqualThan(dateToSearchOn,new Timestamp(gc.getTimeInMillis()));
    }

    QueryByCriteria qbc = new QueryByCriteria(Batch.class,criteria);
    qbc.addOrderBy("customerProfile.chartCode",true);
    qbc.addOrderBy("customerProfile.orgCode",true);
    qbc.addOrderBy("customerProfile.subUnitCode",true);
    qbc.addOrderBy(dateToSearchOn,true);
    int total = bsl + 1;
    qbc.setEndAtIndex(total);
    LOG.debug("getAllBatchesForSearchCriteria() Query = " + qbc.toString());
    
    List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
    updateUser(l);
    return l;

  }

  public List getAllSingleBatchPayments(Integer id) {
    LOG.info("getAllSingleBatchPayments(id) starting");

    Criteria criteria = new Criteria();
    criteria.addEqualTo("paymentGroup.batch.id",id);
    QueryByCriteria qbc = new QueryByCriteria(PaymentDetail.class,criteria);
    qbc.addOrderBy("paymentGroup.payeeName",true);
    LOG.debug("getAllSingleBatchPayments() Query = " + qbc.toString());
    List l = (List)getPersistenceBrokerTemplate().getCollectionByQuery(qbc);

    return l;
  }
  
  // Inject
  public void setUniversalUserService(UniversalUserService us) {
    userService = us;
  }

  private void updateUser(List l) {
    for (Iterator iter = l.iterator(); iter.hasNext();) {
      updateUser( (Batch)iter.next() );
    }
  }

  private void updateUser(Batch b) {
      UserRequired ur = (UserRequired)b;
      try {
        ur.updateUser(userService);
      } catch (UserNotFoundException e) {
        b.setSubmiterUser(null);
      }
  }
}
