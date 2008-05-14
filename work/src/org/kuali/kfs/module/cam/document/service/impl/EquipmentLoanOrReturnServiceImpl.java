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
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.rules.EquipmentLoanOrReturnDocumentRule;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.EquipmentLoanOrReturnService;

public class EquipmentLoanOrReturnServiceImpl implements EquipmentLoanOrReturnService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetPaymentServiceImpl.class);

    private AssetService assetService;
    private BusinessObjectService businessObjectService;


    public void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document) {
        updateAssetChanges(document);
        updateAssetLocation(document);
    }


    public void updateAssetChanges(EquipmentLoanOrReturnDocument document) {
        AssetHeader assetHeader = document.getAssetHeader();
        Asset updateAsset = new Asset();
        updateAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
        updateAsset = (Asset) getBusinessObjectService().retrieve(updateAsset);
        updateAsset.setLoanDate(document.getLoanDate());
        updateAsset.setLoanReturnDate(document.getLoanReturnDate());
        getBusinessObjectService().save(updateAsset);
    }

    public void updateAssetLocation(EquipmentLoanOrReturnDocument document) {
        updateBorrowerLocation(document);
        updateStoreAtLocation(document);

    }

    public void updateBorrowerLocation(EquipmentLoanOrReturnDocument document) {
        Asset updateAsset = document.getAsset();
        AssetLocation borrowerLocation = new AssetLocation();
        borrowerLocation.setCapitalAssetNumber(updateAsset.getCapitalAssetNumber());
        borrowerLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER);
        if (ObjectUtils.isNull(document.getLoanReturnDate())) {
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
            // borrowerLocation = (AssetLocation) getBusinessObjectService().retrieve(borrowerLocation);
            getBusinessObjectService().delete(updateAsset);
        }
    }

    public void updateStoreAtLocation(EquipmentLoanOrReturnDocument document) {
        Asset updateAsset = document.getAsset();
        AssetLocation storeAtLocation = new AssetLocation();
        storeAtLocation.setCapitalAssetNumber(updateAsset.getCapitalAssetNumber());
        storeAtLocation.setAssetLocationTypeCode(CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE);
        if (ObjectUtils.isNull(document.getLoanReturnDate()) && StringUtils.isNotBlank(document.getBorrowerStorageAddress())) {
            storeAtLocation.setAssetLocationContactName(document.getBorrowerUniversalUser().getPersonName());
            storeAtLocation.setAssetLocationContactIdentifier(document.getBorrowerUniversalIdentifier());
            storeAtLocation.setAssetLocationInstitutionName(document.getBorrowerUniversalUser().getPrimaryDepartmentCode());
            storeAtLocation.setAssetLocationPhoneNumber(document.getBorrowerStoragePhoneNumber());
            storeAtLocation.setAssetLocationStreetAddress(document.getBorrowerStorageAddress());
            storeAtLocation.setAssetLocationCityName(document.getBorrowerStorageCityName());
            storeAtLocation.setAssetLocationStateCode(document.getBorrowerStorageStateCode());
            storeAtLocation.setAssetLocationCountryCode(document.getBorrowerStorageCountryCode());
            storeAtLocation.setAssetLocationZipCode(document.getBorrowerStorageZipCode());
            getBusinessObjectService().save(storeAtLocation);
        }
        else {
            storeAtLocation = (AssetLocation) getBusinessObjectService().retrieve(storeAtLocation);
            getBusinessObjectService().delete(updateAsset);
        }
    }

    /**
     * Checks if asset payment is federally funder or not
     * 
     * @param assetPayment Payment record
     * @return True if financial object sub type code indicates federal contribution
     */
    public boolean canBeLoaned(EquipmentLoanOrReturnDocument document) {
        Asset asset = document.getAsset();
        String campusTagNumber = asset.getCampusTagNumber();
        boolean isRetired = getAssetService().isAssetRetired(asset);
        if (StringUtils.isEmpty(campusTagNumber) || getAssetService().isAssetRetired(asset)) {
            GlobalVariables.getErrorMap().putError(EquipmentLoanOrReturnDocumentRule.DOC_HEADER_PATH, CamsKeyConstants.EquipmentLoanOrReturn.ERROR_ASSET_CANNOT_BE_LOANED, asset.getCapitalAssetNumber().toString());
            return false;
        }
        return true;
    }

    public AssetService getAssetService() {
        return assetService;
    }

    public void setAssetService(AssetService assetService) {
        this.assetService = assetService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
