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
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.dataaccess.TravelDocumentDao;
import org.kuali.kfs.module.tem.document.TEMReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.ReimbursableDocumentPaymentService;
import org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.kfs.sys.document.validation.event.AccountingDocumentSaveWithNoLedgerEntryGenerationEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;

/**
 * Helper class to help PDP extraction of Reimbursable travel & entertainment documents - namely, the Travel Reimbursement,
 * the entertainment document, and the moving and relocation document
 */
public class ReimbursableDocumentExtractionHelperServiceImpl implements PaymentSourceToExtractService, ReimbursableDocumentPaymentService {
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
    public Map<String, List<? extends PaymentSource>> retrievePaymentSourcesByCampus(boolean immediatesOnly) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("retrievePaymentSourcesByCampus() started");
        }

        Map<String, List<? extends PaymentSource>> documentsByCampus = new HashMap<String, List<? extends PaymentSource>>();
        final List<TEMReimbursementDocument> reimbursables = retrieveAllApprovedReimbursableDocuments(immediatesOnly);
        Map<String, String> initiatorCampuses = new HashMap<String, String>();
        for (TEMReimbursementDocument document : reimbursables) {
            final String campusCode = getTravelPaymentsHelperService().findCampusForDocument(document, initiatorCampuses);
            if (!StringUtils.isBlank(campusCode)) {
                List<TEMReimbursementDocument> documentsForCampus = (List<TEMReimbursementDocument>)documentsByCampus.get(campusCode);
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
    public void cancelReimbursableDocument(TEMReimbursementDocument reimbursableDoc, Date cancelDate) {
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
     * @see org.kuali.kfs.module.tem.document.service.ReimbursableDocumentPaymentService#createPaymentGroupForReimbursable(org.kuali.kfs.module.tem.document.TEMReimbursementDocument, java.sql.Date)
     */
    @Override
    public PaymentGroup createPaymentGroupForReimbursable(TEMReimbursementDocument reimbursableDoc, Date processRunDate) {
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
