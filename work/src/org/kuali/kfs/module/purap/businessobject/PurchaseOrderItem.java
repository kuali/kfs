/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.purap.businessobject;

import static org.kuali.rice.core.api.util.type.KualiDecimal.ZERO;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.purap.PurapConstants;
import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Purchase Order Item Business Object.
 */
public class PurchaseOrderItem extends PurchasingItemBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurchaseOrderItem.class);

    private String documentNumber;
    private KualiDecimal itemInvoicedTotalQuantity;
    private KualiDecimal itemInvoicedTotalAmount;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemOutstandingEncumberedQuantity;
    private KualiDecimal itemOutstandingEncumberedAmount;
    private boolean itemActiveIndicator = true;
    private KualiDecimal itemDamagedTotalQuantity;
        
    private PurchaseOrderDocument purchaseOrder;
    
    // Not persisted to DB
    private boolean itemSelectedForRetransmitIndicator;
    private boolean movingToSplit;

    /**
     * Default constructor.
     */
    public PurchaseOrderItem() {

    }

    /**
     * Constructor.
     * 
     * @param ri - Requisition Item
     * @param po - Purchase Order Document
     */
    public PurchaseOrderItem(RequisitionItem ri, PurchaseOrderDocument po, RequisitionCapitalAssetItem reqCamsItem) {
        super();

        this.setPurchaseOrder(po);
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);
        Integer itemIdentifier = sas.getNextAvailableSequenceNumber("PO_ITM_ID", PurchaseOrderDocument.class).intValue();        
        this.setItemIdentifier(itemIdentifier);        
        this.setItemLineNumber(ri.getItemLineNumber());
        this.setItemUnitOfMeasureCode(ri.getItemUnitOfMeasureCode());
        this.setItemQuantity(ri.getItemQuantity());
        this.setItemCatalogNumber(ri.getItemCatalogNumber());
        this.setItemDescription(ri.getItemDescription());
        this.setItemUnitPrice(ri.getItemUnitPrice());
        this.setItemAuxiliaryPartIdentifier(ri.getItemAuxiliaryPartIdentifier());
        this.setItemAssignedToTradeInIndicator(ri.getItemAssignedToTradeInIndicator());        
        this.setItemTaxAmount( ri.getItemTaxAmount() );
        
        //copy use tax items over, and blank out keys (useTaxId and itemIdentifier)
        for (PurApItemUseTax useTaxItem : ri.getUseTaxItems()) {
            PurchaseOrderItemUseTax newItemUseTax = new PurchaseOrderItemUseTax(useTaxItem);
            newItemUseTax.setItemIdentifier(itemIdentifier);
            this.getUseTaxItems().add(newItemUseTax);

        }
        
        this.setExternalOrganizationB2bProductReferenceNumber(ri.getExternalOrganizationB2bProductReferenceNumber());
        this.setExternalOrganizationB2bProductTypeName(ri.getExternalOrganizationB2bProductTypeName());

        this.setItemReceivedTotalQuantity(ZERO);
        this.setItemDamagedTotalQuantity(ZERO);
        
        this.setItemTypeCode(ri.getItemTypeCode());

        if (ri.getSourceAccountingLines() != null && ri.getSourceAccountingLines().size() > 0 && 
                !StringUtils.equals(PurapConstants.ItemTypeCodes.ITEM_TYPE_ORDER_DISCOUNT_CODE,ri.getItemType().getItemTypeCode())) {
            List accounts = new ArrayList();
            for (PurApAccountingLine account : ri.getSourceAccountingLines()) {
                PurchaseOrderAccount poAccount = new PurchaseOrderAccount(account);
                poAccount.setPurchaseOrderItem(this);
                accounts.add(poAccount);
            }
            this.setSourceAccountingLines(accounts);
        }
        // By default, set the item active indicator to true.
        // In amendment, the user can set it to false when they click on
        // the inactivate button.
        this.setItemActiveIndicator(true);
        
        this.setPurchasingCommodityCode(ri.getPurchasingCommodityCode());
        this.setCommodityCode(getCommodityCode());
        
        // If the RequisitionItem has a CapitalAssetItem, create a new PurchasingCapitalAssetItem and add it to the PO.
        if( ObjectUtils.isNotNull(reqCamsItem) ) {
            PurchaseOrderCapitalAssetItem newPOCapitalAssetItem = new PurchaseOrderCapitalAssetItem(reqCamsItem, itemIdentifier);
            po.getPurchasingCapitalAssetItems().add(newPOCapitalAssetItem);
        }
    }
    
    public boolean isItemActiveIndicator() {
        return itemActiveIndicator;
    }
    
