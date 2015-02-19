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

package org.kuali.kfs.vnd.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Arbitrary categories per Vendor Contract for the purpose of tracking various kinds of costs that may be incurred by Purchase
 * Orders which use the Vendors which have these Contracts.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorContract
 */
public class PurchaseOrderCostSource extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String purchaseOrderCostSourceCode;
    private String purchaseOrderCostSourceDescription;
    private BigDecimal itemUnitPriceUpperVariancePercent;
    private BigDecimal itemUnitPriceLowerVariancePercent;
    private boolean active;

    /**
     * Default constructor.
     */
    public PurchaseOrderCostSource() {

    }

    public String getPurchaseOrderCostSourceCode() {

        return purchaseOrderCostSourceCode;
    }

    public void setPurchaseOrderCostSourceCode(String purchaseOrderCostSourceCode) {
        this.purchaseOrderCostSourceCode = purchaseOrderCostSourceCode;
    }

    public String getPurchaseOrderCostSourceDescription() {

        return purchaseOrderCostSourceDescription;
    }

    public void setPurchaseOrderCostSourceDescription(String purchaseOrderCostSourceDescription) {
        this.purchaseOrderCostSourceDescription = purchaseOrderCostSourceDescription;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("purchaseOrderCostSourceCode", this.purchaseOrderCostSourceCode);

        return m;
    }

    public BigDecimal getItemUnitPriceLowerVariancePercent() {
        return itemUnitPriceLowerVariancePercent;
    }

   public void setItemUnitPriceLowerVariancePercent(BigDecimal itemUnitPriceLowerVariancePercent) {
        this.itemUnitPriceLowerVariancePercent = itemUnitPriceLowerVariancePercent;
    }

    public BigDecimal getItemUnitPriceUpperVariancePercent() {
        return itemUnitPriceUpperVariancePercent;
    }

    public void setItemUnitPriceUpperVariancePercent(BigDecimal itemUnitPriceUpperVariancePercent) {
        this.itemUnitPriceUpperVariancePercent = itemUnitPriceUpperVariancePercent;
    }
}
