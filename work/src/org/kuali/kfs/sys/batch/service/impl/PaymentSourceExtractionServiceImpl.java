/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.pdp.PdpParameterConstants;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PaymentFileService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This is the default implementation of the PaymentSourceExtractionService interface.
 */
@Transactional
public class PaymentSourceExtractionServiceImpl implements PaymentSourceExtractionService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentSourceExtractionServiceImpl.class);

    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected CustomerProfileService customerProfileService;
    protected PaymentFileService paymentFileService;
    protected PaymentGroupService paymentGroupService;
    protected BusinessObjectService businessObjectService;
    protected PdpEmailService paymentFileEmailService;
    protected PaymentSourceToExtractService paymentSourceToExtractService;
    protected DocumentService documentService;

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
    @Override
    public boolean extractPayments() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extractPayments() started");
        }
        final Date processRunDate = dateTimeService.getCurrentDate();

        final Principal uuser = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
        if (uuser == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("extractPayments() Unable to find user " + KFSConstants.SYSTEM_USER);
            }
            throw new IllegalArgumentException("Unable to find user " + KFSConstants.SYSTEM_USER);
        }

        // Get a list of campuses that have documents with an 'A' (approved) status.
        Map<String, List<? extends PaymentSource>>  campusListMap = paymentSourceToExtractService.retrievePaymentSourcesByCampus(false);

        if (campusListMap != null && !campusListMap.isEmpty()) {
            // Process each campus one at a time
            for (String campusCode : campusListMap.keySet()) {
                extractPaymentsForCampus(campusCode, uuser.getPrincipalId(), processRunDate, campusListMap.get(campusCode));
            }
        }

        return true;
    }

    /**
     * Pulls all disbursement vouchers with status of "A" and marked for immediate payment from the database and builds payment records for them
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#extractImmediatePayments()
     */
    @Override
    public void extractImmediatePayments() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extractImmediatePayments() started");
        }
        final Date processRunDate = dateTimeService.getCurrentDate();
        final Principal uuser = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
        if (uuser == null) {
            LOG.debug("extractPayments() Unable to find user " + KFSConstants.SYSTEM_USER);
            throw new IllegalArgumentException("Unable to find user " + KFSConstants.SYSTEM_USER);
        }

        // Get a list of campuses that have documents with an 'A' (approved) status.
        Map<String, List<? extends PaymentSource>> documentsByCampus = paymentSourceToExtractService.retrievePaymentSourcesByCampus(true);
        // Process each campus one at a time
        for (String campusCode : documentsByCampus.keySet()) {
            extractImmediatePaymentsForCampus(campusCode, uuser.getPrincipalId(), processRunDate, documentsByCampus.get(campusCode));
        }
    }

    /**
     * This method extracts all outstanding payments from all the disbursement vouchers in approved status for a given campus and
     * adds these payments to a batch file that is uploaded for processing.
     *
     * @param campusCode The id code of the campus the payments will be retrieved for.
     * @param user The user object used when creating the batch file to upload with outstanding payments.
     * @param processRunDate This is the date that the batch file is created, often this value will be today's date.
     */
    protected void extractPaymentsForCampus(String campusCode, String principalId, Date processRunDate, List<? extends PaymentSource> documents) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extractPaymentsForCampus() started for campus: " + campusCode);
        }

        Batch batch = createBatch(campusCode, principalId, processRunDate);
        Integer count = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        for (PaymentSource document : documents) {
            addPayment(document, batch, processRunDate, false);
            count++;
            totalAmount = totalAmount.add(document.getPaymentAmount());
        }

        batch.setPaymentCount(new KualiInteger(count));
        batch.setPaymentTotalAmount(totalAmount);

        businessObjectService.save(batch);
        paymentFileEmailService.sendLoadEmail(batch);
    }

    /**
     * Builds payment batch for Disbursement Vouchers marked as immediate
     * @param campusCode the campus code the disbursement vouchers should be associated with
     * @param user the user responsible building the payment batch (typically the System User, kfs)
     * @param processRunDate the time that the job to build immediate payments is run
     */
    protected void extractImmediatePaymentsForCampus(String campusCode, String principalId, Date processRunDate, List<? extends PaymentSource> documents) {
        LOG.debug("extractImmediatesPaymentsForCampus() started for campus: " + campusCode);

        Batch batch = createBatch(campusCode, principalId, processRunDate);
        Integer count = 0;
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        for (PaymentSource document : documents) {
            addPayment(document, batch, processRunDate, false);
            count++;
            totalAmount = totalAmount.add(document.getPaymentAmount());
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
    protected void addPayment(PaymentSource document, Batch batch, Date processRunDate, boolean immediate) {
        LOG.info("addPayment() started for document number=" + document.getDocumentNumber());

        final java.sql.Date sqlProcessRunDate = new java.sql.Date(processRunDate.getTime());
        PaymentGroup pg = document.generatePaymentGroup(sqlProcessRunDate);
        if (pg != null) { // the payment source returned null instead of a PaymentGroup?  I guess it didn't want to be paid for some reason (for instance, a 0 amount document)
            pg.setBatch(batch);
            if (immediate) {
                pg.setProcessImmediate(Boolean.TRUE);
            }

            this.businessObjectService.save(pg);

            if (!testMode) {
                try {
                    document.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.Payments.EXTRACTED);
                    document.markAsExtracted(sqlProcessRunDate);
                    getDocumentService().saveDocument(document, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
                }
                catch (WorkflowException we) {
                    LOG.error("Could not save disbursement voucher document #" + document.getDocumentNumber() + ": " + we);
                    throw new RuntimeException(we);
                }
            }
        }
    }

    /**
     * This method will take a word and simply chop into smaller
     * text segments that satisfy the limit requirements.  All words
     * brute force chopped, with no regard to preserving whole words.
     *
     * For example:
     *
     *      "Java is a fun programming language!"
     *
     * Might be chopped into:
     *
     *      "Java is a fun prog"
     *      "ramming language!"
     *
     * @param word The word that needs chopping
     * @param limit Number of character that should represent a chopped word
     * @return String [] of chopped words
     */
    protected String[] chopWord(String word, int limit) {
        StringBuilder builder = new StringBuilder();
        if (word != null && word.trim().length() > 0) {

            char[] chars = word.toCharArray();
            int index = 0;

            // First process all the words that fit into the limit.
            for (int i = 0; i < chars.length/limit; i++) {
                builder.append(String.copyValueOf(chars, index, limit));
                builder.append("\n");

                index += limit;
            }

            // Not all words will fit perfectly into the limit amount, so
            // calculate the modulus value to determine any remaining characters.
            int modValue =  chars.length%limit;
            if (modValue > 0) {
                builder.append(String.copyValueOf(chars, index, modValue));
            }

        }

        // Split the chopped words into individual segments.
        return builder.toString().split("\\n");
    }

    /**
     * This method creates a Batch instance and populates it with the information provided.
     *
     * @param campusCode The campus code used to retrieve a customer profile to be set on the batch.
     * @param user The user who submitted the batch.
     * @param processRunDate The date the batch was submitted and the date the customer profile was generated.
     * @return A fully populated batch instance.
     */
    protected Batch createBatch(String campusCode, String principalId, Date processRunDate) {
        final String orgCode = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_ORG_CODE);
        final String subUnitCode = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);
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
        batch.setSubmiterUserId(principalId);

        // Set these for now, we will update them later
        batch.setPaymentCount(KualiInteger.ZERO);
        batch.setPaymentTotalAmount(KualiDecimal.ZERO);

        businessObjectService.save(batch);

        return batch;
    }

    /**
     * This method retrieves a list of disbursement voucher documents that are in the status provided for the campus code given.
     *
     * @param statusCode The status of the disbursement vouchers to be retrieved.
     * @param campusCode The campus code that the disbursement vouchers will be associated with.
     * @param immediatesOnly only retrieve Disbursement Vouchers marked for immediate payment
     * @return A collection of disbursement voucher objects that meet the search criteria given.
     */
    protected Collection<DisbursementVoucherDocument> getListByDocumentStatusCodeCampus(String statusCode, String campusCode, boolean immediatesOnly) {
        LOG.info("getListByDocumentStatusCodeCampus(statusCode=" + statusCode + ", campusCode=" + campusCode + ", immediatesOnly=" + immediatesOnly + ") started");

        Collection<DisbursementVoucherDocument> list = new ArrayList<DisbursementVoucherDocument>();

        try {
            Collection<DisbursementVoucherDocument> docs = SpringContext.getBean(FinancialSystemDocumentService.class).findByDocumentHeaderStatusCode(DisbursementVoucherDocument.class, statusCode);
            for (DisbursementVoucherDocument element : docs) {
                String dvdCampusCode = element.getCampusCode();

                if (dvdCampusCode.equals(campusCode) && KFSConstants.PaymentSourceConstants.PAYMENT_METHOD_CHECK.equals(element.getDisbVchrPaymentMethodCode())) {
                    if ((immediatesOnly && element.isImmediatePaymentIndicator()) || !immediatesOnly) {
                        list.add(element);
                    }
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
    @Override
    public void cancelExtractedPaymentSource(PaymentSource paymentSource, java.sql.Date processDate) {
        paymentSource.cancelPayment(processDate);
    }

    /**
     * This updates the disbursement voucher so that when it is re-extracted, information about it will be accurate
     *
     * @param dv the disbursement voucher document to reset
     * @param processDate the date of the reseting
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#resetExtractedDisbursementVoucher(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void resetExtractedPaymentSource(PaymentSource paymentSource, java.sql.Date processDate) {
        try {
            paymentSource.resetFromExtraction();
            SpringContext.getBean(DocumentService.class).saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Marks the disbursement voucher as paid by setting its paid date
     *
     * @param dv the dv document to mark as paid
     * @param processDate the date when the dv was paid
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#markDisbursementVoucherAsPaid(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void markPaymentSourceAsPaid(PaymentSource paymentSource, java.sql.Date processDate) {
        try {
            paymentSource.markAsPaid(processDate);
            SpringContext.getBean(DocumentService.class).saveDocument(paymentSource, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + paymentSource.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }



    /**
     * Extracts a single DisbursementVoucherDocument
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#extractImmediatePayment(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void extractSingleImmediatePayment(PaymentSource paymentSource) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extractImmediatePayment(DisbursementVoucherDocument) started");
        }
        final Date processRunDate = dateTimeService.getCurrentDate();
        final Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
        if (principal == null) {
            LOG.debug("extractPayments() Unable to find user " + KFSConstants.SYSTEM_USER);
            throw new IllegalArgumentException("Unable to find user " + KFSConstants.SYSTEM_USER);
        }

        Batch batch = createBatch(paymentSource.getCampusCode(), principal.getPrincipalId(), processRunDate);
        KualiDecimal totalAmount = KualiDecimal.ZERO;

        addPayment(paymentSource, batch, processRunDate, true);
        totalAmount = totalAmount.add(paymentSource.getPaymentAmount());

        batch.setPaymentCount(new KualiInteger(1));
        batch.setPaymentTotalAmount(totalAmount);

        businessObjectService.save(batch);
        if (paymentSource instanceof DisbursementVoucherDocument) {
            // TODO should check this!!!
            paymentFileEmailService.sendDisbursementVoucherImmediateExtractEmail((DisbursementVoucherDocument)paymentSource);
        }
    }

    /**
     *
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService#buildNoteForCheckStubText(java.lang.String)
     */
    @Override
    public PaymentNoteText buildNoteForCheckStubText(String text, int previousLineCount) {
        PaymentNoteText pnt = null;
        final String maxNoteLinesParam = parameterService.getParameterValueAsString(KfsParameterConstants.PRE_DISBURSEMENT_ALL.class, PdpParameterConstants.MAX_NOTE_LINES);

        int maxNoteLines;
        try {
            maxNoteLines = Integer.parseInt(maxNoteLinesParam);
        }
        catch (NumberFormatException nfe) {
            throw new IllegalArgumentException("Invalid Max Notes Lines parameter, value: "+maxNoteLinesParam+" cannot be converted to an integer");
        }

        // The WordUtils should be sufficient for the majority of cases.  This method will
        // word wrap the whole string based on the MAX_NOTE_LINE_SIZE, separating each wrapped
        // word by a newline character.  The 'wrap' method adds line feeds to the end causing
        // the character length to exceed the max length by 1, hence the need for the replace
        // method before splitting.
        String   wrappedText = WordUtils.wrap(text, KFSConstants.MAX_NOTE_LINE_SIZE);
        String[] noteLines   = wrappedText.replaceAll("[\r]", "").split("\\n");

        // Loop through all the note lines.
        for (String noteLine : noteLines) {
            if (previousLineCount < (maxNoteLines - 3) && !StringUtils.isEmpty(noteLine)) {

                // This should only happen if we encounter a word that is greater than the max length.
                // The only concern I have for this occurring is with URLs/email addresses.
                if (noteLine.length() >KFSConstants.MAX_NOTE_LINE_SIZE) {
                    for (String choppedWord : chopWord(noteLine, KFSConstants.MAX_NOTE_LINE_SIZE)) {

                        // Make sure we're still under the maximum number of note lines.
                        if (previousLineCount < (maxNoteLines - 3) && !StringUtils.isEmpty(choppedWord)) {
                            pnt = new PaymentNoteText();
                            pnt.setCustomerNoteLineNbr(new KualiInteger(previousLineCount++));
                            pnt.setCustomerNoteText(choppedWord.replaceAll("\\n", "").trim());
                        }
                        // We can't add any additional note lines, or we'll exceed the maximum, therefore
                        // just break out of the loop early - there's nothing left to do.
                        else {
                            break;
                        }
                    }
                }
                // This should be the most common case.  Simply create a new PaymentNoteText,
                // add the line at the correct line location.
                else {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(new KualiInteger(previousLineCount++));
                    pnt.setCustomerNoteText(noteLine.replaceAll("\\n", "").trim());
                }
            }
        }
        return pnt;
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

    /**
     * Sets the paymentSourceToExtractService so that PaymentSources can be run through the extraction process
     *
     * @param paymentSourceToExtractService the paymentSourceToExtractService implementation to use
     */
    public void setPaymentSourceToExtractService(PaymentSourceToExtractService paymentSourceToExtractService) {
        this.paymentSourceToExtractService = paymentSourceToExtractService;
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
}
