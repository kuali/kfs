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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.core.dao.ojb.PlatformAwareDaoBaseOjb;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.PurapConstants.PaymentRequestStatuses;
import org.kuali.module.purap.bo.NegativePaymentRequestApprovalLimit;
import org.kuali.module.purap.dao.NegativePaymentRequestApprovalLimitDao;
import org.kuali.module.purap.dao.PaymentRequestDao;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.RequisitionDocument;
import org.kuali.module.purap.service.PurapAccountingService;

public class PaymentRequestDaoOjb extends PlatformAwareDaoBaseOjb implements PaymentRequestDao {

    private static Logger LOG = Logger.getLogger(PaymentRequestDaoOjb.class);
    private NegativePaymentRequestApprovalLimitDao negativePaymentRequestApprovalLimitDao;
    private DateTimeService dateTimeService;
    private PurapAccountingService purapAccountingService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * The special payments query should be this:
     *  select *
            from pur.ap_pmt_rqst_t
            where pmt_rqst_stat_cd in ('AUTO', 'DPTA')
              and prcs_cmp_cd = ?
              and pmt_extrt_ts is NULL
          and pmt_hld_ind = 'N'
          and (   (   (    pmt_spcl_handlg_instrc_ln1_txt is not NULL
               or  pmt_spcl_handlg_instrc_ln2_txt is not NULL
               or  pmt_spcl_handlg_instrc_ln3_txt is not NULL
               or pmt_att_ind = 'Y')
               and trunc (pmt_rqst_pay_dt) <= trunc (sysdate))
           or IMD_PMT_IND = 'Y')})

     * @see org.kuali.module.purap.dao.PaymentRequestDao#getPaymentRequestsToExtract(boolean, java.lang.String)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(boolean onlySpecialPayments,String chartCode) {
        LOG.debug("getPaymentRequestsToExtract() started");

        Criteria criteria = new Criteria();
        if ( chartCode != null ) {
            criteria.addEqualTo("processingCampusCode", chartCode);
        }
        criteria.addIn("statusCode",Arrays.asList(PaymentRequestStatuses.STATUSES_ALLOWED_FOR_EXTRACTION));
        criteria.addIsNull("extractedDate");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        if ( onlySpecialPayments ) {
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
        } else {
            criteria.addLessOrEqualThan("paymentRequestPayDate", dateTimeService.getCurrentSqlDateMidnight());
        }

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentRequestDocument.class,criteria));
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getPaymentRequestsToExtract(java.lang.String, java.lang.Integer, java.lang.Integer, java.lang.Integer, java.lang.Integer)
     */
    public Iterator<PaymentRequestDocument> getPaymentRequestsToExtract(String campusCode,Integer paymentRequestIdentifier,Integer purchaseOrderIdentifier,Integer vendorHeaderGeneratedIdentifier,Integer vendorDetailAssignedIdentifier) {
        LOG.debug("getPaymentRequestsToExtract() started");

        List statuses = new ArrayList();
        statuses.add(PurapConstants.PaymentRequestStatuses.AUTO_APPROVED);
        statuses.add(PurapConstants.PaymentRequestStatuses.DEPARTMENT_APPROVED);

        Criteria criteria = new Criteria();
        criteria.addEqualTo("processingCampusCode", campusCode);
        criteria.addIn("statusCode",statuses);
        criteria.addIsNull("extractedDate");
        criteria.addEqualTo("holdIndicator", Boolean.FALSE);

        criteria.addLessOrEqualThan("paymentRequestPayDate", dateTimeService.getCurrentSqlDateMidnight());

        if ( paymentRequestIdentifier != null ) {
            criteria.addEqualTo("paymentRequestIdentifier",paymentRequestIdentifier);
        }
        if ( purchaseOrderIdentifier != null ) {
            criteria.addEqualTo("purchaseOrderIdentifier",purchaseOrderIdentifier);
        }
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier",vendorHeaderGeneratedIdentifier);
        criteria.addEqualTo("vendorDetailAssignedIdentifier",vendorDetailAssignedIdentifier);

        return getPersistenceBrokerTemplate().getIteratorByQuery(new QueryByCriteria(PaymentRequestDocument.class,criteria));
    }

    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getEligibleForAutoApproval()
     */
    public Iterator<PaymentRequestDocument> getEligibleForAutoApproval() {
        
        Date todayAtMidnight = dateTimeService.getCurrentSqlDateMidnight();
        
        String samt = kualiConfigurationService.getApplicationParameterValue(
                PurapParameterConstants.PURAP_ADMIN_GROUP, 
                PurapParameterConstants.PURAP_DEFAULT_NEGATIVE_PAYMENT_REQUEST_APPROVAL_LIMIT);
        KualiDecimal defaultMinimumAmount = new KualiDecimal(samt);
        
        Criteria criteria = new Criteria();
        criteria.addLessThan(PurapPropertyConstants.PAYMENT_REQUEST_PAY_DATE, todayAtMidnight);
        criteria.addNotEqualTo("holdIndicator", "Y");
        criteria.addNotEqualTo("paymentRequestedCancelIndicator", "Y");
        criteria.addIn("status", Arrays.asList(PurapConstants.PaymentRequestStatuses.PREQ_STATUSES_FOR_AUTO_APPROVE));
        
        Query query = new QueryByCriteria(PaymentRequestDocument.class, criteria);
        Iterator<PaymentRequestDocument> iterator = 
            (Iterator<PaymentRequestDocument>) getPersistenceBrokerTemplate().getIteratorByQuery(query);
        
        Set<PaymentRequestDocument> eligibleDocuments = new HashSet<PaymentRequestDocument>();
        while(iterator.hasNext()) {
            PaymentRequestDocument document = iterator.next();
            //document.getDocumentHeader().getDocumentStatus().
            if(isEligibleForAutoApproval(document, defaultMinimumAmount)) {
                eligibleDocuments.add(document);
            }
        }
        
        return eligibleDocuments.iterator();
    }
    
    /**
     * This method determines whether or not a payment request document can be
     * automatically approved. 
     * 
     * @param document
     * @return
     */
    private boolean isEligibleForAutoApproval(PaymentRequestDocument document, KualiDecimal defaultMinimumLimit) {
        boolean isEligible = false;
        
        // This minimum will be set to the minimum limit derived from all
        // accounting lines on the document. If no limit is determined, the 
        // default will be used.
        KualiDecimal minimumAmount = null;
        
        // Iterate all source accounting lines on the document, deriving a 
        // minimum limit from each according to chart, chart and account, and 
        // chart and organization. 
        for (SourceAccountingLine line : purapAccountingService.generateSummary(document.getItems())) {
            minimumAmount = minimumLimitAmount(
                    negativePaymentRequestApprovalLimitDao.findByChart(
                            line.getChartOfAccountsCode()), minimumAmount);
            minimumAmount = minimumLimitAmount(
                    negativePaymentRequestApprovalLimitDao.findByChartAndAccount(
                            line.getChartOfAccountsCode(), line.getAccountNumber()), minimumAmount);
            minimumAmount = minimumLimitAmount(
                    negativePaymentRequestApprovalLimitDao.findByChartAndOrganization(
                            line.getChartOfAccountsCode(), line.getOrganizationReferenceId()), minimumAmount);
        }
        
        // If no limit was found, the default limit is used.
        if(null == minimumAmount) {
            minimumAmount = defaultMinimumLimit;
        }
        
        // The document is eligible for auto-approval if the document total is 
        // below the limit. 
        if(document.getDocumentHeader().getFinancialDocumentTotalAmount().isLessThan(minimumAmount)) {
            isEligible = true;
        }
        
        return isEligible;
    }
    
    /**
     * This method iterates a collection of negative payment request approval 
     * limits and returns the minimum of a given minimum amount and the least
     * among the limits in the collection.
     * 
     * @param limits
     * @param minimumAmount
     * @return
     */
    private KualiDecimal minimumLimitAmount(Collection<NegativePaymentRequestApprovalLimit> limits, KualiDecimal minimumAmount) {
        for (NegativePaymentRequestApprovalLimit limit : limits) {
            KualiDecimal amount = limit.getNegativePaymentRequestApprovalLimitAmount();
            if (null == minimumAmount) {
                minimumAmount = amount;
            }
            else if (minimumAmount.isGreaterThan(amount)) {
                minimumAmount = amount;
            }
        }
        return minimumAmount;
    }
    
    public String getDocumentNumberByPaymentRequestId(Integer id) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(PurapPropertyConstants.PURAP_DOC_ID, id);
        return getDocumentNumberOfPaymentRequestByCriteria(criteria);
    }
    
    public List<String> getDocumentNumbersByPurchaseOrderId(Integer poPurApId) {
        List<String> returnList = new ArrayList<String>();
        Criteria criteria = new Criteria();
        criteria.addEqualTo( PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poPurApId );
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        while (iter.hasNext()) {
            Object[] cols = (Object[])iter.next();
            returnList.add((String)cols[0]);
        }
        return returnList;
    }
    
    public List<PaymentRequestDocument> getPaymentRequestsByPurchaseOrderId(Integer poDocId) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo( PurapPropertyConstants.PURCHASE_ORDER_IDENTIFIER, poDocId );

        List<PaymentRequestDocument> pReqs = (List<PaymentRequestDocument>)this.getPersistenceBrokerTemplate().getCollectionByQuery(
                new QueryByCriteria(PaymentRequestDocument.class, criteria));
 
        return pReqs; 
    }
    
    private String getDocumentNumberOfPaymentRequestByCriteria(Criteria criteria) {
        LOG.debug("getDocumentNumberOfPaymentRequestByCriteria() started");
        Iterator<Object[]> iter = getDocumentNumbersOfPaymentRequestByCriteria(criteria, false);
        if (iter.hasNext()) {
            Object[] cols = (Object[])iter.next();
            if (iter.hasNext()) {
                // the iterator should have held only a single doc id of data but it holds 2 or more
                String errorMsg = "Expected single document number for given criteria but multiple (at least 2) were returned";
                LOG.error(errorMsg);
                throw new RuntimeException();
            }
            return (String)cols[0];
        }
        return null;
    }
    
    private Iterator<Object[]> getDocumentNumbersOfPaymentRequestByCriteria(Criteria criteria, boolean orderByAscending) {
        LOG.debug("getDocumentNumberOfPaymentRequestByCriteria() started");
        ReportQueryByCriteria rqbc = new ReportQueryByCriteria(RequisitionDocument.class, criteria);
        rqbc.setAttributes(new String[] {KFSPropertyConstants.DOCUMENT_NUMBER});
        if (orderByAscending) {
            rqbc.addOrderByAscending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        else {
            rqbc.addOrderByDescending(KFSPropertyConstants.DOCUMENT_NUMBER);
        }
        return getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(rqbc);
    }
      
      private List getPaymentRequestsByQueryByCriteria(QueryByCriteria qbc) {
        LOG.debug("getPaymentRequestsByQueryByCriteria() started");
        List l = (List) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return l;
      }
      
      /**
       * Retreives a list of Pay Reqs with the given vendor id and invoice number.
       * 
       * @param vendorHeaderGeneratedId  header id of the vendor id
       * @param vendorDetailAssignedId   detail id of the vendor id
       * @param invoiceNumber            invoice number as entered by AP
       * @return List of Pay Reqs.
       */
      public List getActivePaymentRequestsByVendorNumberInvoiceNumber(Integer vendorHeaderGeneratedId, Integer vendorDetailAssignedId,String invoiceNumber) {
        LOG.debug("getActivePaymentRequestsByVendorNumberInvoiceNumber() started");
        Criteria criteria = new Criteria();
        criteria.addEqualTo("vendorHeaderGeneratedIdentifier", vendorHeaderGeneratedId);
        criteria.addEqualTo("vendorDetailAssignedIdentifier", vendorDetailAssignedId);
        criteria.addEqualTo("invoiceNumber", invoiceNumber);
        QueryByCriteria qbc = new QueryByCriteria(PaymentRequestDocument.class,criteria);
        return this.getPaymentRequestsByQueryByCriteria(qbc);
      }
      
    /**
     * @see org.kuali.module.purap.dao.PaymentRequestDao#getActivePaymentRequestsByPOIdInvoiceAmountInvoiceDate(java.lang.Integer, org.kuali.core.util.KualiDecimal, java.sql.Date)
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
