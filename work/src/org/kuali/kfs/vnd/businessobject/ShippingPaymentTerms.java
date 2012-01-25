/*
 * Copyright 2007 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
