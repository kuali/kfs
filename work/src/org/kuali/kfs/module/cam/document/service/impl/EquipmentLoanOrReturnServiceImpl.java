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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.cams.CamsConstants;
import org.kuali.module.cams.CamsKeyConstants;
import org.kuali.module.cams.bo.Asset;
import org.kuali.module.cams.bo.AssetHeader;
import org.kuali.module.cams.bo.AssetLocation;
import org.kuali.module.cams.document.EquipmentLoanOrReturnDocument;
import org.kuali.module.cams.rules.EquipmentLoanOrReturnDocumentRule;
import org.kuali.module.cams.service.AssetService;
import org.kuali.module.cams.service.EquipmentLoanOrReturnService;

import edu.iu.uis.eden.exception.WorkflowException;

public class EquipmentLoanOrReturnServiceImpl implements EquipmentLoanOrReturnService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnServiceImpl.class);

    private AssetService assetService;
    private BusinessObjectService businessObjectService;

    /**
     * This method is called when the work flow document is reached its final approval
     * <ol>
     * <li>Gets the latest asset details from DB</li>
     * <li>Save asset owner data</li>
     * <li>Save borrower's location changes </li>
     * <li>Save store at location changes</li>
     * </ol>
     */
    public void processApprovedEquipmentLoanOrReturn(EquipmentLoanOrReturnDocument document) {
        AssetHeader assetHeader = document.getAssetHeader();
        Asset updateAsset = new Asset();
        updateAsset.setCapitalAssetNumber(assetHeader.getCapitalAssetNumber());
        updateAsset = (Asset) getBusinessObjectService().retrieve(updateAsset);
        updateAsset.setLoanDate(document.getLoanDate());
        updateAsset.setLoanReturnDate(document.getLoanReturnDate());

        updateBorrowerLocation(document, updateAsset);
//        LOG.info("===>After updateBorrowerLocation()");
        updateStoreAtLocation(document, updateAsset);

        getBusinessObjectService().save((PersistableBusinessObject) updateAsset);
    }


    private void updateBorrowerLocation(EquipmentLoanOrReturnDocument document, Asset updateAsset) {
        
        AssetLocation borrowerLocation = null;
        List<AssetLocation> orginalLocations = updateAsset.getAssetLocations();
        for (AssetLocation assetLocation : orginalLocations) {
            if (CamsConstants.AssetLocationTypeCode.BORROWER.equals(assetLocation.getAssetLocationTypeCode())) {
                borrowerLocation = assetLocation;
                break;
            }
        }

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
            // getBusinessObjectService().save(borrowerLocation);
        }
        else {
            if (borrowerLocation != null) {
                updateAsset.getAssetLocations().remove(borrowerLocation);
//                getBusinessObjectService().delete(borrowerLocation);
            }
        }
    }

    private void updateStoreAtLocation(EquipmentLoanOrReturnDocument document, Asset updateAsset) {
        AssetLocation storeAtLocation = null;
        List<AssetLocation> orginalLocations = updateAsset.getAssetLocations();
        for (AssetLocation assetLocation : orginalLocations) {
            if (CamsConstants.AssetLocationTypeCode.BORROWER_STORAGE.equals(assetLocation.getAssetLocationTypeCode())) {
                storeAtLocation = assetLocation;
                break;
            }
        }

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
//            getBusinessObjectService().save((PersistableBusinessObject) storeAtLocation);
        }
        else {
            if (storeAtLocation != null) {
               updateAsset.getAssetLocations().remove(storeAtLocation);
//               getBusinessObjectService().delete((PersistableBusinessObject) storeAtLocation);
            }
        }
    }

    /**
     * Checks if asset can be Loaned
     * 
     * @param equipmentLoanOrReturn record
     * @return True if the asset can be loaned
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
    
    /**
     * @see org.kuali.module.integration.service.LaborModuleService#createAndBlankApproveSalaryExpenseTransferDocument(java.lang.String,
     *      java.lang.String, java.lang.String, java.util.List, java.util.List, java.util.List)
     */
    public void createBlanketApproveEquipmentLoanOrReturnDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, EquipmentLoanOrReturnDocument sourceDocument) throws WorkflowException {
        LOG.info("====>createBlanketApproveEquipmentLoanOrReturnDocument");


        EquipmentLoanOrReturnDocument document = (EquipmentLoanOrReturnDocument) getDocumentService().getNewDocument(EquipmentLoanOrReturnDocument.class);

        Asset asset = document.getAsset();
        AssetHeader assetHeader = new AssetHeader();
        assetHeader.setDocumentNumber(document.getDocumentNumber());
        assetHeader.setCapitalAssetNumber(sourceDocument.getAsset().getCapitalAssetNumber());
        document.setAssetHeader(assetHeader);
        document.setCampusTagNumber(asset.getCampusTagNumber());
        document.setOrganizationTagNumber(asset.getOrganizationTagNumber());

        DocumentHeader documentHeader = document.getDocumentHeader();
        documentHeader.setFinancialDocumentDescription(documentDescription);
        documentHeader.setExplanation(explanation);

        getDocumentService().blanketApproveDocument(document, annotation, adHocRecipients);
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

    /**
     * Gets the documentService attribute.
     * 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

}
