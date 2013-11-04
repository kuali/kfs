/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.endow.EndowPropertyConstants;

/**
 * Business Object for Fee Payment Type.
 */
public class FeePaymentType extends FeeMethodCodeBase {
    private static final Logger LOG = Logger.getLogger(FeePaymentType.class);
    
    private String paymentTypeCode;
    
    private PaymentTypeCode paymentType;
    
    /**
     * Default constructor.
     */   
    public FeePaymentType() {
       super();
    }    
   
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> m = new LinkedHashMap<String, Object>();
        m.put(EndowPropertyConstants.FEE_METHOD_CODE, super.getFeeMethodCode());
        m.put(EndowPropertyConstants.FEE_PAYMENT_TYPE_CODE, this.getPaymentTypeCode());        
        return m;
        
    }

    /**
     * This method gets paymentType
     * 
     * @return paymentType
     */
    public String getPaymentTypeCode() {
        return paymentTypeCode;
    }

    /**
     * This method sets paymentType.
     * 
     * @param paymentType
     */
    public void setPaymentTypeCode(String paymentTypeCode) {
        this.paymentTypeCode = paymentTypeCode;
    }
            
    /**
     * This method gets the paymentType.
     * 
     * @return paymentType
     */
    public PaymentTypeCode getPaymentType() {
        return paymentType;
    }

    /**
     * This method sets the paymentType.
     * 
     * @param paymentType
     */
    public void setPaymentType(PaymentTypeCode paymentType) {
        this.paymentType = paymentType;
    }
}
