package edu.arizona.kfs.module.tax.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.pdp.PdpConstants.PayeeIdTypeCodes;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.fp.businessobject.DisbursementVoucherIncomeType;
import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.module.purap.businessobject.CreditMemoIncomeType;
import edu.arizona.kfs.module.purap.businessobject.PaymentRequestIncomeType;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.module.tax.TaxConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.DocumentPaymentInformation;
import edu.arizona.kfs.module.tax.businessobject.ExtractHistory;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payment;
import edu.arizona.kfs.module.tax.businessobject.PaymentDetailSearch;
import edu.arizona.kfs.module.tax.dataaccess.TaxReportingDao;
import edu.arizona.kfs.module.tax.service.TaxBatchExtractService;
import edu.arizona.kfs.module.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.module.tax.service.TaxPayeeService;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSPropertyConstants;
import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;
import edu.arizona.kfs.sys.service.IncomeTypeHandlerService;

public class TaxBatchExtractServiceImpl implements TaxBatchExtractService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxBatchExtractService.class);

    private BusinessObjectService businessObjectService;
    private TaxParameterHelperService taxParameterHelperService;
    private IncomeTypeHandlerService incomeTypeHandlerService;
    private TaxPayeeService taxPayeeService;
    private TaxReportingDao taxReportingDao;
    private Set<String> documentIncomeTypeSet;

    // Spring Injectors

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public void setTaxParameterHelperService(TaxParameterHelperService taxParameterHelperService) {
        this.taxParameterHelperService = taxParameterHelperService;
    }

    public void setIncomeTypeHandlerService(IncomeTypeHandlerService incomeTypeHandlerService) {
        this.incomeTypeHandlerService = incomeTypeHandlerService;
    }

    public void setTaxPayeeService(TaxPayeeService taxPayeeService) {
        this.taxPayeeService = taxPayeeService;
    }

    public void setTaxReportingDao(TaxReportingDao taxReportingDao) {
        this.taxReportingDao = taxReportingDao;
    }

    public void setDocumentIncomeTypeSet(Set<String> documentIncomeTypeSet) {
        this.documentIncomeTypeSet = documentIncomeTypeSet;
    }

    // Public Service Methods

    @Override
    public boolean extractPayees() {
        LOG.info("Start extractPayees(); " + new Date());

        boolean retval = false; // Whether the next step in the batch job should run

        try {
            // load 1099 properties KFS parameters
            Set<String> vendorOwnershipCodes = taxParameterHelperService.getVendorOwnershipCodes();
            boolean ownershipCodesAllow = taxParameterHelperService.isVendorOwnershipCodesAllow();
            Integer taxYear = taxParameterHelperService.getTaxYear();
            boolean replaceData = taxParameterHelperService.getReplaceData();
            Timestamp taxYearStartDate = taxParameterHelperService.getPaymentStartDate();
            Timestamp taxYearEndDate = taxParameterHelperService.getPaymentEndDate();
            double incomeThreshold = taxParameterHelperService.getIncomeThreshold();

            LOG.info("tax year:  " + taxYear);
            LOG.info("income threshold: " + incomeThreshold);
            LOG.info("payment start date : " + taxYearStartDate);
            LOG.info("payment end date : " + taxYearEndDate);
            LOG.info("replace data: " + replaceData);
            LOG.info("vendor codes : " + vendorOwnershipCodes);
            LOG.info("vendor codes allow: " + ownershipCodesAllow);

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

                    extractVendorPdpPayments(vendor, taxYear, taxYearStartDate, taxYearEndDate, eh, replaceData);

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
        } catch (Exception ex) {
            throw new RuntimeException("1099 Payee Daily Extract Error: ", ex);
        }
        return retval;
    }

    // Internal Service Methods

    private int deleteTaxYearPayments(Timestamp startDt, Timestamp endDt, Integer taxYear) {
        int deleted = 0;
        LOG.info("begin deleteTaxYearPayments() - " + new Date());
        List<Payment> payments = taxReportingDao.getAllExistingPayments(startDt, endDt, taxYear);

        if ((payments != null) && !payments.isEmpty()) {
            for (Payment payment : payments) {
                businessObjectService.delete(payment);

                if (LOG.isDebugEnabled()) {
                    LOG.debug("deleting payee payment - payee=" + payment.getPayeeId() + ", paymentGroup=" + payment.getPaymentGroupId() + ", amount=" + payment.getAcctNetAmount());
                }

                deleted++;
            }
        }
        LOG.info("deleted " + deleted + " 1099 payments : startDate=" + TaxConstants.DATE_FORMAT.format(startDt) + " - endDate=" + TaxConstants.DATE_FORMAT.format(endDt));
        LOG.info("end deleteTaxYearPayments() - " + new Date());
        return deleted;
    }

    private List<VendorDetail> getVendors(String ownershipCode, boolean isAllow) {
        List<VendorDetail> retval = null;
        String vendorOwnershipCode = null;
        String vendorOwnershipCategoryCode = null;

        if (!ownershipCode.contains(KFSConstants.EQUALS)) {
            vendorOwnershipCode = ownershipCode;
        } else {
            int pos = ownershipCode.indexOf(KFSConstants.EQUALS);
            vendorOwnershipCode = ownershipCode.substring(0, pos);
            vendorOwnershipCategoryCode = ownershipCode.substring(pos + 1);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("vendor ownership code: " + vendorOwnershipCode);
            LOG.debug("vendor ownership category code: " + vendorOwnershipCategoryCode);
        }

        retval = taxReportingDao.getVendors(vendorOwnershipCode, vendorOwnershipCategoryCode, isAllow);

        if (LOG.isInfoEnabled()) {
            int cnt = 0;
            if ((retval != null) && !retval.isEmpty()) {
                cnt = retval.size();
            }
            LOG.info("found " + cnt + " vendors with ownership code=" + vendorOwnershipCode);
        }

        return retval;
    }

    private void extractVendorPdpPayments(VendorDetail vendor, Integer taxYear, Timestamp taxYearStartDate, Timestamp taxYearEndDate, ExtractHistory eh, boolean replaceData) {
        Map<String, String> pmtTypeCodes = taxParameterHelperService.getOverridePaymentTypeCodeMap();
        Map<String, String> objectCodeMap = incomeTypeHandlerService.getObjectCodeMap();
        List<String> extractCodes = incomeTypeHandlerService.getExtractCodes();

        // find all the payments for the given time frame for each vendor
        PaymentDetailSearch pds = new PaymentDetailSearch();
        pds.setBeginDisbursementDate(taxYearStartDate);
        pds.setPayeeIdTypeCd(PayeeIdTypeCodes.VENDOR_ID);

        // add 1 day to end date then truncate time and set as a java.sql.Date
        pds.setEndDisbursementDate(new java.sql.Date(DateUtils.addDays(DateUtils.truncate(taxYearEndDate, Calendar.DAY_OF_MONTH), 1).getTime()));

        pds.setPayeeId(vendor.getVendorNumber());

        List<PaymentDetail> paymentDetails = taxReportingDao.getAllPaymentsForSearchCriteria(pds);

        if ((paymentDetails != null) && !paymentDetails.isEmpty()) {
            if (LOG.isInfoEnabled()) {
                LOG.info(vendor.getVendorName() + ": #payments=" + paymentDetails.size());
            }

            // Lookup payee by vendor number and tax year - if we don't find and existing Payee we will create a new one
            Payee payee = getPayeeByVendor(vendor, taxYear);

            // process payment where the DocumentIncomeType tables should be used (PREQ, DV, CM) return any payment in the unprocessedPayments that do
            // not fit the criteria
            List<PaymentDetail> unprocessedPayments = processDocumentIncomeTypePayments(payee.getId(), paymentDetails, replaceData, eh);

            if (LOG.isDebugEnabled()) {
                if (!unprocessedPayments.isEmpty()) {
                    LOG.debug("found " + unprocessedPayments.size() + " unprocessed payment details");
                }
            }

            // processing any remaining payments in the default manner
            for (PaymentDetail paymentDetail : unprocessedPayments) {
                // if we are replacing all payment information or this payment information does not already exist
                boolean hasPayments = hasPayments(payee.getId(), paymentDetail);
                if (replaceData || !hasPayments) {
                    eh.incrementPaymentsExtracted(savePayments(objectCodeMap, extractCodes, pmtTypeCodes, payee, paymentDetail));
                }
            }
        } else if (LOG.isDebugEnabled()) {
            LOG.debug(vendor.getVendorName() + " : No Payments Found");
        }
    }

    private void extractVendorNonPdpPayments(VendorDetail vendor, Integer taxYear, Timestamp taxYearStartDate, Timestamp taxYearEndDate, ExtractHistory eh, boolean replaceData) {
        // find all the wire transfer document numbers for the given time frame for each vendor
        List<String> dvDocs = taxReportingDao.getNonPdpDVDocumentNumbersForVendor(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), taxYearStartDate, taxYearEndDate, !replaceData);

        List<String> preqDocs = taxReportingDao.getNonPdpPreqDocumentNumbersForVendor(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), taxYearStartDate, taxYearEndDate, !replaceData);

        List<String> cmDocs = taxReportingDao.getNonPdpCMDocumentNumbersForVendor(vendor.getVendorHeaderGeneratedIdentifier(), vendor.getVendorDetailAssignedIdentifier(), taxYearStartDate, taxYearEndDate, !replaceData);

        if (LOG.isDebugEnabled()) {
            LOG.debug("dcDocs.size()=" + dvDocs.size() + ", preqDocs.size()=" + preqDocs.size() + ", cmDocs.size()=" + cmDocs.size());
        }

        // Lookup payee by vendor num an tax year - if we don't find an existing payee we will create a new one
        Payee payee = getPayeeByVendor(vendor, taxYear);

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

    private <T> List<PaymentDetail> processDocumentIncomeTypePayments(Integer payeeId, List<PaymentDetail> paymentDetails, boolean replaceData, ExtractHistory eh) {
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
                    boolean hasPayments = hasPayments(payeeId, paymentDetail);
                    if (replaceData || !hasPayments) {
                        List<DocumentIncomeType<T>> incomeTypes = get1099DocumentIncomeTypes(paymentDetail.getFinancialDocumentTypeCode(), paymentDetail.getCustPaymentDocNbr());

                        // if we found income types then create and save payments
                        if ((incomeTypes != null) && !incomeTypes.isEmpty()) {
                            eh.incrementPaymentsExtracted(savePayments(payeeId, paymentDetail, incomeTypes));
                        } else {
                            LOG.info("found no income type entry for document " + paymentDetail.getFinancialDocumentTypeCode() + "[" + paymentDetail.getCustPaymentDocNbr() + "]");
                        }
                    }
                    processedDocsSet.add(key);
                }
            } else {
                retval.add(paymentDetail);
            }
        }
        return retval;
    }

    private int savePayments(Map<String, String> objectCodeMap, List<String> extractCodes, Map<String, String> pmtTypeCodes, Payee payee, PaymentDetail paymentDetail) {
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
                    } else if ((pad.getFinObjectCode() != null) && objectCodeMap.containsKey(pad.getFinObjectCode())) {
                        payment.setPaymentTypeCode(objectCodeMap.get(pad.getFinObjectCode()));
                    } else {
                        // Set payment type to type 7 by default
                        payment.setPaymentTypeCode(KFSConstants.IncomeTypeConstants.IncomeTypeAmountCodes.AMOUNT_CODE_7);
                    }

                    businessObjectService.save(payment);

                    retval++;
                } else {
                    LOG.info("no extract object code found for: " + pad.getFinObjectCode());
                }
            }
        } else {
            LOG.warn("no account data for payment detail id: " + paymentDetail.getId());
        }

        return retval;
    }

    private <T> int savePayments(Integer payeeId, PaymentDetail paymentDetail, List<DocumentIncomeType<T>> incomeTypes) {
        int retval = 0;
        if (LOG.isDebugEnabled()) {
            LOG.debug("payee=" + payeeId + ", #incomeTypes=" + incomeTypes.size());
        }

        DocumentPaymentInformation paymentInfo = null;

        for (DocumentIncomeType<T> incomeType : incomeTypes) {
            boolean reportable = !KFSConstants.IncomeTypeConstants.INCOME_TYPE_NON_REPORTABLE_CODE.equals(incomeType.getIncomeTypeCode());

            if (reportable) {
                // lazy load paymentInfo (date paid, payment method code, invoice number) for this document
                if (paymentInfo == null) {
                    paymentInfo = taxReportingDao.getDocumentPaymentInformation(paymentDetail.getFinancialDocumentTypeCode(), paymentDetail.getCustPaymentDocNbr());
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
                        if (isDv(paymentDetail.getFinancialDocumentTypeCode())) {
                            payment.setDocType(TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT);
                        } else {
                            payment.setDocType(paymentDetail.getFinancialDocumentTypeCode());
                        }
                    } else {
                        if (isPreq(paymentDetail.getFinancialDocumentTypeCode())) {
                            payment.setDocType(PaymentRequestDocument.DOCUMENT_TYPE_NON_CHECK);
                        } else if (isCm(paymentDetail.getFinancialDocumentTypeCode())) {
                            payment.setDocType(VendorCreditMemoDocument.DOCUMENT_TYPE_NON_CHECK);
                        } else if (isDv(paymentDetail.getFinancialDocumentTypeCode())) {
                            payment.setDocType(DisbursementVoucherDocument.DOCUMENT_TYPE_DV_NON_CHECK);
                        }
                    }

                    payment.setDocNbr(paymentDetail.getCustPaymentDocNbr());
                    payment.setInvoiceNbr(paymentInfo.getInvoiceNumber());
                    payment.setPoNbr(paymentDetail.getPurchaseOrderNbr());
                    payment.setFinChartCode(incomeType.getChartOfAccountsCode());
                    payment.setPaymentTypeCode(incomeType.getIncomeType().getIncomeTypeBox());

                    // set as negative for CM
                    if (isCm(paymentDetail.getFinancialDocumentTypeCode())) {
                        payment.setAcctNetAmount(incomeType.getAmount().negated());
                    } else {
                        payment.setAcctNetAmount(incomeType.getAmount());
                    }

                    if (LOG.isDebugEnabled()) {
                        LOG.debug("saving payment for payee=" + payment.getPayeeId() + ", incomeType=" + incomeType.getIncomeType().getIncomeTypeBox() + ", amount=" + incomeType.getAmount());
                    }

                    businessObjectService.save(payment);
                    retval++;
                } else {
                    LOG.warn("unable to find documnet payment information for payee=" + payeeId + ", incomeType=" + incomeType.getIncomeType().getIncomeTypeBox() + ", documentNumber=" + paymentDetail.getCustPaymentDocNbr());
                }
            }
        }

        return retval;
    }

    private String getOverridePaymentType(Payee p, Map<String, String> pmtTypeCodes) {
        return pmtTypeCodes.get(p.getHeaderOwnershipCode() + KFSConstants.PIPE + p.getHeaderOwnershipCategoryCode());
    }

    private <T> List<DocumentIncomeType<T>> get1099DocumentIncomeTypes(String financialDocumentTypeCode, String docNum) {
        List<DocumentIncomeType<T>> retval = new ArrayList<DocumentIncomeType<T>>();
        @SuppressWarnings("rawtypes")
        Class clazz = null;
        String fieldName = null;
        Object documentIdentifier = null;

        if (LOG.isDebugEnabled()) {
            LOG.debug("document " + financialDocumentTypeCode + " [" + docNum + "]");
        }

        // need a valid document type
        if (StringUtils.isNotBlank(financialDocumentTypeCode)) {
            if (isPreq(financialDocumentTypeCode)) {
                clazz = PaymentRequestIncomeType.class;
                fieldName = PurapPropertyConstants.PURAP_DOC_ID;
                documentIdentifier = taxReportingDao.getPurapIdentifierFromDocumentNumber(PaymentRequestDocument.class, docNum);
            } else if (isCm(financialDocumentTypeCode)) {
                clazz = CreditMemoIncomeType.class;
                fieldName = PurapPropertyConstants.PURAP_DOC_ID;
                documentIdentifier = taxReportingDao.getPurapIdentifierFromDocumentNumber(VendorCreditMemoDocument.class, docNum);
            } else if (isDv(financialDocumentTypeCode)) {
                clazz = DisbursementVoucherIncomeType.class;
                fieldName = KFSPropertyConstants.DOCUMENT_NUMBER;
                documentIdentifier = docNum;
            }

            // if we have the criteria we need - try to load the DocumentIncomeType list
            if ((clazz != null) && (fieldName != null) && (documentIdentifier != null)) {
                Map<String, Object> criteria = new HashMap<String, Object>();
                criteria.put(fieldName, documentIdentifier);

                // find income types associated with this document
                @SuppressWarnings("unchecked")
                List<DocumentIncomeType<T>> results = (List<DocumentIncomeType<T>>) businessObjectService.findMatching(clazz, criteria);

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

    private int saveNonPdpDVPayments(Payee payee, String documentNumber) {
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
                    payment.setDocType(TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT_NON_CHECK);
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

    private int saveNonPdpPreqPayments(Payee payee, String documentNumber) {
        int retval = 0;

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);

        List<PaymentRequestDocument> docs = (List<PaymentRequestDocument>) businessObjectService.findMatching(PaymentRequestDocument.class, criteria);

        if ((docs != null) && !docs.isEmpty()) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("found " + docs.size() + " docs");
            }

            PaymentRequestDocument doc = docs.get(0);

            List<DocumentIncomeType<PaymentRequestDocument>> incomeTypes = get1099DocumentIncomeTypes(TaxConstants.DocumentTypes.PAYMENT_REQUEST_DOCUMENT, documentNumber);

            if (incomeTypes != null) {
                for (DocumentIncomeType<PaymentRequestDocument> incomeType : incomeTypes) {
                    // get a new Payment that is pre-populated with PaymentDetail information
                    Payment payment = new Payment();

                    payment.setPayeeId(payee.getId());
                    payment.setExcludeIndicator(Boolean.FALSE);
                    payment.setDocNbr(documentNumber);
                    payment.setDocType(TaxConstants.DocumentTypes.PAYMENT_REQUEST_DOCUMENT_NON_CHECK);
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

    private int saveNonPdpCMPayments(Payee payee, String documentNumber) {
        int retval = 0;

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
        List<VendorCreditMemoDocument> docs = (List<VendorCreditMemoDocument>) businessObjectService.findMatching(VendorCreditMemoDocument.class, criteria);

        if ((docs != null) && !docs.isEmpty()) {
            VendorCreditMemoDocument doc = docs.get(0);
            if (LOG.isDebugEnabled()) {
                LOG.debug("found " + docs.size() + " docs");
            }

            List<DocumentIncomeType<VendorCreditMemoDocument>> incomeTypes = get1099DocumentIncomeTypes(TaxConstants.DocumentTypes.CREDIT_MEMO_DOCUMENT, documentNumber);

            // if we found income type then create and save payments
            if (incomeTypes != null) {
                for (DocumentIncomeType<VendorCreditMemoDocument> incomeType : incomeTypes) {
                    // get a new Payment that is pre-populated with PaymentDetail information
                    Payment payment = new Payment();

                    payment.setPayeeId(payee.getId());
                    payment.setExcludeIndicator(Boolean.FALSE);
                    payment.setDocNbr(documentNumber);
                    payment.setDocType(TaxConstants.DocumentTypes.CREDIT_MEMO_DOCUMENT_NON_CHECK);
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

    private Payee getPayeeByVendor(VendorDetail vendor, Integer taxYear) {
        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(TaxPropertyConstants.PayeeFields.VENDOR_NUMBER, vendor.getVendorNumber());
        criteria.put(TaxPropertyConstants.PayeeFields.TAX_YEAR, taxYear);
        List<Payee> payees = (List<Payee>) businessObjectService.findMatching(Payee.class, criteria);
        Payee retval = null;
        if ((payees != null) && !payees.isEmpty()) {
            retval = payees.get(0);
        }
        if (retval == null) {
            retval = taxPayeeService.createNewPayee(taxYear, vendor);
            businessObjectService.save(retval);
        }
        return retval;
    }

    private boolean hasPayments(Integer payeeId, PaymentDetail paymentDetail) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(TaxPropertyConstants.PaymentFields.PAYMENT_GROUP_ID, paymentDetail.getPaymentGroupId());
        fieldValues.put(TaxPropertyConstants.PaymentFields.DOCUMENT_NUMBER, paymentDetail.getCustPaymentDocNbr());
        fieldValues.put(TaxPropertyConstants.PAYMENT_DETAIL_PAYEE_ID, payeeId);
        int cnt = businessObjectService.countMatching(Payment.class, fieldValues);
        if (LOG.isDebugEnabled()) {
            LOG.debug("found payments(" + cnt + "): payee=" + payeeId + ", docNumber=" + paymentDetail.getCustPaymentDocNbr() + ", paymentGroup=" + paymentDetail.getPaymentGroupId());
        }
        boolean retval = (cnt > 0);
        return retval;
    }

    private boolean isPreq(String documentType) {
        boolean retval = TaxConstants.DocumentTypes.PAYMENT_REQUEST_DOCUMENT.equals(documentType);
        retval |= TaxConstants.DocumentTypes.PAYMENT_REQUEST_DOCUMENT_NON_CHECK.equals(documentType);
        return retval;
    }

    private boolean isCm(String documentType) {
        boolean retval = TaxConstants.DocumentTypes.CREDIT_MEMO_DOCUMENT.equals(documentType);
        return retval;
    }

    private boolean isDv(String documentType) {
        boolean retval = TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT.equals(documentType);
        retval |= TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT_CHECKACH.equals(documentType);
        retval |= TaxConstants.DocumentTypes.DISBURSEMENT_VOUCHER_DOCUMENT_NON_CHECK.equals(documentType);
        return retval;
    }

    private String buildDocumentKey(PaymentDetail paymentDetail) {
        StringBuilder retval = new StringBuilder(64);
        retval.append(paymentDetail.getCustPaymentDocNbr());
        retval.append(".");
        retval.append(paymentDetail.getPaymentGroupId());
        return retval.toString();
    }

}
