/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.bo;

import java.sql.Timestamp;
import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * Internal Billing Item Business Object
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class InternalBillingItem extends BusinessObjectBase {

    private static final long serialVersionUID = -2830091652446423539L;
    private String financialDocumentNumber;
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
     * Gets the financialDocumentNumber attribute.
     * 
     * @return Returns the financialDocumentNumber.
     */
    public String getFinancialDocumentNumber() {
        return financialDocumentNumber;
    }


    /**
     * Sets the financialDocumentNumber attribute value.
     * 
     * @param financialDocumentNumber The financialDocumentNumber to set.
     */
    public void setFinancialDocumentNumber(String financialDocumentNumber) {
        this.financialDocumentNumber = financialDocumentNumber;
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

        m.put("docHeaderId", getFinancialDocumentNumber());
        m.put("itemSequenceId", getItemSequenceId());

        return m;
    }
}