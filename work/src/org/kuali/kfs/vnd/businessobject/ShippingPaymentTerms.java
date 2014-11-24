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

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Payment Terms for Shipping from a particular Vendor. Per Vendor as distinct from <code>PaymentTermType</code>.
 * 
 * @see org.kuali.kfs.vnd.businessobject.PaymentTermType
 */
public class ShippingPaymentTerms extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String vendorShippingPaymentTermsCode;
    private String vendorShippingPaymentTermsDescription;
    private boolean vendorShippingPaymentTermsPayIndicator;
    private boolean active;

    /**
     * Default constructor.
     */
    public ShippingPaymentTerms() {

    }

    public String getVendorShippingPaymentTermsCode() {

        return vendorShippingPaymentTermsCode;
    }

    public void setVendorShippingPaymentTermsCode(String vendorShippingPaymentTermsCode) {
        this.vendorShippingPaymentTermsCode = vendorShippingPaymentTermsCode;
    }

    public String getVendorShippingPaymentTermsDescription() {

        return vendorShippingPaymentTermsDescription;
    }

    public void setVendorShippingPaymentTermsDescription(String vendorShippingPaymentTermsDescription) {
        this.vendorShippingPaymentTermsDescription = vendorShippingPaymentTermsDescription;
    }

    public boolean getVendorShippingPaymentTermsPayIndicator() {

        return vendorShippingPaymentTermsPayIndicator;
    }

    public void setVendorShippingPaymentTermsPayIndicator(boolean vendorShippingPaymentTermsPayIndicator) {
        this.vendorShippingPaymentTermsPayIndicator = vendorShippingPaymentTermsPayIndicator;
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
        m.put("vendorShippingPaymentTermsCode", this.vendorShippingPaymentTermsCode);

        return m;
    }
}
