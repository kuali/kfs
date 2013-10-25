/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableSensitiveData;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestView;
import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.util.PurApRelatedViews;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingAccountsPayableModuleServiceImpl implements PurchasingAccountsPayableModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableModuleServiceImpl.class);

    protected PurchaseOrderService purchaseOrderService;
    protected PurapService purapService;
    protected DocumentService documentService;
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#addAssignedAssetNumbers(java.lang.Integer,
     *      java.util.List)
     */
    @Override
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, String principalId, String noteText) {
        PurchaseOrderDocument document = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderNumber);

        try {
            Note assetNote = SpringContext.getBean(DocumentService.class).createNoteFromDocument(document, noteText);
            // set the initiator user info to the new note
            assetNote.setAuthorUniversalIdentifier(principalId);
            document.addNote(assetNote);
            SpringContext.getBean(NoteService.class).save(assetNote);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getPurchaseOrderInquiryUrl(java.lang.Integer)
     */
    @Override
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber) {
        PurchaseOrderDocument po = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderNumber);
        if (ObjectUtils.isNotNull(po)) {
            return "purapPurchaseOrder.do?methodToCall=docHandler&docId=" + po.getDocumentNumber() + "&command=displayDocSearchView";
        }
        else {
            return "";
        }
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getAllSensitiveDatas()
     */
    @Override
    public List<PurchasingAccountsPayableSensitiveData> getAllSensitiveDatas() {
        List<PurchasingAccountsPayableSensitiveData> sensitiveDatas = new ArrayList<PurchasingAccountsPayableSensitiveData>();
        Collection sensitiveDatasAsObjects = SpringContext.getBean(BusinessObjectService.class).findAll(SensitiveData.class);
        for (Object rm : sensitiveDatasAsObjects) {
            sensitiveDatas.add((PurchasingAccountsPayableSensitiveData) rm);
        }
        return sensitiveDatas;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getSensitiveDataByCode(java.lang.String)
     */
    @Override
    public PurchasingAccountsPayableSensitiveData getSensitiveDataByCode(String sensitiveDataCode) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("sensitiveDataCode", sensitiveDataCode);
        return SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(SensitiveData.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#isPurchasingBatchDocument(java.lang.String)
     */
    @Override
    public boolean isPurchasingBatchDocument(String documentTypeCode) {
        if (PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(documentTypeCode) || PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(documentTypeCode)) {
            return true;
        }
        return false;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#handlePurchasingBatchCancels(java.lang.String)
     */
    @Override
    public void handlePurchasingBatchCancels(String documentNumber, String documentTypeCode, boolean primaryCancel, boolean disbursedPayment) {
        LOG.info("Begin handlePurchasingBatchCancels(documentNumber=" + documentNumber + ", documentTypeCode=" + documentTypeCode + ", primaryCancel=" + primaryCancel + ", disbursedPayment=" + disbursedPayment);

        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        PaymentRequestService paymentRequestService = SpringContext.getBean(PaymentRequestService.class);
        CreditMemoService creditMemoService = SpringContext.getBean(CreditMemoService.class);

        String preqCancelNote = parameterService.getParameterValueAsString(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PDP_PREQ_CANCEL_NOTE);
        String preqResetNote = parameterService.getParameterValueAsString(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PDP_PREQ_RESET_NOTE);
        String cmCancelNote = parameterService.getParameterValueAsString(VendorCreditMemoDocument.class, PurapParameterConstants.PURAP_PDP_CM_CANCEL_NOTE);
        String cmResetNote = parameterService.getParameterValueAsString(VendorCreditMemoDocument.class, PurapParameterConstants.PURAP_PDP_CM_RESET_NOTE);

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
            VendorCreditMemoDocument cm = creditMemoService.getCreditMemoByDocumentNumber(documentNumber);
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
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#handlePurchasingBatchPaids(java.lang.String)
     */
    @Override
    public void handlePurchasingBatchPaids(String documentNumber, String documentTypeCode, Date processDate) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        PaymentRequestService paymentRequestService = SpringContext.getBean(PaymentRequestService.class);
        CreditMemoService creditMemoService = SpringContext.getBean(CreditMemoService.class);

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
            VendorCreditMemoDocument cm = creditMemoService.getCreditMemoByDocumentNumber(documentNumber);
            if (cm != null) {
                creditMemoService.markPaid(cm, processDate);
            }
            else {
                LOG.error("processPdpPaids() DOES NOT EXIST, CANNOT PROCESS - Credit Memo with doc type of " + documentTypeCode + " with id " + documentNumber);
            }
        }

    }

    /**
     * Retrieves the Requisition documents, pulls the PaymentRequestDocuments from their related document
     * views, and then adds up the total paid amount
     * @see org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService#getTotalPaidAmountToRequisitions(java.util.List)
     */
    @Override
    public KualiDecimal getTotalPaidAmountToRequisitions(List<String> documentNumbers) {
        KualiDecimal totalPaidAmountToRequests = KualiDecimal.ZERO;
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumbers);
        Collection<RequisitionDocument> reqses = businessObjectService.findMatching(RequisitionDocument.class, fieldValues);
        for (RequisitionDocument reqs : reqses) {
            try {
                PurApRelatedViews relatedviews = reqs.getRelatedViews();
                if (relatedviews != null && relatedviews.getRelatedPaymentRequestViews() != null && relatedviews.getRelatedPaymentRequestViews().size() > 0) {
                    List<PaymentRequestView> preqViews = relatedviews.getRelatedPaymentRequestViews();
                    for (PaymentRequestView preqView : preqViews) {
                        PaymentRequestDocument preqDocument;

                        preqDocument = (PaymentRequestDocument) documentService.getByDocumentHeaderId(preqView.getDocumentNumber());
                        if (preqDocument.getDocumentHeader().getWorkflowDocument().isFinal()) {
                            totalPaidAmountToRequests = totalPaidAmountToRequests.add(preqDocument.getVendorInvoiceAmount());
                        }
                    }
                }
            }
            catch (WorkflowException we) {
                throw new RuntimeException("Could not retrieve document to determine totals paid on Requisitions", we);
            }
        }
        return totalPaidAmountToRequests;
    }

    @Override
    public String getB2BUrlString() {
        return PurapConstants.B2B_URL_STRING;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

