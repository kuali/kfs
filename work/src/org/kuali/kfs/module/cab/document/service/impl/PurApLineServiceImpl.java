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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.dataaccess.PurApLineDao;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;


/**
 * This class provides default implementations of {@link PurApLineService}
 */
@Transactional
public class PurApLineServiceImpl implements PurApLineService {
    private static final Logger LOG = Logger.getLogger(PurApLineServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private PurApLineDao purApLineDao;

    /**
     * 
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processAdditionalChargeAllocate(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void processAdditionalChargeAllocate(PurchasingAccountsPayableItemAsset itemAsset) {
        // TODO Auto-generated method stub
        
    }
    
    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processPercentPayment(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public void processPercentPayment(PurchasingAccountsPayableItemAsset itemAsset) {
        KualiDecimal oldQty = itemAsset.getCapitalAssetBuilderQuantity();
        KualiDecimal newQty = new KualiDecimal(1);
        // update quantity and unit cost.
        if (oldQty.isLessThan(newQty)) {
            itemAsset.setCapitalAssetBuilderQuantity(newQty);
            // unit cost will be the same value as total cost since quantity is updated to 1.
            itemAsset.setUnitCost(calculateItemAssetTotalCost(itemAsset));
        }
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#processSplit(org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset)
     */
    public PurchasingAccountsPayableItemAsset processSplit(PurchasingAccountsPayableItemAsset currentItemAsset) {
        // create a new item asset from the current item asset.
        PurchasingAccountsPayableItemAsset newItemAsset = new PurchasingAccountsPayableItemAsset(currentItemAsset);

        Integer maxCabLineNbr = purApLineDao.getMaxCabLineNumber(currentItemAsset.getDocumentNumber(), currentItemAsset.getAccountsPayableLineItemIdentifier());
        newItemAsset.setCapitalAssetBuilderLineNumber(++maxCabLineNbr);
        newItemAsset.setAccountsPayableItemQuantity(currentItemAsset.getSplitQty());

        // Set account list for new item asset and update current account amount value.
        createAccountsForNewItemAsset(currentItemAsset, newItemAsset);

        // Set total cost and unit cost for new item asset
        KualiDecimal totalCost = calculateItemAssetTotalCost(newItemAsset);
        newItemAsset.setTotalCost(totalCost);
        setItemAssetUnitCost(newItemAsset, totalCost);

        // Adjust current item asset quantity, total cost and unit cost
        currentItemAsset.setAccountsPayableItemQuantity(currentItemAsset.getCapitalAssetBuilderQuantity().subtract(currentItemAsset.getSplitQty()));
        totalCost = calculateItemAssetTotalCost(currentItemAsset);
        currentItemAsset.setTotalCost(totalCost);
        setItemAssetUnitCost(currentItemAsset, totalCost);
        currentItemAsset.setSplitQty(null);

        return newItemAsset;
    }

    /**
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#saveBusinessObject(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void saveBusinessObjects(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            List<PurchasingAccountsPayableItemAsset> itemAssets = purApDoc.getPurchasingAccountsPayableItemAssets();
            if (itemAssets != null && !itemAssets.isEmpty()) {
                businessObjectService.save(itemAssets);
                for (PurchasingAccountsPayableItemAsset itemAsset : itemAssets) {
                    List<PurchasingAccountsPayableLineAssetAccount> assetAccounts = itemAsset.getPurchasingAccountsPayableLineAssetAccounts();
                    if (assetAccounts != null && !assetAccounts.isEmpty()) {
                        businessObjectService.save(assetAccounts);
                    }
                }
            }
        }

    }

    /**
     * Create asset account list for new item asset and update the current account amount.
     * 
     * @param oldItemAsset old line item.
     * @param newItemAsset new line item.
     */
    private void createAccountsForNewItemAsset(PurchasingAccountsPayableItemAsset currentItemAsset, PurchasingAccountsPayableItemAsset newItemAsset) {
        KualiDecimal currentQty = currentItemAsset.getCapitalAssetBuilderQuantity();
        KualiDecimal splitQty = currentItemAsset.getSplitQty();
        List<PurchasingAccountsPayableLineAssetAccount> accountsList = newItemAsset.getPurchasingAccountsPayableLineAssetAccounts();
        PurchasingAccountsPayableLineAssetAccount newAccount;
        for (PurchasingAccountsPayableLineAssetAccount currentAccount : currentItemAsset.getPurchasingAccountsPayableLineAssetAccounts()) {
            // create accounts for new item asset.
            newAccount = new PurchasingAccountsPayableLineAssetAccount(currentItemAsset, currentAccount.getGeneralLedgerAccountIdentifier());
            newAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().multiply(splitQty).divide(currentQty));
            newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.GENERAL_LEDGER_ENTRY);
            newAccount.refreshReferenceObject(CabPropertyConstants.PurchasingAccountsPayableLineAssetAccount.PURAP_ITEM_ASSET);
            accountsList.add(newAccount);

            // Adjust account amount for split item
            currentAccount.setItemAccountTotalAmount(currentAccount.getItemAccountTotalAmount().subtract(newAccount.getItemAccountTotalAmount()));
        }
    }


    /**
     * Set object code by the first one from the accounting lines.
     * 
     * @param item Selected line item.
     */
    private void setFirstFinancialObjectCode(PurchasingAccountsPayableItemAsset item) {
        String firstFinancialObjectCode = null;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            if (ObjectUtils.isNotNull(account.getGeneralLedgerEntry())) {
                firstFinancialObjectCode = account.getGeneralLedgerEntry().getFinancialObjectCode();
            }
        }
        item.setFirstFincialObjectCode(firstFinancialObjectCode);
    }


    /**
     * 
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#setPurchaseOrderInfo(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void setPurchaseOrderInfo(PurApLineForm purApLineForm) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(PurapPropertyConstants.PURAP_DOC_ID, purApLineForm.getPurchaseOrderIdentifier());
        cols.put(PurapPropertyConstants.PURCHASE_ORDER_CURRENT_INDICATOR, "Y");
        Collection<PurchaseOrderDocument> poDocs = businessObjectService.findMatching(PurchaseOrderDocument.class, cols);

        for (PurchaseOrderDocument purchaseOrderDocument : poDocs) {
            if (purchaseOrderDocument.getInstitutionContactEmailAddress() != null) {
                purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getInstitutionContactEmailAddress());
            }
            else if (purchaseOrderDocument.getRequestorPersonEmailAddress() != null) {
                purApLineForm.setPurApContactEmailAddress(purchaseOrderDocument.getRequestorPersonEmailAddress());
            }

            if (purchaseOrderDocument.getInstitutionContactPhoneNumber() != null) {
                purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getInstitutionContactPhoneNumber());
            }
            else if (purchaseOrderDocument.getRequestorPersonPhoneNumber() != null) {
                purApLineForm.setPurApContactPhoneNumber(purchaseOrderDocument.getRequestorPersonPhoneNumber());
            }

            break;
        }
    }


    /**
     * 
     * @see org.kuali.kfs.module.cab.document.service.PurApLineService#buildPurApItemAssetsList(org.kuali.kfs.module.cab.document.web.struts.PurApLineForm)
     */
    public void buildPurApItemAssetsList(PurApLineForm purApLineForm) {
        for (PurchasingAccountsPayableDocument purApDoc : purApLineForm.getPurApDocs()) {
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // set fields from PurAp tables
                setCabItemFieldsFromPurAp(item, purApDoc.getDocumentTypeCode());

                // set total cost and unit cost
                KualiDecimal totalCost = calculateItemAssetTotalCost(item);
                item.setTotalCost(totalCost);
                setItemAssetUnitCost(item, totalCost);

                // set financial object code
                setFirstFinancialObjectCode(item);

                // set additional charge indicator in the form
                if (item.isAdditionalChargeNonTradeInIndicator() && !purApLineForm.isAdditionalChargeIndicator()) {
                    purApLineForm.setAdditionalChargeIndicator(true);
                }
            }

            Collections.sort(purApDoc.getPurchasingAccountsPayableItemAssets());
        }

    }

    /**
     * Set item asset unit cost.
     * 
     * @param item line item
     * @param totalCost total cost for this line item.
     */
    private void setItemAssetUnitCost(PurchasingAccountsPayableItemAsset item, KualiDecimal totalCost) {
        // set unit cost
        KualiDecimal quantity = item.getAccountsPayableItemQuantity();
        if (quantity != null && quantity.isNonZero()) {
            item.setUnitCost(totalCost.divide(quantity));
        }
    }

    /**
     * Calculate item asset total cost
     * 
     * @param item
     * @return line item total cost
     */
    public KualiDecimal calculateItemAssetTotalCost(PurchasingAccountsPayableItemAsset item) {
        // Calculate and set total cost
        KualiDecimal totalCost = KualiDecimal.ZERO;
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            totalCost = totalCost.add(account.getItemAccountTotalAmount());
        }
        return totalCost;
    }


    /**
     * Set CAB line item information from PurAp PaymentRequestItem or CreditMemoItem.
     * 
     * @param purchasingAccountsPayableItemAsset
     * @param docTypeCode
     */
    private void setCabItemFieldsFromPurAp(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset, String docTypeCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(PurapPropertyConstants.ITEM_IDENTIFIER, purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier());

        // Access PurAp data based on item type(PREQ or CM).
        if (CabConstants.PREQ.equalsIgnoreCase(docTypeCode)) {
            PaymentRequestItem item = (PaymentRequestItem) businessObjectService.findByPrimaryKey(PaymentRequestItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isItemTypeBelowTheLineIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
        }
        else {
            CreditMemoItem item = (CreditMemoItem) businessObjectService.findByPrimaryKey(CreditMemoItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInAllowance(item.getItemType().isItemTypeBelowTheLineIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
            }
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
        }
        pKeys.clear();
    }


    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public PurApLineDao getPurApLineDao() {
        return purApLineDao;
    }

    public void setPurApLineDao(PurApLineDao purApLineDao) {
        this.purApLineDao = purApLineDao;
    }


}
