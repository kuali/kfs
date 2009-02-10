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
package org.kuali.kfs.fp.batch.service.impl;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.batch.DvToPdpExtractStep;
import org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeExpense;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherNonEmployeeTravel;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceDetail;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.kfs.fp.dataaccess.DisbursementVoucherDao;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.KualiInteger;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the default implementation of the DisbursementVoucherExtractService interface.
 */
@Transactional
public class DisbursementVoucherExtractServiceImpl implements DisbursementVoucherExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherExtractServiceImpl.class);

    private ParameterService parameterService;
    private DisbursementVoucherDao disbursementVoucherDao;
    private DateTimeService dateTimeService;
    private org.kuali.rice.kim.service.PersonService personService;
    private CustomerProfileService customerProfileService;
    private PaymentFileService paymentFileService;
    private PaymentGroupService paymentGroupService;
    private BusinessObjectService businessObjectService;
    private PdpEmailService paymentFileEmailService;
    private int maxNoteLines;

    // This should only be set to true when testing this system. Setting this to true will run the code but
    // won't set the doc status to extracted
    boolean testMode = false;

    /**
     * This method extracts all payments from a disbursement voucher with a status code of "A" and uploads them as a batch for
     * processing.
     * 
     * @return Always returns true if the method completes.
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#extractPayments()
     */
    public boolean extractPayments() {
        LOG.debug("extractPayments() started");

        Date processRunDate = dateTimeService.getCurrentDate();

        String noteLines = parameterService.getParameterValue(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);

        try {
            maxNoteLines = Integer.parseInt(noteLines);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Max Notes Lines parameter");
        }

        Person uuser = personService.getPersonByPrincipalName(KFSConstants.SYSTEM_USER);
        if (uuser == null) {
            LOG.debug("extractPayments() Unable to find user " + KFSConstants.SYSTEM_USER);
            throw new IllegalArgumentException("Unable to find user " + KFSConstants.SYSTEM_USER);
        }

        // Get a list of campuses that have documents with an 'A' (approved) status.
        Set<String> campusList = getCampusListByDocumentStatusCode(DisbursementVoucherConstants.DocumentStatusCodes.APPROVED);

        // Process each campus one at a time
        for (String campusCode : campusList) {
            extractPaymentsForCampus(campusCode, uuser, processRunDate);
        }

        return true;
    }

    /**
     * This method extracts all outstanding payments from all the disbursement vouchers in approved status for a given campus and
     * adds these payments to a batch file that is uploaded for processing.
     * 
     * @param campusCode The id code of the campus the payments will be retrieved for.
     * @param user The user object used when creating the batch file to upload with outstanding payments.
     * @param processRunDate This is the date that the batch file is created, often this value will be today's date.
     */
    private void extractPaymentsForCampus(String campusCode, Person user, Date processRunDate) {
        LOG.debug("extractPaymentsForCampus() started for campus: " + campusCode);

        Batch batch = createBatch(campusCode, user, processRunDate);
        Integer count = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        Collection<DisbursementVoucherDocument> dvd = getListByDocumentStatusCodeCampus(DisbursementVoucherConstants.DocumentStatusCodes.APPROVED, campusCode);
        for (DisbursementVoucherDocument document : dvd) {
            addPayment(document, batch, processRunDate);
            count++;
            totalAmount = totalAmount.add(document.getDisbVchrCheckTotalAmount());
        }

        batch.setPaymentCount(new KualiInteger(count));
        batch.setPaymentTotalAmount(totalAmount);

        businessObjectService.save(batch);
        paymentFileEmailService.sendLoadEmail(batch);
    }

    /**
     * This method creates a payment group from the disbursement voucher and batch provided and persists that group to the database.
     * 
     * @param document The document used to build a payment group detail.
     * @param batch The batch file used to build a payment group and detail.
     * @param processRunDate The date the batch file is to post.
     */
    private void addPayment(DisbursementVoucherDocument document, Batch batch, Date processRunDate) {
        LOG.debug("addPayment() started");

        PaymentGroup pg = buildPaymentGroup(document, batch);
        PaymentDetail pd = buildPaymentDetail(document, batch, processRunDate);

        pd.setPaymentGroup(pg);
        pg.addPaymentDetails(pd);
        this.businessObjectService.save(pg);

        if (!testMode) {
            try {
                document.getDocumentHeader().setFinancialDocumentStatusCode(DisbursementVoucherConstants.DocumentStatusCodes.EXTRACTED);
                document.setExtractDate(new java.sql.Date(processRunDate.getTime()));
                SpringContext.getBean(DocumentService.class).saveDocument(document, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("Could not save disbursement voucher document #" + document.getDocumentNumber() + ": " + we);
                throw new RuntimeException(we);
            }
        }
    }

    /**
     * This method creates a PaymentGroup from the disbursement voucher and batch provided. The values provided by the disbursement
     * voucher are used to assign appropriate attributes to the payment group, including address and vendor detail information. The
     * information added to the payment group includes tax encoding to identify if taxes should be taken out of the payment. The tax
     * rules vary depending on the type of individual or entity being paid
     * 
     * @param document The document to be used for retrieving the information about the vendor being paid.
     * @param batch The batch that the payment group will be associated with.
     * @return A PaymentGroup object fully populated with all the values necessary to make a payment.
     */
    private PaymentGroup buildPaymentGroup(DisbursementVoucherDocument document, Batch batch) {
        LOG.debug("buildPaymentGroup() started");

        PaymentGroup pg = new PaymentGroup();
        pg.setBatch(batch);
        pg.setCombineGroups(Boolean.TRUE);
        pg.setCampusAddress(Boolean.FALSE);

        DisbursementVoucherPayeeDetail pd = document.getDvPayeeDetail();
        String rc = pd.getDisbVchrPaymentReasonCode();

        // If the payee is an employee, set these flags accordingly
        if ((document.getDvPayeeDetail().isVendor() && SpringContext.getBean(VendorService.class).isVendorInstitutionEmployee(pd.getDisbVchrVendorHeaderIdNumberAsInteger())) || document.getDvPayeeDetail().isEmployee()) {
            pg.setEmployeeIndicator(Boolean.TRUE);
            pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID);

            // All payments are taxable except research participant, rental & royalties
            pg.setTaxablePayment((!DisbursementVoucherConstants.PaymentReasonCodes.RESEARCH_PARTICIPANT.equals(rc)) && (!DisbursementVoucherConstants.PaymentReasonCodes.RENTAL_PAYMENT.equals(rc)) && (!DisbursementVoucherConstants.PaymentReasonCodes.ROYALTIES.equals(rc)));
        }
        else { // Payee is not an employee
            // Assume it is not taxable until proven otherwise
            pg.setTaxablePayment(Boolean.FALSE);

            // These are taxable
            VendorDetail vendDetail = SpringContext.getBean(VendorService.class).getVendorDetail(pd.getDisbVchrVendorHeaderIdNumberAsInteger(), pd.getDisbVchrVendorDetailAssignedIdNumberAsInteger());
            String vendorOwnerCode = vendDetail.getVendorHeader().getVendorOwnershipCode();
            String vendorOwnerCategoryCode = vendDetail.getVendorHeader().getVendorOwnershipCategoryCode();
            String payReasonCode = pd.getDisbVchrPaymentReasonCode();

            ParameterEvaluator parameterEvaluator1 = this.parameterService.getParameterEvaluator(DvToPdpExtractStep.class, PdpParameterConstants.TAXABLE_PAYMENT_REASON_CODES_BY_OWNERSHIP_CODES_PARAMETER_NAME, PdpParameterConstants.NON_TAXABLE_PAYMENT_REASON_CODES_BY_OWNERSHIP_CODES_PARAMETER_NAME, vendorOwnerCode, payReasonCode);
            ParameterEvaluator parameterEvaluator2 = this.parameterService.getParameterEvaluator(DvToPdpExtractStep.class, PdpParameterConstants.TAXABLE_PAYMENT_REASON_CODES_BY_CORPORATION_OWNERSHIP_TYPE_CATEGORY_PARAMETER_NAME, PdpParameterConstants.NON_TAXABLE_PAYMENT_REASON_CODES_BY_CORPORATION_OWNERSHIP_TYPE_CATEGORY_PARAMETER_NAME, vendorOwnerCategoryCode, payReasonCode);
            
            if ( parameterEvaluator1.evaluationSucceeds() ) pg.setTaxablePayment(Boolean.TRUE);
            
            else if (this.parameterService.getParameterValue(DvToPdpExtractStep.class, PdpParameterConstants.CORPORATION_OWNERSHIP_TYPE_PARAMETER_NAME).equals("CP") &&
                      StringUtils.isEmpty(vendorOwnerCategoryCode) &&
                      this.parameterService.getParameterEvaluator(DvToPdpExtractStep.class, PdpParameterConstants.TAXABLE_PAYMENT_REASON_CODES_FOR_BLANK_CORPORATION_OWNERSHIP_TYPE_CATEGORIES_PARAMETER_NAME, payReasonCode).evaluationSucceeds()) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
            
            else if (this.parameterService.getParameterValue(DvToPdpExtractStep.class, PdpParameterConstants.CORPORATION_OWNERSHIP_TYPE_PARAMETER_NAME).equals("CP")
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

        try {
            pg.setPaymentDate(dateTimeService.convertToSqlDate(batch.getFileProcessTimestamp()));
        }
        catch (ParseException e) {
            throw new RuntimeException("Unable to parse file process timestamp into sql date " + e.getMessage());
        }

        // It doesn't look like the DV has a way to do immediate processes
        pg.setProcessImmediate(Boolean.FALSE);
        pg.setPymtAttachment(document.isDisbVchrAttachmentCode());
        pg.setPymtSpecialHandling(document.isDisbVchrSpecialHandlingCode());
        pg.setNraPayment(pd.isDisbVchrAlienPaymentCode());

        pg.setBankCode(document.getDisbVchrBankCode());
        pg.setPaymentStatusCode(KFSConstants.PdpConstants.PAYMENT_OPEN_STATUS_CODE);

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
    private PaymentDetail buildPaymentDetail(DisbursementVoucherDocument document, Batch batch, Date processRunDate) {
        LOG.debug("buildPaymentDetail() started");

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
        for (Iterator iter = document.getSourceAccountingLines().iterator(); iter.hasNext();) {
            SourceAccountingLine sal = (SourceAccountingLine) iter.next();

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
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingLine1Address)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingLine1Address);
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingLine2Address)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingLine2Address);
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(dvSpecialHandlingCity)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(dvSpecialHandlingCity + ", " + dvSpecialHandlingState + " " + dvSpecialHandlingZip);
            pd.addNote(pnt);
        }
        if (document.isDisbVchrAttachmentCode()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Attachment Included");
            pd.addNote(pnt);
        }

        String paymentReasonCode = dvpd.getDisbVchrPaymentReasonCode();
        if (DisbursementVoucherConstants.PaymentReasonCodes.TRAVEL_NONEMPLOYEE.equals(paymentReasonCode) || DisbursementVoucherConstants.PaymentReasonCodes.TRAVEL_HONORARIUM.equals(paymentReasonCode)) {
            DisbursementVoucherNonEmployeeTravel dvnet = document.getDvNonEmployeeTravel();
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Reimbursement associated with " + dvnet.getDisbVchrServicePerformedDesc());
            pd.addNote(pnt);

            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("The total per diem amount for your daily expenses is " + dvnet.getDisbVchrPerdiemCalculatedAmt());
            pd.addNote(pnt);

            if (dvnet.getDisbVchrPersonalCarAmount() != null && dvnet.getDisbVchrPersonalCarAmount().compareTo(KualiDecimal.ZERO) != 0) {
                pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                pnt.setCustomerNoteText("The total dollar amount for your vehicle mileage is " + dvnet.getDisbVchrPersonalCarAmount());
                pd.addNote(pnt);

                for (Iterator iter = dvnet.getDvNonEmployeeExpenses().iterator(); iter.hasNext();) {
                    DisbursementVoucherNonEmployeeExpense exp = (DisbursementVoucherNonEmployeeExpense) iter.next();

                    if (line < (maxNoteLines - 8)) {
                        pnt = new PaymentNoteText();
                        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                        pnt.setCustomerNoteText(exp.getDisbVchrExpenseCompanyName() + " " + exp.getDisbVchrExpenseAmount());
                        pd.addNote(pnt);
                    }
                }
            }
        }
        else if (DisbursementVoucherConstants.PaymentReasonCodes.TRAVEL_PREPAID.equals(paymentReasonCode)) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Payment is for the following indviuals/charges:");
            pd.addNote(pnt);

            DisbursementVoucherPreConferenceDetail dvpcd = document.getDvPreConferenceDetail();

            for (Iterator iter = dvpcd.getDvPreConferenceRegistrants().iterator(); iter.hasNext();) {
                DisbursementVoucherPreConferenceRegistrant dvpcr = (DisbursementVoucherPreConferenceRegistrant) iter.next();

                if (line < (maxNoteLines - 8)) {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                    pnt.setCustomerNoteText(dvpcr.getDvConferenceRegistrantName() + " " + dvpcr.getDisbVchrExpenseAmount());
                    pd.addNote(pnt);
                }
            }
        }

        String text = document.getDisbVchrCheckStubText();
        if (text.length() > 0) {
            String[] lines = text.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                if (line < (maxNoteLines - 3)) {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
                    if (lines[i].length() > 90) {
                        pnt.setCustomerNoteText(lines[i].substring(0, 90));
                    }
                    else {
                        pnt.setCustomerNoteText(lines[i]);
                    }
                    pd.addNote(pnt);
                }
            }
        }
        return pd;
    }

    /**
     * This method creates a Batch instance and populates it with the information provided.
     * 
     * @param campusCode The campus code used to retrieve a customer profile to be set on the batch.
     * @param user The user who submitted the batch.
     * @param processRunDate The date the batch was submitted and the date the customer profile was generated.
     * @return A fully populated batch instance.
     */
    private Batch createBatch(String campusCode, Person user, Date processRunDate) {
        String orgCode = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DvPdpExtractGroup.DV_PDP_ORG_CODE);
        String subUnitCode = parameterService.getParameterValue(DisbursementVoucherDocument.class, DisbursementVoucherConstants.DvPdpExtractGroup.DV_PDP_SBUNT_CODE);
        CustomerProfile customer = customerProfileService.get(campusCode, orgCode, subUnitCode);
        if (customer == null) {
            throw new IllegalArgumentException("Unable to find customer profile for " + campusCode + "/" + orgCode + "/" + subUnitCode);
        }

        // Create the group for this campus
        Batch batch = new Batch();
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName(KFSConstants.DISBURSEMENT_VOUCHER_PDP_EXTRACT_FILE_NAME);
        batch.setSubmiterUser(user);

        // Set these for now, we will update them later
        batch.setPaymentCount(KualiInteger.ZERO);
        batch.setPaymentTotalAmount(KualiDecimal.ZERO);

        businessObjectService.save(batch);

        return batch;
    }

    /**
     * This method retrieves a collection of campus instances representing all the campuses which currently have disbursement
     * vouchers with the status code provided.
     * 
     * @param statusCode The status code to retrieve disbursement vouchers by.
     * @return A collection of campus codes of all the campuses with disbursement vouchers in the status given.
     */
    private Set<String> getCampusListByDocumentStatusCode(String statusCode) {
        LOG.debug("getCampusListByDocumentStatusCode() started");

        Set<String> campusSet = new HashSet<String>();

        Collection<DisbursementVoucherDocument> docs = disbursementVoucherDao.getDocumentsByHeaderStatus(statusCode);
        for (DisbursementVoucherDocument element : docs) {
            String dvdCampusCode = element.getCampusCode();
            campusSet.add(dvdCampusCode);
        }

        return campusSet;
    }

    /**
     * This method retrieves a list of disbursement voucher documents that are in the status provided for the campus code given.
     * 
     * @param statusCode The status of the disbursement vouchers to be retrieved.
     * @param campusCode The campus code that the disbursement vouchers will be associated with.
     * @return A collection of disbursement voucher objects that meet the search criteria given.
     */
    private Collection<DisbursementVoucherDocument> getListByDocumentStatusCodeCampus(String statusCode, String campusCode) {
        LOG.debug("getListByDocumentStatusCodeCampus() started");

        Collection<DisbursementVoucherDocument> list = new ArrayList<DisbursementVoucherDocument>();

        try {
            Collection<DisbursementVoucherDocument> docs = SpringContext.getBean(FinancialSystemDocumentService.class).findByDocumentHeaderStatusCode(DisbursementVoucherDocument.class, statusCode);
            for (DisbursementVoucherDocument element : docs) {
                String dvdCampusCode = element.getCampusCode();

                if (dvdCampusCode.equals(campusCode)) {
                    list.add(element);
                }
            }
        }
        catch (WorkflowException we) {
            LOG.error("Could not load Disbursement Voucher Documents with status code = " + statusCode + ": " + we);
            throw new RuntimeException(we);
        }

        return list;
    }

    /**
     * This cancels the disbursement voucher
     * 
     * @param dv the disbursement voucher document to cancel
     * @param processDate the date of the cancelation
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#cancelExtractedDisbursementVoucher(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public void cancelExtractedDisbursementVoucher(DisbursementVoucherDocument dv, java.sql.Date processDate) {
        if (dv.getCancelDate() == null) {
            try {
                BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
                // set the canceled date
                dv.setCancelDate(processDate);
                dv.refreshReferenceObject("generalLedgerPendingEntries");
                if (ObjectUtils.isNull(dv.getGeneralLedgerPendingEntries()) || dv.getGeneralLedgerPendingEntries().size() == 0) {
                    // generate all the pending entries for the document
                    SpringContext.getBean(GeneralLedgerPendingEntryService.class).generateGeneralLedgerPendingEntries(dv);
                    // for each pending entry, opposite-ify it and reattach it to the document
                    GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper = new GeneralLedgerPendingEntrySequenceHelper();
                    for (GeneralLedgerPendingEntry glpe : dv.getGeneralLedgerPendingEntries()) {
                        oppositifyEntry(glpe, boService, glpeSeqHelper);
                    }
                }
                else {
                    List<GeneralLedgerPendingEntry> newGLPEs = new ArrayList<GeneralLedgerPendingEntry>();
                    GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper = new GeneralLedgerPendingEntrySequenceHelper(dv.getGeneralLedgerPendingEntries().size() + 1);
                    for (GeneralLedgerPendingEntry glpe : dv.getGeneralLedgerPendingEntries()) {
                        glpe.refresh();
                        if (glpe.getFinancialDocumentApprovedCode().equals(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.PROCESSED)) {
                            // damn! it got processed! well, make a copy, oppositify, and save
                            GeneralLedgerPendingEntry undoer = new GeneralLedgerPendingEntry(glpe);
                            oppositifyEntry(undoer, boService, glpeSeqHelper);
                            newGLPEs.add(undoer);
                        }
                        else {
                            // just delete the GLPE before anything happens to it
                            boService.delete(glpe);
                        }
                    }
                    dv.setGeneralLedgerPendingEntries(newGLPEs);
                }
                // set the financial document status to canceled
                dv.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
                // save the document
                SpringContext.getBean(DocumentService.class).saveDocument(dv, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
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
    private void oppositifyEntry(GeneralLedgerPendingEntry glpe, BusinessObjectService boService, GeneralLedgerPendingEntrySequenceHelper glpeSeqHelper) {
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
     * This updates the disbursement voucher so that when it is re-extracted, information about it will be accurate
     * 
     * @param dv the disbursement voucher document to reset
     * @param processDate the date of the reseting
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#resetExtractedDisbursementVoucher(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public void resetExtractedDisbursementVoucher(DisbursementVoucherDocument dv, java.sql.Date processDate) {
        try {
            // 1. reset the extracted date
            dv.setExtractDate(null);
            dv.setPaidDate(null);
            // 2. save the doc
            SpringContext.getBean(DocumentService.class).saveDocument(dv, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + dv.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Looks up the document using document service, and deals with any nasty WorkflowException or ClassCastExceptions that pop up
     * 
     * @param documentNumber the number of the document to look up
     * @return the dv doc if found, or null otherwise
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#getDocumentById(java.lang.String)
     */
    public DisbursementVoucherDocument getDocumentById(String documentNumber) {
        DisbursementVoucherDocument dv = null;
        try {
            dv = (DisbursementVoucherDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentNumber);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to retrieve Disbursement Voucher: " + dv.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
        return dv;
    }

    /**
     * Marks the disbursement voucher as paid by setting its paid date
     * 
     * @param dv the dv document to mark as paid
     * @param processDate the date when the dv was paid
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#markDisbursementVoucherAsPaid(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public void markDisbursementVoucherAsPaid(DisbursementVoucherDocument dv, java.sql.Date processDate) {
        try {
            dv.setPaidDate(processDate);
            SpringContext.getBean(DocumentService.class).saveDocument(dv, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + dv.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
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
     * This method sets the ParameterService instance.
     * 
     * @param parameterService The ParameterService to be set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * This method sets the dateTimeService instance.
     * 
     * @param dateTimeService The DateTimeService to be set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * This method sets the personService instance.
     * 
     * @param personService The org.kuali.rice.kim.service.PersonService to be set.
     */
    public void setPersonService(org.kuali.rice.kim.service.PersonService personService) {
        this.personService = personService;
    }

    /**
     * This method sets the customerProfileService instance.
     * 
     * @param customerProfileService The CustomerProfileService to be set.
     */
    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    /**
     * This method sets the paymentFileService instance.
     * 
     * @param paymentFileService The PaymentFileService to be set.
     */
    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    /**
     * This method sets the paymentGroupService instance.
     * 
     * @param paymentGroupService The PaymentGroupService to be set.
     */
    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the paymentFileEmailService attribute value.
     * 
     * @param paymentFileEmailService The paymentFileEmailService to set.
     */
    public void setPaymentFileEmailService(PdpEmailService paymentFileEmailService) {
        this.paymentFileEmailService = paymentFileEmailService;
    }

}
