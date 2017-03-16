package edu.arizona.kfs.pdp.dataaccess.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.pdp.businessobject.ShippingInvoice;
import edu.arizona.kfs.pdp.dataaccess.ShippingInvoiceDao;

public class ShippingInvoiceDaoOjb extends PlatformAwareDaoBaseOjb implements ShippingInvoiceDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ShippingInvoiceDaoOjb.class);
    
    protected static final String SHIPPING_COMPANY = "shippingCompany";
    
	public ShippingInvoiceDaoOjb() {
		super();
	}
	            
    public List<String> getShippingInvoiceCompanies() {
        LOG.debug("getShippingInvoiceCompanies() started");
        
        Criteria criteria = new Criteria();
        criteria.addNotNull(SHIPPING_COMPANY);		
        ReportQueryByCriteria q = QueryFactory.newReportQuery(ShippingInvoice.class, criteria, true);
        q.setAttributes(new String[] { SHIPPING_COMPANY });
        q.addOrderByAscending(SHIPPING_COMPANY);
        
        Iterator<Object[]> iter = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(q);
        List<String> companies = new ArrayList<String>();
        while (iter.hasNext()) {
            final String company = (String) iter.next()[0];
            companies.add(company);
        }
     
        return companies;
    }

}
