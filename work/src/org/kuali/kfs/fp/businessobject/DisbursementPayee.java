/*
 * Copyright 2007 The Kuali Foundation.
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

package org.kuali.kfs.fp.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.PaymentTermType;
import org.kuali.kfs.vnd.businessobject.ShippingPaymentTerms;
import org.kuali.kfs.vnd.businessobject.ShippingTitle;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorAlias;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorContact;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorCustomerNumber;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorInactiveReason;
import org.kuali.kfs.vnd.businessobject.VendorPhoneNumber;
import org.kuali.kfs.vnd.businessobject.VendorShippingSpecialCondition;
import org.kuali.kfs.vnd.document.routing.VendorRoutingComparable;
import org.kuali.rice.kns.bo.BusinessObjectBase;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.bo.TransientBusinessObjectBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.util.KimPropertyConstants;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Contains all data for a specific parent or division Vendor, including a link to the <code>VendorHeader</code>, which only
 * contains information about the parent company, but can be shared between division Vendors.
 * 
 * @see org.kuali.kfs.vnd.businessobject.VendorHeader
 */
public class DisbursementPayee extends TransientBusinessObjectBase {
    private static Logger LOG = Logger.getLogger(DisbursementPayee.class);

    private String payeeIdNumber;
    private String payeeName;
    private String payeeTypeCode;
    private String paymentReasonCode;
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
        map.put(KFSPropertyConstants.PAYEE_ID_NUMBER, this.payeeIdNumber);
        map.put(KFSPropertyConstants.PAYEE_NAME, this.payeeName);
        
        return map;
    }

    /**
     * Gets the payeeIdNumber attribute. 
     * @return Returns the payeeIdNumber.
     */
    public String getPayeeIdNumber() {
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
     * Gets the payeeName attribute. 
     * @return Returns the payeeName.
     */
    public String getPayeeName() {
        return payeeName;
    }

    /**
     * Sets the payeeName attribute value.
     * @param payeeName The payeeName to set.
     */
    public void setPayeeName(String payeeName) {
        this.payeeName = payeeName;
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
    
    public static Map<String, String> getFieldConversionBetweenPayeeAndVendor(){
        Map<String, String> fieldConversionMap = new HashMap<String, String>();
        
        fieldConversionMap.put(KFSPropertyConstants.PAYEE_NAME, KFSPropertyConstants.VENDOR_NAME);
        fieldConversionMap.put(KFSPropertyConstants.PAYEE_ID_NUMBER, KFSPropertyConstants.VENDOR_NUMBER);
        
        return fieldConversionMap;
    }
    
    public static Map<String, String> getFieldConversionBetweenPayeeAndPerson(){
        Map<String, String> fieldConversionMap = new HashMap<String, String>();
        
        fieldConversionMap.put(KFSPropertyConstants.PAYEE_NAME, KimPropertyConstants.PRINCIPAL_NAME);
        fieldConversionMap.put(KFSPropertyConstants.PAYEE_ID_NUMBER, KimPropertyConstants.PRINCIPAL_ID);
        
        return fieldConversionMap;
    }

    /**
     * Gets the paymentReasonCode attribute. 
     * @return Returns the paymentReasonCode.
     */
    public String getPaymentReasonCode() {
        return paymentReasonCode;
    }

    /**
     * Sets the paymentReasonCode attribute value.
     * @param paymentReasonCode The paymentReasonCode to set.
     */
    public void setPaymentReasonCode(String paymentReasonCode) {
        this.paymentReasonCode = paymentReasonCode;
    }
}

