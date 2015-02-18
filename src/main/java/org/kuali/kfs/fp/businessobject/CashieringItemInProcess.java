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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class represents in a cashiering item in process. This cashiering item in process
 *  has an item amount, reduced amount, and remaining amount.  It also has a closed and open date.  
 */
public class CashieringItemInProcess extends PersistableBusinessObjectBase {

    private String campusCode;
    private Integer itemIdentifier;
    private KualiDecimal itemAmount;
    private KualiDecimal itemReducedAmount;
    private KualiDecimal itemRemainingAmount;
    private KualiDecimal currentPayment;
    private Date itemOpenDate;
    private Date itemClosedDate;
    private String itemDescription;

    /**
     * Default constructor.
     */
    public CashieringItemInProcess() {
    }

    /**
     * Gets the campusCode attribute.
     * 
     * @return Returns the campusCode
     */
    public String getCampusCode() {
        return campusCode;
    }

    /**
     * Sets the campusCode attribute.
     * 
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the itemIdentifier attribute.
     * 
     * @return Returns the itemIdentifier
     */
    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    /**
     * Sets the itemIdentifier attribute.
     * 
     * @param itemIdentifier The itemIdentifier to set.
     */
    public void setItemIdentifier(Integer itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }


    /**
     * Gets the itemAmount attribute.
     * 
     * @return Returns the itemAmount
     */
    public KualiDecimal getItemAmount() {
        return itemAmount;
    }

    /**
     * Sets the itemAmount attribute.
     * 
     * @param itemAmount The itemAmount to set.
     */
    public void setItemAmount(KualiDecimal itemAmount) {
        this.itemAmount = itemAmount;
    }


    /**
     * Gets the itemReducedAmount attribute.
     * 
     * @return Returns the itemReducedAmount
     */
    public KualiDecimal getItemReducedAmount() {
        return itemReducedAmount;
    }

    /**
     * Sets the itemReducedAmount attribute.
     * 
     * @param itemReducedAmount The itemReducedAmount to set.
     */
    public void setItemReducedAmount(KualiDecimal itemReducedAmount) {
        this.itemReducedAmount = itemReducedAmount;
    }


    /**
     * Gets the itemRemainingAmount attribute.
     * 
     * @return Returns the itemRemainingAmount
     */
    public KualiDecimal getItemRemainingAmount() {
        return itemRemainingAmount;
    }

    /**
     * Sets the itemRemainingAmount attribute.
     * 
     * @param itemRemainingAmount The itemRemainingAmount to set.
     */
    public void setItemRemainingAmount(KualiDecimal itemTotalAmount) {
        this.itemRemainingAmount = itemTotalAmount;
    }


    /**
     * Gets the itemOpenDate attribute.
     * 
     * @return Returns the itemOpenDate
     */
    public Date getItemOpenDate() {
        return itemOpenDate;
    }

    /**
     * Sets the itemOpenDate attribute.
     * 
     * @param itemOpenDate The itemOpenDate to set.
     */
    public void setItemOpenDate(Date itemOpenDate) {
        this.itemOpenDate = itemOpenDate;
    }


    /**
     * Gets the itemClosedDate attribute.
     * 
     * @return Returns the itemClosedDate
     */
    public Date getItemClosedDate() {
        return itemClosedDate;
    }

    /**
     * Sets the itemClosedDate attribute.
     * 
     * @param itemClosedDate The itemClosedDate to set.
     */
    public void setItemClosedDate(Date itemClosedDate) {
        this.itemClosedDate = itemClosedDate;
    }


    /**
     * Gets the itemDescription attribute.
     * 
     * @return Returns the itemDescription
     */
    public String getItemDescription() {
        return itemDescription;
    }

    /**
     * Sets the itemDescription attribute.
     * 
     * @param itemDescription The itemDescription to set.
     */
    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    /**
     * Gets the currentPayment attribute.
     * 
     * @return Returns the currentPayment.
     */
    public KualiDecimal getCurrentPayment() {
        return currentPayment;
    }

    /**
     * Sets the currentPayment attribute value.
     * 
     * @param currentPayment The currentPayment to set.
     */
    public void setCurrentPayment(KualiDecimal currentPayment) {
        this.currentPayment = currentPayment;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("campusCode", this.campusCode);
        if (this.itemIdentifier != null) {
            m.put("itemIdentifier", this.itemIdentifier.toString());
        }
        return m;
    }

    /**
     * This method determines if this cashiering item in process was likely filled in by someone Since campusCode is likely
     * automatically populated, it doesn't count
     * 
     * @return if this item in process is populated
     */
    public boolean isPopulated() {
        return (this.itemOpenDate != null && itemAmount != null && !itemAmount.equals(KualiDecimal.ZERO));
    }
}
