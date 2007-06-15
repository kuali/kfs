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
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.util.PurApObjectUtils;
import org.apache.commons.lang.StringUtils;


/**
 * 
 */
public class PaymentRequestItem extends AccountsPayableItemBase {
    
    //note: the qty from PurApItemBase is invoiceQty
    
    //  TODO: we should get rid of this and use regular qty from the super
    private KualiDecimal itemInvoicedQuantity;
	
    private BigDecimal purchaseOrderItemUnitPrice;
	private KualiDecimal itemExtendedPrice;
	private String purchaseOrderCommodityCode;
    private KualiDecimal itemOutstandingInvoiceQuantity;
    private KualiDecimal itemOutstandingInvoiceAmount;
    
    private PaymentRequestDocument paymentRequest;

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestItem.class);

    
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
        this.refreshNonUpdateableReferences();
        //copy custom
        this.purchaseOrderItemUnitPrice = poi.getItemUnitPrice();
        this.purchaseOrderCommodityCode = poi.getPurchaseOrderCommodityCd();
        //set doc fields
        this.setPurapDocumentIdentifier(preq.getPurapDocumentIdentifier());
        this.paymentRequest = preq;
    }

    public PurchaseOrderItem getPurchaseOrderItem() {
        if (paymentRequest != null) {
          PurchaseOrderDocument po = paymentRequest.getPurchaseOrderDocument();
          PurchaseOrderItem poi = (PurchaseOrderItem)po.getItem(this.getItemLineNumber().intValue());
          if (poi != null) {
            return poi;
          } else {
            LOG.warn("getPurchaseOrderItem() Returning null because PurchaseOrderItem object for line number" + getItemLineNumber() + " is null");
            return null;
          }
        } else {
          LOG.error("getPurchaseOrderItem() Returning null because paymentRequest object is null");
          throw new PurError("Payment Request Object in Purchase Order item line number " + getItemLineNumber() + " is null");
        }
      }
    
    public KualiDecimal getPoOutstandingAmount() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        return this.getPoOutstandingAmount(poi);
    }

    private KualiDecimal getPoOutstandingAmount(PurchaseOrderItem poi) {
        if ( poi == null ) {
          return KualiDecimal.ZERO;
        } else {
          return poi.getItemOutstandingEncumbranceAmount();
        }
    }
    
    public KualiDecimal getPoOriginalAmount() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        if ( poi == null ) {
          return null;
        } else {
          return poi.getExtendedPrice();
        }    
      }

    public KualiDecimal getPoOutstandingQuantity() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        if ( poi == null ) {
          return null;
        } else {
          return getPoOutstandingQuantity(poi);
        }
    }

    /** 
     * This method is here due to a setter requirement by the htmlControlAttribute
     * @param amount
     */
    public void setPoOutstandingQuantity(KualiDecimal qty){
        //do nothing
    }
    
    public KualiDecimal getPoOutstandingQuantity(PurchaseOrderItem poi) {
        if(poi == null) {
            return KualiDecimal.ZERO;
        } else {
            KualiDecimal outstandingQuantity = (poi.getItemQuantity()!=null)?poi.getItemQuantity():KualiDecimal.ZERO;
            KualiDecimal invoicedQuantity = (poi.getItemInvoicedTotalQuantity()!=null)?poi.getItemInvoicedTotalQuantity():KualiDecimal.ZERO;
            return outstandingQuantity.subtract(invoicedQuantity);
        }
    }

    public KualiDecimal getPurchaseOrderItemPaidAmount() {
        PurchaseOrderItem poi = this.getPurchaseOrderItem();
        if((poi==null)||(poi.isItemActiveIndicator())){
            return KualiDecimal.ZERO;
        }
        return poi.getItemInvoicedTotalAmount();
    }
    
    
    public KualiDecimal getPurchaseOrderItemEncumbranceRelievedAmount() {
        //get po item
        PurchaseOrderItem poi = getPurchaseOrderItem();
        //check that it is active else return zero
        if(poi==null || poi.isItemActiveIndicator()) {
            return KualiDecimal.ZERO;
        }
        //setup outstanding amount and get totalEncumberance from poi.getExtendedCost()
        KualiDecimal outstandingAmount = KualiDecimal.ZERO;
        KualiDecimal totalEncumberance = poi.getExtendedPrice();
        
        ItemType iT = poi.getItemType();
        //if service add the po outstanding amount to outstandingamount 
        if(StringUtils.equals(iT.getItemTypeCode(),PurapConstants.ItemTypeCodes.ITEM_TYPE_SERVICE_CODE)) {
            outstandingAmount.add(poi.getItemOutstandingEncumbranceAmount());
        } else {
            //else add outstanding quantity * unitprice
            BigDecimal qty = new BigDecimal(this.getPoOutstandingQuantity(poi).toString());
            outstandingAmount = outstandingAmount.add(new KualiDecimal(poi.getItemUnitPrice().multiply(qty)));
        }
        
        
        //return the total encumberance subtracted by the outstandingamount from above
        return totalEncumberance.subtract(outstandingAmount);
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
        return PaymentRequestAccount.class;
    }

}
