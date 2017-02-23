package edu.arizona.kfs.fp.dataaccess.impl;

import java.util.Collection;

import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;

import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

public class DisbursementVoucherDaoOjb extends org.kuali.kfs.fp.dataaccess.impl.DisbursementVoucherDaoOjb {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherDaoOjb.class);
    private static final String FINANCIAL_DOCUMENT_STATUS_CODE = KFSConstants.DOCUMENT_HEADER_PROPERTY_NAME + "." + KFSPropertyConstants.FINANCIAL_DOCUMENT_STATUS_CODE;
    
    @Override
    public Collection<DisbursementVoucherDocument> getDocumentsByHeaderStatus(String statusCode, boolean immediatesOnly) {
        LOG.debug("getDocumentsByHeaderStatus() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo(FINANCIAL_DOCUMENT_STATUS_CODE, statusCode);
        criteria.addEqualTo(KFSPropertyConstants.DISB_VCHR_PAYMENT_METHOD_CODE, KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_CHECK);
        if (immediatesOnly) {
            criteria.addEqualTo(KFSPropertyConstants.IMMEDIATE_PAYMENT_INDICATOR, Boolean.TRUE);
        }

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(DisbursementVoucherDocument.class, criteria));
    }
}
