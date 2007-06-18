/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.purap.bo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * 
 */
public class PurchaseOrderItem extends PurchasingItemBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderItem.class);

    private String documentNumber;
    private KualiDecimal itemInvoicedTotalQuantity;
    private KualiDecimal itemInvoicedTotalAmount;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemReturnedTotalQuantity;
    private KualiDecimal itemOutstandingEncumberedQuantity;
    private KualiDecimal itemOutstandingEncumbranceAmount;
    private boolean itemActiveIndicator=true;
    private String purchaseOrderCommodityCd;

    private PurchaseOrderDocument purchaseOrder;

    // Not persisted to DB
    private boolean itemSelectedForRetransmitIndicator;

    /**
     * Default constructor.
     */
    public PurchaseOrderItem() {

    }

    public PurchaseOrderItem(RequisitionItem ri, PurchaseOrderDocument po) {
        super();
        
        this.setPurchaseOrder(po);

        this.setItemLineNumber(ri.getItemLineNumber());
        this.setItemUnitOfMeasureCode(ri.getItemUnitOfMeasureCode());
        this.setItemQuantity(ri.getItemQuantity());
        this.setItemCatalogNumber(ri.getItemCatalogNumber());
        this.setItemDescription(ri.getItemDescription());
        this.setItemCapitalAssetNoteText(ri.getItemCapitalAssetNoteText());
        this.setItemUnitPrice(ri.getItemUnitPrice());
        this.setItemAuxiliaryPartIdentifier(ri.getItemAuxiliaryPartIdentifier());
        this.setItemAssignedToTradeInIndicator(ri.getItemAssignedToTradeInIndicator());
        
        this.setExternalOrganizationB2bProductReferenceNumber(ri.getExternalOrganizationB2bProductReferenceNumber());
        this.setExternalOrganizationB2bProductTypeName(ri.getExternalOrganizationB2bProductTypeName());

        this.setCapitalAssetTransactionTypeCode(ri.getCapitalAssetTransactionTypeCode());
        this.setItemTypeCode(ri.getItemTypeCode());
        
        //to get around a null pointer error in ItemTypeCode
        //TODO: remove this after the new base copy is implemented here
        this.refreshNonUpdateableReferences();

        /* TODO: Uncomment these when we're ready with item capital asset in Kuali
        if (ri.getItemCapitalAssetNumbers() != null) {
            List assets = new ArrayList();
            for (Iterator assetIter = ri.getCapitalAssetNumbers().iterator(); assetIter.hasNext();) {
                RequisitionItemCapitalAsset reqAsset = (RequisitionItemCapitalAsset) assetIter.next();
                PurchaseOrderItemCapitalAsset poAsset = new PurchaseOrderItemCapitalAsset(reqAsset);
                poAsset.setPurchaseOrder(po);
                poAsset.setPurchaseOrderItem(this);
                assets.add(poAsset);
            }
            this.setCapitalAssetNumbers(assets);
        }
        */
        
        if (ri.getSourceAccountingLines() != null && ri.getSourceAccountingLines().size() > 0) {
            List accounts = new ArrayList();
            for (PurApAccountingLine account : ri.getSourceAccountingLines()) {
                PurchaseOrderAccount poAccount = new PurchaseOrderAccount(account);
                poAccount.setPurchaseOrderItem(this);
                accounts.add(poAccount);
            }
            this.setSourceAccountingLines(accounts);
        }
        //By default, set the item active indicator to true. 
        //In amendment, the user can set it to false when they click on 
        //the inactivate button.
        this.setItemActiveIndicator(true);
    }    

    public void prepareToSave() {
        List accounts = (List)this.getSourceAccountingLines();
//        Collections.sort(accounts);

        KualiDecimal accountTotalAmount = new KualiDecimal(0);
        PurchaseOrderAccount lastAccount = null;

        for (Iterator iterator = accounts.iterator(); iterator.hasNext();) {
            PurchaseOrderAccount account = (PurchaseOrderAccount) iterator.next();

            if (!account.isEmpty()) {
                KualiDecimal acctAmount = this.getExtendedPrice().multiply(new KualiDecimal(account.getAccountLinePercent().toString()));
//                acctAmount = acctAmount.divide(new KualiDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
                acctAmount = acctAmount.divide(new KualiDecimal(100));
                account.setAmount(acctAmount);
                account.setItemAccountOutstandingEncumbranceAmount(acctAmount);
                LOG.debug("getDisplayItems() account amount = " + account.getAmount());

                accountTotalAmount = accountTotalAmount.add(acctAmount);
                lastAccount = (PurchaseOrderAccount)ObjectUtils.deepCopy(account);
            }
        }

        // Rounding
//        if (lastAccount != null && this.getAmount() != null) {
//            KualiDecimal difference = this.getAmount().subtract(accountTotalAmount);
//            KualiDecimal tempAmount = lastAccount.getAmount();
//            lastAccount.setAmount(tempAmount.add(difference));
//        }

    }

    /**
     * Gets the itemActiveIndicator attribute.
     * 
     * @return Returns the itemActiveIndicator.
     */
    public boolean isItemActiveIndicator() {
        return itemActiveIndicator;
    }

    /**
     * Sets the itemActiveIndicator attribute value.
     * 
     * @param itemActiveIndicator The itemActiveIndicator to set.
     */
    public void setItemActiveIndicator(boolean itemActiveIndicator) {
        this.itemActiveIndicator = itemActiveIndicator;
    }

    /**
     * Gets the itemInvoicedTotalAmount attribute.
     * 
     * @return Returns the itemInvoicedTotalAmount.
     */
    public KualiDecimal getItemInvoicedTotalAmount() {
        return itemInvoicedTotalAmount;
    }

    /**
     * Sets the itemInvoicedTotalAmount attribute value.
     * 
     * @param itemInvoicedTotalAmount The itemInvoicedTotalAmount to set.
     */
    public void setItemInvoicedTotalAmount(KualiDecimal itemInvoicedTotalAmount) {
        this.itemInvoicedTotalAmount = itemInvoicedTotalAmount;
    }

    /**
     * Gets the itemInvoicedTotalQuantity attribute.
     * 
     * @return Returns the itemInvoicedTotalQuantity.
     */
    public KualiDecimal getItemInvoicedTotalQuantity() {
        return itemInvoicedTotalQuantity;
    }

    /**
     * Sets the itemInvoicedTotalQuantity attribute value.
     * 
     * @param itemInvoicedTotalQuantity The itemInvoicedTotalQuantity to set.
     */
    public void setItemInvoicedTotalQuantity(KualiDecimal itemInvoicedTotalQuantity) {
        this.itemInvoicedTotalQuantity = itemInvoicedTotalQuantity;
    }

    /**
     * Gets the itemOutstandingEncumberedQuantity attribute.
     * 
     * @return Returns the itemOutstandingEncumberedQuantity.
     */
    public KualiDecimal getItemOutstandingEncumberedQuantity() {
        return itemOutstandingEncumberedQuantity;
    }

    /**
     * Sets the itemOutstandingEncumberedQuantity attribute value.
     * 
     * @param itemOutstandingEncumberedQuantity The itemOutstandingEncumberedQuantity to set.
     */
    public void setItemOutstandingEncumberedQuantity(KualiDecimal itemOutstandingEncumberedQuantity) {
        this.itemOutstandingEncumberedQuantity = itemOutstandingEncumberedQuantity;
    }

    /**
     * Gets the itemOutstandingEncumbranceAmount attribute.
     * 
     * @return Returns the itemOutstandingEncumbranceAmount.
     */
    public KualiDecimal getItemOutstandingEncumbranceAmount() {
        return itemOutstandingEncumbranceAmount;
    }

    /**
     * Sets the itemOutstandingEncumbranceAmount attribute value.
     * 
     * @param itemOutstandingEncumbranceAmount The itemOutstandingEncumbranceAmount to set.
     */
    public void setItemOutstandingEncumbranceAmount(KualiDecimal itemOutstandingEncumbranceAmount) {
        this.itemOutstandingEncumbranceAmount = itemOutstandingEncumbranceAmount;
    }

    /**
     * Gets the itemReceivedTotalQuantity attribute.
     * 
     * @return Returns the itemReceivedTotalQuantity.
     */
    public KualiDecimal getItemReceivedTotalQuantity() {
        return itemReceivedTotalQuantity;
    }

    /**
     * Sets the itemReceivedTotalQuantity attribute value.
     * 
     * @param itemReceivedTotalQuantity The itemReceivedTotalQuantity to set.
     */
    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity) {
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
    }

    /**
     * Gets the itemReturnedTotalQuantity attribute.
     * 
     * @return Returns the itemReturnedTotalQuantity.
     */
    public KualiDecimal getItemReturnedTotalQuantity() {
        return itemReturnedTotalQuantity;
    }

    /**
     * Sets the itemReturnedTotalQuantity attribute value.
     * 
     * @param itemReturnedTotalQuantity The itemReturnedTotalQuantity to set.
     */
    public void setItemReturnedTotalQuantity(KualiDecimal itemReturnedTotalQuantity) {
        this.itemReturnedTotalQuantity = itemReturnedTotalQuantity;
    }

    /**
     * Gets the purchaseOrder attribute.
     * 
     * @return Returns the purchaseOrder.
     */
    public PurchaseOrderDocument getPurchaseOrder() {
        return purchaseOrder;
    }

    /**
     * Sets the purchaseOrder attribute value.
     * 
     * @param purchaseOrder The purchaseOrder to set.
     */
    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        this.purchaseOrder = purchaseOrder;
    }

    /**
     * Gets the purchaseOrderCommodityCd attribute.
     * 
     * @return Returns the purchaseOrderCommodityCd.
     */
    public String getPurchaseOrderCommodityCd() {
        return purchaseOrderCommodityCd;
    }

    /**
     * Sets the purchaseOrderCommodityCd attribute value.
     * 
     * @param purchaseOrderCommodityCd The purchaseOrderCommodityCd to set.
     */
    public void setPurchaseOrderCommodityCd(String purchaseOrderCommodityCd) {
        this.purchaseOrderCommodityCd = purchaseOrderCommodityCd;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public boolean isItemSelectedForRetransmitIndicator() {
        return itemSelectedForRetransmitIndicator;
    }

    public void setItemSelectedForRetransmitIndicator(boolean itemSelectedForRetransmitIndicator) {
        this.itemSelectedForRetransmitIndicator = itemSelectedForRetransmitIndicator;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.getItemIdentifier() != null) {
            m.put("itemIdentifier", this.getItemIdentifier().toString());
        }
        return m;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItemBase#getAccountingLineClass()
     */
    @Override
    public Class getAccountingLineClass() {
        return PurchaseOrderAccount.class;
    }
    
    @Override
    public boolean isCanInactivateItem() {
        if (purchaseOrder == null && versionNumber == null) {
            //don't allow newly added item to be inactivatable.
            return false;
        }
        else if (versionNumber!= null && isAmendmentStatus() && itemActiveIndicator && !purchaseOrder.getContainsUnpaidPaymentRequestsOrCreditMemos()) {
            return true;
        }
        return false;
    }

    private boolean isAmendmentStatus() {
        return purchaseOrder.getStatusCode().equals(PurapConstants.PurchaseOrderStatuses.AMENDMENT);
    }
}
