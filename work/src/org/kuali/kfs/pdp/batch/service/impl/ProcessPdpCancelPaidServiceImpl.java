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
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.batch.service.ProcessPdpCancelPaidService;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.PaymentSourceExtractionService;
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
    protected PaymentSourceExtractionService dvExtractService;
    protected DocumentService documentService;

    /**
     * @see org.kuali.kfs.module.purap.service.ProcessPdpCancelPaidService#processPdpCancels()
     */
    @Override
    public void processPdpCancels() {
        LOG.debug("processPdpCancels() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        String organization = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String purapSubUnit = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);
        String dvSubUnit = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);

        List<String> subUnits = new ArrayList<String>();
        subUnits.add(purapSubUnit);
        subUnits.add(dvSubUnit);

        Iterator<PaymentDetail> details = paymentDetailService.getUnprocessedCancelledDetails(organization, subUnits);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            boolean primaryCancel = paymentDetail.getPrimaryCancelledPayment();
            boolean disbursedPayment = PdpConstants.PaymentStatusCodes.CANCEL_DISBURSEMENT.equals(paymentDetail.getPaymentGroup().getPaymentStatusCode());

            if(purchasingAccountsPayableModuleService.isPurchasingBatchDocument(documentTypeCode)) {
                purchasingAccountsPayableModuleService.handlePurchasingBatchCancels(documentNumber, documentTypeCode, primaryCancel, disbursedPayment);
            }
            else if (dvExtractService.getPaymentSourceCheckACHDocumentTypes().contains(documentTypeCode)) {
                try {
                    PaymentSource dv = (PaymentSource)getDocumentService().getByDocumentHeaderId(documentNumber);
                    if (dv != null) {
                        if (disbursedPayment || primaryCancel) {
                            dvExtractService.cancelExtractedPaymentSource(dv, processDate);
                        } else {
                            dvExtractService.resetExtractedPaymentSource(dv, processDate);
                        }
                    }
                } catch (WorkflowException we) {
                    throw new RuntimeException("Could not retrieve document #"+documentNumber, we);
                }
            }
            else {
                LOG.warn("processPdpCancels() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                continue;
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

        String organization = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_ORG_CODE);
        String purapSubUnit = parameterService.getParameterValueAsString(KfsParameterConstants.PURCHASING_BATCH.class, KFSParameterKeyConstants.PurapPdpParameterConstants.PURAP_PDP_SUB_UNIT_CODE);
        String dvSubUnit = parameterService.getParameterValueAsString(DisbursementVoucherDocument.class, KFSParameterKeyConstants.PdpExtractBatchParameters.PDP_SBUNT_CODE);

        List<String> subUnits = new ArrayList<String>();
        subUnits.add(purapSubUnit);
        subUnits.add(dvSubUnit);

        Iterator<PaymentDetail> details = paymentDetailService.getUnprocessedPaidDetails(organization, subUnits);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            if(purchasingAccountsPayableModuleService.isPurchasingBatchDocument(documentTypeCode)) {
                purchasingAccountsPayableModuleService.handlePurchasingBatchPaids(documentNumber, documentTypeCode, processDate);
            }
            else if (dvExtractService.getPaymentSourceCheckACHDocumentTypes().contains(documentTypeCode)) {
                try {
                    PaymentSource dv = (PaymentSource)getDocumentService().getByDocumentHeaderId(documentNumber);
                    dvExtractService.markPaymentSourceAsPaid(dv, processDate);
                } catch (WorkflowException we) {
                    throw new RuntimeException("Could not retrieve document #"+documentNumber, we);
                }
            }
            else {
                LOG.warn("processPdpPaids() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                continue;
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
     * Sets the dvExtractService attribute value.
     * @param dvExtractService The dvExtractService to set.
     */
    public void setDvExtractService(PaymentSourceExtractionService dvExtractService) {
        this.dvExtractService = dvExtractService;
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
