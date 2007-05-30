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
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.util.PurApObjectUtils;

/**
 * 
 */
public class PaymentRequestItem extends AccountsPayableItemBase {
    
	private KualiDecimal itemInvoicedQuantity;
	private BigDecimal purchaseOrderItemUnitPrice;
	private KualiDecimal itemExtendedPrice;
	private String purchaseOrderCommodityCode;
    private KualiDecimal itemOutstandingInvoiceQuantity;
    private KualiDecimal itemOutstandingInvoiceAmount;
    
    private PaymentRequestDocument paymentRequest;

	/**
	 * Default constructor.
	 */
	public PaymentRequestItem() {

	}

    /**
     * po constructor.
     */
    public PaymentRequestItem(PurchaseOrderItem poi,PaymentRequestDocument preq) {
        //copy base attributes w/ extra array of fields not to be copied
        PurApObjectUtils.populateFromBaseClass(PurApItemBase.class, poi, this, PurapConstants.ITEM_UNCOPYABLE_FIELDS);
        
        //set up accounts
        List accounts = new ArrayList();
        for (PurApAccountingLine account : poi.getSourceAccountingLines()) {
            PurchaseOrderAccount poa = (PurchaseOrderAccount)account;
            accounts.add(new PaymentRequestAccount(this,poa));
        }
        this.setSourceAccountingLines(accounts);
                
        //copy custom
        this.purchaseOrderItemUnitPrice = poi.getItemUnitPrice();
        this.purchaseOrderCommodityCode = poi.getPurchaseOrderCommodityCd();
        //set doc fields
        this.paymentRequest = preq;
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
	public PaymentRequestDocument getPaymentRequest() { 
		return paymentRequest;
	}

	/**
	 * Sets the paymentRequest attribute.
	 * 
	 * @param paymentRequest The paymentRequest to set.
	 * @deprecated
	 */
	public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

    
     /**
     * @see org.kuali.module.purap.bo.PurchasingItemBase#getAccountingLineClass()
     */
    @Override
    public Class getAccountingLineClass() {
        // TODO Auto-generated method stub
        return RequisitionAccount.class;
    }

}
