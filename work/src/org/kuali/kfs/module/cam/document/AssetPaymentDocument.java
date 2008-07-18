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

import org.apache.log4j.Logger;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.SaveDocumentEvent;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.MaintenanceDocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentAccountingLineParser;
import org.kuali.kfs.module.cam.businessobject.AssetPaymentDetail;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.businessobject.AccountingLineParser;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;

/**
 * 
 * Capital assets document class for the asset payment document
 * 
 */
public class AssetPaymentDocument extends AccountingDocumentBase implements Copyable, AmountTotaling {
    private static Logger LOG = Logger.getLogger(AssetPaymentDocument.class);

    private Long capitalAssetNumber;
    private Integer nextCapitalAssetPaymentLineNumber;
    KualiDecimal previousTotalCostAmount;
    
    private List<AssetPaymentDetail> assetPaymentDetail;
    private Asset asset;

    public AssetPaymentDocument() {
        super();
        assetPaymentDetail = new ArrayList<AssetPaymentDetail>();      
    }

    /**
     * Determines if the given AccountingLine (as a GeneralLedgerPostable) is a credit or a debit, in terms of GLPE generation
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#isDebit(org.kuali.kfs.bo.GeneralLedgerPostable)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        return false;
    }


    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class<AssetPaymentDetail> getSourceAccountingLineClass() {
        return AssetPaymentDetail.class;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#addSourceAccountingLine(org.kuali.kfs.sys.businessobject.SourceAccountingLine)
     */
    @Override
    public void addSourceAccountingLine(SourceAccountingLine line) {
        AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) line;

        //Assigning the line number to the just added accounting line
        //assetPaymentDetail.setFinancialDocumentLineNumber(this.getNextSourceLineNumber());
        assetPaymentDetail.setSequenceNumber(this.getNextSourceLineNumber());
        
        //Assigning the system date to a field is not being edited on the screen.
        assetPaymentDetail.setPaymentApplicationDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());

        line = (SourceAccountingLine) assetPaymentDetail;

        this.sourceAccountingLines.add(line);
        this.nextSourceLineNumber = new Integer(this.getNextSourceLineNumber().intValue() + 1);        
        this.setNextCapitalAssetPaymentLineNumber(this.nextSourceLineNumber);
    }

    /**
     * @see org.kuali.core.document.DocumentBase#postProcessSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void postProcessSave(KualiDocumentEvent event) {
        super.postProcessSave(event);

        if (!(event instanceof SaveDocumentEvent)) { //don't lock until they route
            MaintenanceDocumentService maintenanceDocumentService = SpringContext.getBean(MaintenanceDocumentService.class);
            AssetService assetService = SpringContext.getBean(AssetService.class);

            maintenanceDocumentService.deleteLocks(this.getDocumentNumber());

            List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
            maintenanceLocks.add(assetService.generateAssetLock(documentNumber, capitalAssetNumber));
            maintenanceDocumentService.storeLocks(maintenanceLocks);
        }
    }    

    /**
     * 
     * @see org.kuali.kfs.sys.document.GeneralLedgerPostingDocumentBase#handleRouteStatusChange()
     */
    @Override
    public void handleRouteStatusChange() {
        super.handleRouteStatusChange();
		KualiWorkflowDocument workflowDocument = getDocumentHeader().getWorkflowDocument();        
        
        //Update asset payment table with the approved asset detail records.
        if (workflowDocument.stateIsProcessed()) {
            SpringContext.getBean(AssetPaymentService.class).processApprovedAssetPayment(this);

            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(this.getDocumentNumber());
        }
        
        if (workflowDocument.stateIsCanceled() || workflowDocument.stateIsDisapproved()) {
            SpringContext.getBean(MaintenanceDocumentService.class).deleteLocks(this.getDocumentNumber());
        }
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#prepareForSave(org.kuali.core.rule.event.KualiDocumentEvent)
     */
    @Override
    public void prepareForSave(KualiDocumentEvent event) {
        // This is an empty method in order to prevent kuali from generating a gl pending entry record.     
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new AssetPaymentAccountingLineParser();
    }


    public Long getCapitalAssetNumber() {
        return capitalAssetNumber;
    }


    public void setCapitalAssetNumber(Long capitalAssetNumber) {
        this.capitalAssetNumber = capitalAssetNumber;
    }

    public Integer getNextCapitalAssetPaymentLineNumber() {
        return nextCapitalAssetPaymentLineNumber;
    }

    public void setNextCapitalAssetPaymentLineNumber(Integer nextCapitalAssetPaymentLineNumber) {
        this.nextCapitalAssetPaymentLineNumber = nextCapitalAssetPaymentLineNumber;
    }


    public List<AssetPaymentDetail> getAssetPaymentDetail() {
        return assetPaymentDetail;
    }

    public void setAssetPaymentDetail(List<AssetPaymentDetail> assetPaymentDetail) {
        this.assetPaymentDetail = assetPaymentDetail;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public KualiDecimal getPreviousTotalCostAmount() {
        return previousTotalCostAmount;
    }

    public void setPreviousTotalCostAmount(KualiDecimal previousTotalCostAmount) {
        this.previousTotalCostAmount = previousTotalCostAmount;
    }
}
