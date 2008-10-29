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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.CreditMemoDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
public class PurApInfoServiceImpl implements PurApInfoService {
    private static final Logger LOG = Logger.getLogger(PurApInfoServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private PurchaseOrderService purchaseOrderService;


    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#setPurchaseOrderInfo(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void setPurchaseOrderFromPurAp(PurApLineForm purApLineForm) {
        PurchaseOrderDocument purchaseOrderDocument = purchaseOrderService.getCurrentPurchaseOrder(purApLineForm.getPurchaseOrderIdentifier());
        // Set contact email address.
        if (purchaseOrderDocument.getInstitutionContactEmailAddress() != null) {
            purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getInstitutionContactEmailAddress());
        }
        else if (purchaseOrderDocument.getRequestorPersonEmailAddress() != null) {
            purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getRequestorPersonEmailAddress());
        }

        // Set contact phone number.
        if (purchaseOrderDocument.getInstitutionContactPhoneNumber() != null) {
            purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getInstitutionContactPhoneNumber());
        }
        else if (purchaseOrderDocument.getRequestorPersonPhoneNumber() != null) {
            purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getRequestorPersonPhoneNumber());
        }

        // set reqs_id
        purApLineForm.setRequisitionIdentifier(purchaseOrderDocument.getRequisitionIdentifier());

    }


    /**
     * Set CAMS transaction type code the user entered in PurAp
     * 
     * @param poId
     */
    public void setCamsTransactionFromPurAp(PurApLineForm purApLineForm) {
        Integer poId = purApLineForm.getPurchaseOrderIdentifier();
        PurchaseOrderDocument purApdocument = (PurchaseOrderDocument) purchaseOrderService.getCurrentPurchaseOrder(poId);
        String capitalAssetSystemTypeCode = purApdocument.getCapitalAssetSystemTypeCode();
        String capitalAssetSystemStateCode = purApdocument.getCapitalAssetSystemStateCode();

        if (PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            setIndividualAssetsFromPurAp(poId, purApLineForm.getPurApDocs(), capitalAssetSystemStateCode);
        }
        else if (PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            setOneSystemFromPurAp(poId, purApLineForm.getPurApDocs(), capitalAssetSystemStateCode);

        }
        else if (PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            setMultipleSystemFromPurAp(poId, purApLineForm.getPurApDocs(), capitalAssetSystemStateCode);
        }
    }

    /**
     * Set Multiple system capital asset transaction type code and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    private void setMultipleSystemFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs, String capitalAssetSystemStateCode) {
        List<CapitalAssetSystem> capitalAssetSystems = purchaseOrderService.retrieveCapitalAssetSystemsForMultipleSystem(poId);
        if (ObjectUtils.isNotNull(capitalAssetSystems)) {
            // TODO: currently PurAp multiple system in fact return one system.
            CapitalAssetSystem capitalAssetSystem = capitalAssetSystems.get(0);
            if (ObjectUtils.isNotNull(capitalAssetSystem)) {
                String capitalAssetTransactionType = getCapitalAssetTransTypeForOneSystem(poId);
                // if modify existing asset, acquire the assets from Purap
                List<ItemCapitalAsset> purApCapitalAssets = null;
                if (PurapConstants.CapitalAssetSystemStates.MODIFY.equalsIgnoreCase(capitalAssetSystemStateCode)) {
                    purApCapitalAssets = getAssetsFromItemCapitalAsset(capitalAssetSystem.getItemCapitalAssets());
                }

                if (StringUtils.isNotBlank(capitalAssetTransactionType) || (purApCapitalAssets != null && !purApCapitalAssets.isEmpty())) {
                    for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                        for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                            // TODO : Purap will add this field to PREQ/CM item.
                            item.setCapitalAssetTransactionTypeCode(capitalAssetTransactionType);
                            // set for item capital assets
                            if (purApCapitalAssets != null && !purApCapitalAssets.isEmpty()) {
                                item.getPurApItemAssets().addAll(purApCapitalAssets);
                            }
                            // set for capital asset system ID
                            item.setCapitalAssetSystemIdentifier(capitalAssetSystem.getCapitalAssetSystemIdentifier());
                        }
                    }
                }
            }
        }
    }

    /**
     * Set One System capital asset transaction type code and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    private void setOneSystemFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs, String capitalAssetSystemStateCode) {
        CapitalAssetSystem capitalAssetSystem = purchaseOrderService.retrieveCapitalAssetSystemForOneSystem(poId);
        String capitalAssetTransactionType = getCapitalAssetTransTypeForOneSystem(poId);
        List<ItemCapitalAsset> purApCapitalAssets = null;
        // if modify existing asset, acquire the assets from Purap
        if (PurapConstants.CapitalAssetSystemStates.MODIFY.equalsIgnoreCase(capitalAssetSystemStateCode)) {
            purApCapitalAssets = getAssetsFromItemCapitalAsset(capitalAssetSystem.getItemCapitalAssets());
        }

        if (StringUtils.isNotBlank(capitalAssetTransactionType) || (purApCapitalAssets != null && !purApCapitalAssets.isEmpty())) {
            for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                    // TODO : Purap will add this field to PREQ/CM item.
                    item.setCapitalAssetTransactionTypeCode(capitalAssetTransactionType);
                    // set for item capital assets
                    if (purApCapitalAssets != null && !purApCapitalAssets.isEmpty()) {
                        item.getPurApItemAssets().addAll(purApCapitalAssets);
                    }
                    // set for capital asset system ID
                    item.setCapitalAssetSystemIdentifier(capitalAssetSystem.getCapitalAssetSystemIdentifier());
                }
            }
        }
    }

    /**
     * Get capitalAssetTransactionTypeCode for one system from PurAp.
     * 
     * @param poId
     * @return
     */
    private String getCapitalAssetTransTypeForOneSystem(Integer poId) {
        PurchaseOrderDocument poDoc = purchaseOrderService.getCurrentPurchaseOrder(poId);
        if (ObjectUtils.isNotNull(poDoc)) {
            List<PurchasingCapitalAssetItem> capitalAssetItems = poDoc.getPurchasingCapitalAssetItems();

            if (ObjectUtils.isNotNull(capitalAssetItems) && capitalAssetItems.get(0) != null) {
                capitalAssetItems.get(0).getCapitalAssetTransactionTypeCode();
            }
        }
        return null;
    }

    /**
     * Set Individual system asset transaction type and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    private void setIndividualAssetsFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs, String capitalAssetSystemStateCode) {
        List<PurchasingCapitalAssetItem> capitalAssetItems = purchaseOrderService.retrieveCapitalAssetItemsForIndividual(poId);
        String capitalAssetTransactionTypeCode = null;
        List<ItemCapitalAsset> purApCapitalAssets = null;

        for (PurchasingCapitalAssetItem purchasingCapitalAssetItem : capitalAssetItems) {
            // TODO: PURAP will add this field into PREQ/CM item.
            capitalAssetTransactionTypeCode = purchasingCapitalAssetItem.getCapitalAssetTransactionTypeCode();
            // get matching purchasingAccountsPayableItemAssets
            List<PurchasingAccountsPayableItemAsset> matchingItems = getMatchingItems(purchasingCapitalAssetItem.getItemIdentifier(), purApDocs);
            // if modify existing asset, acquire the assets from Puraps
            if (PurapConstants.CapitalAssetSystemStates.MODIFY.equalsIgnoreCase(capitalAssetSystemStateCode)) {
                purApCapitalAssets = getAssetsFromItemCapitalAsset(purchasingCapitalAssetItem.getPurchasingCapitalAssetSystem().getItemCapitalAssets());
            }

            // set capitalAssetTransactionTypeCode, itemCapitalAssets or locations for each matching item
            for (PurchasingAccountsPayableItemAsset itemAsset : matchingItems) {
                itemAsset.setCapitalAssetTransactionTypeCode(capitalAssetTransactionTypeCode);

                // set for item capital assets
                if (purApCapitalAssets != null && !purApCapitalAssets.isEmpty()) {
                    itemAsset.getPurApItemAssets().addAll(purApCapitalAssets);
                }

                // set for capital asset system ID
                itemAsset.setCapitalAssetSystemIdentifier(purchasingCapitalAssetItem.getCapitalAssetSystemIdentifier());
            }
        }
    }

    /**
     * Get asset number list from ItemCapitalAsset list.
     * 
     * @param itemCapitalAssets
     * @return
     */
    private List<ItemCapitalAsset> getAssetsFromItemCapitalAsset(List<ItemCapitalAsset> itemCapitalAssets) {
        List<ItemCapitalAsset> assetNumbers = new ArrayList<ItemCapitalAsset>();

        for (ItemCapitalAsset asset : itemCapitalAssets) {
            if (asset.getCapitalAssetNumber() != null && !isAssetNumberDuplicate(asset.getCapitalAssetNumber(), assetNumbers)) {
                assetNumbers.add(asset);
            }
        }
        return assetNumbers;
    }

    /**
     * Check if given capitalAssetNumber is an duplicate number in assetNumbers list.
     * 
     * @param capitalAssetNumber
     * @param assetNumbers
     * @return
     */
    private boolean isAssetNumberDuplicate(Long candidateNumber, List<ItemCapitalAsset> assetNumbers) {
        for (ItemCapitalAsset existingNumber : assetNumbers) {
            if (existingNumber.getCapitalAssetNumber().equals(candidateNumber)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finding out the matching PREQ/CM items originating from the same PurchaseOrderItem.
     * 
     * @param itemIdentifier
     * @param purApDocs
     * @return
     */
    private List getMatchingItems(Integer itemIdentifier, List<PurchasingAccountsPayableDocument> purApDocs) {
        List<PurchasingAccountsPayableItemAsset> matchingItems = new ArrayList<PurchasingAccountsPayableItemAsset>();

        if (itemIdentifier != null) {
            for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                    if (itemIdentifier.equals(item.getPurchaseOrderItemIdentifier())) {
                        matchingItems.add(item);
                    }
                }
            }
        }
        return matchingItems;
    }


    /**
     * Set CAB line item information from PurAp PaymentRequestItem or CreditMemoItem.
     * 
     * @param purchasingAccountsPayableItemAsset
     * @param docTypeCode
     */
    public void setAccountsPayableItemsFromPurAp(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset, String docTypeCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(PurapPropertyConstants.ITEM_IDENTIFIER, purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier());

        // Access PurAp data based on item type(PREQ or CM).
        if (CabConstants.PREQ.equalsIgnoreCase(docTypeCode)) {
            // for PREQ document
            PaymentRequestItem item = (PaymentRequestItem) businessObjectService.findByPrimaryKey(PaymentRequestItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isAdditionalChargeIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isAdditionalChargeIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
            PurchaseOrderItem poi = item.getPurchaseOrderItem();
            if (poi != null) {
                purchasingAccountsPayableItemAsset.setPurchaseOrderItemIdentifier(poi.getItemIdentifier());
            }
        }
        else {
            // for CM document
            CreditMemoItem item = (CreditMemoItem) businessObjectService.findByPrimaryKey(CreditMemoItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isAdditionalChargeIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isAdditionalChargeIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
            PurchaseOrderItem poi = getPurchaseOrderItemfromCreditMemoItem(item);
            if (poi != null) {
                purchasingAccountsPayableItemAsset.setPurchaseOrderItemIdentifier(poi.getItemIdentifier());
            }
        }
        pKeys.clear();
    }


    /**
     * Retreives a purchase order item for a given CreditMemoItem by inspecting the item type to see if its above the line or below
     * the line and returns the appropriate type.
     * 
     * @param item
     * @return
     */
    private PurchaseOrderItem getPurchaseOrderItemfromCreditMemoItem(CreditMemoItem item) {
        if (ObjectUtils.isNotNull(item.getPurapDocumentIdentifier())) {
            if (ObjectUtils.isNull(item.getPurapDocument())) {
                item.refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
            }
        }
        // ideally we should do this a different way - maybe move it all into the service or save this info somehow (make sure and
        // update though)
        if (item.getPurapDocument() != null) {
            PurchaseOrderDocument po = ((CreditMemoDocument) item.getPurapDocument()).getPurchaseOrderDocument();
            PurchaseOrderItem poi = null;
            if (item.getItemType().isLineItemIndicator()) {
                poi = (PurchaseOrderItem) po.getItem(item.getItemLineNumber().intValue() - 1);
            }
            else {
                poi = (PurchaseOrderItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(item.getPurapDocument(), item.getItemType());
            }
            if (poi != null) {
                return poi;
            }
            else {
                LOG.debug("getPurchaseOrderItemfromCreditMemoItem() Returning null because PurchaseOrderItem object for line number" + item.getItemLineNumber() + "or itemType " + item.getItemTypeCode() + " is null");
                return null;
            }
        }
        else {

            LOG.error("getPurchaseOrderItemfromCreditMemoItem() Returning null because paymentRequest object is null");
            throw new PurError("Credit Memo Object in Purchase Order item line number " + item.getItemLineNumber() + "or itemType " + item.getItemTypeCode() + " is null");
        }
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }


    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the purchaseOrderService attribute.
     * 
     * @return Returns the purchaseOrderService.
     */
    public PurchaseOrderService getPurchaseOrderService() {
        return purchaseOrderService;
    }

    /**
     * Sets the purchaseOrderService attribute value.
     * 
     * @param purchaseOrderService The purchaseOrderService to set.
     */
    public void setPurchaseOrderService(PurchaseOrderService purchaseOrderService) {
        this.purchaseOrderService = purchaseOrderService;
    }


}
