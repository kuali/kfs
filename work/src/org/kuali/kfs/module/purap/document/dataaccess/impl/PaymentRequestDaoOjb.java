/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.purap.dao.ojb;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TransactionalServiceUtils;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.bo.PaymentRequestSummaryAccount;
import org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.PurapAccountingService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * OJB Implementation of PaymentRequestDao.
 */
public class PaymentRequestDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentRequestDao {
    private static Logger LOG = Logger.getLogger(PaymentRequestDaoOjb.class);

    private NegativePaymentRequestApprovalLimitDao negativePaymentRequestApprovalLimitDao;
    private DateTimeService dateTimeService;
    private PurapAccountingService purapAccountingService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * The special payments query should be this: select * from pur.ap_pmt_rqst_t where pmt_rqst_stat_cd in ('AUTO', 'DPTA') and
     * prcs_cmp_cd = ? and pmt_extrt_ts is NULL and pmt_hld_ind = 'N' and ( ( ( pmt_spcl_handlg_instrc_ln1_txt is not NULL or
     * pmt_spcl_handlg_instrc_ln2_txt is not NULL or pmt_spcl_handlg_instrc_ln3_txt is not NULL or pmt_att_ind = 'Y') and trunc
     * (pmt_rqst_pay_dt) <= trunc (sysdate)) or IMD_PMT_IND = 'Y')})
     * 
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getPaymentRequestsToExtract(boolean, java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(boolean onlySpecialPayments, String chartCode) {
        LOG.debug("getPaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if (chartCode != null) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }
        criteria.addIn("statusCode", Arrays.asList(PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION));
        criteria.addIsNull("extractedDate");
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
            a.addLessOrEqualThan("paymentRequestPayDate", dateTimeService.getCurrentSqlDateMidnight());

            Criteria c5 = new Criteria();
            c5.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);
            c5.addOrCriteria(a);

            criteria.addAndCriteria(a);
        }
        else {
            Criteria c1 = new Criteria();
            c1.addLessOrEqualThan("paymentRequestPayDate", dateTimeService.getCurrentSqlDateMidnight());

            Criteria c2 = new Criteria();
            c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

            c1.addOrCriteria(c2);
            criteria.addAndCriteria(c1);
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getImmediatePaymentRequestsToExtract(java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getImmediatePaymentRequestsToExtract(String chartCode) {
        LOG.debug("getImmediatePaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if (chartCode != null) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }

        criteria.addIn("statusCode", Arrays.asList(PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION));
        criteria.addIsNull("extractedDate");
        criteria.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getPaymentRequestsToExtract(java.lang.String, java.lang.Integer,
     *      java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(String campusCode, Integer paymentRequestIdentifier, Integer purchaseOrderIdentifier, Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier) {
        LOG.debug("getPaymentRequestsToExtract() started");

        List statuses = new ArrayList();
        statuses.add(PurapConstants.PaymentRequestStatuses.AUTO_APPROVED);
        statuses.add(PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", campusCode);
        criteria.addIn("statusCode", statuses);
        criteria.addIsNull("extractedDate");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        Criteria c1 = new Criteria();
        c1.addLessOrEqualThan("paymentRequestPayDate", dateTimeService.getCurrentSqlDateMidnight());

        Criteria c2 = new Criteria();
        c2.addEqualTo("immediatePaymentIndicator", Boolean.TRUE);

        c1.addOrCriteria(c2);
        criteria.addAndCriteria(c1);

        if (paymentRequestIdentifier != null) {
            criteria.addEqualTo("purapDocumentIdentifier", paymentRequestIdentifier);
        }
        if (purchaseOrderIdentifier != null) {
            criteria.addEqualTo("purchaseOrderIdentifier", purchaseOrderIdentifier);
        }
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedIdentifier);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedIdentifier);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentRequestDocument.class, criteria));
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getEligibleForAutoApproval()
     */
    public List<PaymentRequestDocument> getEligibleForAutoApproval() {
        Date todayAtMidnight = dateTimeService.getCurrentSqlDateMidnight();
        Criteria criteria = new Criteria();
        criteria.addLessOrEqualThan(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, todayAtMidnight);
        criteria.addNotEqualTo("holdIndicator", "Y");
        criteria.addNotEqualTo("paymentRequestedCancelIndicator", "Y");
        criteria.addIn("status", Arrays.asList(PurapConstants.PaymentRequestStatuses.PREQ_STATUSES_FOR_AUTO_APPROVE));

        Query query = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        Iterator<PaymentRequestDocument> documents = (Iterator<PaymentRequestDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        ArrayList<String> documentHeaderIds = new ArrayList<String>();
        while (documents.hasNext()) {
            PaymentRequestDocument document = (PaymentRequestDocument) documents.next();
            documentHeaderIds.add(document.getDocumentNumber());
        }

        if (documentHeaderIds.size() > 0) {
            try {
                return SpringContext.getBean(DocumentService.class).getDocumentsByListOfDocumentHeaderIds(PaymentRequestDocument.class, documentHeaderIds);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve paymentRequestDocuments", e);
            }
        }
        else {
            return null;
        }

    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getDocumentNumberByPaymentRequestId(java.lang.Integer)
     */
    public String getDocumentNumberByPaymentRequestId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        return getDocumentNumberOfPaymentRequestByCriteria(criteria);
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getDocumentNumbersByPurchaseOrderId(java.lang.Integer)
     */
    public List<String> getDocumentNumbersByPurchaseOrderId(Integer poPurApId) {
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poPurApId);
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            returnList.add((String) cols[0]);
        }
        return returnList;
    }

    /**
     * Retrieves a document number for a payment request by user defined criteria.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @return - document number
     */
    private String getDocumentNumberOfPaymentRequestByCriteria(Criteria criteria) {
        LOG.debug("getDocumentNumberOfPaymentRequestByCriteria() started");
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        if (iter.hasNext()) {
            Object[] cols = (Object[]) iter.next();
            if (iter.hasNext()) {
                // the iterator should have held only a single doc id of data but it holds 2 or more
                String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
                LOG.error(errorMsg);
                TransactionalServiceUtils.exhaustIterator(iter);
                throw new RuntimeException();
            }
            // at this part of the code, we know there's no more elements in iterator
            return (String) cols[0];
        }
        return null;
    }

    /**
     * Retrieves a document number for a payment request by user defined criteria and sorts the values ascending if orderByAscending
     * parameter is true, descending otherwise.
     * 
     * @param criteria - list of criteria to use in the retrieve
     * @param orderByAscending - boolean to sort results ascending if true, descending otherwise
     * @return - Iterator of document numbers
     */
    private Iterator<Object[]> getDocumentNumbersOfPaymentRequestByCriteria(Criteria criteria, boolean orderByAscending) {
        LOG.debug("getDocumentNumberOfPaymentRequestByCriteria() started");
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(PaymentRequestDocument.class, criteria);
        rqbc.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }

    /**
     * Retrieves a list of payment requests by user defined criteria.
     * 
     * @param qbc - query with criteria
     * @return - list of payment requests
     */
    private List getPaymentRequestsByQueryByCriteria(QueryByCriteria qbc) {
        LOG.debug("getPaymentRequestsByQueryByCriteria() started");
        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return l;
    }

    /**
     * Retrieves a list of payment requests with the given vendor id and invoice number.
     * 
     * @param vendorHeaderGeneratedId - header id of the vendor id
     * @param vendorDetailAssignedId - detail id of the vendor id
     * @param invoiceNumber - invoice number as entered by AP
     * @return - List of payment requests.
     */
    public List getActivePaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId, String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedId);
        criteria.addEqualTo("invoiceNumber", invoiceNumber);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(java.lang.Integer,
     *      org.kuali.core.util.KualiDecimal, java.sql.Date)
     */
    public List getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(Integer poId, KualiDecimal vendorInvoiceAmount, Date invoiceDate) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("purchaseOrderIdentifier", poId);
        criteria.addEqualTo("vendorInvoiceAmount", vendorInvoiceAmount);
        criteria.addEqualTo("invoiceDate", invoiceDate);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#deleteSummaryAccounts(java.lang.Integer)
     */
    public void deleteSummaryAccounts(Integer purapDocumentIdentifier) {
        LOG.debug("deleteSummaryAccounts() started");

        if (purapDocumentIdentifier != null) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, purapDocumentIdentifier);

            getPersistenceBrokerTemplate().deleteByQuery(QueryFactory.newQuery(PaymentRequestSummaryAccount.class, criteria));
            getPersistenceBrokerTemplate().clearCache();
        }
    }

    public void setNegativePaymentRequestApprovalLimitDao(NegativePaymentRequestApprovalLimitDao negativePaymentRequestApprovalLimitDao) {
        this.negativePaymentRequestApprovalLimitDao = negativePaymentRequestApprovalLimitDao;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
