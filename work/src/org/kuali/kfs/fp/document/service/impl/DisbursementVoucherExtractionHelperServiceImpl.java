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
package org.kuali.kfs.fp.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.DvToPdpExtractStep;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.dataaccess.DisbursementVoucherDao;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPaymentService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

public class DisbursementVoucherExtractionHelperServiceImpl implements DisbursementVoucherPaymentService, PaymentSourceToExtractService<DisbursementVoucherDocument> {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherExtractionHelperServiceImpl.class);

    protected BusinessObjectService businessObjectService;
    protected DocumentService documentService;
    protected GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    protected ParameterService parameterService;
    protected ParameterEvaluatorService parameterEvaluatorService;
    protected VendorService vendorService;
    protected DisbursementVoucherDao disbursementVoucherDao;
    protected PaymentSourceHelperService paymentSourceHelperService;

    @Override
    public void cancelDisbursementVoucher(DisbursementVoucherDocument dv, Date cancelDate) {
        if (dv.getCancelDate() == null) {
            try {
                // set the canceled date
                dv.setCancelDate(cancelDate);
                dv.refreshReferenceObject("generalLedgerPendingEntries");
                getPaymentSourceHelperService().handleEntryCancellation(dv);
                // set the financial document status to canceled
                dv.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
                // save the document
                getDocumentService().saveDocument(dv, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + dv.getDocumentNumber() + " " + we);
                throw new RuntimeException(we);
            }
        }

    }

    /**
     * Updates the given general ledger pending entry so that it will have the opposite effect of what it was created to do; this,
     * in effect, undoes the entries that were already posted for this document
     *
     * @param glpe the general ledger pending entry to undo
     */
    protected void oppositifyEntry(GeneralLedgerPendingEntry glpe, BusinessObjectService boService, GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper) {
        if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_CREDIT_CODE)) {
            glpe.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else if (glpe.getTransactionDebitCreditCode().equals(KFSConstants.GL_DEBIT_CODE)) {
            glpe.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        glpe.setTransactionLedgerEntrySequenceNumber(glpeSeqHelper.getSequenceCounter());
        glpeSeqHelper.increment();
        glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        boService.save(glpe);
    }

    /**
     * Retrieve disbursement vouchers for extraction
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<DisbursementVoucherDocument>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        if (immediatesOnly) {
            throw new UnsupportedOperationException("DisbursementVoucher PDP does immediates extraction through normal document processing; immediates for DisbursementVoucher should not be run through batch.");
        }

        Map<String, List<DisbursementVoucherDocument>> documentsByCampus = new HashMap<String, List<DisbursementVoucherDocument>>();

        Collection<DisbursementVoucherDocument> docs = disbursementVoucherDao.getDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, false);
        for (DisbursementVoucherDocument element : docs) {
            String dvdCampusCode = element.getCampusCode();
            if (StringUtils.isNotBlank(dvdCampusCode)) {
                if (documentsByCampus.containsKey(dvdCampusCode)) {
                    List<DisbursementVoucherDocument> documents = documentsByCampus.get(dvdCampusCode);
                    documents.add(element);
                }
                else {
                    List<DisbursementVoucherDocument> documents = new ArrayList<DisbursementVoucherDocument>();
                    documents.add(element);
                    documentsByCampus.put(dvdCampusCode, documents);
                }
            }
        }

        return documentsByCampus;
    }

    @Override
    public PaymentGroup createPaymentGroupForDisbursementVoucher(DisbursementVoucherDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPaymentGroupForDisbursementVoucher() started");
        }

        PaymentGroup pg = new PaymentGroup();
        pg.setCombineGroups(Boolean.TRUE);
        pg.setCampusAddress(Boolean.FALSE);

        document.refreshReferenceObject(KFSPropertyConstants.DV_PAYEE_DETAIL);
        DisbursementVoucherPayeeDetail pd = document.getDvPayeeDetail();
        String rc = pd.getDisbVchrPaymentReasonCode();

        if (KFSConstants.PaymentPayeeTypes.CUSTOMER.equals(document.getDvPayeeDetail().getDisbursementVoucherPayeeTypeCode())) {
            pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.CUSTOMER);
            pg.setTaxablePayment(Boolean.FALSE);
        }
        // If the payee is an employee, set these flags accordingly
        else if ((document.getDvPayeeDetail().isVendor() && getVendorService().isVendorInstitutionEmployee(pd.getDisbVchrVendorHeaderIdNumberAsInteger())) || document.getDvPayeeDetail().isEmployee()) {
            pg.setEmployeeIndicator(Boolean.TRUE);
            pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.EMPLOYEE);
            pg.setTaxablePayment(
                    !/*REFACTORME*/getParameterEvaluatorService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.RESEARCH_PAYMENT_REASONS_PARM_NM, rc).evaluationSucceeds()
                        && !getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PAYMENT_REASON_CODE_RENTAL_PAYMENT_PARM_NM).equals(rc)
                        && !getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PAYMENT_REASON_CODE_ROYALTIES_PARM_NM).equals(rc));
        }
        // Payee is not an employee
        else {

            // These are taxable
            VendorDetail vendDetail = getVendorService().getVendorDetail(pd.getDisbVchrVendorHeaderIdNumberAsInteger(), pd.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            String vendorOwnerCode = vendDetail.getVendorHeader().getVendorOwnershipCode();
            String vendorOwnerCategoryCode = vendDetail.getVendorHeader().getVendorOwnershipCategoryCode();
            String payReasonCode = pd.getDisbVchrPaymentReasonCode();

            pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.VENDOR_ID);

            // Assume it is not taxable until proven otherwise
            pg.setTaxablePayment(Boolean.FALSE);
            pg.setPayeeOwnerCd(vendorOwnerCode);

            ParameterEvaluator parameterEvaluator1 = /*REFACTORME*/getParameterEvaluatorService().getParameterEvaluator(DvToPdpExtractStep.class, PdpParameterConstants.TAXABLE_PAYMENT_REASON_CODES_BY_OWNERSHIP_CODES_PARAMETER_NAME, PdpParameterConstants.NON_TAXABLE_PAYMENT_REASON_CODES_BY_OWNERSHIP_CODES_PARAMETER_NAME, vendorOwnerCode, payReasonCode);
            ParameterEvaluator parameterEvaluator2 = /*REFACTORME*/getParameterEvaluatorService().getParameterEvaluator(DvToPdpExtractStep.class, PdpParameterConstants.TAXABLE_PAYMENT_REASON_CODES_BY_CORPORATION_OWNERSHIP_TYPE_CATEGORY_PARAMETER_NAME, PdpParameterConstants.NON_TAXABLE_PAYMENT_REASON_CODES_BY_CORPORATION_OWNERSHIP_TYPE_CATEGORY_PARAMETER_NAME, vendorOwnerCategoryCode, payReasonCode);

            if ( parameterEvaluator1.evaluationSucceeds() ) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
            else if (getParameterService().getParameterValueAsString(DvToPdpExtractStep.class, PdpParameterConstants.CORPORATION_OWNERSHIP_TYPE_PARAMETER_NAME).equals("CP") &&
                      StringUtils.isEmpty(vendorOwnerCategoryCode) &&
                      /*REFACTORME*/getParameterEvaluatorService().getParameterEvaluator(DvToPdpExtractStep.class, PdpParameterConstants.TAXABLE_PAYMENT_REASON_CODES_FOR_BLANK_CORPORATION_OWNERSHIP_TYPE_CATEGORIES_PARAMETER_NAME, payReasonCode).evaluationSucceeds()) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
            else if (getParameterService().getParameterValueAsString(DvToPdpExtractStep.class, PdpParameterConstants.CORPORATION_OWNERSHIP_TYPE_PARAMETER_NAME).equals("CP")
                        && !StringUtils.isEmpty(vendorOwnerCategoryCode)
                        && parameterEvaluator2.evaluationSucceeds() ) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
        }

        pg.setCity(pd.getDisbVchrPayeeCityName());
        pg.setCountry(pd.getDisbVchrPayeeCountryCode());
        pg.setLine1Address(pd.getDisbVchrPayeeLine1Addr());
        pg.setLine2Address(pd.getDisbVchrPayeeLine2Addr());
        pg.setPayeeName(pd.getDisbVchrPayeePersonName());
        pg.setPayeeId(pd.getDisbVchrPayeeIdNumber());
        pg.setState(pd.getDisbVchrPayeeStateCode());
        pg.setZipCd(pd.getDisbVchrPayeeZipCode());
        pg.setPaymentDate(document.getDisbursementVoucherDueDate());
        pg.setProcessImmediate(document.isImmediatePaymentIndicator());
        pg.setPymtAttachment(document.isDisbVchrAttachmentCode());
        pg.setPymtSpecialHandling(document.isDisbVchrSpecialHandlingCode());
        pg.setNraPayment(pd.isDisbVchrAlienPaymentCode());

        pg.setBankCode(document.getDisbVchrBankCode());
        pg.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.OPEN);

        // now add the payment detail
        final PaymentDetail paymentDetail = buildPaymentDetail(document, processRunDate);
        pg.addPaymentDetails(paymentDetail);
        paymentDetail.setPaymentGroup(pg);

        return pg;
    }

    /**
     * This method builds a payment detail object from the disbursement voucher document provided and links that detail file to the
     * batch and process run date given.
     *
     * @param document The disbursement voucher document to retrieve payment information from to populate the PaymentDetail.
     * @param batch The batch file associated with the payment.
     * @param processRunDate The date of the payment detail invoice.
     * @return A fully populated PaymentDetail instance.
     */
    protected PaymentDetail buildPaymentDetail(DisbursementVoucherDocument document, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("buildPaymentDetail() started");
        }
        final String maxNoteLinesParam = getParameterService().getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);

        int maxNoteLines;
        try {
            maxNoteLines = Integer.parseInt(maxNoteLinesParam);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Max Notes Lines parameter, value: "+maxNoteLinesParam+" cannot be converted to an integer");
        }

        PaymentDetail pd = new PaymentDetail();
        if (StringUtils.isNotEmpty(document.getDocumentHeader().getOrganizationDocumentNumber())) {
            pd.setOrganizationDocNbr(document.getDocumentHeader().getOrganizationDocumentNumber());
        }
        pd.setCustPaymentDocNbr(document.getDocumentNumber());
        pd.setInvoiceDate(new java.sql.Date(processRunDate.getTime()));
        pd.setOrigInvoiceAmount(document.getDisbVchrCheckTotalAmount());
        pd.setInvTotDiscountAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherCreditAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherDebitAmount(KualiDecimal.ZERO);
        pd.setInvTotShipAmount(KualiDecimal.ZERO);
        pd.setNetPaymentAmount(document.getDisbVchrCheckTotalAmount());
        pd.setPrimaryCancelledPayment(Boolean.FALSE);
        pd.setFinancialDocumentTypeCode(DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH);
        pd.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);

        // Handle accounts
        for (SourceAccountingLine sal : (List<? extends SourceAccountingLine>)document.getSourceAccountingLines()) {
            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setFinChartCode(sal.getChartOfAccountsCode());
            pad.setAccountNbr(sal.getAccountNumber());
            if (StringUtils.isNotEmpty(sal.getSubAccountNumber())) {
                pad.setSubAccountNbr(sal.getSubAccountNumber());
            }
            else {
                pad.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
            }
            pad.setFinObjectCode(sal.getFinancialObjectCode());
            if (StringUtils.isNotEmpty(sal.getFinancialSubObjectCode())) {
                pad.setFinSubObjectCode(sal.getFinancialSubObjectCode());
            }
            else {
                pad.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            if (StringUtils.isNotEmpty(sal.getOrganizationReferenceId())) {
                pad.setOrgReferenceId(sal.getOrganizationReferenceId());
            }
            if (StringUtils.isNotEmpty(sal.getProjectCode())) {
                pad.setProjectCode(sal.getProjectCode());
            }
            else {
                pad.setProjectCode(KFSConstants.getDashProjectCode());
            }
            pad.setAccountNetAmount(sal.getAmount());
            pd.addAccountDetail(pad);
        }

        // Handle notes
        DisbursementVoucherPayeeDetail dvpd = document.getDvPayeeDetail();

        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Info: " + document.getDisbVchrContactPersonName() + " " + document.getDisbVchrContactPhoneNumber());
        pd.addNote(pnt);

        String dvSpecialHandlingPersonName = null;
        String dvSpecialHandlingLine1Address = null;
        String dvSpecialHandlingLine2Address = null;
        String dvSpecialHandlingCity = null;
        String dvSpecialHandlingState = null;
        String dvSpecialHandlingZip = null;

        dvSpecialHandlingPersonName = dvpd.getDisbVchrSpecialHandlingPersonName();
        dvSpecialHandlingLine1Address = dvpd.getDisbVchrSpecialHandlingLine1Addr();
        dvSpecialHandlingLine2Address = dvpd.getDisbVchrSpecialHandlingLine2Addr();
        dvSpecialHandlingCity = dvpd.getDisbVchrSpecialHandlingCityName();
        dvSpecialHandlingState = dvpd.getDisbVchrSpecialHandlingStateCode();
        dvSpecialHandlingZip = dvpd.getDisbVchrSpecialHandlingZipCode();

        if (StringUtils.isNotEmpty(dvSpecialHandlingPersonName)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Send Check To: " + dvSpecialHandlingPersonName);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling person name note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingLine1Address)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingLine1Address);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 1 note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingLine2Address)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingLine2Address);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 2 note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingCity)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingCity + ", " + dvSpecialHandlingState + " " + dvSpecialHandlingZip);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling city note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (document.isDisbVchrAttachmentCode()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Attachment Included");
            if (LOG.isDebugEnabled()) {
                LOG.debug("create attachment note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        String paymentReasonCode = dvpd.getDisbVchrPaymentReasonCode();
        if (/*REFACTORME*/getParameterEvaluatorService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.NONEMPLOYEE_TRAVEL_PAY_REASONS_PARM_NM, paymentReasonCode).evaluationSucceeds()) {
            DisbursementVoucherNonEmployeeTravel dvnet = document.getDvNonEmployeeTravel();

            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Reimbursement associated with " + dvnet.getDisbVchrServicePerformedDesc());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating non employee travel notes: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);

            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("The total per diem amount for your daily expenses is " + dvnet.getDisbVchrPerdiemCalculatedAmt());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating non employee travel notes: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);

            if (dvnet.getDisbVchrPersonalCarAmount() != null && dvnet.getDisbVchrPersonalCarAmount().compareTo(KualiDecimal.ZERO) != 0) {
                pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                pnt.setCustomerNoteText("The total dollar amount for your vehicle mileage is " + dvnet.getDisbVchrPersonalCarAmount());
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Creating non employee travel vehicle note: "+pnt.getCustomerNoteText());
                }
                pd.addNote(pnt);

                for (DisbursementVoucherNonEmployeeExpense exp : (List<DisbursementVoucherNonEmployeeExpense>)dvnet.getDvNonEmployeeExpenses()) {
                    if (line < (maxNoteLines - 8)) {
                        pnt = new PaymentNoteText();
                        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                        pnt.setCustomerNoteText(exp.getDisbVchrExpenseCompanyName() + " " + exp.getDisbVchrExpenseAmount());
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Creating non employee travel expense note: "+pnt.getCustomerNoteText());
                        }
                        pd.addNote(pnt);
                    }
                }
            }
        }
        else if (/*REFACTORME*/getParameterEvaluatorService().getParameterEvaluator(DisbursementVoucherDocument.class, DisbursementVoucherConstants.PREPAID_TRAVEL_PAYMENT_REASONS_PARM_NM, paymentReasonCode).evaluationSucceeds()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Payment is for the following individuals/charges:");
            pd.addNote(pnt);
            if (LOG.isDebugEnabled()) {
                LOG.info("Creating prepaid travel note note: "+pnt.getCustomerNoteText());
            }

            DisbursementVoucherPreConferenceDetail dvpcd = document.getDvPreConferenceDetail();

            for (DisbursementVoucherPreConferenceRegistrant dvpcr : (List<DisbursementVoucherPreConferenceRegistrant>)dvpcd.getDvPreConferenceRegistrants()) {
                if (line < (maxNoteLines - 8)) {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                    pnt.setCustomerNoteText(dvpcr.getDvConferenceRegistrantName() + " " + dvpcr.getDisbVchrExpenseAmount());
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Creating pre-paid conference registrants note: "+pnt.getCustomerNoteText());
                    }
                    pd.addNote(pnt);
                }
            }
        }

        // Get the original, raw form, note text from the DV document.
        final String text = document.getDisbVchrCheckStubText();
        if (!StringUtils.isBlank(text)) {
            pnt = this.getPaymentSourceHelperService().buildNoteForCheckStubText(text, line);
            // Logging...
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating check stub text note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        return pd;
    }

    /**
     * Uses the value in the KFS-FP / DisbursementVoucher / PRE_DISBURSEMENT_EXTRACT_ORGANIZATION parameter
     * @see org.kuali.kfs.sys.document.PaymentSource#getPreDisbursementCustomerProfileUnit()
     */
    @Override
    public String getPreDisbursementCustomerProfileUnit() {
        final String unit = getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_ORG_CODE);
        return unit;
    }


    /**
     * Uses the value in the KFS-FP / DisbursementVoucher / PRE_DISBURSEMENT_EXTRACT_SUB_UNIT
     * @see org.kuali.kfs.sys.document.PaymentSource#getPreDisbursementCustomerProfileSubUnit()
     */
    @Override
    public String getPreDisbursementCustomerProfileSubUnit() {
        final String subUnit = getParameterService().getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);
        return subUnit;
    }

    /**
     * @return the injected implementation of the BusinessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Injects an implementation of the BusinessObjectService
     * @param businessObjectService the implementation of the BusinessObjectService to inject
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @return the injected implementation of the GeneralLedgerPendingEntryService
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return generalLedgerPendingEntryService;
    }

    /**
     * Injects an implementation of the GeneralLedgerPendingEntryService
     * @param generalLedgerPendingEntryService the implementation of GeneralLedgerPendingEntryService to inject and use
     */
    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    /**
     * This method sets the disbursementVoucherDao instance.
     *
     * @param disbursementVoucherDao The DisbursementVoucherDao to be set.
     */
    public void setDisbursementVoucherDao(DisbursementVoucherDao disbursementVoucherDao) {
        this.disbursementVoucherDao = disbursementVoucherDao;
    }

    /**
     * @return an implementation of the ParameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the implementation of the ParameterService for this service to use
     * @param parameterService an implementation of ParameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * @return an implementation of the ParameterEvaluatorService
     */
    public ParameterEvaluatorService getParameterEvaluatorService() {
        return parameterEvaluatorService;
    }

    /**
     * Sets the implementation of the ParameterEvaluatorService for this service to use
     * @param parameterService an implementation of ParameterEvaluatorService
     */
    public void setParameterEvaluatorService(ParameterEvaluatorService parameterEvaluatorService) {
        this.parameterEvaluatorService = parameterEvaluatorService;
    }

    /**
     * @return an implementation of the VendorService
     */
    public VendorService getVendorService() {
        return vendorService;
    }

    /**
     * Sets the implementation of the VendorService for this service to use
     * @param parameterService an implementation of VendorService
     */
    public void setVendorService(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    /**
     * @return an implementation of the DocumentService
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the implementation of the DocumentService for this service to use
     * @param parameterService an implementation of DocumentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * @return an implementation of the PaymentSourceHelperService
     */
    public PaymentSourceHelperService getPaymentSourceHelperService() {
        return paymentSourceHelperService;
    }

    /**
     * Sets the implementation of the PaymentSourceHelperService for this service to use
     * @param paymentSourceHelperService an implementation of PaymentSourceHelperService
     */
    public void setPaymentSourceHelperService(PaymentSourceHelperService paymentSourceHelperService) {
        this.paymentSourceHelperService = paymentSourceHelperService;
    }
}
