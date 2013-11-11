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
import java.util.Set;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.pdp.businessobject.Batch;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.kfs.pdp.service.PdpEmailService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
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

    protected DateTimeService dateTimeService;
    protected CustomerProfileService customerProfileService;
    protected BusinessObjectService businessObjectService;
    protected PdpEmailService paymentFileEmailService;
    protected PaymentSourceToExtractService<PaymentSource> paymentSourceToExtractService;
    protected DocumentService documentService;
    protected Set<String> checkAchFsloDocTypes;

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
        Map<String, List<PaymentSource>>  campusListMap = paymentSourceToExtractService.retrievePaymentSourcesByCampus(false);

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
        Map<String, List<PaymentSource>> documentsByCampus = paymentSourceToExtractService.retrievePaymentSourcesByCampus(true);
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
            if (getPaymentSourceToExtractService().shouldExtractPayment(document)) {
                addPayment(document, batch, processRunDate, false);
                count++;
                totalAmount = totalAmount.add(getPaymentSourceToExtractService().getPaymentAmount(document));
            }
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

        if (!documents.isEmpty()) {
            final PaymentSource firstPaymentSource = documents.get(0);


            Batch batch = createBatch(campusCode, principalId, processRunDate);
            Integer count = 0;
            KualiDecimal totalAmount = KualiDecimal.ZERO;

            for (PaymentSource document : documents) {
                if (getPaymentSourceToExtractService().shouldExtractPayment(document)) {
                    addPayment(document, batch, processRunDate, false);
                    count++;
                    totalAmount = totalAmount.add(getPaymentSourceToExtractService().getPaymentAmount(document));
                }
            }

            batch.setPaymentCount(new KualiInteger(count));
            batch.setPaymentTotalAmount(totalAmount);

            businessObjectService.save(batch);
            paymentFileEmailService.sendLoadEmail(batch);
        }
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
        PaymentGroup pg = getPaymentSourceToExtractService().createPaymentGroup(document, sqlProcessRunDate);
        if (pg != null) { // the payment source returned null instead of a PaymentGroup?  I guess it didn't want to be paid for some reason (for instance, a 0 amount document or doc which didn't have a travel advance, etc)
            pg.setBatch(batch);
            if (immediate) {
                pg.setProcessImmediate(Boolean.TRUE);
            }

            this.businessObjectService.save(pg);

            if (!testMode) {
                getPaymentSourceToExtractService().markAsExtracted(document, sqlProcessRunDate, pg.getId());
            }
        }
    }

    /**
     * This method creates a Batch instance and populates it with the information provided.
     *
     * @param campusCode The campus code used to retrieve a customer profile to be set on the batch.
     * @param orgCode the organization code used to retrieve a customer profile to be set on the batch.
     * @param subUnitCode the sub-unit code used to retrieve a customer profile to be set on the batch.
     * @param user The user who submitted the batch.
     * @param processRunDate The date the batch was submitted and the date the customer profile was generated.
     * @return A fully populated batch instance.
     */
    protected Batch createBatch(String campusCode, String principalId, Date processRunDate) {
        final String orgCode = getPaymentSourceToExtractService().getPreDisbursementCustomerProfileUnit();
        final String subUnitCode = getPaymentSourceToExtractService().getPreDisbursementCustomerProfileSubUnit();
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
     * Extracts a single DisbursementVoucherDocument
     * @see org.kuali.kfs.fp.batch.service.DisbursementVoucherExtractService#extractImmediatePayment(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void extractSingleImmediatePayment(PaymentSource paymentSource) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("extractImmediatePayment(DisbursementVoucherDocument) started");
        }
        if (getPaymentSourceToExtractService().shouldExtractPayment(paymentSource)) {
            final Date processRunDate = dateTimeService.getCurrentDate();
            final Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
            if (principal == null) {
                LOG.debug("extractPayments() Unable to find user " + KFSConstants.SYSTEM_USER);
                throw new IllegalArgumentException("Unable to find user " + KFSConstants.SYSTEM_USER);
            }

            Batch batch = createBatch(paymentSource.getCampusCode(), principal.getPrincipalId(), processRunDate);
            KualiDecimal totalAmount = KualiDecimal.ZERO;

            addPayment(paymentSource, batch, processRunDate, true);
            totalAmount = totalAmount.add(getPaymentSourceToExtractService().getPaymentAmount(paymentSource));

            batch.setPaymentCount(new KualiInteger(1));
            batch.setPaymentTotalAmount(totalAmount);

            businessObjectService.save(batch);
            paymentFileEmailService.sendPaymentSourceImmediateExtractEmail(paymentSource, getPaymentSourceToExtractService().getImmediateExtractEMailFromAddress(), getPaymentSourceToExtractService().getImmediateExtractEmailToAddresses());
        }
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
     * @return the injected implementation of the PaymentSourceToExtractService
     */
    public PaymentSourceToExtractService<PaymentSource> getPaymentSourceToExtractService() {
        return this.paymentSourceToExtractService;
    }

    /**
     * Sets the paymentSourceToExtractService so that PaymentSources can be run through the extraction process
     *
     * @param paymentSourceToExtractService the paymentSourceToExtractService implementation to use
     */
    public void setPaymentSourceToExtractService(PaymentSourceToExtractService<PaymentSource> paymentSourceToExtractService) {
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
