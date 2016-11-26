package edu.arizona.kfs.module.purap.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfCopy;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherIncomeType;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.module.purap.PurapConstants;
import edu.arizona.kfs.module.purap.TaxHelper;
import edu.arizona.kfs.module.purap.businessobject.CreditMemoIncomeType;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestIncomeType;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.module.purap.document.dataaccess.TaxReporting1099Dao;
import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;
import edu.arizona.kfs.tax.businessobject.DocumentIncomeType;
import edu.arizona.kfs.tax.businessobject.ExtractHistory;
import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.businessobject.Payer;
import edu.arizona.kfs.tax.businessobject.Payment;
import edu.arizona.kfs.tax.businessobject.PaymentDetailSearch;
import edu.arizona.kfs.tax.document.web.struts.PayeeSearchForm;
import edu.arizona.kfs.tax.service.impl.DocumentPaymentInformation;
import edu.arizona.kfs.vnd.VendorConstants;

@Transactional
public class TaxReporting1099ServiceImpl implements TaxReporting1099Service {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxReporting1099ServiceImpl.class);
	private static final SimpleDateFormat DF = new SimpleDateFormat("yyyy-MM-dd HH:mm");

	private TaxReporting1099Dao taxReporting1099Dao;
	private BusinessObjectService businessObjectService;
	private ParameterService parameterService;
	private String pdfDirectory;
	private Set<String> documentIncomeTypeSet;
	private double taxThreshold = 0.0;
	private Map<String, KualiDecimal> taxAmountByPaymentType;
	private Map<String, String> form1099BoxInformation;

	public TaxReporting1099ServiceImpl() {
	}

	@Override
	public boolean extractPayees(String jobName, Date jobRunDate) {
		LOG.info("Start extractPayees(); " + new Date());

		boolean retval = false; // Whether the next step in the batch job should run

		try {
			// load 1099 properties KFS parameters
			Map<String, String> pmtTypeCodes = TaxHelper.getOverridePaymentTypeCodeMap(parameterService);
			Map<String, String> objectCodeMap = TaxHelper.getObjectCodeMap(parameterService);
			Set<String> overridingObjCodes = TaxHelper.getOverridingObjectCodes(parameterService);
			String extractType = TaxHelper.getExtractType(parameterService);
			Set<String> extractCodes = TaxHelper.getExtractCodes(parameterService, this, extractType, overridingObjCodes);
			Set<String> vendorOwnershipCodes = TaxHelper.getVendorOwnershipCodes(parameterService, businessObjectService);
			boolean ownershipCodesAllow = TaxHelper.isVendorOwnershipCodesAllow(parameterService);
			Integer taxYear = Integer.valueOf(TaxHelper.getTaxYear(parameterService));
			boolean replaceData = parameterService.getParameterValueAsBoolean(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxConstants.Form1099.PROPERTY_REPLACE_DATA_DURING_LOAD);
			Timestamp taxYearStartDate = TaxHelper.getPaymentDate(parameterService, TaxConstants.Form1099.PROPERTY_PAYMENT_PERIOD_START);
			Timestamp taxYearEndDate = TaxHelper.getPaymentDate(parameterService, TaxConstants.Form1099.PROPERTY_PAYMENT_PERIOD_END);

			if (LOG.isInfoEnabled()) {
				LOG.info("tax year:  " + taxYear);
				LOG.info("extract type: " + extractType);
				LOG.info("extract codes: " + extractCodes);
				LOG.info("income threshold: " + getTaxThreshold(true));
				LOG.info("payment start date : " + taxYearStartDate);
				LOG.info("payment end date : " + taxYearEndDate);
				LOG.info("replace data: " + replaceData);
				LOG.info("vendor codes : " + vendorOwnershipCodes);
				LOG.info("vendor codes allow: " + ownershipCodesAllow);
			}

			ExtractHistory eh = new ExtractHistory();
			eh.setExtractStartDt(taxYearStartDate);
			eh.setExtractEndDt(taxYearEndDate);
			eh.setReplaceDataInd(replaceData);
			eh.setTaxYear(taxYear);
			eh.setExtractDt(new Timestamp(System.currentTimeMillis()));

			// Delete existing payment for period
			if (replaceData) {
				eh.setPaymentsDeleted(deleteTaxYearPayments(taxYearStartDate, taxYearEndDate, taxYear));
			}

			for (String vendorOwnershipCode : vendorOwnershipCodes) {
				Collection<VendorDetail> vendors = getVendors(vendorOwnershipCode, ownershipCodesAllow);

				for (VendorDetail vendor : vendors) {
					int saveExtractedPaymentCount = eh.getPaymentsExtracted().intValue();

					extractVendorPdpPayments(vendor, taxYear, taxYearStartDate, taxYearEndDate, eh, replaceData, objectCodeMap, extractCodes, pmtTypeCodes);

					extractVendorNonPdpPayments(vendor, taxYear, taxYearStartDate, taxYearEndDate, eh, replaceData);

					// if we have added payments then increment payees extracted
					if (eh.getPaymentsExtracted().intValue() > saveExtractedPaymentCount) {
						eh.incrementPayeesExtracted();
					}
				}
			}

			// save the ExtractHistory
			businessObjectService.save(eh);

			retval = true;

			LOG.info("end extractPayees(): " + new Date());
		} 
		catch (Exception ex) {
			throw new RuntimeException("1099 Payee Master Extract Error: ", ex);

		}
		return retval;
	}

	private void extractVendorPdpPayments(VendorDetail vendor, Integer taxYear, Timestamp taxYearStartDate, Timestamp taxYearEndDate, ExtractHistory eh, boolean replaceData, Map<String, String> objectCodeMap, Set<String> extractCodes, Map<String, String> pmtTypeCodes) throws Exception {
		// find all the payments for the given time frame for each vendor
		PaymentDetailSearch pds = new PaymentDetailSearch();
		pds.setBeginDisbursementDate(taxYearStartDate);
		pds.setPayeeIdTypeCd(PayeeIdTypeCodes.VENDOR_ID);

		// add 1 day to end date then truncate time and set as a java.sql.Date
		pds.setEndDisbursementDate(new java.sql.Date(DateUtils.addDays(DateUtils.truncate(taxYearEndDate, Calendar.DAY_OF_MONTH), 1).getTime()));

		pds.setPayeeId(vendor.getVendorNumber());

		List<PaymentDetail> paymentDetails = taxReporting1099Dao.getAllPaymentsForSearchCriteria(pds);

		if ((paymentDetails != null) && !paymentDetails.isEmpty()) {
			if (LOG.isInfoEnabled()) {
				LOG.info(vendor.getVendorName() + ": #payments=" + paymentDetails.size());
			}

			// Lookup payee by vendor number and tax year - if we don't find and existing Payee we will create a new one
			Payee payee = getPayeeByVendorNum(vendor, taxYear);

			// process payment where the DocumentIncomeType tables should be used (PREQ, DV, CM) return any payment in the unprocessedPayments that do not fit the criteria
			List<PaymentDetail> unprocessedPayments = processDocumentIncomeTypePayments(payee.getId(), paymentDetails, replaceData, eh);

			if (LOG.isDebugEnabled()) {
				if (!unprocessedPayments.isEmpty()) {
					LOG.debug("found " + unprocessedPayments.size() + " unprocessed payment details");
				}
			}

			// processing any remaining payments in the default manner
			for (PaymentDetail paymentDetail : unprocessedPayments) {
				// if we are replacing all payment information or this payment information does not already exist
				if (replaceData || !hasPayments(payee.getId(), paymentDetail)) {
					eh.incrementPaymentsExtracted(savePayments(objectCodeMap, extractCodes, pmtTypeCodes, payee, paymentDetail));
				}
			}
		} 
		else if (LOG.isDebugEnabled()) {
			LOG.debug(vendor.getVendorName() + " : No Payments Found");
		}
	}

	// TODO: This will be implemented later, requires completion of UAF-3576, UAF-3581, and UAF-3586 (1099 Tab on DV, PREQ, & CM)
	private void extractVendorNonPdpPayments(VendorDetail vendor, Integer taxYear, Timestamp taxYearStartDate, Timestamp taxYearEndDate, ExtractHistory eh, boolean replaceData) throws Exception {
		// find all the wire transfer document numbers for the given time frame for each vendor
		List<String> dvDocs = taxReporting1099Dao.getNonPdpDVDocumentNumbersForVendor(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), taxYearStartDate, taxYearEndDate, !replaceData);

		List<String> preqDocs = taxReporting1099Dao.getNonPdpPreqDocumentNumbersForVendor(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), taxYearStartDate, taxYearEndDate, !replaceData);

		List<String> cmDocs = taxReporting1099Dao.getNonPdpCMDocumentNumbersForVendor(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), taxYearStartDate, taxYearEndDate, !replaceData);

		if (LOG.isDebugEnabled()) {
			LOG.debug("dcDocs.size()=" + dvDocs.size() + ", preqDocs.size()=" + preqDocs.size() + ", cmDocs.size()=" + cmDocs.size());
		}

		// Lookup payee by vendor num an tax year - if we don't find an existing payee we will create a new one
		Payee payee = getPayeeByVendorNum(vendor, taxYear);

		// processing any remaining payment in the default manner
		for (String documentNumber : dvDocs) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("[dv] " + documentNumber);
			}
			eh.incrementPaymentsExtracted(saveNonPdpDVPayments(payee, documentNumber));
		}

		for (String documentNumber : preqDocs) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("[preq] " + documentNumber);
			}
			eh.incrementPaymentsExtracted(saveNonPdpPreqPayments(payee, documentNumber));
		}

		for (String documentNumber : cmDocs) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("[cm] " + documentNumber);
			}
			eh.incrementPaymentsExtracted(saveNonPdpCMPayments(payee, documentNumber));
		}
	}

	private <T> List<PaymentDetail> processDocumentIncomeTypePayments(Integer payeeId, List<PaymentDetail> paymentDetails, boolean replaceData, ExtractHistory eh) throws Exception {
		List<PaymentDetail> retval = new ArrayList<PaymentDetail>();

		/**
		 * use this to track which document we have processed using the
		 * DocumentIncomeType list. We may have multiple payments but will
		 * process all DocumentIncomeTypes in one shot. so we can ignore any
		 * other PaymentDetails that are referencing a previously processed
		 * document for this payee
		 */
		Set<String> processedDocsSet = new HashSet<String>();

		// lookup over payment details and see if the associated document has DocumentIncomeTypes Saved
		for (PaymentDetail paymentDetail : paymentDetails) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("paymentDetail - docType=" + paymentDetail.getFinancialDocumentTypeCode() + ", docNumber=" + paymentDetail.getCustPaymentDocNbr() + ", amount=" + paymentDetail.getNetPaymentAmount());
			}

			// if this document is a type that requires DocumentIncomeType entries
			if (documentIncomeTypeSet.contains(paymentDetail.getFinancialDocumentTypeCode())) {
				String key = buildDocumentKey(paymentDetail);
				if (LOG.isDebugEnabled()) {
					LOG.debug("Key =" + key);
				}

				// if we have not already seen this document
				if (!processedDocsSet.contains(key)) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("key " + key + " not yet processed");
					}

					// if this payment has not already been created
					if (replaceData || !hasPayments(payeeId, paymentDetail)) {
						List<DocumentIncomeType<T>> incomeTypes = get1099DocumentIncomeTypes(paymentDetail);

						// if we found income types then create and save payments
						if ((incomeTypes != null) && !incomeTypes.isEmpty()) {
							eh.incrementPaymentsExtracted(savePayments(payeeId, paymentDetail, incomeTypes));
						} 
						else {
							LOG.info("found no income type entry for document " + paymentDetail.getFinancialDocumentTypeCode() + "[" + paymentDetail.getCustPaymentDocNbr() + "]");
						}
					}
					processedDocsSet.add(key);
				}
			} 
			else {
				retval.add(paymentDetail);
			}
		}
		return retval;
	}

	private Payee createNewPayee(Integer taxYear, VendorDetail vendor) {
		Payee retval = new Payee();

		VendorAddress defaultAddress = getVendorDefaultAddress(vendor);

		if (defaultAddress != null) {
			setPayeeAddress(defaultAddress, retval);
		} 
		else {
			retval.setAddressTypeCode(TaxPropertyConstants.TAX_ADDRESS_TYPE_CD);
			LOG.warn("No address found for " + vendor.getVendorName());
		}

		setPayeeAttributes(taxYear, vendor, retval);

		// Save payee
		businessObjectService.save(retval);

		if (LOG.isInfoEnabled()) {
			LOG.info("Payee Created : " + retval.getVendorName());
		}

		return retval;
	}

	private boolean hasPayments(Integer payeeId, PaymentDetail paymentDetail) {
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		fieldValues.put(TaxPropertyConstants.PAYMENT_DETAIL_PAYMENT_GROUP_ID, paymentDetail.getPaymentGroupId());
		fieldValues.put(TaxPropertyConstants.PAYMENT_DETAIL_PAYEE_ID, payeeId);
		fieldValues.put(TaxPropertyConstants.PAYMENT_DOCUMENT_NUMBER, paymentDetail.getCustPaymentDocNbr());

		int cnt = businessObjectService.countMatching(Payment.class, fieldValues);

		if (LOG.isDebugEnabled()) {
			LOG.debug("found payments(" + cnt + "): payee=" + payeeId + ", docNumber=" + paymentDetail.getCustPaymentDocNbr() + ", paymentGroup=" + paymentDetail.getPaymentGroupId());
		}

		return (cnt > 0);
	}

	private void setPayeeAddress(VendorAddress defaultAddress, Payee p) {
		p.setAddressCityName(defaultAddress.getVendorCityName());
		p.setAddressCountryCode(defaultAddress.getVendorCountryCode());

		if (StringUtils.isNotBlank(defaultAddress.getVendorLine1Address()) && (defaultAddress.getVendorLine1Address().length() > TaxPropertyConstants.VENDOR_MAX_ADDRESS_LENGTH)) {
			p.setAddressLine1Address(defaultAddress.getVendorLine1Address().substring(0, TaxPropertyConstants.VENDOR_MAX_ADDRESS_LENGTH));

			if (LOG.isInfoEnabled()) {
				LOG.info("truncated address line 1 of payee : " + p.getVendorName());
			}
		} 
		else {
			p.setAddressLine1Address(defaultAddress.getVendorLine1Address());
		}

		if (StringUtils.isNotBlank(defaultAddress.getVendorLine2Address()) && (defaultAddress.getVendorLine2Address().length() > TaxPropertyConstants.VENDOR_MAX_ADDRESS_LENGTH)) {
			p.setAddressLine2Address(defaultAddress.getVendorLine2Address().substring(0, TaxPropertyConstants.VENDOR_MAX_ADDRESS_LENGTH));

			if (LOG.isInfoEnabled()) {
				LOG.info("truncated address line 2 of payee : " + p.getVendorName());
			}
		} 
		else {
			p.setAddressLine2Address(defaultAddress.getVendorLine2Address());
		}

		p.setAddressStateCode(defaultAddress.getVendorStateCode());
		p.setAddressZipCode(StringUtils.remove(defaultAddress.getVendorZipCode(), "-"));
		p.setAddressTypeCode(defaultAddress.getVendorAddressTypeCode());
	}

	private void setPayeeAttributes(Integer taxYear, VendorDetail vendor, Payee p) {
		p.setVendorHeaderGeneratedIdentifier(vendor.getVendorHeader().getVendorHeaderGeneratedIdentifier());
		p.setVendorDetailAssignedIdentifier(vendor.getVendorDetailAssignedIdentifier());
		p.setVendorName(vendor.getVendorName());
		p.setHeaderOwnershipCategoryCode(vendor.getVendorHeader().getVendorOwnershipCategoryCode());
		p.setHeaderOwnershipCode(vendor.getVendorHeader().getVendorOwnershipCode());
		p.setHeaderTaxNumber(vendor.getVendorHeader().getVendorTaxNumber());
		p.setHeaderTypeCode(vendor.getVendorHeader().getVendorTypeCode());
		p.setTaxYear(taxYear);

		if (vendor.getVendorHeader().getVendorForeignIndicator() == null) {
			p.setVendorForeignIndicator(Boolean.FALSE);
		} 
		else {
			p.setVendorForeignIndicator(vendor.getVendorHeader().getVendorForeignIndicator());
		}

		p.setCorrectionIndicator(Boolean.FALSE);
		p.setExcludeIndicator(Boolean.FALSE);
		p.setVendorFederalWithholdingTaxBeginningDate(vendor.getVendorHeader().getVendorFederalWithholdingTaxBeginningDate());
		p.setVendorFederalWithholdingTaxEndDate(vendor.getVendorHeader().getVendorFederalWithholdingTaxEndDate());
		p.setVendorNumber(vendor.getVendorNumber());
	}

	private <T> int savePayments(Integer payeeId, PaymentDetail paymentDetail, List<DocumentIncomeType<T>> incomeTypes) throws Exception {
		int retval = 0;
		if (LOG.isDebugEnabled()) {
			LOG.debug("payee=" + payeeId + ", #incomeTypes=" + incomeTypes.size());
		}

		DocumentPaymentInformation paymentInfo = null;

		for (DocumentIncomeType<T> incomeType : incomeTypes) {
			boolean reportable = !TaxPropertyConstants.INCOME_TYPE_CODE_NOT_REPORTABLE.equals(incomeType.getIncomeTypeCode());

			if (reportable) {
				// lazy load paymentInfo (date paid, payment method code, invoice number) for this document
				if (paymentInfo == null) {
					paymentInfo = taxReporting1099Dao.getDocumentPaymentInformation(TaxHelper.getClassForDocumentType(paymentDetail.getFinancialDocumentTypeCode()), paymentDetail.getCustPaymentDocNbr());
				}

				if (paymentInfo != null) {
					// get a new Payment that is pre-populated with PaymentDetail information
					Payment payment = new Payment();

					payment.setPayeeId(payeeId);
					payment.setExcludeIndicator(Boolean.FALSE);
					payment.setDisbursementDt(paymentDetail.getPaymentGroup().getDisbursementDate());

					if (paymentInfo.isCheckACHPayment()) {
						payment.setDisbursementNbr(paymentDetail.getPaymentGroup().getDisbursementNbr());
						payment.setPaymentGroupId(paymentDetail.getPaymentGroup().getId());
						if (TaxHelper.isDv(paymentDetail.getFinancialDocumentTypeCode())) {
							payment.setDocType(TaxPropertyConstants.DV_DOCUMENT_TYPE_CODE);
						} 
						else {
							payment.setDocType(paymentDetail.getFinancialDocumentTypeCode());
						}
					} 
					else {
						if (TaxHelper.isPreq(paymentDetail.getFinancialDocumentTypeCode())) {
							payment.setDocType(PaymentRequestDocument.DOCUMENT_TYPE_NON_CHECK);
						}
						else if (TaxHelper.isCm(paymentDetail.getFinancialDocumentTypeCode())) {
							payment.setDocType(VendorCreditMemoDocument.DOCUMENT_TYPE_NON_CHECK);
						} 
						else if (TaxHelper.isDv(paymentDetail.getFinancialDocumentTypeCode())) {
							payment.setDocType(DisbursementVoucherDocument.DOCUMENT_TYPE_DV_NON_CHECK);
						}
					}

					payment.setDocNbr(paymentDetail.getCustPaymentDocNbr());
					payment.setInvoiceNbr(paymentInfo.getInvoiceNumber());
					payment.setPoNbr(paymentDetail.getPurchaseOrderNbr());
					payment.setFinChartCode(incomeType.getChartOfAccountsCode());
					payment.setPaymentTypeCode(incomeType.getIncomeType().getIncomeTypeBox());

					// set as negative for CM
					if (TaxHelper.isCm(paymentDetail.getFinancialDocumentTypeCode())) {
						payment.setAcctNetAmount(incomeType.getAmount().negated());
					} 
					else {
						payment.setAcctNetAmount(incomeType.getAmount());
					}

					if (LOG.isDebugEnabled()) {
						LOG.debug("saving payment for payee=" + payment.getPayeeId() + ", incomeType=" + incomeType.getIncomeType().getIncomeTypeBox() + ", amount=" + incomeType.getAmount());
					}

					businessObjectService.save(payment);
					retval++;
				} 
				else {
					LOG.warn("unable to find documnet payment information for payee=" + payeeId + ", incomeType=" + incomeType.getIncomeType().getIncomeTypeBox() + ", documentNumber=" + paymentDetail.getCustPaymentDocNbr());
				}
			}
		}

		return retval;
	}

	private int saveNonPdpDVPayments(Payee payee, String documentNumber) throws Exception {
		int retval = 0;

		DisbursementVoucherDocument doc = businessObjectService.findBySinglePrimaryKey(DisbursementVoucherDocument.class, documentNumber);

		if (doc != null) {
			List<DocumentIncomeType<DisbursementVoucherDocument>> incomeTypes = get1099DocumentIncomeTypes(KFSConstants.DOCUWARE_DV_DOC_TYPE, documentNumber);

			// if we found income types then create and save payments
			if (incomeTypes != null) {
				for (DocumentIncomeType<DisbursementVoucherDocument> incomeType : incomeTypes) {
					// get a new Payment that is pre-populated with PaymentDetail information
					Payment payment = new Payment();

					payment.setPayeeId(payee.getId());
					payment.setExcludeIndicator(Boolean.FALSE);
					payment.setDocNbr(documentNumber);
					payment.setDocType(DisbursementVoucherDocument.DOCUMENT_TYPE_DV_NON_CHECK);
					payment.setAcctNetAmount(incomeType.getAmount());
					payment.setFinChartCode(incomeType.getChartOfAccountsCode());
					payment.setDisbursementDt(doc.getPaidDate());
					payment.setPaymentTypeCode(incomeType.getIncomeType().getIncomeTypeBox());
					businessObjectService.save(payment);

					retval++;
				}
			}
		}

		return retval;
	}

	private int saveNonPdpCMPayments(Payee payee, String documentNumber) throws Exception {
		int retval = 0;

		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

		List<VendorCreditMemoDocument> docs = (List<VendorCreditMemoDocument>) businessObjectService.findMatching(VendorCreditMemoDocument.class, criteria);

		if ((docs != null) && !docs.isEmpty()) {
			VendorCreditMemoDocument doc = docs.get(0);
			if (LOG.isDebugEnabled()) {
				LOG.debug("found " + docs.size() + " docs");
			}

			List<DocumentIncomeType<VendorCreditMemoDocument>> incomeTypes = get1099DocumentIncomeTypes(PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT, documentNumber);

			// if we found income type then create and save payments
			if (incomeTypes != null) {
				for (DocumentIncomeType<VendorCreditMemoDocument> incomeType : incomeTypes) {
					// get a new Payment that is pre-populated with PaymentDetail information
					Payment payment = new Payment();

					payment.setPayeeId(payee.getId());
					payment.setExcludeIndicator(Boolean.FALSE);
					payment.setDocNbr(documentNumber);
					payment.setDocType(VendorCreditMemoDocument.DOCUMENT_TYPE_NON_CHECK);
					payment.setAcctNetAmount(incomeType.getAmount().negated());
					payment.setFinChartCode(incomeType.getChartOfAccountsCode());
					payment.setDisbursementDt(new java.sql.Date(doc.getCreditMemoPaidTimestamp().getTime()));
					payment.setPaymentTypeCode(incomeType.getIncomeType().getIncomeTypeBox());
					businessObjectService.save(payment);

					retval++;
				}
			}
		}

		return retval;
	}

	private int saveNonPdpPreqPayments(Payee payee, String documentNumber) throws Exception {
		int retval = 0;

		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

		List<PaymentRequestDocument> docs = (List<PaymentRequestDocument>) businessObjectService.findMatching(PaymentRequestDocument.class, criteria);

		if ((docs != null) && !docs.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("found " + docs.size() + " docs");
			}

			PaymentRequestDocument doc = docs.get(0);

			List<DocumentIncomeType<PaymentRequestDocument>> incomeTypes = get1099DocumentIncomeTypes(KFSConstants.DOCUWARE_PREQ_DOC_TYPE, documentNumber);

			if (incomeTypes != null) {
				for (DocumentIncomeType<PaymentRequestDocument> incomeType : incomeTypes) {
					// get a new Payment that is pre-populated with PaymentDetail information
					Payment payment = new Payment();

					payment.setPayeeId(payee.getId());
					payment.setExcludeIndicator(Boolean.FALSE);
					payment.setDocNbr(documentNumber);
					payment.setDocType(PaymentRequestDocument.DOCUMENT_TYPE_NON_CHECK);
					payment.setPaymentTypeCode(incomeType.getIncomeType().getIncomeTypeBox());
					payment.setAcctNetAmount(incomeType.getAmount());
					payment.setFinChartCode(incomeType.getChartOfAccountsCode());
					payment.setDisbursementDt(new java.sql.Date(doc.getPaymentPaidTimestamp().getTime()));
					payment.setInvoiceNbr(doc.getInvoiceNumber());

					if (doc.getPurchaseOrderIdentifier() != null) {
						payment.setPoNbr(doc.getPurchaseOrderIdentifier().toString());
					}

					businessObjectService.save(payment);

					retval++;
				}
			}
		}

		return retval;
	}

	private int savePayments(Map<String, String> objectCodeMap, Set<String> extractCodes, Map<String, String> pmtTypeCodes, Payee payee, PaymentDetail paymentDetail) throws Exception {
		List<PaymentAccountDetail> accounts = paymentDetail.getAccountDetail();
		int retval = 0;

		if ((accounts != null) && !accounts.isEmpty()) {
			for (PaymentAccountDetail pad : accounts) {
				if (extractCodes.contains(pad.getFinObjectCode())) {
					// get a new Payment that is pre-populated with PaymentDetail information
					Payment payment = new Payment();

					payment.setPayeeId(payee.getId());
					payment.setExcludeIndicator(Boolean.FALSE);
					payment.setDisbursementDt(paymentDetail.getPaymentGroup().getDisbursementDate());
					payment.setDisbursementNbr(paymentDetail.getPaymentGroup().getDisbursementNbr());
					payment.setDocNbr(paymentDetail.getCustPaymentDocNbr());
					payment.setDocType(paymentDetail.getFinancialDocumentTypeCode());
					payment.setInvoiceNbr(paymentDetail.getInvoiceNbr());
					payment.setPaymentGroupId(paymentDetail.getPaymentGroup().getId());
					payment.setPoNbr(paymentDetail.getPurchaseOrderNbr());
					payment.setAcctNetAmount(pad.getAccountNetAmount());
					payment.setAccountNbr(pad.getAccountNbr());
					payment.setPaymentAcctDetailId(pad.getId());
					payment.setFinChartCode(pad.getFinChartCode());
					payment.setFinObjectCode(pad.getFinObjectCode());

					String overrideTypeCode = getOverridePaymentType(payee, pmtTypeCodes);

					if (overrideTypeCode != null) {
						payment.setPaymentTypeCode(overrideTypeCode);
					} 
					else if ((pad.getFinObjectCode() != null) && objectCodeMap.containsKey(pad.getFinObjectCode())) {
						payment.setPaymentTypeCode(objectCodeMap.get(pad.getFinObjectCode()));
					} 
					else {
						// Set payment type to type 7 by default
						payment.setPaymentTypeCode(TaxConstants.AMOUNT_CODE_7);
					}

					businessObjectService.save(payment);

					retval++;
				} 
				else {
					LOG.info("no extract object code found for: " + pad.getFinObjectCode());
				}
			}
		} 
		else {
			LOG.warn("no account data for payment detail id: " + paymentDetail.getId());
		}

		return retval;
	}

	private String getOverridePaymentType(Payee p, Map<String, String> pmtTypeCodes) {
		return pmtTypeCodes.get(p.getHeaderOwnershipCode() + "|" + p.getHeaderOwnershipCategoryCode());
	}

	private int deleteTaxYearPayments(Timestamp startDt, Timestamp endDt, Integer taxYear) {
		int deleted = 0;

		LOG.info("begin deleteTaxYearPayments() - " + new Date());
		List<Payment> payments = taxReporting1099Dao.getAllExistingPayments(startDt, endDt, taxYear);

		if ((payments != null) && !payments.isEmpty()) {
			for (Payment payment : payments) {
				businessObjectService.delete(payment);

				if (LOG.isDebugEnabled()) {
					LOG.debug("deleting payee payment - payee=" + payment.getPayeeId() + ", paymentGroup=" + payment.getPaymentGroupId() + ", amount=" + payment.getAcctNetAmount());
				}

				deleted++;
			}
		}

		LOG.info("deleted " + deleted + " 1099 payments : startDate=" + DF.format(startDt) + " - endDate=" + DF.format(endDt));
		LOG.info("end deleteTaxYearPayments() - " + new Date());

		return deleted;
	}

	private Payee getPayeeByVendorNum(String vendorNumber, Integer taxYear) {
		Payee retval = null;

		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(VendorPropertyConstants.VENDOR_NUMBER, vendorNumber);
		criteria.put(TaxPropertyConstants.TAX_YEAR, taxYear);
		List<Payee> payees = (List<Payee>) businessObjectService.findMatching(Payee.class, criteria);

		if ((payees != null) && !payees.isEmpty()) {
			retval = payees.get(0);
		}

		return retval;
	}

	private Payee getPayeeByVendorNum(VendorDetail vendor, Integer taxYear) {
		Payee retval = getPayeeByVendorNum(vendor.getVendorNumber(), taxYear);

		// if we did not find payee then create a new one
		if (retval == null) {
			retval = createNewPayee(taxYear, vendor);
		}

		return retval;
	}

	private List<VendorDetail> getVendors(String ownershipCode, boolean isAllow) {
		List<VendorDetail> retval = null;
		String vendorOwnershipCode = null;
		String vendorOwnershipCategoryCode = null;

		if (!ownershipCode.contains("=")) {
			vendorOwnershipCode = ownershipCode;
		} 
		else {
			int pos = ownershipCode.indexOf('=');
			vendorOwnershipCode = ownershipCode.substring(0, pos);
			vendorOwnershipCategoryCode = ownershipCode.substring(pos + 1);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("vendor ownership code: " + vendorOwnershipCode);
			LOG.debug("vendor ownership category code: " + vendorOwnershipCategoryCode);
		}

		retval = taxReporting1099Dao.getVendors(vendorOwnershipCode, vendorOwnershipCategoryCode, isAllow);

		if (LOG.isInfoEnabled()) {
			int cnt = 0;
			if ((retval != null) && !retval.isEmpty()) {
				cnt = retval.size();
			}

			LOG.info("found " + cnt + " vendors with ownership code=" + vendorOwnershipCode);
		}

		return retval;
	}

	private PdfReader getPdfReader(Integer year) {
		PdfReader retval = null;
		FileInputStream fispdf = null;
		try {
			fispdf = new FileInputStream(get1099PdfFile(year));
			retval = new PdfReader(fispdf);
		} 
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		finally {
			try {
				fispdf.close();
			} 
			catch (Exception ex) {
				LOG.error("Error closing FileInputStream in TaxReporting1099ServiceImple#getPdfReader().\n" + ex.getMessage());
			}

		}

		return retval;
	}

	private File get1099PdfFile(Integer year) {
		File retval = null;

		String fname = pdfDirectory + "/" + TaxPropertyConstants.PDF_1099_FILENAME_PREFIX + "-" + year + ".pdf";

		LOG.info("pdfDirectory: " + pdfDirectory);

		retval = new File(fname);

		if (!retval.exists() || !retval.isFile() || retval.length() <= 0) {
			throw new RuntimeException("The IRS 1099 Misc PDF [" + retval.getAbsolutePath() + "] is not available in the file system.");
		}

		LOG.info("1099 PDF filename = " + retval.getAbsolutePath());

		return retval;
	}

	@Override
	public List<Payee> loadPayees(Integer year) {
		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(TaxPropertyConstants.TAX_YEAR, year);
		criteria.put(TaxPropertyConstants.EXCLUDE_INDICATOR, Boolean.FALSE);
		return loadPayee1099Information(year, (List<Payee>) businessObjectService.findMatching(Payee.class, criteria));
	}

	private Payee loadPayee(Integer year, Payee payee) {
		Payee retval = null;

		// lets get all the child payees for this parent payee
		Map<String, Object> criteria = new HashMap<String, Object>();

		criteria.put(TaxPropertyConstants.TAX_YEAR, year);
		criteria.put(TaxPropertyConstants.EXCLUDE_INDICATOR, Boolean.FALSE);
		criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, payee.getVendorHeaderGeneratedIdentifier());
		List<Payee> payees = (List<Payee>) businessObjectService.findMatching(Payee.class, criteria);

		List<Payee> payeeInfo = loadPayee1099Information(year, payees);

		if (!payeeInfo.isEmpty()) {
			if (LOG.isDebugEnabled()) {
				for (Payee p : payeeInfo) {
					LOG.debug("payee: " + p.getVendorNumber());
				}
			}
			retval = payeeInfo.get(0);
		}

		if (LOG.isDebugEnabled()) {
			if (retval != null) {
				LOG.debug("retval payee: " + retval.getVendorNumber());
			} 
			else {
				LOG.debug("retval payee: is null");
			}
		}

		return retval;
	}

	private List<Payee> loadPayee1099Information(Integer year, List<Payee> payees) {
		List<Payee> retval = new ArrayList<Payee>();

		if ((payees == null) || payees.isEmpty()) {
			LOG.warn("No payee candidates found");
		} 
		else {
			if (LOG.isDebugEnabled()) {
				LOG.debug("found " + payees.size() + " payee candidates");
			}

			getTaxAmountByPaymentType(true);
			getTaxThreshold(true);

			Map<Integer, List<Payee>> payeeMap = new HashMap<Integer, List<Payee>>();

			// Create a map of payee lists keyed by vendorHeaderGeneratedIdentifier. Each list will be related child/parent payees
			for (Payee p : payees) {
				List<Payee> plist = payeeMap.get(p.getVendorHeaderGeneratedIdentifier());

				if (plist == null) {
					payeeMap.put(p.getVendorHeaderGeneratedIdentifier(), plist = new ArrayList<Payee>());
				}

				plist.add(p);
			}

			// loop over the parent/child payee lists, find the parent payee and copy all child payee payments to the parent
			for (List<Payee> plist : payeeMap.values()) {
				if (plist.isEmpty()) {
					LOG.warn("empty payee list found");
				} 
				else {
					// find the Payee record for the vendor parent. If parent has no Payee record create one
					Payee parentPayee = getParentPayee(year, plist);

					if (parentPayee != null) {
						// copy all child payee payments to the parent
						KualiDecimal saveOriginalParentTax = null;

						if (LOG.isDebugEnabled()) {
							// save this for debug information
							saveOriginalParentTax = parentPayee.getTaxAmount(year);
							LOG.debug("original parent tax: " + saveOriginalParentTax);
						}

						summarizePayeeTax(parentPayee, plist);

						// check to see if this payee meets tax threshold for a  1099
						if (isAboveTaxThreshold(parentPayee, year)) {
							retval.add(parentPayee);

							if (LOG.isDebugEnabled()) {
								LOG.debug("payee " + parentPayee.getVendorNumber() + " meets tax threshhold");
								if (!plist.isEmpty()) {
									LOG.debug("*** tax totals ***");
									for (Payee p : plist) {
										LOG.debug("child payee: " + p.getVendorNumber() + ", tax=" + p.getTaxAmount(year));
									}
									LOG.debug("parent payee: " + parentPayee.getVendorNumber() + ", original tax=" + saveOriginalParentTax + ", summarized tax=" + parentPayee.getTaxAmount(year));
								}
							}
						}
					}
				}
			}
		}

		return retval;
	}

	private Payee createParentPayeeFromChild(Integer year, Payee childPayee) {
		Payee retval = new Payee();

		Map<String, Object> criteria = new HashMap<String, Object>();
		criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, childPayee.getVendorHeaderGeneratedIdentifier());
		criteria.put(VendorPropertyConstants.VENDOR_PARENT_INDICATOR, true);

		VendorDetail vendor = (VendorDetail) businessObjectService.findByPrimaryKey(VendorDetail.class, criteria);

		if (vendor != null) {
			VendorAddress defaultAddress = getVendorDefaultAddress(vendor);

			if (defaultAddress != null) {
				setPayeeAddress(defaultAddress, retval);
			} 
			else {
				retval.setAddressTypeCode(TaxPropertyConstants.TAX_ADDRESS_TYPE_CD);
				LOG.info("No address found for " + vendor.getVendorName());
			}

			setPayeeAttributes(year, vendor, retval);

			// Save parent payee
			businessObjectService.save(retval);

			if (LOG.isDebugEnabled()) {
				LOG.debug("created parent payee " + retval.getVendorNumber());
			}
		} 
		else {
			LOG.error("failed to load parent vendor for child payee " + childPayee.getVendorNumber());
		}

		return retval;

	}

	private VendorAddress findAddressByType(List<VendorAddress> addresses, String type) {
		VendorAddress retval = null;

		List<VendorAddress> validAddresses = new ArrayList<VendorAddress>();

		// load all the addresses for this type into a list
		for (VendorAddress address : addresses) {
			if (type.equals(address.getVendorAddressTypeCode())) {
				validAddresses.add(address);
			}
		}

		if (!validAddresses.isEmpty()) {
			// see if we have a default - if so use it
			for (VendorAddress address : validAddresses) {
				if (address.isVendorDefaultAddressIndicator()) {
					retval = address;
					break;
				}
			}

			// if no default take the first one
			if (retval == null) {
				retval = validAddresses.get(0);
			}
		}

		return retval;
	}

	private VendorAddress getVendorDefaultAddress(VendorDetail vendor) {
		VendorAddress retval = null;
		// make sure we have some addresses
		if ((vendor.getVendorAddresses() != null) && !vendor.getVendorAddresses().isEmpty()) {

			// look for tax first
			retval = findAddressByType(vendor.getVendorAddresses(), TaxPropertyConstants.TAX_ADDRESS_TYPE_CD);

			// then look for remit
			if (retval == null) {
				retval = findAddressByType(vendor.getVendorAddresses(), VendorConstants.AddressTypes.REMIT);
			} 
			else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("found TX address for vendor " + vendor.getVendorNumber());
				}
			}

			// then po
			if (retval == null) {
				retval = findAddressByType(vendor.getVendorAddresses(), VendorConstants.AddressTypes.PURCHASE_ORDER);
			}

			// if we cannot find one of the desired addresses take the first
			if (retval == null) {
				retval = vendor.getVendorAddresses().get(0);
			}
		}

		return retval;
	}

	private void summarizePayeeTax(Payee parentPayee, List<Payee> plist) {
		// remove parentPayee from list if required so we only have a list of child payees
		Iterator<Payee> it = plist.iterator();
		while (it.hasNext()) {
			Payee p = it.next();
			if ((parentPayee == p) || parentPayee.getVendorNumber().equals(p.getVendorNumber())) {
				it.remove();
				break;
			}
		}

		if (LOG.isDebugEnabled()) {
			if (plist.size() > 0) {
				LOG.debug("found " + plist.size() + " child payees for parent payee(" + parentPayee.getVendorHeaderGeneratedIdentifier() + ")");
			}
		}

		// check to make sure we have a valid list
		if (parentPayee.getPayments() == null) {
			parentPayee.setPayments(new ArrayList<Payment>());
		}

		// add all the payments from the child records to the parentPayee record
		for (Payee p : plist) {
			if ((p.getPayments() != null) && !p.getPayments().isEmpty()) {
				parentPayee.getPayments().addAll(p.getPayments());
			}
		}
	}

	private Payee getParentPayee(Integer year, List<Payee> plist) {
		Payee retval = null;

		if ((plist != null) && !plist.isEmpty()) {
			// try to find the parent Payee(vendor) in the list
			for (Payee p : plist) {
				if (p.getVendorDetail().isVendorParentIndicator()) {
					retval = p;
					break;
				}
			}

			// if we did not find the parent we will attempt to load from the database
			if (retval == null) {
				// get the first payee and use this vendor identifier
				Payee p = plist.get(0);

				if (LOG.isDebugEnabled()) {
					LOG.debug("did not find parent payee (" + p.getVendorHeaderGeneratedIdentifier() + ") in list so will attempt to load from db");
				}

				Map<String, Object> criteria = new HashMap<String, Object>();
				criteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, p.getVendorDetail().getVendorHeaderGeneratedIdentifier());
				criteria.put(VendorPropertyConstants.VENDOR_DETAIL + "." + VendorPropertyConstants.VENDOR_PARENT_INDICATOR, true);
				criteria.put(TaxPropertyConstants.TAX_YEAR, year);

				Collection<Payee> payees = businessObjectService.findMatching(Payee.class, criteria);
				// if we found some payees take the first one (should only be one)
				if ((payees != null) && !payees.isEmpty()) {
					retval = payees.iterator().next();
				}

				// if we did not find the parent payee create one
				if (retval == null) {
					retval = createParentPayeeFromChild(year, p);
				}

				if (retval == null) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("failed to load parent payee for (" + p.getVendorNumber() + ") 1099 will not be created");
					}
				}
			}
		}

		return retval;
	}

	private boolean isAboveTaxThreshold(Payee payee, Integer year) {
		boolean retval = false;

		for (String box : this.form1099BoxInformation.keySet()) {
			KualiDecimal amount = getPayeeTaxAmount(payee, box, year);

			if (amount.isPositive()) {
				retval = true;
				break;
			}
		}

		return retval;
	}

	@Override
	public Payee getPayee(Integer id) {
		Payee retval = null;
		Map<String, Object> fieldValues = new HashMap<String, Object>();
		fieldValues.put(KFSPropertyConstants.ID, id);

		List<Payee> payees = (List<Payee>) businessObjectService.findMatching(Payee.class, fieldValues);

		if ((payees != null) && !payees.isEmpty()) {
			retval = payees.get(0);
		}

		return retval;
	}

	@Override
	public Payer getDefaultPayer() {
		Payer retval = null;

		Map<String, Object> fieldValues = new HashMap<String, Object>();
		fieldValues.put(TaxPropertyConstants.TRANS_CD, parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, TaxPropertyConstants.PAYER_TRANSCD));

		List<Payer> payers = (List<Payer>) businessObjectService.findMatching(Payer.class, fieldValues);

		if ((payers != null) && !payers.isEmpty()) {
			retval = payers.get(0);
		}

		return retval;
	}

	private void createPdfFile(PdfReader reader, int pageNum, OutputStream os, Payer payer, List<Payee> payees) throws Exception {
		Document document = null;
		PdfCopy copy = null;
		try {
			document = new Document(reader.getPageSizeWithRotation(1));
			copy = new PdfCopy(document, os);
			document.open();

			for (Payee payee : payees) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				getPdfFilePage(reader, pageNum, bos, payer, payee);
				PdfReader r = new PdfReader(bos.toByteArray());
				copy.addPage(copy.getImportedPage(r, 1));
				copy.freeReader(r);
			}

			document.close();
		} catch (Exception ex) {
			LOG.error("Error in TaxReporting1099ServiceImpl#createPdfFile.\n" + ex.getMessage());
		}

		finally {
			try {
				copy.close();
			} 
			catch (Exception ex) {
				LOG.error("Error in TaxReporting1099ServiceImpl#createPdfFile.\n" + ex.getMessage());
			}

			try {
				document.close();
			} 
			catch (Exception ex) {
				LOG.error("Error in TaxReporting1099ServiceImpl#createPdfFile.\n" + ex.getMessage());
			}

		}
	}

	public void getPdfFilePage(PdfReader reader, int pageNum, OutputStream os,
			Payer payer, Payee payee) throws Exception {

		if (LOG.isDebugEnabled()) {
			int sz = 0;
			if (payee.getPayments() != null) {
				sz = payee.getPayments().size();
			}
			LOG.debug("creating pdf for payee " + payee.getVendorNumber() + ", #payments=" + sz);
		}

		PdfStamper stamper = null;
		Document document = null;
		PdfCopy copy = null;
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			document = new Document(reader.getPageSizeWithRotation(1));

			copy = new PdfCopy(document, bos);

			document.open();

			PdfImportedPage page = copy.getImportedPage(reader, pageNum);
			copy.addPage(page);
			document.close();

			PdfReader nr = new PdfReader(bos.toByteArray());
			stamper = new PdfStamper(nr, os);

			// Always page 1
			addPageText(stamper, 1, payer, payee);

			stamper.close();
		} catch (Exception ex) {
			LOG.error("Error in TaxReporting1099ServiceImpl#getPdfFilePage.\n" + ex.getMessage());
		}

		finally {
			try {
				copy.close();
			} 
			catch (Exception ex) {
				LOG.error("Error in TaxReporting1099ServiceImpl#getPdfFilePage.\n" + ex.getMessage());
			}

			try {
				document.close();
			} 
			catch (Exception ex) {
				LOG.error("Error in TaxReporting1099ServiceImpl#getPdfFilePage.\n" + ex.getMessage());
			}

			try {
				stamper.close();
			} 
			catch (Exception ex) {
				LOG.error("Error in TaxReporting1099ServiceImpl#getPdfFilePage.\n" + ex.getMessage());
			}

		}
	}

	private void populate1099TaxBoxes(PdfContentByte pcb, BaseFont font, int taxYear, Payee payee) {
		for (String box : form1099BoxInformation.keySet()) {
			String param = this.form1099BoxInformation.get(box);

			if (param != null) {
				KualiDecimal taxAmount = getPayeeTaxAmount(payee, box, taxYear);
				addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, taxAmount.toString(), getLocationX(param), getLocationY(param));
			}
		}
	}

	private void addPageText(PdfStamper stamper, int pageNum, Payer payer, Payee payee) throws Exception {
		BaseFont font = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);

		int x = getLocationX("PAYER");
		int y = getLocationY("PAYER");

		PdfContentByte pcb = stamper.getOverContent(pageNum);
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payer.getName1() + " " + (payer.getName2() == null ? "" : payer.getName2()), x, y);
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payer.getAddress(), x, y - 12);
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payer.getCity() + ", " + payer.getState() + " " + payer.getZipCode(), x, y - 24);
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payer.getPhoneNumber(), x, y - 36);

		// Payer TIN
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payer.getTin(), getLocationX("PAYER_TAX_ID"), getLocationY("PAYER_TAX_ID"));
		// Payee TIN
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, (payee.getHeaderTaxNumber() == null ? "" : payee.getHeaderTaxNumber()), getLocationX("PAYEE_TAX_ID"), getLocationY("PAYEE_TAX_ID"));

		Integer taxYear = payee.getTaxYear();

		populate1099TaxBoxes(pcb, font, taxYear, payee);

		// State Tax
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, getStateTaxWitheld(payee), getLocationX("WITHHELD"), getLocationY("WITHHELD"));
		// State No
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payer.getState(), getLocationX("STATE_NO"), getLocationY("STATE_NO"));
		// State Inc
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, getStateIncome(payee), getLocationX("STATE_INCOME"), getLocationY("STATE_INCOME"));

		// Payee Name and Address
		x = getLocationX("PAYEE_ADDRESS");
		y = getLocationY("PAYEE_ADDRESS");

		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getVendorName(), getLocationX("PAYEE_NAME"), getLocationY("PAYEE_NAME"));

		if (Boolean.TRUE.equals(payee.getVendorForeignIndicator()) || KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(payee.getAddressCountryCode())) {
			String payeeForeignAddressLine = buildPayeeForeignAddressLine(payee);

			if (payeeForeignAddressLine.length() > TaxPropertyConstants.VENDOR_MAX_ADDRESS_LENGTH) {
				payeeForeignAddressLine = payeeForeignAddressLine.substring(0, TaxPropertyConstants.VENDOR_MAX_ADDRESS_LENGTH).trim();
			}

			addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payeeForeignAddressLine, x, y);
		} 
		else {
			if (StringUtils.isNotBlank(payee.getAddressLine1Address()) && payee.getAddressLine1Address().startsWith(TaxConstants.DBA_BUSINESS_PREFIX)) {
				addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine1Address(), getLocationX("PAYEE_NAME"), getLocationY("PAYEE_NAME") - 12);

				if (StringUtils.isNotBlank(payee.getAddressLine2Address()) && payee.getAddressLine2Address().contains(",")) {
					addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address().substring(0, payee.getAddressLine2Address().indexOf(",")), x, y);
					addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address().substring(payee.getAddressLine2Address().indexOf(",") + 1).trim(), x, y - 12);
				} 
				else {
					addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address(), x, y);
				}
			} 
			else {
				addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine1Address(), x, y);
				addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressLine2Address(), x, y - 12);
			}
			// Payee State, City, Zip
			addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getAddressCityName() + ", " + handleNull(payee.getAddressStateCode()) + " " + getFormattedZip(payee.getAddressZipCode()), getLocationX("PAYEE_STATE_CITY_ZIP"), getLocationY("PAYEE_STATE_CITY_ZIP"));
		}

		// Account #
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, payee.getVendorNumber(), getLocationX("ACCOUNT_NUM"), getLocationY("ACCOUNT_NUM"));

		// Sec 409a Deferrals
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, getPayeeTaxAmount(payee, TaxConstants.AMOUNT_CODE_D, taxYear).toString(), getLocationX("DEFERRALS"), getLocationY("DEFERRALS"));
		// Sec 409a Income
		addText(pcb, font, TaxPropertyConstants.FORM_1099_DEFAULT_FONT_SIZE, getPayeeTaxAmount(payee, TaxConstants.AMOUNT_CODE_E, taxYear).toString(), getLocationX("INCOME"), getLocationY("INCOME"));

		if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxPropertyConstants.PDF_1099_PAGE_COPY_A) {
			addCorrectionIndicator(pcb, getLocationX("CORRECTION_IND"), getLocationY("CORRECTION_IND"));
		} 
		else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxPropertyConstants.PDF_1099_PAGE_COPY_B) {
			addCorrectionIndicator(pcb, getLocationX("CORRECTION_IND"), getLocationY("CORRECTION_IND"));
		} 
		else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxPropertyConstants.PDF_1099_PAGE_COPY_C) {
			addCorrectionIndicator(pcb, getLocationX("CORRECTION_IND"), getLocationY("CORRECTION_IND"));
		} 
		else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxPropertyConstants.PDF_1099_PAGE_COPY_1) {
			addCorrectionIndicator(pcb, getLocationX("CORRECTION_IND"), getLocationY("CORRECTION_IND"));
		} 
		else if (payee.getCorrectionIndicator().booleanValue() && pageNum == TaxPropertyConstants.PDF_1099_PAGE_COPY_2) {
			addCorrectionIndicator(pcb, getLocationX("CORRECTION_IND"), getLocationY("CORRECTION_IND"));
		}
	}

	private String handleNull(String input) {
		String retval = input;
		if (retval == null) {
			retval = "";
		}

		return retval;
	}

	private String getFormattedZip(String input) {
		String s = handleNull(input);
		StringBuilder retval = new StringBuilder(9);

		if (s.length() == 9) {
			retval.append(s.substring(0, 5));
			retval.append("-");
			retval.append(s.substring(5, 9));
		} 
		else {
			retval.append(s);
		}

		return retval.toString();
	}

	private String buildDocumentKey(PaymentDetail paymentDetail) {
		StringBuilder retval = new StringBuilder(64);

		retval.append(paymentDetail.getCustPaymentDocNbr());
		retval.append(".");
		retval.append(paymentDetail.getPaymentGroupId());

		return retval.toString();
	}

	private String buildPayeeForeignAddressLine(Payee payee) {
		StringBuilder retval = new StringBuilder(256);

		if (StringUtils.isNotBlank(payee.getAddressLine1Address())) {
			retval.append(payee.getAddressLine1Address().trim());
			retval.append(" ");
		}

		if (StringUtils.isNotBlank(payee.getAddressLine2Address())) {
			retval.append(payee.getAddressLine2Address().trim());
			retval.append(" ");
		}

		if (StringUtils.isNotBlank(payee.getAddressCityName())) {
			retval.append(payee.getAddressCityName().trim());
			retval.append(" ");
		}

		if (StringUtils.isNotBlank(payee.getAddressStateCode())) {
			retval.append(payee.getAddressStateCode().trim());
			retval.append(" ");
		}

		if (StringUtils.isNotBlank(payee.getAddressZipCode())) {
			retval.append(getFormattedZip(payee.getAddressZipCode()));
			retval.append(" ");
		}

		if (StringUtils.isNotBlank(payee.getAddressCountryCode())) {
			retval.append(payee.getAddressCountryCode().trim());
		}

		return retval.toString().trim();
	}

	private KualiDecimal getPayeeTaxAmountByTypeCode(Payee payee, String typeCode, Integer taxYear) {
		KualiDecimal retval = KualiDecimal.ZERO;

		if ((payee.getPayments() != null) && !payee.getPayments().isEmpty()) {
			if (StringUtils.isNotBlank(typeCode)) {
				for (Payment payment : (List<Payment>) payee.getPayments()) {
					if (!payment.getExcludeIndicator() && typeCode.equals(payment.getPaymentTypeCode()) && taxYear.equals(payment.getPayee().getTaxYear())) {
						retval = retval.add(payment.getAcctNetAmount());
					}
				}
			}
		}

		return retval;
	}

	public KualiDecimal getPayeeTaxAmount(Payee payee, String typeCode, Integer taxYear) {
		KualiDecimal retval = KualiDecimal.ZERO;

		KualiDecimal amount = getPayeeTaxAmountByTypeCode(payee, typeCode, taxYear);

		// if this type threshold is setup in parameters
		if (getTaxAmountByPaymentType(false).containsKey(typeCode)) {
			if (amount.isGreaterEqual(getTaxAmountByPaymentType(false).get(typeCode))) {
				retval = amount;
			}
		} 
		else if (amount.doubleValue() >= getTaxThreshold(false)) {
			retval = amount;
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("payee: " + payee.getVendorNumber() + ", typeCode: " + typeCode + ", taxAmount: " + retval.toString() + ", amountFound: " + amount);
		}

		return retval;
	}

	private <T> List<DocumentIncomeType<T>> get1099DocumentIncomeTypes(PaymentDetail paymentDetail) {
		return get1099DocumentIncomeTypes(paymentDetail.getFinancialDocumentTypeCode(), paymentDetail.getCustPaymentDocNbr());
	}

	private <T> List<DocumentIncomeType<T>> get1099DocumentIncomeTypes(String financialDocumentTypeCode, String docNum) {
		List<DocumentIncomeType<T>> retval = new ArrayList<DocumentIncomeType<T>>();
		Class clazz = null;
		String fieldName = null;
		Object documentIdentifier = null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("document " + financialDocumentTypeCode + " [" + docNum + "]");
		}

		// need a valid document type
		if (StringUtils.isNotBlank(financialDocumentTypeCode)) {
			if (TaxHelper.isPreq(financialDocumentTypeCode)) {
				clazz = PaymentRequestIncomeType.class;
				fieldName = PurapPropertyConstants.PURAP_DOC_ID;
				documentIdentifier = taxReporting1099Dao.getPurapIdentifierFromDocumentNumber(PaymentRequestDocument.class, docNum);
			} 
			else if (TaxHelper.isCm(financialDocumentTypeCode)) {
				clazz = CreditMemoIncomeType.class;
				fieldName = PurapPropertyConstants.PURAP_DOC_ID;
				documentIdentifier = taxReporting1099Dao.getPurapIdentifierFromDocumentNumber(VendorCreditMemoDocument.class, docNum);
			} 
			else if (TaxHelper.isDv(financialDocumentTypeCode)) {
				clazz = DisbursementVoucherIncomeType.class;
				fieldName = KFSPropertyConstants.DOCUMENT_NUMBER;
				documentIdentifier = docNum;
			}

			// if we have the criteria we need - try to load the DocumentIncomeType list
			if ((clazz != null) && (fieldName != null) && (documentIdentifier != null)) {
				Map<String, Object> criteria = new HashMap<String, Object>();
				criteria.put(fieldName, documentIdentifier);

				// find income types associated with this document
				Collection results = businessObjectService.findMatching(clazz, criteria);

				// if we find income types we will use these for the 1099 forms
				if ((results != null) && !results.isEmpty()) {
					retval.addAll(results);

					if (LOG.isDebugEnabled()) {
						LOG.debug("found " + results.size() + " incomes type(s) for document " + financialDocumentTypeCode + " [" + docNum + "]");
					}
				}
			}
		}

		return retval;
	}

	private String getStateIncome(Payee payee) {
		return "";
	}

	private String getStateTaxWitheld(Payee payee) {
		return "";
	}

	private int getLocationX(String parm) {
		int retval = 0;
		String value = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, parm);

		if (StringUtils.isNotBlank(value)) {
			String[] s = value.split(";");

			if ((s == null) || (s.length < 1)) {
				throw new RuntimeException(parm + " is not a valid location");
			} 
			else {
				retval = Integer.parseInt(s[0]);
			}
		} 
		else {
			throw new RuntimeException(parm + " is not a valid location");
		}

		return retval;
	}

	private int getLocationY(String parm) {
		int retval = 0;
		String value = parameterService.getParameterValueAsString(TaxConstants.NMSPC_CD, TaxConstants.PAYEE_MASTER_EXTRACT_STEP, parm);

		if (StringUtils.isNotBlank(value)) {
			String[] s = value.split(";");

			if ((s == null) || (s.length < 2)) {
				throw new RuntimeException(parm + " is not a valid location");
			} 
			else {
				retval = Integer.parseInt(s[1]);
			}
		} 
		else {
			throw new RuntimeException(parm + " is not a valid location");
		}

		return retval;
	}

	private void addCorrectionIndicator(PdfContentByte pcb, int x, int y) {
		pcb.moveTo(x, y);
		pcb.lineTo(x + TaxPropertyConstants.FORM_1099_CORRECTION_INDICATOR_LENGTH, y + TaxPropertyConstants.FORM_1099_CORRECTION_INDICATOR_LENGTH);
		pcb.stroke();

		pcb.moveTo(x, y + TaxPropertyConstants.FORM_1099_CORRECTION_INDICATOR_LENGTH);
		pcb.lineTo(x + TaxPropertyConstants.FORM_1099_CORRECTION_INDICATOR_LENGTH, y);
		pcb.stroke();
	}

	private void addText(PdfContentByte pcb, BaseFont bf, int fontSize, String text, int x, int y) {
		pcb.beginText();
		pcb.setFontAndSize(bf, fontSize);
		pcb.setTextMatrix(x, y);

		if (text == null) {
			text = "";
		}

		pcb.showText(text);
		pcb.endText();
	}

	public void setDocumentIncomeTypeSet(Set<String> documentIncomeTypeSet) {
		if (LOG.isDebugEnabled()) {
			if ((documentIncomeTypeSet != null) && !documentIncomeTypeSet.isEmpty()) {
				for (String s : documentIncomeTypeSet) {
					LOG.debug("documentType=" + s);
				}
			} 
			else {
				LOG.debug("no documentIncomeTypeSet");
			}
		}

		this.documentIncomeTypeSet = documentIncomeTypeSet;
	}

	@Override
	public List<Payee> searchPayees(PayeeSearchForm form) {
		List<Payee> payees = taxReporting1099Dao.getPayees(form);
		Integer year = form.getTaxYear();
		// remove payees that do not meet tax threshhold
		List<Payee> retval = removePayeesBelowThreshold(payees, year);

		// sort by vendor number
		Collections.sort(retval, new Comparator<Payee>() {
			@Override
			public int compare(Payee p1, Payee p2) {
				return p1.getVendorNumber().compareTo(p2.getVendorNumber());
			}
		});

		return retval;
	}

	private List<Payee> removePayeesBelowThreshold(List<Payee> payees, Integer year) {
		List<Payee> retval = new ArrayList<Payee>();
		Map<Integer, Map<String, Double>> totals = new HashMap<Integer, Map<String, Double>>();
		Map<String, KualiDecimal> taxAmountByPaymentType = getTaxAmountByPaymentType(true);
		Map<String, Double> payeeTotalsMap;

		// loop over payees and rollup amount to parent
		for (Payee p : payees) {
			payeeTotalsMap = totals.get(p.getVendorHeaderGeneratedIdentifier());

			if (payeeTotalsMap == null) {
				totals.put(p.getVendorHeaderGeneratedIdentifier(), payeeTotalsMap = new HashMap<String, Double>());
			}

			// sum up amounts by different types
			for (Payment payment : (List<Payment>) p.getPayments()) {
				if (!payment.getExcludeIndicator() && year.equals(p.getTaxYear())) {
					if (payment.getAcctNetAmount().isNonZero()) {
						Double taxAmount = payeeTotalsMap.get(payment.getPaymentTypeCode());
						if (taxAmount == null) {
							payeeTotalsMap.put(payment.getPaymentTypeCode(), taxAmount = Double.valueOf(0));
						}

						taxAmount = Double.valueOf(taxAmount.doubleValue() + payment.getAcctNetAmount().doubleValue());
						payeeTotalsMap.put(payment.getPaymentTypeCode(), taxAmount);
						totals.put(p.getVendorHeaderGeneratedIdentifier(), payeeTotalsMap);
					}
				}
			}
		}

		double taxThreshhold = getTaxThreshold(true);

		// loop over payees and add any that meet minimum required amount based on type
		for (Payee p : payees) {
			payeeTotalsMap = totals.get(p.getVendorHeaderGeneratedIdentifier());

			if (payeeTotalsMap != null) {
				for (String paymentType : payeeTotalsMap.keySet()) {
					Double amount = payeeTotalsMap.get(paymentType);

					KualiDecimal d = taxAmountByPaymentType.get(paymentType);
					// if this limit is setup in parameters
					if ((d != null) && (amount.doubleValue() >= d.doubleValue())) {
						retval.add(p);
						break;
					} 
					else if (amount.doubleValue() >= taxThreshhold) { // default limit
						retval.add(p);
						break;
					}
				}
			}
		}
		return retval;
	}

	private double getTaxThreshold(boolean forceReload) {
		if (forceReload || (taxThreshold == 0)) {
			taxThreshold = TaxHelper.getIncomeThreshold(parameterService);
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("taxThreshhold: " + taxThreshold);
		}

		return taxThreshold;
	}

	private Map<String, KualiDecimal> getTaxAmountByPaymentType(boolean forceReload) {
		if (forceReload || (taxAmountByPaymentType == null)) {
			taxAmountByPaymentType = TaxHelper.getTaxAmountByPaymentType(parameterService);
		}

		if (LOG.isDebugEnabled()) {
			if (taxAmountByPaymentType != null) {
				for (String s : taxAmountByPaymentType.keySet()) {
					LOG.debug(s + "=" + taxAmountByPaymentType.get(s));
				}
			}
		}

		return taxAmountByPaymentType;
	}

	public List<String> getObjectCodes(List<String> levels, String type) {
		List<String> retval = taxReporting1099Dao.getObjectCodes(levels, type);

		return retval;
	}

	@Override
	public byte[] getPayee1099Form(Integer id, String year) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		if (LOG.isDebugEnabled()) {
			LOG.debug("attempting to load 1099 form for payee= " + id + ", year=" + year);
		}

		// make sure we have a default payer
		Payer payer = getDefaultPayer();
		if (payer == null) {
			throw new RuntimeException("Default payer for 1099 process not found. Check 1099 payer system property configuration.");
		}

		Integer taxYear = Integer.valueOf(TaxHelper.getTaxYear(parameterService));

		if ((year != null) && StringUtils.isNumeric(year)) {
			taxYear = Integer.valueOf(year);
		}

		PdfReader reader = getPdfReader(taxYear.intValue());

		getTaxThreshold(true);
		getTaxAmountByPaymentType(true);

		// write out populated pdf 1099 forms
		try {
			List<Payee> payees = new ArrayList<Payee>();
			Payee p = loadPayee(taxYear, getPayee(id));

			if (p != null) {
				payees.add(p);
				createPdfFile(reader, TaxPropertyConstants.PDF_1099_PAGE_COPY_A, bos, payer, payees);
			} 
			else {
				throw new RuntimeException("cannot find Payee: " + id);
			}
		} 
		catch (Exception ex) {
			throw new RuntimeException("error creating 1099 form for payee: " + id, ex);
		} 
		finally {
			try {
				reader.close();
			} catch (Exception ex) {
				LOG.error("Error in TaxReporting1099ServiceImpl#getPayee1099Form.\n" + ex.getMessage());
			}
		}

		return bos.toByteArray();
	}

	@Override
	public void updatePayeeInformation(VendorDetail vendorDetail) {
		if (vendorDetail.isVendorParentIndicator()) {
			// lets get the parent payee for this vendor if it exists
			Integer taxYear = TaxHelper.getTaxYear(parameterService);

			if (LOG.isDebugEnabled()) {
				LOG.debug("vendorNumber: " + vendorDetail.getVendorNumber() + ", taxYear: " + taxYear);
			}

			Payee payee = getPayeeByVendorNum(vendorDetail.getVendorNumber(), taxYear);

			// if we found the parent lets get the vendor detail and update the payee
			if (payee != null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("payee: " + payee.getId());
				}

				String vendorName = null;

				if (vendorDetail.isVendorFirstLastNameIndicator() && StringUtils.isNotBlank(vendorDetail.getVendorLastName()) && StringUtils.isNotBlank(vendorDetail.getVendorFirstName())) {
					vendorName = (vendorDetail.getVendorLastName() + VendorConstants.NAME_DELIM + vendorDetail.getVendorFirstName());
				} 
				else {
					vendorName = vendorDetail.getVendorName();
				}

				if (StringUtils.isNotBlank(vendorName)) {
					if (vendorName.length() > VendorConstants.VENDOR_FIRST_PLUS_LAST_NAME_MAXLENGTH) {
						payee.setVendorName(vendorName.substring(0, VendorConstants.VENDOR_FIRST_PLUS_LAST_NAME_MAXLENGTH));
					} 
					else {
						payee.setVendorName(vendorName);
					}
				}

				payee.setHeaderTaxNumber(vendorDetail.getVendorHeader().getVendorTaxNumber());

				if (LOG.isDebugEnabled()) {
					LOG.debug("vendor name: " + vendorDetail.getVendorName());
				}

				VendorAddress address = getVendorDefaultAddress(vendorDetail);

				if (address != null) {
					setPayeeAddress(address, payee);

					if (LOG.isDebugEnabled()) {
						LOG.debug("city: " + address.getVendorCityName());
						LOG.debug("addressLine1: " + address.getVendorLine1Address());
						LOG.debug("state: " + address.getVendorStateCode());
					}
				}

				businessObjectService.save(payee);
			}
		}
	}

	public Set<String> getActive1099Boxes() {
		return form1099BoxInformation.keySet();
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public void setPaymentDetailSearchDao(
			TaxReporting1099Dao taxReporting1099Dao) {
		this.taxReporting1099Dao = taxReporting1099Dao;
	}

	public void setTaxReporting1099Dao(TaxReporting1099Dao taxReporting1099Dao) {
		this.taxReporting1099Dao = taxReporting1099Dao;
	}

	public void setPdfDirectory(String pdfDirectory) {
		this.pdfDirectory = pdfDirectory;
	}

	public void setForm1099BoxInformation(Map<String, String> form1099BoxInformation) {
		this.form1099BoxInformation = form1099BoxInformation;
	}

	// these will be implemented on the annual batch
	@Override
	public boolean generateBatchPayeeForms() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean generatePayeeForms(String vendorNumber) {
		// TODO Auto-generated method stub
		return false;
	}

}
