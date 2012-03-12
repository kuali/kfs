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
package org.kuali.kfs.module.cab.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.integration.purap.CapitalAssetSystem;
import org.kuali.kfs.integration.purap.ItemCapitalAsset;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.exception.PurApDocumentUnavailableException;
import org.kuali.kfs.module.cab.document.service.PurApInfoService;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetService;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchasingCapitalAssetItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
public class PurApInfoServiceImpl implements PurApInfoService {
    private static final Logger LOG = Logger.getLogger(PurApInfoServiceImpl.class);
    private BusinessObjectService businessObjectService;

    protected static final String PURCHASE_ORDER_CURRENT_INDICATOR = "purchaseOrderCurrentIndicator";

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApInfoService#getDocumentNumberForPurchaseOrderIdentifier(java.lang.Integer)
     */
    public PurchaseOrderDocument getCurrentDocumentForPurchaseOrderIdentifier(Integer poId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();

        fieldValues.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURAP_DOCUMENT_IDENTIFIER, poId);
        fieldValues.put(PURCHASE_ORDER_CURRENT_INDICATOR, "Y");
        Collection<PurchaseOrderDocument> poDocs = getBusinessObjectService().findMatching(PurchaseOrderDocument.class, fieldValues);
        if (poDocs != null && !poDocs.isEmpty()) {
            Iterator<PurchaseOrderDocument> poIterator = poDocs.iterator();
            if (poIterator.hasNext()) {
                return poIterator.next();
            }
        }

        return null;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#setPurchaseOrderInfo(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void setPurchaseOrderFromPurAp(PurApLineForm purApLineForm) {
        PurchaseOrderDocument purchaseOrderDocument = getCurrentDocumentForPurchaseOrderIdentifier(purApLineForm.getPurchaseOrderIdentifier());

