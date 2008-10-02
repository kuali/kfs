/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.integration.purap.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.integration.purap.PurchasingAccountsPayableRestrictedMaterial;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.RestrictedMaterial;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.UrlFactory;

public class PurchasingAccountsPayableModuleServiceImpl implements PurchasingAccountsPayableModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableModuleServiceImpl.class);
    
    private PurchaseOrderService purchaseOrderService;
    private PurapService purapService;
    private DocumentService documentService;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#addAssignedAssetNumbers(java.lang.Integer, java.util.List)
     */
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, List<Integer> assetNumbers) {
        PurchaseOrderDocument document = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderNumber);
              
        // Create and add the note.
        String noteText = "Asset Numbers have been created for this document: ";
        for(int i=0; i < assetNumbers.size(); i++) {
            noteText += assetNumbers.get(i).toString();
            if(i < assetNumbers.size() - 1) {
                noteText += ", ";
            }
        }
        try {
            Note assetNote = documentService.createNoteFromDocument(document, noteText);
            document.addNote(assetNote);
        } catch ( Exception e ) {
            throw new RuntimeException(e);
        }
        
        // Save the document.
        purapService.saveDocumentNoValidation(document);
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getPurchaseOrderInquiryUrl(java.lang.Integer)
     */
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber) {
        
        String url = kualiConfigurationService.getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        
        Properties parameters = new Properties();
        parameters.setProperty(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        try {
            PurchaseOrderDocument currentDocument = purchaseOrderService.getCurrentPurchaseOrder(purchaseOrderNumber);
            parameters.setProperty("businessObjectClassName", PurchaseOrderDocument.class.getName());
            parameters.setProperty("documentNumber", currentDocument.getDocumentNumber());
        }
        catch (NullPointerException npe) {
            throw new RuntimeException("Could not find the current PO document" + npe);
        }
        url = UrlFactory.parameterizeUrl(url + "/" + KFSConstants.INQUIRY_ACTION, parameters);
        return url;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getAllRestrictedMaterials()
     */
    public List<PurchasingAccountsPayableRestrictedMaterial> getAllRestrictedMaterials() {
        List<PurchasingAccountsPayableRestrictedMaterial> restrictedMaterials = new ArrayList<PurchasingAccountsPayableRestrictedMaterial>();
        Collection restrictedMaterialsAsObjects = SpringContext.getBean(BusinessObjectService.class).findAll(RestrictedMaterial.class);
        for (Object rm: restrictedMaterialsAsObjects) {
            restrictedMaterials.add((PurchasingAccountsPayableRestrictedMaterial)rm);
        }
        return restrictedMaterials;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getRestrictedMaterialByCode(java.lang.String)
     */
    public PurchasingAccountsPayableRestrictedMaterial getRestrictedMaterialByCode(String restrictedMaterialCode) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("restrictedMaterialCode", restrictedMaterialCode);
        return (PurchasingAccountsPayableRestrictedMaterial)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(RestrictedMaterial.class, primaryKeys);
    }

    /**
     * 
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#isPurchasingBatchDocument(java.lang.String)
     */
    public boolean isPurchasingBatchDocument(String documentTypeCode) {
        if (PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(documentTypeCode) || PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(documentTypeCode)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#handlePurchasingBatchCancels(java.lang.String)
     */
    public void handlePurchasingBatchCancels(String documentNumber, String documentTypeCode, boolean primaryCancel, boolean disbursedPayment) {
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        PaymentRequestService paymentRequestService = SpringContext.getBean(PaymentRequestService.class);
        CreditMemoService creditMemoService = SpringContext.getBean(CreditMemoService.class);
        
        String preqCancelNote = parameterService.getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PDP_PREQ_CANCEL_NOTE);
        String preqResetNote = parameterService.getParameterValue(PaymentRequestDocument.class, PurapParameterConstants.PURAP_PDP_PREQ_RESET_NOTE);
        String cmCancelNote = parameterService.getParameterValue(CreditMemoDocument.class, PurapParameterConstants.PURAP_PDP_CM_CANCEL_NOTE);
        String cmResetNote = parameterService.getParameterValue(CreditMemoDocument.class, PurapParameterConstants.PURAP_PDP_CM_RESET_NOTE);
        
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
    }

    /**
     * 
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#handlePurchasingBatchPaids(java.lang.String)
     */
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
            CreditMemoDocument cm = creditMemoService.getCreditMemoByDocumentNumber(documentNumber);
            if (cm != null) {
                creditMemoService.markPaid(cm, processDate);
            }
            else {
                LOG.error("processPdpPaids() DOES NOT EXIST, CANNOT PROCESS - Credit Memo with doc type of " + documentTypeCode + " with id " + documentNumber);
            }
        }
        
    }

    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public PurapService getPurapService() {
        return purapService;
    }

    public void setPurapService(PurapService purapService) {
        this.purapService = purapService;
    }

    public KualiConfigurationService getKualiConfigurationService() {
        return kualiConfigurationService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

}
