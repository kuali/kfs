package edu.arizona.kfs.module.purap.document.dataaccess.impl;

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
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.framework.persistence.ojb.dao.PlatformAwareDaoBaseOjb;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherSourceAccountingLineExtension;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.module.purap.TaxHelper;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.module.purap.document.dataaccess.TaxReporting1099Dao;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;
import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.businessobject.Payment;
import edu.arizona.kfs.tax.businessobject.PaymentDetailSearch;
import edu.arizona.kfs.tax.document.web.struts.PayeeSearchForm;
import edu.arizona.kfs.tax.service.impl.DocumentPaymentInformation;

public class TaxReporting1099DaoOjb extends PlatformAwareDaoBaseOjb implements TaxReporting1099Dao {
	private static final Logger LOG = Logger.getLogger(TaxReporting1099DaoOjb.class);

	private Set<String> nonPdpPaymentMethodCodes;

	public List<VendorDetail> getVendors(String vendorOwnershipCode, String vendorOwnershipCategoryCode, boolean isAllow) {
		Criteria criteria = new Criteria();
		criteria.addEqualTo(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE, vendorOwnershipCode);

		if (isAllow) {
			if (StringUtils.isNotBlank(vendorOwnershipCategoryCode)) {
				criteria.addEqualTo(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE, vendorOwnershipCategoryCode);
			}
		} 
		else {
			if (StringUtils.isNotBlank(vendorOwnershipCategoryCode)) {
				criteria.addEqualTo(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE, vendorOwnershipCategoryCode);
			} 
			else {
				criteria.addIsNull(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE);
			}
		}

		QueryByCriteria qbc = new QueryByCriteria(VendorDetail.class, criteria);

		if (LOG.isDebugEnabled()) {
			LOG.debug("getVendors() query = " + qbc.toString());
		}

		return (List<VendorDetail>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
	}

	public List<Payee> getPayees(PayeeSearchForm form) {
		Criteria criteria = new Criteria();

		if (form.getVendorName() != null) {
			criteria.addLike(VendorPropertyConstants.VENDOR_NAME, form.getVendorName());
		}

		if (form.getHeaderTaxNumber() != null) {
			criteria.addEqualTo(TaxPropertyConstants.HEADER_TAX_NUMBER, form.getHeaderTaxNumber());
		}

		if (form.getVendorNumber() != null) {
			VendorDetail vDUtil = new VendorDetail();
			vDUtil.setVendorNumber(form.getVendorNumber());

			criteria.addEqualTo(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vDUtil.getVendorHeaderGeneratedIdentifier());
			criteria.addEqualTo(VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vDUtil.getVendorDetailAssignedIdentifier());
		}

		if (form.getTaxYear() != null) {
			criteria.addEqualTo(TaxPropertyConstants.TAX_YEAR, form.getTaxYear());
		}

		QueryByCriteria qbc = new QueryByCriteria(Payee.class, criteria);
		qbc.addOrderBy(TaxPropertyConstants.PAYEE_ADDRESS_ZIP_CODE, true);
		qbc.addOrderBy(VendorPropertyConstants.VENDOR_NAME, true);

		if (LOG.isDebugEnabled()) {
			LOG.debug("getPayees() query = " + qbc.toString());
		}

		return (List<Payee>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
	}

	public List<Payment> getAllExistingPayments(Timestamp startDt, Timestamp endDt, Integer taxYear) {
		Criteria criteria = new Criteria();

		criteria.addEqualTo(TaxPropertyConstants.PAYEE_TAX_YEAR, taxYear);

		if (startDt != null) {
			criteria.addGreaterOrEqualThan(TaxPropertyConstants.PAYMENT_DISBURSEMENT_DATE, startDt);
		}
		if (endDt != null) {
			criteria.addLessOrEqualThan(TaxPropertyConstants.PAYMENT_DISBURSEMENT_DATE, endDt);
		}

		QueryByCriteria qbc = new QueryByCriteria(Payment.class, criteria);

		if (LOG.isDebugEnabled()) {
			LOG.debug("getAllExistingPayments() query = " + qbc.toString());
		}

		return (List<Payment>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
	}

	public List<PaymentDetail> getAllPaymentsForSearchCriteria(PaymentDetailSearch pds) {
		Criteria criteria = new Criteria();

		if (StringUtils.isNotBlank(pds.getCustPaymentDocNbr())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_DOC_NUMBER, "%" + pds.getCustPaymentDocNbr() + "%");
		}

		if (StringUtils.isNotBlank(pds.getInvoiceNbr())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_INVOICE_NUMBER, "%" + pds.getInvoiceNbr() + "%");
		}

		if (StringUtils.isNotBlank(pds.getRequisitionNbr())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_REQUISITION_NUMBER, "%" + pds.getRequisitionNbr() + "%");
		}

		if (StringUtils.isNotBlank(pds.getPurchaseOrderNbr())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_PURCHASE_ORDER_NUMBER, "%" + pds.getPurchaseOrderNbr() + "%");
		}

