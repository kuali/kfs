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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableLineAssetAccount;
import org.kuali.kfs.module.cab.document.service.PurApLineService;
import org.kuali.kfs.module.cab.document.web.struts.PurApLineForm;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

public class PurApLineServiceImpl implements PurApLineService {
    private static final Logger LOG = Logger.getLogger(PurApLineServiceImpl.class);
    private BusinessObjectService businessObjectService;

    public void setPurApInformation(PurApLineForm purApLineForm, HttpServletRequest request) {
        String purchaseOrderIdentifier = request.getParameter(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER);
        purApLineForm.setPurchaseOrderIdentifier(purchaseOrderIdentifier);

        setPurchaseOrderInfo(purApLineForm, purchaseOrderIdentifier);
        buildPurApItemAssetsList(purApLineForm, purchaseOrderIdentifier);


    }


    private String getFincialObjectCode(PurchasingAccountsPayableItemAsset item) {
        for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
            if (ObjectUtils.isNotNull(account.getGeneralLedgerEntry())) {
                return account.getGeneralLedgerEntry().getFinancialObjectCode();
            }
        }
        return "";
    }

    private void setPurchaseOrderInfo(PurApLineForm purApLineForm, String purchaseOrderIdentifier) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(PurapPropertyConstants.PURAP_DOC_ID, purchaseOrderIdentifier);
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


    private void buildPurApItemAssetsList(PurApLineForm purApLineForm, String purchaseOrderIdentifier) {
        Map<String, Object> cols = new HashMap<String, Object>();
        cols.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURCHASE_ORDER_IDENTIFIER, purchaseOrderIdentifier);
        Collection<PurchasingAccountsPayableDocument> purApDocs = businessObjectService.findMatching(PurchasingAccountsPayableDocument.class, cols);

        purApLineForm.getPurApDocs().addAll(purApDocs);

        List<PurchasingAccountsPayableItemAsset> tradeInItems;
        List<PurchasingAccountsPayableItemAsset> additionalChargeItems;
        List<PurchasingAccountsPayableItemAsset> assetItems;

        for (PurchasingAccountsPayableDocument purApDoc : purApDocs) {
            tradeInItems = purApDoc.getTradeInLineItems();
            additionalChargeItems = purApDoc.getAdditionalChargeLineItems();
            assetItems = purApDoc.getAssetLineItems();
            for (PurchasingAccountsPayableItemAsset item : purApDoc.getPurchasingAccountsPayableItemAssets()) {
                // set fields from PurAp tables
                setCabItemFieldsFromPurAp(item, purApDoc.getDocumentTypeCode());

                // classify items
                if (item.isTradeInIndicator()) {
                    tradeInItems.add(item);
                }
                else if (item.isAdditionalChargeNonTradeInIndicator()) {
                    additionalChargeItems.add(item);
                }
                else {
                    assetItems.add(item);
                }

                // Calculate and set total cost
                KualiDecimal totalCost = KualiDecimal.ZERO;
                for (PurchasingAccountsPayableLineAssetAccount account : item.getPurchasingAccountsPayableLineAssetAccounts()) {
                    totalCost = totalCost.add(account.getItemAccountTotalAmount());
                }
                item.setTotalCost(totalCost);
                
                // set unit cost
                KualiDecimal quantity = item.getAccountsPayableItemQuantity();
                if (quantity != null && quantity.isNonZero()) {
                    item.setUnitCost(totalCost.divide(quantity));
                }
                // set financial object code
                item.setFirstFincialObjectCode(getFincialObjectCode(item));
            }
        }

    }

    /**
     * Set lineItemNumber by PaymentRequestItem.itemLineNumber or CreditMemoItem.itemLineNumber.
     * 
     * @param purchasingAccountsPayableItemAsset
     * @param docTypeCode
     */
    private void setCabItemFieldsFromPurAp(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset, String docTypeCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(PurapPropertyConstants.ITEM_IDENTIFIER, purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier());
        // TODO: refactoring?
        if (CabConstants.PREQ.equalsIgnoreCase(docTypeCode)) {
            PaymentRequestItem item = (PaymentRequestItem) getBusinessObjectService().findByPrimaryKey(PaymentRequestItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
            }
            purchasingAccountsPayableItemAsset.setCapitalAssetDescription(item.getItemDescription());
            purchasingAccountsPayableItemAsset.setItemAssignedToTradeInIndicator(item.getItemAssignedToTradeInIndicator());
        }
        else {
            CreditMemoItem item = (CreditMemoItem) getBusinessObjectService().findByPrimaryKey(CreditMemoItem.class, pKeys);
            purchasingAccountsPayableItemAsset.setItemLineNumber(item.getItemLineNumber());
            if (item.getItemType() != null) {
                purchasingAccountsPayableItemAsset.setAdditionalChargeNonTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & !CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
                purchasingAccountsPayableItemAsset.setTradeInIndicator(item.getItemType().isItemTypeBelowTheLineIndicator() & CabConstants.TRADE_IN_TYPE_CODE.equalsIgnoreCase(item.getItemTypeCode()));
            }
            purchasingAccountsPayableItemAsset.setCapitalAssetDescription(item.getItemDescription());
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


}
