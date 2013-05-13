/*
 * Copyright 2012 The Kuali Foundation.
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
