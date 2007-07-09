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
import java.util.Iterator;

import org.kuali.core.util.KualiDecimal;
import org.kuali.module.purap.document.CreditMemoDocument;

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
     * Constructs a CreditMemoItem.java from an existing PurchaseOrderItem.
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
     * Constructs a CreditMemoItem.java from an existing PaymentRequestItem
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

    /**
     * Gets the poExtendedPrice attribute.
     * 
     * @return Returns the poExtendedPrice.
     */
    public KualiDecimal getPoExtendedPrice() {
        return poExtendedPrice;
    }

    /**
     * Sets the poExtendedPrice attribute value.
     * 
     * @param poExtendedPrice The poExtendedPrice to set.
     */
    public void setPoExtendedPrice(KualiDecimal poExtendedCost) {
        this.poExtendedPrice = poExtendedCost;
    }

    /**
     * Gets the poInvoicedTotalQuantity attribute.
     * 
     * @return Returns the poInvoicedTotalQuantity.
     */
    public KualiDecimal getPoInvoicedTotalQuantity() {
        return poInvoicedTotalQuantity;
    }

    /**
     * Sets the poInvoicedTotalQuantity attribute value.
     * 
     * @param poInvoicedTotalQuantity The poInvoicedTotalQuantity to set.
     */
    public void setPoInvoicedTotalQuantity(KualiDecimal poInvoicedTotalQuantity) {
        this.poInvoicedTotalQuantity = poInvoicedTotalQuantity;
    }

    /**
     * Gets the poUnitPrice attribute.
     * 
     * @return Returns the poUnitPrice.
     */
    public BigDecimal getPoUnitPrice() {
        return poUnitPrice;
    }

    /**
     * Sets the poUnitPrice attribute value.
     * 
     * @param poUnitPrice The poUnitPrice to set.
     */
    public void setPoUnitPrice(BigDecimal poUnitPrice) {
        this.poUnitPrice = poUnitPrice;
    }

    /**
     * Gets the preqExtendedPrice attribute.
     * 
     * @return Returns the preqExtendedPrice.
     */
    public KualiDecimal getPreqExtendedPrice() {
        return preqExtendedPrice;
    }

    /**
     * Sets the preqExtendedPrice attribute value.
     * 
     * @param preqExtendedPrice The preqExtendedPrice to set.
     */
    public void setPreqExtendedPrice(KualiDecimal preqExtendedCost) {
        this.preqExtendedPrice = preqExtendedCost;
    }

    /**
     * Gets the preqInvoicedTotalQuantity attribute.
     * 
     * @return Returns the preqInvoicedTotalQuantity.
     */
    public KualiDecimal getPreqInvoicedTotalQuantity() {
        return preqInvoicedTotalQuantity;
    }

    /**
     * Sets the preqInvoicedTotalQuantity attribute value.
     * 
     * @param preqInvoicedTotalQuantity The preqInvoicedTotalQuantity to set.
     */
    public void setPreqInvoicedTotalQuantity(KualiDecimal preqInvoicedTotalQuantity) {
        this.preqInvoicedTotalQuantity = preqInvoicedTotalQuantity;
    }

    /**
     * Gets the preqUnitPrice attribute.
     * 
     * @return Returns the preqUnitPrice.
     */
    public BigDecimal getPreqUnitPrice() {
        return preqUnitPrice;
    }

    /**
     * Sets the preqUnitPrice attribute value.
     * 
     * @param preqUnitPrice The preqUnitPrice to set.
     */
    public void setPreqUnitPrice(BigDecimal preqUnitPrice) {
        this.preqUnitPrice = preqUnitPrice;
    }
}
