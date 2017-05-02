package edu.arizona.kfs.module.cab.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import edu.arizona.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableItemAsset;
import org.kuali.kfs.module.cab.document.exception.PurApDocumentUnavailableException;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.CreditMemoItem;
import org.kuali.kfs.module.purap.businessobject.PaymentRequestItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurApInfoServiceImpl extends org.kuali.kfs.module.cab.document.service.impl.PurApInfoServiceImpl {
	
	@Override
	public void setAccountsPayableItemsFromPurAp(PurchasingAccountsPayableItemAsset purchasingAccountsPayableItemAsset, String docTypeCode) {
        Map<String, Object> pKeys = new HashMap<String, Object>();
        pKeys.put(PurapPropertyConstants.ITEM_IDENTIFIER, purchasingAccountsPayableItemAsset.getAccountsPayableLineItemIdentifier());

        // Access PurAp data based on item type(PREQ or CM).
        if (CabConstants.PREQ.equalsIgnoreCase(docTypeCode) || CabConstants.PRNC.equalsIgnoreCase(docTypeCode)) {
            // for PREQ document
            PaymentRequestItem item = (PaymentRequestItem) getBusinessObjectService().findByPrimaryKey(PaymentRequestItem.class, pKeys);
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
            CreditMemoItem item = (CreditMemoItem) getBusinessObjectService().findByPrimaryKey(CreditMemoItem.class, pKeys);
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
}