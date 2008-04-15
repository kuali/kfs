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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetPaymentDetail;

/**
 * This class overrides the base {@link KualiGlobalMaintainableImpl} to generate the specific maintenance locks for Global assets
 */
public class AssetGlobalMaintainableImpl extends KualiGlobalMaintainableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetGlobalMaintainableImpl.class);

    /**
     * Hook for quantity and setting asset numbers.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        /*
         * AssetGlobalDetail addAssetLine = (AssetGlobalDetail) newCollectionLines.get(collectionName);
         * addAssetLine.setNewCollectionRecord(true); Collection maintCollection = (Collection)
         * ObjectUtils.getPropertyValue(getBusinessObject(), collectionName); maintCollection.add(addAssetLine);
         */

        //super.addNewLineToCollection(collectionName);
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        if ("assetPaymentDetails".equalsIgnoreCase(collectionName)) {
            AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) newCollectionLines.get(collectionName);
            if (assetPaymentDetail != null) {
                assetPaymentDetail.setFinancialDocumentLineNumber(assetGlobal.incrementFinancialDocumentLineNumber());
            }
        }
        super.addNewLineToCollection(collectionName);
    }

    /**
     * This creates the particular locking representation for this global document.
     * 
     * @see org.kuali.core.maintenance.Maintainable#generateMaintenanceLocks()
     */
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<MaintenanceLock> maintenanceLocks = new ArrayList();

        for (AssetGlobalDetail detail : assetGlobal.getAssetGlobalDetails()) {
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

            maintenanceLock.setDocumentNumber(assetGlobal.getDocumentNumber());
            maintenanceLock.setLockingRepresentation(lockRep.toString());
            maintenanceLocks.add(maintenanceLock);
        }
        return maintenanceLocks;
    }

    @Override
    public void prepareForSave() {
        super.prepareForSave();
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();

        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetGlobalDetails();
        List<AssetGlobalDetail> newDetails = new TypedArrayList(AssetGlobalDetail.class);

        for (AssetGlobalDetail locationDetail : assetGlobalDetails) {
            List<AssetGlobalDetail> assetGlobalUniqueDetails = locationDetail.getAssetGlobalUniqueDetails();

            for (AssetGlobalDetail detail : assetGlobalUniqueDetails) {
                // read from location and set it to detail
                detail.setCampusCode(locationDetail.getCampusCode());
                detail.setBuildingCode(locationDetail.getBuildingCode());
                detail.setBuildingRoomNumber(locationDetail.getBuildingRoomNumber());
                detail.setBuildingSubRoomNumber(locationDetail.getBuildingSubRoomNumber());
                detail.setOffCampusName(locationDetail.getOffCampusName());
                detail.setOffCampusAddress(locationDetail.getOffCampusAddress());
                detail.setOffCampusCityName(locationDetail.getOffCampusCityName());
                detail.setOffCampusStateCode(locationDetail.getOffCampusStateCode());
                detail.setOffCampusCountryCode(locationDetail.getOffCampusCountryCode());
                detail.setOffCampusZipCode(locationDetail.getOffCampusZipCode());
                newDetails.add(detail);
            }
        }
        assetGlobal.getAssetGlobalDetails().clear();
        assetGlobal.setAssetGlobalDetails(newDetails);


    }

    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetGlobalDetails();
        AssetGlobalDetail currLocationDetail = null;
        HashMap<String, AssetGlobalDetail> locationMap = new HashMap<String, AssetGlobalDetail>();

        for (AssetGlobalDetail detail : assetGlobalDetails) {
            String key = generateLocationKey(detail);
            if ((currLocationDetail = locationMap.get(key)) == null) {
                currLocationDetail = detail;
                locationMap.put(key, currLocationDetail);
            }
            currLocationDetail.getAssetGlobalUniqueDetails().add(detail);
        }
        assetGlobal.getAssetGlobalDetails().clear();
        assetGlobal.setAssetGlobalDetails(new ArrayList<AssetGlobalDetail>(locationMap.values()));
    }

    private String generateLocationKey(AssetGlobalDetail location) {
        StringBuilder builder = new StringBuilder();
        builder.append(location.getCampusCode() == null ? "" : location.getCampusCode().toLowerCase().trim());
        builder.append(location.getBuildingCode() == null ? "" : location.getBuildingCode().toLowerCase().trim());
        builder.append(location.getBuildingRoomNumber() == null ? "" : location.getBuildingRoomNumber().toLowerCase().trim());
        builder.append(location.getBuildingSubRoomNumber() == null ? "" : location.getBuildingSubRoomNumber().toLowerCase().trim());
        builder.append(location.getOffCampusName() == null ? "" : location.getOffCampusName().toLowerCase().trim());
        builder.append(location.getOffCampusAddress() == null ? "" : location.getOffCampusAddress().toLowerCase().trim());
        builder.append(location.getOffCampusCityName() == null ? "" : location.getOffCampusCityName().toLowerCase().trim());
        builder.append(location.getOffCampusStateCode() == null ? "" : location.getOffCampusStateCode().toLowerCase().trim());
        builder.append(location.getOffCampusCountryCode() == null ? "" : location.getOffCampusCountryCode().toLowerCase().trim());
        builder.append(location.getOffCampusZipCode() == null ? "" : location.getOffCampusZipCode().toLowerCase().trim());
        return builder.toString();
    }
}