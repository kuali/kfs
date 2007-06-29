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
import org.kuali.module.purap.document.CreditMemoDocument;

/**
 * Item line Business Object for Credit Memo Document.
 */
public class CreditMemoItem extends AccountsPayableItemBase {
    private Integer creditMemoIdentifier;
    private Integer itemLineNumber;
    private String itemTypeCode;
    private KualiDecimal itemCreditQuantity;
    private BigDecimal itemUnitPrice;
    private KualiDecimal itemExtendedPrice;
    private boolean itemAssignedToTradeInIndicator;
    private String itemDescription;
    private KualiDecimal purchaseOrderInvoicedTotalQuantity;
    private BigDecimal purchaseOrderUnitPrice;
    private KualiDecimal purchaseOrderExtendedPrice;
    private KualiDecimal paymentRequestInvoicedTotalQuantity;
    private BigDecimal paymentRequestUnitPrice;
    private KualiDecimal paymentRequestExtendedPrice;
    
    private CreditMemoDocument creditMemo;

    /**
     * Default constructor.
     */
    public CreditMemoItem() {

    }

    /**
     * Gets the creditMemoIdentifier attribute.
     * 
     * @return Returns the creditMemoIdentifier
     */
    public Integer getCreditMemoIdentifier() {
        return creditMemoIdentifier;
    }

    /**
     * Sets the creditMemoIdentifier attribute.
     * 
     * @param creditMemoIdentifier The creditMemoIdentifier to set.
     */
    public void setCreditMemoIdentifier(Integer creditMemoIdentifier) {
        this.creditMemoIdentifier = creditMemoIdentifier;
    }


    /**
     * Gets the itemLineNumber attribute.
     * 
     * @return Returns the itemLineNumber
     */
    public Integer getItemLineNumber() {
        return itemLineNumber;
    }

    /**
     * Sets the itemLineNumber attribute.
     * 
     * @param itemLineNumber The itemLineNumber to set.
     */
    public void setItemLineNumber(Integer itemLineNumber) {
        this.itemLineNumber = itemLineNumber;
    }


    /**
     * Gets the itemTypeCode attribute.
     * 
     * @return Returns the itemTypeCode
     */
    public String getItemTypeCode() {
        return itemTypeCode;
    }

    /**
     * Sets the itemTypeCode attribute.
     * 
     * @param itemTypeCode The itemTypeCode to set.
     */
    public void setItemTypeCode(String itemTypeCode) {
        this.itemTypeCode = itemTypeCode;
    }


    /**
     * Gets the itemCreditQuantity attribute.
     * 
     * @return Returns the itemCreditQuantity
     */
    public KualiDecimal getItemCreditQuantity() {
        return itemCreditQuantity;
    }

    /**
     * Sets the itemCreditQuantity attribute.
     * 
     * @param itemCreditQuantity The itemCreditQuantity to set.
     */
    public void setItemCreditQuantity(KualiDecimal itemCreditQuantity) {
        this.itemCreditQuantity = itemCreditQuantity;
    }


    /**
     * Gets the itemUnitPrice attribute.
     * 
     * @return Returns the itemUnitPrice
     */
    public BigDecimal getItemUnitPrice() {
        return itemUnitPrice;
    }

    /**
     * Sets the itemUnitPrice attribute.
     * 
     * @param itemUnitPrice The itemUnitPrice to set.
     */
    public void setItemUnitPrice(BigDecimal itemUnitPrice) {
        this.itemUnitPrice = itemUnitPrice;
    }


    /**
     * Gets the itemExtendedPrice attribute.
     * 
     * @return Returns the itemExtendedPrice
     */
    public KualiDecimal getItemExtendedPrice() {
        return itemExtendedPrice;
    }

    /**
     * Sets the itemExtendedPrice attribute.
     * 
     * @param itemExtendedPrice The itemExtendedPrice to set.
     */
    public void setItemExtendedPrice(KualiDecimal itemExtendedPrice) {
        this.itemExtendedPrice = itemExtendedPrice;
    }


    /**
     * Gets the itemAssignedToTradeInIndicator attribute.
     * 
     * @return Returns the itemAssignedToTradeInIndicator
     */
    public boolean getItemAssignedToTradeInIndicator() {
        return itemAssignedToTradeInIndicator;
    }

