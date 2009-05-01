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
package org.kuali.kfs.module.cam.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cam.CapitalAssetManagementModuleService;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.CamsConstants.DocumentTypeName;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetPayment;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobal;
import org.kuali.kfs.module.cam.businessobject.AssetRetirementGlobalDetail;
import org.kuali.kfs.module.cam.document.gl.AssetRetirementGeneralLedgerPendingEntrySource;
import org.kuali.kfs.module.cam.document.service.AssetPaymentService;
import org.kuali.kfs.module.cam.document.service.AssetRetirementService;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends FinancialSystemGlobalMaintainable {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);
    private static final String RETIRED_ASSET_TRANSFERRED_EXTERNALLY = "RetiredAssetTransferredExternally";
    private static final String RETIRED_ASSET_SOLD_OR_GIFTED = "RetiredAssetSoldOrGifted";

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemGlobalMaintainable#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        String retirementReason = ((AssetRetirementGlobal) getBusinessObject()).getRetirementReasonCode();
        if (this.RETIRED_ASSET_TRANSFERRED_EXTERNALLY.equals(nodeName)) {
            return CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(retirementReason);
        }
        if (this.RETIRED_ASSET_SOLD_OR_GIFTED.equals(nodeName)) {
            return CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(retirementReason) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(retirementReason);
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        
        if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
            capitalAssetNumbers.add(assetRetirementGlobal.getMergedTargetCapitalAssetNumber());
        }

        for (AssetRetirementGlobalDetail retirementDetail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            capitalAssetNumbers.add(retirementDetail.getCapitalAssetNumber());
        }
        // get asset locks
        this.getCapitalAssetManagementModuleService().storeAssetLocks(capitalAssetNumbers, documentNumber, DocumentTypeName.ASSET_RETIREMENT_GLOBAL, null);

        return new ArrayList<MaintenanceLock>();
    }

    protected CapitalAssetManagementModuleService getCapitalAssetManagementModuleService() {
        return SpringContext.getBean(CapitalAssetManagementModuleService.class);
    }


    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
            if (ObjectUtils.isNotNull(assetRetirementGlobal.getMergedTargetCapitalAssetNumber())) {
                assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
            }
        }

        // add doc header description if retirement reason is "MERGED"
        if (CamsConstants.AssetRetirementReasonCode.MERGED.equals(assetRetirementGlobal.getRetirementReasonCode())) {
            document.getDocumentHeader().setDocumentDescription(CamsConstants.AssetRetirementGlobal.MERGE_AN_ASSET_DESCRIPTION);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            // Set non-persistent values. So the screen can show them after submit.
            getAssetService().setAssetSummaryFields(assetRetirementGlobalDetail.getAsset());
        }
    }

    @Override
    public void addMultipleValueLookupResults(MaintenanceDocument document, String collectionName, Collection<PersistableBusinessObject> rawValues, boolean needsBlank, PersistableBusinessObject bo) {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        int nElements = assetRetirementGlobal.getAssetRetirementGlobalDetails().size() + rawValues.size();
        if (!getAssetService().isDocumentEnrouting(document) && !getAssetRetirementService().isAllowedRetireMultipleAssets(document) && nElements > new Integer(1)) {
            GlobalVariables.getErrorMap().putErrorForSectionId(CamsConstants.AssetRetirementGlobal.SECTION_ID_ASSET_DETAIL_INFORMATION, CamsKeyConstants.Retirement.ERROR_MULTIPLE_ASSET_RETIRED);
        }
        else {
            GlobalVariables.getErrorMap().clear();

            // Adding the selected asset.
            super.addMultipleValueLookupResults(document, collectionName, rawValues, needsBlank, bo);
        }
    }


    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        if (KFSConstants.MULTIPLE_VALUE.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in multiple lookup result collection. So the screen can show them when return from multiple
            // lookup.
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                getAssetService().setAssetSummaryFields(assetRetirementGlobalDetail.getAsset());
            }
        }
        else if (CamsConstants.AssetRetirementGlobal.ASSET_LOOKUPABLE_ID.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in the result from asset lookup. So the screen can show them when return from single asset
            // lookup.
            String referencesToRefresh = (String) fieldValues.get(KNSConstants.REFERENCES_TO_REFRESH);
            if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal) && referencesToRefresh.equals(CamsPropertyConstants.AssetRetirementGlobal.MERGED_TARGET_CAPITAL_ASSET)) {
                assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
            }
            AssetRetirementGlobalDetail newDetail = (AssetRetirementGlobalDetail) newCollectionLines.get(CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS);
            getAssetService().setAssetSummaryFields(newDetail.getAsset());
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#handleRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void handleRouteStatusChange(DocumentHeader documentHeader) {
        super.handleRouteStatusChange(documentHeader);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        if (documentHeader.getWorkflowDocument().stateIsEnroute()) {
            // display a message for asset not generating ledger entries when it is federally owned
            boolean allPaymentsFederalOwned = true;
            List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                for (AssetPayment assetPayment : assetRetirementGlobalDetail.getAsset().getAssetPayments()) {
                    if (!getAssetPaymentService().isPaymentFederalOwned(assetPayment)) {
                        allPaymentsFederalOwned = false;
                    }
                }
            }

            if (allPaymentsFederalOwned) {
                GlobalVariables.getMessageList().add(CamsKeyConstants.Retirement.MESSAGE_NO_LEDGER_ENTRY_REQUIRED_RETIREMENT);
            }

        }
        // all approvals have been processed, the retirement date is set to the approval date
        if (documentHeader.getWorkflowDocument().stateIsProcessed()) {
            assetRetirementGlobal.setRetirementDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
            SpringContext.getBean(BusinessObjectService.class).save(assetRetirementGlobal);

            if (getAssetRetirementService().isAssetRetiredByMerged(assetRetirementGlobal)) {
                assetRetirementGlobal.getMergedTargetCapitalAsset().setCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAssetDescription());
                SpringContext.getBean(BusinessObjectService.class).save(assetRetirementGlobal.getMergedTargetCapitalAsset());
            }

        }
        new AssetRetirementGeneralLedgerPendingEntrySource((FinancialSystemDocumentHeader) documentHeader).handleRouteStatusChange(assetRetirementGlobal.getGeneralLedgerPendingEntries());

        // release the lock when document status changed as following...
        KualiWorkflowDocument workflowDoc = documentHeader.getWorkflowDocument();
        if (workflowDoc.stateIsCanceled() || workflowDoc.stateIsDisapproved() || workflowDoc.stateIsProcessed() || workflowDoc.stateIsFinal()) {
            this.getCapitalAssetManagementModuleService().deleteAssetLocks(documentNumber, null);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        super.addNewLineToCollection(collectionName);

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        if (StringUtils.isBlank(assetRetirementGlobal.getMergedTargetCapitalAssetDescription()) && ObjectUtils.isNotNull(assetRetirementGlobal.getMergedTargetCapitalAssetNumber())) {
            assetRetirementGlobal.setMergedTargetCapitalAssetDescription(assetRetirementGlobal.getMergedTargetCapitalAsset().getCapitalAssetDescription());
        }
    }

    @Override
    public Class<? extends PersistableBusinessObject> getPrimaryEditedBusinessObjectClass() {
        return Asset.class;
    }


    private AssetRetirementService getAssetRetirementService() {
        return SpringContext.getBean(AssetRetirementService.class);
    }

    private AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
    }

    private AssetPaymentService getAssetPaymentService() {
        return SpringContext.getBean(AssetPaymentService.class);
    }

}