        if (ObjectUtils.isNull(purchaseOrderDocument)) {
            return;
        }
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
    public void setCamsTransactionFromPurAp(List<PurchasingAccountsPayableDocument> purApDocs) {
        if (ObjectUtils.isNull(purApDocs) || purApDocs.isEmpty()) {
            return;
        }
        Integer poId = purApDocs.get(0).getPurchaseOrderIdentifier();
        PurchaseOrderDocument purApdocument = getCurrentDocumentForPurchaseOrderIdentifier(poId);
        if (ObjectUtils.isNull(purApdocument)) {
            return;
        }

        String capitalAssetSystemTypeCode = purApdocument.getCapitalAssetSystemTypeCode();
        String capitalAssetSystemStateCode = purApdocument.getCapitalAssetSystemStateCode();
        boolean individualItemLock = false;

        if (PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            // If PurAp sets the CAMS as INDIVIDUAL system
            setIndividualAssetsFromPurAp(poId, purApDocs, capitalAssetSystemStateCode);
            individualItemLock = true;
        }
        else if (PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            // If PurAp sets the CAMS as ONE system
            setOneSystemFromPurAp(poId, purApDocs, capitalAssetSystemStateCode);

        }
        else if (PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            // If PurAp sets the CAMS as MULTIPLE system
            setMultipleSystemFromPurAp(poId, purApDocs, capitalAssetSystemStateCode);
        }

        // Setting locking information based on capital asset system type code. Currently, only individual system can set asset
        // numbers for each item
        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            for (PurchasingAccountsPayableItemAsset itemAsset : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                itemAsset.setLockingInformation(individualItemLock ? itemAsset.getAccountsPayableLineItemIdentifier().toString() : CamsConstants.defaultLockingInformation);
            }
        }
    }

    /**
     * Set Multiple system capital asset transaction type code and asset numbers.
     * 
     * @param poId
     * @param purApDocs
     */
    protected void setMultipleSystemFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs, String capitalAssetSystemStateCode) {
        List<CapitalAssetSystem> capitalAssetSystems = this.getPurchaseOrderService().retrieveCapitalAssetSystemsForMultipleSystem(poId);
        if (ObjectUtils.isNotNull(capitalAssetSystems) && !capitalAssetSystems.isEmpty()) {
            // PurAp doesn't support multiple system asset information for KFS3.0. It works as one system for 3.0.
            CapitalAssetSystem capitalAssetSystem = capitalAssetSystems.get(0);
            if (ObjectUtils.isNotNull(capitalAssetSystem)) {
                String capitalAssetTransactionType = getCapitalAssetTransTypeForOneSystem(poId);
                // if modify existing asset, acquire the assets from Purap
                List<ItemCapitalAsset> purApCapitalAssets = null;
                if (PurapConstants.CapitalAssetSystemStates.MODIFY.equalsIgnoreCase(capitalAssetSystemStateCode)) {
                    purApCapitalAssets = getAssetsFromItemCapitalAsset(capitalAssetSystem.getItemCapitalAssets());
                }

                // set TransactionTypeCode, itemCapitalAssets and system identifier for each item
                for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
                    setItemAssetsCamsTransaction(capitalAssetSystem.getCapitalAssetSystemIdentifier(), capitalAssetTransactionType, purApCapitalAssets, purApDoc.getPurchasingAccountsPayableItemAssets());
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
    protected void setOneSystemFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs, String capitalAssetSystemStateCode) {
        CapitalAssetSystem capitalAssetSystem = this.getPurchaseOrderService().retrieveCapitalAssetSystemForOneSystem(poId);
        String capitalAssetTransactionTypeCode = getCapitalAssetTransTypeForOneSystem(poId);
        List<ItemCapitalAsset> purApCapitalAssets = null;
        // if modify existing asset, acquire the assets from Purap
        if (PurapConstants.CapitalAssetSystemStates.MODIFY.equalsIgnoreCase(capitalAssetSystemStateCode)) {
            purApCapitalAssets = getAssetsFromItemCapitalAsset(capitalAssetSystem.getItemCapitalAssets());
        }

        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            // set TransactionTypeCode, itemCapitalAssets and system identifier for each item
            setItemAssetsCamsTransaction(capitalAssetSystem.getCapitalAssetSystemIdentifier(), capitalAssetTransactionTypeCode, purApCapitalAssets, purApDoc.getPurchasingAccountsPayableItemAssets());
        }
    }

    /**
     * Update item assets by PurAp Cams Transaction setting
     * 
     * @param capitalAssetSystemIdentifier
     * @param capitRalAssetTransactionTypeCode
     * @param purApCapitalAssets
     * @param itemAssets
     */
    protected void setItemAssetsCamsTransaction(Integer capitalAssetSystemIdentifier, String capitalAssetTransactionTypeCode, List<ItemCapitalAsset> purApCapitalAssets, List<PurchasingAccountsPayableItemAsset> itemAssets) {
        for (PurchasingAccountsPayableItemAsset item : itemAssets) {
            item.setCapitalAssetTransactionTypeCode(capitalAssetTransactionTypeCode);
            // set for item capital assets
            if (purApCapitalAssets != null && !purApCapitalAssets.isEmpty()) {
                item.getPurApItemAssets().addAll(purApCapitalAssets);
            }
            // set for capital asset system ID
            item.setCapitalAssetSystemIdentifier(capitalAssetSystemIdentifier);
        }
    }

    /**
     * Get capitalAssetTransactionTypeCode for one system from PurAp.
     * 
     * @param poId
     * @return
     */
    protected String getCapitalAssetTransTypeForOneSystem(Integer poId) {
        PurchaseOrderDocument poDoc = getCurrentDocumentForPurchaseOrderIdentifier(poId);
        if (ObjectUtils.isNotNull(poDoc)) {
            List<PurchasingCapitalAssetItem> capitalAssetItems = poDoc.getPurchasingCapitalAssetItems();

            if (ObjectUtils.isNotNull(capitalAssetItems) && capitalAssetItems.get(0) != null) {
                return capitalAssetItems.get(0).getCapitalAssetTransactionTypeCode();
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
    protected void setIndividualAssetsFromPurAp(Integer poId, List<PurchasingAccountsPayableDocument> purApDocs, String capitalAssetSystemStateCode) {
        List<PurchasingCapitalAssetItem> capitalAssetItems = this.getPurchaseOrderService().retrieveCapitalAssetItemsForIndividual(poId);
        String capitalAssetTransactionTypeCode = null;
        List<ItemCapitalAsset> purApCapitalAssets = null;

        // For each capitalAssetItem from PurAp, we set it to all matching CAB items
        for (PurchasingCapitalAssetItem purchasingCapitalAssetItem : capitalAssetItems) {
            capitalAssetTransactionTypeCode = purchasingCapitalAssetItem.getCapitalAssetTransactionTypeCode();
            // get matching CAB items origin from the same PO item.
            List<PurchasingAccountsPayableItemAsset> matchingItems = getMatchingItems(purchasingCapitalAssetItem.getItemIdentifier(), purApDocs);
            // if modify existing asset, acquire the assets from Puraps
            if (PurapConstants.CapitalAssetSystemStates.MODIFY.equalsIgnoreCase(capitalAssetSystemStateCode)) {
                purApCapitalAssets = getAssetsFromItemCapitalAsset(purchasingCapitalAssetItem.getPurchasingCapitalAssetSystem().getItemCapitalAssets());
            }

            // set TransactionTypeCode, itemCapitalAssets and system identifier for each matching item
            setItemAssetsCamsTransaction(purchasingCapitalAssetItem.getCapitalAssetSystemIdentifier(), capitalAssetTransactionTypeCode, purApCapitalAssets, matchingItems);
        }
    }

    /**
     * Get asset number list from ItemCapitalAsset list.
     * 
     * @param itemCapitalAssets
     * @return
     */
    protected List<ItemCapitalAsset> getAssetsFromItemCapitalAsset(List<ItemCapitalAsset> itemCapitalAssets) {
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
    protected boolean isAssetNumberDuplicate(Long candidateNumber, List<ItemCapitalAsset> assetNumbers) {
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
    protected List<PurchasingAccountsPayableItemAsset> getMatchingItems(Integer itemIdentifier, List<PurchasingAccountsPayableDocument> purApDocs) {
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
            if (ObjectUtils.isNull(item)) {
                throw new PurApDocumentUnavailableException("PaymentRequestItem with id = " + purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier() + " doesn't exist in table.");
            }

            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isAdditionalChargeIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isAdditionalChargeIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
            // PurchaseOrderItemIdentifier will be used when populate PurAp asset data
            PurchaseOrderItem poi = item.getPurchaseOrderItem();
            if (poi != null) {
                purchasingAccountsPayableItemAsset.setPurchaseOrderItemIdentifier(poi.getItemIdentifier());
            }
        }
        else {
            // for CM document
            CreditMemoItem item = (CreditMemoItem) businessObjectService.findByPrimaryKey(CreditMemoItem.class, pKeys);
            if (ObjectUtils.isNull(item)) {
                throw new PurApDocumentUnavailableException("CreditMemoItem with id = " + purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier() + " doesn't exist in table.");
            }

            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isAdditionalChargeIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isAdditionalChargeIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setItemTypeCode(item.getItemTypeCode());
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
            // PurchaseOrderItemIdentifier will be used when populate PurAp asset data
            PurchaseOrderItem poi = getPurchaseOrderItemfromCreditMemoItem(item);
            if (poi != null) {
                purchasingAccountsPayableItemAsset.setPurchaseOrderItemIdentifier(poi.getItemIdentifier());
            }
            // if PREQ Credit Memo document
            VendorCreditMemoDocument cmDoc = (VendorCreditMemoDocument) item.getPurapDocument();
            if (ObjectUtils.isNotNull(cmDoc) && cmDoc.isSourceDocumentPaymentRequest()) {
                purchasingAccountsPayableItemAsset.setPaymentRequestIdentifier(cmDoc.getPaymentRequestIdentifier());
            }
        }
    }


    /**
     * Retreives a purchase order item for a given CreditMemoItem by inspecting the item type to see if its above the line or below
     * the line and returns the appropriate type.
     * 
     * @param item
     * @return
     */
    protected PurchaseOrderItem getPurchaseOrderItemfromCreditMemoItem(CreditMemoItem item) {
        if (ObjectUtils.isNotNull(item.getPurapDocumentIdentifier())) {
            if (ObjectUtils.isNull(item.getPurapDocument())) {
                item.refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
            }
        }
        // ideally we should do this a different way - maybe move it all into the service or save this info somehow (make sure and
        // update though)
        if (item.getPurapDocument() != null) {
            PurchaseOrderDocument po = ((VendorCreditMemoDocument) item.getPurapDocument()).getPurchaseOrderDocument();
            PurchaseOrderItem poi = null;
            if (item.getItemType().isLineItemIndicator()) {
                if (po.getItems() == null || po.getItems().isEmpty()) {
                    po.refreshReferenceObject("items");
                }
                poi = (PurchaseOrderItem) po.getItem(item.getItemLineNumber().intValue() - 1);
            }
            else {
                // To get the purchaseOrderItem by given CreditMemoItem. Since the additional charge type may be different in CM and
                // PO, there could be no PO Item for a given CM item.
                poi = (PurchaseOrderItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(po, item.getItemType());
            }
            if (poi != null) {
                return poi;
            }
            else {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getPurchaseOrderItemfromCreditMemoItem() Returning null because PurchaseOrderItem object for line number" + item.getItemLineNumber() + "or itemType " + item.getItemTypeCode() + " is null");
                }
                return null;
            }
        }
        else {

            LOG.error("getPurchaseOrderItemfromCreditMemoItem() Returning null because paymentRequest object is null");
            throw new PurError("Credit Memo Object in Purchase Order item line number " + item.getItemLineNumber() + "or itemType " + item.getItemTypeCode() + " is null");
        }
    }


    public List<Long> retrieveValidAssetNumberForLocking(Integer poId, String capitalAssetSystemTypeCode, PurApItem purApItem) {
        List<Long> capitalAssetNumbers = new ArrayList<Long>();
        CapitalAssetSystem capitalAssetSystem = null;

        if (PurapConstants.CapitalAssetTabStrings.INDIVIDUAL_ASSETS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            // If PurAp sets the CAMS as INDIVIDUAL system
            capitalAssetSystem = getCapitalAssetSystemForIndividual(poId, purApItem);

        }
        else if (PurapConstants.CapitalAssetTabStrings.ONE_SYSTEM.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            capitalAssetSystem = this.getPurchaseOrderService().retrieveCapitalAssetSystemForOneSystem(poId);
        }
        else if (PurapConstants.CapitalAssetTabStrings.MULTIPLE_SYSTEMS.equalsIgnoreCase(capitalAssetSystemTypeCode)) {
            List<CapitalAssetSystem> capitalAssetSystems = this.getPurchaseOrderService().retrieveCapitalAssetSystemsForMultipleSystem(poId);
            if (ObjectUtils.isNotNull(capitalAssetSystems) && !capitalAssetSystems.isEmpty()) {
                // PurAp doesn't support multiple system asset information for KFS3.0. It works as One system.
                capitalAssetSystem = capitalAssetSystems.get(0);
            }
        }

        if (ObjectUtils.isNotNull(capitalAssetSystem) && capitalAssetSystem.getItemCapitalAssets() != null && !capitalAssetSystem.getItemCapitalAssets().isEmpty()) {
            for (ItemCapitalAsset itemCapitalAsset : capitalAssetSystem.getItemCapitalAssets()) {
                if (itemCapitalAsset.getCapitalAssetNumber() != null) {
                    Map pKeys = new HashMap<String, Object>();
                    // Asset must be valid and capital active 'A','C','S','U'
                    pKeys.put(CamsPropertyConstants.Asset.CAPITAL_ASSET_NUMBER, itemCapitalAsset.getCapitalAssetNumber());

                    Asset asset = (Asset) businessObjectService.findByPrimaryKey(Asset.class, pKeys);
                    if (ObjectUtils.isNotNull(asset) && getAssetService().isCapitalAsset(asset) && !getAssetService().isAssetRetired(asset)) {
                        capitalAssetNumbers.add(itemCapitalAsset.getCapitalAssetNumber());
                    }
                }
            }
        }
        return capitalAssetNumbers;
    }

    protected CapitalAssetSystem getCapitalAssetSystemForIndividual(Integer poId, PurApItem purApItem) {
        List<PurchasingCapitalAssetItem> capitalAssetItems = this.getPurchaseOrderService().retrieveCapitalAssetItemsForIndividual(poId);
        if (capitalAssetItems == null || capitalAssetItems.isEmpty()) {
            return null;
        }

        Integer purchaseOrderItemIdentifier = null;
        PurchaseOrderItem poi = null;
        if (purApItem instanceof PaymentRequestItem) {
            poi = ((PaymentRequestItem) purApItem).getPurchaseOrderItem();

        }
        else if (purApItem instanceof CreditMemoItem) {
            poi = getPurchaseOrderItemfromCreditMemoItem((CreditMemoItem) purApItem);
        }

        if (poi != null) {
            purchaseOrderItemIdentifier = poi.getItemIdentifier();
        }
        for (PurchasingCapitalAssetItem capitalAssetItem : capitalAssetItems) {
            if (capitalAssetItem.getItemIdentifier().equals(purchaseOrderItemIdentifier)) {
                return capitalAssetItem.getPurchasingCapitalAssetSystem();
            }
        }
        return null;
    }

    protected AssetService getAssetService() {
        return SpringContext.getBean(AssetService.class);
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
        return SpringContext.getBean(PurchaseOrderService.class);
    }


}
