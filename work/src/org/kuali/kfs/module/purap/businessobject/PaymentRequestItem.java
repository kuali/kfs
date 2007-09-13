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
import java.util.HashMap;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.PurapConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.purap.exceptions.PurError;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.service.PurapService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.module.purap.util.PurApItemUtils;
import org.kuali.module.purap.util.PurApObjectUtils;

public class PaymentRequestItem extends AccountsPayableItemBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestItem.class);
    
    private BigDecimal purchaseOrderItemUnitPrice;
	private String purchaseOrderCommodityCode;
    private KualiDecimal itemOutstandingInvoiceQuantity;
    private KualiDecimal itemOutstandingInvoiceAmount;
    
    private transient PaymentRequestDocument paymentRequest;
    
	/**
	 * Default constructor.
	 */
	public PaymentRequestItem() {

	}

    /**
     * preq item constructor.
     */
    public PaymentRequestItem(PurchaseOrderItem poi,PaymentRequestDocument preq) {
        //copy base attributes w/ extra array of fields not to be copied
        PurApObjectUtils.populateFromBaseClass(PurApItemBase.class, poi, this, PurapConstants.PREQ_ITEM_UNCOPYABLE_FIELDS);
        
        //set up accounts
        List accounts = new ArrayList();
        for (PurApAccountingLine account : poi.getSourceAccountingLines()) {
            PurchaseOrderAccount poa = (PurchaseOrderAccount)account;
            accounts.add(new PaymentRequestAccount(this,poa));
        }
        this.setSourceAccountingLines(accounts);

        //clear amount and desc on below the line - we probably don't need that null 
        //itemType check but it's there just in case remove if it causes problems
        if(ObjectUtils.isNotNull(this.getItemType())&&
           !this.getItemType().isItemTypeAboveTheLineIndicator()) {
            //setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
            this.setItemDescription("");
        }
        //copy custom
        this.purchaseOrderItemUnitPrice = poi.getItemUnitPrice();
        this.purchaseOrderCommodityCode = poi.getPurchaseOrderCommodityCd();
        //set doc fields
        this.setPurapDocumentIdentifier(preq.getPurapDocumentIdentifier());
        this.paymentRequest = preq;
    }

    /**
     * This method constructs a new payment request item, but also merges expired accounts.
     *  
     * @param poi
     * @param preq
     * @param expiredOrClosedAccountList
     */
    public PaymentRequestItem(PurchaseOrderItem poi,PaymentRequestDocument preq, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        //TODO (KULPURAP-1575) Merge this method with the other constructor. cleanup
        
        //copy base attributes w/ extra array of fields not to be copied
        PurApObjectUtils.populateFromBaseClass(PurApItemBase.class, poi, this, PurapConstants.PREQ_ITEM_UNCOPYABLE_FIELDS);
        
        //set up accounts
        List accounts = new ArrayList();
        for (PurApAccountingLine account : poi.getSourceAccountingLines()) {
            PurchaseOrderAccount poa = (PurchaseOrderAccount)account;
            
            //check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(poa, expiredOrClosedAccountList);
            
            accounts.add(new PaymentRequestAccount(this,poa));
        }
        this.setSourceAccountingLines(accounts);

        //clear amount and desc on below the line - we probably don't need that null 
        //itemType check but it's there just in case remove if it causes problems
        if(ObjectUtils.isNotNull(this.getItemType())&&
           !this.getItemType().isItemTypeAboveTheLineIndicator()) {
            //setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
            this.setItemDescription("");
        }        
        //copy custom
        this.purchaseOrderItemUnitPrice = poi.getItemUnitPrice();
        this.purchaseOrderCommodityCode = poi.getPurchaseOrderCommodityCd();
        //set doc fields
        this.setPurapDocumentIdentifier(preq.getPurapDocumentIdentifier());
        this.paymentRequest = preq;
    }

    public PurchaseOrderItem getPurchaseOrderItem() {
        // TODO (KULPURAP-1393) look into, this is total hackery but works for now, revisit during QA
        if (ObjectUtils.isNotNull(this.getPurapDocumentIdentifier())) {
            if (ObjectUtils.isNull(this.getPaymentRequest())) {
                this.refreshReferenceObject(PurapPropertyConstants.PAYMENT_REQUEST);
            }
        }
        // ideally we should do this a different way - maybe move it all into the service or save this info somehow (make sure and update though)
        if (getPaymentRequest() != null) {
            PurchaseOrderDocument po = getPaymentRequest().getPurchaseOrderDocument();
            PurchaseOrderItem poi = null;
            if (this.getItemType().isItemTypeAboveTheLineIndicator()) {
                poi = (PurchaseOrderItem) po.getItem(this.getItemLineNumber().intValue() - 1);
            }
            else {
                poi = (PurchaseOrderItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(po, this.getItemType());
            }
            if (poi != null) {
                return poi;
            }
            else {
                LOG.warn("getPurchaseOrderItem() Returning null because PurchaseOrderItem object for line number" + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
                return null;
            }
        }
        else {

            LOG.error("getPurchaseOrderItem() Returning null because paymentRequest object is null");
            throw new PurError("Payment Request Object in Purchase Order item line number " + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
        }
    }
    
    public KualiDecimal getPoOutstandingAmount() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        return this.getPoOutstandingAmount(poi);
    }

    private KualiDecimal getPoOutstandingAmount(PurchaseOrderItem poi) {
        if (poi == null) {
            return KualiDecimal.ZERO;
        }
        else {
            return poi.getItemOutstandingEncumberedAmount();
        }
    }
    
    public KualiDecimal getPoOriginalAmount() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        if (poi == null) {
            return null;
        }
        else {
            return poi.getExtendedPrice();
        }
    }

    /** 
     * This method is here due to a setter requirement by the htmlControlAttribute
     * @param amount
     */
    public void setPoOutstandingAmount(KualiDecimal amount){
        //do nothing
    }

    
    public KualiDecimal getPoOutstandingQuantity() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        if (poi == null) {
            return null;
        }
        else {
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
        if (poi == null) {
            return KualiDecimal.ZERO;
        }
        else {
            KualiDecimal outstandingQuantity = (poi.getItemQuantity() != null) ? poi.getItemQuantity() : KualiDecimal.ZERO;
            KualiDecimal invoicedQuantity = (poi.getItemInvoicedTotalQuantity() != null) ? poi.getItemInvoicedTotalQuantity() : KualiDecimal.ZERO;
            return outstandingQuantity.subtract(invoicedQuantity);
        }
    }

    public KualiDecimal getPurchaseOrderItemPaidAmount() {
        PurchaseOrderItem poi = this.getPurchaseOrderItem();
        if ((poi == null) || !(poi.isItemActiveIndicator())) {
            return KualiDecimal.ZERO;
        }
        return poi.getItemInvoicedTotalAmount();
    }
    
    
    public KualiDecimal getPurchaseOrderItemEncumbranceRelievedAmount() {
        //get po item
        PurchaseOrderItem poi = getPurchaseOrderItem();
        //check that it is active else return zero
        if(poi==null || !poi.isItemActiveIndicator()) {
            return KualiDecimal.ZERO;
        }
        //setup outstanding amount and get totalEncumberance from poi.getExtendedCost()
        KualiDecimal outstandingAmount = KualiDecimal.ZERO;
        KualiDecimal totalEncumberance = poi.getExtendedPrice();
        
        ItemType iT = poi.getItemType();
        //if service add the po outstanding amount to outstandingamount 
        if(!iT.isQuantityBasedGeneralLedgerIndicator()) {
            outstandingAmount.add(poi.getItemOutstandingEncumberedAmount());
        } else {
            //else add outstanding quantity * unitprice
            BigDecimal qty = new BigDecimal(this.getPoOutstandingQuantity(poi).toString());
            outstandingAmount = outstandingAmount.add(new KualiDecimal(poi.getItemUnitPrice().multiply(qty)));
        }
        
        
        //return the total encumberance subtracted by the outstandingamount from above
        return totalEncumberance.subtract(outstandingAmount);
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
	 * @param paymentRequest The paymentRequest to set
	 */
	public void setPaymentRequest(PaymentRequestDocument paymentRequest) {
		this.paymentRequest = paymentRequest;
	}

    public void generateAccountListFromPoItemAccounts(List<PurApAccountingLine> accounts) {
        for (PurApAccountingLine line : accounts) {
            PurchaseOrderAccount poa = (PurchaseOrderAccount)line; 
            if(!line.isEmpty()) {
                getSourceAccountingLines().add(new PaymentRequestAccount(this,poa));
            }
        }
    }
    
     /**
     * @see org.kuali.module.purap.bo.PurchasingItemBase#getAccountingLineClass()
     */
    @Override
    public Class getAccountingLineClass() {
        return PaymentRequestAccount.class;
    }

    public boolean isDisplayOnPreq() {
        PurchaseOrderItem poi = getPurchaseOrderItem();
        if(ObjectUtils.isNull(poi)) {
            LOG.debug("poi was null");
            return false;
        }

        //if the po item is not active... skip it
        if(!poi.isItemActiveIndicator()) {
            LOG.debug("poi was not active: "+poi.toString());
            return false;
        }
        
        ItemType poiType = poi.getItemType();
        
        if(poiType.isQuantityBasedGeneralLedgerIndicator()) {
            if(poi.getItemQuantity().isGreaterThan(poi.getItemInvoicedTotalQuantity())) {
                return true;
            } else {
                if(ObjectUtils.isNotNull(this.getItemQuantity()) &&
                   this.getItemQuantity().isGreaterThan(KualiDecimal.ZERO)) {
                   return true; 
                }
            }
            
            return false;
        } else { //not quantity based
            if(poi.getItemOutstandingEncumberedAmount().isGreaterThan(KualiDecimal.ZERO)) {
                return true;
            } else {
                if(PurApItemUtils.isNonZeroExtended(this)) {
                    return true;
                }
                return false;
            }
            
        }
    }

    @Override
    public void resetAccount() {
        super.resetAccount();
        this.getNewSourceLine().setAccountLinePercent(new BigDecimal(0));
    }
    
    
}