//    public String getItemActiveIndicator() {
//        return (new Boolean(itemActiveIndicator)).toString();
//    }

    public void setItemActiveIndicator(boolean itemActiveIndicator) {
        this.itemActiveIndicator = itemActiveIndicator;
    }

    public KualiDecimal getItemInvoicedTotalAmount() {
        return itemInvoicedTotalAmount;
    }

    public void setItemInvoicedTotalAmount(KualiDecimal itemInvoicedTotalAmount) {
        this.itemInvoicedTotalAmount = itemInvoicedTotalAmount;
    }

    public KualiDecimal getItemInvoicedTotalQuantity() {
        return itemInvoicedTotalQuantity;
    }

    public void setItemInvoicedTotalQuantity(KualiDecimal itemInvoicedTotalQuantity) {
        this.itemInvoicedTotalQuantity = itemInvoicedTotalQuantity;
    }

    public KualiDecimal getItemOutstandingEncumberedQuantity() {
        return itemOutstandingEncumberedQuantity;
    }

    public void setItemOutstandingEncumberedQuantity(KualiDecimal itemOutstandingEncumberedQuantity) {
        this.itemOutstandingEncumberedQuantity = itemOutstandingEncumberedQuantity;
    }

    public KualiDecimal getItemOutstandingEncumberedAmount() {
        return itemOutstandingEncumberedAmount;
    }

    public void setItemOutstandingEncumberedAmount(KualiDecimal itemOutstandingEncumbranceAmount) {
        this.itemOutstandingEncumberedAmount = itemOutstandingEncumbranceAmount;
    }

    public KualiDecimal getItemReceivedTotalQuantity() {
        return itemReceivedTotalQuantity;
    }

    public void setItemReceivedTotalQuantity(KualiDecimal itemReceivedTotalQuantity) {
        this.itemReceivedTotalQuantity = itemReceivedTotalQuantity;
    }

    /**
     * Gets the itemDamagedTotalQuantity attribute. 
     * @return Returns the itemDamagedTotalQuantity.
     */
    public KualiDecimal getItemDamagedTotalQuantity() {
        return itemDamagedTotalQuantity;
    }

    /**
     * Sets the itemDamagedTotalQuantity attribute value.
     * @param itemDamagedTotalQuantity The itemDamagedTotalQuantity to set.
     */
    public void setItemDamagedTotalQuantity(KualiDecimal itemDamagedTotalQuantity) {
        this.itemDamagedTotalQuantity = itemDamagedTotalQuantity;
    }

    public PurchaseOrderDocument getPurchaseOrder() {
        if (ObjectUtils.isNull(purchaseOrder)) {
            refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
        }
        return super.getPurapDocument();
    }

    public void setPurchaseOrder(PurchaseOrderDocument purchaseOrder) {
        setPurapDocument(purchaseOrder);
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public boolean isItemSelectedForRetransmitIndicator() {
        return itemSelectedForRetransmitIndicator;
    }

    public void setItemSelectedForRetransmitIndicator(boolean itemSelectedForRetransmitIndicator) {
        this.itemSelectedForRetransmitIndicator = itemSelectedForRetransmitIndicator;
    }    

    public boolean isMovingToSplit() {
        return movingToSplit;
    }

    public void setMovingToSplit(boolean movingToSplit) {
        this.movingToSplit = movingToSplit;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("documentNumber", this.documentNumber);
        if (this.getItemIdentifier() != null) {
            m.put("itemIdentifier", this.getItemIdentifier().toString());
        }
        return m;
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItem#getAccountingLineClass()
     */
    public Class getAccountingLineClass() {
        return PurchaseOrderAccount.class;
    }
    
    /**
     * 
     * This method returns the total item paid amount
     * @return
     */
    public KualiDecimal getItemPaidAmount() {
        if (!(this.isItemActiveIndicator())) {
            return KualiDecimal.ZERO;
        }
        return this.getItemInvoicedTotalAmount();
    }

    public KualiDecimal getItemEncumbranceRelievedAmount() {
        // check that it is active else return zero
        if (this == null || !this.isItemActiveIndicator()) {
            return KualiDecimal.ZERO;
        }
        // setup outstanding amount and get totalEncumberance from this.getExtendedCost()
        KualiDecimal outstandingAmount = KualiDecimal.ZERO;
        KualiDecimal totalEncumberance = this.getTotalAmount();

        ItemType iT = this.getItemType();
        // if service add the po outstanding amount to outstanding amount
        if (iT.isAmountBasedGeneralLedgerIndicator()) {
            outstandingAmount = outstandingAmount.add(this.getItemOutstandingEncumberedAmount());
        }
        else {
            // else add outstanding quantity * unit price
            BigDecimal qty = new BigDecimal(this.getOutstandingQuantity().toString());
            outstandingAmount = outstandingAmount.add(new KualiDecimal(this.getItemUnitPrice().multiply(qty).setScale(KualiDecimal.SCALE, KualiDecimal.ROUND_BEHAVIOR)));
            
            KualiDecimal itemTaxAmount = this.getItemTaxAmount() == null ? ZERO : this.getItemTaxAmount();
            KualiDecimal outstandingTaxAmount = new KualiDecimal(qty).divide(this.getItemQuantity()).multiply(itemTaxAmount);
            outstandingAmount = outstandingAmount.add(outstandingTaxAmount);
        }

        // return the total encumbrance subtracted by the outstanding amount from above
        return totalEncumberance.subtract(outstandingAmount);
    }
    
    /**
     * Exists due to a setter requirement by the htmlControlAttribute
     * @deprecated
     * @param amount - outstanding quantity
     */
    public void setOutstandingQuantity(){
        // do nothing
    }
    
    public KualiDecimal getOutstandingQuantity() {
            KualiDecimal outstandingQuantity = (this.getItemQuantity() != null) ? this.getItemQuantity() : KualiDecimal.ZERO;
            KualiDecimal invoicedQuantity = (this.getItemInvoicedTotalQuantity() != null) ? this.getItemInvoicedTotalQuantity() : KualiDecimal.ZERO;
            return outstandingQuantity.subtract(invoicedQuantity);
    }
    
    public boolean isCanInactivateItem() {
        if (versionNumber == null) {
            // don't allow newly added item to be inactivatable.
            return false;
        }
        else if (versionNumber != null && itemActiveIndicator && !getPurchaseOrder().getContainsUnpaidPaymentRequestsOrCreditMemos()) {
            return true;
        }
        return false;
    }
        
    /**
     * Override the method in PurApItemBase so that if the item is
     * not eligible to be displayed in the account summary tab,
     * which is if the item is inactive, we'll return null and
     * the item won't be added to the list of account summary.
     * 
     * @see org.kuali.kfs.module.purap.businessobject.PurApItemBase#getSummaryItem()
     */
    @Override
    public PurApSummaryItem getSummaryItem() {
        if (!this.itemActiveIndicator) {
            return null;
        }
        else {
            return super.getSummaryItem();
        }
    }

    public boolean isNewUnorderedItem(){
        return SpringContext.getBean(PurchaseOrderService.class).isNewUnorderedItem(this);
    }
    
    @Override
    public boolean isNewItemForAmendment() {
        return SpringContext.getBean(PurchaseOrderService.class).isNewItemForAmendment(this);
    }

    @Override
    public Class getUseTaxClass() {
        return PurchaseOrderItemUseTax.class;
    }
}
