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

import org.kuali.kfs.integration.businessobject.PurchasingAccountsPayableItemBuyerAndSellerSummary;
import org.kuali.kfs.integration.businessobject.PurchasingAccountsPayableItemCostSummary;
import org.kuali.kfs.integration.businessobject.purap.PurchasingAccountsPayableRestrictedMaterial;
import org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.businessobject.RestrictedMaterial;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * 
 */
public class PurchasingAccountsPayableModuleServiceImpl implements PurchasingAccountsPayableModuleService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingAccountsPayableModuleServiceImpl.class);

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#addAssignedAssetNumbers(java.lang.Integer, java.util.List)
     */
    public void addAssignedAssetNumbers(Integer purchaseOrderNumber, List<Integer> assetNumbers) {
        // TODO Auto-generated method stub
        
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getItemBuyerAndSellerSummarys(java.util.List, java.util.List, java.util.List, java.util.Date, org.kuali.rice.kns.util.KualiDecimal)
     */
    public List<PurchasingAccountsPayableItemBuyerAndSellerSummary> getItemBuyerAndSellerSummarys(List<String> chartCodes, List<String> objectSubTypeCodes, List<String> subFundGroupCodes, Date purchaseOrderOpenAsOfDate, KualiDecimal capitalizationLimit) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getItemCostSummarys(java.util.Date)
     */
    public List<PurchasingAccountsPayableItemCostSummary> getItemCostSummarys(Date purchaseOrderOpenAsOfDate) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#getPurchaseOrderInquiryUrl(java.lang.Integer)
     */
    public String getPurchaseOrderInquiryUrl(Integer purchaseOrderNumber) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see org.kuali.kfs.integration.service.PurchasingAccountsPayableModuleService#populateAssetBuilderInformation()
     */
    public void populateAssetBuilderInformation() {
        // TODO Auto-generated method stub
        
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

}