    /**
     * Sets the itemAssignedToTradeInIndicator attribute.
     * 
     * @param itemAssignedToTradeInIndicator The itemAssignedToTradeInIndicator to set.
     */
    public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
        this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
    }

    /**
     * Gets the itemDescription attribute. 
     * @return Returns the itemDescription.
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * Sets the itemDescription attribute value.
     * @param itemDescription The itemDescription to set.
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    /**
     * Gets the purchaseOrderExtendedPrice attribute. 
     * @return Returns the purchaseOrderExtendedPrice.
     */
    public KualiDecimal getPurchaseOrderExtendedPrice() {
        return purchaseOrderExtendedPrice;
    }

    /**
     * Sets the purchaseOrderExtendedPrice attribute value.
     * @param purchaseOrderExtendedPrice The purchaseOrderExtendedPrice to set.
     */
    public void setPurchaseOrderExtendedPrice(KualiDecimal purchaseOrderExtendedPrice) {
        this.purchaseOrderExtendedPrice = purchaseOrderExtendedPrice;
    }

    /**
     * Gets the purchaseOrderInvoicedTotalQuantity attribute. 
     * @return Returns the purchaseOrderInvoicedTotalQuantity.
     */
    public KualiDecimal getPurchaseOrderInvoicedTotalQuantity() {
        return purchaseOrderInvoicedTotalQuantity;
    }

    /**
     * Sets the purchaseOrderInvoicedTotalQuantity attribute value.
     * @param purchaseOrderInvoicedTotalQuantity The purchaseOrderInvoicedTotalQuantity to set.
     */
    public void setPurchaseOrderInvoicedTotalQuantity(KualiDecimal purchaseOrderInvoicedTotalQuantity) {
        this.purchaseOrderInvoicedTotalQuantity = purchaseOrderInvoicedTotalQuantity;
    }

    /**
     * Gets the purchaseOrderUnitPrice attribute. 
     * @return Returns the purchaseOrderUnitPrice.
     */
    public BigDecimal getPurchaseOrderUnitPrice() {
        return purchaseOrderUnitPrice;
    }

    /**
     * Sets the purchaseOrderUnitPrice attribute value.
     * @param purchaseOrderUnitPrice The purchaseOrderUnitPrice to set.
     */
    public void setPurchaseOrderUnitPrice(BigDecimal purchaseOrderUnitPrice) {
        this.purchaseOrderUnitPrice = purchaseOrderUnitPrice;
    }

    /**
     * Gets the paymentRequestExtendedPrice attribute. 
     * @return Returns the paymentRequestExtendedPrice.
     */
    public KualiDecimal getPaymentRequestExtendedPrice() {
        return paymentRequestExtendedPrice;
    }

    /**
     * Sets the paymentRequestExtendedPrice attribute value.
     * @param paymentRequestExtendedPrice The paymentRequestExtendedPrice to set.
     */
    public void setPaymentRequestExtendedPrice(KualiDecimal paymentRequestExtendedPrice) {
        this.paymentRequestExtendedPrice = paymentRequestExtendedPrice;
    }

    /**
     * Gets the paymentRequestInvoicedTotalQuantity attribute. 
     * @return Returns the paymentRequestInvoicedTotalQuantity.
     */
    public KualiDecimal getPaymentRequestInvoicedTotalQuantity() {
        return paymentRequestInvoicedTotalQuantity;
    }

    /**
     * Sets the paymentRequestInvoicedTotalQuantity attribute value.
     * @param paymentRequestInvoicedTotalQuantity The paymentRequestInvoicedTotalQuantity to set.
     */
    public void setPaymentRequestInvoicedTotalQuantity(KualiDecimal paymentRequestInvoicedTotalQuantity) {
        this.paymentRequestInvoicedTotalQuantity = paymentRequestInvoicedTotalQuantity;
    }

    /**
     * Gets the paymentRequestUnitPrice attribute. 
     * @return Returns the paymentRequestUnitPrice.
     */
    public BigDecimal getPaymentRequestUnitPrice() {
        return paymentRequestUnitPrice;
    }

    /**
     * Sets the paymentRequestUnitPrice attribute value.
     * @param paymentRequestUnitPrice The paymentRequestUnitPrice to set.
     */
    public void setPaymentRequestUnitPrice(BigDecimal paymentRequestUnitPrice) {
        this.paymentRequestUnitPrice = paymentRequestUnitPrice;
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItemBase#getAccountingLineClass()
     */
    @Override
    public Class<CreditMemoAccount> getAccountingLineClass() {
        return CreditMemoAccount.class;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.getItemIdentifier() != null) {
            m.put("creditMemoItemIdentifier", this.getItemIdentifier().toString());
        }
        return m;
    }

    public CreditMemoDocument getCreditMemo() {
        return creditMemo;
    }
}
