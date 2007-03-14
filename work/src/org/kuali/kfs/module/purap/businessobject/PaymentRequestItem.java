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

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class PaymentRequestItem extends AccountsPayableItemBase {

	private Integer paymentRequestItemIdentifier;
	private Integer paymentRequestIdentifier;
	private Integer itemLineNumber;
	private String itemTypeCode;
	private String itemDescription;
	private String itemCatalogNumber;
	private String itemAuxiliaryPartIdentifier;
	private String itemUnitOfMeasureCode;
	private KualiDecimal itemInvoicedQuantity;
	private BigDecimal itemUnitPrice;
	private BigDecimal purchaseOrderItemUnitPrice;
	private KualiDecimal itemExtendedPrice;
	private String capitalAssetTransactionTypeCode;
	private String itemCapitalAssetNoteText;
	private String purchaseOrderCommodityCode;
	private boolean itemAssignedToTradeInIndicator;
    private KualiDecimal itemOutstandingInvoiceQuantity;
    private KualiDecimal itemOutstandingInvoiceAmount;
    
    private PaymentRequest paymentRequest;

	/**
	 * Default constructor.
	 */
	public PaymentRequestItem() {

	}

	/**
	 * Gets the paymentRequestItemIdentifier attribute.
	 * 
	 * @return Returns the paymentRequestItemIdentifier
	 * 
	 */
	public Integer getPaymentRequestItemIdentifier() { 
		return paymentRequestItemIdentifier;
	}

	/**
	 * Sets the paymentRequestItemIdentifier attribute.
	 * 
	 * @param paymentRequestItemIdentifier The paymentRequestItemIdentifier to set.
	 * 
	 */
	public void setPaymentRequestItemIdentifier(Integer paymentRequestItemIdentifier) {
		this.paymentRequestItemIdentifier = paymentRequestItemIdentifier;
	}


	/**
	 * Gets the paymentRequestIdentifier attribute.
	 * 
	 * @return Returns the paymentRequestIdentifier
	 * 
	 */
	public Integer getPaymentRequestIdentifier() { 
		return paymentRequestIdentifier;
	}

	/**
	 * Sets the paymentRequestIdentifier attribute.
	 * 
	 * @param paymentRequestIdentifier The paymentRequestIdentifier to set.
	 * 
	 */
	public void setPaymentRequestIdentifier(Integer paymentRequestIdentifier) {
		this.paymentRequestIdentifier = paymentRequestIdentifier;
	}


	/**
	 * Gets the itemLineNumber attribute.
	 * 
	 * @return Returns the itemLineNumber
	 * 
	 */
	public Integer getItemLineNumber() { 
		return itemLineNumber;
	}

	/**
	 * Sets the itemLineNumber attribute.
	 * 
	 * @param itemLineNumber The itemLineNumber to set.
	 * 
	 */
	public void setItemLineNumber(Integer itemLineNumber) {
		this.itemLineNumber = itemLineNumber;
	}


	/**
	 * Gets the itemTypeCode attribute.
	 * 
	 * @return Returns the itemTypeCode
	 * 
	 */
	public String getItemTypeCode() { 
		return itemTypeCode;
	}

	/**
	 * Sets the itemTypeCode attribute.
	 * 
	 * @param itemTypeCode The itemTypeCode to set.
	 * 
	 */
	public void setItemTypeCode(String itemTypeCode) {
		this.itemTypeCode = itemTypeCode;
	}


	/**
	 * Gets the itemDescription attribute.
	 * 
	 * @return Returns the itemDescription
	 * 
	 */
	public String getItemDescription() { 
		return itemDescription;
	}

	/**
	 * Sets the itemDescription attribute.
	 * 
	 * @param itemDescription The itemDescription to set.
	 * 
	 */
	public void setItemDescription(String itemDescription) {
		this.itemDescription = itemDescription;
	}


	/**
	 * Gets the itemCatalogNumber attribute.
	 * 
	 * @return Returns the itemCatalogNumber
	 * 
	 */
	public String getItemCatalogNumber() { 
		return itemCatalogNumber;
	}

	/**
	 * Sets the itemCatalogNumber attribute.
	 * 
	 * @param itemCatalogNumber The itemCatalogNumber to set.
	 * 
	 */
	public void setItemCatalogNumber(String itemCatalogNumber) {
		this.itemCatalogNumber = itemCatalogNumber;
	}


	/**
	 * Gets the itemAuxiliaryPartIdentifier attribute.
	 * 
	 * @return Returns the itemAuxiliaryPartIdentifier
	 * 
	 */
	public String getItemAuxiliaryPartIdentifier() { 
		return itemAuxiliaryPartIdentifier;
	}

	/**
	 * Sets the itemAuxiliaryPartIdentifier attribute.
	 * 
	 * @param itemAuxiliaryPartIdentifier The itemAuxiliaryPartIdentifier to set.
	 * 
	 */
	public void setItemAuxiliaryPartIdentifier(String itemAuxiliaryPartIdentifier) {
		this.itemAuxiliaryPartIdentifier = itemAuxiliaryPartIdentifier;
	}


	/**
	 * Gets the itemUnitOfMeasureCode attribute.
	 * 
	 * @return Returns the itemUnitOfMeasureCode
	 * 
	 */
	public String getItemUnitOfMeasureCode() { 
		return itemUnitOfMeasureCode;
	}

	/**
	 * Sets the itemUnitOfMeasureCode attribute.
	 * 
	 * @param itemUnitOfMeasureCode The itemUnitOfMeasureCode to set.
	 * 
	 */
	public void setItemUnitOfMeasureCode(String itemUnitOfMeasureCode) {
		this.itemUnitOfMeasureCode = itemUnitOfMeasureCode;
	}


	/**
	 * Gets the itemInvoicedQuantity attribute.
	 * 
	 * @return Returns the itemInvoicedQuantity
	 * 
	 */
	public KualiDecimal getItemInvoicedQuantity() { 
		return itemInvoicedQuantity;
	}

	/**
	 * Sets the itemInvoicedQuantity attribute.
	 * 
	 * @param itemInvoicedQuantity The itemInvoicedQuantity to set.
	 * 
	 */
	public void setItemInvoicedQuantity(KualiDecimal itemInvoicedQuantity) {
		this.itemInvoicedQuantity = itemInvoicedQuantity;
	}


	/**
	 * Gets the itemUnitPrice attribute.
	 * 
	 * @return Returns the itemUnitPrice
	 * 
	 */
	public BigDecimal getItemUnitPrice() { 
		return itemUnitPrice;
	}

	/**
	 * Sets the itemUnitPrice attribute.
	 * 
	 * @param itemUnitPrice The itemUnitPrice to set.
	 * 
	 */
	public void setItemUnitPrice(BigDecimal itemUnitPrice) {
		this.itemUnitPrice = itemUnitPrice;
	}


	/**
	 * Gets the purchaseOrderItemUnitPrice attribute.
	 * 
	 * @return Returns the purchaseOrderItemUnitPrice
	 * 
	 */
	public BigDecimal getPurchaseOrderItemUnitPrice() { 
		return purchaseOrderItemUnitPrice;
	}

	/**
	 * Sets the purchaseOrderItemUnitPrice attribute.
	 * 
	 * @param purchaseOrderItemUnitPrice The purchaseOrderItemUnitPrice to set.
	 * 
	 */
	public void setPurchaseOrderItemUnitPrice(BigDecimal purchaseOrderItemUnitPrice) {
		this.purchaseOrderItemUnitPrice = purchaseOrderItemUnitPrice;
	}


	/**
	 * Gets the itemExtendedPrice attribute.
	 * 
	 * @return Returns the itemExtendedPrice
	 * 
	 */
	public KualiDecimal getItemExtendedPrice() { 
		return itemExtendedPrice;
	}

	/**
	 * Sets the itemExtendedPrice attribute.
	 * 
	 * @param itemExtendedPrice The itemExtendedPrice to set.
	 * 
	 */
	public void setItemExtendedPrice(KualiDecimal itemExtendedPrice) {
		this.itemExtendedPrice = itemExtendedPrice;
	}


	/**
	 * Gets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @return Returns the capitalAssetTransactionTypeCode
	 * 
	 */
	public String getCapitalAssetTransactionTypeCode() { 
		return capitalAssetTransactionTypeCode;
	}

	/**
	 * Sets the capitalAssetTransactionTypeCode attribute.
	 * 
	 * @param capitalAssetTransactionTypeCode The capitalAssetTransactionTypeCode to set.
	 * 
	 */
	public void setCapitalAssetTransactionTypeCode(String capitalAssetTransactionTypeCode) {
		this.capitalAssetTransactionTypeCode = capitalAssetTransactionTypeCode;
	}


	/**
	 * Gets the itemCapitalAssetNoteText attribute.
	 * 
	 * @return Returns the itemCapitalAssetNoteText
	 * 
	 */
	public String getItemCapitalAssetNoteText() { 
		return itemCapitalAssetNoteText;
	}

	/**
	 * Sets the itemCapitalAssetNoteText attribute.
	 * 
	 * @param itemCapitalAssetNoteText The itemCapitalAssetNoteText to set.
	 * 
	 */
	public void setItemCapitalAssetNoteText(String itemCapitalAssetNoteText) {
		this.itemCapitalAssetNoteText = itemCapitalAssetNoteText;
	}


	/**
	 * Gets the purchaseOrderCommodityCode attribute.
	 * 
	 * @return Returns the purchaseOrderCommodityCode
	 * 
	 */
	public String getPurchaseOrderCommodityCode() { 
		return purchaseOrderCommodityCode;
	}

	/**
	 * Sets the purchaseOrderCommodityCode attribute.
	 * 
	 * @param purchaseOrderCommodityCode The purchaseOrderCommodityCode to set.
	 * 
	 */
	public void setPurchaseOrderCommodityCode(String purchaseOrderCommodityCode) {
		this.purchaseOrderCommodityCode = purchaseOrderCommodityCode;
	}


	/**
	 * Gets the itemAssignedToTradeInIndicator attribute.
	 * 
	 * @return Returns the itemAssignedToTradeInIndicator
	 * 
	 */
	public boolean getItemAssignedToTradeInIndicator() { 
		return itemAssignedToTradeInIndicator;
	}

	/**
	 * Sets the itemAssignedToTradeInIndicator attribute.
	 * 
	 * @param itemAssignedToTradeInIndicator The itemAssignedToTradeInIndicator to set.
	 * 
	 */
	public void setItemAssignedToTradeInIndicator(boolean itemAssignedToTradeInIndicator) {
		this.itemAssignedToTradeInIndicator = itemAssignedToTradeInIndicator;
	}

    /**
     * Gets the itemOutstandingInvoiceAmount attribute. 
     * @return Returns the itemOutstandingInvoiceAmount.
     */
    public KualiDecimal getItemOutstandingInvoiceAmount() {
        return itemOutstandingInvoiceAmount;
    }

    /**
     * Sets the itemOutstandingInvoiceAmount attribute value.
     * @param itemOutstandingInvoiceAmount The itemOutstandingInvoiceAmount to set.
     */
    public void setItemOutstandingInvoiceAmount(KualiDecimal itemOutstandingInvoiceAmount) {
        this.itemOutstandingInvoiceAmount = itemOutstandingInvoiceAmount;
    }

    /**
     * Gets the itemOutstandingInvoiceQuantity attribute. 
     * @return Returns the itemOutstandingInvoiceQuantity.
     */
    public KualiDecimal getItemOutstandingInvoiceQuantity() {
        return itemOutstandingInvoiceQuantity;
    }

    /**
     * Sets the itemOutstandingInvoiceQuantity attribute value.
     * @param itemOutstandingInvoiceQuantity The itemOutstandingInvoiceQuantity to set.
     */
    public void setItemOutstandingInvoiceQuantity(KualiDecimal itemOutstandingInvoiceQuantity) {
        this.itemOutstandingInvoiceQuantity = itemOutstandingInvoiceQuantity;
    }
    
	/**
	 * Gets the paymentRequest attribute.
	 * 
	 * @return Returns the paymentRequest
	 * 
	 */
	public PaymentRequest getPaymentRequest() { 
		return paymentRequest;
	}

	/**
	 * Sets the paymentRequest attribute.
	 * 
	 * @param paymentRequest The paymentRequest to set.
	 * @deprecated
	 */
	public void setPaymentRequest(PaymentRequest paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

	/**
	 * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
	 */
	protected LinkedHashMap toStringMapper() {
	    LinkedHashMap m = new LinkedHashMap();	    
        if (this.paymentRequestItemIdentifier != null) {
            m.put("paymentRequestItemIdentifier", this.paymentRequestItemIdentifier.toString());
        }
	    return m;
    }
}
