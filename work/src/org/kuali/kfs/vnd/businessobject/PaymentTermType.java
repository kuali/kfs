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
 * Types of Payment Terms for Vendor Contracts. Per contract as distinct from <code>ShippingPaymentTerms</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.ShippingPaymentTerms
 */
public class PaymentTermType extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorPaymentTermsCode;
    private String vendorDiscountDueTypeDescription;
    private Integer vendorDiscountDueNumber;
    private BigDecimal vendorPaymentTermsPercent;
    private String vendorNetDueTypeDescription;
    private Integer vendorNetDueNumber;
    private String vendorPaymentTermsDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public PaymentTermType() {

    }

    public String getVendorPaymentTermsCode() {

        return vendorPaymentTermsCode;
    }

    public void setVendorPaymentTermsCode(String vendorPaymentTermsCode) {
        this.vendorPaymentTermsCode = vendorPaymentTermsCode;
    }

    public String getVendorDiscountDueTypeDescription() {

        return vendorDiscountDueTypeDescription;
    }

    public void setVendorDiscountDueTypeDescription(String vendorDiscountDueTypeDescription) {
        this.vendorDiscountDueTypeDescription = vendorDiscountDueTypeDescription;
    }

    public Integer getVendorDiscountDueNumber() {

        return vendorDiscountDueNumber;
    }

    public void setVendorDiscountDueNumber(Integer vendorDiscountDueNumber) {
        this.vendorDiscountDueNumber = vendorDiscountDueNumber;
    }

    public BigDecimal getVendorPaymentTermsPercent() {

        return vendorPaymentTermsPercent;
    }

    public void setVendorPaymentTermsPercent(BigDecimal vendorPaymentTermsPercent) {
        this.vendorPaymentTermsPercent = vendorPaymentTermsPercent;
    }

    public String getVendorNetDueTypeDescription() {

        return vendorNetDueTypeDescription;
    }

    public void setVendorNetDueTypeDescription(String vendorNetDueTypeDescription) {
        this.vendorNetDueTypeDescription = vendorNetDueTypeDescription;
    }

    public Integer getVendorNetDueNumber() {

        return vendorNetDueNumber;
    }

    public void setVendorNetDueNumber(Integer vendorNetDueNumber) {
        this.vendorNetDueNumber = vendorNetDueNumber;
    }

    public String getVendorPaymentTermsDescription() {

        return vendorPaymentTermsDescription;
    }

    public void setVendorPaymentTermsDescription(String vendorPaymentTermsDescription) {
        this.vendorPaymentTermsDescription = vendorPaymentTermsDescription;
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
        m.put("vendorPaymentTermsCode", this.vendorPaymentTermsCode);

        return m;
    }
}
