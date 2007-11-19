/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent the documentation location for a disbursement voucher.
 */
public class DisbursementVoucherDocumentationLocation extends PersistableBusinessObjectBase {

    private String disbursementVoucherDocumentationLocationCode;
    private String disbursementVoucherDocumentationLocationName;
    private String disbursementVoucherDocumentationLocationAddress;

    /**
     * Default constructor.
     */
    public DisbursementVoucherDocumentationLocation() {

    }

    /**
     * Gets the disbursementVoucherDocumentationLocationCode attribute.
     * 
     * @return Returns the disbursementVoucherDocumentationLocationCode
     */
    public String getDisbursementVoucherDocumentationLocationCode() {
        return disbursementVoucherDocumentationLocationCode;
    }

    /**
     * Sets the disbursementVoucherDocumentationLocationCode attribute.
     * 
     * @param disbursementVoucherDocumentationLocationCode The disbursementVoucherDocumentationLocationCode to set.
     */
    public void setDisbursementVoucherDocumentationLocationCode(String disbursementVoucherDocumentationLocationCode) {
        this.disbursementVoucherDocumentationLocationCode = disbursementVoucherDocumentationLocationCode;
    }


    /**
     * Gets the disbursementVoucherDocumentationLocationName attribute.
     * 
     * @return Returns the disbursementVoucherDocumentationLocationName
     */
    public String getDisbursementVoucherDocumentationLocationName() {
        return disbursementVoucherDocumentationLocationName;
    }

    /**
     * Sets the disbursementVoucherDocumentationLocationName attribute.
     * 
     * @param disbursementVoucherDocumentationLocationName The disbursementVoucherDocumentationLocationName to set.
     */
    public void setDisbursementVoucherDocumentationLocationName(String disbursementVoucherDocumentationLocationName) {
        this.disbursementVoucherDocumentationLocationName = disbursementVoucherDocumentationLocationName;
    }


    /**
     * Gets the disbursementVoucherDocumentationLocationAddress attribute.
     * 
     * @return Returns the disbursementVoucherDocumentationLocationAddress
     */
    public String getDisbursementVoucherDocumentationLocationAddress() {
        return disbursementVoucherDocumentationLocationAddress;
    }

    /**
     * Sets the disbursementVoucherDocumentationLocationAddress attribute.
     * 
     * @param disbursementVoucherDocumentationLocationAddress The disbursementVoucherDocumentationLocationAddress to set.
     */
    public void setDisbursementVoucherDocumentationLocationAddress(String disbursementVoucherDocumentationLocationAddress) {
        this.disbursementVoucherDocumentationLocationAddress = disbursementVoucherDocumentationLocationAddress;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("disbursementVoucherDocumentationLocationCode", this.disbursementVoucherDocumentationLocationCode);
        return m;
    }
}
