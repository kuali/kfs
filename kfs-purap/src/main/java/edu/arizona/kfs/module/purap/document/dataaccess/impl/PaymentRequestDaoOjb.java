package edu.arizona.kfs.module.purap.document.dataaccess.impl;

import java.sql.Date;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.kuali.kfs.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.util.VendorGroupingHelper;

public class PaymentRequestDaoOjb extends org.kuali.kfs.module.purap.document.dataaccess.impl.PaymentRequestDaoOjb {
	
	private static Logger LOG = Logger.getLogger(PaymentRequestDaoOjb.class);
	
    @Override
    public List<PaymentRequestDocument> getPaymentRequestsToExtract(boolean onlySpecialPayments, String chartCode, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if (chartCode != null) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        if (onlySpecialPayments) {
            Criteria a = new Criteria();

            Criteria c1 = new Criteria();
            c1.addNotNull("specialHandlingInstructionLine1Text");
            Criteria c2 = new Criteria();
            c2.addNotNull("specialHandlingInstructionLine2Text");
            Criteria c3 = new Criteria();
            c3.addNotNull("specialHandlingInstructionLine3Text");
            Criteria c4 = new Criteria();
            c4.addEqualTo("paymentAttachmentIndicator", Boolean.TRUE);

            c1.addOrCriteria(c2);
            c1.addOrCriteria(c3);
            c1.addOrCriteria(c4);

            a.addAndCriteria(c1);
            a.addLessOrEqualThan("paymentRequestPayDate", onOrBeforePaymentRequestPayDate);

            Criteria c5 = new Criteria();
            c5.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);
            c5.addOrCriteria(a);

            criteria.addAndCriteria(a);
        }
        else {
            Criteria c1 = new Criteria();
            c1.addLessOrEqualThan("paymentRequestPayDate", onOrBeforePaymentRequestPayDate);

            Criteria c2 = new Criteria();
            c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

            c1.addOrCriteria(c2);
            criteria.addAndCriteria(c1);
        }

        return (List<PaymentRequestDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(edu.arizona.kfs.module.purap.document.PaymentRequestDocument.class, criteria));
    }
    
    @Override
    public List<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        LOG.debug("getImmediatePaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if (chartCode != null) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }

        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        return (List<PaymentRequestDocument>) getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(edu.arizona.kfs.module.purap.document.PaymentRequestDocument.class, criteria));
    }
    
    @Override
    public Collection<PaymentRequestDocument> getPaymentRequestsToExtractForVendor(String campusCode, VendorGroupingHelper vendor, Date onOrBeforePaymentRequestPayDate) {
        LOG.debug("getPaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", campusCode);
        criteria.addIsNull("extractedTimestamp");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        Criteria c1 = new Criteria();
        c1.addLessOrEqualThan("paymentRequestPayDate", onOrBeforePaymentRequestPayDate);

        Criteria c2 = new Criteria();
        c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        c1.addOrCriteria(c2);
        criteria.addAndCriteria(c1);

        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendor.getVendorHeaderGeneratedIdentifier());
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendor.getVendorDetailAssignedIdentifier());
        criteria.addEqualTo("vendorCountryCode", vendor.getVendorCountry());
        criteria.addLike("vendorPostalCode", vendor.getVendorPostalCode() + "%");

        return getPersistenceBrokerTemplate().getCollectionByQuery(new QueryByCriteria(edu.arizona.kfs.module.purap.document.PaymentRequestDocument.class, criteria));
    }
    
    @Override
    public List<PaymentRequestDocument> getPaymentRequestInReceivingStatus() {
        Criteria criteria = new Criteria();
        criteria.addNotEqualTo("holdIndicator", "Y");
        criteria.addNotEqualTo("paymentRequestedCancelIndicator", "Y");
        // UAF-2837 : Added this criteria 
    	criteria.addEqualTo("documentHeader.applicationDocumentStatus", PaymentRequestStatuses.APPDOC_AWAITING_RECEIVING_REVIEW);

    	QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);

    }

}