package edu.arizona.kfs.module.tax.dataaccess.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ojb.broker.query.Criteria;
import org.apache.ojb.broker.query.QueryByCriteria;
import org.apache.ojb.broker.query.QueryFactory;
import org.apache.ojb.broker.query.ReportQueryByCriteria;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.module.purap.PurapPropertyConstants;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.DocumentPaymentInformation;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payment;
import edu.arizona.kfs.module.tax.businessobject.PaymentDetailSearch;
import edu.arizona.kfs.module.tax.dataaccess.TaxReportingDao;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;

@Transactional
public class TaxReportingDaoOjb extends PlatformAwareDaoBaseOjb implements TaxReportingDao {
    private static final String HTX = "HTX";
    private static final String SQL_LIKE = "%";
    private static final Logger LOG = Logger.getLogger(TaxReportingDao.class);
    private Set<String> nonPdpPaymentMethodCodes;

    public void setNonPdpPaymentMethodCodes(Set<String> nonPdpPaymentMethodCodes) {
        this.nonPdpPaymentMethodCodes = nonPdpPaymentMethodCodes;
    }

    @Override
    public List<PaymentDetail> getAllPaymentsForSearchCriteria(PaymentDetailSearch pds) {
        Criteria criteria = new Criteria();
        if (StringUtils.isNotBlank(pds.getCustPaymentDocNbr())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER, SQL_LIKE + pds.getCustPaymentDocNbr() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getInvoiceNbr())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER, SQL_LIKE + pds.getInvoiceNbr() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getRequisitionNbr())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_REQUISITION_NUMBER, SQL_LIKE + pds.getRequisitionNbr() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getPurchaseOrderNbr())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER, SQL_LIKE + pds.getPurchaseOrderNbr() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getCustomerInstitutionNumber())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_INSTITUTION_NUMBER, SQL_LIKE + pds.getCustomerInstitutionNumber() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getPayeeName())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME, SQL_LIKE + pds.getPayeeName().toUpperCase() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getPayeeId())) {
            criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID, pds.getPayeeId());
        }
        if (StringUtils.isNotBlank(pds.getPayeeIdTypeCd())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID_TYPE_CODE, SQL_LIKE + pds.getPayeeIdTypeCd() + SQL_LIKE);
        }
        if ((pds.getDisbursementNbr() != null) && (pds.getDisbursementNbr().intValue() != 0)) {
            criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, pds.getDisbursementNbr());
        }
        if ((pds.getProcessId() != null) && (pds.getProcessId().intValue() != 0)) {
            criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_PROCESS_ID, pds.getProcessId());
        }
        if ((!(pds.getPaymentId() == null)) && (!(pds.getPaymentId() == new Integer(0)))) {
            criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, pds.getPaymentId());
        }
        if ((pds.getNetPaymentAmount() != null) && (pds.getNetPaymentAmount().doubleValue() != 0)) {
            criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_NET_AMOUNT, pds.getNetPaymentAmount());
        }
        if (pds.getBeginDisbursementDate() != null) {
            criteria.addGreaterOrEqualThan(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_DATE, pds.getBeginDisbursementDate());
        }
        if (pds.getEndDisbursementDate() != null) {
            criteria.addLessThan(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_DATE, pds.getEndDisbursementDate());
        }
        if (pds.getBeginPaymentDate() != null) {
            criteria.addGreaterOrEqualThan(PdpPropertyConstants.PaymentDetail.PAYMENT_DATE, pds.getBeginPaymentDate());
        }
        if (pds.getEndPaymentDate() != null) {
            criteria.addLessOrEqualThan(PdpPropertyConstants.PaymentDetail.PAYMENT_DATE, pds.getEndPaymentDate());
        }
        if (StringUtils.isNotBlank(pds.getPaymentStatusCode())) {
            if (TaxConstants.HELD_TAX_ALL.equals(pds.getPaymentStatusCode())) {
                criteria.addLike(TaxPropertyConstants.PAYMENT_STATUS_CODE, HTX + SQL_LIKE);
            } else {
                criteria.addEqualTo(TaxPropertyConstants.PAYMENT_STATUS_CODE, pds.getPaymentStatusCode());
            }
        }
        if (StringUtils.isNotBlank(pds.getDisbursementTypeCode())) {
            criteria.addEqualTo(TaxPropertyConstants.PAYMENT_DISBURSEMENT_TYPE_CODE, pds.getDisbursementTypeCode());
        }
        if (StringUtils.isNotBlank(pds.getChartCode())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_CHART_CODE, SQL_LIKE + pds.getChartCode() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getOrgCode())) {
            criteria.addLike(TaxPropertyConstants.PAYMENT_CUSTMER_ORG_CODE, SQL_LIKE + pds.getOrgCode() + SQL_LIKE);
        }
        if (StringUtils.isNotBlank(pds.getSubUnitCode())) {
            criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, SQL_LIKE + pds.getSubUnitCode() + SQL_LIKE);
        }
        QueryByCriteria qbc = new QueryByCriteria(PaymentDetail.class, criteria);
        qbc.addOrderBy(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME, true);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllPaymentsForSearchCriteria() query = " + qbc.toString());
        }

        @SuppressWarnings("unchecked")
        List<PaymentDetail> retval = (List<PaymentDetail>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return retval;
    }

    @Override
    public List<Payment> getAllExistingPayments(Timestamp startDt, Timestamp endDt, Integer taxYear) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TaxPropertyConstants.PAYEE_TAX_YEAR, taxYear);
        if (startDt != null) {
            criteria.addGreaterOrEqualThan(TaxPropertyConstants.PaymentFields.DISBURSEMENT_DATE, startDt);
        }
        if (endDt != null) {
            criteria.addLessOrEqualThan(TaxPropertyConstants.PaymentFields.DISBURSEMENT_DATE, endDt);
        }
        QueryByCriteria qbc = new QueryByCriteria(Payment.class, criteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getAllExistingPayments() query = " + qbc.toString());
        }

        @SuppressWarnings("unchecked")
        List<Payment> retval = (List<Payment>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return retval;
    }

    @Override
    public List<VendorDetail> getVendors(String vendorOwnershipCode, String vendorOwnershipCategoryCode, boolean isAllow) {
        Criteria criteria = new Criteria();
        criteria.addEqualTo(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE, vendorOwnershipCode);
        if (isAllow) {
            if (StringUtils.isNotBlank(vendorOwnershipCategoryCode)) {
                criteria.addEqualTo(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE, vendorOwnershipCategoryCode);
            }
        } else {
            if (StringUtils.isNotBlank(vendorOwnershipCategoryCode)) {
                criteria.addEqualTo(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE, vendorOwnershipCategoryCode);
            } else {
                criteria.addIsNull(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE);
            }
        }
        QueryByCriteria qbc = new QueryByCriteria(VendorDetail.class, criteria);
        if (LOG.isDebugEnabled()) {
            LOG.debug("getVendors() query = " + qbc.toString());
        }

        @SuppressWarnings("unchecked")
        List<VendorDetail> retval = (List<VendorDetail>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return retval;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Integer getPurapIdentifierFromDocumentNumber(Class clazz, String documentNumber) {
        Integer retval = null;

        // make sure the input class is a purap document
        if (isValidPurapDocumentClass(clazz)) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, criteria);

            query.setAttributes(new String[] { PurapPropertyConstants.PURAP_DOC_ID });

            @SuppressWarnings("unchecked")
            Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
            if (iterator.hasNext()) {
                Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
                if (data[0] != null) {
                    BigDecimal bd = (BigDecimal) data[0];
                    retval = Integer.valueOf(bd.intValue());
                }
            }
        } else if (clazz == null) {
            LOG.warn("null class passed to method");
        } else {
            LOG.warn("Invalid class " + clazz.getName() + " passed to method");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("documentNumber: " + documentNumber + ", retval: " + retval);
        }

        return retval;
    }

    @Override
    public DocumentPaymentInformation getDocumentPaymentInformation(String documentType, String documentNumber) {
        DocumentPaymentInformation retval = null;
        @SuppressWarnings("rawtypes")
        Class clazz = getClassForDocumentType(documentType);

        if (isValidIncomeTypeDocumentClass(clazz)) {
            Criteria criteria = new Criteria();
            criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

            ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, criteria);

            if (isPreqClass(clazz)) {
                // Batch job payeeMasterExtractJob needs to pull PREQ trans for Wire, Foreign Draft and Manual Check
                query.setAttributes(new String[] { KFSPropertyConstants.PAYMENT_METHOD_CODE, PurapPropertyConstants.PAYMENT_REQUEST_PAYMENT_PAID_TIMESTAMP, PurapPropertyConstants.INVOICE_NUMBER });
            } else if (isCmClass(clazz)) {
                query.setAttributes(new String[] { KFSPropertyConstants.PAYMENT_METHOD_CODE, PurapPropertyConstants.CREDIT_MEMO_PAID_TIMESTAMP, PurapPropertyConstants.CREDIT_MEMO_NUMBER });
            } else if (isDvClass(clazz)) {
                query.setAttributes(new String[] { KFSPropertyConstants.DISB_VCHR_PAYMENT_METHOD_CODE, KFSPropertyConstants.PAID_DATE });
            }

            @SuppressWarnings("unchecked")
            Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
            if (iterator.hasNext()) {
                retval = new DocumentPaymentInformation();
                Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
                retval.setPaymentMethodCode((String) data[0]);
                retval.setPayDate((java.util.Date) data[1]);

                if (!DisbursementVoucherDocument.class.equals(clazz)) {
                    retval.setInvoiceNumber((String) data[2]);
                } else {
                    retval.setInvoiceNumber(getInvoiceNumberFromDvAccountingLine(documentNumber));
                }

            } else {
                LOG.warn("no data found for class=" + clazz.getName() + ", document=" + documentNumber);
            }
        } else if (clazz == null) {
            LOG.warn("null class passed to method");
        } else {
            LOG.warn("Invalid class " + clazz.getName() + " passed to method");
        }

        if (LOG.isDebugEnabled()) {
            if (retval != null) {
                LOG.debug("documentNumber: " + documentNumber + ", paymentMethodCode: " + retval.getPaymentMethodCode() + ", payDate: " + retval.getPayDate() + ", invoiceNbr: " + retval.getInvoiceNumber());
            } else {
                LOG.debug("no document info found for documentNumber: " + documentNumber);
            }
        }

        return retval;
    }

    @Override
    public List<String> getNonPdpDVDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly) {
        List<String> retval = new ArrayList<String>();
        Criteria criteria = new Criteria();

        if (LOG.isDebugEnabled()) {
            LOG.debug("vendorHeaderGeneratedIdentifier: " + vendorHeaderGeneratedIdentifier + ", vendorDetailAssignedIdentifier: " + vendorDetailAssignedIdentifier + ", startDate" + startDate + ", endDate: " + endDate + ", newEntriesOnly: " + newEntriesOnly);
        }

        criteria.addEqualTo(KFSPropertyConstants.DV_PAYEE_DETAIL_PAYEE_ID_NUMBER, vendorHeaderGeneratedIdentifier + KFSConstants.DASH + vendorDetailAssignedIdentifier);
        criteria.addIn(KFSPropertyConstants.DISB_VCHR_PAYMENT_METHOD_CODE, nonPdpPaymentMethodCodes);

        criteria.addGreaterOrEqualThan(KFSPropertyConstants.PAID_DATE, startDate);
        criteria.addLessThan(KFSPropertyConstants.PAID_DATE, endDate);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(DisbursementVoucherDocument.class, criteria);
        query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

        @SuppressWarnings("unchecked")
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        if (iterator.hasNext()) {
            Object[] data = iterator.next();
            String docId = (String) data[0];

            if (!newEntriesOnly || !paymentExists(docId)) {
                retval.add(docId);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("found " + retval.size() + " documents");
        }

        return retval;
    }

    @Override
    public List<String> getNonPdpPreqDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly) {
        List<String> retval = new ArrayList<String>();
        Criteria criteria = new Criteria();

        if (LOG.isDebugEnabled()) {
            LOG.debug("vendorHeaderGeneratedIdentifier: " + vendorHeaderGeneratedIdentifier + ", vendorDetailAssignedIdentifier: " + vendorDetailAssignedIdentifier + ", startDate" + startDate + ", endDate: " + endDate + ", newEntriesOnly: " + newEntriesOnly);
        }

        criteria.addEqualTo(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
        criteria.addEqualTo(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);
        criteria.addIn(KFSPropertyConstants.PAYMENT_METHOD_CODE, nonPdpPaymentMethodCodes);

        criteria.addGreaterOrEqualThan(PurapPropertyConstants.PAYMENT_REQUEST_PAYMENT_PAID_TIMESTAMP, startDate);
        criteria.addLessThan(PurapPropertyConstants.PAYMENT_REQUEST_PAYMENT_PAID_TIMESTAMP, endDate);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(PaymentRequestDocument.class, criteria);
        query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

        @SuppressWarnings("unchecked")
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        if (iterator.hasNext()) {
            Object[] data = iterator.next();
            String docId = (String) data[0];

            if (!newEntriesOnly || !paymentExists(docId)) {
                retval.add(docId);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("found " + retval.size() + " documents");
        }

        return retval;
    }

    @Override
    public List<String> getNonPdpCMDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly) {
        List<String> retval = new ArrayList<String>();
        Criteria criteria = new Criteria();

        if (LOG.isDebugEnabled()) {
            LOG.debug("vendorHeaderGeneratedIdentifier: " + vendorHeaderGeneratedIdentifier + ", vendorDetailAssignedIdentifier: " + vendorDetailAssignedIdentifier + ", startDate" + startDate + ", endDate: " + endDate + ", newEntriesOnly: " + newEntriesOnly);
        }

        criteria.addEqualTo(PurapPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
        criteria.addEqualTo(PurapPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);

        criteria.addIn(KFSPropertyConstants.PAYMENT_METHOD_CODE, nonPdpPaymentMethodCodes);

        criteria.addGreaterOrEqualThan(PurapPropertyConstants.CREDIT_MEMO_PAID_TIMESTAMP, startDate);
        criteria.addLessThan(PurapPropertyConstants.CREDIT_MEMO_PAID_TIMESTAMP, endDate);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(VendorCreditMemoDocument.class, criteria);
        query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

        @SuppressWarnings("unchecked")
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        if (iterator.hasNext()) {
            Object[] data = iterator.next();
            String docId = (String) data[0];

            if (!newEntriesOnly || !paymentExists(docId)) {
                retval.add(docId);
            }
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("found " + retval.size() + " documents");
        }

        return retval;
    }

    @Override
    public List<Payee> getPayees(String vendorName, String headerTaxNumber, String vendorNumber, Integer taxYear) {

        Criteria criteria = new Criteria();

        if (vendorName != null) {
            criteria.addLike(VendorPropertyConstants.VENDOR_NAME, vendorName);
        }

        if (headerTaxNumber != null) {
            criteria.addEqualTo(TaxPropertyConstants.PayeeFields.HEADER_TAX_NUMBER, headerTaxNumber);
        }

        if (vendorNumber != null) {
            VendorDetail vDUtil = new VendorDetail();
            vDUtil.setVendorNumber(vendorNumber);

            criteria.addEqualTo(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vDUtil.getVendorHeaderGeneratedIdentifier());
            criteria.addEqualTo(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vDUtil.getVendorDetailAssignedIdentifier());
        }

        if (taxYear != null) {
            criteria.addEqualTo(TaxPropertyConstants.PayeeFields.TAX_YEAR, taxYear);
        }

        QueryByCriteria qbc = new QueryByCriteria(Payee.class, criteria);
        qbc.addOrderBy(TaxPropertyConstants.PayeeFields.ADDRESS_ZIP_CODE, true);
        qbc.addOrderBy(VendorPropertyConstants.VENDOR_NAME, true);

        if (LOG.isDebugEnabled()) {
            LOG.debug("getPayees() query = " + qbc.toString());
        }

        @SuppressWarnings("unchecked")
        List<Payee> payeeList = (List<Payee>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
        return payeeList;
    }

    private String getInvoiceNumberFromDvAccountingLine(String documentNumber) {
        String retval = null;
        Criteria criteria = new Criteria();
        criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        criteria.addEqualTo(KFSPropertyConstants.SEQUENCE_NUMBER, Integer.valueOf(1));

        ReportQueryByCriteria query = QueryFactory.newReportQuery(DisbursementVoucherSourceAccountingLineExtension.class, criteria);
        query.setAttributes(new String[] { PurapPropertyConstants.INVOICE_NUMBER });

        @SuppressWarnings("unchecked")
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            retval = (String) data[0];
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("documentNumber: " + documentNumber + ", invoiceNumber: " + retval);
        }

        return retval;
    }

    private boolean paymentExists(String documentNumber) {
        boolean retval = false;
        Criteria criteria = new Criteria();
        criteria.addEqualTo(TaxPropertyConstants.PaymentFields.DOCUMENT_NUMBER, documentNumber);

        ReportQueryByCriteria query = QueryFactory.newReportQuery(Payment.class, criteria);
        query.setAttributes(new String[] { "count(" + TaxPropertyConstants.PaymentFields.DOCUMENT_NUMBER + ")" });

        @SuppressWarnings("unchecked")
        Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);

        if (iterator.hasNext()) {
            Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
            BigDecimal bd = (BigDecimal) data[0];

            retval = (bd.intValue() > 0);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("documentNumber= " + documentNumber + ", retval= " + retval);
        }

        return retval;
    }

    private boolean isPreqClass(@SuppressWarnings("rawtypes") Class clazz) {
        boolean retval = PaymentRequestDocument.class.equals(clazz);
        return retval;
    }

    private boolean isCmClass(@SuppressWarnings("rawtypes") Class clazz) {
        boolean retval = VendorCreditMemoDocument.class.equals(clazz);
        return retval;
    }

    private boolean isDvClass(@SuppressWarnings("rawtypes") Class clazz) {
        boolean retval = DisbursementVoucherDocument.class.equals(clazz);
        return retval;
    }

    @SuppressWarnings("rawtypes")
    private Class getClassForDocumentType(String documentType) {
        switch (documentType) {
            case TaxConstants.DocumentTypes.PAYMENT_REQUEST_DOCUMENT:
            case TaxConstants.DocumentTypes.PAYMENT_REQUEST_DOCUMENT_NON_CHECK:
                return PaymentRequestDocument.class;
            case TaxConstants.DocumentTypes.CREDIT_MEMO_DOCUMENT:
                return VendorCreditMemoDocument.class;
            case TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT:
            case TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT_CHECKACH:
            case TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT_NON_CHECK:
                return DisbursementVoucherDocument.class;
        }
        return null;
    }

    private boolean isValidIncomeTypeDocumentClass(@SuppressWarnings("rawtypes") Class clazz) {
        return ((clazz != null) && (isCmClass(clazz) || isPreqClass(clazz) || isDvClass(clazz)));
    }

    private boolean isValidPurapDocumentClass(@SuppressWarnings("rawtypes") Class clazz) {
        return ((clazz != null) && (isCmClass(clazz) || isPreqClass(clazz)));
    }

}