		if (StringUtils.isNotBlank(pds.getCustomerInstitutionNumber())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_CUSTOMER_INSTITUTION_NUMBER, "%" + pds.getCustomerInstitutionNumber() + "%");
		}

		if (StringUtils.isNotBlank(pds.getPayeeName())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME, "%" + pds.getPayeeName().toUpperCase() + "%");
		}

		if (StringUtils.isNotBlank(pds.getPayeeId())) {
			criteria.addEqualTo(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID, pds.getPayeeId());
		}

		if (StringUtils.isNotBlank(pds.getPayeeIdTypeCd())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_ID_TYPE_CODE, "%" + pds.getPayeeIdTypeCd() + "%");
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
			if (TaxPropertyConstants.HELD_TAX_ALL.equals(pds.getPaymentStatusCode())) {
				criteria.addLike(TaxPropertyConstants.PAYMENT_STATUS_CODE, "HTX%");
			} 
			else {
				criteria.addEqualTo(TaxPropertyConstants.PAYMENT_STATUS_CODE, pds.getPaymentStatusCode());
			}
		}

		if (StringUtils.isNotBlank(pds.getDisbursementTypeCode())) {
			criteria.addEqualTo(TaxPropertyConstants.PAYMENT_DISBURSEMENT_TYPE_CODE, pds.getDisbursementTypeCode());
		}

		if (StringUtils.isNotBlank(pds.getChartCode())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_CHART_CODE, "%" + pds.getChartCode() + "%");
		}

		if (StringUtils.isNotBlank(pds.getOrgCode())) {
			criteria.addLike(TaxPropertyConstants.PAYMENT_CUSTMER_ORG_CODE, "%" + pds.getOrgCode() + "%");
		}

		if (StringUtils.isNotBlank(pds.getSubUnitCode())) {
			criteria.addLike(PdpPropertyConstants.PaymentDetail.PAYMENT_SUBUNIT_CODE, "%" + pds.getSubUnitCode() + "%");
		}

		QueryByCriteria qbc = new QueryByCriteria(PaymentDetail.class, criteria);
		qbc.addOrderBy(PdpPropertyConstants.PaymentDetail.PAYMENT_PAYEE_NAME, true);

		if (LOG.isDebugEnabled()) {
			LOG.debug("getAllPaymentsForSearchCriteria() query = " + qbc.toString());
		}

		return (List<PaymentDetail>) getPersistenceBrokerTemplate().getCollectionByQuery(qbc);
	}

	public Integer getPurapIdentifierFromDocumentNumber(Class clazz, String documentNumber) {
		Integer retval = null;

		// make sure the input class is a purap document
		if (TaxHelper.isValidPurapDocumentClass(clazz)) {
			Criteria criteria = new Criteria();
			criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

			ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, criteria);

			query.setAttributes(new String[] { PurapPropertyConstants.PURAP_DOC_ID });
			Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
			if (iterator.hasNext()) {
				Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
				if (data[0] != null) {
					BigDecimal bd = (BigDecimal) data[0];
					retval = Integer.valueOf(bd.intValue());
				}
			}
		} 
		else if (clazz == null) {
			LOG.warn("null class passed to method");
		} 
		else {
			LOG.warn("Invalid class " + clazz.getName() + " passed to method");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("documentNumber: " + documentNumber + ", retval: " + retval);
		}

		return retval;
	}

	public DocumentPaymentInformation getDocumentPaymentInformation(Class clazz, String documentNumber) {
		DocumentPaymentInformation retval = null;

		if (TaxHelper.isValidIncomeTypeDocumentClass(clazz)) {
			Criteria criteria = new Criteria();
			criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

			ReportQueryByCriteria query = QueryFactory.newReportQuery(clazz, criteria);

			if (TaxHelper.isPreqClass(clazz)) {
				// Batch job payeeMasterExtractJob needs to pull PREQ trans for Wire, Foreign Draft and Manual Check
				query.setAttributes(new String[] {
						TaxPropertyConstants.PAYMENT_METHOD_CODE,
						TaxPropertyConstants.PAYMENT_PAID_TIMESTAMP,
						PurapPropertyConstants.INVOICE_NUMBER });
			} 
			else if (TaxHelper.isCmClass(clazz)) {
				query.setAttributes(new String[] {
						TaxPropertyConstants.PAYMENT_METHOD_CODE,
						TaxPropertyConstants.CREDIT_MEMO_PAID_TIMESTAMP,
						PurapPropertyConstants.CREDIT_MEMO_NUMBER });
			} 
			else if (TaxHelper.isDvClass(clazz)) {
				query.setAttributes(new String[] {
						KFSPropertyConstants.DISB_VCHR_PAYMENT_METHOD_CODE,
						TaxPropertyConstants.PAID_DATE });
			}

			Iterator<Object[]> iterator = getPersistenceBrokerTemplate().getReportQueryIteratorByQuery(query);
			if (iterator.hasNext()) {
				retval = new DocumentPaymentInformation();
				Object[] data = (Object[]) TransactionalServiceUtils.retrieveFirstAndExhaustIterator(iterator);
				retval.setPaymentMethodCode((String) data[0]);
				retval.setPayDate((java.util.Date) data[1]);

				if (!DisbursementVoucherDocument.class.equals(clazz)) {
					retval.setInvoiceNumber((String) data[2]);
				} 
				else {
					retval.setInvoiceNumber(getInvoiceNumberFromDvAccountingLine(documentNumber));
				}

			} 
			else {
				LOG.warn("no data found for class=" + clazz.getName() + ", document=" + documentNumber);
			}
		} 
		else if (clazz == null) {
			LOG.warn("null class passed to method");
		} 
		else {
			LOG.warn("Invalid class " + clazz.getName() + " passed to method");
		}

		if (LOG.isDebugEnabled()) {
			if (retval != null) {
				LOG.debug("documentNumber: " + documentNumber + ", paymentMethodCode: " + retval.getPaymentMethodCode() + ", payDate: " + retval.getPayDate() + ", invoiceNbr: " + retval.getInvoiceNumber());
			} 
			else {
				LOG.debug("no document info found for documentNumber: " + documentNumber);
			}
		}

		return retval;
	}

	private String getInvoiceNumberFromDvAccountingLine(String documentNumber) {
		String retval = null;
		Criteria criteria = new Criteria();
		criteria.addEqualTo(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
		criteria.addEqualTo(KFSPropertyConstants.SEQUENCE_NUMBER, Integer.valueOf(1));

		ReportQueryByCriteria query = QueryFactory.newReportQuery(DisbursementVoucherSourceAccountingLineExtension.class, criteria);
		query.setAttributes(new String[] { PurapPropertyConstants.INVOICE_NUMBER });

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
		criteria.addEqualTo(TaxPropertyConstants.PAYMENT_DOCUMENT_NUMBER, documentNumber);

		ReportQueryByCriteria query = QueryFactory.newReportQuery(Payment.class, criteria);
		query.setAttributes(new String[] { "count(" + TaxPropertyConstants.PAYMENT_DOCUMENT_NUMBER + ")" });

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

	public List<String> getNonPdpDVDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly) {
		List<String> retval = new ArrayList<String>();
		Criteria criteria = new Criteria();

		if (LOG.isDebugEnabled()) {
			LOG.debug("vendorHeaderGeneratedIdentifier: " + vendorHeaderGeneratedIdentifier + ", vendorDetailAssignedIdentifier: " + vendorDetailAssignedIdentifier + ", startDate" + startDate + ", endDate: " + endDate + ", newEntriesOnly: " + newEntriesOnly);
		}

		criteria.addEqualTo(TaxPropertyConstants.DV_PAYEE_DETAIL_ID_NUMBER, vendorHeaderGeneratedIdentifier + "-" + vendorDetailAssignedIdentifier);
		criteria.addIn(KFSPropertyConstants.DISB_VCHR_PAYMENT_METHOD_CODE, nonPdpPaymentMethodCodes);

		criteria.addGreaterOrEqualThan(TaxPropertyConstants.PAID_DATE, startDate);
		criteria.addLessThan(TaxPropertyConstants.PAID_DATE, endDate);

		ReportQueryByCriteria query = QueryFactory.newReportQuery(DisbursementVoucherDocument.class, criteria);
		query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

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

	public List<String> getNonPdpPreqDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly) {
		List<String> retval = new ArrayList<String>();
		Criteria criteria = new Criteria();

		if (LOG.isDebugEnabled()) {
			LOG.debug("vendorHeaderGeneratedIdentifier: " + vendorHeaderGeneratedIdentifier + ", vendorDetailAssignedIdentifier: " + vendorDetailAssignedIdentifier + ", startDate" + startDate + ", endDate: " + endDate + ", newEntriesOnly: " + newEntriesOnly);
		}

		criteria.addEqualTo(KFSPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
		criteria.addEqualTo(KFSPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);
		criteria.addIn(TaxPropertyConstants.PAYMENT_METHOD_CODE, nonPdpPaymentMethodCodes);

		criteria.addGreaterOrEqualThan(TaxPropertyConstants.PAYMENT_PAID_TIMESTAMP, startDate);
		criteria.addLessThan(TaxPropertyConstants.PAYMENT_PAID_TIMESTAMP, endDate);

		ReportQueryByCriteria query = QueryFactory.newReportQuery(PaymentRequestDocument.class, criteria);
		query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

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

	public List<String> getNonPdpCMDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly) {
		List<String> retval = new ArrayList<String>();
		Criteria criteria = new Criteria();

		if (LOG.isDebugEnabled()) {
			LOG.debug("vendorHeaderGeneratedIdentifier: " + vendorHeaderGeneratedIdentifier + ", vendorDetailAssignedIdentifier: " + vendorDetailAssignedIdentifier + ", startDate" + startDate + ", endDate: " + endDate + ", newEntriesOnly: " + newEntriesOnly);
		}

		criteria.addEqualTo(PurapPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorHeaderGeneratedIdentifier);
		criteria.addEqualTo(PurapPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, vendorDetailAssignedIdentifier);

		criteria.addIn(TaxPropertyConstants.PAYMENT_METHOD_CODE, nonPdpPaymentMethodCodes);

		criteria.addGreaterOrEqualThan(TaxPropertyConstants.CREDIT_MEMO_PAID_TIMESTAMP, startDate);
		criteria.addLessThan(TaxPropertyConstants.CREDIT_MEMO_PAID_TIMESTAMP, endDate);

		ReportQueryByCriteria query = QueryFactory.newReportQuery(VendorCreditMemoDocument.class, criteria);
		query.setAttributes(new String[] { KFSPropertyConstants.DOCUMENT_NUMBER });

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

	public void setNonPdpPaymentMethodCodes(Set<String> nonPdpPaymentMethodCodes) {
		this.nonPdpPaymentMethodCodes = nonPdpPaymentMethodCodes;
	}

}
