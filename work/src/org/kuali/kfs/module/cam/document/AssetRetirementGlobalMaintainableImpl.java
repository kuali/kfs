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
package org.kuali.module.cams.maintenance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.service.PaymentSummaryService;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);
    private static PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);

    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        // Lock the merge target capital asset if it exists

        if (assetRetirementService.isAssetRetiredByMerged(assetRetirementGlobal)) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName());
            lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append("documentNumber");
            lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(assetRetirementGlobal.getDocumentNumber());
            lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
            lockRep.append("capitalAssetNumber");
            lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(assetRetirementGlobal.getMergedTargetCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(assetRetirementGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());

            maintenanceLocks.add(maintenanceLock);
        }

        // Lock all source assets
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName());
            lockRep.append(KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append("capitalAssetNumber");
            lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(detail.getCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(detail.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());

            maintenanceLocks.add(maintenanceLock);
        }

        return maintenanceLocks;
    }

    /**
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#setupNewFromExisting(org.kuali.core.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        // Setup default asset retirement date. <defaultValueFinderClass> defined in DD can NOT work at this point.
        AssetRetirementGlobal assetRetirementGlobal = ((AssetRetirementGlobal) getBusinessObject());
        assetRetirementGlobal.setRetirementDate(DateUtils.convertToSqlDate(new java.util.Date()));

    }

    /**
     * Hide retirement detail sections based on the retirement reason code
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.core.maintenance.Maintainable)
     */
    @Override
    public List<Section> getCoreSections(Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(oldMaintainable);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        if (assetRetirementService.isAssetRetiredByExternalTransferOrGift(assetRetirementGlobal)) {
            for (Section section : sections) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT.equalsIgnoreCase(section.getSectionId())) {
                    section.setHidden(true);
                }
            }
        }
        else if (assetRetirementService.isAssetRetiredBySoldOrAuction(assetRetirementGlobal)) {
            for (Section section : sections) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT.equalsIgnoreCase(section.getSectionId())) {
                    section.setHidden(true);
                }
            }
        }
        else if (assetRetirementService.isAssetRetiredByTheft(assetRetirementGlobal)) {
            for (Section section : sections) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD.equalsIgnoreCase(section.getSectionId())) {
                    section.setHidden(true);
                }
            }
        }
        else {
            for (Section section : sections) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT.equalsIgnoreCase(section.getSectionId())) {
                    section.setHidden(true);
                }
            }
        }
        return sections;
    }

    /**
     * Restore sharedRetirementInfo by the first assetRetirementGlobalDetail
     * 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            if (assetRetirementGlobal.getSharedRetirementInfo() == null) {
                AssetRetirementGlobalDetail sharedRetirementInfo = new AssetRetirementGlobalDetail();
                sharedRetirementInfo = (AssetRetirementGlobalDetail) ObjectUtils.deepCopy(assetRetirementGlobalDetail);
                assetRetirementGlobal.setSharedRetirementInfo(sharedRetirementInfo);
            }

            setAssetNonPersistentFields(assetRetirementGlobalDetail.getAsset());
            // refreshAsset(assetRetirementGlobalDetail);
        }
    }

    /**
     * Populate shared columns for each assetRetirementGlobalDetail based sharedRetirementInfo
     * 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#prepareGlobalsForSave()
     */
    @Override
    protected void prepareGlobalsForSave() {
        // TODO Auto-generated method stub

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        AssetRetirementGlobalDetail sharedRetirementInfo = assetRetirementGlobal.getSharedRetirementInfo();
        List<AssetRetirementGlobalDetail> newAssetRetirementGlobalDetails = new TypedArrayList(AssetRetirementGlobalDetail.class);
        AssetRetirementGlobalDetail assetRetirementGlobalDetail = null;
        if (sharedRetirementInfo != null) {
            // deep copy for each assetRetirmentDetail
            for (AssetRetirementGlobalDetail assetDetail : assetRetirementGlobalDetails) {
                assetRetirementGlobalDetail = (AssetRetirementGlobalDetail) ObjectUtils.deepCopy(sharedRetirementInfo);
                assetRetirementGlobalDetail.setCapitalAssetNumber(assetDetail.getCapitalAssetNumber());
                assetRetirementGlobalDetail.setVersionNumber(assetDetail.getVersionNumber());
                assetRetirementGlobalDetail.setNewCollectionRecord(assetDetail.isNewCollectionRecord());
                refreshAsset(assetRetirementGlobalDetail);
                newAssetRetirementGlobalDetails.add(assetRetirementGlobalDetail);
            }
        }
        assetRetirementGlobal.setAssetRetirementGlobalDetails(newAssetRetirementGlobalDetails);

        super.prepareGlobalsForSave();

    }

    /**
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        super.addNewLineToCollection(collectionName);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            refreshAsset(detail);
        }
    }

    /**
     * 
     * Refresh reference object asset to show the asset detail information whenever the page refreshed.
     * 
     * @param detail
     */
    private void refreshAsset(AssetRetirementGlobalDetail detail) {
        if (detail.getAsset() == null) {
            detail.refreshReferenceObject("asset");
        }
        setAssetNonPersistentFields(detail.getAsset());
    }

    /**
     * This method calls the service codes to calculate the summary fields for each asset
     * 
     * @param asset
     */
    private void setAssetNonPersistentFields(Asset asset) {
        if (asset != null) {
            asset.setFederalContribution(paymentSummaryService.calculateFederalContribution(asset));
        }
    }

}
