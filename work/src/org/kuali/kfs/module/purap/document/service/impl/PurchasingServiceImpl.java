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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.cam.CamsKeyConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapKeyConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Room;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.service.impl.PersistenceServiceStructureImplBase;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.RiceKeyConstants;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchasingServiceImpl extends PersistenceServiceStructureImplBase implements PurchasingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingServiceImpl.class);

    private ParameterService parameterService;
    private SequenceAccessorService sequenceAccessorService;
    private PurapAccountingService purapAccountingService;
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;
    private PostalCodeValidationService postalCodeValidationService;
    
    public void setPurapAccountingService(PurapAccountingService purapAccountingService) {
        this.purapAccountingService = purapAccountingService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public void setCapitalAssetBuilderModuleService(CapitalAssetBuilderModuleService capitalAssetBuilderModuleService) {
        this.capitalAssetBuilderModuleService = capitalAssetBuilderModuleService;
    }

    public void setPostalCodeValidationService(PostalCodeValidationService postalCodeValidationService) {
        this.postalCodeValidationService = postalCodeValidationService;
    }

    public void setupCapitalAssetItems(PurchasingDocument purDoc) {

        List<PurchasingCapitalAssetItem> camsItemsList = purDoc.getPurchasingCapitalAssetItems();
        List<PurchasingCapitalAssetItem> newCamsItemsList = new TypedArrayList(purDoc.getPurchasingCapitalAssetItemClass());

        for (PurApItem purapItem : purDoc.getItems()) {
            if (purapItem.getItemType().isLineItemIndicator() || PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE.equals(purapItem.getItemTypeCode())) {
                if (capitalAssetBuilderModuleService.doesItemNeedCapitalAsset(purapItem.getItemTypeCode(), purapItem.getSourceAccountingLines())) {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    // If either the camsItem is null or if its system is null and the document's system type is IND (this is
                    // the case when the user tries to switch from ONE system type to IND system type), we'll have to create
                    // the camsItem again along with its system to prevent the No collection found error.
                    if (ObjectUtils.isNull(camsItem) || (purDoc.getCapitalAssetSystemTypeCode() != null && purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL) && ObjectUtils.isNull(camsItem.getPurchasingCapitalAssetSystem()))) {
                        PurchasingCapitalAssetItem newCamsItem = createCamsItem(purDoc, purapItem);
                        newCamsItemsList.add(newCamsItem);
                    }
                    else {
                        // We need this next line so that the line item info is refreshed
                        // within the Capital Asset tab each time the document content has
                        // changed during editing to prevent KULPURAP-2926.
                        camsItem.setPurchasingDocument(purDoc);
                        newCamsItemsList.add(camsItem);
                    }
                }
                else {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    if (camsItem != null && camsItem.isEmpty()) {
                        camsItemsList.remove(camsItem);
                    }
                }
            }
        }

        purDoc.setPurchasingCapitalAssetItems(newCamsItemsList);
        
        if (purDoc.getPurchasingCapitalAssetItems().isEmpty()) {
            purDoc.setCapitalAssetSystemStateCode(null);
            purDoc.setCapitalAssetSystemTypeCode(null);
        }
    }

    private PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        PurchasingDocumentSpecificService purchasingDocumentSpecificService = purDoc.getDocumentSpecificService();
        if (purapItem.getItemIdentifier() == null) {
            ClassDescriptor cd = this.getClassDescriptor(purapItem.getClass());
            String sequenceName = cd.getFieldDescriptorByName(PurapPropertyConstants.ITEM_IDENTIFIER).getSequenceName();
                        
            Integer itemIdentifier = sequenceAccessorService.getNextAvailableSequenceNumber(sequenceName, PurApItem.class).intValue();
            purapItem.setItemIdentifier(itemIdentifier);
        }
        PurchasingCapitalAssetItem camsItem = purchasingDocumentSpecificService.createCamsItem(purDoc, purapItem);
        return camsItem;
    }


    private PurchasingCapitalAssetItem getItemIfAlreadyInCamsItemsList(PurApItem item, List<PurchasingCapitalAssetItem> camsItemsList) {
        for (PurchasingCapitalAssetItem camsItem : camsItemsList) {
            if (camsItem.getItemIdentifier() != null && camsItem.getItemIdentifier().equals(item.getItemIdentifier())) {
                return camsItem;
            }
        }

        return null;
    }


    public void deleteCapitalAssetItems(PurchasingDocument purDoc, Integer itemIdentifier) {
        // delete the corresponding CAMS items.
        int index = 0;
        for (PurchasingCapitalAssetItem camsItem : purDoc.getPurchasingCapitalAssetItems()) {
            if (camsItem.getItemIdentifier().equals(itemIdentifier)) {
                break;
            }
            index++;
        }
        purDoc.getPurchasingCapitalAssetItems().remove(index);
    }

    public void setupCapitalAssetSystem(PurchasingDocument purDoc) {
        CapitalAssetSystem resultSystem = purDoc.getDocumentSpecificService().createCapitalAssetSystem();
        if (purDoc.getCapitalAssetSystemTypeCode() != null && (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS))) {
            if (purDoc.getPurchasingCapitalAssetSystems().size() == 0) {
                purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
            }
        }
    }

    public boolean getDefaultUseTaxIndicatorValue(PurchasingDocument purDoc) {
        
        purDoc.refreshReferenceObject("vendorDetail");
        VendorDetail vendor = purDoc.getVendorDetail();        
        if (vendor != null) {
            String vendorStateCode = vendor.getDefaultAddressStateCode();
            String billingStateCode = purDoc.getBillingStateCode();
            if (StringUtils.equals(vendorStateCode, billingStateCode) || (vendor.isTaxableIndicator())) {
                return false;
            }
        }
        else {
            // don't set use tax if no vendor on req
            return true;
        }

        return true;
    }

    public String getDefaultAssetTypeCodeNotThisFiscalYear() {
        //FIXME (hjs) is this breaking modularization??
        return parameterService.getParameterValue(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR);
    }

    public void clearAllTaxes(PurchasingDocument purDoc) {
        if (!purDoc.isUseTaxIndicator() && purDoc.getItems() != null) {
            for (int i = 0; i < purDoc.getItems().size(); i++) {
                PurApItem item = purDoc.getItems().get(i);
                if (item.getItemType().isLineItemIndicator()) {
                    item.setItemTaxAmount(null);
                }
            }
        }
    }

    public boolean checkCapitalAssetLocation(CapitalAssetLocation location) {
        // if any of the date fields have a value AND one of them does not have a value...
        if (ObjectUtils.isNotNull(location) &&
                (StringUtils.isEmpty(location.getCapitalAssetLine1Address()) ||
                StringUtils.isEmpty(location.getCapitalAssetCityName()) ||
                StringUtils.isEmpty(location.getCapitalAssetCountryCode()) )) { 
            String missingFields = "";
            if (StringUtils.isEmpty(location.getCapitalAssetLine1Address())) {
                missingFields += "Address";
            }
            if (StringUtils.isEmpty(location.getCapitalAssetCityName())) {
                if (!StringUtils.isEmpty(missingFields)) {
                    missingFields += ", ";
                }
                missingFields += "City";
            }
            if (StringUtils.isEmpty(location.getCapitalAssetCountryCode())) {
                if (!StringUtils.isEmpty(missingFields)) {
                    missingFields += ", ";
                }
                missingFields += "Country";
            }
            // add error!
            GlobalVariables.getMessageMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, PurapKeyConstants.ERROR_CAPITAL_ASSET_INCOMPLETE_ADDRESS, missingFields);
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(PurapConstants.CAPITAL_ASSET_TAB_ERRORS);
            postalCodeValidationService.validateAddress(location.getCapitalAssetCountryCode(), location.getCapitalAssetStateCode(), location.getCapitalAssetPostalCode(), PurapConstants.CAPITAL_ASSET_TAB_ERRORS + "." + PurapPropertyConstants.CAPITAL_ASSET_LOCATION_STATE, PurapConstants.CAPITAL_ASSET_TAB_ERRORS + "." + PurapPropertyConstants.CAPITAL_ASSET_LOCATION_POSTAL_CODE);
            GlobalVariables.getMessageMap().clearErrorPath();
            return false;
        }
        return true;
    }
    
    
    public boolean checkValidRoomNumber(CapitalAssetLocation location){
        boolean valid = true;
        if (StringUtils.isNotBlank(location.getBuildingCode()) && StringUtils.isNotBlank(location.getBuildingRoomNumber())) {
            Map objectKeys = new HashMap();
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, location.getCampusCode());
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.BUILDING_CODE, location.getBuildingCode());
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, location.getBuildingRoomNumber());
            Room room = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, objectKeys);

            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getErrorMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER, location.getBuildingCode(), location.getBuildingRoomNumber(), location.getCampusCode());
                valid &= false;
            }
            else if (!room.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Room.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_ROOM_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }else if (StringUtils.isBlank(location.getBuildingCode()) && StringUtils.isNotBlank(location.getBuildingRoomNumber())){
            Map objectKeys = new HashMap();
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.CAMPUS_CODE, location.getCampusCode());
            objectKeys.put(CamsPropertyConstants.AssetGlobalDetail.BUILDING_ROOM_NUMBER, location.getBuildingRoomNumber());
            Room room = (Room) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, objectKeys);

            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getErrorMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER_FOR_CAMPUS, location.getBuildingRoomNumber(), location.getCampusCode());
                valid &= false;
            }
            else if (!room.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Room.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_ROOM_NUMBER).getLabel();
                GlobalVariables.getErrorMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, RiceKeyConstants.ERROR_INACTIVE, label);
                valid &= false;
            }
        }
        return valid;
    }
    
    /**
     * Gets the dataDictionaryService attribute.
     * 
     * @return an implementation of the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }
    
    
}
