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
package org.kuali.module.purap.service.impl;

import java.sql.Date;
import java.util.Iterator;

import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.service.PaymentDetailService;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapParameterConstants;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.service.CreditMemoService;
import org.kuali.module.purap.service.PaymentRequestService;
import org.kuali.module.purap.service.ProcessPdpCancelPaidService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ProcessPdpCancelPaidService
 */
@Transactional
public class ProcessPdpCancelPaidServiceImpl implements ProcessPdpCancelPaidService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ProcessPdpCancelPaidServiceImpl.class);

    private PaymentGroupService paymentGroupService;
    private PaymentDetailService paymentDetailService;
    private PaymentRequestService paymentRequestService;
    private CreditMemoService creditMemoService;
    private ParameterService parameterService;
    private DateTimeService dateTimeService;

    /**
     * @see org.kuali.module.purap.service.ProcessPdpCancelPaidService#processPdpCancels()
     */
    public void processPdpCancels() {
        LOG.debug("processPdpCancels() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        String organization = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnit = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);

        String preqCancelNote = parameterService.getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PDP_PREQ_CANCEL_NOTE);
        String preqResetNote = parameterService.getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PDP_PREQ_RESET_NOTE);
        String cmCancelNote = parameterService.getParameterValue(CreditMemoDocument.class, PurapParameterConstants.PURAP_PDP_CM_CANCEL_NOTE);
        String cmResetNote = parameterService.getParameterValue(CreditMemoDocument.class, PurapParameterConstants.PURAP_PDP_CM_RESET_NOTE);

        Iterator details = paymentDetailService.getUnprocessedCancelledDetails(organization, subUnit);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = (PaymentDetail) details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            boolean primaryCancel = paymentDetail.getPrimaryCancelledPayment();
            boolean disbursedPayment = PdpConstants.PaymentStatusCodes.CANCEL_PAYMENT.equals(paymentDetail.getPaymentGroup().getPaymentStatusCode());

            if (PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(documentTypeCode)) {
                PaymentRequestDocument pr = paymentRequestService.getPaymentRequestByDocumentNumber(documentNumber);
                if (pr != null) {
                    if (disbursedPayment || primaryCancel) {
                        paymentRequestService.cancelExtractedPaymentRequest(pr, preqCancelNote);
                    }
                    else {
                        paymentRequestService.resetExtractedPaymentRequest(pr, preqResetNote);
                    }
                }
                else {
                    LOG.error("processPdpCancels() DOES NOT EXIST, CANNOT PROCESS - Payment Request with doc type of " + documentTypeCode + " with id " + documentNumber);
                }
            }
            else if (PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(documentTypeCode)) {
                CreditMemoDocument cm = creditMemoService.getCreditMemoByDocumentNumber(documentNumber);
                if (cm != null) {
                    if (disbursedPayment || primaryCancel) {
                        creditMemoService.cancelExtractedCreditMemo(cm, cmCancelNote);
                    }
                    else {
                        creditMemoService.resetExtractedCreditMemo(cm, cmResetNote);
                    }
                }
                else {
                    LOG.error("processPdpCancels() DOES NOT EXIST, CANNOT PROCESS - Credit Memo with doc type of " + documentTypeCode + " with id " + documentNumber);
                }
            }
            else {
                LOG.error("processPdpCancels() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                throw new IllegalArgumentException("Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
            }

            paymentGroupService.processCancelledGroup(paymentDetail.getPaymentGroup(), processDate);
        }
    }

    /**
     * @see org.kuali.module.purap.service.ProcessPdpCancelPaidService#processPdpPaids()
     */
    public void processPdpPaids() {
        LOG.debug("processPdpPaids() started");

        Date processDate = dateTimeService.getCurrentSqlDate();

        String organization = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_ORG_CODE);
        String subUnit = parameterService.getParameterValue(ParameterConstants.PURCHASING_BATCH.class, PurapParameterConstants.PURAP_PDP_EPIC_SBUNT_CODE);

        Iterator details = paymentDetailService.getUnprocessedPaidDetails(organization, subUnit);
        while (details.hasNext()) {
            PaymentDetail paymentDetail = (PaymentDetail) details.next();

            String documentTypeCode = paymentDetail.getFinancialDocumentTypeCode();
            String documentNumber = paymentDetail.getCustPaymentDocNbr();

            if (PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(documentTypeCode)) {
                PaymentRequestDocument pr = paymentRequestService.getPaymentRequestByDocumentNumber(documentNumber);
                if (pr != null) {
                    paymentRequestService.markPaid(pr, processDate);
                }
                else {
                    LOG.error("processPdpPaids() DOES NOT EXIST, CANNOT MARK - Payment Request with doc type of " + documentTypeCode + " with id " + documentNumber);
                }
            }
            else if (PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(documentTypeCode)) {
                CreditMemoDocument cm = creditMemoService.getCreditMemoByDocumentNumber(documentNumber);
                if (cm != null) {
                    creditMemoService.markPaid(cm, processDate);
                }
                else {
                    LOG.error("processPdpPaids() DOES NOT EXIST, CANNOT PROCESS - Credit Memo with doc type of " + documentTypeCode + " with id " + documentNumber);
                }
            }
            else {
                LOG.error("processPdpPaids() Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
                throw new IllegalArgumentException("Unknown document type (" + documentTypeCode + ") for document ID: " + documentNumber);
            }

            paymentGroupService.processPaidGroup(paymentDetail.getPaymentGroup(), processDate);
        }
    }

    /**
     * @see org.kuali.module.purap.service.ProcessPdpCancelPaidService#processPdpCancelsAndPaids()
     */
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

    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setPaymentRequestService(PaymentRequestService paymentRequestService) {
        this.paymentRequestService = paymentRequestService;
    }

    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }
}
