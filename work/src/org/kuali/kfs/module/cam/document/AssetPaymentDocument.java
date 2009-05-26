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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAccountingLineParser;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kew.dto.DocumentRouteStatusChangeDTO;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.TypedArrayList;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;

/**
 * Capital assets document class for the asset payment document
 */
public class AssetPaymentDocument extends AccountingDocumentBase implements Copyable, AmountTotaling {
    private static Logger LOG = Logger.getLogger(AssetPaymentDocument.class);

    private List<AssetPaymentAssetDetail> assetPaymentAssetDetail;
    private Long capitalAssetNumber;
    private boolean capitalAssetBuilderOriginIndicator;

    public AssetPaymentDocument() {
        super();
        this.setAssetPaymentAssetDetail(new TypedArrayList(AssetPaymentAssetDetail.class));
    }

    /**
     * Lock on purchase order document since post processor will update PO document by adding notes.
     * 
     * @see org.kuali.rice.kns.document.DocumentBase#getWorkflowEngineDocumentIdsToLock()
     */
    @Override
    public List<Long> getWorkflowEngineDocumentIdsToLock() {
        if (this.isCapitalAssetBuilderOriginIndicator()) {
            String poDocId = SpringContext.getBean(CapitalAssetBuilderModuleService.class).getCurrentPurchaseOrderDocumentNumber(this.getDocumentNumber());
            if (StringUtils.isNotBlank(poDocId)) {
                List<Long> documentIds = new ArrayList<Long>();
                documentIds.add(new Long(poDocId));
                return documentIds;
            }
        }
        return null;
    }

    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPostable) is a credit or a debit, in terms of GLPE generation
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }

    public boolean isCapitalAssetBuilderOriginIndicator() {
        return capitalAssetBuilderOriginIndicator;
    }

    public void setCapitalAssetBuilderOriginIndicator(boolean capitalAssetBuilderOriginIndicator) {
        this.capitalAssetBuilderOriginIndicator = capitalAssetBuilderOriginIndicator;
    }

    /**
     * This method...
     * 
     * @param assetPaymentAssetDetail
     */
    public void addAssetPaymentAssetDetail(AssetPaymentAssetDetail assetPaymentAssetDetail) {
        this.getAssetPaymentAssetDetail().add(assetPaymentAssetDetail);
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class<AssetPaymentDetail> getSourceAccountingLineClass() {
        return AssetPaymentDetail.class;
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            ArrayList<Long> capitalAssetNumbers = new ArrayList<Long>();
            for (AssetPaymentAssetDetail assetPaymentAssetDetail : this.getAssetPaymentAssetDetail()) {
                if (assetPaymentAssetDetail.getCapitalAssetNumber() != null) {
                    capitalAssetNumbers.add(assetPaymentAssetDetail.getCapitalAssetNumber());
                }
            }

            String documentTypeForLocking = CamsConstants.DocumentTypeName.ASSET_PAYMENT;
            if (this.isCapitalAssetBuilderOriginIndicator()) {
                documentTypeForLocking = CamsConstants.DocumentTypeName.ASSET_PAYMENT_FROM_CAB;
            }

            if (!this.getCapitalAssetManagementModuleService().storeAssetLocks(capitalAssetNumbers, this.getDocumentNumber(), documentTypeForLocking, null)) {
                throw new ValidationException("Asset " + capitalAssetNumbers.toString() + " is being locked by other documents.");
            }
        }
    }

    private CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#doRouteStatusChange()
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChangeDTO statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);
        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        // Update asset payment table with the approved asset detail records.
        if (workflowDocument.stateIsProcessed()) {
            SpringContext.getBean(AssetPaymentService.class).processApprovedAssetPayment(this);
        }

        // Remove asset lock when doc status change. We don't include stateIsFinal since document always go to 'processed' first.
        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved() || workflowDocument.stateIsProcessed()) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(this.getDocumentNumber(), null);
        }

        if (isCapitalAssetBuilderOriginIndicator()) {
            SpringContext.getBean(CapitalAssetBuilderModuleService.class).notifyRouteStatusChange(getDocumentHeader());
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#prepareForSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // This is an empty method in order to prevent kuali from generating a gl pending entry record.
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new AssetPaymentAccountingLineParser();
    }

    public List<AssetPaymentAssetDetail> getAssetPaymentAssetDetail() {
        return assetPaymentAssetDetail;
    }

    public void setAssetPaymentAssetDetail(List<AssetPaymentAssetDetail> assetPaymentAssetDetail) {
        this.assetPaymentAssetDetail = assetPaymentAssetDetail;
    }

    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }

    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    /**
     * calculates the total previous cost amount of all the assets in the document
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getAssetsTotalHistoricalCost() {
        KualiDecimal total = new KualiDecimal(0);
        if (this.getAssetPaymentAssetDetail().isEmpty())
            return new KualiDecimal(0);

        for (AssetPaymentAssetDetail detail : this.getAssetPaymentAssetDetail()) {
            KualiDecimal amount = (detail.getPreviousTotalCostAmount() == null ? new KualiDecimal(0) : detail.getPreviousTotalCostAmount());
            total = total.add(amount);
        }
        return total;
    }
}