/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cam.document.service.impl;

import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

public class EquipmentLoanOrReturnServiceImpl implements EquipmentLoanOrReturnService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnServiceImpl.class);

    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#processApprovedEquipmentLoanOrReturn(org.kuali.module.cams.document.EquipmentLoanOrReturn)
     *      This method is called when the work flow document is reached its final approval
     *      <ol>
     *      <li>Gets the latest equipmentLoanOrReturn details from DB</li>
     *      <li>Save asset data changes</li>
     *      <li>Save borrower's location changes </li>
     *      <li>Save store at location changes</li>
     *      </ol>
     */
    public void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document) {
        Asset updateAsset = new Asset();
        updateAsset.setCapitalAssetNumber(document.getCapitalAssetNumber());
        updateAsset = (Asset) getBusinessObjectService().retrieve(updateAsset);
        updateAsset.setExpectedReturnDate(document.getExpectedReturnDate());
        updateAsset.setLoanDate(document.getLoanDate());
        updateAsset.setLoanReturnDate(document.getLoanReturnDate());
        updateBorrowerLocation(document, updateAsset);
        updateStoreAtLocation(document, updateAsset);
        updateAsset.setLastInventoryDate(new Timestamp(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate().getTime()));

        getBusinessObjectService().save((PersistableBusinessObject) updateAsset);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#setEquipmentLoanInfo(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public void setEquipmentLoanInfo(Asset asset) {

        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            // find borrower and storage address and set those values
            asset.refreshReferenceObject(CamsPropertyConstants.Asset.ASSET_LOCATIONS);
            List<AssetLocation> assetLocations = asset.getAssetLocations();
            if (ObjectUtils.isNotNull(assetLocations)) {
                for (AssetLocation assetLocation : assetLocations) {
                    if (CamsConstants.AssetLocationTypeCode.BORROWER.equals(assetLocation.getAssetLocationTypeCode())) {
                        asset.setBorrowerLocation(assetLocation);
                    }
                    if (CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE.equals(assetLocation.getAssetLocationTypeCode())) {
                        asset.setBorrowerStorageLocation(assetLocation);
                    }
                }
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#updateBorrowerLocation(org.kuali.module.cams.document.EquipmentLoanOrReturn)
     */
    protected void updateBorrowerLocation(EquipmentLoanOrReturnDocument document, Asset updateAsset) {
        AssetLocation borrowerLocation = new AssetLocation();
        borrowerLocation.setCapitalAssetNumber(updateAsset.getCapitalAssetNumber());
        borrowerLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER);
        borrowerLocation = (AssetLocation) getBusinessObjectService().retrieve(borrowerLocation);

        if (ObjectUtils.isNull(document.getLoanReturnDate())) {
            if (borrowerLocation == null) {
                borrowerLocation = new AssetLocation();
                borrowerLocation.setCapitalAssetNumber(updateAsset.getCapitalAssetNumber());
                borrowerLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER);
                updateAsset.getAssetLocations().add(borrowerLocation);
            }
            borrowerLocation.setAssetLocationContactName(document.getBorrowerPerson().getPrincipalName());
            borrowerLocation.setAssetLocationContactIdentifier(document.getBorrowerUniversalIdentifier());
            borrowerLocation.setAssetLocationInstitutionName(document.getBorrowerPerson().getPrimaryDepartmentCode());
            borrowerLocation.setAssetLocationPhoneNumber(document.getBorrowerPhoneNumber());
            borrowerLocation.setAssetLocationStreetAddress(document.getBorrowerAddress());
            borrowerLocation.setAssetLocationCityName(document.getBorrowerCityName());
            borrowerLocation.setAssetLocationStateCode(document.getBorrowerStateCode());
            borrowerLocation.setAssetLocationCountryCode(document.getBorrowerCountryCode());
            borrowerLocation.setAssetLocationZipCode(document.getBorrowerZipCode());
            getBusinessObjectService().save(borrowerLocation);
        }
        else {
            if (borrowerLocation != null) {
                updateAsset.getAssetLocations().remove(borrowerLocation);
                getBusinessObjectService().delete(borrowerLocation);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#updateStoreAtLocation(org.kuali.module.cams.document.EquipmentLoanOrReturn)
     */
    protected void updateStoreAtLocation(EquipmentLoanOrReturnDocument document, Asset updateAsset) {
        AssetLocation storeAtLocation = new AssetLocation();
        storeAtLocation.setCapitalAssetNumber(updateAsset.getCapitalAssetNumber());
        storeAtLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE);
        storeAtLocation = (AssetLocation) getBusinessObjectService().retrieve(storeAtLocation);

        if (ObjectUtils.isNull(document.getLoanReturnDate()) && StringUtils.isNotBlank(document.getBorrowerStorageAddress())) {
            if (storeAtLocation == null) {
                storeAtLocation = new AssetLocation();
                storeAtLocation.setCapitalAssetNumber(updateAsset.getCapitalAssetNumber());
                storeAtLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE);
                updateAsset.getAssetLocations().add(storeAtLocation);
            }
            storeAtLocation.setAssetLocationContactName(document.getBorrowerPerson().getPrincipalName());
            storeAtLocation.setAssetLocationContactIdentifier(document.getBorrowerUniversalIdentifier());
            storeAtLocation.setAssetLocationInstitutionName(document.getBorrowerPerson().getPrimaryDepartmentCode());
            storeAtLocation.setAssetLocationPhoneNumber(document.getBorrowerStoragePhoneNumber());
            storeAtLocation.setAssetLocationStreetAddress(document.getBorrowerStorageAddress());
            storeAtLocation.setAssetLocationCityName(document.getBorrowerStorageCityName());
            storeAtLocation.setAssetLocationStateCode(document.getBorrowerStorageStateCode());
            storeAtLocation.setAssetLocationCountryCode(document.getBorrowerStorageCountryCode());
            storeAtLocation.setAssetLocationZipCode(document.getBorrowerStorageZipCode());
            getBusinessObjectService().save((PersistableBusinessObject) storeAtLocation);
        }
        else {
            if (storeAtLocation != null) {
                updateAsset.getAssetLocations().remove(storeAtLocation);
                getBusinessObjectService().delete((PersistableBusinessObject) storeAtLocation);
            }
        }
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
