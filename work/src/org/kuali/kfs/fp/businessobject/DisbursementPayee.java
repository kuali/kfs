/*
 * Copyright 2007-2008 The Kuali Foundation
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

package org.kuali.kfs.fp.businessobject;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.kuali.kfs.fp.document.DisbursementVoucherConstants;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.krad.util.KRADPropertyConstants;

public class DisbursementPayee extends TransientBusinessObjectBase implements MutableInactivatable {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementPayee.class);

    private String payeeIdNumber;
    private String payeeTypeCode;
    private String payeeTypeDescription;
    private String payeeName;

    private String paymentReasonCode;
    private String taxNumber;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String vendorName;
    private String vendorNumber;
    private String address;
    private boolean active;
    
    private String principalId;
    
    /**
     * Constructs a DisbursementPayee.java.
     */
    public DisbursementPayee() {
        super();
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(KFSPropertyConstants.PAYEE_ID_NUMBER, this.payeeIdNumber);
        map.put(KFSPropertyConstants.PAYEE_TYPE_CODE, this.payeeTypeCode);
        map.put(KFSPropertyConstants.PAYEE_NAME, this.payeeName);

        return map;
    }

    /**
     * Gets the payeeIdNumber attribute.
     * 
     * @return Returns the payeeIdNumber.
     */
    public String getPayeeIdNumber() {
        return payeeIdNumber;
    }

    /**
     * Sets the payeeIdNumber attribute value.
     * 
     * @param payeeIdNumber The payeeIdNumber to set.
     */
    public void setPayeeIdNumber(String payeeIdNumber) {
        this.payeeIdNumber = payeeIdNumber;
    }

    /**
     * Gets the payeeTypeCode attribute.
     * 
     * @return Returns the payeeTypeCode.
     */
    public String getPayeeTypeCode() {
        return payeeTypeCode;
    }

    /**
     * Sets the payeeTypeCode attribute value.
     * 
     * @param payeeTypeCode The payeeTypeCode to set.
     */
    public void setPayeeTypeCode(String payeeTypeCode) {
        this.payeeTypeCode = payeeTypeCode;
    }

    /**
     * Gets the payeeName attribute.
     * 
     * @return Returns the payeeName.
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * Sets the payeeName attribute value.
     * 
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
    }

    /**
     * Gets the paymentReasonCode attribute.
     * 
     * @return Returns the paymentReasonCode.
     */
    public String getPaymentReasonCode() {
        return paymentReasonCode;
    }

    /**
     * Sets the paymentReasonCode attribute value.
     * 
     * @param paymentReasonCode The paymentReasonCode to set.
     */
    public void setPaymentReasonCode(String paymentReasonCode) {
        this.paymentReasonCode = paymentReasonCode;
    }

    /**
     * Gets the taxNumber attribute.
     * 
     * @return Returns the taxNumber.
     */
    public String getTaxNumber() {
        return taxNumber;
    }

    /**
     * Sets the taxNumber attribute value.
     * 
     * @param taxNumber The taxNumber to set.
     */
    public void setTaxNumber(String taxNumber) {
        this.taxNumber = taxNumber;
    }

    /**
     * Gets the employeeId attribute.
     * 
     * @return Returns the employeeId.
     */
    public String getEmployeeId() {
        return employeeId;
    }

    /**
     * Sets the employeeId attribute value.
     * 
     * @param employeeId The employeeId to set.
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    /**
     * Gets the vendorName attribute.
     * 
     * @return Returns the vendorName.
     */
    public String getVendorName() {
        return vendorName;
    }

    /**
     * Sets the vendorName attribute value.
     * 
     * @param vendorName The vendorName to set.
     */
    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    /**
     * Gets the address attribute.
     * 
     * @return Returns the address.
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address attribute value.
     * 
     * @param address The address to set.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Gets the vendorNumber attribute.
     * 
     * @return Returns the vendorNumber.
     */
    public String getVendorNumber() {
        return vendorNumber;
    }

    /**
     * Sets the vendorNumber attribute value.
     * 
     * @param vendorNumber The vendorNumber to set.
     */
    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    /**
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the firstName attribute.
     * 
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName attribute value.
     * 
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the lastName attribute.
     * 
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName attribute value.
     * 
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the payeeTypeDescription attribute.
     * 
     * @return Returns the payeeTypeDescription.
     */
    public String getPayeeTypeDescription() {
        DisbursementVoucherPayeeService payeeService = SpringContext.getBean(DisbursementVoucherPayeeService.class);
        
        return payeeService.getPayeeTypeDescription(payeeTypeCode);
    }

    /**
     * Sets the payeeTypeDescription attribute value.
     * 
     * @param payeeTypeDescription The payeeTypeDescription to set.
     */
    public void setPayeeTypeDescription(String payeeTypeDescription) {
        this.payeeTypeDescription = payeeTypeDescription;
    }

    /**
     * Gets the principalId attribute. 
     * @return Returns the principalId.
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

}
