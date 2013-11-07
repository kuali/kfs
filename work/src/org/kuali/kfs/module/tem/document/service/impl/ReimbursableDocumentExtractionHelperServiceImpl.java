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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Helper class to help PDP extraction of Reimbursable travel & entertainment documents - namely, the Travel Reimbursement,
 * the entertainment document, and the moving and relocation document
 */
@Transactional
public class ReimbursableDocumentExtractionHelperServiceImpl implements PaymentSourceToExtractService<TEMReimbursementDocument> {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReimbursableDocumentExtractionHelperServiceImpl.class);
    protected DocumentService documentService;
    protected TravelerService travelerService;
    protected PaymentSourceHelperService paymentSourceHelperService;
    protected TravelDocumentDao travelDocumentDao;
    protected TravelPaymentsHelperService travelPaymentsHelperService;
    protected ParameterService parameterService;

    /**
     *
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#retrievePaymentSourcesByCampus(boolean)
     */
    @Override
    public Map<String, List<TEMReimbursementDocument>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        Map<String, List<TEMReimbursementDocument>> documentsByCampus = new HashMap<String, List<TEMReimbursementDocument>>();
        final List<TEMReimbursementDocument> reimbursables = retrieveAllApprovedReimbursableDocuments(immediatesOnly);
        Map<String, String> initiatorCampuses = new HashMap<String, String>();
        for (TEMReimbursementDocument document : reimbursables) {
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
        return documentsByCampus;
    }

    /**
     * Retrieves all the TravelReimbursement, TravelRelocation, and TravelEntertainment documents paid by check at approved status in one convenient call
     * @param immediatesOnly true if only those documents marked for immediate payment should be retrieved, false if all qualifying documents should be retrieved
     * @return all of the documents to process in a list
     */
    protected List<TEMReimbursementDocument> retrieveAllApprovedReimbursableDocuments(boolean immediatesOnly) {
        List<TEMReimbursementDocument> allReimbursables = new ArrayList<TEMReimbursementDocument>();
        allReimbursables.addAll(getTravelDocumentDao().getReimbursementDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly));
        allReimbursables.addAll(getTravelDocumentDao().getRelocationDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly));
        allReimbursables.addAll(getTravelDocumentDao().getEntertainmentDocumentsByHeaderStatus(KFSConstants.DocumentStatusCodes.APPROVED, immediatesOnly));
        return allReimbursables;
    }

    /**
     * Cancels the reimbursable travel & entertainment document
     * @see org.kuali.kfs.module.tem.document.service.ReimbursableDocumentPaymentService#cancelReimbursableDocument(org.kuali.kfs.module.tem.document.TEMReimbursementDocument, java.sql.Date)
     */
    @Override
    public void cancelPayment(TEMReimbursementDocument reimbursableDoc, Date cancelDate) {
        if (reimbursableDoc.getTravelPayment().getCancelDate() == null) {
            try {
                reimbursableDoc.getTravelPayment().setCancelDate(cancelDate);
                getPaymentSourceHelperService().handleEntryCancellation(reimbursableDoc);
                reimbursableDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.CANCELLED);
                // save the document
                getDocumentService().saveDocument(reimbursableDoc, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
            }
            catch (WorkflowException we) {
                LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + reimbursableDoc.getDocumentNumber() + " " + we);
                throw new RuntimeException(we);
            }
        }
    }

    /**
     * Creates a payment group for the reimbursable travel & entertainment document
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#createPaymentGroup(org.kuali.rice.krad.document.Document, java.sql.Date)
     */
    @Override
    public PaymentGroup createPaymentGroup(TEMReimbursementDocument reimbursableDoc, Date processRunDate) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("createPaymentGroupForReimbursable() started");
        }

        PaymentGroup pg = getTravelPaymentsHelperService().buildGenericPaymentGroup(reimbursableDoc.getTraveler(), reimbursableDoc.getTravelPayment(), reimbursableDoc.getFinancialDocumentBankCode());
        if (getTravelerService().isEmployee(reimbursableDoc.getTraveler())){
            pg.setPayeeId(reimbursableDoc.getTemProfile().getEmployeeId());
        }else{
            pg.setPayeeId(reimbursableDoc.getTraveler().getCustomerNumber());
        }

        // now add the payment detail
        final PaymentDetail paymentDetail = buildPaymentDetail(reimbursableDoc, processRunDate);
        pg.addPaymentDetails(paymentDetail);
        paymentDetail.setPaymentGroup(pg);

        return pg;
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

        PaymentDetail pd = getTravelPaymentsHelperService().buildGenericPaymentDetail(document.getDocumentHeader(), processRunDate, document.getTravelPayment(), getTravelPaymentsHelperService().getInitiator(document), document.getAchCheckDocumentType());
        // Handle accounts
        final List<PaymentAccountDetail> paymentAccounts = this.getTravelPaymentsHelperService().buildGenericPaymentAccountDetails(document.getSourceAccountingLines());
        for (PaymentAccountDetail pad : paymentAccounts) {
            pd.addAccountDetail(pad);
        }

        return pd;
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
     * Marks the extract date on the travel payment associated with the document
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#markAsExtracted(org.kuali.rice.krad.document.Document, java.sql.Date)
     */
    @Override
    public void markAsExtracted(TEMReimbursementDocument document, Date sqlProcessRunDate) {
        try {
            document.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.Payments.EXTRACTED);
            document.getTravelPayment().setExtractDate(sqlProcessRunDate);
            getDocumentService().saveDocument(document, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("Could not save TEMReimbursementDocument document #" + document.getDocumentNumber() + ": " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Returns the travel payment check total amount
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#getPaymentAmount(org.kuali.rice.krad.document.Document)
     */
    @Override
    public KualiDecimal getPaymentAmount(TEMReimbursementDocument document) {
        return document.getTravelPayment().getCheckTotalAmount();
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
     * Sets the canceled date on the TravelPayment
     * @see org.kuali.kfs.sys.document.PaymentSource#markAsPaid(java.sql.Date)
     */
    @Override
    public void markAsPaid(TEMReimbursementDocument doc, Date processDate) {
        try {
            doc.getTravelPayment().setPaidDate(processDate);
            getDocumentService().saveDocument(doc, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + doc.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Resets the extraction date and paid date to null; resets the document's financial status code to approved
     * @see org.kuali.kfs.sys.document.PaymentSource#resetFromExtraction()
     */
    @Override
    public void resetFromExtraction(TEMReimbursementDocument doc) {
        try {
            doc.getTravelPayment().setExtractDate(null);
            doc.getTravelPayment().setPaidDate(null);
            doc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
            getDocumentService().saveDocument(doc, AccountingDocumentSaveWithNoLedgerEntryGenerationEvent.class);
        }
        catch (WorkflowException we) {
            LOG.error("encountered workflow exception while attempting to save Disbursement Voucher: " + doc.getDocumentNumber() + " " + we);
            throw new RuntimeException(we);
        }
    }

    /**
     * Defers to the document to find out which
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#getAchCheckDocumentType(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public String getAchCheckDocumentType(TEMReimbursementDocument paymentSource) {
        return paymentSource.getAchCheckDocumentType();
    }

    /**
     * Handles ENCA, RECA, and TRCA
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#handlesAchCheckDocumentType(java.lang.String)
     */
    @Override
    public boolean handlesAchCheckDocumentType(String achCheckDocumentType) {
        return StringUtils.equals(achCheckDocumentType, TemConstants.TravelDocTypes.TRAVEL_REIMBURSEMENT_CHECK_ACH_DOCUMENT) || StringUtils.equals(achCheckDocumentType, TemConstants.TravelDocTypes.ENTERTAINMENT_CHECK_ACH_DOCUMENT) || StringUtils.equals(achCheckDocumentType, TemConstants.TravelDocTypes.RELOCATION_CHECK_ACH_DOCUMENT);
    }

    /**
     * Determines if the payment would be 0 - if it's greater than that, it should be extracted
     * @see org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService#shouldExtractPayment(org.kuali.kfs.sys.document.PaymentSource)
     */
    @Override
    public boolean shouldExtractPayment(TEMReimbursementDocument paymentSource) {
        return KualiDecimal.ZERO.isLessThan(getPaymentAmount(paymentSource));
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
     * @return an implementation of the TravelerService
     */
    public TravelerService getTravelerService() {
        return travelerService;
    }

    /**
     * Sets the implementation of the TravelerService for this service to use
     * @param parameterService an implementation of TravelerService
     */
    public void setTravelerService(TravelerService travelerService) {
        this.travelerService = travelerService;
    }

    /**
     * @return an implementation of the PaymentSourceHelperService
     */
    public PaymentSourceHelperService getPaymentSourceHelperService() {
        return paymentSourceHelperService;
    }

    /**
     * Sets the implementation of the PaymentSourceHelperService for this service to use
     * @param parameterService an implementation of PaymentSourceHelperService
     */
    public void setPaymentSourceHelperService(PaymentSourceHelperService paymentSourceHelperService) {
        this.paymentSourceHelperService = paymentSourceHelperService;
    }

    /**
     * @return an implementation of the DAO for TravelDocuments
     */
    public TravelDocumentDao getTravelDocumentDao() {
        return travelDocumentDao;
    }

    /**
     * Sets the implementation of the DAO for TravelDocuments for this service to use
     * @param parameterService an implementation of the data access object for travel documents
     */
    public void setTravelDocumentDao(TravelDocumentDao travelDocumentDao) {
        this.travelDocumentDao = travelDocumentDao;
    }

    /**
     * @return an implementation of the TravelPaymentsHelperService
     */
    public TravelPaymentsHelperService getTravelPaymentsHelperService() {
        return travelPaymentsHelperService;
    }

    /**
     * Sets the implementation of the TravelPaymentsHelperService for this service to use
     * @param travelPaymentsHelperService an implementation of the TravelPaymentsHelperService
     */
    public void setTravelPaymentsHelperService(TravelPaymentsHelperService travelPaymentsHelperService) {
        this.travelPaymentsHelperService = travelPaymentsHelperService;
    }

    /**
     * @return the injected implementation of the ParameterService
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Injects an implementation of the ParameterService
     * @param parameterService the implementation of the ParameterService
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
