/*
 * Copyright 2013 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Service which supports creating PDP payments for Corporate Cards by extracting reimbursable documents
 */
public class CorporateCardExtractionServiceImpl implements PaymentSourceToExtractService<TEMReimbursementDocument> {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CorporateCardExtractionServiceImpl.class);
    protected ParameterService parameterService;
    protected DocumentService documentService;
    protected TravelDocumentDao travelDocumentDao;
    protected PaymentSourceHelperService paymentSourceHelperService;
    protected TravelPaymentsHelperService travelPaymentsHelperService;
    protected BusinessObjectService businessObjectService;
    protected VendorService vendorService;

    /**
     *
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<TEMReimbursementDocument>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        final boolean disburseCorporateCardPayments = getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.CORPORATE_CARD_PAYMENT_IND);
        if (!disburseCorporateCardPayments) {
            return new HashMap<String, List<TEMReimbursementDocument>>(); // can't disburse payments? then don't find any documents to disburse
        }

        Map<String, List<TEMReimbursementDocument>> documentsByCampus = new HashMap<String, List<TEMReimbursementDocument>>();
        final List<TEMReimbursementDocument> reimbursables = retrieveAllCorporateCardDocuments(immediatesOnly);
        Map<String, String> initiatorCampuses = new HashMap<String, String>();
        for (TEMReimbursementDocument document : reimbursables) {
            if (document.isCorporateCardPayable() && shouldExtractPayment(document)) {
                final String campusCode = getTravelPaymentsHelperService().findCampusForDocument(document, initiatorCampuses);
                if (!StringUtils.isBlank(campusCode)) {
                    List<TEMReimbursementDocument> documentsForCampus = documentsByCampus.get(campusCode);
                    if (documentsForCampus == null) {
                        documentsForCampus = new ArrayList<TEMReimbursementDocument>();
                        documentsByCampus.put(campusCode, documentsForCampus);
                    }
                    documentsForCampus.add(document);
                }
            }
        }
        return documentsByCampus;
    }

    /**
     * Retrieves all the TravelReimbursement, TravelRelocation, and TravelEntertainment documents paid by check at approved status in one convenient call
     * @param immediatesOnly true if only those documents marked for immediate payment should be retrieved, false if all qualifying documents should be retrieved
     * @return all of the documents to process in a list
     */
    protected List<TEMReimbursementDocument> retrieveAllCorporateCardDocuments(boolean immediatesOnly) {
        List<TEMReimbursementDocument> allReimbursables = new ArrayList<TEMReimbursementDocument>();
        allReimbursables.addAll(getTravelDocumentDao().getReimbursementDocumentsNeedingCorporateCardExtraction());
        allReimbursables.addAll(getTravelDocumentDao().getEntertainmentDocumentsNeedingCorporateCardExtraction());
        allReimbursables.addAll(getTravelDocumentDao().getRelocationDocumentsNeedingCorporateCardExtraction());
        return allReimbursables;
    }

    /**
     * Uses the value in the KFS-TEM / Document / PRE_DISBURSEMENT_EXTRACT_ORGANIZATION parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getPreDisbursementCustomerProfileUnit()
     */
    @Override
    public String getPreDisbursementCustomerProfileUnit() {
        final String unit = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_ORG_CODE);
        return unit;
    }

    /**
     * Uses the value in the KFS-TEM / Document / PRE_DISBURSEMENT_EXTRACT_SUB_UNIT
     * @see org.kuali.kfs.sys.document.PaymentSource#getPreDisbursementCustomerProfileSubUnit()
     */
    @Override
    public String getPreDisbursementCustomerProfileSubUnit() {
        final String subUnit = getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);
        return subUnit;
    }

    /**
     * Sets the corporate card payment extracted date on the document
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#markAsExtracted(org.kuali.kfs.sys.document.PaymentSource, java.sql.Date)
     */
    @Override
    public void markAsExtracted(TEMReimbursementDocument document, Date sqlProcessRunDate, KualiInteger paymentGroupId) {
        try {
            document.setCorporateCardPaymentExtractDate(sqlProcessRunDate);
            associatePaymentGroupWithCreditCardData(document, paymentGroupId);
            getDocumentService().saveDocument(document, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("Could not save TEMReimbursementDocument document #" + document.getDocumentNumber() + ": " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Associates the given payment group id with every historical travel expense that represents a corporate card charge on the given document
     * @param document the document to update
     * @param paymentGroupNumber the payment group id to update the historical expenses with
     */
    protected void associatePaymentGroupWithCreditCardData(TEMReimbursementDocument document, KualiInteger paymentGroupNumber) {
        for (HistoricalTravelExpense expense : document.getHistoricalTravelExpenses()){
            if (expense.getCreditCardStagingData() != null){
                if (StringUtils.equals(expense.getCreditCardStagingData().getCreditCardAgency().getTravelCardTypeCode(), TemConstants.TRAVEL_TYPE_CORP)){
                    expense.getCreditCardStagingData().setPaymentGroupId(paymentGroupNumber);
                    getBusinessObjectService().save(expense.getCreditCardStagingData());
                }
            }
        }
    }

    /**
     *
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#createPaymentGroup(org.kuali.kfs.sys.document.PaymentSource, java.sql.Date)
     */
    @Override
    public PaymentGroup createPaymentGroup(TEMReimbursementDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPaymentGroupForReimbursable() started");
        }

        final boolean disburseCorporateCardPayments = getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TemConstants.TravelParameters.CORPORATE_CARD_PAYMENT_IND);
        if (!disburseCorporateCardPayments) {
            return null; // can't disburse payments? then don't create payment groups
        }

        PaymentGroup pg = new PaymentGroup();
        final VendorDetail vendor = getCorporateCardVendor(document);
        final VendorAddress vendorAddress = getVendorService().getVendorDefaultAddress(vendor.getVendorAddresses(), vendor.getVendorHeader().getVendorType().getAddressType().getVendorAddressTypeCode(), "");
        pg.setCombineGroups(Boolean.TRUE);
        pg.setCampusAddress(Boolean.FALSE);

        pg.setCity(vendorAddress.getVendorCityName());
        pg.setCountry(vendorAddress.getVendorCountryCode());
        pg.setLine1Address(vendorAddress.getVendorLine1Address());
        pg.setLine2Address(vendorAddress.getVendorLine2Address());
        pg.setPayeeName(vendor.getVendorName());
        pg.setState(vendorAddress.getVendorStateCode());
        pg.setZipCd(vendorAddress.getVendorZipCode());
        pg.setPaymentDate(getNextDate(processRunDate));
        pg.setProcessImmediate(false);
        pg.setPymtAttachment(false);
        pg.setPymtSpecialHandling(false);
        pg.setNraPayment(false);
        pg.setBankCode(document.getFinancialDocumentBankCode());
        pg.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.OPEN);
        if (StringUtils.equals(document.getTraveler().getTravelerTypeCode(), TemConstants.EMP_TRAVELER_TYP_CD)) {
            pg.setEmployeeIndicator(true);
        }
        pg.setPayeeId(vendor.getVendorNumber());
        pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);
        pg.setTaxablePayment(Boolean.FALSE);
        pg.setPayeeOwnerCd(vendor.getVendorHeader().getVendorOwnershipCode());

        // now add the payment detail
        final PaymentDetail paymentDetail = buildPaymentDetail(document, processRunDate);
        pg.addPaymentDetails(paymentDetail);
        paymentDetail.setPaymentGroup(pg);

        return pg;
    }

    /**
     * Finds the vendor for the corporate card used on the given document
     * @param document the document to find a corporate card vendor for
     * @return the found vendor or null if a vendor could not be found
     */
    protected VendorDetail getCorporateCardVendor(TEMReimbursementDocument document) {
       final String vendorNumber =  findCorporateCardVendorNumber(document);
       if (!StringUtils.isBlank(vendorNumber)) {
           return getVendorService().getByVendorNumber(vendorNumber);
       }
       return null;
    }

    /**
     * Finds the vendor number from the first historical travel expense associated with the given document
     * @param document a document to find a corporate card vendor for
     * @return the id of the found vendor or null if nothing was found
     */
    protected String findCorporateCardVendorNumber(TEMReimbursementDocument document) {
        for (HistoricalTravelExpense historicalTravelExpense : document.getHistoricalTravelExpenses()){
            if (historicalTravelExpense.isCreditCardTravelExpense() && StringUtils.equals(historicalTravelExpense.getCreditCardAgency().getTravelCardTypeCode(), TemConstants.TRAVEL_TYPE_CORP)) {
                return historicalTravelExpense.getCreditCardAgency().getVendorNumber();
            }
        }
        return null;
    }

    /**
     * Returns a Date object representing the day after the given processDate
     * @param processDate the date to find the next day for
     * @return the next day for the given date
     */
    protected Date getNextDate(Date processDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(processDate.getTime());
        cal.add(Calendar.DATE, 1);
        return new java.sql.Date(cal.getTimeInMillis());
    }

    /**
     * Builds the PaymentDetail for the given reimbursable travel & entertainment document
     * @param document the reimbursable travel & entertainment document to create a payment for
     * @param processRunDate the date when the extraction is occurring
     * @return a PaymentDetail to add to the PaymentGroup
     */
    protected PaymentDetail buildPaymentDetail(TEMReimbursementDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("buildPaymentDetail() started");
        }

        PaymentDetail pd = new PaymentDetail();
        if (StringUtils.isNotEmpty(document.getDocumentHeader().getOrganizationDocumentNumber())) {
            pd.setOrganizationDocNbr(document.getDocumentHeader().getOrganizationDocumentNumber());
        }
        pd.setCustPaymentDocNbr(document.getDocumentNumber());
        pd.setInvoiceDate(new java.sql.Date(processRunDate.getTime()));
        pd.setOrigInvoiceAmount(getPaymentAmount(document));
        pd.setInvTotDiscountAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherCreditAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherDebitAmount(KualiDecimal.ZERO);
        pd.setInvTotShipAmount(KualiDecimal.ZERO);
        pd.setNetPaymentAmount(getPaymentAmount(document));
        pd.setPrimaryCancelledPayment(Boolean.FALSE);
        pd.setFinancialDocumentTypeCode(getAchCheckDocumentType(document));
        pd.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);
        pd.setPurchaseOrderNbr(document.getTravelDocumentIdentifier());

        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        final String travelerId = (!ObjectUtils.isNull(document.getTemProfile()) ?
                (!ObjectUtils.isNull(document.getTemProfile().getPrincipal()) ? document.getTemProfile().getPrincipal().getPrincipalName() : document.getTemProfile().getCustomerNumber()) :
                    document.getDocumentNumber()); // they got this far without a payee id?  that really probably shouldn't happen
        pnt.setCustomerNoteText("Info: " + travelerId + " " + document.getTemProfile().getPhoneNumber());
        pd.addNote(pnt);

        final String text = document.getDocumentHeader().getDocumentDescription();
        if (!StringUtils.isBlank(text)) {
            pnt = getPaymentSourceHelperService().buildNoteForCheckStubText(text, line);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating check stub text note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        // Handle accounts
        final List<PaymentAccountDetail> paymentAccounts = buildPaymentAccountDetails(document.getSourceAccountingLines());
        for (PaymentAccountDetail pad : paymentAccounts) {
            pd.addAccountDetail(pad);
        }

        return pd;
    }

    /**
    * Only build account details for corporate card accounting lines
    * @see org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService#buildGenericPaymentAccountDetails(java.util.List)
    */
   protected List<PaymentAccountDetail> buildPaymentAccountDetails(List<? extends AccountingLine> accountingLines) {
       List<PaymentAccountDetail> details = new ArrayList<PaymentAccountDetail>();
       for (AccountingLine al : accountingLines) {
           final TemSourceAccountingLine accountingLine = (TemSourceAccountingLine)al;
           if (StringUtils.equals(accountingLine.getCardType(), TemConstants.TRAVEL_TYPE_CORP)) {
               PaymentAccountDetail pad = new PaymentAccountDetail();
               pad.setFinChartCode(accountingLine.getChartOfAccountsCode());
               pad.setAccountNbr(accountingLine.getAccountNumber());
               if (!StringUtils.isBlank(accountingLine.getSubAccountNumber())) {
                   pad.setSubAccountNbr(accountingLine.getSubAccountNumber());
               }
               else {
                   pad.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
               }
               pad.setFinObjectCode(accountingLine.getFinancialObjectCode());
               if (!StringUtils.isBlank(accountingLine.getFinancialSubObjectCode())) {
                   pad.setFinSubObjectCode(accountingLine.getFinancialSubObjectCode());
               }
               else {
                   pad.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
               }
               if (!StringUtils.isBlank(accountingLine.getOrganizationReferenceId())) {
                   pad.setOrgReferenceId(accountingLine.getOrganizationReferenceId());
               }
               if (!StringUtils.isBlank(accountingLine.getProjectCode())) {
                   pad.setProjectCode(accountingLine.getProjectCode());
               }
               else {
                   pad.setProjectCode(KFSConstants.getDashProjectCode());
               }
               pad.setAccountNetAmount(accountingLine.getAmount());
               details.add(pad);
           }
       }
       return details;
   }

    /**
     * The sum of all CORP accounting lines on the document
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#getPaymentAmount(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public KualiDecimal getPaymentAmount(TEMReimbursementDocument document) {
        KualiDecimal amount = KualiDecimal.ZERO;
        for(TemSourceAccountingLine line: (List<TemSourceAccountingLine>)document.getSourceAccountingLines()){
            if (StringUtils.equals(line.getCardType(), TemConstants.TRAVEL_TYPE_CORP)){
                amount = amount.add(line.getAmount());
            }
        }
        return amount;
    }

    /**
     * Sets teh corporate card payment paid date on the document to the process date
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#markAsPaid(org.kuali.kfs.sys.document.PaymentSource, java.sql.Date)
     */
    @Override
    public void markAsPaid(TEMReimbursementDocument paymentSource, Date processDate) {
        try {
            paymentSource.setCorporateCardPaymentPaidDate(processDate);
            getDocumentService().saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Sets the corporate card payment cancel date to the given cancel date and handles the entry cancellation for the payment
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#cancelPayment(org.kuali.kfs.sys.document.PaymentSource, java.sql.Date)
     */
    @Override
    public void cancelPayment(TEMReimbursementDocument paymentSource, Date cancelDate) {
        if (paymentSource.getCorporateCardPaymentCancelDate() == null) {
            try {
                paymentSource.setCorporateCardPaymentCancelDate(cancelDate);
                getPaymentSourceHelperService().handleEntryCancellation(paymentSource);
                // save the document
                getDocumentService().saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
                throw new RuntimeException(we);
            }
        }

    }

    /**
     * Resets the corporate card payment extract date and paid date on the document to null
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#resetFromExtraction(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public void resetFromExtraction(TEMReimbursementDocument paymentSource) {
        try {
            paymentSource.setCorporateCardPaymentExtractDate(null);
            paymentSource.setCorporateCardPaymentPaidDate(null);
            getDocumentService().saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }

    }

    /**
     * Returns the value of the KFS-TEM / Document / IMMEDIATE_EXTRACT_NOTIFICATION_FROM_EMAIL_ADDRESS parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getImmediateExtractEMailFromAddress()
     */
    @Override
    public String getImmediateExtractEMailFromAddress() {
        return getParameterService().getParameterValueAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.IMMEDIATE_EXTRACT_FROM_ADDRESS_PARM_NM);
    }

    /**
     * Returns the value of the KFS-TEM / Document / IMMEDIATE_EXTRACT_NOTIFICATION_TO_EMAIL_ADDRESSES parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getImmediateExtractEmailToAddresses()
     */
    @Override
    public List<String> getImmediateExtractEmailToAddresses() {
        List<String> toAddresses = new ArrayList<String>();
        toAddresses.addAll(getParameterService().getParameterValuesAsString(TemParameterConstants.TEM_DOCUMENT.class, KFSParameterKeyConstants.PdpExtractBatchParameters.IMMEDIATE_EXTRACT_TO_ADDRESSES_PARM_NM));
        return toAddresses;
    }

    /**
     * Returns RCCA.  But not TCCA or TAVA.
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#getAchCheckDocumentType(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public String getAchCheckDocumentType(TEMReimbursementDocument paymentSource) {
        return TemConstants.TravelDocTypes.REIMBURSABLE_CORPORATE_CARD_CHECK_ACH_DOCUMENT;
    }

    /**
     * This extraction service handles RCCA payment details
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#handlesAchCheckDocumentType(java.lang.String)
     */
    @Override
    public boolean handlesAchCheckDocumentType(String achCheckDocumentType) {
        return StringUtils.equals(TemConstants.TravelDocTypes.REIMBURSABLE_CORPORATE_CARD_CHECK_ACH_DOCUMENT, achCheckDocumentType);
    }

    /**
     * Determines if the payment would be 0 - if it's greater than that, it should be extracted
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#shouldExtractPayment(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public boolean shouldExtractPayment(TEMReimbursementDocument paymentSource) {
        return KualiDecimal.ZERO.isLessThan(getPaymentAmount(paymentSource));
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    public PaymentSourceHelperService getPaymentSourceHelperService() {
        return paymentSourceHelperService;
    }

    public void setPaymentSourceHelperService(PaymentSourceHelperService paymentSourceHelperService) {
        this.paymentSourceHelperService = paymentSourceHelperService;
    }

    public TravelPaymentsHelperService getTravelPaymentsHelperService() {
        return travelPaymentsHelperService;
    }

    public void setTravelPaymentsHelperService(TravelPaymentsHelperService travelPaymentsHelperService) {
        this.travelPaymentsHelperService = travelPaymentsHelperService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public VendorService getVendorService() {
        return vendorService;
    }

    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

}
