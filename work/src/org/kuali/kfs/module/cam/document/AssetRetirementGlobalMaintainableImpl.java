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
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetRetirementGlobal;
import org.kuali.module.cams.bo.AssetRetirementGlobalDetail;
import org.kuali.module.cams.service.PaymentSummaryService;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global location
 * assets
 */
public class AssetRetirementGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetRetirementGlobalMaintainableImpl.class);
    private static PaymentSummaryService paymentSummaryService = SpringContext.getBean(PaymentSummaryService.class);

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
        if (CamsConstants.AssetRetirementReasonCode.MERGED.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode())) {
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
            lockRep.append("documentNumber");
            lockRep.append(KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
            lockRep.append(detail.getDocumentNumber());
            lockRep.append(KFSConstants.Maintenance.AFTER_VALUE_DELIM);
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
     * Hide retirement detail sections based on the retirement reason code
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#getCoreSections(org.kuali.core.maintenance.Maintainable)
     */
    @Override
    public List<Section> getCoreSections(Maintainable oldMaintainable) {
        List<Section> sections = super.getCoreSections(oldMaintainable);
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();

        if (CamsConstants.AssetRetirementReasonCode.EXTERNAL_TRANSFER.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.GIFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode())) {
            for (Section section : sections) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_ID_AUCTION_OR_SOLD.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT.equalsIgnoreCase(section.getSectionId())) {
                    section.setHidden(true);
                }
            }
        }
        else if (CamsConstants.AssetRetirementReasonCode.AUCTION.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode()) || CamsConstants.AssetRetirementReasonCode.SOLD.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode())) {
            for (Section section : sections) {
                if (CamsConstants.AssetRetirementGlobal.SECTION_ID_EXTERNAL_TRANSFER_OR_GIFT.equalsIgnoreCase(section.getSectionId()) || CamsConstants.AssetRetirementGlobal.SECTION_ID_THEFT.equalsIgnoreCase(section.getSectionId())) {
                    section.setHidden(true);
                }
            }
        }
        else if (CamsConstants.AssetRetirementReasonCode.THEFT.equalsIgnoreCase(assetRetirementGlobal.getRetirementReasonCode())) {
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
     * Populate shared columns for each assetRetirementGlobalDetail based sharedRetirementInfo
     * 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#prepareForSave()
     */
    @Override
    public void prepareForSave() {
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        AssetRetirementGlobalDetail sharedRetirementInfo = assetRetirementGlobal.getSharedRetirementInfo();
        if (sharedRetirementInfo != null) {
            // deep copy for each assetRetirmentDetail
            for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
                Long capitalAssetNumber = assetRetirementGlobalDetail.getCapitalAssetNumber();
                assetRetirementGlobalDetail = (AssetRetirementGlobalDetail) ObjectUtils.deepCopy(sharedRetirementInfo);
                assetRetirementGlobalDetail.setCapitalAssetNumber(capitalAssetNumber);
            }
        }
        super.prepareForSave();
    }

    /**
     * Restore sharedRetirementInfo by the first assetRetirementGlobalDetail
     * 
     * @see org.kuali.core.maintenance.KualiGlobalMaintainableImpl#processAfterRetrieve()
     */
    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        AssetRetirementGlobal assetRetirementGlobal = (AssetRetirementGlobal) getBusinessObject();
        AssetRetirementGlobalDetail sharedRetirementInfo = new AssetRetirementGlobalDetail();
        List<AssetRetirementGlobalDetail> assetRetirementGlobalDetails = assetRetirementGlobal.getAssetRetirementGlobalDetails();
        
//        assetRetirementGlobal.refreshReferenceObject("assetRetirementGlobalDetails");
        for (AssetRetirementGlobalDetail assetRetirementGlobalDetail : assetRetirementGlobalDetails) {
            if (sharedRetirementInfo == null) {
                sharedRetirementInfo = (AssetRetirementGlobalDetail) ObjectUtils.deepCopy(assetRetirementGlobalDetail);
                assetRetirementGlobal.setSharedRetirementInfo(sharedRetirementInfo);
            }

            Asset asset = assetRetirementGlobalDetail.getAsset();
            // set calculation field
            if (asset != null) {
                asset.setFederalContribution(paymentSummaryService.calculateFederalContribution(asset));
            }
        }
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
            // Refresh reference object asset to show the asset detail information whenever the page refreshed. This method...
            detail.refreshReferenceObject("asset");
            Asset asset = detail.getAsset();

            // set calculation field
            if (asset != null) {
                asset.setFederalContribution(paymentSummaryService.calculateFederalContribution(asset));
            }
        }
    }


    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.setupNewFromExisting(document, parameters);

        AssetRetirementGlobal assetRetirementGlobal = ((AssetRetirementGlobal) getBusinessObject());
        assetRetirementGlobal.setRetirementDate(DateUtils.convertToSqlDate(new java.util.Date()));

    }

}
