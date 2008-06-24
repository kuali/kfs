/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.fp.document.web;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherRuleConstants;

/**
 * This class...
 */
public class DisbursementVoucherInitForm extends KualiForm {

    private String payeeTypeCode = DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR;
    private String payeeIdNumber;
    private String payeePersonName;
    private String vendorHeaderGeneratedIdentifier;
    private String vendorDetailAssignedIdentifier;
    private String vendorAddressGeneratedIdentifier;
    
    private boolean hasMultipleAddresses = false;
    
    /**
     * Constructs a DisbursementVoucherInitForm.java.
     */
    public DisbursementVoucherInitForm() {
        super();
    }

    /**
     * Gets the payeeTypeCode attribute. 
     * @return Returns the payeeTypeCode.
     */
    public String getPayeeTypeCode() {
        return payeeTypeCode;
    }

    /**
     * Sets the payeeTypeCode attribute value.
     * @param payeeTypeCode The payeeTypeCode to set.
     */
    public void setPayeeTypeCode(String payeeTypeCode) {
        this.payeeTypeCode = payeeTypeCode;
    }

    /**
     * Gets the payeeIdNumber attribute. 
     * @return Returns the payeeIdNumber.
     */
    public String getPayeeIdNumber() {
        if(StringUtils.isBlank(payeeIdNumber)) {
            if(StringUtils.isNotBlank(vendorHeaderGeneratedIdentifier) && StringUtils.isNotBlank(vendorDetailAssignedIdentifier)) {
                payeeIdNumber = vendorHeaderGeneratedIdentifier + "-" + vendorDetailAssignedIdentifier;
            }
        }
        return payeeIdNumber;
    }

    /**
     * Sets the payeeIdNumber attribute value.
     * @param payeeIdNumber The payeeIdNumber to set.
     */
    public void setPayeeIdNumber(String payeeIdNumber) {
        this.payeeIdNumber = payeeIdNumber;
    }

    /**
     * Gets the payeePersonName attribute. 
     * @return Returns the payeePersonName.
     */
    public String getPayeePersonName() {
        return payeePersonName;
    }

    /**
     * Sets the payeePersonName attribute value.
     * @param payeePersonName The payeePersonName to set.
     */
    public void setPayeePersonName(String payeePersonName) {
        this.payeePersonName = payeePersonName;
    }

    /**
     * Gets the hasMultipleAddresses attribute. 
     * @return Returns the hasMultipleAddresses.
     */
    public boolean hasMultipleAddresses() {
        return hasMultipleAddresses;
    }

    /**
     * Gets the hasMultipleAddresses attribute. 
     * @return Returns the hasMultipleAddresses.
     */
    public boolean getHasMultipleAddresses() {
        return hasMultipleAddresses;
    }

    /**
     * Sets the hasMultipleAddresses attribute value.
     * @param hasMultipleAddresses The hasMultipleAddresses to set.
     */
    public void setHasMultipleAddresses(boolean hasMultipleAddresses) {
        this.hasMultipleAddresses = hasMultipleAddresses;
    }
    
    /**
     * Gets the vendorHeaderGeneratedIdentifier attribute. 
     * @return Returns the vendorHeaderGeneratedIdentifier.
     */
    public String getVendorHeaderGeneratedIdentifier() {
        // If payee is not a vendor, then return null
        if(StringUtils.equals(getPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
            vendorHeaderGeneratedIdentifier = null;
        } else if(StringUtils.isBlank(vendorHeaderGeneratedIdentifier)) {
            if(StringUtils.isNotBlank(getPayeeIdNumber())) {
                vendorHeaderGeneratedIdentifier = payeeIdNumber.substring(0, (payeeIdNumber.indexOf('-')));
            }
        }
        return vendorHeaderGeneratedIdentifier;
    }

    /**
     * Sets the vendorHeaderGeneratedIdentifier attribute value.
     * @param vendorHeaderGeneratedIdentifier The vendorHeaderGeneratedIdentifier to set.
     */
    public void setVendorHeaderGeneratedIdentifier(String vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /**
     * Gets the vendorDetailAssignedIdentifier attribute. 
     * @return Returns the vendorDetailAssignedIdentifier.
     */
    public String getVendorDetailAssignedIdentifier() {
        // If payee is not a vendor, then return null
        if(StringUtils.equals(getPayeeTypeCode(), DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE)) {
            vendorDetailAssignedIdentifier = null;
        } else if(StringUtils.isBlank(vendorDetailAssignedIdentifier)) {
            if(StringUtils.isNotBlank(getPayeeIdNumber())) {
                vendorDetailAssignedIdentifier = payeeIdNumber.substring(payeeIdNumber.indexOf('-')+1);
            }
        }
        return vendorDetailAssignedIdentifier;
    }

    /**
     * Sets the vendorDetailAssignedIdentifier attribute value.
     * @param vendorDetailAssignedIdentifier The vendorDetailAssignedIdentifier to set.
     */
    public void setVendorDetailAssignedIdentifier(String vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    /**
     * Gets the vendorAddressGeneratedIdentifier attribute. 
     * @return Returns the vendorAddressGeneratedIdentifier.
     */
    public String getVendorAddressGeneratedIdentifier() {
        return vendorAddressGeneratedIdentifier;
    }

    /**
     * Sets the vendorAddressGeneratedIdentifier attribute value.
     * @param vendorAddressGeneratedIdentifier The vendorAddressGeneratedIdentifier to set.
     */
    public void setVendorAddressGeneratedIdentifier(String vendorAddressGeneratedIdentifier) {
        this.vendorAddressGeneratedIdentifier = vendorAddressGeneratedIdentifier;
    }

    /**
     * 
     * This method...
     * @return
     */
    public boolean isEmployee() {
        return StringUtils.equals(payeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_EMPLOYEE);
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public boolean isVendor() {
        return StringUtils.equals(payeeTypeCode, DisbursementVoucherRuleConstants.DV_PAYEE_TYPE_VENDOR);
    }

    /**
     * Perform logic needed to clear the initial fields
     */
    public void clearInitFields() {
        // Clearing payee init fields
        this.setPayeeIdNumber("");
        this.setPayeeTypeCode("");
        this.setPayeePersonName("");
        this.setHasMultipleAddresses(false);
        
    }

    

}
