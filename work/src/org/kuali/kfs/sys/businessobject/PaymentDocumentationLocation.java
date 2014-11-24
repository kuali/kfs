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
