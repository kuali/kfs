/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.sys.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent the documentation location for a disbursement voucher.
 */
public class PaymentDocumentationLocation extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String paymentDocumentationLocationCode;
    private String paymentDocumentationLocationName;
    private String paymentDocumentationLocationAddress;
    private boolean active;

    /**
     * Default constructor.
     */
    public PaymentDocumentationLocation() {

    }

    /**
     * Gets the paymentDocumentationLocationCode attribute.
     * 
     * @return Returns the paymentDocumentationLocationCode
     */
    public String getPaymentDocumentationLocationCode() {
        return paymentDocumentationLocationCode;
    }

    /**
     * Sets the paymentDocumentationLocationCode attribute.
     * 
     * @param paymentDocumentationLocationCode The paymentDocumentationLocationCode to set.
     */
    public void setPaymentDocumentationLocationCode(String disbursementVoucherDocumentationLocationCode) {
        this.paymentDocumentationLocationCode = disbursementVoucherDocumentationLocationCode;
    }


    /**
     * Gets the paymentDocumentationLocationName attribute.
     * 
     * @return Returns the paymentDocumentationLocationName
     */
    public String getPaymentDocumentationLocationName() {
        return paymentDocumentationLocationName;
    }

    /**
     * Sets the paymentDocumentationLocationName attribute.
     * 
     * @param paymentDocumentationLocationName The paymentDocumentationLocationName to set.
     */
    public void setPaymentDocumentationLocationName(String disbursementVoucherDocumentationLocationName) {
        this.paymentDocumentationLocationName = disbursementVoucherDocumentationLocationName;
    }


    /**
     * Gets the paymentDocumentationLocationAddress attribute.
     * 
     * @return Returns the paymentDocumentationLocationAddress
     */
    public String getPaymentDocumentationLocationAddress() {
        return paymentDocumentationLocationAddress;
    }

    /**
     * Sets the paymentDocumentationLocationAddress attribute.
     * 
     * @param paymentDocumentationLocationAddress The paymentDocumentationLocationAddress to set.
     */
    public void setPaymentDocumentationLocationAddress(String disbursementVoucherDocumentationLocationAddress) {
        this.paymentDocumentationLocationAddress = disbursementVoucherDocumentationLocationAddress;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("paymentDocumentationLocationCode", this.paymentDocumentationLocationCode);
        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
