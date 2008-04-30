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

import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.KualiGlobalMaintainableImpl;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetGlobal;
import org.kuali.module.cams.bo.AssetGlobalDetail;
import org.kuali.module.cams.bo.AssetPaymentDetail;
import org.kuali.module.cams.lookup.valuefinder.NextAssetNumberFinder;

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

        // super.addNewLineToCollection(collectionName);
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        if ("assetPaymentDetails".equalsIgnoreCase(collectionName)) {
            AssetPaymentDetail assetPaymentDetail = (AssetPaymentDetail) newCollectionLines.get(collectionName);
            if (assetPaymentDetail != null) {
                assetPaymentDetail.setFinancialDocumentLineNumber(assetGlobal.incrementFinancialDocumentLineNumber());
            }
        }
        int pos = assetGlobal.getAssetSharedDetails().size() - 1;
        if (pos > -1 && ("assetSharedDetails[" + pos + "].assetGlobalUniqueDetails").equalsIgnoreCase(collectionName)) {
            AssetGlobalDetail assetGlobalDetail = (AssetGlobalDetail) newCollectionLines.get(collectionName);
            if (assetGlobalDetail != null) {
                assetGlobalDetail.setCapitalAssetNumber(NextAssetNumberFinder.getLongValue());
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
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();

        for (AssetGlobalDetail detail : assetGlobal.getAssetGlobalDetails()) {
            MaintenanceLock maintenanceLock = new MaintenanceLock();
            StringBuffer lockRep = new StringBuffer();

            lockRep.append(Asset.class.getName() + KFSConstants.Maintenance.AFTER_CLASS_DELIM);
            lockRep.append("capitalAssetNumber" + KFSConstants.Maintenance.AFTER_FIELDNAME_DELIM);
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
        List<AssetGlobalDetail> assetSharedDetails = assetGlobal.getAssetSharedDetails();
        List<AssetGlobalDetail> newDetails = new TypedArrayList(AssetGlobalDetail.class);
        AssetGlobalDetail newAssetGlobalDetail = null;
        if (assetSharedDetails != null){
            assetGlobal.getAssetHeader().setCapitalAssetNumber(assetSharedDetails.get(0).getAssetGlobalUniqueDetails().get(0).getCapitalAssetNumber()); 
        }
        
        for (AssetGlobalDetail locationDetail : assetSharedDetails) {
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

                /*
                 * Alternate way, using deepcopy: newAssetGlobalDetail = (AssetGlobalDetail) ObjectUtils.deepCopy(locationDetail);
                 * newAssetGlobalDetail.setVersionNumber(detail.getVersionNumber());
                 * newAssetGlobalDetail.setOrganizationInventoryName(detail.getOrganizationInventoryName());
                 * newAssetGlobalDetail.setSerialNumber(detail.getSerialNumber());
                 * newAssetGlobalDetail.setOrganizationAssetTypeIdentifier(detail.getOrganizationAssetTypeIdentifier());
                 * newAssetGlobalDetail.setGovernmentTagNumber(detail.getGovernmentTagNumber());
                 * newAssetGlobalDetail.setCampusTagNumber(detail.getCampusTagNumber());
                 * newAssetGlobalDetail.setNationalStockNumber(detail.getNationalStockNumber());
                 * newDetails.add(newAssetGlobalDetail);
                 */
            }
        }
        assetGlobal.getAssetGlobalDetails().clear();
        assetGlobal.setAssetGlobalDetails(newDetails);


    }


    @Override
    public void processAfterRetrieve() {
        super.processAfterRetrieve();
        AssetGlobal assetGlobal = (AssetGlobal) getBusinessObject();
        assetGlobal.refresh();
        List<AssetGlobalDetail> assetGlobalDetails = assetGlobal.getAssetGlobalDetails();
        AssetGlobalDetail currLocationDetail = null;
        HashMap<String, AssetGlobalDetail> locationMap = new HashMap<String, AssetGlobalDetail>();
        AssetGlobalDetail copyValue = null;
        for (AssetGlobalDetail detail : assetGlobalDetails) {
            copyValue = (AssetGlobalDetail) ObjectUtils.deepCopy(detail);
            String key = generateLocationKey(copyValue);
            if ((currLocationDetail = locationMap.get(key)) == null) {
                currLocationDetail = copyValue;
                locationMap.put(key, currLocationDetail);
            }
            currLocationDetail.getAssetGlobalUniqueDetails().add(copyValue);
        }
        assetGlobal.getAssetSharedDetails().clear();
        assetGlobal.getAssetSharedDetails().addAll(locationMap.values());
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