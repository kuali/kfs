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
package org.kuali.kfs.module.cam.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.businessobject.AssetLocation;
import org.kuali.kfs.module.cam.document.EquipmentLoanOrReturnDocument;
import org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;

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

        getBusinessObjectService().save((PersistableBusinessObject) updateAsset);
    }

    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#setEquipmentLoanInfo(org.kuali.kfs.module.cam.businessobject.Asset)
     */
    public void setEquipmentLoanInfo(Asset asset) {

        if (asset.getExpectedReturnDate() != null && asset.getLoanReturnDate() == null) {
            Map<String, Long> params = new HashMap<String, Long>();
            params.put(CamsPropertyConstants.EquipmentLoanOrReturnDocument.CAPITAL_ASSET_NUMBER, asset.getCapitalAssetNumber());
            Collection<EquipmentLoanOrReturnDocument> matchingDocs = getBusinessObjectService().findMatching(EquipmentLoanOrReturnDocument.class, params);

            List<EquipmentLoanOrReturnDocument> sortableList = new ArrayList<EquipmentLoanOrReturnDocument>();

            for (EquipmentLoanOrReturnDocument equipmentLoanOrReturn : matchingDocs) {
                equipmentLoanOrReturn.refreshReferenceObject(CamsPropertyConstants.EquipmentLoanOrReturnDocument.DOCUMENT_HEADER);
                if (equipmentLoanOrReturn.getDocumentHeader() != null && KFSConstants.DocumentStatusCodes.APPROVED.equals(equipmentLoanOrReturn.getDocumentHeader().getFinancialDocumentStatusCode())) {
                    sortableList.add(equipmentLoanOrReturn);
                }
            }
            Comparator<EquipmentLoanOrReturnDocument> comparator = new Comparator<EquipmentLoanOrReturnDocument>() {
                public int compare(EquipmentLoanOrReturnDocument o1, EquipmentLoanOrReturnDocument o2) {
                    // sort descending based on loan date
                    return o2.getLoanDate().compareTo(o1.getLoanDate());
                }
            };
            Collections.sort(sortableList, comparator);

            if (!sortableList.isEmpty()) {
                asset.setLoanOrReturnInfo(sortableList.get(0));
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#updateBorrowerLocation(org.kuali.module.cams.document.EquipmentLoanOrReturn)
     */
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

    /**
     * @see org.kuali.kfs.module.cam.document.service.EquipmentLoanOrReturnService#updateStoreAtLocation(org.kuali.module.cams.document.EquipmentLoanOrReturn)
     */
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
