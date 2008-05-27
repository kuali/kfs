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
package org.kuali.module.cams.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.service.EquipmentLoanOrReturnService;

public class EquipmentLoanOrReturnServiceImpl implements EquipmentLoanOrReturnService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnServiceImpl.class);

    // private AssetService assetService;
    private BusinessObjectService businessObjectService;

    /**
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest equipmentLoanOrReturn details from DB</li>
     * <li>Save asset data changes</li>
     * <li>Save borrower's location changes </li>
     * <li>Save store at location changes</li>
     * </ol>
     */
    public void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document) {
        AssetHeader assetHeader = document.getAssetHeader();
        Asset updateAsset = new Asset();
        updateAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
        updateAsset = (Asset) getBusinessObjectService().retrieve(updateAsset);
        updateAsset.setExpectedReturnDate(document.getExpectedReturnDate());
        updateAsset.setLoanDate(document.getLoanDate());
        updateAsset.setLoanReturnDate(document.getLoanReturnDate());
        updateAsset.setSignatureCode(document.isSignatureCode());

        updateBorrowerLocation(document, updateAsset);
        updateStoreAtLocation(document, updateAsset);

        getBusinessObjectService().save((PersistableBusinessObject) updateAsset);
    }


    private void updateBorrowerLocation(EquipmentLoanOrReturnDocument document, Asset updateAsset) {
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
            borrowerLocation.setAssetLocationContactName(document.getBorrowerUniversalUser().getPersonName());
            borrowerLocation.setAssetLocationContactIdentifier(document.getBorrowerUniversalIdentifier());
            borrowerLocation.setAssetLocationInstitutionName(document.getBorrowerUniversalUser().getPrimaryDepartmentCode());
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

    private void updateStoreAtLocation(EquipmentLoanOrReturnDocument document, Asset updateAsset) {
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
            storeAtLocation.setAssetLocationContactName(document.getBorrowerUniversalUser().getPersonName());
            storeAtLocation.setAssetLocationContactIdentifier(document.getBorrowerUniversalIdentifier());
            storeAtLocation.setAssetLocationInstitutionName(document.getBorrowerUniversalUser().getPrimaryDepartmentCode());
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
