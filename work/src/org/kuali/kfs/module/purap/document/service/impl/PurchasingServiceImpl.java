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
package org.kuali.kfs.module.purap.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetLocation;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.cam.CamsKeyConstants;
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
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.service.impl.PersistenceServiceStructureImplBase;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
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

    @Override
    public void setupCapitalAssetItems(PurchasingDocument purDoc) {

        List<PurchasingCapitalAssetItem> camsItemsList = purDoc.getPurchasingCapitalAssetItems();
        List<PurchasingCapitalAssetItem> newCamsItemsList = new ArrayList();

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

    protected PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
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


    protected PurchasingCapitalAssetItem getItemIfAlreadyInCamsItemsList(PurApItem item, List<PurchasingCapitalAssetItem> camsItemsList) {
        for (PurchasingCapitalAssetItem camsItem : camsItemsList) {
            if (camsItem.getItemIdentifier() != null && camsItem.getItemIdentifier().equals(item.getItemIdentifier())) {
                return camsItem;
            }
        }

        return null;
    }


    @Override
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

    @Override
    public void setupCapitalAssetSystem(PurchasingDocument purDoc) {
        CapitalAssetSystem resultSystem = purDoc.getDocumentSpecificService().createCapitalAssetSystem();
        if (purDoc.getCapitalAssetSystemTypeCode() != null && (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS))) {
            if (purDoc.getPurchasingCapitalAssetSystems().size() == 0) {
                purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
            }
        }
    }

    @Override
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

    @Override
    public String getDefaultAssetTypeCodeNotThisFiscalYear() {
        //FIXME (hjs) is this breaking modularization??
        return parameterService.getParameterValueAsString(KfsParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR);
    }

    @Override
    public boolean checkCapitalAssetLocation(CapitalAssetLocation location) {
        // if any of the date fields have a value AND one of them does not have a value...
        if (ObjectUtils.isNotNull(location) &&
                (StringUtils.isEmpty(location.getCapitalAssetLine1Address()) ||
                StringUtils.isEmpty(location.getCapitalAssetCityName()) ||
                StringUtils.isEmpty(location.getCapitalAssetCountryCode()) )) {
                String missingFields = "";
                if (StringUtils.isEmpty(location.getCapitalAssetLine1Address())) {
                    missingFields = "Address";
                    addErrorToCapitalAssetLocation(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ADDRESS_LINE1,missingFields);
                }
                if (StringUtils.isEmpty(location.getCapitalAssetCityName())) {
                    missingFields = "City";
                    addErrorToCapitalAssetLocation(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CITY,missingFields);
                }
                if (StringUtils.isEmpty(location.getCapitalAssetCountryCode())) {
                   missingFields = "Country";
                   addErrorToCapitalAssetLocation(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_COUNTRY,missingFields);
                }
                return false;
        }
        return true;
    }


    @Override
    public boolean checkValidRoomNumber(CapitalAssetLocation location){
        boolean valid = true;
        if (StringUtils.isNotBlank(location.getBuildingCode()) && StringUtils.isNotBlank(location.getBuildingRoomNumber())) {
            Map objectKeys = new HashMap();
            objectKeys.put(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CAMPUS, location.getCampusCode());
            objectKeys.put(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_BUILDING, location.getBuildingCode());
            objectKeys.put(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM, location.getBuildingRoomNumber());
            Room room = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, objectKeys);

            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getMessageMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER,location.getBuildingCode(),location.getBuildingRoomNumber(), location.getCampusCode());
                GlobalVariables.getMessageMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                valid &= false;
            }
            else if (!room.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Room.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_ROOM_NUMBER).getLabel();
                //GlobalVariables.getMessageMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, RiceKeyConstants.ERROR_INACTIVE, label);
                GlobalVariables.getMessageMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM, RiceKeyConstants.ERROR_INACTIVE, label);
                GlobalVariables.getMessageMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                valid &= false;
            }
        }else if (StringUtils.isBlank(location.getBuildingCode()) && StringUtils.isNotBlank(location.getBuildingRoomNumber())){
            Map objectKeys = new HashMap();
            objectKeys.put(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_CAMPUS, location.getCampusCode());
            objectKeys.put(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM, location.getBuildingRoomNumber());
            Room room = SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Room.class, objectKeys);

            if (ObjectUtils.isNull(room)) {
                GlobalVariables.getMessageMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM, CamsKeyConstants.AssetLocationGlobal.ERROR_INVALID_ROOM_NUMBER_FOR_CAMPUS,location.getBuildingRoomNumber(), location.getCampusCode());
                GlobalVariables.getMessageMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                valid &= false;
            }
            else if (!room.isActive()) {
                String label = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(Room.class.getName()).getAttributeDefinition(KFSPropertyConstants.BUILDING_ROOM_NUMBER).getLabel();
                GlobalVariables.getMessageMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                GlobalVariables.getMessageMap().putError(PurapPropertyConstants.CAPITAL_ASSET_LOCATION_ROOM, RiceKeyConstants.ERROR_INACTIVE, label);
                GlobalVariables.getMessageMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
                //GlobalVariables.getMessageMap().putError(PurapConstants.CAPITAL_ASSET_TAB_ERRORS, RiceKeyConstants.ERROR_INACTIVE, label);
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

    public void addErrorToCapitalAssetLocation(String path, String field) {

        GlobalVariables.getMessageMap().addToErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
        GlobalVariables.getMessageMap().putError(path,PurapKeyConstants.ERROR_CAPITAL_ASSET_INCOMPLETE_ADDRESS,field);
        GlobalVariables.getMessageMap().removeFromErrorPath(PurapPropertyConstants.NEW_PURCHASING_CAPITAL_ASSET_LOCATION_LINE);
   }


}
