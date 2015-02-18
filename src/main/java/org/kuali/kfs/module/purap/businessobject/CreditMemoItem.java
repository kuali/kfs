/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.module.purap.businessobject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.AccountsPayableService;
import org.kuali.kfs.module.purap.document.service.PaymentRequestService;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.document.service.PurchaseOrderService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.module.purap.util.ExpiredOrClosedAccountEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Item line Business Object for Credit Memo Document.
 */
public class CreditMemoItem extends AccountsPayableItemBase {
    private KualiDecimal poInvoicedTotalQuantity;
    private BigDecimal poUnitPrice;
    private KualiDecimal poTotalAmount;
    private KualiDecimal preqInvoicedTotalQuantity;
    private BigDecimal preqUnitPrice;
    private KualiDecimal preqTotalAmount;

    /**
     * Default constructor.
     */
    public CreditMemoItem() {
    }

    /**
     * Constructs a CreditMemoItem object from an existing Purchase Order Item. - Delegate
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param poItem the Purchase Order Item to copy from.
     */
    public CreditMemoItem(VendorCreditMemoDocument cmDocument, PurchaseOrderItem poItem) {
        this(cmDocument, poItem, new HashMap<String, ExpiredOrClosedAccountEntry>());
    }

    /**
     * Constructs a CreditMemoItem object from an existing Purchase Order Item, and check and process expired or closed accounts
     * item might contain.
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param poItem the Purchase Order Item to copy from.
     * @param expiredOrClosedAccountList the list of expired or closed accounts to check against.
     */
    public CreditMemoItem(VendorCreditMemoDocument cmDocument, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setPurapDocument(cmDocument);
        setItemLineNumber(poItem.getItemLineNumber());
        setPoInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
        setPoUnitPrice(poItem.getItemUnitPrice());
        setPoTotalAmount(poItem.getItemInvoicedTotalAmount());
        setItemTypeCode(poItem.getItemTypeCode());
        
        //recalculate tax
        SpringContext.getBean(PurapService.class).calculateTax(cmDocument);
        
        if ((ObjectUtils.isNotNull(this.getItemType()) && this.getItemType().isAmountBasedGeneralLedgerIndicator())) {
            // setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
        }
        else {
            setItemUnitPrice(poItem.getItemUnitPrice());
        }

        setItemCatalogNumber(poItem.getItemCatalogNumber());
        
        setItemDescription(poItem.getItemDescription());
                 
        if (getPoInvoicedTotalQuantity() == null) {
            setPoInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPoUnitPrice() == null) {
            setPoUnitPrice(BigDecimal.ZERO);
        }
        if (getPoTotalAmount() == null) {
            setPoTotalAmount(KualiDecimal.ZERO);
        }
        
        for (Iterator iter = poItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();

            // check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);

            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * Constructs a CreditMemoItem object from an existing Payment Request Item, and check and process expired or closed accounts
     * item might contain.
     * 
     * @param cmDocument the Credit Memo Document this item belongs to.
     * @param preqItem the Payment Request Item to copy from.
     * @param poItem the Purchase Order Item to copy from.
     * @param expiredOrClosedAccountList the list of expired or closed accounts to check against.
     */
    public CreditMemoItem(VendorCreditMemoDocument cmDocument, PaymentRequestItem preqItem, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(preqItem.getItemLineNumber());
        this.setPurapDocument(cmDocument);
        
        // take invoiced quantities from the lower of the preq and po if different
        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
            setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
            setPreqTotalAmount(poItem.getItemInvoicedTotalAmount());
        }
        else {
            setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            setPreqTotalAmount(preqItem.getTotalAmount());
        }

        setPreqUnitPrice(preqItem.getItemUnitPrice());
        setItemTypeCode(preqItem.getItemTypeCode());

        if ((ObjectUtils.isNotNull(this.getItemType()) && this.getItemType().isAmountBasedGeneralLedgerIndicator())) {
            // setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
        }
        else {
            setItemUnitPrice(preqItem.getItemUnitPrice());
        }

        setItemCatalogNumber(preqItem.getItemCatalogNumber());
        setItemDescription(preqItem.getItemDescription());
        
        setCapitalAssetTransactionTypeCode(preqItem.getCapitalAssetTransactionTypeCode());

        if (getPreqInvoicedTotalQuantity() == null) {
            setPreqInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPreqUnitPrice() == null) {
            setPreqUnitPrice(BigDecimal.ZERO);
        }
        if (getPreqTotalAmount() == null) {
            setPreqTotalAmount(KualiDecimal.ZERO);
        }

        for (Iterator iter = preqItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PaymentRequestAccount account = (PaymentRequestAccount) iter.next();

            // check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);

            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * @see org.kuali.kfs.module.purap.businessobject.PurApItemBase#getAccountingLineClass()
     */
    @Override
    public Class<CreditMemoAccount> getAccountingLineClass() {
        return CreditMemoAccount.class;
    }
   
    public KualiDecimal getPoTotalAmount() {
        return poTotalAmount;
    }

    public void setPoTotalAmount(KualiDecimal poTotalAmount) {
        this.poTotalAmount = poTotalAmount;
    }

    public KualiDecimal getPoInvoicedTotalQuantity() {
        return poInvoicedTotalQuantity;
    }

    public void setPoInvoicedTotalQuantity(KualiDecimal poInvoicedTotalQuantity) {
        this.poInvoicedTotalQuantity = poInvoicedTotalQuantity;
    }

    public BigDecimal getPoUnitPrice() {
        return poUnitPrice;
    }

    public void setPoUnitPrice(BigDecimal poUnitPrice) {
        this.poUnitPrice = poUnitPrice;
    }

    public KualiDecimal getPreqTotalAmount() {
        return preqTotalAmount;
    }

    public void setPreqTotalAmount(KualiDecimal preqTotalAmount) {
        this.preqTotalAmount = preqTotalAmount;
    }

    public KualiDecimal getPreqInvoicedTotalQuantity() {
        return preqInvoicedTotalQuantity;
    }

    public void setPreqInvoicedTotalQuantity(KualiDecimal preqInvoicedTotalQuantity) {
        this.preqInvoicedTotalQuantity = preqInvoicedTotalQuantity;
    }

    public BigDecimal getPreqUnitPrice() {
        return preqUnitPrice;
    }

    public void setPreqUnitPrice(BigDecimal preqUnitPrice) {
        this.preqUnitPrice = preqUnitPrice;
    }

    @Override
    public Class getUseTaxClass() {
        return CreditMemoItemUseTax.class;
    }
    
    public PurchaseOrderItem getPurchaseOrderItem(){
        
        PurchaseOrderItem poi = null;
        //refresh vendor document
        if (ObjectUtils.isNotNull(this.getPurapDocumentIdentifier())) {
            if (ObjectUtils.isNull(this.getVendorCreditMemo())) {
                this.refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
            }
        }

        //if vendor document not null, then attempt to pull PO off of it
        if (ObjectUtils.isNotNull(getVendorCreditMemo())) {
            PurchaseOrderDocument purchaseOrderDocument = null;            
            Integer purchaseOrderDocumentId = getVendorCreditMemo().getPurchaseOrderIdentifier();
            
            if (getVendorCreditMemo().isSourceDocumentPaymentRequest() && ObjectUtils.isNull(purchaseOrderDocumentId)) {
                PaymentRequestDocument paymentRequestDocument = SpringContext.getBean(PaymentRequestService.class).getPaymentRequestById(getVendorCreditMemo().getPaymentRequestIdentifier());
                purchaseOrderDocumentId = paymentRequestDocument.getPurchaseOrderIdentifier();
            }
            
            // if we found a valid po id number then check it for reopening
            if (ObjectUtils.isNotNull(purchaseOrderDocumentId)) {
                purchaseOrderDocument = SpringContext.getBean(PurchaseOrderService.class).getCurrentPurchaseOrder(purchaseOrderDocumentId);
            }
            
            //if we have a PO document, get po item
            if(ObjectUtils.isNotNull(purchaseOrderDocument)){                
                if (this.getItemType().isLineItemIndicator()) {
                    List<PurchaseOrderItem> items = purchaseOrderDocument.getItems();
                    poi = items.get(this.getItemLineNumber().intValue() - 1);

                }
                else {
                    poi = (PurchaseOrderItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(purchaseOrderDocument, this.getItemType());
                }
            }
        }
        else {            
            throw new PurError("Credit Memo Object in Purchase Order item line number " + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
        }
        
        return poi;
    }
    
    public VendorCreditMemoDocument getVendorCreditMemo() {
        return super.getPurapDocument();
    }

}
