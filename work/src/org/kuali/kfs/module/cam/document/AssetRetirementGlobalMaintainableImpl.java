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

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.DateUtils;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsPropertyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.gl.AssetRetirementGlPoster;
import org.kuali.module.cams.service.AssetRetirementService;
import org.kuali.module.cams.service.AssetService;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);
    private static AssetRetirementService assetRetirementService = SpringContext.getBean(AssetRetirementService.class);
    private static AssetService assetService = SpringContext.getBean(AssetService.class);

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

            lockRep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append("capitalAssetNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(assetRetirementGlobal.getMergedTargetCapitalAssetNumber());

            maintenanceLock.setDocumentNumber(assetRetirementGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());

            maintenanceLocks.add(maintenanceLock);
        }

        // Lock all source assets
        for (AssetRetirementGlobalDetail detail : assetRetirementGlobal.getAssetRetirementGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append("capitalAssetNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
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
        java.sql.Date currentDate = SpringContext.getBean(DateTimeService.class).getCurrentSqlDate();
        assetRetirementGlobal.setRetirementDate(currentDate);
    }

    /**
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.core.maintenance.Maintainable)
     */
    @Override
    public List<Section> getCoreSections(Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(oldMaintainable);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        // Hide retirement detail sections based on the retirement reason code
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
     * 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#prepareGlobalsForSave()
     */
    @Override
    protected void prepareGlobalsForSave() {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        AssetRetirementGlobalDetail sharedRetirementInfo = assetRetirementGlobal.getSharedRetirementInfo();
        List<AssetRetirementGlobalDetail> newAssetRetirementGlobalDetails = new TypedArrayList(AssetRetirementGlobalDetail.class);
        AssetRetirementGlobalDetail assetRetirementGlobalDetail = null;

        // Populate shared columns for each assetRetirementGlobalDetail based sharedRetirementInfo
        if (sharedRetirementInfo != null) {
            // deep copy for each assetRetirmentDetail
            for (AssetRetirementGlobalDetail assetDetail : assetRetirementGlobalDetails) {
                assetRetirementGlobalDetail = (AssetRetirementGlobalDetail) ObjectUtils.deepCopy(sharedRetirementInfo);
                // restore back the original value overridden by deep copy.
                assetRetirementGlobalDetail.setCapitalAssetNumber(assetDetail.getCapitalAssetNumber());
                assetRetirementGlobalDetail.setVersionNumber(assetDetail.getVersionNumber());
                assetRetirementGlobalDetail.setNewCollectionRecord(assetDetail.isNewCollectionRecord());

                newAssetRetirementGlobalDetails.add(assetRetirementGlobalDetail);
            }
        }
        assetRetirementGlobal.setAssetRetirementGlobalDetails(newAssetRetirementGlobalDetails);

        super.prepareGlobalsForSave();
    }

    /**
     * 
     * 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#processGlobalsAfterRetrieve()
     */
    @Override
    protected void processGlobalsAfterRetrieve() {
        super.processGlobalsAfterRetrieve();

        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            // Restore sharedRetirementInfo by the first assetRetirementGlobalDetail
            if (assetRetirementGlobal.getSharedRetirementInfo() == null) {
                AssetRetirementGlobalDetail sharedRetirementInfo = new AssetRetirementGlobalDetail();
                sharedRetirementInfo = (AssetRetirementGlobalDetail) ObjectUtils.deepCopy(assetRetirementGlobalDetail);
                assetRetirementGlobal.setSharedRetirementInfo(sharedRetirementInfo);
            }
            // Set non-persistent values. So the screen can show them after submit.
            assetService.setAssetNonPersistentFields(assetRetirementGlobalDetail.getAsset());
        }
    }


    /**
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        super.refresh(refreshCaller, fieldValues, document);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) document.getDocumentBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();

        if (KFSConstants.MULTIPLE_VALUE.equalsIgnoreCase(refreshCaller)) {
            assetRetirementService.checkRetireMultipleAssets(assetRetirementGlobal.getRetirementReasonCode(), assetRetirementGlobalDetails, new Integer(1), true);
            // Set non-persistent values in multiple lookup result collection. So the screen can show them when return from multiple
            // lookup.
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                assetService.setAssetNonPersistentFields(assetRetirementGlobalDetail.getAsset());
            }
        }
        else if (CamsConstants.ASSET_LOOKUPABLE_ID.equalsIgnoreCase(refreshCaller)) {
            // Set non-persistent values in the result from asset lookup. So the screen can show them when return from sigle asset
            // lookup.
            AssetRetirementGlobalDetail newDetail = (AssetRetirementGlobalDetail) newCollectionLines.get(CamsPropertyConstants.AssetRetirementGlobal.ASSET_RETIREMENT_GLOBAL_DETAILS);
            assetService.setAssetNonPersistentFields(newDetail.getAsset());
        }

    }

    @Override
    public void handleRouteStatusChange(DocumentHeader documentHeader) {
        super.handleRouteStatusChange(documentHeader);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        assetRetirementGlobal.refreshReferenceObject("generalLedgerPendingEntries");
        new AssetRetirementGlPoster(documentHeader).handleRouteStatusChange(assetRetirementGlobal.getGeneralLedgerPendingEntries());
    }

    public List<GeneralLedgerPendingEntry> getGeneralLedgerPendingEntries() {
        return ((AssetRetirementGlobal) getBusinessObject()).getGeneralLedgerPendingEntries();
    }

    public void setGeneralLedgerPendingEntries(List<GeneralLedgerPendingEntry> list) {
        // TODO
        // do nothing now

    }
}
