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
package org.kuali.kfs.fp.businessobject;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents an Internal Billing Item business object.
 */
public class InternalBillingItem extends PersistableBusinessObjectBase {

    private static final long serialVersionUID = -2830091652446423539L;
    private String documentNumber;
    private Integer itemSequenceId;
    private String itemStockNumber;
    private String itemStockDescription;
    private Timestamp itemServiceDate;
    private Integer itemQuantity;
    private KualiDecimal itemUnitAmount;
    private String unitOfMeasureCode;

    /**
     * Constructs a InternalBillingItem.
     */
    public InternalBillingItem() {
        setItemUnitAmount(KualiDecimal.ZERO);
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


    /**
     * Gets the itemQuantity attribute.
     * 
     * @return Returns the itemQuantity.
     */
    public Integer getItemQuantity() {
        return itemQuantity;
    }


    /**
     * Sets the itemQuantity attribute value.
     * 
     * @param itemQuantity The itemQuantity to set.
     */
    public void setItemQuantity(Integer itemQuantity) {
        this.itemQuantity = itemQuantity;
    }


    /**
     * Gets the itemSequenceId attribute.
     * 
     * @return Returns the itemSequenceId.
     */
    public Integer getItemSequenceId() {
        return itemSequenceId;
    }


    /**
     * Sets the itemSequenceId attribute value.
     * 
     * @param itemSequenceId The itemSequenceId to set.
     */
    public void setItemSequenceId(Integer itemSequenceId) {
        this.itemSequenceId = itemSequenceId;
    }


    /**
     * Gets the itemServiceDate attribute.
     * 
     * @return Returns the itemServiceDate.
     */
    public Timestamp getItemServiceDate() {
        return itemServiceDate;
    }


    /**
     * Sets the itemServiceDate attribute value.
     * 
     * @param itemServiceDate The itemServiceDate to set.
     */
    public void setItemServiceDate(Timestamp itemServiceDate) {
        this.itemServiceDate = itemServiceDate;
    }


    /**
     * Gets the itemStockDescription attribute.
     * 
     * @return Returns the itemStockDescription.
     */
    public String getItemStockDescription() {
        return itemStockDescription;
    }


    /**
     * Sets the itemStockDescription attribute value.
     * 
     * @param itemStockDescription The itemStockDescription to set.
     */
    public void setItemStockDescription(String itemStockDescription) {
        this.itemStockDescription = itemStockDescription;
    }


    /**
     * Gets the itemStockNumber attribute.
     * 
     * @return Returns the itemStockNumber.
     */
    public String getItemStockNumber() {
        return itemStockNumber;
    }


    /**
     * Sets the itemStockNumber attribute value.
     * 
     * @param itemStockNumber The itemStockNumber to set.
     */
    public void setItemStockNumber(String itemStockNumber) {
        this.itemStockNumber = itemStockNumber;
    }


    /**
     * Gets the itemUnitAmount attribute.
     * 
     * @return Returns the itemUnitAmount.
     */
    public KualiDecimal getItemUnitAmount() {
        return itemUnitAmount;
    }


    /**
     * Sets the itemUnitAmount attribute value.
     * 
     * @param itemUnitAmount The itemUnitAmount to set.
     */
    public void setItemUnitAmount(KualiDecimal itemUnitAmount) {
        this.itemUnitAmount = itemUnitAmount;
    }


    /**
     * Gets the unitOfMeasureCode attribute.
     * 
     * @return Returns the unitOfMeasureCode.
     */
    public String getUnitOfMeasureCode() {
        return unitOfMeasureCode;
    }


    /**
     * Sets the unitOfMeasureCode attribute value.
     * 
     * @param unitOfMeasureCode The unitOfMeasureCode to set.
     */
    public void setUnitOfMeasureCode(String unitOfMeasureCode) {
        this.unitOfMeasureCode = unitOfMeasureCode;
    }


    /**
     * @return the total amount for this item
     */
    public KualiDecimal getTotal() {
        KualiDecimal total = new KualiDecimal(itemQuantity.toString());
        return total.multiply(itemUnitAmount);
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("docHeaderId", getDocumentNumber());
        m.put("itemSequenceId", getItemSequenceId());

        return m;
    }
}
