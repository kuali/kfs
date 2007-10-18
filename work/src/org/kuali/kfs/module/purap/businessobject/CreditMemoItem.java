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
import java.util.HashMap;
import java.util.Iterator;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.document.CreditMemoDocument;
import org.kuali.module.purap.service.AccountsPayableService;
import org.kuali.module.purap.util.ExpiredOrClosedAccountEntry;

/**
 * Item line Business Object for Credit Memo Document.
 */
public class CreditMemoItem extends AccountsPayableItemBase {
    private KualiDecimal poInvoicedTotalQuantity;
    private BigDecimal poUnitPrice;
    private KualiDecimal poExtendedPrice;
    private KualiDecimal preqInvoicedTotalQuantity;
    private BigDecimal preqUnitPrice;
    private KualiDecimal preqExtendedPrice;
    
    /**
     * Default constructor.
     */
    public CreditMemoItem() {
    }

    /**
     * Constructs a CreditMemoItem object from an existing Purchase Order Item.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PurchaseOrderItem poItem) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(poItem.getItemLineNumber());
        setPoInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
        setPoUnitPrice(poItem.getItemUnitPrice());
        setPoExtendedPrice(poItem.getItemInvoicedTotalAmount());
        setItemTypeCode(poItem.getItemTypeCode());
        setItemUnitPrice(poItem.getItemUnitPrice());
        setItemCatalogNumber(poItem.getItemCatalogNumber());
        setItemDescription(poItem.getItemDescription());

        if (getPoInvoicedTotalQuantity() == null) {
            setPoInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPoUnitPrice() == null) {
            setPoUnitPrice(BigDecimal.ZERO);
        }
        if (getPoExtendedPrice() == null) {
            setPoExtendedPrice(KualiDecimal.ZERO);
        }

        for (Iterator iter = poItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();
            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * Constructs a CreditMemoItem object from an existing Purchase Order Item.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        //TODO (KULPURAP-1575) Merge this method with the other constructor. cleanup
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(poItem.getItemLineNumber());
        setPoInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
        setPoUnitPrice(poItem.getItemUnitPrice());
        setPoExtendedPrice(poItem.getItemInvoicedTotalAmount());
        setItemTypeCode(poItem.getItemTypeCode());
        if( (ObjectUtils.isNotNull(this.getItemType()) && !this.getItemType().isQuantityBasedGeneralLedgerIndicator()) ) {
            //setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
        } else {
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
        if (getPoExtendedPrice() == null) {
            setPoExtendedPrice(KualiDecimal.ZERO);
        }

        for (Iterator iter = poItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PurchaseOrderAccount account = (PurchaseOrderAccount) iter.next();

            //check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);

            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }
    
    /**
     * Constructs a CreditMemoItem object from an existing Payment Request Item.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PaymentRequestItem preqItem, PurchaseOrderItem poItem) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(preqItem.getItemLineNumber());

        // take invoiced quantities from the lower of the preq and po if different
        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
            setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
            setPreqExtendedPrice(poItem.getItemInvoicedTotalAmount());
        }
        else {
            setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            setPreqExtendedPrice(preqItem.getExtendedPrice());
        }

        setPreqUnitPrice(preqItem.getItemUnitPrice());
        setItemTypeCode(preqItem.getItemTypeCode());
        if( (ObjectUtils.isNotNull(this.getItemType()) && !this.getItemType().isQuantityBasedGeneralLedgerIndicator()) ) {
            //setting unit price to be null to be more consistent with other below the line
            this.setItemUnitPrice(null);
        } else {
            setItemUnitPrice(preqItem.getItemUnitPrice());
        }
        setItemCatalogNumber(preqItem.getItemCatalogNumber());
        setItemDescription(preqItem.getItemDescription());

        if (getPreqInvoicedTotalQuantity() == null) {
            setPreqInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPreqUnitPrice() == null) {
            setPreqUnitPrice(BigDecimal.ZERO);
        }
        if (getPreqExtendedPrice() == null) {
            setPreqExtendedPrice(KualiDecimal.ZERO);
        }

        for (Iterator iter = preqItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PaymentRequestAccount account = (PaymentRequestAccount) iter.next();
            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * Constructs a CreditMemoItem object from an existing Payment Request Item.
     */
    public CreditMemoItem(CreditMemoDocument cmDocument, PaymentRequestItem preqItem, PurchaseOrderItem poItem, HashMap<String, ExpiredOrClosedAccountEntry> expiredOrClosedAccountList) {
        super();

        setPurapDocumentIdentifier(cmDocument.getPurapDocumentIdentifier());
        setItemLineNumber(preqItem.getItemLineNumber());

        // take invoiced quantities from the lower of the preq and po if different
        if (poItem.getItemInvoicedTotalQuantity() != null && preqItem.getItemQuantity() != null && poItem.getItemInvoicedTotalQuantity().isLessThan(preqItem.getItemQuantity())) {
            setPreqInvoicedTotalQuantity(poItem.getItemInvoicedTotalQuantity());
            setPreqExtendedPrice(poItem.getItemInvoicedTotalAmount());
        }
        else {
            setPreqInvoicedTotalQuantity(preqItem.getItemQuantity());
            setPreqExtendedPrice(preqItem.getExtendedPrice());
        }

        setPreqUnitPrice(preqItem.getItemUnitPrice());
        setItemTypeCode(preqItem.getItemTypeCode());
        setItemUnitPrice(preqItem.getItemUnitPrice());
        setItemCatalogNumber(preqItem.getItemCatalogNumber());
        setItemDescription(preqItem.getItemDescription());

        if (getPreqInvoicedTotalQuantity() == null) {
            setPreqInvoicedTotalQuantity(KualiDecimal.ZERO);
        }
        if (getPreqUnitPrice() == null) {
            setPreqUnitPrice(BigDecimal.ZERO);
        }
        if (getPreqExtendedPrice() == null) {
            setPreqExtendedPrice(KualiDecimal.ZERO);
        }

        for (Iterator iter = preqItem.getSourceAccountingLines().iterator(); iter.hasNext();) {
            PaymentRequestAccount account = (PaymentRequestAccount) iter.next();

            //check if this account is expired/closed and replace as needed
            SpringContext.getBean(AccountsPayableService.class).processExpiredOrClosedAccount(account, expiredOrClosedAccountList);
            
            getSourceAccountingLines().add(new CreditMemoAccount(account));
        }
    }

    /**
     * @see org.kuali.module.purap.bo.PurApItemBase#getAccountingLineClass()
     */
    @Override
    public Class<CreditMemoAccount> getAccountingLineClass() {
        return CreditMemoAccount.class;
    }

    public KualiDecimal getPoExtendedPrice() {
        return poExtendedPrice;
    }

    public void setPoExtendedPrice(KualiDecimal poExtendedCost) {
        this.poExtendedPrice = poExtendedCost;
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

    public KualiDecimal getPreqExtendedPrice() {
        return preqExtendedPrice;
    }

    public void setPreqExtendedPrice(KualiDecimal preqExtendedCost) {
        this.preqExtendedPrice = preqExtendedCost;
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
}
