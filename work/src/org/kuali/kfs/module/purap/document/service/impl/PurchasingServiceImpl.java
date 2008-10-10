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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.ClassDescriptor;
import org.kuali.kfs.integration.cab.CapitalAssetBuilderModuleService;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapParameterConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchasingDocument;
import org.kuali.kfs.module.purap.document.service.PurchasingDocumentSpecificService;
import org.kuali.kfs.module.purap.document.service.PurchasingService;
import org.kuali.kfs.module.purap.service.PurapAccountingService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.kfs.sys.service.impl.ParameterConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.service.impl.PersistenceServiceStructureImplBase;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PurchasingServiceImpl extends PersistenceServiceStructureImplBase implements PurchasingService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchasingServiceImpl.class);

    private ParameterService parameterService;
    private SequenceAccessorService sequenceAccessorService;
    private PurapAccountingService purapAccountingService;
    private CapitalAssetBuilderModuleService capitalAssetBuilderModuleService;
    
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

    public void saveDocumentWithoutValidation(PurchasingDocument document) {
        document.getDocumentSpecificService().saveDocumentWithoutValidation(document);
    }

    public void setupCapitalAssetItems(PurchasingDocument purDoc) {

        List<PurchasingCapitalAssetItem> camsItemsList = purDoc.getPurchasingCapitalAssetItems();
        List<PurchasingCapitalAssetItem> newCamsItemsList = new TypedArrayList(purDoc.getPurchasingCapitalAssetItemClass());
        
        for (PurApItem purapItem : purDoc.getItems()) {
            if (purapItem.getItemType().isItemTypeAboveTheLineIndicator()) {
                if (capitalAssetBuilderModuleService.doesItemNeedCapitalAsset(purapItem)) {
                    PurchasingCapitalAssetItem camsItem = getItemIfAlreadyInCamsItemsList(purapItem, camsItemsList);
                    //If either the camsItem is null or if its system is null and the document's system type is IND (this is
                    //the case when the user tries to switch from ONE system type to IND system type), we'll have to create
                    //the camsItem again along with its system to prevent the No collection found error.
                    if (ObjectUtils.isNull(camsItem) || (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetSystemTypes.INDIVIDUAL) && ObjectUtils.isNull(camsItem.getPurchasingCapitalAssetSystem())) ) {
                        PurchasingCapitalAssetItem newCamsItem = createCamsItem(purDoc, purapItem);
                        newCamsItemsList.add(newCamsItem);
                    }
                    else {
                        //We need this next line so that the line item info is refreshed 
                        //within the Capital Asset tab each time the document content has 
                        //changed during editing to prevent KULPURAP-2926.
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
    }
    
    private PurchasingCapitalAssetItem createCamsItem(PurchasingDocument purDoc, PurApItem purapItem) {
        PurchasingDocumentSpecificService purchasingDocumentSpecificService = purDoc.getDocumentSpecificService();
        if (purapItem.getItemIdentifier() == null) {
            ClassDescriptor cd = this.getClassDescriptor(purapItem.getClass());
            String sequenceName = cd.getFieldDescriptorByName(PurapPropertyConstants.ITEM_IDENTIFIER).getSequenceName();
            Integer itemIdentifier = new Integer(sequenceAccessorService.getNextAvailableSequenceNumber(sequenceName).toString());
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
        if (purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM) || purDoc.getCapitalAssetSystemTypeCode().equals(PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS)) {
            if (purDoc.getPurchasingCapitalAssetSystems().size() == 0) {
                purDoc.getPurchasingCapitalAssetSystems().add(resultSystem);
            }
        }
    }    

    /**
     * 
     * @see org.kuali.kfs.module.purap.document.service.PurchasingService#prorateForTradeInAndFullOrderDiscount(org.kuali.kfs.module.purap.document.PurchasingDocument)
     */
    public void prorateForTradeInAndFullOrderDiscount(PurchasingDocument purDoc) {
        PurApItem fullOrderDiscount = null;
        PurApItem tradeIn = null;
        KualiDecimal totalAmount = KualiDecimal.ZERO;        

        List<PurApAccountingLine> distributedAccounts = null;
        List<SourceAccountingLine> summaryAccounts = null;
        
        // iterate through below the line and grab FoD and TrdIn.  
        for (PurApItem item : purDoc.getItems()) {
            if(item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE)) {
                fullOrderDiscount = item;
            } else if(item.getItemTypeCode().equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_TRADE_IN_CODE)) {
                tradeIn = item;
            }
        }
        //If Discount is not null or zero get proration list for all non misc items and set (if not empty?)
        if(fullOrderDiscount!=null && fullOrderDiscount.getExtendedPrice().isNonZero()) {
            //TODO: Chris check if we should update maybe use doc specific service since req is always update and po is only if empty
            fullOrderDiscount.getSourceAccountingLines().clear();

            totalAmount = purDoc.getTotalDollarAmountAllItems(new String[]{PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE});

            summaryAccounts = purapAccountingService.generateSummary(purDoc.getItems());

            distributedAccounts = purapAccountingService.generateAccountDistributionForProration(summaryAccounts, totalAmount, PurapConstants.PRORATION_SCALE, fullOrderDiscount.getAccountingLineClass());
            fullOrderDiscount.setSourceAccountingLines(distributedAccounts);
        }
        //TODO: If Trade in is not null or zero get proration list for all trade in selected items
        
    }
    
    public boolean getDefaultUseTaxIndicatorValue(PurchasingDocument purDoc) {        
        
        VendorDetail vendor = purDoc.getVendorDetail();
        if(vendor!=null) {
            String vendorStateCode = vendor.getDefaultAddressStateCode();
            String billingStateCode = purDoc.getBillingStateCode();
            if(StringUtils.equals(vendorStateCode, billingStateCode) ||
              (vendor.isTaxableIndicator())) { 
                return false;
            }
        } else {
            //don't set use tax if no vendor on req
            return true;
        }

        return true;
    }    
    
    public String getDefaultAssetTypeCodeNotThisFiscalYear() {
        return parameterService.getParameterValue(ParameterConstants.CAPITAL_ASSET_BUILDER_DOCUMENT.class, PurapParameterConstants.CapitalAsset.PURCHASING_DEFAULT_ASSET_TYPE_WHEN_NOT_THIS_FISCAL_YEAR);
    }

    public void clearAllTaxes(PurchasingDocument purDoc) {
        if (!purDoc.isUseTaxIndicator() && purDoc.getItems() != null){
            for (int i = 0; i < purDoc.getItems().size(); i++) {
                PurApItem item = purDoc.getItems().get(i);
                if (item.getItemType().isItemTypeAboveTheLineIndicator()){
                    item.setItemTaxAmount(null);
                }
            }
        }
    }
}
