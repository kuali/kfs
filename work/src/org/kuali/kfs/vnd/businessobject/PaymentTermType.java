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
