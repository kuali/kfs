package edu.arizona.kfs.tax.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectLevel;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.dataaccess.TaxParameterHelperDao;

public class TaxParameterHelperDaoImpl extends PlatformAwareDaoBaseOjb implements TaxParameterHelperDao {

	@Override
	public List<String> getObjectCodes(String type, List<String> values) {
		List<String> retval = new ArrayList<String>();
		String whereFieldName = null;
		String resultFieldName = null;
		Class clazz;

		if (TaxConstants.Form1099.LEVEL.equals(type)) {
			whereFieldName = KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE;
			resultFieldName = KFSPropertyConstants.FINANCIAL_OBJECT_CODE;
			clazz = ObjectCode.class;
		} else {
			whereFieldName = KFSPropertyConstants.FINANCIAL_CONSOLIDATION_OBJECT_CODE;
			resultFieldName = KFSPropertyConstants.FINANCIAL_OBJECT_LEVEL_CODE;
			clazz = ObjectLevel.class;
		}

		Criteria criteria = new Criteria();
		criteria.addIn(whereFieldName, values);
		ReportQueryByCriteria query = QueryFactory.newReportQuery(ObjectCode.class, criteria);
		query.setAttributes(new String[] { resultFieldName });
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
