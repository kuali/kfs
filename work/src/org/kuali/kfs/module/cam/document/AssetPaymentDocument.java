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
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAccountingLineParser;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAssetDetail;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.businessobject.BarcodeInventoryErrorDetail;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.rule.event.SaveDocumentEvent;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.MaintenanceDocumentService;
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

    public AssetPaymentDocument() {
        super();
        this.setAssetPaymentAssetDetail(new TypedArrayList(AssetPaymentAssetDetail.class));
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
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#addSourceAccountingLine(org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) line;

        // Assigning the system date to a field is not being edited on the screen.
        assetPaymentDetail.setPaymentApplicationDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        super.addSourceAccountingLine(assetPaymentDetail);
    }

    /**
     * @see org.kuali.rice.kns.document.DocumentBase#postProcessSave(org.kuali.rice.kns.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (!(event instanceof SaveDocumentEvent)) { // don't lock until they route
            MaintenanceDocumentService maintenanceDocumentService = SpringContext.getBean(MaintenanceDocumentService.class);
            AssetService assetService = SpringContext.getBean(AssetService.class);

            maintenanceDocumentService.deleteLocks(this.getDocumentNumber());

            List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
            for (AssetPaymentAssetDetail assetPaymentAssetDetail : this.getAssetPaymentAssetDetail()) {
                maintenanceLocks.add(assetService.generateAssetLock(documentNumber, assetPaymentAssetDetail.getCapitalAssetNumber()));
            }
            maintenanceDocumentService.storeLocks(maintenanceLocks);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
        KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();

        // Update asset payment table with the approved asset detail records.
        if (workflowDocument.stateIsProcessed()) {
            SpringContext.getBean(AssetPaymentService.class).processApprovedAssetPayment(this);

            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(this.getDocumentNumber());
        }

        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(this.getDocumentNumber());
        }

        SpringContext.getBean(CapitalAssetBuilderModuleService.class).notifyRouteStatusChange(getDocumentHeader().getDocumentNumber(), getDocumentHeader().getFinancialDocumentStatusCode());
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
        for (AssetPaymentAssetDetail detail : this.getAssetPaymentAssetDetail())
            total = total.add(detail.getPreviousTotalCostAmount());

        return total;
    }
}