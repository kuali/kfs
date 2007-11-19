/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.financial.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

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
        setItemUnitAmount(new KualiDecimal(0));
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
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("docHeaderId", getDocumentNumber());
        m.put("itemSequenceId", getItemSequenceId());

        return m;
    }
}