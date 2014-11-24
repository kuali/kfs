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
package org.kuali.kfs.vnd.web.struts;

import java.util.HashMap;
import java.util.Map;

import org.kuali.rice.kns.web.struts.form.LookupForm;

public class VendorExclusionForm extends LookupForm {
    
    private String confirmStatusCode;
    private String debarredVendorId;
    private String vendorExclusionStatus;
    private String vendorType;
    
    /**
     * Gets the confirmStatusCode attribute. 
     * @return Returns the confirmStatusCode.
     */
    public String getConfirmStatusCode() {
        return confirmStatusCode;
    }

    /**
     * Sets the confirmStatusCode attribute value.
     * @param statusCode The statusCode to set.
     */
    public void setConfirmStatusCode(String confirmStatusCode) {
        if (confirmStatusCode == null) {
            this.confirmStatusCode = "";
        } else {
            this.confirmStatusCode = confirmStatusCode;
        }
    }

    /**
     * Gets the debarredVendorId attribute. 
     * @return Returns the debarredVendorId.
     */
    public String getDebarredVendorId() {
        return debarredVendorId;
    }

    /**
     * Sets the debarredVendorId attribute value.
     * @param debarredVendorId The debarredVendorId to set.
     */
    public void setDebarredVendorId(String debarredVendorId) {
        this.debarredVendorId = debarredVendorId;
    }

    /**
     * Gets the vendorExclusionStatus attribute. 
     * @return Returns the vendorExclusionStatus.
     */
    public String getVendorExclusionStatus() {
        return vendorExclusionStatus;
    }

    /**
     * Sets the vendorExclusionStatus attribute value.
     * @param vendorExclusionStatus The vendorExclusionStatus to set.
     */
    public void setVendorExclusionStatus(String vendorExclusionStatus) {
        this.vendorExclusionStatus = vendorExclusionStatus;
    }

    /**
     * Gets the vendorType attribute. 
     * @return Returns the vendorType.
     */
    public String getVendorType() {
        return vendorType;
    }

    /**
     * Sets the vendorType attribute value.
     * @param vendorType The vendorType to set.
     */
    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }

}
