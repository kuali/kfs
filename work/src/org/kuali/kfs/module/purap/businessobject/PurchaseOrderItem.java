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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PurchaseOrderDocument;

/**
 * 
 */
public class PurchaseOrderItem extends PurchasingItemBase {

    private String documentNumber;
    private KualiDecimal itemInvoicedTotalQuantity;
    private KualiDecimal itemInvoicedTotalAmount;
    private KualiDecimal itemReceivedTotalQuantity;
    private KualiDecimal itemReturnedTotalQuantity;
    private KualiDecimal itemOutstandingEncumberedQuantity;
    private KualiDecimal itemOutstandingEncumbranceAmount;
    private boolean itemActiveIndicator;
    private String purchaseOrderCommodityCd;

    private PurchaseOrderDocument purchaseOrder;

    // Not persisted to DB
    private boolean itemSelectedForRetransmitIndicator;

    /**
     * Default constructor.
     */
    public PurchaseOrderItem() {

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

}
