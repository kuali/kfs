package edu.arizona.kfs.sys.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.dataaccess.IncomeTypeHandlerDao;

@Transactional
public class IncomeTypeHandlerDaoOjb extends PlatformAwareDaoBaseOjb implements IncomeTypeHandlerDao {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncomeTypeHandlerDao.class);

    @Override
    public List<String> getObjectCodesByObjectLevelCodes(List<String> values) {
        LOG.info("Retrieving object codes by object level codes:" + values);
        List<String> retval = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addIn(KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE, values);
        ReportQueryByCriteria query = QueryFactory.newReportQuery(ObjectCode.class, criteria);
        query.setAttributes(new String[] { KFSPropertyConstants.FINANCIAL_OBJECT_CODE });

        @SuppressWarnings("unchecked")
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        while (iterator.hasNext()) {
            Object[] data = iterator.next();
            if (data[0] != null) {
                retval.add(data[0].toString());
            }
        }

        return retval;
    }

}
