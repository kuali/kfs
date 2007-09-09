/*
 * Created on Sep 2, 2004
 *
 */
package org.kuali.module.pdp.dao.ojb;

import java.util.Iterator;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.module.pdp.bo.GlPendingTransaction;
import org.kuali.module.pdp.dao.GlPendingTransactionDao;


/**
 * @author jsissom
 *
 */
public class GlPendingTransactionDaoOjb extends PlatformAwareDaoBaseOjb implements GlPendingTransactionDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GlPendingTransactionDaoOjb.class);

    public GlPendingTransactionDaoOjb() {
        super();
    }

    /**
     * @see org.kuali.module.pdp.dao.GlPendingTransactionDao#getUnextractedTransactions()
     */
    public Iterator getUnextractedTransactions() {
        LOG.debug("save() started");

        Criteria criteria = new Criteria();
        criteria.addIsNull("processInd");
        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(GlPendingTransaction.class,criteria));
    }

    /**
     * @see org.kuali.module.pdp.dao.GlPendingTransactionDao#save(org.kuali.module.pdp.bo.GlPendingTransaction)
     */
    public void save(GlPendingTransaction gpt) {
        LOG.debug("save() starting");

        getPersistenceBrokerTemplate().store(gpt);
    }
}
