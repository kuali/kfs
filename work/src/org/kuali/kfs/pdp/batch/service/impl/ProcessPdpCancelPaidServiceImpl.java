/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.pdp.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.ProcessPdpCancelPaidService;
import org.kuali.kfs.pdp.businessobject.ExtractionUnit;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceToExtractService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.PaymentSource;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ProcessPdpCancelPaidService
 */
@Transactional
public class ProcessPdpCancelPaidServiceImpl implements ProcessPdpCancelPaidService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessPdpCancelPaidServiceImpl.class);

    protected PaymentGroupService paymentGroupService;
    protected PaymentDetailService paymentDetailService;
    protected ParameterService parameterService;
    protected DateTimeService dateTimeService;
    protected PurchasingAccountsPayableModuleService purchasingAccountsPayableModuleService;
    protected DocumentService documentService;

    protected volatile Set<String> paymentSourceCheckACHDocumentTypes;
    protected volatile List<PaymentSourceToExtractService<PaymentSource>> paymentSourceToExtractServices;

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpCancels()
     */
    @Override
    public void processPdpCancels() {
        LOG.debug("processPdpCancels() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        final List<ExtractionUnit> extractionUnits = getExtractionUnits();
        Iterator<PaymentDetail> details = paymentDetailService.getUnprocessedCancelledDetails(extractionUnits);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            boolean primaryCancel = paymentDetail.getPrimaryCancelledPayment();
            boolean disbursedPayment = PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT.equals(paymentDetail.getPaymentGroup().getPaymentStatusCode());

            if(purchasingAccountsPayableModuleService.isPurchasingBatchDocument(documentTypeCode)) {
                purchasingAccountsPayableModuleService.handlePurchasingBatchCancels(documentNumber, documentTypeCode, primaryCancel, disbursedPayment);
            }
            else {
                PaymentSourceToExtractService<PaymentSource> extractService = getPaymentSourceToExtractService(paymentDetail);
                if (extractService != null) {
                    try {
                        PaymentSource dv = (PaymentSource)getDocumentService().getByDocumentHeaderId(documentNumber);
                        if (dv != null) {
                            if (disbursedPayment || primaryCancel) {
                                extractService.cancelPayment(dv, processDate);
                            } else {
                                extractService.resetFromExtraction(dv);
                            }
                        }
                    } catch (WorkflowException we) {
                        throw new RuntimeException("Could not retrieve document #"+documentNumber, we);
                    }
                } else {
                    LOG.warn("processPdpCancels() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                    continue;
                }
            }

            paymentGroupService.processCancelledGroup(paymentDetail.getPaymentGroup(), processDate);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpPaids()
     */
    @Override
    public void processPdpPaids() {
        LOG.debug("processPdpPaids() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        final List<ExtractionUnit> extractionUnits = getExtractionUnits();
        Iterator<PaymentDetail> details = paymentDetailService.getUnprocessedPaidDetails(extractionUnits);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            if(purchasingAccountsPayableModuleService.isPurchasingBatchDocument(documentTypeCode)) {
                purchasingAccountsPayableModuleService.handlePurchasingBatchPaids(documentNumber, documentTypeCode, processDate);
            }
            else {
                PaymentSourceToExtractService<PaymentSource> extractService = getPaymentSourceToExtractService(paymentDetail);
                if (extractService != null) {
                    try {
                        PaymentSource dv = (PaymentSource)getDocumentService().getByDocumentHeaderId(documentNumber);
                        extractService.markAsPaid(dv, processDate);
                    } catch (WorkflowException we) {
                        throw new RuntimeException("Could not retrieve document #"+documentNumber, we);
                    }
                } else {
                    LOG.warn("processPdpPaids() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                    continue;
                }
            }

            paymentGroupService.processPaidGroup(paymentDetail.getPaymentGroup(), processDate);
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpCancelsAndPaids()
     */
    @Override
    public void processPdpCancelsAndPaids() {
        LOG.debug("processPdpCancelsAndPaids() started");

        processPdpCancels();
        processPdpPaids();
    }

    /**
     * @return a List of all available PaymentSourceToExtractService implementations
     */
    protected List<PaymentSourceToExtractService<PaymentSource>> getPaymentSourceToExtractServices() {
        if (paymentSourceToExtractServices == null) {
            paymentSourceToExtractServices = new ArrayList<PaymentSourceToExtractService<PaymentSource>>();
            Map<String, PaymentSourceToExtractService> extractionServices = SpringContext.getBeansOfType(PaymentSourceToExtractService.class);
            for (PaymentSourceToExtractService<PaymentSource> extractionService : extractionServices.values()) {
                paymentSourceToExtractServices.add(extractionService);
            }
        }
        return paymentSourceToExtractServices;
    }

    /**
     * Looks up the PaymentSourceToExtractService which can act upon the given PaymentDetail, based on the PaymentDetail's document type
     * @param paymentDetail the payment detail to find an extraction service to act upon
     * @return the matching PaymentSourceToExtractService, or null if a matching service could not be found (which would be weird, because _something_ created this PaymentDetail, but...whatever)
     */
    protected PaymentSourceToExtractService<PaymentSource> getPaymentSourceToExtractService(PaymentDetail paymentDetail) {
        for (PaymentSourceToExtractService<PaymentSource> extractionService : getPaymentSourceToExtractServices()) {
            if (extractionService.handlesAchCheckDocumentType(paymentDetail.getFinancialDocumentTypeCode())) {
                return extractionService;
            }
        }
        return null;
    }

    /**
     * Loops through the PaymentSourceToExtractService List and builds ExtractionUnits for each
     * @return a List of ExtractionUnits for each customer profile organization and sub-organization handled by PaymentSourceToExtractServices
     */
    protected List<ExtractionUnit> getExtractionUnitsForPaymentSourceToExtractServices() {
        List<ExtractionUnit> extractionUnits = new ArrayList<ExtractionUnit>();
        for (PaymentSourceToExtractService<PaymentSource> extractionService : getPaymentSourceToExtractServices()) {
            final ExtractionUnit extractionUnit = new ExtractionUnit(extractionService.getPreDisbursementCustomerProfileUnit(), extractionService.getPreDisbursementCustomerProfileSubUnit());
            if (!extractionUnits.contains(extractionUnit)) {
                extractionUnits.add(extractionUnit);
            }
        }
        return extractionUnits;
    }

    /**
     * @return a List of all known ExtractionUnits
     */
    protected List<ExtractionUnit> getExtractionUnits() {
        List<ExtractionUnit> extractionUnits = getExtractionUnitsForPaymentSourceToExtractServices();
        final String purapOrg = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        final String purapSubUnit = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);
        final ExtractionUnit purapExtractionUnit = new ExtractionUnit(purapOrg, purapSubUnit);
        extractionUnits.add(purapExtractionUnit);
        return Collections.unmodifiableList(extractionUnits);
    }

    public void setPaymentDetailService(PaymentDetailService paymentDetailService) {
        this.paymentDetailService = paymentDetailService;
    }

    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPurchasingAccountsPayableModuleService(PurchasingAccountsPayableModuleService purchasingAccountsPayableModuleService) {
        this.purchasingAccountsPayableModuleService = purchasingAccountsPayableModuleService;
    }

    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    /**
     * @return the implementation of the DocumentService to use
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the implementation of the DocumentService to use
     * @param documentService the implementation of the DocumentService to use
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

}
